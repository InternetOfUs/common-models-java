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
 * Test the {@link UserName}.
 *
 * @see UserName
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class UserNameTest extends ModelTestCase<UserName> {

  /**
   * {@inheritDoc}
   */
  @Override
  public UserName createModelExample(final int index) {

    final var name = new UserName();
    name.prefix = "prefix_" + index;
    name.first = "first_" + index;
    name.middle = "middle_" + index;
    name.last = "last_" + index;
    name.suffix = "suffix_" + index;
    return name;
  }

  /**
   * Check that the {@link #createModelExample(int)} is valid.
   *
   * @param index       to verify
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserName#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(index);
    assertIsValid(model, vertx, testContext);

  }

  /**
   * Check that the name is not valid if has a large prefix.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserName#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithALargePrefix(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new UserName();
    model.prefix = "12345678901";
    assertIsNotValid(model, "prefix", vertx, testContext);

  }

  /**
   * Check that the name is not valid if has a large prefix.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserName#validate(String, Vertx)
   */
  @Test
  public void shouldBeValidAPrefixWithSpaces(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new UserName();
    model.prefix = "   1234567890   ";
    assertIsValid(model, vertx, testContext, () -> assertThat(model.prefix).isEqualTo("1234567890"));

  }

  /**
   * Check that the name is not valid if has a large first.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserName#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithALargeFirst(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new UserName();
    model.first = ValidationsTest.STRING_256;
    assertIsNotValid(model, "first", vertx, testContext);

  }

  /**
   * Check that the name is not valid if has a large first.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserName#validate(String, Vertx)
   */
  @Test
  public void shouldBeValidAFirstWithSpaces(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new UserName();
    model.first = "   First name 1234567890   ";
    assertIsValid(model, vertx, testContext, () -> assertThat(model.first).isEqualTo("First name 1234567890"));

  }

  /**
   * Check that the name is not valid if has a large middle.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserName#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithALargeMiddle(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new UserName();
    model.middle = ValidationsTest.STRING_256;
    assertIsNotValid(model, "middle", vertx, testContext);

  }

  /**
   * Check that the name is not valid if has a large middle.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserName#validate(String, Vertx)
   */
  @Test
  public void shouldBeValidAMiddleWithSpaces(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new UserName();
    model.middle = "   Middle name 1234567890   ";
    assertIsValid(model, vertx, testContext, () -> assertThat(model.middle).isEqualTo("Middle name 1234567890"));

  }

  /**
   * Check that the name is not valid if has a large last.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserName#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithALargeLast(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new UserName();
    model.last = ValidationsTest.STRING_256;
    assertIsNotValid(model, "last", vertx, testContext);

  }

  /**
   * Check that the name is not valid if has a large last.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserName#validate(String, Vertx)
   */
  @Test
  public void shouldBeValidALastWithSpaces(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new UserName();
    model.last = "   Last name 1234567890   ";
    assertIsValid(model, vertx, testContext, () -> assertThat(model.last).isEqualTo("Last name 1234567890"));

  }

  /**
   * Check that the name is not valid if has a large suffix.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserName#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithALargeSuffix(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new UserName();
    model.suffix = "12345678901";
    assertIsNotValid(model, "suffix", vertx, testContext);

  }

  /**
   * Check that the name is not valid if has a large suffix.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserName#validate(String, Vertx)
   */
  @Test
  public void shouldBeValidASuffixWithSpaces(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new UserName();
    model.suffix = "   1234567890   ";
    assertIsValid(model, vertx, testContext, () -> assertThat(model.suffix).isEqualTo("1234567890"));

  }

  /**
   * Check that the name is not merge if has a large prefix.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserName#merge(UserName, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithALargePrefix(final Vertx vertx, final VertxTestContext testContext) {

    final var target = new UserName();
    final var source = new UserName();
    source.prefix = "12345678901";
    assertCannotMerge(target, source, "prefix", vertx, testContext);

  }

  /**
   * Check that the name is not merge if has a large prefix.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserName#merge(UserName, String, Vertx)
   */
  @Test
  public void shouldMergeAPrefixWithSpaces(final Vertx vertx, final VertxTestContext testContext) {

    final var target = new UserName();
    final var source = new UserName();
    source.prefix = "   1234567890   ";
    assertCanMerge(target, source, vertx, testContext, merged -> assertThat(merged.prefix).isEqualTo("1234567890"));

  }

  /**
   * Check that the name is not merge if has a large first.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserName#merge(UserName, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithALargeFirst(final Vertx vertx, final VertxTestContext testContext) {

    final var target = new UserName();
    final var source = new UserName();
    source.first = ValidationsTest.STRING_256;
    assertCannotMerge(target, source, "first", vertx, testContext);

  }

  /**
   * Check that the name is not merge if has a large first.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserName#merge(UserName, String, Vertx)
   */
  @Test
  public void shouldMergeAFirstWithSpaces(final Vertx vertx, final VertxTestContext testContext) {

    final var target = new UserName();
    final var source = new UserName();
    source.first = "   First name 1234567890   ";
    assertCanMerge(target, source, vertx, testContext, merged -> assertThat(merged.first).isEqualTo("First name 1234567890"));

  }

  /**
   * Check that the name is not merge if has a large middle.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserName#merge(UserName, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithALargeMiddle(final Vertx vertx, final VertxTestContext testContext) {

    final var target = new UserName();
    final var source = new UserName();
    source.middle = ValidationsTest.STRING_256;
    assertCannotMerge(target, source, "middle", vertx, testContext);

  }

  /**
   * Check that the name is not merge if has a large middle.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserName#merge(UserName, String, Vertx)
   */
  @Test
  public void shouldMergeAMiddleWithSpaces(final Vertx vertx, final VertxTestContext testContext) {

    final var target = new UserName();
    final var source = new UserName();
    source.middle = "   Middle name 1234567890   ";
    assertCanMerge(target, source, vertx, testContext, merged -> assertThat(merged.middle).isEqualTo("Middle name 1234567890"));

  }

  /**
   * Check that the name is not merge if has a large last.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserName#merge(UserName, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithALargeLast(final Vertx vertx, final VertxTestContext testContext) {

    final var target = new UserName();
    final var source = new UserName();
    source.last = ValidationsTest.STRING_256;
    assertCannotMerge(target, source, "last", vertx, testContext);

  }

  /**
   * Check that the name is not merge if has a large last.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserName#merge(UserName, String, Vertx)
   */
  @Test
  public void shouldMergeALastWithSpaces(final Vertx vertx, final VertxTestContext testContext) {

    final var target = new UserName();
    final var source = new UserName();
    source.last = "   Last name 1234567890   ";
    assertCanMerge(target, source, vertx, testContext, merged -> assertThat(merged.last).isEqualTo("Last name 1234567890"));

  }

  /**
   * Check that the name is not merge if has a large suffix.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserName#merge(UserName, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithALargeSuffix(final Vertx vertx, final VertxTestContext testContext) {

    final var target = new UserName();
    final var source = new UserName();
    source.suffix = "12345678901";
    assertCannotMerge(target, source, "suffix", vertx, testContext);

  }

  /**
   * Check that the name is not merge if has a large suffix.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserName#merge(UserName, String, Vertx)
   */
  @Test
  public void shouldMergeASuffixWithSpaces(final Vertx vertx, final VertxTestContext testContext) {

    final var target = new UserName();
    final var source = new UserName();
    source.suffix = "   1234567890   ";
    assertCanMerge(target, source, vertx, testContext, merged -> assertThat(merged.suffix).isEqualTo("1234567890"));

  }

  /**
   * Check that merge two models.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserName#merge(UserName, String, Vertx)
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
   * @see UserName#merge(UserName, String, Vertx)
   */
  @Test
  public void shouldMergeWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanMerge(target, null, vertx, testContext, merged -> assertThat(merged).isSameAs(target));

  }

  /**
   * Check that merge only the prefix.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserName#merge(UserName, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyPrefix(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new UserName();
    source.prefix = "NEW VALUE";
    assertCanMerge(target, source, vertx, testContext, merged -> {
      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.prefix = "NEW VALUE";
      assertThat(merged).isEqualTo(target);
    });
  }

  /**
   * Check that merge only the first.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserName#merge(UserName, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyFirst(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new UserName();
    source.first = "NEW VALUE";
    assertCanMerge(target, source, vertx, testContext, merged -> {
      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.first = "NEW VALUE";
      assertThat(merged).isEqualTo(target);
    });
  }

  /**
   * Check that merge only the middle.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserName#merge(UserName, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyMiddle(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new UserName();
    source.middle = "NEW VALUE";
    assertCanMerge(target, source, vertx, testContext, merged -> {
      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.middle = "NEW VALUE";
      assertThat(merged).isEqualTo(target);
    });
  }

  /**
   * Check that merge only the last.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserName#merge(UserName, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyLast(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new UserName();
    source.last = "NEW VALUE";
    assertCanMerge(target, source, vertx, testContext, merged -> {
      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.last = "NEW VALUE";
      assertThat(merged).isEqualTo(target);
    });
  }

  /**
   * Check that merge only the suffix.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserName#merge(UserName, String, Vertx)
   */
  @Test
  public void shouldMergeOnlySuffix(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new UserName();
    source.suffix = "NEW VALUE";
    assertCanMerge(target, source, vertx, testContext, merged -> {
      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.suffix = "NEW VALUE";
      assertThat(merged).isEqualTo(target);
    });
  }

}
