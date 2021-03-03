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

import static eu.internetofus.common.components.AbstractComponentMocker.createClientWithDefaultSession;
import static eu.internetofus.common.components.MergeAsserts.assertCanMerge;
import static eu.internetofus.common.components.MergeAsserts.assertCannotMerge;
import static eu.internetofus.common.components.UpdatesTest.assertCanUpdate;
import static eu.internetofus.common.components.UpdatesTest.assertCannotUpdate;
import static eu.internetofus.common.components.ValidationsTest.assertIsNotValid;
import static eu.internetofus.common.components.ValidationsTest.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.HumanDescription;
import eu.internetofus.common.components.HumanDescriptionTest;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ModelTestCase;
import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.ValidationsTest;
import eu.internetofus.common.components.profile_manager.CommunityProfile;
import eu.internetofus.common.components.profile_manager.Norm;
import eu.internetofus.common.components.profile_manager.NormTest;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.components.profile_manager.WeNetProfileManagerMocker;
import eu.internetofus.common.components.profile_manager.WeNetUserProfile;
import eu.internetofus.common.components.service.App;
import eu.internetofus.common.components.service.WeNetService;
import eu.internetofus.common.components.service.WeNetServiceSimulator;
import eu.internetofus.common.components.service.WeNetServiceSimulatorMocker;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
@ExtendWith(VertxExtension.class)
public class TaskTest extends ModelTestCase<Task> {

  /**
   * The profile manager mocked server.
   */
  protected static WeNetProfileManagerMocker profileManagerMocker;

  /**
   * The task manager mocked server.
   */
  protected static WeNetTaskManagerMocker taskManagerMocker;

  /**
   * The service mocked server.
   */
  protected static WeNetServiceSimulatorMocker serviceMocker;

  /**
   * Start the mocker server.
   */
  @BeforeAll
  public static void startMockers() {

    profileManagerMocker = WeNetProfileManagerMocker.start();
    taskManagerMocker = WeNetTaskManagerMocker.start();
    serviceMocker = WeNetServiceSimulatorMocker.start();
  }

  /**
   * Stop the mocker server.
   */
  @AfterAll
  public static void stopMockers() {

    profileManagerMocker.stopServer();
    taskManagerMocker.stopServer();
    serviceMocker.stopServer();
  }

