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

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import io.vertx.core.json.JsonObject;

/**
 * The component to deserialize a {@link JsonObject} to any of it possible sub types.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class JsonObjectDeserializer extends StdDeserializer<JsonObject> {

  /**
   * Serial version identifier.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The type for the deserializer.
   */
  private final TypeReference<Map<String, Object>> type = new TypeReference<Map<String, Object>>() {
  };

  /**
   * Create a new deserializer
   */
  public JsonObjectDeserializer() {

    this(null);
  }

  /**
   * Create a new deserializer for a type.
   *
   * @param type for the deserializer
   */
  public JsonObjectDeserializer(final Class<?> type) {

    super(type);
  }

  @Override
  public JsonObject deserialize(final JsonParser jsonParser, final DeserializationContext context) throws IOException {

    final var objectCodec = jsonParser.getCodec();
    final var value = objectCodec.readValue(jsonParser, this.type);

    return new JsonObject(value);
  }

}