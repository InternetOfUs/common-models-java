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

import eu.internetofus.common.components.WeNetComponent;
import eu.internetofus.common.components.models.Incentive;
import eu.internetofus.common.components.models.Task;
import eu.internetofus.common.components.models.TaskTransaction;
import eu.internetofus.common.model.Model;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.serviceproxy.ServiceBinder;
import javax.validation.constraints.NotNull;

/**
 * The class used to interact with the WeNet interaction protocol engine.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ProxyGen
public interface WeNetInteractionProtocolEngine extends WeNetComponent {

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_component.InteractionProtocolEngine";

  /**
   * Create a proxy of the {@link WeNetInteractionProtocolEngine}.
   *
   * @param vertx where the service has to be used.
   *
   * @return the task.
   */
  static WeNetInteractionProtocolEngine createProxy(final Vertx vertx) {

    return new WeNetInteractionProtocolEngineVertxEBProxy(vertx, WeNetInteractionProtocolEngine.ADDRESS);
  }

  /**
   * Register this service.
   *
   * @param vertx  that contains the event bus to use.
   * @param client to do HTTP requests to other services.
   * @param conf   configuration to use.
   */
  static void register(final Vertx vertx, final WebClient client, final JsonObject conf) {

    new ServiceBinder(vertx).setAddress(WeNetInteractionProtocolEngine.ADDRESS)
        .register(WeNetInteractionProtocolEngine.class, new WeNetInteractionProtocolEngineClient(client, conf));

  }

  /**
   * {@inheritDoc}
   *
   * ATTENTION: You must to maintains this method to guarantee that VertX
   * generates the code for this method.
   */
  @Override
  void obtainApiUrl(final Handler<AsyncResult<String>> handler);

  /**
   * Send a message to be processed.
   *
   * @param message to be processed.
   * @param handler to send message.
   */
  void sendMessage(@NotNull JsonObject message, Handler<AsyncResult<JsonObject>> handler);

  /**
   * Send a message to be processed.
   *
   * @param message to be processed.
   *
   * @return the future message.
   */
  @GenIgnore
  default Future<ProtocolMessage> sendMessage(@NotNull final ProtocolMessage message) {

    final Promise<JsonObject> promise = Promise.promise();
    this.sendMessage(message.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), ProtocolMessage.class);
  }

  /**
   * Send a incentive to be processed.
   *
   * @param incentive to be processed.
   * @param handler   for the send incentive.
   */
  void sendIncentive(@NotNull JsonObject incentive, Handler<AsyncResult<JsonObject>> handler);

  /**
   * Send a incentive to be processed.
   *
   * @param incentive to be processed.
   *
   * @return the future incentive.
   */
  @GenIgnore
  default Future<Incentive> sendIncentive(@NotNull final Incentive incentive) {

    final Promise<JsonObject> promise = Promise.promise();
    this.sendIncentive(incentive.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), Incentive.class);

  }

  /**
   * Inform that a task has been created.
   *
   * @param task    that has been created.
   * @param handler for the created task.
   */
  void createdTask(@NotNull JsonObject task, Handler<AsyncResult<JsonObject>> handler);

  /**
   * Inform that a task has been created.
   *
   * @param task that has been created.
   *
   * @return the future created task.
   */
  @GenIgnore
  default Future<Task> createdTask(@NotNull final Task task) {

    final Promise<JsonObject> promise = Promise.promise();
    this.createdTask(task.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), Task.class);

  }

  /**
   * Try to do a transaction over a task.
   *
   * @param transaction to do.
   * @param handler     for teh transaction to do.
   */
  void doTransaction(@NotNull JsonObject transaction, Handler<AsyncResult<JsonObject>> handler);

  /**
   * Try to do a transaction over a task.
   *
   * @param transaction to do.
   *
   * @return the future transaction.
   */
  @GenIgnore
  default Future<TaskTransaction> doTransaction(@NotNull final TaskTransaction transaction) {

    final Promise<JsonObject> promise = Promise.promise();
    this.doTransaction(transaction.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), TaskTransaction.class);

  }

  /**
   * Obtain the status of an user in a community.
   *
   * @param communityId identifier of the community where is the user.
   * @param userId      identifier of the user to get the state.
   *
   * @return the future community user state.
   */
  @GenIgnore
  default Future<State> retrieveCommunityUserState(@NotNull final String communityId, @NotNull final String userId) {

    final Promise<JsonObject> promise = Promise.promise();
    this.retrieveCommunityUserState(communityId, userId, promise);
    return Model.fromFutureJsonObject(promise.future(), State.class);

  }

  /**
   * Obtain the status of an user in a community.
   *
   * @param communityId identifier of the community where is the user.
   * @param userId      identifier of the user to get the state.
   * @param handler     for the obtained community user state.
   */
  void retrieveCommunityUserState(@NotNull final String communityId, @NotNull final String userId,
      Handler<AsyncResult<JsonObject>> handler);

  /**
   * Merge the state of the user on a community.
   *
   * @param communityId identifier of the community where is the user.
   * @param userId      identifier of the user to get the state.
   * @param newState    for the user in the community.
   *
   * @return the future community user state.
   */
  @GenIgnore
  default Future<State> mergeCommunityUserState(@NotNull final String communityId, @NotNull final String userId,
      @NotNull final State newState) {

    final Promise<JsonObject> promise = Promise.promise();
    this.mergeCommunityUserState(communityId, userId, newState.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), State.class);

  }

  /**
   * Merge the state of the user on a community.
   *
   * @param communityId identifier of the community where is the user.
   * @param userId      identifier of the user to get the state.
   * @param newState    for the user in the community.
   * @param handler     for the merged community user state.
   */
  void mergeCommunityUserState(@NotNull final String communityId, @NotNull final String userId,
      @NotNull final JsonObject newState, Handler<AsyncResult<JsonObject>> handler);

  /**
   * Send a event to be processed.
   *
   * @param event   to be processed.
   * @param handler to send event.
   */
  void sendEvent(@NotNull JsonObject event, Handler<AsyncResult<JsonObject>> handler);

  /**
   * Send a event to be processed.
   *
   * @param event to be processed.
   *
   * @return the future event.
   */
  @GenIgnore
  default Future<ProtocolEvent> sendEvent(@NotNull final ProtocolEvent event) {

    final Promise<JsonObject> promise = Promise.promise();
    this.sendEvent(event.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), ProtocolEvent.class);
  }

  /**
   * Delete a event to be processed.
   *
   * @param id      identifier of the event to delete.
   * @param handler to inform id teh vent is deleted.
   */
  void deleteEvent(long id, Handler<AsyncResult<Void>> handler);

  /**
   * Delete a event to be processed.
   *
   * @param id identifier of the event to delete.
   *
   * @return the future if the delete is done.
   */
  @GenIgnore
  default Future<Void> deleteEvent(final long id) {

    final Promise<Void> promise = Promise.promise();
    this.deleteEvent(id, promise);
    return promise.future();
  }

}
