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

/**
 * The services to interact with the mocked version of the {@link WeNetService}.
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
   * Create a proxy of the {@link WeNetServiceSimulator}.
   *
   * @param vertx where the service has to be used.
   *
   * @return the task.
   */
  static WeNetServiceSimulator createProxy(final Vertx vertx) {

    return new WeNetServiceSimulatorVertxEBProxy(vertx, WeNetServiceSimulator.ADDRESS);
  }

  /**
   * Register this service for a {@link WeNetService} and a {@link WeNetServiceSimulator}.
   *
   * @param vertx  that contains the event bus to use.
   * @param client to do HTTP requests to other services.
   * @param conf   configuration to use.
   *
   * @see WeNetService#ADDRESS
   * @see WeNetServiceSimulator#ADDRESS
   */
  static void register(final Vertx vertx, final WebClient client, final JsonObject conf) {

    new ServiceBinder(vertx).setAddress(WeNetServiceSimulator.ADDRESS).register(WeNetServiceSimulator.class, new WeNetServiceSimulatorClient(client, conf));

  }

  /**
   * Return an {@link App} in JSON format.
   *
   * @param id      identifier of the app to get.
   * @param handler to manage the found application.
   */
  void retrieveApp(@NotNull String id, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Return an application.
   *
   * @param id identifier of the app to get.
   *
   * @return the found application.
   */
  @GenIgnore
  default Future<App> retrieveApp(@NotNull final String id) {

    final Promise<JsonObject> promise = Promise.promise();
    this.retrieveApp(id, promise);
    return Model.fromFutureJsonObject(promise.future(), App.class);

  }

  /**
   * Defined method only for testing and can store an APP.
   *
   * @param app     to create.
   * @param handler to manage the created application.
   */
  void createApp(@NotNull JsonObject app, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Defined method only for testing and can store an APP.
   *
   * @param app to create.
   *
   * @return the created application.
   */
  @GenIgnore
  default Future<App> createApp(@NotNull final App app) {

    final Promise<JsonObject> promise = Promise.promise();
    this.createApp(app.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), App.class);

  }

  /**
   * Defined method only for testing and can delete an APP.
   *
   * @param id      identifier of the application to remove.
   * @param handler to manage the deleted application.
   */
  void deleteApp(@NotNull String id, @NotNull Handler<AsyncResult<Void>> handler);

  /**
   * Defined method only for testing and can delete an APP.
   *
   * @param id identifier of the application to remove.
   *
   * @return the deleted application.
   */
  @GenIgnore
  default Future<Void> deleteApp(@NotNull final String id) {

    final Promise<Void> promise = Promise.promise();
    this.deleteApp(id, promise);
    return promise.future();

  }

  /**
   * Return all the callbacks messages received by an {@link App}.
   *
   * @param appId   identifier of the app to get the callback messages.
   * @param handler to manage the callbacks.
   */
  void retrieveCallbacks(@NotNull String appId, @NotNull Handler<AsyncResult<JsonArray>> handler);

  /**
   * Return all the callbacks messages received by an {@link App}.
   *
   * @param appId identifier of the app to get the callback messages.
   *
   * @return the callback messsages.
   */
  @GenIgnore
  default Future<JsonArray> retrieveCallbacks(@NotNull final String appId) {

    final Promise<JsonArray> promise = Promise.promise();
    this.retrieveCallbacks(appId, promise);
    return promise.future();

  }

  /**
   * Add a callback message for an application.
   *
   * @param appId   identifier of the application to add the message.
   * @param message callback message.
   * @param handler for the added callback.
   */
  void addCallBack(String appId, JsonObject message, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Add a callback message for an application.
   *
   * @param appId   identifier of the application to add the message.
   * @param message callback message.
   *
   * @return the added callback.
   */
  @GenIgnore
  default Future<JsonObject> addCallBack(final String appId, final JsonObject message) {

    final Promise<JsonObject> promise = Promise.promise();
    this.addCallBack(appId, message, promise);
    return promise.future();

  }

  /**
   * Delete all the callbacks for an application.
   *
   * @param appId   identifier of the application to delete all the callbacks.
   * @param handler to manage the deleted callbacks result.
   */
  void deleteCallbacks(String appId, @NotNull Handler<AsyncResult<Void>> handler);

  /**
   * Delete all the callbacks for an application.
   *
   * @param appId identifier of the application to delete all the callbacks.
   *
   * @return the deleted callbacks result.
   */
  @GenIgnore
  default Future<Void> deleteCallbacks(final String appId) {

    final Promise<Void> promise = Promise.promise();
    this.deleteCallbacks(appId, promise);
    return promise.future();

  }

  /**
   * Return all the users users received by an {@link App}.
   *
   * @param appId   identifier of the app to get the user users.
   * @param handler to manage the retrieved users identifiers.
   */
  void retrieveAppUserIds(@NotNull String appId, @NotNull Handler<AsyncResult<JsonArray>> handler);

  /**
   * Return all the users users received by an {@link App}.
   *
   * @param appId identifier of the app to get the user users.
   *
   * @return the application users.
   */
  @GenIgnore
  default Future<JsonArray> retrieveAppUserIds(@NotNull final String appId) {

    final Promise<JsonArray> promise = Promise.promise();
    this.retrieveAppUserIds(appId, promise);
    return promise.future();

  }

  /**
   * Add users into an application.
   *
   * @param appId   identifier of the application to add the users.
   * @param users   to add.
   * @param handler to manage the added user.
   */
  void addUsers(String appId, JsonArray users, @NotNull Handler<AsyncResult<JsonArray>> handler);

  /**
   * Add users into an application.
   *
   * @param appId identifier of the application to add the users.
   * @param users to add.
   *
   * @return the added user.
   */
  @GenIgnore
  default Future<JsonArray> addUsers(final String appId, final JsonArray users) {

    final Promise<JsonArray> promise = Promise.promise();
    this.addUsers(appId, users, promise);
    return promise.future();

  }

  /**
   * Delete all the users of an application.
   *
   * @param appId   identifier of the application to delete all the users.
   * @param handler to manage the removed users.
   */
  void deleteUsers(String appId, @NotNull Handler<AsyncResult<Void>> handler);

  /**
   * Delete all the users of an application.
   *
   * @param appId identifier of the application to delete all the users.
   *
   * @return the result of the deleted users.
   */
  @GenIgnore
  default Future<Void> deleteUsers(final String appId) {

    final Promise<Void> promise = Promise.promise();
    this.deleteUsers(appId, promise);
    return promise.future();

  }

}
