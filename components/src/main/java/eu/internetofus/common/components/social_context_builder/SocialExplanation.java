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

package eu.internetofus.common.components.social_context_builder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eu.internetofus.common.model.JsonObjectDeserializer;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.json.JsonObject;

/**
 * The calculated user relation by the social context builder.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "social_explanation", description = "A social explanation.")
public class SocialExplanation extends ReflectionModel implements Model {

  /**
   * The description of the social explanation.
   */
  @Schema(example = "Social explanation")
  public String description;

  /**
   * The description of the social explanation.
   */
  @Schema(type = "object", implementation = Object.class)
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  public JsonObject Summary;

}
