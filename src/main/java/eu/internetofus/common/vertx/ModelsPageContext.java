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
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
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

import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;

/**
 * Contains the information of a models page that is used on a operation.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ModelsPageContext {

  /**
   * The pattern that has to match the models to search.
   */
  public JsonObject query;

  /**
   * Description of the order in witch the models has to be returned.
   */
  public JsonObject sort;

  /**
   * The index of the first model to return.
   */
  public int offset;

  /**
   * The number maximum of models to return.
   */
  public int limit;

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {

    final StringBuilder builder = new StringBuilder();
    if (this.query != null) {

      builder.append("query:\n");
      builder.append(this.query.encodePrettily());
      builder.append('\n');
    }
    if (this.sort != null) {

      builder.append("sort:\n");
      builder.append(this.sort.encodePrettily());
      builder.append('\n');
    }

    builder.append("offset:");
    builder.append(this.offset);
    builder.append('\n');

    builder.append("limit:");
    builder.append(this.limit);
    builder.append('\n');

    return builder.toString();
  }

  /**
   * Convert the context to MongoDB find options.
   *
   * @return the find options defined by this context.
   */
  public FindOptions toFindOptions() {

    final var options = new FindOptions();
    if (this.sort != null) {
      options.setSort(this.sort);
    }
    options.setSkip(this.offset);
    options.setLimit(this.limit);
    return options;
  }

}
