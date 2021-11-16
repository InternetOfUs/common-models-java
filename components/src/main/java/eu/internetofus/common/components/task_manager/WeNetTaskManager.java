/*
 * -----------------------------------------------------------------------------
 *
 * Copyright 2019 - 2022 UDT-IA, IIIA-CSIC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * -----------------------------------------------------------------------------
 */

package eu.internetofus.common.components.task_manager;

import eu.internetofus.common.components.WeNetComponent;
import eu.internetofus.common.components.models.Message;
import eu.internetofus.common.components.models.Task;
import eu.internetofus.common.components.models.TaskTransaction;
import eu.internetofus.common.components.models.TaskType;
import eu.internetofus.common.model.Model;
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
   * Update a {@link TaskType} in Json format.
   *
   * @param id       identifier of the taskType to get.
   * @param taskType the new values for the taskType.
   * @param handler  to the updated taskType.
   */
  void updateTaskType(@NotNull String id, @NotNull JsonObject taskType,
      @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Update a taskType.
   *
   * @param id       identifier of the taskType to get.
   * @param taskType the new values for the taskType.
   *
   * @return the future updated taskType.
   */
  @GenIgnore
  default Future<TaskType> updateTaskType(@NotNull final String id, @NotNull final TaskType taskType) {

    final Promise<JsonObject> promise = Promise.promise();
    this.updateTaskType(id, taskType.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), TaskType.class);

  }

  /**
   * Merge a {@link TaskType} in Json format.
   *
   * @param id       identifier of the taskType to get.
   * @param taskType the new values for the taskType.
   * @param handler  to the merged taskType.
   */
  void mergeTaskType(@NotNull String id, @NotNull JsonObject taskType,
      @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Merge a taskType.
   *
   * @param id       identifier of the taskType to get.
   * @param taskType the new values for the taskType.
   *
   * @return the future merged taskType.
   */
  @GenIgnore
  default Future<TaskType> mergeTaskType(@NotNull final String id, @NotNull final TaskType taskType) {

    final Promise<JsonObject> promise = Promise.promise();
    this.mergeTaskType(id, taskType.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), TaskType.class);

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

  /**
   * Search for some tasks that match some parameters.
   *
   * @param appId           application identifier to match for the tasks to
   *                        return.
   * @param requesterId     requester identifier to match for the tasks to return.
   * @param taskTypeId      task type identifier to match for the tasks to return.
   * @param goalName        pattern to match with the goal name of the tasks to
   *                        return.
   * @param goalDescription pattern to match with the goal description of the
   *                        tasks to return.
   * @param creationFrom    minimal creation time stamp of the tasks to return.
   * @param creationTo      maximal creation time stamp of the tasks to return.
   * @param updateFrom      minimal update time stamp of the tasks to return.
   * @param updateTo        maximal update time stamp of the tasks to return.
   * @param hasCloseTs      this is {@code true} if the tasks to return need to
   *                        have a {@link Task#closeTs}
   * @param closeFrom       minimal close time stamp of the tasks to return.
   * @param closeTo         maximal close time stamp of the tasks to return.
   * @param order           of the tasks to return.
   * @param offset          index of the first task to return.
   * @param limit           number maximum of tasks to return.
   * @param handler         for the tasks page.
   */
  void getTasksPage(final String appId, final String requesterId, final String taskTypeId, final String goalName,
      final String goalDescription, final Long creationFrom, final Long creationTo, final Long updateFrom,
      final Long updateTo, final Boolean hasCloseTs, final Long closeFrom, final Long closeTo, final String order,
      final int offset, final int limit, @NotNull final Handler<AsyncResult<JsonObject>> handler);

  /**
   * Search for some tasks that match some parameters.
   *
   * @param appId           application identifier to match for the tasks to
   *                        return.
   * @param requesterId     requester identifier to match for the tasks to return.
   * @param taskTypeId      task type identifier to match for the tasks to return.
   * @param goalName        pattern to match with the goal name of the tasks to
   *                        return.
   * @param goalDescription pattern to match with the goal description of the
   *                        tasks to return.
   * @param creationFrom    minimal creation time stamp of the tasks to return.
   * @param creationTo      maximal creation time stamp of the tasks to return.
   * @param updateFrom      minimal update time stamp of the tasks to return.
   * @param updateTo        maximal update time stamp of the tasks to return.
   * @param hasCloseTs      this is {@code true} if the tasks to return need to
   *                        have a {@link Task#closeTs}
   * @param closeFrom       minimal close time stamp of the tasks to return.
   * @param closeTo         maximal close time stamp of the tasks to return.
   * @param order           of the tasks to return.
   * @param offset          index of the first task to return.
   * @param limit           number maximum of tasks to return.
   *
   * @return the future with the tasks page.
   */
  @GenIgnore
  default Future<TasksPage> getTasksPage(final String appId, final String requesterId, final String taskTypeId,
      final String goalName, final String goalDescription, final Long creationFrom, final Long creationTo,
      final Long updateFrom, final Long updateTo, final Boolean hasCloseTs, final Long closeFrom, final Long closeTo,
      final String order, final int offset, final int limit) {

    final Promise<JsonObject> promise = Promise.promise();
    this.getTasksPage(appId, requesterId, taskTypeId, goalName, goalDescription, creationFrom, creationTo, updateFrom,
        updateTo, hasCloseTs, closeFrom, closeTo, order, offset, limit, promise);
    return Model.fromFutureJsonObject(promise.future(), TasksPage.class);

  }

  /**
   * Called when want to search for some task types.
   *
   * @param name        the pattern to match with the names of the task types.
   * @param description the pattern to match with the descriptions of the task
   *                    types.
   * @param keywords    the patterns to match with the keywords of the task types.
   * @param order       to return the found task types.
   * @param offset      index of the first task type to return.
   * @param limit       number maximum of task types to return.
   * @param handler     for the task types page.
   */
  void getTaskTypesPage(final String name, final String description, final String keywords, final String order,
      final int offset, final int limit, @NotNull final Handler<AsyncResult<JsonObject>> handler);

  /**
   * Called when want to search for some task types.
   *
   * @param name        the pattern to match with the names of the task types.
   * @param description the pattern to match with the descriptions of the task
   *                    types.
   * @param keywords    the patterns to match with the keywords of the task types.
   * @param order       to return the found task types.
   * @param offset      index of the first task type to return.
   * @param limit       number maximum of task types to return.
   *
   * @return the future with the task types page.
   */
  @GenIgnore
  default Future<TaskTypesPage> getTaskTypesPage(final String name, final String description, final String keywords,
      final String order, final int offset, final int limit) {

    final Promise<JsonObject> promise = Promise.promise();
    this.getTaskTypesPage(name, description, keywords, order, offset, limit, promise);
    return Model.fromFutureJsonObject(promise.future(), TaskTypesPage.class);

  }

  /**
   * Check if a task is defined.
   *
   * @param id identifier of the task to check if exist.
   *
   * @return the future that check if the task exist or not.
   */
  @GenIgnore
  default Future<Boolean> isTaskDefined(final String id) {

    final Promise<Boolean> promise = Promise.promise();
    this.isTaskDefined(id, promise);
    return promise.future();

  }

  /**
   * Check if a task is defined.
   *
   * @param id      identifier of the task to check if exist.
   * @param handler to manage if the task exist or not.
   */
  void isTaskDefined(final String id, @NotNull Handler<AsyncResult<Boolean>> handler);

  /**
   * Check if a task type is defined.
   *
   * @param id identifier of the task type to check if exist.
   *
   * @return the future that check if the task type exist or not.
   */
  @GenIgnore
  default Future<Boolean> isTaskTypeDefined(final String id) {

    final Promise<Boolean> promise = Promise.promise();
    this.isTaskTypeDefined(id, promise);
    return promise.future();

  }

  /**
   * Check if a task type is defined.
   *
   * @param id      identifier of the task type to check if exist.
   * @param handler to manage if the task type exist or not.
   */
  void isTaskTypeDefined(final String id, @NotNull Handler<AsyncResult<Boolean>> handler);

}
