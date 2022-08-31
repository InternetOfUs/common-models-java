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

/**
 * Test the pilot M46 at LSE.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractPilotM46LSEProtocolITC extends AbstractPilotM46WithCommonDomainProtocolITC {

  /**
   * The last validated state.
   */
  protected JsonObject state = null;

  /**
   * {@inheritDoc}
   *
   * @see DefaultProtocols#PILOT_M46_LSE
   */
  @Override
  protected DefaultProtocols getDefaultProtocolsToUse() {

    return DefaultProtocols.PILOT_M46_LSE;
  }

  /**
   * {@inheritDoc}
   *
   * @return {@code 0} in any case
   */
  @Override
  public double maxDistance() {

    return 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String positionOfAnswerer() {

    return "anywhere";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final boolean validTaskUserStateAfterCreation(final JsonObject state) {

    this.state = null;
    final var result = super.validTaskUserStateAfterCreation(state);
    if (result) {

      this.state = state;
    }

    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean validPhysicalClosenessUsers(final JsonObject state) {

    return !state.containsKey("physicalClosenessUsers");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final String explanationTextFor(final int index) {

    if (this.state == null || "indifferent".equals(this.task.attributes.getString("domainInterest"))
        && "indifferent".equals(this.task.attributes.getString("beliefsAndValues"))
        && "indifferent".equals(this.task.attributes.getString("socialCloseness"))) {

      return "Recall that no requirements were set w.r.t domains, values and social closeness. Nevertheless, we tried to increase the gender diversity of selected users.";

    } else {

      final var user = this.users.get(index);
      final var socialClosenessUsers = this.state.getJsonArray("socialClosenessUsers", new JsonArray());
      final var beliefsAndValuesUsers = this.state.getJsonArray("beliefsAndValuesUsers", new JsonArray());
      final var domainInterestUsers = this.state.getJsonArray("domainInterestUsers", new JsonArray());
      final var socialCloseness = this.getUserValueIn(socialClosenessUsers, user.id);
      final var beliefsAndValues = this.getUserValueIn(beliefsAndValuesUsers, user.id);
      final var domainInterest = this.getUserValueIn(domainInterestUsers, user.id);
      if (socialCloseness != null && socialCloseness > 0.5d || beliefsAndValues != null && beliefsAndValues > 0d
          || domainInterest != null && domainInterest > 0d) {

        return "This user fits the requirements to a certain extent. While choosing whom to ask, we also tried to increase the gender diversity of selected users.";

      } else {

        return "Not enough members in the community fit the requirements. We had to relax the requirements in order to find some answers, which is how this user was chosen. While choosing whom to ask, we also tried to increase the gender diversity of selected users.";

      }

    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Double socialClosenessTo(final int index) {

    var value = super.socialClosenessTo(index);
    if (value == null) {

      value = 0.5d;
    }

    return value;
  }

}
