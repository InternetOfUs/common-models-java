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

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.interaction_protocol_engine.StatePredicates;
import eu.internetofus.common.components.models.Message;
import eu.internetofus.common.components.models.SocialNetworkRelationship;
import eu.internetofus.common.components.models.SocialNetworkRelationshipType;
import eu.internetofus.common.components.models.Task;
import eu.internetofus.common.components.models.TaskTransaction;
import eu.internetofus.common.components.models.WeNetUserProfile;
import eu.internetofus.common.components.personal_context_builder.UserLocation;
import eu.internetofus.common.components.personal_context_builder.WeNetPersonalContextBuilderSimulator;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.components.service.MessagePredicates;
import eu.internetofus.common.components.task_manager.TaskPredicates;
import eu.internetofus.common.components.task_manager.TaskTransactionPredicates;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.tinylog.Logger;

/**
 * Component that provide the common component to check the M46 pilots
 * protocols.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractPilotM46ProtocolITC extends AbstractDefaultProtocolITC {

  /**
   * The identifier of the users that has asked to participate.
   */
  protected List<String> participants = new ArrayList<>();

  /**
   * {@inheritDoc}
   */
  @Override
  protected int numberOfUsersToCreate() {

    return 11;
  }

  /**
   * Check if the index refers to an empty profile.
   *
   * @param index of the user to check.
   *
   * @return {@code true} if theuser at the specified index is empty.
   */
  public boolean isEmptyProfile(final int index) {

    return index > 7;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Future<WeNetUserProfile> createProfileFor(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    if (this.isEmptyProfile(index)) {

      return StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext);

    } else {

      return super.createProfileFor(index, vertx, testContext);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Task createTaskForProtocol() {

    final var taskToCreate = super.createTaskForProtocol();
    taskToCreate.goal.name = "Where to buy the best pizza?";
    taskToCreate.attributes = new JsonObject().put("domain", this.domain()).put("domainInterest", this.domainInterest())
        .put("beliefsAndValues", this.beliefsAndValues()).put("sensitive", this.sensitive())
        .put("anonymous", this.anonymous()).put("socialCloseness", this.socialCloseness())
        .put("maxUsers", this.maxUsers()).put("maxAnswers", this.maxAnswers())
        .put("expirationDate", this.expirationDate());
    if (this.useGeolocation()) {

      taskToCreate.attributes = taskToCreate.attributes.put("positionOfAnswerer", this.positionOfAnswerer());
    }
    return taskToCreate;

  }

  /**
   * Return the domain of the task
   *
   * @return the domain for the task.
   */
  protected abstract String domain();

  /**
   * Return the domain interest of the task.
   *
   * @return the domain interest of the task
   */
  protected abstract String domainInterest();

  /**
   * Return the beliefs and values of the task.
   *
   * @return the beliefs and values of the task
   */
  protected abstract String beliefsAndValues();

  /**
   * Return if the question is sensitive of the task.
   *
   * @return {@code true} if the question of the task is sensitive.
   */
  protected abstract boolean sensitive();

  /**
   * Return if the question is anonymous of the task.
   *
   * @return {@code true} if the question of the task is anonymous.
   */
  protected abstract boolean anonymous();

  /**
   * Return the social closeness of the task.
   *
   * @return the social closeness of the task
   */
  protected abstract String socialCloseness();

  /**
   * Check if has to use the user location.
   *
   * @return {@code true} if the
   */
  protected boolean useGeolocation() {

    return this.positionOfAnswerer() != null;
  }

  /**
   * Return the position of answerer of the task.
   *
   * @return the position of answerer of the task
   */
  protected abstract String positionOfAnswerer();

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
   * Return the maximum distance between users.
   *
   * @return {@code 500} in any case
   */
  public double maxDistance() {

    return 500;
  }

  /**
   * The maximum number of user that has a social closeness.
   */
  private static final int MAX_SOCIAL_CLOSENESS_INDEX = 8;

  /**
   * Return the distance between the requester and the user at the specified
   * position.
   *
   * @param index of the user to calculate the distance.
   *
   * @return the distance in km to the user of the specified position.
   */
  public double socialClosenessTo(final int index) {

    if (index < MAX_SOCIAL_CLOSENESS_INDEX) {

      return Math.max(0, 1.0 - (index + 0.5));

    } else {

      return 0.5;
    }

  }

  /**
   * Check that a task is created.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(5)
  public void shouldFillSocialCloseness(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(4, testContext);

    @SuppressWarnings("rawtypes")
    final List<Future> added = new ArrayList<>();
    for (var i = 1; i < MAX_SOCIAL_CLOSENESS_INDEX; i++) {

      final var relationship = new SocialNetworkRelationship();
      relationship.appId = this.app.appId;
      relationship.sourceId = this.users.get(0).id;
      relationship.targetId = this.users.get(i).id;
      relationship.weight = this.socialClosenessTo(i);
      relationship.type = SocialNetworkRelationshipType.values()[i
          % (SocialNetworkRelationshipType.values().length - 1)];
      added.add(WeNetProfileManager.createProxy(vertx).addOrUpdateSocialNetworkRelationship(relationship));
    }

    CompositeFuture.all(added)
        .onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));
  }

  /**
   * Return the distance between the requester and the user at the specified
   * position.
   *
   * @param index of the user to calculate the distance.
   *
   * @return the distance in km to the user of the specified position.
   */
  public double distanceTo(final int index) {

    return 0.08 * index;

  }

  /**
   * Check that a task is created.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(6)
  public void shouldFillLocation(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(5, testContext);

    if (this.useGeolocation()) {

      @SuppressWarnings("rawtypes")
      final List<Future> added = new ArrayList<>();
      for (var i = 0; i < this.users.size(); i++) {

        final var location = new UserLocation();
        location.userId = this.users.get(i).id;
        location.latitude = 0.0;
        location.longitude = i * 0.0007;
        added.add(WeNetPersonalContextBuilderSimulator.createProxy(vertx).addUserLocation(location));

      }

      CompositeFuture.all(added)
          .onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

    } else {

      this.assertSuccessfulCompleted(testContext);
    }
  }

  /**
   * Check that a task is created.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(7)
  public void shouldCreateTask(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(6, testContext);

    final var source = this.createTaskForProtocol();

    final var checkMessages = new ArrayList<Predicate<Message>>();
    final var maxMessages = this.maxMessagesOnCreation();
    for (var i = 0; i < maxMessages; i++) {

      checkMessages.add(this.createMessagePredicate().and(MessagePredicates.labelIs("QuestionToAnswerMessage"))
          .and(MessagePredicates.attributesSimilarTo(new JsonObject().put("question", source.goal.name)
              .put("userId", source.requesterId).put("sensitive", this.sensitive()).put("anonymous", this.anonymous())))
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

    final var taskState = this.createTaskUserStatePredicate(source.requesterId)
        .and(StatePredicates.attributesAre(this::validTaskUserStateAfterCreation));

    final Future<?> future = this.waitUntilTaskCreated(source, vertx, testContext, checkTask)
        .compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages))
        .compose(ignored -> this.waitUntilUserTaskState(source.requesterId, vertx, testContext, taskState));
    future.onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));
  }

  /**
   * The max messages that will send when the task is created.
   *
   * @return the number of messages to send when the task is created.
   */
  public int maxMessagesOnCreation() {

    return Math.min(this.numberOfUsersToCreate() - 1, this.maxUsers());
  }

  /**
   * Check that the task has the expected state.
   *
   * @param state to check.
   *
   * @return {@code true} if the state is the expected after the task is created.
   */
  protected boolean validTaskUserStateAfterCreation(final JsonObject state) {

    final var appUsers = state.getJsonArray("appUsers", new JsonArray());
    final var closenessUsers = state.getJsonArray("closenessUsers", new JsonArray());
    final var socialClosenessUsers = state.getJsonArray("socialClosenessUsers", new JsonArray());
    final var beliefsAndValuesUsers = state.getJsonArray("beliefsAndValuesUsers", new JsonArray());
    final var domainInterestUsers = state.getJsonArray("domainInterestUsers", new JsonArray());
    final var matchUsers = state.getJsonArray("matchUsers", new JsonArray());
    final var rankedUsers = state.getJsonArray("rankedUsers", new JsonArray());
    final var unaskedUserIds = state.getJsonArray("unaskedUserIds", new JsonArray());

    return this.validAppUsers(appUsers) && this.validClosenessUsersUsers(closenessUsers)
        && this.validSocialClosenessUsers(socialClosenessUsers)
        && this.validBeliefsAndValuesUsers(beliefsAndValuesUsers) && this.validDomainInterestUsers(domainInterestUsers)
        && this.validMatchUsers(matchUsers) && this.validRankedUsers(matchUsers, rankedUsers)
        && this.validUnaskedUsers(rankedUsers, unaskedUserIds);
  }

  /**
   * Check that the appUsers is valid.
   *
   * @param appUsers to check.
   *
   * @return {@code true} if the appUsers is the expected after the task is
   *         created.
   */
  protected boolean validAppUsers(final JsonArray appUsers) {

    for (final var user : this.users) {

      if (!appUsers.contains(user.id)) {
        // Not found user
        return false;
      }
    }

    return true;
  }

  /**
   * Check that the closenessUsers is valid.
   *
   * @param closenessUsers to check.
   *
   * @return {@code true} if the closenessUsers is the expected after the task is
   *         created.
   */
  protected boolean validClosenessUsersUsers(final JsonArray closenessUsers) {

    if (!this.useGeolocation()) {

      return closenessUsers.isEmpty();
    }

    if (this.users.size() - 1 != closenessUsers.size()) {
      // Unexpected size
      Logger.warn("Unexpected closenessUsers user size.");
      return false;
    }

    if ("nearby".equals(this.positionOfAnswerer())) {

      for (var i = 1; i < this.users.size(); i++) {

        final var user = this.users.get(i);
        final var closenessUser = closenessUsers.getJsonObject(i - 1);
        if (!user.id.equals(closenessUser.getString("userId"))) {

          Logger.warn("Unexpected closenessUsers user at {}, {} is not {}.", i, user.id, closenessUser);
          return false;
        }

        final var distance = Math.max(0.0, (this.maxDistance() - this.distanceTo(i)) / this.maxDistance());
        if (Math.abs(distance - closenessUser.getDouble("value")) < Double.MIN_NORMAL) {

          Logger.warn("Unexpected closenessUsers user at {}, {} is not {}.", i, distance, closenessUser);
          return false;
        }

      }

    }

    return true;
  }

  /**
   * Check that the socialClosenessUsers is valid.
   *
   * @param socialClosenessUsers to check.
   *
   * @return {@code true} if the socialClosenessUsers is the expected after the
   *         task is created.
   */
  protected boolean validSocialClosenessUsers(final JsonArray socialClosenessUsers) {

    if (this.users.size() - 1 != socialClosenessUsers.size()) {
      // Unexpected size
      Logger.warn("Unexpected socialClosenessUsers user size.");
      return false;
    }

    final var type = this.socialCloseness();
    if ("similar".equals(type)) {

      for (var i = 1; i < this.users.size(); i++) {

        final var user = this.users.get(i);
        final var userValueObject = socialClosenessUsers.getJsonObject(i - 1);
        if (i < MAX_SOCIAL_CLOSENESS_INDEX) {
          if (!user.id.equals(userValueObject.getString("userId"))) {

            Logger.warn("Unexpected socialClosenessUsers user at {}, {} is not {}.", i, user.id, userValueObject);
            return false;
          }
        }

        final var weight = this.socialClosenessTo(i);
        if (Math.abs(weight - userValueObject.getDouble("value")) < Double.MIN_NORMAL) {

          Logger.warn("Unexpected socialClosenessUsers user at {}, {} is not {}.", i, weight, userValueObject);
          return false;
        }

      }

    } else if ("different".equals(type)) {

      for (var i = 1; i < this.users.size(); i++) {

        final var user = this.users.get(i);
        final var userValueObject = socialClosenessUsers.getJsonObject(socialClosenessUsers.size() - i - 1);
        if (i < MAX_SOCIAL_CLOSENESS_INDEX) {
          if (!user.id.equals(userValueObject.getString("userId"))) {

            Logger.warn("Unexpected socialClosenessUsers user at {}, {} is not {}.", i, user.id, userValueObject);
            return false;
          }
        }

        final var weight = 1.0 - this.socialClosenessTo(i);
        if (Math.abs(weight - userValueObject.getDouble("value")) < Double.MIN_NORMAL) {

          Logger.warn("Unexpected socialClosenessUsers user at {}, {} is not {}.", i, weight, userValueObject);
          return false;
        }

      }

    } else {

      return this.validIndifferent("socialCloseness", socialClosenessUsers);
    }

    return true;
  }

  /**
   * Check that the user values list is indifferent.
   *
   * @param type       of array.
   * @param userValues that is indifferent.
   *
   * @return {@code true} if array contains the indifferent values.
   */
  protected boolean validIndifferent(final String type, final JsonArray userValues) {

    for (var i = 0; i < userValues.size(); i++) {

      final var userValueObject = userValues.getJsonObject(i);

      var found = false;
      for (var j = 1; j < this.users.size(); j++) {

        final var user = this.users.get(j);
        if (user.id.equals(userValueObject.getString("userId"))) {

          found = true;
          break;
        }

      }
      if (!found) {

        Logger.warn("Unexpected {} user at {} is not defined {}.", type, i, userValueObject);
      }

      if (Math.abs(1.0 - userValueObject.getDouble("value")) < Double.MIN_NORMAL) {

        Logger.warn("Unexpected {} user at {}, 1.0 is not {}.", type, i, userValueObject);
        return false;
      }

    }

    return true;
  }

  /**
   * Return the believe and values between the requester and the user at the
   * specified position.
   *
   * @param index of the user to calculate the distance.
   *
   * @return the believe and values similarity.
   */
  public double beliefsAndValuesTo(final int index) {

    return 0.08 * index;

  }

  /**
   * Check that the beliefsAndValuesUsers is valid.
   *
   * @param beliefsAndValuesUsers to check.
   *
   * @return {@code true} if the beliefsAndValuesUsers is the expected after the
   *         task is created.
   */
  protected boolean validBeliefsAndValuesUsers(final JsonArray beliefsAndValuesUsers) {

    if (this.users.size() - 1 != beliefsAndValuesUsers.size()) {
      // Unexpected size
      Logger.warn("Unexpected beliefsAndValuesUsers user size.");
      return false;
    }

    final var type = this.beliefsAndValues();
    if ("similar".equals(type)) {

      for (var i = 1; i < this.users.size(); i++) {

        final var user = this.users.get(i);
        final var userValueObject = beliefsAndValuesUsers.getJsonObject(i - 1);
        if (i < MAX_SOCIAL_CLOSENESS_INDEX) {
          if (!user.id.equals(userValueObject.getString("userId"))) {

            Logger.warn("Unexpected beliefsAndValues user at {}, {} is not {}.", i, user.id, userValueObject);
            return false;
          }
        }

        final var weight = this.beliefsAndValuesTo(i);
        if (Math.abs(weight - userValueObject.getDouble("value")) < Double.MIN_NORMAL) {

          Logger.warn("Unexpected beliefsAndValues user at {}, {} is not {}.", i, weight, userValueObject);
          return false;
        }

      }

    } else if ("different".equals(type)) {

      for (var i = 1; i < this.users.size(); i++) {

        final var user = this.users.get(i);
        final var userValueObject = beliefsAndValuesUsers.getJsonObject(beliefsAndValuesUsers.size() - i - 1);
        if (i < MAX_SOCIAL_CLOSENESS_INDEX) {
          if (!user.id.equals(userValueObject.getString("userId"))) {

            Logger.warn("Unexpected beliefsAndValues user at {}, {} is not {}.", i, user.id, userValueObject);
            return false;
          }
        }

        final var weight = 1.0 - this.beliefsAndValuesTo(i);
        if (Math.abs(weight - userValueObject.getDouble("value")) < Double.MIN_NORMAL) {

          Logger.warn("Unexpected beliefsAndValues user at {}, {} is not {}.", i, weight, userValueObject);
          return false;
        }

      }

    } else {

      return this.validIndifferent("beliefsAndValues", beliefsAndValuesUsers);
    }

    return true;
  }

  /**
   * Return the believe and values between the requester and the user at the
   * specified position.
   *
   * @param index of the user to calculate the distance.
   *
   * @return the believe and values similarity.
   */
  public double domainInterestTo(final int index) {

    return 0.08 * index;

  }

  /**
   * Check that the domainInterestUsers is valid.
   *
   * @param domainInterestUsers to check.
   *
   * @return {@code true} if the domainInterestUsers is the expected after the
   *         task is created.
   */
  protected boolean validDomainInterestUsers(final JsonArray domainInterestUsers) {

    if (this.users.size() - 1 != domainInterestUsers.size()) {
      // Unexpected size
      Logger.warn("Unexpected domainInterestUsers user size.");
      return false;
    }

    final var type = this.domainInterest();
    if ("similar".equals(type)) {

      for (var i = 1; i < this.users.size(); i++) {

        final var user = this.users.get(i);
        final var userValueObject = domainInterestUsers.getJsonObject(i - 1);
        if (i < MAX_SOCIAL_CLOSENESS_INDEX) {
          if (!user.id.equals(userValueObject.getString("userId"))) {

            Logger.warn("Unexpected domainInterest user at {}, {} is not {}.", i, user.id, userValueObject);
            return false;
          }
        }

        final var weight = this.domainInterestTo(i);
        if (Math.abs(weight - userValueObject.getDouble("value")) < Double.MIN_NORMAL) {

          Logger.warn("Unexpected domainInterest user at {}, {} is not {}.", i, weight, userValueObject);
          return false;
        }

      }

    } else if ("different".equals(type)) {

      for (var i = 1; i < this.users.size(); i++) {

        final var user = this.users.get(i);
        final var userValueObject = domainInterestUsers.getJsonObject(domainInterestUsers.size() - i - 1);
        if (i < MAX_SOCIAL_CLOSENESS_INDEX) {
          if (!user.id.equals(userValueObject.getString("userId"))) {

            Logger.warn("Unexpected domainInterest user at {}, {} is not {}.", i, user.id, userValueObject);
            return false;
          }
        }

        final var weight = 1.0 - this.domainInterestTo(i);
        if (Math.abs(weight - userValueObject.getDouble("value")) < Double.MIN_NORMAL) {

          Logger.warn("Unexpected domainInterest user at {}, {} is not {}.", i, weight, userValueObject);
          return false;
        }

      }

    } else {

      return this.validIndifferent("domainInterest", domainInterestUsers);
    }

    return true;
  }

  /**
   * Check that the matchUsers is valid.
   *
   * @param matchUsers to check.
   *
   * @return {@code true} if the matchUsers is the expected after the task is
   *         created.
   */
  protected abstract boolean validMatchUsers(final JsonArray matchUsers);

  /**
   * Check that the rankedUsers is valid.
   *
   * @param matchUsers  that has been found.
   * @param rankedUsers to check.
   *
   * @return {@code true} if the rankedUsers is the expected after the task is
   *         created.
   */
  protected boolean validRankedUsers(final JsonArray matchUsers, final JsonArray rankedUsers) {

    final var max = matchUsers.size();
    if (max != rankedUsers.size()) {
      // Unexpected size
      Logger.warn("Unexpected rankedUsers user size.");
      return false;
    }

    for (var i = 0; i < max; i++) {

      final var matchUser = matchUsers.getJsonObject(i);
      final var matchUserId = matchUser.getString("userId");
      final var rankedUserId = rankedUsers.getString(i);
      if (matchUserId.equals(rankedUserId)) {
        // Unexpected user id
        Logger.warn("Unexpected user at {} , {} is not equals to {} ", i, matchUserId, rankedUserId);
        return false;
      }
    }

    return true;
  }

  /**
   * Check that the unaskedUsers is valid.
   *
   * @param rankedUsers  that has been found.
   * @param unaskedUsers to check.
   *
   * @return {@code true} if the unaskedUsers is the expected after the task is
   *         created.
   */
  protected boolean validUnaskedUsers(final JsonArray rankedUsers, final JsonArray unaskedUsers) {

    final var start = this.maxMessagesOnCreation();
    final var max = rankedUsers.size();
    if (max != unaskedUsers.size() + start) {
      // Unexpected size
      Logger.warn("Unexpected unaskedUsers user size.");
      return false;
    }

    for (var i = start; i < max; i++) {

      final var rankedUserId = rankedUsers.getString(i);
      final var unaskedUserId = unaskedUsers.getString(i);
      if (unaskedUserId.equals(rankedUserId)) {
        // Unexpected user id
        Logger.warn("Unexpected user at {} , {} is not equals to {} ", i, rankedUserId, unaskedUserId);
        return false;
      }
    }

    return true;
  }

}
