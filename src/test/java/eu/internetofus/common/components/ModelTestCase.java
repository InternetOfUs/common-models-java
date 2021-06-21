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

package eu.internetofus.common.components;

import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

/**
 * Generic test over the classes that extends the {@link Model}.
 *
 * @param <T> the type of model to test.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class ModelTestCase<T extends Model> {

  /**
   * Create an example model that has the specified index.
   *
   * @param index to use in the example.
   *
   * @return the example.
   */
  public abstract T createModelExample(int index);

  /**
   * Check two models are equals.
   */
  @Test
  public void shouldBeEquals() {

    final var model1 = this.createModelExample(1);
    final var model2 = this.createModelExample(1);
    assertThat(model1).isEqualTo(model2);
    assertThat(model1.hashCode()).isEqualTo(model2.hashCode());
    assertThat(model1.toString()).isEqualTo(model2.toString());
    assertThat(model1.toJsonString()).isEqualTo(model2.toJsonString());

  }

  /**
   * Check two models are different.
   */
  @Test
  public void shouldNotBeEquals() {

    final var model1 = this.createModelExample(1);
    final var model2 = this.createModelExample(2);
    assertThat(model1).isNotEqualTo(model2);
    assertThat(model1.hashCode()).isNotEqualTo(model2.hashCode());
    assertThat(model1.toString()).isNotEqualTo(model2.toString());
    assertThat(model1.toJsonString()).isNotEqualTo(model2.toJsonString());

  }

  /**
   * Check the model can be encoded decoded from JSON.
   */
  @Test
  public void shouldJSONManipulable() {

    final var model = this.createModelExample(1);
    final var value = model.toJsonString();
    assertThat(value).isNotEmpty();
    assertThat(Model.fromString(value, model.getClass())).isEqualTo(model);

  }

  /**
   * Check the model can be encoded decoded from {@link JsonObject}.
   */
  @Test
  public void shouldJsonObjectManipulable() {

    final var model = this.createModelExample(1);
    final var value = model.toJsonObject();
    assertThat(value).isNotEmpty();
    assertThat(Model.fromJsonObject(value, model.getClass())).isEqualTo(model);

  }

  /**
   * Check the model can be encoded decoded from {@link Buffer}.
   */
  @Test
  public void shouldBufferManipulable() {

    final var model = this.createModelExample(1);
    final var value = model.toBuffer();
    assertThat(value).isNotNull();
    assertThat(Model.fromBuffer(value, model.getClass())).isEqualTo(model);

  }

  /**
   * Check that is converted with the empty values.
   */
  @Test
  public void shouldToJsonObjectWithEmptyValues() {

    final var model = this.createModelExample(1);
    @SuppressWarnings("unchecked")
    final var emptyModel = (T) Model.fromString("{}", model.getClass());
    assertThat(emptyModel).isNotNull();
    var emptyObject = emptyModel.toJsonObjectWithEmptyValues();
    assertThat(emptyObject).isNotNull().isNotEqualTo(new JsonObject());
    assertThat(emptyObject.fieldNames()).isNotEmpty();

  }

  /**
   * Check that is converted with the empty values buffer.
   */
  @Test
  public void shouldToBufferWithEmptyValues() {

    final var model = this.createModelExample(1);
    @SuppressWarnings("unchecked")
    final var emptyModel = (T) Model.fromString("{}", model.getClass());
    assertThat(emptyModel).isNotNull();
    var emptyBuffer = emptyModel.toBufferWithEmptyValues();
    assertThat(emptyBuffer).isNotNull();
    var emptyObject = new JsonObject(emptyBuffer);
    assertThat(emptyObject).isNotNull().isNotEqualTo(new JsonObject());
    assertThat(emptyObject.fieldNames()).isNotEmpty();

  }

}
