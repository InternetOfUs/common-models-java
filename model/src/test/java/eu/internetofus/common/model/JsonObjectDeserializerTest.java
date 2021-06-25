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

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

/**
 * Test the {@link JsonObjectDeserializer}
 *
 * @see JsonObjectDeserializer
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class JsonObjectDeserializerTest {

  /**
   * A model with an object.
   */
  public static class ModelWithObject extends ReflectionModel implements Model {

    /**
     * The content of the model
     */
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    public JsonObject content;

  }

  /**
   * Should deserialize a json object.
   */
  @Test
  public void shouldDeserializeObject() {

    final var source = new ModelWithObject();
    source.content = new JsonObject().put("string", "string").put("number", 1).put("array",
        new JsonArray().add("1").add("2").add("3"));
    final var coded = source.toJsonString();
    assertThat(coded).isNotNull();
    final var target = Model.fromString(coded, ModelWithObject.class);
    assertThat(target).isEqualTo(source);

  }

}
