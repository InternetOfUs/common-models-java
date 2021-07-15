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

package eu.internetofus.common.components.incentive_server;

import eu.internetofus.common.vertx.ComponentClient;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

/**
 * The client to interact with the {@link WeNetIncentiveServer}.
 *
 * @see WeNetIncentiveServer#register(io.vertx.core.Vertx, WebClient,
 *      io.vertx.core.json.JsonObject)
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetIncentiveServerClient extends ComponentClient implements WeNetIncentiveServer {

  /**
   * The default URL to bind the client.
   */
  public static final String DEFAULT_INCENTIVE_SERVER_API_URL = "https://wenet.u-hopper.com/prod/incentive_server";

  /**
   * The name of the configuration property that contains the URL to the incentive
   * server API.
   */
  public static final String INCENTIVE_SERVER_CONF_KEY = "incentiveServer";

  /**
   * Create a new incentive_server.
   *
   * @param client to interact with the other modules.
   * @param conf   configuration of the component.
   */
  public WeNetIncentiveServerClient(final WebClient client, final JsonObject conf) {

    super(client, conf.getString(INCENTIVE_SERVER_CONF_KEY, DEFAULT_INCENTIVE_SERVER_API_URL));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateTaskStatus(final JsonObject status, final Handler<AsyncResult<JsonObject>> handler) {

    this.post(status, "/Tasks/TaskStatus/").onComplete(handler);

  }

}
