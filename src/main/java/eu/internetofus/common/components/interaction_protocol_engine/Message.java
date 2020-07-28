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
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
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

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import eu.internetofus.common.components.JsonObjectDeserializer;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.ValidationErrorException;
import eu.internetofus.common.components.Validations;
import eu.internetofus.common.components.incentive_server.Incentive;
import eu.internetofus.common.components.profile_manager.Norm;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.components.service.WeNetService;
import eu.internetofus.common.components.task_manager.Task;
import eu.internetofus.common.components.task_manager.TaskTransaction;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * A message that can be interchange in an interaction protocol.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(name = "message", description = "A message that can be interchange in an interaction protocol.")
public class Message extends Model implements Validable {

  /**
   * The identifier of the user that is sending the message.
   */
  @Schema(description = "The identifier of the user that send the message", example = "15837028-645a-4a55-9aaf-ceb846439eba")
  public String senderId;

  /**
   * The identifier of the application that the user has used to send the message.
   */
  @Schema(description = "The identifier of the application that has used the sender to send this message.", example = "E34jhg78tbgh")
  public String appId;

  /**
   * The identifier of the community where the message will be said.
   */
  @Schema(description = "The identifier of the application that has used the sender to send this message.", example = "ceb846439eba-645a-9aaf-4a55-15837028")
  public String communityId;

  /**
   * The identifier of the task that the message is related.
   */
  @Schema(description = "The identifier of the application that has used the sender to send this message.", example = "b129e5509c9bb79")
  public String taskId;

  /**
   * The content of the message.
   */
  @Schema(description = "The content of the message.", example = "Hi!", type = "object")
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  public Object content;

  /**
   * The norms that has to be applied over the message.
   */
  @ArraySchema(schema = @Schema(ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Norm"), arraySchema = @Schema(description = "The norms to apply over the message"))
  public List<Norm> norms;

  /**
   * The type of message that is represented.
   */
  @Schema(description = "The type of message.", example = "TASK_TRANSACTION")
  public Type type;

  /**
   * The possible types of the message.
   */
  public enum Type {
    /**
     * The message is to inform to a task is created.
     */
    TASK_CREATED,
    /**
     * The message is a transaction that has to be done in a task.
     */
    TASK_TRANSACTION,
    /**
     * The message is an incentive to send to an user.
     */
    INCENTIVE,
    /**
     * The message is used to check the interaction with the SWIProlog.
     */
    SWI_PROLOG;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    Future<Void> future = promise.future();
    try {

      this.appId = Validations.validateNullableStringField(codePrefix, "appId", 255, this.appId);
      if (this.appId != null) {

        future = future.compose(val -> {

          final Promise<Void> checkExistApp = Promise.promise();
          WeNetService.createProxy(vertx).retrieveApp(this.appId, retrieve -> {

            if (retrieve.failed()) {

              checkExistApp.fail(new ValidationErrorException(codePrefix + ".appId", "No found application associated to the specified identifier"));

            } else {

              checkExistApp.complete();
            }

          });

          return checkExistApp.future();
        });

      }

      this.communityId = Validations.validateNullableStringField(codePrefix, "communityId", 255, this.communityId);

      this.taskId = Validations.validateNullableStringField(codePrefix, "taskId", 255, this.taskId);
      if (this.taskId != null) {

        future = future.compose(val -> {

          final Promise<Void> checkExistTask = Promise.promise();
          WeNetTaskManager.createProxy(vertx).retrieveTask(this.taskId, retrieve -> {

            if (retrieve.failed()) {

              checkExistTask.fail(new ValidationErrorException(codePrefix + ".taskId", "No found task associated to the specified identifier"));

            } else {

              checkExistTask.complete();
            }

          });

          return checkExistTask.future();
        });
      }

      this.senderId = Validations.validateNullableStringField(codePrefix, "senderId", 255, this.senderId);

      if (this.senderId != null) {

        future = future.compose(val -> {

          final Promise<Void> checkExistSender = Promise.promise();
          WeNetProfileManager.createProxy(vertx).retrieveProfile(this.senderId, retrieve -> {

            if (retrieve.failed()) {

              checkExistSender.fail(new ValidationErrorException(codePrefix + ".senderId", "No found user associated to the specified identifier"));

            } else {

              checkExistSender.complete();
            }

          });

          return checkExistSender.future();
        });
      }

      if (this.content == null) {

        promise.fail(new ValidationErrorException(codePrefix + ".content", "You must to define a content"));

      } else if (this.type == null) {

        promise.fail(new ValidationErrorException(codePrefix + ".type", "You must to define a type"));

      } else {

        switch (this.type) {
        case TASK_CREATED:
          final Task task = Model.fromJsonObject((JsonObject) this.content, Task.class);
          if (task == null) {

            promise.fail(new ValidationErrorException(codePrefix + ".content", "You must define the created task"));

          } else if (this.taskId != null && !this.taskId.equals(task.id)) {

            promise.fail(new ValidationErrorException(codePrefix + ".content", "The mesage taskId not match the taskId of the task defined on the content"));

          } else {

            future = future.compose(mapper -> {

              final Promise<Void> verifyEqualsStoredPromise = Promise.promise();
              WeNetTaskManager.createProxy(vertx).retrieveTask(this.taskId, storedTask -> {

                if (storedTask.failed()) {

                  verifyEqualsStoredPromise.fail(new ValidationErrorException(codePrefix + ".content", "No found task associated to the specified identifier"));

                } else if (!task.equals(storedTask.result())) {

                  verifyEqualsStoredPromise.fail(new ValidationErrorException(codePrefix + ".content", "The task on the content is not equals to the stored one."));

                } else {

                  verifyEqualsStoredPromise.complete();

                }
              });
              return verifyEqualsStoredPromise.future();
            });
          }
          break;

        case TASK_TRANSACTION:
          final TaskTransaction transaction = Model.fromJsonObject((JsonObject) this.content, TaskTransaction.class);
          if (transaction == null) {

            promise.fail(new ValidationErrorException(codePrefix + ".content", "You must define a task transaction as content"));

          } else if (this.taskId != null && !this.taskId.equals(transaction.taskId)) {

            promise.fail(new ValidationErrorException(codePrefix + ".content", "The mesage taskId not match the taskId of the transaction defined on the content"));

          } else {

            future = future.compose(val -> transaction.validate(codePrefix + ".content", vertx));
          }
          break;
        case INCENTIVE:
          final Incentive incentive = Model.fromJsonObject((JsonObject) this.content, Incentive.class);
          if (incentive == null) {

            promise.fail(new ValidationErrorException(codePrefix + ".content", "You must define an incentive as content"));

          } else if (this.appId != null && !this.appId.equals(incentive.AppID)) {

            promise.fail(new ValidationErrorException(codePrefix + ".content", "The mesage appId not match the AppID of the incentive defined on the content"));

          } else {

            future = future.compose(val -> incentive.validate(codePrefix + ".content", vertx));
          }
          break;
        default:
          //Nothing to check.
        }

        future = future.compose(Validations.validate(this.norms, (a, b) -> a.equals(b), codePrefix + ".norms", vertx));

        promise.tryComplete();

      }

    } catch (final ValidationErrorException validationError) {

      promise.fail(validationError);
    }

    return future;

  }

}
