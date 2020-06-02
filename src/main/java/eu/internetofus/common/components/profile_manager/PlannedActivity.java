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

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;
import java.util.function.Function;

import eu.internetofus.common.components.Mergeable;
import eu.internetofus.common.components.Merges;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.ValidationErrorException;
import eu.internetofus.common.components.Validations;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * An activity planned by an user.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(description = "An activity planned by an user.")
public class PlannedActivity extends Model implements Validable, Mergeable<PlannedActivity> {

	/**
	 * The identifier of the activity.
	 */
	@Schema(description = "The identifier of the activity", example = "hfdsfs888")

	public String id;

	/**
	 * The starting time of the activity.
	 */
	@Schema(description = "The starting time of the activity", example = "2017-07-21T17:32:00Z")
	public String startTime;

	/**
	 * The ending time of the activity.
	 */
	@Schema(description = "The ending time of the activity", example = "2019-07-21T17:32:23Z")
	public String endTime;

	/**
	 * The description of the activity.
	 */
	@Schema(description = "The description of the activity", example = "A few beers for relaxing")
	public String description;

	/**
	 * The identifier of other WeNet user taking part to the activity.
	 */
	@ArraySchema(
			schema = @Schema(implementation = String.class),
			arraySchema = @Schema(
					description = "The identifier of other wenet user taking part to the activity",
					example = "[15d85f1d-b1ce-48de-b221-bec9ae954a88]"))
	public List<String> attendees;

	/**
	 * The current status of the activity.
	 */
	@Schema(description = "The current status of the activity", example = "confirmed")
	public PlannedActivityStatus status;

	/**
	 * Create an empty activity.
	 */
	public PlannedActivity() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Void> validate(String codePrefix, Vertx vertx) {

		final Promise<Void> promise = Promise.promise();
		Future<Void> future = promise.future();
		try {

			this.id = Validations.validateNullableStringField(codePrefix, "id", 255, this.id);
			if (this.id != null) {

				return Future.failedFuture(new ValidationErrorException(codePrefix + ".id",
						"You can not specify the identifier of the planned activity to create"));

			} else {

				this.id = UUID.randomUUID().toString();
			}
			this.startTime = Validations.validateNullableStringDateField(codePrefix, "startTime",
					DateTimeFormatter.ISO_INSTANT, this.startTime);
			this.endTime = Validations.validateNullableStringDateField(codePrefix, "endTime", DateTimeFormatter.ISO_INSTANT,
					this.endTime);
			this.description = Validations.validateNullableStringField(codePrefix, "description", 255, this.description);
			if (this.attendees != null && !this.attendees.isEmpty()) {

				for (final ListIterator<String> ids = this.attendees.listIterator(); ids.hasNext();) {

					final int index = ids.nextIndex();
					final String id = Validations.validateNullableStringField(codePrefix, "attendees[" + index + "]", 255,
							ids.next());
					ids.remove();
					if (id != null) {

						ids.add(id);
						for (int j = 0; j < index; j++) {

							if (id.equals(this.attendees.get(j))) {

								return Future.failedFuture(new ValidationErrorException(codePrefix + ".attendees[" + index + "]",
										"Duplicated attendee. It is equals to the attendees[" + j + "]."));

							}
						}

						future = future.compose((Function<Void, Future<Void>>) map -> {

							final Promise<Void> searchPromise = Promise.promise();
							WeNetProfileManager.createProxy(vertx).retrieveProfile(id, search -> {

								if (search.result() != null) {

									searchPromise.complete();

								} else {

									searchPromise.fail(new ValidationErrorException(codePrefix + ".attendees[" + index + "]",
											"Does not exist an user with the identifier '" + id + "'."));
								}

							});

							return searchPromise.future();
						});
					}

				}

			}
			promise.complete();

		} catch (final ValidationErrorException validationError) {

			promise.fail(validationError);
		}

		return future;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<PlannedActivity> merge(PlannedActivity source, String codePrefix, Vertx vertx) {

		final Promise<PlannedActivity> promise = Promise.promise();
		Future<PlannedActivity> future = promise.future();
		if (source != null) {

			// merge the values
			final PlannedActivity merged = new PlannedActivity();
			merged.startTime = source.startTime;
			if (merged.startTime == null) {

				merged.startTime = this.startTime;
			}
			merged.endTime = source.endTime;
			if (merged.endTime == null) {

				merged.endTime = this.endTime;
			}
			merged.description = source.description;
			if (merged.description == null) {

				merged.description = this.description;
			}
			merged.attendees = source.attendees;
			if (merged.attendees == null) {

				merged.attendees = this.attendees;
			}
			merged.status = source.status;
			if (merged.status == null) {

				merged.status = this.status;
			}
			promise.complete(merged);

			// validate the merged value and set the i<d
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
