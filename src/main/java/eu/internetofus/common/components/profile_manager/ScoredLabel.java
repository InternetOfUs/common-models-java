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
  @Schema(description = "label")
  public Label label;

  /**
   * The score of the label.
   */
  @Schema(description = "score of the label")
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

      future = future.compose(Merges.mergeField(this.label, source.label, codePrefix + ".label", vertx, (model, mergedLabel) -> model.label = mergedLabel));

      promise.complete(merged);

      // validate the merged value and set the id
      future = future.compose(Validations.validateChain(codePrefix, vertx));

    } else {

      promise.complete(this);

    }
    return future;

  }

}
