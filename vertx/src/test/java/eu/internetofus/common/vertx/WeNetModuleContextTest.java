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

package eu.internetofus.common.vertx;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Test the {@link WeNetModuleContext},
 *
 * @see WeNetModuleContext
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetModuleContextTest {

  /**
   * Should the constructor store the values.
   */
  @Test
  public void shouldConstructorStoreValues() {


    final Vertx vertx = Vertx.vertx();
    final JsonObject configuration = new JsonObject();
    final var context = new WeNetModuleContext(vertx, configuration);
    assertThat(context.vertx).isSameAs(vertx);
    assertThat(context.configuration).isSameAs(configuration);

  }

}
