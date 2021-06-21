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

import static eu.internetofus.common.components.MergeAsserts.assertCanMerge;
import static eu.internetofus.common.components.UpdatesTest.assertCanUpdate;
import static eu.internetofus.common.components.ValidationsTest.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.ModelTestCase;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test the {@link Norm}
 *
 * @see Norm
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class NormTest extends ModelTestCase<Norm> {

  /**
   * {@inheritDoc}
   */
  @Override
  public Norm createModelExample(final int index) {

    final var norm = new Norm();
    norm.id = null;
    norm.attribute = "attribute_" + index;
    norm.operator = NormOperator.EQUALS;
    norm.comparison = "comparison_" + index;
    norm.negation = true;
    return norm;
  }

  /**
   * Check that the {@link #createModelExample(int)} is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Norm#validate(String,Vertx)
   */
  @Test
  public void shouldExample1BeValid(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    assertIsValid(model, vertx, testContext);

  }

  /**
   * Check that a model with all the values is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Norm#validate(String,Vertx)
   */
  @Test
  public void shouldFullModelBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new Norm();
    model.id = "      ";
    model.attribute = "    attribute    ";
    model.operator = NormOperator.GREATER_THAN;
    model.comparison = "   comparison    ";
    model.negation = false;

    assertIsValid(model, vertx, testContext, () -> {

      final var expected = new Norm();
      expected.id = model.id;
      expected.attribute = "attribute";
      expected.operator = NormOperator.GREATER_THAN;
      expected.comparison = "comparison";
      expected.negation = false;
      assertThat(model).isEqualTo(expected);

    });

  }

  /**
   * Check that the model with id is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Norm#validate(String,Vertx)
   */
  @Test
  public void shouldBeValidWithAnId(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new Norm();
    model.id = "has_id";
    assertIsValid(model, vertx, testContext);

  }

  /**
   * Check that merge.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Norm#merge(Norm, String, io.vertx.core.Vertx)
   */
  @Test
  public void shouldMerge(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = this.createModelExample(2);
    assertCanMerge(target, source, vertx, testContext,
        merged -> assertThat(merged).isNotEqualTo(target).isEqualTo(source));

  }

  /**
   * Check that merge with {@code null}.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Norm#merge(Norm, String, io.vertx.core.Vertx)
   */
  @Test
  public void shouldMergeWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanMerge(target, null, vertx, testContext, merged -> assertThat(merged).isSameAs(target));

  }

  /**
   * Check that merge only the identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Norm#merge(Norm, String, io.vertx.core.Vertx)
   */
  @Test
  public void shouldMergeOnlyId(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "1";
    final var source = new Norm();
    assertCanMerge(target, source, vertx, testContext,
        merged -> assertThat(merged).isEqualTo(target).isNotSameAs(target).isNotEqualTo(source));

  }

  /**
   * Check that merge only the attribute.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Norm#merge(Norm, String, io.vertx.core.Vertx)
   */
  @Test
  public void shouldMergeOnlyAttribute(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new Norm();
    source.attribute = "NEW VALUE";
    assertCanMerge(target, source, vertx, testContext, merged -> {

      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.attribute = "NEW VALUE";
      assertThat(merged).isEqualTo(target);

    });

  }

  /**
   * Check that merge only the operator.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Norm#merge(Norm, String, io.vertx.core.Vertx)
   */
  @Test
  public void shouldMergeOnlyOperator(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new Norm();
    source.operator = NormOperator.GREATER_THAN;
    assertCanMerge(target, source, vertx, testContext, merged -> {

      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.operator = NormOperator.GREATER_THAN;
      assertThat(merged).isEqualTo(target);

    });

  }

  /**
   * Check that merge only the comparison.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Norm#merge(Norm, String, io.vertx.core.Vertx)
   */
  @Test
  public void shouldMergeOnlyComparison(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new Norm();
    source.comparison = "NEW VALUE";
    assertCanMerge(target, source, vertx, testContext, merged -> {

      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.comparison = "NEW VALUE";
      assertThat(merged).isEqualTo(target);

    });

  }

  /**
   * Check that merge only the negation.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Norm#merge(Norm, String, io.vertx.core.Vertx)
   */
  @Test
  public void shouldMergeOnlyNegation(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new Norm();
    source.negation = false;
    assertCanMerge(target, source, vertx, testContext, merged -> {

      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.negation = false;
      assertThat(merged).isEqualTo(target);

    });

  }

  /**
   * Check that update.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Norm#update(Norm, String, io.vertx.core.Vertx)
   */
  @Test
  public void shouldUpdate(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = this.createModelExample(2);
    assertCanUpdate(target, source, vertx, testContext,
        updated -> assertThat(updated).isNotEqualTo(target).isEqualTo(source));

  }

  /**
   * Check that update with {@code null}.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Norm#update(Norm, String, io.vertx.core.Vertx)
   */
  @Test
  public void shouldUpdateWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanUpdate(target, null, vertx, testContext, updated -> assertThat(updated).isSameAs(target));

  }

}
