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

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import eu.internetofus.common.components.ErrorMessage;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ValidationErrorException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationResponse;
import io.vertx.serviceproxy.ServiceException;

/**
 * Classes used to generate handlers for an {@link OperationResponse}.
 *
 * @see OperationResponse
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface OperationReponseHandlers {

  /**
   * Handle with an error message.
   *
   * @param resultHandler handler that will manage the response.
   * @param status        HTTP status code.
   * @param throwable     cause that explains why has failed the request.
   */
  static void responseFailedWith(final Handler<AsyncResult<OperationResponse>> resultHandler, final Status status, final Throwable throwable) {

    String code = "undefined";
    String message = "Unexpected failure";
    if (throwable instanceof ServiceException) {

      final ServiceException exception = (ServiceException) throwable;
      code = String.valueOf(exception.failureCode());
      message = exception.getMessage();
      final ErrorMessage errorMessage = Model.fromJsonObject(exception.getDebugInfo(), ErrorMessage.class);
      if (errorMessage != null) {

        if (errorMessage.code != null) {

          code = errorMessage.code;

        }
        if (errorMessage.message != null) {

          message = errorMessage.message;

        }

      }

    } else if (throwable instanceof ValidationErrorException) {

      final ValidationErrorException validation = (ValidationErrorException) throwable;
      code = validation.getCode();
      message = validation.getMessage();

    } else if (throwable != null) {

      code = throwable.getClass().getSimpleName();
      message = throwable.getMessage();
    }

    responseWithErrorMessage(resultHandler, status, code, message);
  }

  /**
   * Handle with an error message.
   *
   * @param resultHandler handler that will manage the response.
   * @param status        HTTP status code.
   * @param code          of the error.
   * @param message       of the error.
   */
  static void responseWithErrorMessage(final Handler<AsyncResult<OperationResponse>> resultHandler, final Status status, final String code, final String message) {

    responseWith(resultHandler, status, new ErrorMessage(code, message));

  }

  /**
   * Handle with a successful model.
   *
   * @param resultHandler handler that will manage the response.
   * @param model         to response.
   */
  static void responseOk(final Handler<AsyncResult<OperationResponse>> resultHandler, final Object model) {

    responseWith(resultHandler, Status.OK, model);

  }

  /**
   * Handle with a successful empty content.
   *
   * @param resultHandler handler that will manage the response.
   */
  static void responseOk(final Handler<AsyncResult<OperationResponse>> resultHandler) {

    resultHandler.handle(Future.succeededFuture(new OperationResponse().setStatusCode(Status.NO_CONTENT.getStatusCode()).putHeader(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON)));

  }

  /**
   * Handle with a response with the specified status and content.
   *
   * @param resultHandler handler that will manage the response.
   * @param status        HTTP status code.
   * @param model         to send.
   */
  static void responseWith(final Handler<AsyncResult<OperationResponse>> resultHandler, final Status status, final Object model) {

    Buffer buffer;
    if (model instanceof Model) {

      buffer = ((Model) model).toBuffer();

    } else if (model instanceof JsonObject) {

      buffer = ((JsonObject) model).toBuffer();

    } else {

      buffer = Buffer.buffer(String.valueOf(model));
    }
    resultHandler.handle(Future.succeededFuture(new OperationResponse().setStatusCode(status.getStatusCode()).putHeader(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON).setPayload(buffer)));

  }

}
