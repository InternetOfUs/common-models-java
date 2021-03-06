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

import eu.internetofus.common.components.WeNetValidateContext;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import java.time.LocalDate;

/**
 * A birth date of an alive person.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "date", description = "The information of a alive birth date.")
public class AliveBirthDate extends ProfileDate {

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final WeNetValidateContext context) {

    return super.validate(context).compose(mapper -> {

      if (this.year != null && this.month != null && this.day != null) {

        final var birthDate = LocalDate.of(this.year, this.month, this.day);
        if (birthDate.isAfter(LocalDate.now())) {

          return context.fail("The birth date can not be on the future");

        } else if (birthDate.isBefore(LocalDate.of(1903, 1, 2))) {

          return context.fail("The user can not be born before Kane Tanake, the oldest living person on earth");

        }
      }
      return Future.succeededFuture();

    });
  }

  /**
   * {@inheritDoc}
   *
   * @see AliveBirthDate#AliveBirthDate
   */
  @Override
  protected ProfileDate createProfileDate() {

    return new AliveBirthDate();
  }

}
