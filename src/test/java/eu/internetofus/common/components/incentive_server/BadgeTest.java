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

import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ModelTestCase;
import eu.internetofus.common.components.ValidationsTest;
import eu.internetofus.common.components.profile_manager.WeNetUserProfile;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link Badge}.
 *
 * @see Badge
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class BadgeTest extends ModelTestCase<Badge> {

  /**
   * {@inheritDoc}
   */
  @Override
  public Badge createModelExample(final int index) {

    final Badge model = new Badge();
    model.BadgeClass = "class " + index;
    model.ImgUrl = "http://3.126.161.118:8000/media/uploads/badges/assertion-OYmfmtDFSIKG-qeZfXz4QQ.png?index=" + index;
    model.Criteria = "criteria " + index;
    model.Message = "message " + index;
    return model;
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

    final Badge model = this.createModelExample(index);
    final Badge expected = Model.fromJsonObject(model.toJsonObject(), Badge.class);
    model.BadgeClass = "   \n  " + model.BadgeClass + "   \n";
    model.Criteria = "   \n  " + model.Criteria + "   \n";
    model.ImgUrl = "   \n  " + model.ImgUrl + "   \n";
    model.Message = "   \n  " + model.Message + "   \n";
    assertIsValid(model, vertx, testContext, () -> {

      assertThat(model).isEqualTo(expected);
    });

  }

  /**
   * Check that not accept a large class.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithALargeClass(final Vertx vertx, final VertxTestContext testContext) {

    final Badge model = this.createModelExample(1);
    model.BadgeClass = ValidationsTest.STRING_256;
    assertIsNotValid(model, "BadgeClass", vertx, testContext);
  }

  /**
   * Check that not accept a large criteria.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithALargeCriteria(final Vertx vertx, final VertxTestContext testContext) {

    final Badge model = this.createModelExample(1);
    model.Criteria = ValidationsTest.STRING_256;
    assertIsNotValid(model, "Criteria", vertx, testContext);
  }

  /**
   * Check that not accept a large message.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithALargeMessage(final Vertx vertx, final VertxTestContext testContext) {

    final Badge model = this.createModelExample(1);
    model.Message = ValidationsTest.STRING_1024;
    assertIsNotValid(model, "Message", vertx, testContext);
  }

  /**
   * Check that not accept a bad image URL.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadImgUrl(final Vertx vertx, final VertxTestContext testContext) {

    final Badge model = this.createModelExample(1);
    model.ImgUrl = ValidationsTest.STRING_1024;
    assertIsNotValid(model, "ImgUrl", vertx, testContext);
  }

}
