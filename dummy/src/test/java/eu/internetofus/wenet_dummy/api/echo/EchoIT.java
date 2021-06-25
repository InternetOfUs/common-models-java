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
package eu.internetofus.wenet_dummy.api.echo;

import static io.reactiverse.junit5.web.TestRequest.testRequest;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.Model;
import eu.internetofus.wenet_dummy.WeNetDummyIntegrationExtension;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
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
  public void shouldPost(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var model = new JsonObject().put("key", "value").putNull("nullField").put("emptyField", new JsonArray()).put(
        "child", new JsonObject().put("child", "1").putNull("nullChildField").put("emptyChildField", new JsonArray()));
    testRequest(client, HttpMethod.POST, Echo.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.CREATED.getStatusCode());
      final var posted = res.bodyAsJsonObject();
      assertThat(posted).isNotNull().isEqualTo(model);
      assertThat(posted.fieldNames()).isEqualTo(model.fieldNames());
      assertThat(posted.getJsonObject("child").fieldNames()).isEqualTo(model.getJsonObject("child").fieldNames());

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
  public void shouldPatch(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var model = new JsonObject().put("key", "value").putNull("nullField").put("emptyField", new JsonArray()).put(
        "child", new JsonObject().put("child", "1").putNull("nullChildField").put("emptyChildField", new JsonArray()));
    testRequest(client, HttpMethod.PATCH, Echo.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
      final var patched = res.bodyAsJsonObject();
      assertThat(patched).isNotNull().isEqualTo(model);
      assertThat(patched.fieldNames()).isEqualTo(model.fieldNames());
      assertThat(patched.getJsonObject("child").fieldNames()).isEqualTo(model.getJsonObject("child").fieldNames());

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
  public void shouldPut(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var model = new JsonObject().put("key", "value").putNull("nullField").put("emptyField", new JsonArray()).put(
        "child", new JsonObject().put("child", "1").putNull("nullChildField").put("emptyChildField", new JsonArray()));
    testRequest(client, HttpMethod.PUT, Echo.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
      final var puted = res.bodyAsJsonObject();
      assertThat(puted).isNotNull().isEqualTo(model);
      assertThat(puted.fieldNames()).isEqualTo(model.fieldNames());
      assertThat(puted.getJsonObject("child").fieldNames()).isEqualTo(model.getJsonObject("child").fieldNames());

    }).sendJson(model, testContext);

  }

  /**
   * Should post dummy model.
   *
   * @param vertx       that contains the event bus to use.
   * @param client      to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldPostDummyModel(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var model = new DummyModel();
    model.children = new ArrayList<>();
    model.children.add(new DummyModel());
    model.children.add(new DummyModel());
    model.children.add(new DummyModel());
    model.children.get(1).name = "Name";
    model.children.get(1).parent = new DummyParentModel();
    model.children.get(1).parent.name = "Parent";
    final var modelObject = new JsonObject(model.toBufferWithEmptyValues());

    testRequest(client, HttpMethod.POST, Echo.PATH + "/dummy").expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.CREATED.getStatusCode());
      final var posted = res.bodyAsJsonObject();
      assertThat(posted).isNotNull().isEqualTo(modelObject);
      final var postedModel = Model.fromJsonObject(posted, DummyModel.class);
      assertThat(postedModel).isEqualTo(model);

    }).sendJson(modelObject, testContext);

  }
}
