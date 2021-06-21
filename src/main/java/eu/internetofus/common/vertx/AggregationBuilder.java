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

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * This component is used to create an aggregation command pipeline.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class AggregationBuilder {

  /**
   * The query that is creating.
   */
  private final JsonArray pipeline;

  /**
   * Create a query builder.
   */
  public AggregationBuilder() {

    this.pipeline = new JsonArray();

  }

  /**
   * Return the created aggregation command pipeline.
   *
   * @return the created aggregation command pipeline.
   */
  public JsonArray build() {

    return this.pipeline;
  }

  /**
   * Split an element path to the respective fields.
   *
   * @param elementPath to split.
   *
   * @return the splitter value of the element path.
   */
  public static String[] splitElementPath(String elementPath) {

    if (elementPath == null) {

      return new String[0];

    } else {

      var trimmed = elementPath.replaceAll("\\s", "");
      if (trimmed.length() == 0) {

        return new String[0];

      } else {

        return trimmed.split("\\.");
      }

    }

  }

  /**
   * Unwind an element of the document.
   *
   * @param elementPath the name of the element to unwind. It has to be the values
   *                    to access to component on the document.
   *
   * @return this builder.
   */
  public AggregationBuilder unwind(String elementPath) {

    var splitted = splitElementPath(elementPath);
    return this.unwindPath(splitted);

  }

  /**
   * Unwind an element of the document.
   *
   * @param elementPath the name of the element to unwind. It has to be the values
   *                    to access to component on the document.
   *
   * @return this builder.
   */
  public AggregationBuilder unwindPath(String... elementPath) {

    if (elementPath != null && elementPath.length > 0) {

      var path = "$" + elementPath[0];
      this.pipeline.add(new JsonObject().put("$unwind",
          new JsonObject().put("path", path).put("includeArrayIndex", elementPath[0] + "Index")));
      for (var i = 1; i < elementPath.length; i++) {

        path += "." + elementPath[i];
        this.pipeline.add(new JsonObject().put("$unwind",
            new JsonObject().put("path", path).put("includeArrayIndex", elementPath[i] + "Index")));

      }
    }

    return this;
  }

  /**
   * Specify the query that has to match the documents.
   *
   * @param query that has to match the documents.
   *
   * @return this builder.
   */
  public AggregationBuilder match(JsonObject query) {

    if (query != null && !query.isEmpty()) {

      this.pipeline.add(new JsonObject().put("$match", query));

    }

    return this;
  }

  /**
   * Specify the query that has to match the documents.
   *
   * @param order  for the documents.
   * @param offset number of documents to skip.
   * @param limit  maximum number of documents to return.
   *
   * @return this builder.
   */
  public AggregationBuilder sort(JsonObject order, int offset, int limit) {

    if (order != null && !order.isEmpty()) {

      this.pipeline.add(new JsonObject().put("$sort", order));

    }
    this.pipeline.add(new JsonObject().put("$limit", offset + limit));
    if (offset > 0) {

      this.pipeline.add(new JsonObject().put("$skip", offset));

    }

    return this;
  }

}
