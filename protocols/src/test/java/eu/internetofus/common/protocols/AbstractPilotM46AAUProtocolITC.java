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
import java.util.HashMap;
import java.util.Map;
import org.tinylog.Logger;

/**
 * Test the pilot M46 at AAU.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractPilotM46AAUProtocolITC extends AbstractPilotM46ProtocolITC {

  /**
   *
   */
  public enum Explanation {

    /**
     * Explanation when no dimension is specified.
     */
    GROUP_0(
        "Recall that there were no requirements set w.r.t domains, values, social or physical closeness. Nevertheless, we tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 11.
     */
    GROUP_11(
        "This user fulfils all requirements. While searching for users, we tried to increase the gender diversity of selected users."),
    /**
     * Explanation for group 12.
     */
    GROUP_12(
        "Not enough members fulfil the requirements. To find some answers, we had to choose some that don't fulfil any, like this user. While doing so, we also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 2,3,4 type a.
     */
    GROUP_2_3_4_A(
        "This user fulfils the physical and social closeness requirements, but not all of the other requirements. To find some answers, we had to relax some of the other requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 2,3,4 type b.
     */
    GROUP_2_3_4_B(
        "This user fulfils the social closeness requirements, but not all of the other requirements. To find some answers, we had to relax some of the other requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 2,3,4 type c.
     */
    GROUP_2_3_4_C(
        "This user fulfils the physical closeness requirements, but not all of the other requirements. To find some answers, we had to relax some of the other requirements. We also tried to increase the gender diversity of selected users."),
    /**
     * Explanation for group 9,10,11 type a.
     */
    GROUP_9_10_11_A(
        "This user does not fulfil neither the physical and social closeness requirements, nor some of the other requirements. To find some answers, we had to relax some of the other requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 9,10,11 type b.
     */
    GROUP_9_10_11_B(
        "This user does not fulfil neither the social closeness requirements, nor some of the other requirements. To find some answers, we had to relax some of the other requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 9,10,11 type c.
     */
    GROUP_9_10_11_C(
        "This user does not fulfil neither the physical closeness requirements, nor some of the other requirements. To find some answers, we had to relax some of the other requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 5 type a.
     */
    GROUP_5_A(
        "This user does not fulfil the physical closeness requirement. To find some answers, we had to relax this requirement. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 5 type b.
     */
    GROUP_5_B(
        "This user does not fulfil the social closeness requirement. To find some answers, we had to relax this requirement. We also tried to increase the gender diversity of selected users."),
    /**
     * Explanation for group 6,7,8 type a.
     */
    GROUP_6_7_8_A(
        "This user fulfils the social closeness requirement, but neither the physical closeness requirement nor some of the other requirements. To find some answers, we had to relax these requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 6,7,8 type b.
     */
    GROUP_6_7_8_B(
        "This user fulfils the physical closeness requirement, but neither the social closeness requirement nor some of the other requirements. To find some answers, we had to relax these requirements. We also tried to increase the gender diversity of selected users.");

    /**
     * The text associated to the explanation.
     */
    public String text;

    /**
     * Create a new explanation.
     *
     * @param text of the explanation.
     */
    Explanation(final String text) {

      this.text = text;
    }

  }

  /**
   * The expected explanations for the answer of the user.
   */
  protected Map<String, String> expectedExplanations = new HashMap<>();

  /**
   * {@inheritDoc}
   *
   * @see DefaultProtocols#PILOT_M46_AAU
   */
  @Override
  protected DefaultProtocols getDefaultProtocolsToUse() {

    return DefaultProtocols.PILOT_M46_AAU;
  }

  /**
   * {@inheritDoc}
   *
   * @return {@code 500} in any case
   */
  @Override
  public double maxDistance() {

    return 500;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean validTaskUserStateAfterCreation(final JsonObject state) {

    if (!state.containsKey("groupsUsers")) {

      Logger.warn("Not groupUsers is not defined on the state.");
      return false;
    }

    return super.validTaskUserStateAfterCreation(state) && this.validGroupUsers(state);
  }

  /**
   * Check that the groupsUsers is valid.
   *
   * @param state where is the users to check.
   *
   * @return {@code true} if the groupsUsers is the expected after the task is
   *         created.
   */
  protected boolean validGroupUsers(final JsonObject state) {

    final var groupsUsers = state.getJsonArray("groupsUsers", new JsonArray());
    final var appUsers = state.getJsonArray("appUsers", new JsonArray());
    final var appUsersSize = appUsers.size();
    final var groupsUsersSize = groupsUsers.size();
    if (appUsersSize != groupsUsersSize) {
      // Unexpected size
      Logger.warn("Unexpected groups users size, {} != {}.", appUsersSize, groupsUsersSize);
      return false;
    }

    for (var i = 0; i < appUsersSize; i++) {

      final var appUser = appUsers.getString(i);
      final var groupsUser = groupsUsers.getJsonObject(i);
      final var groupsUserId = groupsUser.getString("userId");
      if (!appUser.equals(groupsUserId)) {

        Logger.warn("Unexpected groups user at {}, {} is not {}.", i, appUser, groupsUserId);
        return false;
      }

      final var groupsUserGroup = groupsUser.getInteger("group", -1);
      if (groupsUserGroup < 0 || groupsUserGroup > 12) {

        Logger.warn("Unexpected groups user value at {}, {} is not in [0,12].", i, groupsUserGroup);
        return false;
      }

      final var groupsUserExplanationType = groupsUser.getString("explanationType", null);
      try {

        final var explanation = Explanation.valueOf(groupsUserExplanationType.toUpperCase());
        if (!this.isValidGroupAndExplanationTypeFor(groupsUserId, groupsUserGroup, explanation, state)) {

          Logger.warn("Unexpected group or explanation type for groups user value at {}.", i);
          return false;

        } else {

          this.expectedExplanations.put(appUser, explanation.text);

        }

      } catch (final Throwable error) {

        Logger.warn(error, "Unexpected explanation type for groups user value at {}, {} is not defined.", i,
            groupsUserExplanationType);
        return false;
      }

    }

    return true;
  }

  /**
   * Check if the group and the explanation are valid for the specified user.
   *
   * @param userId      identifier of the user.
   * @param group       for the user.
   * @param explanation for the user.
   * @param state       where are the groups users defined.
   *
   * @return {@code true} if the group and the explanations are valid.
   *
   */
  protected abstract boolean isValidGroupAndExplanationTypeFor(String userId, int group, Explanation explanation,
      JsonObject state);

  /**
   * {@inheritDoc}
   */
  @Override
  protected String explanationTextFor(final int index) {

    final var user = this.users.get(index);
    return this.expectedExplanations.get(user.id);
  }

}
