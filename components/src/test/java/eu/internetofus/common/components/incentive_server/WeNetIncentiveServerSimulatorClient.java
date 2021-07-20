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

package eu.internetofus.common.components.incentive_server;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import javax.validation.constraints.NotNull;

/**
 * Service used to interact with the {@link WeNetIncentiveServerSimulator}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetIncentiveServerSimulatorClient extends WeNetIncentiveServerClient
    implements WeNetIncentiveServerSimulator, WeNetIncentiveServer {

  /**
   * Create a new service to interact with the WeNet incentive server.
   *
   * @param client to interact with the other modules.
   * @param conf   configuration.
   */
  public WeNetIncentiveServerSimulatorClient(final WebClient client, final JsonObject conf) {

    super(client, conf);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<JsonObject> updateTaskTransactionStatus(final @NotNull TaskTransactionStatusBody status) {

    final Promise<JsonObject> promise = Promise.promise();
    this.updateTaskTransactionStatus(status.toJsonObject(), promise);
    return promise.future();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void getTaskTransactionStatus(final Handler<AsyncResult<JsonArray>> handler) {

    this.getJsonArray("/Tasks/TaskTransactionStatus/").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteTaskTransactionStatus(final Handler<AsyncResult<Void>> handler) {

    this.delete("/Tasks/TaskTransactionStatus/").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<JsonObject> updateTaskTypeStatus(final @NotNull TaskTypeStatusBody status) {

    final Promise<JsonObject> promise = Promise.promise();
    this.updateTaskTypeStatus(status.toJsonObject(), promise);
    return promise.future();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void getTaskTypeStatus(final Handler<AsyncResult<JsonArray>> handler) {

    this.getJsonArray("/Tasks/TaskTypeStatus/").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteTaskTypeStatus(final Handler<AsyncResult<Void>> handler) {

    this.delete("/Tasks/TaskTypeStatus/").onComplete(handler);

  }

}
