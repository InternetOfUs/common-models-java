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
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.Validable;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import java.util.List;

/**
 * Describe a transition to do over a task.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "TaskTransaction", description = "Describe a transition to do over a task.")
public class TaskTransaction extends CreateUpdateTsDetails implements Model, Validable<WeNetValidateContext> {

  /**
   * The name of the label used for the transaction that mark is the first when
   * the task is created.
   */
  public static final String CREATE_TASK_LABEL = "CREATE_TASK";

  /**
   * The identifier of the task that it refers.
   */
  @Schema(description = "The unique identified of the transaction.", example = "9dihugkdjfgndfg", accessMode = AccessMode.READ_ONLY)
  public String id;

  /**
   * The identifier of the task that it refers.
   */
  @Schema(description = "The identifier of the WeNet task where the transaction is done.", example = "b129e5509c9bb79", nullable = true)
  public String taskId;

  /**
   * The identifier of the task type.
   */
  @Schema(description = "The label that identify the transaction to do.", example = "acceptVolunteer", nullable = true)
  public String label;

  /**
   * The attributes set to the transaction.
   */
  @Schema(type = "object", description = "The attributes that parameterize the transaction.", implementation = Object.class, nullable = true)
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  public JsonObject attributes;

  /**
   * The identifier of the WeNet user who created the transaction.
   */
  @Schema(description = "The identifier of the user that want to change the task.", example = "15837028-645a-4a55-9aaf-ceb846439eba", nullable = true)
  public String actioneerId;

  /**
   * The list of messages that has provokes the execution of this task
   * transaction.
   */
  @ArraySchema(schema = @Schema(implementation = TaskTransaction.class), arraySchema = @Schema(description = "The list of messages that has provokes the execution of this task transaction.", nullable = true))
  public List<Message> messages;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final WeNetValidateContext context) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();
    if (this.actioneerId != null) {

      future = context.validateDefinedProfileIdField("actioneerId", this.actioneerId, future);

    }

    this.label = context.validateStringField("label", this.label, promise);
    if (CREATE_TASK_LABEL.equals(this.label)) {

      if (this.attributes != null && !this.attributes.isEmpty()) {

        return context.failField("attributes", "The created transaction can not have attributes.");

      }

    } else {

      future = future.compose(empty -> context.validateDefinedTaskByIdField("taskId", this.taskId).transform(search -> {

        if (search.failed()) {

          return Future.failedFuture(search.cause());

        } else {

          final var task = search.result();
          return context.validateDefinedTaskTypeByIdField("taskId@taskTypeId", task.taskTypeId)
              .transform(retrieveTaskType -> {

                if (retrieveTaskType.failed()) {

                  return context.failField("taskId@taskTypeId",
                      "The task type of the task '" + this.taskId + "' is not defined.", retrieveTaskType.cause());

                } else {

                  final var taskType = retrieveTaskType.result();
                  if (taskType.transactions == null) {

                    return context.failField("label", "The task type has not defined any transaction.");

                  } else {

                    final var labelDef = taskType.transactions.getJsonObject(this.label, null);
                    if (labelDef == null) {

                      return context.failField("label",
                          "The label '" + this.label + "' is not defined on the type of the task.");

                    } else {

                      return context.validateOpenAPIValueField("attributes", this.attributes, labelDef)
                          .map(validAttributes -> {

                            this.attributes = validAttributes;
                            return null;
                          });
                    }
                  }
                }

              });

        }

      }));
    }

    promise.tryComplete();
    return future;
  }

}
