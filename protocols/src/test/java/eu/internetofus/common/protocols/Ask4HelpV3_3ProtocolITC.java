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

import eu.internetofus.common.components.models.TaskTransaction;
import eu.internetofus.common.components.task_manager.TaskPredicates;
import eu.internetofus.common.components.task_manager.TaskTransactionPredicates;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Test the Ask4Help version 3.3.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class Ask4HelpV3_3ProtocolITC extends PilotM46LSEProtocolWithAllIndifferentDimensionsITC {

  /**
   * {@inheritDoc}
   *
   * @see DefaultProtocols#ASK_4_HELP_V3_3
   */
  @Override
  protected DefaultProtocols getDefaultProtocolsToUse() {

    return DefaultProtocols.ASK_4_HELP_V3_3;
  }

  /**
   * Check that do answerTransactionLong.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(9)
  public void shouldDoAnswerTransactionLong(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(8, testContext);

    final var user = this.users.get(2);
    final var transaction = new TaskTransaction();
    transaction.actioneerId = user.id;
    transaction.taskId = this.task.id;
    transaction.label = "answerTransactionLong";

    final var checkTask = this.createTaskPredicate().and(TaskPredicates.transactionSizeIs(3)).and(TaskPredicates
        .transactionAt(2, this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(transaction))));

    WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction)
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask))
        .onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

  }

  /**
   * Check that do notAnswerTransaction.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(10)
  public void shouldDoNotAnswerTransaction(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(9, testContext);

    final var user = this.users.get(3);
    final var transaction = new TaskTransaction();
    transaction.actioneerId = user.id;
    transaction.taskId = this.task.id;
    transaction.label = "notAnswerTransaction";

    final var checkTask = this.createTaskPredicate().and(TaskPredicates.transactionSizeIs(4)).and(TaskPredicates
        .transactionAt(3, this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(transaction))));

    WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction)
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask))
        .onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

  }

  /**
   * Check that do reportQuestionTransaction.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(11)
  public void shouldDoReportQuestionTransaction(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(10, testContext);

    final var user = this.users.get(3);
    final var transaction = new TaskTransaction();
    transaction.actioneerId = user.id;
    transaction.taskId = this.task.id;
    transaction.label = "reportQuestionTransaction";
    transaction.attributes = new JsonObject().put("reason", "spam");

    final var checkTask = this.createTaskPredicate().and(TaskPredicates.transactionSizeIs(5)).and(TaskPredicates
        .transactionAt(4, this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(transaction))));

    WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction)
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask))
        .onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

  }

  /**
   * Check that do reportAnswerTransaction.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(12)
  public void shouldDoReportAnswerTransaction(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(11, testContext);

    final var transaction = new TaskTransaction();
    transaction.actioneerId = this.task.requesterId;
    transaction.taskId = this.task.id;
    transaction.label = "reportAnswerTransaction";
    transaction.attributes = new JsonObject().put("transactionId", "1").put("reason", "spam");

    final var checkTask = this.createTaskPredicate().and(TaskPredicates.transactionSizeIs(6)).and(TaskPredicates
        .transactionAt(5, this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(transaction))));

    WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction)
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask))
        .onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

  }

  /**
   * Check that do likeAnswerTransaction.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(13)
  public void shouldDoLikeAnswerTransaction(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(12, testContext);

    final var transaction = new TaskTransaction();
    transaction.actioneerId = this.task.requesterId;
    transaction.taskId = this.task.id;
    transaction.label = "likeAnswerTransaction";
    transaction.attributes = new JsonObject().put("transactionId", "1");

    final var checkTask = this.createTaskPredicate().and(TaskPredicates.transactionSizeIs(7)).and(TaskPredicates
        .transactionAt(6, this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(transaction))));

    WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction)
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask))
        .onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

  }

  /**
   * Check that do followUpTransaction.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(14)
  public void shouldDoFollowUpTransaction(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(13, testContext);

    final var transaction = new TaskTransaction();
    final var user = this.users.get(3);
    transaction.actioneerId = user.id;
    transaction.taskId = this.task.id;
    transaction.label = "followUpTransaction";
    transaction.attributes = new JsonObject().put("transactionId", "1");

    final var checkTask = this.createTaskPredicate().and(TaskPredicates.transactionSizeIs(8)).and(TaskPredicates
        .transactionAt(7, this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(transaction))));

    WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction)
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask))
        .onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

  }

  /**
   * Check that do closeQuestionTransaction.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(15)
  public void shouldCloseQuestionTransaction(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(14, testContext);

    final var transaction = new TaskTransaction();
    transaction.actioneerId = this.task.requesterId;
    transaction.taskId = this.task.id;
    transaction.label = "closeQuestionTransaction";

    final var checkTask = this.createTaskPredicate().and(TaskPredicates.transactionSizeIs(9))
        .and(TaskPredicates.transactionAt(8,
            this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(transaction))))
        .and(TaskPredicates.isClosed());

    WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction)
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask))
        .onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

  }

}
