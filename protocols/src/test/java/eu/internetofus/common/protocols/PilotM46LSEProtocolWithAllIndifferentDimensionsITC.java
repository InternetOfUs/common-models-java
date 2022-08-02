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
 * Test the calculus when uses ‘academic skills’ domain.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class PilotM46LSEProtocolWithAllIndifferentDimensionsITC extends AbstractPilotM46LSEProtocolITC {

  /**
   * {@inheritDoc} ‘academic skills’
   */
  @Override
  protected String domain() {

    return Domain.ACADEMIC_SKILLS.toTaskTypeDomain();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String domainInterest() {

    return "indifferent";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String beliefsAndValues() {

    return "indifferent";
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
  protected boolean validMatchUsers(final JsonObject state) {

    final var appUsers = state.getJsonArray("appUsers", new JsonArray());
    final var matchUsers = state.getJsonArray("matchUsers", new JsonArray());
    if (appUsers.size() != matchUsers.size()) {
      // Unexpected size
      Logger.warn("Unexpected matchUsers user size.");
      return false;
    }

    for (var i = 0; i < appUsers.size(); i++) {

      final var appUserId = appUsers.getString(i);
      final var matchUser = matchUsers.getJsonObject(i);
      final var matchUserId = matchUser.getString("userId");
      if (!appUserId.equals(matchUserId)) {

        Logger.warn("Unexpected matchUser user at {}, {} is not {}.", i, appUserId, matchUserId);
        return false;
      }

      final var matchUserValue = matchUser.getDouble("value");
      if (!this.areSimilar(1.0, matchUserValue)) {

        Logger.warn("Unexpected matchUser user at {}, 1.0 is not {}.", i, matchUserValue);
        return false;
      }

    }

    return true;
  }

}
