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
package eu.internetofus.common.components.profile_diversity_manager;

import eu.internetofus.common.vertx.ComponentClient;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

/**
 * The client to interact with the {@link WeNetProfileDiversityManager}.
 *
 * @see WeNetProfileDiversityManager#register(io.vertx.core.Vertx, WebClient,
 *      io.vertx.core.json.JsonObject)
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetProfileDiversityManagerClient extends ComponentClient implements WeNetProfileDiversityManager {

  /**
   * The default URL to bind the client.
   */
  public static final String DEFAULT_PROFILE_DIVERSITY_MANAGER_API_URL = "https://wenet.u-hopper.com/prod/profile_diversity_manager";

  /**
   * The name of the configuration property that contains the URL to the profile
   * manager API.
   */
  public static final String PROFILE_DIVERSITY_MANAGER_CONF_KEY = "profileDiversityManager";

  /**
   * Create a new service to interact with the WeNet profile manager.
   *
   * @param client to interact with the other modules.
   * @param conf   configuration.
   */
  public WeNetProfileDiversityManagerClient(final WebClient client, final JsonObject conf) {

    super(client, conf.getString(PROFILE_DIVERSITY_MANAGER_CONF_KEY, DEFAULT_PROFILE_DIVERSITY_MANAGER_API_URL));

  }

}
