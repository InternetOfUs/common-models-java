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
import eu.internetofus.common.components.models.TaskTransaction;
import eu.internetofus.common.components.service.MessagePredicates;
import eu.internetofus.common.components.task_manager.TaskPredicates;
import eu.internetofus.common.components.task_manager.TaskTransactionPredicates;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Predicate;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Check the {@link DefaultProtocols#ECHO_V1} protocol. ATTENTION: This test is
 * sequential and maintains the state between methods. In other words, you must
 * to run the entire test methods on the specified order to work.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class EchoV1ProtocolITC extends AbstractDefaultProtocolITC {

  /**
   * {@inheritDoc}
   *
   * @return {@code 1} in any case.
   */
  @Override
  protected int numberOfUsersToCreate() {

    return 1;
  }

  /**
   * {@inheritDoc}
   *
   * @return {@link DefaultProtocols#ECHO_V1}
   */
  @Override
  protected DefaultProtocols getDefaultProtocolsToUse() {

    return DefaultProtocols.ECHO_V1;
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
    final var expectedTransaction = new TaskTransaction();
    expectedTransaction.actioneerId = source.requesterId;
    expectedTransaction.label = source.requesterId;

    final var checkIncentiveTypeStatus = new ArrayList<Predicate<TaskTypeStatusBody>>();
    checkIncentiveTypeStatus.add(this.createIncentiveServerTaskTypeStatusPredicate()
        .and(TaskTypeStatusBodyPredicates.userIs(source.requesterId)).and(TaskTypeStatusBodyPredicates.countIs(1)));

    final var future = this
        .waitUntilTaskCreated(source, vertx, testContext,
            TaskPredicates.similarTo(source).and(TaskPredicates.transactionSizeIs(1))
                .and(TaskPredicates.transactionAt(0, TaskTransactionPredicates.withoutMessages())))
        .compose(
            ignored -> this.waitUntilIncentiveServerHasTaskTypeStatus(vertx, testContext, checkIncentiveTypeStatus));
    future.onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

  }

  /**
   * Should send transaction echo.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(6)
  public void shouldDoTransactionEcho(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(5, testContext);

    final var transaction = new TaskTransaction();
    transaction.actioneerId = this.task.requesterId;
    transaction.taskId = this.task.id;
    transaction.label = "echo";
    final var message = UUID.randomUUID().toString();
    transaction.attributes = new JsonObject().put("message", message);
    final var checkMessages = new ArrayList<Predicate<Message>>();
    final var checkMessage = MessagePredicates.labelIs("echo")
        .and(MessagePredicates.receiverIs(transaction.actioneerId))
        .and(MessagePredicates.attributesAre(new JsonObject().put("message", message).put("taskId", this.task.id)));
    checkMessages.add(checkMessage);
    final var checkTask = TaskPredicates.transactionSizeIs(2)
        .and(TaskPredicates.transactionAt(1,
            TaskTransactionPredicates.similarTo(transaction).and(TaskTransactionPredicates.messagesSizeIs(1))
                .and(TaskTransactionPredicates.messageAt(0, checkMessage))));
    final var checkIncentiveTransactionStatus = new ArrayList<Predicate<TaskTransactionStatusBody>>();
    checkIncentiveTransactionStatus.add(this.createIncentiveServerTaskTransactionStatusPredicate()
        .and(TaskTransactionStatusBodyPredicates.userIs(transaction.actioneerId))
        .and(TaskTransactionStatusBodyPredicates.labelIs("echo")).and(TaskTransactionStatusBodyPredicates.countIs(1)));
    checkIncentiveTransactionStatus.add(this.createIncentiveServerTaskTransactionStatusPredicate()
        .and(TaskTransactionStatusBodyPredicates.userIs(transaction.actioneerId))
        .and(TaskTransactionStatusBodyPredicates.labelIs("echo")).and(TaskTransactionStatusBodyPredicates.countIs(2)));

    final var future = WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction)
        .compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages))
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask)).compose(ignored -> this
            .waitUntilIncentiveServerHasTaskTransactionStatus(vertx, testContext, checkIncentiveTransactionStatus));

    future.onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

  }

}
