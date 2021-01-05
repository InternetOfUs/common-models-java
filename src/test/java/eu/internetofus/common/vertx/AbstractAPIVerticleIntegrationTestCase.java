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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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

import static eu.internetofus.common.vertx.ext.TestRequest.testRequest;
import static org.assertj.core.api.Assertions.assertThat;

import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import eu.internetofus.common.components.ErrorMessage;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;

/**
 * Generic integration test over a {@link AbstractAPIVerticle}
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractAPIVerticleIntegrationTestCase {

  /**
   * Check that return a bad api request when try to post a bad context.
   *
   * @param client      to realize connection over the
   * @param testContext context to test the VertX event bus.
   *
   * @see #getBadRequestPostPath()
   * @see #createBadRequestPostBody()
   */
  @Test
  public void shouldReturnABadRequestError(final WebClient client, final VertxTestContext testContext) {

    testRequest(client, HttpMethod.POST, this.getBadRequestPostPath()).expect(res -> {
      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final var error = HttpResponses.assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isEqualTo("bad_api_request");
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);
    }).sendJson(this.createBadRequestPostBody(), testContext);

  }

  /**
   * Return the path to the API to post and will return a bad request.
   *
   * @return the path to post.
   */
  protected abstract String getBadRequestPostPath();

  /**
   * Create a body to post that produces a bad api request.
   *
   * @return an object that
   */
  protected JsonObject createBadRequestPostBody() {

    return new JsonObject().put("undefinedKey", "undefined value");
  }

  /**
   * Verify that return a not found error.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldReturnNotFoundErrorMessage(final WebClient client, final VertxTestContext testContext) {

    final var undefinedPath = "/undefinedPath";
    testRequest(client, HttpMethod.GET, undefinedPath).expect(res -> {
      assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
      final var error = HttpResponses.assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isEqualTo("not_found_api_request_path");
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code).contains(undefinedPath);
    }).send(testContext);
  }

}
