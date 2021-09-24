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

import static org.junit.jupiter.api.Assertions.fail;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.List;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * Common extension to can run the integration test over a WeNet module
 * component.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractWeNetIntegrationExtension implements ParameterResolver, BeforeTestExecutionCallback,
    AfterTestExecutionCallback, BeforeEachCallback, AfterEachCallback, BeforeAllCallback, AfterAllCallback {

  /**
   * The started WeNet interaction protocol engine for do the integration tests.
   */
  private volatile static WeNetModuleContext context;

  /**
   * Return the defined vertx.
   *
   * @return the started vertx.
   */
  public synchronized WeNetModuleContext getContext() {

    if (context == null) {

      context = this.createContext();

    }

    return context;

  }

  /**
   * Create the context to use on the extension.
   *
   * @return the created extension.
   */
  protected abstract WeNetModuleContext createContext();

  /**
   * The extension that manage the vertx components.
   */
  protected VertxExtension vertxExtension = new VertxExtension();

  /**
   * {@inheritDoc}
   */
  @Override
  public void afterAll(final ExtensionContext context) throws Exception {

    this.vertxExtension.afterAll(context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void beforeAll(final ExtensionContext context) throws Exception {

    if (this.getContext() == null) {

      fail("The WeNet context is not started.");
    }
    this.vertxExtension.beforeAll(context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void afterEach(final ExtensionContext context) throws Exception {

    @SuppressWarnings("unchecked")
    final var contexts = (List<VertxTestContext>) context.getStore(Namespace.GLOBAL).get("VertxTestContext");
    try {

      this.vertxExtension.afterEach(context);

    } finally {

      this.completeAll(contexts);
      this.closeClientsIn(context);

    }
  }

  /**
   * Mark all the the context as completed.
   *
   * @param contexts to mark as completed.
   */
  protected void completeAll(final List<VertxTestContext> contexts) {

    if (contexts != null) {

      for (final VertxTestContext context : contexts) {

        if (!context.completed()) {

          context.completeNow();
        }
      }
    }

  }

  /**
   * Close all the clients that has been created on a context.
   *
   * @param context where the clients has stored.
   */
  protected void closeClientsIn(final ExtensionContext context) {

    // close client and pool after the test context has been completed.
    final var client = context.getStore(ExtensionContext.Namespace.create(this.getClass().getName()))
        .remove(WebClient.class.getName(), WebClient.class);
    if (client != null) {

      client.close();
    }

    final var pool = context.getStore(ExtensionContext.Namespace.create(this.getClass().getName()))
        .remove(MongoClient.class.getName(), MongoClient.class);
    if (pool != null) {

      pool.close();
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void beforeEach(final ExtensionContext context) throws Exception {

    this.vertxExtension.beforeEach(context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void afterTestExecution(final ExtensionContext context) throws Exception {

    @SuppressWarnings("unchecked")
    final var contexts = (List<VertxTestContext>) context.getStore(Namespace.GLOBAL).get("VertxTestContext");
    try {

      this.vertxExtension.afterTestExecution(context);

    } finally {

      this.completeAll(contexts);
      this.closeClientsIn(context);

    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void beforeTestExecution(final ExtensionContext context) throws Exception {

    this.vertxExtension.beforeTestExecution(context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext)
      throws ParameterResolutionException {

    final Class<?> type = parameterContext.getParameter().getType();
    return type == WebClient.class || type == WeNetModuleContext.class || type == MongoClient.class
        || this.vertxExtension.supportsParameter(parameterContext, extensionContext);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext)
      throws ParameterResolutionException {

    final Class<?> type = parameterContext.getParameter().getType();
    if (type == WebClient.class) {

      return extensionContext.getStore(ExtensionContext.Namespace.create(this.getClass().getName()))
          .getOrComputeIfAbsent(WebClient.class.getName(), key -> {

            final var context = this.getContext();

            final var config = context.configuration.copy();
            var webclientConf = config.getJsonObject("webClient", null);
            if (webclientConf == null) {

              webclientConf = new JsonObject();
              config.put("webClient", webclientConf);
            }
            webclientConf.put("defaultHost", context.configuration.getJsonObject("api").getString("host"));
            webclientConf.put("defaultPort", context.configuration.getJsonObject("api").getInteger("port"));
            return AbstractServicesVerticle.createWebClientSession(context.vertx, config);

          }, WebClient.class);

    } else if (type == MongoClient.class) {

      final var pool = extensionContext.getStore(ExtensionContext.Namespace.create(this.getClass().getName()))
          .getOrComputeIfAbsent(MongoClient.class.getName(), key -> {

            final var context = this.getContext();
            final var persitenceConf = context.configuration.getJsonObject("persistence", new JsonObject());
            return MongoClient.create(context.vertx, persitenceConf);
          }, MongoClient.class);
      return pool;

    } else if (type == WeNetModuleContext.class) {

      return this.getContext();

    } else if (type == Vertx.class) {

      return this.getContext().vertx;

    } else {

      return this.vertxExtension.resolveParameter(parameterContext, extensionContext);
    }
  }
}
