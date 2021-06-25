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
