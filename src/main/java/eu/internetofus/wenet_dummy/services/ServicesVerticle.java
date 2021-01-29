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
