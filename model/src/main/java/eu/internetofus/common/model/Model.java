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

package eu.internetofus.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.core.shareddata.ClusterSerializable;
import io.vertx.ext.web.client.HttpResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import org.tinylog.Logger;

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
  public static <T> T fromJsonObject(final JsonObject value, final Class<T> type) {

    if (value != null) {

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
   * Obtain the model associated to a future {@link JsonObject}.
   *
   * @param futureObject the future object of the model.
   * @param type         of model to obtain.
   *
   * @param <T>          type of model.
   *
   * @return the future model defined on the object.
   */
  public static <T extends Model> Future<T> fromFutureJsonObject(@NotNull final Future<JsonObject> futureObject,
      @NotNull final Class<T> type) {

    return futureObject.compose(object -> {

      if (object == null) {

        return Future.failedFuture("No object to get the value");

      } else {

        try {

          final var value = Json.decodeValue(object.toBuffer(), type);
          return Future.succeededFuture(value);

        } catch (final Throwable throwable) {

          return Future.failedFuture(throwable);
        }
      }

    });

  }

  /**
   * Obtain the model associated to a future {@link JsonArray}.
   *
   * @param futureArray the future array of the model.
   * @param type        of model to obtain.
   *
   * @param <T>         type of model.
   *
   * @return the future model defined on the array.
   */
  @SuppressWarnings("unchecked")
  public static <T> Future<List<T>> fromFutureJsonArray(@NotNull final Future<JsonArray> futureArray,
      @NotNull final Class<T> type) {

    return futureArray.compose(array -> {

      final var values = new ArrayList<T>();
      if (array != null) {

        try {

          final var max = array.size();
          for (var i = 0; i < max; i++) {

            final var element = array.getValue(i);
            if (type.isInstance(element) || element == null) {

              values.add((T) element);

            } else if (element instanceof ClusterSerializable) {

              final var buffer = Json.encodeToBuffer(element);
              final var value = Json.decodeValue(buffer, type);
              values.add(value);

            } else {

              return Future.failedFuture("The value '" + element + "' at '" + i + "' is not of the expected type '"
                  + type.getSimpleName() + "'.");

            }

          }

        } catch (final Throwable throwable) {

          return Future.failedFuture(throwable);
        }
      }

      return Future.succeededFuture(values);

    });

  }

  /**
   * Convert a model to JSON string.
   *
   * @return the string representation of this model in JSON or {@code null} if it
   *         can not convert it.
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
   * @return the buffer that contains the JSON encoding of this model or
   *         {@code null} if can not convert it.
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
   * @return the model defined on the resource or {@code null} if can not obtain
   *         it.
   */
  public static <T extends Model> T loadFromResource(final String resourceName, final Class<T> type) {

    try {

      final var stream = type.getClassLoader().getResourceAsStream(resourceName);
      final var encoded = new BufferedReader(new InputStreamReader(stream)).lines().collect(Collectors.joining("\n"));
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
   * @return the model defined on the response or {@code null} if can not obtain
   *         it.
   */
  public static <T extends Model> T fromResponse(final HttpResponse<Buffer> response, final Class<T> type) {

    if (response == null) {

      return null;

    } else {

      try {

        return fromBuffer(response.bodyAsBuffer(), type);

      } catch (final Throwable throwable) {

        Logger.trace(throwable);
        return null;
      }

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
  public static <T> JsonArray toJsonArray(final Iterable<T> models) {

    if (models == null) {

      return null;

    } else {

      final var array = new JsonArray();
      for (final T model : models) {

        if (model instanceof Model) {

          final var object = ((Model) model).toJsonObject();
          array.add(object);

        } else {

          array.add(model);
        }

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
   * @return the models of the array, or {@code null} if it can not obtain all the
   *         models.
   */
  public static <T> List<T> fromJsonArray(final JsonArray array, final Class<T> type) {

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
   * @return the models of the array, or {@code null} if it can not obtain all the
   *         models.
   */
  public static <T> List<T> fromJsonArray(final Buffer buffer, final Class<T> type) {

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
   * Convert a model to a {@link JsonObject} with all the {@code null} and empty
   * values.
   *
   * @return the object of the model or {@code null} if can not convert it.
   *
   * @see ModelForJsonObjectWithEmptyValues
   */
  default public JsonObject toJsonObjectWithEmptyValues() {

    try {

      final var mapper = DatabindCodec.mapper().copy();
      mapper.addMixIn(Model.class, ModelForJsonObjectWithEmptyValues.class);
      final var json = mapper.writeValueAsString(this);
      Buffer.buffer(json);
      return new JsonObject(json);

    } catch (final Throwable throwable) {

      Logger.trace(throwable);
      return null;
    }

  }

  /**
   * Convert a model to a {@link Buffer} with all the {@code null} and empty
   * values.
   *
   * @return the buffer of the model or {@code null} if can not convert it.
   *
   * @see ModelForJsonObjectWithEmptyValues
   */
  default public Buffer toBufferWithEmptyValues() {

    try {

      final var mapper = DatabindCodec.mapper().copy();
      mapper.addMixIn(Model.class, ModelForJsonObjectWithEmptyValues.class);
      final var json = mapper.writeValueAsString(this);
      return Buffer.buffer(json);

    } catch (final Throwable throwable) {

      Logger.trace(throwable);
      return null;
    }

  }

}
