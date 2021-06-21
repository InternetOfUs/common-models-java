/*
 * -----------------------------------------------------------------------------
 *
 *   Copyright 2019 - 2022 UDT-IA, IIIA-CSIC
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

package eu.internetofus.common.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.client.WebClientSession;

/**
 * Abstract verticle to start the services to interact with the other WeNet
 * modules.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractServicesVerticle extends AbstractVerticle {

  /**
   * The name of the header with the component api key.
   */
  public static final String WENET_COMPONENT_APIKEY_HEADER = "x-wenet-component-apikey";

  /**
   * The name of the configuration key with the component api key.
   */
  public static final String WENET_COMPONENT_APIKEY_CONF_KEY = "wenetComponentApikey";

  /**
   * The name of the configuration key with the web client values.
   */
  public static final String WEB_CLIENT_CONF_KEY = "webClient";

  /**
   * The client to do the HTTP request to other components.
   */
  protected WebClientSession client;

  /**
   * {@inheritDoc}
   */
  @Override
  public void start(final Promise<Void> startPromise) throws Exception {

    try {

      this.client = createWebClientSession(this.getVertx(), this.config());

      final var serviceConf = this.config().getJsonObject("wenetComponents", new JsonObject());
      this.registerServices(serviceConf);

      startPromise.complete();

    } catch (final Throwable cause) {

      startPromise.fail(cause);
    }
  }

  /**
   * Called when has to register the services to the Vertx event bus.
   *
   * @param serviceConf configuration of the services.
   *
   * @throws Exception If can not register or create teh service to register.
   */
  protected abstract void registerServices(JsonObject serviceConf) throws Exception;

  /**
   * Create a web session client.
   *
   * @param vertx  event bus to use.
   * @param config configuration of the platform.
   *
   * @return the client with the session information.
   */
  public static WebClientSession createWebClientSession(final Vertx vertx, final JsonObject config) {

    final var webClientConf = config.getJsonObject(WEB_CLIENT_CONF_KEY, new JsonObject());
    final var options = new WebClientOptions(webClientConf);

    final var client = WebClientSession.create(WebClient.create(vertx, options));
    final var apiKey = webClientConf.getString(WENET_COMPONENT_APIKEY_CONF_KEY, "UDEFINED");
    client.addHeader(WENET_COMPONENT_APIKEY_HEADER, apiKey);
    return client;

  }

  /**
   * Close the web client.
   *
   * {@inheritDoc}
   *
   * @see #client
   */
  @Override
  public void stop() {

    if (this.client != null) {

      this.client.close();
      this.client = null;
    }
  }

}
