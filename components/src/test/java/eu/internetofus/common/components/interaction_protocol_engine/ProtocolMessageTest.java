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

package eu.internetofus.common.components.interaction_protocol_engine;

import static eu.internetofus.common.model.ValidableAsserts.assertIsNotValid;

import eu.internetofus.common.components.WeNetIntegrationExtension;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
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
@ExtendWith(WeNetIntegrationExtension.class)
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
