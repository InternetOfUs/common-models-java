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

package eu.internetofus.common.components.profile_manager;

import javax.validation.constraints.NotNull;

import eu.internetofus.common.vertx.ComponentClient;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.serviceproxy.ServiceBinder;

/**
 * The class used to interact with the WeNet profile manager.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ProxyGen
public interface WeNetProfileManager {

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_component.profileManager";

  /**
   * Create a proxy of the {@link WeNetProfileManager}.
   *
   * @param vertx where the service has to be used.
   *
   * @return the task.
   */
  static WeNetProfileManager createProxy(final Vertx vertx) {

    return new WeNetProfileManagerVertxEBProxy(vertx, WeNetProfileManager.ADDRESS);
  }

  /**
   * Register this service.
   *
   * @param vertx  that contains the event bus to use.
   * @param client to do HTTP requests to other services.
   * @param conf   configuration to use.
   */
  static void register(final Vertx vertx, final WebClient client, final JsonObject conf) {

    new ServiceBinder(vertx).setAddress(WeNetProfileManager.ADDRESS).register(WeNetProfileManager.class, new WeNetProfileManagerClient(client, conf));

  }

  /**
   * Create a {@link WeNetUserProfile} in Json format.
   *
   * @param profile       to create.
   * @param createHandler handler to manage the creation process.
   */
  void createProfile(@NotNull JsonObject profile, @NotNull Handler<AsyncResult<JsonObject>> createHandler);

  /**
   * Create a profile.
   *
   * @param profile       to create.
   * @param createHandler handler to manage the creation process.
   */
  @GenIgnore
  default void createProfile(@NotNull final WeNetUserProfile profile, @NotNull final Handler<AsyncResult<WeNetUserProfile>> createHandler) {

    this.createProfile(profile.toJsonObject(), ComponentClient.handlerForModel(WeNetUserProfile.class, createHandler));

  }

  /**
   * Return a {@link WeNetUserProfile} in Json format.
   *
   * @param id              identifier of the profile to get.
   * @param retrieveHandler handler to manage the retrieve process.
   */
  void retrieveJsonProfile(@NotNull String id, @NotNull Handler<AsyncResult<JsonObject>> retrieveHandler);

  /**
   * Return a profile.
   *
   * @param id              identifier of the profile to get.
   * @param retrieveHandler handler to manage the retrieve process.
   */
  @GenIgnore
  default void retrieveProfile(@NotNull final String id, @NotNull final Handler<AsyncResult<WeNetUserProfile>> retrieveHandler) {

    this.retrieveJsonProfile(id, ComponentClient.handlerForModel(WeNetUserProfile.class, retrieveHandler));

  }

  /**
   * Delete a profile.
   *
   * @param id            identifier of the profile to get.
   * @param deleteHandler handler to manage the delete process.
   */
  void deleteProfile(@NotNull String id, @NotNull Handler<AsyncResult<Void>> deleteHandler);

  /**
   * Create a {@link CommunityProfile} in Json format.
   *
   * @param community     to create.
   * @param createHandler handler to manage the creation process.
   */
  void createCommunity(@NotNull JsonObject community, @NotNull Handler<AsyncResult<JsonObject>> createHandler);

  /**
   * Create a community.
   *
   * @param community     to create.
   * @param createHandler handler to manage the creation process.
   */
  @GenIgnore
  default void createCommunity(@NotNull final CommunityProfile community, @NotNull final Handler<AsyncResult<CommunityProfile>> createHandler) {

    this.createCommunity(community.toJsonObject(), ComponentClient.handlerForModel(CommunityProfile.class, createHandler));

  }

  /**
   * Return a {@link CommunityProfile} in Json format.
   *
   * @param id              identifier of the community to get.
   * @param retrieveHandler handler to manage the retrieve process.
   */
  void retrieveJsonCommunity(@NotNull String id, @NotNull Handler<AsyncResult<JsonObject>> retrieveHandler);

  /**
   * Return a community.
   *
   * @param id              identifier of the community to get.
   * @param retrieveHandler handler to manage the retrieve process.
   */
  @GenIgnore
  default void retrieveCommunity(@NotNull final String id, @NotNull final Handler<AsyncResult<CommunityProfile>> retrieveHandler) {

    this.retrieveJsonCommunity(id, ComponentClient.handlerForModel(CommunityProfile.class, retrieveHandler));

  }

  /**
   * Delete a community.
   *
   * @param id            identifier of the community to get.
   * @param deleteHandler handler to manage the delete process.
   */
  void deleteCommunity(@NotNull String id, @NotNull Handler<AsyncResult<Void>> deleteHandler);

}
