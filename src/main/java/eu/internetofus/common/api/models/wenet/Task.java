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

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import eu.internetofus.common.TimeManager;
import eu.internetofus.common.api.models.JsonObjectDeserializer;
import eu.internetofus.common.api.models.Mergeable;
import eu.internetofus.common.api.models.Merges;
import eu.internetofus.common.api.models.Validable;
import eu.internetofus.common.api.models.ValidationErrorException;
import eu.internetofus.common.api.models.Validations;
import eu.internetofus.common.services.WeNetProfileManagerService;
import eu.internetofus.common.services.WeNetServiceApiService;
import eu.internetofus.common.services.WeNetTaskManagerService;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * The profile of a WeNet user.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "Task", description = "A WeNet task.")
public class Task extends CreateUpdateTsDetails implements Validable, Mergeable<Task> {

	/**
	 * The identifier of the profile.
	 */
	@Schema(description = "The unique identifier of the task.", example = "b129e5509c9bb79")
	public String id;

	/**
	 * The identifier of the task type associated to the task.
	 */
	@Schema(description = "The identifier of the task type associated to the task.", example = "b129e5509c9bb79")
	public String taskTypeId;

	/**
	 * The identifier of the WeNet user who created the task.
	 */
	@Schema(
			description = "The identifier of the WeNet user who created the task.",
			example = "15837028-645a-4a55-9aaf-ceb846439eba")
	public String requesterId;

	/**
	 * The identifier of the application the task is associated to.
	 */
	@Schema(description = "The identifier of the application the task is associated to.", example = "yub129e5509bb79")
	public String appId;

	/**
	 * The explanation of the task objective..
	 */
	@Schema(description = "The explanation of the task objective.")
	public TaskGoal goal;

	/**
	 * The difference, measured in seconds, between the time when the task should be
	 * started and midnight, January 1, 1970 UTC.
	 */
	@Schema(
			description = "The UTC epoch timestamp representing the time the task should be started.",
			example = "1563900000")
	public Long startTs;

	/**
	 * The difference, measured in seconds, between the time when the task should be
	 * completed by and midnight, January 1, 1970 UTC.
	 */
	@Schema(
			description = "The UTC epoch timestamp representing the time the task should be completed by.",
			example = "1563930000")
	public Long endTs;

	/**
	 * The difference, measured in seconds, between the time when the task accept
	 * new volunteers and midnight, January 1, 1970 UTC.
	 */
	@Schema(
			description = "The UTC epoch timestamp representing the deadline time for offering help.",
			example = "1563930000")
	public Long deadlineTs;

	/**
	 * The set of norms that define the interaction of the user when do the task.
	 */
	@ArraySchema(
			schema = @Schema(implementation = Norm.class),
			arraySchema = @Schema(description = "The set of norms that define the interaction of the user when do the task."))
	public List<Norm> norms;

	/**
	 * The set of norms that define the interaction of the user when do the task.
	 */
	@Schema(type = "object", description = "The set of norms that define the interaction of the user when do the task.")
	@JsonDeserialize(using = JsonObjectDeserializer.class)
	public JsonObject attributes;

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

