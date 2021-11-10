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

import eu.internetofus.common.model.Mergeable;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import eu.internetofus.common.model.Updateable;
import eu.internetofus.common.model.Validable;
import eu.internetofus.common.model.ValidationCache;
import eu.internetofus.common.model.ValidationErrorException;
import eu.internetofus.common.model.Validations;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

/**
 * The user model.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class User extends ReflectionModel implements Model, Validable, Mergeable<User>, Updateable<User> {

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
  public Future<User> update(final User source, final String codePrefix, final Vertx vertx,
      final ValidationCache cache) {

    final var updated = new User();
    updated._id = this._id;
    updated.name = source.name;
    return Future.succeededFuture(updated).compose(Validations.validateChain(codePrefix, vertx, cache));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<User> merge(final User source, final String codePrefix, final Vertx vertx,
      final ValidationCache cache) {

    final var merged = new User();
    merged._id = this._id;
    merged.name = source.name;
    if (merged.name == null) {

      merged.name = this.name;
    }
    return Future.succeededFuture(merged).compose(Validations.validateChain(codePrefix, vertx, cache));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx, final ValidationCache cache) {

    if (this.name == null || this.name.length() == 0) {

      return Future.failedFuture(new ValidationErrorException(codePrefix + ".name", "You must define a name"));

    } else {

      return Future.succeededFuture();
    }

  }

}
