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

package eu.internetofus.common.components.task_manager;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import eu.internetofus.common.components.JsonObjectDeserializer;
import eu.internetofus.common.components.Mergeable;
import eu.internetofus.common.components.Merges;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.ValidationErrorException;
import eu.internetofus.common.components.Validations;
import eu.internetofus.common.components.profile_manager.Norm;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Describe a type of task that can be done by the users.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "TaskType", description = "Describe a type of task that can be done by the users.")
public class TaskType extends Model implements Validable, Mergeable<TaskType> {

	/**
	 * The identifier of the profile.
	 */
	@Schema(description = "The unique identifier of the task type.", example = "4a559aafceb8464")
	public String id;

	/**
	 * A name that identify the type.
	 */
	@Schema(description = "A name that identify the type.", example = "Eat together task")
	public String name;

	/**
	 * A human readable description of the task type.
	 */
	@Schema(
			description = "A human readable description of the task type.",
			example = "A task for organizing social dinners")
	public String description;

	/**
	 * A name that identify the type.
	 */
	@ArraySchema(
			schema = @Schema(implementation = String.class),
			arraySchema = @Schema(
					description = "The keywords that describe the task type",
					example = "[\"social interaction\",\"eat\"]"))
	public List<String> keywords;

	/**
	 * The individual norms of the user
	 */
	@ArraySchema(
			schema = @Schema(implementation = Norm.class),
			arraySchema = @Schema(
					description = "The norms that describe the interaction of the users to do the tasks of this type."))
	public List<Norm> norms;

	/**
	 * The attribute that has to be instantiated when create the task of this type.
	 */
	@ArraySchema(
			schema = @Schema(implementation = TaskAttributeType.class),
			arraySchema = @Schema(
					description = "The attribute that has to be instantiated when create the task of this type."))
	public List<TaskAttributeType> attributes;

	/**
	 * The supported transaction types for the task.
	 */
	@ArraySchema(
			schema = @Schema(implementation = TaskTransactionType.class),
			arraySchema = @Schema(description = "The supported transaction types for the task."))
	public List<TaskTransactionType> transactions;

	/**
	 * The individual norms of the user
	 */
	@Schema(type = "object", description = "The attribute with a fixed value.")
	@JsonDeserialize(using = JsonObjectDeserializer.class)
	public JsonObject constants;

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
					WeNetTaskManager.createProxy(vertx).retrieveTaskType(this.id, profile -> {

						if (profile.failed()) {

							verifyNotRepeatedIdPromise.complete();

						} else {

							verifyNotRepeatedIdPromise.fail(new ValidationErrorException(codePrefix + ".id",
									"The '" + this.id + "' is already used by a task type."));
						}
					});
					return verifyNotRepeatedIdPromise.future();
				});
			}

			this.name = Validations.validateStringField(codePrefix, "name", 255, this.name);
			this.description = Validations.validateNullableStringField(codePrefix, "description", 1023, this.description);
			this.keywords = Validations.validateNullableListStringField(codePrefix, "keywords", 255, this.keywords);
			future = future.compose(Validations.validate(this.norms, (a, b) -> a.equals(b), codePrefix + ".norms", vertx));
			future = future.compose(
					Validations.validate(this.attributes, (a, b) -> a.name.equals(b.name), codePrefix + ".attributes", vertx));
			future = future.compose(Validations.validate(this.transactions, (a, b) -> a.label.equals(b.label),
					codePrefix + ".transactions", vertx));
			future = future.compose(mapper -> {

				if (this.transactions == null || this.transactions.isEmpty()) {

					return Future.failedFuture(new ValidationErrorException(codePrefix + ".transactions",
							"You must to define at least one transaction."));

				} else {

					return Future.succeededFuture();
				}
			});

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
	public Future<TaskType> merge(TaskType source, String codePrefix, Vertx vertx) {

		final Promise<TaskType> promise = Promise.promise();
		Future<TaskType> future = promise.future();
		if (source != null) {

			final TaskType merged = new TaskType();
			future = future.compose(Merges.mergeTaskTransactionTypes(this.transactions, source.transactions,
					codePrefix + ".transactions", vertx, (model, mergedTransactions) -> {
						model.transactions = mergedTransactions;
					})).map(model -> {

						model.name = source.name;
						if (model.name == null) {

							model.name = this.name;
						}
						model.description = source.description;
						if (model.description == null) {

							model.description = this.description;
						}

						model.keywords = source.keywords;
						if (model.keywords == null) {

							model.keywords = this.keywords;
						}

						model.constants = source.constants;
						if (model.constants == null) {

							model.constants = this.constants;
						}

						return model;
					});

			future = future.compose(Merges.validateMerged(codePrefix, vertx));
			future = future
					.compose(Merges.mergeNorms(this.norms, source.norms, codePrefix + ".norms", vertx, (model, mergedNorms) -> {
						model.norms = mergedNorms;
					}));
			future = future.compose(Merges.mergeTaskAttributeTypes(this.attributes, source.attributes,
					codePrefix + ".attributes", vertx, (model, mergedAttributes) -> {
						model.attributes = mergedAttributes;
					}));

			promise.complete(merged);
			future = future.map(mergedValidatedModel -> {

				mergedValidatedModel.id = this.id;
				return mergedValidatedModel;
			});

		} else {

			promise.complete(this);
		}
		return future;
	}

}
