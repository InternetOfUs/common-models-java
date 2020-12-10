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
import static eu.internetofus.common.components.UpdatesTest.assertCanUpdate;
import static eu.internetofus.common.components.ValidationsTest.assertIsNotValid;
import static eu.internetofus.common.components.ValidationsTest.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import eu.internetofus.common.components.ModelTestCase;
import eu.internetofus.common.components.profile_manager.WeNetUserProfile;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link ProtocolNorm}.
 *
 * @see ProtocolNorm
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class ProtocolNormTest extends ModelTestCase<ProtocolNorm> {

  /**
   * {@inheritDoc}
   */
  @Override
  public ProtocolNorm createModelExample(final int index) {

    final var model = new ProtocolNorm();
    model.whenever = "whenever" + index;
    model.thenceforth = "thenceforth_" + index;
    model.ontology = "ontology" + index;
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
   * @see TaskType#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(index);
    assertIsValid(model, vertx, testContext);

  }

  /**
   * Check that the model is not valid without whenever.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithoutWhenever(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.whenever = null;
    assertIsNotValid(model, "whenever", vertx, testContext);

  }

  /**
   * Check that the model is not valid without thenceforth.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithoutThenceforth(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.thenceforth = null;
    assertIsNotValid(model, "thenceforth", vertx, testContext);

  }

  /**
   * Check that merge two models.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskType#merge(TaskType, String, Vertx)
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
   * @see TaskType#merge(TaskType, String, Vertx)
   */
  @Test
  public void shouldMergeWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanMerge(target, null, vertx, testContext, merged -> assertThat(merged).isSameAs(target));

  }

  /**
   * Check that update two models.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskType#update(TaskType, String, Vertx)
   */
  @Test
  public void shouldUpdate(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = this.createModelExample(23);
    assertCanUpdate(target, source, vertx, testContext, updated -> assertThat(updated).isNotEqualTo(target).isEqualTo(source));

  }

  /**
   * Check that update with {@code null}.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see TaskType#update(TaskType, String, Vertx)
   */
  @Test
  public void shouldUpdateWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanUpdate(target, null, vertx, testContext, updated -> assertThat(updated).isSameAs(target));

  }

}
