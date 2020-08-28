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
import eu.internetofus.common.components.ReflectionModel;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.ValidationErrorException;
import eu.internetofus.common.components.Validations;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * Describe a type of task that can be done by the users.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "TaskAttributeType", description = "Describe an attribute that has to be initialized when start a task of an specific type.")
public class TaskAttributeType extends ReflectionModel implements Model, Validable, Mergeable<TaskAttributeType> {

  /**
   * The name of the string type.
   */
  public static final String STRING_TYPE_NAME = "string";

  /**
   * The name of the number type.
   */
  public static final String NUMBER_TYPE_NAME = "number";

  /**
   * The name of the boolean type.
   */
  public static final String BOOLEAN_TYPE_NAME = "boolean";

  /**
   * The name of the profile identifier.
   */
  public static final String PROFILE_ID_TYPE_NAME = "profileId";

  /**
   * The name of the allowed types.
   */
  public static final String[] TYPE_NAMES = { STRING_TYPE_NAME, NUMBER_TYPE_NAME, BOOLEAN_TYPE_NAME, PROFILE_ID_TYPE_NAME };

  /**
   * A name that identify the type.
   */
  @Schema(description = "The name of the attribute.", example = "communityId")
  public String name;

  /**
   * A name that identify the type.
   */
  @Schema(description = "A human readable description of the attribute.", example = "The identifier of the community.")
  public String description;

  /**
   * A name that identify the type.
   */
  @Schema(description = "The type of the attribute values. It can be:\n\t* 'string' - If it is a string value.\n" + "\t* 'number' - If it is a number value.\n\t* 'boolean' - If it is a boolean value.\n"
      + "\t* 'profileId' - If it represent the identifier of a WeNet user.", example = "string", allowableValues = { STRING_TYPE_NAME, NUMBER_TYPE_NAME, BOOLEAN_TYPE_NAME, PROFILE_ID_TYPE_NAME })
  public String type;

  /**
   * A name that identify the type.
   */
  @Schema(description = "This is 'true' if the attribute is an array of values of the specified type.", example = "e56whjt8s09t", defaultValue = "false")
  public boolean isArray;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    try {

      this.name = Validations.validateStringField(codePrefix, "name", 255, this.name);
      this.description = Validations.validateNullableStringField(codePrefix, "description", 1023, this.description);
      this.type = Validations.validateStringField(codePrefix, "type", 50, this.type, TYPE_NAMES);
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
  public Future<TaskAttributeType> merge(final TaskAttributeType source, final String codePrefix, final Vertx vertx) {

    final Promise<TaskAttributeType> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var merged = new TaskAttributeType();
      merged.name = source.name;
      if (merged.name == null) {

        merged.name = this.name;
      }
      merged.description = source.description;
      if (merged.description == null) {

        merged.description = this.description;
      }
      merged.type = source.type;
      if (merged.type == null) {

        merged.type = this.type;
      }
      merged.isArray = source.isArray;

      promise.complete(merged);

      future = future.compose(Merges.validateMerged(codePrefix, vertx));

    } else {

      promise.complete(this);
    }
    return future;

  }

}
