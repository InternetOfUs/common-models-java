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
 * Test the {@link Car}.
 *
 * @see Car
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class CarTest extends MaterialTestCase<Car> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Car createModelExample(int index) {

		final Car car = new Car();
		car.id = null;
		car.carPlate = "car_plate_" + index;
		car.carType = "car_type_" + index;
		return car;
	}

	/**
	 * Check that a model with all the values is valid.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Car#validate(String, io.vertx.core.Vertx)
	 */
	@Test
	public void shouldFullModelBeValid(Vertx vertx, VertxTestContext testContext) {

		final Car model = new Car();
		model.id = "      ";
		model.carType = "    car type    ";
		model.carPlate = "    car plate    ";

		assertIsValid(model, vertx, testContext, () -> {

			final Car expected = new Car();
			expected.id = model.id;
			expected.carType = "car type";
			expected.carPlate = "car plate";
			assertThat(model).isEqualTo(expected);

		});

	}

	/**
	 * Check that the model with id is not valid.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Car#validate(String, io.vertx.core.Vertx)
	 */
	@Test
	public void shouldNotBeValidWithAnId(Vertx vertx, VertxTestContext testContext) {

		final Car model = new Car();
		model.id = "has_id";
		assertIsNotValid(model, "id", vertx, testContext);

	}

	/**
	 * Check that not accept cars with bad car type.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Car#validate(String, io.vertx.core.Vertx)
	 */
	@Test
	public void shouldNotBeValidWithABadCarType(Vertx vertx, VertxTestContext testContext) {

		final Car model = new Car();
		model.carType = ValidationsTest.STRING_256;
		assertIsNotValid(model, "carType", vertx, testContext);

	}

	/**
	 * Check that not accept cars with bad car plate.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Car#validate(String, io.vertx.core.Vertx)
	 */
	@Test
	public void shouldNotBeValidWithABadCarPlate(Vertx vertx, VertxTestContext testContext) {

		final Car model = new Car();
		model.carPlate = ValidationsTest.STRING_256;
		assertIsNotValid(model, "carPlate", vertx, testContext);

	}

	/**
	 * Check that not merge with bad car type.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Car#merge(Material, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithABadCarType(Vertx vertx, VertxTestContext testContext) {

		final Car target = this.createModelExample(1);
		final Car source = new Car();
		source.carType = ValidationsTest.STRING_256;
		assertCannotMerge(target, source, "carType", vertx, testContext);

	}

	/**
	 * Check that not merge with bad car plate.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Car#merge(Material, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithABadCarPlate(Vertx vertx, VertxTestContext testContext) {

		final Car target = this.createModelExample(1);
		final Car source = new Car();
		source.carPlate = ValidationsTest.STRING_256;
		assertCannotMerge(target, source, "carPlate", vertx, testContext);

	}

	/**
	 * Check that merge two models.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Car#merge(Material, String, Vertx)
	 */
	@Test
	public void shouldMerge(Vertx vertx, VertxTestContext testContext) {

		final Car target = this.createModelExample(1);
		target.id = "1";
		final Car source = this.createModelExample(2);
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
	 * @see Car#merge(Material, String, Vertx)
	 */
	@Test
	public void shouldMergeWithNull(Vertx vertx, VertxTestContext testContext) {

		final Car target = this.createModelExample(1);
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

		final Car target = this.createModelExample(1);
		target.mergeCar(null, "codePrefix", vertx).onComplete(testContext.succeeding(merged -> testContext.verify(() -> {

			assertThat(merged).isSameAs(target);
			testContext.completeNow();

		})));

	}

	/**
	 * Check that merge only car type.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Car#merge(Material, String, Vertx)
	 */
	@Test
	public void shouldMergeOnlyCarType(Vertx vertx, VertxTestContext testContext) {

		final Car target = this.createModelExample(1);
		target.id = "1";
		final Car source = new Car();
		source.carType = "NEW CAR TYPE";
		assertCanMerge(target, source, vertx, testContext, merged -> {

			assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
			target.carType = "NEW CAR TYPE";
			assertThat(merged).isEqualTo(target);

		});

	}

	/**
	 * Check that merge only car plate.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Car#merge(Material, String, Vertx)
	 */
	@Test
	public void shouldMergeOnlyCarPlate(Vertx vertx, VertxTestContext testContext) {

		final Car target = this.createModelExample(1);
		target.id = "1";
		final Car source = new Car();
		source.carPlate = "NEW CAR PLATE";
		assertCanMerge(target, source, vertx, testContext, merged -> {

			assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
			target.carPlate = "NEW CAR PLATE";
			assertThat(merged).isEqualTo(target);

		});

	}

	/**
	 * Check that merge only id.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Car#merge(Material, String, Vertx)
	 */
	@Test
	public void shouldMergeOnlyId(Vertx vertx, VertxTestContext testContext) {

		final Car target = this.createModelExample(1);
		target.id = "1";
		final Car source = new Car();
		assertCanMerge(target, source, vertx, testContext,
				merged -> assertThat(merged).isEqualTo(target).isNotSameAs(target).isNotEqualTo(source));

	}

}
