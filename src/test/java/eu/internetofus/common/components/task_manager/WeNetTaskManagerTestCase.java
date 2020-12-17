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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import eu.internetofus.common.components.Model;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link WeNetTaskManager}.
 *
 * @see WeNetTaskManager
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class WeNetTaskManagerTestCase {

  /**
   * Should not create a bad task.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotCreateBadTask(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertFailure(WeNetTaskManager.createProxy(vertx).createTask(new JsonObject().put("undefinedField", "value"))).onFailure(handler -> testContext.completeNow());

  }

  /**
   * Should not retrieve undefined task.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNorRetrieveUndefinedTask(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertFailure(WeNetTaskManager.createProxy(vertx).retrieveTask("undefined-task-identifier")).onFailure(handler -> testContext.completeNow());

  }

  /**
   * Should not delete undefined task.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotDeleteUndefinedTask(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertFailure(WeNetTaskManager.createProxy(vertx).deleteTask("undefined-task-identifier")).onFailure(handler -> testContext.completeNow());
  }

  /**
   * Should not merge undefined task.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotMergeUndefinedTask(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertFailure(WeNetTaskManager.createProxy(vertx).mergeTask("undefined-task-identifier", new Task())).onFailure(handler -> testContext.completeNow());
  }

  /**
   * Should not do a transaction on an undefined task.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotDoTranactionMergeUndefinedTask(final Vertx vertx, final VertxTestContext testContext) {

    final var taskTransaction = new TaskTransaction();
    testContext.assertFailure(WeNetTaskManager.createProxy(vertx).doTaskTransaction(taskTransaction)).onFailure(handler -> testContext.completeNow());
  }

  /**
   * Should create, retrieve and delete a task.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldCreateRetrieveMergeAndDeleteTask(final Vertx vertx, final VertxTestContext testContext) {

    new TaskTest().createModelExample(1, vertx, testContext).onSuccess(task -> {

      final var service = WeNetTaskManager.createProxy(vertx);
      testContext.assertComplete(service.createTask(task)).onSuccess(createdTask -> {

        final var id = createdTask.id;
        testContext.assertComplete(service.retrieveTask(id)).onSuccess(retrieve -> testContext.verify(() -> {

          assertThat(createdTask).isEqualTo(retrieve);
          final var taskToMerge = Model.fromJsonObject(createdTask.toJsonObject(), Task.class);
          taskToMerge.attributes = new JsonObject().put("newKey", "NEW VALUE");
          testContext.assertComplete(service.mergeTask(id, taskToMerge)).onSuccess(mergedTask -> testContext.verify(() -> {

            createdTask._lastUpdateTs = mergedTask._lastUpdateTs;
            createdTask.attributes = new JsonObject().put("newKey", "NEW VALUE");
            assertThat(mergedTask).isEqualTo(createdTask);
            testContext.assertComplete(service.deleteTask(id)).onSuccess(empty -> {

              testContext.assertFailure(service.retrieveTask(id)).onFailure(handler -> testContext.completeNow());

            });

          }));

        }));

      });

    });
  }

  /**
   * Should not create a bad task type.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotCreateBadTaskType(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertFailure(WeNetTaskManager.createProxy(vertx).createTaskType(new JsonObject().put("undefinedField", "value"))).onFailure(handler -> testContext.completeNow());
  }

  /**
   * Should not retrieve undefined task type.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNorRetrieveUndefinedTaskType(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertFailure(WeNetTaskManager.createProxy(vertx).retrieveTaskType("undefined-task-type-identifier")).onFailure(handler -> testContext.completeNow());
  }

  /**
   * Should not delete undefined task type.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotDeleteUndefinedTaskType(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertFailure(WeNetTaskManager.createProxy(vertx).deleteTaskType("undefined-task-type-identifier")).onFailure(handler -> testContext.completeNow());
  }

  /**
   * Should create, retrieve and delete a task type.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldCreateRetrieveAndDeleteTaskType(final Vertx vertx, final VertxTestContext testContext) {

    final var taskType = new TaskTypeTest().createModelExample(1);
    final var service = WeNetTaskManager.createProxy(vertx);
    testContext.assertComplete(service.createTaskType(taskType)).onSuccess(create -> {

      final var id = create.id;
      testContext.assertComplete(service.retrieveTaskType(id)).onSuccess(retrieve -> testContext.verify(() -> {

        assertThat(create).isEqualTo(retrieve);
        testContext.assertComplete(service.deleteTaskType(id)).onSuccess(empty -> {

          testContext.assertFailure(service.retrieveTaskType(id)).onFailure(handler -> testContext.completeNow());

        });
      }));
    });

  }

}
