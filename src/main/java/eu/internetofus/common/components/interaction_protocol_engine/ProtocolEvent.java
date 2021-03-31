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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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

      this.userId = Validations.validateStringField(codePrefix, "userId", 255, this.userId);
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
