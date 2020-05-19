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

import javax.validation.constraints.NotNull;

import eu.internetofus.common.vertx.ComponentClient;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

/**
 * Service used to interact with the {@link ServiceApiSimulator}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ServiceApiSimulatorServiceImpl extends ComponentClient
		implements ServiceApiSimulatorService, WeNetServiceApiService {

	/**
	 * Create a new service to interact with the WeNet interaction protocol engine.
	 *
	 * @param client to interact with the other modules.
	 * @param conf   configuration.
	 */
	public ServiceApiSimulatorServiceImpl(WebClient client, JsonObject conf) {

		super(client, conf.getString("service", "https://wenet.u-hopper.com/service"));

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void retrieveApp(@NotNull String id, @NotNull Handler<AsyncResult<App>> retrieveHandler) {

		this.retrieveJsonApp(id, ComponentClient.handlerForModel(App.class, retrieveHandler));

	}

	/**
	 * Call the {@link ServiceApiSimulator} to create an application.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void createApp(JsonObject app, Handler<AsyncResult<JsonObject>> createHandler) {

		this.post(app, createHandler, "/app");

	}

	/**
	 * Call the {@link ServiceApiSimulator} to delete an application.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void deleteApp(String id, Handler<AsyncResult<Void>> deleteHandler) {

		this.delete(deleteHandler, "/app", id);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void retrieveJsonApp(String id, Handler<AsyncResult<JsonObject>> retrieveHandler) {

		this.getJsonObject(retrieveHandler, "/app", id);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void retrieveJsonCallbacks(String id, Handler<AsyncResult<JsonObject>> retrieveHandler) {

		this.getJsonObject(retrieveHandler, "/callback", id);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addJsonCallBack(String appId, JsonObject message, Handler<AsyncResult<JsonObject>> handler) {

		this.post(message, handler, "/callback", appId);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteCallbacks(String appId, Handler<AsyncResult<Void>> handler) {

		this.delete(handler, "/callback", appId);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void retrieveJsonArrayAppUserIds(String id, Handler<AsyncResult<JsonArray>> retrieveHandler) {

		this.getJsonArray(retrieveHandler, "/app/" + id + "/users");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addUsers(String appId, JsonArray users, Handler<AsyncResult<JsonArray>> handler) {

		this.post(users, handler, "/app/" + appId + "/users");

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteUsers(String appId, Handler<AsyncResult<Void>> handler) {

		this.delete(handler, "/app/" + appId + "/users");

	}

}
