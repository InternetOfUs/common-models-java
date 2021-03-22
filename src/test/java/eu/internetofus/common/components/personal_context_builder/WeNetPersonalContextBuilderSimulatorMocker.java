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

package eu.internetofus.common.components.personal_context_builder;

import eu.internetofus.common.components.AbstractComponentMocker;
import eu.internetofus.common.components.ErrorMessage;
import eu.internetofus.common.components.Model;
import io.vertx.core.Handler;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Response.Status;

/**
 * The mocked server for the {@link WeNetPersonalContextBuilder}.
 *
 * @see WeNetPersonalContextBuilder
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetPersonalContextBuilderSimulatorMocker extends AbstractComponentMocker {

  /**
   * The locations that has been stored.
   */
  protected List<UserLocation> locations = new ArrayList<>();

  /**
   * Start a mocker builder into a random port.
   *
   * @return the started mocker.
   */
  public static WeNetPersonalContextBuilderSimulatorMocker start() {

    return start(0);

  }

  /**
   * Start a mocker builder into a port.
   *
   * @param port to bind the server.
   *
   * @return the started mocker.
   */
  public static WeNetPersonalContextBuilderSimulatorMocker start(final int port) {

    final var mocker = new WeNetPersonalContextBuilderSimulatorMocker();
    mocker.startServerAndWait(port);
    return mocker;
  }

  /**
   * {@inheritDoc}
   *
   * @see WeNetPersonalContextBuilderClient#PERSONAL_CONTEXT_BUILDER_CONF_KEY
   */
  @Override
  protected String getComponentConfigurationName() {

    return WeNetPersonalContextBuilderClient.PERSONAL_CONTEXT_BUILDER_CONF_KEY;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void fillInRouterHandler(final Router router) {

    router.post("/locations").handler(this.createPostLocationsHandler());
    router.put("/locations/:id").handler(this.createPutLocationHandler());
    router.delete("/locations/:id").handler(this.createDeleteLocationHandler());

  }

  /**
   * Handler for post the locations of a set of users.
   *
   * @return the handler to manage an users locations.
   */
  protected Handler<RoutingContext> createPostLocationsHandler() {

    return ctx -> {

      final var body = Model.fromJsonObject(ctx.getBodyAsJson(), Users.class);
      if (body == null) {

        final var response = ctx.response();
        response.setStatusCode(Status.BAD_REQUEST.getStatusCode());
        response.end(new ErrorMessage("unexpected_users", "The post does not contains the users").toBuffer());

      } else {

        final var usersLocation = new UsersLocations();
        for (final var userId : body.users) {

          for (final var location : this.locations) {

            if (location.userId.equals(userId)) {

              usersLocation.locations.add(location);
              break;
            }
          }

        }

        final var response = ctx.response();
        response.setStatusCode(Status.OK.getStatusCode());
        response.end(usersLocation.toBuffer());

      }

    };
  }

  /**
   * Handler for post a new loction for an user.
   *
   * @return the handler to manage an users locations.
   */
  protected Handler<RoutingContext> createPutLocationHandler() {

    return ctx -> {

      final var id = ctx.pathParam("id");
      final var body = Model.fromJsonObject(ctx.getBodyAsJson(), UserLocation.class);
      if (body == null || id == null) {

        final var response = ctx.response();
        response.setStatusCode(Status.BAD_REQUEST.getStatusCode());
        response.end(new ErrorMessage("unexpected_location", "The put does not contains an user location").toBuffer());

      } else {

        body.userId = id;
        final var max = this.locations.size();
        for (var i = 0; i < max; i++) {

          final var location = this.locations.get(i);
          if (location.userId.equals(id)) {

            this.locations.remove(i);
            this.locations.add(body);
            break;
          }
        }

        final var response = ctx.response();
        response.setStatusCode(Status.OK.getStatusCode());
        response.end(body.toBuffer());
      }

    };
  }

  /**
   * Handler for post a new location for an user.
   *
   * @return the handler to manage an users locations.
   */
  protected Handler<RoutingContext> createDeleteLocationHandler() {

    return ctx -> {

      final var id = ctx.pathParam("id");
      final var max = this.locations.size();
      for (var i = 0; i < max; i++) {

        final var location = this.locations.get(i);
        if (location.userId.equals(id)) {

          this.locations.remove(i);
          break;
        }
      }

      final var response = ctx.response();
      response.setStatusCode(Status.NO_CONTENT.getStatusCode());
      response.end();

    };
  }

}
