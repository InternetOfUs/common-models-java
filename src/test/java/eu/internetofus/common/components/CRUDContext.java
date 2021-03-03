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

package eu.internetofus.common.components;

import eu.internetofus.common.TimeManager;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiPredicate;
import javax.ws.rs.core.Response.Status;

/**
 * Generic component to provide CRUD operations.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class CRUDContext {

  /**
   * The values stored on the context.
   */
  public Map<String, JsonObject> values;

  /**
   * The type of models that can be accepted.
   */
  public Class<?> modelType;

  /**
   * The name of the property that contains the identifier of the values.
   */
  private final String idName;

  /**
   * The name of the property to store the models of the page.
   */
  private final String valuesName;

  /**
   * Create the context.
   *
   * @param idName      name of the filed of the value that contains the
   *                    identifier.
   * @param valuesNames name of the page field that contains the values to return.
   * @param modelType   type of model that are valid by the context.
   */
  public CRUDContext(final String idName, final String valuesNames, final Class<?> modelType) {

    this.values = new TreeMap<>();
    this.idName = idName;
    this.valuesName = valuesNames;
    this.modelType = modelType;

  }

  /**
   * Handler for the a value.
   */
  @FunctionalInterface
  public static interface ValueHandler {

    /**
     * Called when has to handle a value.
     *
     * @param ctx   router context to reply.
     * @param id    identifier of the value.
     * @param value defined on the context.
     */
    void handle(RoutingContext ctx, String id, JsonObject value);
  }

  /**
   * Handler for a value.
   *
   * @param handler to convert.
   *
   * @return the handler to manage an element.
   */
  public Handler<RoutingContext> toRoutingContextHandler(final ValueHandler handler) {

    return ctx -> {

      final var id = ctx.pathParam("id");
      final var value = this.values.get(id);
      if (value == null) {

        final var response = ctx.response();
        response.setStatusCode(Status.NOT_FOUND.getStatusCode());
        response.end(new ErrorMessage("undefined_value", "The identifier is not associated to any value.").toBuffer());

      } else {

        handler.handle(ctx, id, value);
      }

    };
  }

  /**
   * Return the handler to get the value with the identifier defined on the path.
   *
   * @return the handler to obtain a value by its identifier.
   */
  public Handler<RoutingContext> getHandler() {

    return this.toRoutingContextHandler((ctx, id, value) -> {

      final var response = ctx.response();
      response.setStatusCode(Status.OK.getStatusCode());
      response.end(value.toBuffer());

    });

  }

  /**
   * Return the integer value defined on a query parameter.
   *
   * @param ctx          routing context to extract the query parameters.
   * @param key          identifier of the query parameter.
   * @param defaultValue value to return if is not defined or valid.
   *
   * @return the integer defined in the query parameters, or the
   *         {@code defaultValue} if it is not defined or it is not a valid
   *         integer.
   *
   */
  public int queryIntParam(final RoutingContext ctx, final String key, final int defaultValue) {

    try {

      final var param = ctx.queryParams().get(key);
      return Integer.parseInt(param);

    } catch (final Throwable ignored) {

      return defaultValue;
    }
  }

  /**
   * Return the handler to get all the value with the values that pass the filter.
   *
   * @param filter to apply over the element to return.
   *
   * @return the handler to obtain a value by its identifier.
   */
  public Handler<RoutingContext> getPageHandler(final BiPredicate<RoutingContext, JsonObject> filter) {

    return ctx -> {

      final var response = ctx.response();
      final var offset = this.queryIntParam(ctx, "offset", 0);
      final var limit = this.queryIntParam(ctx, "limit", 10);
      var array = new JsonArray();
      final var max = this.values.size();
      final var iter = this.values.keySet().iterator();
      for (var i = 0; i < offset && iter.hasNext();) {

        final var id = iter.next();
        final var value = this.values.get(id);
        if (filter == null || filter.test(ctx, value)) {

          i++;
        }

      }
      for (var i = 0; i < limit && iter.hasNext();) {

        final var id = iter.next();
        final var value = this.values.get(id);
        if (filter == null || filter.test(ctx, value)) {
          array = array.add(value);
          i++;
        }

      }
      if (array.size() == 0) {

        array = null;
      }
      final var page = new JsonObject().put("offset", offset).put("total", max).put(this.valuesName, array);
      response.setStatusCode(Status.OK.getStatusCode());
      response.end(page.toBuffer());

    };

  }

  /**
   * Create a filter to filter the values requested by a query.
   *
   * @param paramNames names of the parameters to filter the values.
   *
   * @return the filter with the specified query parameters with the same value on
   *         the model.
   */
  public BiPredicate<RoutingContext, JsonObject> createValueFilter(final String... paramNames) {

    return (ctx, value) -> {

      if (paramNames != null) {

        for (final var paramName : paramNames) {

          final var paramValue = ctx.queryParams().get(paramName);
          if (paramValue != null && !paramValue.equals(value.getString(paramName))) {

            return false;

          }
        }
      }

      return true;
    };

  }

  /**
   * Return the handler to get all the value with the values defined on the path.
   *
   * @param paramNames names of the parameters to filter the values.
   *
   * @return the handler to obtain a value by its identifier.
   */
  public Handler<RoutingContext> getPageHandler(final String... paramNames) {

    return this.getPageHandler(this.createValueFilter(paramNames));
  }

  /**
   * Return the handler to delete a value for an identifier defined on the path.
   *
   * @return the handler to delete value by its identifier.
   */
  public Handler<RoutingContext> deleteHandler() {

    return this.toRoutingContextHandler((ctx, id, value) -> {

      this.values.remove(id);
      final var response = ctx.response();
      response.setStatusCode(Status.NO_CONTENT.getStatusCode());
      response.end();

    });

  }

  /**
   * Obtain the new value to use.
   *
   * @param ctx routing context to get value form the body.
   *
   * @return the value defined on the body or {@code null} if not value is
   *         defined.
   */
  public JsonObject getNewValueFrom(final RoutingContext ctx) {

    return this.getBodyOf(ctx, this.modelType);

  }

  /**
   * Obtain the body of the specified type.
   *
   * @param ctx  routing context to get value form the body.
   * @param type of the body.
   *
   * @return the value defined on the body or {@code null} if the body is not of
   *         the specified type.
   */
  public JsonObject getBodyOf(final RoutingContext ctx, final Class<?> type) {

    try {

      final var newValue = ctx.getBodyAsJson();
      if (newValue != null) {

        final var model = Json.decodeValue(newValue.toBuffer(), type);
        if (model != null) {

          return newValue;

        }
      }

    } catch (final Throwable ignored) {

    }

    final var response = ctx.response();
    response.setStatusCode(Status.BAD_REQUEST.getStatusCode());
    response.end(new ErrorMessage("bad_value", "The value is not a " + type).toBuffer());
    return null;

  }

  /**
   * Return the handler to replace (put) a value for an identifier defined on the
   * path.
   *
   * @return the handler to replace value by its identifier.
   */
  public Handler<RoutingContext> postHandler() {

    return ctx -> {

      var newValue = this.getNewValueFrom(ctx);
      if (newValue != null) {

        final var response = ctx.response();
        var id = newValue.getString(this.idName);
        if (id == null) {

          var index = this.values.size() + 1;
          while (this.values.containsKey(String.valueOf(index))) {

            index++;
          }

          id = String.valueOf(index);
          newValue = newValue.put(this.idName, id);

        } else if (this.values.containsKey(id)) {

          response.setStatusCode(Status.BAD_REQUEST.getStatusCode());
          response.end(new ErrorMessage("bad_id", "The identifier is already defined").toBuffer());
          return;
        }

        if (CreateUpdateTsDetails.class.isAssignableFrom(this.modelType)) {

          final var now = TimeManager.now();
          newValue = newValue.put("_creationTs", now);
          newValue = newValue.put("_lastUpdateTs", now);
        }

        this.values.put(id, newValue);
        response.setStatusCode(Status.CREATED.getStatusCode());
        response.end(newValue.toBuffer());
      }

    };

  }

  /**
   * Return the handler to replace (put) a value for an identifier defined on the
   * path.
   *
   * @return the handler to replace value by its identifier.
   */
  public Handler<RoutingContext> putHandler() {

    return this.toRoutingContextHandler((ctx, id, value) -> {

      var newValue = this.getNewValueFrom(ctx);
      if (newValue != null) {

        final var response = ctx.response();
        newValue = newValue.put(this.idName, id);
        if (CreateUpdateTsDetails.class.isAssignableFrom(this.modelType)) {

          final var now = TimeManager.now();
          newValue = newValue.put("_creationTs", value.getValue("_creationTs"));
          newValue = newValue.put("_lastUpdateTs", now);
        }

        this.values.put(id, newValue);
        response.setStatusCode(Status.OK.getStatusCode());
        response.end(newValue.toBuffer());

      }

    });

  }

  /**
   * Return the handler to merge (patch) a value for an identifier with the
   * current one.
   *
   * @return the handler to merge value by its identifier.
   */
  public Handler<RoutingContext> patchHandler() {

    return this.toRoutingContextHandler((ctx, id, value) -> {

      final var newValue = this.getNewValueFrom(ctx);
      if (newValue != null) {

        var mergedValue = Merges.mergeJsonObjects(value, newValue);
        mergedValue = mergedValue.put(this.idName, id);
        if (CreateUpdateTsDetails.class.isAssignableFrom(this.modelType)) {

          final var now = TimeManager.now();
          mergedValue = mergedValue.put("_creationTs", value.getValue("_creationTs"));
          mergedValue = mergedValue.put("_lastUpdateTs", now);
        }
        final var response = ctx.response();
        this.values.put(id, mergedValue);
        response.setStatusCode(Status.OK.getStatusCode());
        response.end(mergedValue.toBuffer());

      }

    });

  }

}
