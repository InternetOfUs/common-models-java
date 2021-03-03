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

package eu.internetofus.common.components.profile_manager;

import static eu.internetofus.common.components.MergeAsserts.assertCanMerge;
import static eu.internetofus.common.components.MergeAsserts.assertCannotMerge;
import static eu.internetofus.common.components.UpdatesTest.assertCanUpdate;
import static eu.internetofus.common.components.UpdatesTest.assertCannotUpdate;
import static eu.internetofus.common.components.ValidationsTest.assertIsNotValid;
import static eu.internetofus.common.components.ValidationsTest.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.internetofus.common.components.ModelTestCase;
import eu.internetofus.common.components.ValidationsTest;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

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
   * Check that not accept norms with bad attribute.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Norm#validate(String,Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadAttribute(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new Norm();
    model.attribute = ValidationsTest.STRING_256;
    assertIsNotValid(model, "attribute", vertx, testContext);

  }

  /**
   * Check that not accept norms with bad comparison.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Norm#validate(String,Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadComparison(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new Norm();
    model.comparison = ValidationsTest.STRING_256;
    assertIsNotValid(model, "comparison", vertx, testContext);

  }

  /**
   * Check that not merge with bad attribute.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Norm#merge(Norm, String, io.vertx.core.Vertx)
   */
  @Test
  public void shouldNotMergeWithABadAttribute(final Vertx vertx, final VertxTestContext testContext) {

    final var target = new Norm();
    final var source = new Norm();
    source.attribute = ValidationsTest.STRING_256;
    assertCannotMerge(target, source, "attribute", vertx, testContext);

  }

  /**
   * Check that not merge with bad comparison.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Norm#merge(Norm, String, io.vertx.core.Vertx)
   */
  @Test
  public void shouldNotMergeWithABadComparison(final Vertx vertx, final VertxTestContext testContext) {

    final var target = new Norm();
    final var source = new Norm();
    source.comparison = ValidationsTest.STRING_256;
    assertCannotMerge(target, source, "comparison", vertx, testContext);

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
    assertCanMerge(target, source, vertx, testContext, merged -> assertThat(merged).isNotEqualTo(target).isEqualTo(source));

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
    assertCanMerge(target, source, vertx, testContext, merged -> assertThat(merged).isEqualTo(target).isNotSameAs(target).isNotEqualTo(source));

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
    assertCanUpdate(target, source, vertx, testContext, updated -> assertThat(updated).isNotEqualTo(target).isEqualTo(source));

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

  /**
   * Check that not update with bad attribute.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Norm#update(Norm, String, io.vertx.core.Vertx)
   */
  @Test
  public void shouldNotUpdateWithABadAttribute(final Vertx vertx, final VertxTestContext testContext) {

    final var target = new Norm();
    final var source = new Norm();
    source.attribute = ValidationsTest.STRING_256;
    assertCannotUpdate(target, source, "attribute", vertx, testContext);

  }

}
