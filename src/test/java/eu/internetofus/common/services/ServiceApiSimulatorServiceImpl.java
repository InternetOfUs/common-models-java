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

import javax.validation.constraints.NotNull;

import eu.internetofus.common.ServiceApiSimulator;
import eu.internetofus.common.api.models.wenet.App;
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
public class ServiceApiSimulatorServiceImpl extends Service
		implements ServiceApiSimulatorService, WeNetServiceApiService {

	/**
	 * Create a new service to interact with the WeNet interaction protocol engine.
	 *
	 * @param client to interact with the other modules.
	 * @param conf   configuration.
	 */
	public ServiceApiSimulatorServiceImpl(WebClient client, JsonObject conf) {

		super(client, conf);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void retrieveApp(@NotNull String id, @NotNull Handler<AsyncResult<App>> retrieveHandler) {

		this.retrieveJsonApp(id, Service.handlerForModel(App.class, retrieveHandler));

	}

	/**
	 * Call the {@link ServiceApiSimulator} to create an application.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void createApp(JsonObject app, Handler<AsyncResult<JsonObject>> createHandler) {

		this.post("/app", app, createHandler);

	}

	/**
	 * Call the {@link ServiceApiSimulator} to delete an application.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void deleteApp(String id, Handler<AsyncResult<JsonObject>> deleteHandler) {

		this.delete("/app/" + id, deleteHandler);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void retrieveJsonApp(String id, Handler<AsyncResult<JsonObject>> retrieveHandler) {

		this.get("/app/" + id, retrieveHandler);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void retrieveJsonCallbacks(String id, Handler<AsyncResult<JsonObject>> retrieveHandler) {

		this.get("/callback/" + id, retrieveHandler);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addJsonCallBack(String appId, JsonObject message, Handler<AsyncResult<JsonObject>> handler) {

		this.post("/callback/" + appId, message, handler);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteCallbacks(String appId, Handler<AsyncResult<JsonObject>> handler) {

		this.delete("/callback/" + appId, handler);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void retrieveJsonArrayAppUserIds(String id, Handler<AsyncResult<JsonArray>> retrieveHandler) {

		this.getArray("/app/" + id + "/users", retrieveHandler);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addUsers(String appId, JsonArray users, Handler<AsyncResult<JsonArray>> handler) {

		this.postArray("/app/" + appId + "/users", users, handler);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteUsers(String appId, Handler<AsyncResult<JsonArray>> handler) {

		this.deleteArray("/app/" + appId + "/users", handler);

	}

}
