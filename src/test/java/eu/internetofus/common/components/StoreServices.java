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

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import eu.internetofus.common.components.profile_manager.CommunityProfile;
import eu.internetofus.common.components.profile_manager.CommunityProfileTest;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.components.profile_manager.WeNetUserProfile;
import eu.internetofus.common.components.profile_manager.WeNetUserProfileTest;
import eu.internetofus.common.components.service.App;
import eu.internetofus.common.components.service.AppTest;
import eu.internetofus.common.components.service.WeNetServiceSimulator;
import eu.internetofus.common.components.task_manager.Task;
import eu.internetofus.common.components.task_manager.TaskTest;
import eu.internetofus.common.components.task_manager.TaskType;
import eu.internetofus.common.components.task_manager.TaskTypeTest;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;

/**
 * Generic methods used to store models by the services.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface StoreServices {

  /**
   * Store a profile.
   *
   * @param profile      to store.
   * @param vertx        event bus to use.
   * @param testContext  test context to use.
   * @param storeHandler the component that will manage the stored model.
   */
  static void storeProfile(final WeNetUserProfile profile, final Vertx vertx, final VertxTestContext testContext, final Handler<AsyncResult<WeNetUserProfile>> storeHandler) {

    WeNetProfileManager.createProxy(vertx).createProfile(profile.toJsonObject(), testContext.succeeding(created -> {

      final var result = Model.fromJsonObject(created, WeNetUserProfile.class);
      storeHandler.handle(Future.succeededFuture(result));

    }));

  }

  /**
   * Store a profile example.
   *
   * @param index        of the example to store.
   * @param vertx        event bus to use.
   * @param testContext  test context to use.
   * @param storeHandler the component that will manage the stored model.
   */
  static void storeProfileExample(final int index, final Vertx vertx, final VertxTestContext testContext, final Handler<AsyncResult<WeNetUserProfile>> storeHandler) {

    new WeNetUserProfileTest().createModelExample(index, vertx, testContext, testContext.succeeding(example -> {

      storeProfile(example, vertx, testContext, storeHandler);

    }));

  }

  /**
   * Store a task type.
   *
   * @param taskType     to store.
   * @param vertx        event bus to use.
   * @param testContext  test context to use.
   * @param storeHandler the component that will manage the stored model.
   */
  static void storeTaskType(final TaskType taskType, final Vertx vertx, final VertxTestContext testContext, final Handler<AsyncResult<TaskType>> storeHandler) {

    WeNetTaskManager.createProxy(vertx).createTaskType(taskType, testContext.succeeding(created -> {

      storeHandler.handle(Future.succeededFuture(created));

    }));

  }

  /**
   * Store a task type example.
   *
   * @param index        of the example to store.
   * @param vertx        event bus to use.
   * @param testContext  test context to use.
   * @param storeHandler the component that will manage the stored model.
   */
  static void storeTaskTypeExample(final int index, final Vertx vertx, final VertxTestContext testContext, final Handler<AsyncResult<TaskType>> storeHandler) {

    final var example = new TaskTypeTest().createModelExample(index);
    storeTaskType(example, vertx, testContext, storeHandler);

  }

  /**
   * Store a task.
   *
   * @param task         to store.
   * @param vertx        event bus to use.
   * @param testContext  test context to use.
   * @param storeHandler the component that will manage the stored model.
   */
  static void storeTask(final Task task, final Vertx vertx, final VertxTestContext testContext, final Handler<AsyncResult<Task>> storeHandler) {

    WeNetTaskManager.createProxy(vertx).createTask(task, testContext.succeeding(created -> {

      storeHandler.handle(Future.succeededFuture(created));

    }));

  }

  /**
   * Store a task example.
   *
   * @param index        of the example to store.
   * @param vertx        event bus to use.
   * @param testContext  test context to use.
   * @param storeHandler the component that will manage the stored model.
   */
  static void storeTaskExample(final int index, final Vertx vertx, final VertxTestContext testContext, final Handler<AsyncResult<Task>> storeHandler) {

    new TaskTest().createModelExample(index, vertx, testContext, testContext.succeeding(example -> {

      storeTask(example, vertx, testContext, storeHandler);

    }));

  }

  /**
   * Store an application.
   *
   * @param app          to store.
   * @param vertx        event bus to use.
   * @param testContext  test context to use.
   * @param storeHandler the component that will manage the stored model.
   */
  static void storeApp(final App app, final Vertx vertx, final VertxTestContext testContext, final Handler<AsyncResult<App>> storeHandler) {

    WeNetServiceSimulator.createProxy(vertx).createApp(app, testContext.succeeding(created -> {

      storeHandler.handle(Future.succeededFuture(created));

    }));

  }

  /**
   * Store an application example.
   *
   * @param index        of the example to store.
   * @param vertx        event bus to use.
   * @param testContext  test context to use.
   * @param storeHandler the component that will manage the stored model.
   */
  static void storeAppExample(final int index, final Vertx vertx, final VertxTestContext testContext, final Handler<AsyncResult<App>> storeHandler) {

    final var example = new AppTest().createModelExample(index);
    storeApp(example, vertx, testContext, storeHandler);

  }

  /**
   * Store some tasks.
   *
   * @param max         number of tasks to create.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   * @param change      function to modify the pattern before to store it. The first argument is the index and the second
   *                    the created task.
   *
   * @return the future with the created tasks.
   */
  static Future<List<Task>> storeSomeTask(final int max, final Vertx vertx, final VertxTestContext testContext, final BiConsumer<Integer, Task> change) {

    final Promise<List<Task>> promise = Promise.promise();
    var future = promise.future();
    promise.complete(new ArrayList<Task>());
    for (var i = 0; i < max; i++) {

      final var exampleIndex = i;
      future = future.compose(tasks -> {

        final Promise<List<Task>> storePromise = Promise.promise();
        new TaskTest().createModelExample(exampleIndex, vertx, testContext, testContext.succeeding(task -> {

          if (change != null) {

            change.accept(exampleIndex, task);
          }
          storeTask(task, vertx, testContext, testContext.succeeding(storedTask -> {
            tasks.add(storedTask);
            storePromise.complete(tasks);
          }));

        }));
        return storePromise.future();
      });

    }

    return future;
  }

  /**
   * Store a community.
   *
   * @param community    to store.
   * @param vertx        event bus to use.
   * @param testContext  test context to use.
   * @param storeHandler the component that will manage the stored model.
   */
  static void storeCommunity(final CommunityProfile community, final Vertx vertx, final VertxTestContext testContext, final Handler<AsyncResult<CommunityProfile>> storeHandler) {

    WeNetProfileManager.createProxy(vertx).createCommunity(community, testContext.succeeding(created -> {

      storeHandler.handle(Future.succeededFuture(created));

    }));

  }

  /**
   * Store a community example.
   *
   * @param index        of the example to store.
   * @param vertx        event bus to use.
   * @param testContext  test context to use.
   * @param storeHandler the component that will manage the stored model.
   */
  static void storeCommunityExample(final int index, final Vertx vertx, final VertxTestContext testContext, final Handler<AsyncResult<CommunityProfile>> storeHandler) {

    new CommunityProfileTest().createModelExample(index, vertx, testContext, testContext.succeeding(example -> {

      example.id = null;
      storeCommunity(example, vertx, testContext, storeHandler);

    }));

  }
}
