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
 * Test the {@link Material}.
 *
 * @see Material
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class MaterialTest extends ModelTestCase<Material> {

  /**
   * {@inheritDoc}
   */
  @Override
  public Material createModelExample(final int index) {

    final var model = new Material();
    model.name = "name_" + index;
    model.description = "description_" + index;
    model.classification = "classification_" + index;
    model.quantity = Math.max(1, index);
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

    final var model = new Material();
    model.name = "    name_" + index + "    ";
    model.description = "    description_" + index + "    ";
    model.classification = "    classification_" + index + "    ";
    model.quantity = Math.max(1, index);

    assertIsValid(model, vertx, testContext, () -> {

      final var expected = this.createModelExample(index);
      assertThat(model).isEqualTo(expected);
    });

  }

  /**
   * Check that the material is not valid if has a bad name.
   *
   * @param name        that is invalid.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
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
   * Check that the material is not valid if has a bad description.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @Test
  public void shouldNotBeValidWithABadDescription(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.description = ValidationsTest.STRING_1024;
    assertIsNotValid(model, "description", vertx, testContext);

  }

  /**
   * Check that the material is valid with a {@code null} description.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @Test
  public void shouldBeValidWithANullDescription(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.description = null;
    assertIsValid(model, vertx, testContext);

  }

  /**
   * Check that the material is not valid if has a bad classification.
   *
   * @param classification that is invalid.
   * @param vertx          event bus to use.
   * @param testContext    test context to use.
   */
  @ParameterizedTest(name = "Should not be valid with the classification {0}")
  @NullSource
  @ValueSource(strings = { ValidationsTest.STRING_256 })
  public void shouldNotBeValidWithABadClassification(final String classification, final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.classification = classification;
    assertIsNotValid(model, "classification", vertx, testContext);

  }

  /**
   * Check that the material is not valid if has a bad quantity.
   *
   * @param quantity    that is invalid.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @ParameterizedTest(name = "Should not be valid with the quantity {0}")
  @NullSource
  @ValueSource(ints = { -1, 0 })
  public void shouldNotBeValidWithABadQuantity(final Integer quantity, final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.quantity = quantity;
    assertIsNotValid(model, "quantity", vertx, testContext);

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

    final var source = new Material();
    source.name = "    name_" + index + "    ";
    source.description = "    description_" + index + "    ";
    source.classification = "    classification_" + index + "    ";
    source.quantity = Math.max(1, index);

    final var target = this.createModelExample(index - 1);

    assertCanMerge(target, source, vertx, testContext, merged -> {

      final var expected = this.createModelExample(index);
      assertThat(merged).isEqualTo(expected);
    });

  }

  /**
   * Check that cannot merge a material with a bad name.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @Test
  public void shouldCannotMergeWithABadName(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new Material();
    source.name = ValidationsTest.STRING_256;
    final var target = this.createModelExample(1);
    assertCannotMerge(target, source, "name", vertx, testContext);

  }

  /**
   * Check that cannot merge a material with a bad description.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @Test
  public void shouldCannotMergeWithABadDescription(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new Material();
    source.description = ValidationsTest.STRING_1024;
    final var target = this.createModelExample(1);
    assertCannotMerge(target, source, "description", vertx, testContext);

  }

  /**
   * Check that cannot merge a material with a bad classification.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @Test
  public void shouldCannotMergeWithABadClassification(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new Material();
    source.classification = ValidationsTest.STRING_256;
    final var target = this.createModelExample(1);
    assertCannotMerge(target, source, "classification", vertx, testContext);

  }

  /**
   * Check that cannot merge a material with a bad quantity.
   *
   * @param quantity    that is invalid.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @ParameterizedTest(name = "Should not be merged with the quantity {0}")
  @ValueSource(ints = { -1, 0 })
  public void shouldCannotMergeWithABadQuantity(final Integer quantity, final Vertx vertx, final VertxTestContext testContext) {

    final var source = new Material();
    source.quantity = quantity;
    final var target = this.createModelExample(1);
    assertCannotMerge(target, source, "quantity", vertx, testContext);

  }

  /**
   * Should merge with {@code null}
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#merge(CommunityProfile, String, Vertx)
   */
  @Test
  public void shouldMergeWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanMerge(target, null, vertx, testContext, merged -> {
      assertThat(merged).isSameAs(target);
    });

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

    final var source = new Material();
    source.name = "    name_" + index + "    ";
    source.description = "    description_" + index + "    ";
    source.classification = "    classification_" + index + "    ";
    source.quantity = Math.max(1, index);

    final var target = this.createModelExample(index - 1);

    assertCanUpdate(target, source, vertx, testContext, updated -> {

      final var expected = this.createModelExample(index);
      assertThat(updated).isEqualTo(expected);
    });

  }

  /**
   * Check that cannot update a material with a bad name.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @Test
  public void shouldCannotUpdateWithABadName(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new Material();
    source.name = ValidationsTest.STRING_256;
    final var target = this.createModelExample(1);
    assertCannotUpdate(target, source, "name", vertx, testContext);

  }

  /**
   * Check that cannot update a material with a bad description.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @Test
  public void shouldCannotUpdateWithABadDescription(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new Material();
    source.name = "name";
    source.description = ValidationsTest.STRING_1024;
    final var target = this.createModelExample(1);
    assertCannotUpdate(target, source, "description", vertx, testContext);

  }

  /**
   * Check that cannot update a material with a bad classification.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @Test
  public void shouldCannotUpdateWithABadClassification(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new Material();
    source.name = "name";
    source.quantity = 1;
    source.classification = ValidationsTest.STRING_256;
    final var target = this.createModelExample(1);
    assertCannotUpdate(target, source, "classification", vertx, testContext);

  }

  /**
   * Check that cannot update a material with a bad quantity.
   *
   * @param quantity    that is invalid.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @ParameterizedTest(name = "Should not be updated with the quantity {0}")
  @ValueSource(ints = { -1, 0 })
  public void shouldCannotUpdateWithABadQuantity(final Integer quantity, final Vertx vertx, final VertxTestContext testContext) {

    final var source = this.createModelExample(0);
    source.quantity = quantity;
    final var target = this.createModelExample(1);
    assertCannotUpdate(target, source, "quantity", vertx, testContext);

  }

  /**
   * Should update with {@code null}
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#update(CommunityProfile, String, Vertx)
   */
  @Test
  public void shouldUpdateWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanUpdate(target, null, vertx, testContext, updated -> {
      assertThat(updated).isSameAs(target);
    });

  }
}
