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

import eu.internetofus.common.components.models.TaskType;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Interaction test case over a protocol. ATTENTION: This test is sequential and
 * maintains the state between methods. In other words, you must to run the
 * entire test methods on the specified order to work.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public abstract class AbstractDefaultProtocolITC extends AbstractProtocolITC {

  /**
   * The default protocol to test.
   *
   * @return the default protocol to test.
   */
  protected abstract DefaultProtocols getDefaultProtocolsToUse();

  /**
   * {@inheritDoc}
   *
   * @see #getDefaultProtocolsToUse()
   */
  @Override
  protected Future<TaskType> createTaskTypeForProtocol(final Vertx vertx, final VertxTestContext testContext) {

    final var protocol = this.getDefaultProtocolsToUse();
    return protocol.load(vertx).map(foundTaskType -> {

      foundTaskType.id = null;
      return foundTaskType;

    });
  }

}
