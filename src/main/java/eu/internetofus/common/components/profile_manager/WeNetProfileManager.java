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

package eu.internetofus.common.components.profile_manager;

import eu.internetofus.common.components.Model;
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
 * The class used to interact with the WeNet profile manager.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ProxyGen
public interface WeNetProfileManager {

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_component.profileManager";

  /**
   * Create a proxy of the {@link WeNetProfileManager}.
   *
   * @param vertx where the service has to be used.
   *
   * @return the task.
   */
  static WeNetProfileManager createProxy(final Vertx vertx) {

    return new WeNetProfileManagerVertxEBProxy(vertx, WeNetProfileManager.ADDRESS);
  }

  /**
   * Register this service.
   *
   * @param vertx  that contains the event bus to use.
   * @param client to do HTTP requests to other services.
   * @param conf   configuration to use.
   */
  static void register(final Vertx vertx, final WebClient client, final JsonObject conf) {

    new ServiceBinder(vertx).setAddress(WeNetProfileManager.ADDRESS).register(WeNetProfileManager.class,
        new WeNetProfileManagerClient(client, conf));

  }

  /**
   * Create a {@link WeNetUserProfile} in Json format.
   *
   * @param profile to create.
   * @param handler for the created profile.
   */
  void createProfile(@NotNull JsonObject profile, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Create a profile.
   *
   * @param profile to create.
   *
   * @return the future created profile.
   */
  @GenIgnore
  default Future<WeNetUserProfile> createProfile(@NotNull final WeNetUserProfile profile) {

    final Promise<JsonObject> promise = Promise.promise();
    this.createProfile(profile.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), WeNetUserProfile.class);

  }

