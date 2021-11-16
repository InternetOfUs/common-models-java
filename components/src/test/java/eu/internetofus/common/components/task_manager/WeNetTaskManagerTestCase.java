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

package eu.internetofus.common.components.task_manager;

import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.WeNetComponentTestCase;
import eu.internetofus.common.components.models.Message;
import eu.internetofus.common.components.models.MessageTest;
import eu.internetofus.common.components.models.Task;
import eu.internetofus.common.components.models.TaskTest;
import eu.internetofus.common.components.models.TaskTransaction;
import eu.internetofus.common.components.models.TaskTransactionTest;
import eu.internetofus.common.components.models.TaskType;
import eu.internetofus.common.components.models.TaskTypeTest;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import java.util.UUID;
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
                  final var newAttributeValue = UUID.randomUUID().toString();
                  taskToMerge.attributes = new JsonObject().put("a_str", newAttributeValue);
                  testContext.assertComplete(service.mergeTask(id, taskToMerge))
                      .onSuccess(mergedTask -> testContext.verify(() -> {

                        taskToUpdate._lastUpdateTs = mergedTask._lastUpdateTs;
                        taskToUpdate.attributes.put("a_str", newAttributeValue);
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

  /**
   * Should not update undefined task type.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotUpdateUndefinedTaskType(final Vertx vertx, final VertxTestContext testContext) {

    testContext
        .assertFailure(this.createComponentProxy(vertx).updateTaskType("undefined-taskType-identifier", new TaskType()))
        .onFailure(handler -> testContext.completeNow());
  }

  /**
   * Should not merge undefined task type.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotMergeUndefinedTaskType(final Vertx vertx, final VertxTestContext testContext) {

    testContext
        .assertFailure(this.createComponentProxy(vertx).mergeTaskType("undefined-taskType-identifier", new TaskType()))
        .onFailure(handler -> testContext.completeNow());
  }

  /**
   * Should create, retrieve, update, merge and delete a task type.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldCreateRetrieveUpdateMergeAndDeleteTaskType(final Vertx vertx, final VertxTestContext testContext) {

    new TaskTypeTest().createModelExample(1, vertx, testContext).onSuccess(taskType -> {

      taskType.id = null;
      final var service = this.createComponentProxy(vertx);
      testContext.assertComplete(service.createTaskType(taskType)).onSuccess(createdTaskType -> {

        final var id = createdTaskType.id;
        testContext.assertComplete(service.retrieveTaskType(id)).onSuccess(retrieve -> testContext.verify(() -> {

          assertThat(createdTaskType).isEqualTo(retrieve);

          new TaskTypeTest().createModelExample(2, vertx, testContext).onSuccess(taskTypeToUpdate -> {
            testContext.assertComplete(service.updateTaskType(id, taskTypeToUpdate))
                .onSuccess(updatedTaskType -> testContext.verify(() -> {

                  taskTypeToUpdate.id = createdTaskType.id;
                  taskTypeToUpdate._creationTs = createdTaskType._creationTs;
                  taskTypeToUpdate._lastUpdateTs = updatedTaskType._lastUpdateTs;
                  assertThat(updatedTaskType).isEqualTo(taskTypeToUpdate);

                  final var taskTypeToMerge = new TaskType();
                  taskTypeToMerge.attributes = new JsonObject().put("type", "object").put("properties",
                      new JsonObject().put("newStringAttribute", new JsonObject().put("type", "string")));
                  testContext.assertComplete(service.mergeTaskType(id, taskTypeToMerge))
                      .onSuccess(mergedTaskType -> testContext.verify(() -> {

                        taskTypeToUpdate._lastUpdateTs = mergedTaskType._lastUpdateTs;
                        taskTypeToUpdate.attributes.getJsonObject("properties").put("newStringAttribute",
                            new JsonObject().put("type", "string"));
                        assertThat(mergedTaskType).isEqualTo(taskTypeToUpdate);
                        testContext.assertComplete(service.deleteTaskType(id)).onSuccess(empty -> {

                          testContext.assertFailure(service.retrieveTaskType(id))
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
   * Should get task types.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldGetTaskTypesPage(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertComplete(WeNetTaskManager.createProxy(vertx).getTaskTypesPage(null, null, null, null, 0, 10))
        .onSuccess(page -> testContext.verify(() -> {

          assertThat(page).isNotNull();
          assertThat(page.offset).isEqualTo(0);
          if (page.total == 0) {

            assertThat(page.taskTypes).isNull();

          } else {

            assertThat(page.taskTypes).isNotNull().isNotEmpty().hasSizeGreaterThanOrEqualTo(1)
                .hasSizeLessThanOrEqualTo(10);
          }

          testContext.completeNow();
        }));

  }

  /**
   * Should get tasks.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldGetTasksPage(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertComplete(WeNetTaskManager.createProxy(vertx).getTasksPage(null, null, null, null, null, null,
        null, null, null, null, null, null, null, 0, 10)).onSuccess(page -> testContext.verify(() -> {

          assertThat(page).isNotNull();
          assertThat(page.offset).isEqualTo(0);
          if (page.total == 0) {

            assertThat(page.tasks).isNull();

          } else {

            assertThat(page.tasks).isNotNull().isNotEmpty().hasSizeGreaterThanOrEqualTo(1).hasSizeLessThanOrEqualTo(10);
          }

          testContext.completeNow();
        }));

  }

  /**
   * Should get empty tasks.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldGetEmptyTasksPage(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertComplete(WeNetTaskManager.createProxy(vertx).getTasksPage("Undefined appId",
        "Undefined requesterId", "Undefined taskTypeId", "Undefined name", "undefined description", 0l, 1000l, 1000l,
        2000l, true, 1500l, 2500l, "appId,-taskTypeId", 0, 10)).onSuccess(page -> testContext.verify(() -> {

          assertThat(page).isNotNull();
          assertThat(page.offset).isEqualTo(0);
          assertThat(page.total).isEqualTo(0);
          assertThat(page.tasks).isNull();
          testContext.completeNow();

        }));

  }

  /**
   * Should get empty task types.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldGetEmptyTaskTypesPage(final Vertx vertx, final VertxTestContext testContext) {

    testContext
        .assertComplete(WeNetTaskManager.createProxy(vertx).getTaskTypesPage("Undefined name", "Undefined description",
            "Undefined keywords," + UUID.randomUUID().toString(), "-name,description", 0, 10))
        .onSuccess(page -> testContext.verify(() -> {

          assertThat(page).isNotNull();
          assertThat(page.offset).isEqualTo(0);
          assertThat(page.total).isEqualTo(0);
          assertThat(page.taskTypes).isNull();
          testContext.completeNow();

        }));

  }

  /**
   * Should defined task .
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldDefinedTask(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeTaskExample(0, vertx, testContext).onSuccess(task -> {

      testContext.assertComplete(this.createComponentProxy(vertx).isTaskDefined(task.id)).onSuccess(defined -> {

        testContext.verify(() -> {

          assertThat(defined).isTrue();
        });
        testContext.completeNow();

      });
    });

  }

  /**
   * Should not defined task .
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotDefinedTask(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertComplete(this.createComponentProxy(vertx).isTaskDefined("undefined-task-identifier"))
        .onSuccess(defined -> {

          testContext.verify(() -> {

            assertThat(defined).isFalse();
          });
          testContext.completeNow();

        });
  }

  /**
   * Should defined task type.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldDefinedTaskType(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeTaskTypeExample(0, vertx, testContext).onSuccess(taskType -> {

      testContext.assertComplete(this.createComponentProxy(vertx).isTaskTypeDefined(taskType.id)).onSuccess(defined -> {

        testContext.verify(() -> {

          assertThat(defined).isTrue();
        });
        testContext.completeNow();

      });
    });

  }

  /**
   * Should not defined task type.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotDefinedTaskType(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertComplete(this.createComponentProxy(vertx).isTaskTypeDefined("undefined-taskType-identifier"))
        .onSuccess(defined -> {

          testContext.verify(() -> {

            assertThat(defined).isFalse();
          });
          testContext.completeNow();

        });
  }

}
