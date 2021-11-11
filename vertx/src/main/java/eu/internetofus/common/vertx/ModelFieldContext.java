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

import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ValidateContext;
import java.util.List;

/**
 * The context of a field for a model.
 *
 * @param <T>  type of model that contains the fields.
 * @param <IT> type of the model identifier.
 * @param <E>  type of the field.
 * @param <IE> type of the field identifier.
 * @param <C>  type of validation context to use.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ModelFieldContext<T extends Model, IT, E extends Model, IE, C extends ValidateContext<C>>
    extends ModelContext<E, IE, C> {

  /**
   * The model where the field is defined.
   */
  public ModelContext<T, IT, C> model;

  /**
   * The value of the field.
   */
  public List<E> field;

  /**
   * The position of the element in the field.
   */
  public int index = -1;

}
