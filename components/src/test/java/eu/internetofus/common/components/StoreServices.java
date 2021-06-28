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

package eu.internetofus.common.components;

import eu.internetofus.common.model.Models.CommunityProfile;
import eu.internetofus.common.model.Models.CommunityProfileTest;
import eu.internetofus.common.model.Models.Task;
import eu.internetofus.common.model.Models.TaskTest;
import eu.internetofus.common.model.Models.TaskType;
import eu.internetofus.common.model.Models.TaskTypeTest;
import eu.internetofus.common.model.Models.WeNetUserProfile;
import eu.internetofus.common.model.Models.WeNetUserProfileTest;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.components.service.App;
import eu.internetofus.common.components.service.AppTest;
import eu.internetofus.common.components.service.WeNetServiceSimulator;
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

    return testContext
        .assertComplete(new WeNetUserProfileTest().createModelExample(index, vertx, testContext).compose(example -> {

          example.id = null;
          return storeProfile(example, vertx, testContext);

        }));

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

    return testContext
        .assertComplete(new TaskTypeTest().createModelExample(index, vertx, testContext).compose(example -> {

          example.id = null;
          return storeTaskType(example, vertx, testContext);

        }));

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

    return testContext.assertComplete(new TaskTest().createModelExample(index, vertx, testContext).compose(example -> {

      example.id = null;
      return storeTask(example, vertx, testContext);

    }));

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
    example.appId = null;
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
  static Future<List<Task>> storeSomeTask(final int max, final long delay, final Vertx vertx,
      final VertxTestContext testContext, final BiConsumer<Integer, Task> change) {

    Future<List<Task>> future = Future.succeededFuture(new ArrayList<Task>());
    for (var i = 0; i < max; i++) {

      final var exampleIndex = i;
      future = future
          .compose(tasks -> new TaskTest().createModelExample(exampleIndex, vertx, testContext).compose(task -> {

            if (change != null) {

              change.accept(exampleIndex, task);
            }
            task.id = null;

            return storeTask(task, vertx, testContext).compose(storedTask -> {

              tasks.add(storedTask);
              final Promise<List<Task>> promise = Promise.promise();
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
