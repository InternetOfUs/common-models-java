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

import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test the {@link AggregationBuilder}.
 *
 * @see AggregationBuilder
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class AggregationBuilderTest {

  /**
   * Should create empty query.
   */
  @Test
  public void shouldCreateEmptyCommand() {

    assertThat(new AggregationBuilder().build()).isEqualTo(new JsonArray());

  }

  /**
   * Split element to an empty array.
   *
   * @param elementPath to split.
   *
   * @see AggregationBuilder#splitElementPath(String)
   */
  @ParameterizedTest(name = "Split {0} to an empty array")
  @EmptySource
  @NullSource
  @ValueSource(strings = { "       " })
  public void shouldSplitEmptyElementPaths(String elementPath) {

    assertThat(AggregationBuilder.splitElementPath(elementPath)).isEqualTo(new String[0]);

  }

  /**
   * Split simple element path.
   *
   * @see AggregationBuilder#splitElementPath(String)
   */
  @Test
  public void shouldSplitSimpleElementPath() {

    assertThat(AggregationBuilder.splitElementPath("elements")).isEqualTo(new String[] { "elements" });

  }

  /**
   * Split complex element path.
   *
   * @see AggregationBuilder#splitElementPath(String)
   */
  @Test
  public void shouldSplitComplexElementPath() {

    assertThat(AggregationBuilder.splitElementPath("  a . b . c . d . e "))
        .isEqualTo(new String[] { "a", "b", "c", "d", "e" });

  }

  /**
   * Should create empty command when if not unwind values.
   *
   * @param element empty element to unwind.
   *
   * @see AggregationBuilder#unwind(String)
   */
  @ParameterizedTest(name = "Empty command when unwind element {0}")
  @EmptySource
  @NullSource
  @ValueSource(strings = { "       " })
  public void shouldCreateEmptyCommandWithEmptyUnwindElement(String element) {

    assertThat(new AggregationBuilder().unwind(element).build()).isEqualTo(new JsonArray());

  }

  /**
   * Should create empty command when the element path array is {@code null}.
   *
   * @see AggregationBuilder#unwindPath(String[])
   */
  @Test
  public void shouldCreateEmptyCommandUnwindNullElementPathArrray() {

    assertThat(new AggregationBuilder().unwindPath((String[]) null).build()).isEqualTo(new JsonArray());

  }

  /**
   * Should create empty command when the element path array is empty.
   *
   * @see AggregationBuilder#unwindPath(String[])
   */
  @Test
  public void shouldCreateEmptyCommandUnwindEmptylElementPathArrray() {

    assertThat(new AggregationBuilder().unwindPath(new String[0]).build()).isEqualTo(new JsonArray());

  }

  /**
   * Should create empty query with {@code null} pattern.
   *
   * @see AggregationBuilder#unwind(String)
   */
  @Test
  public void shouldCreateAggregationUnwindCommand() {

    var expected = new JsonArray();
    var builder = new AggregationBuilder();
    assertThat(builder.unwind("elements")).isSameAs(builder);
    expected.add(new JsonObject().put("$unwind",
        new JsonObject().put("path", "$elements").put("includeArrayIndex", "elementsIndex")));
    assertThat(builder.build()).isEqualTo(expected);

    assertThat(builder.unwind("transactions.messages")).isSameAs(builder);
    expected.add(new JsonObject().put("$unwind",
        new JsonObject().put("path", "$transactions").put("includeArrayIndex", "transactionsIndex")));
    expected.add(new JsonObject().put("$unwind",
        new JsonObject().put("path", "$transactions.messages").put("includeArrayIndex", "messagesIndex")));
    assertThat(builder.build()).isEqualTo(expected);

    assertThat(builder.unwind("a.b.c.d.e")).isSameAs(builder);
    expected
        .add(new JsonObject().put("$unwind", new JsonObject().put("path", "$a").put("includeArrayIndex", "aIndex")));
    expected
        .add(new JsonObject().put("$unwind", new JsonObject().put("path", "$a.b").put("includeArrayIndex", "bIndex")));
    expected.add(
        new JsonObject().put("$unwind", new JsonObject().put("path", "$a.b.c").put("includeArrayIndex", "cIndex")));
    expected.add(
        new JsonObject().put("$unwind", new JsonObject().put("path", "$a.b.c.d").put("includeArrayIndex", "dIndex")));
    expected.add(
        new JsonObject().put("$unwind", new JsonObject().put("path", "$a.b.c.d.e").put("includeArrayIndex", "eIndex")));
    assertThat(builder.build()).isEqualTo(expected);

  }

  /**
   * Should create empty command when match {@code null}.
   *
   * @see AggregationBuilder#match(JsonObject)
   */
  @Test
  public void shouldCreateEmptyCommandWithNullMatch() {

    assertThat(new AggregationBuilder().match(null).build()).isEqualTo(new JsonArray());

  }

  /**
   * Should create empty command when match empty query.
   *
   * @see AggregationBuilder#match(JsonObject)
   */
  @Test
  public void shouldCreateEmptyCommandWithEmptyMatch() {

    assertThat(new AggregationBuilder().match(new JsonObject()).build()).isEqualTo(new JsonArray());

  }

  /**
   * Should create command that match query.
   *
   * @see AggregationBuilder#match(JsonObject)
   */
  @Test
  public void shouldCreateCommandWithMatchQuery() {

    var query = new JsonObject().put("id", "value");
    assertThat(new AggregationBuilder().match(query).build())
        .isEqualTo(new JsonArray().add(new JsonObject().put("$match", query)));

  }

  /**
   * Should create command with a {@code null} order.
   *
   * @see AggregationBuilder#sort(JsonObject, int, int)
   */
  @Test
  public void shouldCreateCommandWithNullSort() {

    assertThat(new AggregationBuilder().sort(null, 0, 100).build())
        .isEqualTo(new JsonArray().add(new JsonObject().put("$limit", 100)));

  }

  /**
   * Should create command with an empty order.
   *
   * @see AggregationBuilder#sort(JsonObject, int, int)
   */
  @Test
  public void shouldCreateCommandWithEmptySort() {

    assertThat(new AggregationBuilder().sort(new JsonObject(), 1, 2).build())
        .isEqualTo(new JsonArray().add(new JsonObject().put("$limit", 3)).add(new JsonObject().put("$skip", 1)));

  }

  /**
   * Should create command with an order.
   *
   * @see AggregationBuilder#sort(JsonObject, int, int)
   */
  @Test
  public void shouldCreateCommandWithSort() {

    var order = new JsonObject().put("id", 1);
    assertThat(new AggregationBuilder().sort(order, 1, 2).build())
        .isEqualTo(new JsonArray().add(new JsonObject().put("$sort", order)).add(new JsonObject().put("$limit", 3))
            .add(new JsonObject().put("$skip", 1)));

  }

}
