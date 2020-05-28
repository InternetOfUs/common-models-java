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

import javax.validation.constraints.NotNull;

import eu.internetofus.common.vertx.ComponentClient;
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
  static WeNetTaskManagerService createProxy(final Vertx vertx) {

    return new WeNetTaskManagerServiceVertxEBProxy(vertx, WeNetTaskManagerService.ADDRESS);
  }

  /**
   * Register this service.
   *
   * @param vertx  that contains the event bus to use.
   * @param client to do HTTP requests to other services.
   * @param conf   configuration to use.
   */
  static void register(final Vertx vertx, final WebClient client, final JsonObject conf) {

    new ServiceBinder(vertx).setAddress(WeNetTaskManagerService.ADDRESS).register(WeNetTaskManagerService.class, new WeNetTaskManagerServiceImpl(client, conf));

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
  default void retrieveTask(@NotNull final String id, @NotNull final Handler<AsyncResult<Task>> retrieveHandler) {

    this.retrieveJsonTask(id, ComponentClient.handlerForModel(Task.class, retrieveHandler));

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
  default void createTask(@NotNull final Task task, @NotNull final Handler<AsyncResult<Task>> createHandler) {

    this.createTask(task.toJsonObject(), ComponentClient.handlerForModel(Task.class, createHandler));
  }

  /**
   * Update a task.
   *
   * @param id            identifier of the task to get.
   * @param task          the new values for the task.
   * @param updateHandler handler to manage the update process.
   */
  @GenIgnore
  default void updateTask(@NotNull final String id, @NotNull final Task task, @NotNull final Handler<AsyncResult<Task>> updateHandler) {

    this.updateJsonTask(id, task.toJsonObject(), ComponentClient.handlerForModel(Task.class, updateHandler));

  }

  /**
   * Update a {@link Task} in Json format.
   *
   * @param id            identifier of the task to get.
   * @param task          the new values for the task.
   * @param updateHandler handler to manage the update process.
   */
  void updateJsonTask(@NotNull String id, @NotNull JsonObject task, @NotNull Handler<AsyncResult<JsonObject>> updateHandler);

  /**
   * Delete a task.
   *
   * @param id            identifier of the task to get.
   * @param deleteHandler handler to manage the delete process.
   */
  void deleteTask(@NotNull String id, @NotNull Handler<AsyncResult<Void>> deleteHandler);

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
  default void retrieveTaskType(@NotNull final String id, @NotNull final Handler<AsyncResult<TaskType>> retrieveHandler) {

    this.retrieveJsonTaskType(id, ComponentClient.handlerForModel(TaskType.class, retrieveHandler));

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
  default void createTaskType(@NotNull final TaskType taskType, @NotNull final Handler<AsyncResult<TaskType>> createHandler) {

    this.createTaskType(taskType.toJsonObject(), ComponentClient.handlerForModel(TaskType.class, createHandler));

  }

  /**
   * Delete a task type.
   *
   * @param id            identifier of the task to get.
   * @param deleteHandler handler to manage the delete process.
   */
  void deleteTaskType(@NotNull String id, @NotNull Handler<AsyncResult<Void>> deleteHandler);

}
