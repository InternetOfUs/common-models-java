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
import eu.internetofus.common.components.JsonObjectDeserializer;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.ValidationErrorException;
import eu.internetofus.common.components.Validations;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.components.service.Message;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Describe a transition to do over a task.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "TaskTransaction", description = "Describe a transition to do over a task.")
public class TaskTransaction extends CreateUpdateTsDetails implements Model, Validable {

  /**
   * The identifier of the task that it refers.
   */
  @Schema(description = "The unique identified of the transaction.", example = "9dihugkdjfgndfg")
  public String id;

  /**
   * The identifier of the task that it refers.
   */
  @Schema(description = "The identifier of the WeNet task where the transaction is done.", example = "b129e5509c9bb79")
  public String taskId;

  /**
   * The identifier of the task type.
   */
  @Schema(description = "The label that identify the transaction to do.", example = "acceptVolunteer")
  public String label;

  /**
   * The attributes set to the transaction.
   */
  @Schema(type = "object", description = "The attributes that parameterize the transaction.")
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  public JsonObject attributes;

  /**
   * The identifier of the WeNet user who created the transaction.
   */
  @Schema(description = "The identifier of the user that want to change the task.", example = "15837028-645a-4a55-9aaf-ceb846439eba")
  public String actioneerId;

  /**
   * The list of messages that has provokes the execution of this task
   * transaction.
   */
  @ArraySchema(schema = @Schema(implementation = TaskTransaction.class), arraySchema = @Schema(description = "The list of messages that has provokes the execution of this task transaction."))
  public List<Message> messages;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();
    try {

      this.taskId = Validations.validateStringField(codePrefix, "taskId", 255, this.taskId);
      future = Validations.composeValidateId(future, codePrefix, "taskId", this.taskId, true,
          WeNetTaskManager.createProxy(vertx)::retrieveTask);

      this.actioneerId = Validations.validateNullableStringField(codePrefix, "actioneerId", 255, this.actioneerId);
      if (this.actioneerId != null) {

        future = Validations.composeValidateId(future, codePrefix, "actioneerId", this.actioneerId, true,
            WeNetProfileManager.createProxy(vertx)::retrieveProfile);

      }
      this.label = Validations.validateStringField(codePrefix, "label", 255, this.label);

      // TODO verify the attributes are valid for the task transaction type
      promise.complete();

    } catch (final ValidationErrorException validationError) {

      promise.fail(validationError);
    }

    return future;
  }

}
