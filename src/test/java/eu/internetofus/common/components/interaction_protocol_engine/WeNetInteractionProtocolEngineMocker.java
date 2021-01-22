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

import eu.internetofus.common.components.AbstractComponentMocker;
import io.vertx.core.Handler;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import javax.ws.rs.core.Response.Status;

/**
 * The mocked server for the {@link WeNetInteractionProtocolEngine}.
 *
 * @see WeNetInteractionProtocolEngine
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetInteractionProtocolEngineMocker extends AbstractComponentMocker {

  /**
   * Start a mocker builder into a random port.
   *
   * @return the started mocker.
   */
  public static WeNetInteractionProtocolEngineMocker start() {

    return start(0);

  }

  /**
   * Start a mocker builder into a port.
   *
   * @param port to bind the server.
   *
   * @return the started mocker.
   */
  public static WeNetInteractionProtocolEngineMocker start(final int port) {

    final var mocker = new WeNetInteractionProtocolEngineMocker();
    mocker.startServerAndWait(port);
    return mocker;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getComponentConfigurationName() {

    return WeNetInteractionProtocolEngineClient.INTERACTION_PROTOCOL_ENGINE_CONF_KEY;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void fillInRouterHandler(final Router router) {

    router.post("/messages").handler(this.createEchoHandler());
    router.post("/incentives").handler(this.createEchoHandler());
    router.post("/tasks/created").handler(this.createEchoHandler());
    router.post("/tasks/transactions").handler(this.createEchoHandler());

    router.get("/states/communities/:communityId/users/:userId").handler(this.createEmptyStateHandler());
    router.patch("/states/communities/:communityId/users/:userId").handler(this.createEchoHandler());
  }

  /**
   * Handler that do echos.
   *
   * @return the echo handler.
   */
  private Handler<RoutingContext> createEchoHandler() {

    return ctx -> {

      final var value = ctx.getBodyAsJson();
      final var response = ctx.response();
      response.setStatusCode(Status.CREATED.getStatusCode());
      response.end(value.toBuffer());

    };
  }

  /**
   * Handler that return an empty state.
   *
   * @return the empty state handler.
   */
  private Handler<RoutingContext> createEmptyStateHandler() {

    return ctx -> {

      var state = new State();
      state.communityId = ctx.pathParam("communityId");
      state.taskId = ctx.pathParam("taskId");
      state.userId = ctx.pathParam("userId");
      final var response = ctx.response();
      response.setStatusCode(Status.OK.getStatusCode());
      response.end(state.toBufferWithEmptyValues());

    };
  }

}
