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
