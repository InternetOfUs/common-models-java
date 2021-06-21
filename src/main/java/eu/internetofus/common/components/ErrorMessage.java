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

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Generic model to return the that describe an error reported
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(name = "ErrorMessage", description = "Inform of an error that happens when interacts with the API")
public class ErrorMessage extends ReflectionModel implements Model {

  /**
   * The code of the error.
   */
  @Schema(description = "Contain code that identifies the error", example = "error_code")
  public String code;

  /**
   * A brief description of the error to be read by a human.
   */
  @Schema(description = "Contain a brief description of the error to be read by a human", example = "Error readable by a human")
  public String message;

  /**
   * Create empty error message.
   */
  public ErrorMessage() {

  }

  /**
   * Create a new error message.
   *
   * @param code    for the error message.
   * @param message a brief description of the error to be read by a human.
   */
  public ErrorMessage(final String code, final String message) {

    this.code = code;
    this.message = message;

  }
}