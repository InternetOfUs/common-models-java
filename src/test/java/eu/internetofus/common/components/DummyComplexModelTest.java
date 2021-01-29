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

package eu.internetofus.common.components;

import static eu.internetofus.common.components.MergesTest.assertCanMerge;
import static eu.internetofus.common.components.MergesTest.assertCannotMerge;
import static eu.internetofus.common.components.UpdatesTest.assertCannotUpdate;
import static eu.internetofus.common.components.ValidationsTest.assertIsNotValid;
import static eu.internetofus.common.components.ValidationsTest.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.profile_manager.CommunityProfile;
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
   * Check that an empty model is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see DummyComplexModel#validate(String, Vertx)
   */
  @Test
  public void shouldModelWithBadIdNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new DummyComplexModel();
    model.id = ValidationsTest.STRING_256;
    assertIsNotValid(model, "id", vertx, testContext);

  }

  /**
   * Check that the {@link #createModelExample(int)} is not valid.
   *
   * @param index       to verify
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see DummyComplexModel#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(index);
    assertIsValid(model, vertx, testContext);

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
   * Should merge two examples.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see DummyComplexModel#merge(DummyComplexModel, String, Vertx)
   */
  @Test
  public void shouldMergeExamples(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = this.createModelExample(2);
    assertCanMerge(target, source, vertx, testContext, merged -> {
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
   * @see DummyComplexModel#merge(DummyComplexModel, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithBadSibling(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new DummyComplexModel();
    source.siblings = new ArrayList<>();
    source.siblings.add(new DummyComplexModel());
    source.siblings.get(0).id = ValidationsTest.STRING_256;
    assertCannotMerge(target, source, "siblings[0].id", vertx, testContext);

  }

  /**
   * Should not update with a bad sibling.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see DummyComplexModel#update(DummyComplexModel, String, Vertx)
   */
  @Test
  public void shouldNotUpdateWithBadSibling(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new DummyComplexModel();
    source.siblings = new ArrayList<>();
    source.siblings.add(new DummyComplexModel());
    source.siblings.get(0).id = ValidationsTest.STRING_256;
    assertCannotUpdate(target, source, "siblings[0].id", vertx, testContext);

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
    final var expected = new JsonObject().put("index", 0).putNull("id").put("siblings",
        new JsonArray().add(new JsonObject().put("index", 0).putNull("id").putNull("siblings")));
    final var target = new JsonObject(source.toBufferWithEmptyValues());
    assertThat(target).isEqualTo(expected);
    assertThat(target.fieldNames()).isEqualTo(expected.fieldNames());
    assertThat(target.getJsonArray("siblings").getJsonObject(0).fieldNames())
        .isEqualTo(expected.getJsonArray("siblings").getJsonObject(0).fieldNames());
    super.shouldToBufferWithEmptyValues();
  }

}
