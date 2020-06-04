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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import eu.internetofus.common.components.ValidationErrorException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * Test {@link Repository}
 *
 * @see Repository
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class RepositoryTest extends RepositoryTestCase<Repository> {

  /**
   * {@inheritDoc}
   *
   * @see Repository#Repository(MongoClient)
   */
  @Override
  protected Repository createRepository(final MongoClient pool) {

    return new Repository(pool);
  }

  /**
   * Verify that the sort object is null when an empty or {@code null} value is converted.
   *
   * @param values to convert.
   *
   * @see Repository#queryParamToSort(Iterable, String)
   */
  @ParameterizedTest(name = "Should {0} be return null")
  @NullAndEmptySource
  public void shouldSortReturnNull(final List<String> values) {

    assertThatCode(() -> {

      assertThat(Repository.queryParamToSort(values, "codePrefix")).isNull();

    }).doesNotThrowAnyException();
  }

  /**
   * Verify that obtain the sort object form a set of values.
   *
   * @param param with the values to convert.
   *
   * @see Repository#queryParamToSort(Iterable, String)
   */
  @ParameterizedTest(name = "Should sort {0}")
  @ValueSource(strings = { "value;{\"value\":1}", "+value;{\"value\":1}", "  -value    ;{\"value\":-1}", "key1  , key2 , key3;{\"key1\":1,\"key2\":1,\"key3\":1}", "  key1  , +key2 , -key3  ;{\"key1\":1,\"key2\":1,\"key3\":-1}" })
  public void shouldSort(final String param) {

    assertThatCode(() -> {

      final int endIndex = param.indexOf(';');
      final String[] values = param.substring(0, endIndex).split(",");
      final String expected = param.substring(endIndex + 1).trim();
      final JsonObject sortExpected = (JsonObject) Json.decodeValue(expected);
      assertThat(Repository.queryParamToSort(Arrays.asList(values), "codePrefix")).isEqualTo(sortExpected);

    }).doesNotThrowAnyException();
  }

  /**
   * Verify that the not obtain the sort object with bad values.
   *
   * @param param with the values to convert.
   *
   * @see Repository#queryParamToSort(Iterable, String)
   */
  @ParameterizedTest(name = "Should {0} can not converted to sort")
  @ValueSource(strings = { ",key;[0]", "key,  ;[1]", "key1,-key2,+key3, ;[3]", "+key1,-key1;[1]" })
  public void shouldSortThrowException(final String param) {

    final int endIndex = param.indexOf(';');
    final String[] values = param.substring(0, endIndex).split(",");
    final String expected = param.substring(endIndex + 1).trim();

    final ValidationErrorException error = catchThrowableOfType(() -> Repository.queryParamToSort(Arrays.asList(values), "codePrefix"), ValidationErrorException.class);
    assertThat(error).isNotNull();
    assertThat(error.getCode()).isEqualTo("codePrefix" + expected);

  }

  /**
   * Verify that the sort object is null when an empty or {@code null} value is converted.
   *
   * @see Repository#queryParamToSort(Iterable, String)
   */
  @Test
  public void shouldSortThrowExceptionWhenValueIsNull() {

    final ValidationErrorException error = catchThrowableOfType(() -> Repository.queryParamToSort(Arrays.asList("key1", "-key2", null), "codePrefix"), ValidationErrorException.class);
    assertThat(error).isNotNull();
    assertThat(error.getCode()).isEqualTo("codePrefix[2]");

  }

}
