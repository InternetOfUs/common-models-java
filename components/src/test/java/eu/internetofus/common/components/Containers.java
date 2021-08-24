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

package eu.internetofus.common.components;

import eu.internetofus.common.components.incentive_server.WeNetIncentiveServerSimulatorMocker;
import eu.internetofus.common.components.personal_context_builder.WeNetPersonalContextBuilderSimulatorMocker;
import eu.internetofus.common.components.service.WeNetServiceSimulatorMocker;
import eu.internetofus.common.components.social_context_builder.WeNetSocialContextBuilderSimulatorMocker;
import eu.internetofus.common.test.MongoContainer;
import eu.internetofus.common.test.WeNetComponentContainers;
import eu.internetofus.common.vertx.AbstractMain;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import java.nio.file.FileSystems;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.wait.strategy.Wait;
import org.tinylog.Logger;

/**
 * Maintains the information of the started containers.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class Containers extends MongoContainer<Containers> implements WeNetComponentContainers {

  /**
   * The port that listen for the API requests on a container to be exported.
   */
  public static final int EXPORT_API_PORT = 8080;

  /**
   * The name of the WeNet profile manager docker container to use.
   */
  public static final String WENET_PROFILE_MANAGER_DOCKER_NAME = "internetofus/profile-manager:0.20.0";

  /**
   * The name of the WeNet task manager docker container to use.
   */
  public static final String WENET_TASK_MANAGER_DOCKER_NAME = "internetofus/task-manager:0.13.0";

  /**
   * The name of the WeNet interaction manager docker container to use.
   */
  public static final String WENET_INTERACTION_PROTOCOL_ENGINE_DOCKER_NAME = "internetofus/interaction-protocol-engine:0.22.0";

  /**
   * The current implementation of the containers.
   */
  private static final Containers INSTANCE = new Containers();

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
   * The mocker of the personal context builder module.
   */
  public WeNetPersonalContextBuilderSimulatorMocker personalContextBuilder;

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
   * Return current containers.
   *
   * @return the current instance of the containers.
   */
  public static Containers status() {

    return INSTANCE;
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
   * Start the personal context builder module if it is not started yet.
   *
   * @return this containers instance.
   */
  public Containers startPersonalContextBuilder() {

    if (this.personalContextBuilder == null) {

      Logger.trace("Starting Personal Context Mocker");
      this.personalContextBuilder = WeNetPersonalContextBuilderSimulatorMocker.start();
      Logger.trace("Started Personal Context Mocker");

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

      this.profileManagerApiPort = WeNetComponentContainers.nextFreePort();
      this.taskManagerApiPort = WeNetComponentContainers.nextFreePort();
      this.interactionProtocolEngineApiPort = WeNetComponentContainers.nextFreePort();
      Testcontainers.exposeHostPorts(this.profileManagerApiPort, this.taskManagerApiPort,
          this.interactionProtocolEngineApiPort);
    }

    return this;

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

    final var builder = new StringBuilder();
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
  @Override
  public String getProfileManagerApi() {

    return this.createApiFor(this.profileManagerContainer, this.profileManagerApiPort);

  }

  /**
   * Return the URL to the task manager API.
   *
   * @return the task manager API URL.
   */
  @Override
  public String getTaskManagerApi() {

    return this.createApiFor(this.taskManagerContainer, this.taskManagerApiPort);

  }

  /**
   * Return the URL to the interaction protocol engine API.
   *
   * @return the interaction protocol engine API URL.
   */
  @Override
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
          .withEnv("COMP_AUTH_KEY", DEFAULT_WENET_COMPONENT_APIKEY).waitingFor(Wait.forListeningPort());
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
          .withEnv("WENET_SERVICE_API", this.service.getApiUrl())
          .withEnv("COMP_AUTH_KEY", DEFAULT_WENET_COMPONENT_APIKEY).waitingFor(Wait.forListeningPort());
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
          .withEnv("WENET_INCENTIVE_SERVER_API", this.incentiveServer.getApiUrl())
          .withEnv("WENET_PERSONAL_CONTEXT_BUILDER_API", this.personalContextBuilder.getApiUrl())
          .withEnv("COMP_AUTH_KEY", DEFAULT_WENET_COMPONENT_APIKEY).waitingFor(Wait.forListeningPort());
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
  public static void defaultEffectiveConfiguration(final Vertx vertx,
      final Handler<AsyncResult<JsonObject>> configurationHandler) {

    final var effectiveConfigurationFile = new ConfigStoreOptions().setType("file").setFormat("json")
        .setConfig(new JsonObject().put("path", FileSystems.getDefault()
            .getPath(AbstractMain.DEFAULT_EFFECTIVE_CONFIGURATION_PATH).toFile().getAbsolutePath()));

    final var options = new ConfigRetrieverOptions().addStore(effectiveConfigurationFile);
    ConfigRetriever.create(vertx, options).getConfig(configurationHandler);

  }

  /**
   * Return the effective configuration over the started component.
   *
   * @param vertx event bus to use to load the configurations.
   *
   * @return the future effective configuration.
   */
  public static Future<JsonObject> defaultEffectiveConfiguration(final Vertx vertx) {

    final Promise<JsonObject> promise = Promise.promise();
    defaultEffectiveConfiguration(vertx, result -> promise.handle(result));
    return promise.future();

  }

  /**
   * Start the basic containers.
   *
   * @return this containers instance.
   *
   * @see #startService()
   * @see #startSocialContextBuilder()
   * @see #startIncentiveServer()
   * @see #startPersonalContextBuilder()
   * @see #startMongoContainer()
   * @see #exposeModulePortsContainers()
   */
  public Containers startBasic() {

    this.startService();
    this.startSocialContextBuilder();
    this.startIncentiveServer();
    this.startPersonalContextBuilder();
    this.startMongoContainer();
    return this.exposeModulePortsContainers();
  }

  /**
   * Start the basic containers.
   *
   * @return this containers instance.
   *
   * @see #startBasic()
   * @see #startProfileManagerContainer()
   * @see #startTaskManagerContainer()
   * @see #startInteractionProtocolEngineContainer()
   */
  public Containers startAll() {

    this.startBasic();
    this.startProfileManagerContainer();
    this.startTaskManagerContainer();
    this.startInteractionProtocolEngineContainer();
    return this;
  }

  /**
   * Create the main arguments to start a component.
   */
  protected class MainStartArgumentBuilder {

  }

  /**
   * {@inheritDoc}
   *
   * @see #service
   */
  @Override
  public String getServiceApi() {

    return this.service.getApiUrl();
  }

  /**
   * {@inheritDoc}
   *
   * @see #socialContextBuilder
   */
  @Override
  public String getSocialContextBuilderApi() {

    return this.socialContextBuilder.getApiUrl();
  }

  /**
   * {@inheritDoc}
   *
   * @see #incentiveServer
   */
  @Override
  public String getIncentiveServerApi() {

    return this.incentiveServer.getApiUrl();
  }

  /**
   * {@inheritDoc}
   *
   * @see #personalContextBuilder
   */
  @Override
  public String getPersonalContextBuilderApi() {

    return this.personalContextBuilder.getApiUrl();
  }

}
