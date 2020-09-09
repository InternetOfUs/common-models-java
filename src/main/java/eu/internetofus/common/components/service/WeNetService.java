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

package eu.internetofus.common.components.service;

import javax.validation.constraints.NotNull;

import eu.internetofus.common.vertx.ComponentClient;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.serviceproxy.ServiceBinder;

/**
 * The class used to interact with the WeNet interaction protocol engine.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ProxyGen
public interface WeNetService {

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_component.Service";

  /**
   * Create a proxy of the {@link WeNetService}.
   *
   * @param vertx where the service has to be used.
   *
   * @return the task.
   */
  static WeNetService createProxy(final Vertx vertx) {

    return new WeNetServiceVertxEBProxy(vertx, WeNetService.ADDRESS);
  }

  /**
   * Register this service.
   *
   * @param vertx  that contains the event bus to use.
   * @param client to do HTTP requests to other services.
   * @param conf   configuration to use.
   */
  static void register(final Vertx vertx, final WebClient client, final JsonObject conf) {

    new ServiceBinder(vertx).setAddress(WeNetService.ADDRESS).register(WeNetService.class, new WeNetServiceClient(client, conf));

  }

  /**
   * Return an {@link App} in JSON format.
   *
   * @param id              identifier of the app to get.
   * @param retrieveHandler handler to manage the retrieve process.
   */
  void retrieveJsonApp(@NotNull String id, @NotNull Handler<AsyncResult<JsonObject>> retrieveHandler);

  /**
   * Return an application.
   *
   * @param id              identifier of the app to get.
   * @param retrieveHandler handler to manage the retrieve process.
   */
  @GenIgnore
  default void retrieveApp(@NotNull final String id, @NotNull final Handler<AsyncResult<App>> retrieveHandler) {

    this.retrieveJsonApp(id, ComponentClient.handlerForModel(App.class, retrieveHandler));

  }

  /**
   * Return the identifiers of the users that are defined on an application.
   *
   * @param id              identifier of the app to get the users.
   * @param retrieveHandler handler to manage the retrieve process.
   */
  void retrieveJsonArrayAppUserIds(@NotNull String id, @NotNull Handler<AsyncResult<JsonArray>> retrieveHandler);

}
