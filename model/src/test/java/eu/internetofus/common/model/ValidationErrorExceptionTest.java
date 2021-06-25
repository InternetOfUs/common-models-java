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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Test the {@link ValidationErrorException}
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ValidationErrorExceptionTest {

  /**
   * Check that create with a code and message.
   */
  @Test
  public void shouldCreateWithCodeAndMessage() {

    final var error = new ValidationErrorException("code", "message");
    assertThat(error.getCode()).isEqualTo("code");
    assertThat(error.getMessage()).isEqualTo("message");
  }

  /**
   * Check that create with a code and cause.
   */
  @Test
  public void shouldCreateWithCodeAndCause() {

    final var cause = new Throwable("cause");
    final var error = new ValidationErrorException("code", cause);
    assertThat(error.getCode()).isEqualTo("code");
    assertThat(error.getMessage()).isEqualTo(cause.toString());
    assertThat(error.getCause()).isEqualTo(cause);
  }

  /**
   * Check that create with a code, cause and message.
   */
  @Test
  public void shouldCreateWithCodeCauseAndMessage() {

    final var cause = new Throwable("cause");
    final var error = new ValidationErrorException("code", "message", cause);
    assertThat(error.getCode()).isEqualTo("code");
    assertThat(error.getMessage()).isEqualTo("message");
    assertThat(error.getCause()).isEqualTo(cause);
  }

}
