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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test the {@link Competence}.
 *
 * @see Competence
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class CompetenceTest extends ModelTestCase<Competence> {

  /**
   * {@inheritDoc}
   */
  @Override
  public Competence createModelExample(final int index) {

    final var model = new Competence();
    model.name = "name_" + index;
    model.ontology = "ontology_" + index;
    model.level = 1.0 / Math.max(1, index);
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

    final var model = new Competence();
    model.name = "    name_" + index + "    ";
    model.ontology = "    ontology_" + index + "    ";
    model.level = 1.0 / Math.max(1, index);

    assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

      final var expected = this.createModelExample(index);
      assertThat(model).isEqualTo(expected);
    });

  }

  /**
   * Check that the competence is not valid if has a bad name.
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
    assertIsNotValid(model, "name", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that the competence is not valid if has a bad ontology.
   *
   * @param ontology    that is invalid.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @ParameterizedTest(name = "Should not be valid with the ontology {0}")
  @NullSource
  public void shouldNotBeValidWithABadOntology(final String ontology, final Vertx vertx,
      final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.ontology = ontology;
    assertIsNotValid(model, "ontology", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that the competence is not valid if has a bad level.
   *
   * @param level       that is invalid.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @ParameterizedTest(name = "Should not be valid with the level {0}")
  @NullSource
  @ValueSource(doubles = { -1, -0.1, 1.1, 2 })
  public void shouldNotBeValidWithABadLevel(final Double level, final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.level = level;
    assertIsNotValid(model, "level", new WeNetValidateContext("codePrefix", vertx), testContext);

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

    final var source = new Competence();
    source.name = "    name_" + index + "    ";
    source.ontology = "    ontology_" + index + "    ";
    source.level = 1.0 / Math.max(1, index);

    final var target = this.createModelExample(index - 1);

    assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

      final var expected = this.createModelExample(index);
      assertThat(merged).isEqualTo(expected);
    });

  }

  /**
   * Check that cannot merge a competence with a bad level.
   *
   * @param level       that is invalid.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @ParameterizedTest(name = "Should not be merged with the level {0}")
  @ValueSource(doubles = { -1, -0.1, 1.1, 2 })
  public void shouldCannotMergeWithABadLevel(final Double level, final Vertx vertx,
      final VertxTestContext testContext) {

    final var source = new Competence();
    source.level = level;
    final var target = this.createModelExample(1);
    assertCannotMerge(target, source, "level", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Should merge with {@code null}
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#merge(CommunityProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanMerge(target, null, new WeNetValidateContext("codePrefix", vertx), testContext,
        merged -> assertThat(merged).isSameAs(target));

  }

  /**
   * Should merge with empty competence.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#merge(CommunityProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeWithEmpty(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanMerge(target, new Competence(), new WeNetValidateContext("codePrefix", vertx), testContext,
        merged -> assertThat(merged).isNotSameAs(target).isEqualTo(target));

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

    final var source = new Competence();
    source.name = "    name_" + index + "    ";
    source.ontology = "    ontology_" + index + "    ";
    source.level = 1.0 / Math.max(1, index);

    final var target = this.createModelExample(index - 1);

    assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {

      final var expected = this.createModelExample(index);
      assertThat(updated).isEqualTo(expected);
    });

  }

  /**
   * Check that cannot update a competence with a bad name.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @Test
  public void shouldCannotUpdateWithABadName(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new Competence();
    source.name = null;
    final var target = this.createModelExample(1);
    assertCannotUpdate(target, source, "name", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that cannot update a competence with a bad ontology.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @Test
  public void shouldCannotUpdateWithABadOntology(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new Competence();
    source.name = "name";
    source.ontology = null;
    final var target = this.createModelExample(1);
    assertCannotUpdate(target, source, "ontology", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that cannot update a competence with a bad level.
   *
   * @param level       that is invalid.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @ParameterizedTest(name = "Should not be updated with the level {0}")
  @ValueSource(doubles = { -1, -0.1, 1.1, 2 })
  public void shouldCannotUpdateWithABadLevel(final Double level, final Vertx vertx,
      final VertxTestContext testContext) {

    final var source = new Competence();
    source.name = "name";
    source.ontology = "ontology";
    source.level = level;
    final var target = this.createModelExample(1);
    assertCannotUpdate(target, source, "level", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Should update with {@code null}
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#update(CommunityProfile, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanUpdate(target, null, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {
      assertThat(updated).isSameAs(target);
    });

  }

}
