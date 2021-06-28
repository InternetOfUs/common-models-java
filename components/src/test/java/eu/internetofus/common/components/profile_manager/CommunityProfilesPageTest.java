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

import java.util.ArrayList;
import eu.internetofus.common.model.Models.CommunityProfileTest;
import eu.internetofus.common.model.ModelTestCase;

/**
 * Test the {@link CommunityProfilesPage}.
 *
 * @see CommunityProfilesPage
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class CommunityProfilesPageTest extends ModelTestCase<CommunityProfilesPage> {

  /**
   * {@inheritDoc}
   */
  @Override
  public CommunityProfilesPage createModelExample(final int index) {

    final var model = new CommunityProfilesPage();
    model.offset = index;
    model.total = 3 + 10 * index;
    model.communities = new ArrayList<>();
    for (var i = 0; i < 3; i++) {

      final var profile = new CommunityProfileTest().createModelExample(index + i);
      model.communities.add(profile);

    }

    return model;
  }

}
