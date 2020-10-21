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

package eu.internetofus.common.components.task_manager;

import java.util.function.Predicate;

import io.vertx.core.Future;
import io.vertx.core.Promise;
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

    final Promise<Task> promise = Promise.promise();
    WeNetTaskManager.createProxy(vertx).retrieveTask(taskId, testContext.succeeding(task -> {

      if (checkTask.test(task)) {

        promise.complete(task);

      } else if (!testContext.completed()) {

        waitUntilTask(taskId, checkTask, vertx, testContext).onComplete(testContext.succeeding(result -> promise.complete(result)));
      }

    }));

    return promise.future();
  }
}
