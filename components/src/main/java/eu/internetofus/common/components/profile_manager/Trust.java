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
 * Contains information of the calculated trust.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(description = "The calculated trust between two users.")
public class Trust extends ReflectionModel implements Model {

  /**
   * The trust over the user. It has to be on the range {@code [0,1]}.
   */
  @Schema(description = "The trust over an user respect another. It has to be on the range [0,1].", example = "0.43")
  public Number value;

  /**
   * The time when the trust was calculated.
   */
  @Schema(description = "The difference, measured in seconds, between the trust was calculated and midnight, January 1, 1970 UTC.", example = "1571412479710")
  public long calculatedTime;

}
