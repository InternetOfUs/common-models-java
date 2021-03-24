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

import eu.internetofus.common.components.ErrorMessage;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.WeNetComponent;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.serviceproxy.ServiceException;
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
public class ComponentClient implements WeNetComponent {

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
   * {@inheritDoc}
   *
   * @see #componentURL
   */
  @Override
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

      if (builder.charAt(builder.length() - 1) != '/') {

        builder.append('/');
      }

      var element = String.valueOf(path).trim();
      if (element.charAt(0) == '/') {

        element = element.substring(1);
      }
      builder.append(element);

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
   * @param content object to post.
   * @param paths   to the component to post.
   *
   * @return the future with the response object.
   */
  protected Future<JsonObject> post(@NotNull final JsonObject content, @NotNull final Object... paths) {

    return this.request(HttpMethod.POST, this.createAbsoluteUrlWith(paths), content.toBuffer(),
        this.createObjectExtractor());

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

    return this.request(HttpMethod.POST, this.createAbsoluteUrlWith(paths), content.toBuffer(),
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

    return this.request(HttpMethod.PUT, this.createAbsoluteUrlWith(paths), content.toBuffer(),
        this.createObjectExtractor());

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

    return this.request(HttpMethod.PUT, this.createAbsoluteUrlWith(paths), content.toBuffer(),
        this.createArrayExtractor());

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

    return this.request(HttpMethod.PATCH, this.createAbsoluteUrlWith(paths), content.toBuffer(),
        this.createObjectExtractor());

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

    return this.request(HttpMethod.PATCH, this.createAbsoluteUrlWith(paths), content.toBuffer(),
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
    for (final var entry : queryParams.entrySet()) {

      final var key = entry.getKey();
      final var value = entry.getValue();
      request = request.addQueryParam(key, value);

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
    this.client.requestAbs(method, url).send()
        .onSuccess(this.createHandlerThatExtractBodyFromSuccessResponse(extractor, promise, actionId))
        .onFailure(this.createRequestFailureHandler(promise, actionId));
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
    this.createRequestFor(method, url, queryParams).send()
        .onSuccess(this.createHandlerThatExtractBodyFromSuccessResponse(extractor, promise, actionId))
        .onFailure(this.createRequestFailureHandler(promise, actionId));
    return promise.future();

  }

  /**
   * Request and process the response.
   *
   * @param method    the HTTP method.
   * @param url       to request.
   * @param content   to send.
   * @param extractor function to obtain the
   *
   * @param <T>       type of model to receive.
   *
   * @return the future with the received model as response.
   */
  protected <T> Future<T> request(final HttpMethod method, @NotNull final String url, final Buffer content,
      @NotNull final Function<HttpResponse<Buffer>, T> extractor) {

    final Promise<T> promise = Promise.promise();
    final var actionId = this.createActionId(method, url);
    Logger.trace("{} with {} STARTED", actionId, content);
    this.client.requestAbs(method, url).sendJson(content)
        .onSuccess(this.createHandlerThatExtractBodyFromSuccessResponse(extractor, promise, actionId))
        .onFailure(this.createRequestFailureHandler(promise, actionId));
    return promise.future();

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

}
