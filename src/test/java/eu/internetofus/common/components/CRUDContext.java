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

import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import java.util.Map;
import java.util.TreeMap;
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
  private String idName;

  /**
   * The name of the property to store the models of the page.
   */
  private String valuesName;

  /**
   * Create the context.
   *
   * @param idName      name of the filed of the value that contains the
   *                    identifier.
   * @param valuesNames name of the page field that contains the values to return.
   * @param modelType   type of model that are valid by the context.
   */
  public CRUDContext(String idName, String valuesNames, Class<?> modelType) {

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
      var value = this.values.get(id);
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
  public int queryIntParam(RoutingContext ctx, String key, int defaultValue) {

    try {

      var param = ctx.queryParams().get(key);
      return Integer.parseInt(param);

    } catch (Throwable ignored) {

      return defaultValue;
    }
  }

  /**
   * Return the handler to get all the value with the identifier defined on the
   * path.
   *
   * @return the handler to obtain a value by its identifier.
   */
  public Handler<RoutingContext> getPageHandler() {

    return ctx -> {

      final var response = ctx.response();
      final var offset = this.queryIntParam(ctx, "offset", 0);
      final var limit = this.queryIntParam(ctx, "limit", 10);
      var array = new JsonArray();
      var max = this.values.size();
      var iter = this.values.keySet().iterator();
      for (int i = 0; i < offset && iter.hasNext(); i++) {

        iter.next();
      }
      for (int i = 0; i < limit && iter.hasNext(); i++) {

        var id = iter.next();
        var value = this.values.get(id);
        array = array.add(value);
      }
      if (array.size() == 0) {

        array = null;
      }
      JsonObject page = new JsonObject().put("offset", offset).put("total", max).put(this.valuesName, array);
      response.setStatusCode(Status.OK.getStatusCode());
      response.end(page.toBuffer());

    };

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
  public JsonObject getNewValueFrom(RoutingContext ctx) {

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
  public JsonObject getBodyOf(RoutingContext ctx, Class<?> type) {

    try {

      var newValue = ctx.getBodyAsJson();
      if (newValue != null) {

        var model = Json.decodeValue(newValue.toBuffer(), type);
        if (model != null) {

          return newValue;

        }
      }

    } catch (Throwable ignored) {

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

          int index = this.values.size() + 1;
          while (this.values.containsKey(String.valueOf(index))) {

            index++;
          }

          id = String.valueOf(index);
          newValue = newValue.put(this.idName, id);

        } else if (this.values.containsKey(id)) {

          response.setStatusCode(Status.BAD_REQUEST.getStatusCode());
          response.end(new ErrorMessage("bad_id", "The identiifer is already defined").toBuffer());
          return;
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

      var newValue = this.getNewValueFrom(ctx);
      if (newValue != null) {

        newValue = newValue.put(this.idName, id);
        final var response = ctx.response();
        for (var key : value.fieldNames()) {

          if (!newValue.containsKey(key)) {

            newValue = newValue.put(key, value.getValue(key));
          }

        }
        this.values.put(id, newValue);
        response.setStatusCode(Status.OK.getStatusCode());
        response.end(newValue.toBuffer());

      }

    });

  }

}
