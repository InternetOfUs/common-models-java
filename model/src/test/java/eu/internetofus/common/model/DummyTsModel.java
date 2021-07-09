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
package eu.internetofus.common.model;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import java.util.List;

/**
 * Dummy model with timestamp.
 *
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class DummyTsModel extends CreateUpdateTsDetails
    implements Model, Validable, Updateable<DummyTsModel>, Mergeable<DummyTsModel> {

  /**
   * The identifier of the model.
   */
  public String _id;

  /**
   * The value of the model.
   */
  public String value;

  /**
   * A list of elements.
   */
  public List<DummyTsModel> dummies;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<DummyTsModel> merge(final DummyTsModel source, final String codePrefix, final Vertx vertx) {

    final Promise<DummyTsModel> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var merged = new DummyTsModel();
      merged._creationTs = this._creationTs;
      merged._lastUpdateTs = TimeManager.now();
      merged.value = source.value;
      if (merged.value != null) {

        merged.value = this.value;
      }
      future = future.compose(Merges.mergeFieldList(this.dummies, source.dummies, codePrefix + ".dummies", vertx,
          dummy -> dummy._id != null, (model1, model2) -> model1._id.equals(model2._id),
          (model, dummies) -> model.dummies = dummies));
      future = future.compose(model -> {
        model._id = this._id;
        return Future.succeededFuture(model);
      });
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
  public Future<DummyTsModel> update(final DummyTsModel source, final String codePrefix, final Vertx vertx) {

    final Promise<DummyTsModel> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var updated = new DummyTsModel();
      updated._creationTs = this._creationTs;
      updated._lastUpdateTs = TimeManager.now();
      updated.value = source.value;
      updated.dummies = source.dummies;
      updated._id = this._id;
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
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();
    future = future.compose(Validations.validate(this.dummies,
        (model1, model2) -> model1._id == model2._id || model1._id != null && model1._id.equals(model2._id),
        codePrefix + ".dummies", vertx));
    promise.complete();
    return future;
  }

}
