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
  public Future<List<UserRelation>> retrieveSocialRelations(@NotNull final String userId) {

    final Promise<JsonArray> promise = Promise.promise();
    this.retrieveSocialRelations(userId, promise);
    return Model.fromFutureJsonArray(promise.future(), UserRelation.class);

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
  public void setSocialRelations(@NotNull final String userId, @NotNull final JsonArray relations,
      @NotNull final Handler<AsyncResult<JsonArray>> handler) {

    this.post(relations, "/social/relations", userId, "/").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void getPreferencesForUserOnTask(@NotNull final String userId, @NotNull final String taskId,
      @NotNull final Handler<AsyncResult<JsonArray>> handler) {

    this.getJsonArray("/social/preferences", userId, taskId, "/").onComplete(handler);

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
  public Future<List<String>> postSocialPreferencesAnswersForUserOnTask(@NotNull final String userId,
      @NotNull final String taskId, @NotNull final List<UserAnswer> userAnswers) {

    final Promise<JsonArray> promise = Promise.promise();
    final var array = Model.toJsonArray(userAnswers);
    this.postSocialPreferencesAnswersForUserOnTask(userId, taskId, array, promise);
    return Model.fromFutureJsonArray(promise.future(), String.class);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void getPreferencesAnswersForUserOnTask(@NotNull final String userId, @NotNull final String taskId,
      @NotNull final Handler<AsyncResult<JsonArray>> handler) {

    this.getJsonArray("/social/preferences/answers", userId, taskId, "/").onComplete(handler);

  }
}
