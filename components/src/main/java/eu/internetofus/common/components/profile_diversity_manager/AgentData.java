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

package eu.internetofus.common.components.profile_diversity_manager;

import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

/**
 * The information of an agent to calculate the diversity.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(name = "AgentData", description = "The information of an agent to calculate the diversity.")
public class AgentData extends ReflectionModel implements Model {

  /**
   * The agent identifier.
   */
  @Schema(description = "The identifier of the agent.", example = "\"1\"")
  public String id;

  /**
   * The quantitative attribute values.
   */
  @Schema(description = "The quantitative attribute values.", example = "{\"extrovert\":0.5,\"naturalist\":0.7,\"introvert\":0.9}")
  public Map<String, Double> quantitativeAttributes;

  /**
   * The qualitative attribute values.
   */
  @Schema(description = "The qualitative attribute values.", example = "{\"gender\":\"F\",\"civilStatus\":\"single\"}")
  public Map<String, String> qualitativeAttributes;

}
