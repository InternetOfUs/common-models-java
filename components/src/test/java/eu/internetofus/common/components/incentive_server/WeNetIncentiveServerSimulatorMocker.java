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
   * The posted transaction status.
   */
  protected JsonArray transactionStatus = new JsonArray();

  /**
   * The posted type status.
   */
  protected JsonArray typeStatus = new JsonArray();

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

    router.post("/Tasks/TaskTransactionStatus").handler(ctx -> {

      final var value = ctx.body().asJsonObject();
      final var state = Model.fromJsonObject(value, TaskTransactionStatusBody.class);
      if (state == null) {

        final var response = ctx.response();
        response.setStatusCode(Status.BAD_REQUEST.getStatusCode());
        response.end(new ErrorMessage("bad_status", "The body is not a valid task transaction status").toBuffer());

      } else {

        this.transactionStatus.add(value);
        final var response = ctx.response();
        response.setStatusCode(Status.CREATED.getStatusCode());
        response.end(value.toBuffer());
      }

    });
    router.get("/Tasks/TaskTransactionStatus").handler(ctx -> {

      final var response = ctx.response();
      response.setStatusCode(Status.OK.getStatusCode());
      response.end(this.transactionStatus.toBuffer());

    });
    router.delete("/Tasks/TaskTransactionStatus").handler(ctx -> {

      this.transactionStatus.clear();
      final var response = ctx.response();
      response.setStatusCode(Status.NO_CONTENT.getStatusCode());
      response.end();
    });

    router.post("/Tasks/TaskTypeStatus").handler(ctx -> {

      final var value = ctx.body().asJsonObject();
      final var state = Model.fromJsonObject(value, TaskTypeStatusBody.class);
      if (state == null) {

        final var response = ctx.response();
        response.setStatusCode(Status.BAD_REQUEST.getStatusCode());
        response.end(new ErrorMessage("bad_status", "The body is not a valid task type status").toBuffer());

      } else {

        this.typeStatus.add(value);
        final var response = ctx.response();
        response.setStatusCode(Status.CREATED.getStatusCode());
        response.end(value.toBuffer());
      }

    });
    router.get("/Tasks/TaskTypeStatus").handler(ctx -> {

      final var response = ctx.response();
      response.setStatusCode(Status.OK.getStatusCode());
      response.end(this.typeStatus.toBuffer());

    });
    router.delete("/Tasks/TaskTypeStatus").handler(ctx -> {

      this.typeStatus.clear();
      final var response = ctx.response();
      response.setStatusCode(Status.NO_CONTENT.getStatusCode());
      response.end();
    });

  }

}
