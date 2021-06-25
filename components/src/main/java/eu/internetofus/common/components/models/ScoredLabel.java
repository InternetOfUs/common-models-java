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

import eu.internetofus.common.components.Mergeable;
import eu.internetofus.common.components.Merges;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ReflectionModel;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.ValidationErrorException;
import eu.internetofus.common.components.Validations;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * An activity that an user do regularly.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "ScoredLabel", description = "Label with score.")
public class ScoredLabel extends ReflectionModel implements Model, Validable, Mergeable<ScoredLabel> {

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
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();

    if (this.label == null) {

      promise.fail(new ValidationErrorException(codePrefix + ".label", "You must to define a label."));

    } else {

      future = future.compose(mapper -> this.label.validate(codePrefix + ".label", vertx));
      if (this.score == null) {

        promise.fail(new ValidationErrorException(codePrefix + ".score", "You must to define a score."));

      } else {

        promise.complete();
      }
    }

    return future;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<ScoredLabel> merge(final ScoredLabel source, final String codePrefix, final Vertx vertx) {

    final Promise<ScoredLabel> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var merged = new ScoredLabel();
      merged.score = source.score;
      if (merged.score == null) {

        merged.score = this.score;
      }

      future = future.compose(Merges.mergeField(this.label, source.label, codePrefix + ".label", vertx,
          (model, mergedLabel) -> model.label = mergedLabel));

      promise.complete(merged);

      // validate the merged value and set the id
      future = future.compose(Validations.validateChain(codePrefix, vertx));

    } else {

      promise.complete(this);

    }
    return future;

  }

}
