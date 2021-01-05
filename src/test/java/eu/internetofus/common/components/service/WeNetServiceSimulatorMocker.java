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

package eu.internetofus.common.components.service;

import java.util.ArrayList;
import java.util.List;

import eu.internetofus.common.components.AbstractComponentMocker;
import eu.internetofus.common.components.ErrorMessage;
import eu.internetofus.common.components.Model;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import javax.ws.rs.core.Response.Status;

/**
 * The mocked server for the {@link WeNetService}.
 *
 * @see WeNetService
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetServiceSimulatorMocker extends AbstractComponentMocker {

  /**
   * {@inheritDoc}
   *
   * @see WeNetServiceClient#SERVICE_CONF_KEY
   */
  @Override
  protected String getComponentConfigurationName() {

    return WeNetServiceClient.SERVICE_CONF_KEY;
  }

  /**
   * The data stored by this component.
   */
  public static class DataElement {

    /**
     * The application associated to the data.
     */
    public App app;

    /**
     * The users associated to the application.
     */
    public JsonArray users = new JsonArray();

    /**
     * The posted callback messages to the application.
     */
    public JsonArray callbacks = new JsonArray();

  }

  /**
   * Handler for the a data element.
   */
  @FunctionalInterface
  public static interface DataElementHandler {

    /**
     * Called when has to handle an element.
     *
     * @param ctx   router context to reply.
     * @param index of the element in the data,
     */
    void handle(RoutingContext ctx, int index);
  }

  /**
   * Create a new element.
   *
   * @param app application of the element.
   *
   * @return the created element;
   */
  protected DataElement createDataElement(final App app) {

    final var element = new DataElement();
    element.app = app;
    if (element.app.appId == null) {

      var index = this.data.size() + 1;
      while (this.indexOf(String.valueOf(index)) > -1) {
        index++;
      }
      element.app.appId = String.valueOf(index);
    }
    if (element.app.messageCallbackUrl == null) {

      element.app.messageCallbackUrl = this.getApiUrl() + "/app/" + element.app.appId + "/callbacks";
    }

    return element;
  }

  /**
   * The data stored by the mocked service.
   */
  protected List<DataElement> data;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> startServer(final int port) {

    return super.startServer(port).compose(started -> {

      WeNetServiceSimulatorMocker.this.data = new ArrayList<>();
      return Future.succeededFuture();
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> stopServer() {

    return super.stopServer().compose(stopped -> {

      WeNetServiceSimulatorMocker.this.data = null;
      return Future.succeededFuture();
    });
  }

  /**
   * Start a mocker builder into a random port.
   *
   * @return the started mocker.
   */
  public static WeNetServiceSimulatorMocker start() {

    return start(0);

  }

  /**
   * Start a mocker builder into a port.
   *
   * @param port to bind the server.
   *
   * @return the started mocker.
   */
  public static WeNetServiceSimulatorMocker start(final int port) {

    final var mocker = new WeNetServiceSimulatorMocker();
    mocker.startServerAndWait(port);
    return mocker;
  }

  /**
   * Return the position of the data associated into an identifier.
   *
   * @param id identifier of the element to get.
   *
   * @return the index of the element in the data or {@code -1} if it is not
   *         defined.
   */
  protected int indexOf(final String id) {

    final var max = this.data.size();
    for (var i = 0; i < max; i++) {

      final var element = this.data.get(i);
      if (id.equals(element.app.appId)) {

        return i;
      }
    }
    return -1;

  }

  /**
   * Return the position of the data associated into the identifier of the path.
   *
   * @param ctx to get the identifier from the path.
   *
   * @return the index of the element in the data or {@code -1} if it is not
   *         defined.
   */
  protected int indexOf(final RoutingContext ctx) {

    final var id = ctx.pathParam("id");
    if (id != null) {

      return this.indexOf(id);

    } else {

      return -1;

    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void fillInRouterHandler(final Router router) {

    router.post("/app").handler(this.createPostAppHandler());
    router.get("/app/:id").handler(this.toRoutingContextHandler(this.createGetAppHandler()));
    router.delete("/app/:id").handler(this.toRoutingContextHandler(this.createDeleteAppHandler()));

    router.post("/app/:id/callbacks").handler(this.toRoutingContextHandler(this.createPostCallbacksHandler()));
    router.get("/app/:id/callbacks").handler(this.toRoutingContextHandler(this.createGetCallbacksHandler()));
    router.delete("/app/:id/callbacks").handler(this.toRoutingContextHandler(this.createDeleteCallbacksHandler()));

    router.post("/app/:id/users").handler(this.toRoutingContextHandler(this.createPostUsersHandler()));
    router.get("/app/:id/users").handler(this.toRoutingContextHandler(this.createGetUsersHandler()));
    router.delete("/app/:id/users").handler(this.toRoutingContextHandler(this.createDeleteUsersHandler()));

  }

  /**
   * Handler for a {@link DataElement}.
   *
   * @param handler to convert.
   *
   * @return the handler to manage an element.
   */
  protected Handler<RoutingContext> toRoutingContextHandler(final DataElementHandler handler) {

    return ctx -> {

      final var id = ctx.pathParam("id");
      final var index = this.indexOf(id);
      if (index < 0) {

        final var response = ctx.response();
        response.setStatusCode(Status.NOT_FOUND.getStatusCode());
        response.end(new ErrorMessage("undefined_app", "The application does not exist").toBuffer());

      } else {

        handler.handle(ctx, index);
      }

    };
  }

  /**
   * Create the handler to obtain the callback handlers.
   *
   * @return the handler to retrieve the callbacks.
   */
  protected DataElementHandler createGetCallbacksHandler() {

    return (ctx, index) -> {

      final var response = ctx.response();
      response.setStatusCode(Status.OK.getStatusCode());
      response.end(this.data.get(index).callbacks.toBuffer());

    };

  }

  /**
   * Create the handler to add a callback message.
   *
   * @return the handler to post a callback message.
   */
  protected DataElementHandler createPostCallbacksHandler() {

    return (ctx, index) -> {

      final var message = ctx.getBodyAsJson();
      if (message == null) {

        final var response = ctx.response();
        response.setStatusCode(Status.BAD_REQUEST.getStatusCode());
        response.end(new ErrorMessage("unexpected_message", "The post message is not a JsonObject").toBuffer());

      } else {

        this.data.get(index).callbacks.add(message);
        final var response = ctx.response();
        response.setStatusCode(Status.OK.getStatusCode());
        response.end(message.toBuffer());

      }

    };
  }

  /**
   * Create the handler to delete all the callbacks.
   *
   * @return the handler to delete all the callbacks.
   */
  protected DataElementHandler createDeleteCallbacksHandler() {

    return (ctx, index) -> {

      final var app = this.data.get(index);
      app.callbacks = new JsonArray();
      final var response = ctx.response();
      response.setStatusCode(Status.NO_CONTENT.getStatusCode());
      response.end();

    };
  }

  /**
   * Create the handler to obtain the user handlers.
   *
   * @return the handler to retrieve the users.
   */
  protected DataElementHandler createGetUsersHandler() {

    return (ctx, index) -> {

      final var response = ctx.response();
      response.setStatusCode(Status.OK.getStatusCode());
      response.end(this.data.get(index).users.toBuffer());

    };

  }

  /**
   * Create the handler to add a user message.
   *
   * @return the handler to post a user message.
   */
  protected DataElementHandler createPostUsersHandler() {

    return (ctx, index) -> {

      final var users = ctx.getBodyAsJsonArray();
      if (users == null) {

        final var response = ctx.response();
        response.setStatusCode(Status.BAD_REQUEST.getStatusCode());
        response.end(new ErrorMessage("unexpected_users", "The post message is not a JsonArray").toBuffer());

      } else {

        this.data.get(index).users = users;
        final var response = ctx.response();
        response.setStatusCode(Status.OK.getStatusCode());
        response.end(users.toBuffer());

      }

    };
  }

  /**
   * Create the handler to delete all the users.
   *
   * @return the handler to delete all the users.
   */
  protected DataElementHandler createDeleteUsersHandler() {

    return (ctx, index) -> {

      final var app = this.data.get(index);
      app.users = new JsonArray();
      final var response = ctx.response();
      response.setStatusCode(Status.NO_CONTENT.getStatusCode());
      response.end();

    };
  }

  /**
   * Create the handler to remove an application.
   *
   * @return the handler to delete an application.
   */
  protected DataElementHandler createDeleteAppHandler() {

    return (ctx, index) -> {

      this.data.remove(index);
      final var response = ctx.response();
      response.setStatusCode(Status.NO_CONTENT.getStatusCode());
      response.end();

    };

  }

  /**
   * Create the handler to retrieve an application.
   *
   * @return the handler to get an application.
   */
  protected DataElementHandler createGetAppHandler() {

    return (ctx, index) -> {

      final var response = ctx.response();
      response.setStatusCode(Status.OK.getStatusCode());
      response.end(this.data.get(index).app.toBuffer());

    };

  }

  /**
   * Create the handler to add a new application.
   *
   * @return the handler to post a new application.
   */
  protected Handler<RoutingContext> createPostAppHandler() {

    return ctx -> {

      final App app = Model.fromBuffer(ctx.getBody(), App.class);
      if (app == null) {

        final var response = ctx.response();
        response.setStatusCode(Status.BAD_REQUEST.getStatusCode());
        response.end(new ErrorMessage("bad_app", "The content is not an application").toBuffer());

      } else if (app.appId == null || this.indexOf(app.appId) < 0) {

        this.data.add(this.createDataElement(app));
        final var response = ctx.response();
        response.setStatusCode(Status.CREATED.getStatusCode());
        response.end(app.toBuffer());

      } else {

        final var response = ctx.response();
        response.setStatusCode(Status.BAD_REQUEST.getStatusCode());
        response.end(new ErrorMessage("dulicated_id", "The application already exist").toBuffer());
      }

    };

  }

}
