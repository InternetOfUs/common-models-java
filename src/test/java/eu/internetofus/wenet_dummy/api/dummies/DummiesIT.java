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
package eu.internetofus.wenet_dummy.api.dummies;

import static eu.internetofus.common.vertx.HttpResponses.assertThatBodyIs;
import static eu.internetofus.common.vertx.ext.TestRequest.testRequest;
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
