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
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import eu.internetofus.common.model.CreateUpdateTsDetails;
import eu.internetofus.common.model.JsonObjectDeserializer;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.Validable;
import eu.internetofus.common.model.ValidationErrorException;
import eu.internetofus.common.model.Validations;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import java.util.HashSet;
import java.util.List;

/**
 * Describe a transition to do over a task.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "TaskTransaction", description = "Describe a transition to do over a task.")
public class TaskTransaction extends CreateUpdateTsDetails implements Model, Validable {

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
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();

    if (this.actioneerId != null) {

      future = Validations.composeValidateId(future, codePrefix, "actioneerId", this.actioneerId, true,
          WeNetProfileManager.createProxy(vertx)::retrieveProfile);

    }

    final Promise<Void> checkLabel = Promise.promise();
    future = future.compose(empty -> checkLabel.future());

    if (this.label == null) {

      promise.fail(new ValidationErrorException(codePrefix + ".label", "The transaction needs a label."));

    } else {

      WeNetTaskManager.createProxy(vertx).retrieveTask(this.taskId).onComplete(retrieveTask -> {

        if (retrieveTask.failed()) {

          checkLabel.fail(new ValidationErrorException(codePrefix + ".taskId",
              "The task '" + this.taskId + "' is not defined.", retrieveTask.cause()));

        } else {

          final var task = retrieveTask.result();
          WeNetTaskManager.createProxy(vertx).retrieveTaskType(task.taskTypeId).onComplete(retrieveTaskType -> {

            if (retrieveTaskType.failed()) {

              checkLabel.fail(new ValidationErrorException(codePrefix + ".taskId",
                  "The task type of the task '" + this.taskId + "' is not defined.", retrieveTaskType.cause()));

            } else {

              final var taskType = retrieveTaskType.result();
              if (taskType.transactions == null) {

                checkLabel.fail(new ValidationErrorException(codePrefix + ".label",
                    "The task type has not defined any transaction."));

              } else {

                final var labelDef = taskType.transactions.getJsonObject(this.label, null);
                if (labelDef == null) {

                  checkLabel.fail(new ValidationErrorException(codePrefix + ".label",
                      "The label '" + this.label + "' is not defined on the type of the task."));

                } else {

                  final var attributesDef = labelDef.getJsonObject("properties", new JsonObject());
                  if (attributesDef.isEmpty() && this.attributes != null && !this.attributes.isEmpty()) {

                    checkLabel.fail(new ValidationErrorException(codePrefix + ".attributes",
                        "The transaction does not allow to have attributes."));

                  } else if (this.attributes == null || this.attributes.isEmpty()) {

                    checkLabel.fail(new ValidationErrorException(codePrefix + ".attributes",
                        "The transaction need the attributes " + attributesDef.fieldNames() + "."));

                  } else {

                    final var fields = new HashSet<>(attributesDef.fieldNames());
                    for (final var attribute : this.attributes.fieldNames()) {

                      if (!fields.remove(attribute)) {

                        checkLabel.fail(new ValidationErrorException(codePrefix + ".attributes." + attribute,
                            "The transaction does not have defined this attribute."));
                        break;
                      }

                    }

                    if (!fields.isEmpty()) {

                      checkLabel.fail(new ValidationErrorException(codePrefix + ".attributes",
                          "The attributes " + fields + " are not defined on the transaction."));

                    } else {

                      checkLabel.tryComplete();
                    }
                  }
                }
              }
            }

          });

        }

      });

      promise.complete();

    }

    return future;
  }

}
