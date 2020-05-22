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

package eu.internetofus.common.vertx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Locale.LanguageRange;

import javax.ws.rs.core.HttpHeaders;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationRequest;

/**
 * Classes used to extract information of an {@link OperationRequest}.
 *
 * @see OperationRequest
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface OperationRequests {

	/**
	 * Obtain the accepted language defined on the header.
	 *
	 * @param defaultLanguage   the language to return if it can not obtain the
	 *                          accepted language.
	 * @param request           to get the information.
	 * @param poosibleLanguages the possible languages that can be used.
	 *
	 * @return the
	 *
	 * @see HttpHeaders#ACCEPT_LANGUAGE
	 */
	static String acceptedLanguageIn(final OperationRequest request, final String defaultLanguage, final String... poosibleLanguages) {

		try {

			final String acceptLanguages = request.getHeaders().get(HttpHeaders.ACCEPT_LANGUAGE);
			final List<LanguageRange> range = Locale.LanguageRange.parse(acceptLanguages);
			final String lang = Locale.lookupTag(range, Arrays.asList(poosibleLanguages));
			if (lang == null) {

				return defaultLanguage;

			} else {

				return lang;
			}

		} catch (final Throwable badLanguage) {

			return defaultLanguage;
		}

	}

	/**
	 * Return the query parameters defined in a context.
	 *
	 * @param context to extract the query parameters.
	 *
	 * @return the query parameters of the context.
	 */
	static JsonObject getQueryParamters(final OperationRequest context) {

		return context.getParams().getJsonObject("query", new JsonObject());

	}

	/**
	 * Convert an array of values to a string list. This is useful to convert a
	 * array query parameter.
	 *
	 * @param array to convert.
	 *
	 * @return the string list defined on the array. If the array is {@code null} or
	 *         empty it return a{@code null} value.
	 */
	static List<String> toListString(final JsonArray array) {

		if (array == null) {

			return null;

		} else {

			final int max = array.size();
			if (max == 0) {

				return null;

			} else {

				final List<String> values = new ArrayList<>();
				for (int i = 0; i < max; i++) {

					values.add(array.getString(i));
				}
				return values;
			}
		}

	}

}
