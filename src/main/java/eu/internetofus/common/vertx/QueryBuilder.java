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

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * This component is used to create a query.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class QueryBuilder {

  /**
   * The query that is creating.
   */
  private final JsonObject query;

  /**
   * Create a query builder.
   */
  public QueryBuilder() {

    this.query = new JsonObject();

  }

  /**
   * Add a regular expression for a field.
   *
   * @param fieldName name of the field.
   * @param pattern   pattern that the field has to match.
   *
   * @return the factory that is using.
   */
  public QueryBuilder withRegex(final String fieldName, final String pattern) {

    if (pattern != null) {

      this.query.put(fieldName, new JsonObject().put("$regex", pattern));
    }

    return this;
  }

  /**
   * Add a regular expression for a field that has an array of strings.
   *
   * @param fieldName name of the field.
   * @param patterns  patterns that the field has to match.
   *
   * @return the factory that is using.
   */
  public QueryBuilder withRegex(final String fieldName, final Iterable<String> patterns) {

    if (patterns != null) {

      final JsonArray patternsMatch = new JsonArray();
      for (final String pattern : patterns) {

        patternsMatch.add(new JsonObject().put("$elemMatch", new JsonObject().put("$regex", pattern)));
      }
      if (patternsMatch.size() != 0) {

        this.query.put(fieldName, new JsonObject().put("$all", patternsMatch));
      }
    }

    return this;
  }

  /**
   * Add a the restrictions to mark the filed to be in the specified range.
   *
   * @param fieldName name of the field.
   * @param from      the value, inclusive, that mark the lowest value of the range.
   * @param to        the value, inclusive, that mark the highest value of the range.
   *
   * @return the factory that is using.
   */
  public QueryBuilder withRange(final String fieldName, final Number from, final Number to) {

    if (from != null || to != null) {

      final JsonObject restriction = new JsonObject();
      if (from != null) {

        restriction.put("$gte", from);

      }
      if (to != null) {

        restriction.put("$lte", to);

      }

      this.query.put(fieldName, restriction);

    }

    return this;
  }

  /**
   * Add a the restrictions to mark the filed exist, thus that it is defined and not {@code null}.
   *
   * @param fieldName  name of the field.
   * @param hasToExist is {@code true} is the field has to exist, {@ode false} if has to no exist, or {@code null} to
   *                   ignore this restriction.
   *
   * @return the factory that is using.
   */
  public QueryBuilder withExist(final String fieldName, final Boolean hasToExist) {

    if (hasToExist != null) {

      if (hasToExist) {

        final JsonObject restriction = new JsonObject();
        restriction.put("$exists", true);
        restriction.putNull("$ne");
        this.query.put(fieldName, restriction);

      } else {

        this.query.putNull(fieldName);

      }

    }

    return this;
  }

  /**
   * Add a the restrictions to mark the filed to be equals to the specified field.
   *
   * @param fieldName name of the field.
   * @param value     for the field.
   *
   * @return the factory that is using.
   */
  public QueryBuilder with(final String fieldName, final Object value) {

    if (value != null) {

      this.query.put(fieldName, value);

    } else {

      this.query.putNull(fieldName);
    }

    return this;
  }

  /**
   * Add a the restrictions to mark the field with an specific value or a regular expression. It it is a regular
   * expression it has to be between {@code /}.
   *
   * @param fieldName name of the field.
   * @param value     or regular expression for the field. If it is {@code null} the field is ignored.
   *
   * @return the factory that is using.
   */
  public QueryBuilder withEqOrRegex(final String fieldName, final String value) {

    if (value != null) {

      if (value.length() > 1 && value.startsWith("/") && value.endsWith("/")) {

        final String pattern = value.substring(1, value.length() - 2);
        return this.withRegex(fieldName, pattern);

      } else {

        this.query.put(fieldName, value);
      }

    }

    return this;

  }

  /**
   * Return the created query.
   *
   * @return the created query.
   */
  public JsonObject build() {

    return this.query;
  }

}