				future = future.compose(mapper -> {

					final Promise<Void> verifyNotRepeatedIdPromise = Promise.promise();
					WeNetTaskManagerService.createProxy(vertx).retrieveTask(this.id, task -> {

						if (task.failed()) {

							verifyNotRepeatedIdPromise.complete();

						} else {

							verifyNotRepeatedIdPromise.fail(
									new ValidationErrorException(codePrefix + ".id", "The user '" + this.id + "' has already a task."));
						}
					});
					return verifyNotRepeatedIdPromise.future();
				});
			}
			this.taskTypeId = Validations.validateStringField(codePrefix, "taskTypeId", 255, this.taskTypeId);
			future = future.compose(mapper -> {

				final Promise<Void> verifyNotRepeatedIdPromise = Promise.promise();
				WeNetTaskManagerService.createProxy(vertx).retrieveTaskType(this.taskTypeId, taskType -> {

					if (!taskType.failed()) {

						verifyNotRepeatedIdPromise.complete();

					} else {

						verifyNotRepeatedIdPromise.fail(new ValidationErrorException(codePrefix + ".taskTypeId",
								"The '" + this.taskTypeId + "' is not defined."));
					}
				});
				return verifyNotRepeatedIdPromise.future();
			});
			this.requesterId = Validations.validateStringField(codePrefix, "requesterId", 255, this.requesterId);
			future = future.compose(mapper -> {

				final Promise<Void> verifyRequesterIdExistPromise = Promise.promise();
				WeNetProfileManagerService.createProxy(vertx).retrieveProfile(this.requesterId, search -> {

					if (!search.failed()) {

						verifyRequesterIdExistPromise.complete();

					} else {

						verifyRequesterIdExistPromise.fail(new ValidationErrorException(codePrefix + ".requesterId",
								"The '" + this.requesterId + "' is not defined.", search.cause()));
					}
				});
				return verifyRequesterIdExistPromise.future();
			});
			this.appId = Validations.validateStringField(codePrefix, "appId", 255, this.appId);
			future = future.compose(mapper -> {

				final Promise<Void> verifyNotRepeatedIdPromise = Promise.promise();
				WeNetServiceApiService.createProxy(vertx).retrieveApp(this.appId, app -> {

					if (!app.failed()) {

						verifyNotRepeatedIdPromise.complete();

					} else {

						verifyNotRepeatedIdPromise
								.fail(new ValidationErrorException(codePrefix + ".appId", "The '" + this.appId + "' is not defined."));
					}
				});
				return verifyNotRepeatedIdPromise.future();
			});

			this.startTs = Validations.validateNullableTimeStamp(codePrefix, "startTs", this.startTs, TimeManager.now() + 1,
					false, Long.MAX_VALUE, true);
			this.endTs = Validations.validateNullableTimeStamp(codePrefix, "endTs", this.endTs, this.startTs, false,
					Long.MAX_VALUE, true);
			this.deadlineTs = Validations.validateNullableTimeStamp(codePrefix, "deadlineTs", this.deadlineTs, this.startTs,
					false, this.endTs, false);
			future = future.compose(Validations.validate(this.norms, (a, b) -> a.equals(b), codePrefix + ".norms", vertx));

			// TODO check the attributes fetch the attributes on the type

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
	public Future<Task> merge(Task source, String codePrefix, Vertx vertx) {

		final Promise<Task> promise = Promise.promise();
		Future<Task> future = promise.future();

		final Task merged = new Task();
		merged.taskTypeId = source.taskTypeId;
		if (merged.taskTypeId == null) {

			merged.taskTypeId = this.taskTypeId;
		}

		merged.requesterId = source.requesterId;
		if (merged.requesterId == null) {

			merged.requesterId = this.requesterId;
		}

		merged.appId = source.appId;
		if (merged.appId == null) {

			merged.appId = this.appId;
		}

		merged.startTs = source.startTs;
		if (merged.startTs == null) {

			merged.startTs = this.startTs;
		}

		merged.endTs = source.endTs;
		if (merged.endTs == null) {

			merged.endTs = this.endTs;
		}

		merged.deadlineTs = source.deadlineTs;
		if (merged.deadlineTs == null) {

			merged.deadlineTs = this.deadlineTs;
		}

		merged.attributes = source.attributes;
		if (merged.attributes == null) {

			merged.attributes = this.attributes;
		}

		future = future.compose(Merges.validateMerged(codePrefix, vertx));

		future = future.compose(Merges.mergeField(this.goal, source.goal, codePrefix + ".goal", vertx,
				(model, mergedGoal) -> model.goal = mergedGoal));

		future = future
				.compose(Merges.mergeNorms(this.norms, source.norms, codePrefix + ".norms", vertx, (model, mergedNorms) -> {
					model.norms = mergedNorms;
				}));

		promise.complete(merged);

		// When merged set the fixed field values
		future = future.map(mergedValidatedModel -> {

			mergedValidatedModel.id = this.id;
			mergedValidatedModel._creationTs = this._creationTs;
			mergedValidatedModel._lastUpdateTs = this._lastUpdateTs;
			return mergedValidatedModel;
		});

		return future;
	}

}