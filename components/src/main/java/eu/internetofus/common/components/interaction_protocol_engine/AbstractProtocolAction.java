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

package eu.internetofus.common.components.interaction_protocol_engine;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eu.internetofus.common.components.WeNetValidateContext;
import eu.internetofus.common.components.models.TaskTransaction;
import eu.internetofus.common.model.JsonObjectDeserializer;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import eu.internetofus.common.model.Validable;
import eu.internetofus.common.model.ValidationErrorException;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

/**
 * An action that happens into a protocol.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class AbstractProtocolAction extends ReflectionModel implements Model, Validable<WeNetValidateContext> {

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
  public Future<Void> validate(final WeNetValidateContext context) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();
    try {

      if (this.appId != null) {

        future = context.validateDefinedAppIdField("appId", this.appId, future);

      }

      if (this.communityId != null) {

        future = context.validateDefinedCommunityIdField("communityId", this.communityId, future);

      }

      if (this.taskId != null) {

        future = future
            .compose(empty -> context.validateDefinedTaskByIdField("taskId", this.taskId).transform(search -> {

              if (search.failed()) {

                return Future.failedFuture(search.cause());

              } else {

                final var task = search.result();
                if (this.transactionId != null) {

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

                    return context.failField("transactionId", "Cannot found transaction associated to the identifier.");
                  }
                }
                return Future.succeededFuture();
              }

            }));

      }

      this.particle = context.validateStringField("particle", this.particle, promise);

      if (this.transactionId != null && this.taskId == null) {

        return context.fail("transactionId",
            "You must to define the task identifier ('taskId') where the transaction is associated.");

      } else if (this.content == null) {

        return context.fail("content", "You must to define a content");

      } else {

        promise.tryComplete();

      }

    } catch (final ValidationErrorException validationError) {

      promise.fail(validationError);
    }

    return future;

  }

}
