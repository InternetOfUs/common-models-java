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

import java.time.format.DateTimeFormatter;
import java.util.UUID;

import eu.internetofus.common.api.models.Mergeable;
import eu.internetofus.common.api.models.Merges;
import eu.internetofus.common.api.models.Model;
import eu.internetofus.common.api.models.Validable;
import eu.internetofus.common.api.models.ValidationErrorException;
import eu.internetofus.common.api.models.Validations;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * An activity that an user do regularly.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(
		ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Routine",
		description = "An activity that an user do regularly.")
public class Routine extends Model implements Validable, Mergeable<Routine> {

	/**
	 * The identifier of the activity.
	 */
	@Schema(description = "The identifier of the routine", example = "oishd0godlkgj")
	public String id;

	/**
	 * The identifier of the activity.
	 */
	@Schema(description = "The label of the routine", example = "work")
	public String label;

	/**
	 * The identifier of the routine.
	 */
	@Schema(description = "The identifier of the routine", example = "oishd0godlkgj")
	public String proximity;

	/**
	 * The time when the routine starts.
	 */
	@Schema(description = "The time when the routine starts", example = "18:00")
	public String from_time;

	/**
	 * The time when the routine ends.
	 */
	@Schema(description = "The time when the routine ends", example = "22:00")
	public String to_time;

	/**
	 * Create an empty routine.
	 */
	public Routine() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Void> validate(String codePrefix, Vertx vertx) {

		final Promise<Void> promise = Promise.promise();
		try {

			this.id = Validations.validateNullableStringField(codePrefix, "id", 255, this.id);
			if (this.id != null) {

				promise.fail(new ValidationErrorException(codePrefix + ".id",
						"You can not specify the identifier of the norm to create"));

			} else {

				this.label = Validations.validateNullableStringField(codePrefix, "label", 255, this.label);
				this.proximity = Validations.validateNullableStringField(codePrefix, "proximity", 255, this.proximity);
				this.from_time = Validations.validateNullableStringDateField(codePrefix, "from_time", DateTimeFormatter.ISO_TIME,
						this.from_time);
				this.to_time = Validations.validateNullableStringDateField(codePrefix, "to_time", DateTimeFormatter.ISO_TIME,
						this.to_time);

				this.id = UUID.randomUUID().toString();
				promise.complete();
			}

		} catch (final ValidationErrorException validationError) {

			promise.fail(validationError);
		}

		return promise.future();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Routine> merge(Routine source, String codePrefix, Vertx vertx) {

		final Promise<Routine> promise = Promise.promise();
		Future<Routine> future = promise.future();
		if (source != null) {

			final Routine merged = new Routine();
			merged.label = source.label;
			if (merged.label == null) {

				merged.label = this.label;
			}

			merged.proximity = source.proximity;
			if (merged.proximity == null) {

				merged.proximity = this.proximity;
			}

			merged.from_time = source.from_time;
			if (merged.from_time == null) {

				merged.from_time = this.from_time;
			}

			merged.to_time = source.to_time;
			if (merged.to_time == null) {

				merged.to_time = this.to_time;
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
