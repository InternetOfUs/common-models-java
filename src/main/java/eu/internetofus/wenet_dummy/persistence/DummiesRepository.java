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

import eu.internetofus.common.components.Model;
import eu.internetofus.wenet_dummy.service.Dummy;
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

/**
 * The service to manage the {@link Dummy} on the database.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ProxyGen
public interface DummiesRepository {

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_dummy_manager.persistence.dummies";

  /**
   * Create a proxy of the {@link DummiesRepository}.
   *
   * @param vertx where the service has to be used.
   *
   * @return the dummy.
   */
  static DummiesRepository createProxy(final Vertx vertx) {

    return new DummiesRepositoryVertxEBProxy(vertx, DummiesRepository.ADDRESS);
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

    final var repository = new DummiesRepositoryImpl(pool, version);
    new ServiceBinder(vertx).setAddress(DummiesRepository.ADDRESS).register(DummiesRepository.class, repository);
    return repository.migrateDocumentsToCurrentVersions();

  }

  /**
   * Store a dummy.
   *
   * @param dummy to store.
   *
   * @return the future stored dummy.
   */
  @GenIgnore
  default Future<Dummy> storeDummy(final Dummy dummy) {

    Promise<JsonObject> promise = Promise.promise();
    final var object = dummy.toJsonObject();
    if (object == null) {

      promise.fail("The dummy can not converted to JSON.");

    } else {

      this.storeDummy(object, promise);
    }

    return Model.fromFutureJsonObject(promise.future(), Dummy.class);
  }

  /**
   * Store a dummy.
   *
   * @param dummy        to store.
   * @param storeHandler handler to manage the store.
   */
  void storeDummy(JsonObject dummy, Handler<AsyncResult<JsonObject>> storeHandler);

}
