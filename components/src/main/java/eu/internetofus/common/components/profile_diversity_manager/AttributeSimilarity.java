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

/**
 * The information of the similarity of an attribute.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(title = "AattributeSimilarity", description = "The information of the similarity of an attribute.")
public class AttributeSimilarity extends ReflectionModel implements Model {

  /**
   * The agent identifier.
   */
  @Schema(description = "The name of the attribute.", example = "\"gender\"")
  public String attribute;

  /**
   * The agent identifier.
   */
  @Schema(description = "The similarity of the attribute.", example = "0.56")
  public double similarity;

}
