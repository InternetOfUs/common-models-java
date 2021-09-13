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

import static eu.internetofus.common.components.AbstractComponentMocker.createClientWithDefaultSession;
import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test the {@link WeNetIncentiveServerSimulator}.
 *
 * @see WeNetIncentiveServerSimulator
 * @see WeNetIncentiveServerSimulatorClient
 * @see WeNetIncentiveServerSimulatorMocker
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class WeNetIncentiveServerSimulatorTest extends WeNetIncentiveServerTestCase {

  /**
   * The social context builder mocked server.
   */
  protected static WeNetIncentiveServerSimulatorMocker IncentiveServerMocker;

  /**
   * Start the mocker servers.
   */
  @BeforeAll
  public static void startMockers() {

    IncentiveServerMocker = WeNetIncentiveServerSimulatorMocker.start();
  }

  /**
   * Stop the mocker server.
   */
  @AfterAll
  public static void stopMockers() {

    IncentiveServerMocker.stopServer();
  }

  /**
   * Register the client.
   *
   * @param vertx event bus to use.
   */
  @BeforeEach
  public void registerClient(final Vertx vertx) {

    final var client = createClientWithDefaultSession(vertx);
    final var IncentiveServerConf = IncentiveServerMocker.getComponentConfiguration();
    WeNetIncentiveServerSimulator.register(vertx, client, IncentiveServerConf);
    WeNetIncentiveServer.register(vertx, client, IncentiveServerConf);

  }

  /**
   * Should manage the social notification.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldSetGetDeleteTaskTypeStatus(final Vertx vertx, final VertxTestContext testContext) {

    final var body = new TaskTypeStatusBodyTest().createModelExample(1);
    WeNetIncentiveServerSimulator.createProxy(vertx).updateTaskTypeStatus(body)
        .compose(any -> WeNetIncentiveServerSimulator.createProxy(vertx).getTaskTypeStatus()).onComplete(

            testContext.succeeding(getted -> testContext.verify(() -> {

              assertThat(getted).isNotEmpty().hasSizeGreaterThanOrEqualTo(1);
              assertThat(getted.get(getted.size() - 1)).isEqualTo(body);
              WeNetIncentiveServerSimulator.createProxy(vertx).deleteTaskTypeStatus()
                  .compose(any -> WeNetIncentiveServerSimulator.createProxy(vertx).getTaskTypeStatus()).onComplete(

                      testContext.succeeding(empty -> testContext.verify(() -> {

                        assertThat(empty).isEmpty();
                        testContext.completeNow();

                      })));
            })));

  }

  /**
   * Should manage the social notification.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldSetGetDeleteTaskTransactionStatus(final Vertx vertx, final VertxTestContext testContext) {

    final var body = new TaskTransactionStatusBodyTest().createModelExample(1);
    WeNetIncentiveServerSimulator.createProxy(vertx).updateTaskTransactionStatus(body)
        .compose(any -> WeNetIncentiveServerSimulator.createProxy(vertx).getTaskTransactionStatus()).onComplete(

            testContext.succeeding(getted -> testContext.verify(() -> {

              assertThat(getted).isNotEmpty().hasSizeGreaterThanOrEqualTo(1);
              assertThat(getted.get(getted.size() - 1)).isEqualTo(body);
              WeNetIncentiveServerSimulator.createProxy(vertx).deleteTaskTransactionStatus()
                  .compose(any -> WeNetIncentiveServerSimulator.createProxy(vertx).getTaskTransactionStatus())
                  .onComplete(

                      testContext.succeeding(empty -> testContext.verify(() -> {

                        assertThat(empty).isEmpty();
                        testContext.completeNow();

                      })));
            })));

  }

}
