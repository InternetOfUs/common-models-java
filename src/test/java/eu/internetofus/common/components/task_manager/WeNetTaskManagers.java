/*
 * -----------------------------------------------------------------------------
 *
 *   Copyright 2019 - 2022 UDT-IA, IIIA-CSIC
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

package eu.internetofus.common.components.task_manager;

import java.util.function.Predicate;
import eu.internetofus.common.components.models.Task;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;

/**
 * Utility methods to interact with the {@link WeNetTaskManager}.
 *
 * @see WeNetTaskManager
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface WeNetTaskManagers {

  /**
   * Wait until the task satisfy a predicate.
   *
   * @param taskId      identifier of the task to get the information.
   * @param checkTask   the function that has to be true to the task is on the state that is waiting.
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   *
   * @return the future that will return the task that satisfy the predicate.
   */
  static Future<Task> waitUntilTask(final String taskId, final Predicate<Task> checkTask, final Vertx vertx, final VertxTestContext testContext) {

    return testContext.assertComplete(WeNetTaskManager.createProxy(vertx).retrieveTask(taskId)).compose(task -> {

      if (checkTask.test(task)) {

        return Future.succeededFuture(task);

      } else if (!testContext.completed()) {

        return waitUntilTask(taskId, checkTask, vertx, testContext);

      } else {

        return Future.failedFuture("Test finished");
      }

    });

  }
}
