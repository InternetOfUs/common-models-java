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

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import eu.internetofus.common.api.models.Merges;
import eu.internetofus.common.api.models.ValidationErrorException;
import eu.internetofus.common.api.models.Validations;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * A car that can be used on a social activity.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(description = "A car that can be used on a social activity.")
@JsonDeserialize(using = JsonDeserializer.None.class)
public class Car extends Material {

	/**
	 * The identifier of the car.
	 */
	@Schema(description = "The car place identifier.", example = "UH765TG")
	public String carPlate;

	/**
	 * The identifier of the car.
	 */
	@Schema(description = "The type of car.", example = "Sport car - 2 seats")
	public String carType;

	/**
	 * Create an empty car.
	 */
	public Car() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Void> validate(String codePrefix, Vertx vertx) {

		return super.validate(codePrefix, vertx).compose(map -> {

			final Promise<Void> promise = Promise.promise();
			try {

				this.carPlate = Validations.validateNullableStringField(codePrefix, "carPlate", 255, this.carPlate);
				this.carType = Validations.validateNullableStringField(codePrefix, "carType", 255, this.carType);
				promise.complete();

			} catch (final ValidationErrorException validationError) {

				promise.fail(validationError);
			}

			return promise.future();
		});

	}

	/**
	 * Merge the current car with a new one, and verify the result is valid.
	 *
	 * @param source     car to merge with the current one.
	 * @param codePrefix the prefix of the code to use for the error message.
	 * @param vertx      the event bus infrastructure to use.
	 *
	 * @return the future that provide the merged model that has to be valid. If it
	 *         can not merge or the merged value is not valid the cause will be a
	 *         {@link ValidationErrorException}.
	 *
	 * @see ValidationErrorException
	 */
	public Future<Car> mergeCar(Car source, String codePrefix, Vertx vertx) {

		final Promise<Car> promise = Promise.promise();
		Future<Car> future = promise.future();
		if (source != null) {

			final Car merged = new Car();
			merged.carPlate = source.carPlate;
			if (merged.carPlate == null) {

				merged.carPlate = this.carPlate;
			}

			merged.carType = source.carType;
			if (merged.carType == null) {

				merged.carType = this.carType;
			}
			promise.complete(merged);

			// validate the merged value and set the id
			future = future.compose(Merges.validateMerged(codePrefix, vertx)).map(mergedValidatedModel -> {

				mergedValidatedModel.id = this.id;
				return mergedValidatedModel;
			});

		} else {

			promise.complete(this);
		}

		return future;
	}

}
