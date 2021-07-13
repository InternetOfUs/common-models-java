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

import static io.reactiverse.junit5.web.TestRequest.testRequest;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.model.ErrorMessage;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import javax.ws.rs.core.Response.Status;
import org.junit.jupiter.api.Test;

/**
 * Generic integration test over a {@link AbstractAPIVerticle}
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractAPIVerticleIntegrationTestCase {

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
