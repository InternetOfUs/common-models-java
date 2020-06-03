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
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
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

import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link WeNetServiceSimulator}.
 *
 * @see WeNetService
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class WeNetServiceSimulatorTestCase {

  /**
   * Should not retrieve undefined app.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotRetrieveUndefinedApp(final Vertx vertx, final VertxTestContext testContext) {

    WeNetServiceSimulator.createProxy(vertx).retrieveApp("undefined-app-identifier", testContext.failing(handler -> {
      testContext.completeNow();

    }));

  }

  /**
   * Should not delete undefined app.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotDeleteUndefinedApp(final Vertx vertx, final VertxTestContext testContext) {

    WeNetServiceSimulator.createProxy(vertx).deleteApp("undefined-app-identifier", testContext.failing(handler -> {
      testContext.completeNow();

    }));

  }

  /**
   * Should create, retrieve and delete a app.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldCreateRetrieveAndDeleteApp(final Vertx vertx, final VertxTestContext testContext) {

    final WeNetServiceSimulator service = WeNetServiceSimulator.createProxy(vertx);
    service.createApp(new App(), testContext.succeeding(create -> {

      final String id = create.appId;
      service.retrieveApp(id, testContext.succeeding(retrieve -> testContext.verify(() -> {

        assertThat(create).isEqualTo(retrieve);
        service.deleteApp(id, testContext.succeeding(empty -> {

          service.retrieveApp(id, testContext.failing(handler -> {
            testContext.completeNow();

          }));

        }));

      })));

    }));

  }

  /**
   * Should create, retrieve and delete a app users.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldCreateRetrieveAndDeleteAppUsers(final Vertx vertx, final VertxTestContext testContext) {

    final WeNetServiceSimulator service = WeNetServiceSimulator.createProxy(vertx);
    service.createApp(new App(), testContext.succeeding(created -> {

      final JsonArray users = new JsonArray().add("1").add("43").add("23");
      service.addUsers(created.appId, users, testContext.succeeding(create -> {

        service.retrieveJsonArrayAppUserIds(created.appId, testContext.succeeding(retrieve -> testContext.verify(() -> {

          assertThat(retrieve).isEqualTo(users);
          service.deleteUsers(created.appId, testContext.succeeding(empty -> {

            service.retrieveJsonArrayAppUserIds(created.appId, testContext.failing(handler -> {

              service.deleteApp(created.appId, testContext.succeeding(emptyApp -> {

                testContext.completeNow();

              }));
            }));
          }));
        })));
      }));
    }));

  }

  /**
   * Should create, retrieve and delete a app callbacks.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldCreateRetrieveAndDeleteAppCallbacks(final Vertx vertx, final VertxTestContext testContext) {

    final WeNetServiceSimulator service = WeNetServiceSimulator.createProxy(vertx);
    service.createApp(new App(), testContext.succeeding(created -> {

      final JsonObject callbacks = new JsonObject().put("action", "Name of the action").put("users", new JsonArray().add("1").add("43").add("23"));
      service.addJsonCallBack(created.appId, callbacks, testContext.succeeding(create -> {

        service.retrieveJsonCallbacks(created.appId, testContext.succeeding(retrieve -> testContext.verify(() -> {

          assertThat(retrieve).isEqualTo(callbacks);
          service.deleteCallbacks(created.appId, testContext.succeeding(empty -> {

            service.retrieveJsonCallbacks(created.appId, testContext.failing(handler -> {

              service.deleteApp(created.appId, testContext.succeeding(emptyApp -> {

                testContext.completeNow();

              }));
            }));
          }));
        })));
      }));
    }));

  }

  /**
   * Should not retrieve callbacks for an undefined app.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotRetrieveCallbackForUndefinedApp(final Vertx vertx, final VertxTestContext testContext) {

    WeNetServiceSimulator.createProxy(vertx).retrieveJsonCallbacks("undefined-app-identifier", testContext.failing(handler -> {
      testContext.completeNow();

    }));

  }

  /**
   * Should not delete undefined app callbacks.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotDeleteUndefinedCallback(final Vertx vertx, final VertxTestContext testContext) {

    WeNetServiceSimulator.createProxy(vertx).deleteCallbacks("undefined-app-identifier", testContext.failing(handler -> {
      testContext.completeNow();

    }));

  }

  /**
   * Should create, retrieve and delete a app callbacks.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldPostMessagoToAppCallbacks(final Vertx vertx, final VertxTestContext testContext) {

    final WeNetServiceSimulator service = WeNetServiceSimulator.createProxy(vertx);
    service.createApp(new App(), testContext.succeeding(created -> {

      final JsonObject message = new JsonObject().put("action", "Name of the action").put("users", new JsonArray().add("1").add("43").add("23"));
      final WebClient client = WebClient.create(vertx);
      client.postAbs(created.messageCallbackUrl).sendJson(message, testContext.succeeding(create -> {

        service.retrieveJsonCallbacks(created.appId, testContext.succeeding(retrieve -> testContext.verify(() -> {

          assertThat(retrieve).isEqualTo(message);

          service.deleteApp(created.appId, testContext.succeeding(emptyApp -> {

            testContext.completeNow();

          }));
        })));
      }));
    }));

  }
}
