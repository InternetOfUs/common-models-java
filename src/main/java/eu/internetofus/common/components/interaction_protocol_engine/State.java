/*
 * -----------------------------------------------------------------------------
 *
 *   Copyright 2019 - 2022 UDT-IA, IIIA-CSIC
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
import eu.internetofus.common.components.JsonObjectDeserializer;
import eu.internetofus.common.components.models.CreateUpdateTsDetails;
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
