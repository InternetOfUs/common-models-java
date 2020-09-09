/*
 * -----------------------------------------------------------------------------
 *
 * Copyright (c) 2019 - 2022 UDT-IA, IIIA-CSIC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * -----------------------------------------------------------------------------
 */

package eu.internetofus.common.components.service;

import java.util.List;
import java.util.function.Predicate;

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.profile_manager.WeNetUserProfile;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.junit5.VertxTestContext;

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
  static Future<App> createApp(final List<WeNetUserProfile> users, final Vertx vertx, final VertxTestContext testContext) {

    final Promise<App> promise = Promise.promise();
    StoreServices.storeApp(new App(), vertx, testContext, testContext.succeeding(app -> {

      final var appUsers = new JsonArray();
      for (final WeNetUserProfile profile : users) {

        appUsers.add(profile.id);
      }
      WeNetServiceSimulator.createProxy(vertx).addUsers(app.appId, appUsers, testContext.succeeding(added -> promise.complete(app)));
    }));

    return promise.future();
  }

  /**
   * Wait the App has received the specified call backs.
   *
   * @param appId          identifier of the application to get the call backs.
   * @param checkCallbacks the function that has to be true to finish the get the callbacks.
   * @param vertx          event bus to use.
   * @param testContext    context to do the test.
   *
   * @return the future that will return the callbacks that satisfy the predicate.
   */
  static Future<JsonArray> waitUntilCallbacks(final String appId, final Predicate<JsonArray> checkCallbacks, final Vertx vertx, final VertxTestContext testContext) {

    final Promise<JsonArray> promise = Promise.promise();
    WeNetServiceSimulator.createProxy(vertx).retrieveJsonCallbacks(appId, testContext.succeeding(callbacks -> {

      if (checkCallbacks.test(callbacks)) {

        promise.complete(callbacks);

      } else {

        waitUntilCallbacks(appId, checkCallbacks, vertx, testContext).onComplete(testContext.succeeding(result -> promise.complete(result)));
      }

    }));

    return promise.future();
  }

}
