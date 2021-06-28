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

import eu.internetofus.common.components.AbstractComponentMocker;
import eu.internetofus.common.model.ErrorMessage;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.TimeManager;
import io.vertx.core.Handler;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
   * The stored states.
   */
  protected Map<String, State> states = new HashMap<>();

  /**
   * The stored events.
   */
  protected List<ProtocolEvent> events = new ArrayList<>();

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

    router.get("/states/communities/:communityId/users/:userId").handler(this.createGetStateHandler());
    router.patch("/states/communities/:communityId/users/:userId").handler(this.createPostStateHandler());

    router.post("/events").handler(this.createPostEventsHandler());
    router.delete("/events/:id").handler(this.createDeleteEventsHandler());

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
   * Create an empty state with the parameters values of the context.
   *
   * @param ctx routing context.
   *
   * @return the empty state defined on the request.
   */
  private State createEmptyState(final RoutingContext ctx) {

    final var state = new State();
    state.communityId = ctx.pathParam("communityId");
    state.taskId = ctx.pathParam("taskId");
    state.userId = ctx.pathParam("userId");
    return state;

  }

  /**
   * Create the identifier associated to the state.
   *
   * @param state to create the identifier.
   *
   * @return the identifier associated to the state.
   */
  private String createId(final State state) {

    return state.communityId + "#" + state.taskId + "#" + state.userId;

  }

  /**
   * Handler that get a state.
   *
   * @return the get a state handler.
   */
  private Handler<RoutingContext> createGetStateHandler() {

    return ctx -> {

      var returnState = this.createEmptyState(ctx);
      final var id = this.createId(returnState);
      final var state = this.states.get(id);
      if (state != null) {

        returnState = state;
      }
      final var response = ctx.response();
      response.setStatusCode(Status.OK.getStatusCode());
      response.end(returnState.toBufferWithEmptyValues());

    };
  }

  /**
   * Handler that post a state.
   *
   * @return the post a state handler.
   */
  private Handler<RoutingContext> createPostStateHandler() {

    return ctx -> {

      var currentState = this.createEmptyState(ctx);
      final var id = this.createId(currentState);
      final var state = this.states.get(id);
      if (state != null) {

        currentState = state;
      }
      currentState._lastUpdateTs = TimeManager.now();

      final var newState = Model.fromJsonObject(ctx.getBodyAsJson(), State.class);
      if (newState.attributes != null) {

        if (currentState.attributes == null) {

          currentState.attributes = newState.attributes;

        } else {

          for (final var field : newState.attributes.fieldNames()) {

            final var value = newState.attributes.getValue(field);
            currentState.attributes.put(field, value);

          }
        }
      }

      this.states.put(id, currentState);

      final var response = ctx.response();
      response.setStatusCode(Status.OK.getStatusCode());
      response.end(currentState.toBufferWithEmptyValues());

    };
  }

  /**
   * Handler that delete an event.
   *
   * @return the delete event handler.
   */
  private Handler<RoutingContext> createDeleteEventsHandler() {

    return ctx -> {

      try {

        final var id = Long.parseLong(ctx.pathParam("id"));
        final var max = this.events.size();
        for (var i = 0; i < max; i++) {

          final var event = this.events.get(i);
          if (event.id == id) {

            this.events.remove(i);
            final var response = ctx.response();
            response.setStatusCode(Status.NO_CONTENT.getStatusCode());
            response.end();
            return;
          }
        }

      } catch (final Throwable ignored) {

      }

      final var response = ctx.response();
      response.setStatusCode(Status.NOT_FOUND.getStatusCode());
      response.end(new ErrorMessage("undefined_event", "The identifier is not associated to any event.").toBuffer());

    };
  }

  /**
   * Handler that post an event.
   *
   * @return the post event handler.
   */
  private Handler<RoutingContext> createPostEventsHandler() {

    return ctx -> {

      final var newEvent = Model.fromJsonObject(ctx.getBodyAsJson(), ProtocolEvent.class);
      if (newEvent == null) {

        final var response = ctx.response();
        response.setStatusCode(Status.BAD_REQUEST.getStatusCode());
        response.end(new ErrorMessage("bad_event", "The post model is not an event.").toBuffer());

      } else {
        newEvent.id = (long) this.events.size();
        var found = true;
        while (found) {

          found = false;
          for (final var event : this.events) {

            if (event.id == newEvent.id) {

              newEvent.id++;
              found = true;
              break;
            }
          }

        }
        this.events.add(newEvent);

        final var response = ctx.response();
        response.setStatusCode(Status.OK.getStatusCode());
        response.end(newEvent.toBufferWithEmptyValues());
      }

    };

  }

}