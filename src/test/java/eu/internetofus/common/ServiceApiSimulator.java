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

package eu.internetofus.common;

import eu.internetofus.common.services.ServiceApiSimulatorService;
import eu.internetofus.common.services.ServiceApiSimulatorServiceOnMemory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

/**
 * Class used to simulate the interaction with the
 * {@link ServiceApiSimulatorService }.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ServiceApiSimulator extends AbstractVerticle {

	/**
	 * Port to listen for the HTTP requests.
	 */
	protected int port;

	/**
	 * The server that provides the simulation interaction.
	 */
	protected HttpServer server;

	/**
	 * Create a new simulator.
	 *
	 * @param port to listen.
	 */
	public ServiceApiSimulator(int port) {

		this.port = port;

	}

	/**
	 * Start the simulator to the specified port.
	 *
	 * @param port to listen.
	 *
	 * @return the information of the started simulator.
	 */
	public static Future<ServiceApiSimulator> start(int port) {

		final Promise<ServiceApiSimulator> promise = Promise.promise();
		final Vertx vertx = Vertx.vertx();
		final ServiceApiSimulator simulator = new ServiceApiSimulator(port);
		vertx.deployVerticle(simulator, started -> {

			if (started.failed()) {

				promise.fail(started.cause());

			} else {

				promise.complete(simulator);
			}
		});
		return promise.future();
	}

	/**
	 * Stops the simulator.
	 */
	@Override
	public synchronized void stop() {

		if (this.vertx != null) {
			this.vertx.close();
			this.vertx = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(Promise<Void> startPromise) throws Exception {

		try {

			ServiceApiSimulatorServiceOnMemory.register(this.vertx);
			this.server = this.vertx.createHttpServer();

			final Router router = Router.router(this.vertx);

			this.createHandlerForGetApp(router);
			this.createHandlerForPostApp(router);
			this.createHandlerForDeleteApp(router);
			this.createHandlerForPostCallback(router);
			this.createHandlerForGetCallbacks(router);
			this.createHandlerForDeleteCallbacks(router);
			this.createHandlerForPostUsers(router);
			this.createHandlerForGetUsers(router);
			this.createHandlerForDeleteUsers(router);

			this.server.requestHandler(router).listen(this.port, start -> {

				if (start.failed()) {

					startPromise.fail(start.cause());

				} else {

					startPromise.complete();
				}
			});

		} catch (final Throwable throwable) {

			startPromise.fail(throwable);
		}

	}

	/**
	 * Handle the petition to get an application.
	 *
	 * @param router for the server.
	 */
	protected void createHandlerForGetApp(Router router) {

		router.route(HttpMethod.GET, "/app/:appId").handler(routingContext -> {

			final String appId = routingContext.request().getParam("appId");
			ServiceApiSimulatorService.createProxy(this.vertx).retrieveJsonApp(appId, retrieve -> {

				final HttpServerResponse response = routingContext.response();
				if (retrieve.failed()) {

					response.putHeader("content-type", "text/plain");
					response.setStatusCode(400);
					response.end(retrieve.cause().getMessage());

				} else {

					response.putHeader("content-type", "application/json");
					response.setStatusCode(200);
					response.end(retrieve.result().encode());
				}

			});
		});
	}

	/**
	 * Handle the petition to post an application.
	 *
	 * @param router for the server.
	 */
	protected void createHandlerForPostApp(Router router) {

		router.route(HttpMethod.POST, "/app").handler(routingContext -> {

			routingContext.request().bodyHandler(body -> {
				final JsonObject app = new JsonObject(body);
				if (!app.containsKey("messageCallbackUrl")) {
					String callback = routingContext.request().absoluteURI().toString();
					callback = callback.substring(0, callback.lastIndexOf("/app")) + "/callback/:appId";
					app.put("messageCallbackUrl", callback);
				}
				ServiceApiSimulatorService.createProxy(this.vertx).createApp(app, create -> {

					final HttpServerResponse response = routingContext.response();
					if (create.failed()) {

						response.putHeader("content-type", "text/plain");
						response.setStatusCode(400);
						response.end(create.cause().getMessage());

					} else {

						response.putHeader("content-type", "application/json");
						response.setStatusCode(200);
						response.end(create.result().encode());
					}

				});

			});
		});

	}

	/**
	 * Handle the petition to delete an application.
	 *
	 * @param router for the server.
	 */
	protected void createHandlerForDeleteApp(Router router) {

		router.route(HttpMethod.DELETE, "/app/:appId").handler(routingContext -> {

			final String appId = routingContext.request().getParam("appId");
			ServiceApiSimulatorService.createProxy(this.vertx).deleteApp(appId, delete -> {

				final HttpServerResponse response = routingContext.response();
				if (delete.failed()) {

					response.putHeader("content-type", "text/plain");
					response.setStatusCode(400);
					response.end(delete.cause().getMessage());

				} else {

					response.putHeader("content-type", "application/json");
					response.setStatusCode(204);
					response.end();
				}

			});
		});

	}

	/**
	 * Handle the petition to post a callback message for an application.
	 *
	 * @param router for the server.
	 */
	protected void createHandlerForPostCallback(Router router) {

		router.route(HttpMethod.POST, "/callback/:appId").handler(routingContext -> {

			final String appId = routingContext.request().getParam("appId");
			routingContext.request().bodyHandler(body -> {

				final JsonObject message = new JsonObject(body);
				ServiceApiSimulatorService.createProxy(this.vertx).addJsonCallBack(appId, message, create -> {

					final HttpServerResponse response = routingContext.response();
					if (create.failed()) {

						response.putHeader("content-type", "text/plain");
						response.setStatusCode(400);
						response.end(create.cause().getMessage());

					} else {

						response.putHeader("content-type", "application/json");
						response.setStatusCode(200);
						response.end(create.result().encode());
					}

				});

			});
		});

	}

	/**
	 * Handle the petition to return the callback message for an application.
	 *
	 * @param router for the server.
	 */
	protected void createHandlerForGetCallbacks(Router router) {

		router.route(HttpMethod.GET, "/callback/:appId").handler(routingContext -> {

			final String appId = routingContext.request().getParam("appId");
			ServiceApiSimulatorService.createProxy(this.vertx).retrieveJsonCallbacks(appId, create -> {

				final HttpServerResponse response = routingContext.response();
				if (create.failed()) {

					response.putHeader("content-type", "text/plain");
					response.setStatusCode(400);
					response.end(create.cause().getMessage());

				} else {

					response.putHeader("content-type", "application/json");
					response.setStatusCode(200);
					response.end(create.result().encode());
				}

			});

		});

	}

	/**
	 * Handle the petition to delete all the callback messages over an application.
	 *
	 * @param router for the server.
	 */
	protected void createHandlerForDeleteCallbacks(Router router) {

		router.route(HttpMethod.DELETE, "/callback/:appId").handler(routingContext -> {

			final String appId = routingContext.request().getParam("appId");
			ServiceApiSimulatorService.createProxy(this.vertx).deleteCallbacks(appId, delete -> {

				final HttpServerResponse response = routingContext.response();
				if (delete.failed()) {

					response.putHeader("content-type", "text/plain");
					response.setStatusCode(400);
					response.end(delete.cause().getMessage());

				} else {

					response.putHeader("content-type", "application/json");
					response.setStatusCode(204);
					response.end();
				}

			});
		});

	}

	/**
	 * Handle the petition to post users for an application.
	 *
	 * @param router for the server.
	 */
	protected void createHandlerForPostUsers(Router router) {

		router.route(HttpMethod.POST, "/user/:appId/users").handler(routingContext -> {

			final String appId = routingContext.request().getParam("appId");
			routingContext.request().bodyHandler(body -> {

				final JsonArray users = new JsonArray(body);
				ServiceApiSimulatorService.createProxy(this.vertx).addUsers(appId, users, create -> {

					final HttpServerResponse response = routingContext.response();
					if (create.failed()) {

						response.putHeader("content-type", "text/plain");
						response.setStatusCode(400);
						response.end(create.cause().getMessage());

					} else {

						response.putHeader("content-type", "application/json");
						response.setStatusCode(200);
						response.end(create.result().encode());
					}

				});

			});
		});

	}

	/**
	 * Handle the petition to return the users for an application.
	 *
	 * @param router for the server.
	 */
	protected void createHandlerForGetUsers(Router router) {

		router.route(HttpMethod.GET, "/app/:appId/users").handler(routingContext -> {

			final String appId = routingContext.request().getParam("appId");
			ServiceApiSimulatorService.createProxy(this.vertx).retrieveJsonArrayAppUserIds(appId, create -> {

				final HttpServerResponse response = routingContext.response();
				if (create.failed()) {

					response.putHeader("content-type", "text/plain");
					response.setStatusCode(400);
					response.end(create.cause().getMessage());

				} else {

					response.putHeader("content-type", "application/json");
					response.setStatusCode(200);
					response.end(create.result().encode());
				}

			});

		});

	}

	/**
	 * Handle the petition to delete all the users over an application.
	 *
	 * @param router for the server.
	 */
	protected void createHandlerForDeleteUsers(Router router) {

		router.route(HttpMethod.DELETE, "/user/:appId/users").handler(routingContext -> {

			final String appId = routingContext.request().getParam("appId");
			ServiceApiSimulatorService.createProxy(this.vertx).deleteUsers(appId, delete -> {

				final HttpServerResponse response = routingContext.response();
				if (delete.failed()) {

					response.putHeader("content-type", "text/plain");
					response.setStatusCode(400);
					response.end(delete.cause().getMessage());

				} else {

					response.putHeader("content-type", "application/json");
					response.setStatusCode(204);
					response.end();
				}

			});
		});

	}

}