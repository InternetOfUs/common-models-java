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
import java.util.HashMap;

/**
 * Test the {@link SimilarityResult}.
 *
 * @see SimilarityResult
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class SimilarityResultTest extends ModelTestCase<SimilarityResult> {

  /**
   * {@inheritDoc}
   */
  @Override
  public SimilarityResult createModelExample(final int index) {

    final var model = new SimilarityResult();
    model.attributes = new HashMap<>();
    model.attributes.put("index", (double) index);
    model.attributes.put("prev", index - 1.0d);
    model.attributes.put("next", index + 1.0d);
    return model;

  }

}
