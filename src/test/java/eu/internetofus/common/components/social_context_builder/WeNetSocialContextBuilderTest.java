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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link WeNetSocialContextBuilder}.
 *
 * @see WeNetSocialContextBuilder
 * @see WeNetSocialContextBuilderClient
 * @see WeNetSocialContextBuilderMocker
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class WeNetSocialContextBuilderTest {

  /**
   * Register the service before to test it.
   *
   * @param vertx event bus to use.
   */
  @BeforeEach
  public void registerServiceBeforeStartTest(final Vertx vertx) {

    final WeNetSocialContextBuilderMocker mocker = WeNetSocialContextBuilderMocker.start();
    final WebClient client = WebClient.create(vertx);
    final JsonObject conf = mocker.getComponentConfiguration();
    WeNetSocialContextBuilder.register(vertx, client, conf);

  }

  /**
   * @param vertx       event bus to use.
   * @param testContext context of the executing test.
   */
  @Test
  public void shouldReturnEmpty(final Vertx vertx, final VertxTestContext testContext) {

    final WeNetSocialContextBuilder socialContextBuilder = WeNetSocialContextBuilder.createProxy(vertx);
    socialContextBuilder.retrieveSocialRelations("userId", testContext.succeeding(relations -> testContext.verify(() -> {

      assertThat(relations).isEmpty();
      testContext.completeNow();

    })));

  }

}
