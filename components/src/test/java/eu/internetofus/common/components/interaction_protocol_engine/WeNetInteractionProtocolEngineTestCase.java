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

import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.WeNetComponentTestCase;
import eu.internetofus.common.components.models.IncentiveTest;
import eu.internetofus.common.components.models.TaskTest;
import eu.internetofus.common.components.models.TaskTransactionTest;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;

/**
 * General test over the classes that implements the
 * {@link WeNetInteractionProtocolEngine}.
 *
 * @see WeNetInteractionProtocolEngine
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetInteractionProtocolEngineTestCase extends WeNetComponentTestCase<WeNetInteractionProtocolEngine> {

  /**
   * {@inheritDoc}
   *
   * @see WeNetInteractionProtocolEngine#createProxy(Vertx)
   */
  @Override
  protected WeNetInteractionProtocolEngine createComponentProxy(final Vertx vertx) {

    return WeNetInteractionProtocolEngine.createProxy(vertx);
  }

  /**
   * Should send message.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldSendMessage(final Vertx vertx, final VertxTestContext testContext) {

    new ProtocolMessageTest().createModelExample(1, vertx, testContext).onSuccess(message -> {

      testContext.assertComplete(this.createComponentProxy(vertx).sendMessage(message))
          .onSuccess(sent -> testContext.verify(() -> {

            assertThat(message).isEqualTo(sent);
            testContext.completeNow();

          }));

    });

  }

  /**
   * Should send incentive.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldSendIncentive(final Vertx vertx, final VertxTestContext testContext) {

    new IncentiveTest().createModelExample(1, vertx, testContext).onSuccess(incentive -> {

      testContext.assertComplete(this.createComponentProxy(vertx).sendIncentive(incentive))
          .onSuccess(sent -> testContext.verify(() -> {

            assertThat(incentive).isEqualTo(sent);
            testContext.completeNow();

          }));

    });

  }

  /**
   * Should inform that a task is created.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldCreatedTask(final Vertx vertx, final VertxTestContext testContext) {

    new TaskTest().createModelExample(1, vertx, testContext).onSuccess(task -> {

      testContext.assertComplete(this.createComponentProxy(vertx).createdTask(task))
          .onSuccess(created -> testContext.verify(() -> {

            assertThat(task).isEqualTo(created);
            testContext.completeNow();

          }));

    });

  }

  /**
   * Should do a task transaction.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldDoTaskTransaction(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertComplete(new TaskTransactionTest().createModelExample(1, vertx, testContext))
        .onSuccess(taskTransaction -> {

          testContext.assertComplete(this.createComponentProxy(vertx).doTransaction(taskTransaction))
              .onSuccess(done -> testContext.verify(() -> {

                assertThat(taskTransaction).isEqualTo(done);
                testContext.completeNow();

              }));

        });

  }

  /**
   * Should retrieve community user state.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldRetrieveCommunityUserState(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeCommunityExample(1, vertx, testContext).onSuccess(community -> {

      final var userId = community.members.get(0).userId;
      testContext.assertComplete(this.createComponentProxy(vertx).retrieveCommunityUserState(community.id, userId))
          .onSuccess(done -> testContext.verify(() -> {

            final var state = new State();
            state.communityId = community.id;
            state.userId = userId;
            state._creationTs = done._creationTs;
            state._lastUpdateTs = done._lastUpdateTs;
            assertThat(done).isEqualTo(state);
            assertThat(done._creationTs).isEqualTo(done._lastUpdateTs);
            testContext.completeNow();

          }));
    });
  }

  /**
   * Should merge community user state.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldMergeCommunityUserState(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeCommunityExample(1, vertx, testContext).onSuccess(community -> {
      final var state = new StateTest().createModelExample(1);
      state.communityId = community.id;
      state.taskId = null;
      state.userId = community.members.get(0).userId;
      testContext
          .assertComplete(
              this.createComponentProxy(vertx).mergeCommunityUserState(state.communityId, state.userId, state))
          .onSuccess(done -> testContext.verify(() -> {

            state._creationTs = done._creationTs;
            state._lastUpdateTs = done._lastUpdateTs;
            assertThat(done).isEqualTo(state);

            state.attributes.put("newKey", "testContext");
            testContext
                .assertComplete(
                    this.createComponentProxy(vertx).mergeCommunityUserState(state.communityId, state.userId, state))
                .onSuccess(done2 -> testContext.verify(() -> {

                  state._lastUpdateTs = done2._lastUpdateTs;
                  assertThat(done2).isEqualTo(state);
                  testContext.completeNow();

                }));

          }));
    });

  }

  /**
   * Should send and delete event.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldSendDeleteEvent(final Vertx vertx, final VertxTestContext testContext) {

    new ProtocolEventTest().createModelExample(1, vertx, testContext).onSuccess(event -> {

      event.delay = (long) 10000;
      testContext.assertComplete(this.createComponentProxy(vertx).sendEvent(event))
          .onSuccess(sent -> testContext.verify(() -> {

            assertThat(sent.id).isNotNull();

            testContext.assertComplete(this.createComponentProxy(vertx).deleteEvent(sent.id)).onSuccess(deleted -> {

              testContext.assertFailure(this.createComponentProxy(vertx).deleteEvent(sent.id))
                  .onFailure(error -> testContext.completeNow());

            });

          }));

    });

  }

}
