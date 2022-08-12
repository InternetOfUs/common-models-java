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

package eu.internetofus.common.components.social_context_builder;

import eu.internetofus.common.model.ModelTestCase;
import java.util.ArrayList;

/**
 * Test the {@link UserIds}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class UserIdsTest extends ModelTestCase<UserIds> {

  /**
   * {@inheritDoc}
   */
  @Override
  public UserIds createModelExample(final int index) {

    final var model = new UserIds();
    model.users_IDs = new ArrayList<>();
    model.users_IDs.add(String.valueOf(index));
    model.users_IDs.add(String.valueOf(index + 1));
    model.users_IDs.add(String.valueOf(index + 2));
    return model;

  }

}
