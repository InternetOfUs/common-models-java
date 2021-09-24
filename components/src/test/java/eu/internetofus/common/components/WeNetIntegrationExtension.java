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

package eu.internetofus.common.components;

import eu.internetofus.common.components.interaction_protocol_engine.WeNetInteractionProtocolEngine;
import eu.internetofus.common.components.profile_diversity_manager.WeNetProfileDiversityManager;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.components.service.WeNetService;
import eu.internetofus.common.components.service.WeNetServiceSimulator;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import eu.internetofus.common.vertx.AbstractServicesVerticle;
import eu.internetofus.common.vertx.AbstractWeNetIntegrationExtension;
import eu.internetofus.common.vertx.WeNetModuleContext;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;

/**
 * Extension that start all the containers.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetIntegrationExtension extends AbstractWeNetIntegrationExtension {

  /**
   * {@inheritDoc}
   */
  @Override
  protected synchronized WeNetModuleContext createContext() {

    final var containers = Containers.status();

    containers.startAll();

    final var wenetComponents = new JsonObject();
    final var configuration = new JsonObject().put("wenetComponents", wenetComponents)
        .put("api", new JsonObject().put("host", "localhost").put("port", 80))
        .put("persistence", containers.getMongoDBConfig())
        .put(AbstractServicesVerticle.WEB_CLIENT_CONF_KEY, new JsonObject()
            .put(AbstractServicesVerticle.WENET_COMPONENT_APIKEY_CONF_KEY, Containers.DEFAULT_WENET_COMPONENT_APIKEY));
    wenetComponents.mergeIn(containers.getProfileManagerClientConf());
    wenetComponents.mergeIn(containers.getProfileDiversityManagerClientConf());
    wenetComponents.mergeIn(containers.getTaskManagerClientConf());
    wenetComponents.mergeIn(containers.getInteractionProtocolEngineClientConf());
    wenetComponents.mergeIn(containers.getServiceClientConf());

    final var options = new VertxOptions(configuration);
    final var vertx = Vertx.vertx(options);
    final var client = AbstractServicesVerticle.createWebClientSession(vertx, configuration);

    WeNetProfileManager.register(vertx, client, wenetComponents);
    WeNetProfileDiversityManager.register(vertx, client, wenetComponents);
    WeNetTaskManager.register(vertx, client, wenetComponents);
    WeNetInteractionProtocolEngine.register(vertx, client, wenetComponents);
    WeNetService.register(vertx, client, wenetComponents);
    WeNetServiceSimulator.register(vertx, client, wenetComponents);

    return new WeNetModuleContext(vertx, configuration);
  }

}
