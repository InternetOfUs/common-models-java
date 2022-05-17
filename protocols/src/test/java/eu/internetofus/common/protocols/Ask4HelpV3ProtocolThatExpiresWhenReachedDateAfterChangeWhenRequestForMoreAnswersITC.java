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
 * Check the {@link DefaultProtocols#ASK_4_HELP_V3} protocol. ATTENTION: This
 * test is sequential and maintains the state between methods. In other words,
 * you must to run the entire test methods on the specified order to work.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class Ask4HelpV3ProtocolThatExpiresWhenReachedDateAfterChangeWhenRequestForMoreAnswersITC
    extends AbstractAsk4HelpV3ProtocolITC {

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
   * @return {@code 5} in any case.
   */
  @Override
  public int maxUsers() {

    return 5;
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
   * @return ten minutes from now.
   */
  @Override
  public long expirationDate() {

    return TimeManager.now() + Duration.ofMinutes(10).toSeconds();

  }

  /**
   * Check that the requester can ask for more answers.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(6)
  public void shouldAskForMoreAnswers(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(5, testContext);

    final var checkMessages = new ArrayList<Predicate<Message>>();
    for (final var user : this.users) {

      final var userId = user.id;
      if (!this.task.requesterId.equals(userId) && !this.participants.contains(userId)) {

        checkMessages.add(this.createMessagePredicate().and(MessagePredicates.labelIs("QuestionToAnswerMessage"))
            .and(MessagePredicates.receiverIs(userId))
            .and(MessagePredicates
                .attributesSimilarTo(new JsonObject().put("question", this.task.goal.name).put("taskId", this.task.id)
                    .put("userId", this.task.requesterId).put("sensitive", false).put("anonymous", false))));

      }
    }

    final var moreAnswerTransaction = new TaskTransaction();
    moreAnswerTransaction.taskId = this.task.id;
    moreAnswerTransaction.actioneerId = this.task.requesterId;
    moreAnswerTransaction.label = "moreAnswerTransaction";
    moreAnswerTransaction.attributes = new JsonObject().put("expirationDate",
        TimeManager.now() + Duration.ofSeconds(10).toSeconds());
    final var checkTask = this.createTaskPredicate()
        .and(TaskPredicates.lastTransactionIs(
            this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(moreAnswerTransaction))
                .and(TaskTransactionPredicates.messagesSizeIs(checkMessages.size()))
                .and(TaskTransactionPredicates.containsMessages(checkMessages))));

    final Future<?> future = WeNetTaskManager.createProxy(vertx).doTaskTransaction(moreAnswerTransaction)
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask))
        .compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages));
    future.onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

  }

  /**
   * Check that the task was expired.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(7)
  public void shouldTaskExpireByDate(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(6, testContext);

    final var checkMessages = new ArrayList<Predicate<Message>>();
    final var checkMessage = this.createMessagePredicate().and(MessagePredicates.labelIs("QuestionExpirationMessage"))
        .and(MessagePredicates.receiverIs(this.task.requesterId))
        .and(MessagePredicates.attributesSimilarTo(new JsonObject().put("taskId", this.task.id)
            .put("question", this.task.goal.name).put("listOfTransactionIds", new JsonArray())));
    checkMessages.add(checkMessage);

    final var future = this.waitUntilCallbacks(vertx, testContext, checkMessages);
    future.onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

  }
}
