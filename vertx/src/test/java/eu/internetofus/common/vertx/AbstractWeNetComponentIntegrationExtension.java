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

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientSession;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import org.tinylog.Level;
import org.tinylog.provider.InternalLogger;

/**
 * Common extension to can run the integration test over a WeNet module
 * component.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractWeNetComponentIntegrationExtension extends AbstractWeNetIntegrationExtension {

  /**
   * {@inheritDoc}
   */
  @Override
  protected synchronized WeNetModuleContext createContext() {

    final List<WeNetModuleContext> created = new ArrayList<>();
    WeNetModuleContext createdContext = null;
    try {

      final var semaphore = new Semaphore(0);
      final var main = this.createMain();
      final var startArguments = this.createMainStartArguments();
      main.startWith(startArguments).onComplete(start -> {

        if (start.failed()) {

          InternalLogger.log(Level.ERROR, start.cause(), "Cannot start the WeNet component");

        } else {

          created.add(start.result());
        }
        semaphore.release();
      });

      semaphore.acquire();
      if (!created.isEmpty()) {

        createdContext = created.get(0);
        this.afterStarted(createdContext);
      }

    } catch (final Throwable throwable) {

      InternalLogger.log(Level.ERROR, throwable,
          "Cannot create the Main class to start the WeNet component to run the integration tests");
    }

    return createdContext;

  }

  /**
   * Create the main class that initialize the WeNet module.
   *
   * @return the argument to start the WeNet module.
   *
   * @see AbstractMain#startWith(String[])
   */
  protected abstract String[] createMainStartArguments();

  /**
   * Create the context to start the WeNet module and return the arguments that
   * are necessary to start it.
   *
   * @return the arguments necessaries to start the module.
   *
   * @see AbstractMain#startWith(String[])
   */
  protected abstract AbstractMain createMain();

  /**
   * Start the simulator services.
   *
   * @param context that has been started.
   */
  protected void afterStarted(final WeNetModuleContext context) {

    final var vertx = context.vertx;
    final var client = AbstractServicesVerticle.createWebClientSession(vertx, context.configuration);
    final var conf = context.configuration.getJsonObject("wenetComponents", new JsonObject());
    this.afterStarted(vertx, client, conf);

  }

  /**
   * Start the simulator services.
   *
   * @param vertx  event bus to use.
   * @param client to interact with the API.
   * @param conf   configuration of the WeNet components.
   */
  protected abstract void afterStarted(final Vertx vertx, WebClientSession client, JsonObject conf);

}
