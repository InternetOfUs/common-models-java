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

import eu.internetofus.common.components.ModelTestCase;
import eu.internetofus.common.components.ValidationsTest;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link Language}.
 *
 * @see Language
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class LanguageTest extends ModelTestCase<Language> {

  /**
   * {@inheritDoc}
   */
  @Override
  public Language createModelExample(final int index) {

    final var name = new Language();
    name.code = "ca";
    name.name = "name_" + index;
    name.level = LanguageLevel.values()[index % LanguageLevel.values().length];
    return name;
  }

  /**
   * Check that the {@link #createModelExample(int)} is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Language#validate(String, Vertx)
   */
  @Test
  public void shouldExample1BeValid(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new Language();
    model.code = "  ca   ";
    model.name = "   name_1     ";
    model.level = LanguageLevel.C1;

    assertIsValid(model, vertx, testContext, () -> {

      final var expected = this.createModelExample(1);
      assertThat(model).isEqualTo(expected);
    });

  }

  /**
   * Check that the name is not valid if has a large code.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Language#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithALargeCode(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new Language();
    model.code = "cat";
    assertIsNotValid(model, "code", vertx, testContext);

  }

  /**
   * Check that the name is not valid if has a large name.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Language#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithALargeName(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new Language();
    model.name = ValidationsTest.STRING_256;
    assertIsNotValid(model, "name", vertx, testContext);

  }

  /**
   * Check that merge two models.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Language#merge(Language, String, Vertx)
   */
  @Test
  public void shouldMerge(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = this.createModelExample(23);
    assertCanMerge(target, source, vertx, testContext, merged -> assertThat(merged).isNotEqualTo(target).isEqualTo(source));

  }

  /**
   * Check that merge with {@code null}.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Language#merge(Language, String, Vertx)
   */
  @Test
  public void shouldMergeWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanMerge(target, null, vertx, testContext, merged -> assertThat(merged).isSameAs(target));

  }

  /**
   * Check that merge only the name.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Language#merge(Language, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyName(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new Language();
    source.name = "NEW VALUE";
    assertCanMerge(target, source, vertx, testContext, merged -> {

      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.name = "NEW VALUE";
      assertThat(merged).isEqualTo(target);

    });
  }

  /**
   * Check that not merge with a bad name.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Language#merge(Language, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithBadName(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new Language();
    source.name = ValidationsTest.STRING_256;
    assertCannotMerge(target, source, "name", vertx, testContext);

  }

  /**
   * Check that merge only the code.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Language#merge(Language, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyCode(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new Language();
    source.code = "en";
    assertCanMerge(target, source, vertx, testContext, merged -> {

      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.code = "en";
      assertThat(merged).isEqualTo(target);

    });

  }

  /**
   * Check that not merge with a bad code.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Language#merge(Language, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithBadCode(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new Language();
    source.code = "english";
    assertCannotMerge(target, source, "code", vertx, testContext);

  }

  /**
   * Check that merge only the level.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Language#merge(Language, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyLevel(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new Language();
    source.level = LanguageLevel.B1;
    assertCanMerge(target, source, vertx, testContext, merged -> {

      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.level = LanguageLevel.B1;
      assertThat(merged).isEqualTo(target);

    });

  }

}
