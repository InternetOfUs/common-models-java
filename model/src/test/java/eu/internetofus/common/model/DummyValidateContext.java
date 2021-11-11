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
 * Dummy cache to use on some tests.
 *
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class DummyValidateContext implements ValidateContext<DummyValidateContext> {

  /**
   * Error code of the context.
   */
  protected String errorCode;

  /**
   * Create new context.
   *
   * @param errorCode for the context.
   */
  public DummyValidateContext(final String errorCode) {

    this.errorCode = errorCode;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String errorCode() {

    return this.errorCode;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DummyValidateContext createContextWithErrorCode(final String errorCode) {

    return new DummyValidateContext(errorCode);
  }

}
