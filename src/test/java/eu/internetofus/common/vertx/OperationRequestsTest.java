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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import javax.ws.rs.core.HttpHeaders;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationRequest;

/**
 * Test the {@link OperationRequests}.
 *
 * @see OperationRequests
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class OperationRequestsTest {

  /**
   * Check return the default language.
   *
   * @param acceptLanguage header value.
   */
  @ParameterizedTest(name = "Should return the default value for the Accept-Language= {0}")
  @NullAndEmptySource
  @ValueSource(strings = { "*", "es", "es-US,es;q=0.5" })
  public void shouldAcceptLanguageByTheDefault(final String acceptLanguage) {

    final var headers = new JsonObject();
    if (acceptLanguage != null) {

      headers.put(HttpHeaders.ACCEPT_LANGUAGE, acceptLanguage);
    }
    final var request = new OperationRequest(new JsonObject().put("headers", headers));
    assertThat(OperationRequests.acceptedLanguageIn(request, "en", "ca")).isEqualTo("en");

  }

  /**
   * Check accept a language defined on the requests.
   */
  @Test
  public void shouldAcceptLanguageInRequest() {

    final var headers = new JsonObject();
    headers.put(HttpHeaders.ACCEPT_LANGUAGE, "fr-CH, fr;q=0.9, en;q=0.8, ca;q=0.7, es;q=0.3,*;q=0.5");
    final var request = new OperationRequest(new JsonObject().put("headers", headers));
    assertThat(OperationRequests.acceptedLanguageIn(request, "en", "ca", "es")).isEqualTo("ca");

  }

  /**
   * Check that convert a {@code null} array to a {@code null} list.
   */
  @Test
  public void shouldNullArrayBeNullListString() {

    assertThat(OperationRequests.toListString(null)).isNull();

  }

  /**
   * Check that convert an empty array to a {@code null} list.
   */
  @Test
  public void shouldEmptyArrayBeNullListString() {

    assertThat(OperationRequests.toListString(new JsonArray())).isNull();

  }

  /**
   * Check that convert an empty array to a string list.
   */
  @Test
  public void shouldArrayBeConvertedToListString() {

    assertThat(OperationRequests.toListString(new JsonArray().add("value").add("true").add("3"))).isEqualTo(Arrays.asList("value", "true", "3"));

  }

  /**
   * Check that return an empty query object when it is not defiend.
   */
  @Test
  public void shouldEmptyQueryObjectWhenNotDefined() {

    final var context = new OperationRequest(new JsonObject());
    assertThat(OperationRequests.getQueryParamters(context)).isEqualTo(new JsonObject());

  }

}
