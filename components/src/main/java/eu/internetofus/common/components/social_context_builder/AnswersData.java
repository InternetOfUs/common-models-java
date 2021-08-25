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

import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * The data to call the answers ranking.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "user_answer", description = "A User to User answer.")
public class AnswersData extends ReflectionModel implements Model {

  /**
   * The first user identifier.
   */
  @ArraySchema(schema = @Schema(implementation = UserAnswer.class, description = "The user answers."))
  public List<UserAnswer> data;

}
