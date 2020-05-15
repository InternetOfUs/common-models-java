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

import eu.internetofus.common.components.ErrorException;
import eu.internetofus.common.components.ErrorMessage;
import eu.internetofus.common.components.Model;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationResponse;

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
	static void responseFailedWith(Handler<AsyncResult<OperationResponse>> resultHandler, Status status,
			Throwable throwable) {

		if (throwable == null) {

			responseWith(resultHandler, status, new ErrorMessage("undefined", "Unexpected failure"));

		} else {

			String code;
			if (throwable instanceof ErrorException) {

				code = ((ErrorException) throwable).getCode();

			} else {

				code = throwable.getClass().getSimpleName();

			}

			responseWith(resultHandler, status, new ErrorMessage(code, throwable.getMessage()));

		}
	}

	/**
	 * Handle with an error message.
	 *
	 * @param resultHandler handler that will manage the response.
	 * @param status        HTTP status code.
	 * @param code          of the error.
	 * @param message       of the error.
	 */
	static void responseWithErrorMessage(Handler<AsyncResult<OperationResponse>> resultHandler, Status status,
			String code, String message) {

		responseWith(resultHandler, status, new ErrorMessage(code, message));

	}

	/**
	 * Handle with a successful model.
	 *
	 * @param resultHandler handler that will manage the response.
	 * @param model         to response.
	 */
	static void responseOk(Handler<AsyncResult<OperationResponse>> resultHandler, Object model) {

		responseWith(resultHandler, Status.OK, model);

	}

	/**
	 * Handle with a successful empty content.
	 *
	 * @param resultHandler handler that will manage the response.
	 */
	static void responseOk(Handler<AsyncResult<OperationResponse>> resultHandler) {

		resultHandler.handle(Future.succeededFuture(new OperationResponse().setStatusCode(Status.NO_CONTENT.getStatusCode())
				.putHeader(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON)));

	}

	/**
	 * Handle with a response with the specified status and content.
	 *
	 * @param resultHandler handler that will manage the response.
	 * @param status        HTTP status code.
	 * @param model         to send.
	 */
	static void responseWith(Handler<AsyncResult<OperationResponse>> resultHandler, Status status, Object model) {

		Buffer buffer;
		if (model instanceof Model) {

			buffer = ((Model) model).toBuffer();

		} else if (model instanceof JsonObject) {

			buffer = ((JsonObject) model).toBuffer();

		} else {

			buffer = Buffer.buffer(String.valueOf(model));
		}
		resultHandler.handle(Future.succeededFuture(new OperationResponse().setStatusCode(status.getStatusCode())
				.putHeader(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON).setPayload(buffer)));

	}

}
