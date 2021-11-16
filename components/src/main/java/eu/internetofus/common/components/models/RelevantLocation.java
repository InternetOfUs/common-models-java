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
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import eu.internetofus.common.model.Updateable;
import eu.internetofus.common.model.Validable;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import java.util.UUID;

/**
 * A location of interest for the user, may be the home or work location. This
 * information is generated by the platform AI.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, description = "A location of interest for the user - may be the home or work location -. This information is generated by the platform AI.")
public class RelevantLocation extends ReflectionModel implements Model, Validable<WeNetValidateContext>,
    Mergeable<RelevantLocation, WeNetValidateContext>, Updateable<RelevantLocation, WeNetValidateContext> {

  /**
   * The identifier of the relevant location.
   */
  @Schema(description = "The identifier of the location", example = "kdjfghd8hikdfg", accessMode = AccessMode.READ_ONLY)
  public String id;

  /**
   * The descriptor of the location.
   */
  @Schema(description = "The descriptor of the location", example = "Home", nullable = true)
  public String label;

  /**
   * The latitude of the location.
   */
  @Schema(description = "The latitude of the location", example = "40.388756", nullable = true)
  public Double latitude;

  /**
   * The longitude of the location.
   */
  @Schema(description = "The longitude of the location", example = "-3.588622", nullable = true)
  public Double longitude;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final WeNetValidateContext context) {

    final Promise<Void> promise = Promise.promise();
    final var future = promise.future();

    if (this.id == null) {

      this.id = UUID.randomUUID().toString();

    } else {

      this.id = context.normalizeString(this.id);
    }

    this.label = context.normalizeString(this.label);
    context.validateNumberOnRangeField("latitude", this.latitude, -90.0d, 90.0d, promise);
    context.validateNumberOnRangeField("longitude", this.longitude, -180.0d, 180.0d, promise);

    promise.tryComplete();

    return future;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<RelevantLocation> merge(final RelevantLocation source, final WeNetValidateContext context) {

    final Promise<RelevantLocation> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var merged = new RelevantLocation();

      merged.label = source.label;
      if (merged.label == null) {

        merged.label = this.label;
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

      // Validate the merged value and set the id
      future = future.compose(context.chain()).map(mergedValidatedModel -> {

        mergedValidatedModel.id = this.id;
        return mergedValidatedModel;
      });

    } else {

      promise.complete(this);

    }
    return future;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<RelevantLocation> update(final RelevantLocation source, final WeNetValidateContext context) {

    final Promise<RelevantLocation> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var updated = new RelevantLocation();

      updated.label = source.label;
      updated.latitude = source.latitude;
      updated.longitude = source.longitude;

      promise.complete(updated);

      // Validate the updated value and set the id
      future = future.compose(context.chain()).map(updatedValidatedModel -> {

        updatedValidatedModel.id = this.id;
        return updatedValidatedModel;
      });

    } else {

      promise.complete(this);

    }
    return future;
  }

  /**
   * Check if two relevant locations are equivalent by its identifier fields.
   *
   * @param a first model to compare.
   * @param b second model to compare.
   *
   * @return {@code true} if the relevant locations can be considered equals by
   *         its identifier.
   */
  static boolean compareIds(final RelevantLocation a, final RelevantLocation b) {

    return a != null && a.id != null && a.id.equals(b.id);

  }

}
