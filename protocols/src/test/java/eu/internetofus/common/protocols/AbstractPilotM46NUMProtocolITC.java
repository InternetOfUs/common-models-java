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

import eu.internetofus.common.components.models.WeNetUserProfile;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.HashMap;
import java.util.Map;
import org.tinylog.Logger;

/**
 * Test the pilot M46 at NUM.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractPilotM46NUMProtocolITC extends AbstractPilotM46WithCommonDomainProtocolITC {

  /**
   * The possible explanation why an user is selected.
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
    GROUP_1(
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
        "This user fulfils the physical closeness, social closeness, and academic skills requirements, but not all of the other requirements. To find some answers, we had to relax some of the other requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 2,3,4 type b.
     */
    GROUP_2_3_4_B(
        "This user fulfils the physical and social closeness requirements, but not all of the other requirements. To find some answers, we had to relax some of the other requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 2,3,4 type c.
     */
    GROUP_2_3_4_C(
        "This user fulfils the academic skills and physical closeness requirements, but not all of the other requirements. To find some answers, we had to relax some of the other requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 2,3,4 type d.
     */
    GROUP_2_3_4_D(
        "This user fulfils the academic skills and social closeness requirements, but not all of the other requirements. To find some answers, we had to relax some of the other requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 2,3,4 type e.
     */
    GROUP_2_3_4_E(
        "This user fulfils the academic skills requirements, but not all of the other requirements. To find some answers, we had to relax some of the other requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 2,3,4 type f.
     */
    GROUP_2_3_4_F(
        "This user fulfils the social closeness requirements, but not all of the other requirements. To find some answers, we had to relax some of the other requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 2,3,4 type g.
     */
    GROUP_2_3_4_G(
        "This user fulfils the physical closeness requirements, but not all of the other requirements. To find some answers, we had to relax some of the other requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 9,10,11 type a.
     */
    GROUP_9_10_11_A(
        "This user does not fulfil neither the physical closeness, social closeness, and academic skills requirements, nor some of the other requirements. To find some answers, we had to relax some of the other requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 9,10,11 type b.
     */
    GROUP_9_10_11_B(
        "This user does not fulfil neither the physical and social closeness requirements, nor some of the other requirements. To find some answers, we had to relax some of the other requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 9,10,11 type c.
     */
    GROUP_9_10_11_C(
        "This user does not fulfil neither the academic skills and physical closeness requirements, nor some of the other requirements. To find some answers, we had to relax some of the other requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 9,10,11 type d.
     */
    GROUP_9_10_11_D(
        "This user does not fulfil neither the academic skills and social closeness requirements, nor some of the other requirements. To find some answers, we had to relax some of the other requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 9,10,11 type e.
     */
    GROUP_9_10_11_E(
        "This user does not fulfil neither the academic skills requirements, nor some of the other requirements. To find some answers, we had to relax some of the other requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 9,10,11 type f.
     */
    GROUP_9_10_11_F(
        "This user does not fulfil neither the social closeness requirements, nor some of the other requirements. To find some answers, we had to relax some of the other requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 9,10,11 type g.
     */
    GROUP_9_10_11_G(
        "This user does not fulfil neither the physical closeness requirements, nor some of the other requirements. To find some answers, we had to relax some of the other requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 5 type a.
     */
    GROUP_5_A(
        "This user does not fulfil the physical and social closeness requirements. To find some answers, we had to relax these requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 5 type b.
     */
    GROUP_5_B(
        "This user does not fulfil the academic skills and physical closeness requirements. To find some answers, we had to relax these requirements. We also tried to increase the gender diversity of selected users."),
    /**
     * Explanation for group 5 type c.
     */
    GROUP_5_C(
        "This user does not fulfil the academic skills and social closeness requirements. To find some answers, we had to relax these requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 5 type d.
     */
    GROUP_5_D(
        "This user does not fulfil the physical closeness requirement. To find some answers, we had to relax these requirements. We also tried to increase the gender diversity of selected users."),
    /**
     * Explanation for group 5 type e.
     */
    GROUP_5_E(
        "This user does not fulfil the social closeness requirements. To find some answers, we had to relax these requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 5 type f.
     */
    GROUP_5_F(
        "This user does not fulfil the academic skills requirements. To find some answers, we had to relax these requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 6,7,8 type a.
     */
    GROUP_6_7_8_A(
        "This user does not fulfil neither the physical and social closeness requirements nor some of the other requirements. To find some answers, we had to relax these requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 6,7,8 type b.
     */
    GROUP_6_7_8_B(
        "This user does not fulfil neither the academic skills and physical closeness requirements nor some of the other requirements. To find some answers, we had to relax these requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 6,7,8 type c.
     */
    GROUP_6_7_8_C(
        "This user does not fulfil neither the academic skills and social closeness requirements nor some of the other requirements. To find some answers, we had to relax these requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 6,7,8 type d.
     */
    GROUP_6_7_8_D(
        "This user does not fulfil neither the physical closeness requirement nor some of the other requirements. To find some answers, we had to relax these requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 6,7,8 type e.
     */
    GROUP_6_7_8_E(
        "This user does not fulfil neither the social closeness requirements nor some of the other requirements. To find some answers, we had to relax these requirements. We also tried to increase the gender diversity of selected users."),

    /**
     * Explanation for group 6,7,8 type f.
     */
    GROUP_6_7_8_F(
        "This user does not fulfil neither the academic skills requirements nor some of the other requirements. To find some answers, we had to relax these requirements. We also tried to increase the gender diversity of selected users.");

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
   * @see DefaultProtocols#PILOT_M46_NUM
   */
  @Override
  protected DefaultProtocols getDefaultProtocolsToUse() {

    return DefaultProtocols.PILOT_M46_NUM;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Double domainInterestTo(final int index) {

    Double value = null;
    if (Domain.CAMPUS_LIFE.toTaskTypeDomain().equals(this.domain())) {

      if (this.compareAttribute(this.users.get(0), this.users.get(index), "materials.study_year") == 0) {

        value = 1d;

      } else {

        value = 0d;
      }

    } else if (Domain.ACADEMIC_SKILLS.toTaskTypeDomain().equals(this.domain())) {

      if ("different".equals(this.domainInterest())) {

        return this.simCompetences(index);

      } else {
        // This is the only case that accept a null
        return null;
      }

    } else if (!Domain.SENSITIVE.toTaskTypeDomain().equals(this.domain())
        && !Domain.RANDOM_THOUGHTS.toTaskTypeDomain().equals(this.domain())) {

      value = super.domainInterestTo(index);
    }

    if (value == null) {

      if ("different".equals(this.domainInterest())) {

        value = 1d;

      } else {

        value = 0d;
      }
    }
    return value;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Double beliefsAndValuesTo(final int index) {

    var value = super.beliefsAndValuesTo(index);
    if (value == null) {

      if ("different".equals(this.beliefsAndValues())) {

        value = 1d;

      } else {

        value = 0d;
      }
    }
    return value;
  }

  /**
   * Return the similarity of competences.
   *
   * @param index of the user to calculate the similarity.
   *
   * @return the similarity between materials.
   */
  protected Double simCompetences(final int index) {

    final var attributes = new String[] { "competences.c_u_active", "competences.c_read", "competences.c_essay",
        "competences.c_org", "competences.c_balance", "competences.c_assess", "competences.c_theory",
        "competences.c_pract", "competences.course_fa", "competences.course_plc", "competences.course_oop",
        "materials.program_study" };
    final var requester = this.users.get(0);
    final var user = this.users.get(index);
    if (this.compareAttribute(requester, user, attributes[0]) > 0) {

      for (final var attribute : attributes) {

        if (this.compareAttribute(requester, user, attribute) <= 0) {

          return 0.5d;
        }

      }

      // it is reverse because as use different => 1 - sim
      return 1d;

    } else {

      for (final var attribute : attributes) {

        if (this.compareAttribute(requester, user, attribute) > 0) {

          return 0.5d;
        }

      }

      // it is reverse because as use different => 1 - sim
      return 0d;

    }

  }

  /**
   * Compare two attributes of two profiles.
   *
   * @param source profile to get the attribute value.
   * @param target profile to get the attribute value.
   * @param name   of the attribute to compare.
   *
   * @return {@code 0} if are equals,{@code 1} if source is greater than target
   *         and {@code -1} otherwise.
   */
  protected int compareAttribute(final WeNetUserProfile source, final WeNetUserProfile target, final String name) {

    if (name.startsWith("competences.")) {

      final var competence = name.substring(12);
      final var sourceValue = this.competenceLevel(source, competence);
      final var targetValue = this.competenceLevel(target, competence);
      return Double.compare(sourceValue, targetValue);

    } else {

      final var material = name.substring(10);
      final var sourceValue = this.materialDescription(source, material);
      final var targetValue = this.materialDescription(target, material);
      return sourceValue.compareTo(targetValue);
    }
  }

  /**
   * Return the level of the name.
   *
   * @param profile to get the value.
   * @param name    of the competence.
   *
   * @return the value of the competence.
   */
  protected double competenceLevel(final WeNetUserProfile profile, final String name) {

    if (profile != null && profile.competences != null) {

      for (final var competence : profile.competences) {

        if (competence.name.equals(name)) {

          return competence.level;
        }
      }
    }

    return 0d;

  }

  /**
   * Return the level of the name.
   *
   * @param profile to get the value.
   * @param name    of the material.
   *
   * @return the value of the material.
   */
  protected String materialDescription(final WeNetUserProfile profile, final String name) {

    if (profile != null && profile.materials != null) {

      for (final var material : profile.materials) {

        if (material.name.equals(name)) {

          return material.description;
        }
      }
    }

    return "";

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Double socialClosenessTo(final int index) {

    Double value = null;
    if (Domain.ACADEMIC_SKILLS.toTaskTypeDomain().equals(this.domain())) {

      if (!this.isEmptyProfile(index)) {

        final var socialAttributes = new String[] { "materials.department", "materials.study_year",
            "materials.program_study" };
        final var requester = this.users.get(0);
        final var user = this.users.get(index);
        value = 0d;
        for (final var attribute : socialAttributes) {

          if (this.compareAttribute(requester, user, attribute) == 0) {
            value = 1d;
            break;
          }

        }
      }

    } else {

      value = super.socialClosenessTo(index);
    }
    if (value == null) {

      if ("different".equals(this.socialCloseness())) {

        value = 1d;

      } else {

        value = 0d;
      }
    }

    return value;
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
