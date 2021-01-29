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

package eu.internetofus.common.components.interaction_protocol_engine;

import static eu.internetofus.common.components.AbstractComponentMocker.createClientWithDefaultSession;

import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.components.profile_manager.WeNetProfileManagerMocker;
import eu.internetofus.common.components.service.WeNetService;
import eu.internetofus.common.components.service.WeNetServiceSimulator;
import eu.internetofus.common.components.service.WeNetServiceSimulatorMocker;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import eu.internetofus.common.components.task_manager.WeNetTaskManagerMocker;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test the {@link WeNetInteractionProtocolEngine}.
 *
 * @see WeNetInteractionProtocolEngine
 * @see WeNetInteractionProtocolEngineClient
 * @see WeNetInteractionProtocolEngineMocker
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class WeNetInteractionProtocolEngineTest extends WeNetInteractionProtocolEngineTestCase {

  /**
   * The task manager mocked server.
   */
  protected static WeNetTaskManagerMocker taskManagerMocker;

  /**
   * The profile manager mocked server.
   */
  protected static WeNetProfileManagerMocker profileManagerMocker;

  /**
   * The service mocked server.
   */
  protected static WeNetServiceSimulatorMocker serviceMocker;

  /**
   * The interaction protocol engine mocked server.
   */
  protected static WeNetInteractionProtocolEngineMocker interactionProtocolEngineMocker;

  /**
   * Start the mocker servers.
   */
  @BeforeAll
  public static void startMockers() {

    taskManagerMocker = WeNetTaskManagerMocker.start();
    profileManagerMocker = WeNetProfileManagerMocker.start();
    serviceMocker = WeNetServiceSimulatorMocker.start();
    interactionProtocolEngineMocker = WeNetInteractionProtocolEngineMocker.start();
  }

  /**
   * Stop the mocker server.
   */
  @AfterAll
  public static void stopMockers() {

    taskManagerMocker.stopServer();
    profileManagerMocker.stopServer();
    serviceMocker.stopServer();
    interactionProtocolEngineMocker.stopServer();
  }

  /**
   * Register the client.
   *
   * @param vertx event bus to use.
   */
  @BeforeEach
  public void registerClient(final Vertx vertx) {

    final var client = createClientWithDefaultSession(vertx);
    final var taskConf = taskManagerMocker.getComponentConfiguration();
    WeNetTaskManager.register(vertx, client, taskConf);

    final var profileConf = profileManagerMocker.getComponentConfiguration();
    WeNetProfileManager.register(vertx, client, profileConf);

    final var conf = serviceMocker.getComponentConfiguration();
    WeNetService.register(vertx, client, conf);
    WeNetServiceSimulator.register(vertx, client, conf);

    final var interactionProtocolEngineMockerConf = interactionProtocolEngineMocker.getComponentConfiguration();
    WeNetInteractionProtocolEngine.register(vertx, client, interactionProtocolEngineMockerConf);

  }
}
