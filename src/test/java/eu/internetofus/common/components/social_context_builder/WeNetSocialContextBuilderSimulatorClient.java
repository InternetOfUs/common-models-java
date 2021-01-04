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

package eu.internetofus.common.components.social_context_builder;

import java.util.List;

import javax.validation.constraints.NotNull;

import eu.internetofus.common.components.Model;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

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
  public Future<JsonArray> updatePreferencesForUserOnTask(@NotNull final String userId, @NotNull final String taskId,
      @NotNull final JsonArray volunteers) {

    final Promise<JsonArray> promise = Promise.promise();
    this.updatePreferencesForUserOnTask(userId, taskId, volunteers, promise);
    return promise.future();

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
  public void setSocialRelations(@NotNull String userId, @NotNull JsonArray relations,
      @NotNull Handler<AsyncResult<JsonArray>> handler) {

    this.post(relations, "/social/relations", userId, "/").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void getPreferencesForUserOnTask(@NotNull String userId, @NotNull String taskId,
      @NotNull Handler<AsyncResult<JsonArray>> handler) {

    this.getJsonArray("/social/preferences", userId, taskId, "/").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setSocialExplanation(@NotNull String userId, @NotNull String taskId, @NotNull JsonObject explanation,
      @NotNull Handler<AsyncResult<JsonObject>> handler) {

    this.post(explanation, "/social/explanations", userId, taskId, "/").onComplete(handler);

  }

}
