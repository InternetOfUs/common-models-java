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

package eu.internetofus.common.vertx.basic;

import eu.internetofus.common.model.Model;
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
 * The class used to interact with the Basic component.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ProxyGen
public interface BasicComponent {

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_component.userManager";

  /**
   * Create a proxy of the {@link BasicComponent}.
   *
   * @param vertx where the service has to be used.
   *
   * @return the user.
   */
  static BasicComponent createProxy(final Vertx vertx) {

    return new BasicComponentVertxEBProxy(vertx, BasicComponent.ADDRESS);
  }

  /**
   * Register this service.
   *
   * @param vertx  that contains the event bus to use.
   * @param client to do HTTP requests to other services.
   * @param conf   configuration to use.
   */
  static void register(final Vertx vertx, final WebClient client, final JsonObject conf) {

    new ServiceBinder(vertx).setAddress(BasicComponent.ADDRESS).register(BasicComponent.class,
        new BasicComponentClient(client, conf));

  }

  /**
   * Obtain the URL to the API for interact with this component.
   *
   * @param handler to inform of the API.
   */
  void obtainApiUrl(final Handler<AsyncResult<String>> handler);

  /**
   * Obtain the URL to the API for interact with this component.
   *
   * @return the future URL to the API of this component.
   */
  @GenIgnore
  default Future<String> obtainApiUrl() {

    final Promise<String> promise = Promise.promise();
    this.obtainApiUrl(promise);
    return promise.future();
  }

  /**
   * Search for a {@link User} in Json format.
   *
   * @param id      identifier of the user to get.
   * @param handler for the retrieved user.
   */
  void retrieveUser(@NotNull String id, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Return a user.
   *
   * @param id identifier of the user to get.
   *
   * @return the future with the retrieved user.
   */
  @GenIgnore
  default Future<User> retrieveUser(@NotNull final String id) {

    final Promise<JsonObject> promise = Promise.promise();
    this.retrieveUser(id, promise);
    return Model.fromFutureJsonObject(promise.future(), User.class);

  }

  /**
   * Create a {@link User} in Json format.
   *
   * @param user    to create.
   * @param handler to the created user.
   */
  void createUser(@NotNull JsonObject user, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Create a user.
   *
   * @param user to create.
   *
   * @return the future created user.
   */
  @GenIgnore
  default Future<User> createUser(@NotNull final User user) {

    final Promise<JsonObject> promise = Promise.promise();
    this.createUser(user.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), User.class);

  }

  /**
   * Update a {@link User} in Json format.
   *
   * @param id      identifier of the user to get.
   * @param user    the new values for the user.
   * @param handler to the updated user.
   */
  void updateUser(@NotNull String id, @NotNull JsonObject user, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Update a user.
   *
   * @param id   identifier of the user to get.
   * @param user the new values for the user.
   *
   * @return the future updated user.
   */
  @GenIgnore
  default Future<User> updateUser(@NotNull final String id, @NotNull final User user) {

    final Promise<JsonObject> promise = Promise.promise();
    this.updateUser(id, user.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), User.class);

  }

  /**
   * Merge a {@link User} in Json format.
   *
   * @param id      identifier of the user to get.
   * @param user    the new values for the user.
   * @param handler to the merged user.
   */
  void mergeUser(@NotNull String id, @NotNull JsonObject user, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Merge a user.
   *
   * @param id   identifier of the user to get.
   * @param user the new values for the user.
   *
   * @return the future merged user.
   */
  @GenIgnore
  default Future<User> mergeUser(@NotNull final String id, @NotNull final User user) {

    final Promise<JsonObject> promise = Promise.promise();
    this.mergeUser(id, user.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), User.class);

  }

  /**
   * Delete a user.
   *
   * @param id      identifier of the user to get.
   * @param handler to inform if the user is deleted user.
   */
  void deleteUser(@NotNull String id, @NotNull Handler<AsyncResult<Void>> handler);

  /**
   * Delete a user.
   *
   * @param id identifier of the user to get.
   *
   * @return the future that inform if the user is deleted user.
   */
  @GenIgnore
  default Future<Void> deleteUser(@NotNull final String id) {

    final Promise<Void> promise = Promise.promise();
    this.deleteUser(id, promise);
    return promise.future();

  }

}
