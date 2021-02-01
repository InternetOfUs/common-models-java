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

}
