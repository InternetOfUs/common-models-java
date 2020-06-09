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

package eu.internetofus.common.components.profile_manager;

import static eu.internetofus.common.components.MergesTest.assertCanMerge;
import static eu.internetofus.common.components.MergesTest.assertCannotMerge;
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
 * Test the {@link ScoredLabel}
 *
 * @see Routine
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class ScoredLabelTest extends ModelTestCase<ScoredLabel> {

  /**
   * {@inheritDoc}
   */
  @Override
  public ScoredLabel createModelExample(final int index) {

    final ScoredLabel model = new ScoredLabel();
    model.label = new LabelTest().createModelExample(index);
    model.score = 1.0 / Math.max(1.0, index);
    return model;
  }

  /**
   * Check that the {@link #createModelExample(int)} is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(String, Vertx)
   */
  @Test
  public void shouldExampleBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final ScoredLabel model = this.createModelExample(1);
    assertIsValid(model, vertx, testContext);

  }

  /**
   * Check that a {@link ScoredLabel} without a label is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(String, Vertx)
   */
  @Test
  public void shouldScoredLabelWithoutLabelNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final ScoredLabel model = this.createModelExample(1);
    model.label = null;
    assertIsNotValid(model, "label", vertx, testContext);

  }

  /**
   * Check that a {@link ScoredLabel} with a bad label is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(String, Vertx)
   */
  @Test
  public void shouldLabelWithTooLargeNameNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final ScoredLabel model = this.createModelExample(1);
    model.label.name = ValidationsTest.STRING_256;
    assertIsNotValid(model, "label.name", vertx, testContext);

  }

  /**
   * Check that a {@link ScoredLabel} without a acore is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(String, Vertx)
   */
  @Test
  public void shouldScoredLabelWithoutScoreNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final ScoredLabel model = this.createModelExample(1);
    model.score = null;
    assertIsNotValid(model, "score", vertx, testContext);

  }

  /**
   * Check that merge with {@code null}.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldMergeWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final ScoredLabel target = this.createModelExample(1);
    assertCanMerge(target, null, vertx, testContext, merged -> assertThat(merged).isSameAs(target));
  }

  /**
   * Check that merge two examples.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldMergeTwoExamples(final Vertx vertx, final VertxTestContext testContext) {

    final ScoredLabel source = this.createModelExample(1);
    final ScoredLabel target = this.createModelExample(2);
    assertCanMerge(target, source, vertx, testContext, merged -> assertThat(merged).isEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source));
  }

  /**
   * Check that only merge the {@link ScoredLabel#label}.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyLabel(final Vertx vertx, final VertxTestContext testContext) {

    final ScoredLabel source = new ScoredLabel();
    source.label = new Label();
    source.label.name = "NEW NAME";
    final ScoredLabel target = this.createModelExample(2);
    assertCanMerge(target, source, vertx, testContext, merged -> {
      assertThat(merged).isNotEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);
      target.label.name = source.label.name;
      assertThat(merged).isEqualTo(target);
    });
  }

  /**
   * Check that can not merge with a bad label.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldNotMergeBadLabel(final Vertx vertx, final VertxTestContext testContext) {

    final ScoredLabel source = new ScoredLabel();
    source.label = new Label();
    source.label.name = ValidationsTest.STRING_256;
    final ScoredLabel target = this.createModelExample(2);
    assertCannotMerge(target, source, "label.name", vertx, testContext);
  }

  /**
   * Check that only merge the {@link ScoredLabel#score}.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyScore(final Vertx vertx, final VertxTestContext testContext) {

    final ScoredLabel source = new ScoredLabel();
    source.score = 10.0d;
    final ScoredLabel target = this.createModelExample(2);
    assertCanMerge(target, source, vertx, testContext, merged -> {
      assertThat(merged).isNotEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);
      target.score = source.score;
      assertThat(merged).isEqualTo(target);
    });
  }
}
