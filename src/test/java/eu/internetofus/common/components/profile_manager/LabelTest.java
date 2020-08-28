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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import eu.internetofus.common.components.ModelTestCase;
import eu.internetofus.common.components.ValidationsTest;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link Label}
 *
 * @see Routine
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class LabelTest extends ModelTestCase<Label> {

  /**
   * {@inheritDoc}
   */
  @Override
  public Label createModelExample(final int index) {

    final var model = new Label();
    model.name = "name_" + index;
    model.semantic_class = (double) index;
    model.latitude = -1.0 - index % 179;
    model.longitude = 1.0 + index % 89;
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

    final var model = this.createModelExample(1);
    assertIsValid(model, vertx, testContext);

  }

  /**
   * Check that a {@link Label} without a name is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(String, Vertx)
   */
  @Test
  public void shouldLabelWithoutNameNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.name = null;
    assertIsNotValid(model, "name", vertx, testContext);

  }

  /**
   * Check that a {@link Label} with a too large name is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(String, Vertx)
   */
  @Test
  public void shouldLabelWithTooLargeNameNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.name = ValidationsTest.STRING_256;
    assertIsNotValid(model, "name", vertx, testContext);

  }

  /**
   * Check that a {@link Label} without a semantic_class is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(String, Vertx)
   */
  @Test
  public void shouldLabelWithoutSemanticClassNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.semantic_class = null;
    assertIsNotValid(model, "semantic_class", vertx, testContext);

  }

  /**
   * Check that a {@link Label} without a longitude is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(String, Vertx)
   */
  @Test
  public void shouldLabelWithoutLongitudeNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.longitude = null;
    assertIsNotValid(model, "longitude", vertx, testContext);

  }

  /**
   * Check that a {@link Label} with a bad longitude value is not valid.
   *
   * @param value       that is not valid for a longitude.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(String, Vertx)
   */
  @ParameterizedTest(name = "Should a Label with a {0} longitude not be valid")
  @ValueSource(doubles = { -180.1, 180.1, -180.00001, 180.000001 })
  public void shouldLabelWitBadLongitudeNotBeValid(final double value, final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.longitude = value;
    assertIsNotValid(model, "longitude", vertx, testContext);

  }

  /**
   * Check that a {@link Label} without a latitude is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(String, Vertx)
   */
  @Test
  public void shouldLabelWithoutLatitudeNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.latitude = null;
    assertIsNotValid(model, "latitude", vertx, testContext);

  }

  /**
   * Check that a {@link Label} with a bad latitude value is not valid.
   *
   * @param value       that is not valid for a latitude.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(String, Vertx)
   */
  @ParameterizedTest(name = "Should a Label with a {0} latitude not be valid")
  @ValueSource(doubles = { -90.1, 90.1, -90.00001, 90.000001 })
  public void shouldLabelWitBadLatitudeNotBeValid(final double value, final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.latitude = value;
    assertIsNotValid(model, "latitude", vertx, testContext);

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

    final var target = this.createModelExample(1);
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

    final var source = this.createModelExample(1);
    final var target = this.createModelExample(2);
    assertCanMerge(target, source, vertx, testContext, merged -> assertThat(merged).isEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source));
  }

  /**
   * Check that only merge the {@link Label#name}.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyName(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new Label();
    source.name = "NEW NAME";
    final var target = this.createModelExample(2);
    assertCanMerge(target, source, vertx, testContext, merged -> {
      assertThat(merged).isNotEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);
      target.name = source.name;
      assertThat(merged).isEqualTo(target);
    });
  }

  /**
   * Check that can not merge with a large name.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldNotMergeLargeName(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new Label();
    source.name = ValidationsTest.STRING_256;
    final var target = this.createModelExample(2);
    assertCannotMerge(target, source, "name", vertx, testContext);
  }

  /**
   * Check that only merge the {@link Label#semantic_class}.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldMergeOnlySemantic_class(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new Label();
    source.semantic_class = -1.0;
    final var target = this.createModelExample(2);
    assertCanMerge(target, source, vertx, testContext, merged -> {
      assertThat(merged).isNotEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);
      target.semantic_class = source.semantic_class;
      assertThat(merged).isEqualTo(target);
    });
  }

  /**
   * Check that only merge the {@link Label#latitude}.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyLatitude(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new Label();
    source.latitude = 43d;
    final var target = this.createModelExample(2);
    assertCanMerge(target, source, vertx, testContext, merged -> {
      assertThat(merged).isNotEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);
      target.latitude = source.latitude;
      assertThat(merged).isEqualTo(target);
    });
  }

  /**
   * Check that can not merge with a bad latitude.
   *
   * @param value       that is not valid for a latitude.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @ParameterizedTest(name = "Should not merge a Label with a {0} latitude")
  @ValueSource(doubles = { -90.1, 90.1, -90.00001, 90.000001 })
  public void shouldNotMergeBadLatitude(final double value, final Vertx vertx, final VertxTestContext testContext) {

    final var source = new Label();
    source.latitude = value;
    final var target = this.createModelExample(2);
    assertCannotMerge(target, source, "latitude", vertx, testContext);
  }

  /**
   * Check that only merge the {@link Label#longitude}.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyLongitude(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new Label();
    source.longitude = 43d;
    final var target = this.createModelExample(2);
    assertCanMerge(target, source, vertx, testContext, merged -> {
      assertThat(merged).isNotEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);
      target.longitude = source.longitude;
      assertThat(merged).isEqualTo(target);
    });
  }

  /**
   * Check that can not merge with a bad longitude.
   *
   * @param value       that is not valid for a longitude.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @ParameterizedTest(name = "Should not merge a Label with a {0} longitude")
  @ValueSource(doubles = { -180.1, 180.1, -180.00001, 180.000001 })
  public void shouldNotMergeBadLongitude(final double value, final Vertx vertx, final VertxTestContext testContext) {

    final var source = new Label();
    source.longitude = value;
    final var target = this.createModelExample(2);
    assertCannotMerge(target, source, "longitude", vertx, testContext);
  }

}
