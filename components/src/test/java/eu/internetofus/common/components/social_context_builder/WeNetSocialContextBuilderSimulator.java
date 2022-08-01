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
 * The service to interact with the mocked version of the
 * {@link WeNetSocialContextBuilder}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ProxyGen
public interface WeNetSocialContextBuilderSimulator {

  /**
   * The address of this social context builder.
   */
  String ADDRESS = "wenet_component.SocialContextBuilderSimulator";

  /**
   * Create a proxy of the {@link WeNetSocialContextBuilderSimulator}.
   *
   * @param vertx where the social context builder has to be used.
   *
   * @return the task.
   */
  static WeNetSocialContextBuilderSimulator createProxy(final Vertx vertx) {

    return new WeNetSocialContextBuilderSimulatorVertxEBProxy(vertx, WeNetSocialContextBuilderSimulator.ADDRESS);
  }

  /**
   * Register this social context builder for a {@link WeNetSocialContextBuilder}
   * and a {@link WeNetSocialContextBuilderSimulator}.
   *
   * @param vertx  that contains the event bus to use.
   * @param client to do HTTP requests to other social context builder.
   * @param conf   configuration to use.
   *
   * @see WeNetSocialContextBuilder#ADDRESS
   * @see WeNetSocialContextBuilderSimulator#ADDRESS
   */
  static void register(final Vertx vertx, final WebClient client, final JsonObject conf) {

    new ServiceBinder(vertx).setAddress(WeNetSocialContextBuilderSimulator.ADDRESS)
        .register(WeNetSocialContextBuilderSimulator.class, new WeNetSocialContextBuilderSimulatorClient(client, conf));

  }

  /**
   * Post the preferences of an user. This calculate the social user ranking. In
   * other words, from a set of volunteers it order form the best to the worst.
   *
   * @param userId     identifier of the user.
   * @param taskId     identifier of the task
   * @param volunteers the identifier of the volunteers of the task.
   * @param handler    to notify the ranking of the users.
   */
  void postSocialPreferencesForUserOnTask(@NotNull String userId, @NotNull String taskId, @NotNull JsonArray volunteers,
      @NotNull Handler<AsyncResult<JsonArray>> handler);

  /**
   * Post the preferences of an user. This calculate the social user ranking. In
   * other words, from a set of volunteers it order form the best to the worst.
   *
   * @param userId     identifier of the user.
   * @param taskId     identifier of the task
   * @param volunteers the identifier of the volunteers of the task.
   *
   * @return the future with the ranking of the users.
   */
  @GenIgnore
  default Future<List<String>> postSocialPreferencesForUserOnTask(@NotNull final String userId,
      @NotNull final String taskId, @NotNull final List<String> volunteers) {

    final Promise<JsonArray> promise = Promise.promise();
    final var array = Model.toJsonArray(volunteers);
    this.postSocialPreferencesForUserOnTask(userId, taskId, array, promise);
    return Model.fromFutureJsonArray(promise.future(), String.class);

  }

  /**
   * Get the preferences of an user.
   *
   * @param userId  identifier of the user.
   * @param taskId  identifier of the task
   * @param handler for the user preferences on task.
   */
  void getSocialPreferencesForUserOnTask(@NotNull String userId, @NotNull String taskId,
      @NotNull Handler<AsyncResult<JsonArray>> handler);

  /**
   * Get the preferences of an user.
   *
   * @param userId identifier of the user.
   * @param taskId identifier of the task
   *
   * @return the future with user preferences on task.
   */
  @GenIgnore
  default Future<List<String>> getPreferencesForUserOnTask(@NotNull final String userId, @NotNull final String taskId) {

    final Promise<JsonArray> promise = Promise.promise();
    this.getSocialPreferencesForUserOnTask(userId, taskId, promise);
    return Model.fromFutureJsonArray(promise.future(), String.class);

  }

  /**
   * Return the social explanation why an user is selected to be a possible
   * volunteer.
   *
   * @param userId identifier of the user.
   * @param taskId identifier of the task the user is involved.
   *
   * @return the future with the retrieved social explanation.
   */
  @GenIgnore
  default Future<SocialExplanation> retrieveSocialExplanation(@NotNull final String userId,
      @NotNull final String taskId) {

    final Promise<JsonObject> promise = Promise.promise();
    this.retrieveSocialExplanation(userId, taskId, promise);
    return Model.fromFutureJsonObject(promise.future(), SocialExplanation.class);

  }

