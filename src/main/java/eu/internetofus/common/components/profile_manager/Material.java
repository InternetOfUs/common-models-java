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

import eu.internetofus.common.components.Mergeable;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ReflectionModel;
import eu.internetofus.common.components.Updateable;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.ValidationErrorException;
import eu.internetofus.common.components.Validations;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * A material necessary for do a social practice.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "Material", description = "It describes an object that is available to a user.")
public class Material extends ReflectionModel implements Model, Validable, Mergeable<Material>, Updateable<Material> {

  /**
   * The name of the material.
   */
  @Schema(description = "The name of the object", example = "car")
  public String name;

  /**
   * The description of the material.
   */
  @Schema(description = "A description of the object", example = "Fiat 500")
  public String description;

  /**
   * The quantity of the material.
   */
  @Schema(description = "The amount of units available", example = "1")
  public Integer quantity;

  /**
   * The classification of the material.
   *
   * <ul>
   * <li>Global Product Classification (https://www.gs1.org/standards/gpc)</li>
   * <li>NICE classification (Classification of Goods and Services)</li>
   * </ul>
   */
  @Schema(description = "The classification used for representing the object, such as Global Product Classification (https://www.gs1.org/standards/gpc) or NICE classification (Classification of Goods and Services)", example = "nice")
  public String classification;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    try {

      this.name = Validations.validateStringField(codePrefix, "name", 255, this.name);
      this.description = Validations.validateNullableStringField(codePrefix, "description", 1023, this.description);
      this.quantity = Validations.validateNumberOnRange(codePrefix, "quantity", this.quantity, false, 1, null);
      this.classification = Validations.validateStringField(codePrefix, "classification", 255, this.classification);
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
  public Future<Material> merge(final Material source, final String codePrefix, final Vertx vertx) {

    final Promise<Material> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var merged = new Material();
      merged.name = source.name;
      if (merged.name == null) {

        merged.name = this.name;
      }

      merged.description = source.description;
      if (merged.description == null) {

        merged.description = this.description;
      }

      merged.quantity = source.quantity;
      if (merged.quantity == null) {

        merged.quantity = this.quantity;
      }

      merged.classification = source.classification;
      if (merged.classification == null) {

        merged.classification = this.classification;
      }

      promise.complete(merged);

      // Validate the merged value
      future = future.compose(Validations.validateChain(codePrefix, vertx));

    } else {

      promise.complete(this);
    }
    return future;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Material> update(final Material source, final String codePrefix, final Vertx vertx) {

    final Promise<Material> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var updated = new Material();
      updated.name = source.name;
      updated.description = source.description;
      updated.quantity = source.quantity;
      updated.classification = source.classification;

      promise.complete(updated);

      // Validate the updated value
      future = future.compose(Validations.validateChain(codePrefix, vertx));

    } else {

      promise.complete(this);
    }
    return future;

  }

}
