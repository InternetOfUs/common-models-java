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

import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.incentive_server.Incentive;
import eu.internetofus.common.components.task_manager.Task;
import eu.internetofus.common.components.task_manager.TaskTransaction;
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
public interface WeNetInteractionProtocolEngine {

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

}