  /**
   * Return the social explanation object why an user is selected to be a possible
   * volunteer.
   *
   * @param userId  identifier of the user.
   * @param taskId  identifier of the task the user is involved.
   * @param handler for the retrieved social explanation.
   */
  void retrieveSocialExplanation(@NotNull final String userId, @NotNull final String taskId,
      @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Set the social explanation why an user is selected to be a possible
   * volunteer.
   *
   * @param userId      identifier of the user.
   * @param taskId      identifier of the task the user is involved.
   * @param explanation why an user is selected.
   *
   * @return the future with the social explanation.
   */
  @GenIgnore
  default Future<SocialExplanation> setSocialExplanation(@NotNull final String userId, @NotNull final String taskId,
      @NotNull final SocialExplanation explanation) {

    final Promise<JsonObject> promise = Promise.promise();
    this.setSocialExplanation(userId, taskId, explanation.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), SocialExplanation.class);

  }

  /**
   * Set the social explanation object why an user is selected to be a possible
   * volunteer.
   *
   * @param userId      identifier of the user.
   * @param taskId      identifier of the task the user is involved.
   * @param explanation why an user is selected.
   * @param handler     for the new social explanation.
   */
  void setSocialExplanation(@NotNull final String userId, @NotNull final String taskId, @NotNull JsonObject explanation,
      @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Post the preferences answers of an user. This calculate the ranking of that
   * answers that an user has received. In other words, from a set of answers an
   * user has received it order form the best to the worst.
   *
   * @param userId      identifier of the user.
   * @param taskId      identifier of the task
   * @param userAnswers the array of {@link UserAnswer}'s that the user has
   *                    received.
   * @param handler     to notify the ranking of the users.
   *
   */
  void postSocialPreferencesAnswersForUserOnTask(@NotNull final String userId, @NotNull final String taskId,
      @NotNull JsonObject userAnswers, @NotNull Handler<AsyncResult<JsonArray>> handler);

  /**
   * Post the preferences of an user. This calculate the social user ranking. In
   * other words, from a set of volunteers it order form the best to the worst.
   *
   * @param userId      identifier of the user.
   * @param taskId      identifier of the task.
   * @param userAnswers the answers of the users.
   *
   * @return the future with the ranking of the users.
   */
  @GenIgnore
  default Future<List<UserAnswer>> postSocialPreferencesAnswersForUserOnTask(@NotNull final String userId,
      @NotNull final String taskId, @NotNull final UserAnswers userAnswers) {

    final Promise<JsonArray> promise = Promise.promise();
    final var data = userAnswers.toJsonObject();
    this.postSocialPreferencesAnswersForUserOnTask(userId, taskId, data, promise);
    return Model.fromFutureJsonArray(promise.future(), UserAnswer.class);

  }

  /**
   * Initialize the social relations of an user.
   *
   * @param userId  the identifier of the created user.
   * @param handler for the social relations.
   */
  void initializeSocialRelations(@NotNull String userId, @NotNull Handler<AsyncResult<Void>> handler);

  /**
   * Initialize the social relations of an user.
   *
   * @param profile of the created user.
   *
   * @return the user relations.
   */
  @GenIgnore
  default Future<Void> initializeSocialRelations(@NotNull final WeNetUserProfile profile) {

    final Promise<Void> promise = Promise.promise();
    this.initializeSocialRelations(profile.id, promise);
    return promise.future();

  }

  /**
   * Get the last initialize the social relations of an user.
   *
   * @param userId  the identifier of the created user.
   * @param handler for the social relations.
   */
  void getInitializeSocialRelations(@NotNull String userId, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Get the last initialize the social relations of an user.
   *
   * @param userId identifier of the user to get the initialization.
   *
   * @return the initialized data.
   */
  @GenIgnore
  default Future<JsonObject> getInitializeSocialRelations(@NotNull final String userId) {

    final Promise<JsonObject> promise = Promise.promise();
    this.getInitializeSocialRelations(userId, promise);
    return promise.future();

  }

  /**
   * Notify about a message that is interchanged in a task.
   *
   * @param message that has been interchanged.
   * @param handler for the social relations.
   */
  void socialNotificationInteraction(@NotNull JsonObject message, @NotNull Handler<AsyncResult<Void>> handler);

  /**
   * Notify about a message that is interchanged in a task.
   *
   * @param message that has been interchanged.
   *
   * @return the notification result.
   */
  @GenIgnore
  default Future<Void> socialNotificationInteraction(@NotNull final UserMessage message) {

    final Promise<Void> promise = Promise.promise();
    this.socialNotificationInteraction(message.toJsonObject(), promise);
    return promise.future();

  }

  /**
   * Get the last social notification.
   *
   * @param handler for the social notification.
   */
  void getSocialNotificationInteraction(@NotNull Handler<AsyncResult<JsonArray>> handler);

  /**
   * Get the last social notification.
   *
   * @return the last social notification.
   */
  @GenIgnore
  default Future<List<UserMessage>> getSocialNotificationInteraction() {

    final Promise<JsonArray> promise = Promise.promise();
    this.getSocialNotificationInteraction(promise);
    return Model.fromFutureJsonArray(promise.future(), UserMessage.class);

  }

  /**
   * Delete the last social notification.
   *
   * @param handler for the social notification.
   */
  void deleteSocialNotificationInteraction(@NotNull Handler<AsyncResult<Void>> handler);

  /**
   * Delete the last social notification.
   *
   * @return the last social notification.
   */
  @GenIgnore
  default Future<Void> deleteSocialNotificationInteraction() {

    final Promise<Void> promise = Promise.promise();
    this.deleteSocialNotificationInteraction(promise);
    return promise.future();

  }

  /**
   * Put the preferred answers for an user. This notify witch answer of the
   * ranking is selected by an user.
   *
   * @param userId      identifier of the user.
   * @param taskId      identifier of the task
   * @param selection   the selected answer of the question.
   * @param userAnswers the array of {@link UserAnswer}'s that the user has
   *                    received.
   * @param handler     to notify the updated result.
   *
   */
  void putSocialPreferencesSelectedAnswerForUserOnTask(@NotNull final String userId, @NotNull final String taskId,
      final int selection, @NotNull JsonObject userAnswers, @NotNull Handler<AsyncResult<Void>> handler);

  /**
   * Put the preferred answers for an user. This notify witch answer of the
   * ranking is selected by an user.
   *
   * @param userId      identifier of the user.
   * @param taskId      identifier of the task.
   * @param selection   the selected answer of the question.
   * @param userAnswers the answers of the users.
   *
   * @return the future with the updated data.
   */
  @GenIgnore
  default Future<Void> putSocialPreferencesSelectedAnswerForUserOnTask(@NotNull final String userId,
      @NotNull final String taskId, final int selection, @NotNull final UserAnswers userAnswers) {

    final Promise<Void> promise = Promise.promise();
    final var data = userAnswers.toJsonObject();
    this.putSocialPreferencesSelectedAnswerForUserOnTask(userId, taskId, selection, data, promise);
    return promise.future();

  }

  /**
   * Get the preferences answers by a user.
   *
   * @param userId    identifier of the user.
   * @param taskId    identifier of the task
   * @param selection the selected answer of the question.
   * @param handler   to notify the updated result.
   *
   */
  void getSocialPreferencesSelectedAnswerForUserOnTask(@NotNull final String userId, @NotNull final String taskId,
      final int selection, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Get the preferences answers by a user.
   *
   * @param userId    identifier of the user.
   * @param taskId    identifier of the task.
   * @param selection the selected answer of the question.
   *
   * @return the future with the selected user answers.
   */
  @GenIgnore
  default Future<UserAnswers> getSocialPreferencesSelectedAnswerForUserOnTask(@NotNull final String userId,
      @NotNull final String taskId, final int selection) {

    final Promise<JsonObject> promise = Promise.promise();
    this.getSocialPreferencesSelectedAnswerForUserOnTask(userId, taskId, selection, promise);
    return Model.fromFutureJsonObject(promise.future(), UserAnswers.class);

  }

  /**
   * Get the preferences answers by a user on a task.
   *
   * @param userId  identifier of the user.
   * @param taskId  identifier of the task
   * @param handler to notify the updated result.
   *
   */
  void getSocialPreferencesAnswersForUserOnTask(@NotNull final String userId, @NotNull final String taskId,
      @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Get the preferences answers by a user on a task.
   *
   * @param userId identifier of the user.
   * @param taskId identifier of the task.
   *
   * @return the future with the selected user answers.
   */
  @GenIgnore
  default Future<UserAnswers> getSocialPreferencesAnswersForUserOnTask(@NotNull final String userId,
      @NotNull final String taskId) {

    final Promise<JsonObject> promise = Promise.promise();
    this.getSocialPreferencesAnswersForUserOnTask(userId, taskId, promise);
    return Model.fromFutureJsonObject(promise.future(), UserAnswers.class);

  }

  /**
   * Notify about an update over a user profile.
   *
   * @param userId       identifier of the updated profile.
   * @param notification with the changes on the profile.
   * @param handler      for the social relations.
   */
  void socialNotificationProfileUpdate(@NotNull String userId, JsonObject notification,
      @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Notify about an update over a user profile.
   *
   * @param userId       identifier of the updated profile.
   * @param notification with the changes on the profile.
   *
   * @return the user relations.
   */
  @GenIgnore
  default Future<JsonObject> socialNotificationProfileUpdate(@NotNull final String userId,
      final ProfileUpdateNotification notification) {

    final Promise<JsonObject> promise = Promise.promise();
    final var data = notification.toJsonObject();
    this.socialNotificationProfileUpdate(userId, data, promise);
    return promise.future();

  }

  /**
   * Get the last social notification.
   *
   * @param userId  identifier of the updated profile.
   * @param handler for the social notification.
   */
  void getSocialNotificationProfileUpdate(@NotNull String userId, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Get the last social notification.
   *
   * @param userId identifier of the updated profile.
   *
   * @return the last social notification.
   */
  @GenIgnore
  default Future<ProfileUpdateNotification> getSocialNotificationProfileUpdate(@NotNull final String userId) {

    final Promise<JsonObject> promise = Promise.promise();
    this.getSocialNotificationProfileUpdate(userId, promise);
    return Model.fromFutureJsonObject(promise.future(), ProfileUpdateNotification.class);

  }

  /**
   * Delete the last social notification.
   *
   * @param userId  identifier of the updated profile to delete.
   *
   * @param handler for the social notification.
   */
  void deleteSocialNotificationProfileUpdate(@NotNull final String userId, @NotNull Handler<AsyncResult<Void>> handler);

  /**
   * Delete the last social notification.
   *
   * @param userId identifier of the updated profile to delete.
   *
   * @return the last social notification.
   */
  @GenIgnore
  default Future<Void> deleteSocialNotificationProfileUpdate(@NotNull final String userId) {

    final Promise<Void> promise = Promise.promise();
    this.deleteSocialNotificationProfileUpdate(userId, promise);
    return promise.future();

  }

  /**
   * Shuffle a set of user using the learned social diversity.
   *
   * @param userIds identifiers of the users to shuffle.
   * @param handler for the shuffled user identifiers.
   */
  void socialShuffle(JsonObject userIds, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Shuffle a set of user using the learned social diversity.
   *
   * @param userIds identifiers of the users to shuffle.
   *
   * @return the an ordered list of Users based on diversity attributes.
   */
  @GenIgnore
  default Future<UserIds> socialShuffle(@NotNull final UserIds userIds) {

    final Promise<JsonObject> promise = Promise.promise();
    final var data = userIds.toJsonObject();
    this.socialShuffle(data, promise);
    return Model.fromFutureJsonObject(promise.future(), UserIds.class);

  }

  /**
   * Get the last shuffle a set of user using the learned social diversity.
   *
   * @param handler for the shuffled user identifiers.
   */
  void getSocialShuffle(@NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Get the last shuffle a set of user using the learned social diversity.
   *
   * @return the an ordered list of Users based on diversity attributes.
   */
  @GenIgnore
  default Future<UserIds> getSocialShuffle() {

    final Promise<JsonObject> promise = Promise.promise();
    this.getSocialShuffle(promise);
    return Model.fromFutureJsonObject(promise.future(), UserIds.class);

  }

}
