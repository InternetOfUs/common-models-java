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

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

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
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

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
  @Schema(description = "id of the user")
  public String user_id;

  /**
   * The day of the week.
   */
  @Schema(description = "day of the week")
  public String weekday;

  /**
   * The time slots.
   */
  @Schema(type = "object", description = "Time slots.")
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  public JsonObject label_distribution;

  /**
   * The confidence of the result.
   */
  @Schema(description = "confidence of the result")
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

      this.user_id = Validations.validateStringField(codePrefix, "user_id", 255, this.user_id);
      future = future.compose(mapper -> {

        final Promise<Void> verifyUserIdExistPromise = Promise.promise();
        WeNetProfileManager.createProxy(vertx).retrieveProfile(this.user_id, search -> {

          if (!search.failed()) {

            verifyUserIdExistPromise.complete();

          } else {

            verifyUserIdExistPromise.fail(new ValidationErrorException(codePrefix + ".user_id", "The '" + this.user_id + "' is not defined.", search.cause()));
          }
        });
        return verifyUserIdExistPromise.future();
      });

      this.weekday = Validations.validateStringField(codePrefix, "weekday", 255, this.weekday);
      if (this.label_distribution == null) {

        promise.fail(new ValidationErrorException(codePrefix + ".label_distribution", "The 'label_distribution' can not be null."));

      } else {

        for (final String fieldName : this.label_distribution.fieldNames()) {

          try {

            final var array = this.label_distribution.getJsonArray(fieldName);
            final var labels = Model.fromJsonArray(array, ScoredLabel.class);
            final var scorePrefix = codePrefix + ".label_distribution." + fieldName;
            if (labels == null) {

              promise.fail(new ValidationErrorException(scorePrefix, "The '" + array + "' is not a valid array of ScoredLabel."));
              return future;

            } else {

              future = future.compose(Validations.validate(labels, (l1, l2) -> l1.label.name.equals(l2.label.name), scorePrefix, vertx));
            }

          } catch (final ClassCastException cause) {

            throw new ValidationErrorException(codePrefix + ".label_distribution." + fieldName, "Does not contains an array of scored labels.", cause);
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

              promise.fail(new ValidationErrorException(scorePrefix, "The '" + sourceLabelDistributionArray + "' is not a valid array of ScoredLabel."));
              return future;

            }
            final var targetLabelDistributionArray = this.label_distribution.getJsonArray(fieldName);
            final var targetLabelDistribution = Model.fromJsonArray(targetLabelDistributionArray, ScoredLabel.class);
            future = future.compose(Merges.mergeFieldList(targetLabelDistribution, sourceLabelDistribution, scorePrefix, vertx, (Predicate<ScoredLabel>) scoredLabel -> scoredLabel.label != null && scoredLabel.label.name != null,
                (BiPredicate<ScoredLabel, ScoredLabel>) (l1, l2) -> l1.label.name.equals(l2.label.name), (BiConsumer<Routine, List<ScoredLabel>>) (mergedRoutine, mergedScoredLabel) -> {

                  final var value = Model.toJsonArray(mergedScoredLabel);
                  mergedRoutine.label_distribution.put(fieldName, value);

                }));

          } catch (final ClassCastException cause) {

            promise.fail(new ValidationErrorException(codePrefix + ".label_distribution." + fieldName, "Does not contains an array of scored labels.", cause));
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
