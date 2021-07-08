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

import eu.internetofus.common.vertx.Repository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;

/**
 * The implementation of the {@link UsersRepository}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class UsersRepositoryImpl extends Repository implements UsersRepository {

  /**
   * The name of the collection that contains the users.
   */
  public static final String USERS_COLLECTION = "users";

  /**
   * Create a new service.
   *
   * @param vertx   event bus to use.
   * @param pool    to create the connections.
   * @param version of the schemas.
   */
  public UsersRepositoryImpl(final Vertx vertx, final MongoClient pool, final String version) {

    super(vertx, pool, version);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void searchUser(final String id, final Handler<AsyncResult<JsonObject>> searchHandler) {

    final var query = new JsonObject().put("_id", id);
    this.findOneDocument(USERS_COLLECTION, query, null, null).onComplete(searchHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void storeUser(final JsonObject user, final Handler<AsyncResult<JsonObject>> storeHandler) {

    this.storeOneDocument(USERS_COLLECTION, user, null).onComplete(storeHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateUser(final JsonObject user, final Handler<AsyncResult<Void>> updateHandler) {

    final var query = new JsonObject().put("_id", user.getString("_id"));
    this.updateOneDocument(USERS_COLLECTION, query, user).onComplete(updateHandler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteUser(final String id, final Handler<AsyncResult<Void>> deleteHandler) {

    final var query = new JsonObject().put("_id", id);
    this.deleteOneDocument(USERS_COLLECTION, query).onComplete(deleteHandler);

  }

  /**
   * Migrate the collections to the current version.
   *
   * @return the future that will inform if the migration is a success or not.
   */
  public Future<Void> migrateDocumentsToCurrentVersions() {

    return this.updateSchemaVersionOnCollection(USERS_COLLECTION);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveUsersPage(final int offset, final int limit,
      final Handler<AsyncResult<JsonObject>> searchHandler) {

    final var options = new FindOptions();
    options.setSkip(offset);
    options.setLimit(limit);
    this.searchPageObject(USERS_COLLECTION, new JsonObject(), options, "users", null).onComplete(searchHandler);

  }

}
