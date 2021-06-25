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

package eu.internetofus.common.components.models;

import eu.internetofus.common.model.ModelTestCase;
import java.util.ArrayList;

/**
 * Test the {@link HumanDescription}
 *
 * @see HumanDescription
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class HumanDescriptionTest extends ModelTestCase<HumanDescription> {

  /**
   * {@inheritDoc}
   */
  @Override
  public HumanDescription createModelExample(final int index) {

    final var model = new HumanDescription();
    model.name = "name_" + index;
    model.description = "description_" + index;
    model.keywords = new ArrayList<>();
    model.keywords.add("keyword_" + (index - 1));
    model.keywords.add("keyword_" + index);
    model.keywords.add("keyword_" + (index + 1));
    return model;
  }

}
