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

import io.vertx.core.Future;

/**
 * This is implemented by any model that can validate its content.
 *
 * @param <C> type of context to use.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface Validable<C extends ValidateContext<C>> {

  /**
   * Check if the model is right.
   *
   * @param context to use to validate the model.
   *
   *
   * @return the future that inform if the value is right. If the model is not
   *         valid the cause will be a {@link ValidationErrorException}.
   *
   * @see ValidationErrorException
   */
  Future<Void> validate(C context);

}
