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

package eu.internetofus.common.vertx;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.tinylog.Logger;

import eu.internetofus.common.components.ErrorMessage;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;

/**
 * Handler when do an internal server error.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class InternalServerErrorHandler implements Handler<RoutingContext> {

  /**
   * Create the handler for the not found.
   *
   * @return a new instance of the handler for the not found.
   */
  public static Handler<RoutingContext> build() {

    return new InternalServerErrorHandler();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void handle(final RoutingContext event) {

    final var response = event.response();
    response.setStatusCode(Status.INTERNAL_SERVER_ERROR.getStatusCode());
    response.putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    final Throwable failure = event.failure();
    var message = "Internal server error";
    if (failure != null) {

      message = failure.getMessage();
      Logger.error(failure, "Internal server error.");
    }
    final var error = new ErrorMessage("unexpected_error", message);
    response.end(error.toJsonString());

  }

}
