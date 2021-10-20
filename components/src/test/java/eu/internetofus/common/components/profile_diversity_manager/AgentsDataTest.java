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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Test the {@link AgentsData}.
 *
 * @see AgentData
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class AgentsDataTest extends ModelTestCase<AgentsData> {

  /**
   * {@inheritDoc}
   */
  @Override
  public AgentsData createModelExample(final int index) {

    final var model = new AgentsData();
    model.agents = new ArrayList<>();
    for (var i = index - 1; i < index + 2; i++) {

      final var agent = new AgentDataTest().createModelExample(i);
      model.agents.add(agent);
    }

    model.quantitativeAttributes = new HashSet<>();
    model.quantitativeAttributes.add("qa_index");
    model.quantitativeAttributes.add("qa_index_next");
    model.quantitativeAttributes.add("qa_index_previous");
    model.qualitativeAttributes = new HashMap<>();
    final var options = new HashSet<>(Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));
    model.qualitativeAttributes.put("ql_index", options);
    model.qualitativeAttributes.put("ql_index_next", options);
    model.qualitativeAttributes.put("ql_index_previous", options);

    return model;
  }

}
