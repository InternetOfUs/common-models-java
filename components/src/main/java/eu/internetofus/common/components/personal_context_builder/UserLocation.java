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

package eu.internetofus.common.components.personal_context_builder;

import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The location of an user.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "UserLocation", description = "The location of an user.")
public class UserLocation extends ReflectionModel implements Model {

  /**
   * The identifier of the user.
   */
  @Schema(description = "The Id of the user that this is its location.", example = "3e557acc-e846-4736-8218-3f64d8e68d8c")
  public String userId;

  /**
   * The latitude of the location.
   */
  @Schema(description = "The latitude of the location", example = "40.388756")
  public double latitude;

  /**
   * The longitude of the location.
   */
  @Schema(description = "The longitude of the location", example = "-3.588622")
  public double longitude;

}
