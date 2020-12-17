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

package eu.internetofus.common.components.social_context_builder;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link WeNetSocialContextBuilder}.
 *
 * @see WeNetSocialContextBuilder
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class WeNetSocialContextBuilderTestCase {

  /**
   * Should retrieve social relations.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldRetrieveSocialRelations(final Vertx vertx, final VertxTestContext testContext) {

    final var userId = UUID.randomUUID().toString();
    testContext.assertComplete(WeNetSocialContextBuilder.createProxy(vertx).retrieveSocialRelations(userId)).onSuccess(relations -> testContext.verify(() -> {

      assertThat(relations).isNotNull();
      testContext.completeNow();

    }));

  }

  /**
   * Should update preferences for user on task.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldUpdatePreferencesForUserOnTask(final Vertx vertx, final VertxTestContext testContext) {

    final var userId = UUID.randomUUID().toString();
    final var taskId = UUID.randomUUID().toString();
    final var volunteers = new JsonArray();
    testContext.assertComplete(WeNetSocialContextBuilder.createProxy(vertx).updatePreferencesForUserOnTask(userId, taskId, volunteers)).onSuccess(updated -> testContext.verify(() -> {

      assertThat(updated).isNotNull();
      testContext.completeNow();

    }));

  }

  /**
   * Should retrieve social relations.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldRetrievSocialRelations(final Vertx vertx, final VertxTestContext testContext) {

    final var userId = UUID.randomUUID().toString();
    final var taskId = UUID.randomUUID().toString();
    testContext.assertComplete(WeNetSocialContextBuilder.createProxy(vertx).retrieveSocialExplanation(userId, taskId)).onSuccess(relations -> testContext.verify(() -> {

      assertThat(relations).isNotNull();
      testContext.completeNow();

    }));

  }

}
