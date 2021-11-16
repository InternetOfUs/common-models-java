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
 * A competence necessary for do a social practice.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "Competence", description = "It describe a competence of a user.")
public class Competence extends ReflectionModel implements Model, Validable<WeNetValidateContext>,
    Mergeable<Competence, WeNetValidateContext>, Updateable<Competence, WeNetValidateContext> {

  /**
   * The name of the competence.
   */
  @Schema(description = "The name of the competence", example = "language_Italian_C1", nullable = true)
  public String name;

  /**
   * The ontology of the competence.
   */
  @Schema(description = "The ontology of the competence, such as ESCO (https://ec.europa.eu/esco/portal) or ISCO (https://www.ilo.org/public/english/bureau/stat/isco/isco08/)", example = "esco", nullable = true)
  public String ontology;

  /**
   * The level of the competence.
   */
  @Schema(description = "The level of the competence (value in between 0 and 1, both included)", example = "1", nullable = true)
  public Double level;

  /**
   * Create a new empty competence.
   */
  public Competence() {

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final WeNetValidateContext context) {

    final Promise<Void> promise = Promise.promise();
    final var future = promise.future();
    this.name = context.validateStringField("name", this.name, promise);
    this.ontology = context.validateStringField("ontology", this.ontology, promise);
    context.validateNumberOnRangeField("level", this.level, 0.0d, 1.0d, promise);
    promise.tryComplete();
    return future;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Competence> merge(final Competence source, final WeNetValidateContext context) {

    final Promise<Competence> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var merged = new Competence();
      merged.name = source.name;
      if (merged.name == null) {

        merged.name = this.name;
      }

      merged.ontology = source.ontology;
      if (merged.ontology == null) {

        merged.ontology = this.ontology;
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
  public Future<Competence> update(final Competence source, final WeNetValidateContext context) {

    final Promise<Competence> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var updated = new Competence();
      updated.name = source.name;
      updated.ontology = source.ontology;
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
   * Check if two competences are equivalent by its identifier fields.
   *
   * @param a first model to compare.
   * @param b second model to compare.
   *
   * @return {@code true} if the competences can be considered equals by its
   *         identifier.
   */
  static boolean compareIds(final Competence a, final Competence b) {

    return a != null && a.name != null && a.name.equals(b.name) && a.ontology != null && a.ontology.equals(b.ontology);

  }

}
