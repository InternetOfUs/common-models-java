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
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

/**
 * Handler when not found an API.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class NotFoundHandler implements Handler<RoutingContext> {

	/**
	 * Create the handler for the not found.
	 *
	 * @return a new instance of the handler for the not found.
	 */
	public static Handler<RoutingContext> build() {

		return new NotFoundHandler();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handle(RoutingContext event) {

		final HttpServerResponse response = event.response();
		response.setStatusCode(Status.NOT_FOUND.getStatusCode());
		response.putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		final String path = event.normalisedPath();
		final ErrorMessage error = new ErrorMessage("not_found_api_request_path",
				"The '" + path + "' is not defined on the API.");
		final String body = error.toJsonString();
		response.end(body);

	}

}
