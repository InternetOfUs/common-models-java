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

}
