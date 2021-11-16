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
import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 * A meaning necessary for do a social practice.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "Meaning", description = "For defining more general purpose concepts.")
public class Meaning extends ReflectionModel implements Model, Validable<WeNetValidateContext>,
    Mergeable<Meaning, WeNetValidateContext>, Updateable<Meaning, WeNetValidateContext> {

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
  public Future<Void> validate(final WeNetValidateContext context) {

    final Promise<Void> promise = Promise.promise();
    final var future = promise.future();

    this.name = context.validateStringField("name", this.name, promise);
    this.category = context.validateStringField("category", this.category, promise);
    if (this.level == null) {

      return context.failField("level", "You must to define a level");

    } else {

      promise.tryComplete();
      return future;
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Meaning> merge(final Meaning source, final WeNetValidateContext context) {

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
  public Future<Meaning> update(final Meaning source, final WeNetValidateContext context) {

    final Promise<Meaning> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var updated = new Meaning();
      updated.name = source.name;
      updated.category = source.category;
      updated.level = source.level;

      promise.complete(updated);

      // Validate the updated value
      future = future.compose(context.chain());

    } else {

      promise.complete(this);
    }
    return future;
  }

  /**
   * Check if two meanings are equivalent by its identifier fields.
   *
   * @param a first model to compare.
   * @param b second model to compare.
   *
   * @return {@code true} if the meanings can be considered equals by its
   *         identifier.
   */
  static boolean compareIds(final Meaning a, final Meaning b) {

    return a != null && a.name != null && a.name.equals(b.name) && a.category != null && a.category.equals(b.category);

  }

}
