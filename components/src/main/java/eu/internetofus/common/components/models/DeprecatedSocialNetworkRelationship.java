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

package eu.internetofus.common.components.models;

import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The previous social network relationships.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, description = "A social relationship between two WeNet user.")
public class DeprecatedSocialNetworkRelationship extends ReflectionModel implements Model {

  /**
   * The identifier of the application where the relation happens.
   */
  @Schema(description = "The identifier of the application where the relation happens", example = "4c51ee0b", nullable = true)
  public String appId;

  /**
   * The identifier of the WeNet user the relationship is related to the source.
   */
  @Schema(description = "The identifier of the WeNet user the relationship is related", example = "2", nullable = true)
  public String userId;

  /**
   * The relationship type.
   */
  @Schema(description = "The relationship type", example = "friend", nullable = true)
  public SocialNetworkRelationshipType type;

  /**
   * The weight of the relation.
   */
  @Schema(description = "A number from 0 to 1 that indicates the strength of the relation. 0 indicates a deleted/non-exisiting relation.", example = "0.2", nullable = true)
  public Double weight;

}
