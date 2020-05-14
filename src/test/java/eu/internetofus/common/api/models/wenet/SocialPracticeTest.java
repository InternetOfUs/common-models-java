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

package eu.internetofus.common.api.models.wenet;

import static eu.internetofus.common.api.models.MergesTest.assertCanMerge;
import static eu.internetofus.common.api.models.MergesTest.assertCannotMerge;
import static eu.internetofus.common.api.models.ValidationsTest.assertIsNotValid;
import static eu.internetofus.common.api.models.ValidationsTest.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.internetofus.common.api.models.Model;
import eu.internetofus.common.api.models.ModelTestCase;
import eu.internetofus.common.api.models.ValidationsTest;
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
	public SocialPractice createModelExample(int index) {

		final SocialPractice model = new SocialPractice();
		model.id = null;
		model.label = "label_" + index;
		model.materials = new CarTest().createModelExample(index);
		model.competences = new DrivingLicenseTest().createModelExample(index);
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
	public void shouldExampleBeValid(Vertx vertx, VertxTestContext testContext) {

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
	public void shouldFullModelBeValid(Vertx vertx, VertxTestContext testContext) {

		final SocialPractice model = new SocialPractice();
		model.id = "      ";
		model.label = "    label    ";
		model.competences = new DrivingLicenseTest().createModelExample(1);
		model.materials = new CarTest().createModelExample(1);
		model.norms = new ArrayList<>();
		model.norms.add(new NormTest().createModelExample(1));
		assertIsValid(model, vertx, testContext, () -> {

			final SocialPractice expected = new SocialPractice();
			expected.id = model.id;
			expected.label = "label";
			expected.competences = new DrivingLicenseTest().createModelExample(1);
			expected.competences.id = model.competences.id;
			expected.materials = new CarTest().createModelExample(1);
			expected.materials.id = model.materials.id;
			expected.norms = new ArrayList<>();
			expected.norms.add(new NormTest().createModelExample(1));
			expected.norms.get(0).id = model.norms.get(0).id;
			assertThat(model).isEqualTo(expected);

		});
	}

	/**
	 * Check that the model with id is not valid.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see SocialPractice#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithAnId(Vertx vertx, VertxTestContext testContext) {

		final SocialPractice model = new SocialPractice();
		model.id = "has_id";
		assertIsNotValid(model, "id", vertx, testContext);

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
	public void shouldNotBeValidWithABadLabel(Vertx vertx, VertxTestContext testContext) {

		final SocialPractice model = new SocialPractice();
		model.label = ValidationsTest.STRING_256;
		assertIsNotValid(model, "label", vertx, testContext);

	}

	/**
	 * Check that not accept model with bad materials.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see SocialPractice#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithABadMaterials(Vertx vertx, VertxTestContext testContext) {

		final SocialPractice model = new SocialPractice();
		model.materials = new CarTest().createModelExample(1);
		((Car) model.materials).carType = ValidationsTest.STRING_256;
		assertIsNotValid(model, "materials.carType", vertx, testContext);

	}

	/**
	 * Check that not accept model with bad competences.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see SocialPractice#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithABadCompetences(Vertx vertx, VertxTestContext testContext) {

		final SocialPractice model = new SocialPractice();
		model.competences = new DrivingLicenseTest().createModelExample(1);
		((DrivingLicense) model.competences).drivingLicenseId = ValidationsTest.STRING_256;
		assertIsNotValid(model, "competences.drivingLicenseId", vertx, testContext);

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
	public void shouldNotBeValidWithABadNorms(Vertx vertx, VertxTestContext testContext) {

		final SocialPractice model = new SocialPractice();
		model.norms = new ArrayList<>();
		model.norms.add(new Norm());
		model.norms.add(new Norm());
		model.norms.add(new Norm());
		model.norms.get(1).attribute = ValidationsTest.STRING_256;
		assertIsNotValid(model, "norms[1].attribute", vertx, testContext);

	}

	/**
	 * Check that can not be decoded with a generic material.
	 */
	@Test
	public void shouldNotDecodeWithAGenericMaterial() {

		assertThat(Model.fromString("{\"materials\":{}}", SocialPractice.class)).isNull();

	}

	/**
	 * Check that can not be decoded with a generic material.
	 */
	@Test
	public void shouldNotDecodeWithAGenericCompetence() {

		assertThat(Model.fromString("{\"competences\":{}}", SocialPractice.class)).isNull();

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
	public void shouldNotMergeWithABadLabel(Vertx vertx, VertxTestContext testContext) {

		final SocialPractice target = this.createModelExample(1);
		final SocialPractice source = new SocialPractice();
		source.label = ValidationsTest.STRING_256;
		assertCannotMerge(target, source, "label", vertx, testContext);

	}

	/**
	 * Check that not merge model with bad materials.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see SocialPractice#merge(SocialPractice, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithABadMaterials(Vertx vertx, VertxTestContext testContext) {

		final SocialPractice target = this.createModelExample(1);
		target.id = "TargetId";
		final SocialPractice source = new SocialPractice();
		source.materials = new CarTest().createModelExample(1);
		((Car) source.materials).carType = ValidationsTest.STRING_256;
		assertCannotMerge(target, source, "materials.carType", vertx, testContext);

	}

	/**
	 * Check that not merge model with bad competences.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see SocialPractice#merge(SocialPractice, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithABadCompetences(Vertx vertx, VertxTestContext testContext) {

		final SocialPractice target = this.createModelExample(1);
		final SocialPractice source = new SocialPractice();
		source.competences = new DrivingLicenseTest().createModelExample(1);
		((DrivingLicense) source.competences).drivingLicenseId = ValidationsTest.STRING_256;
		assertCannotMerge(target, source, "competences.drivingLicenseId", vertx, testContext);

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
	public void shouldNotMergeWithABadNorms(Vertx vertx, VertxTestContext testContext) {

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
	public void shouldMerge(Vertx vertx, VertxTestContext testContext) {

		final SocialPractice target = this.createModelExample(1);
		target.id = "Target_Id";
		final SocialPractice source = this.createModelExample(2);
		assertCanMerge(target, source, vertx, testContext, merged -> {
			assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
			source.id = target.id;
			source.competences.id = merged.competences.id;
			source.materials.id = merged.materials.id;
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
	public void shouldMergeWithNull(Vertx vertx, VertxTestContext testContext) {

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
	public void shouldMergeOnlyLabel(Vertx vertx, VertxTestContext testContext) {

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
	 * Check that merge and add a new competence.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see SocialPractice#merge(SocialPractice, String, Vertx)
	 */
	@Test
	public void shouldMergeNewCompetence(Vertx vertx, VertxTestContext testContext) {

		final SocialPractice target = this.createModelExample(1);
		target.id = "1";
		target.competences.id = "2";
		final SocialPractice source = new SocialPractice();
		source.competences = new DrivingLicenseTest().createModelExample(2);
		assertCanMerge(target, source, vertx, testContext, merged -> {
			assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
			target.competences = new DrivingLicenseTest().createModelExample(2);
			target.competences.id = merged.competences.id;
			assertThat(merged).isEqualTo(target);
		});
	}

	/**
	 * Check that fail merge a new competence.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see SocialPractice#merge(SocialPractice, String, Vertx)
	 */
	@Test
	public void shouldFailMergeNewCompetence(Vertx vertx, VertxTestContext testContext) {

		final SocialPractice target = this.createModelExample(1);
		target.id = "target identifier";
		final SocialPractice source = new SocialPractice();
		source.competences = new DrivingLicense();
		((DrivingLicense) source.competences).drivingLicenseId = ValidationsTest.STRING_256;
		assertCannotMerge(target, source, "competences.drivingLicenseId", vertx, testContext);

	}

	/**
	 * Check that merge existing competence.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see SocialPractice#merge(SocialPractice, String, Vertx)
	 */
	@Test
	public void shouldMergeExistingCompetence(Vertx vertx, VertxTestContext testContext) {

		final SocialPractice target = this.createModelExample(1);
		target.id = "target identifier";
		final SocialPractice source = new SocialPractice();
		source.competences = new DrivingLicense();
		source.competences.id = target.competences.id;
		((DrivingLicense) source.competences).drivingLicenseId = "New drivingLicense";
		assertCanMerge(target, source, vertx, testContext, merged -> {
			assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
			((DrivingLicense) target.competences).drivingLicenseId = "New drivingLicense";
			assertThat(merged).isEqualTo(target);
		});
	}

	/**
	 * Check that fail merge and existing competence.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see SocialPractice#merge(SocialPractice, String, Vertx)
	 */
	@Test
	public void shouldFailMergeExistingCompetence(Vertx vertx, VertxTestContext testContext) {

		final SocialPractice target = this.createModelExample(1);
		target.id = "target identifier";
		final SocialPractice source = new SocialPractice();
		source.competences = new DrivingLicense();
		source.competences.id = target.competences.id;
		((DrivingLicense) source.competences).drivingLicenseId = ValidationsTest.STRING_256;
		assertCannotMerge(target, source, "competences.drivingLicenseId", vertx, testContext);
	}

	/**
	 * Check that merge and add a new material.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see SocialPractice#merge(SocialPractice, String, Vertx)
	 */
	@Test
	public void shouldMergeNewMaterial(Vertx vertx, VertxTestContext testContext) {

		final SocialPractice target = this.createModelExample(1);
		target.id = "1";
		target.materials.id = "2";
		final SocialPractice source = new SocialPractice();
		source.materials = new CarTest().createModelExample(2);
		assertCanMerge(target, source, vertx, testContext, merged -> {
			assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
			target.materials = new CarTest().createModelExample(2);
			target.materials.id = merged.materials.id;
			assertThat(merged).isEqualTo(target);
		});
	}

	/**
	 * Check that fail merge a new material.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see SocialPractice#merge(SocialPractice, String, Vertx)
	 */
	@Test
	public void shouldFailMergeNewMaterial(Vertx vertx, VertxTestContext testContext) {

		final SocialPractice target = this.createModelExample(1);
		target.id = "1";
		final SocialPractice source = new SocialPractice();
		source.materials = new Car();
		((Car) source.materials).carPlate = ValidationsTest.STRING_256;
		assertCannotMerge(target, source, "materials.carPlate", vertx, testContext);

	}

	/**
	 * Check that merge existing material.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see SocialPractice#merge(SocialPractice, String, Vertx)
	 */
	@Test
	public void shouldMergeExistingMaterial(Vertx vertx, VertxTestContext testContext) {

		final SocialPractice target = this.createModelExample(1);
		target.id = "10";
		final SocialPractice source = new SocialPractice();
		source.materials = new Car();
		source.materials.id = target.materials.id;
		((Car) source.materials).carPlate = "New car plate";
		assertCanMerge(target, source, vertx, testContext, merged -> {
			assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
			((Car) target.materials).carPlate = "New car plate";
			assertThat(merged).isEqualTo(target);
		});
	}

	/**
	 * Check that fail merge and existing material.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see SocialPractice#merge(SocialPractice, String, Vertx)
	 */
	@Test
	public void shouldFailMergeExistingMaterial(Vertx vertx, VertxTestContext testContext) {

		final SocialPractice target = this.createModelExample(1);
		target.id = "190";
		final SocialPractice source = new SocialPractice();
		source.materials = new Car();
		source.materials.id = target.materials.id;
		((Car) source.materials).carPlate = ValidationsTest.STRING_256;
		assertCannotMerge(target, source, "materials.carPlate", vertx, testContext);

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
	public void shouldMergeRemoveNorms(Vertx vertx, VertxTestContext testContext) {

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
	public void shouldMergeModifyANorm(Vertx vertx, VertxTestContext testContext) {

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
	public void shouldMergeModifyANormAddOther(Vertx vertx, VertxTestContext testContext) {

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
