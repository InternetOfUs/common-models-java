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

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import eu.internetofus.common.components.CreateUpdateTsDetails;
import eu.internetofus.common.components.HumanDescription;
import eu.internetofus.common.components.JsonObjectDeserializer;
import eu.internetofus.common.components.Mergeable;
import eu.internetofus.common.components.Merges;
import eu.internetofus.common.components.Updateable;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.ValidationErrorException;
import eu.internetofus.common.components.Validations;
import eu.internetofus.common.components.profile_manager.Norm;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.components.service.WeNetService;
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
public class Task extends CreateUpdateTsDetails implements Validable, Mergeable<Task>, Updateable<Task> {

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
   * The identifier of the application where the task is done.
   */
  @Schema(description = "The identifier of the application where the task is done.", example = "yub129e5509bb79")
  public String appId;

  /**
   * The identifier of the community where the task is done.
   */
  @Schema(description = "The identifier of the community where the task is done.", example = "b846439eba79")
  public String communityId;

  /**
   * The identifier of the WeNet user who created the task.
   */
  @Schema(description = "The identifier of the WeNet user who created the task.", example = "15837028-645a-4a55-9aaf-ceb846439eba")
  public String requesterId;

  /**
   * The explanation of the task objective.
   */
  @Schema(description = "The explanation of the task objective.")
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
  @ArraySchema(schema = @Schema(implementation = Norm.class), arraySchema = @Schema(description = "The set of norms that define the interaction of the user when do the task."))
  public List<Norm> norms;

  /**
   * The set of norms that define the interaction of the user when do the task.
   */
  @Schema(type = "object", description = "The set of norms that define the interaction of the user when do the task.")
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  public JsonObject attributes;

  /**
   * The list of historical transactions that has been done in this task.
   */
  @ArraySchema(schema = @Schema(implementation = TaskTransaction.class), arraySchema = @Schema(description = "List of historical transactions that has been done in this task."))
  public List<TaskTransaction> transactions;

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
            WeNetTaskManager.createProxy(vertx)::retrieveTask);
      }

      this.taskTypeId = Validations.validateStringField(codePrefix, "taskTypeId", 255, this.taskTypeId);
      future = Validations.composeValidateId(future, codePrefix, "taskTypeId", this.taskTypeId, true,
          WeNetTaskManager.createProxy(vertx)::retrieveTaskType);

      this.requesterId = Validations.validateStringField(codePrefix, "requesterId", 255, this.requesterId);
      future = Validations.composeValidateId(future, codePrefix, "requesterId", this.requesterId, true,
          WeNetProfileManager.createProxy(vertx)::retrieveProfile);

      this.appId = Validations.validateStringField(codePrefix, "appId", 255, this.appId);
      future = Validations.composeValidateId(future, codePrefix, "appId", this.appId, true,
          WeNetService.createProxy(vertx)::retrieveApp);

      this.communityId = Validations.validateStringField(codePrefix, "communityId", 255, this.communityId);
      future = Validations.composeValidateId(future, codePrefix, "communityId", this.communityId, true,
          WeNetProfileManager.createProxy(vertx)::retrieveCommunity);

      if (this.goal == null) {

        promise.fail(new ValidationErrorException(codePrefix + ".goal", "You must to define the 'goal' of the task."));

      } else {

        this.goal.name = Validations.validateStringField(codePrefix, "goal.name", 255, this.goal.name);
        this.goal.description = Validations.validateNullableStringField(codePrefix, "goal.description", 1023,
            this.goal.description);
        this.goal.keywords = Validations.validateNullableListStringField(codePrefix, "goal.keywords", 255,
            this.goal.keywords);

        this.closeTs = Validations.validateTimeStamp(codePrefix, "closeTs", this.closeTs, true);
        if (this.closeTs != null && this.closeTs < this._creationTs) {

          promise.fail(new ValidationErrorException(codePrefix + ".closeTs",
              "The 'closeTs' has to be after the '_creationTs'."));

        } else {

          future = future
              .compose(Validations.validate(this.norms, (a, b) -> a.equals(b), codePrefix + ".norms", vertx));

          // TODO check the attributes fetch the attributes on the type

          promise.complete();

        }
      }

    } catch (final ValidationErrorException validationError) {

      promise.fail(validationError);
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

      merged.attributes = source.attributes;
      if (merged.attributes == null) {

        merged.attributes = this.attributes;
      }

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

      future = future
          .compose(Merges.mergeNorms(this.norms, source.norms, codePrefix + ".norms", vertx, (model, mergedNorms) -> {
            model.norms = mergedNorms;
          }));

      future = future.compose(Validations.validateChain(codePrefix, vertx));

      promise.complete(merged);

      // When merged set the fixed field values
      future = future.map(mergedValidatedModel -> {

        mergedValidatedModel.id = this.id;
        mergedValidatedModel._creationTs = this._creationTs;
        mergedValidatedModel._lastUpdateTs = this._lastUpdateTs;
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
        updatedValidatedModel._creationTs = this._creationTs;
        updatedValidatedModel._lastUpdateTs = this._lastUpdateTs;
        return updatedValidatedModel;
      });

      promise.complete(updated);

    } else {

      promise.complete(this);
    }
    return future;
  }

}