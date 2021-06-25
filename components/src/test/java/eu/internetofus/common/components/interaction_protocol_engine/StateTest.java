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

import eu.internetofus.common.components.ModelTestCase;
import io.vertx.core.json.JsonObject;

/**
 * Test the {@link State}.
 *
 * @see State
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class StateTest extends ModelTestCase<State> {

  /**
   * {@inheritDoc}
   */
  @Override
  public State createModelExample(final int index) {

    assert index >= 0;
    final var model = new State();
    model.communityId = "community_" + index;
    model.taskId = "task_" + index;
    model.userId = "user_" + index;
    model.attributes = new JsonObject().put("index", index);
    model._creationTs = 1234567891 + index;
    model._lastUpdateTs = 1234567991 + index * 2;
    return model;
  }

}
