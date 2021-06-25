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

package eu.internetofus.common.vertx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import eu.internetofus.common.components.Model;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.ext.web.client.HttpResponse;

/**
 * Component user to manage the {@link HttpResponse}.
 *
 * @see HttpResponse
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface HttpResponses {

  /**
   * Verify that the body of the response is of the specified class type.
   *
   * @param <T>   type of the context,
   * @param clazz of the content.
   * @param res   response to get the body content.
   *
   * @return the content of the body.
   */
  static <T> T assertThatBodyIs(final Class<T> clazz, final HttpResponse<Buffer> res) {

    try {

      assertThat(res.getHeader(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_JSON);
      return Json.decodeValue(res.body(), clazz);

    } catch (final Throwable throwable) {

      fail(throwable);
      return null;
    }

  }

  /**
   * Verify that the body of the response is an array of the specified class type.
   *
   * @param <T>   type of the elements on the array,
   *
   * @param clazz of the content.
   * @param res   response to get the body content.
   *
   * @return the content of the body.
   */
  static <T extends Model> List<T> assertThatBodyIsArrayOf(final Class<T> clazz, final HttpResponse<Buffer> res) {

    assertThat(res.getHeader(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_JSON);
    return Model.fromJsonArray(res.body(), clazz);

  }

}
