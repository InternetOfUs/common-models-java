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
 * A competence to drive a vehicle.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(description = "A competence to drive a vehicle.")
@JsonDeserialize(using = JsonDeserializer.None.class)
public class DrivingLicense extends Competence {

	/**
	 * The identifier of the driving license.
	 */
	@Schema(description = "The driving license if", example = "ESdfg09dofgk")
	public String drivingLicenseId;

	/**
	 * Create an empty driving license.
	 */
	public DrivingLicense() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Void> validate(String codePrefix, Vertx vertx) {

		return super.validate(codePrefix, vertx).compose(map -> {
			final Promise<Void> promise = Promise.promise();
			try {

				this.drivingLicenseId = Validations.validateNullableStringField(codePrefix, "drivingLicenseId", 255,
						this.drivingLicenseId);
				promise.complete();

			} catch (final ValidationErrorException validationError) {

				promise.fail(validationError);
			}

			return promise.future();
		});

	}

	/**
	 * Merge the current driving license with a new one, and verify the result is
	 * valid.
	 *
	 * @param source     driving license to merge with the current one.
	 * @param codePrefix the prefix of the code to use for the error message.
	 * @param vertx      the event bus infrastructure to use.
	 *
	 * @return the future that provide the merged model that has to be valid. If it
	 *         can not merge or the merged value is not valid the cause will be a
	 *         {@link ValidationErrorException}.
	 *
	 * @see ValidationErrorException
	 */
	public Future<DrivingLicense> mergeDrivingLicense(DrivingLicense source, String codePrefix, Vertx vertx) {

		final Promise<DrivingLicense> promise = Promise.promise();
		Future<DrivingLicense> future = promise.future();
		if (source != null) {

			final DrivingLicense merged = new DrivingLicense();
			merged.drivingLicenseId = source.drivingLicenseId;
			if (merged.drivingLicenseId == null) {

				merged.drivingLicenseId = this.drivingLicenseId;
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
