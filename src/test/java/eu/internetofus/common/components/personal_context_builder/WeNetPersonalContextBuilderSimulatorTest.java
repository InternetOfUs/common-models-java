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

package eu.internetofus.common.components.personal_context_builder;

import static eu.internetofus.common.components.AbstractComponentMocker.createClientWithDefaultSession;
import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test the {@link WeNetPersonalContextBuilderSimulator}.
 *
 * @see WeNetPersonalContextBuilderSimulator
 * @see WeNetPersonalContextBuilderSimulatorClient
 * @see WeNetPersonalContextBuilderSimulatorMocker
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class WeNetPersonalContextBuilderSimulatorTest extends WeNetPersonalContextBuilderTestCase {

  /**
   * The service mocked server.
   */
  protected static WeNetPersonalContextBuilderSimulatorMocker serviceMocker;

  /**
   * Start the mocker servers.
   */
  @BeforeAll
  public static void startMockers() {

    serviceMocker = WeNetPersonalContextBuilderSimulatorMocker.start();
  }

  /**
   * Stop the mocker server.
   */
  @AfterAll
  public static void stopMockers() {

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
    final var serviceConf = serviceMocker.getComponentConfiguration();
    WeNetPersonalContextBuilderSimulator.register(vertx, client, serviceConf);
    WeNetPersonalContextBuilder.register(vertx, client, serviceConf);

  }

  /**
   * Should add and obtain users locations.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldAddObtainAndDeleteUserLocation(final Vertx vertx, final VertxTestContext testContext) {

    final var expectedLocations = new UsersLocations();
    final var definedUsers = new Users();
    Future<?> future = Future.succeededFuture();
    for (var i = 0; i < 10; i++) {

      final var location = new UserLocationTest().createModelExample(i);
      definedUsers.userids.add(location.userId);
      definedUsers.userids.add(UUID.randomUUID().toString());
      expectedLocations.locations.add(location);
      future = future
          .compose(ignored -> WeNetPersonalContextBuilderSimulator.createProxy(vertx).addUserLocation(location));

    }

    future = future
        .compose(ignored -> WeNetPersonalContextBuilderSimulator.createProxy(vertx).obtainUserlocations(definedUsers));
    testContext.assertComplete(future).onSuccess(locations -> testContext.verify(() -> {

      assertThat(locations).isEqualTo(expectedLocations);
      testContext
          .assertComplete(WeNetPersonalContextBuilderSimulator.createProxy(vertx)
              .deleteLocation(expectedLocations.locations.get(0).userId).compose(
                  ignore -> WeNetPersonalContextBuilderSimulator.createProxy(vertx).obtainUserlocations(definedUsers)))
          .onSuccess(loctions2 -> testContext.verify(() -> {

            assertThat(loctions2).isNotEqualTo(expectedLocations);
            expectedLocations.locations.remove(0);
            assertThat(loctions2).isEqualTo(expectedLocations);
            testContext.completeNow();

          }));
    }));

  }

  /**
   * Should obtain the closest users into a location.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldAddLocationsAndObtainClosestUsers(final Vertx vertx, final VertxTestContext testContext) {

    final var expectedDistances = new ArrayList<UserDistance>();
    Future<?> future = Future.succeededFuture();
    for (var i = 0; i < 10; i++) {

      final var location = new UserLocationTest().createModelExample(i);
      future = future
          .compose(ignored -> WeNetPersonalContextBuilderSimulator.createProxy(vertx).addUserLocation(location));
      final var userDistance = new UserDistance();
      userDistance.userId = location.userId;
      userDistance.distance = UserDistance.calculateDistance(0, 0, location.latitude, location.longitude);
      expectedDistances.add(userDistance);

    }

    future = future
        .compose(ignored -> WeNetPersonalContextBuilderSimulator.createProxy(vertx).obtainClosestUsersTo(0, 0, 10));
    testContext.assertComplete(future).onSuccess(closest1 -> testContext.verify(() -> {

      assertThat(closest1).isEqualTo(expectedDistances);
      testContext.assertComplete(WeNetPersonalContextBuilderSimulator.createProxy(vertx).obtainClosestUsersTo(0, 0, 5))
          .onSuccess(closest2 -> testContext.verify(() -> {

            assertThat(closest2).isEqualTo(expectedDistances.subList(0, 5));
            testContext.completeNow();

          }));

    }));
  }
}
