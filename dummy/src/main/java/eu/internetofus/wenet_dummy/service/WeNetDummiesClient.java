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

package eu.internetofus.wenet_dummy.service;

import eu.internetofus.common.vertx.ComponentClient;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import javax.validation.constraints.NotNull;

/**
 * The implementation of the {@link WeNetDummy}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetDummiesClient extends ComponentClient implements WeNetDummy {

  /**
   * The name of the configuration property that contains the URL to the profile
   * manager API.
   */
  public static final String DUMMY_CONF_KEY = "dummy";

  /**
   * Create a new service to interact with the WeNet profile manager.
   *
   * @param client to interact with the other modules.
   * @param conf   configuration.
   */
  public WeNetDummiesClient(final WebClient client, final JsonObject conf) {

    super(client, conf.getString(DUMMY_CONF_KEY, "http://localhost:8080"));

  }

  @Override
  public void createDummy(@NotNull JsonObject dummy, @NotNull Handler<AsyncResult<JsonObject>> handler) {

    this.post(dummy, "/dummies").onComplete(handler);

  }

}
