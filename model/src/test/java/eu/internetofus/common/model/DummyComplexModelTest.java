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
import static eu.internetofus.common.model.UpdateAsserts.assertCannotUpdate;
import static eu.internetofus.common.model.ValidableAsserts.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test the {@link DummyComplexModel}
 *
 * @see DummyComplexModel
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class DummyComplexModelTest extends ModelTestCase<DummyComplexModel> {

  /**
   * {@inheritDoc}
   */
  @Override
  public DummyComplexModel createModelExample(final int index) {

    final var model = new DummyComplexModel();
    model.id = "Id_" + index;
    model.index = index;
    if (index % 2 == 0) {

      model.siblings = new ArrayList<>();
      model.siblings.add(this.createModelExample(index - 1));
      model.siblings.add(this.createModelExample(index + 1));
    }
    return model;
  }

  /**
   * Check that the {@link #createModelExample(int)} is not valid.
   *
   * @param index       to verify
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see DummyComplexModel#validate( DummyValidateContext)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(index);
    assertIsValid(model, new DummyValidateContext("codePrefix"), testContext);

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
    assertCanMerge(target, null, new DummyValidateContext("codePrefix"), testContext, merged -> {
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
    final var source = this.createModelExample(2);
    assertCanMerge(target, source, new DummyValidateContext("codePrefix"), testContext, merged -> {
      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.index = source.index;
      target.siblings = source.siblings;
      assertThat(merged).isEqualTo(target);
    });

  }

  /**
   * Should not merge with a bad sibling.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see DummyComplexModel#merge(DummyComplexModel, DummyValidateContext)
   */
  @Test
  public void shouldNotMergeWithBadSibling(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new DummyComplexModel();
    source.siblings = new ArrayList<>();
    source.siblings.add(new DummyComplexModel());
    source.siblings.get(0).id = "0";
    source.siblings.add(new DummyComplexModel());
    source.siblings.get(1).id = "0";
    assertCannotMerge(target, source, "siblings[1]", new DummyValidateContext("codePrefix"), testContext);

  }

  /**
   * Should not update with a bad sibling.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see DummyComplexModel#update(DummyComplexModel, DummyValidateContext)
   */
  @Test
  public void shouldNotUpdateWithBadSibling(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new DummyComplexModel();
    source.siblings = new ArrayList<>();
    source.siblings.add(new DummyComplexModel());
    source.siblings.get(0).id = "0";
    source.siblings.add(new DummyComplexModel());
    source.siblings.get(1).id = "0";
    assertCannotUpdate(target, source, "siblings[1]", new DummyValidateContext("codePrefix"), testContext);

  }

  /**
   * Should to convert to buffer with empty values.
   *
   * @see DummyComplexModel#toBufferWithEmptyValues()
   */
  @Override
  @Test
  public void shouldToBufferWithEmptyValues() {

    final var source = new DummyComplexModel();
    source.siblings = new ArrayList<>();
    source.siblings.add(new DummyComplexModel());
    final var expected = new JsonObject().put("index", 0).putNull("id")
        .put("siblings",
            new JsonArray().add(new JsonObject().put("index", 0).putNull("id").putNull("siblings").putNull("other")))
        .putNull("other");
    final var target = new JsonObject(source.toBufferWithEmptyValues());
    assertThat(target).isEqualTo(expected);
    assertThat(target.fieldNames()).isEqualTo(expected.fieldNames());
    assertThat(target.getJsonArray("siblings").getJsonObject(0).fieldNames())
        .isEqualTo(expected.getJsonArray("siblings").getJsonObject(0).fieldNames());
    super.shouldToBufferWithEmptyValues();
  }

}
