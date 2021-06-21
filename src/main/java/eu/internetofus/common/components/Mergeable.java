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

import io.vertx.core.Future;
import io.vertx.core.Vertx;

/**
 * This is implemented by any model that can be merged with another model to create a new model.
 *
 * @param <T> type of models that can be merged.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface Mergeable<T> {

  /**
   * Merge the current model with a new one creating a new one.
   *
   * @param source     model to merge with the current one.
   * @param codePrefix the prefix of the code to use for the error message.
   * @param vertx      the event bus infrastructure to use.
   *
   * @return the future that provide the merged model that has to be valid. If it cannot merge to a new model the cause
   *         will be a {@link ValidationErrorException}.
   *
   * @see ValidationErrorException
   */
  Future<T> merge(T source, String codePrefix, Vertx vertx);

}
