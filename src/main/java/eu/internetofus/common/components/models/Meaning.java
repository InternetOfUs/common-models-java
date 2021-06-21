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

/**
 * A meaning necessary for do a social practice.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "Meaning", description = "For defining more general purpose concepts.")
public class Meaning extends ReflectionModel implements Model, Validable, Mergeable<Meaning>, Updateable<Meaning> {

  /**
   * The name of the meaning.
   */
  @Schema(description = "The name of the concept represented by the meaning", example = "extraversion", nullable = true)
  public String name;

  /**
   * The category of the meaning.
   */
  @Schema(description = "The category associated to the meaning such as Post Jungian concepts (perception, judgment, extrovert, attitude, MBTI), Big Five concepts (Extraversion, Agreeableness, Conscientiousness,Neuroticism, Openness), Gardner intelligences(verbal, logicMathematics,visualSpatial, kinestesicaCorporal, musicalRhythmic,intrapersonal,interpersonal, naturalistEnvironmental), Other intelligences(practical, social, emotional, operative, successful,learning_potential), or Schwartz values (Self-Direction, Stimulation, Hedonism, Achievement, Power,Security, Conformity, Tradition, Benevolence, Universalism)", example = "big_five", nullable = true)
  public String category;

  /**
   * The level of the meaning.
   */
  @Schema(description = "The level associated to the concept", example = "1", nullable = true)
  public Double level;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    try {

      this.name = Validations.validateStringField(codePrefix, "name", this.name);
      this.category = Validations.validateStringField(codePrefix, "category", this.category);
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
  public Future<Meaning> update(final Meaning source, final String codePrefix, final Vertx vertx) {

    final Promise<Meaning> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var updated = new Meaning();
      updated.name = source.name;
      updated.category = source.category;
      updated.level = source.level;

      promise.complete(updated);

      // Validate the updated value
      future = future.compose(Validations.validateChain(codePrefix, vertx));

    } else {

      promise.complete(this);
    }
    return future;
  }

}
