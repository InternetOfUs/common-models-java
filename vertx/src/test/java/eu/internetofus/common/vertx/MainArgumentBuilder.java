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

package eu.internetofus.common.vertx;

import eu.internetofus.common.test.MongoContainer;
import eu.internetofus.common.test.WeNetComponentContainers;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;

/**
 * Used to create the Main arguments for a component.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class MainArgumentBuilder {

  /**
   * The arguments for the main.
   */
  protected List<String> args;

  /**
   * Create the builder
   */
  public MainArgumentBuilder() {

    this.args = new ArrayList<>();

  }

  /**
   * Add a parameter into the main.
   *
   * @param key   name of the parameter.
   * @param value of the parameter.
   *
   * @return the argument builder.
   */
  public MainArgumentBuilder with(@NotNull final String key, @NotNull final Object value) {

    final var iter = this.args.iterator();
    while (iter.hasNext()) {

      if (iter.next().startsWith("-p" + key)) {

        iter.remove();
        break;
      }
    }
    final var arg = new StringBuilder();
    arg.append("-p");
    arg.append(key);
    arg.append("=");
    if (value instanceof String) {

      arg.append('"');
      arg.append(value);
      arg.append('"');

    } else {

      arg.append(value);
    }

    this.args.add(arg.toString());
    return this;

  }

  /**
   * Build the main argument.
   *
   * @return the argument to use for launch the component.
   */
  public String[] build() {

    return this.args.toArray(new String[this.args.size()]);
  }

  /**
   * Specify the api port.
   *
   * @param port for the API.
   *
   * @return the argument builder.
   */
  public MainArgumentBuilder withApiPort(final int port) {

    return this.with("api.port", port);
  }

  /**
   * Specify the key of the API.
   *
   * @param key of the API.
   *
   * @return the argument builder.
   */
  public MainArgumentBuilder withApikey(final String key) {

    return this.with("webClient.wenetComponentApikey", key);
  }

  /**
   * Specify the api URL for a wenet component.
   *
   * @param name   of the component.
   * @param apiUrl URL of the component.
   *
   * @return the argument builder.
   */
  public MainArgumentBuilder withWeNetComponent(@NotNull final String name, @NotNull final String apiUrl) {

    return this.with("wenetComponents." + name, apiUrl);
  }

  /**
   * Add all the configuration to interact with all the components defined on the
   * container.
   *
   * @param containers where are the components to use.
   *
   * @return the argument builder.
   */
  public MainArgumentBuilder with(@NotNull final WeNetComponentContainers containers) {

    this.with("persistence.db_name", MongoContainer.MONGODB_NAME);
    this.with("persistence.host", containers.getMongoDBHost());
    this.with("persistence.port", containers.getMongoDBPort());
    this.with("persistence.username", MongoContainer.MONGODB_USER);
    this.with("persistence.password", MongoContainer.MONGODB_PASSWORD);
    this.withWeNetComponent("profileManager", containers.getProfileManagerApi());
    this.withWeNetComponent("taskManager", containers.getTaskManagerApi());
    this.withWeNetComponent("interactionProtocolEngine", containers.getInteractionProtocolEngineApi());
    this.withWeNetComponent("service", containers.getServiceApi());
    this.withWeNetComponent("socialContextBuilder", containers.getSocialContextBuilderApi());
    this.withWeNetComponent("incentiveServer", containers.getIncentiveServerApi());
    this.withWeNetComponent("personalContextBuilder", containers.getPersonalContextBuilderApi());
    this.withApikey(WeNetComponentContainers.DEFAULT_WENET_COMPONENT_APIKEY);

    return this;
  }

}
