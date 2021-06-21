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
import eu.internetofus.common.components.Validations;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * The information of an user name.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, description = "The information of an user name.")
public class UserName extends ReflectionModel implements Model, Validable, Mergeable<UserName> {

  /**
   * The user name prefix.
   */
  @Schema(description = "The prefix of the name such as Mr., Mrs., Ms., Miss, or Dr.", example = "Dr.", nullable = true)
  public String prefix;

  /**
   * The user first name.
   */
  @Schema(description = "The first name (also known as a given name, forename or Christian name) is a part of a person's personal name.", example = "Abbey", nullable = true)
  public String first;

  /**
   * The user middle name.
   */
  @Schema(description = "The portion of a personal name that is written between the person's first name (given) and their last names (surname).", example = "Fitzgerald", nullable = true)
  public String middle;

  /**
   * The user last name.
   */
  @Schema(description = "The last name (surname or family name) is the portion (in some cultures) of a personal name that indicates a person's family (or tribe or community, depending on the culture).", example = "Smith", nullable = true)
  public String last;

  /**
   * The user name suffix.
   */
  @Schema(description = "The suffix of the name such as Jr., Sr., I, II, III, IV, V, MD, DDS, PhD or DVM.", example = "Jr.", nullable = true)
  public String suffix;

  /**
   * Create a new user name.
   */
  public UserName() {

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    try {

      this.prefix = Validations.validateNullableStringField(codePrefix, "prefix", this.prefix);
      this.first = Validations.validateNullableStringField(codePrefix, "first", this.first);
      this.middle = Validations.validateNullableStringField(codePrefix, "middle", this.middle);
      this.last = Validations.validateNullableStringField(codePrefix, "last", this.last);
      this.suffix = Validations.validateNullableStringField(codePrefix, "suffix", this.suffix);
      promise.complete();

    } catch (final ValidationErrorException validationError) {

      promise.fail(validationError);
    }

    return promise.future();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<UserName> merge(final UserName source, final String codePrefix, final Vertx vertx) {

    final Promise<UserName> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var merged = new UserName();
      merged.prefix = source.prefix;
      if (merged.prefix == null) {

        merged.prefix = this.prefix;
      }
      merged.first = source.first;
      if (merged.first == null) {

        merged.first = this.first;
      }
      merged.middle = source.middle;
      if (merged.middle == null) {

        merged.middle = this.middle;
      }
      merged.last = source.last;
      if (merged.last == null) {

        merged.last = this.last;
      }
      merged.suffix = source.suffix;
      if (merged.suffix == null) {

        merged.suffix = this.suffix;
      }

      promise.complete(merged);

      future = future.compose(Validations.validateChain(codePrefix, vertx));

    } else {

      promise.complete(this);
    }
    return future;

  }

}
