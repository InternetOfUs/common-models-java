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

package eu.internetofus.common.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

/**
 * Integration test of the {@link BadRequestHandler}.
 *
 * @see BadRequestHandler
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class BadRequestHandlerTest {

	/**
	 * Verify that build handler.
	 */
	@Test
	public void shouldBuildHandler() {

		final Handler<RoutingContext> handler = BadRequestHandler.build();
		assertThat(handler).isNotNull();

	}

	/**
	 * Verify that return a bar request error.
	 */
	@Test
	public void shouldReturnBadRequestErrorMessage() {

		final BadRequestHandler handler = new BadRequestHandler();
		final RoutingContext event = mock(RoutingContext.class);
		final HttpServerResponse response = mock(HttpServerResponse.class);
		doReturn(response).when(event).response();
		handler.handle(event);
		verify(response, times(1)).setStatusCode(400);
		verify(response, times(1)).putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		verify(response, times(1)).end("{\"code\":\"bad_api_request\",\"message\":\"Bad request\"}");

	}

	/**
	 * Verify that return a bar request error.
	 */
	@Test
	public void shouldReturnBadRequestErrorMessageWithFailedMessage() {

		final BadRequestHandler handler = new BadRequestHandler();
		final RoutingContext event = mock(RoutingContext.class);
		final HttpServerResponse response = mock(HttpServerResponse.class);
		doReturn(response).when(event).response();
		doReturn(new Throwable("Error message")).when(event).failure();
		handler.handle(event);
		verify(response, times(1)).setStatusCode(400);
		verify(response, times(1)).putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		verify(response, times(1)).end("{\"code\":\"bad_api_request\",\"message\":\"Error message\"}");

	}

}
