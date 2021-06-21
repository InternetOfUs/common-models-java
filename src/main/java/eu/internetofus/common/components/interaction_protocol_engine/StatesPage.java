/*
 * -----------------------------------------------------------------------------
 *
 *   Copyright 2019 - 2022 UDT-IA, IIIA-CSIC
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
package eu.internetofus.common.components.interaction_protocol_engine;

import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ReflectionModel;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Contains the found states.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(name = "StatesPage", description = "Contains a set of states")
public class StatesPage extends ReflectionModel implements Model {

  /**
   * The index of the first task returned.
   */
  @Schema(description = "The index of the first state returned.", example = "0")
  public int offset;

  /**
   * The number total of task that satisfies the search.
   */
  @Schema(description = "The number total of states that satisfies the search.", example = "100")
  public long total;

  /**
   * The found profiles.
   */
  @ArraySchema(schema = @Schema(implementation = State.class), arraySchema = @Schema(description = "The set of states found"))
  public List<State> states;

}
