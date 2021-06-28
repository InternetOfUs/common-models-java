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

package eu.internetofus.common.components.profile_manager;

import java.util.ArrayList;
import java.util.List;

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.models.WeNetUserProfile;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;

/**
 * Utility methods to interact with the {@link WeNetProfileManager}.
 *
 * @see WeNetProfileManager
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface WeNetProfileManagers {

  /**
   * Create user for the test.
   *
   * @param maxUsers    number of users to create.
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   *
   * @return the future that will return the created users.
   */
  static Future<List<WeNetUserProfile>> createUsers(final int maxUsers, final Vertx vertx, final VertxTestContext testContext) {

    final Promise<List<WeNetUserProfile>> promise = Promise.promise();
    var future = promise.future();
    for (var i = 0; i < maxUsers; i++) {

      future = future.compose(users -> {

        return StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).compose(profile -> {
          users.add(profile);
          return Future.succeededFuture(users);
        });

      });

    }

    promise.complete(new ArrayList<>());

    return future;
  }

}
