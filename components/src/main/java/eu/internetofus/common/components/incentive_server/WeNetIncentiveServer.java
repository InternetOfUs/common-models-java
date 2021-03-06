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

import eu.internetofus.common.components.WeNetComponent;
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
 * The methods necessaries to interact with the interaction server.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ProxyGen
public interface WeNetIncentiveServer extends WeNetComponent {

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_component.IncentiveServer";

  /**
   * Create a proxy of the {@link WeNetIncentiveServer}.
   *
   * @param vertx where the service has to be used.
   *
   * @return the task.
   */
  static WeNetIncentiveServer createProxy(final Vertx vertx) {

    return new WeNetIncentiveServerVertxEBProxy(vertx, WeNetIncentiveServer.ADDRESS);
  }

  /**
   * Register this service.
   *
   * @param vertx  that contains the event bus to use.
   * @param client to do HTTP requests to other services.
   * @param conf   configuration to use.
   */
  static void register(final Vertx vertx, final WebClient client, final JsonObject conf) {

    new ServiceBinder(vertx).setAddress(WeNetIncentiveServer.ADDRESS).register(WeNetIncentiveServer.class,
        new WeNetIncentiveServerClient(client, conf));

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
   * Update the status of a task transaction.
   *
   * @param status  for the task transaction.
   * @param handler for the task transaction status.
   */
  void updateTaskTransactionStatus(@NotNull JsonObject status, Handler<AsyncResult<JsonObject>> handler);

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
   * Update the status of a task type.
   *
   * @param status  for the task type.
   * @param handler for the task type status.
   */
  void updateTaskTypeStatus(@NotNull JsonObject status, Handler<AsyncResult<JsonObject>> handler);

}
