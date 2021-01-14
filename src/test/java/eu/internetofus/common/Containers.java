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

package eu.internetofus.common;

import eu.internetofus.common.components.incentive_server.WeNetIncentiveServerSimulatorMocker;
import eu.internetofus.common.components.service.WeNetServiceSimulatorMocker;
import eu.internetofus.common.components.social_context_builder.WeNetSocialContextBuilderSimulatorMocker;
import eu.internetofus.common.vertx.AbstractMain;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import java.net.ServerSocket;
import java.nio.file.FileSystems;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;
import org.tinylog.Logger;

/**
 * Maintains the information of the started containers.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class Containers {

  /**
   * The name of the mongo docker container to use.
   */
  public static final String MONGO_DOCKER_NAME = "mongo:4.4.1";

  /**
   * The port for the MongoDB that has to be exported.
   */
  public static final int EXPORT_MONGODB_PORT = 27017;

  /**
   * The name of the database started on the container.
   */
  public static final String MONGODB_NAME = "wenetDB";

  /**
   * The name of the user to access the MongoDB on the container.
   */
  public static final String MONGODB_USER = "wenet";

  /**
   * The password of the user to access the MongoDB on the container.
   */
  public static final String MONGODB_PASSWORD = "password";

  /**
   * The port that listen for the API requests on a container to be exported.
   */
  public static final int EXPORT_API_PORT = 8080;

  /**
   * The name of the WeNet profile manager docker container to use.
   */
  public static final String WENET_PROFILE_MANAGER_DOCKER_NAME = "internetofus/profile-manager:0.16.0";

  /**
   * The name of the WeNet task manager docker container to use.
   */
  public static final String WENET_TASK_MANAGER_DOCKER_NAME = "internetofus/task-manager:0.7.0";

  /**
   * The name of the WeNet interaction manager docker container to use.
   */
  public static final String WENET_INTERACTION_PROTOCOL_ENGINE_DOCKER_NAME = "internetofus/interaction-protocol-engine:0.13.0";

  /**
   * The current implementation of the containers.
   */
  private static final Containers INSTANCE = new Containers();

  /**
   * The network in witch all the containers are linked.
   */
  public Network network;

  /**
   * The container with a mongoDB.
   */
  public GenericContainer<?> mongoContainer;

  /**
   * The mocker of the service module.
   */
  public WeNetServiceSimulatorMocker service;

  /**
   * The mocker of the social context builder module.
   */
  public WeNetSocialContextBuilderSimulatorMocker socialContextBuilder;

  /**
   * The mocker of the incentive server module.
   */
  public WeNetIncentiveServerSimulatorMocker incentiveServer;

  /**
   * The port where the profile manager is exposed.
   */
  public int profileManagerApiPort;

  /**
   * The port where the task manager is exposed.
   */
  public int taskManagerApiPort;

  /**
   * The port where the interaction protocol engine is exposed.
   */
  public int interactionProtocolEngineApiPort;

  /**
   * The container with a profile manager component.
   */
  public WeNetComponentContainer<?> profileManagerContainer;

  /**
   * The container with a task manager component.
   */
  public WeNetComponentContainer<?> taskManagerContainer;

  /**
   * The container with a interaction protocol engine component.
   */
  public WeNetComponentContainer<?> interactionProtocolEngineContainer;

  /**
   * Create a new instance of the containers.
   */
  protected Containers() {

    this.network = Network.newNetwork();
  }

  /**
   * Return current containers.
   *
   * @return the current instance of the containers.
   */
  public static Containers status() {

    return INSTANCE;
  }

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

  /**
   * Start the MongoDB container if it is not started yet.
   *
   * @return this containers instance.
   */
  @SuppressWarnings("resource")
  public Containers startMongoContainer() {

    if (this.mongoContainer == null) {

      Logger.trace("Starting MongoDB container");
      this.mongoContainer = new GenericContainer<>(DockerImageName.parse(MONGO_DOCKER_NAME)).withStartupAttempts(1)
          .withEnv("MONGO_INITDB_ROOT_USERNAME", "root").withEnv("MONGO_INITDB_ROOT_PASSWORD", "password")
          .withEnv("MONGO_INITDB_DATABASE", MONGODB_NAME)
          .withCopyFileToContainer(
              MountableFile.forClasspathResource(
                  Containers.class.getPackageName().replaceAll("\\.", "/") + "/initialize-wenetDB.js"),
              "/docker-entrypoint-initdb.d/init-mongo.js")
          .withExposedPorts(EXPORT_MONGODB_PORT).withNetwork(this.network).withNetworkAliases(MONGODB_NAME)
          .waitingFor(Wait.forListeningPort());
      this.mongoContainer.start();
      Logger.trace("Started MongoDB container");
    }

    return this;
  }

  /**
   * Return the host where the MongoDB is listening.
   *
   * @return the host where is the MongoDB container.
   */
  public String getMongoDBHost() {

    return this.mongoContainer.getHost();
  }

  /**
   * Return the port where the MongoDB is listening.
   *
   * @return the port where is the MongoDB container.
   */
  public int getMongoDBPort() {

    return this.mongoContainer.getMappedPort(Containers.EXPORT_MONGODB_PORT);

  }

  /**
   * Start the service module if it is not started yet.
   *
   * @return this containers instance.
   */
  public Containers startService() {

    if (this.service == null) {

      Logger.trace("Starting Service Mocker");
      this.service = WeNetServiceSimulatorMocker.start();
      Logger.trace("Started Service Mocker");

    }
    return this;
  }

  /**
   * Start the social context builder module if it is not started yet.
   *
   * @return this containers instance.
   */
  public Containers startSocialContextBuilder() {

    if (this.socialContextBuilder == null) {

      Logger.trace("Starting Social Context Mocker");
      this.socialContextBuilder = WeNetSocialContextBuilderSimulatorMocker.start();
      Logger.trace("Started Social Context Mocker");

    }
    return this;
  }

  /**
   * Start the incentive server module if it is not started yet.
   *
   * @return this containers instance.
   */
  public Containers startIncentiveServer() {

    if (this.incentiveServer == null) {

      Logger.trace("Starting Incentive server Mocker");
      this.incentiveServer = WeNetIncentiveServerSimulatorMocker.start();
      Logger.trace("Started Incentive server Mocker");

    }
    return this;
  }

  /**
   * Expose the ports of the module that can be started.
   *
   * @return this containers instance.
   */
  public Containers exposeModulePortsContainers() {

    if (this.profileManagerApiPort == 0) {

      this.profileManagerApiPort = Containers.nextFreePort();
      this.taskManagerApiPort = Containers.nextFreePort();
      this.interactionProtocolEngineApiPort = Containers.nextFreePort();
      Testcontainers.exposeHostPorts(this.profileManagerApiPort, this.taskManagerApiPort,
          this.interactionProtocolEngineApiPort);
    }

    return this;

  }

  /**
   * Return the URL to the API exposed to the specified port.
   *
   * @param port where the API will be exposed.
   *
   * @return the URL to the API exposed on the port.
   */
  static String exposedApiFor(final int port) {

    final var builder = new StringBuilder();
    builder.append("http://host.testcontainers.internal:");
    builder.append(port);
    return builder.toString();

  }

  /**
   * Return the URL to access the module defined on a container.
   *
   * @param container where is started the module.
   * @param port      where the API is bind.
   *
   * @return the URL to the module API.
   */
  protected String createApiFor(final WeNetComponentContainer<?> container, final int port) {

    final StringBuilder builder = new StringBuilder();
    builder.append("http://");
    if (container != null) {

      builder.append(container.getHost());

    } else {

      builder.append("host.testcontainers.internal");
    }
    builder.append(":");
    builder.append(port);
    return builder.toString();
  }

  /**
   * Return the URL to the profile manager API.
   *
   * @return the profile manager API URL.
   */
  public String getProfileManagerApi() {

    return this.createApiFor(this.profileManagerContainer, this.profileManagerApiPort);

  }

  /**
   * Return the URL to the task manager API.
   *
   * @return the task manager API URL.
   */
  public String getTaskManagerApi() {

    return this.createApiFor(this.taskManagerContainer, this.taskManagerApiPort);

  }

  /**
   * Return the URL to the interaction protocol engine API.
   *
   * @return the interaction protocol engine API URL.
   */
  public String getInteractionProtocolEngineApi() {

    return this.createApiFor(this.interactionProtocolEngineContainer, this.interactionProtocolEngineApiPort);

  }

  /**
   * Start the profile manager container if it is not started yet.
   *
   * @return this containers instance.
   */
  public Containers startProfileManagerContainer() {

    if (this.profileManagerContainer == null) {

      Logger.trace("Starting Profile Manager");
      this.profileManagerContainer = this
          .createContainerFor(WENET_PROFILE_MANAGER_DOCKER_NAME, this.profileManagerApiPort)
          .withEnv("WENET_TASK_MANAGER_API", this.getTaskManagerApi())
          .withEnv("WENET_SERVICE_API", this.service.getApiUrl())
          .withEnv("WENET_SOCIAL_CONTEXT_BUILDER_API", this.socialContextBuilder.getApiUrl())
          .waitingFor(Wait.forListeningPort());
      this.profileManagerContainer.start();
      Logger.trace("Started Profile Manager");
    }

    return this;
  }

  /**
   * Create the container for the specified component with the specified port.
   *
   * @param name of the container.
   * @param port to export the API.
   *
   * @return the container for the component.
   */
  @SuppressWarnings("resource")
  private WeNetComponentContainer<?> createContainerFor(final String name, final int port) {

    return new WeNetComponentContainer<>(name, port).with(this);
  }

  /**
   * Start the task manager container if it is not started yet.
   *
   * @return this containers instance.
   */
  public Containers startTaskManagerContainer() {

    if (this.taskManagerContainer == null) {

      Logger.trace("Starting Task Manager");
      this.taskManagerContainer = this.createContainerFor(WENET_TASK_MANAGER_DOCKER_NAME, this.taskManagerApiPort)
          .withEnv("WENET_PROFILE_MANAGER_API", this.getProfileManagerApi())
          .withEnv("WENET_INTERACTION_PROTOCOL_ENGINE_API", this.getInteractionProtocolEngineApi())
          .withEnv("WENET_SERVICE_API", this.service.getApiUrl()).waitingFor(Wait.forListeningPort());
      this.taskManagerContainer.start();
      Logger.trace("Started Task Manager");
    }

    return this;
  }

  /**
   * Start the interaction protocol engine container if it is not started yet.
   *
   * @return this containers instance.
   */
  public Containers startInteractionProtocolEngineContainer() {

    if (this.interactionProtocolEngineContainer == null) {

      Logger.trace("Starting Interaction Protocol Engine");
      this.interactionProtocolEngineContainer = this
          .createContainerFor(WENET_INTERACTION_PROTOCOL_ENGINE_DOCKER_NAME, this.interactionProtocolEngineApiPort)
          .withEnv("WENET_PROFILE_MANAGER_API", this.getProfileManagerApi())
          .withEnv("WENET_TASK_MANAGER_API", this.getTaskManagerApi())
          .withEnv("WENET_SERVICE_API", this.service.getApiUrl())
          .withEnv("WENET_SOCIAL_CONTEXT_BUILDER_API", this.socialContextBuilder.getApiUrl())
          .withEnv("WENET_INCENTIVE_SERVER_API", this.incentiveServer.getApiUrl()).waitingFor(Wait.forListeningPort());
      this.interactionProtocolEngineContainer.start();
      Logger.trace("Started Interaction Protocol Engine");
    }

    return this;
  }

  /**
   * Return the effective configuration over the started component.
   *
   * @param vertx                event bus to use to load the configurations.
   * @param configurationHandler the handler of the effective configuration
   */
  static void defaultEffectiveConfiguration(final Vertx vertx,
      final Handler<AsyncResult<JsonObject>> configurationHandler) {

    final var effectiveConfigurationFile = new ConfigStoreOptions().setType("file").setFormat("json")
        .setConfig(new JsonObject().put("path", FileSystems.getDefault()
            .getPath(AbstractMain.DEFAULT_EFFECTIVE_CONFIGURATION_PATH).toFile().getAbsolutePath()));

    final var options = new ConfigRetrieverOptions().addStore(effectiveConfigurationFile);
    ConfigRetriever.create(vertx, options).getConfig(configurationHandler);

  }

  /**
   * Start the basic containers.
   *
   * @return this containers instance.
   *
   * @see #startService()
   * @see #startSocialContextBuilder()
   * @see #startIncentiveServer()
   * @see #startMongoContainer()
   * @see #exposeModulePortsContainers()
   */
  public Containers startBasic() {

    this.startService();
    this.startSocialContextBuilder();
    this.startIncentiveServer();
    this.startMongoContainer();
    return this.exposeModulePortsContainers();
  }

  /**
   * Return the configuration to interact with the database.
   *
   * @return the configuration to interact with the MongoDB.
   */
  public JsonObject getMongoDBConfig() {

    return new JsonObject().put("db_name", Containers.MONGODB_NAME).put("host", this.getMongoDBHost())
        .put("port", this.getMongoDBPort()).put("username", Containers.MONGODB_USER)
        .put("password", Containers.MONGODB_PASSWORD);
  }

}
