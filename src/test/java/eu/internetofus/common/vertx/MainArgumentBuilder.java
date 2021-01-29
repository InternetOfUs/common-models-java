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

package eu.internetofus.common.vertx;

import eu.internetofus.common.Containers;
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
  public MainArgumentBuilder with(@NotNull final Containers containers) {

    this.with("persistence.db_name", Containers.MONGODB_NAME);
    this.with("persistence.host", containers.getMongoDBHost());
    this.with("persistence.port", containers.getMongoDBPort());
    this.with("persistence.username", Containers.MONGODB_USER);
    this.with("persistence.password", Containers.MONGODB_PASSWORD);
    this.withWeNetComponent("profileManager", containers.getProfileManagerApi());
    this.withWeNetComponent("taskManager", containers.getTaskManagerApi());
    this.withWeNetComponent("interactionProtocolEngine", containers.getInteractionProtocolEngineApi());
    this.withWeNetComponent("service", containers.service.getApiUrl());
    this.withWeNetComponent("socialContextBuilder", containers.socialContextBuilder.getApiUrl());
    this.withWeNetComponent("incentiveServer", containers.incentiveServer.getApiUrl());

    return this;
  }

}
