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

import eu.internetofus.common.components.models.Material;
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

    if (Domain.SENSITIVE.toTaskTypeDomain().equals(this.domain())) {

      return null;

    } else if (Domain.CAMPUS_LIFE.toTaskTypeDomain().equals(this.domain())) {

      return this.simMaterials(index);

    } else if (Domain.ACADEMIC_SKILLS.toTaskTypeDomain().equals(this.domain())) {

      if ("different".equals(this.domainInterest())) {

        return this.simCompetences(index);

      } else {

        return null;
      }

    } else if (Domain.LEISURE_ACTIVITIES.toTaskTypeDomain().equals(this.domain())) {

      final var simMat = this.simMaterials(index);
      final var simDiv = super.domainInterestTo(index);
      if (simMat == null) {

        return simDiv;

      } else if (simDiv == null) {

        return simMat;

      } else {

        return 1.0 - (1 - simMat
            + Math.abs(this.users.get(0).competences.get(0).level - this.users.get(index).competences.get(0).level))
            / 3.0;
      }

    } else {

      return super.domainInterestTo(index);

    }
  }

  /**
   * Return the similarity of competences.
   *
   * @param index of the user to calculate the similarity.
   *
   * @return the similarity between materials.
   */
  protected Double simCompetences(final int index) {

    // The 0.5 value is ignored because all the variables has the same value

    if (this.users.get(0).competences != null) {

      for (final var competence : this.users.get(0).competences) {

        if ("course_ca".equals(competence.name)) {

          if (competence.level != null && this.users.get(index).competences != null) {

            for (final var competence1 : this.users.get(index).competences) {

              if ("course_ca".equals(competence1.name)) {

                if (competence1.level != null && competence.level > competence1.level) {
                  // it is reverse because as use different => 1 - sim
                  return 0d;

                }

                break;
              }
            }
          }

          break;
        }
      }
    }

    // it is reverse because as use different => 1 - sim
    return 1d;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Double socialClosenessTo(final int index) {

    if (Domain.ACADEMIC_SKILLS.toTaskTypeDomain().equals(this.domain())) {

      return this.simMaterials(index);

    } else {

      return super.socialClosenessTo(index);

    }

  }

  /**
   * Return the similarity of materials.
   *
   * @param index of the user to calculate the similarity.
   *
   * @return the similarity between materials.
   */
  protected Double simMaterials(final int index) {

    Material material1 = null;
    if (this.users.get(0).materials != null) {

      for (final var material : this.users.get(0).materials) {

        if ("degree_programme".equals(material.name)) {

          material1 = material;
          break;
        }
      }
      Material material2 = null;
      if (this.users.get(index).materials != null) {

        for (final var material : this.users.get(index).materials) {

          if ("degree_programme".equals(material.name)) {

            material2 = material;
            break;
          }
        }

      }

      if (material1 != null && material2 != null) {

        if (material1.description != null && material1.description.equals(material2.description)) {

          return 1d;

        } else {

          return 0d;
        }

      }
    }

    return null;

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
