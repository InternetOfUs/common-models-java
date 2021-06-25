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

      this.query.put(fieldName, new JsonObject().put("$regex", pattern).put("$options", "i"));
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

      final var patternsMatch = new JsonArray();
      for (final String pattern : patterns) {

        patternsMatch
            .add(new JsonObject().put("$elemMatch", new JsonObject().put("$regex", pattern).put("$options", "i")));
      }
      if (patternsMatch.size() != 0) {

        this.query.put(fieldName, new JsonObject().put("$all", patternsMatch));
      }
    }

    return this;
  }

  /**
   * Add a the restrictions to mark the field to be in the specified range.
   *
   * @param fieldName name of the field.
   * @param from      the value, inclusive, that mark the lowest value of the
   *                  range.
   * @param to        the value, inclusive, that mark the highest value of the
   *                  range.
   *
   * @return the factory that is using.
   */
  public QueryBuilder withRange(final String fieldName, final Number from, final Number to) {

    if (from != null || to != null) {

      final var restriction = new JsonObject();
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
   * Add a the restrictions to mark the field exist, thus that it is defined and
   * not {@code null}.
   *
   * @param fieldName  name of the field.
   * @param hasToExist is {@code true} is the field has to exist, {@code false} if
   *                   has to no exist, or {@code null} to ignore this
   *                   restriction.
   *
   * @return the factory that is using.
   */
  public QueryBuilder withExist(final String fieldName, final Boolean hasToExist) {

    if (hasToExist != null) {

      if (hasToExist) {

        final var restriction = new JsonObject();
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
   * Add a the restrictions to mark the field to be equals to the specified field.
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
   * Add a the restrictions to mark the field with an specific value or a regular
   * expression. It it is a regular expression it has to be between {@code /}.
   *
   * @param fieldName name of the field.
   * @param value     or regular expression for the field. If it is {@code null}
   *                  the field is ignored.
   *
   * @return the factory that is using.
   */
  public QueryBuilder withEqOrRegex(final String fieldName, final String value) {

    if (value != null) {

      if (this.containsPattern(value)) {

        final var pattern = this.extractPattern(value);
        return this.withRegex(fieldName, pattern);

      } else {

        this.query.put(fieldName, value);
      }

    }

    return this;

  }

  /**
   * Check if a value contains a pattern.
   *
   * @param value to check.
   *
   * @return {@code true} if the value contains a pattern.
   */
  protected boolean containsPattern(final String value) {

    return value.length() > 1 && value.startsWith("/") && value.endsWith("/");
  }

  /**
   * Extract the pattern define din a value.
   *
   * @param value to get the pattern.
   *
   * @return the pattern defined on the value.
   */
  protected String extractPattern(final String value) {

    return value.substring(1, value.length() - 1);
  }

  /**
   * Add a the restrictions to mark an array field that contains some elements
   * that match a value or a regular expression. It it is a regular expression it
   * has to be between {@code /}.
   *
   * @param fieldName name of the field that contains an array.
   * @param values    or regular expression for the field. If it is {@code null}
   *                  the field is ignored.
   *
   * @return the factory that is using.
   */
  public QueryBuilder withEqOrRegex(final String fieldName, final Iterable<String> values) {

    if (values != null) {

      final var patternsMatch = new JsonArray();
      for (final String value : values) {

        if (value != null) {

          final var elementMatch = this.elementMatch(value);
          patternsMatch.add(new JsonObject().put("$elemMatch", elementMatch));
        }

      }
      if (patternsMatch.size() != 0) {

        this.query.put(fieldName, new JsonObject().put("$all", patternsMatch));
      }

    }
    return this;
  }

  /**
   * Return the query to match an element value.
   *
   * @param value to match an element.
   *
   * @return the matcher query.
   */
  protected JsonObject elementMatch(final String value) {

    final var elementMatch = new JsonObject();
    if (this.containsPattern(value)) {

      final var pattern = this.extractPattern(value);
      elementMatch.put("$regex", pattern).put("$options", "i");

    } else {

      elementMatch.put("$eq", value);
    }
    return elementMatch;
  }

  /**
   * Return the created query.
   *
   * @return the created query.
   */
  public JsonObject build() {

    return this.query;
  }

  /**
   * Add a the restrictions to mark an array field that contains some elements
   * where a field matches a value or a regular expression. It it is a regular
   * expression it has to be between {@code /}.
   *
   * @param fieldName    name of the field that contains an array.
   * @param subFieldName name of the field for the element on the elements.
   * @param values       or regular expression for the field. If it is
   *                     {@code null} the field is ignored.
   *
   * @return the factory that is using.
   */
  public QueryBuilder withElementEqOrRegex(final String fieldName, final String subFieldName,
      final Iterable<String> values) {

    if (values != null) {

      final var patternsElementsMatch = new JsonArray();
      for (final String value : values) {

        if (value != null) {

          final var elementMatch = this.elementMatch(value);
          patternsElementsMatch
              .add(new JsonObject().put("$elemMatch", new JsonObject().put(subFieldName, elementMatch)));
        }

      }
      if (patternsElementsMatch.size() != 0) {

        this.query.put(fieldName, new JsonObject().put("$all", patternsElementsMatch));
      }

    }
    return this;
  }

  /**
   * Add a the restrictions to mark the field with an specific value or a regular
   * expression. If the value is {@code null} it check if the field not exist or
   * it is {@code null}. it is a regular expression it has to be between
   * {@code /}.
   *
   * @param fieldName name of the field.
   * @param value     or regular expression for the field, or {@code null} if the
   *                  filed has to no exist or be {@code null}.
   *
   *
   * @return the factory that is using.
   */
  public QueryBuilder withNoExistNullEqOrRegex(final String fieldName, final String value) {

    if (value != null) {

      return this.withEqOrRegex(fieldName, value);

    } else {

      this.query.putNull(fieldName);

    }

    return this;

  }
}
