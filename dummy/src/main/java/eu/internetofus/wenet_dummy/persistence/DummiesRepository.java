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

import eu.internetofus.common.model.Model;
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
  String ADDRESS = "wenet_dummy.persistence.dummies";

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

    final var repository = new DummiesRepositoryImpl(vertx, pool, version);
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

    final Promise<JsonObject> promise = Promise.promise();
    if (dummy == null) {

      promise.fail("The dummy can not converted to JSON.");

    } else {

      final var object = dummy.toJsonObject();
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
