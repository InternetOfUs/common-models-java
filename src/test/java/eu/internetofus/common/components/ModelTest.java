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
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Test the {@link Model}
 *
 * @see Model
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ModelTest {

  /**
   * Check not obtain a model form a {@code null} string.
   */
  @Test
  public void shouldNotObtainModelFromNullString() {

    assertThat(Model.fromString(null, Model.class)).isNull();

  }

  /**
   * Check not convert to JSON the {@link UnconvertedToJsonModel}.
   */
  @Test
  public void shouldNotConvertToJsonString() {

    assertThat(new UnconvertedToJsonModel().toJsonString()).isNull();

  }

  /**
   * Check not convert to JSON object the {@link UnconvertedToJsonModel}.
   */
  @Test
  public void shouldNotConvertToJsonObject() {

    assertThat(new UnconvertedToJsonModel().toJsonObject()).isNull();

  }

  /**
   * Check not obtain a model form a {@code null} {@link JsonObject}.
   */
  @Test
  public void shouldNotObtainModelFromNullJsonObject() {

    assertThat(Model.fromJsonObject(null, Model.class)).isNull();

  }

  /**
   * Check not obtain a model form a {@code null} type.
   */
  @Test
  public void shouldNotObtainModelFromNullClass() {

    assertThat(Model.fromJsonObject(new JsonObject(), (Class<Model>) null)).isNull();

  }

  /**
   * Check not convert to buffer the {@link UnconvertedToJsonModel}.
   */
  @Test
  public void shouldNotConvertToBuffer() {

    assertThat(new UnconvertedToJsonModel().toBuffer()).isNull();

  }

  /**
   * Check not obtain a model form a {@code null} string.
   */
  @Test
  public void shouldNotObtainModelFromNullBuffer() {

    assertThat(Model.fromBuffer(null, Model.class)).isNull();

  }

  /**
   * Check not obtain a model form a {@code null} resource.
   */
  @Test
  public void shouldNotLoadModelFromNullResource() {

    assertThat(Model.loadFromResource(null, Model.class)).isNull();

  }

  /**
   * Check that a {@code null} list can not be converted to an array.
   */
  @Test
  public void shouldNullListNotConvertToArray() {

    assertThat(Model.toJsonArray(null)).isNull();

  }

  /**
   * Check that convert an empty list to an empty array.
   */
  @Test
  public void shoulEmptyListConvertToEmptyArray() {

    assertThat(Model.toJsonArray(new ArrayList<DummyModel>())).isEqualTo(new JsonArray());

  }

  /**
   * Check that a {@code null} array return a null list.
   */
  @Test
  public void shouldNullArrayNotConvertToList() {

    assertThat(Model.fromJsonArray((JsonArray) null, DummyModel.class)).isNull();

  }

  /**
   * Check that can not obtain list from a bad array.
   */
  @Test
  public void shoulBadValueOnArrayNotConvertedToList() {

    assertThat(Model.fromJsonArray(new JsonArray().add(1), DummyModel.class)).isNull();

  }

  /**
   * Check that can not obtain list from a bad model on array.
   */
  @Test
  public void shoulBadModelOnArrayNotConvertedToList() {

    assertThat(Model.fromJsonArray(new JsonArray().add(new JsonObject().put("undefined", "value")), DummyModel.class))
        .isNull();

  }

  /**
   * Check that convert an empty list to an empty array.
   */
  @Test
  public void shoulEmptyArrayConvertedToEmptyList() {

    assertThat(Model.fromJsonArray(new JsonArray(), DummyModel.class)).isEmpty();

  }

  /**
   * Check that convert a list to models to array and vice verse.
   */
  @Test
  public void shoulConvertFromToArrayBeEquals() {

    final List<DummyModel> models = new ArrayList<>();
    for (var i = 0; i < 10; i++) {

      final var model = new DummyModelTest().createModelExample(i);
      models.add(model);

    }
    final var array = Model.toJsonArray(models);
    assertThat(array).isNotNull();
    final var result = Model.fromJsonArray(array, DummyModel.class);
    assertThat(result).isNotNull().isEqualTo(models);

  }

  /**
   * Check that a {@code null} buffer return a null list.
   */
  @Test
  public void shouldNullBufferNotConvertToList() {

    assertThat(Model.fromJsonArray((Buffer) null, DummyModel.class)).isNull();

  }

  /**
   * Check that can not obtain list from a bad array on buffer.
   */
  @Test
  public void shoulBadValueOnBufferNotConvertedToList() {

    assertThat(Model.fromJsonArray(Buffer.buffer("[1]"), DummyModel.class)).isNull();

  }

  /**
   * Check that not convert a bad model.
   */
  @Test
  public void shoulBadModelOnBufferNotConvertedToList() {

    assertThat(Model.fromJsonArray(Buffer.buffer("[{\"undefined\":\"value\"}]"), DummyModel.class)).isNull();

  }

  /**
   * Check that convert an empty list to an empty buffer.
   */
  @Test
  public void shoulEmptyBufferConvertedToEmptyList() {

    assertThat(Model.fromJsonArray(Buffer.buffer("[]"), DummyModel.class)).isEmpty();

  }

  /**
   * Check that convert a list to models to buffer and vice verse.
   */
  @Test
  public void shoulConvertFromToBufferBeEquals() {

    final List<DummyModel> models = new ArrayList<>();
    for (var i = 0; i < 10; i++) {

      final var model = new DummyModelTest().createModelExample(i);
      models.add(model);

    }
    final var array = Model.toJsonArray(models);
    assertThat(array).isNotNull();
    final var result = Model.fromJsonArray(array.toBuffer(), DummyModel.class);
    assertThat(result).isNotNull().isEqualTo(models);

  }

  /**
   * Check not convert to JSON with empty values object the
   * {@link UnconvertedToJsonModel}.
   */
  @Test
  public void shouldNotConvertToJsonObjectWithEmptyValues() {

    assertThat(new UnconvertedToJsonModel().toJsonObjectWithEmptyValues()).isNull();

  }

  /**
   * Check capture exception when can not get form a response.
   */
  @Test
  public void shouldCaptureExceptionWhenGetFromResponse() {

    assertThat(Model.fromResponse(null, DummyModel.class)).isNull();

  }

  /**
   * Check not convert to Buffer with empty values object the
   * {@link UnconvertedToJsonModel}.
   */
  @Test
  public void shouldNotConvertToBufferWithEmptyValues() {

    assertThat(new UnconvertedToJsonModel().toBufferWithEmptyValues()).isNull();

  }

}
