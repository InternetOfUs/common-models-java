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

package eu.internetofus.common.components;

import java.util.List;
import java.util.UUID;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * Basic implementation of a {@link Model}
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class DummyComplexModel extends DummyModel implements Validable, Mergeable<DummyComplexModel>, Updateable<DummyComplexModel> {

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
      future = future.compose(Merges.mergeFieldList(this.siblings, source.siblings, codePrefix + ".siblings", vertx, dummyComplexModel -> dummyComplexModel.id != null,
          (dummyCompleModel1, dummyComplexModel2) -> dummyCompleModel1.id.equals(dummyComplexModel2.id), (dummyComplexModel, siblings) -> dummyComplexModel.siblings = siblings));
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

      this.id = Validations.validateNullableStringField(codePrefix, "id", 255, this.id);
      if (this.id == null) {

        this.id = UUID.randomUUID().toString();
      }
      if (this.siblings != null) {

        future = future.compose(Validations.validate(this.siblings, (d1, d2) -> d1.id.equals(d2.id), codePrefix + ".siblings", vertx));
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
