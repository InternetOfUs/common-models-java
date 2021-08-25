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
   * Return the relations of an user.
   *
   * @param userId  identifier of the user.
   * @param handler for the social relations.
   */
  void retrieveSocialRelations(@NotNull String userId, @NotNull Handler<AsyncResult<JsonArray>> handler);

  /**
   * Return the relations of an user.
   *
   * @param userId identifier of the user.
   *
   * @return the user relations.
   */
  @GenIgnore
  default Future<List<UserRelation>> retrieveSocialRelations(@NotNull final String userId) {

    final Promise<JsonArray> promise = Promise.promise();
    this.retrieveSocialRelations(userId, promise);
    return Model.fromFutureJsonArray(promise.future(), UserRelation.class);

  }

  /**
   * Set the relations of an user.
   *
   * @param userId    identifier of the user.
   * @param relations to set for the specified user.
   * @param handler   for the social relations.
   */
  void setSocialRelations(@NotNull String userId, @NotNull JsonArray relations,
      @NotNull Handler<AsyncResult<JsonArray>> handler);

  /**
   * Return the relations of an user.
   *
   * @param userId    identifier of the user.
   * @param relations to set for the specified user.
   *
   * @return the user relations.
   */
  @GenIgnore
  default Future<List<UserRelation>> setSocialRelations(@NotNull final String userId,
      final List<UserRelation> relations) {

    final Promise<JsonArray> promise = Promise.promise();
    final var relationsArray = Model.toJsonArray(relations);
    this.setSocialRelations(userId, relationsArray, promise);
    return Model.fromFutureJsonArray(promise.future(), UserRelation.class);

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
  void getPreferencesForUserOnTask(@NotNull String userId, @NotNull String taskId,
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
    this.getPreferencesForUserOnTask(userId, taskId, promise);
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
      @NotNull final String taskId, @NotNull final AnswersData userAnswers) {

    final Promise<JsonArray> promise = Promise.promise();
    final var data = userAnswers.toJsonObject();
    this.postSocialPreferencesAnswersForUserOnTask(userId, taskId, data, promise);
    return Model.fromFutureJsonArray(promise.future(), UserAnswer.class);

  }

  /**
   * Get the preferences answers of an user.
   *
   * @param userId  identifier of the user.
   * @param taskId  identifier of the task
   * @param handler for the user preferences answers on task.
   */
  void getPreferencesAnswersForUserOnTask(@NotNull String userId, @NotNull String taskId,
      @NotNull Handler<AsyncResult<JsonArray>> handler);

  /**
   * Get the preferences answers of an user.
   *
   * @param userId identifier of the user.
   * @param taskId identifier of the task
   *
   * @return the future with user preferences answers on task.
   */
  @GenIgnore
  default Future<List<UserAnswer>> getPreferencesAnswersForUserOnTask(@NotNull final String userId,
      @NotNull final String taskId) {

    final Promise<JsonArray> promise = Promise.promise();
    this.getPreferencesAnswersForUserOnTask(userId, taskId, promise);
    return Model.fromFutureJsonArray(promise.future(), UserAnswer.class);

  }

}
