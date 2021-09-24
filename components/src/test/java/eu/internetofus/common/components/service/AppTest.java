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

package eu.internetofus.common.components.service;

import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.WeNetIntegrationExtension;
import eu.internetofus.common.model.ModelTestCase;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test the {@link App}.
 *
 * @see App
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetIntegrationExtension.class)
public class AppTest extends ModelTestCase<App> {

  /**
   * {@inheritDoc}
   */
  @Override
  public App createModelExample(final int index) {

    final var model = new App();
    model.appId = "appId_" + index;
    model.appToken = "token_" + index;
    model.messageCallbackUrl = "https://app.endpoint.com/messages/" + index;
    model.metadata = new JsonObject().put("index", index);
    model.allowedPlatforms = new ArrayList<>();
    model.allowedPlatforms.add(new JsonObject().put("platform", index));
    return model;

  }

  /**
   * Check that should create a community for an application without members.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see App#getOrCreateDefaultCommunityFor(String, Vertx)
   */
  @Test
  public void shouldCreateCommunityWithoutMembers(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertComplete(StoreServices.storeApp(new App(), vertx, testContext))
        .onSuccess(app -> testContext.assertComplete(App.getOrCreateDefaultCommunityFor(app.appId, vertx))
            .onSuccess(createdCommunity -> testContext.verify(() -> {

              assertThat(createdCommunity.members).isNull();
              assertThat(createdCommunity.name).contains(app.appId);
              assertThat(createdCommunity.appId).isEqualTo(app.appId);
              testContext.assertComplete(App.getOrCreateDefaultCommunityFor(app.appId, vertx))
                  .onSuccess(getCommunity -> testContext.verify(() -> {

                    assertThat(createdCommunity).isEqualTo(getCommunity);
                    testContext.completeNow();

                  }));

            })));

  }

  /**
   * Add some users into an application.
   *
   * @param appId       identifier of the application to add the users.
   * @param max         the number of users to add to the application.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @return the future application.
   */
  public static Future<JsonArray> addUsersToApp(final String appId, final int max, final Vertx vertx,
      final VertxTestContext testContext) {

    Future<JsonArray> future = Future.succeededFuture(new JsonArray());
    for (var i = 0; i < max; i++) {

      future = future.compose(users -> StoreServices.storeProfileExample(1, vertx, testContext).map(profile -> {
        users.add(profile.id);
        return users;
      }));
    }

    return testContext
        .assertComplete(future.compose(users -> WeNetServiceSimulator.createProxy(vertx).addUsers(appId, users)));
  }

  /**
   * Check that should create a community for an application.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see App#getOrCreateDefaultCommunityFor(String, Vertx)
   */
  @Test
  public void shouldCreateCommunityWithMembers(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeApp(new App(), vertx, testContext).onSuccess(app -> {

      addUsersToApp(app.appId, 5, vertx, testContext)
          .onSuccess(users -> testContext.assertComplete(App.getOrCreateDefaultCommunityFor(app.appId, vertx))
              .onSuccess(createdCommunity -> testContext.verify(() -> {

                assertThat(createdCommunity.members).isNotEmpty();
                for (final var member : createdCommunity.members) {

                  assertThat(users.remove(member.userId)).isTrue();
                }
                assertThat(users).isEmpty();
                assertThat(createdCommunity.name).contains(app.appId);
                assertThat(createdCommunity.appId).isEqualTo(app.appId);

                testContext.assertComplete(App.getOrCreateDefaultCommunityFor(app.appId, vertx))
                    .onSuccess(getCommunity -> testContext.verify(() -> {

                      assertThat(createdCommunity).isEqualTo(getCommunity);
                      testContext.completeNow();

                    }));

              })));

    });

  }

  /**
   * Check that should get default community of an application.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see App#getOrCreateDefaultCommunityFor(String, Vertx)
   */
  @Test
  public void shouldGetDefaultCommunityForApp(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeCommunityExample(1, vertx, testContext).onSuccess(defaultCommunity -> {

      addUsersToApp(defaultCommunity.appId, 5, vertx, testContext).onSuccess(users -> {
        testContext.assertComplete(App.getOrCreateDefaultCommunityFor(defaultCommunity.appId, vertx))
            .onSuccess(getCommunity -> testContext.verify(() -> {

              assertThat(getCommunity).isNotEqualTo(defaultCommunity);

              assertThat(getCommunity.members).isNotEmpty();
              defaultCommunity.members = new ArrayList<>();
              for (final var member : getCommunity.members) {

                for (var i = 0; i < users.size(); i++) {

                  if (member.userId.equals(users.getString(i))) {

                    users.remove(i);
                    defaultCommunity.members.add(member);
                    break;
                  }
                }

              }

              assertThat(users).isEmpty();
              defaultCommunity._lastUpdateTs = getCommunity._lastUpdateTs;
              assertThat(getCommunity).isEqualTo(defaultCommunity);
              testContext.completeNow();

            }));
      });
    });

  }

  /**
   * Check that should fail get default community of an application without
   * identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see App#getOrCreateDefaultCommunityFor(String, Vertx)
   */
  @Test
  public void shouldFailGetDefaultCommunityWithoutAppIdentifier(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertFailure(App.getOrCreateDefaultCommunityFor(null, vertx))
        .onFailure(error -> testContext.completeNow());

  }

  /**
   * Check that should fail get default community of an undefined application.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see App#getOrCreateDefaultCommunityFor(String, Vertx)
   */
  @Test
  public void shouldFailGetDefaultCommunityWithUndefinedApp(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertFailure(App.getOrCreateDefaultCommunityFor("undefined", vertx))
        .onFailure(error -> testContext.completeNow());

  }

}
