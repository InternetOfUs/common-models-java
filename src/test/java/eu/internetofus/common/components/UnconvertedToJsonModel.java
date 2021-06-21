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

package eu.internetofus.common.components;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A model that can not be converted to JSON.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class UnconvertedToJsonModel extends ReflectionModel implements Model {

  /**
   * The identifier of the class.
   */
  @JsonProperty("id")
  public String id;

  /**
   * Another id that fail the JSON encoding.
   */
  @JsonProperty("id")
  public long _id;

}
