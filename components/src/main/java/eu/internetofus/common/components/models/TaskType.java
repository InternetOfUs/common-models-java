/*
 * -----------------------------------------------------------------------------
 *
 * Copyright 2019 - 2022 UDT-IA, IIIA-CSIC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * -----------------------------------------------------------------------------
 */

package eu.internetofus.common.components.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eu.internetofus.common.components.HumanDescriptionWithCreateUpdateTsDetails;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import eu.internetofus.common.model.JsonObjectDeserializer;
import eu.internetofus.common.model.Mergeable;
import eu.internetofus.common.model.Merges;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.Updateable;
import eu.internetofus.common.model.Validable;
import eu.internetofus.common.model.Validations;
import eu.internetofus.common.vertx.OpenAPIValidator;
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
  @Schema(description = "The unique identifier of the task type.", example = "b129e5509c9bb79", nullable = true)
  public String id;

  /**
   * The attributes that describe a task of this type.
   */
  @Schema(type = "object", description = "OpenAPI description of the attributes that can have a task.", implementation = Object.class, nullable = true)
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  public JsonObject attributes;

  /**
   * The transaction that can be done in a task of this type.
   */
  @Schema(type = "object", description = "OpenAPI description of the transaction actions that can be done on the task.", implementation = Object.class, nullable = true)
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  public JsonObject transactions;

  /**
   * The callbacks messages that the user that plays in this task can receive.
   */
  @Schema(type = "object", description = "OpenAPI description of the callbacks messages that can be send to the participants of the task.", implementation = Object.class, nullable = true)
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  public JsonObject callbacks;

  /**
   * The norms that describe the protocol to follow the task of this type.
   */
  @ArraySchema(schema = @Schema(implementation = ProtocolNorm.class), arraySchema = @Schema(description = "The norms that describe the protocol to follow the task of this type.", nullable = true))
  public List<ProtocolNorm> norms;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();
    if (this.id != null) {

      future = Validations.composeValidateId(future, codePrefix, "id", this.id, false,
          WeNetTaskManager.createProxy(vertx)::retrieveTaskType);
    }

    future = future
        .compose(empty -> Validations.validateNullableStringField(codePrefix, "name", this.name).map(value -> {
          this.name = value;
          return null;
        }));
    future = future.compose(
        empty -> Validations.validateNullableStringField(codePrefix, "description", this.description).map(value -> {
          this.description = value;
          return null;
        }));
    future = future.compose(
        empty -> Validations.validateNullableListStringField(codePrefix, "keywords", this.keywords).map(value -> {
          this.keywords = value;
          return null;
        }));
    future = future.compose(Validations.validate(this.norms, (a, b) -> a.equals(b), codePrefix + ".norms", vertx));

    if (this.callbacks != null) {

      future = future
          .compose(empty -> OpenAPIValidator.validateComposedSpecification("callbacks", vertx, this.callbacks));
    }

    if (this.transactions != null) {

      future = future
          .compose(empty -> OpenAPIValidator.validateComposedSpecification("transactions", vertx, this.transactions));
    }

    if (this.attributes != null) {

      future = future.compose(empty -> OpenAPIValidator.validateSpecification("attributes", vertx, this.attributes));
    }

    promise.complete();

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

      merged.attributes = Merges.mergeJsonObjects(this.attributes, source.attributes);
      merged.transactions = Merges.mergeJsonObjects(this.transactions, source.transactions);
      merged.callbacks = Merges.mergeJsonObjects(this.callbacks, source.callbacks);

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
