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
