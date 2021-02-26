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
import eu.internetofus.common.components.incentive_server.TaskStatus;
import eu.internetofus.common.components.incentive_server.TaskStatusPredicates;
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
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.function.Predicate;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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
  @Test
  @Order(5)
  public void shouldCreateTask(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(4, testContext);

    final var source = this.createTaskForProtocol();
    final var checkMessages = new ArrayList<Predicate<Message>>();

    final var userIds = new JsonArray();
    for (final var user : this.users) {

      if (!user.id.equals(source.requesterId)) {

        checkMessages.add(this.createMessagePredicate().and(MessagePredicates.labelIs("TaskProposalNotification"))
            .and(MessagePredicates.receiverIs(user.id))
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
    final var checkTask = this.createTaskPredicate().and(TaskPredicates.similarTo(source))
        .and(TaskPredicates.attributesSimilarTo(new JsonObject().put("unanswered", userIds)))
        .and(TaskPredicates.transactionSizeIs(1))
        .and(TaskPredicates.transactionAt(0,
            this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(createTransaction))
                .and(TaskTransactionPredicates.messagesSizeIs(this.users.size() - 1))
                .and(TaskTransactionPredicates.containsMessages(checkMessages))));

    final var checkStatus = new ArrayList<Predicate<TaskStatus>>();
    checkStatus.add(this.createTaskStatusPredicate().and(TaskStatusPredicates.actionIs("taskCreated"))
        .and(TaskStatusPredicates.userIs(source.requesterId)));

    final var future = this.waitUntilTaskCreated(source, vertx, testContext, checkTask)
        .compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages))
        .compose(ignored -> this.waitUntilTaskStatus(vertx, testContext, checkStatus));
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
    final var unanswered = this.task.attributes.getJsonArray("unanswered").copy();
    unanswered.remove(volunteerId);
    final var checkTask = this.createTaskPredicate().and(TaskPredicates.transactionSizeIs(2))
        .and(TaskPredicates.attributesSimilarTo(
            new JsonObject().put("unanswered", unanswered).put("declined", new JsonArray().add(volunteerId))))
        .and(TaskPredicates.transactionAt(1,
            this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(transaction))));

    final var checkStatus = new ArrayList<Predicate<TaskStatus>>();
    checkStatus.add(this.createTaskStatusPredicate().and(TaskStatusPredicates.actionIs("refuseTask"))
        .and(TaskStatusPredicates.userIs(volunteerId)));

    final var future = WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction)
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask))
        .compose(ignored -> this.waitUntilTaskStatus(vertx, testContext, checkStatus));

    testContext.assertComplete(future).onComplete(removed -> this.assertSuccessfulCompleted(testContext));

  }

  /**
   * Assert that can not do a task transaction on the eat together protocol and
   * receive an error message.
   *
   * @param taskTransaction that can not be done in the protocol.
   * @param vertx           event bus to use.
   * @param testContext     context to do the test.
   */
  protected void assertDoTransactionError(final TaskTransaction taskTransaction, final Vertx vertx,
      final VertxTestContext testContext) {

    final var checkTask = this.createTaskPredicate()
        .and(TaskPredicates.transactionSizeIs(this.task.transactions.size()))
        .and(TaskPredicates.attributesAre(this.task.attributes.copy()));
    final var checkMessages = new ArrayList<Predicate<Message>>();
    checkMessages.add(this.createMessagePredicate().and(MessagePredicates.labelIs("TextualMessage"))
        .and(MessagePredicates.receiverIs(taskTransaction.actioneerId)));
    final var future = WeNetTaskManager.createProxy(vertx).doTaskTransaction(taskTransaction)
        .compose(done -> this.waitUntilCallbacks(vertx, testContext, checkMessages))
        .compose(done -> this.waitUntilTask(vertx, testContext, checkTask));
    testContext.assertComplete(future).onSuccess(empty -> this.assertSuccessfulCompleted(testContext));

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

    this.assertDoTransactionError(transaction, vertx, testContext);

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

    final var checkMessages = new ArrayList<Predicate<Message>>();
    final var checkStatus = new ArrayList<Predicate<TaskStatus>>();

    final var unanswered = this.task.attributes.getJsonArray("unanswered").copy();
    final var volunteers = new JsonArray();
    Future<?> future = Future.succeededFuture();
    for (var i = 2; i < this.users.size() - 1; i++) {

      final var volunteerId = this.users.get(i).id;
      unanswered.remove(volunteerId);
      volunteers.add(volunteerId);

      final var checkMessage = this.createMessagePredicate().and(MessagePredicates.labelIs("TaskVolunteerNotification"))
          .and(MessagePredicates.receiverIs(this.task.requesterId)).and(MessagePredicates.attributesAre(new JsonObject()
              .put("communityId", this.community.id).put("taskId", this.task.id).put("volunteerId", volunteerId)));
      checkMessages.add(checkMessage);

      checkStatus.add(this.createTaskStatusPredicate().and(TaskStatusPredicates.actionIs("volunteerForTask"))
          .and(TaskStatusPredicates.userIs(volunteerId)));

      final var taskTransaction = new TaskTransaction();
      taskTransaction.actioneerId = volunteerId;
      taskTransaction.taskId = this.task.id;
      taskTransaction.label = "volunteerForTask";
      taskTransaction.attributes = new JsonObject().put("volunteerId", volunteerId);

      final var checkTask = this.createTaskPredicate()
          .and(TaskPredicates.attributesSimilarTo(
              new JsonObject().put("unanswered", unanswered.copy()).put("volunteers", volunteers.copy())))
          .and(TaskPredicates.transactionSizeIs(i + 1))
          .and(TaskPredicates.transactionAt(i,
              this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(taskTransaction))
                  .and(TaskTransactionPredicates.messagesSizeIs(1))
                  .and(TaskTransactionPredicates.messageAt(0, checkMessage))));
      future = future.compose(map -> WeNetTaskManager.createProxy(vertx).doTaskTransaction(taskTransaction))
          .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask));

    }

    future = future.compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages))
        .compose(ignored -> this.waitUntilTaskStatus(vertx, testContext, checkStatus));

    testContext.assertComplete(future).onSuccess(empty -> this.assertSuccessfulCompleted(testContext));

  }

  /**
   * Check that an user can not decline a task if it accepted to be volunteer.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(9)
  public void shouldNotDeclineAfterVolunteerForTask(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(8, testContext);

    final var taskTransaction = new TaskTransaction();
    taskTransaction.taskId = this.task.id;
    taskTransaction.label = "refuseTask";
    final var volunteerId = this.users.get(2).id;
    taskTransaction.actioneerId = volunteerId;
    taskTransaction.attributes = new JsonObject().put("volunteerId", volunteerId);

    this.assertDoTransactionError(taskTransaction, vertx, testContext);
  }

  /**
   * Check that an user can be accepted to be volunteer.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(10)
  public void shouldAcceptVolunteer(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(9, testContext);

    final var taskTransaction = new TaskTransaction();
    taskTransaction.actioneerId = this.task.requesterId;
    taskTransaction.taskId = this.task.id;
    taskTransaction.label = "acceptVolunteer";
    final var volunteerId = this.users.get(2).id;
    taskTransaction.attributes = new JsonObject().put("volunteerId", volunteerId);

    final var attributes = this.task.attributes.copy();
    attributes.getJsonArray("volunteers").remove(volunteerId);
    attributes.put("accepted", new JsonArray().add(volunteerId));
    final var checkMessage = this.createMessagePredicate().and(MessagePredicates.labelIs("TaskSelectionNotification"))
        .and(MessagePredicates.receiverIs(volunteerId)).and(MessagePredicates.attributesAre(new JsonObject()
            .put("communityId", this.community.id).put("taskId", this.task.id).put("outcome", "accepted")));
    final var checkMessages = new ArrayList<Predicate<Message>>();
    checkMessages.add(checkMessage);
    final var checkTask = this.createTaskPredicate()
        .and(TaskPredicates.transactionSizeIs(this.task.transactions.size() + 1))
        .and(TaskPredicates.attributesAre(attributes))
        .and(TaskPredicates.transactionAt(this.task.transactions.size(),
            this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(taskTransaction))
                .and(TaskTransactionPredicates.messagesSizeIs(1))
                .and(TaskTransactionPredicates.messageAt(0, checkMessage))));

    final var checkStatus = new ArrayList<Predicate<TaskStatus>>();
    checkStatus.add(this.createTaskStatusPredicate().and(TaskStatusPredicates.actionIs("acceptVolunteer"))
        .and(TaskStatusPredicates.userIs(volunteerId)));

    final var future = WeNetTaskManager.createProxy(vertx).doTaskTransaction(taskTransaction)
        .compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages))
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask))
        .compose(ignored -> this.waitUntilTaskStatus(vertx, testContext, checkStatus));
    testContext.assertComplete(future).onSuccess(empty -> this.assertSuccessfulCompleted(testContext));

  }

  /**
   * Check that an user can not be accepted as volunteer.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(11)
  public void shouldNotAcceptVolunteer(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(10, testContext);

    final var taskTransaction = new TaskTransaction();
    taskTransaction.actioneerId = this.task.requesterId;
    taskTransaction.taskId = this.task.id;
    taskTransaction.label = "acceptVolunteer";
    final var volunteerId = this.users.get(1).id;
    taskTransaction.attributes = new JsonObject().put("volunteerId", volunteerId);

    this.assertDoTransactionError(taskTransaction, vertx, testContext);
  }

  /**
   * Check that an user can be refused to be volunteer.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(12)
  public void shouldRefuseVolunteer(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(11, testContext);

    final var taskTransaction = new TaskTransaction();
    taskTransaction.actioneerId = this.task.requesterId;
    taskTransaction.taskId = this.task.id;
    taskTransaction.label = "refuseVolunteer";
    final var volunteerId = this.users.get(3).id;
    taskTransaction.attributes = new JsonObject().put("volunteerId", volunteerId);

    final var attributes = this.task.attributes.copy();
    attributes.getJsonArray("volunteers").remove(volunteerId);
    attributes.put("refused", new JsonArray().add(volunteerId));
    final var checkMessage = this.createMessagePredicate().and(MessagePredicates.labelIs("TaskSelectionNotification"))
        .and(MessagePredicates.receiverIs(volunteerId)).and(MessagePredicates.attributesAre(new JsonObject()
            .put("communityId", this.community.id).put("taskId", this.task.id).put("outcome", "refused")));
    final var checkMessages = new ArrayList<Predicate<Message>>();
    checkMessages.add(checkMessage);
    final var checkTask = this.createTaskPredicate()
        .and(TaskPredicates.transactionSizeIs(this.task.transactions.size() + 1))
        .and(TaskPredicates.attributesAre(attributes))
        .and(TaskPredicates.transactionAt(this.task.transactions.size(),
            this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(taskTransaction))
                .and(TaskTransactionPredicates.messagesSizeIs(1))
                .and(TaskTransactionPredicates.messageAt(0, checkMessage))));

    final var checkStatus = new ArrayList<Predicate<TaskStatus>>();
    checkStatus.add(this.createTaskStatusPredicate().and(TaskStatusPredicates.actionIs("refuseVolunteer"))
        .and(TaskStatusPredicates.userIs(volunteerId)));

    final var future = WeNetTaskManager.createProxy(vertx).doTaskTransaction(taskTransaction)
        .compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages))
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask))
        .compose(ignored -> this.waitUntilTaskStatus(vertx, testContext, checkStatus));
    testContext.assertComplete(future).onSuccess(empty -> this.assertSuccessfulCompleted(testContext));

  }

  /**
   * Check that an user can not be refused as volunteer.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(13)
  public void shouldNotRefuseVolunteer(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(12, testContext);

    final var taskTransaction = new TaskTransaction();
    taskTransaction.actioneerId = this.task.requesterId;
    taskTransaction.taskId = this.task.id;
    taskTransaction.label = "refuseVolunteer";
    final var volunteerId = this.users.get(1).id;
    taskTransaction.attributes = new JsonObject().put("volunteerId", volunteerId);

    this.assertDoTransactionError(taskTransaction, vertx, testContext);
  }

  /**
   * Check that an user can mark the task as completed.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(14)
  public void shouldTaskCompleted(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(13, testContext);

    final var taskTransaction = new TaskTransaction();
    taskTransaction.actioneerId = this.task.requesterId;
    taskTransaction.taskId = this.task.id;
    taskTransaction.label = "taskCompleted";
    final var outcome = "completed";
    taskTransaction.attributes = new JsonObject().put("outcome", outcome);

    final var checkMessages = new ArrayList<Predicate<Message>>();
    for (final var id : new String[] { this.users.get(2).id, this.users.get(4).id, this.users.get(5).id }) {

      checkMessages.add(this.createMessagePredicate().and(MessagePredicates.labelIs("TaskConcludedNotification"))
          .and(MessagePredicates.receiverIs(id)).and(MessagePredicates.attributesAre(new JsonObject()
              .put("communityId", this.community.id).put("taskId", this.task.id).put("outcome", outcome))));

    }
    final var checkTask = this.createTaskPredicate().and(TaskPredicates.isClosed())
        .and(TaskPredicates.attributesSimilarTo(
            new JsonObject().put("outcome", outcome).put("declined", new JsonArray().add(this.users.get(1).id))
                .put("accepted", new JsonArray().add(this.users.get(2).id))
                .put("refused", new JsonArray().add(this.users.get(3).id))
                .put("volunteers", new JsonArray().add(this.users.get(4).id))
                .put("unanswered", new JsonArray().add(this.users.get(5).id))))
        .and(TaskPredicates.transactionSizeIs(this.task.transactions.size() + 1))
        .and(TaskPredicates.transactionAt(this.task.transactions.size(),
            this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(taskTransaction))
                .and(TaskTransactionPredicates.messagesSizeIs(checkMessages.size()))
                .and(TaskTransactionPredicates.containsMessages(checkMessages))));

    final var checkStatus = new ArrayList<Predicate<TaskStatus>>();
    checkStatus.add(this.createTaskStatusPredicate().and(TaskStatusPredicates.actionIs("taskCompleted"))
        .and(TaskStatusPredicates.userIs(this.task.requesterId)));

    final var future = WeNetTaskManager.createProxy(vertx).doTaskTransaction(taskTransaction)
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask))
        .compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages))
        .compose(ignored -> this.waitUntilTaskStatus(vertx, testContext, checkStatus));

    testContext.assertComplete(future).onSuccess(empty -> this.assertSuccessfulCompleted(testContext));

  }

  /**
   * Check that an user can not complete a closed task.
   *
   * @param outcome     that can not be set to a closed task.
   * @param order       of the parameterized test.
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @ParameterizedTest(name = "Should not complete a task with the outcome {0}")
  @CsvSource(value = { "cancelled:0", "completed:1", "failed:2" }, delimiter = ':')
  @Order(15)
  public void shouldNotChangeCompletedAClosedTask(final String outcome, final String order, final Vertx vertx,
      final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(14 + Integer.parseInt(order), testContext);

    final var taskTransaction = new TaskTransaction();
    taskTransaction.actioneerId = this.task.requesterId;
    taskTransaction.taskId = this.task.id;
    taskTransaction.label = "taskCompleted";
    taskTransaction.attributes = new JsonObject().put("outcome", outcome);

    this.assertDoTransactionError(taskTransaction, vertx, testContext);
  }

  /**
   * Check that can not modify the volunteers of a closed task.
   *
   * @param label       of the task transaction that can not be done.
   * @param order       of the parameterized test.
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @ParameterizedTest(name = "Should not do the task transaction {0}")
  @CsvSource(value = { "volunteerForTask:0", "refuseTask:1", "acceptVolunteer:2",
      "refuseVolunteer:3" }, delimiter = ':')
  @Order(18)
  public void shouldNotChangeVolunteerStatesWhenTaskIsClosed(final String label, final String order, final Vertx vertx,
      final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(17 + Integer.parseInt(order), testContext);

    final var taskTransaction = new TaskTransaction();
    taskTransaction.actioneerId = this.task.requesterId;
    taskTransaction.taskId = this.task.id;
    taskTransaction.label = label;
    final var volunteerId = this.users.get(5).id;
    taskTransaction.attributes = new JsonObject().put("volunteerId", volunteerId);

    this.assertDoTransactionError(taskTransaction, vertx, testContext);
  }

  /**
   * Check that can not create a task without deadline.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(22)
  public void shouldNotCreateTaskWithWithoutDeadline(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(21, testContext);

    final var task = this.createTaskForProtocol();
    task.attributes.remove("deadlineTs");

    final var checkMessages = new ArrayList<Predicate<Message>>();
    checkMessages.add(this.createMessagePredicate().and(MessagePredicates.labelIs("TextualMessage"))
        .and(MessagePredicates.receiverIs(task.requesterId)));
    final var future = WeNetTaskManager.createProxy(vertx).createTask(task)
        .compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages));

    testContext.assertComplete(future).onSuccess(empty -> this.assertSuccessfulCompleted(testContext));

  }

  /**
   * Check that can not create a task with bad deadline.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(23)
  public void shouldNotCreateTaskWithWithBadDeadline(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(22, testContext);

    final var task = this.createTaskForProtocol();
    task.attributes.put("deadlineTs", TimeManager.now());

    final var checkMessages = new ArrayList<Predicate<Message>>();
    checkMessages.add(this.createMessagePredicate().and(MessagePredicates.labelIs("TextualMessage"))
        .and(MessagePredicates.receiverIs(task.requesterId)));
    final var future = WeNetTaskManager.createProxy(vertx).createTask(task)
        .compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages));

    testContext.assertComplete(future).onSuccess(empty -> this.assertSuccessfulCompleted(testContext));

  }

  /**
   * Check that a task is created.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(24)
  public void shouldCreateTaskWithShortDeadline(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(23, testContext);

    final var source = this.createTaskForProtocol();
    source.attributes.put("deadlineTs", TimeManager.now() + 10);
    final var checkMessages = new ArrayList<Predicate<Message>>();
    final var userIds = new JsonArray();
    for (final var user : this.users) {

      if (!user.id.equals(source.requesterId)) {

        checkMessages.add(this.createMessagePredicate().and(MessagePredicates.labelIs("TaskProposalNotification"))
            .and(MessagePredicates.receiverIs(user.id))
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
    final var checkTask = this.createTaskPredicate().and(TaskPredicates.similarTo(source))
        .and(TaskPredicates.attributesSimilarTo(new JsonObject().put("unanswered", userIds)))
        .and(TaskPredicates.transactionSizeIs(1))
        .and(TaskPredicates.transactionAt(0,
            this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(createTransaction))
                .and(TaskTransactionPredicates.messagesSizeIs(this.users.size() - 1))
                .and(TaskTransactionPredicates.containsMessages(checkMessages))))
        .and(task -> task.attributes.getLong("deadlineTs") < TimeManager.now());

    final var checkStatus = new ArrayList<Predicate<TaskStatus>>();
    checkStatus.add(this.createTaskStatusPredicate().and(TaskStatusPredicates.actionIs("taskCreated"))
        .and(TaskStatusPredicates.userIs(source.requesterId)));

    final var future = this.waitUntilTaskCreated(source, vertx, testContext, checkTask)
        .compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages))
        .compose(ignored -> this.waitUntilTaskStatus(vertx, testContext, checkStatus));

    testContext.assertComplete(future).onComplete(stored -> this.assertSuccessfulCompleted(testContext));

  }

  /**
   * Check that not do a transaction if the deadline has reached.
   *
   * @param label       of the task transaction that can not be done after
   *                    deadline has reached.
   * @param order       of the parameterized test.
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @ParameterizedTest(name = "Should not do the task transaction {0} because deadline is reached")
  @CsvSource(value = { "volunteerForTask:0", "refuseTask:1", "acceptVolunteer:2",
      "refuseVolunteer:3" }, delimiter = ':')
  @Order(25)
  public void shouldNotDoTranasctionAfterDeadline(final String label, final String order, final Vertx vertx,
      final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(24 + Integer.parseInt(order), testContext);

    final var taskTransaction = new TaskTransaction();
    taskTransaction.actioneerId = this.users.get(2).id;
    taskTransaction.taskId = this.task.id;
    taskTransaction.label = label;
    final var volunteerId = this.users.get(0).id;
    taskTransaction.attributes = new JsonObject().put("volunteerId", volunteerId);

    this.assertDoTransactionError(taskTransaction, vertx, testContext);
  }

}
