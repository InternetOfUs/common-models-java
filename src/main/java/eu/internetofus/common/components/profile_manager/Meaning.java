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
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
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
 * A meaning necessary for do a social practice.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "Meaning", description = "For defining more general purpose concepts.")
public class Meaning extends ReflectionModel implements Model, Validable, Mergeable<Meaning> {

  /**
   * The name of the meaning.
   */
  @Schema(description = "The name of the concept represented by the meaning", example = "extraversion")
  public String name;

  /**
   * The category of the meaning.
   */
  @Schema(description = "The category associated to the meaning such as Post Jungian concepts (perception, judgment, extrovert, attitude, MBTI), Big Five concepts (Extraversion, Agreeableness, Conscientiousness,Neuroticism, Openness), Gardner intelligences(verbal, logicMathematics,visualSpatial, kinestesicaCorporal, musicalRhythmic,intrapersonal,interpersonal, naturalistEnvironmental), Other intelligences(practical, social, emotional, operative, successful,learning_potential), or Schwartz values (Self-Direction, Stimulation, Hedonism, Achievement, Power,Security, Conformity, Tradition, Benevolence, Universalism)", example = "big_five")
  public String category;

  /**
   * The level of the meaning.
   */
  @Schema(description = "The level associated to the concept", example = "1")
  public Double level;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    try {

      this.name = Validations.validateStringField(codePrefix, "name", 255, this.name);
      this.category = Validations.validateStringField(codePrefix, "category", 255, this.category);
      this.level = Validations.validateNumberOnRange(codePrefix, "level", this.level, false, null, null);
      promise.complete();

    } catch (final ValidationErrorException validationError) {

      promise.fail(validationError);
    }
    return promise.future();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Meaning> merge(final Meaning source, final String codePrefix, final Vertx vertx) {

    final Promise<Meaning> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var merged = new Meaning();
      merged.name = source.name;
      if (merged.name == null) {

        merged.name = this.name;
      }

      merged.category = source.category;
      if (merged.category == null) {

        merged.category = this.category;
      }

      merged.level = source.level;
      if (merged.level == null) {

        merged.level = this.level;
      }

      promise.complete(merged);

      // Validate the merged value
      future = future.compose(Merges.validateMerged(codePrefix, vertx));

    } else {

      promise.complete(this);
    }
    return future;
  }

}
