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

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test the {@link ProtocolMessage}
 *
 * @see ProtocolMessage
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class ProtocolMessageTest extends AbstractProtocolActionTestCase<ProtocolMessage> {

  /**
   * {@inheritDoc}
   */
  @Override
  public ProtocolMessage createModelExample(final int index) {

    final var model = new ProtocolMessage();
    model.appId = "appId_" + index;
    model.communityId = "communityId_" + index;
    model.taskId = "taskId_" + index;
    model.transactionId = "transactionId_" + index;
    model.particle = "particle_" + index;
    model.content = new JsonObject().put("index", index);
    model.sender = new ProtocolAddressTest().createModelExample(index);
    model.receiver = new ProtocolAddressTest().createModelExample(index);
    return model;

  }

  /**
   * Create an example model that has the specified index that create any required
   * component.
   *
   * @param index       to use in the example.
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @return the created protocol message.
   */
  @Override
  public Future<ProtocolMessage> createModelExample(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    return testContext
        .assertComplete(new ProtocolAddressTest().createModelExample(index, vertx, testContext).compose(sender -> {

          return new ProtocolAddressTest().createModelExample(index + 1, vertx, testContext).compose(receiver -> {

            return super.createModelExample(index, vertx, testContext).compose(model -> {

              model.sender = sender;
              model.receiver = receiver;
              return Future.succeededFuture(model);

            });

          });

        }));

  }

  /**
   * Check that is valid without sender.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolMessage#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithoutSender(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.sender = null;
      assertIsNotValid(model, "sender", vertx, testContext);
    });
  }

  /**
   * Check that is valid with bad sender.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolMessage#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithBadSender(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.sender = new ProtocolAddressTest().createModelExample(1);
      assertIsNotValid(model, "sender.userId", vertx, testContext);
    });
  }

  /**
   * Check that is valid without receiver.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolMessage#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithoutReceiver(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.receiver = null;
      assertIsNotValid(model, "receiver", vertx, testContext);
    });
  }

  /**
   * Check that is valid with bad receiver.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolMessage#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithBadReceiver(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.receiver = new ProtocolAddressTest().createModelExample(2);
      assertIsNotValid(model, "receiver.userId", vertx, testContext);
    });
  }

}
