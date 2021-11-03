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
package eu.internetofus.common.components.profile_manager;

import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Contains the calculated diversity for a set of users.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(description = "The value of the diversity for a set of users.")
public class DiversityValue extends ReflectionModel implements Model {

  /**
   * The calculate diversity value for a set of users.
   */
  @Schema(description = "The diversity between the set of users. It is in the range [0,1].", example = "0.1571664406")
  public double diversity;

}
