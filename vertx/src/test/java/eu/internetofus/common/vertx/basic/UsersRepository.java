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
import io.vertx.ext.mongo.MongoClient;
import io.vertx.serviceproxy.ServiceBinder;
import javax.validation.constraints.NotNull;

/**
 * The component to store the {@link User}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ProxyGen
public interface UsersRepository {

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_basic.persistence.users";

  /**
   * Create a proxy of the {@link UsersRepository}.
   *
   * @param vertx where the service has to be used.
   *
   * @return the user.
   */
  static UsersRepository createProxy(final Vertx vertx) {

    return new UsersRepositoryVertxEBProxy(vertx, UsersRepository.ADDRESS);
  }

  /**
   * Register this service.
   *
   * @param vertx   that contains the event bus to use.
   * @param pool    to create the database connections.
   * @param version of the schemas.
   *
   * @return the future that inform when the repository will be registered or not.
   */
  static Future<Void> register(final Vertx vertx, final MongoClient pool, final String version) {

    final var repository = new UsersRepositoryImpl(vertx, pool, version);
    new ServiceBinder(vertx).setAddress(UsersRepository.ADDRESS).register(UsersRepository.class, repository);
    return repository.migrateDocumentsToCurrentVersions();

  }

  /**
   * Search for the user with the specified identifier.
   *
   * @param id identifier of the user to search.
   *
   * @return tyeh future found user.
   */
  @GenIgnore
  default Future<User> searchUser(final String id) {

    final Promise<JsonObject> promise = Promise.promise();
    this.searchUser(id, promise);
    return Model.fromFutureJsonObject(promise.future(), User.class);

  }

  /**
   * Search for the user with the specified identifier.
   *
   * @param id            identifier of the user to search.
   * @param searchHandler handler to manage the search.
   */
  void searchUser(String id, Handler<AsyncResult<JsonObject>> searchHandler);

  /**
   * Store a user.
   *
   * @param user to store.
   *
   * @return the future stored user.
   */
  @GenIgnore
  default Future<User> storeUser(@NotNull final User user) {

    final Promise<JsonObject> promise = Promise.promise();
    this.storeUser(user.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), User.class);

  }

  /**
   * Store a user.
   *
   * @param user         to store.
   * @param storeHandler handler to manage the search.
   */
  void storeUser(JsonObject user, Handler<AsyncResult<JsonObject>> storeHandler);

  /**
   * Update a user.
   *
   * @param user to update.
   *
   * @return the future update result.
   */
  @GenIgnore
  default Future<Void> updateUser(@NotNull final User user) {

    final var object = user.toJsonObjectWithEmptyValues();
    if (object == null) {

      return Future.failedFuture("The user can not converted to JSON.");

    } else {

      final Promise<Void> promise = Promise.promise();
      this.updateUser(object, promise);
      return promise.future();
    }

  }

  /**
   * Update a user.
   *
   * @param user          to update.
   * @param updateHandler handler to manage the update result.
   */
  void updateUser(JsonObject user, Handler<AsyncResult<Void>> updateHandler);

  /**
   * Delete a user.
   *
   * @param id identifier of the user to delete.
   *
   * @return the future delete result.
   */
  @GenIgnore
  default Future<Void> deleteUser(final String id) {

    final Promise<Void> promise = Promise.promise();
    this.deleteUser(id, promise);
    return promise.future();

  }

  /**
   * Delete a user.
   *
   * @param id            identifier of the user to delete.
   * @param deleteHandler handler to manage the delete result.
   */
  void deleteUser(String id, Handler<AsyncResult<Void>> deleteHandler);

  /**
   * Retrieve the task types defined on the context.
   *
   * @param offset the index of the first community to return.
   * @param limit  the number maximum of task types to return.
   *
   * @return the future page.
   */
  @GenIgnore
  default Future<UsersPage> retrieveUsersPage(final int offset, final int limit) {

    final Promise<JsonObject> promise = Promise.promise();
    this.retrieveUsersPage(offset, limit, promise);
    return Model.fromFutureJsonObject(promise.future(), UsersPage.class);

  }

  /**
   * Retrieve a page with some task types.
   *
   * @param offset  the index of the first community to return.
   * @param limit   the number maximum of task types to return.
   * @param handler to inform of the found task types.
   */
  void retrieveUsersPage(int offset, int limit, Handler<AsyncResult<JsonObject>> handler);

}
