/*
 * -----------------------------------------------------------------------------
 *
 *   Copyright 2019 - 2022 UDT-IA, IIIA-CSIC
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

import eu.internetofus.common.components.Model;
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
   * Update the status of a task.
   *
   * @param status for the task.
   *
   * @return the future task status.
   */
  @GenIgnore
  default Future<TaskStatus> updateTaskStatus(final @NotNull TaskStatus status) {

    final Promise<JsonObject> promise = Promise.promise();
    this.updateTaskStatus(status.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), TaskStatus.class);

  }

  /**
   * Update the status of a task.
   *
   * @param status  for the task.
   * @param handler for the task status.
   */
  void updateTaskStatus(@NotNull JsonObject status, Handler<AsyncResult<JsonObject>> handler);

  /**
   * Get the posted task status.
   *
   * @return the future task status.
   */
  @GenIgnore
  default Future<List<TaskStatus>> getTaskStatus() {

    final Promise<JsonArray> promise = Promise.promise();
    this.getTaskStatus(promise);
    return Model.fromFutureJsonArray(promise.future(), TaskStatus.class);

  }

  /**
   * Get the status of a task.
   *
   * @param handler for the task status.
   */
  void getTaskStatus(Handler<AsyncResult<JsonArray>> handler);

  /**
   * Delete the posted task status.
   *
   * @return the future deleted task status.
   */
  @GenIgnore
  default Future<Void> deleteTaskStatus() {

    final Promise<Void> promise = Promise.promise();
    this.deleteTaskStatus(promise);
    return promise.future();

  }

  /**
   * Delete the status of a task.
   *
   * @param handler for the deleted task status.
   */
  void deleteTaskStatus(Handler<AsyncResult<Void>> handler);

}
