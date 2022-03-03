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

package eu.internetofus.common.components.profile_manager;

import eu.internetofus.common.components.WeNetComponent;
import eu.internetofus.common.components.models.CommunityProfile;
import eu.internetofus.common.components.models.SocialNetworkRelationship;
import eu.internetofus.common.components.models.WeNetUserProfile;
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
 * The class used to interact with the WeNet profile manager.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ProxyGen
public interface WeNetProfileManager extends WeNetComponent {

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
   * {@inheritDoc}
   *
   * ATTENTION: You must to maintains this method to guarantee that VertX
   * generates the code for this method.
   */
  @Override
  void obtainApiUrl(final Handler<AsyncResult<String>> handler);

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

  /**
   * Get some user identifiers.
   *
   * @param offset  index of the first identifier.
   * @param limit   maximum number of identifiers to get.
   * @param handler of the user identifiers.
   */
  void getUserIdentifiersPage(final int offset, final int limit, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Get some user identifiers.
   *
   * @param offset index of the first identifier.
   * @param limit  maximum number of identifiers to get.
   *
   * @return the future user identifiers.
   */
  @GenIgnore
  default Future<UserIdentifiersPage> getUserIdentifiersPage(final int offset, final int limit) {

    final Promise<JsonObject> promise = Promise.promise();
    this.getUserIdentifiersPage(offset, limit, promise);
    return Model.fromFutureJsonObject(promise.future(), UserIdentifiersPage.class);

  }

  /**
   * Calculate the diversity of a set of profiles.
   *
   * @param data    with the profiles to calculate the diversity.
   * @param handler to manage the diversity calculus result.
   */
  void operationDiversity(@NotNull JsonObject data, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Calculate the diversity of a set of users.
   *
   * @param data of users to calculate the diversity.
   *
   * @return the result with the diversity value.
   */
  @GenIgnore
  default Future<DiversityValue> operationDiversity(final DiversityData data) {

    final Promise<JsonObject> promise = Promise.promise();
    this.operationDiversity(data.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), DiversityValue.class);

  }

  /**
   * Calculate the similarity of a profile attributes with a text.
   *
   * @param data    with the profiles to calculate the similarity.
   * @param handler to manage the similarity calculus result.
   */
  void operationSimilarity(@NotNull JsonObject data, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Calculate the similarity between the attributes of a profile and a text.
   *
   * @param data of users to calculate the similarity.
   *
   * @return the result with the similarity result.
   */
  @GenIgnore
  default Future<SimilarityResult> operationSimilarity(final SimilarityData data) {

    final Promise<JsonObject> promise = Promise.promise();
    this.operationSimilarity(data.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), SimilarityResult.class);

  }

  /**
   * Check if a profile is defined.
   *
   * @param id identifier of the profile to check if exist.
   *
   * @return the future that check if the profile exist or not.
   */
  @GenIgnore
  default Future<Boolean> isProfileDefined(final String id) {

    final Promise<Boolean> promise = Promise.promise();
    this.isProfileDefined(id, promise);
    return promise.future();

  }

  /**
   * Check if a profile is defined.
   *
   * @param id      identifier of the profile to check if exist.
   * @param handler to manage if the profile exist or not.
   */
  void isProfileDefined(final String id, @NotNull Handler<AsyncResult<Boolean>> handler);

  /**
   * Check if a community is defined.
   *
   * @param id identifier of the community to check if exist.
   *
   * @return the future that check if the community exist or not.
   */
  @GenIgnore
  default Future<Boolean> isCommunityDefined(final String id) {

    final Promise<Boolean> promise = Promise.promise();
    this.isCommunityDefined(id, promise);
    return promise.future();

  }

  /**
   * Check if a community is defined.
   *
   * @param id      identifier of the community to check if exist.
   * @param handler to manage if the community exist or not.
   */
  void isCommunityDefined(final String id, @NotNull Handler<AsyncResult<Boolean>> handler);

  /**
   * Return the relationships that match arguments.
   *
   * @param appId      application identifier to match in the relationships to
   *                   return.
   * @param sourceId   user identifier to match the source of the relationships to
   *                   return.
   * @param targetId   user identifier to match the target of the relationships to
   *                   return.
   * @param type       to match in the relationships to return.
   * @param weightFrom the minimum, inclusive, weight of the relationships to
   *                   return.
   * @param weightTo   the maximum, inclusive, weight of the relationships to
   *                   return.
   * @param order      in with the relationships has to be sort.
   * @param offset     index of the first relationship to return.
   * @param limit      number maximum of relationships to return.
   * @param handler    to the relationships page.
   */
  void retrieveSocialNetworkRelationshipsPage(String appId, String sourceId, String targetId, String type,
      final Double weightFrom, final Double weightTo, String order, int offset, int limit,
      @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Return the found relationships.
   *
   * @param appId      application identifier to match in the relationships to
   *                   return.
   * @param sourceId   user identifier to match the source of the relationships to
   *                   return.
   * @param targetId   user identifier to match the target of the relationships to
   *                   return.
   * @param type       to match in the relationships to return.
   * @param weightFrom the minimum, inclusive, weight of the relationships to
   *                   return.
   * @param weightTo   the maximum, inclusive, weight of the relationships to
   *                   return.
   * @param order      in with the communities has to be sort.
   * @param offset     index of the first community to return.
   * @param limit      number maximum of communities to return.
   *
   * @return the future with the relationships page.
   */
  @GenIgnore
  default Future<SocialNetworkRelationshipsPage> retrieveSocialNetworkRelationshipsPage(final String appId,
      final String sourceId, final String targetId, final String type, final Double weightFrom, final Double weightTo,
      final String order, final int offset, final int limit) {

    final Promise<JsonObject> promise = Promise.promise();
    this.retrieveSocialNetworkRelationshipsPage(appId, sourceId, targetId, type, weightFrom, weightTo, order, offset,
        limit, promise);
    return Model.fromFutureJsonObject(promise.future(), SocialNetworkRelationshipsPage.class);

  }

  /**
   * Add or update a {@link SocialNetworkRelationship} in Json format.
   *
   * @param relationship to add or update.
   *
   * @param handler      of the add or update relationship.
   */
  void addOrUpdateSocialNetworkRelationship(@NotNull JsonObject relationship,
      @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Add or update a relationship.
   *
   * @param relationship to add or update.
   *
   * @return the future with the add or update relationship.
   */
  @GenIgnore
  default Future<SocialNetworkRelationship> addOrUpdateSocialNetworkRelationship(
      @NotNull final SocialNetworkRelationship relationship) {

    final Promise<JsonObject> promise = Promise.promise();
    this.addOrUpdateSocialNetworkRelationship(relationship.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), SocialNetworkRelationship.class);

  }

  /**
   * Add or update some {@link SocialNetworkRelationship}s in Json format.
   *
   * @param relationships to add or update.
   *
   * @param handler       of the add or update relationships.
   */
  void addOrUpdateSocialNetworkRelationships(@NotNull JsonArray relationships,
      @NotNull Handler<AsyncResult<JsonArray>> handler);

  /**
   * Add or update a relationship.
   *
   * @param relationships to add or update.
   *
   * @return the future with the add or update relationship.
   */
  @GenIgnore
  default Future<List<SocialNetworkRelationship>> addOrUpdateSocialNetworkRelationships(
      @NotNull final Iterable<SocialNetworkRelationship> relationships) {

    final Promise<JsonArray> promise = Promise.promise();
    this.addOrUpdateSocialNetworkRelationships(Model.toJsonArray(relationships), promise);
    return Model.fromFutureJsonArray(promise.future(), SocialNetworkRelationship.class);

  }

  /**
   * Delete some relationships that match arguments.
   *
   * @param appId      application identifier to match in the relationships to
   *                   return.
   * @param sourceId   user identifier to match the source of the relationships to
   *                   return.
   * @param targetId   user identifier to match the target of the relationships to
   *                   return.
   * @param type       to match in the relationships to return.
   * @param weightFrom the minimum, inclusive, weight of the relationships to
   *                   return.
   * @param weightTo   the maximum, inclusive, weight of the relationships to
   *                   return.
   * @param handler    to the deleted result.
   */
  void deleteSocialNetworkRelationships(String appId, String sourceId, String targetId, String type,
      final Double weightFrom, final Double weightTo, @NotNull Handler<AsyncResult<Void>> handler);

  /**
   * Delete some relationships.
   *
   * @param appId      application identifier to match in the relationships to
   *                   return.
   * @param sourceId   user identifier to match the source of the relationships to
   *                   return.
   * @param targetId   user identifier to match the target of the relationships to
   *                   return.
   * @param type       to match in the relationships to return.
   * @param weightFrom the minimum, inclusive, weight of the relationships to
   *                   return.
   * @param weightTo   the maximum, inclusive, weight of the relationships to
   *                   return.
   *
   * @return the future that inform if the relationships has deleted.
   */
  @GenIgnore
  default Future<Void> deleteSocialNetworkRelationships(final String appId, final String sourceId,
      final String targetId, final String type, final Double weightFrom, final Double weightTo) {

    final Promise<Void> promise = Promise.promise();
    this.deleteSocialNetworkRelationships(appId, sourceId, targetId, type, weightFrom, weightTo, promise);
    return promise.future();

  }

}
