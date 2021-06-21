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

package eu.internetofus.common.components.models;

import eu.internetofus.common.components.Mergeable;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ReflectionModel;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.ValidationErrorException;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * Represents a date.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "date", description = "The information of a date.")
public class ProfileDate extends ReflectionModel implements Model, Validable, Mergeable<ProfileDate> {

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
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    if (this.month != null && (this.month < 1 || this.month > 12)) {

      promise.fail(new ValidationErrorException(codePrefix + ".month", "The month has to be on the range [1,12]"));

    } else if (this.day != null && (this.day < 1 || this.day > 31)) {

      promise.fail(new ValidationErrorException(codePrefix + ".day", "The day has to be on the range [1,31]"));

    } else if (this.year != null && this.month != null && this.day != null) {

      try {

        java.time.LocalDate.of(this.year, this.month, this.day);
        promise.complete();

      } catch (final Throwable exception) {

        promise.fail(new ValidationErrorException(codePrefix, exception));
      }

    } else {
      // not enough data to check the date
      promise.complete();
    }

    return promise.future();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<ProfileDate> merge(final ProfileDate source, final String codePrefix, final Vertx vertx) {

    if (source != null) {

      final var merged = this.createProfileDate();
      if (source.year != null) {

        merged.year = source.year;

      } else {

        merged.year = this.year;
      }
      if (source.month != null) {

        merged.month = source.month;

      } else {

        merged.month = this.month;
      }
      if (source.day != null) {

        merged.day = source.day;

      } else {

        merged.day = this.day;
      }

      return merged.validate(codePrefix, vertx).map(empty -> merged);

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
