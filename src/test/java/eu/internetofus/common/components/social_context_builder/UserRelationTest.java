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
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import eu.internetofus.common.components.ModelTestCase;
import eu.internetofus.common.components.profile_manager.SocialNetworkRelationship;
import eu.internetofus.common.components.profile_manager.SocialNetworkRelationshipType;

/**
 * Test the {@link UserRelation}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class UserRelationTest extends ModelTestCase<UserRelation> {

  /**
   * {@inheridDoc}
   */
  @Override
  public UserRelation createModelExample(final int index) {

    final UserRelation model = new UserRelation();
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

    final UserRelation model = this.createModelExample(1);
    final SocialNetworkRelationship relation = model.toSocialNetworkRelationship();
    assertThat(relation).isNotNull();
    assertThat(relation.userId).isEqualTo(model.UserID2);
    assertThat(relation.type).isEqualTo(SocialNetworkRelationshipType.acquaintance);

  }

}
