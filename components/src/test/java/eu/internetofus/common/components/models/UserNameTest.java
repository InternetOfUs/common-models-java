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
import static eu.internetofus.common.model.ValidableAsserts.assertIsValid;
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
   * Check that the name is valid with prefix with spaces.
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
   * Check that the name is valid with a first with spaces.
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
   * Check that the name is valid with middle with spaces.
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
   * Check that the name is valid with a last with spaces.
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
   * Check that the name is valid if has a suffix wir¡th spaces.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserName#validate(String, Vertx)
   */
  @Test
  public void shouldBeValidASuffixWithSpaces(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new UserName();
    model.suffix = "   12345  67890   ";
    assertIsValid(model, vertx, testContext, () -> assertThat(model.suffix).isEqualTo("12345  67890"));

  }

  /**
   * Check that the name can be merged with prefix with spaces.
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
    source.prefix = "   123 4567 890   ";
    assertCanMerge(target, source, vertx, testContext, merged -> assertThat(merged.prefix).isEqualTo("123 4567 890"));

  }

  /**
   * Check that the name can merge first with spaces.
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
    assertCanMerge(target, source, vertx, testContext,
        merged -> assertThat(merged.first).isEqualTo("First name 1234567890"));

  }

  /**
   * Check that the name can merge with middle with spaces.
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
    assertCanMerge(target, source, vertx, testContext,
        merged -> assertThat(merged.middle).isEqualTo("Middle name 1234567890"));

  }

  /**
   * Check that the name can merge with last with spaces.
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
    assertCanMerge(target, source, vertx, testContext,
        merged -> assertThat(merged.last).isEqualTo("Last name 1234567890"));

  }

  /**
   * Check that the name can merge a suffix with spaces.
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
    source.suffix = "   12  345678 90   ";
    assertCanMerge(target, source, vertx, testContext, merged -> assertThat(merged.suffix).isEqualTo("12  345678 90"));

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
    assertCanMerge(target, source, vertx, testContext,
        merged -> assertThat(merged).isNotEqualTo(target).isEqualTo(source));

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
