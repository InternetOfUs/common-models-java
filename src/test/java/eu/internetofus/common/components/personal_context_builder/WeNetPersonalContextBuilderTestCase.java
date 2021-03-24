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
