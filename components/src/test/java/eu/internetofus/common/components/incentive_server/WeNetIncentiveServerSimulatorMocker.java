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

package eu.internetofus.common.components.incentive_server;

import eu.internetofus.common.components.AbstractComponentMocker;
import eu.internetofus.common.model.ErrorMessage;
import eu.internetofus.common.model.Model;
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