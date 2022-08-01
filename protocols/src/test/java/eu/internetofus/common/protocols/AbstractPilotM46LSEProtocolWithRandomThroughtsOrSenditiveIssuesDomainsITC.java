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
 * Test the calculus when uses domain is ‘random thoughts’ or ‘sensitive
 * issues’.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractPilotM46LSEProtocolWithRandomThroughtsOrSenditiveIssuesDomainsITC
    extends AbstractPilotM46LSEProtocolITC {

  /**
   * {@inheritDoc}
   */
  @Override
  protected String beliefsAndValues() {

    return "different";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String socialCloseness() {

    return "similar";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean validDomainInterestUsers(final JsonObject state) {

    final var domainInterestUsers = state.getJsonArray("domainInterestUsers", new JsonArray());
    final var domainInterestSize = domainInterestUsers.size();
    final var appUsers = state.getJsonArray("appUsers", new JsonArray());
    final var appUsersSize = appUsers.size();
    if (appUsersSize != domainInterestSize) {
      // Unexpected size
      Logger.warn("Unexpected domainInterest user size, {} != {}.", appUsersSize, domainInterestSize);
      return false;
    }

    for (var i = 0; i < appUsersSize; i++) {

      final var appUser = appUsers.getString(i);
      final var domainInterestUser = domainInterestUsers.getJsonObject(i);
      final var domainInterestUserId = domainInterestUser.getString("userId");
      if (!appUser.equals(domainInterestUserId)) {

        Logger.warn("Unexpected  domainInterest user at {}, {} is not {}.", i, appUser, domainInterestUser);
        return false;
      }

      final var domainInterestValue = domainInterestUser.getDouble("value");
      final var expectedDomainInterestValue = this.indifferentValue();
      if (!this.areSimilar(expectedDomainInterestValue, domainInterestValue)) {

        Logger.warn("Unexpected  domainInterest user value at {}, {} is not {}.", i, expectedDomainInterestValue,
            domainInterestValue);
        return false;
      }

    }

    return true;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean validMatchUsers(final JsonObject state) {

    final var appUsers = state.getJsonArray("appUsers", new JsonArray());
    final var matchUsers = state.getJsonArray("matchUsers", new JsonArray());
    if (appUsers.size() != matchUsers.size()) {
      // Unexpected size
      Logger.warn("Unexpected matchUsers user size.");
      return false;
    }

    final var socialClosenessUsers = state.getJsonArray("socialClosenessUsers", new JsonArray());
    final var beliefsAndValuesUsers = state.getJsonArray("beliefsAndValuesUsers", new JsonArray());
    final var toSort = new ArrayList<JsonObject>();
    for (var i = 0; i < appUsers.size(); i++) {

      final var appUser = appUsers.getString(i);
      final var index = this.indexOfCreatedProfileWithId(appUser);
      if (this.isEmptyProfile(index)) {

        final var closeness = this.socialClosenessTo(index);
        if (closeness == null) {

          toSort.add(new JsonObject().put("userId", appUser).put("value", 0d));

        } else {

          toSort.add(new JsonObject().put("userId", appUser).put("value", closeness));
        }

      } else {

        var y = 0.0d;
        var z = 0.0d;
        var mdV = 0.0d;
        var mdSC = 0.0d;
        final var socialCloseness = this.getUserValueIn(socialClosenessUsers, appUser);
        final var beliefsAndValues = this.getUserValueIn(beliefsAndValuesUsers, appUser);
        if (socialCloseness != null) {

          z = 1d;
          mdSC = socialCloseness;
        }
        if (beliefsAndValues != null) {

          y = 1d;
          mdV = beliefsAndValues;
        }
        var value = 0d;
        if (!this.areSimilar(y, 0d) || !this.areSimilar(z, 0d)) {

          value = (y * mdV + z * mdSC) / (y + z);
        }
        toSort.add(new JsonObject().put("userId", appUser).put("value", value));
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
  protected String explanationText() {

    return "This user fits the requirements to a certain extent. While choosing whom to ask, we also tried to increase the gender diversity of selected users.";
  }

}
