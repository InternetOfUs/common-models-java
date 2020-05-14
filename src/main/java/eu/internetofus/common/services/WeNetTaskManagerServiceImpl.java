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

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

/**
 * The implementation of the {@link WeNetTaskManagerService}.
 *
 *
 * @see WeNetTaskManagerService
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetTaskManagerServiceImpl extends Service implements WeNetTaskManagerService {

	/**
	 * Create a new service to interact with the WeNet task manager.
	 *
	 * @param client to interact with the other modules.
	 * @param conf   configuration.
	 */
	public WeNetTaskManagerServiceImpl(WebClient client, JsonObject conf) {

		super(client, conf);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createTask(JsonObject task, Handler<AsyncResult<JsonObject>> createHandler) {

		this.post("/tasks", task, createHandler);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void retrieveJsonTask(String id, Handler<AsyncResult<JsonObject>> retrieveHandler) {

		this.get("/tasks/" + id, retrieveHandler);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteTask(String id, Handler<AsyncResult<JsonObject>> deleteHandler) {

		this.delete("/tasks/" + id, deleteHandler);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createTaskType(JsonObject taskType, Handler<AsyncResult<JsonObject>> createHandler) {

		this.post("/tasks/types", taskType, createHandler);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void retrieveJsonTaskType(String id, Handler<AsyncResult<JsonObject>> retrieveHandler) {

		this.get("/tasks/types/" + id, retrieveHandler);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteTaskType(String id, Handler<AsyncResult<JsonObject>> deleteHandler) {

		this.delete("/tasks/types/" + id, deleteHandler);

	}

}
