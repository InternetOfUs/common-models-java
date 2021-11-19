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
import eu.internetofus.common.components.WeNetValidateContext;
import eu.internetofus.common.model.JsonObjectDeserializer;
import eu.internetofus.common.model.Mergeable;
import eu.internetofus.common.model.Merges;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.Updateable;
import eu.internetofus.common.model.Validable;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import java.util.List;

/**
 * Describe a type of task that can be done by the users.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "TaskType", description = "The component that describe a possible task.")
public class TaskType extends HumanDescriptionWithCreateUpdateTsDetails
    implements Model, Validable<WeNetValidateContext>, Mergeable<TaskType, WeNetValidateContext>,
    Updateable<TaskType, WeNetValidateContext> {

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
  public Future<Void> validate(final WeNetValidateContext context) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();
    if (this.id != null) {

      future = context.validateNotDefinedTaskTypeIdField("id", this.id, future);

    }

    this.name = context.normalizeString(this.name);
    this.description = context.normalizeString(this.description);
    this.keywords = context.validateNullableStringListField("keywords", this.keywords, promise);
    if (this.norms != null) {

      future = future.compose(context.validateListField("norms", this.norms, ProtocolNorm::compareIds));
    }

    if (this.callbacks != null) {

      future = context.validateComposedOpenAPISpecificationField("callbacks", this.callbacks, future);
    }

    if (this.transactions != null) {

      future = context.validateComposedOpenAPISpecificationField("transactions", this.transactions, future);
    }

    if (this.attributes != null) {

      future = context.validateOpenAPISpecificationField("attributes", this.attributes, future);
    }

    promise.tryComplete();

    return future;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<TaskType> merge(final TaskType source, final WeNetValidateContext context) {

    final Promise<TaskType> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var merged = new TaskType();
      merged._creationTs = this._creationTs;
      merged._lastUpdateTs = this._lastUpdateTs;
      merged.name = Merges.mergeValues(this.name, source.name);
      merged.description = Merges.mergeValues(this.description, source.description);
      merged.keywords = Merges.mergeValues(this.keywords, source.keywords);
      merged.attributes = Merges.mergeJsonObjects(this.attributes, source.attributes);
      merged.transactions = Merges.mergeJsonObjects(this.transactions, source.transactions);
      merged.callbacks = Merges.mergeJsonObjects(this.callbacks, source.callbacks);
      merged.norms = Merges.mergeValues(this.norms, source.norms);
      future = future.compose(context.chain());
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
  public Future<TaskType> update(final TaskType source, final WeNetValidateContext context) {

    final Promise<TaskType> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var updated = new TaskType();
      updated._creationTs = this._creationTs;
      updated._lastUpdateTs = this._lastUpdateTs;

      updated.name = source.name;
      updated.description = source.description;
      updated.keywords = source.keywords;
      updated.attributes = source.attributes;
      updated.transactions = source.transactions;
      updated.callbacks = source.callbacks;
      updated.norms = source.norms;

      future = future.compose(context.chain());
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
