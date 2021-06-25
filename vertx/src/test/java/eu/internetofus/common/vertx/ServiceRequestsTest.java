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

package eu.internetofus.common.vertx;

import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import java.util.Arrays;
import javax.ws.rs.core.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test the {@link ServiceRequests}.
 *
 * @see ServiceRequests
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ServiceRequestsTest {

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
    final var request = new ServiceRequest(new JsonObject().put("headers", headers));
    assertThat(ServiceRequests.acceptedLanguageIn(request, "en", "ca")).isEqualTo("en");

  }

  /**
   * Check accept a language defined on the requests.
   */
  @Test
  public void shouldAcceptLanguageInRequest() {

    final var headers = new JsonObject();
    headers.put(HttpHeaders.ACCEPT_LANGUAGE, "fr-CH, fr;q=0.9, en;q=0.8, ca;q=0.7, es;q=0.3,*;q=0.5");
    final var request = new ServiceRequest(new JsonObject().put("headers", headers));
    assertThat(ServiceRequests.acceptedLanguageIn(request, "en", "ca", "es")).isEqualTo("ca");

  }

  /**
   * Check that convert a {@code null} array to a {@code null} list.
   */
  @Test
  public void shouldNullArrayBeNullListString() {

    assertThat(ServiceRequests.toListString(null)).isNull();

  }

  /**
   * Check that convert an empty array to a {@code null} list.
   */
  @Test
  public void shouldEmptyArrayBeNullListString() {

    assertThat(ServiceRequests.toListString(new JsonArray())).isNull();

  }

  /**
   * Check that convert an empty array to a string list.
   */
  @Test
  public void shouldArrayBeConvertedToListString() {

    assertThat(ServiceRequests.toListString(new JsonArray().add("value").add("true").add("3")))
        .isEqualTo(Arrays.asList("value", "true", "3"));

  }

  /**
   * Check that return an empty query object when it is not defiend.
   */
  @Test
  public void shouldEmptyQueryObjectWhenNotDefined() {

    final var context = new ServiceRequest(new JsonObject());
    assertThat(ServiceRequests.getQueryParamters(context)).isEqualTo(new JsonObject());

  }

  /**
   * Check that can extract an array from a {@code null} value.
   *
   * @see ServiceRequests#extractQueryArray(String)
   */
  @Test
  public void shouldExtractQueryArrayFromNullValue() {

    assertThat(ServiceRequests.extractQueryArray(null)).isNull();

  }

  /**
   * Check that can extract an array from an empty value.
   *
   * @see ServiceRequests#extractQueryArray(String)
   */
  @Test
  public void shouldExtractQueryArrayFromEmptyValue() {

    assertThat(ServiceRequests.extractQueryArray("")).isNull();

  }

  /**
   * Check that can extract an array from multiple empty value.
   *
   * @see ServiceRequests#extractQueryArray(String)
   */
  @Test
  public void shouldExtractQueryArrayFromMultipleEmptyValue() {

    assertThat(ServiceRequests.extractQueryArray("  ,   ,, , ")).isNull();

  }

  /**
   * Check that can extract an array from a value.
   *
   * @see ServiceRequests#extractQueryArray(String)
   */
  @Test
  public void shouldExtractQueryArrayFromOneValueArray() {

    assertThat(ServiceRequests.extractQueryArray(" value ")).isNotEmpty().containsExactly("value");

  }

  /**
   * Check that can extract an array from multiple value.
   *
   * @see ServiceRequests#extractQueryArray(String)
   */
  @Test
  public void shouldExtractQueryArray() {

    assertThat(ServiceRequests.extractQueryArray(" value1 ,    , value2  ,,value3,   value4  ,value5")).isNotEmpty()
        .containsExactly("value1", "value2", "value3", "value4", "value5");

  }

}
