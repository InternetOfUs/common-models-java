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

package eu.internetofus.common.components;

import eu.internetofus.common.TimeManager;
import eu.internetofus.common.components.interaction_protocol_engine.StatePredicates;
import eu.internetofus.common.components.service.Message;
import eu.internetofus.common.components.service.MessagePredicates;
import eu.internetofus.common.components.task_manager.Task;
import eu.internetofus.common.components.task_manager.TaskPredicates;
import eu.internetofus.common.components.task_manager.TaskTransaction;
import eu.internetofus.common.components.task_manager.TaskTransactionPredicates;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.function.Predicate;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Check the English auction protocol with norms. ATTENTION: This test is
 * sequential and maintains the state between methods. In other words, you must
 * to run the entire test methods on the specified order to work.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class EnglishAuctionWithNormsProtocolITC extends AbstractProtocolITC {

  /**
   * {@inheritDoc}
   *
   * @return {@link WeNetTaskManager#ENGLISH_AUCTION_WITH_NORMS_V1_TASK_TYPE_ID}
   */
  @Override
  protected String getDefaultTaskTypeIdToUse() {

    return WeNetTaskManager.ENGLISH_AUCTION_WITH_NORMS_V1_TASK_TYPE_ID;
  }

  /**
   * {@inheritDoc}
   *
   * @return {@code 6} in any case.
   */
  @Override
  protected int numberOfUsersToCreate() {

    return 6;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Task createTaskForProtocol() {

    final var taskToCreate = super.createTaskForProtocol();
    final var startTime = TimeManager.now() + 10;
    taskToCreate.attributes = new JsonObject().put("quorum", 2).put("startPrice", 20).put("whom", "any")
        .put("startTime", startTime).put("offerDelay", 3);
    return taskToCreate;

  }

  /**
   * Check that can not create a task with a bad start time.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(5)
  public void shouldNotCreateTaskWithBadStartTime(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(4, testContext);

    final var source = this.createTaskForProtocol();
    source.attributes.put("startTime", TimeManager.now());
    final var checkMessages = new ArrayList<Predicate<Message>>();
    checkMessages.add(this.createMessagePredicate().and(MessagePredicates.labelIs("Error"))
        .and(MessagePredicates.receiverIs(source.requesterId)).and(MessagePredicates.attributesAre(target -> {

          return "cannot_create_task_with_bad_startTime".equals(target.getString("code"))
              && this.task.id.equals(target.getString("taskId"));

        })));

    final var checkTask = this.createTaskPredicate().and(TaskPredicates.isClosed());

    final var future = this.waitUntilTaskCreated(source, vertx, testContext, checkTask)
        .compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages));
    testContext.assertComplete(future).onComplete(stored -> this.assertSuccessfulCompleted(testContext));

  }

  /**
   * Check that can not create a task because can not found users.
   *
   * @param whom        type of user sthat not found.
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @ParameterizedTest(name = "Should not create task because not found users of type {0}")
  @ValueSource(strings = { "closest", "neighbor", "villager", "citizen" })
  @Order(6)
  public void shouldNotCreateTaskBecauseNoFoundUsers(final String whom, final Vertx vertx,
      final VertxTestContext testContext) {

    this.assertAtLeastSuccessfulTestWas(4, testContext);

    final var source = this.createTaskForProtocol();
    source.attributes.put("whom", whom);
    final var checkMessages = new ArrayList<Predicate<Message>>();
    checkMessages.add(this.createMessagePredicate().and(MessagePredicates.labelIs("Error"))
        .and(MessagePredicates.receiverIs(source.requesterId)).and(MessagePredicates.attributesAre(target -> {

          return "cannot_create_task_no_found_quorum".equals(target.getString("code"))
              && this.task.id.equals(target.getString("taskId"));

        })));

    final var checkTask = this.createTaskPredicate().and(TaskPredicates.isClosed());

    final var future = this.waitUntilTaskCreated(source, vertx, testContext, checkTask)
        .compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages));
    testContext.assertComplete(future).onComplete(stored -> this.assertSuccessfulCompleted(testContext));

  }

  /**
   * Check that can create a task.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(10)
  public void shouldCreateTask(final Vertx vertx, final VertxTestContext testContext) {

    this.assertAtLeastSuccessfulTestWas(4, testContext);

    final var source = this.createTaskForProtocol();
    final var checkMessages = new ArrayList<Predicate<Message>>();
    final var unanswered = new JsonArray();
    final var userIds = new JsonArray();
    for (final var user : this.users) {

      if (!user.id.equals(source.requesterId)) {

        checkMessages.add(this.createMessagePredicate().and(MessagePredicates.labelIs("NewAuction"))
            .and(MessagePredicates.receiverIs(user.id)).and(MessagePredicates.attributesAre(target -> {

              return this.task.id.equals(target.getString("taskId"));

            })));
        userIds.add(user.id);
        unanswered.add(user.id);
      }

    }
    final var createTransaction = new TaskTransaction();
    createTransaction.label = "CREATE_TASK";
    createTransaction.actioneerId = source.requesterId;
    final var checkTask = this.createTaskPredicate().and(TaskPredicates.similarTo(source))
        .and(TaskPredicates.transactionSizeIs(1))
        .and(TaskPredicates.transactionAt(0,
            this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(createTransaction))
                .and(TaskTransactionPredicates.messagesSizeIs(this.users.size() - 1))
                .and(TaskTransactionPredicates.containsMessages(checkMessages))));

    final var checkState = this.createCommunityUserStatePredicate(source.requesterId)
        .and(StatePredicates.attributesAre(new JsonObject().put("unanswered", unanswered)));

    final var future = this.waitUntilTaskCreated(source, vertx, testContext, checkTask)
        .compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages))
        .compose(ignored -> this.waitUntilCommunityUserState(vertx, testContext, source.requesterId, checkState));
    testContext.assertComplete(future).onComplete(stored -> this.assertSuccessfulCompleted(testContext));

  }
}