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

import eu.internetofus.common.components.models.Message;
import eu.internetofus.common.components.models.TaskTransaction;
import eu.internetofus.common.components.service.MessagePredicates;
import eu.internetofus.common.components.task_manager.TaskPredicates;
import eu.internetofus.common.components.task_manager.TaskTransactionPredicates;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import eu.internetofus.common.model.TimeManager;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import java.time.Duration;
import java.util.ArrayList;
import java.util.function.Predicate;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Check the {@link DefaultProtocols#ASK_4_HELP_V3_1} protocol. ATTENTION: This
 * test is sequential and maintains the state between methods. In other words,
 * you must to run the entire test methods on the specified order to work.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class Ask4HelpV3_1ProtocolWithSubjectRandomAndExpiresWhenReachedMaxAnswersITC
    extends AbstractAsk4HelpV3_1ProtocolITC {

  /**
   * {@inheritDoc}
   *
   * @return {@code 10} in any case.
   */
  @Override
  protected int numberOfUsersToCreate() {

    return 10;
  }

  /**
   * {@inheritDoc}
   *
   * @return {@code studying_career} in any case.
   */
  @Override
  public String domain() {

    return "studying_career";
  }

  /**
   * {@inheritDoc}
   *
   * @return {@code true} in any case.
   */
  @Override
  public boolean anonymous() {

    return true;
  }

  /**
   * {@inheritDoc}
   *
   * @return {@code subject_random} in any case.
   */
  @Override
  public String subjectivity() {

    return "subject_random";
  }

  /**
   * {@inheritDoc}
   *
   * @return {@code 3} in any case.
   */
  @Override
  public int maxUsers() {

    return 6;
  }

  /**
   * {@inheritDoc}
   *
   * @return {@code 5} in any case.
   */
  @Override
  public int maxAnswers() {

    return 5;
  }

  /**
   * {@inheritDoc}
   *
   * @return to days from now.
   */
  @Override
  public long expirationDate() {

    return TimeManager.now() + Duration.ofDays(2).toSeconds();

  }

  /**
   * Check that all the user answer the question.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(6)
  public void shouldAnswers(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(5, testContext);

    var checkTransactions = this.createTaskPredicate().and(TaskPredicates.transactionSizeIs(this.maxAnswers() + 1));

    final var checkMessages = new ArrayList<Predicate<Message>>();
    final var listOfTransactionIds = new JsonArray();
    Future<?> future = Future.succeededFuture();
    final var iter = this.participants.iterator();
    for (var i = 1; i <= this.maxAnswers(); i++) {

      listOfTransactionIds.add(String.valueOf(i));
      final var transaction = new TaskTransaction();
      transaction.actioneerId = iter.next();
      transaction.taskId = this.task.id;
      transaction.label = "answerTransaction";
      final var answer = "Response question with ";
      transaction.attributes = new JsonObject().put("answer", answer).put("anonymous", this.anonymous())
          .put("publish", true).put("publishAnonymously", false);

      final var checkMessage = this.createMessagePredicate().and(MessagePredicates.labelIs("AnsweredQuestionMessage"))
          .and(MessagePredicates.receiverIs(this.task.requesterId))
          .and(MessagePredicates.attributesSimilarTo(new JsonObject().put("taskId", this.task.id)
              .put("question", this.task.goal.name).put("transactionId", String.valueOf(i)).put("answer", answer)
              .put("userId", transaction.actioneerId).put("anonymous", this.anonymous())));
      checkMessages.add(checkMessage);

      if (i == this.maxAnswers()) {

        final var checkMessage2 = this.createMessagePredicate()
            .and(MessagePredicates.labelIs("QuestionExpirationMessage"))
            .and(MessagePredicates.receiverIs(this.task.requesterId))
            .and(MessagePredicates.attributesSimilarTo(new JsonObject().put("taskId", this.task.id)
                .put("question", this.task.goal.name).put("listOfTransactionIds", listOfTransactionIds)));
        checkMessages.add(checkMessage2);

        final var transactionCheck = TaskPredicates.transactionAt(i,
            this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(transaction))
                .and(TaskTransactionPredicates.messagesSizeIs(2))
                .and(TaskTransactionPredicates.messageAt(0, checkMessage))
                .and(TaskTransactionPredicates.messageAt(1, checkMessage2)));
        checkTransactions = checkTransactions.and(transactionCheck);
        final var checkTask = this.createTaskPredicate().and(TaskPredicates.transactionSizeIs(i + 1))
            .and(transactionCheck);
        future = future.compose(ignored -> WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction))
            .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask));

      } else {

        final var transactionCheck = TaskPredicates.transactionAt(i,
            this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(transaction))
                .and(TaskTransactionPredicates.messagesSizeIs(1))
                .and(TaskTransactionPredicates.messageAt(0, checkMessage)));
        checkTransactions = checkTransactions.and(transactionCheck);

        final var checkTask = this.createTaskPredicate().and(TaskPredicates.transactionSizeIs(i + 1))
            .and(transactionCheck);
        future = future.compose(ignored -> WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction))
            .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask));

      }

    }

    final var checkTask = this.createTaskPredicate().and(TaskPredicates.transactionSizeIs(this.maxAnswers() + 1))
        .and(checkTransactions);
    future = future.compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask))
        .compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages));
    future.onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

  }

}
