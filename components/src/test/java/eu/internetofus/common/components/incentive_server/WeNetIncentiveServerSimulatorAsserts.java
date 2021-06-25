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

package eu.internetofus.common.components.incentive_server;

import eu.internetofus.common.components.interaction_protocol_engine.WeNetInteractionProtocolEngine;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import java.util.function.Predicate;
import javax.validation.constraints.NotNull;

/**
 * Assert methods that can be used when interact with the
 * {@link WeNetIncentiveServerSimulator}.
 *
 * @see WeNetInteractionProtocolEngine
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface WeNetIncentiveServerSimulatorAsserts {

  /**
   * Assert until a task status satisfy the predicate.
   *
   * @param checkTaskStatus the function that has to be true when the task status
   *                        is the expected one.
   * @param vertx           event bus to use.
   * @param testContext     context to do the test.
   *
   * @return the future task status that is expecting.
   */
  static Future<TaskStatus> assertUntilTaskStatusIs(@NotNull final Predicate<TaskStatus> checkTaskStatus,
      final Vertx vertx, final VertxTestContext testContext) {

    return testContext
        .assertComplete(WeNetIncentiveServerSimulator.createProxy(vertx).getTaskStatus().compose(status -> {

          for (final var taskStatus : status) {

            if (checkTaskStatus.test(taskStatus)) {

              return Future.succeededFuture(taskStatus);
            }

          }

          return assertUntilTaskStatusIs(checkTaskStatus, vertx, testContext);

        }));

  }

}
