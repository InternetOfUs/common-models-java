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

package eu.internetofus.common.components.service;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eu.internetofus.common.components.JsonObjectDeserializer;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ReflectionModel;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.json.JsonObject;

/**
 * A message to send to an {@link App}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "Message", description = "A message that is send to the user of an application.")
public class Message extends ReflectionModel implements Model {

  /**
   * The identifier of the application to send the message.
   */
  @Schema(description = "ID of the Wenet application related to the message", example = "822jhluNZP")
  public String appId;

  /**
   * The identifier of the user to receive the message.
   */
  @Schema(description = "The Wenet user ID of the recipient of the message", example = "a6822c47-f1b8-4c21-80bd-1d025266c3c7")
  public String receiverId;

  /**
   * The identifier of the message type.
   */
  @Schema(description = "The type of the message", example = "TaskVolunteerNotification")
  public String label;

  /**
   * The attributes of the message.
   */
  @Schema(type = "object", description = "the attributes of the message, as key-value pairs.", implementation = Object.class)
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  public JsonObject attributes;

}
