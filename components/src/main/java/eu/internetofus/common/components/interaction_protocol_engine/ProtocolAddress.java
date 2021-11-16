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
import eu.internetofus.common.model.ReflectionModel;
import eu.internetofus.common.model.Validable;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 * An address of the sender or receiver of the {@link ProtocolMessage}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(description = "The component that send/receive a message.")
public class ProtocolAddress extends ReflectionModel implements Model, Validable<WeNetValidateContext> {

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
  public Future<Void> validate(final WeNetValidateContext context) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();
    if (this.component == null) {

      return context.failField("component", "You must to specify the component.");

    } else {

      if (this.userId != null) {

        future = context.validateDefinedProfileIdField("userId", this.userId, future);

      }
      promise.tryComplete();

    }

    return future;
  }

}
