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

package eu.internetofus.common.components.profile_manager;

import eu.internetofus.common.model.ModelTestCase;
import eu.internetofus.common.components.models.WeNetUserProfileTest;
import eu.internetofus.common.components.profile_manager.HistoricWeNetUserProfile;

/**
 * Test the {@link HistoricWeNetUserProfile}.
 *
 * @see HistoricWeNetUserProfile
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class HistoricWeNetUserProfileTest extends ModelTestCase<HistoricWeNetUserProfile> {

  /**
   * {@inheritDoc}
   */
  @Override
  public HistoricWeNetUserProfile createModelExample(final int index) {

    final var model = new HistoricWeNetUserProfile();
    model.from = index;
    model.to = 10 + index;
    model.profile = new WeNetUserProfileTest().createBasicExample(index);
    return model;
  }

}
