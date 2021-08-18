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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import eu.internetofus.common.model.ValidationErrorException;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test the {@link DefaultProtocol}.
 *
 * @see DefaultProtocol
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
@ExtendWith(MockitoExtension.class)
public class DefaultProtocolTest {

  /**
   * Check that cannot load and undefined protocol.
   *
   * @param protocol    mocked value to use in the test.
   * @param vertx       event bus to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldLoad(@Mock final DefaultProtocol protocol, final Vertx vertx, final VertxTestContext testContext) {

    doReturn("undefined").when(protocol).taskTypeId();
    when(protocol.load(vertx)).thenCallRealMethod();

    protocol.load(vertx).onComplete(testContext.failing(error -> testContext.verify(() -> {

      assertThat(error).isNotNull().isInstanceOf(ValidationErrorException.class);
      assertThat(((ValidationErrorException) error).getCode()).isEqualTo("undefined");
      testContext.completeNow();

    })));

  }
}
