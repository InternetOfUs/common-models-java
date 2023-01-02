/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/
package eu.internetofus.common.components.models;

import static eu.internetofus.common.model.MergeAsserts.assertCanMerge;
import static eu.internetofus.common.model.MergeAsserts.assertCannotMerge;
import static eu.internetofus.common.model.UpdateAsserts.assertCanUpdate;
import static eu.internetofus.common.model.UpdateAsserts.assertCannotUpdate;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test the {@link KnownActivity}.
 *
 * @see KnownActivity
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class KnownActivityTest extends ModelTestCase<KnownActivity> {

  /**
   * {@inheritDoc}
   */
  @Override
  public KnownActivity createModelExample(final int index) {

    final var model = new KnownActivity();
    model.activity = "activity_" + index;
    model.timestamp = 1000l * index + 10000l;
    model.confidence = 1.0 / Math.max(1, index);
    return model;
  }

  /**
   * Check that the {@link #createModelExample(int)} is valid.
   *
   * @param index       of the example to test.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @ParameterizedTest(name = "Should be valid the example {0}")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final var model = new KnownActivity();
    model.activity = "    activity_" + index + "    ";
    model.timestamp = 1000l * index + 10000l;
    model.confidence = 1.0 / Math.max(1, index);

    assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

      final var expected = this.createModelExample(index);
      assertThat(model).isEqualTo(expected);
    });

  }

  /**
   * Check that the KnownActivity is not valid if has a bad activity.
   *
   * @param activity    that is invalid.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @ParameterizedTest(name = "Should not be valid with the activity {0}")
  @NullSource
  public void shouldNotBeValidWithABadActivity(final String activity, final Vertx vertx,
      final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.activity = activity;
    assertIsNotValid(model, "activity", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that the KnownActivity is not valid if has a bad timestamp.
   *
   * @param timestamp   that is invalid.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @ParameterizedTest(name = "Should not be valid with the timestamp {0}")
  @NullSource
  public void shouldNotBeValidWithABadTimestamp(final Long timestamp, final Vertx vertx,
      final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.timestamp = timestamp;
    assertIsNotValid(model, "timestamp", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that the KnownActivity is not valid if has a bad confidence.
   *
   * @param confidence  that is invalid.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @ParameterizedTest(name = "Should not be valid with the confidence {0}")
  @NullSource
  @ValueSource(doubles = { -1, -0.1, 1.1, 2 })
  public void shouldNotBeValidWithABadConfidence(final Double confidence, final Vertx vertx,
      final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.confidence = confidence;
    assertIsNotValid(model, "confidence", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that two {@link #createModelExample(int)} can be merged.
   *
   * @param index       of the example to test.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @ParameterizedTest(name = "Should be valid the example {0}")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleCanMerged(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final var source = new KnownActivity();
    source.activity = "    activity_" + index + "    ";
    source.timestamp = 1000l * index + 10000l;
    ;
    source.confidence = 1.0 / Math.max(1, index);

    final var target = this.createModelExample(index - 1);

    assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

      final var expected = this.createModelExample(index);
      assertThat(merged).isEqualTo(expected);
    });

  }

  /**
   * Check that cannot merge a KnownActivity with a bad confidence.
   *
   * @param confidence  that is invalid.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @ParameterizedTest(name = "Should not be merged with the confidence {0}")
  @ValueSource(doubles = { -1, -0.1, 1.1, 2 })
  public void shouldCannotMergeWithABadConfidence(final Double confidence, final Vertx vertx,
      final VertxTestContext testContext) {

    final var source = new KnownActivity();
    source.confidence = confidence;
    final var target = this.createModelExample(1);
    assertCannotMerge(target, source, "confidence", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Should merge with {@code null}
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#merge(CommunityProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanMerge(target, null, new WeNetValidateContext("codePrefix", vertx), testContext,
        merged -> assertThat(merged).isSameAs(target));

  }

  /**
   * Should merge with empty KnownActivity.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#merge(CommunityProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeWithEmpty(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanMerge(target, new KnownActivity(), new WeNetValidateContext("codePrefix", vertx), testContext,
        merged -> assertThat(merged).isNotSameAs(target).isEqualTo(target));

  }

  /**
   * Check that two {@link #createModelExample(int)} can be updated.
   *
   * @param index       of the example to test.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @ParameterizedTest(name = "Should be valid the example {0}")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleCanUpdated(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final var source = new KnownActivity();
    source.activity = "    activity_" + index + "    ";
    source.timestamp = 1000l * index + 10000l;
    source.confidence = 1.0 / Math.max(1, index);

    final var target = this.createModelExample(index - 1);

    assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {

      final var expected = this.createModelExample(index);
      assertThat(updated).isEqualTo(expected);
    });

  }

  /**
   * Check that cannot update a KnownActivity with a bad activity.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @Test
  public void shouldCannotUpdateWithABadActivity(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new KnownActivity();
    source.activity = null;
    final var target = this.createModelExample(1);
    assertCannotUpdate(target, source, "activity", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that cannot update a KnownActivity with a bad timestamp.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @Test
  public void shouldCannotUpdateWithABadTimestamp(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new KnownActivity();
    source.activity = "activity";
    source.timestamp = null;
    final var target = this.createModelExample(1);
    assertCannotUpdate(target, source, "timestamp", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that cannot update a KnownActivity with a bad confidence.
   *
   * @param confidence  that is invalid.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @ParameterizedTest(name = "Should not be updated with the confidence {0}")
  @ValueSource(doubles = { -1, -0.1, 1.1, 2 })
  public void shouldCannotUpdateWithABadConfidence(final Double confidence, final Vertx vertx,
      final VertxTestContext testContext) {

    final var source = new KnownActivity();
    source.activity = "activity";
    source.timestamp = 0l;
    source.confidence = confidence;
    final var target = this.createModelExample(1);
    assertCannotUpdate(target, source, "confidence", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Should update with {@code null}
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#update(CommunityProfile, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanUpdate(target, null, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {
      assertThat(updated).isSameAs(target);
    });

  }

}
