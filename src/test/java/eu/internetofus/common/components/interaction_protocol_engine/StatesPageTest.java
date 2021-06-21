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
import java.util.ArrayList;

/**
 * Test the {@link StatesPage}.
 *
 * @see StatesPage
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class StatesPageTest extends ModelTestCase<StatesPage> {

  /**
   * {@inheritDoc}
   */
  @Override
  public StatesPage createModelExample(final int index) {

    final var model = new StatesPage();
    model.offset = index;
    model.total = 100 + index;
    model.states = new ArrayList<>();
    model.states.add(new StateTest().createModelExample(index));
    return model;
  }

}