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

package eu.internetofus.common.components.task_manager;

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
 * A norm used in a protocol.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "ProtocolNorm", description = "The description of a rule that has to follow by the wenet users.")
public class ProtocolNorm extends ReflectionModel implements Model, Validable, Mergeable<ProtocolNorm>, Updateable<ProtocolNorm> {

  /**
   * The conditions that fires the norms.
   */
  @Schema(description = "The conditions that fires this norm.")
  public String whenever;

  /**
   * The action to do if the conditions are satisfied.
   */
  @Schema(description = "The conditions that fires this norm.")
  public String thenceforth;

  /**
   * The ontology used on the norms.
   */
  @Schema(description = "The ontology used on the norms.")
  public String ontology;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<ProtocolNorm> update(final ProtocolNorm source, final String codePrefix, final Vertx vertx) {

    final Promise<ProtocolNorm> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var updated = new ProtocolNorm();
      updated.whenever = source.whenever;
      updated.thenceforth = source.thenceforth;
      updated.ontology = source.ontology;

      future = future.compose(Validations.validateChain(codePrefix, vertx));
      promise.complete(updated);

    } else {

      promise.complete(this);
    }
    return future;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<ProtocolNorm> merge(final ProtocolNorm source, final String codePrefix, final Vertx vertx) {

    final Promise<ProtocolNorm> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var merged = new ProtocolNorm();
      merged.whenever = source.whenever;
      if (merged.whenever == null) {

        merged.whenever = this.whenever;
      }
      merged.thenceforth = source.thenceforth;
      if (merged.thenceforth == null) {

        merged.thenceforth = this.thenceforth;
      }

      merged.ontology = source.ontology;
      if (merged.ontology == null) {

        merged.ontology = this.ontology;
      }

      future = future.compose(Validations.validateChain(codePrefix, vertx));
      promise.complete(merged);

    } else {

      promise.complete(this);
    }
    return future;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    try {

      this.whenever = Validations.validateStringField(codePrefix, "whenever", Integer.MAX_VALUE, this.whenever);
      this.thenceforth = Validations.validateStringField(codePrefix, "thenceforth", Integer.MAX_VALUE, this.thenceforth);
      this.ontology = Validations.validateNullableStringField(codePrefix, "ontology", Integer.MAX_VALUE, this.ontology);
      promise.complete();

    } catch (final ValidationErrorException validationError) {

      promise.fail(validationError);
    }

    return promise.future();

  }

}
