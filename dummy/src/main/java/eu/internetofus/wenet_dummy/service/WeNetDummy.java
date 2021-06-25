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

package eu.internetofus.wenet_dummy.service;

import eu.internetofus.common.components.Model;
import eu.internetofus.wenet_dummy.api.dummies.Dummies;
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
 * The service to interact with the {@link Dummies}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ProxyGen
public interface WeNetDummy {

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_component.dummy";

  /**
   * Create a proxy of the {@link WeNetDummy}.
   *
   * @param vertx where the service has to be used.
   *
   * @return the task.
   */
  static WeNetDummy createProxy(final Vertx vertx) {

    return new WeNetDummyVertxEBProxy(vertx, WeNetDummy.ADDRESS);
  }

  /**
   * Register this service.
   *
   * @param vertx  that contains the event bus to use.
   * @param client to do HTTP requests to other services.
   * @param conf   configuration to use.
   */
  static void register(final Vertx vertx, final WebClient client, final JsonObject conf) {

    new ServiceBinder(vertx).setAddress(WeNetDummy.ADDRESS).register(WeNetDummy.class,
        new WeNetDummiesClient(client, conf));

  }

  /**
   * Create a {@link Dummy} in Json format.
   *
   * @param dummy   to create.
   * @param handler for the created dummy.
   */
  void createDummy(@NotNull JsonObject dummy, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Create a dummy.
   *
   * @param dummy to create.
   *
   * @return the future created dummy.
   */
  @GenIgnore
  default Future<Dummy> createDummy(@NotNull final Dummy dummy) {

    final Promise<JsonObject> promise = Promise.promise();
    this.createDummy(dummy.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), Dummy.class);

  }

}
