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

      this.id = Validations.validateNullableStringField(codePrefix, "id", this.id);
      if (this.id == null) {

        this.id = UUID.randomUUID().toString();
      }
      this.attribute = Validations.validateNullableStringField(codePrefix, "attribute", this.attribute);
      this.comparison = Validations.validateNullableStringField(codePrefix, "comparison", this.comparison);
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
