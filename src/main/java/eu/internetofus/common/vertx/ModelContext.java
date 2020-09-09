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
