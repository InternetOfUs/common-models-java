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

package eu.internetofus.common.vertx;

import eu.internetofus.common.model.ErrorMessage;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.validation.BodyProcessorException;
import io.vertx.ext.web.validation.ParameterProcessorException;
import io.vertx.json.schema.ValidationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import org.tinylog.Logger;

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
    Throwable failure = event.failure();
    final var code = new StringBuilder();
    code.append("bad_");
    if (failure instanceof BodyProcessorException) {

      code.append("body");
      failure = failure.getCause();
      if (failure instanceof ValidationException) {

        final var validationError = (ValidationException) failure;
        code.append(validationError.schema().getScope().toString().replace("/properties/", "_"));
      }

    } else if (failure instanceof ParameterProcessorException) {

      code.append("parameter_");
      code.append(((ParameterProcessorException) failure).getParameterName());

    } else {

      code.append("request");
    }

    String message;
    if (failure != null) {

      message = failure.getMessage();

    } else {

      message = "Bad request";
    }
    final var error = new ErrorMessage(code.toString(), message);
    Logger.debug("Bad request {} {} with {} because {}", () -> event.request().method(), () -> event.mountPoint(),
        () -> event.body().asString(), () -> error.toJsonString());
    response.end(error.toJsonString());

  }

}
