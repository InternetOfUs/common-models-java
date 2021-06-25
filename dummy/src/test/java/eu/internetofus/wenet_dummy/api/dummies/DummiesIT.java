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
package eu.internetofus.wenet_dummy.api.dummies;

import static eu.internetofus.common.vertx.HttpResponses.assertThatBodyIs;
import static io.reactiverse.junit5.web.TestRequest.testRequest;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.ErrorMessage;
import eu.internetofus.wenet_dummy.WeNetDummyIntegrationExtension;
import eu.internetofus.wenet_dummy.service.Dummy;
import eu.internetofus.wenet_dummy.service.DummyTest;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import javax.ws.rs.core.Response.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Integration test over the {@link Dummies}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetDummyIntegrationExtension.class)
public class DummiesIT {

  /**
   * Should post dummy model.
   *
   * @param vertx       that contains the event bus to use.
   * @param client      to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldPostDummy(Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var model = new DummyTest().createModelExample(1);
    model.id = null;
    testRequest(client, HttpMethod.POST, Dummies.PATH).expect(res -> {
      assertThat(res.statusCode()).isEqualTo(Status.CREATED.getStatusCode());
      final var posted = assertThatBodyIs(Dummy.class, res);
      assertThat(posted).isNotNull().isNotEqualTo(model);
      model.id = posted.id;
      assertThat(posted).isEqualTo(model);
    }).sendJson(model, testContext);

  }

  /**
   * Should fail post bad dummy model.
   *
   * @param vertx       that contains the event bus to use.
   * @param client      to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldFailPostBadDummymodel(Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    testRequest(client, HttpMethod.POST, Dummies.PATH).expect(res -> {
      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error).isNotNull();
    }).sendJson(new JsonObject().put("undefined", "undefined"), testContext);

  }

  /**
   * Should not post dummy model with an existing identifier.
   *
   * @param vertx       that contains the event bus to use.
   * @param client      to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldFailPostDummyIdDefined(Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    var checkpoint = testContext.checkpoint(2);
    final var model = new DummyTest().createModelExample(1);
    model.id = null;
    testRequest(client, HttpMethod.POST, Dummies.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.CREATED.getStatusCode());
      final var posted = assertThatBodyIs(Dummy.class, res);
      assertThat(posted).isNotNull();
      testRequest(client, HttpMethod.POST, Dummies.PATH).expect(res2 -> {

        assertThat(res2.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res2);
        assertThat(error).isNotNull();

      }).sendJson(posted.toJsonObject(), testContext, checkpoint);

    }).sendJson(model, testContext, checkpoint);

  }

}
