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
import eu.internetofus.common.components.personal_context_builder.UserDistance;
import eu.internetofus.common.components.personal_context_builder.UserLocation;
import eu.internetofus.common.components.personal_context_builder.WeNetPersonalContextBuilderSimulator;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.components.service.MessagePredicates;
import eu.internetofus.common.components.task_manager.TaskPredicates;
import eu.internetofus.common.components.task_manager.TaskTransactionPredicates;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import eu.internetofus.common.model.TimeManager;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import java.time.Duration;
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
   * The step distance between users.
   */
  public static final double STEP_DISTANCE = 0.0007d;

  /**
   * The minimum distance when compare double values.
   */
  public static final double MIN_SIM_DISTANCE = 0.000000000001;

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
    taskToCreate.attributes = new JsonObject().put("domain", this.domain()).put("anonymous", this.anonymous())
        .put("domainInterest", this.domainInterest()).put("beliefsAndValues", this.beliefsAndValues())
        .put("socialCloseness", this.socialCloseness()).put("positionOfAnswerer", this.positionOfAnswerer())
        .put("maxUsers", this.maxUsers()).put("maxAnswers", this.maxAnswers())
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
   * Return if the question is anonymous of the task.
   *
   * @return {@code false} in any case.
   */
  protected boolean anonymous() {

    return false;

  }

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
   * @return {@code 5} in any case.
   */
  public int maxUsers() {

    return 5;
  }

  /**
   * Return the number of answers that it is expecting.
   *
   * @return {@code 7} in any case.
   */
  public int maxAnswers() {

    return 7;
  }

  /**
   * Return the expiration date.
   *
   * @return to expiration date one day after now.
   */
  public long expirationDate() {

    return TimeManager.now() + Duration.ofDays(1).toSeconds();
  }

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

    if (index < this.users.size() - 1) {

      return Math.max(0d, 0.6d - index * 0.01d);

    } else {

      return null;
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
    for (var i = 1; i < this.users.size() - 1; i++) {

      final var weight = this.socialClosenessTo(i);
      if (weight != null) {

        final var relationship = new SocialNetworkRelationship();
        relationship.appId = this.app.appId;
        relationship.sourceId = this.users.get(0).id;
        relationship.targetId = this.users.get(i).id;
        relationship.weight = weight;
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
   * @return the normalized distance to the specified user of the specified
   *         position.
   */
  public double distanceTo(final int index) {

    final var max = this.maxDistance();
    if (max == 0) {

      return 0d;

    } else {

      final var distance = UserDistance.calculateDistance(0d, 0d, 0d, STEP_DISTANCE * index);
      return Math.max(0.0d, (max - distance) / max);
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
  public void shouldFillLocation(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(5, testContext);

    @SuppressWarnings("rawtypes")
    final List<Future> added = new ArrayList<>();
    for (var i = 0; i < this.users.size(); i++) {

      final var location = new UserLocation();
      location.userId = this.users.get(i).id;
      location.latitude = 0.0;
      location.longitude = i * STEP_DISTANCE;
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
              .put("userId", source.requesterId).put("domain", this.domain()).put("anonymous", this.anonymous())
              .put("positionOfAnswerer", this.positionOfAnswerer())))
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
    if (!state.containsKey("physicalClosenessUsers") && this.isNearbySelected()) {

      Logger.warn("Not physicalClosenessUsers is not defined on the state.");
      return false;
    }
    if (!state.containsKey("socialClosenessUsers") && !"indifferent".equals(this.socialCloseness())) {

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

    return this.validAppUsers(state) && this.validPhysicalClosenessUsers(state) && this.validSocialClosenessUsers(state)
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

      return Math.abs(source - target) < MIN_SIM_DISTANCE;
    }

  }

  /**
   * Check that the physicalClosenessUsers is valid.
   *
   * @param state where is the users to check.
   *
   * @return {@code true} if the physicalClosenessUsers is the expected after the
   *         task is created.
   */
  protected boolean validPhysicalClosenessUsers(final JsonObject state) {

    final var physicalClosenessUsers = state.getJsonArray("physicalClosenessUsers", new JsonArray());

    final var isNearby = "nearby".equals(this.positionOfAnswerer());
    if (!isNearby && physicalClosenessUsers.isEmpty()) {

      return true;
    }

    final var appUsers = state.getJsonArray("appUsers", new JsonArray());
    final var appUsersSize = appUsers.size();
    final var physicalClosenessUsersSize = physicalClosenessUsers.size();
    if (appUsersSize != physicalClosenessUsersSize) {
      // Unexpected size
      Logger.warn("Unexpected physical closeness user size, {} != {}.", appUsersSize, physicalClosenessUsersSize);
      return false;
    }

    for (var i = 0; i < appUsersSize; i++) {

      final var appUser = appUsers.getString(i);
      final var physicalClosenessUser = physicalClosenessUsers.getJsonObject(i);
      final var physicalClosenessUserId = physicalClosenessUser.getString("userId");
      if (!appUser.equals(physicalClosenessUserId)) {

        Logger.warn("Unexpected physical closeness user at {}, {} is not {}.", i, appUser, physicalClosenessUserId);
        return false;
      }

      final var index = this.indexOfCreatedProfileWithId(appUser);
      Double distance = null;
      if (isNearby) {

        distance = this.distanceTo(index);
      }
      final var physicalClosenessValue = physicalClosenessUser.getDouble("value");
      if (!this.areSimilar(distance, physicalClosenessValue)) {

        Logger.warn("Unexpected physical closeness user value at {}, {} is not {}.", i, distance,
            physicalClosenessValue);
        return false;
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

        Logger.warn("Unexpected  socialCloseness user at {}, {} is not {}.", i, appUser, socialClosenessUserId);
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

        expectedSocialClosenessValue = null;
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
   * Return the believe and values between the requester and the user at the
   * specified position.
   *
   * @param index of the user to calculate the distance.
   *
   * @return the believe and values similarity.
   */
  public Double beliefsAndValuesTo(final int index) {

    if (this.isEmptyProfile(index)) {

      return null;

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

        Logger.warn("Unexpected  beliefsAndValues user at {}, {} is not {}.", i, appUser, beliefsAndValuesUserId);
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

        expectedBeliefsAndValuesValue = null;
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

      return null;

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

        Logger.warn("Unexpected  domainInterest user at {}, {} is not {}.", i, appUser, domainInterestUserId);
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

        expectedDomainInterestValue = null;
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
   * @param index of the user that want to explanation title.
   *
   * @return the explanation title.
   */
  protected String explanationTitleFor(final int index) {

    return "Why is this user chosen?";

  }

  /**
   * Return the explanation text for an answer.
   *
   * @param index of the user that want to explanation text.
   *
   * @return the explanation text.
   */
  protected abstract String explanationTextFor(int index);

  /**
   * The index of the user that has to answer the question.
   *
   * @return {@code 2} in any case.
   */
  protected int answeredIndex() {

    return 2;

  }

  /**
   * Return if the answer has to publish.
   *
   * @return {@code true} in any case.
   */
  protected boolean publish() {

    return true;

  }

  /**
   * Return if the question has to publish anonymously.
   *
   * @return {@code true} if the publish has to be anonymously.
   *
   * @see #anonymous()
   */
  protected boolean publishAnonymously() {

    return this.anonymous();

  }

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

    final var answerIndex = this.answeredIndex();
    final var user = this.users.get(2);
    final var transaction = new TaskTransaction();
    transaction.actioneerId = user.id;
    transaction.taskId = this.task.id;
    transaction.label = "answerTransaction";
    final var answer = "Response question with ";
    transaction.attributes = new JsonObject().put("answer", answer).put("anonymous", this.anonymous())
        .put("publish", this.publish()).put("publishAnonymously", this.publishAnonymously());

    final var transactionIndex = 1;
    final var checkAnswerMessage = this.createMessagePredicate()
        .and(MessagePredicates.labelIs("AnsweredQuestionMessage"))
        .and(MessagePredicates.receiverIs(this.task.requesterId))
        .and(MessagePredicates.attributesSimilarTo(new JsonObject().put("taskId", this.task.id)
            .put("question", this.task.goal.name).put("transactionId", String.valueOf(transactionIndex))
            .put("answer", answer).put("userId", transaction.actioneerId).put("anonymous", this.anonymous())));
    final var checkMessages = new ArrayList<Predicate<Message>>();
    checkMessages.add(checkAnswerMessage);

    final var checkExplanationMessage = this.createMessagePredicate().and(MessagePredicates.labelIs("TextualMessage"))
        .and(MessagePredicates.receiverIs(this.task.requesterId)).and(MessagePredicates.attributesAre(new JsonObject()
            .put("title", this.explanationTitleFor(answerIndex)).put("text", this.explanationTextFor(answerIndex))));
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

        Logger.warn("Unexpected user value at {}, {} != {}", i, sourceValue, targetValue);
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
