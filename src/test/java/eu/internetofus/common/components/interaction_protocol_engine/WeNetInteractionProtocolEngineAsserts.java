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

package eu.internetofus.common.components.interaction_protocol_engine;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import java.util.function.Predicate;
import javax.validation.constraints.NotNull;

/**
 * Assert methods that can be used when interact with the
 * {@link WeNetInteractionProtocolEngine}.
 *
 * @see WeNetInteractionProtocolEngine
 *
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface WeNetInteractionProtocolEngineAsserts {

  /**
   * Assert until a community user state satisfy a predicate.
   *
   * @param checkState  the function that has to be true when the state is the
   *                    expected one.
   * @param communityId identifier of the community.
   * @param userId      identifier of the user.
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   *
   * @return the future that will return the state that the predicate accept.
   */
  static Future<State> assertUntilCommunityUserStateIs(@NotNull final Predicate<State> checkState,
      @NotNull final String communityId, @NotNull final String userId, final Vertx vertx,
      final VertxTestContext testContext) {

    return testContext
        .assertComplete(
            WeNetInteractionProtocolEngine.createProxy(vertx).retrieveCommunityUserState(communityId, userId))
        .compose(state -> {

          if (checkState.test(state)) {

            return Future.succeededFuture(state);

          } else if (!testContext.completed()) {

            return assertUntilCommunityUserStateIs(checkState, communityId, userId, vertx, testContext);

          } else {

            return Future.failedFuture("Finished without the expected community user state.");
          }

        });

  }

}
