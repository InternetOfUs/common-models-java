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

package eu.internetofus.common.components.models;

import static eu.internetofus.common.components.MergeAsserts.assertCanMerge;
import static eu.internetofus.common.components.MergeAsserts.assertCannotMerge;
import static eu.internetofus.common.components.UpdatesTest.assertCanUpdate;
import static eu.internetofus.common.components.UpdatesTest.assertCannotUpdate;
import static eu.internetofus.common.components.ValidationsTest.assertIsNotValid;
import static eu.internetofus.common.components.ValidationsTest.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.ModelTestCase;
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
   * @see RelevantLocation#validate(String, Vertx)
   */
  @Test
  public void shouldExampleBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    assertIsValid(model, vertx, testContext);

  }

  /**
   * Check that a model with all the values is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(String, Vertx)
   */
  @Test
  public void shouldFullModelBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new RelevantLocation();
    model.id = "      ";
    model.label = "    label    ";
    model.longitude = 10d;
    model.latitude = -10d;

    assertIsValid(model, vertx, testContext, () -> {

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
   * @see RelevantLocation#validate(String, Vertx)
   */
  @Test
  public void shouldBeValidWithAnId(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new RelevantLocation();
    model.id = "has_id";
    assertIsValid(model, vertx, testContext);

  }

  /**
   * Check that not accept model with bad longitude.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadLongitudeLessThanMinimum(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new RelevantLocation();
    model.longitude = -180.0001;
    assertIsNotValid(model, "longitude", vertx, testContext);

  }

  /**
   * Check that not accept model with bad longitude.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadLongitudeMoreThanMaximum(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new RelevantLocation();
    model.longitude = 180.0001;
    assertIsNotValid(model, "longitude", vertx, testContext);

  }

  /**
   * Check that not accept model with bad latitude.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadLatitudeLessThanMinimum(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new RelevantLocation();
    model.latitude = -90.0001;
    assertIsNotValid(model, "latitude", vertx, testContext);

  }

  /**
   * Check that not accept model with bad latitude.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadLatitudeMoreThanMaximum(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new RelevantLocation();
    model.latitude = 90.0001;
    assertIsNotValid(model, "latitude", vertx, testContext);

  }

  /**
   * Check that not merge model with bad longitude.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithABadLongitudeLessThanMinimum(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new RelevantLocation();
    source.longitude = -180.0001;
    assertCannotMerge(target, source, "longitude", vertx, testContext);

  }

  /**
   * Check that not merge model with bad longitude.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithABadLongitudeMoreThanMaximum(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new RelevantLocation();
    source.longitude = 180.0001;
    assertCannotMerge(target, source, "longitude", vertx, testContext);

  }

  /**
   * Check that not merge model with bad latitude.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithABadLatitudeLessThanMinimum(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new RelevantLocation();
    source.latitude = -90.0001;
    assertCannotMerge(target, source, "latitude", vertx, testContext);

  }

  /**
   * Check that not merge model with bad latitude.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithABadLatitudeMoreThanMaximum(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new RelevantLocation();
    source.latitude = 90.0001;
    assertCannotMerge(target, source, "latitude", vertx, testContext);

  }

  /**
   * Check that merge.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldMerge(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "1";
    final var source = this.createModelExample(2);
    assertCanMerge(target, source, vertx, testContext, merged -> {
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
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldMergeWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanMerge(target, null, vertx, testContext, merged -> assertThat(merged).isSameAs(target));
  }

  /**
   * Check that merge only label.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyLabel(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "1";
    final var source = new RelevantLocation();
    source.label = "NEW LABEL";
    source.latitude = target.latitude;
    source.longitude = target.longitude;
    assertCanMerge(target, source, vertx, testContext, merged -> {
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
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldMergeLatitude(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "1";
    final var source = new RelevantLocation();
    source.latitude = 45d;
    assertCanMerge(target, source, vertx, testContext, merged -> {
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
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldMergeLongitude(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "1";
    final var source = new RelevantLocation();
    source.longitude = 45d;
    assertCanMerge(target, source, vertx, testContext, merged -> {
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
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyLongitude(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "1";
    final var source = new RelevantLocation();
    source.latitude = target.latitude;
    source.longitude = 0d;
    assertCanMerge(target, source, vertx, testContext, merged -> {
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
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyLatitude(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "1";
    final var source = new RelevantLocation();
    source.longitude = target.longitude;
    source.latitude = 0d;
    assertCanMerge(target, source, vertx, testContext, merged -> {
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
   * @see RelevantLocation#update(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldNotUpdateWithABadLongitudeLessThanMinimum(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new RelevantLocation();
    source.longitude = -180.0001;
    assertCannotUpdate(target, source, "longitude", vertx, testContext);

  }

  /**
   * Check that not update model with bad longitude.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldNotUpdateWithABadLongitudeMoreThanMaximum(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new RelevantLocation();
    source.longitude = 180.0001;
    assertCannotUpdate(target, source, "longitude", vertx, testContext);

  }

  /**
   * Check that not update model with bad latitude.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldNotUpdateWithABadLatitudeLessThanMinimum(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new RelevantLocation();
    source.latitude = -90.0001;
    assertCannotUpdate(target, source, "latitude", vertx, testContext);

  }

  /**
   * Check that not update model with bad latitude.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldNotUpdateWithABadLatitudeMoreThanMaximum(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new RelevantLocation();
    source.latitude = 90.0001;
    assertCannotUpdate(target, source, "latitude", vertx, testContext);

  }

  /**
   * Check that update.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldUpdate(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "1";
    final var source = this.createModelExample(2);
    assertCanUpdate(target, source, vertx, testContext, updated -> {
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
   * @see RelevantLocation#update(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldUpdateWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanUpdate(target, null, vertx, testContext, updated -> assertThat(updated).isSameAs(target));
  }

  /**
   * Check that update only label.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldUpdateOnlyLabel(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "1";
    final var source = new RelevantLocation();
    source.label = "NEW LABEL";
    source.latitude = target.latitude;
    source.longitude = target.longitude;
    assertCanUpdate(target, source, vertx, testContext, updated -> {
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
   * @see RelevantLocation#update(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldUpdateLatitudeLongitude(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "1";
    final var source = new RelevantLocation();
    assertCanUpdate(target, source, vertx, testContext, updated -> {
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
   * @see RelevantLocation#update(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldUpdateOnlyLongitude(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "1";
    final var source = new RelevantLocation();
    source.latitude = target.latitude;
    source.longitude = 0d;
    assertCanUpdate(target, source, vertx, testContext, updated -> {
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
   * @see RelevantLocation#update(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldUpdateOnlyLatitude(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "1";
    final var source = new RelevantLocation();
    source.longitude = target.longitude;
    source.latitude = 0d;
    assertCanUpdate(target, source, vertx, testContext, updated -> {
      assertThat(updated).isNotEqualTo(target).isNotEqualTo(source);
      source.id = target.id;
      assertThat(updated).isEqualTo(source);
    });
  }
}
