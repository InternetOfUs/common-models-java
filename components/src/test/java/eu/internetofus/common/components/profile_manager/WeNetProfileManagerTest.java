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

import static eu.internetofus.common.components.AbstractComponentMocker.createClientWithDefaultSession;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.service.WeNetService;
import eu.internetofus.common.components.service.WeNetServiceSimulator;
import eu.internetofus.common.components.service.WeNetServiceSimulatorMocker;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test the {@link WeNetProfileManager}.
 *
 * @see WeNetProfileManager
 * @see WeNetProfileManagerClient
 * @see WeNetProfileManagerMocker
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class WeNetProfileManagerTest extends WeNetProfileManagerTestCase {

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
  public static void startMocker() {

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
   * Register the client.
   *
   * @param vertx event bus to use.
   */
  @BeforeEach
  public void registerClient(final Vertx vertx) {

    final var client = createClientWithDefaultSession(vertx);
    final var profileManagerConf = profileManagerMocker.getComponentConfiguration();
    WeNetProfileManager.register(vertx, client, profileManagerConf);

    final var serviceConf = serviceMocker.getComponentConfiguration();
    WeNetServiceSimulator.register(vertx, client, serviceConf);
    WeNetService.register(vertx, client, serviceConf);

  }

  /**
   * Should return an empty profiles page.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldReturnEmptyCommunityProfilesPage(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertComplete(WeNetProfileManager.createProxy(vertx).retrieveCommunityProfilesPage(null, null, null,
        null, null, null, 0, 100)).onSuccess(page -> testContext.verify(() -> {

          assertThat(page).isNotNull();
          assertThat(page.offset).isEqualTo(0);
          assertThat(page.total).isEqualTo(0);
          assertThat(page.communities).isNull();

          testContext.assertComplete(WeNetProfileManager.createProxy(vertx).retrieveCommunityProfilesPage("appId",
              "name", "description", "keywords", "members", "order", 0, 100))
              .onSuccess(page2 -> testContext.verify(() -> {

                assertThat(page).isEqualTo(page2);
                testContext.completeNow();

              }));
        }));

  }

}
