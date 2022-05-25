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
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Generic test over the {@link DefaultProtocols#FLOOD_V1}. ATTENTION: This test
 * is sequential and maintains the state between methods. In other words, you
 * must to run the entire test methods on the specified order to work.
 *
 * @see DefaultProtocols#FLOOD_V1
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class FloodV1ProtocolITC extends AbstractDefaultProtocolITC {

  /**
   * {@inheritDoc}
   *
   * @return {@link DefaultProtocols#FLOOD_V1}
   */
  @Override
  protected DefaultProtocols getDefaultProtocolsToUse() {

    return DefaultProtocols.FLOOD_V1;
  }

  /**
   * {@inheritDoc}
   *
   * @return {@code 7} in any case.
   */
  @Override
  protected int numberOfUsersToCreate() {

    return 7;
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

    final var createTransaction = new TaskTransaction();
    createTransaction.label = TaskTransaction.CREATE_TASK_LABEL;
    createTransaction.actioneerId = source.requesterId;

    final var checkMessages = this.createCheckMessagesFor(createTransaction.actioneerId, "Task created");
    final var checkTask = this.createTaskPredicate().and(TaskPredicates.similarTo(source))
        .and(TaskPredicates.transactionSizeIs(1))
        .and(TaskPredicates.transactionAt(0,
            this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(createTransaction))
                .and(TaskTransactionPredicates.messagesSizeIs(checkMessages.size()))
                .and(TaskTransactionPredicates.containsMessages(checkMessages))));

    final Future<?> future = this.waitUntilTaskCreated(source, vertx, testContext, checkTask)
        .compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages));
    future.onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));
  }

  /**
   * Check that a flood message is said.
   *
   * @param index       of the user to send the flood message.
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @ParameterizedTest
  @ValueSource(ints = { 6, 1, 0, 3, 2, 5, 4 })
  @Order(6)
  public void shouldFlood(final int index, final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(5, testContext);

    final var floodTransaction = new TaskTransaction();
    floodTransaction.taskId = this.task.id;
    floodTransaction.label = "flood";
    final var content = UUID.randomUUID().toString();
    floodTransaction.attributes = new JsonObject().put("content", content);
    floodTransaction.actioneerId = this.users.get(index).id;

    final var checkMessages = this.createCheckMessagesFor(floodTransaction.actioneerId, content);
    final var checkTask = this.createTaskPredicate()
        .and(TaskPredicates.lastTransactionIs(
            this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(floodTransaction))
                .and(TaskTransactionPredicates.messagesSizeIs(checkMessages.size()))
                .and(TaskTransactionPredicates.containsMessages(checkMessages))));

    final Future<?> future = WeNetTaskManager.createProxy(vertx).doTaskTransaction(floodTransaction)
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask))
        .compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages));
    future.onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));
  }

  /**
   * Create the messages to check.
   *
   * @param actioneerId identifier of the transaction auctioneer.
   * @param content     for the message.
   *
   * @return the predicates to check the expected messages.
   */
  protected List<Predicate<Message>> createCheckMessagesFor(final String actioneerId, final String content) {

    final var checkMessages = new ArrayList<Predicate<Message>>();
    for (var i = 0; i < this.users.size(); i++) {

      final var userId = this.users.get(i).id;
      if (!userId.equals(actioneerId)) {

        checkMessages.add(this.cerateCheckMessage(userId, content));

      }

    }

    return checkMessages;
  }

  /**
   * Create the check for a message.
   *
   * @param userId  identifier of the receiver.
   * @param content for the message.
   *
   * @return the predicate to check the message.
   */
  protected Predicate<Message> cerateCheckMessage(final String userId, final String content) {

    return this.createMessagePredicate().and(MessagePredicates.labelIs("message"))
        .and(MessagePredicates.attributesSimilarTo(new JsonObject().put("content", content))).and(msg -> {

          if (this.task == null) {

            return false;
          }
          return this.task.id.equals(msg.attributes.getString("taskId"));

        }).and(MessagePredicates.receiverIs(userId));
  }

}
