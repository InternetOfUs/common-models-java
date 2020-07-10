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

package eu.internetofus.common.components.profile_manager;

import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import eu.internetofus.common.components.Mergeable;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.ValidationErrorException;
import eu.internetofus.common.components.Validations;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * A competence necessary for do a social practice.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(description = "A competence necessary for do a social practice.")
@JsonDeserialize(using = CompetenceDeserialize.class)
public class Competence extends Model implements Validable, Mergeable<Competence> {

  /**
   * The identifier of the competence.
   */
  @Schema(description = "The identifier of the competence", example = "aisufh9sdokjnd")
  public String id;

  /**
   * Create a new empty competence.
   */
  public Competence() {

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    try {

      this.id = Validations.validateNullableStringField(codePrefix, "id", 255, this.id);
      if (this.id == null) {

        this.id = UUID.randomUUID().toString();
      }
      promise.complete();

    } catch (final ValidationErrorException validationCause) {

      promise.fail(validationCause);
    }

    return promise.future();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Competence> merge(final Competence source, final String codePrefix, final Vertx vertx) {

    if (source == null) {

      return Future.succeededFuture(this);

    } else if ((source.id == null || this.id.equals(source.id)) && this instanceof DrivingLicense && source instanceof DrivingLicense) {

      return ((DrivingLicense) this).mergeDrivingLicense((DrivingLicense) source, codePrefix, vertx).map(license -> {

        license.id = this.id;
        return (Competence) license;

      });

    } else {

      return source.validate(codePrefix, vertx).map(validation -> {
        source.id = this.id;
        return source;
      });

    }

  }

}
