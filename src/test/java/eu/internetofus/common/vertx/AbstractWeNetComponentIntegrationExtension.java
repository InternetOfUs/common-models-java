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

package eu.internetofus.common.vertx;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.Semaphore;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.tinylog.Level;
import org.tinylog.provider.InternalLogger;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;

/**
 * Common extension to can run the integration test over a WeNet module component.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractWeNetComponentIntegrationExtension implements ParameterResolver, BeforeTestExecutionCallback, AfterTestExecutionCallback, BeforeEachCallback, AfterEachCallback, BeforeAllCallback, AfterAllCallback {

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

      try {

        final var semaphore = new Semaphore(0);
        final var main = this.createMain();
        final var startArguments = this.createMainStartArguments();
        main.startWith(startArguments).onComplete(start -> {

          if (start.failed()) {

            InternalLogger.log(Level.ERROR, start.cause(), "Cannot start the WeNet component");

          } else {

            context = start.result();
          }
          semaphore.release();
        });

        semaphore.acquire();
        this.afterStarted(context);

      } catch (final Throwable throwable) {

        InternalLogger.log(Level.ERROR, throwable, "Cannot create the Main class to start the WeNet component to run the integration tests");
      }

    }

    return context;

  }

  /**
   * Empty method called after the containers has been started.
   *
   * @param context that has been started.
   */
  protected void afterStarted(final WeNetModuleContext context) {

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
   * Create the context to start the WeNet module and return the arguments that are necessary to start it.
   *
   * @return the arguments necessaries to start the module.
   *
   * @see AbstractMain#startWith(String[])
   */
  protected abstract AbstractMain createMain();

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

      fail("The WeNet profile manager is not started.");
    }
    this.vertxExtension.beforeAll(context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void afterEach(final ExtensionContext context) throws Exception {

    this.vertxExtension.afterEach(context);
    this.closeClientsIn(context);
  }

  /**
   * Close all the clients that has been created on a context.
   *
   * @param context where the clients has stored.
   */
  protected void closeClientsIn(final ExtensionContext context) {

    // close client and pool after the test context has been completed.
    final var client = context.getStore(ExtensionContext.Namespace.create(this.getClass().getName())).remove(WebClient.class.getName(), WebClient.class);
    if (client != null) {

      client.close();
    }

    final var pool = context.getStore(ExtensionContext.Namespace.create(this.getClass().getName())).remove(MongoClient.class.getName(), MongoClient.class);
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

    this.vertxExtension.afterTestExecution(context);
    this.closeClientsIn(context);

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
  public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) throws ParameterResolutionException {

    final Class<?> type = parameterContext.getParameter().getType();
    return type == WebClient.class || type == WeNetModuleContext.class || type == MongoClient.class || this.vertxExtension.supportsParameter(parameterContext, extensionContext);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) throws ParameterResolutionException {

    final Class<?> type = parameterContext.getParameter().getType();
    if (type == Vertx.class) {

      return this.getContext().vertx;

    } else if (type == WebClient.class) {

      return extensionContext.getStore(ExtensionContext.Namespace.create(this.getClass().getName())).getOrComputeIfAbsent(WebClient.class.getName(), key -> {

        final var context = this.getContext();
        final var options = new WebClientOptions();
        options.setDefaultHost(context.configuration.getJsonObject("api").getString("host"));
        options.setDefaultPort(context.configuration.getJsonObject("api").getInteger("port"));
        return WebClient.create(context.vertx, options);
      }, WebClient.class);

    } else if (type == MongoClient.class) {

      final var pool = extensionContext.getStore(ExtensionContext.Namespace.create(this.getClass().getName())).getOrComputeIfAbsent(MongoClient.class.getName(), key -> {

        final WeNetModuleContext context = this.getContext();
        final JsonObject persitenceConf = context.configuration.getJsonObject("persistence", new JsonObject());
        return MongoClient.create(context.vertx, persitenceConf);
      }, MongoClient.class);
      return pool;

    } else if (type == WeNetModuleContext.class) {

      return this.getContext();

    } else {

      return this.vertxExtension.resolveParameter(parameterContext, extensionContext);
    }
  }

}
