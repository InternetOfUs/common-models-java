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

import static eu.internetofus.common.model.MergeAsserts.assertCanMerge;
import static eu.internetofus.common.model.MergeAsserts.assertCannotMerge;
import static eu.internetofus.common.model.UpdateAsserts.assertCanUpdate;
import static eu.internetofus.common.model.UpdateAsserts.assertCannotUpdate;
import static eu.internetofus.common.model.ValidableAsserts.assertIsNotValid;
import static eu.internetofus.common.model.ValidableAsserts.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.WeNetIntegrationExtension;
import eu.internetofus.common.components.WeNetValidateContext;
import eu.internetofus.common.components.service.App;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ModelTestCase;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test the {@link Task}.
 *
 * @see Task
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetIntegrationExtension.class)
public class TaskTest extends ModelTestCase<Task> {

  /**
   * {@inheritDoc}
   */
  @Override
  public Task createModelExample(final int index) {

    assert index >= 0;
    final var model = new Task();
    model.taskTypeId = "taskTypeId" + index;
    model.requesterId = "requesterId" + index;
    model.appId = "appId" + index;
    model.communityId = "communityId" + index;
    model.goal = new HumanDescriptionTest().createModelExample(index);
    model.norms = new ArrayList<>();
    model.norms.add(new ProtocolNormTest().createModelExample(index));
    model.attributes = new JsonObject().put("a_int", index);
    model.transactions = new ArrayList<>();
    model.transactions.add(new TaskTransactionTest().createModelExample(index));
    model._creationTs = 1234567891 + index;
    model._lastUpdateTs = 1234567991 + index * 2;
    return model;
  }

