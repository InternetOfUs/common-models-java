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

package eu.internetofus.common.components.interaction_protocol_engine;

import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.ValidationErrorException;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * A message that can be interchange in an interaction protocol.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(name = "ProtocolMessage", description = "A message that can be interchange in an interaction protocol.")
public class ProtocolMessage extends AbstractProtocolAction implements Model, Validable {

  /**
   * The identifier of the user that is sending the message.
   */
  @Schema(description = "The agent that send the message")
  public ProtocolAddress sender;

  /**
   * The identifier of the user that is sending the message.
   */
  @Schema(description = "The receiver of the message")
  public ProtocolAddress receiver;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    var future = super.validate(codePrefix, vertx).compose(empty -> promise.future());
    if (this.sender == null) {

      promise.fail(new ValidationErrorException(codePrefix + ".sender", "You must to define a sender"));

    } else if (this.receiver == null) {

      promise.fail(new ValidationErrorException(codePrefix + ".receiver", "You must to define a receiver"));

    } else {

      future = future.compose(map -> this.sender.validate(codePrefix + ".sender", vertx));
      future = future.compose(map -> this.receiver.validate(codePrefix + ".receiver", vertx));
      promise.complete();

    }

    return future;

  }

}
