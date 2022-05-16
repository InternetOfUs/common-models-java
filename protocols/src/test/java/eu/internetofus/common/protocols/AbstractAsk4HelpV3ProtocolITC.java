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
import eu.internetofus.common.components.models.Task;
import eu.internetofus.common.components.models.TaskTransaction;
import eu.internetofus.common.components.service.MessagePredicates;
import eu.internetofus.common.components.task_manager.TaskPredicates;
import eu.internetofus.common.components.task_manager.TaskTransactionPredicates;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
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
public abstract class AbstractAsk4HelpV3ProtocolITC extends AbstractDefaultProtocolITC {

  /**
   * The identifier of the users that has notify the question.
   */
  protected Set<String> participants = new HashSet<>();

  /**
   * {@inheritDoc}
   *
   * @return {@link DefaultProtocols#ASK_4_HELP_V3}
   */
  @Override
  protected DefaultProtocols getDefaultProtocolsToUse() {

    return DefaultProtocols.ASK_4_HELP_V3;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Task createTaskForProtocol() {

    final var taskToCreate = super.createTaskForProtocol();
    taskToCreate.goal.name = "Where to buy the best pizza?";
    taskToCreate.attributes = new JsonObject().put("domain", "varia_misc").put("domainInterest", "indifferent")
        .put("beliefsAndValues", "indifferent").put("sensitive", false).put("anonymous", false)
        .put("socialCloseness", "indifferent").put("positionOfAnswerer", "anywhere").put("maxUsers", this.maxUsers())
        .put("maxAnswers", this.maxAnswers()).put("expirationDate", this.expirationDate());
    return taskToCreate;

  }

  /**
   * Return the number of maximum users to ask.
   *
   * @return the maximum answers before expire the task.
   */
  public abstract int maxUsers();

  /**
   * Return the number of answers that it is expecting.
   *
   * @return the number of answers to expire the task.
   */
  public abstract int maxAnswers();

  /**
   * Return the expiration date.
   *
   * @return to expiration date.
   */
  public abstract long expirationDate();

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
    final var maxMessages = Math.min(this.numberOfUsersToCreate() - 1, this.maxUsers());
    for (var i = 0; i < maxMessages; i++) {

      checkMessages.add(this.createMessagePredicate().and(MessagePredicates.labelIs("QuestionToAnswerMessage"))
          .and(MessagePredicates.attributesSimilarTo(new JsonObject().put("question", source.goal.name)
              .put("userId", source.requesterId).put("sensitive", false).put("anonymous", false)))
          .and(MessagePredicates.attributesAre(target -> {

            return this.task.id.equals(target.getString("taskId"));

          }).and(msg -> {

            this.participants.add(msg.receiverId);
            return true;

          })));
    }

    final var createTransaction = new TaskTransaction();
    createTransaction.label = TaskTransaction.CREATE_TASK_LABEL;
    createTransaction.actioneerId = source.requesterId;
    final var checkTask = this.createTaskPredicate().and(TaskPredicates.similarTo(source))
        .and(TaskPredicates.transactionSizeIs(1))
        .and(TaskPredicates.transactionAt(0,
            this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(createTransaction))
                .and(TaskTransactionPredicates.messagesSizeIs(maxMessages))
                .and(TaskTransactionPredicates.containsMessages(checkMessages))));

    final Future<?> future = this.waitUntilTaskCreated(source, vertx, testContext, checkTask)
        .compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages));
    future.onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));
  }

}