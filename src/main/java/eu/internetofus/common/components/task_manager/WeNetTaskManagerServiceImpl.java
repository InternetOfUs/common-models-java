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

import eu.internetofus.common.vertx.ComponentClient;
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
public class WeNetTaskManagerServiceImpl extends ComponentClient implements WeNetTaskManagerService {

  /**
   * Create a new service to interact with the WeNet task manager.
   *
   * @param client to interact with the other modules.
   * @param conf   configuration.
   */
  public WeNetTaskManagerServiceImpl(final WebClient client, final JsonObject conf) {

    super(client, conf.getString("taskManager", "https://wenet.u-hopper.com/task_manager"));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createTask(final JsonObject task, final Handler<AsyncResult<JsonObject>> createHandler) {

    this.post(task, createHandler, "/tasks");

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveJsonTask(final String id, final Handler<AsyncResult<JsonObject>> retrieveHandler) {

    this.getJsonObject(retrieveHandler, "/tasks", id);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteTask(final String id, final Handler<AsyncResult<Void>> deleteHandler) {

    this.delete(deleteHandler, "/tasks", id);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createTaskType(final JsonObject taskType, final Handler<AsyncResult<JsonObject>> createHandler) {

    this.post(taskType, createHandler, "/tasks/types");

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveJsonTaskType(final String id, final Handler<AsyncResult<JsonObject>> retrieveHandler) {

    this.getJsonObject(retrieveHandler, "/tasks/types", id);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteTaskType(final String id, final Handler<AsyncResult<Void>> deleteHandler) {

    this.delete(deleteHandler, "/tasks/types", id);

  }

  /**
   * {@inheridDoc}
   */
  @Override
  public void updateJsonTask(final String id, final JsonObject task, final Handler<AsyncResult<JsonObject>> updateHandler) {

    this.put(task, updateHandler, "/tasks", id);

  }

}
