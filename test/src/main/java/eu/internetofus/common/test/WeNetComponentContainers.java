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

package eu.internetofus.common.test;

import java.net.ServerSocket;

/**
 * Interface for the element that provide access to the WeNet components to use
 * in the tests.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface WeNetComponentContainers {

  /**
   * The default wenet component apikey.
   */
  public static final String DEFAULT_WENET_COMPONENT_APIKEY = "secret";

  /**
   * The host where the mongoDB is started.
   *
   * @return the host to the MongoDB.
   */
  public String getMongoDBHost();

  /**
   * The port where the mongoDB is started.
   *
   * @return the port to the MongoDB.
   */
  public int getMongoDBPort();

  /**
   * The URL to the API to interact with the profile manager.
   *
   * @return the URL to the profile manager API.
   */
  public String getProfileManagerApi();

  /**
   * Return the URL to the profile manager extension word net sim API.
   *
   * @return the profile manager extension word net sim API URL.
   */
  public String getProfileManagerExtWordNetSimApi();

  /**
   * The URL to the API to interact with the task manager.
   *
   * @return the URL to the task manager API.
   */
  public String getTaskManagerApi();

  /**
   * The URL to the API to interact with the interaction protocol engine.
   *
   * @return the URL to the interaction protocol engine API.
   */
  public String getInteractionProtocolEngineApi();

  /**
   * The URL to the API to interact with the service.
   *
   * @return the URL to the service API.
   */
  public String getServiceApi();

  /**
   * The URL to the API to interact with the social context builder.
   *
   * @return the URL to the social context builder API.
   */
  public String getSocialContextBuilderApi();

  /**
   * The URL to the API to interact with the incentive server.
   *
   * @return the URL to the incentive server API.
   */
  public String getIncentiveServerApi();

  /**
   * The URL to the API to interact with the personal context builder.
   *
   * @return the URL to the personal context builder API.
   */
  public String getPersonalContextBuilderApi();

  /**
   * Search for a free port.
   *
   * @return a free port.
   */
  public static int nextFreePort() {

    var port = 0;
    try {
      final var server = new ServerSocket(0);
      port = server.getLocalPort();
      server.close();
    } catch (final Throwable ignored) {
    }

    return port;
  }

}
