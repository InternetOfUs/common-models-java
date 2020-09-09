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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ReflectionModel;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.ValidationErrorException;
import eu.internetofus.common.components.Validations;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * The explanation of the task objective.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(description = "The explanation of the task objective.")
public class TaskGoal extends ReflectionModel implements Model, Validable, Mergeable<TaskGoal> {

  /**
   * The user name prefix.
   */
  @Schema(description = "A short description of the task goal.", example = "Go to dinner together")
  public String name;

  /**
   * The user name name.
   */
  @Schema(description = "A human readable description of the objective of the task.", example = "It is my anniversary and I want to celebrate together.")
  public String description;

  /**
   * Create a new user name.
   */
  public TaskGoal() {

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    try {

      this.name = Validations.validateNullableStringField(codePrefix, "name", 255, this.name);
      this.description = Validations.validateNullableStringField(codePrefix, "description", 1023, this.description);
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
  public Future<TaskGoal> merge(final TaskGoal source, final String codePrefix, final Vertx vertx) {

    final Promise<TaskGoal> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var merged = new TaskGoal();
      merged.name = source.name;
      if (merged.name == null) {

        merged.name = this.name;
      }
      merged.description = source.description;
      if (merged.description == null) {

        merged.description = this.description;
      }

      promise.complete(merged);

      future = future.compose(Validations.validateChain(codePrefix, vertx));

    } else {

      promise.complete(this);
    }
    return future;

  }

}
