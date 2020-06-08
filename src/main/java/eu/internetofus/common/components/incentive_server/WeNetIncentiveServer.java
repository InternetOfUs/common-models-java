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

import javax.validation.constraints.NotNull;

import eu.internetofus.common.vertx.ComponentClient;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.serviceproxy.ServiceBinder;

/**
 * The methods necessaries to interact with the interaction server.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ProxyGen
public interface WeNetIncentiveServer {

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

    new ServiceBinder(vertx).setAddress(WeNetIncentiveServer.ADDRESS).register(WeNetIncentiveServer.class, new WeNetIncentiveServerClient(client, conf));

  }

  /**
   * Update the status of a task.
   *
   * @param status        for the task.
   * @param updateHandler handler to manage the update process.
   */
  @GenIgnore
  default void updateTaskStatus(final @NotNull TaskStatus status, @NotNull final Handler<AsyncResult<TaskStatus>> updateHandler) {

    this.updateJsonTaskStatus(status.toJsonObject(), ComponentClient.handlerForModel(TaskStatus.class, updateHandler));
  }

  /**
   * Update the status of a task.
   *
   * @param status        for the task.
   * @param updateHandler handler to manage the update process.
   */
  void updateJsonTaskStatus(@NotNull JsonObject status, @NotNull Handler<AsyncResult<JsonObject>> updateHandler);

}
