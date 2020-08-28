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
 * A competence necessary for do a social practice.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "Competence", description = "It describe a competence of a user.")
public class Competence extends ReflectionModel implements Model, Validable, Mergeable<Competence> {

  /**
   * The name of the competence.
   */
  @Schema(description = "The name of the competence", example = "language_Italian_C1")
  public String name;

  /**
   * The ontology of the competence.
   */
  @Schema(description = "The ontology of the competence, such as ESCO (https://ec.europa.eu/esco/portal) or ISCO (https://www.ilo.org/public/english/bureau/stat/isco/isco08/)", example = "esco")
  public String ontology;

  /**
   * The level of the competence.
   */
  @Schema(description = "The level of the competence (value in between 0 and 1, both included)", example = "1")
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
    try {

      this.name = Validations.validateStringField(codePrefix, "name", 255, this.name);
      this.ontology = Validations.validateStringField(codePrefix, "ontology", 255, this.ontology);
      this.level = Validations.validateNumberOnRange(codePrefix, "level", this.level, false, 0d, 1d);
      promise.complete();

    } catch (final ValidationErrorException validationCause) {

      promise.fail(validationCause);
    }

    return promise.future();
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
      future = future.compose(Merges.validateMerged(codePrefix, vertx));

    } else {

      promise.complete(this);
    }
    return future;
  }

}
