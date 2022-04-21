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

import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

/**
 * The data necessary to calculate the diversity.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(description = "The information necessary to calculate the diversity between some users.")
public class DiversityData extends ReflectionModel implements Model {

  /**
   * The identifiers of the users to calculate the diversity.
   */
  @ArraySchema(schema = @Schema(implementation = String.class), arraySchema = @Schema(description = "The user profile identifiers of calculate the diversity", example = "[\"1\",\"2\"]"))
  public Set<String> userIds;

  /**
   * The name of the attributes of the profile to calculate the diversity.
   */
  @ArraySchema(schema = @Schema(implementation = String.class), arraySchema = @Schema(description = "The name of the profile attributes to calculate the diversity", example = "[\"gender\",\"nationality\",\"meanings.extraversion\"]"))
  public Set<String> attributes;

}
