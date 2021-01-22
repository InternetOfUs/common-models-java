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
package eu.internetofus.wenet_dummy.api.echo;

import static io.reactiverse.junit5.web.TestRequest.testRequest;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.wenet_dummy.WeNetDummyIntegrationExtension;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import javax.ws.rs.core.Response.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Integration test over the {@link Echo}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetDummyIntegrationExtension.class)
public class EchoIT {

  /**
   * Should post.
   *
   * @param vertx       that contains the event bus to use.
   * @param client      to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldPost(Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var model = new JsonObject().put("key", "value").putNull("nullField").put("emptyField", new JsonArray()).put(
        "child", new JsonObject().put("child", "1").putNull("nullChildField").put("emptyChildField", new JsonArray()));
    testRequest(client, HttpMethod.POST, Echo.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.CREATED.getStatusCode());
      final var posted = res.bodyAsJsonObject();
      assertThat(posted).isNotNull().isEqualTo(model);
      assertThat(posted.fieldNames()).isEqualTo(model.fieldNames());

    }).sendJson(model, testContext);

  }

  /**
   * Should patch.
   *
   * @param vertx       that contains the event bus to use.
   * @param client      to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldPatch(Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var model = new JsonObject().put("key", "value").putNull("nullField").put("emptyField", new JsonArray()).put(
        "child", new JsonObject().put("child", "1").putNull("nullChildField").put("emptyChildField", new JsonArray()));
    testRequest(client, HttpMethod.PATCH, Echo.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
      final var patched = res.bodyAsJsonObject();
      assertThat(patched).isNotNull().isEqualTo(model);
      assertThat(patched.fieldNames()).isEqualTo(model.fieldNames());

    }).sendJson(model, testContext);

  }

  /**
   * Should put.
   *
   * @param vertx       that contains the event bus to use.
   * @param client      to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldPut(Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var model = new JsonObject().put("key", "value").putNull("nullField").put("emptyField", new JsonArray()).put(
        "child", new JsonObject().put("child", "1").putNull("nullChildField").put("emptyChildField", new JsonArray()));
    testRequest(client, HttpMethod.PUT, Echo.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
      final var puted = res.bodyAsJsonObject();
      assertThat(puted).isNotNull().isEqualTo(model);
      assertThat(puted.fieldNames()).isEqualTo(model.fieldNames());

    }).sendJson(model, testContext);

  }

}
