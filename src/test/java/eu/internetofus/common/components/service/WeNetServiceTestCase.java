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

package eu.internetofus.common.components.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link WeNetService}.
 *
 * @see WeNetService
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class WeNetServiceTestCase {

  /**
   * Should not retrieve undefined app.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotRetrieveUndefinedApp(final Vertx vertx, final VertxTestContext testContext) {

    WeNetService.createProxy(vertx).retrieveApp("undefined-app-identifier", testContext.failing(handler -> {
      testContext.completeNow();

    }));

  }

  /**
   * Should retrieve app 1.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldRetrieveFirstApp(final Vertx vertx, final VertxTestContext testContext) {

    WeNetService.createProxy(vertx).retrieveApp("1", testContext.succeeding(app -> testContext.verify(() -> {

      assertThat(app).isNotNull();
      assertThat(app.appId).isEqualTo("1");
      assertThat(app.messageCallbackUrl).isNotNull();
      testContext.completeNow();

    })));

  }

  /**
   * Should not retrieve undefined app.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotRetrieveUserFromUndefinedApp(final Vertx vertx, final VertxTestContext testContext) {

    WeNetService.createProxy(vertx).retrieveJsonArrayAppUserIds("undefined-app-identifier", testContext.failing(handler -> {
      testContext.completeNow();

    }));

  }

  /**
   * Should retrieve app 1.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldRetrieveUserFromFirstApp(final Vertx vertx, final VertxTestContext testContext) {

    WeNetService.createProxy(vertx).retrieveJsonArrayAppUserIds("1", testContext.succeeding(users -> testContext.verify(() -> {

      assertThat(users).isNotNull();
      testContext.completeNow();

    })));

  }

}
