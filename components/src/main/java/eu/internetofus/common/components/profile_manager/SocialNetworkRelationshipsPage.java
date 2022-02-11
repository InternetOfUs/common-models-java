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

import eu.internetofus.common.components.models.SocialNetworkRelationship;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Contains the found communities.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(name = "SocialNetworkRelationshipsPage", description = "Contains a set of communities found")
public class SocialNetworkRelationshipsPage extends ReflectionModel implements Model {

  /**
   * The index of the first communities returned.
   */
  @Schema(description = "The index of the first community returned.", example = "0")
  public int offset;

  /**
   * The number total of communities that satisfies the search.
   */
  @Schema(description = "The number total of communities that satisfies the search.", example = "100")
  public long total;

  /**
   * The found communities.
   */
  @ArraySchema(schema = @Schema(ref = "https://raw.githubusercontent.com/InternetOfUs/components-documentation/MODELS_2.1.0/sources/wenet-models-openapi.yaml#/components/schemas/SocialNetworkRelationship"), arraySchema = @Schema(description = "The set of communities found"))
  public List<SocialNetworkRelationship> relationships;

}
