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
import eu.internetofus.common.components.models.SocialNetworkRelationship;
import eu.internetofus.common.components.models.SocialNetworkRelationshipType;
import eu.internetofus.common.components.models.Task;
import eu.internetofus.common.components.models.TaskTransaction;
import eu.internetofus.common.components.personal_context_builder.UserLocation;
import eu.internetofus.common.components.personal_context_builder.WeNetPersonalContextBuilderSimulator;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.components.task_manager.TaskPredicates;
import eu.internetofus.common.components.task_manager.TaskTransactionPredicates;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
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
public class Ask4HelpV2ProtocolWithAllFiltersITC extends AbstractAsk4HelpV2ProtocolITC {

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
    task.attributes.put("domain", "varia_misc").put("domainInterest", "similar").put("beliefsAndValues", "similar")
        .put("socialCloseness", "similar").put("positionOfAnswerer", "nearby");

    return task;
  }

  /**
   * Store the location of the users.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(5)
  public void shouldStoreUserLocations(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(4, testContext);

    Future<?> future = Future.succeededFuture();
    final var max = this.users.size();
    for (var i = 0; i < max; i++) {

      final var userId = this.users.get(i).id;
      final var location = new UserLocation();
      location.userId = userId;
      location.latitude = 0.5 * i;
      location.longitude = 0.5 * i;
      future = future
          .compose(ignored -> WeNetPersonalContextBuilderSimulator.createProxy(vertx).addUserLocation(location));

    }

    future.onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

  }

  /**
   * Store the relationships with the requester profile.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(6)
  public void shouldStoreRelationships(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(5, testContext);

    final var profile = this.users.get(0);
    profile.relationships = new ArrayList<>();
    final var max = this.users.size();
    for (var i = 1; i < max; i++) {

      final var relationship = new SocialNetworkRelationship();
      relationship.appId = this.app.appId;
      relationship.type = SocialNetworkRelationshipType.values()[i % SocialNetworkRelationshipType.values().length];
      relationship.userId = this.users.get(i).id;
      relationship.weight = 0.1 * (max - i);
      profile.relationships.add(relationship);

    }

    WeNetProfileManager.createProxy(vertx).updateProfile(profile).map(updated -> {
      this.users.remove(0);
      this.users.add(0, updated);
      return null;
    }).onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

  }

  /**
   * Modify the profiles of the users to have some domain values.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(7)
  public void shouldFillInDomain(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(6, testContext);

    final Future<?> future = Future.succeededFuture();
    for (var i = 0; i < this.users.size(); i++) {

    }

    future.onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

  }

  /**
   * Modify the profiles of the users to have some believe and values.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(8)
  public void shouldFillInBalieveAndValues(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(7, testContext);

    final Future<?> future = Future.succeededFuture();
    for (var i = 0; i < this.users.size(); i++) {

    }

    future.onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

  }

  /**
   * Check if a list of users is valid.
   *
   * @param userValues to validate.
   *
   * @return {@code true} if the list of users is valid.
   */
  private boolean checkUserListIsValid(final JsonArray userValues) {

    final var max = this.users.size();
    if (userValues == null || userValues.size() != max - 1) {

      return false;

    } else {

      var previousValue = 1.0d;
      for (var i = 1; i < max; i++) {

        final var userId = this.users.get(i).id;
        final var maxElements = userValues.size();
        for (var j = 0; j < maxElements; j++) {

          final var element = userValues.getJsonObject(j);
          if (element == null) {

            return false;
          }
          if (userId.equals(element.getString("userId"))) {

            final var elementValue = element.getDouble("value", -1d);
            if (elementValue < 0.0d || elementValue > previousValue) {

              return false;
            }
            previousValue = elementValue;
            break;
          }

        }

      }

      return true;
    }

  }

  /**
   * Check that a task is created.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(9)
  public void shouldCreateTask(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(8, testContext);

    final var source = this.createTaskForProtocol();
    final var checkMessages = new ArrayList<Predicate<Message>>();

    final var maxUsers = source.attributes.getInteger("maxUsers");
    for (var i = 1; i <= maxUsers; i++) {

      final var receiverId = this.users.get(i).id;
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

            final var whoToAskUsers = userTaskState.attributes.getJsonArray("whoToAskUsers");
            if (!this.checkUserListIsValid(whoToAskUsers)
                || !this.checkUserListIsValid(userTaskState.attributes.getJsonArray("closenessUsers"))
                || !this.checkUserListIsValid(userTaskState.attributes.getJsonArray("socialClosenessUsers"))
                || !this.checkUserListIsValid(userTaskState.attributes.getJsonArray("beliefsAndValuesUsers"))
                || !this.checkUserListIsValid(userTaskState.attributes.getJsonArray("domainInterestUsers"))) {

              return false;
            }

            final var max = whoToAskUsers.size();
            for (var i = 0; i < max; i++) {

              final var element = whoToAskUsers.getJsonObject(i);
              final var userId = this.users.get(i + 1).id;
              if (!userId.equals(element.getString("userId"))) {

                return false;
              }
            }

            final var unaskedUserIds = userTaskState.attributes.getJsonArray("unaskedUserIds");
            if (unaskedUserIds == null || unaskedUserIds.size() != this.users.size() - 1 - maxUsers) {

              return false;
            }
            for (var i = 0; i < unaskedUserIds.size(); i++) {

              final var element = unaskedUserIds.getString(i);
              final var userId = this.users.get(maxUsers + i + 1).id;
              if (!userId.equals(element)) {

                return false;
              }
            }

            return true;
          }

          return false;

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
  @Order(10)
  public void shouldAskForMoreAnswers(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(9, testContext);

    final var checkMessages = new ArrayList<Predicate<Message>>();
    final var maxUsers = this.task.attributes.getInteger("maxUsers");
    for (var i = maxUsers + 1; i < this.users.size(); i++) {

      final var receiverId = this.users.get(i).id;
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
