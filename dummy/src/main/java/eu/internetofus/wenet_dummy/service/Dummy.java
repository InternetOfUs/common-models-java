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
package eu.internetofus.wenet_dummy.service;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eu.internetofus.common.model.JsonObjectDeserializer;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.json.JsonObject;

/**
 * The dummy value that is interchnaged on the API.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(name = "Dummy", description = "A dummy model to interchange to test the common classes")
public class Dummy extends ReflectionModel implements Model {

  /**
   * The identifier of the model.
   */
  @Schema(description = "The identifier of the model", example = "1")
  public String id;

  /**
   * The value associated to the model.
   */
  @Schema(description = "The value associated to the model", example = "value")
  public String value;

  /**
   * The extra information associated to the model.
   */
  @Schema(type = "object", description = "Extra information associated to the model", implementation = Object.class)
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  public JsonObject extra;

}
