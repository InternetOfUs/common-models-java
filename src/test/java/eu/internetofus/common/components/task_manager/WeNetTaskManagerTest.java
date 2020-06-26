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

package eu.internetofus.common.components.task_manager;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.components.profile_manager.WeNetProfileManagerMocker;
import eu.internetofus.common.components.service.WeNetService;
import eu.internetofus.common.components.service.WeNetServiceMocker;
import eu.internetofus.common.components.service.WeNetServiceSimulator;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;

/**
 * Test the {@link WeNetTaskManager}.
 *
 * @see WeNetTaskManager
 * @see WeNetTaskManagerClient
 * @see WeNetTaskManagerMocker
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class WeNetTaskManagerTest extends WeNetTaskManagerTestCase {

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
  protected static WeNetServiceMocker serviceMocker;

  /**
   * Start the mocker servers.
   */
  @BeforeAll
  public static void startMockers() {

    taskManagerMocker = WeNetTaskManagerMocker.start();
    profileManagerMocker = WeNetProfileManagerMocker.start();
    serviceMocker = WeNetServiceMocker.start();
  }

  /**
   * Stop the mocker server.
   */
  @AfterAll
  public static void stopMockers() {

    profileManagerMocker.stop();
    taskManagerMocker.stop();
    serviceMocker.stop();
  }

  /**
   * Register the client.
   *
   * @param vertx event bus to use.
   */
  @BeforeEach
  public void registerClient(final Vertx vertx) {

    final WebClient client = WebClient.create(vertx);
    final JsonObject taskConf = taskManagerMocker.getComponentConfiguration();
    WeNetTaskManager.register(vertx, client, taskConf);

    final JsonObject profileConf = profileManagerMocker.getComponentConfiguration();
    WeNetProfileManager.register(vertx, client, profileConf);

    final JsonObject conf = serviceMocker.getComponentConfiguration();
    WeNetService.register(vertx, client, conf);
    WeNetServiceSimulator.register(vertx, client, conf);

  }

}
