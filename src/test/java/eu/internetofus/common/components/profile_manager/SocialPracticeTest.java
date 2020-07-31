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

package eu.internetofus.common.components.profile_manager;

import static eu.internetofus.common.components.MergesTest.assertCanMerge;
import static eu.internetofus.common.components.MergesTest.assertCannotMerge;
import static eu.internetofus.common.components.ValidationsTest.assertIsNotValid;
import static eu.internetofus.common.components.ValidationsTest.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.internetofus.common.components.ModelTestCase;
import eu.internetofus.common.components.ValidationsTest;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link SocialPractice}
 *
 * @see SocialPractice
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class SocialPracticeTest extends ModelTestCase<SocialPractice> {

  /**
   * {@inheritDoc}
   */
  @Override
  public SocialPractice createModelExample(final int index) {

    final SocialPractice model = new SocialPractice();
    model.id = null;
    model.label = "label_" + index;
    model.materials = new ArrayList<>();
    model.materials.add(new MaterialTest().createModelExample(index));
    model.competences = new ArrayList<>();
    model.competences.add(new CompetenceTest().createModelExample(index));
    model.norms = new ArrayList<>();
    model.norms.add(new NormTest().createModelExample(index));
    return model;
  }

  /**
   * Check that the {@link #createModelExample(int)} is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#validate(String, Vertx)
   */
  @Test
  public void shouldExampleBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final SocialPractice model = this.createModelExample(1);
    assertIsValid(model, vertx, testContext);
  }

  /**
   * Check that a model with all the values is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#validate(String, Vertx)
   */
  @Test
  public void shouldFullModelBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final SocialPractice model = new SocialPractice();
    model.id = "      ";
    model.label = "    label    ";
    model.materials = new ArrayList<>();
    model.materials.add(new MaterialTest().createModelExample(1));
    model.competences = new ArrayList<>();
    model.competences.add(new CompetenceTest().createModelExample(1));
    model.norms = new ArrayList<>();
    model.norms.add(new NormTest().createModelExample(1));
    assertIsValid(model, vertx, testContext, () -> {

      final SocialPractice expected = new SocialPractice();
      expected.id = model.id;
      expected.label = "label";
      expected.materials = new ArrayList<>();
      expected.materials.add(new MaterialTest().createModelExample(1));
      expected.competences = new ArrayList<>();
      expected.competences.add(new CompetenceTest().createModelExample(1));
      expected.norms = new ArrayList<>();
      expected.norms.add(new NormTest().createModelExample(1));
      expected.norms.get(0).id = model.norms.get(0).id;
      assertThat(model).isEqualTo(expected);

    });
  }

  /**
   * Check that the model with id is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#validate(String, Vertx)
   */
  @Test
  public void shouldBeValidWithAnId(final Vertx vertx, final VertxTestContext testContext) {

    final SocialPractice model = new SocialPractice();
    model.id = "has_id";
    assertIsValid(model, vertx, testContext);

  }

  /**
   * Check that not accept social practices with bad label.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadLabel(final Vertx vertx, final VertxTestContext testContext) {

    final SocialPractice model = new SocialPractice();
    model.label = ValidationsTest.STRING_256;
    assertIsNotValid(model, "label", vertx, testContext);

  }

  /**
   * Check that not accept model with bad norms.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadNorms(final Vertx vertx, final VertxTestContext testContext) {

    final SocialPractice model = new SocialPractice();
    model.norms = new ArrayList<>();
    model.norms.add(new Norm());
    model.norms.add(new Norm());
    model.norms.add(new Norm());
    model.norms.get(1).attribute = ValidationsTest.STRING_256;
    assertIsNotValid(model, "norms[1].attribute", vertx, testContext);

  }

  /**
   * Check that not merge social practices with bad label.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#merge(SocialPractice, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithABadLabel(final Vertx vertx, final VertxTestContext testContext) {

    final SocialPractice target = this.createModelExample(1);
    final SocialPractice source = new SocialPractice();
    source.label = ValidationsTest.STRING_256;
    assertCannotMerge(target, source, "label", vertx, testContext);

  }

  /**
   * Check that not merge model with bad norms.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#merge(SocialPractice, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithABadNorms(final Vertx vertx, final VertxTestContext testContext) {

    final SocialPractice target = this.createModelExample(1);
    final SocialPractice source = new SocialPractice();
    source.norms = new ArrayList<>();
    source.norms.add(new Norm());
    source.norms.add(new Norm());
    source.norms.add(new Norm());
    source.norms.get(1).attribute = ValidationsTest.STRING_256;
    assertCannotMerge(target, source, "norms[1].attribute", vertx, testContext);

  }

  /**
   * Check that merge.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#merge(SocialPractice, String, Vertx)
   */
  @Test
  public void shouldMerge(final Vertx vertx, final VertxTestContext testContext) {

    final SocialPractice target = this.createModelExample(1);
    target.id = "Target_Id";
    final SocialPractice source = this.createModelExample(2);
    assertCanMerge(target, source, vertx, testContext, merged -> {
      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      source.id = target.id;
      source.norms.get(0).id = merged.norms.get(0).id;
      assertThat(merged).isEqualTo(source);
    });
  }

  /**
   * Check that merge with {@code null}.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#merge(SocialPractice, String, Vertx)
   */
  @Test
  public void shouldMergeWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final SocialPractice target = this.createModelExample(1);
    assertCanMerge(target, null, vertx, testContext, merged -> assertThat(merged).isSameAs(target));

  }

  /**
   * Check that merge only label.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#merge(SocialPractice, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyLabel(final Vertx vertx, final VertxTestContext testContext) {

    final SocialPractice target = this.createModelExample(1);
    target.id = "1";
    target.norms.get(0).id = "2";
    final SocialPractice source = new SocialPractice();
    source.label = "NEW LABEL";
    assertCanMerge(target, source, vertx, testContext, merged -> {
      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.label = "NEW LABEL";
      assertThat(merged).isEqualTo(target);
    });
  }

  /**
   * Check that merge removing all norms.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#merge(SocialPractice, String, Vertx)
   */
  @Test
  public void shouldMergeRemoveNorms(final Vertx vertx, final VertxTestContext testContext) {

    final SocialPractice target = this.createModelExample(1);
    target.id = "19";
    final SocialPractice source = new SocialPractice();
    source.norms = new ArrayList<>();
    assertCanMerge(target, source, vertx, testContext, merged -> {
      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.norms = new ArrayList<>();
      assertThat(merged).isEqualTo(target);
    });
  }

  /**
   * Check that merge modify a norm.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#merge(SocialPractice, String, Vertx)
   */
  @Test
  public void shouldMergeModifyANorm(final Vertx vertx, final VertxTestContext testContext) {

    final SocialPractice target = this.createModelExample(1);
    target.id = "9";
    target.norms.get(0).id = "Norm Id";
    final SocialPractice source = new SocialPractice();
    source.norms = new ArrayList<>();
    source.norms.add(new Norm());
    source.norms.get(0).id = target.norms.get(0).id;
    source.norms.get(0).attribute = "New attribute";
    assertCanMerge(target, source, vertx, testContext, merged -> {
      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.norms.get(0).attribute = "New attribute";
      assertThat(merged).isEqualTo(target);
    });
  }

  /**
   * Check that merge modify a norm and add another.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#merge(SocialPractice, String, Vertx)
   */
  @Test
  public void shouldMergeModifyANormAddOther(final Vertx vertx, final VertxTestContext testContext) {

    final SocialPractice target = this.createModelExample(1);
    target.id = "Id9";
    target.norms.get(0).id = "Norm Id";
    final SocialPractice source = new SocialPractice();
    source.norms = new ArrayList<>();
    source.norms.add(new NormTest().createModelExample(3));
    source.norms.add(new Norm());
    source.norms.get(1).id = target.norms.get(0).id;
    source.norms.get(1).attribute = "New attribute";
    assertCanMerge(target, source, vertx, testContext, merged -> {
      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.norms.add(0, new NormTest().createModelExample(3));
      target.norms.get(0).id = merged.norms.get(0).id;
      target.norms.get(1).attribute = "New attribute";
      assertThat(merged).isEqualTo(target);
    });
  }

}
