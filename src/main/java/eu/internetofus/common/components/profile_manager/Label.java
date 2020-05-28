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

import eu.internetofus.common.components.Mergeable;
import eu.internetofus.common.components.Merges;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.ValidationErrorException;
import eu.internetofus.common.components.Validations;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * A label.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "Label", description = "label")
public class Label extends Model implements Validable, Mergeable<Label> {

  /**
   * The name of the label.
   */
  @Schema(description = "name of the label")
  public String name;

  /**
   * The number that represent the category of the label.
   */
  @Schema(description = "number that represent the category of the label")
  public Double semantic_class;

  /**
   * The latitude of the label for the user.
   */
  @Schema(description = "latitude of the label for the user", example = "40.388756")
  public Double latitude;

  /**
   * The longitude of the label for the user.
   */
  @Schema(description = "The longitude of the label for the user", example = "-3.588622")
  public Double longitude;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    try {

      this.name = Validations.validateStringField(codePrefix, "name", 255, this.name);
      if (this.semantic_class == null) {

        promise.fail(new ValidationErrorException(codePrefix + ".semantic_class", "You must to define a semantic_class."));

      } else if (this.latitude == null) {

        promise.fail(new ValidationErrorException(codePrefix + ".latitude", "You must to define a latitude."));

      } else if (this.longitude == null) {

        promise.fail(new ValidationErrorException(codePrefix + ".longitude", "You must to define a longitude."));

      } else if (this.latitude < -90 || this.latitude > 90) {

        promise.fail(new ValidationErrorException(codePrefix + ".latitude", "The latitude has to be on the range [-90,90]"));

      } else if (this.longitude < -180 || this.longitude > 180) {

        promise.fail(new ValidationErrorException(codePrefix + ".longitude", "The longitude has to be on the range [-180,180]"));

      } else {

        promise.complete();
      }

    } catch (final ValidationErrorException validationError) {

      promise.fail(validationError);
    }

    return promise.future();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Label> merge(final Label source, final String codePrefix, final Vertx vertx) {

    final Promise<Label> promise = Promise.promise();
    Future<Label> future = promise.future();
    if (source != null) {

      final Label merged = new Label();
      merged.name = source.name;
      if (merged.name == null) {

        merged.name = this.name;
      }
      merged.semantic_class = source.semantic_class;
      if (merged.semantic_class == null) {

        merged.semantic_class = this.semantic_class;
      }

      merged.latitude = source.latitude;
      if (merged.latitude == null) {

        merged.latitude = this.latitude;
      }

      merged.longitude = source.longitude;
      if (merged.longitude == null) {

        merged.longitude = this.longitude;
      }

      promise.complete(merged);

      // validate the merged value and set the id
      future = future.compose(Merges.validateMerged(codePrefix, vertx));

    } else {

      promise.complete(this);

    }
    return future;

  }

}
