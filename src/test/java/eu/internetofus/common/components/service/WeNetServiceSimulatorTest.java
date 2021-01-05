/*
 * -----------------------------------------------------------------------------
 *
 * Copyright (c) 2019 - 2022 UDT-IA, IIIA-CSIC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * -----------------------------------------------------------------------------
 */

package eu.internetofus.common.components.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.serviceproxy.ServiceException;
import javax.ws.rs.core.Response.Status;

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

    final var client = WebClient.create(vertx);
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
      final var client = WebClient.create(vertx);
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
