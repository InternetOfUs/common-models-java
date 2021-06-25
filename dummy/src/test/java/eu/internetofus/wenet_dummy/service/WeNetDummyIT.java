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

package eu.internetofus.wenet_dummy.service;

import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.wenet_dummy.WeNetDummyIntegrationExtension;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Integration test over the {@link WeNetDummy}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetDummyIntegrationExtension.class)
public class WeNetDummyIT {

  /**
   * Should create dummy model.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldCreateDummy(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new DummyTest().createModelExample(1);
    model.id = null;
    testContext.assertComplete(WeNetDummy.createProxy(vertx).createDummy(model))
        .onSuccess(posted -> testContext.verify(() -> {

          assertThat(posted).isNotEqualTo(model);
          model.id = posted.id;
          assertThat(posted).isEqualTo(model);
          testContext.completeNow();

        }));

  }

}
