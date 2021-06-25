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

package eu.internetofus.common.components;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;

/**
 * General test over the classes that implements the {@link WeNetComponent}.
 *
 * @param <T> type of component to test.
 *
 * @see WeNetComponent
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class WeNetComponentTestCase<T extends WeNetComponent> {

  /**
   * Create the component to use in the test.
   *
   * @param vertx that contains the event bus to use.
   *
   * @return the created component.
   */
  protected abstract T createComponentProxy(final Vertx vertx);

  /**
   * Should obtain the the task status.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldObtainApiUrl(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertComplete(this.createComponentProxy(vertx).obtainApiUrl())
        .onSuccess(apiUrl -> testContext.completeNow());

  }

}
