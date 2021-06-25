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

package eu.internetofus.common.components.models;

import static eu.internetofus.common.components.AbstractComponentMocker.createClientWithDefaultSession;
import static eu.internetofus.common.components.MergeAsserts.assertCanMerge;
import static eu.internetofus.common.components.MergeAsserts.assertCannotMerge;
import static eu.internetofus.common.components.UpdatesTest.assertCanUpdate;
import static eu.internetofus.common.components.UpdatesTest.assertCannotUpdate;
import static eu.internetofus.common.components.ValidationsTest.assertIsNotValid;
import static eu.internetofus.common.components.ValidationsTest.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;

import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ModelTestCase;
import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import eu.internetofus.common.components.task_manager.WeNetTaskManagerMocker;
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
 * Test the {@link TaskType}.
 *
 * @see TaskType
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class TaskTypeTest extends ModelTestCase<TaskType> {

  /**
   * The task manager mocked server.
   */
  protected static WeNetTaskManagerMocker taskManagerMocker;

  /**
   * Start the mocker server.
   */
  @BeforeAll
  public static void startMockers() {

    taskManagerMocker = WeNetTaskManagerMocker.start();
  }

  /**
   * Stop the mocker server.
   */
  @AfterAll
  public static void stopMockers() {

    taskManagerMocker.stopServer();
  }

  /**
   * Register the necessary services before to test.
   *
   * @param vertx event bus to register the necessary services.
   */
  @BeforeEach
  public void registerServices(final Vertx vertx) {

    final var client = createClientWithDefaultSession(vertx);
    final var taskConf = taskManagerMocker.getComponentConfiguration();
    WeNetTaskManager.register(vertx, client, taskConf);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TaskType createModelExample(final int index) {

    final var model = new TaskType();
    model.name = "name_" + index;
    model.description = "description_" + index;
    model.keywords = new ArrayList<>();
    model.keywords.add("keyword_" + index);
    model.keywords.add("keyword_" + (index + 1));
    model.keywords.add("keyword_" + (index + 2));
    model.attributes = new JsonObject().put("key", index);
    model.transactions = new JsonObject().put("transaction_" + index, new JsonObject());
    model.callbacks = new JsonObject().put("callbacks_" + index, new JsonObject());
    model.norms = new ArrayList<>();
    model.norms.add(new ProtocolNormTest().createModelExample(index));
    model.norms.add(new ProtocolNormTest().createModelExample(index + 1));
    model.norms.add(new ProtocolNormTest().createModelExample(index + 2));
    return model;

  }

  /**
   * Check that the {@link #createModelExample(int)} is valid.
   *
   * @param index       to verify
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskType#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(index);
    assertIsValid(model, vertx, testContext);

  }

  /**
   * Check that an empty type is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskType#validate(String, Vertx)
   */
  @Test
  public void shouldEmptyBeValid(final Vertx vertx, final VertxTestContext testContext) {

    assertIsValid(new TaskType(), vertx, testContext);

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

    StoreServices.storeTaskTypeExample(1, vertx, testContext).onSuccess(created -> {

      final var model = this.createModelExample(1);
      model.id = created.id;
      assertIsNotValid(model, "id", vertx, testContext);

    });

  }

  /**
   * Check that the model with an undefined identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldBeValidWithAnNotExistingId(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.id = UUID.randomUUID().toString();
    assertIsValid(model, vertx, testContext);

  }

  /**
   * Check that merge two models.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskType#merge(TaskType, String, Vertx)
   */
  @Test
  public void shouldMerge(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = this.createModelExample(23);
    assertCanMerge(target, source, vertx, testContext, merged -> {

      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.transactions.forEach(entry -> source.transactions.put(entry.getKey(), entry.getValue()));
      target.callbacks.forEach(entry -> source.callbacks.put(entry.getKey(), entry.getValue()));
      assertThat(merged).isEqualTo(source);

    });

  }

  /**
   * Check that merge two empty models.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskType#merge(TaskType, String, Vertx)
   */
  @Test
  public void shouldMergeEmptymodels(final Vertx vertx, final VertxTestContext testContext) {

    final var target = new TaskType();
    target.id = "1";
    final var source = new TaskType();
    source.id = "2";
    assertCanMerge(target, source, vertx, testContext, merged -> {

      assertThat(merged).isEqualTo(target).isNotEqualTo(source);

    });

  }

  /**
   * Check that merge with {@code null}.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskType#merge(TaskType, String, Vertx)
   */
  @Test
  public void shouldMergeWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanMerge(target, null, vertx, testContext, merged -> assertThat(merged).isSameAs(target));

  }

  /**
   * Check that the model is valid with a name with spaces.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskType#validate(String, Vertx)
   */
  @Test
  public void shouldBeValidANameWithSpaces(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.name = "   1234567890   ";
    assertIsValid(model, vertx, testContext, () -> assertThat(model.name).isEqualTo("1234567890"));

  }

  /**
   * Check that the model is valid with a description with spaces.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskType#validate(String, Vertx)
   */
  @Test
  public void shouldBeValidADescriptionWithSpaces(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.description = "   description   ";
    assertIsValid(model, vertx, testContext, () -> assertThat(model.description).isEqualTo("description"));

  }

  /**
   * Check that the model is valid with keywords with spaces.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskType#validate(String, Vertx)
   */
  @Test
  public void shouldBeValidAKeywordWithSpaces(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.keywords = new ArrayList<>();
    model.keywords.add(null);
    model.keywords.add("   1234567890   ");
    model.keywords.add(null);
    model.keywords.add("     ");
    model.keywords.add("\n\t");
    assertIsValid(model, vertx, testContext,
        () -> assertThat(model.keywords).isNotEmpty().hasSize(1).contains("1234567890", atIndex(0)));

  }

  /**
   * Check that the model is not valid with a bad norms
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see TaskType#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadNorm(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.norms = new ArrayList<>();
    model.norms.add(new ProtocolNormTest().createModelExample(1));
    model.norms.add(new ProtocolNormTest().createModelExample(2));
    model.norms.add(new ProtocolNormTest().createModelExample(3));
    model.norms.get(1).whenever = null;
    assertIsNotValid(model, "norms[1].whenever", vertx, testContext);

  }

  /**
   * Check that the model is merged with a name with spaces.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskType#merge(TaskType, String, Vertx)
   */
  @Test
  public void shouldMergeANameWithSpaces(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new TaskType();
    source.name = "   1234567890   ";
    assertCanMerge(target, source, vertx, testContext, merged -> assertThat(merged.name).isEqualTo("1234567890"));

  }

  /**
   * Check that the model is merged with a description with spaces.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskType#merge(TaskType, String, Vertx)
   */
  @Test
  public void shouldMergeADescriptionWithSpaces(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.name = "name";
    final var source = new TaskType();
    source.description = "   description   ";
    assertCanMerge(target, source, vertx, testContext,
        merged -> assertThat(merged.description).isEqualTo("description"));

  }

  /**
   * Check that the model is merged with keywords with spaces.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskType#merge(TaskType, String, Vertx)
   */
  @Test
  public void shouldMergeAKeywordWithSpaces(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new TaskType();
    source.keywords = new ArrayList<>();
    source.keywords.add("");
    source.keywords.add("   1234567890   ");
    source.keywords.add(null);
    source.keywords.add("     ");
    source.keywords.add("\n\t");

    assertCanMerge(target, source, vertx, testContext,
        merged -> assertThat(merged.keywords).isNotEmpty().hasSize(1).contains("1234567890", atIndex(0)));

  }

  /**
   * Check that the model does not with bad norm.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see TaskType#merge(TaskType, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithABadNorm(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new TaskType();
    source.norms = new ArrayList<>();
    source.norms.add(new ProtocolNormTest().createModelExample(1));
    source.norms.add(new ProtocolNormTest().createModelExample(2));
    source.norms.add(new ProtocolNormTest().createModelExample(3));
    source.norms.get(1).thenceforth = null;
    final var target = this.createModelExample(1);
    assertCannotMerge(target, source, "norms[1].thenceforth", vertx, testContext);

  }

  /**
   * Check that the model is updated with a name with spaces.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskType#update(TaskType, String, Vertx)
   */
  @Test
  public void shouldUpdateANameWithSpaces(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = Model.fromJsonObject(target.toJsonObject(), TaskType.class);
    source.name = "   1234567890   ";
    assertCanUpdate(target, source, vertx, testContext, updated -> assertThat(updated.name).isEqualTo("1234567890"));

  }

  /**
   * Check that the model is updated with a description with spaces.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskType#update(TaskType, String, Vertx)
   */
  @Test
  public void shouldUpdateADescriptionWithSpaces(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.name = "name";
    final var source = Model.fromJsonObject(target.toJsonObject(), TaskType.class);
    source.description = "   description   ";
    assertCanUpdate(target, source, vertx, testContext,
        updated -> assertThat(updated.description).isEqualTo("description"));

  }

  /**
   * Check that the model is updated with keywords with spaces.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskType#update(TaskType, String, Vertx)
   */
  @Test
  public void shouldUpdateAKeywordWithSpaces(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = Model.fromJsonObject(target.toJsonObject(), TaskType.class);
    source.keywords = new ArrayList<>();
    source.keywords.add("");
    source.keywords.add("   1234567890   ");
    source.keywords.add(null);
    source.keywords.add("     ");
    source.keywords.add("\n\t");

    assertCanUpdate(target, source, vertx, testContext,
        updated -> assertThat(updated.keywords).isNotEmpty().hasSize(1).contains("1234567890", atIndex(0)));

  }

  /**
   * Check that the model does not with bad norm.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see TaskType#update(TaskType, String, Vertx)
   */
  @Test
  public void shouldNotUpdateWithABadNorm(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = Model.fromJsonObject(target.toJsonObject(), TaskType.class);
    source.norms = new ArrayList<>();
    source.norms.add(new ProtocolNormTest().createModelExample(1));
    source.norms.add(new ProtocolNormTest().createModelExample(2));
    source.norms.add(new ProtocolNormTest().createModelExample(3));
    source.norms.get(1).whenever = null;

    assertCannotUpdate(target, source, "norms[1].whenever", vertx, testContext);

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

    final var target = this.createModelExample(1);
    assertCanUpdate(target, null, vertx, testContext, updated -> {
      assertThat(updated).isSameAs(target);
    });

  }

  /**
   * Create a task type that is valid.
   *
   * @param index       of the example.
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @return the future task type.
   */
  public Future<TaskType> createModelExample(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(index);
    model.transactions = new JsonObject().put("echo", new JsonObject().put("type", "object"));
    model.callbacks = new JsonObject().put("echo", new JsonObject().put("type", "object"));
    model.norms = null;
    return Future.succeededFuture(model);

  }

  /**
   * Check that update two empty models.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskType#update(TaskType, String, Vertx)
   */
  @Test
  public void shouldUpdateEmptyModels(final Vertx vertx, final VertxTestContext testContext) {

    final var target = new TaskType();
    target.id = "1";
    final var source = new TaskType();
    source.id = "2";
    assertCanUpdate(target, source, vertx, testContext, updated -> {

      assertThat(updated).isEqualTo(target).isNotEqualTo(source);

    });

  }

}
