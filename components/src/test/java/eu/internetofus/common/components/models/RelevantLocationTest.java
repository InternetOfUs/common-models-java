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

/**
 * Test the {@link RelevantLocation}.
 *
 * @see RelevantLocation
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class RelevantLocationTest extends ModelTestCase<RelevantLocation> {

  /**
   * {@inheritDoc}
   */
  @Override
  public RelevantLocation createModelExample(final int index) {

    final var location = new RelevantLocation();
    location.id = null;
    location.label = "label_" + index;
    location.latitude = (double) index % 90;
    location.longitude = (double) index % 180;
    return location;
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
   * Check that a model with all the values is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(WeNetValidateContext)
   */
  @Test
  public void shouldFullModelBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new RelevantLocation();
    model.id = "      ";
    model.label = "    label    ";
    model.longitude = 10d;
    model.latitude = -10d;

    assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

      final var expected = new RelevantLocation();
      expected.id = model.id;
      expected.label = "label";
      expected.longitude = 10d;
      expected.latitude = -10d;
      assertThat(model).isEqualTo(expected);

    });
  }

  /**
   * Check that the model with id is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(WeNetValidateContext)
   */
  @Test
  public void shouldBeValidWithAnId(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new RelevantLocation();
    model.id = "has_id";
    assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that not accept model with bad longitude.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithABadLongitudeLessThanMinimum(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new RelevantLocation();
    model.longitude = -180.0001;
    assertIsNotValid(model, "longitude", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that not accept model with bad longitude.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithABadLongitudeMoreThanMaximum(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new RelevantLocation();
    model.longitude = 180.0001;
    assertIsNotValid(model, "longitude", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that not accept model with bad latitude.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithABadLatitudeLessThanMinimum(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new RelevantLocation();
    model.latitude = -90.0001;
    assertIsNotValid(model, "latitude", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that not accept model with bad latitude.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithABadLatitudeMoreThanMaximum(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new RelevantLocation();
    model.latitude = 90.0001;
    assertIsNotValid(model, "latitude", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that not merge model with bad longitude.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithABadLongitudeLessThanMinimum(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new RelevantLocation();
    source.longitude = -180.0001;
    assertCannotMerge(target, source, "longitude", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that not merge model with bad longitude.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithABadLongitudeMoreThanMaximum(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new RelevantLocation();
    source.longitude = 180.0001;
    assertCannotMerge(target, source, "longitude", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that not merge model with bad latitude.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithABadLatitudeLessThanMinimum(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new RelevantLocation();
    source.latitude = -90.0001;
    assertCannotMerge(target, source, "latitude", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that not merge model with bad latitude.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithABadLatitudeMoreThanMaximum(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new RelevantLocation();
    source.latitude = 90.0001;
    assertCannotMerge(target, source, "latitude", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that merge.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldMerge(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "1";
    final var source = this.createModelExample(2);
    assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {
      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      source.id = "1";
      assertThat(merged).isEqualTo(source);
    });
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
   * Check that merge only label.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldMergeOnlyLabel(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "1";
    final var source = new RelevantLocation();
    source.label = "NEW LABEL";
    source.latitude = target.latitude;
    source.longitude = target.longitude;
    assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {
      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.label = "NEW LABEL";
      assertThat(merged).isEqualTo(target);
    });
  }

  /**
   * Check that merge the latitude.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldMergeLatitude(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "1";
    final var source = new RelevantLocation();
    source.latitude = 45d;
    assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {
      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.latitude = 45d;
      assertThat(merged).isEqualTo(target);
    });
  }

  /**
   * Check that merge the longitude.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldMergeLongitude(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "1";
    final var source = new RelevantLocation();
    source.longitude = 45d;
    assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {
      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.longitude = 45d;
      assertThat(merged).isEqualTo(target);
    });
  }

  /**
   * Check that merge only longitude.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldMergeOnlyLongitude(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "1";
    final var source = new RelevantLocation();
    source.latitude = target.latitude;
    source.longitude = 0d;
    assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {
      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.longitude = 0d;
      assertThat(merged).isEqualTo(target);
    });

  }

  /**
   * Check that merge only latitude.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldMergeOnlyLatitude(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "1";
    final var source = new RelevantLocation();
    source.longitude = target.longitude;
    source.latitude = 0d;
    assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {
      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.latitude = 0d;
      assertThat(merged).isEqualTo(target);
    });
  }

  /**
   * Check that not update model with bad longitude.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldNotUpdateWithABadLongitudeLessThanMinimum(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new RelevantLocation();
    source.longitude = -180.0001;
    assertCannotUpdate(target, source, "longitude", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that not update model with bad longitude.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldNotUpdateWithABadLongitudeMoreThanMaximum(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new RelevantLocation();
    source.longitude = 180.0001;
    assertCannotUpdate(target, source, "longitude", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that not update model with bad latitude.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldNotUpdateWithABadLatitudeLessThanMinimum(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new RelevantLocation();
    source.latitude = -90.0001;
    assertCannotUpdate(target, source, "latitude", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that not update model with bad latitude.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldNotUpdateWithABadLatitudeMoreThanMaximum(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new RelevantLocation();
    source.latitude = 90.0001;
    assertCannotUpdate(target, source, "latitude", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that update.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldUpdate(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "1";
    final var source = this.createModelExample(2);
    assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {
      assertThat(updated).isNotEqualTo(target).isNotEqualTo(source);
      source.id = "1";
      assertThat(updated).isEqualTo(source);
    });
  }

  /**
   * Check that update with {@code null}.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanUpdate(target, null, new WeNetValidateContext("codePrefix", vertx), testContext,
        updated -> assertThat(updated).isSameAs(target));
  }

  /**
   * Check that update only label.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateOnlyLabel(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "1";
    final var source = new RelevantLocation();
    source.label = "NEW LABEL";
    source.latitude = target.latitude;
    source.longitude = target.longitude;
    assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {
      assertThat(updated).isNotEqualTo(target).isNotEqualTo(source);
      target.label = "NEW LABEL";
      assertThat(updated).isEqualTo(target);
    });
  }

  /**
   * Check that update the latitude and longitude.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateLatitudeLongitude(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "1";
    final var source = new RelevantLocation();
    assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {
      assertThat(updated).isNotEqualTo(target).isNotEqualTo(source);
      source.id = target.id;
      assertThat(updated).isEqualTo(source);
    });
  }

  /**
   * Check that update only longitude.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateOnlyLongitude(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "1";
    final var source = new RelevantLocation();
    source.latitude = target.latitude;
    source.longitude = 0d;
    assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {
      assertThat(updated).isNotEqualTo(target).isNotEqualTo(source);
      source.id = target.id;
      assertThat(updated).isEqualTo(source);
    });

  }

  /**
   * Check that update only latitude.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateOnlyLatitude(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "1";
    final var source = new RelevantLocation();
    source.longitude = target.longitude;
    source.latitude = 0d;
    assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {
      assertThat(updated).isNotEqualTo(target).isNotEqualTo(source);
      source.id = target.id;
      assertThat(updated).isEqualTo(source);
    });
  }
}
