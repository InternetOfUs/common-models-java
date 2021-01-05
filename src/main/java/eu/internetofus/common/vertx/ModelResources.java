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

import eu.internetofus.common.TimeManager;
import eu.internetofus.common.components.CreateUpdateTsDetails;
import eu.internetofus.common.components.Mergeable;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.Updateable;
import eu.internetofus.common.components.Validable;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Consumer;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response.Status;
import org.tinylog.Logger;

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
   * @param model    context of the model to retrieve.
   * @param searcher the function used to obtain the model.
   * @param context  of the request.
   * @param success  function to call if the model can be retrieved.
   *
   * @param <T>      type of model to retrieve.
   * @param <I>      type for the model identifier.
   */
  static public <T extends Model, I> void retrieveModelChain(@NotNull final ModelContext<T, I> model,
      @NotNull final BiConsumer<I, Handler<AsyncResult<T>>> searcher, @NotNull final ServiceContext context,
      @NotNull final Runnable success) {

    searcher.accept(model.id, retrieve -> {

      model.target = retrieve.result();
      if (retrieve.failed() || model.target == null) {

        final var cause = retrieve.cause();
        Logger.trace(cause, "Not found {}.\n{}", model, context);
        ServiceResponseHandlers.responseWithErrorMessage(context.resultHandler, Status.NOT_FOUND,
            "not_found_" + model.name, "Does not exist a '" + model.name + "' associated to '" + model.id + "'.");

      } else {

        Logger.trace("Found {}.\n{}", model, context);
        success.run();
      }

    });

  }

  /**
   * The method to call when is retrieving a model.
   *
   * @param model    context of the model to retrieve.
   * @param searcher the function used to obtain the model.
   * @param context  of the request.
   *
   * @param <T>      type of model to retrieve.
   * @param <I>      type for the model identifier.
   */
  static public <T extends Model, I> void retrieveModel(@NotNull final ModelContext<T, I> model,
      @NotNull final BiConsumer<I, Handler<AsyncResult<T>>> searcher, @NotNull final ServiceContext context) {

    retrieveModelChain(model, searcher, context,
        () -> ServiceResponseHandlers.responseOk(context.resultHandler, model.target));

  }

  /**
   * Create the handler to manage the retrieve of a model.
   *
   * @param model   context of the model to delete.
   * @param deleter the function used to obtain the model.
   * @param context of the request.
   * @param success function to call if the model can be retrieved.
   *
   * @param <T>     type of model to delete.
   * @param <I>     type for the model identifier.
   */
  static public <T extends Model, I> void deleteModelChain(@NotNull final ModelContext<T, I> model,
      @NotNull final BiConsumer<I, Handler<AsyncResult<Void>>> deleter, @NotNull final ServiceContext context,
      @NotNull final Runnable success) {

    deleter.accept(model.id, delete -> {

      if (delete.failed()) {

        final var cause = delete.cause();
        Logger.trace(cause, "Cannot delete {}.\n{}", model, context);
        ServiceResponseHandlers.responseWithErrorMessage(context.resultHandler, Status.NOT_FOUND,
            "not_found_" + model.name, "Does not exist a '" + model.name + "' associated to '" + model.id + "'.");

      } else {

        Logger.trace("Deleted {}.\n{}", model, context);
        success.run();
      }
    });

  }

  /**
   * The method to call when is deleting a model.
   *
   * @param model   context of the model to delete.
   * @param deleter the function used to obtain the model.
   * @param context of the request.
   *
   * @param <T>     type of model to delete.
   * @param <I>     type for the model identifier.
   */
  static public <T extends Model, I> void deleteModel(@NotNull final ModelContext<T, I> model,
      @NotNull final BiConsumer<I, Handler<AsyncResult<Void>>> deleter, @NotNull final ServiceContext context) {

    deleteModelChain(model, deleter, context, () -> ServiceResponseHandlers.responseOk(context.resultHandler));

  }

  /**
   * Validate a model.
   *
   * @param vertx   event bus to use.
   * @param model   context of the model to validate.
   * @param context of the request.
   * @param success component to call if the model is valid.
   *
   * @param <T>     type of model to validate.
   * @param <I>     type for the model identifier.
   */
  static public <T extends Model & Validable, I> void validate(@NotNull final Vertx vertx,
      @NotNull final ModelContext<T, I> model, @NotNull final ServiceContext context, @NotNull final Runnable success) {

    final var codePrefix = "bad_" + model.name;
    model.source.validate(codePrefix, vertx).onComplete(valid -> {

      if (valid.failed()) {

        final var cause = valid.cause();
        Logger.trace(cause, "The {} is not a valid.\n{}", model, context);
        ServiceResponseHandlers.responseFailedWith(context.resultHandler, Status.BAD_REQUEST, cause);

      } else {

        model.value = model.source;
        success.run();
      }
    });

  }

  /**
   * Return the model described in a JSON.
   *
   * @param value   of the model to verify.
   * @param model   context of the model to obtain.
   * @param context of the request.
   * @param success component to call when has obtained the model.
   *
   * @param <T>     type of model to obtain form the JSON.
   * @param <I>     type for the model identifier.
   */
  static public <T extends Model, I> void toModel(final JsonObject value, @NotNull final ModelContext<T, I> model,
      @NotNull final ServiceContext context, @NotNull final Runnable success) {

    if (value == null) {

      Logger.trace("The NULL JSON does not represents a {}.\n{}", () -> model, () -> context);
      ServiceResponseHandlers.responseWithErrorMessage(context.resultHandler, Status.BAD_REQUEST, "bad_" + model.name,
          "No JSON provided for a " + model.name + ".");

    } else {

      model.source = Model.fromJsonObject(value, model.type);
      if (model.source == null) {

        Logger.trace("The JSON does not represents a {}.\n{}\n{}", () -> model, () -> value.encodePrettily(),
            () -> context);
        ServiceResponseHandlers.responseWithErrorMessage(context.resultHandler, Status.BAD_REQUEST, "bad_" + model.name,
            "The JSON does not represents a " + model.name + ".");

      } else {

        Logger.trace("Obtain model {}.\n{}", model, context);
        success.run();
      }
    }
  }

  /**
   * The method to call when is creating a model.
   *
   * @param vertx   event bus to use.
   * @param value   of the model to verify.
   * @param model   context of the model to create.
   * @param storer  the function used to store the model.
   * @param context of the request.
   *
   * @param <T>     type of model to create.
   * @param <I>     type of the model identifier.
   */
  static public <T extends Model & Validable, I> void createModel(@NotNull final Vertx vertx, final JsonObject value,
      @NotNull final ModelContext<T, I> model, @NotNull final BiConsumer<T, Handler<AsyncResult<T>>> storer,
      @NotNull final ServiceContext context) {

    createModelChain(vertx, value, model, storer, context,
        () -> ServiceResponseHandlers.responseWith(context.resultHandler, Status.CREATED, model.value));

  }

  /**
   * The method to call when is creating a model.
   *
   * @param vertx   event bus to use.
   * @param value   of the model to verify.
   * @param model   to create.
   * @param storer  the function used to store the model.
   * @param context of the request.
   * @param success component to manage the created model.
   *
   * @param <T>     type of model to create.
   * @param <I>     type of the model identifier.
   */
  static public <T extends Model & Validable, I> void createModelChain(@NotNull final Vertx vertx,
      final JsonObject value, @NotNull final ModelContext<T, I> model,
      @NotNull final BiConsumer<T, Handler<AsyncResult<T>>> storer, @NotNull final ServiceContext context,
      @NotNull final Runnable success) {

    toModel(value, model, context, () -> {

      validate(vertx, model, context, () -> {

        if (model.value instanceof CreateUpdateTsDetails) {

          final var now = TimeManager.now();
          ((CreateUpdateTsDetails) model.value)._creationTs = now;
          ((CreateUpdateTsDetails) model.value)._lastUpdateTs = now;
        }
        storer.accept(model.value, stored -> {

          if (stored.failed()) {

            final var cause = stored.cause();
            Logger.trace(cause, "Cannot store {}.\n{}", model, context);
            ServiceResponseHandlers.responseFailedWith(context.resultHandler, Status.BAD_REQUEST, cause);

          } else {

            model.value = stored.result();
            Logger.trace("Stored {}.\n{}", model, context);
            success.run();
          }
        });
      });
    });

  }

  /**
   * Merge a source model with the target one.
   *
   * @param vertx   event bus to use.
   * @param model   to merge.
   * @param context of the request.
   * @param success component to call if the model is valid.
   *
   * @param <T>     type of model to merge.
   * @param <I>     type of the model identifier.
   */
  static public <T extends Model & Mergeable<T>, I> void merge(@NotNull final Vertx vertx,
      @NotNull final ModelContext<T, I> model, @NotNull final ServiceContext context, @NotNull final Runnable success) {

    model.target.merge(model.source, "bad_" + model.name, vertx).onComplete(merge -> {

      if (merge.failed()) {

        final var cause = merge.cause();
        Logger.trace(cause, "The {} can not be merged with {}.\n{}", () -> model.source, () -> model.target,
            () -> context);
        ServiceResponseHandlers.responseFailedWith(context.resultHandler, Status.BAD_REQUEST, cause);

      } else {

        model.value = merge.result();
        if (model.target.equals(model.value)) {

          Logger.trace("The merged model {} is equals to the original.\n{}", () -> model.value, () -> context);
          ServiceResponseHandlers.responseWithErrorMessage(context.resultHandler, Status.BAD_REQUEST,
              model.name + "_to_merge_equal_to_original",
              "The merged '" + model.name + "' is equals to the current one.");

        } else {

          success.run();
        }
      }
    });

  }

  /**
   * Merge a JSON model to the defined on the DB and finish with an OK.
   *
   * @param vertx    event bus to use.
   * @param value    of the model to merge.
   * @param model    context of the model to merge.
   * @param searcher the function used to obtain a model.
   * @param updater  the function used to update a model.
   * @param context  of the request.
   *
   * @param <T>      type of model to merge.
   * @param <I>      type of the model identifier.
   */
  static public <T extends Model & Mergeable<T>, I> void mergeModel(@NotNull final Vertx vertx, final JsonObject value,
      @NotNull final ModelContext<T, I> model, @NotNull final BiConsumer<I, Handler<AsyncResult<T>>> searcher,
      @NotNull final BiConsumer<T, Handler<AsyncResult<Void>>> updater, @NotNull final ServiceContext context) {

    mergeModelChain(vertx, value, model, searcher, updater, context,
        () -> ServiceResponseHandlers.responseOk(context.resultHandler, model.value));

  }

  /**
   * Merge a JSON model to the defined on the DB.
   *
   * @param vertx    event bus to use.
   * @param value    of the model to merge.
   * @param model    context of the model to merge.
   * @param searcher the function used to obtain a model.
   * @param updater  the function used to update a model.
   * @param context  of the request.
   * @param success  component to process the merged model.
   *
   * @param <T>      type of model to merge.
   * @param <I>      type of the model identifier.
   */
  static public <T extends Model & Mergeable<T>, I> void mergeModelChain(@NotNull final Vertx vertx,
      final JsonObject value, @NotNull final ModelContext<T, I> model,
      @NotNull final BiConsumer<I, Handler<AsyncResult<T>>> searcher,
      @NotNull final BiConsumer<T, Handler<AsyncResult<Void>>> updater, @NotNull final ServiceContext context,
      @NotNull final Runnable success) {

    toModel(value, model, context, () -> {

      retrieveModelChain(model, searcher, context, () -> {

        merge(vertx, model, context, () -> updateModelChain(model, updater, context, success));

      });
    });

  }

  /**
   * Update a JSON model to the defined on the DB.
   *
   * @param vertx    event bus to use.
   * @param value    of the model to update.
   * @param model    context of the model to update.
   * @param searcher the function used to obtain a model.
   * @param updater  the function used to store a model.
   * @param context  of the request.
   * @param success  component to process the updated model.
   *
   * @param <T>      type of model to update.
   * @param <I>      type of the model identifier.
   */
  static public <T extends Model & Updateable<T>, I> void updateModelChain(@NotNull final Vertx vertx,
      final JsonObject value, @NotNull final ModelContext<T, I> model,
      @NotNull final BiConsumer<I, Handler<AsyncResult<T>>> searcher,
      @NotNull final BiConsumer<T, Handler<AsyncResult<Void>>> updater, @NotNull final ServiceContext context,
      @NotNull final Runnable success) {

    toModel(value, model, context, () -> {

      retrieveModelChain(model, searcher, context, () -> {

        update(vertx, model, context, () -> updateModelChain(model, updater, context, success));

      });
    });

  }

  /**
   * Update a model into the DB.
   *
   * @param model   context of the model to update.
   * @param updater the function used to store a model.
   * @param context of the request.
   * @param success component to process the updated model.
   *
   * @param <T>     type of model to update.
   * @param <I>     type of the model identifier.
   */
  static public <T extends Model, I> void updateModelChain(@NotNull final ModelContext<T, I> model,
      @NotNull final BiConsumer<T, Handler<AsyncResult<Void>>> updater, @NotNull final ServiceContext context,
      @NotNull final Runnable success) {

    if (model.value instanceof CreateUpdateTsDetails) {

      final var now = TimeManager.now();
      ((CreateUpdateTsDetails) model.value)._lastUpdateTs = now;
    }
    updater.accept(model.value, stored -> {

      if (stored.failed()) {

        final var cause = stored.cause();
        Logger.trace(cause, "Cannot update {}.\n{}", model, context);
        ServiceResponseHandlers.responseFailedWith(context.resultHandler, Status.BAD_REQUEST, cause);

      } else {

        Logger.trace("Updated {}.\n{}", model, context);
        success.run();
      }

    });

  }

  /**
   * Update a source model with the target one.
   *
   * @param vertx   event bus to use.
   * @param model   context of the model to update.
   * @param context of the request.
   * @param success component to call if the model is valid.
   *
   * @param <T>     type of model to update.
   * @param <I>     type of the model identifier.
   */
  static public <T extends Model & Updateable<T>, I> void update(@NotNull final Vertx vertx,
      @NotNull final ModelContext<T, I> model, @NotNull final ServiceContext context, @NotNull final Runnable success) {

    model.target.update(model.source, "bad_" + model.name, vertx).onComplete(update -> {

      if (update.failed()) {

        final var cause = update.cause();
        Logger.trace(cause, "The {} can not be updated with {}.\n{}", () -> model, () -> model.source, () -> context);
        ServiceResponseHandlers.responseFailedWith(context.resultHandler, Status.BAD_REQUEST, cause);

      } else {

        model.value = update.result();
        if (model.target.equals(model.value)) {

          Logger.trace("The updated model {} is equals to the original.\n{}", model, context);
          ServiceResponseHandlers.responseWithErrorMessage(context.resultHandler, Status.BAD_REQUEST,
              model.name + "_to_update_equal_to_original",
              "The updated '" + model.name + "' is equals to the current one.");

        } else {

          success.run();
        }
      }
    });

  }

  /**
   * Update a JSON model to the defined on the DB and finish with an OK.
   *
   * @param vertx    event bus to use.
   * @param value    of the model to update.
   * @param model    context of the model to update.
   * @param searcher the function used to obtain a model from an identifier.
   * @param updater  the function used to store the updated model.
   * @param context  of the request.
   *
   * @param <T>      type of model to update.
   * @param <I>      type of the model identifier.
   */
  static public <T extends Model & Updateable<T>, I> void updateModel(@NotNull final Vertx vertx,
      final JsonObject value, @NotNull final ModelContext<T, I> model,
      @NotNull final BiConsumer<I, Handler<AsyncResult<T>>> searcher,
      @NotNull final BiConsumer<T, Handler<AsyncResult<Void>>> updater, @NotNull final ServiceContext context) {

    updateModelChain(vertx, value, model, searcher, updater, context,
        () -> ServiceResponseHandlers.responseOk(context.resultHandler, model.value));

  }

  /**
   * Retrieve a field defined into a model.
   *
   * @param model    context of the model to retrieve.
   * @param searcher the function used to obtain a model from an identifier.
   * @param getField return the field value associated to a model.
   * @param context  of the request.
   *
   * @param <T>      type of model that contains the fields.
   * @param <E>      type of the field.
   * @param <I>      type of the identifier.
   */
  static public <T extends Model, E extends Model, I> void retrieveModelField(@NotNull final ModelContext<T, I> model,
      @NotNull final BiConsumer<I, Handler<AsyncResult<T>>> searcher, @NotNull final Function<T, List<E>> getField,
      @NotNull final ServiceContext context) {

    retrieveModelChain(model, searcher, context, () -> {

      final List<E> field = getField.apply(model.target);
      JsonArray array = null;
      if (field == null) {

        array = new JsonArray();

      } else {

        array = Model.toJsonArray(field);
      }

      Logger.trace("For {} retrieve {}.\n{}", model, field, context);
      ServiceResponseHandlers.responseOk(context.resultHandler, array);

    });

  }

  /**
   * Retrieve an element of a field defined into a model and return the element.
   *
   * @param element       context of the field to retrieve.
   * @param searcher      the function used to obtain a model from an identifier.
   * @param getField      return the field value associated to a model.
   * @param searchElement return the index of the element that has the specified
   *                      identifier.
   * @param context       of the request.
   *
   * @param <T>           type of model that contains the fields.
   * @param <IT>          type of the model identifier.
   * @param <E>           type of the field.
   * @param <IE>          type of the field identifier.
   */
  static public <T extends Model, E extends Model, IT, IE> void retrieveModelFieldElement(
      @NotNull final ModelFieldContext<T, IT, E, IE> element,
      @NotNull final BiConsumer<IT, Handler<AsyncResult<T>>> searcher, @NotNull final Function<T, List<E>> getField,
      @NotNull final BiFunction<List<E>, IE, Integer> searchElement, @NotNull final ServiceContext context) {

    retrieveModelFieldElementChain(element, searcher, getField, searchElement, context,
        () -> ServiceResponseHandlers.responseOk(context.resultHandler, element.target));

  }

  /**
   * Retrieve an element of a field defined into a model.
   *
   * @param element       context of the field to retrieve.
   * @param searcher      the function used to obtain a model from an identifier.
   * @param getField      return the field value associated to a model.
   * @param searchElement return the index of the element that has the specified
   *                      identifier.
   * @param context       of the request.
   * @param success       function to call if can retrieve the element.
   *
   * @param <T>           type of model that contains the fields.
   * @param <IT>          type of the model identifier.
   * @param <E>           type of the field.
   * @param <IE>          type of the field identifier.
   */
  @SuppressWarnings("unchecked")
  static public <T extends Model, E extends Model, IT, IE> void retrieveModelFieldElementChain(
      @NotNull final ModelFieldContext<T, IT, E, IE> element,
      @NotNull final BiConsumer<IT, Handler<AsyncResult<T>>> searcher, @NotNull final Function<T, List<E>> getField,
      @NotNull final BiFunction<List<E>, IE, Integer> searchElement, @NotNull final ServiceContext context,
      @NotNull final Runnable success) {

    retrieveModelChain(element.model, searcher, context, () -> {

      element.model.value = (T) Model.fromJsonObject(element.model.target.toJsonObject(),
          element.model.target.getClass());
      element.field = getField.apply(element.model.value);
      if (element.field != null) {

        element.index = searchElement.apply(element.field, element.id);
        if (element.index > -1) {

          element.target = element.field.get(element.index);
          Logger.trace("Retrieve {} from {}.\n{}", element, element.model, context);
          success.run();
          return;
        }
      }

      Logger.trace("Not found {} in {}.\n{}", element, element.model, context);
      ServiceResponseHandlers.responseWithErrorMessage(context.resultHandler, Status.NOT_FOUND,
          "not_found_" + element.model.name + "_" + element.name,
          "On '" + element.model + "' does not found '" + element + "' .");

    });

  }

  /**
   * Merge an element of a field defined into a model.
   *
   * @param vertx            event bus to use.
   * @param valueToMerge     to merge the model field element.
   * @param element          to merge.
   * @param searcher         the function used to obtain a model from an
   *                         identifier.
   * @param getField         return the field value associated to a model.
   * @param searchElement    return the index of the element that has the
   *                         specified identifier.
   * @param storerMergeModel the function to merge the model.
   * @param context          of the request.
   *
   * @param <T>              type of model that contains the fields.
   * @param <IT>             type of the model identifier.
   * @param <E>              type of the field.
   * @param <IE>             type of the field identifier.
   */
  static public <T extends Model & Updateable<T>, IT, E extends Model & Mergeable<E>, IE> void mergeModelFieldElement(
      @NotNull final Vertx vertx, final JsonObject valueToMerge, @NotNull final ModelFieldContext<T, IT, E, IE> element,
      @NotNull final BiConsumer<IT, Handler<AsyncResult<T>>> searcher, @NotNull final Function<T, List<E>> getField,
      @NotNull final BiFunction<List<E>, IE, Integer> searchElement,
      final BiConsumer<T, Handler<AsyncResult<Void>>> storerMergeModel, final ServiceContext context) {

    mergeModelFieldElementChain(vertx, valueToMerge, element, searcher, getField, searchElement, storerMergeModel,
        context, () -> ServiceResponseHandlers.responseOk(context.resultHandler, element.value));

  }

  /**
   * Merge an element of a field defined into a model.
   *
   * @param vertx             event bus to use.
   * @param valueToMerge      to merge the model field element.
   * @param element           to merge.
   * @param searcher          the function used to obtain a model from an
   *                          identifier.
   * @param getField          return the field value associated to a model.
   * @param searchElement     return the index of the element that has the
   *                          specified identifier.
   * @param storerMergedModel the function to store the updated model.
   * @param context           of the request.
   * @param success           to inform to the upgrade value
   *
   * @param <T>               type of model that contains the fields.
   * @param <IT>              type of the model identifier.
   * @param <E>               type of the field.
   * @param <IE>              type of the field identifier.
   */
  static public <T extends Model & Updateable<T>, IT, E extends Model & Mergeable<E>, IE> void mergeModelFieldElementChain(
      @NotNull final Vertx vertx, final JsonObject valueToMerge, @NotNull final ModelFieldContext<T, IT, E, IE> element,
      @NotNull final BiConsumer<IT, Handler<AsyncResult<T>>> searcher, @NotNull final Function<T, List<E>> getField,
      @NotNull final BiFunction<List<E>, IE, Integer> searchElement,
      final BiConsumer<T, Handler<AsyncResult<Void>>> storerMergedModel, final ServiceContext context,
      @NotNull final Runnable success) {

    changeModelFieldElementBeforeChain(valueToMerge, element, searcher, getField, searchElement, context,
        () -> merge(vertx, element, context,
            () -> changeModelFieldElementAfterChain(vertx, element, getField, storerMergedModel, context, success)));

  }

  /**
   * Update an element of a field defined into a model.
   *
   * @param vertx              event bus to use.
   * @param valueToUpdate      to update the model field element.
   * @param element            to update.
   * @param searcher           the function used to obtain a model from an
   *                           identifier.
   * @param getField           return the field value associated to a model.
   * @param searchElement      return the index of the element that has the
   *                           specified identifier.
   * @param storerUpdatedModel the function to update the model.
   * @param context            of the request.
   * @param success            to inform to the upgrade value
   *
   * @param <T>                type of model that contains the fields.
   * @param <IT>               type of the model identifier.
   * @param <E>                type of the field.
   * @param <IE>               type of the field identifier.
   */
  static public <T extends Model & Updateable<T>, IT, E extends Model & Updateable<E>, IE> void updateModelFieldElementChain(
      @NotNull final Vertx vertx, final JsonObject valueToUpdate,
      @NotNull final ModelFieldContext<T, IT, E, IE> element,
      @NotNull final BiConsumer<IT, Handler<AsyncResult<T>>> searcher, @NotNull final Function<T, List<E>> getField,
      @NotNull final BiFunction<List<E>, IE, Integer> searchElement,
      @NotNull final BiConsumer<T, Handler<AsyncResult<Void>>> storerUpdatedModel, final ServiceContext context,
      @NotNull final Runnable success) {

    changeModelFieldElementBeforeChain(valueToUpdate, element, searcher, getField, searchElement, context,
        () -> update(vertx, element, context,
            () -> changeModelFieldElementAfterChain(vertx, element, getField, storerUpdatedModel, context, success)));

  }

  /**
   * Update an element of a field defined into a model.
   *
   * @param vertx             event bus to use.
   * @param valueToUpdate     to update the model field element.
   * @param element           to update.
   * @param searcher          the function used to obtain a model from an
   *                          identifier.
   * @param getField          return the field value associated to a model.
   * @param searchElement     return the index of the element that has the
   *                          specified identifier.
   * @param storerUpdateModel the function to update the model.
   * @param context           of the request.
   *
   * @param <T>               type of model that contains the fields.
   * @param <IT>              type of the model identifier.
   * @param <E>               type of the field.
   * @param <IE>              type of the field identifier.
   */
  static public <T extends Model & Updateable<T>, IT, E extends Model & Updateable<E>, IE> void updateModelFieldElement(
      @NotNull final Vertx vertx, final JsonObject valueToUpdate,
      @NotNull final ModelFieldContext<T, IT, E, IE> element,
      @NotNull final BiConsumer<IT, Handler<AsyncResult<T>>> searcher, @NotNull final Function<T, List<E>> getField,
      @NotNull final BiFunction<List<E>, IE, Integer> searchElement,
      final BiConsumer<T, Handler<AsyncResult<Void>>> storerUpdateModel, final ServiceContext context) {

    updateModelFieldElementChain(vertx, valueToUpdate, element, searcher, getField, searchElement, storerUpdateModel,
        context, () -> ServiceResponseHandlers.responseOk(context.resultHandler, element.value));

  }

  /**
   * Change an element of a field defined into a model.
   *
   * @param value         to merge the model field element.
   * @param element       to merge.
   * @param searcher      the function used to obtain a model from an identifier.
   * @param getField      return the field value associated to a model.
   * @param searchElement return the index of the element that has the specified
   *                      identifier.
   * @param context       of the request.
   * @param success       to inform to the upgrade value
   *
   * @param <T>           type of model that contains the fields.
   * @param <IT>          type of the model identifier.
   * @param <E>           type of the field.
   * @param <IE>          type of the field identifier.
   */
  static public <T extends Model & Updateable<T>, IT, E extends Model, IE> void changeModelFieldElementBeforeChain(
      final JsonObject value, @NotNull final ModelFieldContext<T, IT, E, IE> element,
      @NotNull final BiConsumer<IT, Handler<AsyncResult<T>>> searcher, @NotNull final Function<T, List<E>> getField,
      @NotNull final BiFunction<List<E>, IE, Integer> searchElement, final ServiceContext context,
      @NotNull final Runnable success) {

    toModel(value, element, context,
        () -> retrieveModelFieldElementChain(element, searcher, getField, searchElement, context, success));

  }

  /**
   * Change an element of a field defined into a model.
   *
   * @param vertx              event bus to use.
   * @param element            to merge.
   * @param getField           return the field value associated to a model.
   * @param context            of the request.
   * @param storerChangedModel function to store the changed model.
   * @param success            to inform to the upgrade value
   *
   * @param <T>                type of model that contains the fields.
   * @param <IT>               type of the model identifier.
   * @param <E>                type of the field.
   * @param <IE>               type of the field identifier.
   */
  static public <T extends Model & Updateable<T>, IT, E extends Model, IE> void changeModelFieldElementAfterChain(
      final Vertx vertx, @NotNull final ModelFieldContext<T, IT, E, IE> element,
      @NotNull final Function<T, List<E>> getField, final BiConsumer<T, Handler<AsyncResult<Void>>> storerChangedModel,
      @NotNull final ServiceContext context, @NotNull final Runnable success) {

    element.model.source = Model.fromBuffer(element.model.target.toBuffer(), element.model.type);
    final var sourceField = getField.apply(element.model.source);
    sourceField.remove(element.index);
    sourceField.add(element.index, element.value);
    update(vertx, element.model, context, () -> updateModelChain(element.model, storerChangedModel, context, success));

  }

  /**
   * Delete an element of a field defined into a model.
   *
   * @param element            to delete.
   * @param searcher           the function used to obtain a model from an
   *                           identifier.
   * @param getField           return the field value associated to a model.
   * @param searchElement      return the index of the element that has the
   *                           specified identifier.
   * @param storerDeletedModel the function to store the deleted model.
   * @param context            of the request.
   * @param success            to inform to the upgrade value
   *
   * @param <T>                type of model that contains the fields.
   * @param <IT>               type of the model identifier.
   * @param <E>                type of the field.
   * @param <IE>               type of the field identifier.
   */
  static public <T extends Model, IT, E extends Model, IE> void deleteModelFieldElementChain(
      @NotNull final ModelFieldContext<T, IT, E, IE> element,
      @NotNull final BiConsumer<IT, Handler<AsyncResult<T>>> searcher, @NotNull final Function<T, List<E>> getField,
      @NotNull final BiFunction<List<E>, IE, Integer> searchElement,
      final BiConsumer<T, Handler<AsyncResult<Void>>> storerDeletedModel, final ServiceContext context,
      @NotNull final Runnable success) {

    retrieveModelFieldElementChain(element, searcher, getField, searchElement, context, () -> {

      element.field.remove(element.index);
      updateModelChain(element.model, storerDeletedModel, context, success);

    });

  }

  /**
   * Delete an element of a field defined into a model.
   *
   * @param element           to delete.
   * @param searcher          the function used to obtain a model from an
   *                          identifier.
   * @param getField          return the field value associated to a model.
   * @param searchElement     return the index of the element that has the
   *                          specified identifier.
   * @param storerDeleteModel the function to delete the model.
   * @param context           of the request.
   *
   * @param <T>               type of model that contains the fields.
   * @param <IT>              type of the model identifier.
   * @param <E>               type of the field.
   * @param <IE>              type of the field identifier.
   */
  static public <T extends Model, IT, E extends Model, IE> void deleteModelFieldElement(
      @NotNull final ModelFieldContext<T, IT, E, IE> element,
      @NotNull final BiConsumer<IT, Handler<AsyncResult<T>>> searcher, @NotNull final Function<T, List<E>> getField,
      @NotNull final BiFunction<List<E>, IE, Integer> searchElement,
      final BiConsumer<T, Handler<AsyncResult<Void>>> storerDeleteModel, final ServiceContext context) {

    deleteModelFieldElementChain(element, searcher, getField, searchElement, storerDeleteModel, context,
        () -> ServiceResponseHandlers.responseOk(context.resultHandler));

  }

  /**
   * Create an element of a field defined into a model.
   *
   * @param vertx             event bus to use.
   * @param valueToCreate     to create the model field element.
   * @param element           to create.
   * @param searcher          the function used to obtain a model from an
   *                          identifier.
   * @param getField          return the field value associated to a model.
   * @param setField          change the value for the field.
   * @param storerCreateModel the function to create the model.
   * @param context           of the request.
   *
   * @param <T>               type of model that contains the fields.
   * @param <IT>              type of the model identifier.
   * @param <E>               type of the field.
   * @param <IE>              type of the field identifier.
   */
  static public <T extends Model & Updateable<T>, IT, E extends Model & Validable, IE> void createModelFieldElement(
      @NotNull final Vertx vertx, final JsonObject valueToCreate,
      @NotNull final ModelFieldContext<T, IT, E, IE> element,
      @NotNull final BiConsumer<IT, Handler<AsyncResult<T>>> searcher, @NotNull final Function<T, List<E>> getField,
      @NotNull final BiConsumer<T, List<E>> setField, final BiConsumer<T, Handler<AsyncResult<Void>>> storerCreateModel,
      final ServiceContext context) {

    createModelFieldElementChain(vertx, valueToCreate, element, searcher, getField, setField, storerCreateModel,
        context, () -> ServiceResponseHandlers.responseOk(context.resultHandler, element.value));

  }

  /**
   * Create an element of a field defined into a model.
   *
   * @param vertx              event bus to use.
   * @param valueToCreate      to create the model field element.
   * @param element            to create.
   * @param searcher           the function used to obtain a model from an
   *                           identifier.
   * @param getField           return the field value associated to a model.
   * @param setField           change the value for the field.
   * @param storerCreatedModel the function to store the updated model.
   * @param context            of the request.
   * @param success            to inform to the upgrade value
   *
   * @param <T>                type of model that contains the fields.
   * @param <IT>               type of the model identifier.
   * @param <E>                type of the field.
   * @param <IE>               type of the field identifier.
   */
  @SuppressWarnings("unchecked")
  static public <T extends Model & Updateable<T>, IT, E extends Model & Validable, IE> void createModelFieldElementChain(
      @NotNull final Vertx vertx, final JsonObject valueToCreate,
      @NotNull final ModelFieldContext<T, IT, E, IE> element,
      @NotNull final BiConsumer<IT, Handler<AsyncResult<T>>> searcher, @NotNull final Function<T, List<E>> getField,
      @NotNull final BiConsumer<T, List<E>> setField,
      final BiConsumer<T, Handler<AsyncResult<Void>>> storerCreatedModel, final ServiceContext context,
      @NotNull final Runnable success) {

    toModel(valueToCreate, element, context, () -> {

      retrieveModelChain(element.model, searcher, context, () -> {

        element.model.source = (T) Model.fromJsonObject(element.model.target.toJsonObject(),
            element.model.target.getClass());
        element.field = getField.apply(element.model.source);
        if (element.field == null) {

          element.field = new ArrayList<E>();
          setField.accept(element.model.source, element.field);
        }

        element.index = element.field.size();
        element.field.add(element.source);
        update(vertx, element.model, context, () -> {

          element.value = getField.apply(element.model.value).get(element.index);
          updateModelChain(element.model, storerCreatedModel, context, success);

        });

      });
    });

  }

  /**
   * Return the function that can be used to search a value by its identifier.
   *
   * @param idComparator predicate to check if the specified model has the
   *                     specified id.
   *
   * @return the index of the element on the list or {@code -1} if not found.
   *
   * @param <E> type of the elements.
   * @param <I> type of the identifier.
   */
  static public <E, I> BiFunction<List<E>, I, Integer> searchElementById(final BiPredicate<E, I> idComparator) {

    return (models, id) -> {

      if (models != null) {

        final var max = models.size();
        for (var i = 0; i < max; i++) {

          final var model = models.get(i);
          if (idComparator.test(model, id)) {

            return i;
          }
        }
      }

      return -1;
    };

  }

  /**
   * Return the function that can be used to search a value by its index.
   *
   * @return the index of the element on the list or {@code -1} if not found.
   *
   * @param <E> type of the elements.
   */
  static public <E> BiFunction<List<E>, Integer, Integer> searchElementByIndex() {

    return (models, id) -> {

      if (models != null && id != null && id > -1 && id < models.size()) {

        return id;

      } else {
        // Out of range.
        return -1;
      }
    };

  }

  /**
   * Retrieve a page of models.
   *
   * @param offset   index of the first model to return.
   * @param limit    number maximum of models to return.
   * @param searcher function to obtain the page.
   * @param context  of the request.
   */
  static public void retrieveModelsPage(final int offset, final int limit,
      @NotNull final BiConsumer<ModelsPageContext, Promise<JsonObject>> searcher,
      @NotNull final ServiceContext context) {

    retrieveModelsPageChain(offset, limit, searcher, context,
        found -> ServiceResponseHandlers.responseOk(context.resultHandler, found));

  }

  /**
   * Retrieve a page of models.
   *
   * @param offset   index of the first model to return.
   * @param limit    number maximum of models to return.
   * @param searcher function to obtain the page.
   * @param context  of the request.
   * @param success  to inform to the page.
   */
  static public void retrieveModelsPageChain(final int offset, final int limit,
      @NotNull final BiConsumer<ModelsPageContext, Promise<JsonObject>> searcher, @NotNull final ServiceContext context,
      final Consumer<JsonObject> success) {

    final var page = new ModelsPageContext();
    page.offset = offset;
    page.limit = limit;
    final Promise<JsonObject> promise = Promise.promise();
    try {

      searcher.accept(page, promise);

    } catch (final Throwable cause) {

      promise.fail(cause);
    }
    promise.future().onComplete(search -> {

      if (search.failed()) {

        final var cause = search.cause();
        Logger.trace(cause, "Cannot obtain the models.\n{}\n{}", page, context);
        ServiceResponseHandlers.responseFailedWith(context.resultHandler, Status.BAD_REQUEST, cause);

      } else {

        final var found = search.result();
        success.accept(found);

      }

    });

  }

}
