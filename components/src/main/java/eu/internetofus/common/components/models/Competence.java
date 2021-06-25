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
import eu.internetofus.common.components.Validations;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * A competence necessary for do a social practice.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "Competence", description = "It describe a competence of a user.")
public class Competence extends ReflectionModel
    implements Model, Validable, Mergeable<Competence>, Updateable<Competence> {

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
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();
    future = future.compose(empty -> Validations.validateStringField(codePrefix, "name", this.name).map(name -> {
      this.name = name;
      return null;
    }));
    future = future
        .compose(empty -> Validations.validateStringField(codePrefix, "ontology", this.ontology).map(ontology -> {
          this.ontology = ontology;
          return null;
        }));
    future = future.compose(empty -> Validations.validateNumberOnRange(codePrefix, "level", this.level, false, 0d, 1d));
    promise.complete();
    return future;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Competence> merge(final Competence source, final String codePrefix, final Vertx vertx) {

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
  public Future<Competence> update(final Competence source, final String codePrefix, final Vertx vertx) {

    final Promise<Competence> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var updated = new Competence();
      updated.name = source.name;
      updated.ontology = source.ontology;
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
