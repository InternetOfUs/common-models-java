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
import eu.internetofus.common.model.Validable;
import eu.internetofus.common.model.Validations;
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
public class Label extends ReflectionModel implements Model, Validable, Mergeable<Label> {

  /**
   * The name of the label.
   */
  @Schema(description = "name of the label", nullable = true)
  public String name;

  /**
   * The number that represent the category of the label.
   */
  @Schema(description = "number that represent the category of the label", nullable = true)
  public Double semantic_class;

  /**
   * The latitude of the label for the user.
   */
  @Schema(description = "latitude of the label for the user", example = "40.388756", nullable = true)
  public Double latitude;

  /**
   * The longitude of the label for the user.
   */
  @Schema(description = "The longitude of the label for the user", example = "-3.588622", nullable = true)
  public Double longitude;

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
    future = future.compose(empty -> Validations.validateNumberOnRange(codePrefix, "semantic_class",
        this.semantic_class, false, null, null));
    future = future
        .compose(empty -> Validations.validateNumberOnRange(codePrefix, "latitude", this.latitude, false, -90, 90));
    future = future
        .compose(empty -> Validations.validateNumberOnRange(codePrefix, "longitude", this.longitude, false, -180, 180));

    promise.complete();

    return future;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Label> merge(final Label source, final String codePrefix, final Vertx vertx) {

    final Promise<Label> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var merged = new Label();
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
      future = future.compose(Validations.validateChain(codePrefix, vertx));

    } else {

      promise.complete(this);

    }
    return future;

  }

}