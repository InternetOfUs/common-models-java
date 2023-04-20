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

import static org.junit.jupiter.api.Assertions.fail;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.function.Supplier;
import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.incentive_server.TaskTransactionStatusBody;
import eu.internetofus.common.components.incentive_server.TaskTypeStatusBody;
import eu.internetofus.common.components.incentive_server.WeNetIncentiveServerSimulator;
import eu.internetofus.common.components.interaction_protocol_engine.Interaction;
import eu.internetofus.common.components.interaction_protocol_engine.InteractionsPage;
import eu.internetofus.common.components.interaction_protocol_engine.State;
import eu.internetofus.common.components.interaction_protocol_engine.WeNetInteractionProtocolEngine;
import eu.internetofus.common.components.models.CommunityProfile;
import eu.internetofus.common.components.models.HumanDescription;
import eu.internetofus.common.components.models.Message;
import eu.internetofus.common.components.models.Task;
import eu.internetofus.common.components.models.TaskTransaction;
import eu.internetofus.common.components.models.TaskType;
import eu.internetofus.common.components.models.WeNetUserProfile;
import eu.internetofus.common.components.service.App;
import eu.internetofus.common.components.service.WeNetServiceSimulator;
import eu.internetofus.common.components.social_context_builder.UserMessage;
import eu.internetofus.common.components.social_context_builder.WeNetSocialContextBuilderSimulator;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import eu.internetofus.common.model.Model;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.junit5.VertxTestContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.tinylog.Logger;

