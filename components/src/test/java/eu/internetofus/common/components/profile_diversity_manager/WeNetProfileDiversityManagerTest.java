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

import static eu.internetofus.common.components.AbstractComponentMocker.createClientWithDefaultSession;

import eu.internetofus.common.components.Containers;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Generic test over the {@link WeNetProfileDiversityManager}.
 *
 * @see WeNetProfileDiversityManager
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class WeNetProfileDiversityManagerTest extends WeNetProfileDiversityManagerTestCase {

  /**
   * Start the mocker server.
   */
  @BeforeAll
  public static void startContainer() {

    Containers.status().exposeModulePortsContainers().startProfileDiversityManagerContainer();
  }

  /**
   * Register the client.
   *
   * @param vertx event bus to use.
   */
  @BeforeEach
  public void registerClient(final Vertx vertx) {

    final var client = createClientWithDefaultSession(vertx);
    final var profileManagerConf = new JsonObject().put(
        WeNetProfileDiversityManagerClient.PROFILE_DIVERSITY_MANAGER_CONF_KEY,
        Containers.status().getProfileDiversityManagerApi());
    WeNetProfileDiversityManager.register(vertx, client, profileManagerConf);
  }
}
