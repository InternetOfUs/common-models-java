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
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
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

package eu.internetofus.common;

import java.net.ServerSocket;
import java.nio.file.FileSystems;

import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.MountableFile;

import eu.internetofus.common.vertx.AbstractMain;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Utility classes to manage containers.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface Containers {

  /**
   * The name of the mongo docker container to use.
   */
  String MONGO_DOCKER_NAME = "mongo:4.2.3";

  /**
   * The port for the MongoDB that has to be exported.
   */
  int EXPORT_MONGODB_PORT = 27017;

  /**
   * The port that listen for the API requests on a container to be exported.
   */
  int EXPORT_API_PORT = 8080;

  /**
   * The name of the WeNet profile manager docker container to use.
   */
  String WENET_PROFILE_MANAGER_DOCKER_NAME = "wenet/profile-manager:0.14.0";

  /**
   * The name of the WeNet profile manager database.
   */
  String WENET_PROFILE_MANAGER_DB_NAME = "wenetProfileManagerDB";

  /**
   * The name of the WeNet task manager docker container to use.
   */
  String WENET_TASK_MANAGER_DOCKER_NAME = "wenet/task-manager:0.5.0";

  /**
   * The name of the WeNet task manager database.
   */
  String WENET_TASK_MANAGER_DB_NAME = "wenetTaskManagerDB";

  /**
   * The name of the WeNet interaction manager docker container to use.
   */
  String WENET_INTERACTION_PROTOCOL_ENGINE_DOCKER_NAME = "wenet/interaction-protocol-engine:0.10.0";

  /**
   * The name of the WeNet interaction manager database.
   */
  String WENET_INTERACTION_PROTOCOL_ENGINE_DB_NAME = "wenetInteractionProtocolEngineDB";

  /**
   * Search for a free port.
   *
   * @return a free port.
   */
  static int nextFreePort() {

    int port = 0;
    try {
      final ServerSocket server = new ServerSocket(0);
      port = server.getLocalPort();
      server.close();
    } catch (final Throwable ignored) {
    }

    return port;
  }

  /**
   * Create a new mongo container.
   *
   * @param dbName  name of the database to start
   * @param network that shared between containers.
   *
   ** @return the mongo container to the specified database.
   */
  @SuppressWarnings("resource")
  static GenericContainer<?> createMongoContainerFor(final String dbName, final Network network) {

    return new GenericContainer<>(MONGO_DOCKER_NAME).withStartupAttempts(1).withEnv("MONGO_INITDB_ROOT_USERNAME", "root").withEnv("MONGO_INITDB_ROOT_PASSWORD", "password").withEnv("MONGO_INITDB_DATABASE", dbName)
        .withCopyFileToContainer(MountableFile.forClasspathResource(Containers.class.getPackageName().replaceAll("\\.", "/") + "/initialize-" + dbName + ".js"), "/docker-entrypoint-initdb.d/init-mongo.js")
        .withExposedPorts(EXPORT_MONGODB_PORT).withNetwork(network).withNetworkAliases(dbName).waitingFor(Wait.forListeningPort());

  }

  /**
   * Return the URL to the API exposed to the specified port.
   *
   * @param port where the API will be exposed.
   *
   * @return the URL to the API exposed on the port.
   */
  static String exposedApiFor(final int port) {

    final StringBuilder builder = new StringBuilder();
    builder.append("http://host.testcontainers.internal:");
    builder.append(port);
    return builder.toString();

  }

  /**
   * Create and start the task manager container.
   *
   * @param port                         to bind the task manager API on the localhost.
   * @param profileManagerApi            URL where the profile manager API.
   * @param interactionProtocolEngineApi URL where the interaction protocol engine API.
   * @param serviceApi                   URL where the service API.
   * @param network                      that shared between containers.
   *
   * @return the URL where the task manager is listening.
   */
  @SuppressWarnings("resource")
  static String createAndStartContainersForTaskManager(final int port, final String profileManagerApi, final String interactionProtocolEngineApi, final String serviceApi, final Network network) {

    final GenericContainer<?> taskPersistenceContainer = createMongoContainerFor(WENET_TASK_MANAGER_DB_NAME, network);
    taskPersistenceContainer.start();
    final FixedHostPortGenericContainer<?> taskManagerContainer = new FixedHostPortGenericContainer<>(WENET_TASK_MANAGER_DOCKER_NAME).withStartupAttempts(1).withEnv("DB_HOST", WENET_TASK_MANAGER_DB_NAME)
        .withEnv("WENET_PROFILE_MANAGER_API", profileManagerApi).withEnv("WENET_INTERACTION_PROTOCOL_ENGINE_API", interactionProtocolEngineApi).withEnv("WENET_SERVICE_API", serviceApi).withNetwork(network)
        .withFixedExposedPort(port, EXPORT_API_PORT).waitingFor(Wait.forListeningPort());
    taskManagerContainer.start();
    return "http://" + taskManagerContainer.getContainerIpAddress() + ":" + port;

  }

  /**
   * Create and start the engine protocol engine container.
   *
   * @param port                    to bind the task manager API on the localhost.
   * @param profileManagerApi       URL where the profile manager API.
   * @param taskManagerApi          URL where the task manager API.
   * @param serviceApi              URL where the service API.
   * @param socialContextBuilderApi URL where the social context builder API.
   * @param incentiveServerApi      URL where the incentive server API.
   * @param network                 that shared between containers.
   *
   * @return the URL where the interaction protocol engine is listening.
   */
  @SuppressWarnings("resource")
  static String createAndStartContainersForInteractionProtocolEngine(final int port, final String profileManagerApi, final String taskManagerApi, final String serviceApi, final String socialContextBuilderApi,
      final String incentiveServerApi, final Network network) {

    final GenericContainer<?> interactionProtocolEnginePersistenceContainer = createMongoContainerFor(WENET_INTERACTION_PROTOCOL_ENGINE_DB_NAME, network);
    interactionProtocolEnginePersistenceContainer.start();
    final FixedHostPortGenericContainer<?> interactionProtocolEngineContainer = new FixedHostPortGenericContainer<>(WENET_INTERACTION_PROTOCOL_ENGINE_DOCKER_NAME).withStartupAttempts(1)
        .withEnv("DB_HOST", WENET_INTERACTION_PROTOCOL_ENGINE_DB_NAME).withEnv("WENET_PROFILE_MANAGER_API", profileManagerApi).withEnv("WENET_TASK_MANAGER_API", taskManagerApi).withEnv("WENET_SERVICE_API", serviceApi)
        .withEnv("WENET_SOCIAL_CONTEXT_BUILDER_API", socialContextBuilderApi).withEnv("WENET_INCENTIVE_SERVER_API", incentiveServerApi).withNetwork(network).withFixedExposedPort(port, EXPORT_API_PORT).waitingFor(Wait.forListeningPort());
    interactionProtocolEngineContainer.start();
    return "http://" + interactionProtocolEngineContainer.getContainerIpAddress() + ":" + port;
  }

  /**
   * Create and start the profile manager container.
   *
   * @param port                    to bind the profile manager API on the localhost.
   * @param taskManagerApi          URL where the task manager API.
   * @param serviceApi              URL where the service API.
   * @param socialContextBuilderApi URL where the social context builder API.
   * @param network                 that shared between containers.
   *
   * @return the URL where the profile manager is listening.
   */
  @SuppressWarnings("resource")
  static String createAndStartContainersForProfileManager(final int port, final String taskManagerApi, final String serviceApi, final String socialContextBuilderApi, final Network network) {

    final GenericContainer<?> profilePersistenceContainer = createMongoContainerFor(WENET_PROFILE_MANAGER_DB_NAME, network);
    profilePersistenceContainer.start();
    final FixedHostPortGenericContainer<?> profileManagerContainer = new FixedHostPortGenericContainer<>(WENET_PROFILE_MANAGER_DOCKER_NAME).withStartupAttempts(1).withEnv("DB_HOST", WENET_PROFILE_MANAGER_DB_NAME)
        .withEnv("WENET_TASK_MANAGER_API", taskManagerApi).withEnv("WENET_SERVICE_API", serviceApi).withEnv("WENET_SOCIAL_CONTEXT_BUILDER_API", socialContextBuilderApi).withNetwork(network).withFixedExposedPort(port, EXPORT_API_PORT)
        .waitingFor(Wait.forListeningPort());
    profileManagerContainer.start();
    return "http://" + profilePersistenceContainer.getContainerIpAddress() + ":" + port;

  }

  /**
   * Return the effective configuration over the started component.
   *
   * @param vertx                event bus to use to load the configurations.
   * @param configurationHandler the handler of the effective configuration
   */
  static void defaultEffectiveConfiguration(final Vertx vertx, final Handler<AsyncResult<JsonObject>> configurationHandler) {

    final ConfigStoreOptions effectiveConfigurationFile = new ConfigStoreOptions().setType("file").setFormat("json")
        .setConfig(new JsonObject().put("path", FileSystems.getDefault().getPath(AbstractMain.DEFAULT_EFFECTIVE_CONFIGURATION_PATH).toFile().getAbsolutePath()));

    final ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(effectiveConfigurationFile);
    ConfigRetriever.create(vertx, options).getConfig(configurationHandler);

  }

}
