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

package eu.internetofus.common.components.interaction_protocol_engine;

import eu.internetofus.common.vertx.ComponentClient;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import javax.validation.constraints.NotNull;

/**
 * The implementation of the {@link WeNetInteractionProtocolEngine}.
 *
 * @see WeNetInteractionProtocolEngine
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetInteractionProtocolEngineClient extends ComponentClient implements WeNetInteractionProtocolEngine {

  /**
   * The default URL to bind the client.
   */
  public static final String DEFAULT_INTERACTION_PROTOCOL_ENGINE_API_URL = "https://wenet.u-hopper.com/prod/interaction_protocol_engine";

  /**
   * The name of the configuration property that contains the URL to the incentive
   * server API.
   */
  public static final String INTERACTION_PROTOCOL_ENGINE_CONF_KEY = "interactionProtocolEngine";

  /**
   * Create a new service to interact with the WeNet interaction protocol engine.
   *
   * @param client to interact with the other modules.
   * @param conf   configuration.
   */
  public WeNetInteractionProtocolEngineClient(final WebClient client, final JsonObject conf) {

    super(client, conf.getString(INTERACTION_PROTOCOL_ENGINE_CONF_KEY, DEFAULT_INTERACTION_PROTOCOL_ENGINE_API_URL));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void sendMessage(final JsonObject message, final Handler<AsyncResult<JsonObject>> handler) {

    this.post(message, "/messages").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void sendIncentive(final JsonObject incentive, final Handler<AsyncResult<JsonObject>> handler) {

    this.post(incentive, "/incentives").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createdTask(@NotNull final JsonObject task, final Handler<AsyncResult<JsonObject>> handler) {

    this.post(task, "/tasks/created").onComplete(handler);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void doTransaction(@NotNull final JsonObject transaction, final Handler<AsyncResult<JsonObject>> handler) {

    this.post(transaction, "/tasks/transactions").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveCommunityUserState(@NotNull final String communityId, @NotNull final String userId,
      final Handler<AsyncResult<JsonObject>> handler) {

    this.getJsonObject("/states/communities", communityId, "/users", userId).onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeCommunityUserState(@NotNull final String communityId, @NotNull final String userId,
      @NotNull final JsonObject newState, final Handler<AsyncResult<JsonObject>> handler) {

    this.patch(newState, "/states/communities", communityId, "/users", userId).onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void sendEvent(final JsonObject event, final Handler<AsyncResult<JsonObject>> handler) {

    this.post(event, "/events").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteEvent(final long id, final Handler<AsyncResult<Void>> handler) {

    this.delete("/events", id).onComplete(handler);

  }

}
