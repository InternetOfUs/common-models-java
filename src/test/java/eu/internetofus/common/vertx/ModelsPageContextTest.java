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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.vertx.core.json.JsonObject;

/**
 * Test the {@link ModelsPageContext}.
 *
 * @see ModelsPageContext
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ModelsPageContextTest {

  /**
   * Check to string conversion of empty context.
   *
   * @see ModelsPageContext#toString()
   */
  @Test
  public void shoulCovertToStringEmptyContext() {

    final var context = new ModelsPageContext();
    assertThat(context.toString()).isEqualTo("offset:0\nlimit:0\n");

  }

  /**
   * Check to string conversion with a query value.
   *
   * @see ModelsPageContext#toString()
   */
  @Test
  public void shoulCovertToStringWithQuery() {

    final var context = new ModelsPageContext();
    context.query = new JsonObject().put("key", "value").put("element", new JsonObject().put("key", "value"));
    context.offset = 10;
    context.limit = 20;
    assertThat(context.toString()).isEqualTo("query:\n" + context.query.encodePrettily() + "\noffset:10\nlimit:20\n");

  }

  /**
   * Check to string conversion with a sort value.
   *
   * @see ModelsPageContext#toString()
   */
  @Test
  public void shoulCovertToStringWithSort() {

    final var context = new ModelsPageContext();
    context.sort = new JsonObject().put("key", "value").put("element", new JsonObject().put("key", "value"));
    context.offset = 20;
    context.limit = 10;
    assertThat(context.toString()).isEqualTo("sort:\n" + context.sort.encodePrettily() + "\noffset:20\nlimit:10\n");

  }

  /**
   * Check to string conversion.
   *
   * @see ModelsPageContext#toString()
   */
  @Test
  public void shoulCovertToString() {

    final var context = new ModelsPageContext();
    context.query = new JsonObject().put("key", "value").put("element", new JsonObject().put("key", "value"));

    context.sort = new JsonObject().put("key", "value").put("element", new JsonObject().put("key", "value"));
    context.offset = 1;
    context.limit = 2;
    assertThat(context.toString()).isEqualTo("query:\n" + context.query.encodePrettily() + "\nsort:\n" + context.sort.encodePrettily() + "\noffset:1\nlimit:2\n");

  }

  /**
   * Check to find options conversion.
   *
   * @see ModelsPageContext#toFindOptions()
   */
  @Test
  public void shoulCovertToFindOptions() {

    final var context = new ModelsPageContext();
    context.query = new JsonObject().put("key", "value").put("element", new JsonObject().put("key", "value"));
    context.sort = new JsonObject().put("key", "value").put("element", new JsonObject().put("key", "value"));
    context.offset = 1;
    context.limit = 2;
    final var options = context.toFindOptions();
    assertThat(options.getSort()).isEqualTo(context.sort);
    assertThat(options.getSkip()).isEqualTo(1);
    assertThat(options.getLimit()).isEqualTo(2);

  }

  /**
   * Check to find options conversion without sort.
   *
   * @see ModelsPageContext#toFindOptions()
   */
  @Test
  public void shoulCovertToFindOptionsWithoutSort() {

    final var context = new ModelsPageContext();
    context.query = new JsonObject().put("key", "value").put("element", new JsonObject().put("key", "value"));
    context.offset = 11;
    context.limit = 222;
    final var options = context.toFindOptions();
    assertThat(options.getSort()).isEqualTo(new JsonObject());
    assertThat(options.getSkip()).isEqualTo(11);
    assertThat(options.getLimit()).isEqualTo(222);

  }

}
