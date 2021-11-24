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

import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test the {@link ComponentClientWithCache}
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith({ VertxExtension.class, MockitoExtension.class })
public class ComponentClientWithCacheTest {

  /**
   * Verify that can not get a cached head response.
   *
   * @param vertx       event bus to use.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotHeadCache(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var service = new ComponentClientWithCache(client, new JsonObject(), "undefined", "http://undefined/");
    testContext.assertFailure(service.headWithCache("path")).onFailure(ignored -> testContext.completeNow());

  }

  /**
   * Verify that can get a head response and cache it.
   *
   * @param vertx       platform that manage the event bus.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldHeadWithCachePassThrough(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    vertx.createHttpServer().requestHandler(request -> {
      final var response = request.response();
      response.setStatusCode(204);
      response.putHeader("content-type", "application/json");
      response.end();
    }).listen(0, "localhost", testContext.succeeding(server -> {

      final var url = "http://localhost:" + server.actualPort() + "/api";
      final var service = new ComponentClientWithCache(client, new JsonObject().put("api", url), "api",
          "http://undefined");
      service.headWithCache("1").onComplete(testContext.succeeding(content -> testContext.verify(() -> {

        assertThat(content).isTrue();
        assertThat(service.cache.getIfPresent("head:" + url + "/1")).isNotNull();
        server.close();
        testContext.completeNow();

      })));

    }));

  }

  /**
   * Verify that can get a cached head response.
   *
   * @param vertx       platform that manage the event bus.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldHeadWithCache(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var defaultUrl = "http://localhost:1234/api";
    final var service = new ComponentClientWithCache(client, new JsonObject(), "api", defaultUrl);
    service.cache.put("head:" + defaultUrl + "/1", "true");
    service.headWithCache("1").onComplete(testContext.succeeding(content -> testContext.verify(() -> {

      assertThat(content).isTrue();
      testContext.completeNow();

    })));

  }

}