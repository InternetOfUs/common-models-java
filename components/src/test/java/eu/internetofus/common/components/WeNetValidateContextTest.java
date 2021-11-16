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

import org.junit.jupiter.api.Test;

/**
 * Test the {@link WeNetValidateContext}.
 *
 * @see WeNetValidateContext
 *
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetValidateContextTest {

  /**
   * Check that can not obtain model with undefined or {@code null} arguments.
   */
  @Test
  public void shouldNoGetUndefinedModel() {

    final var cache = new WeNetValidateContext("codePrefix", null);
    assertThat(cache.getModel(null, (Class<?>) null)).isNull();
    assertThat(cache.getModel("undefined", (Class<?>) null)).isNull();
    assertThat(cache.getModel(null, String.class)).isNull();
    assertThat(cache.getModel("undefined", String.class)).isNull();
  }

  /**
   * Check that can not obtain model with the same key if the type is different.
   */
  @Test
  public void shouldNoGetModelWithSameKeyDiferentClass() {

    final var cache = new WeNetValidateContext("codePrefix", null);
    cache.setModel("id", 3);
    assertThat(cache.getModel("id", Number.class)).isNull();

  }

  /**
   * Check that set/get model.
   */
  @Test
  public void shouldSetGetModel() {

    final var cache = new WeNetValidateContext("codePrefix", null);
    cache.setModel("id", "value");
    assertThat(cache.getModel("id", String.class)).isEqualTo("value");
    assertThat(cache.existModel("id", String.class)).isTrue();

  }

  /**
   * Check that set {@code null} model.
   */
  @Test
  public void shouldNotSetNullModel() {

    final var cache = new WeNetValidateContext("codePrefix", null);
    cache.setModel("id", null);
    assertThat(cache.existModel("id", String.class)).isFalse();

  }

  /**
   * Should not exist model with {@code null} id.
   */
  @Test
  public void shouldNotExistWithNullId() {

    final var cache = new WeNetValidateContext("codePrefix", null);
    assertThat(cache.existModel(null, String.class)).isFalse();

  }

  /**
   * Should not exist model with {@code null} type.
   */
  @Test
  public void shouldNotExistWithNullType() {

    final var cache = new WeNetValidateContext("codePrefix", null);
    assertThat(cache.existModel("id", null)).isFalse();

  }

}
