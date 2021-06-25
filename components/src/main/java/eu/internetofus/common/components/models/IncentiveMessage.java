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

package eu.internetofus.common.components.models;

import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import eu.internetofus.common.model.Validable;
import eu.internetofus.common.model.Validations;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * The message on an {@link Incentive}
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "Message", description = "The message on an incentive.")
public class IncentiveMessage extends ReflectionModel implements Model, Validable {

  /**
   * The content of the message.
   */
  @Schema(example = "we are happy to see your participation in the I-log app you are doing a great job!")
  public String content;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();

    future = future
        .compose(empty -> Validations.validateStringField(codePrefix, "content", this.content).map(content -> {
          this.content = content;
          return null;
        }));
    promise.complete();

    return future;
  }

}
