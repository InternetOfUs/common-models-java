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

package eu.internetofus.common.vertx;

import eu.internetofus.common.model.Mergeable;
import eu.internetofus.common.model.Model;

/**
 * Function used to consumer the merge result.
 *
 * @param <T> type of model that consumer the merge result.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@FunctionalInterface
public interface MergeConsumer<T extends Model & Mergeable<T>> {

  /**
   * Called when the merge was a success.
   *
   * @param source model to merge
   * @param target model to merge.
   * @param merged the resulted merged model.
   */
  void accept(T source, T target, T merged);

}
