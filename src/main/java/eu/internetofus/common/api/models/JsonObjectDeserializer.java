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

package eu.internetofus.common.api.models;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import io.vertx.core.json.JsonObject;

/**
 * The component to deserialize a {@link JsonObject} to any of it possible sub
 * types.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class JsonObjectDeserializer extends StdDeserializer<JsonObject> {

	/**
	 * Serial version identifier.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The type for the deserializer.
	 */
	private final TypeReference<Map<String, Object>> type = new TypeReference<Map<String, Object>>() {
	};

	/**
	 * Create a new deserializer
	 */
	public JsonObjectDeserializer() {

		this(null);
	}

	/**
	 * Create a new deserializer for a type.
	 *
	 * @param type for the deserializer
	 */
	public JsonObjectDeserializer(final Class<?> type) {

		super(type);
	}

	@Override
	public JsonObject deserialize(final JsonParser jsonParser, final DeserializationContext context) throws IOException {

		final ObjectCodec objectCodec = jsonParser.getCodec();
		final Map<String, Object> value = objectCodec.readValue(jsonParser, this.type);

		return new JsonObject(value);
	}

}