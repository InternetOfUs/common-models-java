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

/**
 * The class used to interact with the WeNet social context builder.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ProxyGen
public interface WeNetSocialContextBuilder {

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_component.SocialContextBuilder";

  /**
   * Create a proxy of the {@link WeNetSocialContextBuilder}.
   *
   * @param vertx where the service has to be used.
   *
   * @return the task.
   */
  static WeNetSocialContextBuilder createProxy(final Vertx vertx) {

    return new WeNetSocialContextBuilderVertxEBProxy(vertx, WeNetSocialContextBuilder.ADDRESS);
  }

  /**
   * Register this service.
   *
   * @param vertx  that contains the event bus to use.
   * @param client to do HTTP requests to other services.
   * @param conf   configuration to use.
   */
  static void register(final Vertx vertx, final WebClient client, final JsonObject conf) {

    new ServiceBinder(vertx).setAddress(WeNetSocialContextBuilder.ADDRESS).register(WeNetSocialContextBuilder.class, new WeNetSocialContextBuilderClient(client, conf));

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
   * Update the preferences of an user.
   *
   * @param userId     identifier of the user.
   * @param taskId     identifier of the task
   * @param volunteers the identifier of the volunteers of the task.
   * @param handler    for the user relations.
   */
  void updatePreferencesForUserOnTask(@NotNull String userId, @NotNull String taskId, @NotNull JsonArray volunteers, @NotNull Handler<AsyncResult<JsonArray>> handler);

  /**
   * Update the preferences of an user.
   *
   * @param userId     identifier of the user.
   * @param taskId     identifier of the task
   * @param volunteers the identifier of the volunteers of the task.
   *
   * @return the future with the user relations.
   */
  @GenIgnore
  default Future<JsonArray> updatePreferencesForUserOnTask(@NotNull final String userId, @NotNull final String taskId, @NotNull final JsonArray volunteers) {

    final Promise<JsonArray> promise = Promise.promise();
    this.updatePreferencesForUserOnTask(userId, taskId, volunteers, promise);
    return promise.future();

  }

  /**
   * Return the social explanation why an user is selected to be a possible volunteer.
   *
   * @param userId identifier of the user.
   * @param taskId identifier of the task the user is involved.
   *
   * @return the future with the retrieved social explanation.
   */
  @GenIgnore
  default Future<SocialExplanation> retrieveSocialExplanation(@NotNull final String userId, @NotNull final String taskId) {

    final Promise<JsonObject> promise = Promise.promise();
    this.retrieveSocialExplanation(userId, taskId, promise);
    return Model.fromFutureJsonObject(promise.future(), SocialExplanation.class);

  }

  /**
   * Return the social explanation object why an user is selected to be a possible volunteer.
   *
   * @param userId  identifier of the user.
   * @param taskId  identifier of the task the user is involved.
   * @param handler for the retrieved social explanation.
   */
  void retrieveSocialExplanation(@NotNull final String userId, @NotNull final String taskId, @NotNull Handler<AsyncResult<JsonObject>> handler);

}
