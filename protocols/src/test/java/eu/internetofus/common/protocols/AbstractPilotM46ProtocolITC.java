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
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
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
        .put("beliefsAndValues", this.beliefsAndValues()).put("positionOfAnswerer", this.positionOfAnswerer())
        .put("socialCloseness", this.socialCloseness()).put("sensitive", this.sensitive())
        .put("anonymous", this.anonymous()).put("maxUsers", this.maxUsers()).put("maxAnswers", this.maxAnswers())
        .put("expirationDate", this.expirationDate());
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
   * Return the distance between the requester and the user at the specified
   * position.
   *
   * @param index of the user to calculate the distance.
   *
   * @return the distance in km to the user of the specified position.
   */
  public Double socialClosenessTo(final int index) {

    if (this.hasSocialClosenessTo(index)) {

      return Math.max(0d, 0.6d - index * 0.01d);

    } else {

      return this.indifferentValue();
    }

  }

  /**
   * Check if a user has asocial closeness.
   *
   * @param index of the user to check.
   *
   * @return {@code true} if the user at the index has a social closeness.
   */
  protected boolean hasSocialClosenessTo(final int index) {

    return index < this.users.size() - 1;
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
    for (var i = 1; i < this.users.size() - 1; i++) {

      if (this.hasSocialClosenessTo(i)) {

        final var relationship = new SocialNetworkRelationship();
        relationship.appId = this.app.appId;
        relationship.sourceId = this.users.get(0).id;
        relationship.targetId = this.users.get(i).id;
        relationship.weight = this.socialClosenessTo(i);
        relationship.type = SocialNetworkRelationshipType.values()[i
            % (SocialNetworkRelationshipType.values().length - 1)];
        added.add(WeNetProfileManager.createProxy(vertx).addOrUpdateSocialNetworkRelationship(relationship));
      }
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
    final var checkCreateTask = this.createTaskPredicate().and(TaskPredicates.similarTo(source))
        .and(TaskPredicates.transactionSizeIs(1)).and(TaskPredicates.transactionAt(0,
            this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(createTransaction))));
    final var checkTask = this.createTaskPredicate()
        .and(TaskPredicates.transactionAt(0,
            this.createTaskTransactionPredicate().and(TaskTransactionPredicates.messagesSizeIs(maxMessages))
                .and(TaskTransactionPredicates.containsMessages(checkMessages))));

    final var taskState = this.createTaskUserStatePredicate(source.requesterId)
        .and(StatePredicates.attributesAre(this::validTaskUserStateAfterCreation));

    final Future<?> future = this.waitUntilTaskCreated(source, vertx, testContext, checkCreateTask)
        .compose(ignored -> this.waitUntilUserTaskState(source.requesterId, vertx, testContext, taskState))
        .compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages))
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask));
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
   * Check if the use has selected to ask to users that are nearby.
   *
   * @return {@code true} if the position to answers is nearby.
   */
  protected boolean isNearbySelected() {

    return "nearby".equals(this.positionOfAnswerer());
  }

  /**
   * Check that the task has the expected state.
   *
   * @param state to check.
   *
   * @return {@code true} if the state is the expected after the task is created.
   */
  protected boolean validTaskUserStateAfterCreation(final JsonObject state) {

    if (!state.containsKey("appUsers")) {

      Logger.warn("Not appUsers is not defined on the state.");
      return false;
    }
    if (!state.containsKey("closenessUsers") && this.isNearbySelected()) {

      Logger.warn("Not closenessUsers is not defined on the state.");
      return false;
    }
    if (!state.containsKey("socialClosenessUsers")) {

      Logger.warn("Not socialClosenessUsers is not defined on the state.");
      return false;
    }
    if (!state.containsKey("beliefsAndValuesUsers")) {

      Logger.warn("Not beliefsAndValuesUsers is not defined on the state.");
      return false;
    }
    if (!state.containsKey("domainInterestUsers")) {

      Logger.warn("Not domainInterestUsers is not defined on the state.");
      return false;
    }
    if (!state.containsKey("matchUsers")) {

      Logger.warn("Not matchUsers is not defined on the state.");
      return false;
    }
    if (!state.containsKey("rankedUsers")) {

      Logger.warn("Not rankedUsers is not defined on the state.");
      return false;
    }
    if (!state.containsKey("unaskedUserIds")) {

      Logger.warn("Not unaskedUserIds is not defined on the state.");
      return false;
    }

    return this.validAppUsers(state) && this.validClosenessUsers(state) && this.validSocialClosenessUsers(state)
        && this.validBeliefsAndValuesUsers(state) && this.validDomainInterestUsers(state) && this.validMatchUsers(state)
        && this.validRankedUsers(state) && this.validUnaskedUsersIds(state);
  }

  /**
   * Check that the appUsers is valid.
   *
   * @param state where is the users to check.
   *
   * @return {@code true} if the appUsers is the expected after the task is
   *         created.
   */
  protected boolean validAppUsers(final JsonObject state) {

    final var appUsers = state.getJsonArray("appUsers", new JsonArray());
    final var appUsersSize = appUsers.size();
    final var usersSize = this.users.size();
    if (appUsersSize != usersSize - 1) {
      // Unexpected size
      Logger.warn("Unexpected app users size, {} != {}", usersSize, appUsersSize);
      return false;
    }

    for (var i = 0; i < appUsersSize; i++) {

      final var appUser = appUsers.getString(i);
      if (this.getUserById(appUser) == null) {

        Logger.warn("The app user {} at {} is not a defined user.", appUser, i);
        return false;
      }
    }

    return true;
  }

  /**
   * Check if two double values are similar.
   *
   * @param source value to compare.
   * @param target value to compare.
   *
   * @return {@code true} if the two double values are similar.
   */
  protected boolean areSimilar(final Double source, final Double target) {

    if (source == null) {

      return target == null;

    } else if (target == null) {

      return false;

    } else {

      return Math.abs(source - target) < Double.MIN_NORMAL;
    }

  }

  /**
   * Check that the closenessUsers is valid.
   *
   * @param state where is the users to check.
   *
   * @return {@code true} if the closenessUsers is the expected after the task is
   *         created.
   */
  protected boolean validClosenessUsers(final JsonObject state) {

    final var closenessUsers = state.getJsonArray("", new JsonArray());

    if (!"nearby".equals(this.positionOfAnswerer())) {

      return closenessUsers.isEmpty();
    }

    final var appUsers = state.getJsonArray("appUsers", new JsonArray());
    final var appUsersSize = appUsers.size();
    final var closenessUsersSize = closenessUsers.size();
    if (appUsersSize != closenessUsersSize) {
      // Unexpected size
      Logger.warn("Unexpected closeness user size, {} != {}.", appUsersSize, closenessUsersSize);
      return false;
    }

    if ("nearby".equals(this.positionOfAnswerer())) {

      for (var i = 0; i < appUsersSize; i++) {

        final var appUser = appUsers.getString(i);
        final var closenessUser = closenessUsers.getJsonObject(i);
        final var closenessUserId = closenessUser.getString("userId");
        if (!appUser.equals(closenessUserId)) {

          Logger.warn("Unexpected closeness user at {}, {} is not {}.", i, appUser, closenessUser);
          return false;
        }

        final var index = this.indexOfCreatedProfileWithId(appUser);
        final var distance = Math.max(0.0, (this.maxDistance() - this.distanceTo(index)) / this.maxDistance());
        final var closenessValue = closenessUser.getDouble("value");
        if (!this.areSimilar(distance, closenessValue)) {

          Logger.warn("Unexpected closeness user value at {}, {} is not {}.", i, distance, closenessValue);
          return false;
        }

      }

    }

    return true;
  }

  /**
   * Check that the socialClosenessUsers is valid.
   *
   * @param state where is the users to check.
   *
   * @return {@code true} if the socialClosenessUsers is the expected after the
   *         task is created.
   */
  protected boolean validSocialClosenessUsers(final JsonObject state) {

    final var socialClosenessUsers = state.getJsonArray("socialClosenessUsers", new JsonArray());
    final var socialClosenessSize = socialClosenessUsers.size();
    final var appUsers = state.getJsonArray("appUsers", new JsonArray());
    final var appUsersSize = appUsers.size();
    if (appUsersSize != socialClosenessSize) {
      // Unexpected size
      Logger.warn("Unexpected socialCloseness user size, {} != {}.", appUsersSize, socialClosenessSize);
      return false;
    }

    final var type = this.socialCloseness();
    for (var i = 0; i < appUsersSize; i++) {

      final var appUser = appUsers.getString(i);
      final var socialClosenessUser = socialClosenessUsers.getJsonObject(i);
      final var socialClosenessUserId = socialClosenessUser.getString("userId");
      if (!appUser.equals(socialClosenessUserId)) {

        Logger.warn("Unexpected  socialCloseness user at {}, {} is not {}.", i, appUser, socialClosenessUser);
        return false;
      }

      final var socialClosenessValue = socialClosenessUser.getDouble("value");
      Double expectedSocialClosenessValue = null;
      if ("similar".equals(type)) {

        final var index = this.indexOfCreatedProfileWithId(appUser);
        expectedSocialClosenessValue = this.socialClosenessTo(index);

      } else if ("different".equals(type)) {

        final var index = this.indexOfCreatedProfileWithId(appUser);
        expectedSocialClosenessValue = this.socialClosenessTo(index);
        if (expectedSocialClosenessValue != null) {

          expectedSocialClosenessValue = 1.0d - expectedSocialClosenessValue;
        }

      } else {

        expectedSocialClosenessValue = this.indifferentValue();
      }
      if (!this.areSimilar(expectedSocialClosenessValue, socialClosenessValue)) {

        Logger.warn("Unexpected  socialCloseness user value at {}, {} is not {}.", i, expectedSocialClosenessValue,
            socialClosenessValue);
        return false;
      }

    }

    return true;
  }

  /**
   * The value when the dimension is indifferent.
   *
   * @return the expected value when is indifferent.
   */
  protected abstract Double indifferentValue();

  /**
   * Return the believe and values between the requester and the user at the
   * specified position.
   *
   * @param index of the user to calculate the distance.
   *
   * @return the believe and values similarity.
   */
  public Double beliefsAndValuesTo(final int index) {

    if (this.isEmptyProfile(index)) {

      return this.indifferentValue();

    } else {

      return 1.0
          - Math.abs(this.users.get(0).meanings.get(0).level - this.users.get(index).meanings.get(0).level) / 2.0;

    }

  }

  /**
   * Check that the beliefsAndValuesUsers is valid.
   *
   * @param state where is the users to check.
   *
   * @return {@code true} if the beliefsAndValuesUsers is the expected after the
   *         task is created.
   */
  protected boolean validBeliefsAndValuesUsers(final JsonObject state) {

    final var beliefsAndValuesUsers = state.getJsonArray("beliefsAndValuesUsers", new JsonArray());
    final var beliefsAndValuesSize = beliefsAndValuesUsers.size();
    final var appUsers = state.getJsonArray("appUsers", new JsonArray());
    final var appUsersSize = appUsers.size();
    if (appUsersSize != beliefsAndValuesSize) {
      // Unexpected size
      Logger.warn("Unexpected beliefsAndValues user size, {} != {}.", appUsersSize, beliefsAndValuesSize);
      return false;
    }

    final var type = this.beliefsAndValues();
    for (var i = 0; i < appUsersSize; i++) {

      final var appUser = appUsers.getString(i);
      final var beliefsAndValuesUser = beliefsAndValuesUsers.getJsonObject(i);
      final var beliefsAndValuesUserId = beliefsAndValuesUser.getString("userId");
      if (!appUser.equals(beliefsAndValuesUserId)) {

        Logger.warn("Unexpected  beliefsAndValues user at {}, {} is not {}.", i, appUser, beliefsAndValuesUser);
        return false;
      }

      final var beliefsAndValuesValue = beliefsAndValuesUser.getDouble("value");
      Double expectedBeliefsAndValuesValue = null;
      if ("similar".equals(type)) {

        final var index = this.indexOfCreatedProfileWithId(appUser);
        expectedBeliefsAndValuesValue = this.beliefsAndValuesTo(index);

      } else if ("different".equals(type)) {

        final var index = this.indexOfCreatedProfileWithId(appUser);
        expectedBeliefsAndValuesValue = this.beliefsAndValuesTo(index);
        if (expectedBeliefsAndValuesValue != null) {

          expectedBeliefsAndValuesValue = 1.0d - expectedBeliefsAndValuesValue;
        }

      } else {

        expectedBeliefsAndValuesValue = this.indifferentValue();
      }
      if (!this.areSimilar(expectedBeliefsAndValuesValue, beliefsAndValuesValue)) {

        Logger.warn("Unexpected  beliefsAndValues user value at {}, {} is not {}.", i, expectedBeliefsAndValuesValue,
            beliefsAndValuesValue);
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
  public Double domainInterestTo(final int index) {

    if (this.isEmptyProfile(index)) {

      return this.indifferentValue();

    } else {

      return 1.0
          - Math.abs(this.users.get(0).competences.get(0).level - this.users.get(index).competences.get(0).level) / 2.0;

    }

  }

  /**
   * Check that the domainInterestUsers is valid.
   *
   * @param state where is the users to check.
   *
   * @return {@code true} if the domainInterestUsers is the expected after the
   *         task is created.
   */
  protected boolean validDomainInterestUsers(final JsonObject state) {

    final var domainInterestUsers = state.getJsonArray("domainInterestUsers", new JsonArray());
    final var domainInterestSize = domainInterestUsers.size();
    final var appUsers = state.getJsonArray("appUsers", new JsonArray());
    final var appUsersSize = appUsers.size();
    if (appUsersSize != domainInterestSize) {
      // Unexpected size
      Logger.warn("Unexpected domainInterest user size, {} != {}.", appUsersSize, domainInterestSize);
      return false;
    }

    final var type = this.domainInterest();
    for (var i = 0; i < appUsersSize; i++) {

      final var appUser = appUsers.getString(i);
      final var domainInterestUser = domainInterestUsers.getJsonObject(i);
      final var domainInterestUserId = domainInterestUser.getString("userId");
      if (!appUser.equals(domainInterestUserId)) {

        Logger.warn("Unexpected  domainInterest user at {}, {} is not {}.", i, appUser, domainInterestUser);
        return false;
      }

      final var domainInterestValue = domainInterestUser.getDouble("value");
      Double expectedDomainInterestValue = null;
      if ("similar".equals(type)) {

        final var index = this.indexOfCreatedProfileWithId(appUser);
        expectedDomainInterestValue = this.domainInterestTo(index);

      } else if ("different".equals(type)) {

        final var index = this.indexOfCreatedProfileWithId(appUser);
        expectedDomainInterestValue = this.domainInterestTo(index);
        if (expectedDomainInterestValue != null) {

          expectedDomainInterestValue = 1.0d - expectedDomainInterestValue;

        }

      } else {

        expectedDomainInterestValue = this.indifferentValue();
      }
      if (!this.areSimilar(expectedDomainInterestValue, domainInterestValue)) {

        Logger.warn("Unexpected  domainInterest user value at {}, {} is not {}.", i, expectedDomainInterestValue,
            domainInterestValue);
        return false;
      }

    }

    return true;
  }

  /**
   * Check that the matchUsers is valid.
   *
   * @param state where are the match users.
   *
   * @return {@code true} if the matchUsers is the expected after the task is
   *         created.
   */
  protected abstract boolean validMatchUsers(JsonObject state);

  /**
   * Check that the rankedUsers is valid.
   *
   * @param state where is the users to check.
   *
   * @return {@code true} if the rankedUsers is the expected after the task is
   *         created.
   */
  protected boolean validRankedUsers(final JsonObject state) {

    final var matchUsers = state.getJsonArray("matchUsers", new JsonArray());
    final var rankedUsers = state.getJsonArray("rankedUsers", new JsonArray());

    final var max = matchUsers.size();
    final var rankedSize = rankedUsers.size();
    if (max != rankedSize) {
      // Unexpected size
      Logger.warn("Unexpected ranked user size, {} != {}.", max, rankedSize);
      return false;
    }

    for (var i = 0; i < max; i++) {

      final var matchUser = matchUsers.getJsonObject(i);
      final var matchUserId = matchUser.getString("userId");
      final var rankedUserId = rankedUsers.getString(i);
      if (!matchUserId.equals(rankedUserId)) {
        // Unexpected user id
        Logger.warn("Unexpected match user at {} , {} is not equals to {} ", i, matchUserId, rankedUserId);
        return false;
      }
    }

    return true;
  }

  /**
   * Check that the unaskedUsers is valid.
   *
   * @param state where is the users to check.
   *
   * @return {@code true} if the unaskedUsers is the expected after the task is
   *         created.
   */
  protected boolean validUnaskedUsersIds(final JsonObject state) {

    final var rankedUsers = state.getJsonArray("rankedUsers", new JsonArray());
    final var unaskedUsers = state.getJsonArray("unaskedUserIds", new JsonArray());
    final var start = this.maxMessagesOnCreation();
    final var expectedUnaskedSixe = rankedUsers.size() - start;
    final var unaskedSize = unaskedUsers.size();
    if (expectedUnaskedSixe != unaskedSize) {
      // Unexpected size
      Logger.warn("Unexpected unasked users size, {} != {}.", expectedUnaskedSixe, unaskedSize);
      return false;
    }

    for (var i = 0; i < unaskedSize; i++) {

      final var rankedUserId = rankedUsers.getString(start + i);
      final var unaskedUserId = unaskedUsers.getString(i);
      if (!unaskedUserId.equals(rankedUserId)) {
        // Unexpected user id
        Logger.warn("Unexpected unasked user at {} , {} is not equals to {} ", i, rankedUserId, unaskedUserId);
        return false;
      }
    }

    return true;
  }

  /**
   * Return the explanation title for an answer.
   *
   * @return the explanation title.
   */
  protected String explanationTitle() {

    return "Why is this user chosen?";

  }

  /**
   * Return the explanation text for an answer.
   *
   * @return the explanation text.
   */
  protected abstract String explanationText();

  /**
   * Check that receive an explanation when receive the answer.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(8)
  public void shouldReceiveExplanationWithAnswer(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(7, testContext);

    final var user = this.users.get(2);
    final var transaction = new TaskTransaction();
    transaction.actioneerId = user.id;
    transaction.taskId = this.task.id;
    transaction.label = "answerTransaction";
    final var answer = "Response question with ";
    transaction.attributes = new JsonObject().put("answer", answer).put("anonymous", this.anonymous());

    final var transactionIndex = 1;
    final var checkAnswerMessage = this.createMessagePredicate()
        .and(MessagePredicates.labelIs("AnsweredQuestionMessage"))
        .and(MessagePredicates.receiverIs(this.task.requesterId))
        .and(MessagePredicates.attributesSimilarTo(new JsonObject().put("answer", answer).put("taskId", this.task.id)
            .put("question", this.task.goal.name).put("transactionId", String.valueOf(transactionIndex))
            .put("answer", answer).put("userId", transaction.actioneerId).put("anonymous", this.anonymous())));
    final var checkMessages = new ArrayList<Predicate<Message>>();
    checkMessages.add(checkAnswerMessage);

    final var checkExplanationMessage = this.createMessagePredicate().and(MessagePredicates.labelIs("TextualMessage"))
        .and(MessagePredicates.receiverIs(this.task.requesterId)).and(MessagePredicates
            .attributesAre(new JsonObject().put("title", this.explanationTitle()).put("text", this.explanationText())));
    checkMessages.add(checkExplanationMessage);

    final var checkTask = this.createTaskPredicate().and(TaskPredicates.transactionSizeIs(2))
        .and(TaskPredicates.transactionAt(transactionIndex,
            this.createTaskTransactionPredicate().and(TaskTransactionPredicates.similarTo(transaction))
                .and(TaskTransactionPredicates.messagesSizeIs(checkMessages.size()))
                .and(TaskTransactionPredicates.messageAt(0, checkAnswerMessage))
                .and(TaskTransactionPredicates.messageAt(1, checkExplanationMessage))));

    WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction)
        .compose(ignored -> this.waitUntilTask(vertx, testContext, checkTask))
        .compose(ignored -> this.waitUntilCallbacks(vertx, testContext, checkMessages))
        .onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

  }

  /**
   * Validate that to user-value list are equals.
   *
   * @param source array to check.
   * @param target array to check.
   *
   * @return {@code true} if the array are equals.
   */
  protected boolean validateThatUserValueListAreEquals(final JsonArray source, final JsonArray target) {

    final var sourceSize = source.size();
    final var targetSize = target.size();
    if (sourceSize != targetSize) {
      // Unexpected size
      Logger.warn("Unexpected user-value array sizes, {} != {}", sourceSize, targetSize);
      return false;
    }

    for (var i = 0; i < sourceSize; i++) {

      final var sourceObject = source.getJsonObject(i);
      final var targetObject = target.getJsonObject(i);
      final var sourceUserId = sourceObject.getString("userId");
      final var targetUserId = targetObject.getString("userId");
      if (sourceUserId != targetUserId && (sourceUserId == null || !sourceUserId.equals(targetUserId))) {

        Logger.warn("Unexpected user id at {}, {} != {}", i, sourceUserId, targetUserId);
        return false;

      }

      final var sourceValue = sourceObject.getDouble("value");
      final var targetValue = targetObject.getDouble("value");
      if (!this.areSimilar(sourceValue, targetValue)) {

        Logger.warn("Unexpected user id at {}, {} != {}", i, sourceValue, targetValue);
        return false;

      }
    }

    return true;

  }

  /**
   * Return the value associated to a user-value array for a specific user.
   *
   * @param source array to get the value.
   * @param userId identifier of the user to get the value.
   *
   * @return the value of the user or {@code null} if not found it.
   */
  protected Double getUserValueIn(final JsonArray source, final String userId) {

    final var sourceSize = source.size();
    for (var i = 0; i < sourceSize; i++) {

      final var sourceObject = source.getJsonObject(i);
      final var sourceUserId = sourceObject.getString("userId");
      if (sourceUserId == userId || sourceUserId != null && sourceUserId.equals(userId)) {

        return sourceObject.getDouble("value");

      }
    }

    return null;

  }

}
