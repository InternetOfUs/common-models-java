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
import eu.internetofus.common.model.Mergeable;
import eu.internetofus.common.model.Merges;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import eu.internetofus.common.model.Updateable;
import eu.internetofus.common.model.Validable;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;

/**
 * Represents a date.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "date", description = "The information of a date.")
public class ProfileDate extends ReflectionModel implements Model, Validable<WeNetValidateContext>,
    Mergeable<ProfileDate, WeNetValidateContext>, Updateable<ProfileDate, WeNetValidateContext> {

  /**
   * The year of the date.
   */
  @Schema(description = "The year of the date.", example = "1976", nullable = true)
  public Integer year;

  /**
   * The year of the date.
   */
  @Schema(description = "The month of the date,from 1 to 12 (1: Jan and 12: Dec).", example = "4", nullable = true)
  public Byte month;

  /**
   * The day of the date.
   */
  @Schema(description = "The day of the date.", example = "23", nullable = true)
  public Byte day;

  /**
   * Create an empty date.
   */
  public ProfileDate() {

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final WeNetValidateContext context) {

    if (this.month != null && (this.month < 1 || this.month > 12)) {

      return context.failField("month", "The day has to be on the range [1,12]");

    } else if (this.day != null && (this.day < 1 || this.day > 31)) {

      return context.failField("day", "The day has to be on the range [1,31]");

    } else if (this.year != null && this.month != null && this.day != null) {

      try {

        java.time.LocalDate.of(this.year, this.month, this.day);

      } catch (final Throwable exception) {

        return context.fail(exception);
      }

    }

    return Future.succeededFuture();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<ProfileDate> merge(final ProfileDate source, final WeNetValidateContext context) {

    if (source != null) {

      final var merged = this.createProfileDate();
      merged.day = Merges.mergeValues(this.day, source.day);
      merged.month = Merges.mergeValues(this.month, source.month);
      merged.year = Merges.mergeValues(this.year, source.year);
      return Future.succeededFuture(merged).compose(context.chain());

    } else {

      return Future.succeededFuture(this);

    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<ProfileDate> update(final ProfileDate source, final WeNetValidateContext context) {

    if (source != null) {

      final var updated = this.createProfileDate();
      updated.day = source.day;
      updated.month = source.month;
      updated.year = source.year;
      return Future.succeededFuture(updated).compose(context.chain());

    } else {

      return Future.succeededFuture(this);

    }

  }

  /**
   * Create a new profile date to use when merge or update this model.
   *
   * @return a new profile date.
   */
  protected ProfileDate createProfileDate() {

    return new ProfileDate();
  }

}
