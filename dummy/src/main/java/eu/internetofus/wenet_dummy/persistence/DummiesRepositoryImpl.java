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
package eu.internetofus.wenet_dummy.persistence;

import eu.internetofus.common.vertx.Repository;
import eu.internetofus.wenet_dummy.service.Dummy;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * Implementation of the {@link DummiesRepository}.
 *
 * @see DummiesRepository
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class DummiesRepositoryImpl extends Repository implements DummiesRepository {

  /**
   * The name of the collection that contains the dummies.
   */
  public static final String DUMMIES_COLLECTION = "dummies";

  /**
   * Create a new repository.
   *
   * @param pool    to create the connections.
   * @param version of the schemas.
   */
  public DummiesRepositoryImpl(final MongoClient pool, final String version) {

    super(pool, version);

  }

  /**
   * Migrate the collections to the current version.
   *
   * @return the future that will inform if the migration is a success or not.
   */
  public Future<Void> migrateDocumentsToCurrentVersions() {

    return this.migrateCollection(DUMMIES_COLLECTION, Dummy.class);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void storeDummy(final JsonObject dummy, final Handler<AsyncResult<JsonObject>> storeHandler) {

    final var id = (String) dummy.remove("id");
    if (id != null) {

      dummy.put("_id", id);
    }
    this.storeOneDocument(DUMMIES_COLLECTION, dummy, stored -> {

      final var _id = (String) stored.remove("_id");
      return stored.put("id", _id);

    }).onComplete(storeHandler);

  }

}
