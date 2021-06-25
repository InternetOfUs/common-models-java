/*
 * -----------------------------------------------------------------------------
 *
 *   Copyright 2019 - 2022 UDT-IA, IIIA-CSIC
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
import eu.internetofus.common.components.JsonObjectDeserializer;
import eu.internetofus.common.components.Mergeable;
import eu.internetofus.common.components.Merges;
import eu.internetofus.common.components.Updateable;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.ValidationErrorException;
import eu.internetofus.common.components.Validations;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.components.service.WeNetService;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import java.util.List;

/**
 * The profile of a WeNet user.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "Task", description = "A WeNet task.")
public class Task extends CreateUpdateTsDetails implements Validable, Mergeable<Task>, Updateable<Task> {

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
   * by and midnight, January 1, 1970 UTC.
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
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();

    if (this.id != null) {

      future = Validations.composeValidateId(future, codePrefix, "id", this.id, false,
          WeNetTaskManager.createProxy(vertx)::retrieveTask);
    }

    future = Validations.composeValidateId(future, codePrefix, "taskTypeId", this.taskTypeId, true,
        WeNetTaskManager.createProxy(vertx)::retrieveTaskType);

    future = Validations.composeValidateId(future, codePrefix, "requesterId", this.requesterId, true,
        WeNetProfileManager.createProxy(vertx)::retrieveProfile);

    future = Validations.composeValidateId(future, codePrefix, "appId", this.appId, true,
        WeNetService.createProxy(vertx)::retrieveApp);

    future = Validations.composeValidateId(future, codePrefix, "communityId", this.communityId, true,
        WeNetProfileManager.createProxy(vertx)::retrieveCommunity);

    if (this.goal == null) {

      promise.fail(new ValidationErrorException(codePrefix + ".goal", "You must to define the 'goal' of the task."));

    } else {

      future = future
          .compose(empty -> Validations.validateStringField(codePrefix, "goal.name", this.goal.name).map(name -> {
            this.goal.name = name;
            return null;
          }));
      future = future.compose(empty -> Validations
          .validateNullableStringField(codePrefix, "goal.description", this.goal.description).map(description -> {
            this.goal.description = description;
            return null;
          }));
      future = future.compose(empty -> Validations
          .validateNullableListStringField(codePrefix, "keywords", this.goal.keywords).map(keywords -> {
            this.goal.keywords = keywords;
            return null;
          }));

      future = future.compose(empty -> Validations.validateTimeStamp(codePrefix, "closeTs", this.closeTs, true));
      if (this.closeTs != null && this.closeTs < this._creationTs) {

        promise.fail(
            new ValidationErrorException(codePrefix + ".closeTs", "The 'closeTs' has to be after the '_creationTs'."));

      } else {

        future = future.compose(Validations.validate(this.norms, (a, b) -> a.equals(b), codePrefix + ".norms", vertx));

        // TODO check the attributes fetch the attributes on the type

        promise.complete();

      }
    }

    return future;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Task> merge(final Task source, final String codePrefix, final Vertx vertx) {

    final Promise<Task> promise = Promise.promise();
    var future = promise.future();

    if (source != null) {

      final var merged = new Task();
      merged._creationTs = this._creationTs;
      merged._lastUpdateTs = this._lastUpdateTs;

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

      merged.communityId = source.communityId;
      if (merged.communityId == null) {

        merged.communityId = this.communityId;
      }

      merged.closeTs = source.closeTs;
      if (merged.closeTs == null) {

        merged.closeTs = this.closeTs;
      }

      merged.attributes = Merges.mergeJsonObjects(this.attributes, source.attributes);

      merged.transactions = source.transactions;
      if (merged.transactions == null) {

        merged.transactions = this.transactions;
      }

      merged.goal = source.goal;
      if (merged.goal == null) {

        merged.goal = this.goal;

      } else {

        if (merged.goal.name == null) {

          merged.goal.name = this.goal.name;
        }
        if (merged.goal.description == null) {

          merged.goal.description = this.goal.description;
        }

        if (merged.goal.keywords == null) {

          merged.goal.keywords = this.goal.keywords;
        }

      }

      merged.norms = source.norms;
      if (merged.norms == null) {

        merged.norms = this.norms;
      }

      future = future.compose(Validations.validateChain(codePrefix, vertx));

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
  public Future<Task> update(final Task source, final String codePrefix, final Vertx vertx) {

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

      future = future.compose(Validations.validateChain(codePrefix, vertx));

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