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

import org.junit.jupiter.api.Test;

import eu.internetofus.common.components.Model;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

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

		final T model1 = this.createModelExample(1);
		final T model2 = this.createModelExample(1);
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

		final T model1 = this.createModelExample(1);
		final T model2 = this.createModelExample(2);
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

		final T model = this.createModelExample(1);
		final String value = model.toJsonString();
		assertThat(value).isNotEmpty();
		assertThat(Model.fromString(value, model.getClass())).isEqualTo(model);

	}

	/**
	 * Check the model can be encoded decoded from {@link JsonObject}.
	 */
	@Test
	public void shouldJsonObjectManipulable() {

		final T model = this.createModelExample(1);
		final JsonObject value = model.toJsonObject();
		assertThat(value).isNotEmpty();
		assertThat(Model.fromJsonObject(value, model.getClass())).isEqualTo(model);

	}

	/**
	 * Check the model can be encoded decoded from {@link Buffer}.
	 */
	@Test
	public void shouldBufferManipulable() {

		final T model = this.createModelExample(1);
		final Buffer value = model.toBuffer();
		assertThat(value).isNotNull();
		assertThat(Model.fromBuffer(value, model.getClass())).isEqualTo(model);

	}

}
