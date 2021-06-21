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

package eu.internetofus.common.components.social_context_builder;

import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ReflectionModel;
import eu.internetofus.common.components.models.SocialNetworkRelationship;
import eu.internetofus.common.components.models.SocialNetworkRelationshipType;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The calculated user relation by the social context builder.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "user_relation", description = "A User to User relation.")
public class UserRelation extends ReflectionModel implements Model {

  /**
   * The first user identifier.
   */
  @Schema(description = "User ID 1 a unique identifier.", example = "bf274393-1e7b-4d40-a897-88cb96277edd")
  public String UserID1;

  /**
   * The second user identifier.
   */
  @Schema(description = "User ID 2 a unique identifier.", example = "bf274393-1e7b-4d40-a897-88cb96277edd")
  public String UserID2;

  /**
   * The type of relation.
   */
  @Schema(description = "The relation type among the two users.", example = "contact")
  public int RelationType;

  /**
   * The weight of the relation.
   */
  @Schema(description = "A number from 0 to 1 that indicates the strength of the relation. 0 indicates a deleted/non-exisiting relation.", example = "0.6")
  public double Weight;

  /**
   * The application where the relation is obtained.
   */
  @Schema(description = "An AppID that indicates where the relation originated from.", example = "Facebook")
  public String SourceID;

  /**
   * Convert to a {@link SocialNetworkRelationship}.
   *
   * @return the relationship that can be inferred of the relation.
   */
  public SocialNetworkRelationship toSocialNetworkRelationship() {

    final var relationship = new SocialNetworkRelationship();
    relationship.userId = this.UserID2;
    relationship.type = SocialNetworkRelationshipType.acquaintance;
    return relationship;
  }

}
