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
 * The services to interact with the {@link WeNetServiceSimulator}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ProxyGen
public interface WeNetServiceSimulator {

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_component.ServiceSimulator";

  /**
   * Create a proxy of the {@link WeNetService}.
   *
   * @param vertx where the service has to be used.
   *
   * @return the task.
   */
  static WeNetServiceSimulator createProxy(final Vertx vertx) {

    return new WeNetServiceSimulatorVertxEBProxy(vertx, WeNetServiceSimulator.ADDRESS);
  }

  /**
   * Register this service.
   *
   * @param vertx  that contains the event bus to use.
   * @param client to do HTTP requests to other services.
   * @param conf   configuration to use.
   */
  static void register(final Vertx vertx, final WebClient client, final JsonObject conf) {

    new ServiceBinder(vertx).setAddress(WeNetServiceSimulator.ADDRESS).register(WeNetServiceSimulator.class, new WeNetServiceSimulatorClient(client, conf));

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
   * Defined method only for testing and can store an APP.
   *
   * @param app           to create.
   * @param createHandler handler to manage the creation process.
   */
  void createApp(@NotNull JsonObject app, @NotNull Handler<AsyncResult<JsonObject>> createHandler);

  /**
   * Defined method only for testing and can store an APP.
   *
   * @param app           to create.
   * @param createHandler handler to manage the creation process.
   */
  @GenIgnore
  default void createApp(@NotNull final App app, @NotNull final Handler<AsyncResult<App>> createHandler) {

    this.createApp(app.toJsonObject(), ComponentClient.handlerForModel(App.class, createHandler));

  }

  /**
   * Defined method only for testing and can delete an APP.
   *
   * @param id            identifier of the application to remove.
   * @param deleteHandler handler to manage the delete process.
   */
  void deleteApp(@NotNull String id, @NotNull Handler<AsyncResult<Void>> deleteHandler);

  /**
   * Return all the callbacks messages received by an {@link App}.
   *
   * @param id              identifier of the app to get the callback messages.
   * @param retrieveHandler handler to manage the retrieve process.
   */
  void retrieveJsonCallbacks(@NotNull String id, @NotNull Handler<AsyncResult<JsonArray>> retrieveHandler);

  /**
   * Add a callback message for an application.
   *
   * @param appId   identifier of the application to add the message.
   * @param message callback message.
   * @param handler to manage the adding.
   */
  void addJsonCallBack(String appId, JsonObject message, Handler<AsyncResult<JsonObject>> handler);

  /**
   * Delete all the callbacks for an application.
   *
   * @param appId   identifier of the application to delete all the callbacks.
   * @param handler to manage the delete.
   */
  void deleteCallbacks(String appId, Handler<AsyncResult<Void>> handler);

  /**
   * Return all the users users received by an {@link App}.
   *
   * @param id              identifier of the app to get the user users.
   * @param retrieveHandler handler to manage the retrieve process.
   */
  void retrieveJsonArrayAppUserIds(@NotNull String id, @NotNull Handler<AsyncResult<JsonArray>> retrieveHandler);

  /**
   * Add users into an application.
   *
   * @param appId   identifier of the application to add the users.
   * @param users   to add.
   * @param handler to manage the adding.
   */
  void addUsers(String appId, JsonArray users, Handler<AsyncResult<JsonArray>> handler);

  /**
   * Delete all the users of an application.
   *
   * @param appId   identifier of the application to delete all the users.
   * @param handler to manage the delete.
   */
  void deleteUsers(String appId, Handler<AsyncResult<Void>> handler);

}
