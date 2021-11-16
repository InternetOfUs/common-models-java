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

package eu.internetofus.common.components.interaction_protocol_engine;

import eu.internetofus.common.components.WeNetValidateContext;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.Validable;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 * A message that can be interchange in an interaction protocol.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(name = "ProtocolMessage", description = "A message that can be interchange in an interaction protocol.")
public class ProtocolMessage extends AbstractProtocolAction implements Model, Validable<WeNetValidateContext> {

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
  public Future<Void> validate(final WeNetValidateContext context) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();
    future = future.compose(empty -> super.validate(context));
    future = future.compose(context.validateField("sender", this.sender));
    future = future.compose(context.validateField("receiver", this.receiver));
    promise.tryComplete();
    return future;

  }

}
