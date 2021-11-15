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
package eu.internetofus.common.vertx.basic;

import eu.internetofus.common.model.DummyValidateContext;
import eu.internetofus.common.model.Mergeable;
import eu.internetofus.common.model.Merges;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import eu.internetofus.common.model.Updateable;
import eu.internetofus.common.model.Validable;
import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 * The user model.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class User extends ReflectionModel implements Model, Validable<DummyValidateContext>,
    Mergeable<User, DummyValidateContext>, Updateable<User, DummyValidateContext> {

  /**
   * The identifier of the community.
   */
  public String _id;

  /**
   * The identifier of the community.
   */
  public String name;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<User> update(final User source, final DummyValidateContext context) {

    final var updated = new User();
    updated._id = this._id;
    updated.name = source.name;
    return Future.succeededFuture(updated).compose(context.chain());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<User> merge(final User source, final DummyValidateContext context) {

    final var merged = new User();
    merged._id = this._id;
    merged.name = Merges.mergeValues(this.name, source.name);
    return Future.succeededFuture(merged).compose(context.chain());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final DummyValidateContext context) {

    final Promise<Void> promise = Promise.promise();
    this.name = context.validateStringField("name", this.name, promise);
    promise.tryComplete();
    return promise.future();

  }

}
