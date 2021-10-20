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

package eu.internetofus.common.components.profile_diversity_manager;

import eu.internetofus.common.model.ModelTestCase;
import java.util.HashMap;

/**
 * Test the {@link AgentData}.
 *
 * @see AgentData
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class AgentDataTest extends ModelTestCase<AgentData> {

  /**
   * {@inheritDoc}
   */
  @Override
  public AgentData createModelExample(final int index) {

    final var model = new AgentData();
    model.id = "Agent of " + index;
    model.quantitativeAttributes = new HashMap<>();
    model.quantitativeAttributes.put("qa_index", (double) index);
    model.quantitativeAttributes.put("qa_index_next", 1.0 + index);
    model.quantitativeAttributes.put("qa_index_previous", index - 1.0);
    model.qualitativeAttributes = new HashMap<>();
    model.qualitativeAttributes.put("ql_index", String.valueOf(index % 10));
    model.qualitativeAttributes.put("ql_index_next", String.valueOf((index + 1) % 10));
    model.qualitativeAttributes.put("ql_index_previous", String.valueOf((index - 1) % 10));

    return model;
  }

}
