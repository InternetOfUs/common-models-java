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
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Interaction test case over a protocol.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@TestMethodOrder(OrderAnnotation.class)
public abstract class AbstractProtocolITC {

  /**
   * Number maximum of users to use on the test.
   */
  public static final int MAX_USERS = 6;

  /**
   * The users that has been created.
   */
  protected static List<WeNetUserProfile> users;

  /**
   * The application that will involved on the test.
   */
  protected static App app;

  /**
   * The community that will involved on the test.
   */
  protected static CommunityProfile community;

  /**
   * The task type that will involved on the test.
   */
  protected static TaskType taskType;

  /**
   * The task that will involved on the test.
   */
  protected static Task task;

  /**
   * Create the users that will be used on the tests.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(1)
  public void shouldCreateUsers(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeProfileExample(1, vertx, testContext).onSuccess(me -> {

      testContext.assertComplete(createUsers(MAX_USERS, vertx, testContext)).onSuccess(createdUsers -> {
        users = createdUsers;
        users.add(0, me);
        testContext.completeNow();
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

    assert users != null;
    testContext
        .assertComplete(WeNetServiceSimulator.createProxy(vertx).createApp(this.createApp()).compose(addedApp -> {
          app = addedApp;
          final var appUsers = new JsonArray();
          for (final WeNetUserProfile profile : users) {

            appUsers.add(profile.id);
          }
          return WeNetServiceSimulator.createProxy(vertx).addUsers(addedApp.appId, appUsers);
        })).onComplete(added -> testContext.completeNow());

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

    assert app != null;

    testContext.assertComplete(App.getOrCreateDefaultCommunityFor(app.appId, vertx)).onSuccess(added -> {

      community = added;
      testContext.completeNow();
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

    assert community != null;

    final var taskTypeId = this.getDefaultTaskTypeIdToUse();
    testContext.assertComplete(WeNetTaskManager.createProxy(vertx).retrieveTaskType(taskTypeId))
        .onSuccess(foundTaskType -> {

          taskType = foundTaskType;
          testContext.completeNow();
        });

  }

  /**
   * Create the task to be stored and used on tests.
   *
   * @return the task to store and use on the tests.
   */
  protected Task createTask() {

    final var task = new Task();
    task.appId = app.appId;
    task.communityId = community.id;
    task.taskTypeId = taskType.id;
    task.goal = new HumanDescription();
    task.goal.name = "Task to test";
    task.requesterId = users.get(0).id;
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

    return WeNetTaskManagers.waitUntilTask(task.id, currentTask -> {

      task = currentTask;
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

    return WeNetServiceSimulator.createProxy(vertx).retrieveCallbacks(app.appId).compose(callbacks -> {

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

        return WeNetServiceSimulator.createProxy(vertx).deleteCallbacks(app.appId)
            .compose(deleted -> Future.succeededFuture(msgs));

      } else {

        return this.waitUntilCallbacks(vertx, testContext, checkMessages);
      }

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

    assert taskType != null;

    final var future = WeNetTaskManager.createProxy(vertx).createTask(this.createTask()).compose(createdTask -> {

      task = createdTask;
      return this.waitUntilTask(vertx, testContext, () -> {

        return task.transactions != null && !task.transactions.isEmpty();

      });
    });
    testContext.assertComplete(future).onComplete(stored -> testContext.completeNow());
  }

}
