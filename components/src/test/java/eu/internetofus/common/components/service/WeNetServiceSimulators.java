/*
 * -----------------------------------------------------------------------------
 *
 *   Copyright 2019 - 2022 UDT-IA, IIIA-CSIC
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

package eu.internetofus.common.components.service;

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.models.WeNetUserProfile;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.junit5.VertxTestContext;
import java.util.List;
import java.util.function.Predicate;

/**
 * Utility methods to interact with the {@link WeNetServiceSimulator}.
 *
 * @see WeNetServiceSimulator
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface WeNetServiceSimulators {

  /**
   * Create the APP for the test.
   *
   * @param users       to be defined on the App.
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   *
   * @return the future that will return the created users.
   */
  static Future<App> createApp(final List<WeNetUserProfile> users, final Vertx vertx,
      final VertxTestContext testContext) {

    final Promise<App> promise = Promise.promise();
    StoreServices.storeApp(new App(), vertx, testContext).onSuccess(app -> {

      final var appUsers = new JsonArray();
      for (final WeNetUserProfile profile : users) {

        appUsers.add(profile.id);
      }
      WeNetServiceSimulator.createProxy(vertx).addUsers(app.appId, appUsers,
          testContext.succeeding(added -> promise.complete(app)));
    });

    return promise.future();
  }

  /**
   * Wait the App has received the specified call backs.
   *
   * @param appId          identifier of the application to get the call backs.
   * @param checkCallbacks the function that has to be true to finish the get the
   *                       callbacks.
   * @param vertx          event bus to use.
   * @param testContext    context to do the test.
   *
   * @return the future that will return the callbacks that satisfy the predicate.
   */
  static Future<JsonArray> waitUntilCallbacks(final String appId, final Predicate<JsonArray> checkCallbacks,
      final Vertx vertx, final VertxTestContext testContext) {

    final Promise<JsonArray> promise = Promise.promise();
    waitUntilCallbacks(appId, checkCallbacks, vertx, testContext, promise);
    return promise.future();

  }

  /**
   * Wait the App has received the specified call backs.
   *
   * @param appId          identifier of the application to get the call backs.
   * @param checkCallbacks the function that has to be true to finish the get the
   *                       callbacks.
   * @param vertx          event bus to use.
   * @param testContext    context to do the test.
   * @param promise        to inform of the callbacks.
   */
  static void waitUntilCallbacks(final String appId, final Predicate<JsonArray> checkCallbacks, final Vertx vertx,
      final VertxTestContext testContext, final Promise<JsonArray> promise) {

    WeNetServiceSimulator.createProxy(vertx).retrieveCallbacks(appId, testContext.succeeding(callbacks -> {

      if (checkCallbacks.test(callbacks)) {

        promise.complete(callbacks);

      } else if (!testContext.completed()) {

        waitUntilCallbacks(appId, checkCallbacks, vertx, testContext, promise);
      }

    }));

  }
}
