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

import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test the {@link QueryBuilder}.
 *
 * @see QueryBuilder
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class QueryBuilderTest {

  /**
   * Should create empty query.
   */
  @Test
  public void shouldCreateEmptyQuery() {

    assertThat(new QueryBuilder().build()).isEqualTo(new JsonObject());

  }

  /**
   * Should create empty query with {@code null} pattern.
   *
   * @see QueryBuilder#withRegex(String, String)
   */
  @Test
  public void shouldCreateEmptyQueryWithNullPattern() {

    final var builder = new QueryBuilder();
    assertThat(builder.withRegex("fieldName", (String) null)).isSameAs(builder);
    assertThat(builder.build()).isEqualTo(new JsonObject());

  }

  /**
   * Should create regex query with a pattern.
   *
   * @see QueryBuilder#withRegex(String, String)
   */
  @Test
  public void shouldCreateQueryWithRegexp() {

    final var builder = new QueryBuilder();
    assertThat(builder.withRegex("field", "pattern")).isSameAs(builder);
    assertThat(builder.build()).isEqualTo(new JsonObject().put("field", new JsonObject().put("$regex", "pattern")));

  }

  /**
   * Should create empty query with {@code null} iterable.
   *
   * @see QueryBuilder#withRegex(String, Iterable)
   */
  @Test
  public void shouldCreateEmptyQueryWithNullIterable() {

    final var builder = new QueryBuilder();
    assertThat(builder.withRegex("fieldName", (Iterable<String>) null)).isSameAs(builder);
    assertThat(builder.build()).isEqualTo(new JsonObject());

  }

  /**
   * Should create empty query with empty iterable.
   *
   * @see QueryBuilder#withRegex(String, Iterable)
   */
  @Test
  public void shouldCreateEmptyQueryWithEmptyIterable() {

    final var builder = new QueryBuilder();
    assertThat(builder.withRegex("fieldName", new ArrayList<String>())).isSameAs(builder);
    assertThat(builder.build()).isEqualTo(new JsonObject());

  }

  /**
   * Should create query with {@code null}.
   *
   * @see QueryBuilder#with(String, Object)
   */
  @Test
  public void shouldCreateQueryWithNull() {

    final var builder = new QueryBuilder();
    assertThat(builder.with("field", null)).isSameAs(builder);
    assertThat(builder.build()).isEqualTo(new JsonObject().putNull("field"));

  }

  /**
   * Should create query with a value.
   *
   * @see QueryBuilder#with(String, Object)
   */
  @Test
  public void shouldCreateQueryWitValue() {

    final var builder = new QueryBuilder();
    assertThat(builder.with("field", "value")).isSameAs(builder);
    assertThat(builder.build()).isEqualTo(new JsonObject().put("field", "value"));

  }

  /**
   * Should create empty query with {@code null} regex or value.
   *
   * @see QueryBuilder#withEqOrRegex(String, String)
   */
  @Test
  public void shouldCreateEmptyQueryWithNullRegexOrValue() {

    final var builder = new QueryBuilder();
    assertThat(builder.withEqOrRegex("fieldName", (String) null)).isSameAs(builder);
    assertThat(builder.build()).isEqualTo(new JsonObject());

  }

  /**
   * Should create empty query with {@code null} iterable regex or value.
   *
   * @see QueryBuilder#withEqOrRegex(String, String)
   */
  @Test
  public void shouldCreateEmptyQueryWithNullIterableRegexOrValue() {

    final var builder = new QueryBuilder();
    assertThat(builder.withEqOrRegex("fieldName", (Iterable<String>) null)).isSameAs(builder);
    assertThat(builder.build()).isEqualTo(new JsonObject());

  }

  /**
   * Should create empty query with empty iterable regex or value.
   *
   * @see QueryBuilder#withEqOrRegex(String, String)
   */
  @Test
  public void shouldCreateEmptyQueryWithEmptyIterableRegexOrValue() {

    final var builder = new QueryBuilder();
    assertThat(builder.withEqOrRegex("fieldName", new ArrayList<String>())).isSameAs(builder);
    assertThat(builder.build()).isEqualTo(new JsonObject());

  }

  /**
   * Should create empty query with empty range.
   *
   * @see QueryBuilder#withRange(String, Number, Number)
   */
  @Test
  public void shouldCreateEmptyQueryWithEmptyRange() {

    final var builder = new QueryBuilder();
    assertThat(builder.withRange("fieldName", null, null)).isSameAs(builder);
    assertThat(builder.build()).isEqualTo(new JsonObject());

  }

  /**
   * Should create empty query with minimum range.
   *
   * @see QueryBuilder#withRange(String, Number, Number)
   */
  @Test
  public void shouldCreateQueryWithMinRange() {

    final var builder = new QueryBuilder();
    assertThat(builder.withRange("fieldName", 1, null)).isSameAs(builder);
    assertThat(builder.build()).isEqualTo(new JsonObject().put("fieldName", new JsonObject().put("$gte", 1)));

  }

  /**
   * Should create empty query with maximum range.
   *
   * @see QueryBuilder#withRange(String, Number, Number)
   */
  @Test
  public void shouldCreateQueryWithMaxRange() {

    final var builder = new QueryBuilder();
    assertThat(builder.withRange("fieldName", null, 2)).isSameAs(builder);
    assertThat(builder.build()).isEqualTo(new JsonObject().put("fieldName", new JsonObject().put("$lte", 2)));

  }

  /**
   * Should create empty query with range.
   *
   * @see QueryBuilder#withRange(String, Number, Number)
   */
  @Test
  public void shouldCreateQueryWithRange() {

    final var builder = new QueryBuilder();
    assertThat(builder.withRange("fieldName", 1, 2)).isSameAs(builder);
    assertThat(builder.build())
        .isEqualTo(new JsonObject().put("fieldName", new JsonObject().put("$gte", 1).put("$lte", 2)));

  }

  /**
   * Should create empty query with empty range.
   *
   * @see QueryBuilder#withExist(String, Boolean)
   */
  @Test
  public void shouldCreateEmptyQueryWithNullExist() {

    final var builder = new QueryBuilder();
    assertThat(builder.withExist("fieldName", null)).isSameAs(builder);
    assertThat(builder.build()).isEqualTo(new JsonObject());

  }

  /**
   * Should create query with no exist.
   *
   * @see QueryBuilder#withExist(String, Boolean)
   */
  @Test
  public void shouldCreateQueryWithNoExist() {

    final var builder = new QueryBuilder();
    assertThat(builder.withExist("fieldName", false)).isSameAs(builder);
    assertThat(builder.build()).isEqualTo(new JsonObject().putNull("fieldName"));

  }

  /**
   * Should create query with exist.
   *
   * @see QueryBuilder#withExist(String, Boolean)
   */
  @Test
  public void shouldCreateQueryWithExist() {

    final var builder = new QueryBuilder();
    assertThat(builder.withExist("fieldName", true)).isSameAs(builder);
    assertThat(builder.build())
        .isEqualTo(new JsonObject().put("fieldName", new JsonObject().put("$exists", true).putNull("$ne")));

  }

  /**
   * Should create query with iterable regex.
   *
   * @see QueryBuilder#withRegex(String, Iterable)
   */
  @Test
  public void shouldCreateQueryWithRegexIterable() {

    final var builder = new QueryBuilder();
    assertThat(builder.withRegex("fieldName", Arrays.asList("key1", "key2", "key3"))).isSameAs(builder);
    assertThat(builder.build()).isEqualTo(new JsonObject().put("fieldName",
        new JsonObject().put("$all",
            new JsonArray().add(new JsonObject().put("$elemMatch", new JsonObject().put("$regex", "key1")))
                .add(new JsonObject().put("$elemMatch", new JsonObject().put("$regex", "key2")))
                .add(new JsonObject().put("$elemMatch", new JsonObject().put("$regex", "key3"))))));

  }

  /**
   * Should create query with equals.
   *
   * @param value that has to be equals.
   *
   * @see QueryBuilder#withEqOrRegex(String, String)
   */
  @ParameterizedTest(name = "Should create Eq query with value {0}")
  @EmptySource
  @ValueSource(strings = { "/", "//8", "value", " /something/", "/something/ " })
  public void shouldCreateQueryWithEqValue(final String value) {

    final var builder = new QueryBuilder();
    assertThat(builder.withEqOrRegex("fieldName", value)).isSameAs(builder);
    assertThat(builder.build()).isEqualTo(new JsonObject().put("fieldName", value));

  }

  /**
   * Should create query with regex.
   *
   * @param value that contains the regex.
   *
   * @see QueryBuilder#withEqOrRegex(String, String)
   */
  @ParameterizedTest(name = "Should create Regex query with value {0}")
  @ValueSource(strings = { "/8/", "/value/", "//" })
  public void shouldCreateQueryWithRegexValue(final String value) {

    final var builder = new QueryBuilder();
    assertThat(builder.withEqOrRegex("fieldName", value)).isSameAs(builder);
    assertThat(builder.build())
        .isEqualTo(new JsonObject().put("fieldName", new JsonObject().put("$regex", builder.extractPattern(value))));

  }

  /**
   * Should create empty query with empty iterable.
   *
   * @see QueryBuilder#withEqOrRegex(String, Iterable)
   */
  @Test
  public void shouldCreateEmptyQueryWithEmptyIterableRegexValue() {

    final var builder = new QueryBuilder();
    assertThat(builder.withEqOrRegex("fieldName", new ArrayList<>())).isSameAs(builder);
    assertThat(builder.build()).isEqualTo(new JsonObject());

  }

  /**
   * Should create query with iterable regex and values.
   *
   * @see QueryBuilder#withEqOrRegex(String, Iterable)
   */
  @Test
  public void shouldCreateQueryWithIterableRegexValue() {

    final var builder = new QueryBuilder();
    assertThat(builder.withEqOrRegex("fieldName", Arrays.asList("value", null, "/key.+/"))).isSameAs(builder);
    assertThat(builder.build()).isEqualTo(new JsonObject().put("fieldName",
        new JsonObject().put("$all",
            new JsonArray().add(new JsonObject().put("$elemMatch", new JsonObject().put("$eq", "value")))
                .add(new JsonObject().put("$elemMatch", new JsonObject().put("$regex", "key.+"))))));

  }

  /**
   * Should create query with element equals or with regexp with {@code null}
   * values.
   *
   * @see QueryBuilder#withEqOrRegex(String, Iterable)
   */
  @Test
  public void shouldCreateQueryWithElementEqOrRegexWithNullValues() {

    final var builder = new QueryBuilder();
    assertThat(builder.withElementEqOrRegex("fieldName", "subFieldName", null)).isSameAs(builder);
    assertThat(builder.build()).isEqualTo(new JsonObject());

  }

  /**
   * Should create query with element equals or with regexp with empty values.
   *
   * @see QueryBuilder#withEqOrRegex(String, Iterable)
   */
  @Test
  public void shouldCreateQueryWithElementEqOrRegexWithEmptyValues() {

    final var builder = new QueryBuilder();
    assertThat(builder.withElementEqOrRegex("fieldName", "subFieldName", new ArrayList<>())).isSameAs(builder);
    assertThat(builder.build()).isEqualTo(new JsonObject());

  }

  /**
   * Should create query with element equals or with regexp.
   *
   * @see QueryBuilder#withEqOrRegex(String, Iterable)
   */
  @Test
  public void shouldCreateQueryWithElementEqOrRegex() {

    final var builder = new QueryBuilder();
    assertThat(builder.withElementEqOrRegex("fieldName", "subFieldName", Arrays.asList("value", "/re.*xp/", null)))
        .isSameAs(builder);
    assertThat(builder.build()).isEqualTo(new JsonObject().put("fieldName",
        new JsonObject().put("$all",
            new JsonArray()
                .add(new JsonObject().put("$elemMatch",
                    new JsonObject().put("subFieldName", new JsonObject().put("$eq", "value"))))
                .add(new JsonObject().put("$elemMatch",
                    new JsonObject().put("subFieldName", new JsonObject().put("$regex", "re.*xp")))))));

  }

  /**
   * Should create a regexp.
   *
   * @see QueryBuilder#withNoExistNullEqOrRegex(String, String)
   */
  @Test
  public void shouldWithNoExistNullEqOrRegexMatchRegexp() {

    final var builder = new QueryBuilder();
    assertThat(builder.withNoExistNullEqOrRegex("fieldName", "/re.*xp/")).isSameAs(builder);
    assertThat(builder.build()).isEqualTo(new JsonObject().put("fieldName", new JsonObject().put("$regex", "re.*xp")));

  }

  /**
   * Should create an equals value.
   *
   * @see QueryBuilder#withNoExistNullEqOrRegex(String, String)
   */
  @Test
  public void shouldWithNoExistNullEqOrRegexMatchEq() {

    final var builder = new QueryBuilder();
    assertThat(builder.withNoExistNullEqOrRegex("fieldName", "value")).isSameAs(builder);
    assertThat(builder.build()).isEqualTo(new JsonObject().put("fieldName", "value"));

  }

  /**
   * Should create an equals value.
   *
   * @see QueryBuilder#withNoExistNullEqOrRegex(String, String)
   */
  @Test
  public void shouldWithNoExistNullEqOrRegexMatchNoExsitOrNull() {

    final var builder = new QueryBuilder();
    assertThat(builder.withNoExistNullEqOrRegex("fieldName", null)).isSameAs(builder);
    assertThat(builder.build()).isEqualTo(new JsonObject().putNull("fieldName"));

  }

}