  /**
   * Return a {@link WeNetUserProfile} in Json format.
   *
   * @param id      identifier of the profile to get.
   * @param handler for the found profile.
   */
  void retrieveProfile(@NotNull String id, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Return a profile.
   *
   * @param id identifier of the profile to get.
   *
   * @return the future found profile.
   */
  @GenIgnore
  default Future<WeNetUserProfile> retrieveProfile(@NotNull final String id) {

    final Promise<JsonObject> promise = Promise.promise();
    this.retrieveProfile(id, promise);
    return Model.fromFutureJsonObject(promise.future(), WeNetUserProfile.class);

  }

  /**
   * Delete a profile.
   *
   * @param id      identifier of the profile to get.
   * @param handler to the deleted profile.
   */
  void deleteProfile(@NotNull String id, @NotNull Handler<AsyncResult<Void>> handler);

  /**
   * Delete a profile.
   *
   * @param id identifier of the profile to get.
   *
   * @return if the profile is deleted.
   */
  @GenIgnore
  default Future<Void> deleteProfile(@NotNull final String id) {

    final Promise<Void> promise = Promise.promise();
    this.deleteProfile(id, promise);
    return promise.future();

  }

  /**
   * Create a {@link CommunityProfile} in Json format.
   *
   * @param community to create.
   *
   * @param handler   of the created community.
   */
  void createCommunity(@NotNull JsonObject community, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Create a community.
   *
   * @param community to create.
   *
   * @return the future created community.
   */
  @GenIgnore
  default Future<CommunityProfile> createCommunity(@NotNull final CommunityProfile community) {

    final Promise<JsonObject> promise = Promise.promise();
    this.createCommunity(community.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), CommunityProfile.class);

  }

  /**
   * Return a {@link CommunityProfile} in Json format.
   *
   * @param id      identifier of the community to get.
   * @param handler for the found community.
   */
  void retrieveCommunity(@NotNull String id, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Return a community.
   *
   * @param id identifier of the community to get.
   *
   * @return the future found community.
   */
  @GenIgnore
  default Future<CommunityProfile> retrieveCommunity(@NotNull final String id) {

    final Promise<JsonObject> promise = Promise.promise();
    this.retrieveCommunity(id, promise);
    return Model.fromFutureJsonObject(promise.future(), CommunityProfile.class);

  }

  /**
   * Delete a community.
   *
   * @param id      identifier of the community to get.
   * @param handler to inform if the community is deleted.
   */
  void deleteCommunity(@NotNull String id, @NotNull Handler<AsyncResult<Void>> handler);

  /**
   * Delete a community.
   *
   * @param id identifier of the community to get.
   *
   * @return the future deleted community.
   */
  @GenIgnore
  default Future<Void> deleteCommunity(@NotNull final String id) {

    final Promise<Void> promise = Promise.promise();
    this.deleteCommunity(id, promise);
    return promise.future();
  }

  /**
   * Return the page of the found community profiles.
   *
   * @param appId       application identifier to match for the communities to
   *                    return.
   * @param name        to match for the communities to return.
   * @param description to match for the communities to return.
   * @param keywords    to match for the communities to return.
   * @param members     to match for the communities to return.
   * @param order       in with the communities has to be sort.
   * @param offset      index of the first community to return.
   * @param limit       number maximum of communities to return.
   * @param handler     to the communities page.
   */
  void retrieveCommunityProfilesPage(String appId, String name, String description, String keywords, String members,
      String order, int offset, int limit, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Return the found communities.
   *
   * @param appId       application identifier to match for the communities to
   *                    return.
   * @param name        to match for the communities to return.
   * @param description to match for the communities to return.
   * @param keywords    to match for the communities to return.
   * @param members     to match for the communities to return.
   * @param order       in with the communities has to be sort.
   * @param offset      index of the first community to return.
   * @param limit       number maximum of communities to return.
   *
   * @return the future with the communities page.
   */
  @GenIgnore
  default Future<CommunityProfilesPage> retrieveCommunityProfilesPage(final String appId, final String name,
      final String description, final String keywords, final String members, final String order, final int offset,
      final int limit) {

    final Promise<JsonObject> promise = Promise.promise();
    this.retrieveCommunityProfilesPage(appId, name, description, keywords, members, order, offset, limit, promise);
    return Model.fromFutureJsonObject(promise.future(), CommunityProfilesPage.class);

  }

  /**
   * Update a {@link CommunityProfile} in Json format.
   *
   * @param id        identifier of the community to update.
   * @param community to update.
   *
   * @param handler   of the updated community.
   */
  void updateCommunity(@NotNull String id, @NotNull JsonObject community,
      @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Update a community.
   *
   * @param id        identifier of the community to update.
   * @param community to update.
   *
   * @return the future updated community.
   */
  @GenIgnore
  default Future<CommunityProfile> updateCommunity(@NotNull final String id,
      @NotNull final CommunityProfile community) {

    final Promise<JsonObject> promise = Promise.promise();
    this.updateCommunity(id, community.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), CommunityProfile.class);

  }

  /**
   * Update a community.
   *
   * @param community to update.
   *
   * @return the future updated community.
   */
  @GenIgnore
  default Future<CommunityProfile> updateCommunity(@NotNull final CommunityProfile community) {

    return this.updateCommunity(community.id, community);

  }

  /**
   * Update a {@link WeNetUserProfile} in Json format.
   *
   * @param id      identifier of the profile to update.
   * @param profile to update.
   *
   * @param handler of the updated profile.
   */
  void updateProfile(@NotNull String id, @NotNull JsonObject profile,
      @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Update a profile.
   *
   * @param id      identifier of the profile to update.
   * @param profile to update.
   *
   * @return the future updated profile.
   */
  @GenIgnore
  default Future<WeNetUserProfile> updateProfile(@NotNull final String id, @NotNull final WeNetUserProfile profile) {

    final Promise<JsonObject> promise = Promise.promise();
    this.updateProfile(id, profile.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), WeNetUserProfile.class);

  }

  /**
   * Update a profile.
   *
   * @param profile to update.
   *
   * @return the future updated profile.
   */
  @GenIgnore
  default Future<WeNetUserProfile> updateProfile(@NotNull final WeNetUserProfile profile) {

    return this.updateProfile(profile.id, profile);

  }

}