/**
 * Interaction test case over a protocol. ATTENTION: This test is sequential and
 * maintains the state between methods. In other words, you must to run the
 * entire test methods on the specified order to work.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public abstract class AbstractProtocolITC {

  /**
   * The value of the last successful test.
   */
  public int lastSuccessfulTest;

  /**
   * The users that has been created.
   */
  protected List<WeNetUserProfile> users;

  /**
   * The application that will involved on the test.
   */
  protected App app;

  /**
   * The community that will involved on the test.
   */
  protected CommunityProfile community;

  /**
   * The task type that will involved on the test.
   */
  protected TaskType taskType;

  /**
   * The task that will involved on the test.
   */
  protected Task task;

  /**
   * Assert that the last successful test was the specified step.
   *
   * @param step        to be successful.
   * @param testContext context to do the test.
   */
  protected void assertLastSuccessfulTestWas(final int step, final VertxTestContext testContext) {

    if (step > this.lastSuccessfulTest) {

      final var msg = "Previous test not succeeded";
      testContext.failNow(msg);
      fail(msg);
    }

  }

  /**
   * Assert that the current test is completed with a successful.
   *
   * @param testContext context to do the test.
   */
  protected void assertSuccessfulCompleted(final VertxTestContext testContext) {

    this.lastSuccessfulTest++;
    testContext.completeNow();

  }

  /**
   * Initialize the variables to use on the tests.
   */
  @Test
  @Order(0)
  public void initializeTests() {

    this.lastSuccessfulTest = 0;
    this.users = new ArrayList<>();
    this.app = null;
    this.community = null;
    this.taskType = null;
    this.task = null;

  }

  /**
   * Return the number of users that you want to create.
   *
   * @return the number of users to create.
   */
  protected abstract int numberOfUsersToCreate();

  /**
   * Create the user at the specified position.
   *
   * @param index       of the profile to create.
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   *
   * @return the future created profile.
   */
  protected Future<WeNetUserProfile> createProfileFor(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    return StoreServices.storeProfileExample(index, vertx, testContext);
  }

  /**
   * Create the users that will be used on the tests.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(1)
  public void shouldCreateUsers(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(0, testContext);

    @SuppressWarnings("rawtypes")
    final List<Future> futures = new ArrayList<>();
    final var max = this.numberOfUsersToCreate();
    for (var i = 0; i < max; i++) {

      this.users.add(new WeNetUserProfile());
      final var index = i;
      futures.add(this.createProfileFor(index, vertx, testContext).map(createdUser -> {

        this.users.set(index, createdUser);
        return null;
      }));

    }

    CompositeFuture.all(futures).onFailure(error -> testContext.failNow(error))
        .onSuccess(any -> this.assertSuccessfulCompleted(testContext));

  }

  /**
   * Create the task to be stored and used on tests.
   *
   * @return the application to store and use on the tests.
   */
  protected App createApp() {

    return new App();
  }

  /**
   * Create the app that will be used on the tests.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(2)
  public void shouldCreateApp(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(1, testContext);

    WeNetServiceSimulator.createProxy(vertx).createApp(this.createApp()).compose(addedApp -> {

      this.app = addedApp;
      final var appUsers = new JsonArray();
      for (final WeNetUserProfile profile : this.users) {

        appUsers.add(profile.id);
      }
      return WeNetServiceSimulator.createProxy(vertx).addUsers(addedApp.appId, appUsers);

    }).onComplete(testContext.succeeding(added -> this.assertSuccessfulCompleted(testContext)));

  }

  /**
   * Create the community to use on the tests.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   *
   * @see App#getOrCreateDefaultCommunityFor(String, Vertx)
   */
  @Test
  @Order(3)
  public void shouldCreateCommunity(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(2, testContext);

    testContext.assertComplete(App.getOrCreateDefaultCommunityFor(this.app.appId, vertx)).onSuccess(added -> {

      this.community = added;
      this.assertSuccessfulCompleted(testContext);
    });

  }

  /**
   * Create the task type of the protocol to test.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   *
   * @return the future with the task type to use.
   */
  protected abstract Future<TaskType> createTaskTypeForProtocol(final Vertx vertx, final VertxTestContext testContext);

  /**
   * Load a task type from a JSON file.
   *
   * @param resourcePath the path to the resource with the file with the task
   *                     type.
   * @param vertx        event bus to use.
   * @param testContext  context to do the test.
   *
   * @return the future with the loaded task.
   */
  protected Future<TaskType> loadTaskTypeForProtocol(final String resourcePath, final Vertx vertx,
      final VertxTestContext testContext) {

    return vertx.executeBlocking(promise -> {

      try {

        final var input = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
        final var content = new BufferedReader(new InputStreamReader(input)).lines().collect(Collectors.joining("\n"));
        final var taskType = Model.fromString(content, TaskType.class);
        promise.complete(taskType);

      } catch (final Throwable cause) {

        testContext.failNow(cause);
      }

    });

  }

  /**
   * Create a new instance of the task type to test.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(4)
  public void shouldCreateTaskType(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(3, testContext);
    var future = this.createTaskTypeForProtocol(vertx, testContext);
    future = future.compose(foundTaskType -> WeNetTaskManager.createProxy(vertx).createTaskType(foundTaskType));
    future.onComplete(testContext.succeeding(createdTaskType -> {
      this.taskType = createdTaskType;
      this.assertSuccessfulCompleted(testContext);
    }));

  }

  /**
   * Create the task to be stored and used on tests.
   *
   * @return the task to store and use on the tests.
   */
  protected Task createTaskForProtocol() {

    final var taskForProtocol = new Task();
    taskForProtocol.appId = this.app.appId;
    taskForProtocol.communityId = this.community.id;
    taskForProtocol.taskTypeId = this.taskType.id;
    taskForProtocol.goal = new HumanDescription();
    taskForProtocol.goal.name = "Task to test";
    taskForProtocol.requesterId = this.users.get(0).id;
    return taskForProtocol;
  }

  /**
   * The process that is waiting until the
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   * @param supplier    to obtain the model.
   * @param check       the function to check if the obtained model is what is
   *                    expected.
   *
   * @param <T>         type of the waiting data.
   *
   * @return the future model that satisfy the check.
   */
  protected <T> Future<T> waitUntil(final Vertx vertx, final VertxTestContext testContext,
      final Supplier<Future<T>> supplier, final Predicate<T> check) {

    final Promise<T> promise = Promise.promise();
    final var address = UUID.randomUUID().toString();
    final var consumer = vertx.eventBus().localConsumer(address);
    final var options = new DeliveryOptions();
    options.setLocalOnly(true);
    consumer.handler(message -> {

      final var step = ((Number) message.body()).intValue();
      Logger.trace("Start a wait until step {}", step);
      if (testContext.completed()) {

        consumer.unregister();
        promise.fail("Test finished");

      } else {

        supplier.get().onComplete(result -> {

          if (result.failed()) {

            consumer.unregister();
            final var error = result.cause();
            promise.fail(error);
            Logger.trace(error, "Wait until step {} fails because can get model", step);

          } else {

            final var target = result.result();
            vertx.executeBlocking(block -> {

              try {

                if (check.test(target)) {

                  consumer.unregister();
                  promise.complete(target);

                } else {

                  Thread.sleep(1500);
                  vertx.eventBus().send(address, step + 1, options);
                }

              } catch (final Throwable t) {

                consumer.unregister();
                promise.fail(t);
                Logger.trace(t, "Wait until step {} fails because test throws exception", step);
              }

              block.complete();

            }).onComplete(any -> Logger.trace("Finished a wait until step {}", step));

          }

        });
      }

    });
    vertx.eventBus().send(address, 0, options);
    return promise.future();

  }

  /**
   * Wait until the task satisfy the predicate.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   * @param checkTask   the function that has to be true to the task is on the
   *                    state that is waiting.
   *
   * @return the task that satisfy the predicate.
   */
  protected Future<Task> waitUntilTask(final Vertx vertx, final VertxTestContext testContext,
      final Predicate<Task> checkTask) {

    return this.waitUntil(vertx, testContext,
        () -> WeNetTaskManager.createProxy(vertx).retrieveTask(this.task.id).map(target -> {
          this.task = target;
          return this.task;
        }), checkTask);

  }

  /**
   * Wait until has received the specified callbacks.
   *
   * @param vertx         event bus to use.
   * @param testContext   context to do the test.
   * @param checkMessages the functions that has all to be {@code true} to stop
   *                      waiting.
   *
   * @return the messages that satisfy the predicates.
   */
  protected Future<List<Message>> waitUntilCallbacks(@NotNull final Vertx vertx,
      @NotNull final VertxTestContext testContext, @NotNull final List<Predicate<Message>> checkMessages) {

    return this.waitUntil(vertx, testContext, () -> Model
        .fromFutureJsonArray(WeNetServiceSimulator.createProxy(vertx).retrieveCallbacks(this.app.appId), Message.class),
        callbacks -> {

          final List<Predicate<Message>> copy = new ArrayList<>(checkMessages);
          final var callbacksIter = callbacks.iterator();
          while (callbacksIter.hasNext()) {

            final var msg = callbacksIter.next();
            if (msg != null) {

              final var iter = copy.iterator();
              while (iter.hasNext()) {
                final var checkMessage = iter.next();
                if (checkMessage.test(msg)) {

                  iter.remove();
                  break;
                }
              }
            }
            callbacksIter.remove();
          }

          return copy.isEmpty();

        }).compose(callbacks -> WeNetServiceSimulator.createProxy(vertx).deleteCallbacks(this.app.appId)
            .map(empty -> callbacks));

  }

  /**
   * Check that a task is created.
   *
   *
   * @param source      task to create.
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   * @param checkTask   this predicate is true when the created task has the
   *                    expected values.
   *
   * @return the future created task.
   *
   * @see #waitUntilTask(Vertx, VertxTestContext, Predicate)
   */
  protected Future<Task> waitUntilTaskCreated(@NotNull final Task source, @NotNull final Vertx vertx,
      @NotNull final VertxTestContext testContext, @NotNull final Predicate<Task> checkTask) {

    return WeNetTaskManager.createProxy(vertx).createTask(source).compose(target -> {
      this.task = target;
      return this.waitUntilTask(vertx, testContext, checkTask);
    });

  }

  /**
   * Check that is send the specified task transaction status.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   * @param checkStatus this predicate is true when the task transaction status
   *                    has the expected values.
   *
   * @return the future notified task transaction.
   *
   * @see #waitUntilTask(Vertx, VertxTestContext, Predicate)
   */
  protected Future<List<TaskTransactionStatusBody>> waitUntilIncentiveServerHasTaskTransactionStatus(
      @NotNull final Vertx vertx, @NotNull final VertxTestContext testContext,
      @NotNull final List<Predicate<TaskTransactionStatusBody>> checkStatus) {

    return this.waitUntil(vertx, testContext,
        () -> WeNetIncentiveServerSimulator.createProxy(vertx).getTaskTransactionStatus(), status -> {

          final List<Predicate<TaskTransactionStatusBody>> copy = new ArrayList<>(checkStatus);
          final var statusIter = status.iterator();
          while (statusIter.hasNext()) {

            var state = statusIter.next();
            final var iter = copy.iterator();
            while (iter.hasNext()) {

              final var check = iter.next();
              if (check.test(state)) {

                iter.remove();
                state = null;
                break;
              }
            }

            if (state != null) {

              statusIter.remove();
            }

          }

          return copy.isEmpty();

        }).compose(status -> WeNetIncentiveServerSimulator.createProxy(vertx).deleteTaskTransactionStatus()
            .map(ignored -> status));

  }

  /**
   * Check that is send the specified task type status.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   * @param checkStatus this predicate is true when the task type status has the
   *                    expected values.
   *
   * @return the future notified task type.
   *
   * @see #waitUntil(Vertx, VertxTestContext, Supplier, Predicate)
   */
  protected Future<List<TaskTypeStatusBody>> waitUntilIncentiveServerHasTaskTypeStatus(@NotNull final Vertx vertx,
      @NotNull final VertxTestContext testContext, @NotNull final List<Predicate<TaskTypeStatusBody>> checkStatus) {

    return this.waitUntil(vertx, testContext,
        () -> WeNetIncentiveServerSimulator.createProxy(vertx).getTaskTypeStatus(), status -> {

          final List<Predicate<TaskTypeStatusBody>> copy = new ArrayList<>(checkStatus);
          final var statusIter = status.iterator();
          while (statusIter.hasNext()) {

            var state = statusIter.next();
            final var iter = copy.iterator();
            while (iter.hasNext()) {

              final var check = iter.next();
              if (check.test(state)) {

                iter.remove();
                state = null;
                break;
              }
            }

            if (state != null) {

              statusIter.remove();
            }

          }

          return copy.isEmpty();
        }).compose(
            status -> WeNetIncentiveServerSimulator.createProxy(vertx).deleteTaskTypeStatus().map(ignored -> status));

  }

  /**
   * Create the predicate over a task for the current state of the test.
   *
   * @return the predicate for the task.
   *
   * @see #app
   * @see #task
   * @see #community
   */
  protected Predicate<Task> createTaskPredicate() {

    return task -> {

      if (this.app != null && !this.app.appId.equals(task.appId)) {

        Logger.trace("Unexpected APP for task");
        return false;
      }
      if (this.task != null && !this.task.id.equals(task.id)) {

        Logger.trace("Unexpected task");
        return false;
      }
      if (this.community != null && !this.community.id.equals(task.communityId)) {

        Logger.trace("Unexpected community for task");
        return false;
      }

      return true;
    };

  }

  /**
   * Create the predicate over a message for the current state of the test.
   *
   * @return the predicate for the message.
   *
   * @see #app
   */
  protected Predicate<Message> createMessagePredicate() {

    return message -> {

      if (this.app != null && !this.app.appId.equals(message.appId)) {

        Logger.trace("Unexpected APP for message");
        return false;
      }

      return true;
    };

  }

  /**
   * Create the task transaction predicate over the current satte of the test.
   *
   * @return the predicate for the transaction.
   *
   * @see #task
   */
  protected Predicate<TaskTransaction> createTaskTransactionPredicate() {

    return transaction -> {

      if (this.task != null && !this.task.id.equals(transaction.taskId)) {

        Logger.trace("Unexpected task for transaction");
        return false;
      }

      return true;
    };

  }

  /**
   * Create the task transaction status predicate over the current state of the
   * test.
   *
   * @return the predicate for the task transaction status.
   *
   * @see #app
   * @see #task
   * @see #community
   */
  protected Predicate<TaskTransactionStatusBody> createIncentiveServerTaskTransactionStatusPredicate() {

    return state -> {

      if (this.app != null && !this.app.appId.equals(state.app_id)) {

        Logger.trace("Unexpected APP for incentive server transaction");
        return false;
      }
      if (this.community != null && !this.community.id.equals(state.community_id)) {

        Logger.trace("Unexpected community for incentive server transaction");
        return false;
      }
      if (this.taskType != null && !this.taskType.id.equals(state.taskTypeId)) {

        Logger.trace("Unexpected task type for incentive server transaction");
        return false;
      }
      if (this.task != null && !this.task.id.equals(state.taskId)) {

        Logger.trace("Unexpected task for incentive server transaction");
        return false;
      }

      return true;
    };

  }

  /**
   * Create the task type status predicate over the current state of the test.
   *
   * @return the predicate for the task type status.
   *
   * @see #app
   * @see #task
   * @see #community
   */
  protected Predicate<TaskTypeStatusBody> createIncentiveServerTaskTypeStatusPredicate() {

    return state -> {

      if (this.app != null && !this.app.appId.equals(state.app_id)) {

        return false;
      }
      if (this.community != null && !this.community.id.equals(state.community_id)) {

        return false;
      }
      if (this.taskType != null && !this.taskType.id.equals(state.taskTypeId)) {

        return false;
      }
      if (this.task != null && !this.task.id.equals(state.taskId)) {

        return false;
      }

      return true;
    };

  }

  /**
   * Create the predicate over an user state with the data from the test.
   *
   * @param userId identifier of the user.
   *
   * @return the predicate for the user state.
   *
   * @see #app
   * @see #task
   * @see #community
   */
  protected Predicate<State> createUserStatePredicate(@NotNull final String userId) {

    return state -> {

      if (!userId.equals(state.userId)) {

        return false;
      }
      if (state.taskId != null) {

        return false;
      }
      if (state.communityId != null) {

        return false;
      }

      return true;
    };

  }

  /**
   * Create the predicate over a community user state with the data from the test.
   *
   * @param userId identifier of the user.
   *
   * @return the predicate for the community user state.
   *
   * @see #app
   * @see #task
   * @see #community
   */
  protected Predicate<State> createCommunityUserStatePredicate(@NotNull final String userId) {

    return state -> {

      if (!userId.equals(state.userId)) {

        return false;
      }
      if (state.taskId != null) {

        return false;
      }
      if (this.community != null && !this.community.id.equals(state.communityId)) {

        return false;
      }

      return true;
    };

  }

  /**
   * Create the predicate over a task user state with the data from the test.
   *
   * @param userId identifier of the user.
   *
   * @return the predicate for the task user state.
   *
   * @see #app
   * @see #task
   * @see #community
   */
  protected Predicate<State> createTaskUserStatePredicate(@NotNull final String userId) {

    return state -> {

      if (!userId.equals(state.userId)) {

        return false;
      }
      if (this.task != null && !this.task.id.equals(state.taskId)) {

        return false;
      }
      if (state.communityId != null) {

        return false;
      }

      return true;
    };

  }

  /**
   * Check that is send the specified task status.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   * @param userId      identifier of the user.
   * @param checkState  this predicate is true when the community user state has
   *                    the expected values.
   *
   * @return the future community user state.
   *
   * @see #waitUntilTask(Vertx, VertxTestContext, Predicate)
   */
  protected Future<State> waitUntilCommunityUserState(@NotNull final Vertx vertx,
      @NotNull final VertxTestContext testContext, @NotNull final String userId,
      @NotNull final Predicate<State> checkState) {

    return this.waitUntil(vertx, testContext,
        () -> WeNetInteractionProtocolEngine.createProxy(vertx).retrieveCommunityUserState(this.community.id, userId),
        checkState);

  }

  /**
   * Check that is send the specified social notifications.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   * @param checkStatus this predicate is true when the social notification has
   *                    the expected values.
   *
   * @return the future notified social notification.
   *
   * @see #waitUntil(Vertx, VertxTestContext, Supplier, Predicate)
   */
  protected Future<List<UserMessage>> waitUntilSocialNotification(@NotNull final Vertx vertx,
      @NotNull final VertxTestContext testContext, @NotNull final List<Predicate<UserMessage>> checkStatus) {

    return this.waitUntil(vertx, testContext,
        () -> WeNetSocialContextBuilderSimulator.createProxy(vertx).getSocialNotificationInteraction(), status -> {

          final List<Predicate<UserMessage>> copy = new ArrayList<>(checkStatus);
          final var statusIter = status.iterator();
          while (statusIter.hasNext()) {

            var state = statusIter.next();
            final var iter = copy.iterator();
            while (iter.hasNext()) {

              final var check = iter.next();
              if (check.test(state)) {

                iter.remove();
                state = null;
                break;
              }
            }

            if (state != null) {

              statusIter.remove();
            }

          }

          return copy.isEmpty();
        }).compose(status -> WeNetSocialContextBuilderSimulator.createProxy(vertx).deleteSocialNotificationInteraction()
            .map(ignored -> status));

  }

  /**
   * Create the predicate over a user message with the data from the test.
   *
   * @return the predicate for the social task user state.
   *
   * @see #app
   * @see #task
   * @see #community
   */
  protected Predicate<UserMessage> createSocialNotificationPredicate() {

    return msg -> {

      if (this.task != null && !this.task.id.equals(msg.taskId)) {

        return false;
      }

      return true;
    };

  }

  /**
   * Get the user associated by an identifier.
   *
   * @param userId identifier of the user to return.
   *
   * @return the user associated to the identifier of {@code null} if it is not
   *         defined.
   */
  protected WeNetUserProfile getUserById(final String userId) {

    for (final var user : this.users) {

      if (user.id.equals(userId)) {

        return user;

      }
    }

    return null;
  }

  /**
   * Create the predicate over a user interaction.
   *
   * @return the predicate for the interaction.
   *
   * @see #app
   * @see #task
   * @see #taskType
   * @see #community
   */
  protected Predicate<Interaction> createInteractionPredicate() {

    return interaction -> {

      if (this.app != null && !this.app.appId.equals(interaction.appId)) {

        return false;
      }
      if (this.community != null && !this.community.id.equals(interaction.communityId)) {

        return false;
      }
      if (this.taskType != null && !this.taskType.id.equals(interaction.taskTypeId)) {

        return false;
      }
      if (this.task != null && !this.task.id.equals(interaction.taskId)) {

        return false;
      }

      return true;
    };

  }

  /**
   * Wait until has received the specified interactions.
   *
   * @param vertx             event bus to use.
   * @param testContext       context to do the test.
   * @param checkInteractions the functions that has all to be {@code true} to
   *                          stop waiting.
   *
   * @return the interactions that satisfy the predicates.
   */
  protected Future<InteractionsPage> waitUntilInteractions(@NotNull final Vertx vertx,
      @NotNull final VertxTestContext testContext, @NotNull final List<Predicate<Interaction>> checkInteractions) {

    return this.waitUntil(vertx, testContext,
        () -> WeNetInteractionProtocolEngine.createProxy(vertx).getInteractionsPage(this.app.appId, this.community.id,
            this.taskType.id, this.task.id, null, null, null, null, null, null, null, null, null, null,
            "-messageTs,-transactionTs", 0, 100),
        page -> {

          if (page.interactions == null || page.interactions.isEmpty()) {

            return false;

          } else {

            final List<Predicate<Interaction>> copy = new ArrayList<>(checkInteractions);
            final var interactionsIter = page.interactions.iterator();
            while (interactionsIter.hasNext()) {

              final var interaction = interactionsIter.next();
              if (interaction != null) {

                final var iter = copy.iterator();
                while (iter.hasNext()) {
                  final var checkInteraction = iter.next();
                  if (checkInteraction.test(interaction)) {

                    iter.remove();
                    break;
                  }
                }
              }
              interactionsIter.remove();
            }

            return copy.isEmpty();
          }

        })
        .compose(page -> WeNetInteractionProtocolEngine.createProxy(vertx)
            .deleteInteractions(this.app.appId, this.community.id, this.taskType.id, this.task.id, null, null, null,
                null, null, null, null, null, null, null)
            .map(empty -> page));

  }

  /**
   * Wait until has task user state satisfy the predicate.
   *
   * @param userId      the user to check the state.
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   * @param checkState  the functions that check if the state is the expected one.
   *
   * @return the interactions that satisfy the predicates.
   */
  protected Future<State> waitUntilUserTaskState(@NotNull final String userId, @NotNull final Vertx vertx,
      @NotNull final VertxTestContext testContext, @NotNull final Predicate<State> checkState) {

    return this.waitUntil(vertx, testContext,
        () -> WeNetInteractionProtocolEngine.createProxy(vertx).retrieveTaskUserState(this.task.id, userId),
        checkState::test);

  }

  /**
   * Return the index of the profile associated to the specified identifier.
   *
   * @param id identifier of the profile that is looking for.
   *
   * @return the index where the profile is defined.
   *
   * @throws AssertionError if no created profile has the specified identifier.
   */
  protected int indexOfCreatedProfileWithId(final String id) {

    for (var i = 0; i < this.users.size(); i++) {

      final var user = this.users.get(i);
      if (user.id.equals(id)) {

        return i;
      }
    }

    fail("Any created profile matches the identifier " + id);
    return -1;
  }

}
