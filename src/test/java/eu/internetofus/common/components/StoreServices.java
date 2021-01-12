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
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Generic methods used to store models by the services.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface StoreServices {

  /**
   * Store a profile.
   *
   * @param profile     to store.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @return the stored profile.
   */
  static Future<WeNetUserProfile> storeProfile(final WeNetUserProfile profile, final Vertx vertx,
      final VertxTestContext testContext) {

    return testContext.assertComplete(WeNetProfileManager.createProxy(vertx).createProfile(profile));

  }

  /**
   * Store a profile example.
   *
   * @param index       of the example to store.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @return the stored profile.
   */
  static Future<WeNetUserProfile> storeProfileExample(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    return testContext.assertComplete(new WeNetUserProfileTest().createModelExample(index, vertx, testContext)
        .compose(example -> storeProfile(example, vertx, testContext)));

  }

  /**
   * Store a task type.
   *
   * @param taskType    to store.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @return the stored model.
   */
  static Future<TaskType> storeTaskType(final TaskType taskType, final Vertx vertx,
      final VertxTestContext testContext) {

    return testContext.assertComplete(WeNetTaskManager.createProxy(vertx).createTaskType(taskType));

  }

  /**
   * Store a task type example.
   *
   * @param index       of the example to store.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @return the stored model.
   */
  static Future<TaskType> storeTaskTypeExample(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final var example = new TaskTypeTest().createModelExample(index);
    return storeTaskType(example, vertx, testContext);

  }

  /**
   * Store a task.
   *
   * @param task        to store.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @return the stored task.
   */
  static Future<Task> storeTask(final Task task, final Vertx vertx, final VertxTestContext testContext) {

    return testContext.assertComplete(WeNetTaskManager.createProxy(vertx).createTask(task));

  }

  /**
   * Store a task example.
   *
   * @param index       of the example to store.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @return the stored task.
   */
  static Future<Task> storeTaskExample(final int index, final Vertx vertx, final VertxTestContext testContext) {

    return testContext.assertComplete(new TaskTest().createModelExample(index, vertx, testContext)
        .compose(example -> storeTask(example, vertx, testContext)));

  }

  /**
   * Store an application.
   *
   * @param app         to store.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @return the stored application.
   */
  static Future<App> storeApp(final App app, final Vertx vertx, final VertxTestContext testContext) {

    return testContext.assertComplete(WeNetServiceSimulator.createProxy(vertx).createApp(app));

  }

  /**
   * Store an application example.
   *
   * @param index       of the example to store.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @return the stored application.
   */
  static Future<App> storeAppExample(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final var example = new AppTest().createModelExample(index);
    return testContext.assertComplete(storeApp(example, vertx, testContext));

  }

  /**
   * Store some tasks.
   *
   * @param max         number of tasks to create.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   * @param change      function to modify the pattern before to store it. The
   *                    first argument is the index and the second the created
   *                    task.
   *
   * @return the future with the created tasks.
   */
  static Future<List<Task>> storeSomeTask(final int max, final Vertx vertx, final VertxTestContext testContext,
      final BiConsumer<Integer, Task> change) {

    return storeSomeTask(max, 0l, vertx, testContext, change);
  }

  /**
   * Store some tasks.
   *
   * @param max         number of tasks to create.
   * @param delay       in the creation of the tasks.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   * @param change      function to modify the pattern before to store it. The
   *                    first argument is the index and the second the created
   *                    task.
   *
   * @return the future with the created tasks.
   */
  static Future<List<Task>> storeSomeTask(final int max, long delay, final Vertx vertx,
      final VertxTestContext testContext, final BiConsumer<Integer, Task> change) {

    Future<List<Task>> future = Future.succeededFuture(new ArrayList<Task>());
    for (var i = 0; i < max; i++) {

      final var exampleIndex = i;
      future = future
          .compose(tasks -> new TaskTest().createModelExample(exampleIndex, vertx, testContext).compose(task -> {

            if (change != null) {

              change.accept(exampleIndex, task);
            }

            return storeTask(task, vertx, testContext).compose(storedTask -> {

              tasks.add(storedTask);
              Promise<List<Task>> promise = Promise.promise();
              if (delay > 0) {

                vertx.setTimer(delay, id -> promise.complete(tasks));

              } else {
                promise.complete(tasks);
              }

              return promise.future();

            });

          }));

    }

    return future;
  }

  /**
   * Store a community.
   *
   * @param community   to store.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @return the stored profile.
   */
  static Future<CommunityProfile> storeCommunity(final CommunityProfile community, final Vertx vertx,
      final VertxTestContext testContext) {

    return testContext.assertComplete(WeNetProfileManager.createProxy(vertx).createCommunity(community));

  }

  /**
   * Store a community example.
   *
   * @param index       of the example to store.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @return the stored profile.
   */
  static Future<CommunityProfile> storeCommunityExample(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    return testContext
        .assertComplete(new CommunityProfileTest().createModelExample(index, vertx, testContext).compose(example -> {

          example.id = null;
          return storeCommunity(example, vertx, testContext);

        }));

  }
}
