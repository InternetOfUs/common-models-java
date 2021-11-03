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
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

/**
 * Contains the calculated similarity for the attributes of a user profile.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(description = "The similarity of the user attributes to a text.")
public class SimilarityResult extends ReflectionModel implements Model {

  /**
   * The calculate similarity for a profile attributes.
   */
  @Schema(description = "The similarity between the profile attributes and a text. The key is the name of the attribute and the value is the similarity on the range [0,1].", example = "{\"gender\":0.4}")
  public Map<String, Double> attributes;

}
