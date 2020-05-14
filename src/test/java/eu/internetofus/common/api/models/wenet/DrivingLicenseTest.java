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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.internetofus.common.api.models.ValidationsTest;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link DrivingLicense}.
 *
 * @see DrivingLicense
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class DrivingLicenseTest extends CompetenceTestCase<DrivingLicense> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DrivingLicense createModelExample(int index) {

		final DrivingLicense drivinglicense = new DrivingLicense();
		drivinglicense.id = null;
		drivinglicense.drivingLicenseId = "driving_license_id_" + index;
		return drivinglicense;
	}

	/**
	 * Check that a model with all the values is valid.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see DrivingLicense#validate(String, io.vertx.core.Vertx)
	 */
	@Test
	public void shouldFullModelBeValid(Vertx vertx, VertxTestContext testContext) {

		final DrivingLicense model = new DrivingLicense();
		model.id = "      ";
		model.drivingLicenseId = "    driving license id    ";

		assertIsValid(model, vertx, testContext, () -> {

			final DrivingLicense expected = new DrivingLicense();
			expected.id = model.id;
			expected.drivingLicenseId = "driving license id";
			assertThat(model).isEqualTo(expected);

		});

	}

	/**
	 * Check that the model with id is not valid.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see DrivingLicense#validate(String, io.vertx.core.Vertx)
	 */
	@Test
	public void shouldNotBeValidWithAnId(Vertx vertx, VertxTestContext testContext) {

		final DrivingLicense model = new DrivingLicense();
		model.id = "has_id";
		assertIsNotValid(model, "id", vertx, testContext);

	}

	/**
	 * Check that not accept driving licenses with bad driving license id.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see DrivingLicense#validate(String, io.vertx.core.Vertx)
	 */
	@Test
	public void shouldNotBeValidWithABadDrivingLicenseId(Vertx vertx, VertxTestContext testContext) {

		final DrivingLicense model = new DrivingLicense();
		model.drivingLicenseId = ValidationsTest.STRING_256;
		assertIsNotValid(model, "drivingLicenseId", vertx, testContext);

	}

	/**
	 * Check that not merge with bad driving license id.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see DrivingLicense#merge(Competence, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithABadDrivingLicenseId(Vertx vertx, VertxTestContext testContext) {

		final DrivingLicense target = this.createModelExample(1);
		final DrivingLicense source = new DrivingLicense();
		source.drivingLicenseId = ValidationsTest.STRING_256;
		assertCannotMerge(target, source, "drivingLicenseId", vertx, testContext);

	}

	/**
	 * Check that merge two models.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see DrivingLicense#merge(Competence, String, Vertx)
	 */
	@Test
	public void shouldMerge(Vertx vertx, VertxTestContext testContext) {

		final DrivingLicense target = this.createModelExample(1);
		target.id = "1";
		final DrivingLicense source = this.createModelExample(2);
		assertCanMerge(target, source, vertx, testContext, merged -> {

			assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
			source.id = "1";
			assertThat(merged).isEqualTo(source);

		});

	}

	/**
	 * Check that merge with {@code null} source.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see DrivingLicense#merge(Competence, String, Vertx)
	 */
	@Test
	public void shouldMergeWithNull(Vertx vertx, VertxTestContext testContext) {

		final DrivingLicense target = this.createModelExample(1);
		assertCanMerge(target, null, vertx, testContext, merged -> assertThat(merged).isSameAs(target));

	}

	/**
	 * Check that merge with {@code null} source.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Car#merge(Material, String, Vertx)
	 */
	@Test
	public void shouldMergeCarWithNull(Vertx vertx, VertxTestContext testContext) {

		final DrivingLicense target = this.createModelExample(1);
		target.mergeDrivingLicense(null, "codePrefix", vertx)
				.onComplete(testContext.succeeding(merged -> testContext.verify(() -> {

					assertThat(merged).isSameAs(target);
					testContext.completeNow();

				})));

	}

	/**
	 * Check that merge only driving license id.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see DrivingLicense#merge(Competence, String, Vertx)
	 */
	@Test
	public void shouldMergeOnlyDrivingLicenseId(Vertx vertx, VertxTestContext testContext) {

		final DrivingLicense target = this.createModelExample(1);
		target.id = "1";
		final DrivingLicense source = new DrivingLicense();
		source.drivingLicenseId = "NEW DRIVINGLICENSE TYPE";
		assertCanMerge(target, source, vertx, testContext, merged -> {

			assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
			target.drivingLicenseId = "NEW DRIVINGLICENSE TYPE";
			assertThat(merged).isEqualTo(target);

		});
	}

	/**
	 * Check that merge only id.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see DrivingLicense#merge(Competence, String, Vertx)
	 */
	@Test
	public void shouldMergeOnlyId(Vertx vertx, VertxTestContext testContext) {

		final DrivingLicense target = this.createModelExample(1);
		target.id = "1";
		final DrivingLicense source = new DrivingLicense();
		assertCanMerge(target, source, vertx, testContext,
				merged -> assertThat(merged).isEqualTo(target).isNotSameAs(target).isNotEqualTo(source));

	}

}
