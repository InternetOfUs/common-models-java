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
import static eu.internetofus.common.model.ValidableAsserts.assertIsNotValid;
import static eu.internetofus.common.model.ValidableAsserts.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.WeNetValidateContext;
import eu.internetofus.common.model.ModelTestCase;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

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

    final var model = new ScoredLabel();
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
   * @see RelevantLocation#validate(WeNetValidateContext)
   */
  @Test
  public void shouldExampleBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that a {@link ScoredLabel} without a label is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(WeNetValidateContext)
   */
  @Test
  public void shouldScoredLabelWithoutLabelNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.label = null;
    assertIsNotValid(model, "label", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that a {@link ScoredLabel} with a bad label is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(WeNetValidateContext)
   */
  @Test
  public void shouldLabelWithTooLargeNameNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.label.name = null;
    assertIsNotValid(model, "label.name", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that a {@link ScoredLabel} without a acore is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(WeNetValidateContext)
   */
  @Test
  public void shouldScoredLabelWithoutScoreNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.score = null;
    assertIsNotValid(model, "score", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that merge with {@code null}.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldMergeWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanMerge(target, null, new WeNetValidateContext("codePrefix", vertx), testContext,
        merged -> assertThat(merged).isSameAs(target));
  }

  /**
   * Check that merge two examples.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldMergeTwoExamples(final Vertx vertx, final VertxTestContext testContext) {

    final var source = this.createModelExample(1);
    final var target = this.createModelExample(2);
    assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext,
        merged -> assertThat(merged).isEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source));
  }

  /**
   * Check that only merge the {@link ScoredLabel#label}.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldMergeOnlyLabel(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new ScoredLabel();
    source.label = new Label();
    source.label.name = "NEW NAME";
    final var target = this.createModelExample(2);
    assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {
      assertThat(merged).isNotEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);
      target.label.name = source.label.name;
      assertThat(merged).isEqualTo(target);
    });
  }

  /**
   * Check that only merge the {@link ScoredLabel#score}.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldMergeOnlyScore(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new ScoredLabel();
    source.score = 10.0d;
    final var target = this.createModelExample(2);
    assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {
      assertThat(merged).isNotEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);
      target.score = source.score;
      assertThat(merged).isEqualTo(target);
    });
  }
}
