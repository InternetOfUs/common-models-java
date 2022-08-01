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

import eu.internetofus.common.components.AbstractComponentMocker;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

/**
 * The mocked server for the {@link WeNetSocialContextBuilder} and
 * {@link WeNetSocialContextBuilderSimulator}.
 *
 * @see WeNetSocialContextBuilder
 * @see WeNetSocialContextBuilderSimulator
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetSocialContextBuilderSimulatorMocker extends AbstractComponentMocker {

  /**
   * The context with the values stored on the mocker.
   */
  protected WeNetSocialContextBuilderSimulatorGetSetContext context = new WeNetSocialContextBuilderSimulatorGetSetContext();

  /**
   * The posted notifications.
   */
  protected JsonArray notifications = new JsonArray();

  /**
   * Start a mocker builder into a random port.
   *
   * @return the started mocker.
   */
  public static WeNetSocialContextBuilderSimulatorMocker start() {

    return start(0);

  }

  /**
   * Start a mocker builder into a port.
   *
   * @param port to bind the server.
   *
   * @return the started mocker.
   */
  public static WeNetSocialContextBuilderSimulatorMocker start(final int port) {

    final var mocker = new WeNetSocialContextBuilderSimulatorMocker();
    mocker.startServerAndWait(port);
    return mocker;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getComponentConfigurationName() {

    return WeNetSocialContextBuilderClient.SOCIAL_CONTEXT_BUILDER_CONF_KEY;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void fillInRouterHandler(final Router router) {

    router.get("/social/explanations/:userId/:taskId/")
        .handler(this.context.createGetHandler("SOCIAL_EXPLANATIONS", new JsonObject(), "userId", "taskId"));
    router.post("/social/explanations/:userId/:taskId/")
        .handler(this.context.createSetHandler("SOCIAL_EXPLANATIONS", "userId", "taskId"));

    router.get("/social/preferences/:userId/:taskId/").handler(this.context.createGetHandler("SOCIAL_PREFERENCES",
        new JsonObject().put("users_IDs", new JsonArray()), "userId", "taskId"));
    router.post("/social/preferences/:userId/:taskId/")
        .handler(this.context.createSetHandler("SOCIAL_PREFERENCES", "userId", "taskId"));

    router.get("/social/relations/initialize/:userId")
        .handler(this.context.createGetHandler("SOCIAL_RELATIONS_INITIALIZE", new JsonObject(), "userId"));
    router.post("/social/relations/initialize/:userId")
        .handler(this.context.createSetHandler("SOCIAL_RELATIONS_INITIALIZE", "userId"));

    router.post("/social/notification/interaction")
        .handler(this.context.createAddSocialNotificationInteractionHandler());
    router.get("/social/notification/interaction")
        .handler(this.context.createGetHandler("SOCIAL_NOTIFICATION_INTERACTION", new JsonArray()));
    router.delete("/social/notification/interaction")
        .handler(this.context.createDeleteSocialNotificationInteractionHandler());

    router.get("/social/preferences/answers/:userId/:taskId")
        .handler(this.context.createGetHandler("SOCIAL_PREFERENCES_ANSWERS", new JsonObject(), "userId", "taskId"));
    router.post("/social/preferences/answers/:userId/:taskId")
        .handler(this.context.createSetSocialPreferencesAnswersHandler());

    router.get("/social/preferences/answers/:userId/:taskId/:selection/update").handler(this.context.createGetHandler(
        "SOCIAL_PREFERENCES_ANSWERS_SELECTION_UPDATE", new JsonArray(), "userId", "taskId", "selection"));
    router.put("/social/preferences/answers/:userId/:taskId/:selection/update").handler(
        this.context.createSetHandler("SOCIAL_PREFERENCES_ANSWERS_SELECTION_UPDATE", "userId", "taskId", "selection"));

    router.post("/social/notification/profileUpdate/:userId")
        .handler(this.context.createSetHandler("SOCIAL_NOTIFICATION_PROFILE_UPDATE", "userId"));
    router.get("/social/notification/profileUpdate/:userId")
        .handler(this.context.createGetHandler("SOCIAL_NOTIFICATION_PROFILE_UPDATE", new JsonObject(), "userId"));
    router.delete("/social/notification/profileUpdate/:userId")
        .handler(this.context.createDeleteHandler("SOCIAL_NOTIFICATION_PROFILE_UPDATE", "userId"));

    router.get("/social/shuffle").handler(this.context.createGetHandler("SOCIAL_SHUFFLE", new JsonObject()));
    router.post("/social/shuffle").handler(this.context.createSetHandler("SOCIAL_SHUFFLE"));

  }

}
