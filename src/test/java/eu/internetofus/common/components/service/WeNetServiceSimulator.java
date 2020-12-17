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
import io.vertx.core.Future;
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
   * @param id identifier of the app to get.
   *
   * @return the found application.
   */
  Future<JsonObject> retrieveJsonApp(@NotNull String id);

  /**
   * Return an application.
   *
   * @param id identifier of the app to get.
   *
   * @return the found application.
   */
  @GenIgnore
  default Future<App> retrieveApp(@NotNull final String id) {

    return Model.fromFutureJsonObject(this.retrieveJsonApp(id), App.class);

  }

  /**
   * Defined method only for testing and can store an APP.
   *
   * @param app to create.
   *
   * @return the created application.
   */
  Future<JsonObject> createApp(@NotNull JsonObject app);

  /**
   * Defined method only for testing and can store an APP.
   *
   * @param app to create.
   *
   * @return the created application.
   */
  @GenIgnore
  default Future<App> createApp(@NotNull final App app) {

    return Model.fromFutureJsonObject(this.createApp(app.toJsonObject()), App.class);

  }

  /**
   * Defined method only for testing and can delete an APP.
   *
   * @param id identifier of the application to remove.
   *
   * @return the deleted application.
   */
  Future<Void> deleteApp(@NotNull String id);

  /**
   * Return all the callbacks messages received by an {@link App}.
   *
   * @param id identifier of the app to get the callback messages.
   *
   * @return the callback messsages.
   */
  Future<JsonArray> retrieveJsonCallbacks(@NotNull String id);

  /**
   * Add a callback message for an application.
   *
   * @param appId   identifier of the application to add the message.
   * @param message callback message.
   *
   * @return the added callback.
   */
  Future<JsonObject> addJsonCallBack(String appId, JsonObject message);

  /**
   * Delete all the callbacks for an application.
   *
   * @param appId identifier of the application to delete all the callbacks.
   *
   * @return the deleted callbacks result.
   */
  Future<Void> deleteCallbacks(String appId);

  /**
   * Return all the users users received by an {@link App}.
   *
   * @param id identifier of the app to get the user users.
   *
   * @return the appliction users.
   */
  Future<JsonArray> retrieveAppUserIds(@NotNull String id);

  /**
   * Add users into an application.
   *
   * @param appId identifier of the application to add the users.
   * @param users to add.
   *
   * @return the added user.
   */
  Future<JsonArray> addUsers(String appId, JsonArray users);

  /**
   * Delete all the users of an application.
   *
   * @param appId identifier of the application to delete all the users.
   *
   * @return the result of the deleted users.
   */
  Future<Void> deleteUsers(String appId);

}
