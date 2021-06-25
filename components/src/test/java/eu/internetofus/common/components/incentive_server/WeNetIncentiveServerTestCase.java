/*
 * -----------------------------------------------------------------------------
 *
 *   Copyright 2019 - 2022 UDT-IA, IIIA-CSIC
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
 * General test over the classes that implements the
 * {@link WeNetIncentiveServer}.
 *
 * @see WeNetIncentiveServer
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetIncentiveServerTestCase extends WeNetComponentTestCase<WeNetIncentiveServer> {

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
   * Should update the task status.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldUpdateTaskStatus(final Vertx vertx, final VertxTestContext testContext) {

    final var status = new TaskStatusTest().createModelExample(1);
    this.createComponentProxy(vertx).updateTaskStatus(status).onSuccess(updated -> testContext.verify(() -> {

      assertThat(updated).isEqualTo(status);
      testContext.completeNow();

    }));

  }

}
