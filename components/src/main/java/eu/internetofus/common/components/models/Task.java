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
import eu.internetofus.common.components.WeNetValidateContext;
import eu.internetofus.common.model.CreateUpdateTsDetails;
import eu.internetofus.common.model.JsonObjectDeserializer;
import eu.internetofus.common.model.Mergeable;
import eu.internetofus.common.model.Merges;
import eu.internetofus.common.model.Updateable;
import eu.internetofus.common.model.Validable;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import java.util.List;

/**
 * The profile of a WeNet user.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "Task", description = "A WeNet task.")
public class Task extends CreateUpdateTsDetails implements Validable<WeNetValidateContext>,
    Mergeable<Task, WeNetValidateContext>, Updateable<Task, WeNetValidateContext> {

  /**
   * The identifier of the profile.
   */
  @Schema(description = "The unique identifier of the task.", example = "b129e5509c9bb79", accessMode = AccessMode.READ_ONLY)
  public String id;

  /**
   * The identifier of the task type associated to the task.
   */
  @Schema(description = "The identifier of the task type associated to the task.", example = "b129e5509c9bb79", nullable = true)
  public String taskTypeId;

  /**
   * The identifier of the application where the task is done.
   */
  @Schema(description = "The identifier of the application where the task is done.", example = "yub129e5509bb79", nullable = true)
  public String appId;

  /**
   * The identifier of the community where the task is done.
   */
  @Schema(description = "The identifier of the community where the task is done.", example = "b846439eba79", nullable = true)
  public String communityId;

  /**
   * The identifier of the WeNet user who created the task.
   */
  @Schema(description = "The identifier of the WeNet user who created the task.", example = "15837028-645a-4a55-9aaf-ceb846439eba", nullable = true)
  public String requesterId;

  /**
   * The explanation of the task objective.
   */
  @Schema(description = "The explanation of the task objective.", nullable = true)
  public HumanDescription goal;

  /**
   * The difference, measured in seconds, between the time when the task is closed
   * and midnight, January 1, 1970 UTC.
   */
  @Schema(description = "The UTC epoch timestamp representing the time the task is closed. It its not defined the task still open.", example = "1563930000", nullable = true)
  public Long closeTs;

  /**
   * The set of norms that define the interaction of the user when do the task.
   */
  @ArraySchema(schema = @Schema(implementation = ProtocolNorm.class), arraySchema = @Schema(description = "The set of norms that define the interaction of the user when do the task.", nullable = true))
  public List<ProtocolNorm> norms;

  /**
   * The set of norms that define the interaction of the user when do the task.
   */
  @Schema(type = "object", description = "The set of norms that define the interaction of the user when do the task.", implementation = Object.class, nullable = true)
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  public JsonObject attributes;

  /**
   * The list of historical transactions that has been done in this task.
   */
  @ArraySchema(schema = @Schema(implementation = TaskTransaction.class), arraySchema = @Schema(description = "List of historical transactions that has been done in this task.", nullable = true))
  public List<TaskTransaction> transactions;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final WeNetValidateContext context) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();

    if (this.id != null) {

      future = context.validateNotDefinedTaskIdField("id", this.id, future);
    }

    future = context.validateDefinedProfileIdField("requesterId", this.requesterId, future);
    future = context.validateDefinedAppIdField("appId", this.appId, future);
    future = context.validateDefinedCommunityIdField("communityId", this.communityId, future);

    if (this.goal == null) {

      return context.failField("goal", "You must to define the 'goal' of the task.");

    } else {

      this.goal.name = context.validateStringField("goal.name", this.goal.name, promise);
      this.goal.description = context.normalizeString(this.goal.description);
      this.goal.keywords = context.validateNullableStringListField("goal.keywords", this.goal.keywords, promise);

      if (this.closeTs != null) {

        context.validateTimeStampField("closeTs", this.closeTs, promise);
        if (this.closeTs < this._creationTs) {

          return context.failField("closeTs", "The 'closeTs' has to be after the '_creationTs'.");
        }
      }

      if (this.norms != null) {

        future = future.compose(context.validateListField("norms", this.norms, ProtocolNorm::compareIds));
      }

      future = future.compose(
          empty -> context.validateDefinedTaskTypeByIdField("taskTypeId", this.taskTypeId).transform(search -> {

            if (search.failed()) {

              return Future.failedFuture(search.cause());

            } else {

              final var taskType = search.result();
              if (taskType.attributes == null) {

                if (this.attributes == null || this.attributes.isEmpty()) {

                  return Future.succeededFuture();

                } else {

                  return context.failField("attributes", "The task type does not allow to have attributes.");
                }

              } else {

                return context.validateOpenAPIValueField("attributes", this.attributes, taskType.attributes)
                    .map(validAttributes -> {

                      this.attributes = validAttributes;
                      return null;
                    });
              }
            }

          }));

      promise.tryComplete();

    }

    return future;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Task> merge(final Task source, final WeNetValidateContext context) {

    final Promise<Task> promise = Promise.promise();
    var future = promise.future();

    if (source != null) {

      final var merged = new Task();
      merged._creationTs = this._creationTs;
      merged._lastUpdateTs = this._lastUpdateTs;

      merged.taskTypeId = Merges.mergeValues(this.taskTypeId, source.taskTypeId);
      merged.requesterId = Merges.mergeValues(this.requesterId, source.requesterId);
      merged.appId = Merges.mergeValues(this.appId, source.appId);
      merged.communityId = Merges.mergeValues(this.communityId, source.communityId);
      merged.closeTs = Merges.mergeValues(this.closeTs, source.closeTs);
      merged.attributes = Merges.mergeJsonObjects(this.attributes, source.attributes);
      merged.transactions = Merges.mergeValues(this.transactions, source.transactions);

      if (this.goal != null) {

        merged.goal = new HumanDescription();
        if (source.goal == null) {

          merged.goal.name = this.goal.name;
          merged.goal.description = this.goal.description;
          merged.goal.keywords = this.goal.keywords;

        } else {

          merged.goal.name = Merges.mergeValues(this.goal.name, source.goal.name);
          merged.goal.description = Merges.mergeValues(this.goal.description, source.goal.description);
          merged.goal.keywords = Merges.mergeValues(this.goal.keywords, source.goal.keywords);

        }

      } else if (source.goal != null) {

        merged.goal = new HumanDescription();
        merged.goal.name = source.goal.name;
        merged.goal.description = source.goal.description;
        merged.goal.keywords = source.goal.keywords;

      }

      merged.norms = Merges.mergeValues(this.norms, source.norms);

      future = future.compose(context.chain());

      promise.complete(merged);

      // When merged set the fixed field values
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
  public Future<Task> update(final Task source, final WeNetValidateContext context) {

    final Promise<Task> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var updated = new Task();
      updated._creationTs = this._creationTs;
      updated._lastUpdateTs = this._lastUpdateTs;

      updated.taskTypeId = source.taskTypeId;
      updated.requesterId = source.requesterId;
      updated.appId = source.appId;
      updated.communityId = source.communityId;
      updated.closeTs = source.closeTs;
      updated.attributes = source.attributes;
      updated.goal = source.goal;
      updated.norms = source.norms;
      updated.transactions = source.transactions;

      future = future.compose(context.chain());

      // When updated set the fixed field values
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