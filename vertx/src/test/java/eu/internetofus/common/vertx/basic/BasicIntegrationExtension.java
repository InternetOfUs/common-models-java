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
package eu.internetofus.common.vertx.basic;

import eu.internetofus.common.test.MongoContainer;
import eu.internetofus.common.test.WeNetComponentContainers;
import eu.internetofus.common.vertx.AbstractMain;
import eu.internetofus.common.vertx.AbstractWeNetComponentIntegrationExtension;
import eu.internetofus.common.vertx.MainArgumentBuilder;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientSession;

/**
 * The extension to do integration test over the vertx components.
 *
 * @see Main
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class BasicIntegrationExtension extends AbstractWeNetComponentIntegrationExtension {

  /**
   * The container with the mongo database.
   */
  protected static final MongoContainer<?> container = new MongoContainer<>();

  /**
   * The port for the basoic API.
   */
  protected static final int apiPort = WeNetComponentContainers.nextFreePort();

  /**
   * {@inheritDoc}
   */
  @Override
  protected void afterStarted(final Vertx vertx, final WebClientSession client, final JsonObject conf) {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String[] createMainStartArguments() {

    container.startMongoContainer();
    return new MainArgumentBuilder().withApiPort(apiPort).withMongoDB(container).build();
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
