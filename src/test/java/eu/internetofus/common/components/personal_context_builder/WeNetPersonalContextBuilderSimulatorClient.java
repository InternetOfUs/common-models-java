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
