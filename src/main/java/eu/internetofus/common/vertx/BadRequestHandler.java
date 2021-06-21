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

import eu.internetofus.common.components.ErrorMessage;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

/**
 * Handler when do a bad request over the API.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class BadRequestHandler implements Handler<RoutingContext> {

  /**
   * Create the handler for the not found.
   *
   * @return a new instance of the handler for the not found.
   */
  public static Handler<RoutingContext> build() {

    return new BadRequestHandler();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void handle(final RoutingContext event) {

    final var response = event.response();
    response.setStatusCode(Status.BAD_REQUEST.getStatusCode());
    response.putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    final Throwable failure = event.failure();
    var message = "Bad request";
    if (failure != null) {

      message = failure.getMessage();
    }
    final var error = new ErrorMessage("bad_api_request", message);
    response.end(error.toJsonString());

  }

}
