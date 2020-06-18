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
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
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

package eu.internetofus.common.components.task_manager;

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
 * Test the {@link TaskGoal}.
 *
 * @see TaskGoal
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class TaskGoalTest extends ModelTestCase<TaskGoal> {

  /**
   * {@inheritDoc}
   */
  @Override
  public TaskGoal createModelExample(final int index) {

    final TaskGoal model = new TaskGoal();
    model.name = "name_" + index;
    model.description = "description_" + index;
    return model;
  }

  /**
   * Check that the {@link #createModelExample(int)} is valid.
   *
   * @param index       to verify
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskGoal#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final TaskGoal model = this.createModelExample(index);
    assertIsValid(model, vertx, testContext);

  }

  /**
   * Check that the name is not valid if has a large name.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskGoal#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithALargeName(final Vertx vertx, final VertxTestContext testContext) {

    final TaskGoal model = new TaskGoal();
    model.name = ValidationsTest.STRING_256;
    assertIsNotValid(model, "name", vertx, testContext);

  }

  /**
   * Check that the name is not valid if has a large name.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskGoal#validate(String, Vertx)
   */
  @Test
  public void shouldBeValidANameWithSpaces(final Vertx vertx, final VertxTestContext testContext) {

    final TaskGoal model = new TaskGoal();
    model.name = "   1234567890   ";
    assertIsValid(model, vertx, testContext, () -> assertThat(model.name).isEqualTo("1234567890"));

  }

  /**
   * Check that the name is not valid if has a large description.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskGoal#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithALargeDescription(final Vertx vertx, final VertxTestContext testContext) {

    final TaskGoal model = new TaskGoal();
    model.description = ValidationsTest.STRING_1024;
    assertIsNotValid(model, "description", vertx, testContext);

  }

  /**
   * Check that the name is not valid if has a large description.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskGoal#validate(String, Vertx)
   */
  @Test
  public void shouldBeValidADescriptionWithSpaces(final Vertx vertx, final VertxTestContext testContext) {

    final TaskGoal model = new TaskGoal();
    model.description = "   Description name 1234567890   ";
    assertIsValid(model, vertx, testContext, () -> assertThat(model.description).isEqualTo("Description name 1234567890"));

  }

  /**
   * Check that the name is not merge if has a large name.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskGoal#merge(TaskGoal, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithALargeName(final Vertx vertx, final VertxTestContext testContext) {

    final TaskGoal target = new TaskGoal();
    final TaskGoal source = new TaskGoal();
    source.name = ValidationsTest.STRING_256;
    assertCannotMerge(target, source, "name", vertx, testContext);

  }

  /**
   * Check that the name is not merge if has a large name.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskGoal#merge(TaskGoal, String, Vertx)
   */
  @Test
  public void shouldMergeANameWithSpaces(final Vertx vertx, final VertxTestContext testContext) {

    final TaskGoal target = new TaskGoal();
    final TaskGoal source = new TaskGoal();
    source.name = "   1234567890   ";
    assertCanMerge(target, source, vertx, testContext, merged -> assertThat(merged.name).isEqualTo("1234567890"));

  }

  /**
   * Check that the name is not merge if has a large description.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskGoal#merge(TaskGoal, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithALargeDescription(final Vertx vertx, final VertxTestContext testContext) {

    final TaskGoal target = new TaskGoal();
    final TaskGoal source = new TaskGoal();
    source.description = ValidationsTest.STRING_1024;
    assertCannotMerge(target, source, "description", vertx, testContext);

  }

  /**
   * Check that the name is not merge if has a large description.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskGoal#merge(TaskGoal, String, Vertx)
   */
  @Test
  public void shouldMergeADescriptionWithSpaces(final Vertx vertx, final VertxTestContext testContext) {

    final TaskGoal target = new TaskGoal();
    final TaskGoal source = new TaskGoal();
    source.description = "   Description name 1234567890   ";
    assertCanMerge(target, source, vertx, testContext, merged -> assertThat(merged.description).isEqualTo("Description name 1234567890"));

  }

  /**
   * Check that merge two models.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskGoal#merge(TaskGoal, String, Vertx)
   */
  @Test
  public void shouldMerge(final Vertx vertx, final VertxTestContext testContext) {

    final TaskGoal target = this.createModelExample(1);
    final TaskGoal source = this.createModelExample(23);
    assertCanMerge(target, source, vertx, testContext, merged -> assertThat(merged).isNotEqualTo(target).isEqualTo(source));

  }

  /**
   * Check that merge with {@code null}.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskGoal#merge(TaskGoal, String, Vertx)
   */
  @Test
  public void shouldMergeWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final TaskGoal target = this.createModelExample(1);
    assertCanMerge(target, null, vertx, testContext, merged -> assertThat(merged).isSameAs(target));

  }

  /**
   * Check that merge only the name.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskGoal#merge(TaskGoal, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyName(final Vertx vertx, final VertxTestContext testContext) {

    final TaskGoal target = this.createModelExample(1);
    final TaskGoal source = new TaskGoal();
    source.name = "NEW NAME";
    assertCanMerge(target, source, vertx, testContext, merged -> {
      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.name = "NEW NAME";
      assertThat(merged).isEqualTo(target);
    });
  }

  /**
   * Check that merge only the description.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskGoal#merge(TaskGoal, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyDescription(final Vertx vertx, final VertxTestContext testContext) {

    final TaskGoal target = this.createModelExample(1);
    final TaskGoal source = new TaskGoal();
    source.description = "NEW DESCRIPTION";
    assertCanMerge(target, source, vertx, testContext, merged -> {
      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.description = "NEW DESCRIPTION";
      assertThat(merged).isEqualTo(target);
    });
  }

}
