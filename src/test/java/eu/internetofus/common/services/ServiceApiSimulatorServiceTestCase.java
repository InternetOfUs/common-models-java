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

package eu.internetofus.common.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import eu.internetofus.common.api.models.wenet.App;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link ServiceApiSimulatorService}
 *
 * @see ServiceApiSimulatorService
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class ServiceApiSimulatorServiceTestCase {

	/**
	 * Should not create a bad app.
	 *
	 * @param vertx       that contains the event bus to use.
	 * @param testContext context over the tests.
	 */
	@Test
	public void shouldNotCreateBadJsonApp(Vertx vertx, VertxTestContext testContext) {

		ServiceApiSimulatorService.createProxy(vertx).createApp(new JsonObject().put("undefinedField", "value"),
				testContext.failing(handler -> {
					testContext.completeNow();

				}));

	}

	/**
	 * Should not create a bad app.
	 *
	 * @param vertx       that contains the event bus to use.
	 * @param testContext context over the tests.
	 */
	@Test
	public void shouldNotCreateAnAppWithExistingId(Vertx vertx, VertxTestContext testContext) {

		ServiceApiSimulatorService.createProxy(vertx).createApp(new JsonObject(), testContext.succeeding(created -> {

			final App app = new App();
			app.appId = created.getString("appId");
			ServiceApiSimulatorService.createProxy(vertx).createApp(app, testContext.failing(handler -> {
				testContext.completeNow();

			}));

		}));

	}

	/**
	 * Should not retrieve undefined app.
	 *
	 * @param vertx       that contains the event bus to use.
	 * @param testContext context over the tests.
	 */
	@Test
	public void shouldNotRetrieveJsonFromUndefinedApp(Vertx vertx, VertxTestContext testContext) {

		ServiceApiSimulatorService.createProxy(vertx).retrieveJsonApp("undefined-app-identifier",
				testContext.failing(handler -> {
					testContext.completeNow();

				}));

	}

	/**
	 * Should not retrieve undefined app.
	 *
	 * @param vertx       that contains the event bus to use.
	 * @param testContext context over the tests.
	 */
	@Test
	public void shouldNotRetrieveUndefinedApp(Vertx vertx, VertxTestContext testContext) {

		ServiceApiSimulatorService.createProxy(vertx).retrieveApp("undefined-app-identifier",
				testContext.failing(handler -> {
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
	public void shouldNotDeleteUndefinedApp(Vertx vertx, VertxTestContext testContext) {

		ServiceApiSimulatorService.createProxy(vertx).deleteApp("undefined-app-identifier", testContext.failing(handler -> {
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
	public void shouldCreateRetrieveAndDeleteApp(Vertx vertx, VertxTestContext testContext) {

		final ServiceApiSimulatorService service = ServiceApiSimulatorService.createProxy(vertx);
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
	 * Should not retrieve callbacks from an undefined app.
	 *
	 * @param vertx       that contains the event bus to use.
	 * @param testContext context over the tests.
	 */
	@Test
	public void shouldFailRetrieveCallbacksFromUndefinedApp(Vertx vertx, VertxTestContext testContext) {

		ServiceApiSimulatorService.createProxy(vertx).retrieveJsonCallbacks("undefined-app-identifier",
				testContext.failing(handler -> {
					testContext.completeNow();

				}));

	}

	/**
	 * Should not add callbacks into an undefined app.
	 *
	 * @param vertx       that contains the event bus to use.
	 * @param testContext context over the tests.
	 */
	@Test
	public void shouldFailAddCallbackIntoUndefinedApp(Vertx vertx, VertxTestContext testContext) {

		ServiceApiSimulatorService.createProxy(vertx).addJsonCallBack("undefined-app-identifier", new JsonObject(),
				testContext.failing(handler -> {
					testContext.completeNow();

				}));

	}

	/**
	 * Should not delete callbacks from an undefined app.
	 *
	 * @param vertx       that contains the event bus to use.
	 * @param testContext context over the tests.
	 */
	@Test
	public void shouldFailDeletCallbackIntoUndefinedApp(Vertx vertx, VertxTestContext testContext) {

		ServiceApiSimulatorService.createProxy(vertx).deleteCallbacks("undefined-app-identifier",
				testContext.failing(handler -> {
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
	public void shouldAddRetrieveAndDeleteCallbacksFromAnApp(Vertx vertx, VertxTestContext testContext) {

		final ServiceApiSimulatorService service = ServiceApiSimulatorService.createProxy(vertx);
		service.createApp(new App(), testContext.succeeding(created -> {

			final String id = created.appId;
			final JsonObject message = new JsonObject().put("key", "value");
			service.addJsonCallBack(id, message, testContext.succeeding(added1 -> testContext.verify(() -> {

				assertThat(message).isEqualTo(added1);
				final JsonObject message2 = new JsonObject().put("key2", "value2");
				service.addJsonCallBack(id, message2, testContext.succeeding(added2 -> testContext.verify(() -> {

					assertThat(message2).isEqualTo(added2);
					service.retrieveJsonCallbacks(id, testContext.succeeding(retrieve -> testContext.verify(() -> {

						assertThat(retrieve)
								.isEqualTo(new JsonObject().put("messages", new JsonArray().add(message).add(message2)));

						service.deleteCallbacks(id, testContext.succeeding(empty -> {

							service.retrieveJsonCallbacks(id, testContext.failing(handler -> {
								testContext.completeNow();

							}));
						}));
					})));
				})));
			})));
		}));

	}

}
