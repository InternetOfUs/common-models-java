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

package eu.internetofus.common.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.tinylog.Logger;

/**
 * The common verticle that provide the persistence services.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractPersistenceVerticle extends AbstractVerticle {

  /**
   * The name of the pool of connections.
   */
  public static final String PERSISTENCE_POOL_NAME = "WENET_MODULE_POOL";

  /**
   * The pool of database connections.
   */
  protected MongoClient pool;

  /**
   * {@inheritDoc}
   */
  @Override
  public void start(final Promise<Void> startPromise) throws Exception {

    // Create the pool
    final var persitenceConf = this.config().getJsonObject("persistence", new JsonObject());
    this.pool = MongoClient.createShared(this.getVertx(), persitenceConf, PERSISTENCE_POOL_NAME);

    // Register the repositories
    final var schemaVersion = this.apiVersion();
    final var register = this.registerRepositoriesFor(schemaVersion);
    register.onComplete(registered -> {

      if (registered.failed()) {
        // Cannot registered repositories
        final var cause = registered.cause();
        Logger.error(cause, "Cannot register the repositories");
        startPromise.fail(cause);

      } else {

        Logger.trace("Registered repositories");
        startPromise.complete();
      }

    });

  }

  /**
   * Register the repository services that will be provided.
   *
   * @param schemaVersion version of the models schema to store.
   *
   * @return the future that will inform if the repositories has been registered.
   */
  protected abstract Future<Void> registerRepositoriesFor(String schemaVersion);

  /**
   * Close the connections pool.
   *
   * {@inheritDoc}
   */
  @Override
  public void stop() throws Exception {

    if (this.pool != null) {

      this.pool.close();
      this.pool = null;
    }

  }

  /**
   * Return the API version defined on the configuration.
   *
   * @return the current API version.
   */
  protected String apiVersion() {

    return this.config().getJsonObject("help", new JsonObject()).getJsonObject("info", new JsonObject())
        .getString("apiVersion", "Undefined");

  }

}
