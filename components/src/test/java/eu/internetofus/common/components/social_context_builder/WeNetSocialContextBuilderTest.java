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

package eu.internetofus.common.components.social_context_builder;

import static eu.internetofus.common.components.AbstractComponentMocker.createClientWithDefaultSession;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test the {@link WeNetSocialContextBuilder}.
 *
 * @see WeNetSocialContextBuilder
 * @see WeNetSocialContextBuilderClient
 * @see WeNetSocialContextBuilderSimulatorMocker
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class WeNetSocialContextBuilderTest extends WeNetSocialContextBuilderTestCase {

  /**
   * The profile manager mocked server.
   */
  protected static WeNetSocialContextBuilderSimulatorMocker mocker;

  /**
   * Start the mocker server.
   */
  @BeforeAll
  public static void startMocker() {

    mocker = WeNetSocialContextBuilderSimulatorMocker.start();
  }

  /**
   * Stop the mocker server.
   */
  @AfterAll
  public static void stopMockers() {

    mocker.stopServer();
  }

  /**
   * Register the client.
   *
   * @param vertx event bus to use.
   */
  @BeforeEach
  public void registerClient(final Vertx vertx) {

    final var client = createClientWithDefaultSession(vertx);
    final var conf = mocker.getComponentConfiguration();
    WeNetSocialContextBuilder.register(vertx, client, conf);
  }

}
