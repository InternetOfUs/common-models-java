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
