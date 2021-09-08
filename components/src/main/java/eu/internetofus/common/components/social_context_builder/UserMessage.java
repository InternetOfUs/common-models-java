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
package eu.internetofus.common.components.social_context_builder;

import eu.internetofus.common.components.models.Message;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A message that is send into an user.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "user_message", description = "A message that is interchanges between users.")
public class UserMessage extends ReflectionModel implements Model {

  /**
   * The first user identifier.
   */
  @Schema(description = "Identifier of the task where the message is interchanged.", example = "bf274393")
  public String taskId;

  /**
   * The first user identifier.
   */
  @Schema(description = "Identifier of the transaction where the message is send.", example = "2")
  public String transactionId;

  /**
   * The difference, measured in seconds, between the time when the interaction
   * has been done and midnight, January 1, 1970 UTC.
   */
  @Schema(description = "The UTC epoch timestamp representing the time when the interaction has been done.", example = "274393")
  public Long timestamp;

  /**
   * The first user identifier.
   */
  @Schema(description = "Identifier of the user that has send the message.", example = "3")
  public String senderId;

  /**
   * The first user identifier.
   */
  @Schema(description = "The message that has been interchanged.")
  public Message message;

}
