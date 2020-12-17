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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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

import eu.internetofus.common.components.Model;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
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
public interface WeNetTaskManager {

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_component.taskManager";

  /**
   * Create a proxy of the {@link WeNetTaskManager}.
   *
   * @param vertx where the service has to be used.
   *
   * @return the task.
   */
  static WeNetTaskManager createProxy(final Vertx vertx) {

    return new WeNetTaskManagerVertxEBProxy(vertx, WeNetTaskManager.ADDRESS);
  }

  /**
   * Register this service.
   *
   * @param vertx  that contains the event bus to use.
   * @param client to do HTTP requests to other services.
   * @param conf   configuration to use.
   */
  static void register(final Vertx vertx, final WebClient client, final JsonObject conf) {

    new ServiceBinder(vertx).setAddress(WeNetTaskManager.ADDRESS).register(WeNetTaskManager.class, new WeNetTaskManagerClient(client, conf));

  }

  /**
   * Search for a {@link Task} in Json format.
   *
   * @param id      identifier of the task to get.
   * @param handler for the retrieved task.
   */
  void retrieveTask(@NotNull String id, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Return a task.
   *
   * @param id identifier of the task to get.
   *
   * @return the future with the retrieved task.
   */
  @GenIgnore
  default Future<Task> retrieveTask(@NotNull final String id) {

    final Promise<JsonObject> promise = Promise.promise();
    this.retrieveTask(id, promise);
    return Model.fromFutureJsonObject(promise.future(), Task.class);

  }

  /**
   * Create a {@link Task} in Json format.
   *
   * @param task    to create.
   * @param handler to the created task.
   */
  void createTask(@NotNull JsonObject task, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Create a task.
   *
   * @param task to create.
   *
   * @return the future created task.
   */
  @GenIgnore
  default Future<Task> createTask(@NotNull final Task task) {

    final Promise<JsonObject> promise = Promise.promise();
    this.createTask(task.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), Task.class);

  }

  /**
   * Merge a {@link Task} in Json format.
   *
   * @param id      identifier of the task to get.
   * @param task    the new values for the task.
   * @param handler to the merged task.
   */
  void mergeTask(@NotNull String id, @NotNull JsonObject task, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Merge a task.
   *
   * @param id   identifier of the task to get.
   * @param task the new values for the task.
   *
   * @return the future merged task.
   */
  @GenIgnore
  default Future<Task> mergeTask(@NotNull final String id, @NotNull final Task task) {

    final Promise<JsonObject> promise = Promise.promise();
    this.mergeTask(id, task.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), Task.class);

  }

  /**
   * Delete a task.
   *
   * @param id      identifier of the task to get.
   * @param handler to inform if the task is deleted task.
   */
  void deleteTask(@NotNull String id, @NotNull Handler<AsyncResult<Void>> handler);

  /**
   * Delete a task.
   *
   * @param id identifier of the task to get.
   *
   * @return the future that inform if the task is deleted task.
   */
  @GenIgnore
  default Future<Void> deleteTask(@NotNull final String id) {

    final Promise<Void> promise = Promise.promise();
    this.deleteTask(id, promise);
    return promise.future();

  }

  /**
   * Return a {@link TaskType} in Json format.
   *
   * @param id      identifier of the task to get.
   * @param handler to the found task type.
   */
  void retrieveTaskType(@NotNull String id, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Return a task type.
   *
   * @param id identifier of the task to get.
   *
   * @return the future found task type.
   */
  @GenIgnore
  default Future<TaskType> retrieveTaskType(@NotNull final String id) {

    final Promise<JsonObject> promise = Promise.promise();
    this.retrieveTaskType(id, promise);
    return Model.fromFutureJsonObject(promise.future(), TaskType.class);

  }

  /**
   * Create a {@link TaskType} in Json format.
   *
   * @param task    to create.
   * @param handler to the created task type.
   */
  void createTaskType(@NotNull JsonObject task, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Create a task type.
   *
   * @param taskType to create.
   *
   * @return the future created task type.
   */
  @GenIgnore
  default Future<TaskType> createTaskType(@NotNull final TaskType taskType) {

    final Promise<JsonObject> promise = Promise.promise();
    this.createTaskType(taskType.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), TaskType.class);

  }

  /**
   * Delete a task type.
   *
   * @param id      identifier of the task to get.
   * @param handler to inform if the task type is deleted.
   */
  void deleteTaskType(@NotNull String id, @NotNull Handler<AsyncResult<Void>> handler);

  /**
   * Delete a task type.
   *
   * @param id identifier of the task to get.
   *
   * @return the future to inform if the task type is deleted.
   */
  @GenIgnore
  default Future<Void> deleteTaskType(@NotNull final String id) {

    final Promise<Void> promise = Promise.promise();
    this.deleteTaskType(id, promise);
    return promise.future();

  }

  /**
   * Do a transaction over a task.
   *
   * @param taskTransaction to do.
   * @param handler         for the transaction that is try to do.
   */
  void doTaskTransaction(@NotNull JsonObject taskTransaction, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Do a transaction over a task.
   *
   * @param taskTransaction to do.
   *
   * @return the future with the transaction that is try to do.
   */
  @GenIgnore
  default Future<TaskTransaction> doTaskTransaction(@NotNull final TaskTransaction taskTransaction) {

    final Promise<JsonObject> promise = Promise.promise();
    this.doTaskTransaction(taskTransaction.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), TaskTransaction.class);

  }

}
