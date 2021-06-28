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

import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ModelTestCase;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

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

    final var model = new SocialPractice();
    model.id = null;
    model.label = "label_" + index;
    model.materials = new ArrayList<>();
    model.competences = new ArrayList<>();
    model.norms = new ArrayList<>();
    for (var i = 0; i < 3; i++) {

      model.materials.add(new MaterialTest().createModelExample(index + i));

      model.competences.add(new CompetenceTest().createModelExample(index + i));

      model.norms.add(new ProtocolNormTest().createModelExample(index + i));

    }
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

    final var model = this.createModelExample(1);
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

    final var model = new SocialPractice();
    model.id = "      ";
    model.label = "    label    ";
    model.materials = new ArrayList<>();
    model.materials.add(new MaterialTest().createModelExample(1));
    model.competences = new ArrayList<>();
    model.competences.add(new CompetenceTest().createModelExample(1));
    model.norms = new ArrayList<>();
    model.norms.add(new ProtocolNormTest().createModelExample(1));
    assertIsValid(model, vertx, testContext, () -> {

      final var expected = new SocialPractice();
      expected.id = model.id;
      expected.label = "label";
      expected.materials = new ArrayList<>();
      expected.materials.add(new MaterialTest().createModelExample(1));
      expected.competences = new ArrayList<>();
      expected.competences.add(new CompetenceTest().createModelExample(1));
      expected.norms = new ArrayList<>();
      expected.norms.add(new ProtocolNormTest().createModelExample(1));
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

    final var model = new SocialPractice();
    model.id = "has_id";
    assertIsValid(model, vertx, testContext);

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

    final var model = new SocialPractice();
    model.norms = new ArrayList<>();
    model.norms.add(new ProtocolNormTest().createModelExample(1));
    model.norms.add(new ProtocolNormTest().createModelExample(2));
    model.norms.add(new ProtocolNormTest().createModelExample(3));
    model.norms.get(1).whenever = model.norms.get(1).thenceforth;
    assertIsNotValid(model, "norms[1].thenceforth", vertx, testContext);

  }

  /**
   * Check that is not valid a model with a material that has the same name and
   * classification.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithAMaterialWithSameNameAndClassification(final Vertx vertx,
      final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.materials.add(Model.fromJsonObject(model.materials.get(0).toJsonObject(), Material.class));
    model.materials.get(model.materials.size() - 1).description = "Other description";
    assertIsNotValid(model, "materials[3]", vertx, testContext);

  }

  /**
   * Check that is valid a model with a material that has the same name but has
   * different classification.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#validate(String, Vertx)
   */
  @Test
  public void shouldBeValidWithAMaterialWithTheSameNameButDiferentClassification(final Vertx vertx,
      final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.materials.add(Model.fromJsonObject(model.materials.get(0).toJsonObject(), Material.class));
    model.materials.get(model.materials.size() - 1).classification = "OtherClassification";
    assertIsValid(model, vertx, testContext);

  }

  /**
   * Check that is valid a model with a material that has the same classification
   * but has different name.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#validate(String, Vertx)
   */
  @Test
  public void shouldBeValidWithAMaterialWithTheSameClassificationButDiferentName(final Vertx vertx,
      final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.materials.add(Model.fromJsonObject(model.materials.get(0).toJsonObject(), Material.class));
    model.materials.get(model.materials.size() - 1).name = "OtherName";
    assertIsValid(model, vertx, testContext);

  }

  /**
   * Check that is not valid a model with a competence that has the same name and
   * ontology.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithACompetenceWithSameNameAndOntology(final Vertx vertx,
      final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.competences.add(Model.fromJsonObject(model.competences.get(0).toJsonObject(), Competence.class));
    model.competences.get(model.competences.size() - 1).level = 0d;
    assertIsNotValid(model, "competences[3]", vertx, testContext);

  }

  /**
   * Check that is valid a model with a competence that has the same name but has
   * different ontology.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#validate(String, Vertx)
   */
  @Test
  public void shouldBeValidWithACompetenceWithTheSameNameButDiferentOntology(final Vertx vertx,
      final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.competences.add(Model.fromJsonObject(model.competences.get(0).toJsonObject(), Competence.class));
    model.competences.get(model.competences.size() - 1).ontology = "OtherOntology";
    assertIsValid(model, vertx, testContext);

  }

  /**
   * Check that is valid a model with a competence that has the same ontology but
   * has different name.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#validate(String, Vertx)
   */
  @Test
  public void shouldBeValidWithACompetenceWithTheSameOntologyButDiferentName(final Vertx vertx,
      final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.competences.add(Model.fromJsonObject(model.competences.get(0).toJsonObject(), Competence.class));
    model.competences.get(model.competences.size() - 1).name = "OtherName";
    assertIsValid(model, vertx, testContext);

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

    final var target = this.createModelExample(1);
    final var source = new SocialPractice();
    source.norms = new ArrayList<>();
    source.norms.add(new ProtocolNormTest().createModelExample(1));
    source.norms.add(new ProtocolNormTest().createModelExample(2));
    source.norms.add(new ProtocolNormTest().createModelExample(3));
    source.norms.get(1).whenever = source.norms.get(1).thenceforth;
    assertCannotMerge(target, source, "norms[1].thenceforth", vertx, testContext);

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

    final var target = this.createModelExample(1);
    target.id = "Target_Id";
    final var source = this.createModelExample(2);
    assertCanMerge(target, source, vertx, testContext, merged -> {
      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      source.id = target.id;
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

    final var target = this.createModelExample(1);
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

    final var target = this.createModelExample(1);
    target.id = "1";
    final var source = new SocialPractice();
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

    final var target = this.createModelExample(1);
    target.id = "19";
    final var source = new SocialPractice();
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

    final var target = this.createModelExample(1);
    target.id = "9";
    final var source = new SocialPractice();
    source.norms = new ArrayList<>();
    for (final var norm : target.norms) {

      source.norms.add(Model.fromJsonObject(norm.toJsonObject(), ProtocolNorm.class));

    }
    source.norms.get(0).whenever = "New attribute";
    assertCanMerge(target, source, vertx, testContext, merged -> {
      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.norms.get(0).whenever = "New attribute";
      assertThat(merged).isEqualTo(target);
    });
  }

  /**
   * Check that merge modify the social practice norms.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#merge(SocialPractice, String, Vertx)
   */
  @Test
  public void shouldMergeUptadingRemovingAndAddingNorms(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "Id9";
    final var source = new SocialPractice();
    source.norms = new ArrayList<>();
    source.norms.add(new ProtocolNormTest().createModelExample(4));
    source.norms.add(new ProtocolNormTest().createModelExample(1));
    source.norms.get(1).whenever = "New attribute";
    assertCanMerge(target, source, vertx, testContext, merged -> {
      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.norms.clear();
      target.norms.add(new ProtocolNormTest().createModelExample(4));
      target.norms.add(new ProtocolNormTest().createModelExample(1));
      target.norms.get(1).whenever = "New attribute";
      assertThat(merged).isEqualTo(target);
    });
  }

  /**
   * Check that merge removing all materials.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#merge(SocialPractice, String, Vertx)
   */
  @Test
  public void shouldMergeRemoveMaterials(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "19";
    final var source = new SocialPractice();
    source.materials = new ArrayList<>();
    assertCanMerge(target, source, vertx, testContext, merged -> {
      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.materials = new ArrayList<>();
      assertThat(merged).isEqualTo(target);
    });
  }

  /**
   * Check that merge modify a material.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#merge(SocialPractice, String, Vertx)
   */
  @Test
  public void shouldMergeModifyAMaterial(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "9";
    final var source = new SocialPractice();
    source.materials = new ArrayList<>();
    for (final var material : target.materials) {

      source.materials.add(Model.fromJsonObject(material.toJsonObject(), Material.class));

    }
    source.materials.get(0).description = "New Description";
    assertCanMerge(target, source, vertx, testContext, merged -> {
      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.materials.get(0).description = "New Description";
      assertThat(merged).isEqualTo(target);
    });
  }

  /**
   * Check that merge modify the social practice materials.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#merge(SocialPractice, String, Vertx)
   */
  @Test
  public void shouldMergeUptadingRemovingAndAddingMaterials(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "Id9";
    final var source = new SocialPractice();
    source.materials = new ArrayList<>();
    source.materials.add(new MaterialTest().createModelExample(4));
    source.materials.add(new Material());
    source.materials.get(1).name = target.materials.get(0).name;
    source.materials.get(1).classification = target.materials.get(0).classification;
    source.materials.get(1).description = "New Description";
    assertCanMerge(target, source, vertx, testContext, merged -> {
      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.materials.clear();
      target.materials.add(new MaterialTest().createModelExample(4));
      target.materials.add(new MaterialTest().createModelExample(1));
      target.materials.get(1).name = source.materials.get(1).name;
      target.materials.get(1).classification = source.materials.get(1).classification;
      target.materials.get(1).description = "New Description";
      assertThat(merged).isEqualTo(target);
    });
  }

  /**
   * Check that merge removing all competences.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#merge(SocialPractice, String, Vertx)
   */
  @Test
  public void shouldMergeRemoveCompetences(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "19";
    final var source = new SocialPractice();
    source.competences = new ArrayList<>();
    assertCanMerge(target, source, vertx, testContext, merged -> {
      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.competences = new ArrayList<>();
      assertThat(merged).isEqualTo(target);
    });
  }

  /**
   * Check that merge modify a competence.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#merge(SocialPractice, String, Vertx)
   */
  @Test
  public void shouldMergeModifyACompetence(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "9";
    final var source = new SocialPractice();
    source.competences = new ArrayList<>();
    for (final var competence : target.competences) {

      source.competences.add(Model.fromJsonObject(competence.toJsonObject(), Competence.class));

    }
    source.competences.get(0).level = 0d;
    assertCanMerge(target, source, vertx, testContext, merged -> {
      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.competences.get(0).level = 0d;
      assertThat(merged).isEqualTo(target);
    });
  }

  /**
   * Check that merge modify the social practice competences.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#merge(SocialPractice, String, Vertx)
   */
  @Test
  public void shouldMergeUptadingRemovingAndAddingCompetences(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "Id9";
    final var source = new SocialPractice();
    source.competences = new ArrayList<>();
    source.competences.add(new CompetenceTest().createModelExample(4));
    source.competences.add(new Competence());
    source.competences.get(1).name = target.competences.get(0).name;
    source.competences.get(1).ontology = target.competences.get(0).ontology;
    source.competences.get(1).level = 0d;
    assertCanMerge(target, source, vertx, testContext, merged -> {
      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.competences.clear();
      target.competences.add(new CompetenceTest().createModelExample(4));
      target.competences.add(new CompetenceTest().createModelExample(1));
      target.competences.get(1).name = source.competences.get(1).name;
      target.competences.get(1).ontology = source.competences.get(1).ontology;
      target.competences.get(1).level = 0d;
      assertThat(merged).isEqualTo(target);
    });
  }

  /**
   * Check that not update model with bad norms.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#update(SocialPractice, String, Vertx)
   */
  @Test
  public void shouldNotUpdateWithABadNorms(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new SocialPractice();
    source.norms = new ArrayList<>();
    source.norms.add(new ProtocolNormTest().createModelExample(1));
    source.norms.add(new ProtocolNormTest().createModelExample(2));
    source.norms.add(new ProtocolNormTest().createModelExample(3));
    source.norms.get(1).whenever = source.norms.get(1).thenceforth;
    assertCannotUpdate(target, source, "norms[1].thenceforth", vertx, testContext);

  }

  /**
   * Check that not update model with bad materials.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#update(SocialPractice, String, Vertx)
   */
  @Test
  public void shouldNotUpdateWithABadMaterials(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new SocialPractice();
    source.materials = new ArrayList<>();
    source.materials.add(new MaterialTest().createModelExample(1));
    source.materials.add(new MaterialTest().createModelExample(2));
    source.materials.add(new MaterialTest().createModelExample(3));
    source.materials.get(1).name = null;
    assertCannotUpdate(target, source, "materials[1].name", vertx, testContext);

  }

  /**
   * Check that not update model with bad competences.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#update(SocialPractice, String, Vertx)
   */
  @Test
  public void shouldNotUpdateWithABadCompetences(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new SocialPractice();
    source.competences = new ArrayList<>();
    source.competences.add(new CompetenceTest().createModelExample(1));
    source.competences.add(new CompetenceTest().createModelExample(2));
    source.competences.add(new CompetenceTest().createModelExample(3));
    source.competences.get(1).name = null;
    assertCannotUpdate(target, source, "competences[1].name", vertx, testContext);

  }

  /**
   * Check that update.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#update(SocialPractice, String, Vertx)
   */
  @Test
  public void shouldUpdate(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "Target_Id";
    final var source = this.createModelExample(2);
    assertCanUpdate(target, source, vertx, testContext, updated -> {
      assertThat(updated).isNotEqualTo(target).isNotEqualTo(source);
      source.id = target.id;
      assertThat(updated).isEqualTo(source);
    });
  }

  /**
   * Check that update with {@code null}.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#update(SocialPractice, String, Vertx)
   */
  @Test
  public void shouldUpdateWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanUpdate(target, null, vertx, testContext, updated -> assertThat(updated).isSameAs(target));

  }

  /**
   * Check that update only label.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#update(SocialPractice, String, Vertx)
   */
  @Test
  public void shouldUpdateOnlyLabel(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "1";
    final var source = new SocialPractice();
    source.label = "NEW LABEL";
    assertCanUpdate(target, source, vertx, testContext, updated -> {
      assertThat(updated).isNotEqualTo(target).isNotEqualTo(source);
      source.id = target.id;
      assertThat(updated).isEqualTo(source);
    });
  }

  /**
   * Check that update removing all norms.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#update(SocialPractice, String, Vertx)
   */
  @Test
  public void shouldUpdateRemoveNorms(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "19";
    final var source = new SocialPractice();
    source.norms = new ArrayList<>();
    assertCanUpdate(target, source, vertx, testContext, updated -> {
      assertThat(updated).isNotEqualTo(target).isNotEqualTo(source);
      source.id = target.id;
      assertThat(updated).isEqualTo(source);
    });
  }

  /**
   * Check that update social practice norms.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#update(SocialPractice, String, Vertx)
   */
  @Test
  public void shouldUpdateNorms(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "9";
    final var source = new SocialPractice();
    source.norms = new ArrayList<>();
    for (final var norm : target.norms) {

      source.norms.add(Model.fromJsonObject(norm.toJsonObject(), ProtocolNorm.class));

    }
    source.norms.get(0).whenever = "New attribute";
    assertCanUpdate(target, source, vertx, testContext, updated -> {
      assertThat(updated).isNotEqualTo(target).isNotEqualTo(source);
      source.id = target.id;
      assertThat(updated).isEqualTo(source);
    });
  }

  /**
   * Check that update social practice materials.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#update(SocialPractice, String, Vertx)
   */
  @Test
  public void shouldUpdateMaterials(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "9";
    final var source = new SocialPractice();
    source.materials = new ArrayList<>();
    for (final var material : target.materials) {

      source.materials.add(Model.fromJsonObject(material.toJsonObject(), Material.class));

    }
    source.materials.get(0).description = "New Description";
    assertCanUpdate(target, source, vertx, testContext, updated -> {
      assertThat(updated).isNotEqualTo(target).isNotEqualTo(source);
      source.id = target.id;
      assertThat(updated).isEqualTo(source);
    });
  }

  /**
   * Check that update removing all competences.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#update(SocialPractice, String, Vertx)
   */
  @Test
  public void shouldUpdateRemoveCompetences(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "19";
    final var source = new SocialPractice();
    source.competences = new ArrayList<>();
    assertCanUpdate(target, source, vertx, testContext, updated -> {
      assertThat(updated).isNotEqualTo(target).isNotEqualTo(source);
      source.id = target.id;
      assertThat(updated).isEqualTo(source);
    });
  }

  /**
   * Check that update social practice competences.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialPractice#update(SocialPractice, String, Vertx)
   */
  @Test
  public void shouldUpdateCompetences(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "9";
    final var source = new SocialPractice();
    source.competences = new ArrayList<>();
    for (final var competence : target.competences) {

      source.competences.add(Model.fromJsonObject(competence.toJsonObject(), Competence.class));

    }
    source.competences.get(0).level = 0d;
    assertCanUpdate(target, source, vertx, testContext, updated -> {
      assertThat(updated).isNotEqualTo(target).isNotEqualTo(source);
      source.id = target.id;
      assertThat(updated).isEqualTo(source);
    });
  }

}
