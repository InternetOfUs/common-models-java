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

package eu.internetofus.common.model;

import static eu.internetofus.common.model.MergeAsserts.assertCanMerge;
import static eu.internetofus.common.model.MergeAsserts.assertCannotMerge;
import static eu.internetofus.common.model.UpdateAsserts.assertCanUpdate;
import static eu.internetofus.common.model.UpdateAsserts.assertCannotUpdate;
import static eu.internetofus.common.model.ValidableAsserts.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test the {@link DummyTsModel}
 *
 * @see DummyTsModel
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class DummyTsModelTest extends ModelTestCase<DummyTsModel> {

  /**
   * {@inheritDoc}
   */
  @Override
  public DummyTsModel createModelExample(final int index) {

    final var model = new DummyTsModel();
    model.value = "Value_" + index;
    model.dummies = new ArrayList<>();
    final var child = new DummyTsModel();
    model._id = "0";
    model.value = "Child_" + index;
    model.dummies.add(child);
    return model;
  }

  /**
   * Check that the {@link #createModelExample(int)} is not valid.
   *
   * @param index       to verify
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see DummyComplexModel#validate(DummyValidateContext)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(index);
    final var context = new DummyValidateContext("codePrefix");
    assertIsValid(model, context, testContext);

  }

  /**
   * Should merge with {@code null}
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   */
  @Test
  public void shouldMergeWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var context = new DummyValidateContext("codePrefix");
    assertCanMerge(target, null, context, testContext, merged -> {
      assertThat(merged).isSameAs(target);
    });

  }

  /**
   * Should merge two examples.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see DummyComplexModel#merge(DummyComplexModel, DummyValidateContext)
   */
  @Test
  public void shouldMergeExamples(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target._creationTs = 10000;
    target._lastUpdateTs = 1000000;
    final var source = this.createModelExample(2);
    final var context = new DummyValidateContext("codePrefix");
    assertCanMerge(target, source, context, testContext, merged -> {
      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      source._id = target._id;
      source.value = target.value;
      source._creationTs = target._creationTs;
      source._lastUpdateTs = merged._lastUpdateTs;
      assertThat(merged).isEqualTo(source);
    });

  }

  /**
   * Should not merge with a duplicated dummy.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see DummyComplexModel#merge(DummyComplexModel, DummyValidateContext)
   */
  @Test
  public void shouldNotMergeWithDuplicatedDummy(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = this.createModelExample(2);
    source.dummies.add(source.dummies.get(0));
    final var context = new DummyValidateContext("codePrefix");
    assertCannotMerge(target, source, "dummies[1]", context, testContext);

  }

  /**
   * Should update with {@code null}
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   */
  @Test
  public void shouldUpdateWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var context = new DummyValidateContext("codePrefix");
    assertCanUpdate(target, null, context, testContext, updated -> {
      assertThat(updated).isSameAs(target);
    });

  }

  /**
   * Should update two examples.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see DummyComplexModel#update(DummyComplexModel, DummyValidateContext)
   */
  @Test
  public void shouldUpdateExamples(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target._creationTs = 10000;
    target._lastUpdateTs = 1000000;
    final var source = this.createModelExample(2);
    final var context = new DummyValidateContext("codePrefix");
    assertCanUpdate(target, source, context, testContext, updated -> {
      assertThat(updated).isNotEqualTo(target).isNotEqualTo(source);
      source._id = target._id;
      source._creationTs = target._creationTs;
      source._lastUpdateTs = updated._lastUpdateTs;
      assertThat(updated).isEqualTo(source);
    });

  }

  /**
   * Should not update with a duplicated dummy.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see DummyComplexModel#update(DummyComplexModel, DummyValidateContext)
   */
  @Test
  public void shouldNotUpdateWithDuplicatedDummy(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = this.createModelExample(2);
    source.dummies.add(source.dummies.get(0));
    final var context = new DummyValidateContext("codePrefix");
    assertCannotUpdate(target, source, "dummies[1]", context, testContext);

  }

}
