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

    assertIsValid(model, vertx, testContext, () -> {

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
  @ValueSource(strings = { ValidationsTest.STRING_256 })
  public void shouldNotBeValidWithABadName(final String name, final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.name = name;
    assertIsNotValid(model, "name", vertx, testContext);

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
  @ValueSource(strings = { ValidationsTest.STRING_256 })
  public void shouldNotBeValidWithABadOntology(final String ontology, final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.ontology = ontology;
    assertIsNotValid(model, "ontology", vertx, testContext);

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

    final var source = new Competence();
    source.name = "    name_" + index + "    ";
    source.ontology = "    ontology_" + index + "    ";
    source.level = 1.0 / Math.max(1, index);

    final var target = this.createModelExample(index - 1);

    assertCanMerge(target, source, vertx, testContext, merged -> {

      final var expected = this.createModelExample(index);
      assertThat(merged).isEqualTo(expected);
    });

  }

  /**
   * Check that cannot merge a competence with a bad name.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @Test
  public void shouldCannotMergeWithABadName(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new Competence();
    source.name = ValidationsTest.STRING_256;
    final var target = this.createModelExample(1);
    assertCannotMerge(target, source, "name", vertx, testContext);

  }

  /**
   * Check that cannot merge a competence with a bad ontology.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  @Test
  public void shouldCannotMergeWithABadOntology(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new Competence();
    source.ontology = ValidationsTest.STRING_256;
    final var target = this.createModelExample(1);
    assertCannotMerge(target, source, "ontology", vertx, testContext);

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
  public void shouldCannotMergeWithABadLevel(final Double level, final Vertx vertx, final VertxTestContext testContext) {

    final var source = new Competence();
    source.level = level;
    final var target = this.createModelExample(1);
    assertCannotMerge(target, source, "level", vertx, testContext);

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
  public void shoudMergeWithNull(final Vertx vertx, final VertxTestContext testContext) {

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

    final var source = new Competence();
    source.name = "    name_" + index + "    ";
    source.ontology = "    ontology_" + index + "    ";
    source.level = 1.0 / Math.max(1, index);

    final var target = this.createModelExample(index - 1);

    assertCanUpdate(target, source, vertx, testContext, updated -> {

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
    source.name = ValidationsTest.STRING_256;
    final var target = this.createModelExample(1);
    assertCannotUpdate(target, source, "name", vertx, testContext);

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
    source.ontology = ValidationsTest.STRING_256;
    final var target = this.createModelExample(1);
    assertCannotUpdate(target, source, "ontology", vertx, testContext);

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
  public void shouldCannotUpdateWithABadLevel(final Double level, final Vertx vertx, final VertxTestContext testContext) {

    final var source = new Competence();
    source.name = "name";
    source.ontology = "ontology";
    source.level = level;
    final var target = this.createModelExample(1);
    assertCannotUpdate(target, source, "level", vertx, testContext);

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
  public void shoudUpdateWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanUpdate(target, null, vertx, testContext, updated -> {
      assertThat(updated).isSameAs(target);
    });

  }

}
