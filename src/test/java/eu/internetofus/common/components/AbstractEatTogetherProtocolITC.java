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
import eu.internetofus.common.components.service.Message;
import eu.internetofus.common.components.service.MessagePredicates;
import eu.internetofus.common.components.task_manager.Task;
import eu.internetofus.common.components.task_manager.TaskPredicates;
import eu.internetofus.common.components.task_manager.TaskTransaction;
import eu.internetofus.common.components.task_manager.TaskTransactionPredicates;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
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

  /**
   * {@inheritDoc}
   */
  @Override
  protected Task createTaskForProtocol() {

    final var taskToCreate = super.createTaskForProtocol();
    final var deadlineTs = TimeManager.now() + 1200;
    final var startTs = deadlineTs + 30;
    final var endTs = startTs + 300;
    taskToCreate.attributes = new JsonObject().put("deadlineTs", deadlineTs).put("startTs", startTs).put("endTs",
        endTs);
    return taskToCreate;

  }

  /**
   * Check that a task is created.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Timeout(value = 1, timeUnit = TimeUnit.MINUTES)
  @Test
  @Order(5)
  public void shouldCreateTask(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(4, testContext);

    final var source = this.createTaskForProtocol();
    final var checkMessages = new ArrayList<Predicate<Message>>();
    final var userIds = new JsonArray();
    for (final var user : this.users) {

      if (!user.id.equals(source.requesterId)) {

        checkMessages
            .add(MessagePredicates.labelIs("TaskProposalNotification").and(MessagePredicates.receiverIs(user.id))
                .and(MessagePredicates.attributesSimilarTo(new JsonObject().put("communityId", this.community.id)))
                .and(MessagePredicates.attributesAre(target -> {

                  return this.task.id.equals(target.getString("taskId"));

                })));
        userIds.add(user.id);
      }

    }
    final var createTransaction = new TaskTransaction();
    createTransaction.label = "CREATE_TASK";
    createTransaction.actioneerId = this.users.get(0).id;
    final var checkTask = TaskPredicates.similarTo(source)
        .and(TaskPredicates.attributesSimilarTo(new JsonObject().put("unanswered", userIds)))
        .and(TaskPredicates.transactionSizeIs(1))
        .and(TaskPredicates.transactionAt(0,
            TaskTransactionPredicates.similarTo(createTransaction)
                .and(TaskTransactionPredicates.messagesSizeIs(this.users.size() - 1))
                .and(TaskTransactionPredicates.containsMessages(checkMessages))));

    final var future = this.waitUntilTaskCreated(source, vertx, testContext, checkTask)
        .compose(task -> this.waitUntilCallbacks(vertx, testContext, checkMessages));
    testContext.assertComplete(future).onComplete(stored -> this.assertSuccessfulCompleted(testContext));

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
    final var checkTask = TaskPredicates.transactionSizeIs(2)
        .and(TaskPredicates.attributesSimilarTo(new JsonObject()
            .put("unanswered", this.task.attributes.getJsonArray("unanswered").copy().remove(volunteerId))
            .put("declined", new JsonArray().add(volunteerId))))
        .and(TaskPredicates.transactionAt(1, TaskTransactionPredicates.similarTo(transaction)));
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

    final var checkTask = TaskPredicates.transactionSizeIs(2)
        .and(TaskPredicates.attributesAre(this.task.attributes.copy())
            .and(TaskPredicates.transactionAt(1, TaskTransactionPredicates.similarTo(transaction))));
    final var checkMessages = new ArrayList<Predicate<Message>>();
    checkMessages.add(MessagePredicates.labelIs("TextualMessage").and(MessagePredicates.receiverIs(volunteerId)));
    final var future = WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction)
        .compose(done -> this.waitUntilCallbacks(vertx, testContext, checkMessages))
        .compose(done -> this.waitUntilTask(vertx, testContext, checkTask));

    testContext.assertComplete(future).onComplete(removed -> this.assertSuccessfulCompleted(testContext));

  }

  /**
   * Check that an user is accept to be volunteer of a task.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Timeout(value = 1, timeUnit = TimeUnit.MINUTES)
  @Test
  @Order(8)
  public void shouldVolunteerForTask(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(7, testContext);

    final var checkMessages = new ArrayList<Predicate<Message>>();
    var checkTask = TaskPredicates.transactionSizeIs(this.users.size());

    final var volunteers = new JsonArray();
    Future<?> future = Future.succeededFuture();
    for (var i = 2; i < MAX_USERS - 1; i++) {

      final var volunteerId = this.users.get(i).id;
      volunteers.add(volunteerId);

      final var checkMessage = MessagePredicates.labelIs("TaskVolunteerNotification")
          .and(MessagePredicates.receiverIs(this.task.requesterId)).and(MessagePredicates.attributesAre(new JsonObject()
              .put("communityId", this.community.id).put("taskId", this.task.id).put("volunteerId", volunteerId)));
      checkMessages.add(checkMessage);

      final var taskTransaction = new TaskTransaction();
      taskTransaction.actioneerId = volunteerId;
      taskTransaction.taskId = this.task.id;
      taskTransaction.label = "volunteerForTask";
      taskTransaction.attributes = new JsonObject().put("volunteerId", volunteerId);

      future = future.compose(map -> WeNetTaskManager.createProxy(vertx).doTaskTransaction(taskTransaction));

      checkTask = checkTask.and(TaskPredicates.containsTransaction(TaskTransactionPredicates.similarTo(taskTransaction)
          .and(TaskTransactionPredicates.messagesSizeIs(1)).and(TaskTransactionPredicates.messageAt(0, checkMessage))));
    }

    final var finalCheckTask = TaskPredicates
        .attributesSimilarTo(new JsonObject().put("unanswered", new JsonArray()).put("volunteers", volunteers))
        .and(checkTask);
    future = future.compose(map -> this.waitUntilCallbacks(vertx, testContext, checkMessages))
        .compose(map -> this.waitUntilTask(vertx, testContext, finalCheckTask));

    testContext.assertComplete(future).onSuccess(empty -> this.assertSuccessfulCompleted(testContext));

  }

}
