/*
 * -----------------------------------------------------------------------------
 *
 * Copyright (c) 1994 - 2021 UDT-IA, IIIA-CSIC
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
import eu.internetofus.common.components.CreateUpdateTsDetails;
import eu.internetofus.common.components.JsonObjectDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.json.JsonObject;

/**
 * Model with a state.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(name = "State", description = "Model that describe a state.")
public class State extends CreateUpdateTsDetails {

  /**
   * The identifier of the community.
   */
  @Schema(description = "The identifier of the community associated to the state.", example = "1", nullable = true)
  public String communityId;

  /**
   * The identifier of the community.
   */
  @Schema(description = "The identifier of the task associated to the state.", example = "1", nullable = true)
  public String taskId;

  /**
   * The identifier of the community.
   */
  @Schema(description = "The identifier of the user associated to the state.", example = "1", nullable = true)
  public String userId;

  /**
   * The attributes that define the state.
   */
  @Schema(type = "object", description = "The attributes that define the state.", implementation = Object.class, nullable = true)
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  public JsonObject attributes;

  /**
   * Create a new state.
   */
  public State() {

    this.attributes = new JsonObject();

  }

}
