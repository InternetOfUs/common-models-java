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

package eu.internetofus.common.components.interaction_protocol_engine;

import static eu.internetofus.common.components.ValidationsTest.assertIsNotValid;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.ValidationsTest;
import eu.internetofus.common.components.interaction_protocol_engine.ProtocolAddress.Component;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test the {@link ProtocolEvent}
 *
 * @see ProtocolEvent
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class ProtocolEventTest extends AbstractProtocolActionTestCase<ProtocolEvent> {

  /**
   * {@inheritDoc}
   */
  @Override
  public ProtocolEvent createModelExample(final int index) {

    final var model = new ProtocolEvent();
    model.appId = "appId_" + index;
    model.communityId = "communityId_" + index;
    model.taskId = "taskId_" + index;
    model.transactionId = "transactionId_" + index;
    model.particle = "particle_" + index;
    model.content = new JsonObject().put("index", index);
    model.delay = (long) index;
    model.userId = "User_" + index;
    return model;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<ProtocolEvent> createModelExample(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    return testContext.assertComplete(StoreServices.storeProfileExample(index, vertx, testContext).compose(profile -> {

      return super.createModelExample(index, vertx, testContext).compose(event -> {

        event.userId = profile.id;
        return Future.succeededFuture(event);

      });
    }));

  }

  /**
   * Check that is valid without delay.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolEvent#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithoutDelay(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.delay = null;
      assertIsNotValid(model, "delay", vertx, testContext);
    });
  }

  /**
   * Check that is valid without userId.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolEvent#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithoutUserId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.userId = null;
      assertIsNotValid(model, "userId", vertx, testContext);
    });
  }

  /**
   * Check that not accept a large userId.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolEvent#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithALargeUserId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.userId = ValidationsTest.STRING_256;
      assertIsNotValid(model, "userId", vertx, testContext);
    });
  }

  /**
   * Check that not accept an undefined userId.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolEvent#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithUndefinedUserId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.userId = "undefined";
      assertIsNotValid(model, "userId", vertx, testContext);
    });
  }

  /**
   * Check that can convert to a protocol message.
   *
   * @see ProtocolEvent#toProtocolMessage()
   */
  @Test
  public void shouldToProtocolMessage() {

    final var event = this.createModelExample(1);
    final var expectedMsg = new ProtocolMessageTest().createModelExample(1);
    expectedMsg.sender.component = Component.INTERACTION_PROTOCOL_ENGINE;
    expectedMsg.sender.userId = event.userId;
    expectedMsg.receiver.component = Component.INTERACTION_PROTOCOL_ENGINE;
    expectedMsg.receiver.userId = event.userId;
    final var convert = event.toProtocolMessage();
    assertThat(convert).isEqualTo(expectedMsg);

  }

}
