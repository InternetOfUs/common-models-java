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
import eu.internetofus.common.components.Validations;
import eu.internetofus.common.components.interaction_protocol_engine.ProtocolAddress.Component;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * An event that will happens on a protocol.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(name = "ProtocolEvent", description = "An event that will be fired on a protocol.")
public class ProtocolEvent extends AbstractProtocolAction implements Model, Validable {

  /**
   * The identifier of the event.
   */
  @Schema(description = "The identifier of the event.", accessMode = AccessMode.READ_ONLY, example = "E34jhg78tbgh")
  public Long id;

  /**
   * The seconds to delay when the event has to be fired.
   */
  @Schema(description = "The identifier of the event.", nullable = false, example = "1")
  public Long delay;

  /**
   * The identifier of the user that is sending the message.
   */
  @Schema(description = "The agent that send the message", nullable = false)
  public String userId;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    var future = super.validate(codePrefix, vertx).compose(empty -> promise.future());
    try {

      this.userId = Validations.validateStringField(codePrefix, "userId", this.userId);
      future = Validations.composeValidateId(future, codePrefix, "userId", this.userId, true,
          WeNetProfileManager.createProxy(vertx)::retrieveProfile);

      if (this.delay == null) {

        promise.fail(new ValidationErrorException(codePrefix + ".delay", "You must to define a delay"));

      } else {

        promise.complete();

      }

    } catch (final ValidationErrorException validationError) {

      promise.fail(validationError);
    }

    return future;

  }

  /**
   * Convert into a protocol message.
   *
   * @return the message with the information of the event.
   */
  public ProtocolMessage toProtocolMessage() {

    final var msg = new ProtocolMessage();
    msg.appId = this.appId;
    msg.communityId = this.communityId;
    msg.taskId = this.taskId;
    msg.transactionId = this.transactionId;
    msg.sender = new ProtocolAddress();
    msg.sender.component = Component.INTERACTION_PROTOCOL_ENGINE;
    msg.sender.userId = this.userId;
    msg.receiver = msg.sender;
    msg.particle = this.particle;
    msg.content = this.content;
    return msg;

  }

}
