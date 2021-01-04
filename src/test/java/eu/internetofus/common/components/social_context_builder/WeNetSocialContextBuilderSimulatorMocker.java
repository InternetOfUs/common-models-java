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
