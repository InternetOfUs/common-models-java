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

package eu.internetofus.common.components.interaction_protocol_engine;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import eu.internetofus.common.components.incentive_server.IncentiveTest;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;

/**
 * General test over the classes that implements the {@link WeNetInteractionProtocolEngine}.
 *
 * @see WeNetInteractionProtocolEngine
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class WeNetInteractionProtocolEngineTestCase {

  /**
   * Should send message.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldSendMessage(final Vertx vertx, final VertxTestContext testContext) {

    new MessageTest().createModelExample(1, vertx, testContext, testContext.succeeding(message -> {

      message.norms = null;
      WeNetInteractionProtocolEngine.createProxy(vertx).sendMessage(message, testContext.succeeding(sent -> testContext.verify(() -> {

        assertThat(message).isEqualTo(sent);
        testContext.completeNow();

      })));

    }));

  }

  /**
   * Should send incentive.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldSendIncentive(final Vertx vertx, final VertxTestContext testContext) {

    new IncentiveTest().createModelExample(1, vertx, testContext, testContext.succeeding(incentive -> {

      WeNetInteractionProtocolEngine.createProxy(vertx).sendIncentive(incentive, testContext.succeeding(sent -> testContext.verify(() -> {

        assertThat(incentive).isEqualTo(sent);
        testContext.completeNow();

      })));

    }));

  }

}
