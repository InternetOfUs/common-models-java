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

import eu.internetofus.common.model.ModelTestCase;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

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
  public void shouldNotBeValidWithABadName(final String name, final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.name = name;
    assertIsNotValid(model, "name", vertx, testContext);

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
  public void shouldNotBeValidWithABadClassification(final String classification, final Vertx vertx,
      final VertxTestContext testContext) {

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
  public void shouldNotBeValidWithABadQuantity(final Integer quantity, final Vertx vertx,
      final VertxTestContext testContext) {

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
   * Check that cannot merge a material with a bad quantity.
   *
   * @param quantity    that is invalid.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @ParameterizedTest(name = "Should not be merged with the quantity {0}")
  @ValueSource(ints = { -1, 0 })
  public void shouldCannotMergeWithABadQuantity(final Integer quantity, final Vertx vertx,
      final VertxTestContext testContext) {

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
    source.name = null;
    final var target = this.createModelExample(1);
    assertCannotUpdate(target, source, "name", vertx, testContext);

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
  public void shouldCannotUpdateWithABadQuantity(final Integer quantity, final Vertx vertx,
      final VertxTestContext testContext) {

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
