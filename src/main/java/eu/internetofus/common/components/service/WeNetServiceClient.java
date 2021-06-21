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

package eu.internetofus.common.components.service;

import eu.internetofus.common.vertx.ComponentClient;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

/**
 * The implementation of the {@link WeNetService}.
 *
 * @see WeNetService
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetServiceClient extends ComponentClient implements WeNetService {

  /**
   * The default URL to bind the client.
   */
  public static final String DEFAULT_SERVICE_API_URL = "https://wenet.u-hopper.com/prod/service";

  /**
   * The name of the configuration property that contains the URL to the service API.
   */
  public static final String SERVICE_CONF_KEY = "service";

  /**
   * Create a new service to interact with the WeNet service.
   *
   * @param client to interact with the other modules.
   * @param conf   configuration.
   */
  public WeNetServiceClient(final WebClient client, final JsonObject conf) {

    super(client, conf.getString(SERVICE_CONF_KEY, DEFAULT_SERVICE_API_URL));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveApp(final String id, final Handler<AsyncResult<JsonObject>> handler) {

    this.getJsonObject("/app", id).onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveAppUserIds(final String id, final Handler<AsyncResult<JsonArray>> handler) {

    this.getJsonArray("/app", id, "/users").onComplete(handler);

  }

}
