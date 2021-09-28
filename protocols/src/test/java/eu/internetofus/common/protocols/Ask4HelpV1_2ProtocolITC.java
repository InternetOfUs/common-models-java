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
package eu.internetofus.common.protocols;

import eu.internetofus.common.components.incentive_server.TaskTransactionStatusBody;
import eu.internetofus.common.components.incentive_server.TaskTransactionStatusBodyPredicates;
import eu.internetofus.common.components.incentive_server.TaskTypeStatusBody;
import eu.internetofus.common.components.incentive_server.TaskTypeStatusBodyPredicates;
import eu.internetofus.common.components.models.Message;
import eu.internetofus.common.components.models.Task;
import eu.internetofus.common.components.models.TaskTransaction;
import eu.internetofus.common.components.service.MessagePredicates;
import eu.internetofus.common.components.social_context_builder.WeNetSocialContextBuilderSimulator;
import eu.internetofus.common.components.task_manager.TaskPredicates;
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

/**
 * Check the {@link DefaultProtocols#ASK_4_HELP_V2} protocol. ATTENTION: This
 * test is sequential and maintains the state between methods. In other words,
 * you must to run the entire test methods on the specified order to work.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class Ask4HelpV1_2ProtocolITC extends AbstractProtocolITC {

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
   *
   * @return {@link DefaultProtocols#ASK_4_HELP_V1_2}
   */
  @Override
  protected DefaultProtocols getDefaultProtocolsToUse() {

    return DefaultProtocols.ASK_4_HELP_V1_2;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Task createTaskForProtocol() {

    final var taskToCreate = super.createTaskForProtocol();
    taskToCreate.attributes = new JsonObject().put("kindOfAnswerer", "ask_to_anyone").put("answeredDetails", "None");
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
    final var checkIncentiveTypeStatus = new ArrayList<Predicate<TaskTypeStatusBody>>();
    final var checkIncentiveTransactionStatus = new ArrayList<Predicate<TaskTransactionStatusBody>>();

    for (final var user : this.users) {

      if (!user.id.equals(source.requesterId)) {

        checkMessages.add(this.createMessagePredicate().and(MessagePredicates.labelIs("QuestionToAnswerMessage"))
            .and(MessagePredicates.receiverIs(user.id))
            .and(MessagePredicates.attributesSimilarTo(
                new JsonObject().put("question", source.goal.name).put("userId", source.requesterId)))
            .and(MessagePredicates.attributesAre(target -> {

              return this.task.id.equals(target.getString("taskId"));

            })));

        checkIncentiveTransactionStatus.add(this.createIncentiveServerTaskTransactionStatusPredicate()
            .and(TaskTransactionStatusBodyPredicates.labelIs("QuestionToAnswerMessage"))
            .and(TaskTransactionStatusBodyPredicates.userIs(user.id))
            .and(TaskTransactionStatusBodyPredicates.countIs(1)));

      } else {

        checkIncentiveTypeStatus.add(this.createIncentiveServerTaskTypeStatusPredicate()
            .and(TaskTypeStatusBodyPredicates.userIs(user.id)).and(TaskTypeStatusBodyPredicates.countIs(1)));

      }
    }
    final var createTransaction = new TaskTransaction();
    createTransaction.label = TaskTransaction.CREATE_TASK_LABEL;
    createTransaction.actioneerId = source.requesterId;
    final var checkTask = this.createTaskPredicate().and(TaskPredicates.similarTo(source))
        .and(TaskPredicates.transactionSizeIs(1))
        .and(TaskPredicates.transactionAt(0,
            this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(createTransaction))
                .and(TaskTransactionPredicates.messagesSizeIs(this.users.size() - 1))
                .and(TaskTransactionPredicates.containsMessages(checkMessages))));

    final Future<?> future = this.waitUntilTaskCreated(source, vertx, testContext, checkTask)
        .compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages))
        .compose(
            ignored -> this.waitUntilIncentiveServerHasTaskTypeStatus(vertx, testContext, checkIncentiveTypeStatus))
        .compose(ignored -> this.waitUntilIncentiveServerHasTaskTransactionStatus(vertx, testContext,
            checkIncentiveTransactionStatus));
    future.onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

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
    final var checkStatus = new ArrayList<Predicate<TaskTransactionStatusBody>>();
    Future<?> future = Future.succeededFuture();
    for (var i = 1; i < 4; i++) {

      final var index = i;
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

      checkStatus.add(this.createIncentiveServerTaskTransactionStatusPredicate()
          .and(TaskTransactionStatusBodyPredicates.userIs(transaction.actioneerId))
          .and(TaskTransactionStatusBodyPredicates.labelIs(transaction.label))
          .and(TaskTransactionStatusBodyPredicates.countIs(1)));
      checkStatus.add(this.createIncentiveServerTaskTransactionStatusPredicate()
          .and(TaskTransactionStatusBodyPredicates.userIs(this.task.requesterId))
          .and(TaskTransactionStatusBodyPredicates.labelIs("AnsweredQuestionMessage"))
          .and(TaskTransactionStatusBodyPredicates.countIs(index)));

      future = future.compose(ignored -> WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction))
          .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask));

    }

    future = future.compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages))
        .compose(ignored -> this.waitUntilIncentiveServerHasTaskTransactionStatus(vertx, testContext, checkStatus));
    future.onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

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
    final var checkStatus = new ArrayList<Predicate<TaskTransactionStatusBody>>();
    checkStatus.add(this.createIncentiveServerTaskTransactionStatusPredicate()
        .and(TaskTransactionStatusBodyPredicates.labelIs(transaction.label))
        .and(TaskTransactionStatusBodyPredicates.countIs(1))
        .and(TaskTransactionStatusBodyPredicates.userIs(transaction.actioneerId)));

    final var checkTask = this.createTaskPredicate().and(TaskPredicates.transactionSizeIs(5)).and(TaskPredicates
        .transactionAt(4, this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(transaction))));

    final var future = WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction)
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask))
        .compose(ignored -> this.waitUntilIncentiveServerHasTaskTransactionStatus(vertx, testContext, checkStatus));
    future.onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

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
    final var checkStatus = new ArrayList<Predicate<TaskTransactionStatusBody>>();
    checkStatus.add(this.createIncentiveServerTaskTransactionStatusPredicate()
        .and(TaskTransactionStatusBodyPredicates.labelIs(transaction.label))
        .and(TaskTransactionStatusBodyPredicates.countIs(1))
        .and(TaskTransactionStatusBodyPredicates.userIs(transaction.actioneerId)));

    final var future = WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction)
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask))
        .compose(ignored -> this.waitUntilIncentiveServerHasTaskTransactionStatus(vertx, testContext, checkStatus));
    future.onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

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
    final var checkStatus = new ArrayList<Predicate<TaskTransactionStatusBody>>();
    checkStatus.add(this.createIncentiveServerTaskTransactionStatusPredicate()
        .and(TaskTransactionStatusBodyPredicates.labelIs(transaction.label))
        .and(TaskTransactionStatusBodyPredicates.countIs(1))
        .and(TaskTransactionStatusBodyPredicates.userIs(transaction.actioneerId)));

    final var future = WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction)
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask))
        .compose(ignored -> this.waitUntilIncentiveServerHasTaskTransactionStatus(vertx, testContext, checkStatus));
    future.onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

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
    final var checkStatus = new ArrayList<Predicate<TaskTransactionStatusBody>>();
    checkStatus.add(this.createIncentiveServerTaskTransactionStatusPredicate()
        .and(TaskTransactionStatusBodyPredicates.labelIs(transaction.label))
        .and(TaskTransactionStatusBodyPredicates.countIs(1))
        .and(TaskTransactionStatusBodyPredicates.userIs(transaction.actioneerId)));

    final var future = WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction)
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask))
        .compose(ignored -> this.waitUntilIncentiveServerHasTaskTransactionStatus(vertx, testContext, checkStatus));
    future.onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

  }

  /**
   * Check that can rank the answers.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(11)
  public void shouldRankAnswers(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(6, testContext);
    this.lastSuccessfulTest = 10;

    final var transaction = new TaskTransaction();
    transaction.actioneerId = this.task.requesterId;
    transaction.taskId = this.task.id;
    transaction.label = "rankAnswers";

    final var ranking = new JsonArray();
    for (final var done : this.task.transactions) {

      if (done.label.equals("answerTransaction")) {

        final var userId = done.actioneerId;
        final var answer = done.attributes.getString("answer", "");
        ranking.add(new JsonObject().put("userId", userId).put("answer", answer));

      }

    }

    final var checkMessages = new ArrayList<Predicate<Message>>();
    final var checkMessage = this.createMessagePredicate().and(MessagePredicates.labelIs("AnswersRanking"))
        .and(MessagePredicates.receiverIs(this.task.requesterId)).and(MessagePredicates
            .attributesSimilarTo(new JsonObject().put("taskId", this.task.id).put("ranking", ranking)));
    checkMessages.add(checkMessage);

    final var checkTask = this.createTaskPredicate().and(TaskPredicates.lastTransactionIs(
        this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(transaction))));
    final var checkStatus = new ArrayList<Predicate<TaskTransactionStatusBody>>();
    checkStatus.add(this.createIncentiveServerTaskTransactionStatusPredicate()
        .and(TaskTransactionStatusBodyPredicates.labelIs(transaction.label))
        .and(TaskTransactionStatusBodyPredicates.countIs(1))
        .and(TaskTransactionStatusBodyPredicates.userIs(transaction.actioneerId)));

    final var future = WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction)
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask))
        .compose(ignored -> this.waitUntilIncentiveServerHasTaskTransactionStatus(vertx, testContext, checkStatus))
        .compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages));
    future.onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

  }

  /**
   * Check that pick the best answer.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(12)
  public void shouldBestAnswer(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(6, testContext);
    this.lastSuccessfulTest = 11;

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
        .and(TaskPredicates.lastTransactionIs(this.createTaskTransactionPredicate()
            .and(TaskTransactionPredicates.similarTo(transaction)).and(TaskTransactionPredicates.messagesSizeIs(1))
            .and(TaskTransactionPredicates.messageAt(0, checkMessage))));

    final var checkStatus = new ArrayList<Predicate<TaskTransactionStatusBody>>();
    checkStatus.add(this.createIncentiveServerTaskTransactionStatusPredicate()
        .and(TaskTransactionStatusBodyPredicates.userIs(transaction.actioneerId))
        .and(TaskTransactionStatusBodyPredicates.labelIs(transaction.label))
        .and(TaskTransactionStatusBodyPredicates.countIs(1)));
    checkStatus.add(this.createIncentiveServerTaskTransactionStatusPredicate()
        .and(TaskTransactionStatusBodyPredicates.userIs(this.task.transactions.get(3).actioneerId))
        .and(TaskTransactionStatusBodyPredicates.labelIs("AnsweredPickedMessage"))
        .and(TaskTransactionStatusBodyPredicates.countIs(1)));

    final var future = WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction)
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask))
        .compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages))
        .compose(ignored -> this.waitUntilIncentiveServerHasTaskTransactionStatus(vertx, testContext, checkStatus))
        .compose(ignored -> this.waitUntil(vertx, testContext,
            () -> WeNetSocialContextBuilderSimulator.createProxy(vertx)
                .getSocialPreferencesSelectedAnswerForUserOnTask(transaction.actioneerId, this.task.id, 2),
            any -> true));
    future.onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

  }

  /**
   * Check that create a second task.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(13)
  public void shouldCreateSecondTask(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(5, testContext);
    this.lastSuccessfulTest = 12;

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
    createTransaction.label = TaskTransaction.CREATE_TASK_LABEL;
    createTransaction.actioneerId = source.requesterId;
    final var checkTask = this.createTaskPredicate().and(TaskPredicates.similarTo(source))
        .and(TaskPredicates.transactionSizeIs(1))
        .and(TaskPredicates.transactionAt(0,
            this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(createTransaction))
                .and(TaskTransactionPredicates.messagesSizeIs(this.users.size() - 1))
                .and(TaskTransactionPredicates.containsMessages(checkMessages))));

    final var checkStatus = new ArrayList<Predicate<TaskTypeStatusBody>>();
    checkStatus.add(this.createIncentiveServerTaskTypeStatusPredicate().and(TaskTypeStatusBodyPredicates.countIs(2))
        .and(TaskTypeStatusBodyPredicates.userIs(source.requesterId)));

    final var future = this.waitUntilTaskCreated(source, vertx, testContext, checkTask)
        .compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages))
        .compose(ignored -> this.waitUntilIncentiveServerHasTaskTypeStatus(vertx, testContext, checkStatus));
    future.onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

  }

}
