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
package eu.internetofus.wenet_dummy.services;

import eu.internetofus.common.components.incentive_server.WeNetIncentiveServer;
import eu.internetofus.common.components.interaction_protocol_engine.WeNetInteractionProtocolEngine;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.components.service.WeNetService;
import eu.internetofus.common.components.social_context_builder.WeNetSocialContextBuilder;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import eu.internetofus.common.vertx.AbstractServicesVerticle;
import eu.internetofus.wenet_dummy.service.WeNetDummy;
import io.vertx.core.json.JsonObject;

/**
 * Register the services to interact with the other components.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ServicesVerticle extends AbstractServicesVerticle {

  /**
   * {@inheritDoc}
   *
   * @see WeNetProfileManager#register(io.vertx.core.Vertx,
   *      io.vertx.ext.web.client.WebClient, JsonObject)
   * @see WeNetTaskManager#register(io.vertx.core.Vertx,
   *      io.vertx.ext.web.client.WebClient, JsonObject)
   * @see WeNetInteractionProtocolEngine#register(io.vertx.core.Vertx,
   *      io.vertx.ext.web.client.WebClient, JsonObject)
   * @see WeNetService#register(io.vertx.core.Vertx,
   *      io.vertx.ext.web.client.WebClient, JsonObject)
   * @see WeNetSocialContextBuilder#register(io.vertx.core.Vertx,
   *      io.vertx.ext.web.client.WebClient, JsonObject)
   * @see WeNetIncentiveServer#register(io.vertx.core.Vertx,
   *      io.vertx.ext.web.client.WebClient, JsonObject)
   * @see WeNetDummy#register(io.vertx.core.Vertx,
   *      io.vertx.ext.web.client.WebClient, JsonObject)
   */
  @Override
  protected void registerServices(final JsonObject serviceConf) throws Exception {

    WeNetProfileManager.register(this.vertx, this.client, serviceConf);
    WeNetTaskManager.register(this.vertx, this.client, serviceConf);
    WeNetInteractionProtocolEngine.register(this.vertx, this.client, serviceConf);
    WeNetService.register(this.vertx, this.client, serviceConf);
    WeNetSocialContextBuilder.register(this.vertx, this.client, serviceConf);
    WeNetIncentiveServer.register(this.vertx, this.client, serviceConf);
    WeNetDummy.register(this.vertx, this.client, serviceConf);

  }

}
