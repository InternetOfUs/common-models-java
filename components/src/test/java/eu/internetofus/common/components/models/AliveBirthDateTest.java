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

package eu.internetofus.common.components.models;

import static eu.internetofus.common.model.MergeAsserts.assertCannotMerge;
import static eu.internetofus.common.model.ValidableAsserts.assertIsNotValid;

import eu.internetofus.common.components.WeNetValidateContext;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

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
   * @see AliveBirthDate#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidToBornOnTheFuture(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createEmptyModel();
    final var tomorrow = LocalDate.now().plusDays(1);
    model.year = tomorrow.getYear();
    model.month = (byte) tomorrow.getMonthValue();
    model.day = (byte) tomorrow.getDayOfMonth();
    assertIsNotValid(model, new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Should not be valid to born before the oldest person on the world.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see AliveBirthDate#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidToBornBeforeTheOldestPersonOnTheWorld(final Vertx vertx,
      final VertxTestContext testContext) {

    final var model = this.createEmptyModel();
    model.year = 1903;
    model.month = 1;
    model.day = 1;
    assertIsNotValid(model, new WeNetValidateContext("codePrefix", vertx), testContext);
  }

  /**
   * Should not merge to born in the future.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see AliveBirthDate#merge(ProfileDate, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeToBornOnTheFuture(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createEmptyModel();
    final var source = this.createEmptyModel();
    final var tomorrow = LocalDate.now().plusDays(1);
    source.year = tomorrow.getYear();
    source.month = (byte) tomorrow.getMonthValue();
    source.day = (byte) tomorrow.getDayOfMonth();
    assertCannotMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Should not merge to born before the oldest person on the world.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see AliveBirthDate#merge(ProfileDate, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeToBornBeforeTheOldestPersonOnTheWorld(final Vertx vertx,
      final VertxTestContext testContext) {

    final var target = this.createEmptyModel();
    final var source = this.createEmptyModel();
    source.year = 1903;
    source.month = 1;
    source.day = 1;
    assertCannotMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext);

  }

}
