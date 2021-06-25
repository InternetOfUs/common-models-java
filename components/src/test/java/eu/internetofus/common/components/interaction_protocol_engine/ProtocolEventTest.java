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

import static eu.internetofus.common.model.ValidationsTest.assertIsNotValid;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.StoreServices;
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
