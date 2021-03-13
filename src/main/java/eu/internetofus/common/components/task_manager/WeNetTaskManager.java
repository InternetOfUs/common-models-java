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

import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.WeNetComponent;
import eu.internetofus.common.components.service.Message;
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
import javax.validation.constraints.NotNull;

/**
 * The class used to interact with the WeNet task manager.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ProxyGen
public interface WeNetTaskManager extends WeNetComponent {

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_component.taskManager";

  /**
   * The identifier of the task type that contains the hardcoded version of the
   * dinner protocol.
   */
  String HARDCODED_DINNER_TASK_TYPE_ID = "1";

  /**
   * The identifier of the task type that contains the question and answer
   * protocol.
   */
  String QUESTION_AND_ANSWER_TASK_TYPE_ID = "ask4help";

  /**
   * The identifier of the task type that contains the echo protocol with norms.
   */
  String ECHO_V1_TASK_TYPE_ID = "wenet_echo_v1";

  /**
   * The identifier of the task type that contains the eat together protocol with
   * norms.
   */
  String EAT_TOGETHER_WITH_NORMS_V1_TASK_TYPE_ID = "wenet_eat_together_with_norms_v1";

  /**
   * The identifier of the task type that contains the question and answer
   * protocol done with norms.
   */
  String QUESTION_AND_ANSWER_WITH_NORMS_V1_TASK_TYPE_ID = "wenet_ask_4_help_with_norms_v1";

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

    new ServiceBinder(vertx).setAddress(WeNetTaskManager.ADDRESS).register(WeNetTaskManager.class,
        new WeNetTaskManagerClient(client, conf));

  }

  /**
   * {@inheritDoc}
   *
   * ATTENTION: You must to maintains this method to guarantee that VertX
   * generates the code for this method.
   */
  @Override
  void obtainApiUrl(final Handler<AsyncResult<String>> handler);

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
   * Update a {@link Task} in Json format.
   *
   * @param id      identifier of the task to get.
   * @param task    the new values for the task.
   * @param handler to the updated task.
   */
  void updateTask(@NotNull String id, @NotNull JsonObject task, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Update a task.
   *
   * @param id   identifier of the task to get.
   * @param task the new values for the task.
   *
   * @return the future updated task.
   */
  @GenIgnore
  default Future<Task> updateTask(@NotNull final String id, @NotNull final Task task) {

    final Promise<JsonObject> promise = Promise.promise();
    this.updateTask(id, task.toJsonObject(), promise);
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

  /**
   * Do a transaction over a task.
   *
   * @param taskId          identifier of the task.
   * @param taskTransaction to do.
   * @param handler         for the added transaction.
   */
  void addTransactionIntoTask(@NotNull String taskId, @NotNull JsonObject taskTransaction,
      @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Add a transaction into a task.
   *
   * @param taskId          identifier of the task.
   * @param taskTransaction to add.
   *
   * @return the future with the added transaction.
   */
  @GenIgnore
  default Future<TaskTransaction> addTransactionIntoTask(@NotNull final String taskId,
      @NotNull final TaskTransaction taskTransaction) {

    final Promise<JsonObject> promise = Promise.promise();
    this.addTransactionIntoTask(taskId, taskTransaction.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), TaskTransaction.class);

  }

  /**
   * Do a transaction over a task.
   *
   * @param taskId            identifier of the task where is the transaction.
   * @param taskTransactionId identifier of the task transaction to add the
   *                          message.
   * @param message           to do.
   * @param handler           for the added transaction.
   */
  void addMessageIntoTransaction(@NotNull String taskId, @NotNull String taskTransactionId, @NotNull JsonObject message,
      @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Add a transaction into a task.
   *
   * @param taskId            identifier of the task where is the transaction.
   * @param taskTransactionId identifier of the task transaction to add the
   *                          message.
   * @param message           to add.
   *
   * @return the future with the added transaction.
   */
  @GenIgnore
  default Future<Message> addMessageIntoTransaction(@NotNull final String taskId,
      @NotNull final String taskTransactionId, @NotNull final Message message) {

    final Promise<JsonObject> promise = Promise.promise();
    this.addMessageIntoTransaction(taskId, taskTransactionId, message.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), Message.class);

  }

}
