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
package eu.internetofus.wenet_dummy.service;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eu.internetofus.common.components.JsonObjectDeserializer;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ReflectionModel;
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
