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

package eu.internetofus.common.components.interaction_protocol_engine;

import eu.internetofus.common.vertx.ComponentClient;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import java.util.LinkedHashMap;
import java.util.Map;
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

  /**
   * {@inheritDoc}
   */
  @Override
  public void addInteraction(@NotNull final JsonObject interaction, final Handler<AsyncResult<JsonObject>> handler) {

    this.post(interaction, "/interactions").onComplete(handler);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void getInteractionsPage(final String appId, final String communityId, final String taskTypeId,
      final String taskId, final String senderId, final String receiverId, final Boolean hasTransaction,
      final String transactionLabel, final Long transactionFrom, final Long transactionTo, final Boolean hasMessage,
      final String messageLabel, final Long messageFrom, final Long messageTo, final String order, final int offset,
      final int limit, final Handler<AsyncResult<JsonObject>> handler) {

    final var params = this.createQueryParamsFor(appId, communityId, taskTypeId, taskId, senderId, receiverId,
        hasTransaction, transactionLabel, transactionFrom, transactionTo, hasMessage, messageLabel, messageFrom,
        messageTo);
    if (order != null) {

      params.put("order", order);
    }
    params.put("offset", String.valueOf(offset));
    params.put("limit", String.valueOf(limit));

    this.getJsonObject(params, "/interactions").onComplete(handler);

  }

  /**
   * Create the query parameters.
   *
   * @param appId            identifier of the application where the interaction
   *                         is done.
   * @param communityId      identifier of the community where the interaction is
   *                         done.
   * @param taskTypeId       identifier of the task type where the interaction is
   *                         done.
   * @param taskId           identifier of the task where the interaction is done.
   * @param senderId         identifier of the user that has started the
   *                         interaction.
   * @param receiverId       identifier of the user that has end the interaction.
   * @param hasTransaction   this is {@code true} if the interaction requires a
   *                         transaction, {@code false} if no transaction has to
   *                         be defined or {@code null} if does not matter.
   * @param transactionLabel the label of the transaction that has started the
   *                         interaction.
   * @param transactionFrom  the minimum time stamp, inclusive, where the
   *                         interaction has to be started, or {@code null} to
   *                         start at midnight, January 1, 1970 UTC.
   * @param transactionTo    the maximum time stamp, inclusive, where the
   *                         interaction has to be started or {@code null} to be
   *                         the current time.
   * @param hasMessage       this is {@code true} if the interaction requires a
   *                         message, {@code false} if no message has to be
   *                         defined or {@code null} if does not matter.
   * @param messageLabel     the label of the message that has end the
   *                         interaction.
   * @param messageFrom      the minimum time stamp, inclusive, where the
   *                         interaction has end, or {@code null} to start at
   *                         midnight, January 1, 1970 UTC.
   * @param messageTo        the maximum time stamp, inclusive, where the
   *                         interaction has end or {@code null} to be the current
   *                         time.
   *
   * @return the query parameters.
   */
  private Map<String, String> createQueryParamsFor(final String appId, final String communityId,
      final String taskTypeId, final String taskId, final String senderId, final String receiverId,
      final Boolean hasTransaction, final String transactionLabel, final Long transactionFrom, final Long transactionTo,
      final Boolean hasMessage, final String messageLabel, final Long messageFrom, final Long messageTo) {

    final var params = new LinkedHashMap<String, String>();
    if (appId != null) {

      params.put("appId", appId);
    }
    if (communityId != null) {

      params.put("communityId", communityId);
    }
    if (taskTypeId != null) {

      params.put("taskTypeId", taskTypeId);
    }
    if (taskId != null) {

      params.put("taskId", taskId);
    }
    if (senderId != null) {

      params.put("senderId", senderId);
    }
    if (receiverId != null) {

      params.put("receiverId", receiverId);
    }
    if (hasTransaction != null) {

      params.put("hasTransaction", String.valueOf(hasTransaction));
    }
    if (transactionLabel != null) {

      params.put("transactionLabel", transactionLabel);
    }
    if (transactionFrom != null) {

      params.put("transactionFrom", String.valueOf(transactionFrom));
    }
    if (transactionTo != null) {

      params.put("transactionTo", String.valueOf(transactionTo));
    }
    if (hasMessage != null) {

      params.put("hasMessage", String.valueOf(hasMessage));
    }
    if (messageLabel != null) {

      params.put("messageLabel", messageLabel);
    }
    if (messageFrom != null) {

      params.put("messageFrom", String.valueOf(messageFrom));
    }
    if (messageTo != null) {

      params.put("messageTo", String.valueOf(messageTo));
    }
    return params;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteInteractions(final String appId, final String communityId, final String taskTypeId,
      final String taskId, final String senderId, final String receiverId, final Boolean hasTransaction,
      final String transactionLabel, final Long transactionFrom, final Long transactionTo, final Boolean hasMessage,
      final String messageLabel, final Long messageFrom, final Long messageTo,
      final Handler<AsyncResult<Void>> handler) {

    final var params = this.createQueryParamsFor(appId, communityId, taskTypeId, taskId, senderId, receiverId,
        hasTransaction, transactionLabel, transactionFrom, transactionTo, hasMessage, messageLabel, messageFrom,
        messageTo);
    this.delete(params, "/interactions").onComplete(handler);
  }

}
