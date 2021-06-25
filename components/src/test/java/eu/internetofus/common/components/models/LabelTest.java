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
import static eu.internetofus.common.model.ValidationsTest.assertIsNotValid;
import static eu.internetofus.common.model.ValidationsTest.assertIsValid;
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
    model.latitude = (double) index % 90;
    model.longitude = (double) index % 180;
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
  public void shouldLabelWitBadLongitudeNotBeValid(final double value, final Vertx vertx,
      final VertxTestContext testContext) {

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
  public void shouldLabelWitBadLatitudeNotBeValid(final double value, final Vertx vertx,
      final VertxTestContext testContext) {

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
    assertCanMerge(target, source, vertx, testContext,
        merged -> assertThat(merged).isEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source));
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
