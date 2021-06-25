/*
 * -----------------------------------------------------------------------------
 *
 *   Copyright 2019 - 2022 UDT-IA, IIIA-CSIC
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

import eu.internetofus.common.vertx.ComponentClient;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

/**
 * Implementation of the {@link WeNetSocialContextBuilder}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetSocialContextBuilderClient extends ComponentClient implements WeNetSocialContextBuilder {

  /**
   * The default URL to bind the client.
   */
  public static final String DEFAULT_SOCIAL_CONTEXT_BUILDER_API_URL = "https://wenet.u-hopper.com/prod/social_context_builder";

  /**
   * The name of the configuration property that contains the URL to the social context builder API.
   */
  public static final String SOCIAL_CONTEXT_BUILDER_CONF_KEY = "socialContextBuilder";

  /**
   * Create a new service to interact with the WeNet interaction protocol engine.
   *
   * @param client to interact with the other modules.
   * @param conf   configuration of the component.
   */
  public WeNetSocialContextBuilderClient(final WebClient client, final JsonObject conf) {

    super(client, conf.getString(SOCIAL_CONTEXT_BUILDER_CONF_KEY, DEFAULT_SOCIAL_CONTEXT_BUILDER_API_URL));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveSocialRelations(final String userId, final Handler<AsyncResult<JsonArray>> handler) {

    this.getJsonArray("/social/relations", userId, "/").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updatePreferencesForUserOnTask(final String userId, final String taskId, final JsonArray volunteers, final Handler<AsyncResult<JsonArray>> handler) {

    this.post(volunteers, "/social/preferences", userId, taskId, "/").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveSocialExplanation(final String userId, final String taskId, final Handler<AsyncResult<JsonObject>> handler) {

    this.getJsonObject("/social/explanations", userId, taskId, "/").onComplete(handler);

  }

}
