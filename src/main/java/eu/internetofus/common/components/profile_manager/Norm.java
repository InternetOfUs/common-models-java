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
import java.util.UUID;

/**
 * The definition of a norm.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(description = "A norm that has to be satisfied.")
public class Norm extends ReflectionModel implements Model, Validable, Mergeable<Norm>, Updateable<Norm> {

  /**
   * The identifier of the norm.
   */
  @Schema(description = "The identifier of the norm.", example = "ceb84643-645a-4a55-9aaf-158370289eba")
  public String id;

  /**
   * The name of the attribute whose value the norm should be compared to.
   */
  @Schema(description = "The name of the attribute whose value the norm should	 be compared to.", example = "has_car")
  public String attribute;

  /**
   * The operator of the norm.
   */
  @Schema(description = "The operator of the norm.", example = "EQUALS")
  public NormOperator operator;

  /**
   * The norm value for the comparison.
   */
  @Schema(description = "The norm value for the comparison.", example = "true")
  public String comparison;

  /**
   * Specified if a negation operator should be applied.
   */
  @Schema(description = "The operator of the norm.", example = "true", defaultValue = "true")
  public boolean negation;

  /**
   * Create a new norm.
   */
  public Norm() {

    this.negation = true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    try {

      this.id = Validations.validateNullableStringField(codePrefix, "id", 255, this.id);
      if (this.id == null) {

        this.id = UUID.randomUUID().toString();
      }
      this.attribute = Validations.validateNullableStringField(codePrefix, "attribute", 255, this.attribute);
      this.comparison = Validations.validateNullableStringField(codePrefix, "comparison", 255, this.comparison);
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
  public Future<Norm> merge(final Norm source, final String codePrefix, final Vertx vertx) {

    final Promise<Norm> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var merged = new Norm();
      merged.attribute = source.attribute;
      if (merged.attribute == null) {

        merged.attribute = this.attribute;
      }

      merged.operator = source.operator;
      if (merged.operator == null) {

        merged.operator = this.operator;
      }

      merged.comparison = source.comparison;
      if (merged.comparison == null) {

        merged.comparison = this.comparison;
      }

      merged.negation = source.negation;

      promise.complete(merged);

      // validate the merged value and set the id
      future = future.compose(Validations.validateChain(codePrefix, vertx)).map(mergedValidatedModel -> {

        mergedValidatedModel.id = this.id;
        return mergedValidatedModel;
      });

    } else {

      promise.complete(this);
    }
    return future;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Norm> update(final Norm source, final String codePrefix, final Vertx vertx) {

    final Promise<Norm> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var updated = new Norm();
      updated.attribute = source.attribute;
      updated.operator = source.operator;
      updated.comparison = source.comparison;
      updated.negation = source.negation;

      promise.complete(updated);

      // validate the updated value and set the id
      future = future.compose(Validations.validateChain(codePrefix, vertx)).map(mergedValidatedModel -> {

        mergedValidatedModel.id = this.id;
        return mergedValidatedModel;
      });

    } else {

      promise.complete(this);
    }
    return future;
  }
}
