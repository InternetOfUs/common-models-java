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
import eu.internetofus.common.model.Validable;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 * A label.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "Label", description = "label")
public class Label extends ReflectionModel
    implements Model, Validable<WeNetValidateContext>, Mergeable<Label, WeNetValidateContext> {

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
  public Future<Void> validate(final WeNetValidateContext context) {

    final Promise<Void> promise = Promise.promise();
    final var future = promise.future();

    this.name = context.validateStringField("name", this.name, promise);
    context.validateNumberOnRangeField("latitude", this.latitude, -90.0d, 90.0d, promise);
    context.validateNumberOnRangeField("longitude", this.longitude, -180.0d, 180.0d, promise);
    if (this.semantic_class == null) {

      return context.failField("semantic_class", "You must define the semantic class.");

    } else {

      promise.tryComplete();
      return future;
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Label> merge(final Label source, final WeNetValidateContext context) {

    final Promise<Label> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var merged = new Label();
      merged.name = Merges.mergeValues(this.name, source.name);
      merged.semantic_class = Merges.mergeValues(this.semantic_class, source.semantic_class);
      merged.latitude = Merges.mergeValues(this.latitude, source.latitude);
      merged.longitude = Merges.mergeValues(this.longitude, source.longitude);
      promise.complete(merged);

      // validate the merged value and set the id
      future = future.compose(context.chain());

    } else {

      promise.complete(this);

    }
    return future;

  }

  /**
   * Check if two labels are equivalent by its identifier fields.
   *
   * @param a first model to compare.
   * @param b second model to compare.
   *
   * @return {@code true} if the labels can be considered equals by its
   *         identifier.
   */
  public static boolean compareIds(final Label a, final Label b) {

    return a != null && b != null && a.name != null && a.name.equals(b.name);

  }

}
