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
import org.tinylog.Logger;

/**
 * Test the calculus when uses ‘sensitive issues’ domain.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class PilotM46AAUProtocolWithSensitiveIssuesDomainITC extends AbstractPilotM46AAUProtocolWithDimensionITC {

  /**
   * {@inheritDoc} ‘academic skills’
   */
  @Override
  protected String domain() {

    return Domain.SENSITIVE_ISSUES.toTaskTypeDomain();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String domainInterest() {

    return "similar";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String beliefsAndValues() {

    return "similar";
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
  protected String positionOfAnswerer() {

    return "nearby";
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

        Logger.warn("Unexpected  domainInterest user at {}, {} is not {}.", i, appUser, domainInterestUserId);
        return false;
      }

      final var domainInterestValue = domainInterestUser.getDouble("value");
      if (domainInterestUser != null) {

        Logger.warn("Unexpected  domainInterest user value at {}, null is not {}.", i, domainInterestValue);
        return false;
      }

    }

    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean validBeliefsAndValuesUsers(final JsonObject state) {

    final var beliefsAndValuesUsers = state.getJsonArray("beliefsAndValuesUsers", new JsonArray());
    final var beliefsAndValuesSize = beliefsAndValuesUsers.size();
    final var appUsers = state.getJsonArray("appUsers", new JsonArray());
    final var appUsersSize = appUsers.size();
    if (appUsersSize != beliefsAndValuesSize) {
      // Unexpected size
      Logger.warn("Unexpected beliefsAndValues user size, {} != {}.", appUsersSize, beliefsAndValuesSize);
      return false;
    }

    for (var i = 0; i < appUsersSize; i++) {

      final var appUser = appUsers.getString(i);
      final var beliefsAndValuesUser = beliefsAndValuesUsers.getJsonObject(i);
      final var beliefsAndValuesUserId = beliefsAndValuesUser.getString("userId");
      if (!appUser.equals(beliefsAndValuesUserId)) {

        Logger.warn("Unexpected  beliefsAndValues user at {}, {} is not {}.", i, appUser, beliefsAndValuesUserId);
        return false;
      }

      final var beliefsAndValuesValue = beliefsAndValuesUser.getDouble("value");
      if (beliefsAndValuesValue != null) {

        Logger.warn("Unexpected  beliefsAndValues user value at {}, null is not {}.", i, beliefsAndValuesValue);
        return false;
      }

    }

    return true;

  }

}
