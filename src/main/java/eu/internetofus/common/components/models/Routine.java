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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eu.internetofus.common.components.JsonObjectDeserializer;
import eu.internetofus.common.components.Mergeable;
import eu.internetofus.common.components.Merges;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ReflectionModel;
import eu.internetofus.common.components.Updateable;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.ValidationErrorException;
import eu.internetofus.common.components.Validations;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * An activity that an user do regularly.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "Routine", description = "Labels distribution for a given user, time and weekday.")
public class Routine extends ReflectionModel implements Model, Validable, Mergeable<Routine>, Updateable<Routine> {

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
   * Create an empty routine.
   */
  public Routine() {

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();
    try {

      this.user_id = Validations.validateStringField(codePrefix, "user_id", this.user_id);
      future = Validations.composeValidateId(future, codePrefix, "user_id", this.user_id, true,
          WeNetProfileManager.createProxy(vertx)::retrieveProfile);

      this.weekday = Validations.validateStringField(codePrefix, "weekday", this.weekday);
      if (this.label_distribution == null) {

        promise.fail(new ValidationErrorException(codePrefix + ".label_distribution",
            "The 'label_distribution' can not be null."));

      } else {

        for (final String fieldName : this.label_distribution.fieldNames()) {

          try {

            final var array = this.label_distribution.getJsonArray(fieldName);
            final var labels = Model.fromJsonArray(array, ScoredLabel.class);
            final var scorePrefix = codePrefix + ".label_distribution." + fieldName;
            if (labels == null) {

              promise.fail(new ValidationErrorException(scorePrefix,
                  "The '" + array + "' is not a valid array of ScoredLabel."));
              return future;

            } else {

              future = future.compose(
                  Validations.validate(labels, (l1, l2) -> l1.label.name.equals(l2.label.name), scorePrefix, vertx));
            }

          } catch (final ClassCastException cause) {

            throw new ValidationErrorException(codePrefix + ".label_distribution." + fieldName,
                "Does not contains an array of scored labels.", cause);
          }

        }

        if (this.confidence == null) {

          promise.fail(new ValidationErrorException(codePrefix + ".confidence", "The 'confidence' can not be null."));

        } else {

          promise.complete();
        }
      }

    } catch (final ValidationErrorException validationError) {

      promise.fail(validationError);
    }

    return future;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Routine> merge(final Routine source, final String codePrefix, final Vertx vertx) {

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

          try {

            final var sourceLabelDistributionArray = merged.label_distribution.getJsonArray(fieldName);
            final var sourceLabelDistribution = Model.fromJsonArray(sourceLabelDistributionArray, ScoredLabel.class);
            final var scorePrefix = codePrefix + ".label_distribution." + fieldName;
            if (sourceLabelDistribution == null) {

              promise.fail(new ValidationErrorException(scorePrefix,
                  "The '" + sourceLabelDistributionArray + "' is not a valid array of ScoredLabel."));
              return future;

            }
            final var targetLabelDistributionArray = this.label_distribution.getJsonArray(fieldName);
            final var targetLabelDistribution = Model.fromJsonArray(targetLabelDistributionArray, ScoredLabel.class);
            future = future
                .compose(Merges.mergeFieldList(targetLabelDistribution, sourceLabelDistribution, scorePrefix, vertx,
                    (Predicate<ScoredLabel>) scoredLabel -> scoredLabel.label != null && scoredLabel.label.name != null,
                    (BiPredicate<ScoredLabel, ScoredLabel>) (l1, l2) -> l1.label.name.equals(l2.label.name),
                    (BiConsumer<Routine, List<ScoredLabel>>) (mergedRoutine, mergedScoredLabel) -> {

                      final var value = Model.toJsonArray(mergedScoredLabel);
                      mergedRoutine.label_distribution.put(fieldName, value);

                    }));

          } catch (final ClassCastException cause) {

            promise.fail(new ValidationErrorException(codePrefix + ".label_distribution." + fieldName,
                "Does not contains an array of scored labels.", cause));
            return future;
          }

        }

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
  public Future<Routine> update(final Routine source, final String codePrefix, final Vertx vertx) {
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
      future = future.compose(Validations.validateChain(codePrefix, vertx));

    } else {

      promise.complete(this);

    }
    return future;
  }

}
