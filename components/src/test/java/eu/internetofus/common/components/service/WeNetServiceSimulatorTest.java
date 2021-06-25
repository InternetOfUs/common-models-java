/*
 * -----------------------------------------------------------------------------
 *
 * Copyright 2019 - 2022 UDT-IA, IIIA-CSIC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * -----------------------------------------------------------------------------
 */

package eu.internetofus.common.components.service;

import static eu.internetofus.common.components.AbstractComponentMocker.createClientWithDefaultSession;
import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.serviceproxy.ServiceException;
import javax.ws.rs.core.Response.Status;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test the {@link WeNetServiceSimulator}.
 *
 * @see WeNetServiceSimulator
 * @see WeNetServiceSimulatorClient
 * @see WeNetServiceSimulatorMocker
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class WeNetServiceSimulatorTest extends WeNetServiceTestCase {

  /**
   * The service mocked server.
   */
  protected static WeNetServiceSimulatorMocker serviceMocker;

  /**
   * Start the mocker servers.
   */
  @BeforeAll
  public static void startMockers() {

    serviceMocker = WeNetServiceSimulatorMocker.start();
  }

  /**
   * Stop the mocker server.
   */
  @AfterAll
  public static void stopMockers() {

    serviceMocker.stopServer();
  }

  /**
   * Register the client.
   *
   * @param vertx event bus to use.
   */
  @BeforeEach
  public void registerClient(final Vertx vertx) {

    final var client = createClientWithDefaultSession(vertx);
    final var serviceConf = serviceMocker.getComponentConfiguration();
    WeNetServiceSimulator.register(vertx, client, serviceConf);
    WeNetService.register(vertx, client, serviceConf);

  }

  /**
   * Should not retrieve undefined app.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Override
  @Test
  public void shouldNotRetrieveUndefinedApp(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertFailure(WeNetServiceSimulator.createProxy(vertx).retrieveApp("undefined-app-identifier"))
        .onFailure(handler -> testContext.completeNow());

  }

  /**
   * Should not delete undefined app.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotDeleteUndefinedApp(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertFailure(WeNetServiceSimulator.createProxy(vertx).deleteApp("undefined-app-identifier"))
        .onFailure(handler -> testContext.completeNow());
  }

  /**
   * Should create, retrieve and delete a app.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldCreateRetrieveAndDeleteApp(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertComplete(WeNetServiceSimulator.createProxy(vertx).createApp(new App())).onSuccess(create -> {

      final var id = create.appId;
      testContext.assertComplete(WeNetServiceSimulator.createProxy(vertx).retrieveApp(id))
          .onSuccess(retrieve -> testContext.verify(() -> {

            assertThat(create).isEqualTo(retrieve);
            testContext.assertComplete(WeNetServiceSimulator.createProxy(vertx).deleteApp(id)).onSuccess(empty -> {

              testContext.assertFailure(WeNetServiceSimulator.createProxy(vertx).retrieveApp(id))
                  .onFailure(error -> testContext.verify(() -> {

                    assertThat(error).isInstanceOf(ServiceException.class);
                    assertThat(((ServiceException) error).failureCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
                    testContext.completeNow();

                  }));

            });

          }));

    });

  }

  /**
   * Should create, retrieve and delete a app users.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldCreateRetrieveAndDeleteAppUsers(final Vertx vertx, final VertxTestContext testContext) {

    final var service = WeNetServiceSimulator.createProxy(vertx);
    testContext.assertComplete(service.createApp(new App())).onSuccess(created -> {

      final var users = new JsonArray().add("1").add("43").add("23");
      testContext.assertComplete(service.addUsers(created.appId, users)).onSuccess(create -> {

        testContext.assertComplete(service.retrieveAppUserIds(created.appId))
            .onSuccess(retrieve -> testContext.verify(() -> {

              assertThat(retrieve).isEqualTo(users);
              testContext.assertComplete(service.deleteUsers(created.appId)).onSuccess(empty -> {

                testContext.assertComplete(service.retrieveAppUserIds(created.appId))
                    .onSuccess(retrieve2 -> testContext.verify(() -> {

                      assertThat(retrieve2).isEqualTo(new JsonArray());
                      testContext.assertComplete(service.deleteApp(created.appId))
                          .onSuccess(emptyApp -> testContext.completeNow());
                    }));
              });
            }));
      });
    });

  }

  /**
   * Should create, retrieve and delete a app callbacks.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldCreateRetrieveAndDeleteAppCallbacks(final Vertx vertx, final VertxTestContext testContext) {

    final var service = WeNetServiceSimulator.createProxy(vertx);
    testContext.assertComplete(service.createApp(new App())).onSuccess(created -> {

      final var message1 = new JsonObject().put("action", "Name of the action").put("users",
          new JsonArray().add("1").add("43").add("23"));
      testContext.assertComplete(service.addCallBack(created.appId, message1)).onSuccess(create1 -> {

        testContext.assertComplete(service.retrieveCallbacks(created.appId))
            .onSuccess(retrieve -> testContext.verify(() -> {

              assertThat(retrieve).isEqualTo(new JsonArray().add(message1));

              final var message2 = new JsonObject().put("action", "Name of the action2").put("users",
                  new JsonArray().add("21").add("243").add("223"));
              testContext.assertComplete(service.addCallBack(created.appId, message2)).onSuccess(create2 -> {

                testContext.assertComplete(service.retrieveCallbacks(created.appId))
                    .onSuccess(retrieve2 -> testContext.verify(() -> {

                      assertThat(retrieve2).isEqualTo(new JsonArray().add(message1).add(message2));

                      testContext.assertComplete(service.deleteCallbacks(created.appId)).onSuccess(empty -> {

                        testContext.assertComplete(service.retrieveCallbacks(created.appId))
                            .onSuccess(retrieve3 -> testContext.verify(() -> {

                              assertThat(retrieve3).isEqualTo(new JsonArray());
                              testContext.assertComplete(service.deleteApp(created.appId))
                                  .onSuccess(emptyApp -> testContext.completeNow());

                            }));
                      });
                    }));
              });
            }));
      });
    });

  }

  /**
   * Should not retrieve callbacks for an undefined app.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotRetrieveCallbackForUndefinedApp(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertFailure(WeNetServiceSimulator.createProxy(vertx).retrieveCallbacks("undefined-app-identifier"))
        .onFailure(handler -> testContext.completeNow());

  }

  /**
   * Should not delete undefined app callbacks.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotDeleteUndefinedCallback(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertFailure(WeNetServiceSimulator.createProxy(vertx).deleteCallbacks("undefined-app-identifier"))
        .onFailure(handler -> testContext.completeNow());
  }

  /**
   * Should create, retrieve and delete a app callbacks.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldPostMessageToAppCallbacks(final Vertx vertx, final VertxTestContext testContext) {

    final var service = WeNetServiceSimulator.createProxy(vertx);
    testContext.assertComplete(service.createApp(new App())).onSuccess(created -> {

      final var message = new JsonObject().put("action", "Name of the action").put("users",
          new JsonArray().add("1").add("43").add("23"));
      final var client = createClientWithDefaultSession(vertx);
      testContext.assertComplete(client.postAbs(created.messageCallbackUrl).sendJson(message)).onSuccess(create -> {

        testContext.assertComplete(service.retrieveCallbacks(created.appId))
            .onSuccess(retrieve -> testContext.verify(() -> {

              assertThat(retrieve).isEqualTo(new JsonArray().add(message));

              testContext.assertComplete(service.deleteApp(created.appId))
                  .onSuccess(emptyApp -> testContext.completeNow());
            }));
      });
    });

  }

}
