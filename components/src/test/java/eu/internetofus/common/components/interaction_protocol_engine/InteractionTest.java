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

import eu.internetofus.common.model.ModelTestCase;
import io.vertx.core.json.JsonObject;

/**
 * Test the {@link Interaction}
 *
 * @see Interaction
 *
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class InteractionTest extends ModelTestCase<Interaction> {

  /**
   * {@inheritDoc}
   */
  @Override
  public Interaction createModelExample(final int index) {

    final var model = new Interaction();
    model.appId = "App id " + index;
    model.communityId = "Community id " + index;
    model.taskTypeId = "Task type id " + index;
    model.taskId = "Task id " + index;
    model.senderId = "Sender id " + index;
    model.receiverId = "Receiver id " + index;
    model.transactionLabel = "Label " + index;
    model.transactionAttributes = new JsonObject().put("transactionAttibuteIndex", index);
    model.transactionTs = (long) index;
    model.messageLabel = "Label " + index;
    model.messageAttributes = new JsonObject().put("messageAttibuteIndex", index);
    model.messageTs = (long) index;
    return model;
  }

}
