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

package eu.internetofus.common.components.task_manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import eu.internetofus.common.components.Model;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;

/**
 * Implementation of the {@link WeNetTaskManagerService} that can be used for
 * unit testing.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetTaskManagerServiceOnMemory implements WeNetTaskManagerService {

	/**
	 * Register this service.
	 *
	 * @param vertx that contains the event bus to use.
	 */
	public static void register(Vertx vertx) {

		new ServiceBinder(vertx).setAddress(WeNetTaskManagerService.ADDRESS).register(WeNetTaskManagerService.class,
				new WeNetTaskManagerServiceOnMemory());

	}

	/**
	 * The tasks that has been stored on the service.
	 */
	private final Map<String, JsonObject> tasks;

	/**
	 * The task types that has been stored on the service.
	 */
	private final Map<String, JsonObject> taskTypes;

	/**
	 * Create the service.
	 */
	public WeNetTaskManagerServiceOnMemory() {

		this.tasks = new HashMap<>();
		this.taskTypes = new HashMap<>();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void createTask(JsonObject task, Handler<AsyncResult<JsonObject>> createHandler) {

		final Task model = Model.fromJsonObject(task, Task.class);
		if (model == null) {
			// bad task
			createHandler.handle(Future.failedFuture("Bad task to store"));

		} else {

			String id = task.getString("id");
			if (id == null) {

				id = UUID.randomUUID().toString();
				task.put("id", id);
			}

			if (this.tasks.containsKey(id)) {

				createHandler.handle(Future.failedFuture("Task already registered"));

			} else {

				this.tasks.put(id, task);
				createHandler.handle(Future.succeededFuture(task));
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void retrieveJsonTask(String id, Handler<AsyncResult<JsonObject>> retrieveHandler) {

		final JsonObject task = this.tasks.get(id);
		if (task == null) {

			retrieveHandler.handle(Future.failedFuture("No Task associated to the ID"));

		} else {

			retrieveHandler.handle(Future.succeededFuture(task));

		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void deleteTask(String id, Handler<AsyncResult<JsonObject>> deleteHandler) {

		final JsonObject task = this.tasks.remove(id);
		if (task == null) {

			deleteHandler.handle(Future.failedFuture("No Task associated to the ID"));

		} else {

			deleteHandler.handle(Future.succeededFuture());

		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void createTaskType(JsonObject taskType, Handler<AsyncResult<JsonObject>> createHandler) {

		final TaskType model = Model.fromJsonObject(taskType, TaskType.class);
		if (model == null) {
			// bad taskType
			createHandler.handle(Future.failedFuture("Bad taskType to store"));

		} else {

			String id = taskType.getString("id");
			if (id == null) {

				id = UUID.randomUUID().toString();
				taskType.put("id", id);
			}

			if (this.taskTypes.containsKey(id)) {

				createHandler.handle(Future.failedFuture("TaskType already registered"));

			} else {

				this.taskTypes.put(id, taskType);
				createHandler.handle(Future.succeededFuture(taskType));
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void retrieveJsonTaskType(String id, Handler<AsyncResult<JsonObject>> retrieveHandler) {

		final JsonObject taskType = this.taskTypes.get(id);
		if (taskType == null) {

			retrieveHandler.handle(Future.failedFuture("No TaskType associated to the ID"));

		} else {

			retrieveHandler.handle(Future.succeededFuture(taskType));

		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void deleteTaskType(String id, Handler<AsyncResult<JsonObject>> deleteHandler) {

		final JsonObject taskType = this.taskTypes.remove(id);
		if (taskType == null) {

			deleteHandler.handle(Future.failedFuture("No TaskType associated to the ID"));

		} else {

			deleteHandler.handle(Future.succeededFuture());

		}

	}

}
