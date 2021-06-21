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
package eu.internetofus.common.components.models;

import eu.internetofus.common.components.ModelTestCase;

/**
 * Test the {@link CreateUpdateTsDetails}
 *
 * @see CreateUpdateTsDetails
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class CreateUpdateTsDetailsTest extends ModelTestCase<CreateUpdateTsDetails> {

  /**
   * {@inheritDoc}
   */
  @Override
  public CreateUpdateTsDetails createModelExample(final int index) {

    final var model = new CreateUpdateTsDetails();
    model._creationTs = index;
    model._lastUpdateTs = index + 1;
    return model;
  }

}
