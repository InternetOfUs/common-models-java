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
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
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

    WeNetTaskManager.createProxy(vertx).createTask(new JsonObject().put("undefinedField", "value"), testContext.failing(handler -> {
      testContext.completeNow();
    }));

  }

  /**
   * Should not retrieve undefined task.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNorRetrieveUndefinedTask(final Vertx vertx, final VertxTestContext testContext) {

    WeNetTaskManager.createProxy(vertx).retrieveTask("undefined-task-identifier", testContext.failing(handler -> {
      testContext.completeNow();
    }));

  }

  /**
   * Should not delete undefined task.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotDeleteUndefinedTask(final Vertx vertx, final VertxTestContext testContext) {

    WeNetTaskManager.createProxy(vertx).deleteTask("undefined-task-identifier", testContext.failing(handler -> {
      testContext.completeNow();

    }));

  }

  /**
   * Should not update undefined task.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotUpdateUndefinedTask(final Vertx vertx, final VertxTestContext testContext) {

    WeNetTaskManager.createProxy(vertx).updateTask("undefined-task-identifier", new Task(), testContext.failing(handler -> {
      testContext.completeNow();

    }));

  }

  /**
   * Should not do a transaction on an undefined task.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotDoTranactionUpdateUndefinedTask(final Vertx vertx, final VertxTestContext testContext) {

    final TaskTransaction taskTransaction = new TaskTransaction();
    WeNetTaskManager.createProxy(vertx).doTaskTransaction(taskTransaction, testContext.failing(handler -> {
      testContext.completeNow();

    }));

  }

  /**
   * Should create, retrieve and delete a task.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldCreateRetrieveUpdateAndDeleteTask(final Vertx vertx, final VertxTestContext testContext) {

    new TaskTest().createModelExample(1, vertx, testContext, testContext.succeeding(task -> {
      final WeNetTaskManager service = WeNetTaskManager.createProxy(vertx);
      service.createTask(task, testContext.succeeding(create -> {

        final String id = create.id;
        service.retrieveTask(id, testContext.succeeding(retrieve -> testContext.verify(() -> {

          assertThat(create).isEqualTo(retrieve);
          new TaskTest().createModelExample(2, vertx, testContext, testContext.succeeding(source -> {
            service.updateTask(id, source, testContext.succeeding(merged -> testContext.verify(() -> {

              source.id = id;
              assertThat(merged).isEqualTo(source);
              service.deleteTask(id, testContext.succeeding(empty -> {

                service.retrieveTask(id, testContext.failing(handler -> {
                  testContext.completeNow();

                }));

              }));

            })));

          }));

        })));

      }));

    }));
  }

  /**
   * Should not create a bad task type.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotCreateBadTaskType(final Vertx vertx, final VertxTestContext testContext) {

    WeNetTaskManager.createProxy(vertx).createTaskType(new JsonObject().put("undefinedField", "value"), testContext.failing(handler -> {
      testContext.completeNow();

    }));

  }

  /**
   * Should not retrieve undefined task type.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNorRetrieveUndefinedTaskType(final Vertx vertx, final VertxTestContext testContext) {

    WeNetTaskManager.createProxy(vertx).retrieveTaskType("undefined-task-type-identifier", testContext.failing(handler -> {
      testContext.completeNow();

    }));

  }

  /**
   * Should not delete undefined task type.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotDeleteUndefinedTaskType(final Vertx vertx, final VertxTestContext testContext) {

    WeNetTaskManager.createProxy(vertx).deleteTaskType("undefined-task-type-identifier", testContext.failing(handler -> {
      testContext.completeNow();

    }));

  }

  /**
   * Should create, retrieve and delete a task type.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldCreateRetrieveAndDeleteTaskType(final Vertx vertx, final VertxTestContext testContext) {

    final TaskType taskType = new TaskTypeTest().createModelExample(1);
    final WeNetTaskManager service = WeNetTaskManager.createProxy(vertx);
    service.createTaskType(taskType, testContext.succeeding(create -> {

      final String id = create.id;
      service.retrieveTaskType(id, testContext.succeeding(retrieve -> testContext.verify(() -> {

        assertThat(create).isEqualTo(retrieve);
        service.deleteTaskType(id, testContext.succeeding(empty -> {

          service.retrieveTaskType(id, testContext.failing(handler -> {
            testContext.completeNow();

          }));

        }));

      })));

    }));

  }

}
