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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.tinylog.Logger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.ext.web.client.HttpResponse;

/**
 * Define a data model.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@JsonInclude(Include.NON_EMPTY)
public interface Model {

  /**
   * Obtain the model form the string representation.
   *
   * @param value to obtain the model.
   * @param type  of model to obtain
   * @param <T>   to obtain
   *
   * @return the model coded on JSON or {@code null} if can not obtain it.
   */
  public static <T extends Model> T fromString(final String value, final Class<T> type) {

    try {

      return Json.decodeValue(value, type);

    } catch (final Throwable throwable) {

      Logger.trace(throwable);
      return null;
    }
  }

  /**
   * Obtain the model associated to a {@link JsonObject}.
   *
   * @param value object to obtain the model.
   * @param type  of model to obtain
   *
   * @param <T>   to obtain
   *
   * @return the model defined on the object or {@code null} if can not obtain it.
   */
  public static <T extends Model> T fromJsonObject(final JsonObject value, final Class<T> type) {

    if (value == null) {

      Logger.trace("CANNOT obtain model from a 'null' value.");

    } else if (type == null) {

      Logger.trace("CANNOT obtain model from a 'null' type.");

    } else {

      try {

        return Json.decodeValue(value.toBuffer(), type);

      } catch (final Throwable throwable) {

        Logger.trace(throwable);
      }
    }
    // No model found
    return null;
  }

  /**
   * Convert a model to JSON string.
   *
   * @return the string representation of this model in JSON or {@code null} if it can not convert it.
   */
  default public String toJsonString() {

    try {

      return Json.encode(this);

    } catch (final Throwable throwable) {

      Logger.trace(throwable);
      return null;
    }

  }

  /**
   * Convert a model to a {@link JsonObject}.
   *
   * @return the object of the model or {@code null} if can not convert it.
   */
  default public JsonObject toJsonObject() {

    try {

      return new JsonObject(this.toBuffer());

    } catch (final Throwable throwable) {

      Logger.trace(throwable);
      return null;
    }

  }

  /**
   * Convert this model to a buffer.
   *
   * @return the buffer that contains the JSON encoding of this model or {@code null} if can not convert it.
   */
  default public Buffer toBuffer() {

    try {

      return Json.encodeToBuffer(this);

    } catch (final Throwable throwable) {

      Logger.trace(throwable);
      return null;
    }
  }

  /**
   * Obtain the model associated to a {@link Buffer}.
   *
   * @param buffer to obtain the model.
   * @param type   of model to obtain
   * @param <T>    to obtain
   *
   * @return the model defined on the buffer or {@code null} if can not obtain it.
   */
  public static <T extends Model> T fromBuffer(final Buffer buffer, final Class<T> type) {

    try {

      return Json.decodeValue(buffer, type);

    } catch (final Throwable throwable) {

      Logger.trace(throwable);
      return null;
    }
  }

  /**
   * Obtain the model associated to a resource.
   *
   * @param resourceName where the model to obtain is defined In JSON.
   * @param type         of model to obtain
   * @param <T>          model to obtain
   *
   * @return the model defined on the resource or {@code null} if can not obtain it.
   */
  public static <T extends Model> T loadFromResource(final String resourceName, final Class<T> type) {

    try {

      final var stream = type.getClassLoader().getResourceAsStream(resourceName);
      final var encoded = IOUtils.toString(stream);
      return fromString(encoded, type);

    } catch (final Throwable throwable) {

      Logger.trace(throwable);
      return null;
    }

  }

  /**
   * Obtain the model associated to a {@link HttpResponse}.
   *
   * @param response to obtain the model.
   * @param type     of model to obtain
   * @param <T>      to obtain
   *
   * @return the model defined on the response or {@code null} if can not obtain it.
   */
  public static <T extends Model> T fromResponse(final HttpResponse<Buffer> response, final Class<T> type) {

    try {

      return fromBuffer(response.bodyAsBuffer(), type);

    } catch (final Throwable throwable) {

      Logger.trace(throwable);
      return null;
    }
  }

  /**
   * Return the array represented by a list of models.
   *
   * @param models to encode to an array.
   *
   * @param <T>    to obtain
   *
   * @return the array that represents the models.
   */
  public static <T extends Model> JsonArray toJsonArray(final Iterable<T> models) {

    if (models == null) {

      return null;

    } else {

      final var array = new JsonArray();
      for (final T model : models) {

        final var object = model.toJsonObject();
        array.add(object);

      }
      return array;
    }

  }

  /**
   * Return the model defined on the array.
   *
   * @param array to get the models.
   * @param type  of model to obtain
   * @param <T>   to obtain
   *
   * @return the models of the array, or {@code null} if it can not obtain all the models.
   */
  public static <T extends Model> List<T> fromJsonArray(final JsonArray array, final Class<T> type) {

    if (array == null) {

      return null;

    } else {

      final List<T> models = new ArrayList<>();
      for (var i = 0; i < array.size(); i++) {

        try {

          final var value = array.getJsonObject(i);
          final var model = fromJsonObject(value, type);
          if (model == null) {

            return null;
          }
          models.add(model);

        } catch (final Throwable throwable) {

          Logger.trace(throwable);
          return null;
        }

      }
      return models;
    }

  }

  /**
   * Return the model defined on the buffer.
   *
   * @param buffer to get the models.
   * @param type   of model to obtain
   * @param <T>    to obtain
   *
   * @return the models of the array, or {@code null} if it can not obtain all the models.
   */
  public static <T extends Model> List<T> fromJsonArray(final Buffer buffer, final Class<T> type) {

    if (buffer != null) {

      try {

        final var decoded = Json.decodeValue(buffer);
        if (decoded instanceof JsonArray) {

          final var array = (JsonArray) decoded;
          return fromJsonArray(array, type);

        }

      } catch (final Throwable throwable) {

        Logger.trace(throwable);
      }
    }

    return null;

  }

  /**
   * Convert a model to a {@link JsonObject} with all the {@code null} and empty values.
   *
   * @return the object of the model or {@code null} if can not convert it.
   *
   * @see ModelForJsonObjectWithEmptyValues
   */
  default public JsonObject toJsonObjectWithEmptyValues() {

    try {

      final var mapper = DatabindCodec.mapper().copy();
      mapper.addMixIn(this.getClass(), ModelForJsonObjectWithEmptyValues.class);
      final var json = mapper.writeValueAsString(this);
      return new JsonObject(json);

    } catch (final Throwable throwable) {

      Logger.trace(throwable);
      return null;
    }

  }
}
