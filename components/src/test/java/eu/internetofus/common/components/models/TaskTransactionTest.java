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

package eu.internetofus.common.components.models;

import static eu.internetofus.common.model.ValidableAsserts.assertIsNotValid;
import static eu.internetofus.common.model.ValidableAsserts.assertIsValid;

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.WeNetIntegrationExtension;
import eu.internetofus.common.components.WeNetValidateContext;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import eu.internetofus.common.model.ModelTestCase;
import eu.internetofus.common.model.TimeManager;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test the {@link TaskTransaction}.
 *
 * @see TaskTransaction
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetIntegrationExtension.class)
public class TaskTransactionTest extends ModelTestCase<TaskTransaction> {

  /**
   * {@inheritDoc}
   */
  @Override
  public TaskTransaction createModelExample(final int index) {

    assert index >= 0;
    final var model = new TaskTransaction();
    model._creationTs = index;
    model._lastUpdateTs = TimeManager.now() - index;
    model.actioneerId = "actioneerId_" + index;
    model.attributes = new JsonObject().put("index", index);
    model.id = "id_" + index;
    model.label = "label_" + index;
    model.messages = new ArrayList<>();
    model.messages.add(new MessageTest().createModelExample(index));
    model.taskId = "taskId_" + index;
    return model;

  }

  /**
   * Create a valid task transaction example.
   *
   * @param index       of the example to create.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @return a future with the created task transaction.
   *
   */
  public Future<TaskTransaction> createModelExample(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    return StoreServices.storeTaskExample(index, vertx, testContext).compose(task -> {

      final var model = this.createModelExample(index);
      model.taskId = task.id;
      model.actioneerId = task.requesterId;
      model.label = "t_one";
      model.attributes = new JsonObject().put("index", index);
      return Future.succeededFuture(model);

    });
  }

  /**
   * Check that the {@link #createModelExample(int)} is not valid.
   *
   * @param index       to verify.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskTransaction#validate(WeNetValidateContext)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleNotBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(index);
    assertIsNotValid(model, "actioneerId", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that the {@link #createModelExample(int,Vertx,VertxTestContext)} is
   * valid.
   *
   * @param index       to verify.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskTransaction#validate(WeNetValidateContext)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(index, vertx, testContext).onComplete(testContext
        .succeeding(model -> assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext)));

  }

  /**
   * An empty transaction not be valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskTransaction#validate(WeNetValidateContext)
   */
  @Test
  public void shouldEmptyTransactionBeNotValid(final Vertx vertx, final VertxTestContext testContext) {

    assertIsNotValid(new TaskTransaction(), "label", new WeNetValidateContext("codePrefix", vertx), testContext);
  }

