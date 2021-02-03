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

import static eu.internetofus.common.components.AbstractComponentMocker.createClientWithDefaultSession;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.ModelTestCase;
import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.components.profile_manager.WeNetProfileManagerMocker;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test the {@link App}.
 *
 * @see App
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class AppTest extends ModelTestCase<App> {

  /**
   * The profile manager mocked server.
   */
  protected static WeNetProfileManagerMocker profileManagerMocker;

  /**
   * The service mocked server.
   */
  protected static WeNetServiceSimulatorMocker serviceMocker;

  /**
   * Start the mocker server.
   */
  @BeforeAll
  public static void startMockers() {

    profileManagerMocker = WeNetProfileManagerMocker.start();
    serviceMocker = WeNetServiceSimulatorMocker.start();
  }

  /**
   * Stop the mocker server.
   */
  @AfterAll
  public static void stopMockers() {

    profileManagerMocker.stopServer();
    serviceMocker.stopServer();
  }

  /**
   * Register the necessary services before to test.
   *
   * @param vertx event bus to register the necessary services.
   */
  @BeforeEach
  public void registerServices(final Vertx vertx) {

    final var client = createClientWithDefaultSession(vertx);
    final var profileConf = profileManagerMocker.getComponentConfiguration();
    WeNetProfileManager.register(vertx, client, profileConf);

    final var conf = serviceMocker.getComponentConfiguration();
    WeNetService.register(vertx, client, conf);
    WeNetServiceSimulator.register(vertx, client, conf);

  }

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
   * Check that should create a community for an application.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see App#getOrCreateDefaultCommunityFor(String, Vertx)
   */
  @Test
  public void shouldCreateCommunityWithMembers(final Vertx vertx, final VertxTestContext testContext) {

    final var users = new JsonArray().add("1").add("2").add("3").add("4");
    testContext.assertComplete(StoreServices.storeApp(new App(), vertx, testContext))
        .onSuccess(app -> testContext
            .assertComplete(testContext.assertComplete(WeNetServiceSimulator.createProxy(vertx)
                .addUsers(app.appId, users).compose(empty -> App.getOrCreateDefaultCommunityFor(app.appId, vertx))))
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

    final var users = new JsonArray().add("10").add("20").add("30").add("40");
    testContext.assertComplete(StoreServices.storeCommunityExample(1, vertx, testContext))
        .onSuccess(defaultCommunity -> testContext
            .assertComplete(WeNetServiceSimulator.createProxy(vertx).addUsers(defaultCommunity.appId, users)
                .compose(empty -> App.getOrCreateDefaultCommunityFor(defaultCommunity.appId, vertx)))
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

            })));

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
