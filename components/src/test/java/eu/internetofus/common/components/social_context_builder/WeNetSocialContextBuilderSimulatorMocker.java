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

import eu.internetofus.common.components.AbstractComponentMocker;
import eu.internetofus.common.components.GetSetContext;
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
  protected GetSetContext context = new GetSetContext();

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

    router.get("/social/relations/:userId/")
        .handler(this.context.createGetHandler("SOCIAL_RELATION", new JsonArray(), "userId"));
    router.post("/social/relations/:userId/").handler(this.context.createSetHandler("SOCIAL_RELATION", "userId"));

    router.get("/social/preferences/:userId/:taskId/")
        .handler(this.context.createGetHandler("SOCIAL_PREFERENCES", new JsonArray(), "userId", "taskId"));
    router.post("/social/preferences/:userId/:taskId/")
        .handler(this.context.createSetHandler("SOCIAL_PREFERENCES", "userId", "taskId"));

    router.get("/social/explanations/:userId/:taskId/")
        .handler(this.context.createGetHandler("SOCIAL_EXPLANATIONS", new JsonObject(), "userId", "taskId"));
    router.post("/social/explanations/:userId/:taskId/")
        .handler(this.context.createSetHandler("SOCIAL_EXPLANATIONS", "userId", "taskId"));

  }

}
