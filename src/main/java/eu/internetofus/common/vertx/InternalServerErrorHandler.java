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
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
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

package eu.internetofus.common.vertx;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response.Status;

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
