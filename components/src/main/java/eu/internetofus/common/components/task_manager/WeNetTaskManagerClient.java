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

import eu.internetofus.common.vertx.ComponentClient;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import java.util.LinkedHashMap;
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
  public void addTransactionIntoTask(@NotNull final String taskId, @NotNull final JsonObject taskTransaction,
      @NotNull final Handler<AsyncResult<JsonObject>> handler) {

    this.post(taskTransaction, "/tasks", taskId, "/transactions").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addMessageIntoTransaction(@NotNull final String taskId, @NotNull final String taskTransactionId,
      @NotNull final JsonObject message, @NotNull final Handler<AsyncResult<JsonObject>> handler) {

    this.post(message, "/tasks", taskId, "/transactions", taskTransactionId, "/messages").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateTaskType(final String id, final JsonObject taskType,
      final Handler<AsyncResult<JsonObject>> handler) {

    this.put(taskType, "/taskTypes", id).onComplete(handler);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeTaskType(final String id, final JsonObject taskType,
      final Handler<AsyncResult<JsonObject>> handler) {

    this.patch(taskType, "/taskTypes", id).onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void getTasksPage(final String appId, final String requesterId, final String taskTypeId, final String goalName,
      final String goalDescription, final Long creationFrom, final Long creationTo, final Long updateFrom,
      final Long updateTo, final Boolean hasCloseTs, final Long closeFrom, final Long closeTo, final String order,
      final int offset, final int limit, @NotNull final Handler<AsyncResult<JsonObject>> handler) {

    final var params = new LinkedHashMap<String, String>();
    if (appId != null) {

      params.put("appId", appId);
    }
    if (requesterId != null) {

      params.put("requesterId", requesterId);
    }
    if (taskTypeId != null) {

      params.put("taskTypeId", taskTypeId);
    }
    if (goalName != null) {

      params.put("goalName", goalName);
    }
    if (goalDescription != null) {

      params.put("goalDescription", goalDescription);
    }
    if (creationFrom != null) {

      params.put("creationFrom", String.valueOf(creationFrom));
    }
    if (creationTo != null) {

      params.put("creationTo", String.valueOf(creationTo));
    }
    if (updateFrom != null) {

      params.put("updateFrom", String.valueOf(updateFrom));
    }
    if (updateTo != null) {

      params.put("updateTo", String.valueOf(updateTo));
    }
    if (hasCloseTs != null) {

      params.put("hasCloseTs", String.valueOf(hasCloseTs));
    }
    if (closeFrom != null) {

      params.put("closeFrom", String.valueOf(closeFrom));
    }
    if (closeTo != null) {

      params.put("closeTo", String.valueOf(closeTo));
    }
    if (order != null) {

      params.put("order", order);
    }
    params.put("offset", String.valueOf(offset));
    params.put("limit", String.valueOf(limit));
    this.getJsonObject(params, "/tasks").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void getTaskTypesPage(final String name, final String description, final String keywords, final String order,
      final int offset, final int limit, @NotNull final Handler<AsyncResult<JsonObject>> handler) {

    final var params = new LinkedHashMap<String, String>();
    if (name != null) {

      params.put("name", name);
    }
    if (description != null) {

      params.put("description", description);
    }
    if (keywords != null) {

      params.put("keywords", keywords);
    }
    if (order != null) {

      params.put("order", order);
    }
    params.put("offset", String.valueOf(offset));
    params.put("limit", String.valueOf(limit));
    this.getJsonObject(params, "/taskTypes").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void isTaskDefined(final String id, @NotNull final Handler<AsyncResult<Boolean>> handler) {

    this.head("/tasks", id).onComplete(handler);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void isTaskTypeDefined(final String id, @NotNull final Handler<AsyncResult<Boolean>> handler) {

    this.head("/taskTypes", id).onComplete(handler);
  }

}
