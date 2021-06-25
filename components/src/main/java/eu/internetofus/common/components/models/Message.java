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
import eu.internetofus.common.components.service.App;
import eu.internetofus.common.components.service.WeNetService;
import eu.internetofus.common.model.JsonObjectDeserializer;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import eu.internetofus.common.model.Validable;
import eu.internetofus.common.model.Validations;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * A message to send to an {@link App}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "Message", description = "A message that is send to the user of an application.")
public class Message extends ReflectionModel implements Model, Validable {

  /**
   * The identifier of the application to send the message.
   */
  @Schema(description = "ID of the Wenet application related to the message", example = "822jhluNZP", nullable = true)
  public String appId;

  /**
   * The identifier of the user to receive the message.
   */
  @Schema(description = "The Wenet user ID of the recipient of the message", example = "a6822c47-f1b8-4c21-80bd-1d025266c3c7", nullable = true)
  public String receiverId;

  /**
   * The identifier of the message type.
   */
  @Schema(description = "The type of the message", example = "TaskVolunteerNotification", nullable = true)
  public String label;

  /**
   * The attributes of the message.
   */
  @Schema(type = "object", description = "the attributes of the message, as key-value pairs.", implementation = Object.class, nullable = true)
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  public JsonObject attributes;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();

    future = Validations.composeValidateId(future, codePrefix, "appId", this.appId, true,
        WeNetService.createProxy(vertx)::retrieveApp);
    future = Validations.composeValidateId(future, codePrefix, "receiverId", this.receiverId, true,
        WeNetProfileManager.createProxy(vertx)::retrieveProfile);

    promise.complete();

    return future;
  }

}
