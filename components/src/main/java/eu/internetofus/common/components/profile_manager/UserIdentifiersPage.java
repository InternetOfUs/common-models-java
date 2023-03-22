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

package eu.internetofus.common.components.profile_manager;

import java.util.List;

import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Contains the found user identifiers.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(title = "UserIdentifiersPage", description = "Contains a set of user identifiers")
public class UserIdentifiersPage extends ReflectionModel implements Model {

  /**
   * The index of the first profile returned.
   */
  @Schema(description = "The index of the first user identifier returned.", example = "0")
  public int offset;

  /**
   * The number total of profiles that satisfies the search.
   */
  @Schema(description = "The number total of user identifiers that satisfies the search.", example = "100")
  public long total;

  /**
   * The found profiles.
   */
  @ArraySchema(schema = @Schema(implementation = String.class), arraySchema = @Schema(description = "The found user identifiers"))
  public List<String> userIds;

}
