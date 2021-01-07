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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eu.internetofus.common.components.HumanDescriptionWithCreateUpdateTsDetails;
import eu.internetofus.common.components.JsonObjectDeserializer;
import eu.internetofus.common.components.Mergeable;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.Updateable;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.ValidationErrorException;
import eu.internetofus.common.components.Validations;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import java.util.List;

/**
 * Describe a type of task that can be done by the users.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "TaskType", description = "The component that describe a possible task.")
public class TaskType extends HumanDescriptionWithCreateUpdateTsDetails
    implements Model, Validable, Mergeable<TaskType>, Updateable<TaskType> {

  /**
   * The identifier of the profile.
   */
  @Schema(description = "The unique identifier of the task type.", example = "b129e5509c9bb79")
  public String id;

  /**
   * The attributes that describe a task of this type.
   */
  @Schema(type = "object", description = "OpenAPI description of the attributes that can have a task.", implementation = Object.class)
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  public JsonObject attributes;

  /**
   * The transaction that can be done in a task of this type.
   */
  @Schema(type = "object", description = "OpenAPI description of the transaction actions that can be done on the task.", implementation = Object.class)
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  public JsonObject transactions;

  /**
   * The callbacks messages that the user that plays in this task can receive.
   */
  @Schema(type = "object", description = "OpenAPI description of the callbacks messages that can be send to the participants of the task.", implementation = Object.class)
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  public JsonObject callbacks;

  /**
   * The individual norms of the user
   */
  @ArraySchema(schema = @Schema(implementation = ProtocolNorm.class), arraySchema = @Schema(description = "The norms that describe the protocol to follow the task of this type."))
  public List<ProtocolNorm> norms;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();
    try {

      this.id = Validations.validateNullableStringField(codePrefix, "id", 255, this.id);
      if (this.id != null) {

        future = Validations.composeValidateId(future, codePrefix, "id", this.id, false,
            WeNetTaskManager.createProxy(vertx)::retrieveTaskType);
      }

      this.name = Validations.validateStringField(codePrefix, "name", 255, this.name);
      this.description = Validations.validateNullableStringField(codePrefix, "description", 1023, this.description);
      this.keywords = Validations.validateNullableListStringField(codePrefix, "keywords", 255, this.keywords);
      future = future.compose(Validations.validate(this.norms, (a, b) -> a.equals(b), codePrefix + ".norms", vertx));
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
  public Future<TaskType> merge(final TaskType source, final String codePrefix, final Vertx vertx) {

    final Promise<TaskType> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var merged = new TaskType();
      merged.name = source.name;
      if (merged.name == null) {

        merged.name = this.name;
      }
      merged.description = source.description;
      if (merged.description == null) {

        merged.description = this.description;
      }

      merged.keywords = source.keywords;
      if (merged.keywords == null) {

        merged.keywords = this.keywords;
      }

      merged.attributes = source.attributes;
      if (merged.attributes == null) {

        merged.attributes = this.attributes;
      }

      merged.transactions = source.transactions;
      if (merged.transactions == null) {

        merged.transactions = this.transactions;
      }

      merged.callbacks = source.callbacks;
      if (merged.callbacks == null) {

        merged.callbacks = this.callbacks;
      }

      merged.norms = source.norms;
      if (merged.norms == null) {

        merged.norms = this.norms;
      }
      future = future.compose(Validations.validateChain(codePrefix, vertx));
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

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<TaskType> update(final TaskType source, final String codePrefix, final Vertx vertx) {

    final Promise<TaskType> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var updated = new TaskType();
      updated.name = source.name;
      updated.description = source.description;
      updated.keywords = source.keywords;
      updated.attributes = source.attributes;
      updated.transactions = source.transactions;
      updated.callbacks = source.callbacks;
      updated.norms = source.norms;

      future = future.compose(Validations.validateChain(codePrefix, vertx));
      future = future.map(updatedValidatedModel -> {

        updatedValidatedModel.id = this.id;
        return updatedValidatedModel;
      });

      promise.complete(updated);

    } else {

      promise.complete(this);
    }
    return future;
  }

}
