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

import javax.ws.rs.core.Response.Status;

import org.tinylog.Logger;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;

/**
 * The verticle to load the API specification and start the HTTP server.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractAPIVerticle extends AbstractVerticle {

  /**
   * The server that manage the HTTP requests.
   */
  protected HttpServer server;

  /**
   * {@inheritDoc}
   */
  @Override
  public void start(final Promise<Void> startPromise) throws Exception {

    OpenAPI3RouterFactory.create(this.getVertx(), this.getOpenAPIResourcePath(), createRouterFactory -> {
      if (createRouterFactory.succeeded()) {

        try {

          final var routerFactory = createRouterFactory.result();

          this.mountServiceInterfaces(routerFactory);

          // bind the ERROR handlers
          final var router = routerFactory.getRouter();
          router.errorHandler(Status.NOT_FOUND.getStatusCode(), NotFoundHandler.build());
          router.errorHandler(Status.BAD_REQUEST.getStatusCode(), BadRequestHandler.build());

          final var apiConf = this.config().getJsonObject("api", new JsonObject());
          final var httpServerOptions = new HttpServerOptions(apiConf);
          this.server = this.getVertx().createHttpServer(httpServerOptions);
          this.server.requestHandler(router).listen(startServer -> {
            if (startServer.failed()) {

              startPromise.fail(startServer.cause());

            } else {

              final var httpServer = startServer.result();
              final var host = httpServerOptions.getHost();
              final var actualPort = httpServer.actualPort();
              apiConf.put("port", actualPort);
              Logger.info("The server is ready at http://{}:{}", host, actualPort);
              this.startedServerAt(host, actualPort);
              startPromise.complete();
            }
          });

        } catch (final Throwable throwable) {
          // Can not start the server , may be the configuration is wrong
          startPromise.fail(throwable);
        }

      } else {
        // In theory never happens, Only can happens if specification is not right or
        // not present on the path
        startPromise.fail(createRouterFactory.cause());
      }
    });

  }

  /**
   * Called when the server has been started.
   *
   * @param host address that the HTTP server is bind.
   * @param port that the HTTP server is bind.
   */
  protected abstract void startedServerAt(String host, int port);

  /**
   * Mount the resources that will manage the API requests.
   *
   * @param routerFactory to register the service interfaces.
   */
  protected abstract void mountServiceInterfaces(OpenAPI3RouterFactory routerFactory);

  /**
   * Get the resource path to the file that contains the OpenAPI specification to load.
   *
   * @return the resource path to the OpenAPI specification.
   */
  protected abstract String getOpenAPIResourcePath();

  /**
   * Stop the HTTP server.
   *
   * {@inheritDoc}
   *
   * @see #server
   */
  @Override
  public void stop() {

    if (this.server != null) {

      this.server.close();
      this.server = null;
    }
  }

}
