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

package eu.internetofus.common.components.interaction_protocol_engine;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eu.internetofus.common.components.JsonObjectDeserializer;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ReflectionModel;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.ValidationErrorException;
import eu.internetofus.common.components.Validations;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.components.service.WeNetService;
import eu.internetofus.common.components.task_manager.TaskTransaction;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * An action that happens into a protocol.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class AbstractProtocolAction extends ReflectionModel implements Model, Validable {

  /**
   * The identifier of the application associated to the protocol.
   */
  @Schema(description = "The identifier of the application associated to the protocol.", nullable = true, example = "E34jhg78tbgh")
  public String appId;

  /**
   * The identifier of the community associated to the protocol.
   */
  @Schema(description = "The identifier of the community associated to the protocol.", nullable = true, example = "ceb846439eba-645a-9aaf-4a55-15837028")
  public String communityId;

  /**
   * The identifier of the task associated to the protocol.
   */
  @Schema(description = "The identifier of the task associated to the protocol.", nullable = true, example = "b129e5509c9bb79")
  public String taskId;

  /**
   * The identifier of the task associated to the protocol.
   */
  @Schema(description = "The identifier of the transaction associated to the protocol.", nullable = true, example = "b129e5509c9bb79")
  public String transactionId;

  /**
   * The particle of the action.
   */
  @Schema(description = "The particle that define the motive of the action.", example = "inform")
  public String particle;

  /**
   * The content of the action.
   */
  @Schema(description = "The content of the action.", example = "{\"entered\":true}", type = "object", nullable = true, implementation = Object.class)
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  public JsonObject content;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();
    try {

      this.appId = Validations.validateNullableStringField(codePrefix, "appId", 255, this.appId);
      this.communityId = Validations.validateNullableStringField(codePrefix, "communityId", 255, this.communityId);
      this.taskId = Validations.validateNullableStringField(codePrefix, "taskId", 255, this.taskId);
      this.transactionId = Validations.validateNullableStringField(codePrefix, "transactionId", 255,
          this.transactionId);
      this.particle = Validations.validateStringField(codePrefix, "particle", 255, this.particle);

      if (this.appId != null) {

        future = Validations.composeValidateId(future, codePrefix, "appId", this.appId, true,
            WeNetService.createProxy(vertx)::retrieveApp);

      }

      if (this.communityId != null) {

        future = Validations.composeValidateId(future, codePrefix, "communityId", this.communityId, true,
            WeNetProfileManager.createProxy(vertx)::retrieveCommunity);

      }

      if (this.taskId != null) {

        future = future.compose(empty -> {

          final Promise<Void> taskPromise = Promise.promise();
          WeNetTaskManager.createProxy(vertx).retrieveTask(this.taskId).onComplete(retrieve -> {

            if (retrieve.failed()) {

              taskPromise.fail(new ValidationErrorException(codePrefix + ".taskId",
                  "Cannot found task associated to the identifier.", retrieve.cause()));

            } else {

              final var task = retrieve.result();
              if (this.transactionId == null) {

                taskPromise.complete();

              } else {

                var found = false;
                if (task.transactions != null) {

                  for (final TaskTransaction transaction : task.transactions) {

                    if (this.transactionId.equals(transaction.id)) {

                      found = true;
                      break;
                    }
                  }
                }

                if (!found) {

                  taskPromise.fail(new ValidationErrorException(codePrefix + ".transactionId",
                      "Cannot found transaction associated to the identifier."));

                } else {

                  taskPromise.complete();
                }
              }

            }

          });

          return taskPromise.future();

        });

      }

      if (this.transactionId != null && this.taskId == null) {

        promise.fail(new ValidationErrorException(codePrefix + ".transactionId",
            "You must to define the task identifier ('taskId') where the transaction is"));

      } else if (this.content == null) {

        promise.fail(new ValidationErrorException(codePrefix + ".content", "You must to define a content"));

      } else {

        promise.complete();

      }

    } catch (final ValidationErrorException validationError) {

      promise.fail(validationError);
    }

    return future;

  }

}
