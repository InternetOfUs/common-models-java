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

package eu.internetofus.common.model;

/**
 * This exception explains why a model is not valid.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ValidationErrorException extends RuntimeException {

  /**
   * Serialization identifier.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The code of the error.
   */
  protected String code;

  /**
   * Create a new validation error exception.
   *
   * @param code    for the error message.
   * @param message a brief description of the error to be read by a human.
   */
  public ValidationErrorException(final String code, final String message) {

    super(message);
    this.code = code;

  }

  /**
   * Create a new validation error exception with a message an a cause.
   *
   * @param code    for the error message.
   * @param message a brief description of the error to be read by a human.
   * @param cause   because the model is not right.
   */
  public ValidationErrorException(final String code, final String message, final Throwable cause) {

    super(message, cause);
    this.code = code;

  }

  /**
   * Create a new validation error exception with a message an a cause.
   *
   * @param code  for the error message.
   * @param cause because the model is not right.
   */
  public ValidationErrorException(final String code, final Throwable cause) {

    super(cause);
    this.code = code;
  }

  /**
   * The code associated to the error.
   *
   * @return the error code.
   *
   * @see #code
   */
  public String getCode() {

    return this.code;
  }

}
