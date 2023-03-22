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
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

/**
 * The information of some attributes to calculate the similarity.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(title = "AttributesData", description = "The information of some attributes to calculate the similarity.")
public class AttributesData extends ReflectionModel implements Model {

  /**
   * The agents to calculate the diveristy.
   */
  @Schema(description = "Source string to obtain the similarity.", example = "\"Do you have a bike?\"")
  public String source;

  /**
   * The name of the quantitative attributes.
   */
  @ArraySchema(schema = @Schema(implementation = String.class), arraySchema = @Schema(description = "The name of the attributes to calculate the similarity.", example = "[\"extrovert\",\"naturalist\",\"introvert\"]"))
  public Set<String> attributes;

  /**
   * The name and possible values of the qualitative attributes.
   */
  @Schema(description = "The aggregation function to use on the similarity.", example = "\"max\"")
  public Aggregation aggregation;

}
