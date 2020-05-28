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

package eu.internetofus.common.components.social_context_builder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpStatusCode;

import eu.internetofus.common.components.Model;
import eu.internetofus.common.vertx.ComponentClientTestCase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link WeNetSocialContextBuilderImpl}
 *
 * @see WeNetSocialContextBuilderImpl
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetSocialContextBuilderImplTest extends ComponentClientTestCase<WeNetSocialContextBuilderImpl> {

  /**
   * Create a new test.
   *
   * @param client for the mocker server.
   */
  public WeNetSocialContextBuilderImplTest(final ClientAndServer client) {
    super(client);
  }

  /**
   * {@inheridDoc}
   */
  @Override
  protected WeNetSocialContextBuilderImpl createClient(final WebClient client) {

    final JsonObject conf = new JsonObject().put("socialContextBuilder", "http://localhost:" + this.mockedServer.getLocalPort() + "/social_context_builder");
    return new WeNetSocialContextBuilderImpl(client, conf);

  }

  /**
   * Should return relations from the social context builder.
   *
   * @param client      to connect with the service.
   * @param testContext to use.
   */
  @Test
  public void shouldReturnRelations(final WebClient client, final VertxTestContext testContext) {

    final WeNetSocialContextBuilderImpl socialContextBuilder = this.createClient(client);
    final String userId = "1234";
    final List<UserRelation> expected = new ArrayList<>();
    expected.add(new UserRelationTest().createModelExample(1));
    expected.add(new UserRelationTest().createModelExample(2));
    expected.add(new UserRelationTest().createModelExample(3));
    expected.add(new UserRelationTest().createModelExample(4));
    this.mockedServer.when(request().withMethod("GET").withPath("/social_context_builder/social/relations/" + userId)).respond(response().withStatusCode(HttpStatusCode.OK_200.code()).withBody(Model.toJsonArray(expected).encode()));
    socialContextBuilder.retrieveSocialRelations(userId, testContext.succeeding(relations -> testContext.verify(() -> {

      assertThat(relations).isEqualTo(expected);
      testContext.completeNow();

    })));

  }

  /**
   * Should not return relations because the returned content is not a social relation.
   *
   * @param client      to connect with the service.
   * @param testContext to use.
   */
  @Test
  public void shouldNotReturnRelations(final WebClient client, final VertxTestContext testContext) {

    final WeNetSocialContextBuilderImpl socialContextBuilder = this.createClient(client);
    final String userId = "12345";
    this.mockedServer.when(request().withMethod("GET").withPath("/social_context_builder/social/relations/" + userId)).respond(response().withStatusCode(HttpStatusCode.OK_200.code()).withBody(new JsonObject().encode()));
    socialContextBuilder.retrieveSocialRelations(userId, testContext.failing(error -> testContext.completeNow()));

  }

}
