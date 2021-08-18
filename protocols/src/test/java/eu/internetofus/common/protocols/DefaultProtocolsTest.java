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

import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Test the {@link DefaultProtocols}.
 *
 * @see DefaultProtocols
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class DefaultProtocolsTest {

  /**
   * Check that can load all the protocols.
   *
   * @param protocol    to load.
   * @param vertx       event bus to use.
   * @param testContext context that manage the test.
   */
  @ParameterizedTest(name = "Should load {0}")
  @EnumSource(DefaultProtocols.class)
  public void shouldLoad(final DefaultProtocols protocol, final Vertx vertx, final VertxTestContext testContext) {

    protocol.load(vertx).onComplete(testContext.succeeding(taskType -> testContext.verify(() -> {

      assertThat(taskType).isNotNull();
      testContext.completeNow();

    })));

  }

  /**
   * Check that the task type identifier id the same that the loaded one.
   *
   * @param protocol    to load.
   * @param vertx       event bus to use.
   * @param testContext context that manage the test.
   */
  @ParameterizedTest(name = "Should task type id of {0} match the id of the loaded one")
  @EnumSource(DefaultProtocols.class)
  public void shouldTaskTypeIdMatchLoadedOne(final DefaultProtocols protocol, final Vertx vertx,
      final VertxTestContext testContext) {

    protocol.load(vertx).onComplete(testContext.succeeding(taskType -> testContext.verify(() -> {

      assertThat(protocol.taskTypeId()).isEqualTo(taskType.id);
      testContext.completeNow();

    })));

  }

  /**
   * Check that the task type associated to the protocol are valid.
   *
   * @param protocol    to load.
   * @param vertx       event bus to use.
   * @param testContext context that manage the test.
   */
  @ParameterizedTest(name = "Should {0} be valid")
  @EnumSource(DefaultProtocols.class)
  public void shouldProtocolsBeValid(final DefaultProtocols protocol, final Vertx vertx,
      final VertxTestContext testContext) {

    protocol.load(vertx).compose(taskType -> taskType.validate("root", vertx))
        .onComplete(testContext.succeeding(taskType -> testContext.completeNow()));

  }
}
