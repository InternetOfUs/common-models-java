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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import eu.internetofus.common.api.models.Model;
import eu.internetofus.common.api.models.wenet.App;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;

/**
 * Implementation of the {@link ServiceApiSimulatorService} that can be used for
 * unit testing.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ServiceApiSimulatorServiceOnMemory implements ServiceApiSimulatorService, WeNetServiceApiService {

	/**
	 * Register this service.
	 *
	 * @param vertx that contains the event bus to use.
	 */
	public static void register(Vertx vertx) {

		final ServiceApiSimulatorServiceOnMemory serviceOnnMemory = new ServiceApiSimulatorServiceOnMemory();
		new ServiceBinder(vertx).setAddress(ServiceApiSimulatorService.ADDRESS).register(ServiceApiSimulatorService.class,
				serviceOnnMemory);
		new ServiceBinder(vertx).setAddress(WeNetServiceApiService.ADDRESS).register(WeNetServiceApiService.class,
				serviceOnnMemory);

	}

	/**
	 * The apps that has been stored on the service.
	 */
	private final Map<String, JsonObject> apps;

	/**
	 * The callback messages to an application.
	 */
	private final Map<String, JsonArray> callbacks;

	/**
	 * The user of an application.
	 */
	private final Map<String, JsonArray> users;

	/**
	 * Create the service.
	 */
	public ServiceApiSimulatorServiceOnMemory() {

		this.apps = new HashMap<>();
		this.callbacks = new HashMap<>();
		this.users = new HashMap<>();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void retrieveApp(@NotNull String id, @NotNull Handler<AsyncResult<App>> retrieveHandler) {

		this.retrieveJsonApp(id, Service.handlerForModel(App.class, retrieveHandler));

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void createApp(JsonObject app, Handler<AsyncResult<JsonObject>> createHandler) {

		final App model = Model.fromJsonObject(app, App.class);
		if (model == null) {
			// bad app
			createHandler.handle(Future.failedFuture("Bad app to store"));

		} else {

			String id = app.getString("appId");
			if (id == null) {

				id = UUID.randomUUID().toString();
				app.put("appId", id);
			}
			String callback = app.getString("messageCallbackUrl");
			if (callback != null && callback.endsWith("/:appId")) {

				callback = callback.substring(0, callback.length() - 6) + id;
				app.put("messageCallbackUrl", callback);
			}

			if (this.apps.containsKey(id)) {

				createHandler.handle(Future.failedFuture("App already registered"));

			} else {

				this.apps.put(id, app);
				createHandler.handle(Future.succeededFuture(app));
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void retrieveJsonApp(String id, Handler<AsyncResult<JsonObject>> retrieveHandler) {

		final JsonObject app = this.apps.get(id);
		if (app == null) {

			retrieveHandler.handle(Future.failedFuture("No Application associated to the ID"));

		} else {

			retrieveHandler.handle(Future.succeededFuture(app));

		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void deleteApp(String id, Handler<AsyncResult<JsonObject>> deleteHandler) {

		final JsonObject app = this.apps.remove(id);
		if (app == null) {

			deleteHandler.handle(Future.failedFuture("No Application associated to the ID"));

		} else {

			deleteHandler.handle(Future.succeededFuture());

		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void retrieveJsonCallbacks(String appId, Handler<AsyncResult<JsonObject>> handler) {

		final JsonArray messages = this.callbacks.get(appId);
		if (messages == null) {
			// no messages
			handler.handle(Future.failedFuture("No callbacks defined for the application"));

		} else {

			handler.handle(Future.succeededFuture(new JsonObject().put("messages", messages)));
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void addJsonCallBack(String appId, JsonObject message, Handler<AsyncResult<JsonObject>> handler) {

		if (this.apps.containsKey(appId)) {

			JsonArray messages = this.callbacks.get(appId);
			if (messages == null) {

				messages = new JsonArray();
				this.callbacks.put(appId, messages);
			}
			messages.add(message);

			handler.handle(Future.succeededFuture(message));

		} else {

			handler.handle(Future.failedFuture("No Application associated to the ID"));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteCallbacks(String appId, Handler<AsyncResult<JsonObject>> handler) {

		final JsonArray messages = this.callbacks.remove(appId);
		if (messages == null) {
			// no messages
			handler.handle(Future.failedFuture("No callbacks defined for the application"));

		} else {

			handler.handle(Future.succeededFuture(new JsonObject().put("messages", messages)));
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void retrieveJsonArrayAppUserIds(String id, Handler<AsyncResult<JsonArray>> handler) {

		final JsonArray users = this.users.get(id);
		if (users == null) {
			// no users
			handler.handle(Future.failedFuture("No users defined for the application"));

		} else {

			handler.handle(Future.succeededFuture(users));
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void addUsers(String appId, JsonArray newUsers, Handler<AsyncResult<JsonArray>> handler) {

		if (this.apps.containsKey(appId)) {

			JsonArray users = this.users.get(appId);
			if (users == null) {

				users = new JsonArray();
				this.users.put(appId, users);
			}
			users.addAll(newUsers);

			handler.handle(Future.succeededFuture(users));

		} else {

			handler.handle(Future.failedFuture("No Application associated to the ID"));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteUsers(String appId, Handler<AsyncResult<JsonArray>> handler) {

		final JsonArray users = this.users.remove(appId);
		if (users == null) {
			// no users
			handler.handle(Future.failedFuture("No users defined for the application"));

		} else {

			handler.handle(Future.succeededFuture(users));
		}

	}

}
