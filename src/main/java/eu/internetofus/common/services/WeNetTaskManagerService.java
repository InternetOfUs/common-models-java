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

import eu.internetofus.common.api.models.wenet.Task;
import eu.internetofus.common.api.models.wenet.TaskType;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.serviceproxy.ServiceBinder;

/**
 * The class used to interact with the WeNet task manager.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ProxyGen
public interface WeNetTaskManagerService {

	/**
	 * The address of this service.
	 */
	String ADDRESS = "wenet_common.service.taskManager";

	/**
	 * Create a proxy of the {@link WeNetTaskManagerService}.
	 *
	 * @param vertx where the service has to be used.
	 *
	 * @return the task.
	 */
	static WeNetTaskManagerService createProxy(Vertx vertx) {

		return new WeNetTaskManagerServiceVertxEBProxy(vertx, WeNetTaskManagerService.ADDRESS);
	}

	/**
	 * Register this service.
	 *
	 * @param vertx  that contains the event bus to use.
	 * @param client to do HTTP requests to other services.
	 * @param conf   configuration to use.
	 */
	static void register(Vertx vertx, WebClient client, JsonObject conf) {

		new ServiceBinder(vertx).setAddress(WeNetTaskManagerService.ADDRESS).register(WeNetTaskManagerService.class,
				new WeNetTaskManagerServiceImpl(client, conf));

	}

	/**
	 * Search for a {@link Task} in Json format.
	 *
	 * @param id              identifier of the task to get.
	 * @param retrieveHandler handler to manage the retrieve process.
	 */
	void retrieveJsonTask(@NotNull String id, @NotNull Handler<AsyncResult<JsonObject>> retrieveHandler);

	/**
	 * Return a task.
	 *
	 * @param id              identifier of the task to get.
	 * @param retrieveHandler handler to manage the retrieve process.
	 */
	@GenIgnore
	default void retrieveTask(@NotNull String id, @NotNull Handler<AsyncResult<Task>> retrieveHandler) {

		this.retrieveJsonTask(id, Service.handlerForModel(Task.class, retrieveHandler));

	}

	/**
	 * Create a {@link Task} in Json format.
	 *
	 * @param task          to create.
	 * @param createHandler handler to manage the creation process.
	 */
	void createTask(@NotNull JsonObject task, @NotNull Handler<AsyncResult<JsonObject>> createHandler);

	/**
	 * Create a task.
	 *
	 * @param task          to create.
	 * @param createHandler handler to manage the creation process.
	 */
	@GenIgnore
	default void createTask(@NotNull Task task, @NotNull Handler<AsyncResult<Task>> createHandler) {

		this.createTask(task.toJsonObject(), Service.handlerForModel(Task.class, createHandler));
	}

	/**
	 * Delete a task.
	 *
	 * @param id            identifier of the task to get.
	 * @param deleteHandler handler to manage the delete process.
	 */
	void deleteTask(@NotNull String id, @NotNull Handler<AsyncResult<JsonObject>> deleteHandler);

	/**
	 * Return a {@link TaskType} in Json format.
	 *
	 * @param id              identifier of the task to get.
	 * @param retrieveHandler handler to manage the retrieve process.
	 */
	void retrieveJsonTaskType(@NotNull String id, @NotNull Handler<AsyncResult<JsonObject>> retrieveHandler);

	/**
	 * Return a task type.
	 *
	 * @param id              identifier of the task to get.
	 * @param retrieveHandler handler to manage the retrieve process.
	 */
	@GenIgnore
	default void retrieveTaskType(@NotNull String id, @NotNull Handler<AsyncResult<TaskType>> retrieveHandler) {

		this.retrieveJsonTaskType(id, Service.handlerForModel(TaskType.class, retrieveHandler));

	}

	/**
	 * Create a {@link TaskType} in Json format.
	 *
	 * @param task          to create.
	 * @param createHandler handler to manage the creation process.
	 */
	void createTaskType(@NotNull JsonObject task, @NotNull Handler<AsyncResult<JsonObject>> createHandler);

	/**
	 * Create a task type.
	 *
	 * @param taskType      to create.
	 * @param createHandler handler to manage the creation process.
	 */
	@GenIgnore
	default void createTaskType(@NotNull TaskType taskType, @NotNull Handler<AsyncResult<TaskType>> createHandler) {

		this.createTaskType(taskType.toJsonObject(), Service.handlerForModel(TaskType.class, createHandler));

	}

	/**
	 * Delete a task type.
	 *
	 * @param id            identifier of the task to get.
	 * @param deleteHandler handler to manage the delete process.
	 */
	void deleteTaskType(@NotNull String id, @NotNull Handler<AsyncResult<JsonObject>> deleteHandler);

}
