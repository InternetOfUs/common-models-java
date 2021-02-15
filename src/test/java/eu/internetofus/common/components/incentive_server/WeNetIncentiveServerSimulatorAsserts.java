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
