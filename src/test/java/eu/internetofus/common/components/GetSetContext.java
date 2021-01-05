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

import java.util.HashMap;
import java.util.Map;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.impl.ClusterSerializable;
import io.vertx.ext.web.RoutingContext;
import javax.ws.rs.core.Response.Status;

/**
 * Generic component to provide get/set operations.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class GetSetContext {

  /**
   * The values stored on the context.
   */
  protected Map<String, ClusterSerializable> values;

  /**
   * Create the context.
   */
  public GetSetContext() {

    this.values = new HashMap<>();

  }

  /**
   * Return the buffer associated to a value.
   *
   * @param value to return the buffer.
   *
   * @return the buffer that contains the value.
   */
  protected Buffer toBuffer(ClusterSerializable value) {

    if (value instanceof JsonObject) {

      return ((JsonObject) value).toBuffer();

    } else {

      return ((JsonArray) value).toBuffer();
    }

  }

  /**
   * Return the identifier associated to a query.
   *
   * @param ctx    context to extract the path identifiers.
   * @param prefix to apply to the identifier.
   * @param params name of the path elements with the identifiers.
   *
   * @return the identifier of the component.
   */
  protected String getIdFrom(RoutingContext ctx, String prefix, String... params) {

    var id = new StringBuilder();
    id.append(prefix);
    for (var key : params) {

      id.append("_");
      id.append(ctx.pathParam(key));

    }

    return id.toString();
  }

  /**
   * Create a handler to retrieve a value.
   *
   * @param prefix       to apply to the identifier.
   * @param defaultValue to return if is not defined or {@code null} to fail if
   *                     not found.
   * @param params       name of the path elements with the identifiers.
   *
   * @return the handler to get a value.
   */
  public Handler<RoutingContext> createGetHandler(String prefix, ClusterSerializable defaultValue, String... params) {

    return ctx -> {

      var id = this.getIdFrom(ctx, prefix, params);
      final var response = ctx.response();
      var value = this.values.get(id);
      if (value != null) {

        response.setStatusCode(Status.OK.getStatusCode());
        response.end(this.toBuffer(value));

      } else if (defaultValue != null) {

        response.setStatusCode(Status.OK.getStatusCode());
        response.end(this.toBuffer(defaultValue));
      } else {

        response.setStatusCode(Status.NOT_FOUND.getStatusCode());
        response.end(new ErrorMessage("bad_id", "Does not exist any value associated to the identifier.").toBuffer());
      }

    };
  }

  /**
   * Create a handler to store a value.
   *
   * @param prefix to apply to the identifier.
   * @param params name of the path elements with the identifiers.
   *
   * @return the handler to set a value.
   */
  public Handler<RoutingContext> createSetHandler(String prefix, String... params) {

    return ctx -> {

      var id = this.getIdFrom(ctx, prefix, params);
      final var response = ctx.response();
      var body = Json.decodeValue(ctx.getBody());
      if (body instanceof ClusterSerializable) {

        var value = (ClusterSerializable) body;
        this.values.put(id, value);
        response.setStatusCode(Status.CREATED.getStatusCode());
        response.end(this.toBuffer(value));

      } else {

        response.setStatusCode(Status.BAD_REQUEST.getStatusCode());
        response.end(new ErrorMessage("bad_value", "Does not exist a valid value on the body.").toBuffer());
      }

    };
  }

}
