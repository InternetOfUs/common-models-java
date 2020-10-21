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
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
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
import eu.internetofus.common.components.ReflectionModel;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.ValidationErrorException;
import eu.internetofus.common.components.Validations;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * An address of the sender or receiver of the {@link ProtocolMessage}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(description = "The component that send/receive a message.")
public class ProtocolAddress extends ReflectionModel implements Model, Validable {

  /**
   * The possible components of the address.
   */
  public static enum Component {

    /**
     * The component is the application of the user.
     */
    USER_APP,

    /**
     * The component is the interaction protocol engine.
     */
    INTERACTION_PROTOCOL_ENGINE,

    /**
     * The component is the social context builder.
     */
    SOCIAL_CONTEXT_BUILDER,

    /**
     * The component is the incentive server.
     */
    INCENTIVE_SERVER,

    /**
     * The component is the task manager.
     */
    TASK_MANAGER,

    /**
     * The component is the profile manager.
     */
    PROFILE_MANAGER;

  }

  /**
   * The component that send or receiver the message.
   */
  @Schema(description = "The component that interact on the protocol.")
  public Component component;

  /**
   * The identifier of the user associated to the component to interact.
   */
  @Schema(description = "The user associated the component that interact.")
  public String userId;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();
    if (this.component == null) {

      promise.fail(new ValidationErrorException(codePrefix + ".component", "You must to specify the component."));

    } else {

      try {

        this.userId = Validations.validateNullableStringField(codePrefix, "userId", 255, this.userId);
        if (this.userId != null) {

          future = Validations.composeValidateId(future, codePrefix, "userId", this.userId, true, WeNetProfileManager.createProxy(vertx)::retrieveProfile);

        }
        promise.complete();

      } catch (final ValidationErrorException error) {

        promise.fail(error);
      }

    }

    return future;
  }

}
