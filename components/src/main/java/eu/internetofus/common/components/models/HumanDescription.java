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

package eu.internetofus.common.components.models;

import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * A model that has information when it is created and updated.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, description = "The attributes used to identify and describe a model for an human with timestamp details.")
public class HumanDescription extends ReflectionModel implements Model {

  /**
   * A name that identify the type.
   */
  @Schema(description = "A name that identify the model.", example = "Eat together task", nullable = true)
  public String name;

  /**
   * A human readable description of the task type.
   */
  @Schema(description = "A human readable description of the model.", example = "A task for organizing social dinners", nullable = true)
  public String description;

  /**
   * A name that identify the type.
   */
  @ArraySchema(schema = @Schema(implementation = String.class), arraySchema = @Schema(description = "The keywords that describe the model.", example = "[\"social interaction\",\"eat\"]", nullable = true))
  public List<String> keywords;

}
