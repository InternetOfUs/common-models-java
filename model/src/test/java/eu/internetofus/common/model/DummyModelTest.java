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

package eu.internetofus.common.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Test the {@link DummyModel}
 *
 * @see DummyModel
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class DummyModelTest extends ModelTestCase<DummyModel> {

  /**
   * {@inheritDoc}
   */
  @Override
  public DummyModel createModelExample(final int index) {

    return new DummyModel(index);
  }

  /**
   * Check can load from resource.
   */
  @Test
  public void shouldLoadFromResource() {

    assertThat(Model.loadFromResource("eu/internetofus/common/model/DummyModel.100.json", DummyModel.class))
        .isEqualTo(this.createModelExample(100));

  }

}
