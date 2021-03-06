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

import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.WeNetComponentTestCase;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;

/**
 * Test the {@link WeNetIncentiveServer}.
 *
 * @see WeNetIncentiveServer
 * @see WeNetIncentiveServerClient
 * @see WeNetIncentiveServerSimulatorMocker
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class WeNetIncentiveServerTestCase extends WeNetComponentTestCase<WeNetIncentiveServer> {

  /**
   * {@inheritDoc}
   *
   * @see WeNetIncentiveServer#createProxy(Vertx)
   */
  @Override
  protected WeNetIncentiveServer createComponentProxy(final Vertx vertx) {

    return WeNetIncentiveServer.createProxy(vertx);
  }

  /**
   * Should update the task transaction status.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldUpdateTaskTransactionStatus(final Vertx vertx, final VertxTestContext testContext) {

    final var transactionStatus = new TaskTransactionStatusBodyTest().createModelExample(1);
    this.createComponentProxy(vertx).updateTaskTransactionStatus(transactionStatus)
        .compose(result -> WeNetIncentiveServerSimulator.createProxy(vertx).getTaskTransactionStatus())
        .onComplete(testContext.succeeding(status -> testContext.verify(() -> {

          assertThat(status).isNotEmpty().contains(transactionStatus);
          WeNetIncentiveServerSimulator.createProxy(vertx).deleteTaskTransactionStatus()
              .compose(empty -> WeNetIncentiveServerSimulator.createProxy(vertx).getTaskTransactionStatus())
              .onComplete(testContext.succeeding(status2 -> testContext.verify(() -> {
                assertThat(status2).isEmpty();
                testContext.completeNow();
              })));
        })));

  }

  /**
   * Should update the task type status.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldUpdateTaskTypeStatus(final Vertx vertx, final VertxTestContext testContext) {

    final var typeStatus = new TaskTypeStatusBodyTest().createModelExample(1);
    this.createComponentProxy(vertx).updateTaskTypeStatus(typeStatus)
        .compose(result -> WeNetIncentiveServerSimulator.createProxy(vertx).getTaskTypeStatus())
        .onComplete(testContext.succeeding(status -> testContext.verify(() -> {

          assertThat(status).isNotEmpty().contains(typeStatus);
          WeNetIncentiveServerSimulator.createProxy(vertx).deleteTaskTypeStatus()
              .compose(empty -> WeNetIncentiveServerSimulator.createProxy(vertx).getTaskTypeStatus())
              .onComplete(testContext.succeeding(status2 -> testContext.verify(() -> {
                assertThat(status2).isEmpty();
                testContext.completeNow();
              })));
        })));

  }

}
