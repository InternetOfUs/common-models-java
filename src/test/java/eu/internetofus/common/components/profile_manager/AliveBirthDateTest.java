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

package eu.internetofus.common.components.profile_manager;

import static eu.internetofus.common.components.MergesTest.assertCannotMerge;
import static eu.internetofus.common.components.ValidationsTest.assertIsNotValid;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link AliveBirthDate}.
 *
 * @see AliveBirthDate
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class AliveBirthDateTest extends ProfileDateTestCase<AliveBirthDate> {

  /**
   * {@inheritDoc}
   */
  @Override
  public AliveBirthDate createEmptyModel() {

    return new AliveBirthDate();
  }

  /**
   * Should not be valid to born in the future.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see AliveBirthDate#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidToBornOnTheFuture(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createEmptyModel();
    final var tomorrow = LocalDate.now().plusDays(1);
    model.year = tomorrow.getYear();
    model.month = (byte) tomorrow.getMonthValue();
    model.day = (byte) tomorrow.getDayOfMonth();
    assertIsNotValid(model, vertx, testContext);

  }

  /**
   * Should not be valid to born before the oldest person on the world.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see AliveBirthDate#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidToBornBeforeTheOldestPersonOnTheWorld(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createEmptyModel();
    model.year = 1903;
    model.month = 1;
    model.day = 1;
    assertIsNotValid(model, vertx, testContext);
  }

  /**
   * Should not merge to born in the future.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see AliveBirthDate#merge(ProfileDate, String, Vertx)
   */
  @Test
  public void shouldNotMergeToBornOnTheFuture(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createEmptyModel();
    final var source = this.createEmptyModel();
    final var tomorrow = LocalDate.now().plusDays(1);
    source.year = tomorrow.getYear();
    source.month = (byte) tomorrow.getMonthValue();
    source.day = (byte) tomorrow.getDayOfMonth();
    assertCannotMerge(target, source, vertx, testContext);

  }

  /**
   * Should not merge to born before the oldest person on the world.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see AliveBirthDate#merge(ProfileDate, String, Vertx)
   */
  @Test
  public void shouldNotMergeToBornBeforeTheOldestPersonOnTheWorld(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createEmptyModel();
    final var source = this.createEmptyModel();
    source.year = 1903;
    source.month = 1;
    source.day = 1;
    assertCannotMerge(target, source, vertx, testContext);

  }

}
