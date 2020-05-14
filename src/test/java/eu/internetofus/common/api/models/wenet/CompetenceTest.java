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
import static eu.internetofus.common.api.models.ValidationsTest.assertIsNotValid;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.internetofus.common.api.models.ValidationsTest;
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
public class CompetenceTest {

	/**
	 * Check that the mode is not valid with an identifier.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Competence#merge(Competence, String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithIdentifier(Vertx vertx, VertxTestContext testContext) {

		final Competence model = new Competence();
		model.id = "Target_Id";
		assertIsNotValid(model, "id", vertx, testContext);
	}

	/**
	 * Check that the mode is not valid with a large identifier.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Competence#merge(Competence, String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithLargeIdentifier(Vertx vertx, VertxTestContext testContext) {

		final Competence model = new Competence();
		model.id = ValidationsTest.STRING_256;
		assertIsNotValid(model, "id", vertx, testContext);
	}

	/**
	 * Check that merge two models.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Competence#merge(Competence, String, Vertx)
	 */
	@Test
	public void shouldMerge(Vertx vertx, VertxTestContext testContext) {

		final Competence target = new DrivingLicenseTest().createModelExample(1);
		target.id = "1";
		final Competence source = new DrivingLicenseTest().createModelExample(2);
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
	 * @see Competence#merge(Competence, String, Vertx)
	 */
	@Test
	public void shouldMergeWithNull(Vertx vertx, VertxTestContext testContext) {

		final Competence target = new Competence();
		assertCanMerge(target, null, vertx, testContext, merged -> assertThat(merged).isSameAs(target));

	}

	/**
	 * Check that merge with two different classes.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Competence#merge(Competence, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithDiferentClasses(Vertx vertx, VertxTestContext testContext) {

		final Competence target = new Competence();
		target.id = "Target_Id";
		final Competence source = new DrivingLicense();
		assertCanMerge(target, source, vertx, testContext, merged -> {

			assertThat(merged).isSameAs(source);
			assertThat(merged.id).isEqualTo(target.id);

		});

	}

	/**
	 * Check that merge two competences.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Competence#merge(Competence, String, Vertx)
	 */
	@Test
	public void shouldMergeTwoCompetences(Vertx vertx, VertxTestContext testContext) {

		final Competence target = new Competence();
		final Competence source = new Competence();
		assertCanMerge(target, source, vertx, testContext, merged -> {

			assertThat(merged).isSameAs(source);
			assertThat(merged.id).isEqualTo(target.id);

		});

	}

}
