/*
 * -----------------------------------------------------------------------------
 *
 * Copyright (c) 2019 - 2022 UDT-IA, IIIA-CSIC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * -----------------------------------------------------------------------------
 */
package eu.internetofus.common.components;

import static eu.internetofus.common.components.profile_manager.WeNetProfileManagers.createUsers;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.profile_manager.CommunityProfile;
import eu.internetofus.common.components.profile_manager.WeNetUserProfile;
import eu.internetofus.common.components.service.App;
import eu.internetofus.common.components.service.Message;
import eu.internetofus.common.components.service.WeNetServiceSimulator;
import eu.internetofus.common.components.task_manager.Task;
import eu.internetofus.common.components.task_manager.TaskType;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import eu.internetofus.common.components.task_manager.WeNetTaskManagers;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

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
   * Number maximum of users to use on the test.
   */
  public static final int MAX_USERS = 6;

  /**
   * The value of the last successfuk test.
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

    testContext.verify(
        () -> assertThat(this.lastSuccessfulTest).withFailMessage("Previous test not succeeded").isEqualTo(step));

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
    this.users = null;
    this.app = null;
    this.community = null;
    this.taskType = null;
    this.task = null;

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
    StoreServices.storeProfileExample(1, vertx, testContext).onSuccess(me -> {

      testContext.assertComplete(createUsers(MAX_USERS, vertx, testContext)).onSuccess(createdUsers -> {
        this.users = createdUsers;
        this.users.add(0, me);
        this.assertSuccessfulCompleted(testContext);

      });

    });
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

    testContext
        .assertComplete(WeNetServiceSimulator.createProxy(vertx).createApp(this.createApp()).compose(addedApp -> {
          this.app = addedApp;
          final var appUsers = new JsonArray();
          for (final WeNetUserProfile profile : this.users) {

            appUsers.add(profile.id);
          }
          return WeNetServiceSimulator.createProxy(vertx).addUsers(addedApp.appId, appUsers);
        })).onComplete(added -> this.assertSuccessfulCompleted(testContext));

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
   * The identifier of the task type to use.
   *
   * @return the identifier of the task type to use.
   */
  protected abstract String getDefaultTaskTypeIdToUse();

  /**
   * Create the task type.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(4)
  public void shouldGetTaskType(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(3, testContext);

    final var taskTypeId = this.getDefaultTaskTypeIdToUse();
    testContext.assertComplete(WeNetTaskManager.createProxy(vertx).retrieveTaskType(taskTypeId))
        .onSuccess(foundTaskType -> {

          this.taskType = foundTaskType;
          this.assertSuccessfulCompleted(testContext);

        });

  }

  /**
   * Create the task to be stored and used on tests.
   *
   * @return the task to store and use on the tests.
   */
  protected Task createTask() {

    final var task = new Task();
    task.appId = this.app.appId;
    task.communityId = this.community.id;
    task.taskTypeId = this.taskType.id;
    task.goal = new HumanDescription();
    task.goal.name = "Task to test";
    task.requesterId = this.users.get(0).id;
    return task;
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
      final BooleanSupplier checkTask) {

    return WeNetTaskManagers.waitUntilTask(this.task.id, currentTask -> {

      this.task = currentTask;
      if (checkTask != null) {

        return checkTask.getAsBoolean();

      } else {

        return true;
      }

    }, vertx, testContext);

  }

  /**
   * Build the message predicates.
   */
  public static class MessagePredicateBuilder {

    /**
     * The builder predicates.
     */
    protected List<Predicate<Message>> predicates = new ArrayList<>();

    /**
     * The predicated to use.
     *
     * @return the build predicates.
     */
    public List<Predicate<Message>> build() {

      return this.predicates;
    }

    /**
     * Add a predicate to use.
     *
     * @param predicate to use.
     *
     * @return this builder.
     */
    public MessagePredicateBuilder with(final Predicate<Message> predicate) {

      this.predicates.add(predicate);
      return this;
    }

    /**
     * Add a predicate to match the messages with the specified label and receiver.
     *
     * @param label      to match for the message.
     * @param receiverId to match for the message.
     *
     * @return this builder.
     */
    public MessagePredicateBuilder withLabelAndReceiverId(final String label, final String receiverId) {

      return this.with(msg -> {

        return msg.label.equals(label) && msg.receiverId.equals(receiverId);

      });
    }

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
  protected Future<List<Message>> waitUntilCallbacks(final Vertx vertx, final VertxTestContext testContext,
      final List<Predicate<Message>> checkMessages) {

    return WeNetServiceSimulator.createProxy(vertx).retrieveCallbacks(this.app.appId).compose(callbacks -> {

      final List<Predicate<Message>> copy = new ArrayList<>(checkMessages);
      final List<Message> msgs = new ArrayList<>();
      for (var i = 0; i < callbacks.size(); i++) {

        final var msg = Model.fromJsonObject(callbacks.getJsonObject(i), Message.class);
        if (msg != null) {

          final var iter = copy.iterator();
          while (iter.hasNext()) {
            final var checkMessage = iter.next();
            if (checkMessage.test(msg)) {

              msgs.add(msg);
              iter.remove();
              break;
            }
          }
        }
      }

      if (copy.isEmpty()) {

        return WeNetServiceSimulator.createProxy(vertx).deleteCallbacks(this.app.appId)
            .compose(deleted -> Future.succeededFuture(msgs));

      } else {

        return this.waitUntilCallbacks(vertx, testContext, checkMessages);
      }

    });

  }

  /**
   * Wait until the task is created.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   *
   * @return the future that will return the created task.
   */
  protected Future<Task> waitUntilTaskCreated(final Vertx vertx, final VertxTestContext testContext) {

    return this.waitUntilTask(vertx, testContext, () -> {

      return this.task.transactions != null && !this.task.transactions.isEmpty();

    });
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

    final var future = WeNetTaskManager.createProxy(vertx).createTask(this.createTask()).compose(createdTask -> {

      this.task = createdTask;
      return this.waitUntilTaskCreated(vertx, testContext);
    });
    testContext.assertComplete(future).onComplete(stored -> this.assertSuccessfulCompleted(testContext));

  }

}
