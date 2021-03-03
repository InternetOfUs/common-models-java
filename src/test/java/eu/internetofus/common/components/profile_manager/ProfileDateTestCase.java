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
import static eu.internetofus.common.components.ValidationsTest.assertIsNotValid;
import static eu.internetofus.common.components.ValidationsTest.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import eu.internetofus.common.components.ModelTestCase;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Generic test of teh class that extends {@link ProfileDate}.
 *
 * @param <T> type of profile date to test.
 *
 * @see ProfileDate
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public abstract class ProfileDateTestCase<T extends ProfileDate> extends ModelTestCase<T> {

  /**
   * {@inheritDoc}
   */
  @Override
  public T createModelExample(final int index) {

    final var model = this.createEmptyModel();
    model.year = 1910 + index % 100;
    model.month = (byte) (1 + index % 11);
    model.day = (byte) (1 + index % 27);
    return model;
  }

  /**
   * Creates an empty model for the testing.
   *
   * @return the empty model.
   */
  public abstract T createEmptyModel();

  /**
   * Should empty model be valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @Test
  public void shouldEmptyModelBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final ProfileDate model = this.createEmptyModel();
    assertIsValid(model, vertx, testContext);

  }

  /**
   * Should not be valid with a bad month.
   *
   * @param month       that is not valid.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @ParameterizedTest(name = "Should not be valid a date with the value {0} as month.")
  @ValueSource(bytes = { 0, 13, -1, 100 })
  public void shouldNotBeValidMonth(final byte month, final Vertx vertx, final VertxTestContext testContext) {

    final ProfileDate model = this.createEmptyModel();
    model.year = 2020;
    model.month = month;
    model.day = 2;
    assertIsNotValid(model, "month", vertx, testContext);

  }

  /**
   * Should not be valid with a bad day.
   *
   * @param day         that is not valid.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @ParameterizedTest(name = "Should not be valid a date with the value {0} as day.")
  @ValueSource(bytes = { 0, 32, -1, 100 })
  public void shouldNotBeValidDay(final byte day, final Vertx vertx, final VertxTestContext testContext) {

    final ProfileDate model = this.createEmptyModel();
    model.year = 2020;
    model.month = 4;
    model.day = day;
    assertIsNotValid(model, "day", vertx, testContext);

  }

  /**
   * Should not be valid with a bad day.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @Test
  public void shouldNotBeValidAnImposibleDate(final Vertx vertx, final VertxTestContext testContext) {

    final ProfileDate model = this.createEmptyModel();
    model.year = 2020;
    model.month = 2;
    model.day = 31;
    assertIsNotValid(model, vertx, testContext);

  }

  /**
   * Should be valid without year.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @Test
  public void shouldBeValidWithoutYear(final Vertx vertx, final VertxTestContext testContext) {

    final ProfileDate model = this.createEmptyModel();
    model.month = 2;
    model.day = 31;
    assertIsValid(model, vertx, testContext);

  }

  /**
   * Should be valid without month.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @Test
  public void shouldBeValidWithoutMonth(final Vertx vertx, final VertxTestContext testContext) {

    final ProfileDate model = this.createEmptyModel();
    model.year = 2020;
    model.day = 31;
    assertIsValid(model, vertx, testContext);

  }

  /**
   * Should be valid without day.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @Test
  public void shouldBeValidWithoutDay(final Vertx vertx, final VertxTestContext testContext) {

    final ProfileDate model = this.createEmptyModel();
    model.year = 2020;
    model.month = 2;
    assertIsValid(model, vertx, testContext);

  }

  /**
   * Should not merge with a bad month.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @param month       that is not valid.
   */
  @ParameterizedTest(name = "Should not merge a date with the value {0} as month.")
  @ValueSource(bytes = { 0, 13, -1, 100 })
  public void shouldNotMergeMonth(final byte month, final Vertx vertx, final VertxTestContext testContext) {

    final ProfileDate target = this.createEmptyModel();
    final ProfileDate source = this.createEmptyModel();
    source.year = 2020;
    source.month = month;
    source.day = 2;
    assertCannotMerge(target, source, "month", vertx, testContext);

  }

  /**
   * Should not merge with a bad day.
   *
   * @param day         that is not valid.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @ParameterizedTest(name = "Should not merge a date with the value {0} as day.")
  @ValueSource(bytes = { 0, 32, -1, 100 })
  public void shouldNotMergeDay(final byte day, final Vertx vertx, final VertxTestContext testContext) {

    final ProfileDate target = this.createEmptyModel();
    final ProfileDate source = this.createEmptyModel();
    source.year = 2020;
    source.month = 4;
    source.day = day;
    assertCannotMerge(target, source, "day", vertx, testContext);

  }

  /**
   * Should not merge with a bad day.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @Test
  public void shouldNotMergeAnImposibleDate(final Vertx vertx, final VertxTestContext testContext) {

    final ProfileDate target = this.createEmptyModel();
    final ProfileDate source = this.createEmptyModel();
    source.year = 2020;
    source.month = 2;
    source.day = 31;
    assertCannotMerge(target, source, vertx, testContext);

  }

  /**
   * Should only merge the year.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see ProfileDate#merge(ProfileDate, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyYear(final Vertx vertx, final VertxTestContext testContext) {

    final ProfileDate target = this.createModelExample(1);
    final ProfileDate source = this.createEmptyModel();
    source.year = target.year - 1;
    assertCanMerge(target, source, vertx, testContext, merged -> {

      assertThat(merged).isNotEqualTo(target);
      target.year--;
      assertThat(merged).isEqualTo(target);

    });

  }

  /**
   * Should only merge the month.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see ProfileDate#merge(ProfileDate, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyMonth(final Vertx vertx, final VertxTestContext testContext) {

    final ProfileDate target = this.createModelExample(1);
    final ProfileDate source = this.createEmptyModel();
    source.month = (byte) (target.month - 1);
    assertCanMerge(target, source, vertx, testContext, merged -> {

      assertThat(merged).isNotEqualTo(target);
      target.month--;
      assertThat(merged).isEqualTo(target);

    });

  }

  /**
   * Should only merge the day.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see ProfileDate#merge(ProfileDate, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyDay(final Vertx vertx, final VertxTestContext testContext) {

    final ProfileDate target = this.createModelExample(1);
    final ProfileDate source = this.createEmptyModel();
    source.day = (byte) (target.day - 1);
    assertCanMerge(target, source, vertx, testContext, merged -> {

      assertThat(merged).isNotEqualTo(target);
      target.day--;
      assertThat(merged).isEqualTo(target);

    });

  }

}
