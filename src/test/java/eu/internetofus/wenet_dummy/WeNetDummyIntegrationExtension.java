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

package eu.internetofus.wenet_dummy;

import eu.internetofus.common.Containers;
import eu.internetofus.common.vertx.AbstractMain;
import eu.internetofus.common.vertx.AbstractWeNetComponentIntegrationExtension;
import eu.internetofus.common.vertx.WeNetModuleContext;
import eu.internetofus.wenet_dummy.service.WeNetDummy;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

/**
 * Extension used to run integration tests over the WeNet dummy.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetDummyIntegrationExtension extends AbstractWeNetComponentIntegrationExtension {

  /**
   * {@inheritDoc}
   */
  @Override
  protected void afterStarted(final WeNetModuleContext context) {

    final var vertx = context.vertx;
    final var client = WebClient.create(vertx);
    final var conf = context.configuration.getJsonObject("wenetComponents", new JsonObject());
    WeNetDummy.register(vertx, client, conf);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String[] createMainStartArguments() {

    final var containers = Containers.status().startBasic();
    var dummyApiPort = Containers.nextFreePort();
    return new String[] { "-papi.port=" + dummyApiPort, "-ppersistence.db_name=" + Containers.MONGODB_NAME,
        "-ppersistence.host=" + containers.getMongoDBHost(), "-ppersistence.port=" + containers.getMongoDBPort(),
        "-ppersistence.username=" + Containers.MONGODB_USER, "-ppersistence.password=" + Containers.MONGODB_PASSWORD,
        "-pwenetComponents.dummy=\"http://localhost:" + dummyApiPort + "\"",
        "-pwenetComponents.profileManager=\"" + containers.getProfileManagerApi() + "\"",
        "-pwenetComponents.taskManager=\"" + containers.getTaskManagerApi() + "\"",
        "-pwenetComponents.interactionProtocolEngine=\"" + containers.getInteractionProtocolEngineApi() + "\"",
        "-pwenetComponents.service=\"" + containers.service.getApiUrl() + "\"",
        "-pwenetComponents.socialContextBuilder=\"" + containers.socialContextBuilder.getApiUrl() + "\"",
        "-pwenetComponents.incentiveServer=\"" + containers.incentiveServer.getApiUrl() + "\"" };

  }

  /**
   * {@inheritDoc}
   *
   * @see Main
   */
  @Override
  protected AbstractMain createMain() {

    return new Main();
  }

}