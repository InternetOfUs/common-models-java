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

import eu.internetofus.common.components.interaction_protocol_engine.State;
import eu.internetofus.common.components.models.Message;
import eu.internetofus.common.components.models.Task;
import eu.internetofus.common.components.models.TaskTransaction;
import eu.internetofus.common.components.task_manager.TaskPredicates;
import eu.internetofus.common.components.task_manager.TaskTransactionPredicates;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
public class Ask4HelpV2_2ProtocolWithAllFiltersITC extends AbstractAsk4HelpV2_2ProtocolITC {

  /**
   * The number maximum users to ask.
   */
  private static final int MAX_USERS = 7;

  /**
   * The expected state after the action is done.
   */
  protected JsonObject expectedState;

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
   */
  @Override
  protected Task createTaskForProtocol() {

    final var task = super.createTaskForProtocol();
    task.attributes.put("domain", "studying_career").put("domainInterest", "similar").put("beliefsAndValues", "similar")
        .put("maxUsers", MAX_USERS);

    return task;
  }

  /**
   * Create the expected state.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(5)
  public void shouldCreateExpectedState(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(4, testContext);

    this.expectedState = new JsonObject();
    final var whoToAskUsers = new JsonArray();
    this.expectedState.put("whoToAskUsers", whoToAskUsers);
    final var unaskedUserIds = new JsonArray();
    this.expectedState.put("unaskedUserIds", unaskedUserIds);
    final var domainInterestUsers = new JsonArray();
    this.expectedState.put("domainInterestUsers", domainInterestUsers);
    final var beliefsAndValuesUsers = new JsonArray();
    this.expectedState.put("beliefsAndValuesUsers", beliefsAndValuesUsers);

    final var max = this.users.size();
    final var requester = this.users.get(0);
    final List<JsonObject> totals = new ArrayList<>();
    for (var i = 1; i < max; i++) {

      final var user = this.users.get(i);

      final var beliefValue = 1.0 - Math.abs(requester.meanings.get(0).level - user.meanings.get(0).level) / 2.0;
      final var domainValue = 1.0 - Math.abs(requester.competences.get(0).level - user.competences.get(0).level) / 2.0;
      final var total = beliefValue * domainValue;
      domainInterestUsers.add(new JsonObject().put("userId", user.id).put("value", domainValue));
      beliefsAndValuesUsers.add(new JsonObject().put("userId", user.id).put("value", beliefValue));
      totals.add(new JsonObject().put("userId", user.id).put("value", total));

    }

    Collections.sort(totals, (a, b) -> b.getDouble("value").compareTo(a.getDouble("value")));
    for (final var whoToAskUser : totals) {

      whoToAskUsers.add(whoToAskUser);
      if (whoToAskUsers.size() > MAX_USERS) {

        unaskedUserIds.add(whoToAskUser.getString("userId"));

      }

    }

    this.assertSuccessfulCompleted(testContext);

  }

  /**
   * Check that a state attribute is equals to the expected.
   *
   * @param state         to check.
   * @param attributeName name of the attribute to compare.
   * @param ordered       is {@code true} if the order is important.
   *
   * @return {@code true} if the expected attribute is equals.
   */
  private boolean assertExpectedTaskStateAttribute(final State state, final String attributeName,
      final boolean ordered) {

    final var value = state.attributes.getJsonArray(attributeName);
    final var expected = this.expectedState.getJsonArray(attributeName);
    if (value == null || expected.size() != value.size()) {

      return false;

    } else if (!ordered) {

      final var copy = new JsonArray(expected.toString());
      final var max = copy.size();
      for (var i = 0; i < max; i++) {

        final var element = value.getValue(i);
        for (var j = 0; j < copy.size(); j++) {

          final var expectedElement = copy.getValue(j);
          if (this.compareUserValues(expectedElement, element)) {

            copy.remove(j);
            break;
          }

        }
      }

      return copy.isEmpty();

    } else {

      final var max = expected.size();
      for (var i = 0; i < max; i++) {

        final var element = value.getValue(i);
        final var expectedElement = expected.getValue(i);
        if (!this.compareUserValues(expectedElement, element)) {

          return false;
        }
      }

      return true;
    }

  }

  /**
   * Compare users values.
   *
   * @param a first value to compare.
   * @param b second value to compare.
   *
   * @return {2code true} if the values are equals.
   */
  private boolean compareUserValues(final Object a, final Object b) {

    if (a instanceof JsonObject) {

      final var source = (JsonObject) a;
      final var target = (JsonObject) b;

      return source.getString("userId", "").equals(target.getString("userId"))
          && Math.abs(source.getDouble("value", -1d) - target.getDouble("value", -2d)) < 0.0000001d;

    } else {

      return a == b || a != null && a.equals(b);

    }

  }

  /**
   * Check that a task is created.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(6)
  public void shouldCreateTask(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(5, testContext);

    final var source = this.createTaskForProtocol();
    final var checkMessages = new ArrayList<Predicate<Message>>();

    final var maxUsers = source.attributes.getInteger("maxUsers");
    final var whoToAskUsers = this.expectedState.getJsonArray("whoToAskUsers");
    for (var i = 0; i < maxUsers; i++) {

      final var receiverId = whoToAskUsers.getJsonObject(i).getString("userId");
      checkMessages.add(this.createMessagePredicateForQuestionToAnswerMessageBy(receiverId));

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
        .compose(ignored -> this.waitUntilUserTaskState(source.requesterId, vertx, testContext, userTaskState -> {

          if (userTaskState.attributes != null) {

            return this.assertExpectedTaskStateAttribute(userTaskState, "whoToAskUsers", true)
                && this.assertExpectedTaskStateAttribute(userTaskState, "beliefsAndValuesUsers", false)
                && this.assertExpectedTaskStateAttribute(userTaskState, "domainInterestUsers", false)
                && this.assertExpectedTaskStateAttribute(userTaskState, "unaskedUserIds", true);

          } else {

            return false;

          }

        }));
    future.onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

  }

  /**
   * Check that the requester can ask for more answers.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(7)
  public void shouldAskForMoreAnswers(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(6, testContext);

    final var checkMessages = new ArrayList<Predicate<Message>>();
    final var usersToAskIds = this.expectedState.getJsonArray("unaskedUserIds");
    for (var i = 0; i < usersToAskIds.size(); i++) {

      final var receiverId = usersToAskIds.getString(i);
      checkMessages.add(this.createMessagePredicateForQuestionToAnswerMessageBy(receiverId));

    }

    final var moreAnswerTransaction = new TaskTransaction();
    moreAnswerTransaction.taskId = this.task.id;
    moreAnswerTransaction.actioneerId = this.task.requesterId;
    moreAnswerTransaction.label = "moreAnswerTransaction";
    final var checkTask = this.createTaskPredicate()
        .and(TaskPredicates.lastTransactionIs(
            this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(moreAnswerTransaction))
                .and(TaskTransactionPredicates.messagesSizeIs(checkMessages.size()))
                .and(TaskTransactionPredicates.containsMessages(checkMessages))));

    final Future<?> future = WeNetTaskManager.createProxy(vertx).doTaskTransaction(moreAnswerTransaction)
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask))
        .compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages))
        .compose(ignored -> this.waitUntilUserTaskState(this.task.requesterId, vertx, testContext, userTaskState -> {

          if (userTaskState.attributes != null) {

            final var unaskedUserIds = userTaskState.attributes.getJsonArray("unaskedUserIds");
            return unaskedUserIds != null && unaskedUserIds.size() == 0;

          } else {

            return false;
          }

        }));
    future.onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

  }

}
