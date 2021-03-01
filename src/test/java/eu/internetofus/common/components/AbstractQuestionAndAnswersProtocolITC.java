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

import eu.internetofus.common.components.incentive_server.TaskStatus;
import eu.internetofus.common.components.incentive_server.TaskStatusPredicates;
import eu.internetofus.common.components.interaction_protocol_engine.State;
import eu.internetofus.common.components.interaction_protocol_engine.StatePredicates;
import eu.internetofus.common.components.interaction_protocol_engine.WeNetInteractionProtocolEngine;
import eu.internetofus.common.components.service.Message;
import eu.internetofus.common.components.service.MessagePredicates;
import eu.internetofus.common.components.task_manager.Task;
import eu.internetofus.common.components.task_manager.TaskPredicates;
import eu.internetofus.common.components.task_manager.TaskTransaction;
import eu.internetofus.common.components.task_manager.TaskTransactionPredicates;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
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
public abstract class AbstractQuestionAndAnswersProtocolITC extends AbstractProtocolITC {

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
    taskToCreate.attributes = new JsonObject().put("kindOfAnswerer", "anyone");
    return taskToCreate;

  }

  /**
   * Create a predicate to check the incentives state of an user.
   *
   * @param userId          identifier of the state.
   * @param questions       the number of questions that the incentive server is
   *                        informed that the user has done.
   * @param answers         the number of answers that the user has done.
   * @param answersAccepted the number of answers that has been accepted.
   *
   * @return the predicate to check the incentive state.
   */
  protected Predicate<State> createCommunityUserIncentivesState(final String userId, final long questions,
      final long answers, final long answersAccepted) {

    return this.createCommunityUserStatePredicate(userId).and(StatePredicates.attributesAre(attributes -> {

      final var incentives = attributes.getJsonObject("incentives");
      final var stateQuestions = incentives.getLong("Questions", 0l);
      final var stateAnswers = incentives.getLong("Answers", 0l);
      final var stateAnswersAccepted = incentives.getLong("AnswersAccepted", 0l);
      return questions == stateQuestions && answers == stateAnswers && answersAccepted == stateAnswersAccepted;

    }));
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
    for (final var user : this.users) {

      if (!user.id.equals(source.requesterId)) {

        checkMessages.add(this.createMessagePredicate().and(MessagePredicates.labelIs("QuestionToAnswerMessage"))
            .and(MessagePredicates.receiverIs(user.id))
            .and(MessagePredicates.attributesSimilarTo(
                new JsonObject().put("question", source.goal.name).put("userId", source.requesterId)))
            .and(MessagePredicates.attributesAre(target -> {

              return this.task.id.equals(target.getString("taskId"));

            })));
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

    final var checkStatus = new ArrayList<Predicate<TaskStatus>>();
    checkStatus.add(this.createTaskStatusPredicate().and(TaskStatusPredicates.actionIs("Questions 1"))
        .and(TaskStatusPredicates.userIs(source.requesterId)));

    final var checkState = this.createCommunityUserIncentivesState(source.requesterId, 1l, 0l, 0l);

    final var future = this.waitUntilTaskCreated(source, vertx, testContext, checkTask)
        .compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages))
        .compose(ignored -> this.waitUntilTaskStatus(vertx, testContext, checkStatus))
        .compose(ignored -> this.waitUntilCommunityUserState(vertx, testContext, source.requesterId, checkState));
    testContext.assertComplete(future).onComplete(stored -> this.assertSuccessfulCompleted(testContext));

  }

  /**
   * Check that an user answer.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(6)
  public void shouldAnswer(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(5, testContext);

    final var checkMessages = new ArrayList<Predicate<Message>>();
    Future<?> future = Future.succeededFuture();
    for (var i = 1; i < 4; i++) {

      final long index = i;
      final var transaction = new TaskTransaction();
      transaction.actioneerId = this.users.get(i).id;
      transaction.taskId = this.task.id;
      transaction.label = "answerTransaction";
      final var answer = "Response question " + i;
      transaction.attributes = new JsonObject().put("answer", answer);

      final var checkMessage = this.createMessagePredicate().and(MessagePredicates.labelIs("AnsweredQuestionMessage"))
          .and(MessagePredicates.receiverIs(this.task.requesterId))
          .and(MessagePredicates.attributesSimilarTo(new JsonObject().put("answer", answer).put("taskId", this.task.id)
              .put("userId", transaction.actioneerId)));
      checkMessages.add(checkMessage);

      final var checkTask = this.createTaskPredicate().and(TaskPredicates.transactionSizeIs(i + 1))
          .and(TaskPredicates.transactionAt(i,
              this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(transaction))
                  .and(TaskTransactionPredicates.messagesSizeIs(1))
                  .and(TaskTransactionPredicates.messageAt(0, checkMessage))
                  .and(source -> source.id.equals(source.messages.get(0).attributes.getString("transactionId")))));

      final var checkStatus = new ArrayList<Predicate<TaskStatus>>();
      checkStatus.add(this.createTaskStatusPredicate().and(TaskStatusPredicates.actionIs("Answers " + (i + 1)))
          .and(TaskStatusPredicates.userIs(transaction.actioneerId)));

      final var checkState = this.createCommunityUserIncentivesState(transaction.actioneerId, index, index + 1, index);

      final var newState = new State();
      newState.attributes = new JsonObject().put("incentives",
          new JsonObject().put("Questions", index).put("Answers", index).put("AnswersAccepted", index));
      future = future
          .compose(ignored -> WeNetInteractionProtocolEngine.createProxy(vertx)
              .mergeCommunityUserState(this.community.id, transaction.actioneerId, newState))
          .compose(ignored -> WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction))
          .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask))
          .compose(ignored -> this.waitUntilTaskStatus(vertx, testContext, checkStatus)).compose(
              ignored -> this.waitUntilCommunityUserState(vertx, testContext, transaction.actioneerId, checkState));

    }

    future = future.compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages));
    testContext.assertComplete(future).onComplete(ignored -> this.assertSuccessfulCompleted(testContext));

  }

  /**
   * Check that an user decline to answer.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(7)
  public void shouldNoAnswer(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(6, testContext);

    final var transaction = new TaskTransaction();
    transaction.actioneerId = this.users.get(this.users.size() - 1).id;
    transaction.taskId = this.task.id;
    transaction.label = "notAnswerTransaction";

    final var checkTask = this.createTaskPredicate().and(TaskPredicates.transactionSizeIs(5)).and(TaskPredicates
        .transactionAt(4, this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(transaction))));

    final var future = WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction)
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask));
    testContext.assertComplete(future).onComplete(ignored -> this.assertSuccessfulCompleted(testContext));

  }

  /**
   * Check that ask some more users.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(8)
  public void shouldAskSomeMoreUsers(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(7, testContext);

    final var transaction = new TaskTransaction();
    transaction.actioneerId = this.task.requesterId;
    transaction.taskId = this.task.id;
    transaction.label = "moreAnswerTransaction";

    final var checkTask = this.createTaskPredicate().and(TaskPredicates.transactionSizeIs(6)).and(TaskPredicates
        .transactionAt(5, this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(transaction))));

    final var future = WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction)
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask));
    testContext.assertComplete(future).onComplete(ignored -> this.assertSuccessfulCompleted(testContext));

  }

  /**
   * Check that report question.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(9)
  public void shouldReportQuestion(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(8, testContext);

    final var transaction = new TaskTransaction();
    transaction.actioneerId = this.users.get(4).id;
    transaction.taskId = this.task.id;
    transaction.label = "reportQuestionTransaction";
    transaction.attributes = new JsonObject().put("reason", "Reason msg").put("comment", "Comment msg");

    final var checkTask = this.createTaskPredicate().and(TaskPredicates.transactionSizeIs(7)).and(TaskPredicates
        .transactionAt(6, this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(transaction))));

    final var future = WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction)
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask));
    testContext.assertComplete(future).onComplete(ignored -> this.assertSuccessfulCompleted(testContext));

  }

  /**
   * Check that report answer.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(10)
  public void shouldReportAnswer(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(9, testContext);

    final var transaction = new TaskTransaction();
    transaction.actioneerId = this.task.requesterId;
    transaction.taskId = this.task.id;
    transaction.label = "reportAnswerTransaction";
    transaction.attributes = new JsonObject().put("transactionId", this.task.transactions.get(2).id)
        .put("reason", "Reason msg").put("comment", "Comment msg");

    final var checkTask = this.createTaskPredicate().and(TaskPredicates.transactionSizeIs(8)).and(TaskPredicates
        .transactionAt(7, this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(transaction))));

    final var future = WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction)
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask));
    testContext.assertComplete(future).onComplete(ignored -> this.assertSuccessfulCompleted(testContext));

  }

  /**
   * Check that pick the best answer.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(11)
  public void shouldBestAnswer(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(10, testContext);

    final var transaction = new TaskTransaction();
    transaction.actioneerId = this.task.requesterId;
    transaction.taskId = this.task.id;
    transaction.label = "bestAnswerTransaction";
    transaction.attributes = new JsonObject().put("transactionId", this.task.transactions.get(3).id).put("reason",
        "Reason msg");

    final var checkMessages = new ArrayList<Predicate<Message>>();
    final var checkMessage = this.createMessagePredicate().and(MessagePredicates.labelIs("AnsweredPickedMessage"))
        .and(MessagePredicates.receiverIs(this.task.transactions.get(3).actioneerId))
        .and(MessagePredicates.attributesSimilarTo(
            new JsonObject().put("taskId", this.task.id).put("transactionId", this.task.transactions.get(3).id)));
    checkMessages.add(checkMessage);

    final var checkTask = this.createTaskPredicate().and(TaskPredicates.isClosed())
        .and(TaskPredicates.transactionSizeIs(9))
        .and(TaskPredicates.transactionAt(8,
            this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(transaction))
                .and(TaskTransactionPredicates.messagesSizeIs(1))
                .and(TaskTransactionPredicates.messageAt(0, checkMessage))

        ));

    final var checkStatus = new ArrayList<Predicate<TaskStatus>>();
    checkStatus.add(this.createTaskStatusPredicate().and(TaskStatusPredicates.actionIs("AnswersAccepted 4"))
        .and(TaskStatusPredicates.userIs(this.task.transactions.get(3).actioneerId)));

    final var checkState = this.createCommunityUserIncentivesState(this.task.transactions.get(3).actioneerId, 3l, 4l,
        4l);

    final var future = WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction)
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask))
        .compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages))
        .compose(ignored -> this.waitUntilTaskStatus(vertx, testContext, checkStatus)).compose(ignored -> this
            .waitUntilCommunityUserState(vertx, testContext, this.task.transactions.get(3).actioneerId, checkState));
    testContext.assertComplete(future).onComplete(ignored -> this.assertSuccessfulCompleted(testContext));

  }

  /**
   * Check that create a second task.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(12)
  public void shouldCreateSecondTask(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(11, testContext);

    final var source = this.createTaskForProtocol();
    final var checkMessages = new ArrayList<Predicate<Message>>();
    for (final var user : this.users) {

      if (!user.id.equals(source.requesterId)) {

        checkMessages.add(this.createMessagePredicate().and(MessagePredicates.labelIs("QuestionToAnswerMessage"))
            .and(MessagePredicates.receiverIs(user.id))
            .and(MessagePredicates.attributesSimilarTo(
                new JsonObject().put("question", source.goal.name).put("userId", source.requesterId)))
            .and(MessagePredicates.attributesAre(target -> {

              return this.task.id.equals(target.getString("taskId"));

            })));
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

    final var checkStatus = new ArrayList<Predicate<TaskStatus>>();
    checkStatus.add(this.createTaskStatusPredicate().and(TaskStatusPredicates.actionIs("Questions 2"))
        .and(TaskStatusPredicates.userIs(source.requesterId)));

    final var checkState = this.createCommunityUserIncentivesState(source.requesterId, 2l, 0l, 0l);

    final var future = this.waitUntilTaskCreated(source, vertx, testContext, checkTask)
        .compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages))
        .compose(ignored -> this.waitUntilTaskStatus(vertx, testContext, checkStatus))
        .compose(ignored -> this.waitUntilCommunityUserState(vertx, testContext, source.requesterId, checkState));
    testContext.assertComplete(future).onComplete(stored -> this.assertSuccessfulCompleted(testContext));

  }

}
