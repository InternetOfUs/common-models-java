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
      final var group = groupsUser.getNumber("value", -1).intValue();
      final var appUser = appUsers.getString(i);
      final var index = this.indexOfCreatedProfileWithId(appUser);

      final var groupsUserId = groupsUser.getString("userId");
      if (!appUser.equals(groupsUserId)) {

        Logger.warn("Unexpected groups user at {}, {} is not {}.", i, appUser, groupsUserId);
        return false;
      }

      final var groupsUserValue = groupsUser.getDouble("value");
      if (groupsUserValue == null || groupsUserValue < 0d || groupsUserValue > 12d) {

        Logger.warn("Unexpected groups user value at {}, {} is not in [0,12].", i, groupsUserValue);
        return false;
      }

    }

    return true;
  }

}
