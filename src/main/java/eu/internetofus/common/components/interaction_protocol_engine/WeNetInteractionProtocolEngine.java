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

package eu.internetofus.common.components.interaction_protocol_engine;

import javax.validation.constraints.NotNull;

import eu.internetofus.common.components.incentive_server.Incentive;
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
 * The class used to interact with the WeNet interaction protocol engine.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ProxyGen
public interface WeNetInteractionProtocolEngine {

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_component.InteractionProtocolEngine";

  /**
   * Create a proxy of the {@link WeNetInteractionProtocolEngine}.
   *
   * @param vertx where the service has to be used.
   *
   * @return the task.
   */
  static WeNetInteractionProtocolEngine createProxy(final Vertx vertx) {

    return new WeNetInteractionProtocolEngineVertxEBProxy(vertx, WeNetInteractionProtocolEngine.ADDRESS);
  }

  /**
   * Register this service.
   *
   * @param vertx  that contains the event bus to use.
   * @param client to do HTTP requests to other services.
   * @param conf   configuration to use.
   */
  static void register(final Vertx vertx, final WebClient client, final JsonObject conf) {

    new ServiceBinder(vertx).setAddress(WeNetInteractionProtocolEngine.ADDRESS).register(WeNetInteractionProtocolEngine.class, new WeNetInteractionProtocolEngineClient(client, conf));

  }

  /**
   * Send a message to be processed.
   *
   * @param message     to be processed.
   * @param sendHandler handler to send process.
   */
  void sendMessage(@NotNull JsonObject message, @NotNull Handler<AsyncResult<JsonObject>> sendHandler);

  /**
   * Send a message to be processed.
   *
   * @param message     to be processed.
   * @param sendHandler handler to send process.
   */
  @GenIgnore
  default void sendMessage(@NotNull final Message message, @NotNull final Handler<AsyncResult<Message>> sendHandler) {

    this.sendMessage(message.toJsonObject(), ComponentClient.handlerForModel(Message.class, sendHandler));
  }

  /**
   * Send a incentive to be processed.
   *
   * @param incentive   to be processed.
   * @param sendHandler handler to send process.
   */
  void sendIncentive(@NotNull JsonObject incentive, @NotNull Handler<AsyncResult<JsonObject>> sendHandler);

  /**
   * Send a incentive to be processed.
   *
   * @param incentive   to be processed.
   * @param sendHandler handler to send process.
   */
  @GenIgnore
  default void sendIncentive(@NotNull final Incentive incentive, @NotNull final Handler<AsyncResult<Incentive>> sendHandler) {

    this.sendIncentive(incentive.toJsonObject(), ComponentClient.handlerForModel(Incentive.class, sendHandler));
  }

}
