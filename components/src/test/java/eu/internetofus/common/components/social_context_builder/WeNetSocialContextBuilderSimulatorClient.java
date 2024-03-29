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

package eu.internetofus.common.components.social_context_builder;

import eu.internetofus.common.components.models.WeNetUserProfile;
import eu.internetofus.common.model.Model;
import io.vertx.codegen.annotations.GenIgnore;
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
 * Service used to interact with the {@link WeNetSocialContextBuilderSimulator}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetSocialContextBuilderSimulatorClient extends WeNetSocialContextBuilderClient
    implements WeNetSocialContextBuilderSimulator, WeNetSocialContextBuilder {

  /**
   * Create a new service to interact with the WeNet social context builder
   * simulator.
   *
   * @param client to interact with the other modules.
   * @param conf   configuration.
   */
  public WeNetSocialContextBuilderSimulatorClient(final WebClient client, final JsonObject conf) {

    super(client, conf);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<List<String>> postSocialPreferencesForUserOnTask(@NotNull final String userId,
      @NotNull final String taskId, @NotNull final List<String> volunteers) {

    final Promise<JsonArray> promise = Promise.promise();
    final var array = Model.toJsonArray(volunteers);
    this.postSocialPreferencesForUserOnTask(userId, taskId, array, promise);
    return Model.fromFutureJsonArray(promise.future(), String.class);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<SocialExplanation> retrieveSocialExplanation(@NotNull final String userId,
      @NotNull final String taskId) {

    final Promise<JsonObject> promise = Promise.promise();
    this.retrieveSocialExplanation(userId, taskId, promise);
    return Model.fromFutureJsonObject(promise.future(), SocialExplanation.class);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void getSocialPreferencesForUserOnTask(@NotNull final String userId, @NotNull final String taskId,
      @NotNull final Handler<AsyncResult<JsonArray>> handler) {

    this.getJsonObject("/social/preferences", userId, taskId, "/")
        .map(body -> body.getJsonArray("users_IDs", new JsonArray())).onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setSocialExplanation(@NotNull final String userId, @NotNull final String taskId,
      @NotNull final JsonObject explanation, @NotNull final Handler<AsyncResult<JsonObject>> handler) {

    this.post(explanation, "/social/explanations", userId, taskId, "/").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void getSocialPreferencesAnswersForUserOnTask(@NotNull final String userId, @NotNull final String taskId,
      @NotNull final Handler<AsyncResult<JsonObject>> handler) {

    this.getJsonObject("/social/preferences/answers", userId, taskId).onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<List<UserAnswer>> postSocialPreferencesAnswersForUserOnTask(@NotNull final String userId,
      @NotNull final String taskId, @NotNull final UserAnswers userAnswers) {

    final Promise<JsonArray> promise = Promise.promise();
    final var data = userAnswers.toJsonObject();
    this.postSocialPreferencesAnswersForUserOnTask(userId, taskId, data, promise);
    return Model.fromFutureJsonArray(promise.future(), UserAnswer.class);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> initializeSocialRelations(@NotNull final WeNetUserProfile profile) {

    final Promise<Void> promise = Promise.promise();
    this.initializeSocialRelations(profile.id, promise);
    return promise.future();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void getInitializeSocialRelations(@NotNull final String userId,
      @NotNull final Handler<AsyncResult<JsonObject>> handler) {

    this.getJsonObject("/social/relations/initialize/", userId).onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> socialNotificationInteraction(@NotNull final UserMessage message) {

    final Promise<Void> promise = Promise.promise();
    this.socialNotificationInteraction(message.toJsonObject(), promise);
    return promise.future();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void getSocialNotificationInteraction(@NotNull final Handler<AsyncResult<JsonArray>> handler) {

    this.getJsonArray("/social/notification/interaction").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteSocialNotificationInteraction(@NotNull final Handler<AsyncResult<Void>> handler) {

    this.delete("/social/notification/interaction").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> putSocialPreferencesSelectedAnswerForUserOnTask(@NotNull final String userId,
      @NotNull final String taskId, final int selection, @NotNull final UserAnswers userAnswers) {

    final Promise<Void> promise = Promise.promise();
    final var data = userAnswers.toJsonObject();
    this.putSocialPreferencesSelectedAnswerForUserOnTask(userId, taskId, selection, data, promise);
    return promise.future();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void getSocialPreferencesSelectedAnswerForUserOnTask(@NotNull final String userId,
      @NotNull final String taskId, final int selection, @NotNull final Handler<AsyncResult<JsonObject>> handler) {

    this.getJsonObject("/social/preferences/answers", userId, taskId, selection, "/update").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<JsonObject> socialNotificationProfileUpdate(@NotNull final String userId,
      final ProfileUpdateNotification notification) {

    final Promise<JsonObject> promise = Promise.promise();
    final var data = notification.toJsonObject();
    this.socialNotificationProfileUpdate(userId, data, promise);
    return promise.future();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void getSocialNotificationProfileUpdate(@NotNull final String userId,
      @NotNull final Handler<AsyncResult<JsonObject>> handler) {

    this.getJsonObject("/social/notification/profileUpdate", userId).onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteSocialNotificationProfileUpdate(@NotNull final String userId,
      @NotNull final Handler<AsyncResult<Void>> handler) {

    this.delete("/social/notification/profileUpdate", userId).onComplete(handler);

  }

  /**
   * Shuffle a set of user using the learned social diversity.
   *
   * @param userIds identifiers of the users to shuffle.
   *
   * @return the an ordered list of Users based on diversity attributes.
   */
  @Override
  @GenIgnore
  public Future<UserIds> socialShuffle(@NotNull final UserIds userIds) {

    final Promise<JsonObject> promise = Promise.promise();
    final var data = userIds.toJsonObject();
    this.socialShuffle(data, promise);
    return Model.fromFutureJsonObject(promise.future(), UserIds.class);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void getSocialShuffle(@NotNull final Handler<AsyncResult<JsonObject>> handler) {

    this.getJsonObject("/social/shuffle").onComplete(handler);
  }

}
