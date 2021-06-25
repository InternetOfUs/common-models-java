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

package eu.internetofus.common.vertx;

import eu.internetofus.common.components.Model;

/**
 * Contains the information of a model that is used on a operation.
 *
 * @param <T> type of model.
 * @param <I> type for the model identifier.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ModelContext<T extends Model, I> {

  /**
   * The name of the model.
   */
  public String name;

  /**
   * The type of the model.
   */
  public Class<T> type;

  /**
   * The identifier of the model.
   */
  public I id;

  /**
   * The value of the context.
   */
  public T value;

  /**
   * The new value for the model.
   */
  public T source;

  /**
   * The stored value of the model.
   */
  public T target;

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {

    final StringBuilder builder = new StringBuilder();

    builder.append(this.name);
    if (this.id != null) {

      builder.append('(');
      builder.append(this.id);
      builder.append(')');
    }
    return builder.toString();
  }

}
