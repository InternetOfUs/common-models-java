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
package eu.internetofus.common.components.profile_manager_ext_wordnetsim;

import eu.internetofus.common.components.WeNetComponent;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
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
 * The component used by the profile manager to calculate the similarity.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ProxyGen
public interface WeNetProfileManagerExtWordNetSim extends WeNetComponent {

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_component.profileManagerExtWordNetSim";

  /**
   * Create a proxy of the {@link WeNetProfileManager}.
   *
   * @param vertx where the service has to be used.
   *
   * @return the task.
   */
  static WeNetProfileManagerExtWordNetSim createProxy(final Vertx vertx) {

    return new WeNetProfileManagerExtWordNetSimVertxEBProxy(vertx, WeNetProfileManagerExtWordNetSim.ADDRESS);
  }

  /**
   * Register this service.
   *
   * @param vertx  that contains the event bus to use.
   * @param client to do HTTP requests to other services.
   * @param conf   configuration to use.
   */
  static void register(final Vertx vertx, final WebClient client, final JsonObject conf) {

    new ServiceBinder(vertx).setAddress(WeNetProfileManagerExtWordNetSim.ADDRESS)
        .register(WeNetProfileManagerExtWordNetSim.class, new WeNetProfileManagerExtWordNetSimClient(client, conf));

  }

  /**
   * {@inheritDoc}
   *
   * ATTENTION: You must to maintains this method to guarantee that VertX
   * generates the code for this method.
   */
  @Override
  void obtainApiUrl(final Handler<AsyncResult<String>> handler);

  /**
   * Calculate the similarity between two strings.
   *
   * @param data    with the string to calculate the similarity.
   * @param handler for the similarity result.
   */
  void similarityBetweenStrings(@NotNull JsonObject data, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Calculate the similarity between two strings.
   *
   * @param data with the string to calculate the similarity.
   *
   * @return the future with the similarity of the strings.
   */
  @GenIgnore
  default Future<SimilarityResult> similarityBetweenStrings(@NotNull final StringsSimilarityData data) {

    final Promise<JsonObject> promise = Promise.promise();
    this.similarityBetweenStrings(data.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), SimilarityResult.class);

  }

}
