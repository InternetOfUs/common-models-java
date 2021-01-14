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

import eu.internetofus.common.vertx.ComponentClient;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import javax.validation.constraints.NotNull;

/**
 * The implementation of the {@link WeNetTaskManager}.
 *
 *
 * @see WeNetTaskManager
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetTaskManagerClient extends ComponentClient implements WeNetTaskManager {

  /**
   * The default URL to bind the client.
   */
  public static final String DEFAULT_TASK_MANAGER_API_URL = "https://wenet.u-hopper.com/prod/task_manager";

  /**
   * The name of the configuration property that contains the URL to the task
   * manager API.
   */
  public static final String TASK_MANAGER_CONF_KEY = "taskManager";

  /**
   * Create a new service to interact with the WeNet task manager.
   *
   * @param client to interact with the other modules.
   * @param conf   configuration.
   */
  public WeNetTaskManagerClient(final WebClient client, final JsonObject conf) {

    super(client, conf.getString(TASK_MANAGER_CONF_KEY, DEFAULT_TASK_MANAGER_API_URL));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createTask(final JsonObject task, final Handler<AsyncResult<JsonObject>> handler) {

    this.post(task, "/tasks").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveTask(final String id, final Handler<AsyncResult<JsonObject>> handler) {

    this.getJsonObject("/tasks", id).onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteTask(final String id, final Handler<AsyncResult<Void>> handler) {

    this.delete("/tasks", id).onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createTaskType(final JsonObject taskType, final Handler<AsyncResult<JsonObject>> handler) {

    this.post(taskType, "/taskTypes").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveTaskType(final String id, final Handler<AsyncResult<JsonObject>> handler) {

    this.getJsonObject("/taskTypes", id).onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteTaskType(final String id, final Handler<AsyncResult<Void>> handler) {

    this.delete("/taskTypes", id).onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateTask(final String id, final JsonObject task, final Handler<AsyncResult<JsonObject>> handler) {

    this.put(task, "/tasks", id).onComplete(handler);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeTask(final String id, final JsonObject task, final Handler<AsyncResult<JsonObject>> handler) {

    this.patch(task, "/tasks", id).onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void doTaskTransaction(final JsonObject taskTransaction, final Handler<AsyncResult<JsonObject>> handler) {

    this.post(taskTransaction, "/tasks/transactions").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addTransactionIntoTask(@NotNull String taskId, @NotNull JsonObject taskTransaction,
      @NotNull Handler<AsyncResult<JsonObject>> handler) {

    this.post(taskTransaction, "/tasks", taskId, "/transactions").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addMessageIntoTransaction(@NotNull String taskId, @NotNull String taskTransactionId,
      @NotNull JsonObject message, @NotNull Handler<AsyncResult<JsonObject>> handler) {

    this.post(message, "/tasks", taskId, "/transactions", taskTransactionId, "/messages").onComplete(handler);

  }

}
