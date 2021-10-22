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

package eu.internetofus.common.components;

import eu.internetofus.common.model.ErrorMessage;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.impl.ClusterSerializable;
import io.vertx.ext.web.RoutingContext;
import java.util.HashMap;
import java.util.Map;
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
  protected Buffer toBuffer(final ClusterSerializable value) {

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
  protected String getIdFrom(final RoutingContext ctx, final String prefix, final String... params) {

    final var id = new StringBuilder();
    id.append(prefix);
    for (final var key : params) {

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
  public Handler<RoutingContext> createGetHandler(final String prefix, final ClusterSerializable defaultValue,
      final String... params) {

    return ctx -> {

      final var id = this.getIdFrom(ctx, prefix, params);
      final var response = ctx.response();
      final var value = this.values.get(id);
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
  public Handler<RoutingContext> createSetHandler(final String prefix, final String... params) {

    return ctx -> {

      final var response = ctx.response();
      try {

        final var id = this.getIdFrom(ctx, prefix, params);
        final var body = ctx.getBody();
        final var value = (ClusterSerializable) Json.decodeValue(body);
        this.values.put(id, value);
        response.setStatusCode(Status.CREATED.getStatusCode());
        response.end(this.toBuffer(value));

      } catch (final Throwable t) {

        response.setStatusCode(Status.BAD_REQUEST.getStatusCode());
        response.end(new ErrorMessage("bad_value", "Does not exist a valid value on the body.").toBuffer());
      }

    };
  }

  /**
   * Create a handler to delete a value.
   *
   * @param prefix to apply to the identifier.
   * @param params name of the path elements with the identifiers.
   *
   * @return the handler to delete a value.
   */
  public Handler<RoutingContext> createDeleteHandler(final String prefix, final String... params) {

    return ctx -> {

      final var id = this.getIdFrom(ctx, prefix, params);
      final var response = ctx.response();
      final var value = this.values.remove(id);
      if (value != null) {

        response.setStatusCode(Status.NO_CONTENT.getStatusCode());
        response.end();

      } else {

        response.setStatusCode(Status.NOT_FOUND.getStatusCode());
        response.end(new ErrorMessage("bad_id", "Does not exist any value associated to the identifier.").toBuffer());
      }

    };
  }

}
