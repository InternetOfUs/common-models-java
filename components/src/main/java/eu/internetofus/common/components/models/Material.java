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

import eu.internetofus.common.model.Mergeable;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import eu.internetofus.common.model.Updateable;
import eu.internetofus.common.model.Validable;
import eu.internetofus.common.model.Validations;
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
    var future = promise.future();

    future = future.compose(empty -> Validations.validateStringField(codePrefix, "name", this.name).map(name -> {
      this.name = name;
      return null;
    }));
    future = future.compose(empty -> Validations
        .validateNullableStringField(codePrefix, "description", this.description).map(description -> {
          this.description = description;
          return null;
        }));
    future = future
        .compose(empty -> Validations.validateNumberOnRange(codePrefix, "quantity", this.quantity, false, 1, null));
    future = future.compose(empty -> Validations.validateStringField(codePrefix, "classification", this.classification)
        .map(classification -> {
          this.classification = classification;
          return null;
        }));
    promise.complete();

    return future;

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
