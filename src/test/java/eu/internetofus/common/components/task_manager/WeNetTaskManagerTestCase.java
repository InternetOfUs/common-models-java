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

import eu.internetofus.common.components.WeNetComponentTestCase;
import eu.internetofus.common.components.service.Message;
import eu.internetofus.common.components.service.MessageTest;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;

/**
 * Test the {@link WeNetTaskManager}.
 *
 * @see WeNetTaskManager
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetTaskManagerTestCase extends WeNetComponentTestCase<WeNetTaskManager> {

  /**
   * {@inheritDoc}
   *
   * @see WeNetTaskManager#createProxy(Vertx)
   */
  @Override
  protected WeNetTaskManager createComponentProxy(final Vertx vertx) {

    return WeNetTaskManager.createProxy(vertx);
  }

  /**
   * Should not create a bad task.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotCreateBadTask(final Vertx vertx, final VertxTestContext testContext) {

    this.createComponentProxy(vertx).createTask(new JsonObject().put("undefinedField", "value"),
        testContext.failing(handler -> testContext.completeNow()));

  }

  /**
   * Should not retrieve undefined task.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNorRetrieveUndefinedTask(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertFailure(this.createComponentProxy(vertx).retrieveTask("undefined-task-identifier"))
        .onFailure(handler -> testContext.completeNow());

  }

  /**
   * Should not delete undefined task.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotDeleteUndefinedTask(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertFailure(this.createComponentProxy(vertx).deleteTask("undefined-task-identifier"))
        .onFailure(handler -> testContext.completeNow());
  }

  /**
   * Should not update undefined task.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotUpdateUndefinedTask(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertFailure(this.createComponentProxy(vertx).updateTask("undefined-task-identifier", new Task()))
        .onFailure(handler -> testContext.completeNow());
  }

  /**
   * Should not merge undefined task.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotMergeUndefinedTask(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertFailure(this.createComponentProxy(vertx).mergeTask("undefined-task-identifier", new Task()))
        .onFailure(handler -> testContext.completeNow());
  }

  /**
   * Should not do a transaction on an undefined task.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotDoTransactionMergeUndefinedTask(final Vertx vertx, final VertxTestContext testContext) {

    final var taskTransaction = new TaskTransaction();
    testContext.assertFailure(this.createComponentProxy(vertx).doTaskTransaction(taskTransaction))
        .onFailure(handler -> testContext.completeNow());
  }

  /**
   * Should create, retrieve, update, merge and delete a task.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldCreateRetrieveUpdateMergeAndDeleteTask(final Vertx vertx, final VertxTestContext testContext) {

    new TaskTest().createModelExample(1, vertx, testContext).onSuccess(task -> {

      task.id = null;
      final var service = this.createComponentProxy(vertx);
      testContext.assertComplete(service.createTask(task)).onSuccess(createdTask -> {

        final var id = createdTask.id;
        testContext.assertComplete(service.retrieveTask(id)).onSuccess(retrieve -> testContext.verify(() -> {

          assertThat(createdTask).isEqualTo(retrieve);

          new TaskTest().createModelExample(2, vertx, testContext).onSuccess(taskToUpdate -> {
            testContext.assertComplete(service.updateTask(id, taskToUpdate))
                .onSuccess(updatedTask -> testContext.verify(() -> {

                  taskToUpdate.id = createdTask.id;
                  taskToUpdate._creationTs = createdTask._creationTs;
                  taskToUpdate._lastUpdateTs = updatedTask._lastUpdateTs;
                  assertThat(updatedTask).isEqualTo(taskToUpdate);

                  final var taskToMerge = new Task();
                  taskToMerge.attributes = new JsonObject().put("newKey2", "value2");
                  testContext.assertComplete(service.mergeTask(id, taskToMerge))
                      .onSuccess(mergedTask -> testContext.verify(() -> {

                        taskToUpdate._lastUpdateTs = mergedTask._lastUpdateTs;
                        taskToUpdate.attributes.put("newKey2", "value2");
                        assertThat(mergedTask).isEqualTo(taskToUpdate);
                        testContext.assertComplete(service.deleteTask(id)).onSuccess(empty -> {

                          testContext.assertFailure(service.retrieveTask(id))
                              .onFailure(handler -> testContext.completeNow());

                        });

                      }));

                }));
          });
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

    this.createComponentProxy(vertx).createTaskType(new JsonObject().put("undefinedField", "value"),
        testContext.failing(handler -> testContext.completeNow()));

  }

  /**
   * Should not retrieve undefined task type.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNorRetrieveUndefinedTaskType(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertFailure(this.createComponentProxy(vertx).retrieveTaskType("undefined-task-type-identifier"))
        .onFailure(handler -> testContext.completeNow());
  }

  /**
   * Should not delete undefined task type.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotDeleteUndefinedTaskType(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertFailure(this.createComponentProxy(vertx).deleteTaskType("undefined-task-type-identifier"))
        .onFailure(handler -> testContext.completeNow());
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
    taskType.id = null;
    final var service = this.createComponentProxy(vertx);
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

  /**
   * Should not add transaction into an undefined task.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotAddTransactionIntoUndefinedTask(final Vertx vertx, final VertxTestContext testContext) {

    final var taskTransaction = new TaskTransaction();
    testContext.assertFailure(this.createComponentProxy(vertx).addTransactionIntoTask("undefined", taskTransaction))
        .onFailure(handler -> testContext.completeNow());
  }

  /**
   * Should not add transaction into an undefined task.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotAddMessageIntoUndefinedTask(final Vertx vertx, final VertxTestContext testContext) {

    final var message = new Message();
    testContext
        .assertFailure(this.createComponentProxy(vertx).addMessageIntoTransaction("undefined", "undefined", message))
        .onFailure(handler -> testContext.completeNow());
  }

  /**
   * Should not add transaction into an undefined transaction.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotAddMessageIntoUndefinedTransaction(final Vertx vertx, final VertxTestContext testContext) {

    new TaskTest().createModelExample(1, vertx, testContext).onSuccess(task -> {

      task.id = null;
      final var service = this.createComponentProxy(vertx);
      testContext.assertComplete(service.createTask(task)).onSuccess(createdTask -> {

        final var message = new Message();
        testContext
            .assertFailure(
                this.createComponentProxy(vertx).addMessageIntoTransaction(createdTask.id, "undefined", message))
            .onFailure(handler -> testContext.completeNow());

      });
    });
  }

  /**
   * Should add transaction and message into a task.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldAddTransactionAndMessageIntoTask(final Vertx vertx, final VertxTestContext testContext) {

    new TaskTransactionTest().createModelExample(1, vertx, testContext).onSuccess(taskTransaction -> {

      taskTransaction.id = null;
      final var service = this.createComponentProxy(vertx);
      testContext.assertComplete(service.addTransactionIntoTask(taskTransaction.taskId, taskTransaction))
          .onSuccess(addedTransaction -> {

            testContext.assertComplete(service.retrieveTask(taskTransaction.taskId))
                .onSuccess(retrieve -> testContext.verify(() -> {

                  assertThat(retrieve.transactions).isNotEmpty().contains(addedTransaction);
                  final var message = new MessageTest().createModelExample(1);
                  message.appId = retrieve.appId;
                  message.receiverId = retrieve.requesterId;
                  testContext
                      .assertComplete(service.addMessageIntoTransaction(retrieve.id, addedTransaction.id, message))
                      .onSuccess(addedMessage -> {

                        testContext.assertComplete(service.retrieveTask(addedTransaction.taskId))
                            .onSuccess(retrieve2 -> testContext.verify(() -> {

                              assertThat(retrieve2.transactions).isNotEmpty();
                              final var transaction2 = retrieve2.transactions.get(0);
                              assertThat(transaction2.messages).isNotEmpty().contains(addedMessage);
                              testContext.assertComplete(service.deleteTask(addedTransaction.taskId))
                                  .onSuccess(empty -> testContext.completeNow());
                            }));
                      });

                }));
          });

    });
  }

}
