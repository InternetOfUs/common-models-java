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
import eu.internetofus.common.components.service.MessagesPredicateBuilder;
import eu.internetofus.common.components.task_manager.Task;
import eu.internetofus.common.components.task_manager.TaskPredicateBuilder;
import eu.internetofus.common.components.task_manager.TaskTransaction;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Check the eat together protocol. ATTENTION: This test is sequential and
 * maintains the state between methods. In other words, you must to run the
 * entire test methods on the specified order to work.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractEatTogetherProtocolITC extends AbstractProtocolITC {

  @Override
  protected Task createTask() {

    final var task = super.createTask();
    final var deadlineTs = TimeManager.now() + 1200;
    final var startTs = deadlineTs + 30;
    final var endTs = startTs + 300;
    task.attributes = new JsonObject().put("deadlineTs", deadlineTs).put("startTs", startTs).put("endTs", endTs);
    return task;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Future<Task> waitUntilTaskCreated(final Vertx vertx, final VertxTestContext testContext) {

    final var mesageBuilder = new MessagesPredicateBuilder();
    for (final var user : this.users) {

      if (!user.id.equals(this.task.requesterId)) {

        mesageBuilder.withLabelReceiverIdAndAttributes("TaskProposalNotification", user.id,
            new JsonObject().put("taskId", this.task.id));

      }

    }
    final var checkMessages = mesageBuilder.build();
    final var createTransaction = new TaskTransaction();
    createTransaction.label = "CREATE_TASK";
    createTransaction.actioneerId = this.users.get(0).id;
    final var checkTask = new TaskPredicateBuilder().with(target -> {

      final var attributes = target.attributes;
      if (attributes == null) {

        return false;
      }
      final var unanswered = attributes.getJsonArray("unanswered");
      if (unanswered == null || unanswered.size() != this.users.size() - 1) {

        return false;

      }
      for (final var user : this.users) {

        if (!user.id.equals(this.task.requesterId)) {

          if (!unanswered.contains(user.id)) {

            return false;
          }

        }
      }

      return true;

    }).withTransactions(1).withSimilarTransactionWithMessages(createTransaction, checkMessages).build();
    return this.waitUntilCallbacks(vertx, testContext, checkMessages)
        .compose(messages -> this.waitUntilTask(vertx, testContext, checkTask));

  }

  /**
   * Check that an user decline to do a task.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(6)
  public void shouldDeclineTask(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(5, testContext);

    final var transaction = new TaskTransaction();
    final var volunteerId = this.users.get(1).id;
    transaction.actioneerId = volunteerId;
    transaction.taskId = this.task.id;
    transaction.label = "refuseTask";
    transaction.attributes = new JsonObject().put("volunteerId", volunteerId);
    final var checkTask = new TaskPredicateBuilder().with(target -> {

      final var attributes = target.attributes;
      if (attributes == null) {

        return false;
      }

      final var unanswered = attributes.getJsonArray("unanswered");
      final var declined = attributes.getJsonArray("declined");
      return unanswered != null && !unanswered.contains(volunteerId) && declined != null && declined.size() == 1
          && declined.contains(volunteerId);

    }).withTransactions(2).withSimilarTransaction(transaction).build();
    final var future = WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction)
        .compose(done -> this.waitUntilTask(vertx, testContext, checkTask));

    testContext.assertComplete(future).onComplete(removed -> this.assertSuccessfulCompleted(testContext));

  }

  /**
   * Check that an user can not be a volunteer if it has declined.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(7)
  public void shouldNotVolunteerForTaskAfterDecline(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(6, testContext);

    final var transaction = new TaskTransaction();
    transaction.taskId = this.task.id;
    transaction.label = "volunteerForTask";
    final var volunteerId = this.users.get(1).id;
    transaction.actioneerId = volunteerId;
    transaction.attributes = new JsonObject().put("volunteerId", volunteerId);
    final var future = WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction)
        .compose(done -> this.waitUntilCallbacks(vertx, testContext,
            new MessagesPredicateBuilder().withLabelAndReceiverId("TextualMessage", volunteerId).build()))
        .compose(done -> this.waitUntilTask(vertx, testContext, new TaskPredicateBuilder().with(target -> {

          final var attributes = target.attributes;
          final var unanswered = attributes.getJsonArray("unanswered");
          final var declined = attributes.getJsonArray("declined");
          final var volunteers = attributes.getJsonArray("volunteers");
          return this.task.transactions.size() == 2 && unanswered.size() == this.users.size() - 2
              && declined.size() == 1 && (volunteers == null || volunteers.size() == 0);

        }).build()));

    testContext.assertComplete(future).onComplete(removed -> this.assertSuccessfulCompleted(testContext));

  }

  /**
   * Check that an user is accept to be volunteer of a task.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(8)
  public void shouldVolunteerForTask(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(7, testContext);

    final var messagesPredicateBuilder = new MessagesPredicateBuilder();
    final var taskPredicateBuilder = new TaskPredicateBuilder().withTransactions(this.users.size()).with(target -> {

      final var attributes = target.attributes;
      if (attributes == null) {

        return false;
      }
      final var unanswered = attributes.getJsonArray("unanswered");
      if (unanswered == null || !unanswered.isEmpty()) {

        return false;
      }
      final var volunteers = attributes.getJsonArray("volunteers");
      if (volunteers == null || volunteers.size() != this.users.size() - 2) {

        return false;
      }
      for (var i = 2; i < this.users.size(); i++) {
        final var user = this.users.get(i);

        if (!volunteers.contains(user.id)) {

          return false;
        }
      }

      return true;

    });
    Future<?> future = Future.succeededFuture();
    for (var i = 2; i < MAX_USERS - 1; i++) {

      final var volunteerId = this.users.get(i).id;
      messagesPredicateBuilder.withLabelReceiverIdAndAttributes("TaskVolunteerNotification", this.task.requesterId,
          new JsonObject().put("volunteerId", volunteerId));

      final var taskTransaction = new TaskTransaction();
      taskTransaction.actioneerId = volunteerId;
      taskTransaction.taskId = this.task.id;
      taskTransaction.label = "volunteerForTask";
      taskTransaction.attributes = new JsonObject().put("volunteerId", volunteerId);

      future = future.compose(map -> WeNetTaskManager.createProxy(vertx).doTaskTransaction(taskTransaction));

      taskPredicateBuilder.withSimilarTransactionWithMessages(taskTransaction,
          new MessagesPredicateBuilder().withLabelReceiverIdAndAttributes("TaskVolunteerNotification",
              this.task.requesterId, new JsonObject().put("volunteerId", volunteerId)).build());
    }

    future = future.compose(map -> this.waitUntilCallbacks(vertx, testContext, messagesPredicateBuilder.build()))
        .compose(map -> this.waitUntilTask(vertx, testContext, taskPredicateBuilder.build()));

    testContext.assertComplete(future).onSuccess(empty -> this.assertSuccessfulCompleted(testContext));

  }

}
