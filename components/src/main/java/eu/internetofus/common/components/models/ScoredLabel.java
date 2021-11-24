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
 * An activity that an user do regularly.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "ScoredLabel", description = "Label with score.")
public class ScoredLabel extends ReflectionModel
    implements Model, Validable<WeNetValidateContext>, Mergeable<ScoredLabel, WeNetValidateContext> {

  /**
   * The label.
   */
  @Schema(description = "label", nullable = true)
  public Label label;

  /**
   * The score of the label.
   */
  @Schema(description = "score of the label", nullable = true)
  public Double score;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final WeNetValidateContext context) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();

    future = future.compose(context.validateField("label", this.label));
    if (this.score == null) {

      return context.failField("score", "You must to define a score.");

    } else {

      promise.complete();
    }

    return future;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<ScoredLabel> merge(final ScoredLabel source, final WeNetValidateContext context) {

    final Promise<ScoredLabel> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var merged = new ScoredLabel();
      merged.score = source.score;
      if (merged.score == null) {

        merged.score = this.score;
      }

      future = future.compose(Merges.mergeField(context, "label", this.label, source.label,
          (model, mergedLabel) -> model.label = mergedLabel));

      promise.complete(merged);

      // validate the merged value and set the id
      future = future.compose(context.chain());

    } else {

      promise.complete(this);

    }
    return future;

  }

  /**
   * Check if two score labels are equivalent by its identifier fields.
   *
   * @param a first model to compare.
   * @param b second model to compare.
   *
   * @return {@code true} if the socre labels can be considered equals by its
   *         identifier.
   */
  public static boolean compareIds(final ScoredLabel a, final ScoredLabel b) {

    return a != null && b != null && Label.compareIds(a.label, b.label);

  }

}
