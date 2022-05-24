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
package eu.internetofus.common.protocols;

import eu.internetofus.common.components.models.Message;
import eu.internetofus.common.components.models.ProtocolNorm;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Test over the {@link DefaultProtocols#FLOOD_V1} with individual user norms
 * that not allow to receive a message of its neighbors. ATTENTION: This test is
 * sequential and maintains the state between methods. In other words, you must
 * to run the entire test methods on the specified order to work.
 *
 * @see DefaultProtocols#FLOOD_V1
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class FloodV1ProtocolDoNotInformNeighboursITC extends FloodV1ProtocolITC {

  /**
   * Add the individual norms to the users.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(2)
  public void shouldAddIndividualNormsToUsers(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(1, testContext);

    @SuppressWarnings("rawtypes")
    final List<Future> futures = new ArrayList<>();
    for (var i = 0; i < this.users.size(); i++) {

      final var user = this.users.get(i);
      user.norms = new ArrayList<>();
      var previous = i - 1;
      if (i == 0) {
        previous = this.users.size() - 1;
      }
      final var previousNorm = new ProtocolNorm();
      previousNorm.description = "Do not accept message of the previous user.";
      previousNorm.whenever = "get_transaction_actioneer_id('" + this.users.get(previous).id + "')";
      previousNorm.thenceforth = "not(send_user_message(_,_))";
      user.norms.add(previousNorm);

      var next = i + 1;
      if (next == this.users.size()) {
        next = 0;
      }
      final var nextNorm = new ProtocolNorm();
      nextNorm.description = "Do not accept message of the next user.";
      nextNorm.whenever = "get_transaction_actioneer_id('" + this.users.get(next).id + "')";
      nextNorm.thenceforth = "not(send_user_message(_,_))";
      user.norms.add(nextNorm);

      futures.add(WeNetProfileManager.createProxy(vertx).updateProfile(user.id, user));

    }

    CompositeFuture.all(futures).onFailure(error -> testContext.failNow(error))
        .onSuccess(any -> testContext.completeNow());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected List<Predicate<Message>> createCheckMessagesFor(final String actioneerId, final String content) {

    final var checkMessages = new ArrayList<Predicate<Message>>();
    final var userIds = new ArrayList<String>();
    var max = this.users.size();
    for (var i = 0; i < max; i++) {

      final var userId = this.users.get(i).id;
      if (actioneerId.equals(userId)) {

        // remove previous
        if (i == 0) {

          max--;

        } else {

          userIds.remove(userIds.size() - 1);
        }

        // ignore next
        i++;

      } else {

        userIds.add(userId);

      }

    }

    for (final var userId : userIds) {

      checkMessages.add(this.cerateCheckMessage(userId, content));

    }

    return checkMessages;

  }

}
