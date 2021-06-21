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

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;

/**
 * A WeNet components that can be interacted with.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface WeNetComponent {

  /**
   * Obtain the URL to the API for interact with this component.
   *
   * @param handler to inform of the API.
   */
  void obtainApiUrl(final Handler<AsyncResult<String>> handler);

  /**
   * Obtain the URL to the API for interact with this component.
   *
   * @return the future URL to the API of this component.
   */
  @GenIgnore
  default Future<String> obtainApiUrl() {

    final Promise<String> promise = Promise.promise();
    this.obtainApiUrl(promise);
    return promise.future();
  }

}
