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
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ValidationErrorException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceResponse;
import io.vertx.serviceproxy.ServiceException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import org.tinylog.Logger;

/**
 * Classes used to generate handlers for an {@link ServiceResponse}.
 *
 * @see ServiceResponse
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface ServiceResponseHandlers {

  /**
   * Handle with an error message.
   *
   * @param resultHandler handler that will manage the response.
   * @param status        HTTP status code.
   * @param throwable     cause that explains why has failed the request.
   */
  static void responseFailedWith(final Handler<AsyncResult<ServiceResponse>> resultHandler, final Status status,
      final Throwable throwable) {

    var code = "undefined";
    var message = "Unexpected failure";
    if (throwable instanceof ServiceException) {

      final var exception = (ServiceException) throwable;
      code = String.valueOf(exception.failureCode());
      message = exception.getMessage();
      final var errorMessage = Model.fromJsonObject(exception.getDebugInfo(), ErrorMessage.class);
      if (errorMessage != null) {

        if (errorMessage.code != null) {

          code = errorMessage.code;

        }
        if (errorMessage.message != null) {

          message = errorMessage.message;

        }

      }

    } else if (throwable instanceof ValidationErrorException) {

      final var validation = (ValidationErrorException) throwable;
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
  static void responseWithErrorMessage(final Handler<AsyncResult<ServiceResponse>> resultHandler, final Status status,
      final String code, final String message) {

    responseWith(resultHandler, status, new ErrorMessage(code, message));

  }

  /**
   * Handle with a successful model.
   *
   * @param resultHandler handler that will manage the response.
   * @param model         to response.
   */
  static void responseOk(final Handler<AsyncResult<ServiceResponse>> resultHandler, final Object model) {

    responseWith(resultHandler, Status.OK, model);

  }

  /**
   * Handle with a successful empty content.
   *
   * @param resultHandler handler that will manage the response.
   */
  static void responseOk(final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    resultHandler.handle(Future.succeededFuture(new ServiceResponse().setStatusCode(Status.NO_CONTENT.getStatusCode())
        .putHeader(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON)));

  }

  /**
   * Handle with a response with the specified status and content.
   *
   * @param resultHandler handler that will manage the response.
   * @param status        HTTP status code.
   * @param model         to send.
   */
  static void responseWith(final Handler<AsyncResult<ServiceResponse>> resultHandler, final Status status,
      final Object model) {

    Buffer buffer;
    if (model instanceof Model) {

      buffer = ((Model) model).toBufferWithEmptyValues();

    } else if (model instanceof JsonObject) {

      buffer = ((JsonObject) model).toBuffer();

    } else {

      buffer = Buffer.buffer(String.valueOf(model));
    }

    Logger.trace("Response {} with {}", status, model);
    resultHandler.handle(Future.succeededFuture(new ServiceResponse().setStatusCode(status.getStatusCode())
        .putHeader(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON).setPayload(buffer)));

  }

}
