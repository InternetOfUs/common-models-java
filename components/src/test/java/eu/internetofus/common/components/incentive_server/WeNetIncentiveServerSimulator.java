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

import eu.internetofus.common.model.Model;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.serviceproxy.ServiceBinder;
import java.util.List;
import javax.validation.constraints.NotNull;

/**
 * The services to interact with the {@link WeNetIncentiveServerSimulator}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ProxyGen
public interface WeNetIncentiveServerSimulator {

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_component.IncentiveServerSimulator";

  /**
   * Create a proxy of the {@link WeNetIncentiveServer}.
   *
   * @param vertx where the service has to be used.
   *
   * @return the task.
   */
  static WeNetIncentiveServerSimulator createProxy(final Vertx vertx) {

    return new WeNetIncentiveServerSimulatorVertxEBProxy(vertx, WeNetIncentiveServerSimulator.ADDRESS);
  }

  /**
   * Register this service.
   *
   * @param vertx  that contains the event bus to use.
   * @param client to do HTTP requests to other services.
   * @param conf   configuration to use.
   */
  static void register(final Vertx vertx, final WebClient client, final JsonObject conf) {

    new ServiceBinder(vertx).setAddress(WeNetIncentiveServerSimulator.ADDRESS)
        .register(WeNetIncentiveServerSimulator.class, new WeNetIncentiveServerSimulatorClient(client, conf));

  }

  /**
   * Update the status of a task transaction.
   *
   * @param status for the task transaction.
   *
   * @return the future task transaction status.
   */
  @GenIgnore
  default Future<JsonObject> updateTaskTransactionStatus(final @NotNull TaskTransactionStatusBody status) {

    final Promise<JsonObject> promise = Promise.promise();
    this.updateTaskTransactionStatus(status.toJsonObject(), promise);
    return promise.future();

  }

  /**
   * Update the status of a task.
   *
   * @param status  for the task.
   * @param handler for the task status.
   */
  void updateTaskTransactionStatus(@NotNull JsonObject status, Handler<AsyncResult<JsonObject>> handler);

  /**
   * Get the posted task transaction status.
   *
   * @return the future task transaction status.
   */
  @GenIgnore
  default Future<List<TaskTransactionStatusBody>> getTaskTransactionStatus() {

    final Promise<JsonArray> promise = Promise.promise();
    this.getTaskTransactionStatus(promise);
    return Model.fromFutureJsonArray(promise.future(), TaskTransactionStatusBody.class);

  }

  /**
   * Get the status of a task transaction.
   *
   * @param handler for the task transaction status.
   */
  void getTaskTransactionStatus(Handler<AsyncResult<JsonArray>> handler);

  /**
   * Delete the posted task transaction status.
   *
   * @return the future deleted task transaction status.
   */
  @GenIgnore
  default Future<Void> deleteTaskTransactionStatus() {

    final Promise<Void> promise = Promise.promise();
    this.deleteTaskTransactionStatus(promise);
    return promise.future();

  }

  /**
   * Delete the status of a task transaction.
   *
   * @param handler for the deleted task transaction status.
   */
  void deleteTaskTransactionStatus(Handler<AsyncResult<Void>> handler);

  /**
   * Update the status of a task type.
   *
   * @param status for the task type.
   *
   * @return the future task type status.
   */
  @GenIgnore
  default Future<JsonObject> updateTaskTypeStatus(final @NotNull TaskTypeStatusBody status) {

    final Promise<JsonObject> promise = Promise.promise();
    this.updateTaskTypeStatus(status.toJsonObject(), promise);
    return promise.future();

  }

  /**
   * Update the status of a task.
   *
   * @param status  for the task.
   * @param handler for the task status.
   */
  void updateTaskTypeStatus(@NotNull JsonObject status, Handler<AsyncResult<JsonObject>> handler);

  /**
   * Get the posted task type status.
   *
   * @return the future task type status.
   */
  @GenIgnore
  default Future<List<TaskTypeStatusBody>> getTaskTypeStatus() {

    final Promise<JsonArray> promise = Promise.promise();
    this.getTaskTypeStatus(promise);
    return Model.fromFutureJsonArray(promise.future(), TaskTypeStatusBody.class);

  }

  /**
   * Get the status of a task type.
   *
   * @param handler for the task type status.
   */
  void getTaskTypeStatus(Handler<AsyncResult<JsonArray>> handler);

  /**
   * Delete the posted task type status.
   *
   * @return the future deleted task type status.
   */
  @GenIgnore
  default Future<Void> deleteTaskTypeStatus() {

    final Promise<Void> promise = Promise.promise();
    this.deleteTaskTypeStatus(promise);
    return promise.future();

  }

  /**
   * Delete the status of a task type.
   *
   * @param handler for the deleted task type status.
   */
  void deleteTaskTypeStatus(Handler<AsyncResult<Void>> handler);

}
