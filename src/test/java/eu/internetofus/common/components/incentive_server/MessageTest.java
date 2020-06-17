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

package eu.internetofus.common.components.incentive_server;

import static eu.internetofus.common.components.ValidationsTest.assertIsNotValid;
import static eu.internetofus.common.components.ValidationsTest.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import eu.internetofus.common.components.ModelTestCase;
import eu.internetofus.common.components.ValidationsTest;
import eu.internetofus.common.components.profile_manager.WeNetUserProfile;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link Message}.
 *
 * @see Message
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class MessageTest extends ModelTestCase<Message> {

  /**
   * {@inheritDoc}
   */
  @Override
  public Message createModelExample(final int index) {

    final Message model = new Message();
    model.content = "content " + index;
    return model;
  }

  /**
   * Check that not accept a large content.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithALargeContent(final Vertx vertx, final VertxTestContext testContext) {

    final Message model = this.createModelExample(1);
    model.content = ValidationsTest.STRING_1024;
    assertIsNotValid(model, "content", vertx, testContext);
  }

  /**
   * Check that not accept without content.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithoutContent(final Vertx vertx, final VertxTestContext testContext) {

    final Message model = this.createModelExample(1);
    model.content = null;
    assertIsNotValid(model, "content", vertx, testContext);
  }

  /**
   * Check that the {@link #createModelExample(int)} is valid.
   *
   * @param index       to verify
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final Message model = this.createModelExample(index);
    final String expected = model.content;
    model.content = "   \n  " + expected + "   \n";
    assertIsValid(model, vertx, testContext, () -> {

      assertThat(model.content).isEqualTo(expected);
    });

  }

}
