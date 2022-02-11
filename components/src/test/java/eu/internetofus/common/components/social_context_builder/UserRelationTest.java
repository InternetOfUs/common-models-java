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

package eu.internetofus.common.components.social_context_builder;

import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.models.SocialNetworkRelationshipType;
import eu.internetofus.common.model.ModelTestCase;
import org.junit.jupiter.api.Test;

/**
 * Test the {@link UserRelation}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class UserRelationTest extends ModelTestCase<UserRelation> {

  /**
   * {@inheritDoc}
   */
  @Override
  public UserRelation createModelExample(final int index) {

    final var model = new UserRelation();
    model.UserID1 = "userId2_" + index;
    model.UserID2 = "userId1_" + index;
    model.RelationType = index;
    model.Weight = 1.0 / Math.max(0.0, index);
    model.SourceID = "SourceId_" + index;
    return model;

  }

  /**
   * Check that can convert
   */
  @Test
  public void shouldConvertToSocialNetworkRelationship() {

    final var model = this.createModelExample(1);
    final var relation = model.toSocialNetworkRelationship();
    assertThat(relation).isNotNull();
    assertThat(relation.sourceId).isEqualTo(model.UserID1);
    assertThat(relation.targetId).isEqualTo(model.UserID2);
    assertThat(relation.type).isEqualTo(SocialNetworkRelationshipType.acquaintance);

  }

}
