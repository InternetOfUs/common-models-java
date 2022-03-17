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

import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.incentive_server.TaskTransactionStatusBody;
import eu.internetofus.common.components.incentive_server.TaskTransactionStatusBodyPredicates;
import eu.internetofus.common.components.incentive_server.TaskTypeStatusBody;
import eu.internetofus.common.components.incentive_server.TaskTypeStatusBodyPredicates;
import eu.internetofus.common.components.interaction_protocol_engine.Interaction;
import eu.internetofus.common.components.interaction_protocol_engine.InteractionPredicates;
import eu.internetofus.common.components.models.Message;
import eu.internetofus.common.components.models.TaskTransaction;
import eu.internetofus.common.components.service.MessagePredicates;
import eu.internetofus.common.components.social_context_builder.UserMessage;
import eu.internetofus.common.components.social_context_builder.UserMessagePredicates;
import eu.internetofus.common.components.task_manager.TaskPredicates;
import eu.internetofus.common.components.task_manager.TaskTransactionPredicates;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Check the {@link DefaultProtocols#ASK_4_HELP_V2} protocol and ask all the
 * users randomly. ATTENTION: This test is sequential and maintains the state
 * between methods. In other words, you must to run the entire test methods on
 * the specified order to work.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class AbstractAsk4HelpV2_2ProtocolRandomUsersITC extends AbstractAsk4HelpV2_2ProtocolITC {

  /**
   * The identifier of the users that has notify the question.
   */
  protected Set<String> participants = new HashSet<>();

  /**
   * The identifier of the users that obtained the second time.
   */
  protected List<String> expectedNewParticipants = new ArrayList<String>();

  /**
   * {@inheritDoc}
   *
   * @return {@code 9} in any case.
   */
  @Override
  protected int numberOfUsersToCreate() {

    return 9;
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
    final var checkSocialNotification = new ArrayList<Predicate<UserMessage>>();
    final var checkInteractions = new ArrayList<Predicate<Interaction>>();

    checkIncentiveTypeStatus.add(this.createIncentiveServerTaskTypeStatusPredicate()
        .and(TaskTypeStatusBodyPredicates.userIs(source.requesterId)).and(TaskTypeStatusBodyPredicates.countIs(1)));

    final var maxUsers = source.attributes.getInteger("maxUsers");
    for (var i = 0; i < maxUsers; i++) {

      checkMessages
          .add(this.createMessagePredicate().and(MessagePredicates.labelIs("QuestionToAnswerMessage")).and(msg -> {

            if (this.getUserbyId(msg.receiverId) == null) {
              // Undefined user
              return false;
            }
            this.participants.add(msg.receiverId);
            return true;

          }).and(MessagePredicates.attributesSimilarTo(new JsonObject().put("question", source.goal.name)
              .put("userId", source.requesterId).put("sensitive", false).put("anonymous", false)))
              .and(MessagePredicates.attributesAre(target -> {
                return this.task.id.equals(target.getString("taskId"));
              })));

      checkIncentiveTransactionStatus.add(this.createIncentiveServerTaskTransactionStatusPredicate()
          .and(TaskTransactionStatusBodyPredicates.labelIs("QuestionToAnswerMessage"))
          .and(transaction -> this.participants.contains(transaction.user_id))
          .and(TaskTransactionStatusBodyPredicates.countIs(1)));
      checkSocialNotification.add(this.createSocialNotificationPredicate().and(msg -> {

        if (this.task == null) {

          return false;
        }
        return UserMessagePredicates.senderId(this.task.requesterId).test(msg);

      }).and(UserMessagePredicates.transactionId("0"))
          .and(UserMessagePredicates
              .message(this.createMessagePredicate().and(MessagePredicates.labelIs("QuestionToAnswerMessage"))
                  .and(msg -> this.participants.contains(msg.receiverId))
                  .and(MessagePredicates.attributesSimilarTo(new JsonObject().put("question", source.goal.name)
                      .put("userId", source.requesterId).put("sensitive", false).put("anonymous", false)))
                  .and(MessagePredicates.attributesAre(target -> {
                    return this.task.id.equals(target.getString("taskId"));
                  })))));

      checkInteractions.add(this.createInteractionPredicate().and(InteractionPredicates.senderIs(source.requesterId))
          .and(InteractionPredicates.transactionLabelIs(TaskTransaction.CREATE_TASK_LABEL))
          .and(interaction -> this.participants.contains(interaction.receiverId))
          .and(InteractionPredicates.messageLabelIs("QuestionToAnswerMessage")));
    }

    final var createTransaction = new TaskTransaction();
    createTransaction.label = TaskTransaction.CREATE_TASK_LABEL;
    createTransaction.actioneerId = source.requesterId;
    final var checkTask = this.createTaskPredicate().and(TaskPredicates.similarTo(source))
        .and(TaskPredicates.transactionSizeIs(1))
        .and(TaskPredicates.transactionAt(0,
            this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(createTransaction))
                .and(TaskTransactionPredicates.messagesSizeIs(maxUsers))
                .and(TaskTransactionPredicates.containsMessages(checkMessages))));

    final Future<?> future = this.waitUntilTaskCreated(source, vertx, testContext, checkTask)
        .compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages))
        .compose(
            ignored -> this.waitUntilIncentiveServerHasTaskTypeStatus(vertx, testContext, checkIncentiveTypeStatus))
        .compose(ignored -> this.waitUntilIncentiveServerHasTaskTransactionStatus(vertx, testContext,
            checkIncentiveTransactionStatus))
        .compose(ignored -> this.waitUntilSocialNotification(vertx, testContext, checkSocialNotification))
        .compose(ignored -> this.waitUntilInteractions(vertx, testContext, checkInteractions));
    future.onComplete(testContext.succeeding(ignored -> testContext.verify(() -> {

      assertThat(this.participants).hasSize(5);
      this.assertSuccessfulCompleted(testContext);

    })));

  }

  /**
   * Check that all the user answer the question.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(6)
  public void shouldAnswer(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(5, testContext);

    final var checkMessages = new ArrayList<Predicate<Message>>();
    final var checkIncentiveTransactionStatus = new ArrayList<Predicate<TaskTransactionStatusBody>>();
    final var checkSocialNotification = new ArrayList<Predicate<UserMessage>>();
    final var checkInteractions = new ArrayList<Predicate<Interaction>>();
    Future<?> future = Future.succeededFuture();
    var count = 1;
    for (final var user : this.participants) {

      final var transactionId = count;
      final var transaction = new TaskTransaction();
      transaction.actioneerId = user;
      transaction.taskId = this.task.id;
      transaction.label = "answerTransaction";
      final var answer = "Response question with " + transactionId;
      transaction.attributes = new JsonObject().put("answer", answer).put("anonymous", transactionId % 2 == 0);

      final var checkMessage = this.createMessagePredicate().and(MessagePredicates.labelIs("AnsweredQuestionMessage"))
          .and(MessagePredicates.receiverIs(this.task.requesterId))
          .and(MessagePredicates.attributesSimilarTo(new JsonObject().put("answer", answer).put("taskId", this.task.id)
              .put("question", this.task.goal.name).put("transactionId", String.valueOf(transactionId))
              .put("answer", answer).put("userId", transaction.actioneerId).put("anonymous", transactionId % 2 == 0)));
      checkMessages.add(checkMessage);

      final var checkTask = this.createTaskPredicate().and(TaskPredicates.transactionSizeIs(transactionId + 1))
          .and(TaskPredicates.transactionAt(transactionId,
              this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(transaction))
                  .and(TaskTransactionPredicates.messagesSizeIs(1))
                  .and(TaskTransactionPredicates.messageAt(0, checkMessage))));

      checkIncentiveTransactionStatus.add(this.createIncentiveServerTaskTransactionStatusPredicate()
          .and(TaskTransactionStatusBodyPredicates.userIs(transaction.actioneerId))
          .and(TaskTransactionStatusBodyPredicates.labelIs(transaction.label))
          .and(TaskTransactionStatusBodyPredicates.countIs(1)));
      checkIncentiveTransactionStatus.add(this.createIncentiveServerTaskTransactionStatusPredicate()
          .and(TaskTransactionStatusBodyPredicates.userIs(this.task.requesterId))
          .and(TaskTransactionStatusBodyPredicates.labelIs("AnsweredQuestionMessage"))
          .and(TaskTransactionStatusBodyPredicates.countIs(transactionId)));
      checkSocialNotification.add(this.createSocialNotificationPredicate().and(UserMessagePredicates.senderId(user))
          .and(UserMessagePredicates.transactionId(String.valueOf(transactionId)))
          .and(UserMessagePredicates.message(checkMessage)));

      checkInteractions
          .add(this.createInteractionPredicate().and(InteractionPredicates.senderIs(transaction.actioneerId))
              .and(InteractionPredicates.transactionLabelIs(transaction.label))
              .and(InteractionPredicates.transactionAttributesAre(transaction.attributes))
              .and(InteractionPredicates.receiverIs(this.task.requesterId))
              .and(InteractionPredicates.messageLabelIs("AnsweredQuestionMessage")));

      future = future.compose(ignored -> WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction))
          .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask));

      count++;
    }

    future = future.compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages))
        .compose(ignored -> this.waitUntilIncentiveServerHasTaskTransactionStatus(vertx, testContext,
            checkIncentiveTransactionStatus))
        .compose(ignored -> this.waitUntilSocialNotification(vertx, testContext, checkSocialNotification))
        .compose(ignored -> this.waitUntilInteractions(vertx, testContext, checkInteractions));
    future.onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

  }

  /**
   * Check that can report answer.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(7)
  public void shouldReportAnswerTransaction(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(6, testContext);

    final var reportAnswerTransaction = new TaskTransaction();
    reportAnswerTransaction.taskId = this.task.id;
    reportAnswerTransaction.actioneerId = this.task.requesterId;
    reportAnswerTransaction.label = "reportAnswerTransaction";
    reportAnswerTransaction.attributes = new JsonObject()
        .put("transactionId", this.task.transactions.get(this.task.transactions.size() - 1).id)
        .put("reason", "abusive");

    final var checkTask = this.createTaskPredicate().and(TaskPredicates.lastTransactionIs(
        this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(reportAnswerTransaction))));

    final Future<?> future = WeNetTaskManager.createProxy(vertx).doTaskTransaction(reportAnswerTransaction)
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask));

    future.onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

  }

  /**
   * Check that the requester can ask for more answers.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(8)
  public void shouldAskForMoreAnswers(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(6, testContext);
    this.lastSuccessfulTest = 7;

    for (final var user : this.users) {

      final var userId = user.id;
      if (!this.task.requesterId.equals(userId) && !this.participants.contains(userId)) {

        this.expectedNewParticipants.add(userId);
      }
    }

    final var checkMessages = new ArrayList<Predicate<Message>>();
    final var checkIncentiveTransactionStatus = new ArrayList<Predicate<TaskTransactionStatusBody>>();
    final var checkSocialNotification = new ArrayList<Predicate<UserMessage>>();
    final var checkInteractions = new ArrayList<Predicate<Interaction>>();

    final var transactionId = String.valueOf(this.task.transactions.size());
    final var maxUsers = this.expectedNewParticipants.size();
    for (var i = 0; i < maxUsers; i++) {

      checkMessages
          .add(this.createMessagePredicate().and(MessagePredicates.labelIs("QuestionToAnswerMessage")).and(msg -> {

            if (!this.expectedNewParticipants.contains(msg.receiverId)) {
              // Undefined user
              return false;
            }
            this.participants.add(msg.receiverId);
            return true;

          }).and(MessagePredicates
              .attributesSimilarTo(new JsonObject().put("question", this.task.goal.name).put("taskId", this.task.id)
                  .put("userId", this.task.requesterId).put("sensitive", false).put("anonymous", false))));

      checkIncentiveTransactionStatus.add(this.createIncentiveServerTaskTransactionStatusPredicate()
          .and(TaskTransactionStatusBodyPredicates.labelIs("QuestionToAnswerMessage"))
          .and(transaction -> this.participants.contains(transaction.user_id))
          .and(TaskTransactionStatusBodyPredicates.countIs(1)));
      checkSocialNotification
          .add(this.createSocialNotificationPredicate().and(UserMessagePredicates.senderId(this.task.requesterId))
              .and(UserMessagePredicates.transactionId(transactionId))
              .and(UserMessagePredicates
                  .message(this.createMessagePredicate().and(MessagePredicates.labelIs("QuestionToAnswerMessage"))
                      .and(msg -> this.participants.contains(msg.receiverId))
                      .and(MessagePredicates.attributesSimilarTo(new JsonObject().put("taskId", this.task.id)
                          .put("question", this.task.goal.name).put("userId", this.task.requesterId)
                          .put("sensitive", false).put("anonymous", false))))));

      checkInteractions.add(this.createInteractionPredicate().and(InteractionPredicates.senderIs(this.task.requesterId))
          .and(InteractionPredicates.transactionLabelIs("moreAnswerTransaction"))
          .and(interaction -> this.participants.contains(interaction.receiverId))
          .and(InteractionPredicates.messageLabelIs("QuestionToAnswerMessage")));

    }

    final var moreAnswerTransaction = new TaskTransaction();
    moreAnswerTransaction.taskId = this.task.id;
    moreAnswerTransaction.actioneerId = this.task.requesterId;
    moreAnswerTransaction.label = "moreAnswerTransaction";
    final var checkTask = this.createTaskPredicate()
        .and(TaskPredicates.lastTransactionIs(
            this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(moreAnswerTransaction))
                .and(TaskTransactionPredicates.messagesSizeIs(maxUsers))
                .and(TaskTransactionPredicates.containsMessages(checkMessages))));

    final Future<?> future = WeNetTaskManager.createProxy(vertx).doTaskTransaction(moreAnswerTransaction)
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask))
        .compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages))
        .compose(ignored -> this.waitUntilIncentiveServerHasTaskTransactionStatus(vertx, testContext,
            checkIncentiveTransactionStatus))
        .compose(ignored -> this.waitUntilSocialNotification(vertx, testContext, checkSocialNotification))
        .compose(ignored -> this.waitUntilInteractions(vertx, testContext, checkInteractions));
    future.onComplete(testContext.succeeding(ignored -> testContext.verify(() -> {

      assertThat(this.participants).hasSize(8);
      this.assertSuccessfulCompleted(testContext);

    })));

  }

  /**
   * Check that the requester can ask for more answers.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(9)
  public void shouldNotAnswer(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(8, testContext);

    final var notAnswerTransaction = new TaskTransaction();
    notAnswerTransaction.taskId = this.task.id;
    notAnswerTransaction.actioneerId = this.expectedNewParticipants.get(0);
    notAnswerTransaction.label = "notAnswerTransaction";

    final var checkTask = this.createTaskPredicate().and(TaskPredicates.lastTransactionIs(
        this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(notAnswerTransaction))));

    final Future<?> future = WeNetTaskManager.createProxy(vertx).doTaskTransaction(notAnswerTransaction)
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask));

    future.onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

  }

  /**
   * Check that can report the question.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(10)
  public void shouldReportQuestionTransaction(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(8, testContext);
    this.lastSuccessfulTest = 9;

    final var reportQuestionTransaction = new TaskTransaction();
    reportQuestionTransaction.taskId = this.task.id;
    reportQuestionTransaction.actioneerId = this.expectedNewParticipants.get(1);
    reportQuestionTransaction.label = "reportQuestionTransaction";
    reportQuestionTransaction.attributes = new JsonObject().put("reason", "abusive");

    final var checkTask = this.createTaskPredicate().and(TaskPredicates.lastTransactionIs(
        this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(reportQuestionTransaction))));

    final Future<?> future = WeNetTaskManager.createProxy(vertx).doTaskTransaction(reportQuestionTransaction)
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask));

    future.onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

  }

  /**
   * Check that pick best answer.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(11)
  public void shouldBestAnswerTransaction(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(6, testContext);
    this.lastSuccessfulTest = 10;

    final var bestAnswerTransaction = new TaskTransaction();
    bestAnswerTransaction.taskId = this.task.id;
    bestAnswerTransaction.actioneerId = this.task.requesterId;
    bestAnswerTransaction.label = "bestAnswerTransaction";
    final var selectedTransaction = this.task.transactions.get(2);
    bestAnswerTransaction.attributes = new JsonObject().put("transactionId", selectedTransaction.id)
        .put("reason", "I love it").put("helpful", "extremelyHelpful");

    final var checkMessages = new ArrayList<Predicate<Message>>();
    final var checkMessage = this.createMessagePredicate().and(MessagePredicates.labelIs("AnsweredPickedMessage"))
        .and(MessagePredicates.receiverIs(selectedTransaction.actioneerId))
        .and(MessagePredicates.attributesSimilarTo(new JsonObject().put("taskId", this.task.id)
            .put("question", this.task.goal.name).put("transactionId", selectedTransaction.id)));
    checkMessages.add(checkMessage);

    final var checkTask = this.createTaskPredicate().and(TaskPredicates.lastTransactionIs(this
        .createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(bestAnswerTransaction))
        .and(TaskTransactionPredicates.messagesSizeIs(1)).and(TaskTransactionPredicates.messageAt(0, checkMessage))))
        .and(TaskPredicates.isClosed());

    final var checkStatus = new ArrayList<Predicate<TaskTransactionStatusBody>>();
    checkStatus.add(this.createIncentiveServerTaskTransactionStatusPredicate()
        .and(TaskTransactionStatusBodyPredicates.userIs(bestAnswerTransaction.actioneerId))
        .and(TaskTransactionStatusBodyPredicates.labelIs(bestAnswerTransaction.label))
        .and(TaskTransactionStatusBodyPredicates.countIs(1)));
    checkStatus.add(this.createIncentiveServerTaskTransactionStatusPredicate()
        .and(TaskTransactionStatusBodyPredicates.userIs(selectedTransaction.actioneerId))
        .and(TaskTransactionStatusBodyPredicates.labelIs("AnsweredPickedMessage"))
        .and(TaskTransactionStatusBodyPredicates.countIs(1)));

    final var checkSocialNotification = new ArrayList<Predicate<UserMessage>>();
    checkSocialNotification
        .add(this.createSocialNotificationPredicate().and(UserMessagePredicates.senderId(this.task.requesterId))
            .and(UserMessagePredicates.transactionId(String.valueOf(this.task.transactions.size())))
            .and(UserMessagePredicates.message(checkMessage)));

    final var checkInteractions = new ArrayList<Predicate<Interaction>>();
    checkInteractions
        .add(this.createInteractionPredicate().and(InteractionPredicates.senderIs(bestAnswerTransaction.actioneerId))
            .and(InteractionPredicates.transactionLabelIs(bestAnswerTransaction.label))
            .and(InteractionPredicates.transactionAttributesAre(bestAnswerTransaction.attributes))
            .and(InteractionPredicates.receiverIs(selectedTransaction.actioneerId))
            .and(InteractionPredicates.messageLabelIs("AnsweredPickedMessage"))
            .and(InteractionPredicates.messageAttributesSimilarTo(new JsonObject().put("taskId", this.task.id)
                .put("question", this.task.goal.name).put("transactionId", selectedTransaction.id))));

    final Future<?> future = WeNetTaskManager.createProxy(vertx).doTaskTransaction(bestAnswerTransaction)
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask))
        .compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages))
        .compose(ignored -> this.waitUntilIncentiveServerHasTaskTransactionStatus(vertx, testContext, checkStatus))
        .compose(ignored -> this.waitUntilSocialNotification(vertx, testContext, checkSocialNotification))
        .compose(ignored -> this.waitUntilInteractions(vertx, testContext, checkInteractions));

    future.onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

  }

}
