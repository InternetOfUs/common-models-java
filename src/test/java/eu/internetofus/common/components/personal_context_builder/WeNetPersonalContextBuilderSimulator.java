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
 * The services to interact with the mocked version of the
 * {@link WeNetPersonalContextBuilder}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ProxyGen
public interface WeNetPersonalContextBuilderSimulator {

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_component.PersonalContextBuilderSimulator";

  /**
   * Create a proxy of the {@link WeNetPersonalContextBuilderSimulator}.
   *
   * @param vertx where the service has to be used.
   *
   * @return the task.
   */
  static WeNetPersonalContextBuilderSimulator createProxy(final Vertx vertx) {

    return new WeNetPersonalContextBuilderSimulatorVertxEBProxy(vertx, WeNetPersonalContextBuilderSimulator.ADDRESS);
  }

  /**
   * Register this service for a {@link WeNetPersonalContextBuilder} and a
   * {@link WeNetPersonalContextBuilderSimulator}.
   *
   * @param vertx  that contains the event bus to use.
   * @param client to do HTTP requests to other services.
   * @param conf   configuration to use.
   *
   * @see WeNetPersonalContextBuilder#ADDRESS
   * @see WeNetPersonalContextBuilderSimulator#ADDRESS
   */
  static void register(final Vertx vertx, final WebClient client, final JsonObject conf) {

    new ServiceBinder(vertx).setAddress(WeNetPersonalContextBuilderSimulator.ADDRESS).register(
        WeNetPersonalContextBuilderSimulator.class, new WeNetPersonalContextBuilderSimulatorClient(client, conf));

  }

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
   * Defined method only for testing and can store a user location.
   *
   * @param location to store.
   * @param handler  to manage the stored user location.
   */
  void addUserLocation(@NotNull JsonObject location, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Defined method only for testing and can store a user location.
   *
   * @param location to add.
   *
   * @return the added location.
   */
  @GenIgnore
  default Future<UserLocation> addUserLocation(@NotNull final UserLocation location) {

    final Promise<JsonObject> promise = Promise.promise();
    this.addUserLocation(location.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), UserLocation.class);

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

  /**
   * Defined method only for testing and can delete a user location.
   *
   * @param id      identifier of the user location to remove.
   * @param handler to manage the deleted user location.
   */
  void deleteLocation(@NotNull String id, @NotNull Handler<AsyncResult<Void>> handler);

  /**
   * Defined method only for testing and can delete a user location.
   *
   * @param id identifier of the user location to remove.
   *
   * @return the deleted user location.
   */
  @GenIgnore
  default Future<Void> deleteLocation(@NotNull final String id) {

    final Promise<Void> promise = Promise.promise();
    this.deleteLocation(id, promise);
    return promise.future();

  }

}
