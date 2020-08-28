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

package eu.internetofus.common.components;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * Basic implementation of a {@link Model}
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class DummyComplexModel extends DummyModel implements Validable, Mergeable<DummyComplexModel> {

  /**
   * The index of the model.
   */
  public String id;

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
      merged.id = source.id;
      if (merged.id == null) {

        merged.id = this.id;
      }

      merged.index = source.index;

      future = future.compose(Merges.validateMerged(codePrefix, vertx));
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
    final var future = promise.future();
    try {

      this.id = Validations.validateStringField(codePrefix, "id", 255, this.id);
      promise.complete();

    } catch (final ValidationErrorException validationError) {

      promise.fail(validationError);
    }

    return future;
  }

}
