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
package eu.internetofus.common.protocols;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import org.tinylog.Logger;

/**
 * Test the pilot M46 at AAU whne is selected at least one dimension.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractPilotM46AAUProtocolWithDimensionITC extends AbstractPilotM46AAUProtocolITC {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean validMatchUsers(final JsonObject state) {

    final var domain = Domain.valueOf(this.domain().toUpperCase());
    final var matchUsers = state.getJsonArray("matchUsers", new JsonArray());
    final var appUsers = state.getJsonArray("appUsers", new JsonArray());
    final var appUsersSize = appUsers.size();
    final var matchUsersSize = matchUsers.size();
    if (appUsersSize != matchUsersSize) {
      // Unexpected size
      Logger.warn("Unexpected match users size, {} != {}.", appUsersSize, matchUsersSize);
      return false;
    }

    final var physicalClosenessUsers = state.getJsonArray("physicalClosenessUsers", new JsonArray());
    final var socialClosenessUsers = state.getJsonArray("socialClosenessUsers", new JsonArray());
    final var beliefsAndValuesUsers = state.getJsonArray("beliefsAndValuesUsers", new JsonArray());
    final var domainInterestUsers = state.getJsonArray("domainInterestUsers", new JsonArray());
    final var groupsUsers = state.getJsonArray("groupsUsers", new JsonArray());
    final var toSort = new ArrayList<JsonObject>();

    for (var i = 0; i < appUsersSize; i++) {

      final var groupsUser = groupsUsers.getJsonObject(i);
      final var appUser = appUsers.getString(i);
      final var groupsUserId = groupsUser.getString("userId");
      if (!appUser.equals(groupsUserId)) {

        Logger.warn("Unexpected groups user at {}, {} is not {}.", i, appUser, groupsUserId);
        return false;
      }

      var hs = 0;
      var ss = 0;
      var hb = 0;
      var sb = 0;
      var x = 0;
      var mdX = this.getUserValueIn(domainInterestUsers, appUser);
      if (mdX != null && !this.areSimilar(mdX, 0d)) {

        x = 1;
        ss += 1;

      } else {

        if (this.areSimilar(mdX, 0d)) {

          sb += 1;
        }
        mdX = 0.0;
      }
      var y = 0;
      var mdV = this.getUserValueIn(beliefsAndValuesUsers, appUser);
      if (mdV != null && !this.areSimilar(mdV, 0d)) {

        y = 1;
        ss += 1;

      } else {

        if (this.areSimilar(mdV, 0d)) {

          sb += 1;
        }
        mdV = 0.0;
      }
      var z = 0;
      var mdSC = this.getUserValueIn(socialClosenessUsers, appUser);
      if (mdSC != null && !this.areSimilar(mdSC, 0d)) {

        z = 1;
        if (Domain.ACADEMIC_SKILLS == domain) {

          hs += 1;

        } else {

          ss += 1;

        }

      } else {

        if (this.areSimilar(mdSC, 0d)) {
          if (Domain.ACADEMIC_SKILLS == domain) {

            hb += 1;

          } else {

            sb += 1;

          }
        }
        mdSC = 0.0;
      }
      var w = 0;
      var mdPC = this.getUserValueIn(physicalClosenessUsers, appUser);
      if (mdPC != null && !this.areSimilar(mdPC, 0d)) {

        w = 1;
        hs += 1;

      } else {

        if (this.areSimilar(mdPC, 0d)) {

          hb = 1;
        }
        mdPC = 0.0;
      }
      var matchingDegree = 0.0d;
      if (x != 0 || y != 0 || z != 0 || w != 0) {

        matchingDegree = (x * mdX + y * mdV + z * mdSC + w * mdPC) / (x + y + z + w);
      }
      toSort.add(new JsonObject().put("userId", appUser).put("value", matchingDegree));

      var expectedGroup = 0;
      if (hb == 0) {

        if (ss > 0) {

          expectedGroup = sb + 1;

        } else {

          expectedGroup = 4;
        }

      } else if (hb == 1 && hs == 1) {

        if (ss > 0) {

          expectedGroup = sb + 5;

        } else {

          expectedGroup = 8;
        }

      } else if (ss > 0) {

        expectedGroup = sb + 9;

      } else {

        expectedGroup = 12;
      }

      final var groupsUserValue = groupsUser.getInteger("group");
      if (groupsUserValue == null || expectedGroup != groupsUserValue) {

        Logger.warn("Unexpected groups user value at {}, {} is not in {}.", i, groupsUserValue, expectedGroup);
        return false;
      }

    }

    toSort.sort((a, b) -> a.getDouble("value", 0d).compareTo(b.getDouble("value", 0d)));
    final var expected = new JsonArray();
    for (final var element : toSort) {

      expected.add(0, element);
    }

    if (!this.validateThatUserValueListAreEquals(expected, matchUsers)) {

      Logger.warn("Unexpected match users, {} != {}", expected, matchUsers);
      return false;
    }

    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isValidGroupAndExplanationTypeFor(final String userId, final int group,
      final Explanation explanation, final JsonObject state) {

    final var physicalClosenessUsers = state.getJsonArray("physicalClosenessUsers", new JsonArray());
    final var mdPC = this.getUserValueIn(physicalClosenessUsers, userId);
    final var socialClosenessUsers = state.getJsonArray("socialClosenessUsers", new JsonArray());
    final var mdSC = this.getUserValueIn(socialClosenessUsers, userId);
    final var domain = Domain.valueOf(this.domain().toUpperCase());

    Explanation expectedExplanation = null;
    if (group == 0) {

      expectedExplanation = Explanation.GROUP_0;

    } else if (group == 1) {

      expectedExplanation = Explanation.GROUP_1;

    } else if (group == 12) {

      expectedExplanation = Explanation.GROUP_12;

    } else if (group == 2 || group == 3 || group == 4) {

      if (mdPC != null && mdSC != null && Domain.ACADEMIC_SKILLS == domain) {

        expectedExplanation = Explanation.GROUP_2_3_4_A;

      } else if (mdPC == null && mdSC != null && Domain.ACADEMIC_SKILLS == domain) {

        expectedExplanation = Explanation.GROUP_2_3_4_B;

      } else {

        expectedExplanation = Explanation.GROUP_2_3_4_C;
      }

    } else if (group == 9 || group == 10 || group == 11) {

      if (mdPC != null && mdSC != null && Domain.ACADEMIC_SKILLS == domain) {

        expectedExplanation = Explanation.GROUP_9_10_11_A;

      } else if (mdPC == null && mdSC != null && Domain.ACADEMIC_SKILLS == domain) {

        expectedExplanation = Explanation.GROUP_9_10_11_B;

      } else {

        expectedExplanation = Explanation.GROUP_9_10_11_C;
      }

    } else if (group == 5) {

      if (this.areSimilar(mdPC, 0d) && mdSC != null && !this.areSimilar(mdSC, 0d) && Domain.ACADEMIC_SKILLS == domain) {

        expectedExplanation = Explanation.GROUP_5_A;

      } else {

        expectedExplanation = Explanation.GROUP_5_B;
      }

    } else if (this.areSimilar(mdPC, 0d) && mdSC != null && !this.areSimilar(mdPC, 0d)
        && Domain.ACADEMIC_SKILLS == domain) {

      expectedExplanation = Explanation.GROUP_6_7_8_A;

    } else {

      expectedExplanation = Explanation.GROUP_6_7_8_B;
    }

    if (explanation != expectedExplanation) {

      Logger.warn("Unexpected groups explanation value for {}, {} is not in {}.", userId, explanation,
          expectedExplanation);
      return false;

    } else {

      return true;

    }
  }

}
