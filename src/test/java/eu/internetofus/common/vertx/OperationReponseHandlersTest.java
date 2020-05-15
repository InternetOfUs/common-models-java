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

import static org.assertj.core.api.Assertions.assertThat;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ValidationErrorException;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link OperationReponseHandlers}.
 *
 * @see OperationReponseHandlers
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class OperationReponseHandlersTest {

	/**
	 * Check the response with a {@link Model}.
	 *
	 * @param testContext context that executes the test.
	 */
	@Test
	public void shouldRepondWithModel(VertxTestContext testContext) {

		OperationReponseHandlers.responseWith(testContext.succeeding(reponse -> testContext.verify(() -> {

			assertThat(reponse.getStatusCode()).isEqualTo(Status.ACCEPTED.getStatusCode());
			assertThat(reponse.getHeaders().get(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(reponse.getPayload().toString()).isEqualTo("{}");
			testContext.completeNow();

		})), Status.ACCEPTED, new Model());

	}

	/**
	 * Check the response with a JSON object.
	 *
	 * @param testContext context that executes the test.
	 */
	@Test
	public void shouldRepondWithStringJsonObject(VertxTestContext testContext) {

		OperationReponseHandlers.responseWith(testContext.succeeding(reponse -> testContext.verify(() -> {

			assertThat(reponse.getStatusCode()).isEqualTo(Status.FOUND.getStatusCode());
			assertThat(reponse.getHeaders().get(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(reponse.getPayload().toString()).isEqualTo("{\"key\":\"value\"}");
			testContext.completeNow();

		})), Status.FOUND, new JsonObject().put("key", "value"));

	}

	/**
	 * Check the response with a string value.
	 *
	 * @param testContext context that executes the test.
	 */
	@Test
	public void shouldRepondWithString(VertxTestContext testContext) {

		OperationReponseHandlers.responseWith(testContext.succeeding(reponse -> testContext.verify(() -> {

			assertThat(reponse.getStatusCode()).isEqualTo(Status.CONFLICT.getStatusCode());
			assertThat(reponse.getHeaders().get(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(reponse.getPayload().toString()).isEqualTo("{\"name\":\"value\"}");
			testContext.completeNow();

		})), Status.CONFLICT, "{\"name\":\"value\"}");

	}

	/**
	 * Check the response Ok with content.
	 *
	 * @param testContext context that executes the test.
	 */
	@Test
	public void shouldRepondWithOk(VertxTestContext testContext) {

		OperationReponseHandlers.responseOk(testContext.succeeding(reponse -> testContext.verify(() -> {

			assertThat(reponse.getStatusCode()).isEqualTo(Status.OK.getStatusCode());
			assertThat(reponse.getHeaders().get(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(reponse.getPayload().toString()).isEqualTo("{\"name\":\"value\"}");
			testContext.completeNow();

		})), "{\"name\":\"value\"}");

	}

	/**
	 * Check the response with an error message.
	 *
	 * @param testContext context that executes the test.
	 */
	@Test
	public void shouldResponseWithErrorMessage(VertxTestContext testContext) {

		OperationReponseHandlers.responseWithErrorMessage(testContext.succeeding(reponse -> testContext.verify(() -> {

			assertThat(reponse.getStatusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			assertThat(reponse.getHeaders().get(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(reponse.getPayload().toString()).isEqualTo("{\"code\":\"code\",\"message\":\"message\"}");
			testContext.completeNow();

		})), Status.NOT_FOUND, "code", "message");

	}

	/**
	 * Check the response with an error message without an exception.
	 *
	 * @param testContext context that executes the test.
	 */
	@Test
	public void shouldResponseFailedWithNullException(VertxTestContext testContext) {

		OperationReponseHandlers.responseFailedWith(testContext.succeeding(reponse -> testContext.verify(() -> {

			assertThat(reponse.getStatusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
			assertThat(reponse.getHeaders().get(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(reponse.getPayload().toString())
					.isEqualTo("{\"code\":\"undefined\",\"message\":\"Unexpected failure\"}");
			testContext.completeNow();

		})), Status.NOT_FOUND, null);

	}

	/**
	 * Check the response with an error message extracted form an exception.
	 *
	 * @param testContext context that executes the test.
	 */
	@Test
	public void shouldResponseFailedWithException(VertxTestContext testContext) {

		OperationReponseHandlers.responseFailedWith(testContext.succeeding(reponse -> testContext.verify(() -> {

			assertThat(reponse.getStatusCode()).isEqualTo(Status.NOT_ACCEPTABLE.getStatusCode());
			assertThat(reponse.getHeaders().get(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(reponse.getPayload().toString())
					.isEqualTo("{\"code\":\"NullPointerException\",\"message\":\"message\"}");
			testContext.completeNow();

		})), Status.NOT_ACCEPTABLE, new NullPointerException("message"));

	}

	/**
	 * Check the response with an error message extracted form an exception.
	 *
	 * @param testContext context that executes the test.
	 */
	@Test
	public void shouldResponseFailedWithValidationErrorException(VertxTestContext testContext) {

		OperationReponseHandlers.responseFailedWith(testContext.succeeding(reponse -> testContext.verify(() -> {

			assertThat(reponse.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
			assertThat(reponse.getHeaders().get(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(reponse.getPayload().toString()).isEqualTo("{\"code\":\"code\",\"message\":\"message\"}");
			testContext.completeNow();

		})), Status.BAD_REQUEST, new ValidationErrorException("code", "message"));

	}

}
