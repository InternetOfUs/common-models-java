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

import eu.internetofus.common.model.ErrorMessage;
import eu.internetofus.common.model.Model;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.ClusterSerializable;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.serviceproxy.ServiceException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response.Status;
import org.tinylog.Logger;

/**
 * A HTTP client to interact with a WeNet platform components.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ComponentClient {

  /**
   * The pool of web clients.
   */
  protected WebClient client;

  /**
   * The partial absolute URL of the component.
   */
  protected String componentURL;

  /**
   * Create a new service.
   *
   * @param client       to interact with the other modules.
   * @param componentURL the URL to the component.
   */
  public ComponentClient(final WebClient client, final String componentURL) {

    this.client = client;
    this.componentURL = componentURL;

  }

  /**
   * Obtain the URL to the API for interact with this component.
   *
   * @return the future URL to the API of this component.
   */
  public Future<String> obtainApiUrl() {

    final Promise<String> promise = Promise.promise();
    this.obtainApiUrl(promise);
    return promise.future();
  }

  /**
   * Obtain the URL to the API for interact with this component.
   *
   * @param handler to inform of the API.
   */
  public void obtainApiUrl(final Handler<AsyncResult<String>> handler) {

    handler.handle(Future.succeededFuture(this.componentURL));

  }

  /**
   * Create the absolute URL to a path of the component.
   *
   * @param paths to the component.
   *
   * @return the absolute URL to component path.
   */
  protected String createAbsoluteUrlWith(final Object... paths) {

    final var builder = new StringBuilder();
    builder.append(this.componentURL);
    for (final Object path : paths) {

      final var pathSegment = String.valueOf(path);
      final var max = pathSegment.length();
      if (max > 0 && pathSegment.charAt(0) != '/' && builder.charAt(builder.length() - 1) != '/') {

        builder.append('/');
      }

      for (var i = 0; i < max; i++) {

        final var c = pathSegment.charAt(i);
        if (Character.isLetterOrDigit(c) || c == '-' || c == '.' || c == '_' || c == '~') {

          builder.append(c);

        } else if (c == '/') {

          if (builder.charAt(builder.length() - 1) != '/') {

            builder.append(c);

          } // else ignored

        } else {

          final var bytes = String.valueOf(c).getBytes(Charset.defaultCharset());
          for (final byte b : bytes) {

            builder.append('%');
            builder.append(Integer.toHexString(b >> 4 & 0xf));
            builder.append(Integer.toHexString(b & 0xf));
          }
        }
      }

    }

    return builder.toString();

  }

  /**
   * Create a service exception from a client response.
   *
   * @param response that contains the error.
   *
   * @return the exception with the error.
   */
  protected ServiceException toServiceException(final HttpResponse<Buffer> response) {

    final var message = Model.fromResponse(response, ErrorMessage.class);
    if (message != null) {

      return new ServiceException(response.statusCode(), response.statusMessage(), message.toJsonObject());

    } else {

      return new ServiceException(response.statusCode(), response.statusMessage());
    }

  }

  /**
   * Create a consumer for a {@link Status#NO_CONTENT} HTTP response.
   *
   * @param actionId identifier of the action.
   *
   * @return the consumer for the no content.
   */
  protected BiConsumer<Handler<AsyncResult<Void>>, HttpResponse<Buffer>> noContentConsumer(
      @NotNull final String actionId) {

    return (handler, response) -> {

      if (response.statusCode() != Status.NO_CONTENT.getStatusCode()) {

        Logger.trace("[{}] FAILED response contains {}", () -> actionId, () -> response.bodyAsString());
        handler.handle(Future.failedFuture("The response has content"));

      } else {

        Logger.trace("[{}] SUCCESS without content", actionId);
        handler.handle(Future.succeededFuture());
      }

    };

  }

  /**
   * Post an object to a component.
   *
   * @param content   object to post.
   * @param paths     to the component to post.
   * @param extractor to obtain the result form a buffer.
   *
   * @param <T>       type of the response content.
   *
   * @return the future with the response object.
   */
  protected <T> Future<T> post(@NotNull final JsonObject content,
      @NotNull final Function<HttpResponse<Buffer>, T> extractor, @NotNull final Object... paths) {

    return this.post(content, extractor, (Map<String, String>) null, paths);

  }

  /**
   * Post an object to a component.
   *
   * @param content object to post.
   * @param paths   to the component to post.
   *
   * @return the future with the response object.
   */
  protected Future<JsonObject> post(@NotNull final JsonObject content, @NotNull final Object... paths) {

    return this.post(content, (Map<String, String>) null, paths);

  }

  /**
   * Post an array to a component.
   *
   * @param content array to post.
   * @param paths   to the component to post.
   *
   * @return the future with the response array.
   */
  protected Future<JsonArray> post(@NotNull final JsonArray content, @NotNull final Object... paths) {

    return this.post(content, (Map<String, String>) null, paths);

  }

  /**
   * Post an object to a component.
   *
   * @param content     object to post.
   * @param paths       to the component to post.
   * @param queryParams parameters for the request.
   * @param extractor   to obtain the result form a buffer.
   *
   * @param <T>         type of the response content.
   *
   * @return the future with the response object.
   */
  protected <T> Future<T> post(@NotNull final JsonObject content,
      @NotNull final Function<HttpResponse<Buffer>, T> extractor, final Map<String, String> queryParams,
      @NotNull final Object... paths) {

    return this.request(HttpMethod.POST, this.createAbsoluteUrlWith(paths), queryParams, content.toBuffer(), extractor);

  }

  /**
   * Post an object to a component.
   *
   * @param content     object to post.
   * @param paths       to the component to post.
   * @param queryParams parameters for the request.
   *
   * @return the future with the response object.
   */
  protected Future<JsonObject> post(@NotNull final JsonObject content, final Map<String, String> queryParams,
      @NotNull final Object... paths) {

    return this.request(HttpMethod.POST, this.createAbsoluteUrlWith(paths), queryParams, content.toBuffer(),
        this.createObjectExtractor());

  }

  /**
   * Post an array to a component.
   *
   * @param content     array to post.
   * @param paths       to the component to post.
   * @param queryParams parameters for the request.
   *
   * @return the future with the response array.
   */
  protected Future<JsonArray> post(@NotNull final JsonArray content, final Map<String, String> queryParams,
      @NotNull final Object... paths) {

    return this.request(HttpMethod.POST, this.createAbsoluteUrlWith(paths), queryParams, content.toBuffer(),
        this.createArrayExtractor());

  }

  /**
   * Put an object to a component.
   *
   * @param content object to put.
   * @param paths   to the component to put.
   *
   * @return the future with the response object.
   */
  protected Future<JsonObject> put(@NotNull final JsonObject content, @NotNull final Object... paths) {

    return this.put(content, (Map<String, String>) null, paths);

  }

  /**
   * Put an array to a component.
   *
   * @param content array to put.
   * @param paths   to the component to put.
   *
   * @return the future with the response array.
   */
  protected Future<JsonArray> put(@NotNull final JsonArray content, @NotNull final Object... paths) {

    return this.put(content, (Map<String, String>) null, paths);

  }

  /**
   * Put an object to a component.
   *
   * @param path      to the component to put.
   * @param content   object to put.
   * @param extractor to obtain the result form a buffer.
   *
   * @param <T>       type of the response content.
   *
   * @return the future with the response object.
   */
  protected <T> Future<T> put(@NotNull final String path, @NotNull final ClusterSerializable content,
      @NotNull final Function<HttpResponse<Buffer>, T> extractor) {

    return this.put(path, (Map<String, String>) null, content, extractor);

  }

  /**
   * Put an object to a component.
   *
   * @param content     object to put.
   * @param paths       to the component to put.
   * @param queryParams parameters for the request.
   *
   * @return the future with the response object.
   */
  protected Future<JsonObject> put(@NotNull final JsonObject content, final Map<String, String> queryParams,
      @NotNull final Object... paths) {

    return this.request(HttpMethod.PUT, this.createAbsoluteUrlWith(paths), queryParams, content.toBuffer(),
        this.createObjectExtractor());

  }

  /**
   * Put an array to a component.
   *
   * @param content     array to put.
   * @param paths       to the component to put.
   * @param queryParams parameters for the request.
   *
   * @return the future with the response array.
   */
  protected Future<JsonArray> put(@NotNull final JsonArray content, final Map<String, String> queryParams,
      @NotNull final Object... paths) {

    return this.request(HttpMethod.PUT, this.createAbsoluteUrlWith(paths), queryParams, content.toBuffer(),
        this.createArrayExtractor());

  }

  /**
   * Put an object to a component.
   *
   * @param path        to the component to put.
   * @param queryParams parameters for the request.
   * @param content     object to put.
   * @param extractor   to obtain the result form a buffer.
   *
   * @param <T>         type of the response content.
   *
   * @return the future with the response object.
   */
  protected <T> Future<T> put(@NotNull final String path, final Map<String, String> queryParams,
      @NotNull final ClusterSerializable content, @NotNull final Function<HttpResponse<Buffer>, T> extractor) {

    final var buffer = Json.CODEC.toBuffer(content, false);
    return this.request(HttpMethod.PUT, path, queryParams, buffer, extractor);

  }

  /**
   * Patch an object to a component.
   *
   * @param content object to patch.
   * @param paths   to the component to patch.
   *
   * @return the future with the response object.
   */
  protected Future<JsonObject> patch(@NotNull final JsonObject content, @NotNull final Object... paths) {

    return this.patch(content, (Map<String, String>) null, paths);

  }

  /**
   * Patch an array to a component.
   *
   * @param content array to patch.
   * @param paths   to the component to patch.
   *
   * @return the future with the response array.
   */
  protected Future<JsonArray> patch(@NotNull final JsonArray content, @NotNull final Object... paths) {

    return this.patch(content, (Map<String, String>) null, paths);

  }

  /**
   * Patch an object to a component.
   *
   * @param content     object to patch.
   * @param paths       to the component to patch.
   * @param queryParams parameters for the request.
   *
   * @return the future with the response object.
   */
  protected Future<JsonObject> patch(@NotNull final JsonObject content, final Map<String, String> queryParams,
      @NotNull final Object... paths) {

    return this.request(HttpMethod.PATCH, this.createAbsoluteUrlWith(paths), queryParams, content.toBuffer(),
        this.createObjectExtractor());

  }

  /**
   * Patch an array to a component.
   *
   * @param content     array to patch.
   * @param paths       to the component to patch.
   * @param queryParams parameters for the request.
   *
   * @return the future with the response array.
   */
  protected Future<JsonArray> patch(@NotNull final JsonArray content, final Map<String, String> queryParams,
      @NotNull final Object... paths) {

    return this.request(HttpMethod.PATCH, this.createAbsoluteUrlWith(paths), queryParams, content.toBuffer(),
        this.createArrayExtractor());

  }

  /**
   * Create a request to the specified URL with the specified query parameters.
   *
   * @param method      the HTTP method.
   * @param url         to request.
   * @param queryParams parameters for the request.
   *
   * @return the request to call.
   */
  protected HttpRequest<Buffer> createRequestFor(final HttpMethod method, @NotNull final String url,
      final Map<String, String> queryParams) {

    var request = this.client.requestAbs(method, url);
    if (queryParams != null) {

      for (final var entry : queryParams.entrySet()) {

        final var key = entry.getKey();
        final var value = entry.getValue();
        request = request.addQueryParam(key, value);
      }
    }
    return request;

  }

  /**
   * Return the action identifier to the specified method and URL.
   *
   * @param method the HTTP method.
   * @param url    to request.
   *
   * @return the action identifier.
   */
  protected String createActionId(@NotNull final HttpMethod method, @NotNull final String url) {

    return "[" + UUID.randomUUID() + "] " + method + " " + url;
  }

  /**
   * Return the action identifier to the specified method and URL.
   *
   * @param method      the HTTP method.
   * @param url         to request.
   * @param queryParams parameters for the request.
   *
   * @return the action identifier.
   */
  protected String createActionId(@NotNull final HttpMethod method, @NotNull final String url,
      @NotNull final Map<String, String> queryParams) {

    return this.createActionId(method, url) + "?" + queryParams;

  }

  /**
   * Request and process the response.
   *
   * @param method    the HTTP method.
   * @param url       to request.
   * @param extractor function to obtain the
   *
   * @param <T>       type of model to receive.
   *
   * @return the future with the received model as response.
   */
  protected <T> Future<T> request(final HttpMethod method, @NotNull final String url,
      @NotNull final Function<HttpResponse<Buffer>, T> extractor) {

    final Promise<T> promise = Promise.promise();
    final var actionId = this.createActionId(method, url);
    Logger.trace("{} STARTED", actionId);
    try {

      this.client.requestAbs(method, url).send()
          .onSuccess(this.createHandlerThatExtractBodyFromSuccessResponse(extractor, promise, actionId))
          .onFailure(this.createRequestFailureHandler(promise, actionId));

    } catch (final Throwable throwable) {

      promise.tryFail(throwable);

    }

    return promise.future();

  }

  /**
   * Request and process the response.
   *
   * @param method      the HTTP method.
   * @param url         to request.
   * @param queryParams parameters for the request.
   * @param extractor   function to obtain the
   *
   * @param <T>         type of model to receive.
   *
   * @return the future with the received model as response.
   */
  protected <T> Future<T> request(final HttpMethod method, @NotNull final String url,
      final Map<String, String> queryParams, @NotNull final Function<HttpResponse<Buffer>, T> extractor) {

    final Promise<T> promise = Promise.promise();
    final var actionId = this.createActionId(method, url, queryParams);
    Logger.trace("{} STARTED", actionId);
    try {

      this.createRequestFor(method, url, queryParams).send()
          .onSuccess(this.createHandlerThatExtractBodyFromSuccessResponse(extractor, promise, actionId))
          .onFailure(this.createRequestFailureHandler(promise, actionId));

    } catch (final Throwable throwable) {

      promise.tryFail(throwable);

    }

    return promise.future();

  }

  /**
   * Request and process the response.
   *
   * @param method      the HTTP method.
   * @param url         to request.
   * @param queryParams parameters for the request.
   * @param content     to send.
   * @param extractor   function to obtain the
   *
   * @param <T>         type of model to receive.
   *
   * @return the future with the received model as response.
   */
  protected <T> Future<T> request(final HttpMethod method, @NotNull final String url,
      final Map<String, String> queryParams, final Buffer content,
      @NotNull final Function<HttpResponse<Buffer>, T> extractor) {

    final Promise<T> promise = Promise.promise();
    final var actionId = this.createActionId(method, url);
    Logger.trace("{} with {} STARTED", actionId, content);
    try {

      this.createRequestFor(method, url, queryParams).sendJson(content)
          .onSuccess(this.createHandlerThatExtractBodyFromSuccessResponse(extractor, promise, actionId))
          .onFailure(this.createRequestFailureHandler(promise, actionId));

    } catch (final Throwable throwable) {

      promise.tryFail(throwable);

    }

    return promise.future();

  }

  /**
   * Create the component to no extract from a response.
   *
   * @return the extractor to not obtain from the extractor.
   */
  protected Function<HttpResponse<Buffer>, Void> createVoidExtractor() {

    return response -> null;
  }

  /**
   * Create the component to extract a {@link JsonObject} from a response.
   *
   * @return the extractor to obtain the object from the response.
   */
  protected Function<HttpResponse<Buffer>, JsonObject> createObjectExtractor() {

    return response -> response.bodyAsJsonObject();
  }

  /**
   * Create the component to extract a {@link JsonArray} from a response.
   *
   * @return the extractor to obtain the array from the response.
   */
  protected Function<HttpResponse<Buffer>, JsonArray> createArrayExtractor() {

    return response -> response.bodyAsJsonArray();
  }

  /**
   * Create the handler to manage when the request failed.
   *
   * @param promise  to inform of the model.
   * @param actionId identifier of the HTTP requests.
   *
   * @param <T>      type of content that has try to send.
   *
   * @return the extractor to obtain the array from the response.
   */
  protected <T> Handler<Throwable> createRequestFailureHandler(final Promise<T> promise, final String actionId) {

    return cause -> {

      Logger.trace(cause, "{} FAILED to be executed", actionId);
      promise.fail(cause);

    };
  }

  /**
   * Create a handler that extract the model from a {@link HttpResponse}.
   *
   * @param extractor function to obtain the content of the response.
   * @param promise   to inform of the model.
   * @param actionId  identifier of the HTTP requests.
   *
   * @param <T>       type of model to extract.
   *
   * @return the handler for the response object.
   */
  protected <T> Handler<HttpResponse<Buffer>> createHandlerThatExtractBodyFromSuccessResponse(
      final Function<HttpResponse<Buffer>, T> extractor, final Promise<T> promise, final String actionId) {

    return response -> {

      final var code = response.statusCode();
      if (Status.NO_CONTENT.getStatusCode() == code) {

        Logger.trace("{} SUCCESS with code {}", actionId, code);
        promise.complete();

      } else if (Status.Family.familyOf(code) == Status.Family.SUCCESSFUL) {

        try {

          final var model = extractor.apply(response);
          Logger.trace("{} SUCCESS with code {} and content {}", actionId, code, model);
          promise.complete(model);

        } catch (final Throwable cause) {

          Logger.trace(cause, "{} FAILED with code {} and unexpected content {}", () -> actionId, () -> code,
              () -> response.bodyAsString());
          promise.fail(cause);
        }

      } else {

        Logger.trace("{} FAILED with code {} and content {}", () -> actionId, () -> code,
            () -> response.bodyAsString());
        final var cause = this.toServiceException(response);
        promise.fail(cause);

      }

    };

  }

  /**
   * Create a handler that accept any body from a success {@link HttpResponse}.
   *
   * @param promise  to inform of the model.
   * @param actionId identifier of the HTTP requests.
   *
   * @return the handler for the response object.
   */
  protected Handler<HttpResponse<Buffer>> createHandlerWithAnyBodyAndSuccessResponse(final Promise<?> promise,
      final String actionId) {

    return response -> {

      final var code = response.statusCode();
      if (Status.Family.familyOf(code) == Status.Family.SUCCESSFUL) {

        Logger.trace("{} SUCCESS with code {}", actionId, code);
        promise.complete();

      } else {

        Logger.trace("{} FAILED with code {} and content {}", () -> actionId, () -> code,
            () -> response.bodyAsString());
        final var cause = this.toServiceException(response);
        promise.fail(cause);

      }

    };

  }

  /**
   * Get a {@link JsonObject}.
   *
   * @param paths to the resource to get.
   *
   * @return the future received {@link JsonObject}.
   */
  protected Future<JsonObject> getJsonObject(final Object... paths) {

    return this.request(HttpMethod.GET, this.createAbsoluteUrlWith(paths), this.createObjectExtractor());

  }

  /**
   * Get a {@link JsonArray}.
   *
   * @param paths to the resource to get.
   *
   * @return the future received {@link JsonArray}.
   */
  protected Future<JsonArray> getJsonArray(final Object... paths) {

    return this.request(HttpMethod.GET, this.createAbsoluteUrlWith(paths), this.createArrayExtractor());

  }

  /**
   * Get a {@link JsonObject} with some parameters.
   *
   * @param queryParams the query parameters.
   * @param paths       to the resource to get.
   *
   * @return the future received {@link JsonObject}.
   */
  protected Future<JsonObject> getJsonObject(final Map<String, String> queryParams, final Object... paths) {

    return this.request(HttpMethod.GET, this.createAbsoluteUrlWith(paths), queryParams, this.createObjectExtractor());

  }

  /**
   * Get a {@link JsonArray} with some parameters.
   *
   * @param queryParams the query parameters.
   * @param paths       to the resource to get.
   *
   * @return the future received {@link JsonArray}.
   */
  protected Future<JsonArray> getJsonArray(final Map<String, String> queryParams, final Object... paths) {

    return this.request(HttpMethod.GET, this.createAbsoluteUrlWith(paths), queryParams, this.createArrayExtractor());

  }

  /**
   * Delete a resource.
   *
   * @param paths to the resource to remove.
   *
   * @return the future that explain if the component is deleted or not.
   */
  protected Future<Void> delete(final Object... paths) {

    return this.request(HttpMethod.DELETE, this.createAbsoluteUrlWith(paths), null);

  }

  /**
   * Delete a resource with some parameters.
   *
   * @param queryParams the query parameters.
   * @param paths       to the resource to delete.
   *
   * @return the future that explain if the component is deleted or not.
   */
  protected Future<Void> delete(final Map<String, String> queryParams, final Object... paths) {

    return this.request(HttpMethod.DELETE, this.createAbsoluteUrlWith(paths), queryParams, null);

  }

  /**
   * Check if the head is defined.
   *
   * @param paths to the component to post.
   *
   * @return the future with the response object.
   */
  protected Future<Boolean> head(@NotNull final Object... paths) {

    final var url = this.createAbsoluteUrlWith(paths);
    return this.headWithAbsolute(url);
  }

  /**
   * Check if the head is defined.
   *
   * @param url to do the head request.
   *
   * @return the future with the response object.
   */
  protected Future<Boolean> headWithAbsolute(final String url) {

    final Promise<Boolean> promise = Promise.promise();
    final var actionId = this.createActionId(HttpMethod.HEAD, url);
    Logger.trace("{} with {} STARTED", actionId);
    try {

      this.client.requestAbs(HttpMethod.HEAD, url).send().onSuccess(response -> {

        final var code = response.statusCode();
        final var success = Status.Family.familyOf(code) == Status.Family.SUCCESSFUL;
        promise.complete(success);

      }).onFailure(this.createRequestFailureHandler(promise, actionId));

    } catch (final Throwable throwable) {

      promise.tryFail(throwable);

    }

    return promise.future();
  }

}
