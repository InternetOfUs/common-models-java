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

package eu.internetofus.common.components;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import java.util.List;
import java.util.UUID;

/**
 * Basic implementation of a {@link Model}
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class DummyComplexModel extends DummyModel
    implements Validable, Mergeable<DummyComplexModel>, Updateable<DummyComplexModel> {

  /**
   * The index of the model.
   */
  public String id;

  /**
   * The components that are siblings of the models.
   */
  public List<DummyComplexModel> siblings;

  /**
   * Create an empty model.
   */
  public DummyComplexModel() {

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<DummyComplexModel> merge(final DummyComplexModel source, final String codePrefix, final Vertx vertx) {

    final Promise<DummyComplexModel> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var merged = new DummyComplexModel();
      merged.index = source.index;
      merged.siblings = source.siblings;
      future = future.compose(Merges.mergeFieldList(this.siblings, source.siblings, codePrefix + ".siblings", vertx,
          dummyComplexModel -> dummyComplexModel.id != null,
          (dummyCompleModel1, dummyComplexModel2) -> dummyCompleModel1.id.equals(dummyComplexModel2.id),
          (dummyComplexModel, siblings) -> dummyComplexModel.siblings = siblings));
      future = future.compose(Validations.validateChain(codePrefix, vertx));
      future = future.map(mergedAndValidated -> {

        mergedAndValidated.id = this.id;
        return mergedAndValidated;
      });
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
    var future = promise.future();
    try {

      this.id = Validations.validateNullableStringField(codePrefix, "id", this.id);
      if (this.id == null) {

        this.id = UUID.randomUUID().toString();
      }
      if (this.siblings != null) {

        future = future.compose(
            Validations.validate(this.siblings, (d1, d2) -> d1.id.equals(d2.id), codePrefix + ".siblings", vertx));
      }
      promise.complete();

    } catch (final ValidationErrorException validationError) {

      promise.fail(validationError);
    }

    return future;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<DummyComplexModel> update(final DummyComplexModel source, final String codePrefix, final Vertx vertx) {

    final Promise<DummyComplexModel> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var updated = new DummyComplexModel();
      updated.index = source.index;
      updated.siblings = source.siblings;
      future = future.compose(Validations.validateChain(codePrefix, vertx));
      future = future.map(updatedAndValidated -> {

        updatedAndValidated.id = this.id;
        return updatedAndValidated;
      });
      promise.complete(updated);

    } else {

      promise.complete(this);
    }

    return future;
  }

}
