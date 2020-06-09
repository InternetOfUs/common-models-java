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
  String WENET_PROFILE_MANAGER_DOCKER_NAME = "wenet/profile-manager:0.13.0";

  /**
   * The name of the WeNet profile manager database.
   */
  String WENET_PROFILE_MANAGER_DB_NAME = "wenetProfileManagerDB";

  /**
   * The name of the WeNet task manager docker container to use.
   */
  String WENET_TASK_MANAGER_DOCKER_NAME = "wenet/task-manager:0.4.0";

  /**
   * The name of the WeNet task manager database.
   */
  String WENET_TASK_MANAGER_DB_NAME = "wenetTaskManagerDB";

  /**
   * The name of the WeNet interaction manager docker container to use.
   */
  String WENET_INTERACTION_PROTOCOL_ENGINE_DOCKER_NAME = "wenet/interaction-protocol-engine:0.9.0";

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
   * Create and start the task manager container.
   *
   * @param port                             to bind the task manager API on the localhost.
   * @param profileManagerApiPort            port where the profile manager will be bind on the localhost.
   * @param interactionProtocolEngineApiPort port where the interaction protocol engine will be bind on the localhost.
   * @param serviceApiPort                   port where the service component will be bind on the localhost.
   * @param network                          that shared between containers.
   */
  @SuppressWarnings("resource")
  static void createAndStartContainersForTaskManager(final int port, final int profileManagerApiPort, final int interactionProtocolEngineApiPort, final int serviceApiPort, final Network network) {

    final GenericContainer<?> taskPersistenceContainer = createMongoContainerFor(WENET_TASK_MANAGER_DB_NAME, network);
    taskPersistenceContainer.start();
    final FixedHostPortGenericContainer<?> taskManagerContainer = new FixedHostPortGenericContainer<>(WENET_TASK_MANAGER_DOCKER_NAME).withStartupAttempts(1).withEnv("DB_HOST", WENET_TASK_MANAGER_DB_NAME)
        .withEnv("WENET_PROFILE_MANAGER_API", "http://host.testcontainers.internal:" + profileManagerApiPort).withEnv("WENET_INTERACTION_PROTOCOL_ENGINE_API", "http://host.testcontainers.internal:" + interactionProtocolEngineApiPort)
        .withEnv("WENET_SERVICE_API", "http://host.testcontainers.internal:" + serviceApiPort).withNetwork(network).withFixedExposedPort(port, EXPORT_API_PORT).waitingFor(Wait.forListeningPort());
    taskManagerContainer.start();

  }

  /**
   * Create and start the engine protocol engine container.
   *
   * @param port                     to bind the task manager API on the localhost.
   * @param profileManagerApiPort    port where the profile manager will be bind on the localhost.
   * @param taskManagerApiPort       port where the task manager will be bind on the localhost.
   * @param serviceApiPort           port where the service component will be bind on the localhost.
   * @param socialContextBuilderPort port where the social context builder component will be bind on the localhost.
   * @param incentiveServerPort      port where the incentive server component will be bind on the localhost.
   * @param network                  that shared between containers.
   */
  @SuppressWarnings("resource")
  static void createAndStartContainersForInteractionProtocolEngine(final int port, final int profileManagerApiPort, final int taskManagerApiPort, final int serviceApiPort, final int socialContextBuilderPort, final int incentiveServerPort,
      final Network network) {

    final GenericContainer<?> interactionProtocolEnginePersistenceContainer = createMongoContainerFor(WENET_INTERACTION_PROTOCOL_ENGINE_DB_NAME, network);
    interactionProtocolEnginePersistenceContainer.start();
    final FixedHostPortGenericContainer<?> interactionProtocolEngineContainer = new FixedHostPortGenericContainer<>(WENET_INTERACTION_PROTOCOL_ENGINE_DOCKER_NAME).withStartupAttempts(1)
        .withEnv("DB_HOST", WENET_INTERACTION_PROTOCOL_ENGINE_DB_NAME).withEnv("WENET_PROFILE_MANAGER_API", "http://host.testcontainers.internal:" + profileManagerApiPort)
        .withEnv("WENET_TASK_MANAGER_API", "http://host.testcontainers.internal:" + taskManagerApiPort).withEnv("WENET_SERVICE_API", "http://host.testcontainers.internal:" + serviceApiPort)
        .withEnv("WENET_SOCIAL_CONTEXT_BUILDER_API", "http://host.testcontainers.internal:" + socialContextBuilderPort).withEnv("WENET_INCENTIVE_SERVER_API", "http://host.testcontainers.internal:" + incentiveServerPort).withNetwork(network)
        .withFixedExposedPort(port, EXPORT_API_PORT).waitingFor(Wait.forListeningPort());
    interactionProtocolEngineContainer.start();

  }

  /**
   * Create and start the profile manager container.
   *
   * @param port                     to bind the profile manager API on the localhost.
   * @param taskManagerApiPort       port where the task manager will be bind on the localhost.
   * @param serviceApiPort           port where the service component will be bind on the localhost.
   * @param socialContextBuilderPort port where the social context builder component will be bind on the localhost.
   * @param network                  that shared between containers.
   */
  @SuppressWarnings("resource")
  static void createAndStartContainersForProfileManager(final int port, final int taskManagerApiPort, final int serviceApiPort, final int socialContextBuilderPort, final Network network) {

    final GenericContainer<?> profilePersistenceContainer = createMongoContainerFor(WENET_PROFILE_MANAGER_DB_NAME, network);
    profilePersistenceContainer.start();
    final FixedHostPortGenericContainer<?> profileManagerContainer = new FixedHostPortGenericContainer<>(WENET_PROFILE_MANAGER_DOCKER_NAME).withStartupAttempts(1).withEnv("DB_HOST", WENET_PROFILE_MANAGER_DB_NAME)
        .withEnv("WENET_TASK_MANAGER_API", "http://host.testcontainers.internal:" + taskManagerApiPort).withEnv("WENET_SERVICE_API", "http://host.testcontainers.internal:" + serviceApiPort)
        .withEnv("WENET_SOCIAL_CONTEXT_BUILDER_API", "http://host.testcontainers.internal:" + socialContextBuilderPort).withNetwork(network).withFixedExposedPort(port, EXPORT_API_PORT).waitingFor(Wait.forListeningPort());
    profileManagerContainer.start();

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
