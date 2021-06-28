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
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

import eu.internetofus.common.model.DummyModel;
import eu.internetofus.common.model.ValidationErrorException;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test {@link Repository}
 *
 * @see Repository
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
@ExtendWith(MockitoExtension.class)
public class RepositoryTest {

  /**
   * Check search communities fail because can not find.
   *
   * @param pool        mocked MongoDB client.
   * @param testContext test context.
   */
  @Test
  public void shouldSearchPageObjectFailedByMongoClientFind(@Mock final MongoClient pool,
      final VertxTestContext testContext) {

    final var repository = new Repository(pool, "version");
    doReturn(Future.succeededFuture(100L)).when(pool).count(any(), any());
    doReturn(Future.failedFuture("Internal error")).when(pool).findWithOptions(any(), any(), any());
    testContext.assertFailure(repository.searchPageObject(null, null, new FindOptions(), null, null))
        .onFailure(error -> testContext.completeNow());

  }

  /**
   * Verify that the sort object is null when an empty or {@code null} value is
   * converted.
   *
   * @param values to convert.
   *
   * @see Repository#queryParamToSort(Iterable, String,Function)
   */
  @ParameterizedTest(name = "Should {0} be return null")
  @NullAndEmptySource
  public void shouldSortReturnNull(final List<String> values) {

    assertThatCode(() -> {

      assertThat(Repository.queryParamToSort(values, "codePrefix", null)).isNull();

    }).doesNotThrowAnyException();
  }

  /**
   * Verify that obtain the sort object form a set of values.
   *
   * @param param with the values to convert.
   *
   * @see Repository#queryParamToSort(Iterable, String,Function)
   */
  @ParameterizedTest(name = "Should sort {0}")
  @ValueSource(strings = { "key;{\"KEY\":1}", "+KeY;{\"KEY\":1}", " -key ;{\"KEY\":-1}",
      "key1 , key2 , key3;{\"KEY1\":1,\"KEY2\":1,\"KEY3\":1}",
      " key1 , +key2 , -key3 ;{\"KEY1\":1,\"KEY2\":1,\"KEY3\":-1}" })
  public void shouldSort(final String param) {

    assertThatCode(() -> {

      final var endIndex = param.indexOf(';');
      final var values = param.substring(0, endIndex).split(",");
      final var expected = param.substring(endIndex + 1).trim();
      final var sortExpected = (JsonObject) Json.decodeValue(expected);
      assertThat(Repository.queryParamToSort(Arrays.asList(values), "codePrefix", (value) -> {

        final var key = value.toUpperCase();
        if (!key.startsWith("KEY")) {

          return null;

        } else {

          return key;
        }

      })).isEqualTo(sortExpected);

    }).doesNotThrowAnyException();
  }

  /**
   * Verify that the not obtain the sort object with bad values.
   *
   * @param param with the values to convert.
   *
   * @see Repository#queryParamToSort(Iterable, String,Function)
   */
  @ParameterizedTest(name = "Should {0} can not converted to sort")
  @ValueSource(strings = { ",key;[0]", "key, ;[1]", "key1,-key2,+key3, ;[3]", "+key1,-key1;[1]", "+key1,-KeY1;[1]",
      "key1,key2,value3;[2]" })
  public void shouldSortThrowException(final String param) {

    final var endIndex = param.indexOf(';');
    final var values = param.substring(0, endIndex).split(",");
    final var expected = param.substring(endIndex + 1).trim();

    final var error = catchThrowableOfType(
        () -> Repository.queryParamToSort(Arrays.asList(values), "codePrefix", (value) -> {

          final var key = value.toLowerCase();
          if (!key.startsWith("key")) {

            return null;

          } else {

            return key;
          }

        }), ValidationErrorException.class);
    assertThat(error).isNotNull();
    assertThat(error.getCode()).isEqualTo("codePrefix" + expected);

  }

  /**
   * Verify that the sort object is null when an empty or {@code null} value is
   * converted.
   *
   * @see Repository#queryParamToSort(Iterable, String,Function)
   */
  @Test
  public void shouldSortThrowExceptionWhenValueIsNull() {

    final var error = catchThrowableOfType(
        () -> Repository.queryParamToSort(Arrays.asList("key1", "-key2", null), "codePrefix", null),
        ValidationErrorException.class);
    assertThat(error).isNotNull();
    assertThat(error.getCode()).isEqualTo("codePrefix[2]");

  }

  /**
   * Should not migrate document because update failed.
   *
   * @param pool        mocked MongoDB client.
   * @param testContext test context.
   */
  @Test
  public void shouldNotMigrateOneDocumentBecauseUpdateFailed(@Mock final MongoClient pool,
      final VertxTestContext testContext) {

    final var repository = new Repository(pool, "version");
    final var collection = "collectionName";
    doReturn(Future.succeededFuture(new JsonObject())).when(pool).findOne(eq(collection), any(), any());
    doReturn(Future.failedFuture("Cannot update")).when(pool).updateCollectionWithOptions(eq(collection), any(), any(),
        any());
    testContext.assertFailure(repository.migrateOneDocument(collection, DummyModel.class, new JsonObject(), 10l))
        .onFailure(error -> testContext.completeNow());

  }

}
