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

import eu.internetofus.common.components.models.ProtocolNorm;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Test over the {@link DefaultProtocols#FLOOD_V1} with individual user norms
 * that not affect the protocol because they have too low priority. ATTENTION:
 * This test is sequential and maintains the state between methods. In other
 * words, you must to run the entire test methods on the specified order to
 * work.
 *
 * @see DefaultProtocols#FLOOD_V1
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class FloodV1ProtocolButIgnoreIndividualNormsBecauseTheyHaveLowPriorityITC extends FloodV1ProtocolITC {

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
      for (var j = 0; j < this.users.size(); j++) {

        if (j != i) {

          final var norm = new ProtocolNorm();
          norm.priority = 0;
          norm.description = "Ignored norm because it has too low priority.";
          norm.whenever = "get_transaction_actioneer_id('" + this.users.get(j).id + "')";
          norm.thenceforth = "not(send_user_message(_,_))";
          user.norms.add(norm);

        }

      }

      futures.add(WeNetProfileManager.createProxy(vertx).updateProfile(user.id, user));

    }

    CompositeFuture.all(futures).onFailure(error -> testContext.failNow(error))
        .onSuccess(any -> testContext.completeNow());

  }

}
