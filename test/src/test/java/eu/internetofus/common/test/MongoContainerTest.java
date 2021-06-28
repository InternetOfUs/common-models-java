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

package eu.internetofus.common.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Test the {@link MongoContainer}.
 *
 * @see MongoContainer
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class MongoContainerTest {

  /**
   * Test that that start a MongoDB.
   */
  @Test
  public void shouldStartContainer() {

    final var container = new MongoContainer<>();
    assertThat(container.network).isNotNull();
    assertThat(container.mongoContainer).isNull();

    assertThat(container.startMongoContainer()).isSameAs(container);
    assertThat(container.mongoContainer).isNotNull();
    assertThat(container.getMongoDBConfig()).isNotNull();
    assertThat(container.getMongoDBHost()).isNotNull();
    assertThat(container.getMongoDBPort()).isNotNull();

    final var current = container.mongoContainer;
    assertThat(container.startMongoContainer()).isSameAs(container);
    assertThat(container.mongoContainer).isSameAs(current);

    current.stop();
  }

}
