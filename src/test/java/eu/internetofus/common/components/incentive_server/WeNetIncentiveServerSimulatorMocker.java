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

package eu.internetofus.common.components.incentive_server;

import eu.internetofus.common.components.AbstractComponentMocker;
import eu.internetofus.common.components.ErrorMessage;
import eu.internetofus.common.components.Model;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import javax.ws.rs.core.Response.Status;

/**
 * The mocked server for the {@link WeNetIncentiveServer}.
 *
 * @see WeNetIncentiveServer
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetIncentiveServerSimulatorMocker extends AbstractComponentMocker {

  /**
   * The posted status.
   */
  protected JsonArray status = new JsonArray();

  /**
   * Start a mocker builder into a random port.
   *
   * @return the started mocker.
   */
  public static WeNetIncentiveServerSimulatorMocker start() {

    return start(0);

  }

  /**
   * Start a mocker builder into a port.
   *
   * @param port to bind the server.
   *
   * @return the started mocker.
   */
  public static WeNetIncentiveServerSimulatorMocker start(final int port) {

    final var mocker = new WeNetIncentiveServerSimulatorMocker();
    mocker.startServerAndWait(port);
    return mocker;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getComponentConfigurationName() {

    return WeNetIncentiveServerClient.INCENTIVE_SERVER_CONF_KEY;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void fillInRouterHandler(final Router router) {

    router.post("/Tasks/TaskStatus").handler(ctx -> {

      final var value = ctx.getBodyAsJson();
      final var state = Model.fromJsonObject(value, TaskStatus.class);
      if (state == null) {

        final var response = ctx.response();
        response.setStatusCode(Status.BAD_REQUEST.getStatusCode());
        response.end(new ErrorMessage("bad_status", "The body is not a valid task status").toBuffer());

      } else {

        this.status.add(value);
        final var response = ctx.response();
        response.setStatusCode(Status.CREATED.getStatusCode());
        response.end(value.toBuffer());
      }

    });
    router.get("/Tasks/TaskStatus").handler(ctx -> {

      final var response = ctx.response();
      response.setStatusCode(Status.OK.getStatusCode());
      response.end(this.status.toBuffer());

    });
    router.delete("/Tasks/TaskStatus").handler(ctx -> {

      this.status.clear();
      final var response = ctx.response();
      response.setStatusCode(Status.NO_CONTENT.getStatusCode());
      response.end();
    });

  }

}
