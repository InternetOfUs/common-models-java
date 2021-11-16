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

import eu.internetofus.common.components.incentive_server.WeNetIncentiveServerClient;
import eu.internetofus.common.components.incentive_server.WeNetIncentiveServerSimulatorMocker;
import eu.internetofus.common.components.interaction_protocol_engine.WeNetInteractionProtocolEngineClient;
import eu.internetofus.common.components.personal_context_builder.WeNetPersonalContextBuilderClient;
import eu.internetofus.common.components.personal_context_builder.WeNetPersonalContextBuilderSimulatorMocker;
import eu.internetofus.common.components.profile_diversity_manager.WeNetProfileDiversityManagerClient;
import eu.internetofus.common.components.profile_manager.WeNetProfileManagerClient;
import eu.internetofus.common.components.service.WeNetServiceClient;
import eu.internetofus.common.components.service.WeNetServiceSimulatorMocker;
import eu.internetofus.common.components.social_context_builder.WeNetSocialContextBuilderClient;
import eu.internetofus.common.components.social_context_builder.WeNetSocialContextBuilderSimulatorMocker;
import eu.internetofus.common.components.task_manager.WeNetTaskManagerClient;
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
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
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
  public static final String WENET_PROFILE_MANAGER_DOCKER_NAME = "internetofus/profile-manager:0.23.0";

  /**
   * The name of the WeNet task manager docker container to use.
   */
  public static final String WENET_TASK_MANAGER_DOCKER_NAME = "internetofus/task-manager:0.16.0";

  /**
   * The name of the WeNet interaction manager docker container to use.
   */
  public static final String WENET_INTERACTION_PROTOCOL_ENGINE_DOCKER_NAME = "internetofus/interaction-protocol-engine:0.24.0";

  /**
   * The name of the WeNet profile manager extension wordnetsim docker container
   * to use.
   */
  public static final String WENET_PROFILE_DIVERSITY_MANAGER_DOCKER_NAME = "internetofus/profile-diversity-manager:0.2.0";

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
   * The port where the profile manager extension for the word net sim is exposed.
   */
  public int profileDiversityManagerApiPort;

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
   * The container with a profile manager extension word net similarity component.
   */
  public WeNetComponentContainer<?> profileDiversityManagerContainer;

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
      this.profileDiversityManagerApiPort = WeNetComponentContainers.nextFreePort();
      this.taskManagerApiPort = WeNetComponentContainers.nextFreePort();
      this.interactionProtocolEngineApiPort = WeNetComponentContainers.nextFreePort();
      Testcontainers.exposeHostPorts(this.profileManagerApiPort, this.profileDiversityManagerApiPort,
          this.taskManagerApiPort, this.interactionProtocolEngineApiPort);
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
   * {@inheritDoc}
   *
   * @see #profileManagerApiPort
   * @see #profileManagerContainer
   */
  @Override
  public String getProfileManagerApi() {

    return this.createApiFor(this.profileManagerContainer, this.profileManagerApiPort);

  }

  /**
   * Get the configuration to can create a client to the profile manager client.
   *
   * @return the configuration to connect with the profile manager.
   *
   * @see WeNetProfileManagerClient
   */
  public JsonObject getProfileManagerClientConf() {

    return new JsonObject().put(WeNetProfileManagerClient.PROFILE_MANAGER_CONF_KEY, this.getProfileManagerApi());

  }

  /**
   * {@inheritDoc}
   *
   * @see #profileDiversityManagerApiPort
   * @see #profileDiversityManagerContainer
   */
  @Override
  public String getProfileDiversityManagerApi() {

    return this.createApiFor(this.profileDiversityManagerContainer, this.profileDiversityManagerApiPort);

  }

  /**
   * Get the configuration to can create a client to the profile manager client.
   *
   * @return the configuration to connect with the profile manager.
   *
   * @see WeNetProfileManagerClient
   */
  public JsonObject getProfileDiversityManagerClientConf() {

    return new JsonObject().put(WeNetProfileDiversityManagerClient.PROFILE_DIVERSITY_MANAGER_CONF_KEY,
        this.getProfileDiversityManagerApi());

  }

  /**
   * {@inheritDoc}
   *
   * @see #taskManagerApiPort
   * @see #taskManagerContainer
   */
  @Override
  public String getTaskManagerApi() {

    return this.createApiFor(this.taskManagerContainer, this.taskManagerApiPort);

  }

  /**
   * Get the configuration to can create a client to the task manager client.
   *
   * @return the configuration to connect with the task manager.
   *
   * @see WeNetTaskManagerClient
   */
  public JsonObject getTaskManagerClientConf() {

    return new JsonObject().put(WeNetTaskManagerClient.TASK_MANAGER_CONF_KEY, this.getTaskManagerApi());

  }

  /**
   * {@inheritDoc}
   *
   * @see #interactionProtocolEngineApiPort
   * @see #interactionProtocolEngineContainer
   */
  @Override
  public String getInteractionProtocolEngineApi() {

    return this.createApiFor(this.interactionProtocolEngineContainer, this.interactionProtocolEngineApiPort);

  }

  /**
   * Get the configuration to can create a client to the interaction protocol
   * engine.
   *
   * @return the configuration to connect with the interaction protocol engine.
   *
   * @see WeNetTaskManagerClient
   */
  public JsonObject getInteractionProtocolEngineClientConf() {

    return new JsonObject().put(WeNetInteractionProtocolEngineClient.INTERACTION_PROTOCOL_ENGINE_CONF_KEY,
        this.getInteractionProtocolEngineApi());

  }

  /**
   * Start the profile manager container if it is not started yet.
   *
   * @return this containers instance.
   */
  @SuppressWarnings("resource")
  public Containers startProfileManagerContainer() {

    if (this.profileManagerContainer == null) {

      Logger.trace("Starting Profile Manager");
      this.profileManagerContainer = new WeNetComponentContainer<>(WENET_PROFILE_MANAGER_DOCKER_NAME,
          this.profileManagerApiPort).with(this)
              .withEnv("WENET_PROFILE_DIVERSITY_MANAGER_API", this.getProfileDiversityManagerApi())
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
   * Start the profile diversity manager container if it is not started yet.
   *
   * @return this containers instance.
   */
  @SuppressWarnings("resource")
  public Containers startProfileDiversityManagerContainer() {

    if (this.profileDiversityManagerContainer == null) {

      Logger.trace("Starting Profile Diversity Manager");
      this.profileDiversityManagerContainer = new WeNetComponentContainer<>(WENET_PROFILE_DIVERSITY_MANAGER_DOCKER_NAME,
          this.profileDiversityManagerApiPort, 80).withNetwork(this).waitingFor(Wait.forListeningPort());
      this.profileDiversityManagerContainer.start();
      Logger.trace("Started Profile Diversity Manager");
    }

    return this;
  }

  /**
   * Start the task manager container if it is not started yet.
   *
   * @return this containers instance.
   */
  @SuppressWarnings("resource")
  public Containers startTaskManagerContainer() {

    if (this.taskManagerContainer == null) {

      Logger.trace("Starting Task Manager");
      this.taskManagerContainer = new WeNetComponentContainer<>(WENET_TASK_MANAGER_DOCKER_NAME, this.taskManagerApiPort)
          .with(this).withEnv("WENET_PROFILE_MANAGER_API", this.getProfileManagerApi())
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
  @SuppressWarnings("resource")
  public Containers startInteractionProtocolEngineContainer() {

    if (this.interactionProtocolEngineContainer == null) {

      Logger.trace("Starting Interaction Protocol Engine");
      this.interactionProtocolEngineContainer = new WeNetComponentContainer<>(
          WENET_INTERACTION_PROTOCOL_ENGINE_DOCKER_NAME, this.interactionProtocolEngineApiPort).with(this)
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
   * Start the basic containers and wait until they are ready.
   *
   * @return this containers instance.
   *
   * @see #startBasic()
   * @see #startProfileManagerContainer()
   * @see #startProfileDiversityManagerContainer()
   * @see #startTaskManagerContainer()
   * @see #startInteractionProtocolEngineContainer()
   */
  public Containers startAll() {

    this.startBasic();
    this.startProfileDiversityManagerContainer();
    this.startProfileManagerContainer();
    this.startTaskManagerContainer();
    this.startInteractionProtocolEngineContainer();

    // Wait until they has been started
    final List<String> started = new ArrayList<>();
    started.add(this.getProfileManagerApi() + "/help/info");
    started.add(this.getTaskManagerApi() + "/help/info");
    started.add(this.getInteractionProtocolEngineApi() + "/help/info");
    do {

      final var iter = started.iterator();
      while (iter.hasNext()) {

        var active = false;
        try {

          final var url = iter.next();
          final var json = IOUtils.toString(new URL(url), Charset.defaultCharset());
          final var info = new JsonObject(json);
          if (info.getString("name", null) != null) {

            iter.remove();
            active = true;

          }

        } catch (final Throwable ignored) {

        }

        if (active) {

          try {
            Thread.sleep(1000);
          } catch (final InterruptedException ignored) {
          }
        }
      }

    } while (!started.isEmpty());

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
   * Get the configuration to can create a client to the service client.
   *
   * @return the configuration to connect with the service.
   *
   * @see WeNetServiceClient
   */
  public JsonObject getServiceClientConf() {

    return new JsonObject().put(WeNetServiceClient.SERVICE_CONF_KEY, this.getServiceApi());

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
   * Get the configuration to can create a client to the social context builder
   * client.
   *
   * @return the configuration to connect with the service.
   *
   * @see WeNetSocialContextBuilderClient
   */
  public JsonObject getSocialContextBuilderClientConf() {

    return new JsonObject().put(WeNetSocialContextBuilderClient.SOCIAL_CONTEXT_BUILDER_CONF_KEY,
        this.getSocialContextBuilderApi());

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
   * Get the configuration to can create a client to the incentive server client.
   *
   * @return the configuration to connect with the service.
   *
   * @see WeNetIncentiveServerClient
   */
  public JsonObject getIncentiveServerClientConf() {

    return new JsonObject().put(WeNetIncentiveServerClient.INCENTIVE_SERVER_CONF_KEY, this.getIncentiveServerApi());

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

  /**
   * Get the configuration to can create a client to the personal context builder
   * client.
   *
   * @return the configuration to connect with the service.
   *
   * @see WeNetPersonalContextBuilderClient
   */
  public JsonObject getPersonalContextBuilderClientConf() {

    return new JsonObject().put(WeNetPersonalContextBuilderClient.PERSONAL_CONTEXT_BUILDER_CONF_KEY,
        this.getPersonalContextBuilderApi());

  }

}
