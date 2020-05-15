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

import eu.internetofus.common.components.Mergeable;
import eu.internetofus.common.components.Merges;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.ValidationErrorException;
import eu.internetofus.common.components.Validations;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * Describe the current value of a task attribute.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(description = "Describe the current value of a task attribute.")
public class TaskAttribute extends Model implements Validable, Mergeable<TaskAttribute> {

	/**
	 * The name of the attribute.
	 */
	@Schema(description = "The name of the attribute.", example = "communityId")
	public String name;

	/**
	 * The value of the attribute.
	 */
	@Schema(description = "The value of the attribute.", example = "2d1x4g3jsuy")
	public Object value;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Void> validate(String codePrefix, Vertx vertx) {

		final Promise<Void> promise = Promise.promise();
		try {

			this.name = Validations.validateStringField(codePrefix, "name", 255, this.name);
			promise.complete();

		} catch (final ValidationErrorException validationError) {

			promise.fail(validationError);
		}

		return promise.future();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<TaskAttribute> merge(TaskAttribute source, String codePrefix, Vertx vertx) {

		final Promise<TaskAttribute> promise = Promise.promise();
		Future<TaskAttribute> future = promise.future();
		if (source != null) {

			final TaskAttribute merged = new TaskAttribute();
			merged.name = source.name;
			if (merged.name == null) {

				merged.name = this.name;
			}
			merged.value = source.value;
			if (merged.value == null) {

				merged.value = this.value;
			}

			promise.complete(merged);

			future = future.compose(Merges.validateMerged(codePrefix, vertx));

		} else {

			promise.complete(this);
		}
		return future;

	}

}
