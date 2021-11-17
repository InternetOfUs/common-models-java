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

package eu.internetofus.common.components;

import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.model.Model;
import io.vertx.core.Vertx;
import org.junit.jupiter.api.Test;

/**
 * Test the {@link WeNetModelContext}.
 *
 * @see WeNetModelContext
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetModelContextTest {

  /**
   * Should create a valid context.
   */
  @Test
  public void shouldCreateContext() {

    final var name = "name";
    final var type = Model.class;
    final var vertx = Vertx.vertx();
    final var context = WeNetModelContext.creteWeNetContext(name, type, vertx);
    assertThat(context).isNotNull();
    assertThat(context.name).isSameAs(name);
    assertThat(context.validateContext).isNotNull();
    assertThat(context.validateContext.errorCode()).isEqualTo("bad_name");
    assertThat(context.type).isSameAs(type);
  }

}
