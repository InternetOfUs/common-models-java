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
import eu.internetofus.common.model.JsonObjectDeserializer;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.json.JsonObject;

/**
 * Provide information about the interaction between users.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(title = "Interaction", description = "Provide information of an interaction between two users")
public class Interaction extends ReflectionModel implements Model {

  /**
   * The application identifier where the interaction happens.
   */
  @Schema(description = "The identifier of the application type where the interaction is done.", example = "b129e5509c9bb79", nullable = true)
  public String appId;

  /**
   * The community identifier where the interaction happens.
   */
  @Schema(description = "The identifier of the community where the interaction is done.", example = "b129e5509c9bb79", nullable = true)
  public String communityId;

  /**
   * The identifier of the task that it refers.
   */
  @Schema(description = "The identifier of the task type where the interaction is done.", example = "b129e5509c9bb79", nullable = true)
  public String taskTypeId;

  /**
   * The identifier of the task that it refers.
   */
  @Schema(description = "The identifier of the task where the interaction is done.", example = "b129e5509c9bb79", nullable = true)
  public String taskId;

  /**
   * The identifier of the user that starts the interaction.
   */
  @Schema(description = "The identifier of the user that starts this interaction.", example = "b129e5509c9bb79", nullable = true)
  public String senderId;

  /**
   * The identifier of the user that ends the interaction.
   */
  @Schema(description = "The identifier of the user that ends this interaction.", example = "b129e5509c9bb79", nullable = true)
  public String receiverId;

  /**
   * The label of the transaction that has started the interaction.
   */
  @Schema(description = "The label of the transaction that has started the interaction.", example = "askMoreUsers", nullable = true)
  public String transactionLabel;

  /**
   * The attributes of the message.
   */
  @Schema(type = "object", description = "the attributes of the transaction, as key-value pairs.", implementation = Object.class, nullable = true)
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  public JsonObject transactionAttributes;

  /**
   * The difference, measured in seconds, between the time when the transaction
   * has been done and midnight, January 1, 1970 UTC.
   */
  @Schema(description = "The UTC epoch timestamp representing the time when the transaction has been done.", example = "1563930000", nullable = true)
  public Long transactionTs;

  /**
   * The message that has end the interaction.
   */
  @Schema(description = "The label of the message that has started the interaction.", example = "askMoreUsers", nullable = true)
  public String messageLabel;

  /**
   * The attributes of the message.
   */
  @Schema(type = "object", description = "the attributes of the message, as key-value pairs.", implementation = Object.class, nullable = true)
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  public JsonObject messageAttributes;

  /**
   * The difference, measured in seconds, between the time when the message has
   * been sent and midnight, January 1, 1970 UTC.
   */
  @Schema(description = "The UTC epoch timestamp representing the time when the message has been sent.", example = "1563930000", nullable = true)
  public Long messageTs;

}
