/*
 * -----------------------------------------------------------------------------
 *
 * Copyright (c) 2019 - 2022 UDT-IA, IIIA-CSIC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * -----------------------------------------------------------------------------
 */

package eu.internetofus.common.components.social_context_builder;

import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ReflectionModel;
import eu.internetofus.common.components.profile_manager.SocialNetworkRelationship;
import eu.internetofus.common.components.profile_manager.SocialNetworkRelationshipType;
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
