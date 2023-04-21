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

import eu.internetofus.common.components.models.WeNetUserProfile;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A previous profile of the user in the time.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(title = "HistoricWeNetUserProfile", description = "The information of the user profile in a period of time.")
public class HistoricWeNetUserProfile extends ReflectionModel implements Model {

  /**
   * The difference, measured in milliseconds, between the time when the profile
   * is modified to have the current state and midnight, January 1, 1970 UTC.
   */
  @Schema(description = "The difference, measured in seconds, between the time when the profile is modified to have the current state and midnight, January 1, 1970 UTC.", example = "1457166440")
  public long from;

  /**
   * The difference, measured in milliseconds, between the time when the profile
   * is not more valid and midnight, January 1, 1970 UTC.
   */
  @Schema(description = "The difference, measured in seconds, between the time when the profile is not more valid and midnight, January 1, 1970 UTC.", example = "1571664406")
  public long to;

  /**
   * The profile on the period of time
   */
  @Schema(description = "The profile on the period of time.", ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.4.0/sources/wenet-models-openapi.yaml#/components/schemas/WeNetUserProfile")
  public WeNetUserProfile profile;

}
