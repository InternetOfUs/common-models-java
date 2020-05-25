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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response.Status;

import org.tinylog.Logger;

import eu.internetofus.common.components.ErrorException;
import eu.internetofus.common.components.ErrorMessage;
import eu.internetofus.common.components.Model;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

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
  public ComponentClient(WebClient client, String componentURL) {

    this.client = client;
    this.componentURL = componentURL;

  }

  /**
   * Create the absolute URL to a path of the component.
   *
   * @param paths to the component.
   *
   * @return the absolute URL to component path.
   */
  protected String createAbsoluteUrlWith(Object... paths) {

    final StringBuilder builder = new StringBuilder();
    builder.append(this.componentURL);
    for (final Object path : paths) {

      final String element = String.valueOf(path);
      if (builder.charAt(builder.length() - 1) != '/') {

        if (element.charAt(0) != '/') {

          builder.append('/');
        }

      } else if (element.charAt(0) != '/') {

        element.substring(1);
      }
      builder.append(element);

    }

    return builder.toString();

  }

  /**
   * Notify to a handler that the HTTP request ends with an error.
   *
   * @param actionHandler to notify the HTTP response is an error.
   * @param response      that contains the error.
   *
   * @param <T>           expected responses for the HTTP request.
   */
  protected <T> void notifyErrorTo(Handler<AsyncResult<T>> actionHandler, HttpResponse<Buffer> response) {

    final ErrorMessage message = Model.fromResponse(response, ErrorMessage.class);
    if (message != null) {

      actionHandler.handle(Future.failedFuture(new ErrorException(message)));

    } else {

      final String error = response.bodyAsString();
      actionHandler.handle(Future.failedFuture(error));
    }

  }

  /**
   * Send a model as JSON by HTTP request.
   *
   * @param request       to the server.
   * @param action        name of the HTTP action.
   * @param url           to do the request.
   * @param content       to send.
   * @param resultHandler handler that manage the result.
   *
   * @param <T>           type of model to send.
   */
  protected <T extends Model> void sendModelAsJson(@NotNull HttpRequest<Buffer> request, @NotNull String action, @NotNull String url, @NotNull T content, @NotNull Handler<AsyncResult<T>> resultHandler) {

    final JsonObject body = content.toJsonObject();
    request.sendJsonObject(body, send -> {

      if (send.failed()) {

        final Throwable cause = send.cause();
        Logger.trace(cause, "FAILED {} {} to {}", action, content, url);
        resultHandler.handle(Future.failedFuture(cause));

      } else {

        final HttpResponse<Buffer> response = send.result();
        final int code = response.statusCode();
        if (Status.Family.familyOf(code) == Status.Family.SUCCESSFUL) {

          @SuppressWarnings("unchecked")
          final Class<T> type = (Class<T>) content.getClass();
          final T result = Model.fromResponse(response, type);
          if (result == null) {

            Logger.trace("FAILED {} {} to {}, because the body {} is not of the type {}.", () -> action, () -> url, () -> result, () -> response.bodyAsString(), () -> type);
            resultHandler.handle(Future.failedFuture(new ErrorException("decode_content_error", "The response content is not of the expected type.")));

          } else {

            Logger.trace("FAILED {} {} to {}, because {} ({}).", () -> action, () -> content, () -> url, () -> response.bodyAsString(), () -> code);
            this.notifyErrorTo(resultHandler, response);

          }

        } else {

        }

      }
    });

  }

  /**
   * Continue if the result has not failed.
   *
   * @param action        HTTP action
   * @param actionId      identifier of the action.
   * @param resultHandler handler for the result.
   * @param consumer      to process the response result.
   *
   * @param <T>           type of content for the response.
   *
   */
  protected <T> void successing(AsyncResult<HttpResponse<Buffer>> action, @NotNull String actionId, @NotNull Handler<AsyncResult<T>> resultHandler, BiConsumer<Handler<AsyncResult<T>>, HttpResponse<Buffer>> consumer) {

    if (action.failed()) {

      final Throwable cause = action.cause();
      Logger.trace(cause, "[{}] FAILED", actionId);
      resultHandler.handle(Future.failedFuture(cause));

    } else {

      final HttpResponse<Buffer> response = action.result();
      final int code = response.statusCode();
      if (Status.Family.familyOf(code) == Status.Family.SUCCESSFUL) {

        consumer.accept(resultHandler, response);

      } else {

        Logger.trace("[{}] FAILED ({}) {}", () -> actionId, () -> code, () -> response.bodyAsString());
        this.notifyErrorTo(resultHandler, response);

      }
    }

  }

  /**
   * Create a consumer to extract a {@link JsonArray} from an HTTP response.
   *
   * @param actionId identifier of the action.
   *
   * @return the consumer that obtain a JSON array.
   */
  protected BiConsumer<Handler<AsyncResult<JsonArray>>, HttpResponse<Buffer>> jsonArrayConsumer(@NotNull String actionId) {

    return (handler, response) -> {

      try {

        final JsonArray array = response.bodyAsJsonArray();
        if (array == null) {

          Logger.trace("[{}] FAILED {} is not a JSON array", () -> actionId, () -> response.bodyAsString());
          handler.handle(Future.failedFuture("The response is not a JsonArray"));

        } else {

          Logger.trace("[{}] SUCCESS with {}", actionId, array);
          handler.handle(Future.succeededFuture(array));

        }

      } catch (final Throwable cause) {

        Logger.trace("[{}] FAILED {} is not a JSON array", () -> actionId, () -> response.bodyAsString());
        handler.handle(Future.failedFuture(cause));

      }
    };

  }

  /**
   * Create a consumer to extract a {@link JsonObject} from an HTTP response.
   *
   * @param actionId identifier of the action.
   *
   * @return the consumer that obtain a JSON object.
   */
  protected BiConsumer<Handler<AsyncResult<JsonObject>>, HttpResponse<Buffer>> jsonObjectConsumer(@NotNull String actionId) {

    return (handler, response) -> {

      try {

        final JsonObject object = response.bodyAsJsonObject();
        if (object == null) {

          Logger.trace("[{}] FAILED {} is not a JSON object", () -> actionId, () -> response.bodyAsString());
          handler.handle(Future.failedFuture("The response is not a JsonObject"));

        } else {

          Logger.trace("[{}] SUCCESS with {}", actionId, object);
          handler.handle(Future.succeededFuture(object));

        }

      } catch (final Throwable cause) {

        Logger.trace("[{}] FAILED {} is not a JSON object", () -> actionId, () -> response.bodyAsString());
        handler.handle(Future.failedFuture(cause));

      }

    };

  }

  /**
   * Create a consumer to extract a {@link Model} from an HTTP response.
   *
   * @param type     of model to extract.
   * @param actionId identifier of the action.
   *
   * @param <T>      type of model to extract.
   *
   * @return the consumer that obtain a JSON array.
   */
  protected <T extends Model> BiConsumer<Handler<AsyncResult<T>>, HttpResponse<Buffer>> modelConsumer(@NotNull Class<T> type, @NotNull String actionId) {

    return (handler, response) -> {

      final T model = Model.fromResponse(response, type);
      if (model == null) {

        Logger.trace("[{}] FAILED {} is not a {}", () -> actionId, () -> response.bodyAsString(), () -> type);
        handler.handle(Future.failedFuture("The response is not a " + type));

      } else {

        Logger.trace("[{}] SUCCESS with {}", actionId, model);
        handler.handle(Future.succeededFuture(model));

      }

    };

  }

  /**
   * Create a consumer for a {@link Status#NO_CONTENT} HTTP response.
   *
   * @param actionId identifier of the action.
   *
   * @return the consumer for the no content.
   */
  protected BiConsumer<Handler<AsyncResult<Void>>, HttpResponse<Buffer>> noContentConsumer(@NotNull String actionId) {

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
   * Post a model to a component.
   *
   * @param content     model to post.
   * @param postHandler the handler to manager the posted resource.
   * @param paths       to the component to post.
   *
   * @param <T>         type of model to post.
   */
  protected <T extends Model> void post(@NotNull T content, @NotNull Handler<AsyncResult<T>> postHandler, @NotNull Object... paths) {

    final String url = this.createAbsoluteUrlWith(paths);
    final String actionId = UUID.randomUUID().toString();
    Logger.trace("[{}] POST {} to {}", actionId, content, url);
    final JsonObject body = content.toJsonObject();
    @SuppressWarnings("unchecked")
    final Class<T> type = (Class<T>) content.getClass();
    this.client.postAbs(url).sendJsonObject(body, response -> this.successing(response, actionId, postHandler, this.modelConsumer(type, actionId)));

  }

  /**
   * Post a {@link JsonArray} to a component.
   *
   * @param content     resource to post.
   * @param postHandler the handler to manager the posted resource.
   * @param paths       to the component to post.
   */
  protected void post(JsonArray content, Handler<AsyncResult<JsonArray>> postHandler, Object... paths) {

    final String url = this.createAbsoluteUrlWith(paths);
    final String actionId = UUID.randomUUID().toString();
    Logger.trace("[{}] POST {} to {}", actionId, content, url);
    this.client.postAbs(url).sendJson(content, response -> this.successing(response, actionId, postHandler, this.jsonArrayConsumer(actionId)));

  }

  /**
   * Post a {@link JsonObject} to a component.
   *
   * @param content     resource to post.
   * @param postHandler the handler to manager the posted resource.
   * @param paths       to the component to post.
   */
  protected void post(JsonObject content, Handler<AsyncResult<JsonObject>> postHandler, Object... paths) {

    final String url = this.createAbsoluteUrlWith(paths);
    final String actionId = UUID.randomUUID().toString();
    Logger.trace("[{}] POST {} to {}", actionId, content, url);
    this.client.postAbs(url).sendJson(content, response -> this.successing(response, actionId, postHandler, this.jsonObjectConsumer(actionId)));

  }

  /**
   * Put a model to a component.
   *
   * @param content    model to put.
   * @param putHandler the handler to manager the put resource.
   * @param paths      to the component to put.
   *
   * @param <T>        type of model to put.
   */
  protected <T extends Model> void put(@NotNull T content, @NotNull Handler<AsyncResult<T>> putHandler, @NotNull Object... paths) {

    final String url = this.createAbsoluteUrlWith(paths);
    final String actionId = UUID.randomUUID().toString();
    Logger.trace("[{}] PUT {} to {}", actionId, content, url);
    final JsonObject body = content.toJsonObject();
    @SuppressWarnings("unchecked")
    final Class<T> type = (Class<T>) content.getClass();
    this.client.putAbs(url).sendJsonObject(body, response -> this.successing(response, actionId, putHandler, this.modelConsumer(type, actionId)));
  }

  /**
   * Put a {@link JsonArray} to a component.
   *
   * @param content    resource to put.
   * @param putHandler the handler to manager the put resource.
   * @param paths      to the component to put.
   */
  protected void put(JsonArray content, Handler<AsyncResult<JsonArray>> putHandler, Object... paths) {

    final String url = this.createAbsoluteUrlWith(paths);
    final String actionId = UUID.randomUUID().toString();
    Logger.trace("[{}] PUT {} to {}", actionId, content, url);
    this.client.putAbs(url).sendJson(content, response -> this.successing(response, actionId, putHandler, this.jsonArrayConsumer(actionId)));

  }

  /**
   * Put a {@link JsonObject} to a component.
   *
   * @param content    resource to put.
   * @param putHandler the handler to manager the put resource.
   * @param paths      to the component to put.
   */
  protected void put(JsonObject content, Handler<AsyncResult<JsonObject>> putHandler, Object... paths) {

    final String url = this.createAbsoluteUrlWith(paths);
    final String actionId = UUID.randomUUID().toString();
    Logger.trace("[{}] PUT {} to {}", actionId, content, url);
    this.client.putAbs(url).sendJson(content, response -> this.successing(response, actionId, putHandler, this.jsonObjectConsumer(actionId)));

  }

  /**
   * Get a resource model.
   *
   * @param type       of model to get.
   * @param getHandler the handler to manage the receiver resource.
   * @param paths      to the resource to get.
   *
   * @param <T>        type of model to get.
   */
  protected <T extends Model> void getModel(Class<T> type, @NotNull Handler<AsyncResult<T>> getHandler, Object... paths) {

    final String url = this.createAbsoluteUrlWith(paths);
    final String actionId = UUID.randomUUID().toString();
    Logger.trace("[{}] GET {}", actionId, url);
    this.client.getAbs(url).send(response -> this.successing(response, actionId, getHandler, this.modelConsumer(type, actionId)));

  }

  /**
   * Get a {@link JsonArray}.
   *
   * @param getHandler the handler to manage the receiver resource.
   * @param paths      to the resource to get.
   */
  protected void getJsonArray(@NotNull Handler<AsyncResult<JsonArray>> getHandler, Object... paths) {

    final String url = this.createAbsoluteUrlWith(paths);
    final String actionId = UUID.randomUUID().toString();
    Logger.trace("[{}] GET {}", actionId, url);
    this.client.getAbs(url).send(response -> this.successing(response, actionId, getHandler, this.jsonArrayConsumer(actionId)));

  }

  /**
   * Get a {@link JsonObject}.
   *
   * @param getHandler the handler to manage the receiver resource.
   * @param paths      to the resource to get.
   */
  protected void getJsonObject(@NotNull Handler<AsyncResult<JsonObject>> getHandler, Object... paths) {

    final String url = this.createAbsoluteUrlWith(paths);
    final String actionId = UUID.randomUUID().toString();
    Logger.trace("[{}] GET {}", actionId, url);
    this.client.getAbs(url).send(response -> this.successing(response, actionId, getHandler, this.jsonObjectConsumer(actionId)));

  }

  /**
   * Delete a resource.
   *
   * @param deleteHandler the handler to manage the delete result.
   * @param paths         to the resource to remove.
   */
  protected void delete(Handler<AsyncResult<Void>> deleteHandler, Object... paths) {

    final String url = this.createAbsoluteUrlWith(paths);
    final String actionId = UUID.randomUUID().toString();
    Logger.trace("[{}] DELETE {}", actionId, url);
    this.client.deleteAbs(url).send(response -> this.successing(response, actionId, deleteHandler, this.noContentConsumer(actionId)));

  }

  /**
   * Create a handler to convert a {@link JsonObject} to a {@link Model}
   *
   * @param type            of model to handle.
   * @param retrieveHandler handler to inform of the model obtained by the {@link JsonObject}.
   *
   * @param <T>             type of model to handle.
   *
   * @return a handler to process the {@link JsonObject} and return the model
   */
  public static <T extends Model> Handler<AsyncResult<JsonObject>> handlerForModel(Class<T> type, Handler<AsyncResult<T>> retrieveHandler) {

    return handler -> {

      if (handler.failed()) {

        retrieveHandler.handle(Future.failedFuture(handler.cause()));

      } else {

        final JsonObject result = handler.result();
        if (result == null) {

          retrieveHandler.handle(Future.succeededFuture());

        } else {

          final T model = Model.fromJsonObject(result, type);
          if (model == null) {

            Logger.trace(handler.cause(), "Unexpected content {} is not of the type {}", result, type);
            retrieveHandler.handle(Future.failedFuture(result + " is not of the type '" + type + "'."));

          } else {

            retrieveHandler.handle(Future.succeededFuture(model));
          }

        }
      }

    };
  }

  /**
   * Create a handler to convert a {@link JsonArray} to a {@link List} {@link Model}'s.
   *
   * @param type            of model to handle.
   * @param retrieveHandler handler to inform of the model obtained by the {@link JsonArray}.
   *
   * @param <T>             type of model to handle.
   *
   * @return a handler to process the {@link JsonArray} and return the list models.
   */
  public static <T extends Model> Handler<AsyncResult<JsonArray>> handlerForListModel(Class<T> type, Handler<AsyncResult<List<T>>> retrieveHandler) {

    return handler -> {

      if (handler.failed()) {

        retrieveHandler.handle(Future.failedFuture(handler.cause()));

      } else {

        final JsonArray result = handler.result();
        if (result == null) {

          retrieveHandler.handle(Future.succeededFuture());

        } else {

          List<T> models = new ArrayList<>();
          for (int i = 0; i < result.size(); i++) {

            JsonObject object = result.getJsonObject(i);
            final T model = Model.fromJsonObject(object, type);
            if (model == null) {

              Logger.trace(handler.cause(), "Unexpected content {} at {} is not of the type {}", result, i, type);
              retrieveHandler.handle(Future.failedFuture(result + " at '" + i + "' is not of the type '" + type + "'."));

            } else {

              models.add(model);
            }

          }

          retrieveHandler.handle(Future.succeededFuture(models));

        }
      }

    };
  }

}
