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

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response.Status;

import org.tinylog.Logger;

import eu.internetofus.common.components.Mergeable;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.Validable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;

/**
 * Operations to apply to a model when is interact as web service.
 *
 * @see Model
 * @see AbstractAPIVerticle
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface ModelResources {

  /**
   * Create the handler to manage the retrieve of a model.
   *
   * @param id            identifier of the model.
   * @param modelName     name of the model.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   * @param success       function to call if the model can be retrieved.
   *
   * @param <T>           type of model to retrieve.
   *
   * @return the handler to manage the retrieved model.
   */
  static public <T extends Model> Handler<AsyncResult<T>> retrieveModelHandler(final String id, @NotNull final String modelName, @NotNull final OperationRequest context, @NotNull final Handler<AsyncResult<OperationResponse>> resultHandler,
      @NotNull final Consumer<T> success) {

    return retrieve -> {

      final var model = retrieve.result();
      if (retrieve.failed() || model == null) {

        final var cause = retrieve.cause();
        Logger.trace(cause, "Not found {} associated to {}.\n{}", () -> modelName, () -> id, () -> context.toJson().encodePrettily());
        OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.NOT_FOUND, "not_found_" + modelName, "Does not exist a '" + modelName + "' associated to '" + id + "'.");

      } else {

        Logger.trace("Found {}.\n{}", () -> model, () -> context.toJson().encodePrettily());
        success.accept(model);
      }
    };

  }

  /**
   * The method to call when is retrieving a model.
   *
   * @param searcher      the function used to obtain the model.
   * @param id            identifier of the model.
   * @param modelName     name of the model.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   *
   * @param <T>           type of model to retrieve.
   */
  static public <T extends Model> void retrieveModel(@NotNull final BiConsumer<String, Handler<AsyncResult<T>>> searcher, final String id, @NotNull final String modelName, @NotNull final OperationRequest context,
      final Handler<AsyncResult<OperationResponse>> resultHandler) {

    searcher.accept(id, retrieveModelHandler(id, modelName, context, resultHandler, model -> OperationReponseHandlers.responseOk(resultHandler, model)));

  }

  /**
   * Create the handler to manage the retrieve of a model.
   *
   * @param id            identifier of the model.
   * @param modelName     name of the model.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   * @param success       function to call if the model can be retrieved.
   *
   * @return the handler to manage the retrieved model.
   */
  static public Handler<AsyncResult<Void>> deleteModelHandler(final String id, @NotNull final String modelName, @NotNull final OperationRequest context, @NotNull final Handler<AsyncResult<OperationResponse>> resultHandler,
      @NotNull final Runnable success) {

    return delete -> {

      if (delete.failed()) {

        final var cause = delete.cause();
        Logger.trace(cause, "Cannot delete {} associated to {}.\n{}", () -> modelName, () -> id, () -> context.toJson().encodePrettily());
        OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.NOT_FOUND, "not_found_" + modelName, "Does not exist a '" + modelName + "' associated to '" + id + "'.");

      } else {

        Logger.trace("Deleted {} associated to {}.\n{}", () -> modelName, () -> id, () -> context.toJson().encodePrettily());
        success.run();
      }
    };

  }

  /**
   * The method to call when is deleting a model.
   *
   * @param deleter       the function used to obtain the model.
   * @param id            identifier of the model.
   * @param modelName     name of the model.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   */
  static public void deleteModel(@NotNull final BiConsumer<String, Handler<AsyncResult<Void>>> deleter, final String id, @NotNull final String modelName, @NotNull final OperationRequest context,
      @NotNull final Handler<AsyncResult<OperationResponse>> resultHandler) {

    deleter.accept(id, deleteModelHandler(id, modelName, context, resultHandler, () -> OperationReponseHandlers.responseOk(resultHandler)));

  }

  /**
   * Validate a model.
   *
   * @param vertx         event bus to use.
   * @param type          of model to validate.
   * @param value         of the model to verify.
   * @param modelName     name of the type.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   * @param success       component to call if the model is valid.
   *
   * @param <T>           type of model to validate.
   */
  static public <T extends Model & Validable> void validate(@NotNull final Vertx vertx, @NotNull final Class<T> type, final JsonObject value, @NotNull final String modelName, @NotNull final OperationRequest context,
      @NotNull final Handler<AsyncResult<OperationResponse>> resultHandler, @NotNull final Consumer<T> success) {

    toModel(type, value, modelName, context, resultHandler, model -> {

      final var codePrefix = "bad_" + modelName;
      model.validate(codePrefix, vertx).onComplete(valid -> {

        if (valid.failed()) {

          final var cause = valid.cause();
          Logger.trace(cause, "The {} is not a valid.\n{}", () -> model, () -> context.toJson().encodePrettily());
          OperationReponseHandlers.responseFailedWith(resultHandler, Status.BAD_REQUEST, cause);

        } else {

          success.accept(model);
        }
      });

    });

  }

  /**
   * Return the model described in a JSON.
   *
   * @param type          of model to validate.
   * @param value         of the model to verify.
   * @param modelName     name of the type.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   * @param success       component to call if the model is valid.
   *
   * @param <T>           type of model to obtain form the JSON.
   */
  static public <T extends Model> void toModel(@NotNull final Class<T> type, final JsonObject value, @NotNull final String modelName, @NotNull final OperationRequest context,
      @NotNull final Handler<AsyncResult<OperationResponse>> resultHandler, @NotNull final Consumer<T> success) {

    final var model = Model.fromJsonObject(value, type);
    if (model == null) {

      Logger.trace("The JSON does not represents a {}.\n{}\n{}", () -> type.getName(), () -> value.encodePrettily(), () -> context.toJson().encodePrettily());
      OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.BAD_REQUEST, "bad_" + modelName, "The JSON does not represents a " + modelName + ".");

    } else {

      success.accept(model);
    }

  }

  /**
   * Create the handler to manage the creation of a model.
   *
   * @param model         to store.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   * @param success       function to call if the model has been stored.
   *
   * @param <T>           type of model to create.
   *
   * @return the handler to manage the created model.
   */
  static public <T extends Model> Handler<AsyncResult<T>> createModelHandler(final T model, @NotNull final OperationRequest context, @NotNull final Handler<AsyncResult<OperationResponse>> resultHandler, final Consumer<T> success) {

    return stored -> {

      if (stored.failed()) {

        final var cause = stored.cause();
        Logger.trace(cause, "Cannot store {}.\n{}", () -> model, () -> context.toJson().encodePrettily());
        OperationReponseHandlers.responseFailedWith(resultHandler, Status.BAD_REQUEST, cause);

      } else {

        final var storedModel = stored.result();
        Logger.trace("Stored {}.\n{}", () -> storedModel, () -> context.toJson().encodePrettily());
        success.accept(storedModel);
      }
    };

  }

  /**
   * The method to call when is creating a model.
   *
   * @param vertx         event bus to use.
   * @param type          of model to validate.
   * @param value         of the model to verify.
   * @param modelName     name of the type.
   * @param storer        the function used to store the model.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   *
   * @param <T>           type of model to create.
   */
  static public <T extends Model & Validable> void createModel(@NotNull final Vertx vertx, @NotNull final Class<T> type, final JsonObject value, @NotNull final String modelName, @NotNull final BiConsumer<T, Handler<AsyncResult<T>>> storer,
      @NotNull final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler) {

    createModel(vertx, type, value, modelName, storer, context, resultHandler, storedModel -> OperationReponseHandlers.responseWith(resultHandler, Status.CREATED, storedModel));

  }

  /**
   * The method to call when is creating a model.
   *
   * @param vertx         event bus to use.
   * @param type          of model to validate.
   * @param value         of the model to verify.
   * @param modelName     name of the type.
   * @param storer        the function used to store the model.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   * @param success       component to manage the created model.
   *
   * @param <T>           type of model to create.
   */
  static public <T extends Model & Validable> void createModel(@NotNull final Vertx vertx, @NotNull final Class<T> type, final JsonObject value, @NotNull final String modelName, @NotNull final BiConsumer<T, Handler<AsyncResult<T>>> storer,
      @NotNull final OperationRequest context, final Handler<AsyncResult<OperationResponse>> resultHandler, @NotNull final Consumer<T> success) {

    validate(vertx, type, value, modelName, context, resultHandler, validModel -> {

      storer.accept(validModel, createModelHandler(validModel, context, resultHandler, storedModel -> OperationReponseHandlers.responseWith(resultHandler, Status.CREATED, storedModel)));

    });

  }

  /**
   * Merge a source model with the target one.
   *
   * @param targetModel   model with the default values.
   * @param sourceModel   model to get the values.
   * @param vertx         event bus to use.
   * @param modelName     name of the type.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   * @param success       component to call if the model is valid.
   *
   * @param <T>           type of model to merge.
   */
  static public <T extends Model & Mergeable<T>> void merge(@NotNull final T targetModel, final T sourceModel, @NotNull final Vertx vertx, @NotNull final String modelName, @NotNull final OperationRequest context,
      @NotNull final Handler<AsyncResult<OperationResponse>> resultHandler, @NotNull final Consumer<T> success) {

    final var codePrefix = "bad_" + modelName;
    targetModel.merge(sourceModel, codePrefix, vertx).onComplete(merge -> {

      if (merge.failed()) {

        final var cause = merge.cause();
        Logger.trace(cause, "The {} can not be merged with {}.\n{}", () -> sourceModel, () -> targetModel, () -> context.toJson().encodePrettily());
        OperationReponseHandlers.responseFailedWith(resultHandler, Status.BAD_REQUEST, cause);

      } else {

        final var mergedModel = merge.result();
        if (targetModel.equals(mergedModel)) {

          Logger.trace("The merged model {} is equals to the original.\n{}", () -> mergedModel, () -> context.toJson().encodePrettily());
          OperationReponseHandlers.responseWithErrorMessage(resultHandler, Status.BAD_REQUEST, codePrefix, "The merged '" + modelName + "' is equals to the current one.");

        } else {

          success.accept(mergedModel);
        }
      }
    });

  }

  /**
   * Merge a JSON model to the defined on the DB and finish with an OK.
   *
   * @param vertx         event bus to use.
   * @param id            identifier of the model.
   * @param modelName     name of the model.
   * @param type          of the model.
   * @param value         of the model to merge.
   * @param searcher      the function used to obtain a model.
   * @param updater       the function used to update a model.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   *
   * @param <T>           type of model to merge.
   */
  static public <T extends Model & Mergeable<T>> void mergeModel(@NotNull final Vertx vertx, final String id, @NotNull final String modelName, @NotNull final Class<T> type,
      @NotNull final BiConsumer<String, Handler<AsyncResult<T>>> searcher, final JsonObject value, @NotNull final BiConsumer<T, Handler<AsyncResult<Void>>> updater, @NotNull final OperationRequest context,
      @NotNull final Handler<AsyncResult<OperationResponse>> resultHandler) {

    mergeModel(vertx, id, modelName, type, searcher, value, updater, context, resultHandler, (sourceModel, targetModel, mergedModel) -> OperationReponseHandlers.responseOk(resultHandler, mergedModel));

  }

  /**
   * Merge a JSON model to the defined on the DB.
   *
   * @param vertx         event bus to use.
   * @param id            identifier of the model.
   * @param modelName     name of the model.
   * @param type          of the model.
   * @param value         of the model to merge.
   * @param searcher      the function used to obtain a model.
   * @param updater       the function used to update a model.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   * @param success       component to process the merged model.
   *
   * @param <T>           type of model to merge.
   */
  static public <T extends Model & Mergeable<T>> void mergeModel(@NotNull final Vertx vertx, final String id, @NotNull final String modelName, @NotNull final Class<T> type,
      @NotNull final BiConsumer<String, Handler<AsyncResult<T>>> searcher, final JsonObject value, @NotNull final BiConsumer<T, Handler<AsyncResult<Void>>> updater, @NotNull final OperationRequest context,
      @NotNull final Handler<AsyncResult<OperationResponse>> resultHandler, @NotNull final MergeConsumer<T> success) {

    toModel(type, value, modelName, context, resultHandler, sourceModel -> {

      searcher.accept(id, retrieveModelHandler(id, modelName, context, resultHandler, targetModel -> {

        merge(targetModel, sourceModel, vertx, modelName, context, resultHandler, mergedModel -> {

          updater.accept(mergedModel, updateModelHandler(mergedModel, context, resultHandler, () -> success.accept​(sourceModel, targetModel, mergedModel)));

        });
      }));

    });

  }

  /**
   * Update the handler to manage the update of a model.
   *
   * @param model         to store.
   * @param context       of the request.
   * @param resultHandler to inform of the response.
   * @param success       function to call if the model has been stored.
   *
   * @param <T>           type of model to update.
   *
   * @return the handler to manage the updated model.
   */
  static public <T extends Model> Handler<AsyncResult<Void>> updateModelHandler(final T model, @NotNull final OperationRequest context, @NotNull final Handler<AsyncResult<OperationResponse>> resultHandler, final Runnable success) {

    return stored -> {

      if (stored.failed()) {

        final var cause = stored.cause();
        Logger.trace(cause, "Cannot update {}.\n{}", () -> model, () -> context.toJson().encodePrettily());
        OperationReponseHandlers.responseFailedWith(resultHandler, Status.BAD_REQUEST, cause);

      } else {

        Logger.trace("Updated {}.\n{}", () -> model, () -> context.toJson().encodePrettily());
        success.run();
      }
    };

  }

}
