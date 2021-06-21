/*
 * -----------------------------------------------------------------------------
 *
 *   Copyright 2019 - 2022 UDT-IA, IIIA-CSIC
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

import eu.internetofus.common.components.Model;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import java.util.List;
import javax.validation.constraints.NotNull;

/**
 * Service used to interact with the
 * {@link WeNetPersonalContextBuilderSimulator}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetPersonalContextBuilderSimulatorClient extends WeNetPersonalContextBuilderClient
    implements WeNetPersonalContextBuilderSimulator, WeNetPersonalContextBuilder {

  /**
   * Create a new service to interact with the WeNet service simulator.
   *
   * @param client to interact with the other modules.
   * @param conf   configuration.
   */
  public WeNetPersonalContextBuilderSimulatorClient(final WebClient client, final JsonObject conf) {

    super(client, conf);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<UsersLocations> obtainUserlocations(@NotNull final Users users) {

    final Promise<JsonObject> promise = Promise.promise();
    this.obtainUserlocations(users.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), UsersLocations.class);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<List<UserDistance>> obtainClosestUsersTo(final double latitude, final double longitude,
      final int numUsers) {

    final Promise<JsonArray> promise = Promise.promise();
    this.obtainClosestUsersTo(latitude, longitude, numUsers, promise);
    return Model.fromFutureJsonArray(promise.future(), UserDistance.class);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addUserLocation(final JsonObject location, final Handler<AsyncResult<JsonObject>> handler) {

    this.put(location, "/locations", location.getString("userId")).onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteLocation(@NotNull final String id, @NotNull final Handler<AsyncResult<Void>> handler) {

    this.delete("/locations", id).onComplete(handler);

  }

}
