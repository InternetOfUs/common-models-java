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

import eu.internetofus.common.Containers;
import eu.internetofus.common.model.ErrorMessage;
import eu.internetofus.common.vertx.AbstractServicesVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;
import java.net.InetAddress;
import java.util.concurrent.Semaphore;
import javax.ws.rs.core.Response.Status;
import org.tinylog.Level;
import org.tinylog.provider.InternalLogger;

/**
 * The generic component used to mock any WeNet component.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractComponentMocker {

  /**
   * The started server.
   */
  protected HttpServer server;

  /**
   * Start the component mocker and wait until it is started.
   *
   * @param port to bind the mocker server or {@code 0} to find any available
   *             port.
   */
  public void startServerAndWait(final int port) {

    final var semaphore = new Semaphore(0);
    new Thread(() -> {

      this.startServer(port).onComplete(handler -> semaphore.release());

    }).start();

    try {

      semaphore.acquire();

    } catch (final InterruptedException e) {

      InternalLogger.log(Level.ERROR, e);
    }

  }

  /**
   * Check if the component is started.
   *
   * @return {@code true} if the component is started.
   */
  public boolean isServerStarted() {

    return this.server != null;

  }

  /**
   * Start a new mocked server.
   *
   * @param port to bind the mocker server or {@code 0} to find any available
   *             port.
   *
   * @return a future completed with the start result.
   */
  public Future<Void> startServer(final int port) {

    if (this.isServerStarted()) {

      return Future.failedFuture("Server is already started");

    } else {

      try {

        final var vertx = Vertx.vertx();
        final var server = vertx.createHttpServer();
        final var router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        router.route().handler(this::checkWeNetComponentApikey);

        this.fillInRouterHandler(router);

        router.routeWithRegex(".*").handler(ctx -> {

          final var response = ctx.response();
          response.putHeader("content-type", "application/json");
          response.setStatusCode(Status.NOT_IMPLEMENTED.getStatusCode());
          response.end();

        });

        return server.requestHandler(router).listen(port).compose(startedServer -> {

          this.server = startedServer;
          return Future.succeededFuture();

        });

      } catch (final Throwable cause) {

        return Future.failedFuture(cause);
      }
    }
  }

  /**
   * Check the request has the required component apikey.
   *
   * @param ctx routing context to check the header.
   */
  protected void checkWeNetComponentApikey(final RoutingContext ctx) {

    final var apiKey = ctx.request().getHeader(AbstractServicesVerticle.WENET_COMPONENT_APIKEY_HEADER);
    if (Containers.DEFAULT_WENET_COMPONENT_APIKEY.equals(apiKey)) {

      ctx.next();

    } else {

      final var response = ctx.response();
      response.setStatusCode(Status.UNAUTHORIZED.getStatusCode());
      response.end(new ErrorMessage("bad_wenet_component_apikey", "Invalid authentication credentials").toBuffer());

    }

  }

  /**
   * Called to add the handlers for the component.
   *
   * @param router to add the handlers.
   */
  protected abstract void fillInRouterHandler(Router router);

  /**
   * Return the base URL to the API that the mocked service respond.
   *
   * @return the mocker API URL.
   */
  public String getApiUrl() {

    final var builder = new StringBuilder();
    builder.append("http://");
    try {

      final var IP = InetAddress.getLocalHost();
      builder.append(IP.getHostAddress());

    } catch (final Throwable t) {

      builder.append("0.0.0.0");
    }

    builder.append(":");
    builder.append(this.getPort());

    return builder.toString();

  }

  /**
   * Stop the started server.
   *
   * @return a future completed with the stop result.
   */
  public Future<Void> stopServer() {

    if (this.isServerStarted()) {

      return this.server.close().compose(map -> {

        AbstractComponentMocker.this.server = null;
        return Future.succeededFuture();
      });

    } else {

      return Future.failedFuture("Server not started");
    }

  }

  /**
   * Return the port that the server is bind.
   *
   * @return the port where the server is bind or {@code 0} if the server is not
   *         started.
   */
  public int getPort() {

    if (this.server != null) {

      return this.server.actualPort();

    }

    return 0;

  }

  /**
   * Return the configuration to use when register the component.
   *
   * @return the configuration to register the component.
   */
  public JsonObject getComponentConfiguration() {

    final var conf = new JsonObject().put(this.getComponentConfigurationName(), "http://localhost:" + this.getPort());
    return conf;
  }

  /**
   * The name for the component on the configuration.
   *
   * @return the name of the component on the configuration.
   */
  protected abstract String getComponentConfigurationName();

  /**
   * Create client with the default session headers.
   *
   * @param vertx event bus to use.
   *
   * @return the client with the default session headers.
   */
  public static WebClient createClientWithDefaultSession(final Vertx vertx) {

    final var config = new JsonObject().put(AbstractServicesVerticle.WEB_CLIENT_CONF_KEY, new JsonObject()
        .put(AbstractServicesVerticle.WENET_COMPONENT_APIKEY_CONF_KEY, Containers.DEFAULT_WENET_COMPONENT_APIKEY));
    return AbstractServicesVerticle.createWebClientSession(vertx, config);

  }

}
