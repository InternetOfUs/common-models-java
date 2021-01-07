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

    }, storeHandler);

  }

}
