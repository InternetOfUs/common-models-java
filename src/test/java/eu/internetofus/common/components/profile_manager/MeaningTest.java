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

import static eu.internetofus.common.components.MergesTest.assertCanMerge;
import static eu.internetofus.common.components.MergesTest.assertCannotMerge;
import static eu.internetofus.common.components.UpdatesTest.assertCanUpdate;
import static eu.internetofus.common.components.UpdatesTest.assertCannotUpdate;
import static eu.internetofus.common.components.ValidationsTest.assertIsNotValid;
import static eu.internetofus.common.components.ValidationsTest.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import eu.internetofus.common.components.ModelTestCase;
import eu.internetofus.common.components.ValidationsTest;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link Meaning}.
 *
 * @see Meaning
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class MeaningTest extends ModelTestCase<Meaning> {

  /**
   * {@inheritDoc}
   */
  @Override
  public Meaning createModelExample(final int index) {

    final var model = new Meaning();
    model.name = "name_" + index;
    model.category = "category_" + index;
    model.level = (double) index;
    return model;
  }

  /**
   * Check that the {@link #createModelExample(int)} is valid.
   *
   * @param index       of the example to test.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Language#validate(String, Vertx)
   */
  @ParameterizedTest(name = "Should be valid the example {0}")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final var model = new Meaning();
    model.name = "    name_" + index + "    ";
    model.category = "    category_" + index + "    ";
    model.level = (double) index;

    assertIsValid(model, vertx, testContext, () -> {

      final var expected = this.createModelExample(index);
      assertThat(model).isEqualTo(expected);
    });

  }

  /**
   * Check that the meaning is not valid if has a bad name.
   *
   * @param name        that is invalid.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Language#validate(String, Vertx)
   */
  @ParameterizedTest(name = "Should not be valid with the name {0}")
  @NullSource
  @ValueSource(strings = { ValidationsTest.STRING_256 })
  public void shouldNotBeValidWithABadName(final String name, final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.name = name;
    assertIsNotValid(model, "name", vertx, testContext);

  }

  /**
   * Check that the meaning is not valid if has a bad category.
   *
   * @param category    that is invalid.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Language#validate(String, Vertx)
   */
  @ParameterizedTest(name = "Should not be valid with the category {0}")
  @NullSource
  @ValueSource(strings = { ValidationsTest.STRING_256 })
  public void shouldNotBeValidWithABadCategory(final String category, final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.category = category;
    assertIsNotValid(model, "category", vertx, testContext);

  }

  /**
   * Check that the meaning is not valid if has a bad level.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Language#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadLevel(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.level = null;
    assertIsNotValid(model, "level", vertx, testContext);

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

    final var source = new Meaning();
    source.name = "    name_" + index + "    ";
    source.category = "    category_" + index + "    ";
    source.level = (double) index;

    final var target = this.createModelExample(index - 1);

    assertCanMerge(target, source, vertx, testContext, merged -> {

      final var expected = this.createModelExample(index);
      assertThat(merged).isEqualTo(expected);
    });

  }

  /**
   * Check that can merge will {@code null}.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @Test
  public void shouldMergeNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanMerge(target, null, vertx, testContext, merged -> {

      assertThat(merged).isSameAs(target);
    });

  }

  /**
   * Check that can not merge a meaning with a bad name.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Language#validate(String, Vertx)
   */
  @Test
  public void shouldCannotMergeWithABadName(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new Meaning();
    source.name = ValidationsTest.STRING_256;
    final var target = this.createModelExample(1);
    assertCannotMerge(target, source, "name", vertx, testContext);

  }

  /**
   * Check that can not merge a meaning with a bad category.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Language#validate(String, Vertx)
   */
  @Test
  public void shouldCannotMergeWithABadCategory(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new Meaning();
    source.category = ValidationsTest.STRING_256;
    final var target = this.createModelExample(1);
    assertCannotMerge(target, source, "category", vertx, testContext);

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

    final var source = new Meaning();
    source.name = "    name_" + index + "    ";
    source.category = "    category_" + index + "    ";
    source.level = (double) index;

    final var target = this.createModelExample(index - 1);

    assertCanUpdate(target, source, vertx, testContext, updated -> {

      final var expected = this.createModelExample(index);
      assertThat(updated).isEqualTo(expected);
    });

  }

  /**
   * Check that can update will {@code null}.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @Test
  public void shouldUpdateNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanUpdate(target, null, vertx, testContext, updated -> {

      assertThat(updated).isSameAs(target);
    });

  }

  /**
   * Check that can not update a meaning with a bad name.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Language#validate(String, Vertx)
   */
  @Test
  public void shouldCannotUpdateWithABadName(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new Meaning();
    source.name = ValidationsTest.STRING_256;
    final var target = this.createModelExample(1);
    assertCannotUpdate(target, source, "name", vertx, testContext);

  }

  /**
   * Check that can not update a meaning with a bad category.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Language#validate(String, Vertx)
   */
  @Test
  public void shouldCannotUpdateWithABadCategory(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new Meaning();
    source.name = "name";
    source.category = ValidationsTest.STRING_256;
    final var target = this.createModelExample(1);
    assertCannotUpdate(target, source, "category", vertx, testContext);

  }

}