  /**
   * Register the necessary services before to test.
   *
   * @param vertx event bus to register the necessary services.
   */
  @BeforeEach
  public void registerServices(final Vertx vertx) {

    final var client = createClientWithDefaultSession(vertx);
    final var profileConf = profileManagerMocker.getComponentConfiguration();
    WeNetProfileManager.register(vertx, client, profileConf);

    final var taskConf = taskManagerMocker.getComponentConfiguration();
    WeNetTaskManager.register(vertx, client, taskConf);

    final var conf = serviceMocker.getComponentConfiguration();
    WeNetService.register(vertx, client, conf);
    WeNetServiceSimulator.register(vertx, client, conf);

  }

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
    model.norms.add(new NormTest().createModelExample(index));
    model.norms.get(0).id = "norm_id_" + index;
    model.attributes = new JsonObject().put("index", index);
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
   * @see Task#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(index, vertx, testContext).onSuccess(model -> assertIsValid(model, vertx, testContext));

  }

  /**
   * Check that an empty task is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldEmptyTaskNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    assertIsNotValid(new Task(), vertx, testContext);

  }

  /**
   * Check that a task without an identifier is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldTaskWithoutIdBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.id = null;
      assertIsValid(model, vertx, testContext);
    });

  }

  /**
   * Check that a task with an undefined identifier is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldTaskWitIdBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> testContext.verify(() -> {
      model.id = UUID.randomUUID().toString();
      assertIsValid(model, vertx, testContext);
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
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithAnExistingId(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeTaskExample(1, vertx, testContext).onSuccess(created -> {
      this.createModelExample(2, vertx, testContext).onSuccess(model -> {

        model.id = created.id;
        assertIsNotValid(model, "id", vertx, testContext);

      });
    });

  }

  /**
   * Check that can not be valid with a too large task type identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithALargeTaskTypeId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.taskTypeId = ValidationsTest.STRING_256;
      assertIsNotValid(model, "taskTypeId", vertx, testContext);

    });

  }

  /**
   * Check that can not be valid with a large identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithALargeId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.id = ValidationsTest.STRING_256;
      assertIsNotValid(model, "id", vertx, testContext);

    });

  }

  /**
   * Check that can not be valid without task type identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithoutTaskTypeId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.taskTypeId = null;
      assertIsNotValid(model, "taskTypeId", vertx, testContext);

    });

  }

  /**
   * Check that can not be valid with an undefined task type identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithAnUndefinedTaskTypeId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.taskTypeId = "Undefined-task-type-ID";
      assertIsNotValid(model, "taskTypeId", vertx, testContext);

    });

  }

  /**
   * Check that can not be valid with a too large requester identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithALargeRequesterId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.requesterId = ValidationsTest.STRING_256;
      assertIsNotValid(model, "requesterId", vertx, testContext);

    });

  }

  /**
   * Check that can not be valid without requester identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithoutRequesterId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.requesterId = null;
      assertIsNotValid(model, "requesterId", vertx, testContext);

    });

  }

  /**
   * Check that can not be valid with an undefined requester identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithAnUndefinedRequesterId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.requesterId = "Undefined-requester-id";
      assertIsNotValid(model, "requesterId", vertx, testContext);

    });

  }

  /**
   * Check that can not be valid with a too large app identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithALargeAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.appId = ValidationsTest.STRING_256;
      assertIsNotValid(model, "appId", vertx, testContext);

    });

  }

  /**
   * Check that can not be valid without an application identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithoutAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.appId = null;
      assertIsNotValid(model, "appId", vertx, testContext);

    });

  }

  /**
   * Check that can not be valid with an undefined app identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithAnUndefinedAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.appId = "Undefined-app-id";
      assertIsNotValid(model, "appId", vertx, testContext);

    });

  }

  /**
   * Check that can not be valid with a too large community identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithALargeCommunityId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.communityId = ValidationsTest.STRING_256;
      assertIsNotValid(model, "communityId", vertx, testContext);

    });

  }

  /**
   * Check that can not be valid without a community identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithoutCommunityId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.communityId = null;
      assertIsNotValid(model, "communityId", vertx, testContext);

    });

  }

  /**
   * Check that can not be valid with an undefined community identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithAnUndefinedCommunityId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.communityId = "Undefined-community-id";
      assertIsNotValid(model, "communityId", vertx, testContext);

    });

  }

  /**
   * Check that not accept profiles with bad norms.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadNorms(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.norms = new ArrayList<>();
      model.norms.add(new NormTest().createModelExample(1));
      model.norms.add(new NormTest().createModelExample(2));
      model.norms.add(new NormTest().createModelExample(3));
      model.norms.get(1).attribute = ValidationsTest.STRING_256;
      assertIsNotValid(model, "norms[1].attribute", vertx, testContext);

    });

  }

  /**
   * Check merge stored profiles.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldMergeStoredModels(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        this.createModelExample(2, vertx, testContext).onSuccess(sourceToStore -> {

          StoreServices.storeTask(sourceToStore, vertx, testContext).onSuccess(source -> {

            assertCanMerge(target, source, vertx, testContext, merged -> {

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
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldOnlyMergeTaskTypeId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        StoreServices.storeTaskType(new TaskType(), vertx, testContext).onSuccess(taskType -> {

          final var source = new Task();
          source.taskTypeId = taskType.id;
          assertCanMerge(target, source, vertx, testContext, merged -> {

            assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
            target.taskTypeId = taskType.id;
            assertThat(merged).isEqualTo(target).isNotEqualTo(source);
          });

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
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithBadTaskTypeId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = new Task();
        source.taskTypeId = "undefined-task-type-identifier";
        assertCannotMerge(target, source, "taskTypeId", vertx, testContext);
      });

    });

  }

  /**
   * Check that merge when only is modified the {@link Task#requesterId}.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldOnlyMergeRequesterId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(requester -> {

          final var source = new Task();
          source.requesterId = requester.id;
          assertCanMerge(target, source, vertx, testContext, merged -> {

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
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithBadRequesterId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = new Task();
        source.requesterId = "undefined-requester-identifier";
        assertCannotMerge(target, source, "requesterId", vertx, testContext);
      });

    });

  }

  /**
   * Check that merge when only is modified the {@link Task#appId}.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldOnlyMergeAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        StoreServices.storeApp(new App(), vertx, testContext).onSuccess(app -> {

          final var source = new Task();
          source.appId = app.appId;
          assertCanMerge(target, source, vertx, testContext, merged -> {

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
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithBadAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = new Task();
        source.appId = "undefined-application-identifier";
        assertCannotMerge(target, source, "appId", vertx, testContext);
      });

    });

  }

  /**
   * Check that merge when only is modified the {@link Task#goal}.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldOnlyMergeGoal(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = new Task();
        source.goal = new HumanDescriptionTest().createModelExample(2);
        assertCanMerge(target, source, vertx, testContext, merged -> {

          assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
          target.goal = new HumanDescriptionTest().createModelExample(2);
          assertThat(merged).isEqualTo(target).isNotEqualTo(source);
        });

      });

    });

  }

  /**
   * Check that can not merge when the {@link Task#goal} has a bas value.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithBadGoal(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = new Task();
        source.goal = new HumanDescription();
        source.goal.name = ValidationsTest.STRING_256;
        assertCannotMerge(target, source, "goal.name", vertx, testContext);
      });

    });

  }

  /**
   * Check that merge when only is modified the {@link Task#norms}.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldOnlyMergeNewNorms(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = new Task();
        source.norms = new ArrayList<>();
        source.norms.add(new NormTest().createModelExample(2));
        assertCanMerge(target, source, vertx, testContext, merged -> {

          assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
          target.norms.clear();
          target.norms.add(new NormTest().createModelExample(2));
          target.norms.get(0).id = merged.norms.get(0).id;
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
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldOnlyMergeAddingNewNorm(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = new Task();
        source.norms = new ArrayList<>();
        source.norms.add(new NormTest().createModelExample(2));
        source.norms.addAll(target.norms);
        assertCanMerge(target, source, vertx, testContext, merged -> {

          assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
          target.norms.add(0, new NormTest().createModelExample(2));
          target.norms.get(0).id = merged.norms.get(0).id;
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
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldOnlyMergeDeleteNorm(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      targetToStore.norms.get(0).id = UUID.randomUUID().toString();
      final var normToMantain = new NormTest().createModelExample(2);
      normToMantain.id = UUID.randomUUID().toString();
      targetToStore.norms.add(normToMantain);

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = new Task();
        source.norms = new ArrayList<>();
        source.norms.add(new Norm());
        source.norms.get(0).id = normToMantain.id;
        source.norms.get(0).attribute = "New attribute";
        assertCanMerge(target, source, vertx, testContext, merged -> {

          assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
          target.norms.clear();
          normToMantain.attribute = "New attribute";
          target.norms.add(normToMantain);
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
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithBadNorms(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = new Task();
        source.norms = new ArrayList<>();
        source.norms.add(new Norm());
        source.norms.get(0).attribute = ValidationsTest.STRING_256;
        assertCannotMerge(target, source, "norms[0].attribute", vertx, testContext);
      });

    });

  }

  /**
   * Check that merge when only is modified the {@link Task#attributes}.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldOnlyMergeNewAttributes(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeTaskExample(1, vertx, testContext).onSuccess(target -> {

      final var source = new Task();
      source.attributes = new JsonObject().put("index", 2);
      assertCanMerge(target, source, vertx, testContext, merged -> {

        assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
        target.attributes = new JsonObject().put("index", 2);
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
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithoutGoal(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.goal = null;
      assertIsNotValid(model, "goal", vertx, testContext);

    });

  }

  /**
   * Check that can not be valid with a too soon close time stamp.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithATooSoonCloseTimeStampn(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.closeTs = model._creationTs - 1;
      assertIsNotValid(model, "closeTs", vertx, testContext);

    });

  }

  /**
   * Check update stored profiles.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#update(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldUpdateStoredModels(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        this.createModelExample(2, vertx, testContext).onSuccess(sourceToStore -> {

          StoreServices.storeTask(sourceToStore, vertx, testContext).onSuccess(source -> {

            assertCanUpdate(target, source, vertx, testContext, updated -> {

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
   * @see WeNetUserProfile#update(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldOnlyUpdateTaskTypeId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        StoreServices.storeTaskType(new TaskType(), vertx, testContext).onSuccess(taskType -> {

          final var source = Model.fromJsonObject(target.toJsonObject(), Task.class);
          source.taskTypeId = taskType.id;
          assertCanUpdate(target, source, vertx, testContext, updated -> {

            assertThat(updated).isNotEqualTo(target).isEqualTo(source);
            target.taskTypeId = taskType.id;
            assertThat(updated).isEqualTo(target);
          });

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
   * @see WeNetUserProfile#update(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotUpdateWithBadTaskTypeId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = Model.fromJsonObject(target.toJsonObject(), Task.class);
        source.taskTypeId = "undefined-task-type-identifier";
        assertCannotUpdate(target, source, "taskTypeId", vertx, testContext);
      });

    });

  }

  /**
   * Check that update when only is modified the {@link Task#requesterId}.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#update(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldOnlyUpdateRequesterId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(requester -> {

          final var source = Model.fromJsonObject(target.toJsonObject(), Task.class);
          source.requesterId = requester.id;
          assertCanUpdate(target, source, vertx, testContext, updated -> {

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
   * @see WeNetUserProfile#update(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotUpdateWithBadRequesterId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = Model.fromJsonObject(target.toJsonObject(), Task.class);
        source.requesterId = "undefined-requester-identifier";
        assertCannotUpdate(target, source, "requesterId", vertx, testContext);
      });

    });

  }

  /**
   * Check that update when only is modified the {@link Task#appId}.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#update(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldOnlyUpdateAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        StoreServices.storeApp(new App(), vertx, testContext).onSuccess(app -> {

          final var source = Model.fromJsonObject(target.toJsonObject(), Task.class);
          source.appId = app.appId;
          assertCanUpdate(target, source, vertx, testContext, updated -> {

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
   * @see WeNetUserProfile#update(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotUpdateWithBadAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = Model.fromJsonObject(target.toJsonObject(), Task.class);
        source.appId = "undefined-application-identifier";
        assertCannotUpdate(target, source, "appId", vertx, testContext);
      });

    });

  }

  /**
   * Check that update when only is modified the {@link Task#goal}.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#update(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldOnlyUpdateGoal(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = Model.fromJsonObject(target.toJsonObject(), Task.class);
        source.goal = new HumanDescriptionTest().createModelExample(2);
        assertCanUpdate(target, source, vertx, testContext, updated -> {

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
   * @see WeNetUserProfile#update(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotUpdateWithBadGoal(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = Model.fromJsonObject(target.toJsonObject(), Task.class);
        source.goal = new HumanDescription();
        source.goal.name = ValidationsTest.STRING_256;
        assertCannotUpdate(target, source, "goal.name", vertx, testContext);
      });

    });

  }

  /**
   * Check that update when only is modified the {@link Task#norms}.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#update(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldOnlyUpdateNewNorms(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = Model.fromJsonObject(target.toJsonObject(), Task.class);
        source.norms = new ArrayList<>();
        source.norms.add(new NormTest().createModelExample(2));
        assertCanUpdate(target, source, vertx, testContext, updated -> {

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
   * @see WeNetUserProfile#update(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldOnlyUpdateAddingNewNorm(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = Model.fromJsonObject(target.toJsonObject(), Task.class);
        source.norms = new ArrayList<>();
        source.norms.add(new NormTest().createModelExample(2));
        source.norms.addAll(target.norms);
        assertCanUpdate(target, source, vertx, testContext, updated -> {

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
   * @see WeNetUserProfile#update(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotUpdateWithBadNorms(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(targetToStore -> {

      StoreServices.storeTask(targetToStore, vertx, testContext).onSuccess(target -> {

        final var source = Model.fromJsonObject(target.toJsonObject(), Task.class);
        source.norms = new ArrayList<>();
        source.norms.add(new Norm());
        source.norms.get(0).attribute = ValidationsTest.STRING_256;
        assertCannotUpdate(target, source, "norms[0].attribute", vertx, testContext);
      });

    });

  }

  /**
   * Check that update when only is modified the {@link Task#attributes}.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#update(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldOnlyUpdateNewAttributes(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeTaskExample(1, vertx, testContext).onSuccess(target -> {

      final var source = Model.fromJsonObject(target.toJsonObject(), Task.class);
      source.attributes = new JsonObject().put("index", 2);
      assertCanUpdate(target, source, vertx, testContext, updated -> {

        assertThat(updated).isNotEqualTo(target).isEqualTo(source);
        target.attributes = new JsonObject().put("index", 2);
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
   * @see CommunityProfile#merge(CommunityProfile, String, Vertx)
   */
  @Test
  public void shouldMergeWithNull(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      assertCanMerge(target, null, vertx, testContext, merged -> {
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
   * @see CommunityProfile#update(CommunityProfile, String, Vertx)
   */
  @Test
  public void shouldUpdateWithNull(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      assertCanUpdate(target, null, vertx, testContext, updated -> {
        assertThat(updated).isSameAs(target);
      });
    });

  }

}
