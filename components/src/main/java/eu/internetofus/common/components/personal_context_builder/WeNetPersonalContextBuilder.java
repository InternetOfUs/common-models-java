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

package eu.internetofus.common.components.personal_context_builder;

import eu.internetofus.common.components.WeNetComponent;
import eu.internetofus.common.model.Model;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.serviceproxy.ServiceBinder;
import java.util.List;
import javax.validation.constraints.NotNull;

/**
 * The class used to interact with the WeNet personal context builder.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ProxyGen
public interface WeNetPersonalContextBuilder extends WeNetComponent {

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_component.PersonalContextBuilder";

  /**
   * Create a proxy of the {@link WeNetPersonalContextBuilder}.
   *
   * @param vertx where the service has to be used.
   *
   * @return the task.
   */
  static WeNetPersonalContextBuilder createProxy(final Vertx vertx) {

    return new WeNetPersonalContextBuilderVertxEBProxy(vertx, WeNetPersonalContextBuilder.ADDRESS);
  }

  /**
   * Register this service.
   *
   * @param vertx  that contains the event bus to use.
   * @param client to do HTTP requests to other services.
   * @param conf   configuration to use.
   */
  static void register(final Vertx vertx, final WebClient client, final JsonObject conf) {

    new ServiceBinder(vertx).setAddress(WeNetPersonalContextBuilder.ADDRESS).register(WeNetPersonalContextBuilder.class,
        new WeNetPersonalContextBuilderClient(client, conf));

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
   * Return the locations of a set of users.
   *
   * @param users   to obtain the locations.
   * @param handler for inform about the user locations.
   */
  void obtainUserlocations(@NotNull JsonObject users, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Return the locations of a set of users.
   *
   * @param users to obtain the locations.
   *
   * @return the future user locations.
   */
  @GenIgnore
  default Future<UsersLocations> obtainUserlocations(@NotNull final Users users) {

    final Promise<JsonObject> promise = Promise.promise();
    this.obtainUserlocations(users.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), UsersLocations.class);

  }

  /**
   * Return a set of users that are closest into a location.
   *
   * @param latitude  of the location.
   * @param longitude of the location.
   * @param numUsers  number of users to return.
   * @param handler   for inform about the closest users.
   */
  void obtainClosestUsersTo(double latitude, double longitude, int numUsers,
      @NotNull Handler<AsyncResult<JsonArray>> handler);

  /**
   * Return a set of users that are closest into a location.
   *
   * @param latitude  of the location.
   * @param longitude of the location.
   * @param numUsers  number of users to return.
   *
   * @return the future closest users.
   */
  @GenIgnore
  default Future<List<UserDistance>> obtainClosestUsersTo(final double latitude, final double longitude,
      final int numUsers) {

    final Promise<JsonArray> promise = Promise.promise();
    this.obtainClosestUsersTo(latitude, longitude, numUsers, promise);
    return Model.fromFutureJsonArray(promise.future(), UserDistance.class);

  }

}