  /**
   * A task transaction without task identifier cannot be valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskTransaction#validate(WeNetValidateContext)
   */
  @Test
  public void shouldTaskTransactionBeNotValidWithoutTaskId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onComplete(testContext.succeeding(model -> {
      model.taskId = null;
      assertIsNotValid(model, "taskId", new WeNetValidateContext("codePrefix", vertx), testContext);
    }));

  }

  /**
   * A task transaction without a {@code null} label.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskTransaction#validate(WeNetValidateContext)
   */
  @Test
  public void shouldTaskTransactionBeNotValidWithoutTaskLabel(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onComplete(testContext.succeeding(model -> {
      model.label = null;
      assertIsNotValid(model, "label", new WeNetValidateContext("codePrefix", vertx), testContext);

    }));
  }

  /**
   * A task transaction without a bad task type.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskTransaction#validate(WeNetValidateContext)
   */
  @Test
  public void shouldTaskTransactionBeNotValidWithUndefinedTaskType(final Vertx vertx,
      final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onComplete(testContext.succeeding(model -> WeNetTaskManager
        .createProxy(vertx).retrieveTask(model.taskId)
        .compose(task -> WeNetTaskManager.createProxy(vertx).deleteTaskType(task.taskTypeId))
        .onComplete(testContext.succeeding(
            empty -> assertIsNotValid(model, "taskId", new WeNetValidateContext("codePrefix", vertx), testContext)))));

  }

  /**
   * A task transaction without a type without transactions.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskTransaction#validate(WeNetValidateContext)
   */
  @Test
  public void shouldTaskTransactionBeNotValidWithTaskTypeWithoutTransactions(final Vertx vertx,
      final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext)
        .onComplete(testContext.succeeding(model -> WeNetTaskManager.createProxy(vertx).retrieveTask(model.taskId)
            .compose(task -> WeNetTaskManager.createProxy(vertx).deleteTaskType(task.taskTypeId).compose(empty -> {

              final var newType = new TaskType();
              newType.id = task.taskTypeId;
              return WeNetTaskManager.createProxy(vertx).createTaskType(newType);

            })).onComplete(testContext.succeeding(empty -> assertIsNotValid(model, "label",
                new WeNetValidateContext("codePrefix", vertx), testContext)))));

  }

  /**
   * A task transaction without a type with transaction but any is the label.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskTransaction#validate(WeNetValidateContext)
   */
  @Test
  public void shouldTaskTransactionBeNotValidWithTaskTypeWithoutLabelTransaction(final Vertx vertx,
      final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext)
        .onComplete(testContext.succeeding(model -> WeNetTaskManager.createProxy(vertx).retrieveTask(model.taskId)
            .compose(task -> WeNetTaskManager.createProxy(vertx).deleteTaskType(task.taskTypeId).compose(empty -> {

              final var newType = new TaskType();
              newType.id = task.taskTypeId;
              newType.transactions = new JsonObject().put("undefined", new JsonObject());
              return WeNetTaskManager.createProxy(vertx).createTaskType(newType);

            })).onComplete(testContext.succeeding(empty -> assertIsNotValid(model, "label",
                new WeNetValidateContext("codePrefix", vertx), testContext)))));

  }

  /**
   * A task transaction without attributes when the type define attributes.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskTransaction#validate(WeNetValidateContext)
   */
  @Test
  public void shouldTaskTransactionBeNotValidWithoutAttributesWhenTypeDefineAttributes(final Vertx vertx,
      final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onComplete(testContext.succeeding(model -> {
      model.attributes = null;
      assertIsNotValid(model, "attributes", new WeNetValidateContext("codePrefix", vertx), testContext);

    }));
  }

  /**
   * A task transaction with empty attributes when the type define attributes.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskTransaction#validate(WeNetValidateContext)
   */
  @Test
  public void shouldTaskTransactionBeNotValidWithEmptyAttributesWhenTypeDefineAttributes(final Vertx vertx,
      final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onComplete(testContext.succeeding(model -> {
      model.attributes = new JsonObject();
      assertIsNotValid(model, "attributes.index", new WeNetValidateContext("codePrefix", vertx), testContext);

    }));
  }

  /**
   * A task transaction with an undefined attributes on the type.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskTransaction#validate(WeNetValidateContext)
   */
  @Test
  public void shouldTaskTransactionBeNotValidWithUndefinedAttributesOnTheType(final Vertx vertx,
      final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onComplete(testContext.succeeding(model -> {
      model.attributes.put("undefined", "value");
      assertIsNotValid(model, "attributes.undefined", new WeNetValidateContext("codePrefix", vertx), testContext);

    }));
  }

  /**
   * A task transaction be valid when missing a nullable attribute.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskTransaction#validate(WeNetValidateContext)
   */
  @Test
  public void shouldTaskTransactionBeValidWithTaskTypeWithLabelButMissingNullableAttributes(final Vertx vertx,
      final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onComplete(testContext.succeeding(model -> {
      WeNetTaskManager.createProxy(vertx).retrieveTask(model.taskId).compose(task -> {

        final var newType = new TaskType();
        newType.id = task.taskTypeId;
        newType.transactions = new JsonObject().put(model.label,
            new JsonObject().put("type", "object").put("properties",
                new JsonObject().put("arg1", new JsonObject().put("type", "string").put("nullable", true)).put("arg2",
                    new JsonObject().put("type", "string").put("nullable", true))));
        return WeNetTaskManager.createProxy(vertx).mergeTaskType(task.taskTypeId, newType);

      }).onComplete(testContext
          .succeeding(empty -> assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext)));
    }));

  }

  /**
   * A creation transaction has not to be valid when has attributes.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskTransaction#validate(WeNetValidateContext)
   */
  @Test
  public void shouldTransactionIsNotValidWhenIsCreateTaskWithSomeAttributes(final Vertx vertx,
      final VertxTestContext testContext) {

    final var model = new TaskTransaction();
    model.label = TaskTransaction.CREATE_TASK_LABEL;
    model.attributes = new JsonObject().put("key", "value");
    assertIsNotValid(model, "attributes", new WeNetValidateContext("codePrefix", vertx), testContext);
  }

  /**
   * A creation transaction that is valid because is a
   * {@link TaskTransaction#CREATE_TASK_LABEL} with empty attributes.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskTransaction#validate(WeNetValidateContext)
   */
  @Test
  public void shouldTransactionBeValidWhenIsCreateTaskWithEmptyAttributes(final Vertx vertx,
      final VertxTestContext testContext) {

    final var model = new TaskTransaction();
    model.label = TaskTransaction.CREATE_TASK_LABEL;
    model.attributes = new JsonObject();
    assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext);
  }

  /**
   * A creation transaction that is valid because is a
   * {@link TaskTransaction#CREATE_TASK_LABEL} with {@code null} attributes.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskTransaction#validate(WeNetValidateContext)
   */
  @Test
  public void shouldTransactionBeValidWhenIsCreateTaskWithNullAttributes(final Vertx vertx,
      final VertxTestContext testContext) {

    final var model = new TaskTransaction();
    model.label = TaskTransaction.CREATE_TASK_LABEL;
    assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext);
  }

}
