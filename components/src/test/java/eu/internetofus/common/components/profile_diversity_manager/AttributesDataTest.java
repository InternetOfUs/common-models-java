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
import java.util.HashSet;

/**
 * Test the {@link AttributesData}.
 *
 * @see AgentData
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class AttributesDataTest extends ModelTestCase<AttributesData> {

  /**
   * {@inheritDoc}
   */
  @Override
  public AttributesData createModelExample(final int index) {

    final var model = new AttributesData();
    model.source = "Source of " + index;
    model.attributes = new HashSet<>();
    model.attributes.add("index_" + index);
    model.attributes.add("next_" + (index + 1));
    model.attributes.add("previous" + (index - 1));
    model.aggregation = Aggregation.values()[index % Aggregation.values().length];

    return model;
  }

}