  /**
   * Check that the {@link #createModelExample(int, Vertx, VertxTestContext)} is
   * valid.
   *
   * @param index       to verify
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(WeNetValidateContext)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(index, vertx, testContext)
        .onSuccess(model -> assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext));

  }

  /**
   * Check that an empty task is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(WeNetValidateContext)
   */
  @Test
  public void shouldEmptyTaskNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    assertIsNotValid(new Task(), new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that a task without an identifier is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(WeNetValidateContext)
   */
  @Test
  public void shouldTaskWithoutIdBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.id = null;
      assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext);
    });

  }

  /**
   * Check that a task with an undefined identifier is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(WeNetValidateContext)
   */
  @Test
  public void shouldTaskWitIdBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> testContext.verify(() -> {
      model.id = UUID.randomUUID().toString();
      assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext);
    }));

  }

  /**
   * Create an example model that has the specified index.
   *
   * @param index       to use in the example.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @return the created task.
   */
  public Future<Task> createModelExample(final int index, final Vertx vertx, final VertxTestContext testContext) {

    return testContext.assertComplete(StoreServices.storeTaskTypeExample(index, vertx, testContext)
        .compose(taskType -> StoreServices.storeCommunityExample(index, vertx, testContext).compose(community -> {

          final var model = this.createModelExample(index);
          model.transactions = null;
          model.requesterId = community.members.get(0).userId;
          model.taskTypeId = taskType.id;
          model.appId = community.appId;
          model.communityId = community.id;
          return Future.succeededFuture(model);

        })));

  }

  /**
   * Check that the model with id is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithAnExistingId(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeTaskExample(1, vertx, testContext).onSuccess(created -> {
      this.createModelExample(2, vertx, testContext).onSuccess(model -> {

        model.id = created.id;
        assertIsNotValid(model, "id", new WeNetValidateContext("codePrefix", vertx), testContext);

      });
    });

  }

  /**
   * Check that can not be valid without task type identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithoutTaskTypeId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.taskTypeId = null;
      assertIsNotValid(model, "taskTypeId", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that can not be valid with an undefined task type identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithAnUndefinedTaskTypeId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.taskTypeId = "Undefined-task-type-ID";
      assertIsNotValid(model, "taskTypeId", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that can not be valid without requester identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithoutRequesterId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.requesterId = null;
      assertIsNotValid(model, "requesterId", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that can not be valid with an undefined requester identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithAnUndefinedRequesterId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.requesterId = "Undefined-requester-id";
      assertIsNotValid(model, "requesterId", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that can not be valid without an application identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithoutAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.appId = null;
      assertIsNotValid(model, "appId", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that can not be valid with an undefined app identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithAnUndefinedAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.appId = "Undefined-app-id";
      assertIsNotValid(model, "appId", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that can not be valid without a community identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithoutCommunityId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.communityId = null;
      assertIsNotValid(model, "communityId", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that can not be valid with an undefined community identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithAnUndefinedCommunityId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.communityId = "Undefined-community-id";
      assertIsNotValid(model, "communityId", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that not accept profiles with bad norms.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithABadNorms(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.norms = new ArrayList<>();
      model.norms.add(new ProtocolNormTest().createModelExample(1));
      model.norms.add(new ProtocolNormTest().createModelExample(2));
      model.norms.add(new ProtocolNormTest().createModelExample(3));
      model.norms.get(1).whenever = model.norms.get(1).thenceforth;
      assertIsNotValid(model, "norms[1].thenceforth", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check merge stored profiles.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#merge(Task, WeNetValidateContext)
   */
  @Test
  public void shouldMergeStoredModels(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        this.createModelExample(2, vertx, testContext).onSuccess(sourceToStore -> {

          StoreServices.storeTask(sourceToStore, vertx, testContext).onSuccess(source -> {

            assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

              source.id = target.id;
              source._creationTs = target._creationTs;
              source._lastUpdateTs = target._lastUpdateTs;
              assertThat(merged).isNotEqualTo(target).isEqualTo(source);

            });
          });
        });
      });
    });

  }

  /**
   * Check that merge when only is modified the {@link Task#taskTypeId}.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#merge(Task, WeNetValidateContext)
   */
  @Test
  public void shouldOnlyMergeTaskTypeId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        StoreServices.storeTaskTypeExample(1, vertx, testContext).onSuccess(taskType -> {

          final var source = new Task();
          source.taskTypeId = taskType.id;
          assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

            assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
            target.taskTypeId = taskType.id;
            assertThat(merged).isEqualTo(target).isNotEqualTo(source);
          });

        });
      });

    });

  }

  /**
   * Check that cannot merge if the new task type ot match the attirbutes of the
   * task.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#merge(Task, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeIfNewTaskTypeIdNotMatchAttributes(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        StoreServices.storeTaskType(new TaskType(), vertx, testContext).onSuccess(taskType -> {

          final var source = new Task();
          source.taskTypeId = taskType.id;
          assertCannotMerge(target, source, "attributes", new WeNetValidateContext("codePrefix", vertx), testContext);

        });
      });

    });

  }

  /**
   * Check that can not merge when the {@link Task#taskTypeId} has a bas value.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#merge(Task, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithBadTaskTypeId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = new Task();
        source.taskTypeId = "undefined-task-type-identifier";
        assertCannotMerge(target, source, "taskTypeId", new WeNetValidateContext("codePrefix", vertx), testContext);
      });

    });

  }

  /**
   * Check that merge when only is modified the {@link Task#requesterId}.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#merge(Task, WeNetValidateContext)
   */
  @Test
  public void shouldOnlyMergeRequesterId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(requester -> {

          final var source = new Task();
          source.requesterId = requester.id;
          assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

            assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
            target.requesterId = requester.id;
            assertThat(merged).isEqualTo(target).isNotEqualTo(source);
          });

        });
      });
    });

  }

  /**
   * Check that can not merge when the {@link Task#requesterId} has a bas value.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#merge(Task, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithBadRequesterId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = new Task();
        source.requesterId = "undefined-requester-identifier";
        assertCannotMerge(target, source, "requesterId", new WeNetValidateContext("codePrefix", vertx), testContext);
      });

    });

  }

  /**
   * Check that merge when only is modified the {@link Task#appId}.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#merge(Task, WeNetValidateContext)
   */
  @Test
  public void shouldOnlyMergeAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        StoreServices.storeApp(new App(), vertx, testContext).onSuccess(app -> {

          final var source = new Task();
          source.appId = app.appId;
          assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

            assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
            target.appId = app.appId;
            assertThat(merged).isEqualTo(target).isNotEqualTo(source);
          });

        });
      });
    });

  }

  /**
   * Check that can not merge when the {@link Task#appId} has a bas value.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#merge(Task, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithBadAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = new Task();
        source.appId = "undefined-application-identifier";
        assertCannotMerge(target, source, "appId", new WeNetValidateContext("codePrefix", vertx), testContext);
      });

    });

  }

  /**
   * Check that merge when only is modified the {@link Task#goal}.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#merge(Task, WeNetValidateContext)
   */
  @Test
  public void shouldOnlyMergeGoal(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = new Task();
        source.goal = new HumanDescriptionTest().createModelExample(2);
        assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

          assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
          target.goal = new HumanDescriptionTest().createModelExample(2);
          assertThat(merged).isEqualTo(target).isNotEqualTo(source);
        });

      });

    });

  }

  /**
   * Check that merge when only is modified the {@link Task#goal}.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#merge(Task, WeNetValidateContext)
   */
  @Test
  public void shouldOnlyMergeGoalWithNullFields(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = new Task();
        source.goal = new HumanDescription();
        assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

          assertThat(merged).isEqualTo(target).isNotEqualTo(source);
        });

      });

    });

  }

  /**
   * Check that can not merge when the {@link Task#taskTypeId} has a bas value.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#merge(Task, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithBadTaskType(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = new Task();
        source.taskTypeId = "undefined";
        assertCannotMerge(target, source, "taskTypeId", new WeNetValidateContext("codePrefix", vertx), testContext);

      });

    });

  }

  /**
   * Check that merge when only is modified the {@link Task#norms}.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#merge(Task, WeNetValidateContext)
   */
  @Test
  public void shouldOnlyMergeNewNorms(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = new Task();
        source.norms = new ArrayList<>();
        source.norms.add(new ProtocolNormTest().createModelExample(2));
        assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

          assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
          target.norms.clear();
          target.norms.add(new ProtocolNormTest().createModelExample(2));
          assertThat(merged).isEqualTo(target).isNotEqualTo(source);
        });

      });

    });

  }

  /**
   * Check that merge when only is modified the {@link Task#norms}.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#merge(Task, WeNetValidateContext)
   */
  @Test
  public void shouldOnlyMergeAddingNewNorm(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = new Task();
        source.norms = new ArrayList<>();
        source.norms.add(new ProtocolNormTest().createModelExample(2));
        source.norms.addAll(target.norms);
        assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

          assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
          target.norms.add(0, new ProtocolNormTest().createModelExample(2));
          assertThat(merged).isEqualTo(target).isNotEqualTo(source);
        });

      });

    });

  }

  /**
   * Check that can not merge when the {@link Task#norms} has a bas value.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#merge(Task, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithBadNorms(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = new Task();
        source.norms = new ArrayList<>();
        source.norms.add(new ProtocolNorm());
        assertCannotMerge(target, source, "norms[0].whenever", new WeNetValidateContext("codePrefix", vertx),
            testContext);
      });

    });

  }

  /**
   * Check that merge when only is modified the {@link Task#attributes}.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#merge(Task, WeNetValidateContext)
   */
  @Test
  public void shouldOnlyMergeNewAttributes(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeTaskExample(1, vertx, testContext).onSuccess(target -> {

      final var source = new Task();
      source.attributes = new JsonObject().put("a_int", 2);
      assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

        assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
        target.attributes = new JsonObject().put("a_int", 2);
        assertThat(merged).isEqualTo(target).isNotEqualTo(source);
      });

    });

  }

  /**
   * Check that can not be valid without a goal.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithoutGoal(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.goal = null;
      assertIsNotValid(model, "goal", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that can not be valid with a too soon close time stamp.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithATooSoonCloseTimeStampn(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.closeTs = model._creationTs - 1;
      assertIsNotValid(model, "closeTs", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check update stored profiles.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#update(Task, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateStoredModels(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        this.createModelExample(2, vertx, testContext).onSuccess(sourceToStore -> {

          StoreServices.storeTask(sourceToStore, vertx, testContext).onSuccess(source -> {

            assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {

              source.id = target.id;
              source._creationTs = target._creationTs;
              source._lastUpdateTs = target._lastUpdateTs;
              assertThat(updated).isNotEqualTo(target).isEqualTo(source);

            });
          });
        });
      });
    });

  }

  /**
   * Check that update when only is modified the {@link Task#taskTypeId}.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#update(Task, WeNetValidateContext)
   */
  @Test
  public void shouldOnlyUpdateTaskTypeId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        StoreServices.storeTaskTypeExample(1, vertx, testContext).onSuccess(taskType -> {

          final var source = Model.fromJsonObject(target.toJsonObject(), Task.class);
          source.taskTypeId = taskType.id;
          assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {

            assertThat(updated).isNotEqualTo(target).isEqualTo(source);
            target.taskTypeId = taskType.id;
            assertThat(updated).isEqualTo(target);
          });

        });
      });

    });

  }

  /**
   * Check that not update whit a task type that not match the task attributes.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#update(Task, WeNetValidateContext)
   */
  @Test
  public void shouldNotUpdateTaskTypeIdBecauseAttributesNotMatch(final Vertx vertx,
      final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        StoreServices.storeTaskType(new TaskType(), vertx, testContext).onSuccess(taskType -> {

          final var source = Model.fromJsonObject(target.toJsonObject(), Task.class);
          source.taskTypeId = taskType.id;
          assertCannotUpdate(target, source, "attributes", new WeNetValidateContext("codePrefix", vertx), testContext);

        });
      });

    });

  }

  /**
   * Check that can not update when the {@link Task#taskTypeId} has a bas value.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#update(Task, WeNetValidateContext)
   */
  @Test
  public void shouldNotUpdateWithBadTaskTypeId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = Model.fromJsonObject(target.toJsonObject(), Task.class);
        source.taskTypeId = "undefined-task-type-identifier";
        assertCannotUpdate(target, source, "taskTypeId", new WeNetValidateContext("codePrefix", vertx), testContext);
      });

    });

  }

  /**
   * Check that update when only is modified the {@link Task#requesterId}.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#update(Task, WeNetValidateContext)
   */
  @Test
  public void shouldOnlyUpdateRequesterId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(requester -> {

          final var source = Model.fromJsonObject(target.toJsonObject(), Task.class);
          source.requesterId = requester.id;
          assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {

            assertThat(updated).isNotEqualTo(target).isEqualTo(source);
            target.requesterId = requester.id;
            assertThat(updated).isEqualTo(target);
          });

        });
      });

    });

  }

  /**
   * Check that can not update when the {@link Task#requesterId} has a bas value.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#update(Task, WeNetValidateContext)
   */
  @Test
  public void shouldNotUpdateWithBadRequesterId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = Model.fromJsonObject(target.toJsonObject(), Task.class);
        source.requesterId = "undefined-requester-identifier";
        assertCannotUpdate(target, source, "requesterId", new WeNetValidateContext("codePrefix", vertx), testContext);
      });

    });

  }

  /**
   * Check that update when only is modified the {@link Task#appId}.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#update(Task, WeNetValidateContext)
   */
  @Test
  public void shouldOnlyUpdateAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        StoreServices.storeApp(new App(), vertx, testContext).onSuccess(app -> {

          final var source = Model.fromJsonObject(target.toJsonObject(), Task.class);
          source.appId = app.appId;
          assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {

            assertThat(updated).isNotEqualTo(target).isEqualTo(source);
            target.appId = app.appId;
            assertThat(updated).isEqualTo(target);
          });

        });
      });

    });

  }

  /**
   * Check that can not update when the {@link Task#appId} has a bas value.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#update(Task, WeNetValidateContext)
   */
  @Test
  public void shouldNotUpdateWithBadAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = Model.fromJsonObject(target.toJsonObject(), Task.class);
        source.appId = "undefined-application-identifier";
        assertCannotUpdate(target, source, "appId", new WeNetValidateContext("codePrefix", vertx), testContext);
      });

    });

  }

  /**
   * Check that update when only is modified the {@link Task#goal}.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#update(Task, WeNetValidateContext)
   */
  @Test
  public void shouldOnlyUpdateGoal(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = Model.fromJsonObject(target.toJsonObject(), Task.class);
        source.goal = new HumanDescriptionTest().createModelExample(2);
        assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {

          assertThat(updated).isNotEqualTo(target).isEqualTo(source);
          target.goal = new HumanDescriptionTest().createModelExample(2);
          assertThat(updated).isEqualTo(target);
        });

      });

    });

  }

  /**
   * Check that can not update when the {@link Task#goal} has a bas value.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#update(Task, WeNetValidateContext)
   */
  @Test
  public void shouldNotUpdateWithBadGoal(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = Model.fromJsonObject(target.toJsonObject(), Task.class);
        source.goal = new HumanDescription();
        source.goal.name = null;
        assertCannotUpdate(target, source, "goal.name", new WeNetValidateContext("codePrefix", vertx), testContext);
      });

    });

  }

  /**
   * Check that update when only is modified the {@link Task#norms}.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#update(Task, WeNetValidateContext)
   */
  @Test
  public void shouldOnlyUpdateNewNorms(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = Model.fromJsonObject(target.toJsonObject(), Task.class);
        source.norms = new ArrayList<>();
        source.norms.add(new ProtocolNormTest().createModelExample(2));
        assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {

          assertThat(updated).isNotEqualTo(target).isEqualTo(source);
        });

      });

    });

  }

  /**
   * Check that update when only is modified the {@link Task#norms}.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#update(Task, WeNetValidateContext)
   */
  @Test
  public void shouldOnlyUpdateAddingNewNorm(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = Model.fromJsonObject(target.toJsonObject(), Task.class);
        source.norms = new ArrayList<>();
        source.norms.add(new ProtocolNormTest().createModelExample(2));
        source.norms.addAll(target.norms);
        assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {

          assertThat(updated).isNotEqualTo(target).isEqualTo(source);
        });

      });

    });

  }

  /**
   * Check that can not update when the {@link Task#norms} has a bas value.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#update(Task, WeNetValidateContext)
   */
  @Test
  public void shouldNotUpdateWithBadNorms(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = Model.fromJsonObject(target.toJsonObject(), Task.class);
        source.norms = new ArrayList<>();
        source.norms.add(new ProtocolNormTest().createModelExample(2));
        source.norms.get(0).whenever = source.norms.get(0).thenceforth;
        assertCannotUpdate(target, source, "norms[0].thenceforth", new WeNetValidateContext("codePrefix", vertx),
            testContext);
      });

    });

  }

  /**
   * Check that update when only is modified the {@link Task#attributes}.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#update(Task, WeNetValidateContext)
   */
  @Test
  public void shouldOnlyUpdateNewAttributes(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeTaskExample(1, vertx, testContext).onSuccess(target -> {

      final var source = Model.fromJsonObject(target.toJsonObject(), Task.class);
      source.attributes = new JsonObject().put("a_int", 2);
      assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {

        assertThat(updated).isNotEqualTo(target).isEqualTo(source);
        target.attributes = new JsonObject().put("a_int", 2);
        assertThat(updated).isEqualTo(target);
      });

    });

  }

  /**
   * Should merge with {@code null}
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#update(Task, WeNetValidateContext)
   */
  @Test
  public void shouldMergeWithNull(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      assertCanMerge(target, null, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {
        assertThat(merged).isSameAs(target);
      });
    });

  }

  /**
   * Should update with {@code null}
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Task#update(Task, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateWithNull(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      assertCanUpdate(target, null, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {
        assertThat(updated).isSameAs(target);
      });
    });

  }

  /**
   * Check that a task without attributes is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(WeNetValidateContext)
   */
  @Test
  public void shouldBeValidWithoutAttributes(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeTaskType(new TaskType(), vertx, testContext).onSuccess(taskType -> {
      StoreServices.storeCommunityExample(1, vertx, testContext).onSuccess(community -> {

        final var model = this.createModelExample(1);
        model.transactions = null;
        model.attributes = new JsonObject();
        model.requesterId = community.members.get(0).userId;
        model.taskTypeId = taskType.id;
        model.appId = community.appId;
        model.communityId = community.id;
        assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext);

      });

    });

  }

  /**
   * Check that a task with {@null} attributes is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(WeNetValidateContext)
   */
  @Test
  public void shouldBeValidWithNullAttributes(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeTaskType(new TaskType(), vertx, testContext).onSuccess(taskType -> {
      StoreServices.storeCommunityExample(1, vertx, testContext).onSuccess(community -> {

        final var model = this.createModelExample(1);
        model.transactions = null;
        model.attributes = null;
        model.requesterId = community.members.get(0).userId;
        model.taskTypeId = taskType.id;
        model.appId = community.appId;
        model.communityId = community.id;
        assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext);

      });

    });

  }

}
