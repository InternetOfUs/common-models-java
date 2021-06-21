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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.vertx.core.Vertx;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link ComponentClient}
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class ComponentClientTest {

  /**
   * Verify that can not post a {@link JsonArray} over an undefined service.
   *
   * @param vertx       event bus to use.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotPostJsonArrayOverAnUndefinedService(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    testContext.assertFailure(service.post(new JsonArray(), "path")).onFailure(ignored -> testContext.completeNow());

  }

  /**
   * Verify that can not post a {@link JsonObject} over an undefined service.
   *
   * @param vertx       event bus to use.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotPostJsonObjectOverAnUndefinedService(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    testContext.assertFailure(service.post(new JsonObject(), "path")).onFailure(ignored -> testContext.completeNow());

  }

  /**
   * Verify that can not put a {@link JsonArray} over an undefined service.
   *
   * @param vertx       event bus to use.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotPutJsonArrayOverAnUndefinedService(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    testContext.assertFailure(service.put(new JsonArray(), "path")).onFailure(ignored -> testContext.completeNow());

  }

  /**
   * Verify that can not put a {@link JsonObject} over an undefined service.
   *
   * @param vertx       event bus to use.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotPutJsonObjectOverAnUndefinedService(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    testContext.assertFailure(service.put(new JsonObject(), "path")).onFailure(ignored -> testContext.completeNow());

  }

  /**
   * Verify that can not get over an undefined service.
   *
   * @param vertx       event bus to use.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotGetJsonArrayOverAnUndefinedService(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    testContext.assertFailure(service.getJsonArray("path")).onFailure(ignored -> testContext.completeNow());

  }

  /**
   * Verify that can not get over an undefined service.
   *
   * @param vertx       event bus to use.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotGetJsonObjectOverAnUndefinedService(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    testContext.assertFailure(service.getJsonObject("path")).onFailure(ignored -> testContext.completeNow());

  }

  /**
   * Verify that fail post when not return a JSON object.
   *
   * @param vertx       platform that manage the event bus.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldFailPostIfNotReturnJsonObject(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    vertx.createHttpServer().requestHandler(request -> {
      final var response = request.response();
      response.putHeader("content-type", "text/plain");
      response.end("Hello World!");
    }).listen(0, "localhost", testContext.succeeding(server -> {

      final var service = new ComponentClient(client, "http://localhost:" + server.actualPort() + "/api");
      testContext.assertFailure(service.post(new JsonObject()).onFailure(cause -> testContext.verify(() -> {

        server.close();
        assertThat(cause).isInstanceOf(DecodeException.class);
        testContext.completeNow();

      })));

    }));

  }

  /**
   * Verify that can not get over an undefined service.
   *
   * @param vertx       event bus to use.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotGetOverAnUndefinedService(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined");
    testContext.assertFailure(service.getJsonObject("path")).onFailure(ignored -> testContext.completeNow());

  }

  /**
   * Verify that can not delete over an undefined service.
   *
   * @param vertx       event bus to use.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotDeleteOverAnUndefinedService(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined");
    testContext.assertFailure(service.delete("path")).onFailure(ignored -> testContext.completeNow());

  }

  /**
   * Test the creation of an absolute URL
   *
   * @param componentURL url to the component.
   * @param values       paths to set it are separated by :.
   * @param expectedURL  the URL that has to create the client.
   * @param vertx        event bus to use.
   * @param client       to use.
   * @param testContext  context that manage the test.
   */
  @ParameterizedTest(name = "Should the absolute URL for component {0} with parameters {1} be equals to {2}")
  @CsvSource({ "https://localhost:8080,     ,https://localhost:8080", "https://localhost:8080,a:b:c,https://localhost:8080/a/b/c", "https://localhost:8080,   a  :  b  : c  ,https://localhost:8080/a/b/c",
    "https://localhost:8080/,/a/:/b:c/,https://localhost:8080/a/b/c/", "https://localhost:8080/,  /a/  :  b/  :  /c  ,https://localhost:8080/a/b/c", "https://localhost:8080/,  a/  :  /b/  :  /c  ,https://localhost:8080/a/b/c" })
  public void shouldCreateAbsoluteUrlWith(final String componentURL, final String values, final String expectedURL, final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var componentClient = new ComponentClient(client, componentURL);
    var paths = new Object[0];
    if (values != null) {

      paths = values.split(":");
    }
    assertThat(componentClient.createAbsoluteUrlWith(paths)).isEqualTo(expectedURL);
    testContext.completeNow();
  }

  /**
   * Verify that can not patch a {@link JsonArray} over an undefined service.
   *
   * @param vertx       event bus to use.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotPatchJsonArrayOverAnUndefinedService(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    testContext.assertFailure(service.patch(new JsonArray(), "path")).onFailure(ignored -> testContext.completeNow());

  }

  /**
   * Verify that can not patch a {@link JsonObject} over an undefined service.
   *
   * @param vertx       event bus to use.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotPatchJsonObjectOverAnUndefinedService(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    testContext.assertFailure(service.patch(new JsonObject(), "path")).onFailure(ignored -> testContext.completeNow());

  }

}
