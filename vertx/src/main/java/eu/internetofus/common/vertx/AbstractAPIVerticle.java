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

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.openapi.RouterBuilder;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.io.IOUtils;
import org.tinylog.Logger;

/**
 * The verticle to load the API specification and start the HTTP server.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractAPIVerticle extends AbstractVerticle {

  /**
   * The configuration property that contains the path where the OpenAPI has to be
   * stored.
   */
  public static final String OPENAPI_FILE_PATH_KEY = "openapi_path";

  /**
   * The default path where the OpenAPI will be stored.
   */
  public static final String DEFAULT_OPENAPI_FILE_PATH = "var/openapi.yaml";

  /**
   * The server that manage the HTTP requests.
   */
  protected HttpServer server;

  /**
   * {@inheritDoc}
   */
  @Override
  public void start(final Promise<Void> startPromise) throws Exception {

    this.getOpenAPIFilePath().compose(path -> {

      final var vertx = this.getVertx();
      return RouterBuilder.create(vertx, path);

    }).onComplete(createRouterFactory -> {

      if (createRouterFactory.succeeded()) {

        try {

          final var routerFactory = createRouterFactory.result();

          this.mountServiceInterfaces(routerFactory);
          routerFactory.rootHandler(this.createCORSHandler());

          // bind the ERROR handlers
          final var router = routerFactory.createRouter();
          router.errorHandler(Status.NOT_FOUND.getStatusCode(), NotFoundHandler.build());
          router.errorHandler(Status.BAD_REQUEST.getStatusCode(), BadRequestHandler.build());
          router.errorHandler(Status.INTERNAL_SERVER_ERROR.getStatusCode(), InternalServerErrorHandler.build());

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
   * Obtain the path to the file where the OpenAPI specification is stored.
   *
   * @return the path to the file with the OpenAPI specification.
   */
  protected Future<String> getOpenAPIFilePath() {

    return this.getVertx().executeBlocking((Handler<Promise<String>>) promise -> {

      try {

        final var in = this.getClass().getClassLoader().getResourceAsStream(this.getOpenAPIResourcePath());
        final var openapi = IOUtils.toString(in, Charset.defaultCharset());
        final var apiConf = this.config().getJsonObject("api", new JsonObject());
        final var path = FileSystems.getDefault()
            .getPath(apiConf.getString(OPENAPI_FILE_PATH_KEY, DEFAULT_OPENAPI_FILE_PATH));
        Files.writeString(path, openapi, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        promise.complete(path.toUri().toString());

      } catch (final Throwable cause) {

        promise.fail(cause);
      }

    });

  }

  /**
   * Create the handler that allow any CORS connection.
   *
   * @return the handler that manage the CORS over the API.
   */
  protected Handler<RoutingContext> createCORSHandler() {

    final var corsConf = this.config().getJsonObject("api", new JsonObject()).getJsonObject("cors", new JsonObject());
    var handler = CorsHandler.create(corsConf.getString("origin", "*"));
    final var headers = corsConf.getJsonArray("headers",
        new JsonArray().add(AbstractServicesVerticle.WENET_COMPONENT_APIKEY_HEADER).add(HttpHeaders.CONTENT_TYPE)
            .add(HttpHeaders.CONTENT_LENGTH).add(HttpHeaders.CONTENT_RANGE).add(HttpHeaders.ACCEPT)
            .add(HttpHeaders.ACCEPT_RANGES).add(HttpHeaders.ORIGIN));

    for (var i = 0; i < headers.size(); i++) {

      final var headerName = headers.getString(i);
      if (headerName != null && headerName.length() > 0) {

        handler = handler.allowedHeader(headerName);
      }

    }

    final var methods = corsConf.getJsonArray("methods",
        new JsonArray().add(HttpMethod.GET.name()).add(HttpMethod.POST.name()).add(HttpMethod.PUT.name())
            .add(HttpMethod.PATCH.name()).add(HttpMethod.DELETE.name()).add(HttpMethod.OPTIONS.name())
            .add(HttpMethod.HEAD.name()));
    for (var i = 0; i < methods.size(); i++) {

      var methodName = methods.getString(i);
      if (methodName != null && methodName.length() > 0) {

        try {

          methodName = methodName.trim().toUpperCase();
          final var method = HttpMethod.valueOf(methodName);
          handler = handler.allowedMethod(method);

        } catch (final Throwable throwable) {

          Logger.error(throwable, "Cannot add {} as allowed method for CORS", methodName);
        }
      }

    }

    handler.allowCredentials(corsConf.getBoolean("credentials", true));
    handler.maxAgeSeconds(corsConf.getInteger("maxAgeSeconds", 1209600));

    return handler;
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
  protected abstract void mountServiceInterfaces(RouterBuilder routerFactory);

  /**
   * Get the resource path to the file that contains the OpenAPI specification to
   * load.
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
