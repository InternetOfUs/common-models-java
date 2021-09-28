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
import static eu.internetofus.common.model.UpdateAsserts.assertCanUpdate;
import static eu.internetofus.common.model.ValidableAsserts.assertIsNotValid;
import static eu.internetofus.common.model.ValidableAsserts.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.model.ModelTestCase;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test the {@link ProtocolNorm}.
 *
 * @see ProtocolNorm
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class ProtocolNormTest extends ModelTestCase<ProtocolNorm> {

  /**
   * {@inheritDoc}
   */
  @Override
  public ProtocolNorm createModelExample(final int index) {

    final var model = new ProtocolNorm();
    model.description = "description " + index;
    model.whenever = "is_received_do_transaction(_,json([index=" + index + "]))";
    model.thenceforth = "put_task_state_attribute('index'," + index + ")";
    model.ontology = "ontology(" + index + ").";
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
   * Check that the model is not valid without whenever.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithoutWhenever(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.whenever = null;
    assertIsNotValid(model, "whenever", vertx, testContext);

  }

  /**
   * Check that the model is not valid without thenceforth.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithoutThenceforth(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.thenceforth = null;
    assertIsNotValid(model, "thenceforth", vertx, testContext);

  }

  /**
   * Check that the model is not valid with thenceforth equals to whenever.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithThenceforthEqualsToWhenever(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.thenceforth = model.whenever;
    assertIsNotValid(model, "thenceforth", vertx, testContext);

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
    assertCanMerge(target, source, vertx, testContext,
        merged -> assertThat(merged).isNotEqualTo(target).isEqualTo(source));

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
   * Check that merge with empty norm.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskType#merge(TaskType, String, Vertx)
   */
  @Test
  public void shouldMergeWithEmptyNorm(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanMerge(target, new ProtocolNorm(), vertx, testContext,
        merged -> assertThat(merged).isNotSameAs(target).isEqualTo(target));

  }

  /**
   * Check that update two models.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskType#update(TaskType, String, Vertx)
   */
  @Test
  public void shouldUpdate(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = this.createModelExample(23);
    assertCanUpdate(target, source, vertx, testContext,
        updated -> assertThat(updated).isNotEqualTo(target).isEqualTo(source));

  }

  /**
   * Check that update with {@code null}.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskType#update(TaskType, String, Vertx)
   */
  @Test
  public void shouldUpdateWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanUpdate(target, null, vertx, testContext, updated -> assertThat(updated).isSameAs(target));

  }

}
