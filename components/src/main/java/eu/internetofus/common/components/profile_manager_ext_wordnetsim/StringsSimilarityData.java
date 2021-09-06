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
package eu.internetofus.common.components.profile_manager_ext_wordnetsim;

import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The data to calculate the similarity.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(name = "StringsSimilarityData", description = "The status of a task type.")
public class StringsSimilarityData extends ReflectionModel implements Model {

  /**
   * The source string to calculate the similarity.
   */
  @Schema(description = "The source string to calculate the similarity.", example = "I hate to fly", nullable = false)
  public String source;

  /**
   * The target string to calculate the similarity.
   */
  @Schema(description = "The target string to calculate the similarity.", example = "I do not want to go by plane", nullable = false)
  public String target;

  /**
   * The type of aggregation function.
   */
  @Schema(description = "The target string to calculate the similarity.", example = "I do not want to go by plane", nullable = false)
  public Aggregation aggregation;

}
