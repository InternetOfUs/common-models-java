/*
 * -----------------------------------------------------------------------------
 *
 * Copyright (c) 2019 - 2022 UDT-IA, IIIA-CSIC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * -----------------------------------------------------------------------------
 */

package eu.internetofus.common.vertx;

import java.util.List;

import eu.internetofus.common.components.Model;

/**
 * The context of a field for a model.
 *
 * @param <T>  type of model that contains the fields.
 * @param <IT> type of the model identifier.
 * @param <E>  type of the field.
 * @param <IE> type of the field identifier.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ModelFieldContext<T extends Model, IT, E extends Model, IE> extends ModelContext<E, IE> {

  /**
   * The model where the field is defined.
   */
  public ModelContext<T, IT> model;

  /**
   * The value of the field.
   */
  public List<E> field;

  /**
   * The position of the element in the field.
   */
  public int index = -1;

}
