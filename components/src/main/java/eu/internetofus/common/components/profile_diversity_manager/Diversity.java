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
 * The calculated diversity of a set of users.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(title = "Diversity", description = "The diversity of some users.")
public class Diversity extends ReflectionModel implements Model {

  /**
   * The agents diversity.
   */
  @Schema(description = "How the set of agents are diverse. This value is in the range [0,1] where 1 is totally diverse and 0 mean no diversity.", example = "0.524")
  public double value;

}
