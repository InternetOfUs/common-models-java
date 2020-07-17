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
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
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

package eu.internetofus.common.components.profile_manager;

import java.util.ArrayList;
import java.util.List;

import eu.internetofus.common.components.StoreServices;
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
    Future<List<WeNetUserProfile>> future = promise.future();
    for (int i = 0; i < maxUsers; i++) {

      future = future.compose(users -> {

        final Promise<List<WeNetUserProfile>> profilePromise = Promise.promise();
        StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext, testContext.succeeding(profile -> {
          users.add(profile);
          profilePromise.complete(users);
        }));
        return profilePromise.future();

      });

    }

    promise.complete(new ArrayList<>());

    return future;
  }

}
