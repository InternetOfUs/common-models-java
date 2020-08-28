/*
 * -----------------------------------------------------------------------------
 *
 * Copyright (c) 2019 - 2022 UDT-IA, IIIA-CSIC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * -----------------------------------------------------------------------------
 */

package eu.internetofus.common.components;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

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

    assertThat(Model.fromJsonArray(null, DummyModel.class)).isNull();

  }

  /**
   * Check that convert an empty list to an empty array.
   */
  @Test
  public void shoulBadValueOnArrayNotConvertedToList() {

    assertThat(Model.fromJsonArray(new JsonArray().add(1), DummyModel.class)).isNull();

  }

  /**
   * Check that convert an empty list to an empty array.
   */
  @Test
  public void shoulBadModelOnArrayNotConvertedToList() {

    assertThat(Model.fromJsonArray(new JsonArray().add(new JsonObject().put("undefined", "value")), DummyModel.class)).isNull();

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
   * Check not convert to JSON with empty values object the {@link UnconvertedToJsonModel}.
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

}
