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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response.Status;

import org.tinylog.Logger;

import eu.internetofus.common.components.ErrorMessage;
import eu.internetofus.common.components.Model;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.serviceproxy.ServiceException;

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
   * Notify to a handler that the HTTP request ends with an error.
   *
   * @param actionHandler to notify the HTTP response is an error.
   * @param response      that contains the error.
   *
   * @param <T>           expected responses for the HTTP request.
   */
  protected <T> void notifyErrorTo(final Handler<AsyncResult<T>> actionHandler, final HttpResponse<Buffer> response) {

    ServiceException cause = null;
    final var message = Model.fromResponse(response, ErrorMessage.class);
    if (message != null) {

      cause = new ServiceException(response.statusCode(), response.statusMessage(), message.toJsonObject());

    } else {

      cause = new ServiceException(response.statusCode(), response.statusMessage());
    }

    actionHandler.handle(Future.failedFuture(cause));
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
  protected <T> void successing(final AsyncResult<HttpResponse<Buffer>> action, @NotNull final String actionId, @NotNull final Handler<AsyncResult<T>> resultHandler,
      final BiConsumer<Handler<AsyncResult<T>>, HttpResponse<Buffer>> consumer) {

    if (action.failed()) {

      final var cause = action.cause();
      Logger.trace(cause, "[{}] FAILED", actionId);
      resultHandler.handle(Future.failedFuture(cause));

    } else {

      final var response = action.result();
      final var code = response.statusCode();
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
  protected BiConsumer<Handler<AsyncResult<JsonArray>>, HttpResponse<Buffer>> jsonArrayConsumer(@NotNull final String actionId) {

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
  protected BiConsumer<Handler<AsyncResult<JsonObject>>, HttpResponse<Buffer>> jsonObjectConsumer(@NotNull final String actionId) {

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
  protected <T extends Model> BiConsumer<Handler<AsyncResult<T>>, HttpResponse<Buffer>> modelConsumer(@NotNull final Class<T> type, @NotNull final String actionId) {

    return (handler, response) -> {

      final var model = Model.fromResponse(response, type);
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
  protected BiConsumer<Handler<AsyncResult<Void>>, HttpResponse<Buffer>> noContentConsumer(@NotNull final String actionId) {

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
  protected <T extends Model> void post(@NotNull final T content, @NotNull final Handler<AsyncResult<T>> postHandler, @NotNull final Object... paths) {

    final var url = this.createAbsoluteUrlWith(paths);
    final var actionId = UUID.randomUUID().toString();
    Logger.trace("[{}] POST {} to {}", actionId, content, url);
    final var body = content.toJsonObject();
    @SuppressWarnings("unchecked")
    final var type = (Class<T>) content.getClass();
    this.client.postAbs(url).sendJsonObject(body, response -> this.successing(response, actionId, postHandler, this.modelConsumer(type, actionId)));

  }

  /**
   * Post a {@link JsonArray} to a component.
   *
   * @param content     resource to post.
   * @param postHandler the handler to manager the posted resource.
   * @param paths       to the component to post.
   */
  protected void post(final JsonArray content, final Handler<AsyncResult<JsonArray>> postHandler, final Object... paths) {

    final var url = this.createAbsoluteUrlWith(paths);
    final var actionId = UUID.randomUUID().toString();
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
  protected void post(final JsonObject content, final Handler<AsyncResult<JsonObject>> postHandler, final Object... paths) {

    final var url = this.createAbsoluteUrlWith(paths);
    final var actionId = UUID.randomUUID().toString();
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
  protected <T extends Model> void put(@NotNull final T content, @NotNull final Handler<AsyncResult<T>> putHandler, @NotNull final Object... paths) {

    final var url = this.createAbsoluteUrlWith(paths);
    final var actionId = UUID.randomUUID().toString();
    Logger.trace("[{}] PUT {} to {}", actionId, content, url);
    final var body = content.toJsonObject();
    @SuppressWarnings("unchecked")
    final var type = (Class<T>) content.getClass();
    this.client.putAbs(url).sendJsonObject(body, response -> this.successing(response, actionId, putHandler, this.modelConsumer(type, actionId)));
  }

  /**
   * Put a {@link JsonArray} to a component.
   *
   * @param content    resource to put.
   * @param putHandler the handler to manager the put resource.
   * @param paths      to the component to put.
   */
  protected void put(final JsonArray content, final Handler<AsyncResult<JsonArray>> putHandler, final Object... paths) {

    final var url = this.createAbsoluteUrlWith(paths);
    final var actionId = UUID.randomUUID().toString();
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
  protected void put(final JsonObject content, final Handler<AsyncResult<JsonObject>> putHandler, final Object... paths) {

    final var url = this.createAbsoluteUrlWith(paths);
    final var actionId = UUID.randomUUID().toString();
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
  protected <T extends Model> void getModel(final Class<T> type, @NotNull final Handler<AsyncResult<T>> getHandler, final Object... paths) {

    final var url = this.createAbsoluteUrlWith(paths);
    final var actionId = UUID.randomUUID().toString();
    Logger.trace("[{}] GET {}", actionId, url);
    this.client.getAbs(url).send(response -> this.successing(response, actionId, getHandler, this.modelConsumer(type, actionId)));

  }

  /**
   * Get a {@link JsonArray}.
   *
   * @param getHandler the handler to manage the receiver resource.
   * @param paths      to the resource to get.
   */
  protected void getJsonArray(@NotNull final Handler<AsyncResult<JsonArray>> getHandler, final Object... paths) {

    final var url = this.createAbsoluteUrlWith(paths);
    final var actionId = UUID.randomUUID().toString();
    Logger.trace("[{}] GET {}", actionId, url);
    this.client.getAbs(url).send(response -> this.successing(response, actionId, getHandler, this.jsonArrayConsumer(actionId)));

  }

  /**
   * Get a {@link JsonObject}.
   *
   * @param getHandler the handler to manage the receiver resource.
   * @param paths      to the resource to get.
   */
  protected void getJsonObject(@NotNull final Handler<AsyncResult<JsonObject>> getHandler, final Object... paths) {

    final var url = this.createAbsoluteUrlWith(paths);
    final var actionId = UUID.randomUUID().toString();
    Logger.trace("[{}] GET {}", actionId, url);
    this.client.getAbs(url).send(response -> this.successing(response, actionId, getHandler, this.jsonObjectConsumer(actionId)));

  }

  /**
   * Delete a resource.
   *
   * @param deleteHandler the handler to manage the delete result.
   * @param paths         to the resource to remove.
   */
  protected void delete(final Handler<AsyncResult<Void>> deleteHandler, final Object... paths) {

    final var url = this.createAbsoluteUrlWith(paths);
    final var actionId = UUID.randomUUID().toString();
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
  public static <T extends Model> Handler<AsyncResult<JsonObject>> handlerForModel(final Class<T> type, final Handler<AsyncResult<T>> retrieveHandler) {

    return handler -> {

      if (handler.failed()) {

        retrieveHandler.handle(Future.failedFuture(handler.cause()));

      } else {

        final var result = handler.result();
        if (result == null) {

          retrieveHandler.handle(Future.succeededFuture());

        } else {

          final var model = Model.fromJsonObject(result, type);
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
  public static <T extends Model> Handler<AsyncResult<JsonArray>> handlerForListModel(final Class<T> type, final Handler<AsyncResult<List<T>>> retrieveHandler) {

    return handler -> {

      if (handler.failed()) {

        retrieveHandler.handle(Future.failedFuture(handler.cause()));

      } else {

        final var result = handler.result();
        if (result == null) {

          retrieveHandler.handle(Future.succeededFuture());

        } else {

          final List<T> models = new ArrayList<>();
          for (var i = 0; i < result.size(); i++) {

            try {

              final var object = result.getJsonObject(i);
              final var model = Model.fromJsonObject(object, type);
              if (model == null) {

                Logger.trace("Unexpected content {} at {} is not of the type {}", result, i, type);
                retrieveHandler.handle(Future.failedFuture(result + " at '" + i + "' is not of the type '" + type + "'."));

              } else {

                models.add(model);
              }

            } catch (final ClassCastException cause) {

              Logger.trace(cause, "Unexpected content {} at {} is not of the type {}", result, i, type);
              retrieveHandler.handle(Future.failedFuture(result + " at '" + i + "' is not of the type '" + type + "'."));

            }

          }

          retrieveHandler.handle(Future.succeededFuture(models));

        }
      }

    };
  }

}
