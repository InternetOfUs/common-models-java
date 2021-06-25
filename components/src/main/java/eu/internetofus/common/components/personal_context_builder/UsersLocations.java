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

package eu.internetofus.common.components.personal_context_builder;

import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;

/**
 * The locations of a set of users.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "UsersLocations", description = "The locations of a set of users.")
public class UsersLocations extends ReflectionModel implements Model {

  /**
   * The obtained user locations.
   */
  @ArraySchema(schema = @Schema(implementation = UserLocation.class), arraySchema = @Schema(description = "The identifiers of the users to obtain their locations."))
  public List<UserLocation> locations;

  /**
   * Create the locations of a set of users.
   */
  public UsersLocations() {

    this.locations = new ArrayList<>();
  }
}
