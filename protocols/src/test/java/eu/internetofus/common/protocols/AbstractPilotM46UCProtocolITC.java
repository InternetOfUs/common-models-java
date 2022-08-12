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

import eu.internetofus.common.components.models.Task;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.HashMap;
import java.util.Map;
import org.tinylog.Logger;

/**
 * Test the pilot M46 at UC.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractPilotM46UCProtocolITC extends AbstractPilotM46ProtocolITC {

  /**
   * The domains of the Pilot.
   */
  public enum Domain {

    /**
     * The exact sciences domain.
     */
    EXACT_SCIENCES,
    /**
     * The computer's science domain.
     */
    COMPUTER_S_SCIENCE,
    /**
     * The health domain.
     */
    HEALTH,
    /**
     * The administrative and accounting domain.
     */
    ADMINISTRATIVE_AND_ACCOUNTING,
    /**
     * The social sciences domain.
     */
    SOCIAL_SCIENCES,
    /**
     * The legal domain.
     */
    LEGAL,
    /**
     * The environmental domain.
     */
    ENVIRONMENTAL,
    /**
     * The design and construction domain.
     */
    DESIGN_AND_CONSTRUCTION,
    /**
     * The electronic sciences domain.
     */
    ELECTRONIC_SCIENCES,
    /**
     * The academic life domain.
     */
    ACADEMIC_LIFE;

    /**
     * Return the value that can be used on the task type.
     *
     * @return the domain used on the task type for the domain.
     */
    public String toTaskTypeDomain() {

      return this.name().toLowerCase();

    }

  }

  /**
   * The possible explanation why an user is selected.
   */
  public enum Explanation {

    /**
     * Explanation when no dimension is specified.
     */
    GROUP_0(
        "Recall that there were no requirements set w.r.t academic life domain, values, competences or physical closeness. Nevertheless, we tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 11.
     */
    GROUP_1(
        "This user fulfils all requirements. While searching for users, we tried to increase the gender diversity of selected users."),
    /**
     * Explanation for group 9.
     */
    GROUP_9(
        "Not enough members fulfil the requirements. To find some answers, we had to choose some that don\u2019t fulfil any, like this user. While doing so, we also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 2,3 type a.
     */
    GROUP_2_3_A(
        "This user fulfils the physical closeness and competence requirements, but not all of the other requirements. To find some answers, we had to relax some of the other requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 2,3 type b.
     */
    GROUP_2_3_B(
        "This user fulfils the competence requirement, but not all of the other requirements. To find some answers, we had to relax some of the other requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 2,3 type c.
     */
    GROUP_2_3_C(
        "This user fulfils the physical closeness requirement, but not all of the other requirements. To find some answers, we had to relax some of the other requirements. We also tried to increase the gender diversity of selected users."),
    /**
     * Explanation for group 7,8 type a.
     */
    GROUP_7_8_A(
        "This user does not fulfil neither the physical closeness and competence requirements, nor some of the other requirements. To find some answers, we had to relax some of the other requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 7,8 type b.
     */
    GROUP_7_8_B(
        "This user does not fulfil neither the competence requirement, nor some of the other requirements. To find some answers, we had to relax some of the other requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 7,8 type c.
     */
    GROUP_7_8_C(
        "This user does not fulfil neither the physical closeness requirement, nor some of the other requirements. To find some answers, we had to relax some of the other requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 4 type a.
     */
    GROUP_4_A(
        "This user does not fulfil the physical closeness requirement. To find some answers, we had to relax this requirement. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 4 type b.
     */
    GROUP_4_B(
        "This user does not fulfil the competence requirement. To find some answers, we had to relax this requirement. We also tried to increase the gender diversity of selected users."),
    /**
     * Explanation for group 5,6 type a.
     */
    GROUP_5_6_A(
        "This user fulfils the competence requirement, but neither the physical closeness requirement nor some of the other requirements. To find some answers, we had to relax these requirements. We also tried to increase the gender diversity of selected users."),
    /**
     * Explanation for group 5,6 type b.
     */
    GROUP_5_6_B(
        "This user fulfils the physical closeness requirement, but neither the competence requirement nor some of the other requirements. To find some answers, we had to relax these requirements. We also tried to increase the gender diversity of selected users.");

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
   */
  @Override
  protected Task createTaskForProtocol() {

    final var task = super.createTaskForProtocol();
    task.attributes.remove("socialCloseness");
    task.attributes.put("competences", this.competences());
    return task;
  }

  /**
   * Return the competences of the task
   *
   * @return the competences for the task.
   */
  protected abstract String competences();

  /**
   * {@inheritDoc}
   *
   * @see DefaultProtocols#PILOT_M46_UC
   */
  @Override
  protected DefaultProtocols getDefaultProtocolsToUse() {

    return DefaultProtocols.PILOT_M46_UC;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String socialCloseness() {

    return "indifferent";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean validSocialClosenessUsers(final JsonObject state) {

    return !state.containsKey("socialClosenessUsers");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean validTaskUserStateAfterCreation(final JsonObject state) {

    if (!state.containsKey("competencesUsers")) {

      Logger.warn("Not competencesUsers is not defined on the state.");
      return false;
    }

    if (!state.containsKey("groupsUsers")) {

      Logger.warn("Not groupsUsers is not defined on the state.");
      return false;
    }

    return this.validCompetencesUsers(state) && super.validTaskUserStateAfterCreation(state)
        && this.validGroupUsers(state);
  }

  /**
   * Check that the competencesUsers is valid.
   *
   * @param state where is the users to check.
   *
   * @return {@code true} if the competencesUsers is the expected after the task
   *         is created.
   */
  protected boolean validCompetencesUsers(final JsonObject state) {

    final var competencesUsers = state.getJsonArray("competencesUsers", new JsonArray());
    final var appUsers = state.getJsonArray("appUsers", new JsonArray());
    final var appUsersSize = appUsers.size();
    final var competencesUsersSize = competencesUsers.size();
    if (appUsersSize != competencesUsersSize) {
      // Unexpected size
      Logger.warn("Unexpected competences users size, {} != {}.", appUsersSize, competencesUsersSize);
      return false;
    }

    final var type = this.competences();
    for (var i = 0; i < appUsersSize; i++) {

      final var appUser = appUsers.getString(i);
      final var competencesUser = competencesUsers.getJsonObject(i);
      final var competencesUserId = competencesUser.getString("userId");
      if (!appUser.equals(competencesUserId)) {

        Logger.warn("Unexpected competences user at {}, {} is not {}.", i, appUser, competencesUserId);
        return false;
      }

      final var competencesValue = competencesUser.getDouble("value");
      Double expectedCompetencesValue = null;
      if ("relevant".equals(type)) {

        final var index = this.indexOfCreatedProfileWithId(appUser);
        expectedCompetencesValue = this.competencesTo(index);

      } else {

        expectedCompetencesValue = null;
      }
      if (!this.areSimilar(expectedCompetencesValue, competencesValue)) {

        Logger.warn("Unexpected  competences user value at {}, {} is not {}.", i, expectedCompetencesValue,
            competencesValue);
        return false;
      }

    }

    return true;
  }

  /**
   * Return the believe and values between the requester and the user at the
   * specified position.
   *
   * @param index of the user to calculate the distance.
   *
   * @return the believe and values similarity.
   */
  public Double competencesTo(final int index) {

    if (this.isEmptyProfile(index)) {

      return null;

    } else {

      return this.users.get(index).meanings.get(0).level;

    }

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
      if (groupsUserGroup < 0 || groupsUserGroup > 9) {

        Logger.warn("Unexpected groups user value at {}, {} is not in [0,9].", i, groupsUserGroup);
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
