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
