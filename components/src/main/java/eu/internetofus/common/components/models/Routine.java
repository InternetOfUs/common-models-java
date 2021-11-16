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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eu.internetofus.common.components.WeNetValidateContext;
import eu.internetofus.common.model.JsonObjectDeserializer;
import eu.internetofus.common.model.Mergeable;
import eu.internetofus.common.model.Merges;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import eu.internetofus.common.model.Updateable;
import eu.internetofus.common.model.Validable;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/**
 * An activity that an user do regularly.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "Routine", description = "Labels distribution for a given user, time and weekday.")
public class Routine extends ReflectionModel implements Model, Validable<WeNetValidateContext>,
    Mergeable<Routine, WeNetValidateContext>, Updateable<Routine, WeNetValidateContext> {

  /**
   * The identifier of the user.
   */
  @Schema(description = "id of the user", nullable = true)
  public String user_id;

  /**
   * The day of the week.
   */
  @Schema(description = "day of the week", nullable = true)
  public String weekday;

  /**
   * The time slots.
   */
  @Schema(type = "object", description = "Time slots.", implementation = Object.class, nullable = true)
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  public JsonObject label_distribution;

  /**
   * The confidence of the result.
   */
  @Schema(description = "confidence of the result", nullable = true)
  public Double confidence;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final WeNetValidateContext context) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();

    future = context.validateDefinedProfileIdField("user_id", this.user_id, future);
    this.weekday = context.validateStringField("weekday", this.weekday, promise);
    if (this.label_distribution == null) {

      return context.failField("label_distribution", "The 'label_distribution' can not be null.");

    } else {

      for (final String fieldName : this.label_distribution.fieldNames()) {

        final var scorePrefix = "label_distribution." + fieldName;
        try {

          final var array = this.label_distribution.getJsonArray(fieldName);
          final var labels = Model.fromJsonArray(array, ScoredLabel.class);
          if (labels == null) {

            return context.failField(scorePrefix, "The '" + array + "' is not a valid array of ScoredLabel.");

          } else {

            future = future.compose(
                context.validateListField(scorePrefix, labels, (l1, l2) -> l1.label.name.equals(l2.label.name)));
          }

        } catch (final ClassCastException cause) {

          return context.failField(scorePrefix, "Does not contains an array of scored labels.", cause);
        }

      }

      if (this.confidence == null) {

        return context.failField("confidence", "The 'confidence' can not be null.");

      } else {

        promise.tryComplete();
      }
    }

    return future;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Routine> merge(final Routine source, final WeNetValidateContext context) {

    final Promise<Routine> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var merged = new Routine();
      merged.user_id = source.user_id;
      if (merged.user_id == null) {

        merged.user_id = this.user_id;
      }

      merged.weekday = source.weekday;
      if (merged.weekday == null) {

        merged.weekday = this.weekday;
      }

      merged.confidence = source.confidence;
      if (merged.confidence == null) {

        merged.confidence = this.confidence;
      }

      merged.label_distribution = source.label_distribution;
      if (merged.label_distribution == null) {

        merged.label_distribution = this.label_distribution;

      } else if (this.label_distribution != null) {

        for (final String fieldName : merged.label_distribution.fieldNames()) {

          final var scorePrefix = "label_distribution." + fieldName;
          try {

            final var sourceLabelDistributionArray = merged.label_distribution.getJsonArray(fieldName);
            final var sourceLabelDistribution = Model.fromJsonArray(sourceLabelDistributionArray, ScoredLabel.class);
            if (sourceLabelDistribution == null) {

              return context.failField(scorePrefix,
                  "The '" + sourceLabelDistributionArray + "' is not a valid array of ScoredLabel.");

            }
            final var targetLabelDistributionArray = this.label_distribution.getJsonArray(fieldName);
            final var targetLabelDistribution = Model.fromJsonArray(targetLabelDistributionArray, ScoredLabel.class);
            future = future
                .compose(Merges.mergeListField(context, scorePrefix, targetLabelDistribution, sourceLabelDistribution,
                    (BiPredicate<ScoredLabel, ScoredLabel>) (l1, l2) -> l1.label != null && l1.label.name != null
                        && l1.label.name.equals(l2.label.name),
                    (BiConsumer<Routine, List<ScoredLabel>>) (mergedRoutine, mergedScoredLabel) -> {

                      final var value = Model.toJsonArray(mergedScoredLabel);
                      mergedRoutine.label_distribution.put(fieldName, value);

                    }));

          } catch (final ClassCastException cause) {

            return context.failField(scorePrefix, "Does not contains an array of scored labels.", cause);
          }

        }

      }

      promise.complete(merged);

      // Validate the merged value
      future = future.compose(context.chain());

    } else {

      promise.complete(this);

    }
    return future;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Routine> update(final Routine source, final WeNetValidateContext context) {
    final Promise<Routine> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var updated = new Routine();
      updated.user_id = source.user_id;
      updated.weekday = source.weekday;
      updated.confidence = source.confidence;
      updated.label_distribution = source.label_distribution;

      promise.complete(updated);

      // Validate the updated value
      future = future.compose(context.chain());

    } else {

      promise.complete(this);

    }
    return future;
  }

  /**
   * Check if two routines are equivalent by its identifier fields.
   *
   * @param a first model to compare.
   * @param b second model to compare.
   *
   * @return {@code true} if the routines can be considered equals by its
   *         identifier.
   */
  static boolean compareIds(final Routine a, final Routine b) {

    return a != null && a.equals(b);

  }

}
