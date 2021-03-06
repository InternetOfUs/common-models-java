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
import java.util.List;

/**
 * Dummy model with timestamp.
 *
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class DummyTsModel extends CreateUpdateTsDetails implements Model, Validable<DummyValidateContext>,
    Updateable<DummyTsModel, DummyValidateContext>, Mergeable<DummyTsModel, DummyValidateContext> {

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
  public Future<DummyTsModel> merge(final DummyTsModel source, final DummyValidateContext context) {

    final Promise<DummyTsModel> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var merged = new DummyTsModel();
      merged._creationTs = this._creationTs;
      merged._lastUpdateTs = TimeManager.now();
      merged.value = Merges.mergeValues(this.value, source.value);
      future = future.compose(Merges.mergeListField(context, "dummies", this.dummies, source.dummies,
          this::equalsDummyTsModelIds, (model, dummies) -> model.dummies = dummies));
      future = future.compose(model -> {
        model._id = this._id;
        return Future.succeededFuture(model);
      });
      future = future.compose(context.chain());
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
  public Future<DummyTsModel> update(final DummyTsModel source, final DummyValidateContext context) {

    final Promise<DummyTsModel> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var updated = new DummyTsModel();
      updated._creationTs = this._creationTs;
      updated._lastUpdateTs = TimeManager.now();
      updated.value = source.value;
      updated.dummies = source.dummies;
      updated._id = this._id;
      future = future.compose(context.chain());
      promise.complete(updated);

    } else {

      promise.complete(this);
    }

    return future;
  }

  /**
   * Check if two dummy models has the same identifier.
   *
   * @param model1 to compare.
   * @param model2 to compare.
   *
   * @return {@code true} if they has the same identifier.
   */
  private boolean equalsDummyTsModelIds(final DummyTsModel model1, final DummyTsModel model2) {

    return model1._id == model2._id || model1._id != null && model1._id.equals(model2._id);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final DummyValidateContext context) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();
    if (this.dummies != null) {

      future = future.compose(context.validateListField("dummies", this.dummies, this::equalsDummyTsModelIds));
    }
    promise.complete();
    return future;
  }

}
