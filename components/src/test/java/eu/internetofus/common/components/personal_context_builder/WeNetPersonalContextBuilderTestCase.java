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

package eu.internetofus.common.components.personal_context_builder;

import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.WeNetComponentTestCase;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;

/**
 * Test the {@link WeNetPersonalContextBuilder}.
 *
 * @see WeNetPersonalContextBuilder
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetPersonalContextBuilderTestCase extends WeNetComponentTestCase<WeNetPersonalContextBuilder> {

  /**
   * {@inheritDoc}
   *
   * @see WeNetPersonalContextBuilder#createProxy(Vertx)
   */
  @Override
  protected WeNetPersonalContextBuilder createComponentProxy(final Vertx vertx) {

    return WeNetPersonalContextBuilder.createProxy(vertx);
  }

  /**
   * Should not obtain user locations of an undefined user.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldObtainEmptyUsersLocationsBecuaseUsersNotDefined(final Vertx vertx,
      final VertxTestContext testContext) {

    final var users = new UsersTest().createModelExample(1);
    testContext.assertComplete(this.createComponentProxy(vertx).obtainUserlocations(users))
        .onSuccess(locations -> testContext.verify(() -> {

          assertThat(locations).isEqualTo(new UsersLocations());
          testContext.completeNow();

        }));

  }

  /**
   * Should obtain the closest user into a location.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldObtainClosestUsers(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertComplete(this.createComponentProxy(vertx).obtainClosestUsersTo(0, 0, 3))
        .onSuccess(usersDistances -> testContext.verify(() -> {

          assertThat(usersDistances).isNotNull().hasSizeBetween(0, 3);
          testContext.completeNow();

        }));

  }

}
