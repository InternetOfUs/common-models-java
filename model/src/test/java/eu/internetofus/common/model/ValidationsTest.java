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

package eu.internetofus.common.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;
import static org.junit.Assert.fail;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test the {@link Validations}.
 *
 * @see Validations
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class ValidationsTest {

  /**
   * Check that a field value is valid and converted to {@code null}.
   *
   * @param value       to be valid.
   * @param testContext test context to use.
   *
   * @see Validations#validateNullableStringField(String, String, String,String[])
   */
  @ParameterizedTest(name = "The value '{0}' is valid and converted to null")
  @NullSource
  @ValueSource(strings = { "", "        " })
  public void shouldNullStringFieldBeValid(final String value, final VertxTestContext testContext) {

    Validations.validateNullableStringField("codePrefix", "fieldName", value)
        .onComplete(testContext.succeeding(result -> testContext.verify(() -> {

          assertThat(result).isNull();
          testContext.completeNow();
        })));
  }

  /**
   * Check that the value is trimmed to be valid.
   *
   * @param value       to be valid.
   * @param testContext test context to use.
   *
   * @see Validations#validateNullableStringField(String, String, String,String[])
   */
  @ParameterizedTest(name = "The value '{0}' is valid")
  @ValueSource(strings = { "a b c", "  a b c      " })
  public void shouldStringWithWhiteFieldBeValid(final String value, final VertxTestContext testContext) {

    Validations.validateNullableStringField("codePrefix", "fieldName", value)
        .onComplete(testContext.succeeding(result -> testContext.verify(() -> {

          assertThat(result).isEqualTo(value.trim());
          testContext.completeNow();
        })));
  }

  /**
   * Check that the value of the field is not valid if the value is not possible.
   *
   * @param testContext test context to use.
   *
   * @see Validations#validateNullableStringField(String, String, String,String[])
   */
  @Test
  public void shouldNotBeValidIfValueIsNotPossible(final VertxTestContext testContext) {

    Validations.validateNullableStringField("codePrefix", "fieldName", "value", "1", "2", "3")
        .onComplete(testContext.failing(error -> testContext.verify(() -> {

          assertThat(error).isInstanceOf(ValidationErrorException.class);
          final var code = ((ValidationErrorException) error).getCode();
          assertThat(code).isEqualTo("codePrefix.fieldName");
          testContext.completeNow();

        })));
  }

  /**
   * Check that an email value is valid but it is {@code null}.
   *
   * @param value       to be valid.
   * @param testContext test context to use.
   *
   * @see Validations#validateNullableEmailField(String, String, String)
   */
  @ParameterizedTest(name = "The value '{0}' is valid email and converted to null")
  @NullSource
  @ValueSource(strings = { "", "        " })
  public void shouldEmailBeValidAndSetToNull(final String value, final VertxTestContext testContext) {

    Validations.validateNullableEmailField("codePrefix", "fieldName", value)
        .onComplete(testContext.succeeding(result -> testContext.verify(() -> {

          assertThat(result).isNull();
          testContext.completeNow();
        })));
  }

  /**
   * Check that the email value is trimmed to be valid.
   *
   * @param value       to be valid.
   * @param testContext test context to use.
   *
   * @see Validations#validateNullableEmailField(String, String, String)
   */
  @ParameterizedTest(name = "The value '{0}' is valid")
  @ValueSource(strings = { "a@b.com", "  a@b.com     " })
  public void shouldEMailBeValid(final String value, final VertxTestContext testContext) {

    Validations.validateNullableEmailField("codePrefix", "fieldName", value)
        .onComplete(testContext.succeeding(result -> testContext.verify(() -> {

          assertThat(result).isEqualTo(value.trim());
          testContext.completeNow();
        })));
  }

  /**
   * Check that the email value is not valid.
   *
   * @param testContext test context to use.
   *
   * @see Validations#validateNullableEmailField(String, String, String)
   */
  @Test
  public void shouldNotBeValidEMail(final VertxTestContext testContext) {

    Validations.validateNullableEmailField("codePrefix", "fieldName", "bad email(at)host.com")
        .onComplete(testContext.failing(error -> testContext.verify(() -> {

          assertThat(error).isInstanceOf(ValidationErrorException.class);
          final var code = ((ValidationErrorException) error).getCode();
          assertThat(code).isEqualTo("codePrefix.fieldName");
          testContext.completeNow();

        })));
  }

  /**
   * Check that a locale value is valid but it is {@code null}.
   *
   * @param value       to be valid.
   * @param testContext test context to use.
   *
   * @see Validations#validateNullableLocaleField(String, String, String)
   */
  @ParameterizedTest(name = "The value '{0}' is valid locale and converted to null")
  @NullSource
  @ValueSource(strings = { "", "        " })
  public void shouldLocaleBeValidAndSetToNull(final String value, final VertxTestContext testContext) {

    Validations.validateNullableLocaleField("codePrefix", "fieldName", value)
        .onComplete(testContext.succeeding(result -> testContext.verify(() -> {

          assertThat(result).isNull();
          testContext.completeNow();
        })));
  }

  /**
   * Check that the locale value is trimmed to be valid.
   *
   * @param value       to be valid.
   * @param testContext test context to use.
   *
   * @see Validations#validateNullableLocaleField(String, String, String)
   */
  @ParameterizedTest(name = "The value '{0}' is valid")
  @ValueSource(strings = { "en_US", "  en_US    ", "en", "   en  " })
  public void shouldLocaleBeValid(final String value, final VertxTestContext testContext) {

    Validations.validateNullableLocaleField("codePrefix", "fieldName", value)
        .onComplete(testContext.succeeding(result -> testContext.verify(() -> {

          assertThat(result).isEqualTo(value.trim());
          testContext.completeNow();
        })));
  }

  /**
   * Check that the locale value is not valid.
   *
   * @param testContext test context to use.
   *
   * @see Validations#validateNullableLocaleField(String, String, String)
   */
  @Test
  public void shouldNotBeValidOcale(final VertxTestContext testContext) {

    Validations.validateNullableLocaleField("codePrefix", "fieldName", "de-Gr")
        .onComplete(testContext.failing(error -> testContext.verify(() -> {

          assertThat(error).isInstanceOf(ValidationErrorException.class);
          final var code = ((ValidationErrorException) error).getCode();
          assertThat(code).isEqualTo("codePrefix.fieldName");
          testContext.completeNow();

        })));
  }

  /**
   * Check that a telephone value is valid but it is {@code null}.
   *
   * @param value       to be valid.
   * @param testContext test context to use.
   *
   * @see Validations#validateNullableTelephoneField(String, String, String,
   *      String)
   */
  @ParameterizedTest(name = "The value '{0}' is valid telephone and converted to null")
  @NullSource
  @ValueSource(strings = { "", "        " })
  public void shouldTelephoneBeValidAndSetToNull(final String value, final VertxTestContext testContext) {

    Validations.validateNullableTelephoneField("codePrefix", "fieldName", null, value)
        .onComplete(testContext.succeeding(result -> testContext.verify(() -> {

          assertThat(result).isNull();
          testContext.completeNow();
        })));
  }

  /**
   * Check that the telephone value is trimmed to be valid.
   *
   * @param testContext test context to use.
   *
   * @see Validations#validateNullableTelephoneField(String, String, String,
   *      String)
   */
  @Test
  public void shouldTelephoneBeValid(final VertxTestContext testContext) {

    Validations.validateNullableTelephoneField("codePrefix", "fieldName", null, "   +34987654321    ")
        .onComplete(testContext.succeeding(result -> testContext.verify(() -> {

          assertThat(result).isEqualTo("+34 987 65 43 21");
          testContext.completeNow();
        })));
  }

  /**
   * Check that the telephone value is trimmed to be valid.
   *
   * @param testContext test context to use.
   *
   * @see Validations#validateNullableTelephoneField(String, String, String,
   *      String)
   */
  @Test
  public void shouldTelephoneBeValidWithLocale(final VertxTestContext testContext) {

    Validations.validateNullableTelephoneField("codePrefix", "fieldName", "es_ES", "   +34987654321    ")
        .onComplete(testContext.succeeding(result -> testContext.verify(() -> {

          assertThat(result).isEqualTo("+34 987 65 43 21");
          testContext.completeNow();
        })));
  }

  /**
   * Check that the telephone value of the field is not valid.
   *
   * @param badPhone    a phone number that is not valid.
   * @param testContext test context to use.
   *
   * @see Validations#validateNullableTelephoneField(String, String,
   *      String,String)
   */
  @ParameterizedTest(name = "The phone number '{0}' has not be valid.")
  @ValueSource(strings = { "+349876543211", "bad telephone number", "1234567890123456789012345678" })
  public void shouldNotBeValidABadTelephoneValue(final String badPhone, final VertxTestContext testContext) {

    Validations.validateNullableTelephoneField("codePrefix", "fieldName", null, badPhone)
        .onComplete(testContext.failing(error -> testContext.verify(() -> {

          assertThat(error).isInstanceOf(ValidationErrorException.class);
          final var code = ((ValidationErrorException) error).getCode();
          assertThat(code).isEqualTo("codePrefix.fieldName");
          testContext.completeNow();

        })));

  }

  /**
   * Check that a date value is valid but it is {@code null}.
   *
   * @param value       to be valid.
   * @param testContext test context to use.
   *
   * @see Validations#validateNullableStringDateField(String, String,
   *      DateTimeFormatter, String)
   */
  @ParameterizedTest(name = "The value '{0}' is valid date and converted to null")
  @NullSource
  @ValueSource(strings = { "", "        " })
  public void shouldDateBeValidAndSetToNull(final String value, final VertxTestContext testContext) {

    Validations.validateNullableStringDateField("codePrefix", "fieldName", DateTimeFormatter.ISO_INSTANT, value)
        .onComplete(testContext.succeeding(result -> testContext.verify(() -> {

          assertThat(result).isNull();
          testContext.completeNow();
        })));
  }

  /**
   * Check that the date value is trimmed to be valid.
   *
   * @param value       to be valid.
   * @param testContext test context to use.
   *
   * @see Validations#validateNullableStringDateField(String, String,
   *      DateTimeFormatter, String)
   */
  @ParameterizedTest(name = "The value '{0}' is valid")
  @ValueSource(strings = { "2011-12-03t10:15:30z", "  2011-12-03t10:15:30z    ", "2011-12-03T10:15:30Z",
      "  2011-12-03t10:15:30Z    " })
  public void shouldDateBeValid(final String value, final VertxTestContext testContext) {

    Validations.validateNullableStringDateField("codePrefix", "fieldName", DateTimeFormatter.ISO_INSTANT, value)
        .onComplete(testContext.succeeding(result -> testContext.verify(() -> {

          assertThat(result).isEqualTo(value.trim().toUpperCase());
          testContext.completeNow();
        })));
  }

  /**
   * Check that the date value is not valid.
   *
   * @param testContext test context to use.
   *
   * @see Validations#validateNullableStringDateField(String, String,
   *      DateTimeFormatter, String)
   */
  @Test
  public void shouldNotBeValidDate(final VertxTestContext testContext) {

    Validations.validateNullableStringDateField("codePrefix", "fieldName", DateTimeFormatter.ISO_INSTANT, "bad date")
        .onComplete(testContext.failing(error -> testContext.verify(() -> {

          assertThat(error).isInstanceOf(ValidationErrorException.class);
          final var code = ((ValidationErrorException) error).getCode();
          assertThat(code).isEqualTo("codePrefix.fieldName");
          testContext.completeNow();

        })));
  }

  /**
   * Check that the date value is not valid.
   *
   * @param testContext test context to use.
   *
   * @see Validations#validateNullableStringDateField(String, String,
   *      DateTimeFormatter, String)
   */
  @Test
  public void shouldNotBeValidDateWithoutFormat(final VertxTestContext testContext) {

    Validations.validateNullableStringDateField("codePrefix", "fieldName", null, "bad date")
        .onComplete(testContext.failing(error -> testContext.verify(() -> {

          assertThat(error).isInstanceOf(ValidationErrorException.class);
          final var code = ((ValidationErrorException) error).getCode();
          assertThat(code).isEqualTo("codePrefix.fieldName");
          testContext.completeNow();

        })));
  }

  /**
   * Check that a field can not be valid.
   *
   * @param value       that is not valid.
   * @param testContext test context to use.
   *
   * @see Validations#validateStringField(String, String, String, String...)
   */
  @ParameterizedTest(name = "The value '{0}' is not valid for a string value")
  @NullSource
  @ValueSource(strings = { "", "        " })
  public void shouldStringFieldNotBeValid(final String value, final VertxTestContext testContext) {

    Validations.validateStringField("codePrefix", "fieldName", value)
        .onComplete(testContext.failing(error -> testContext.verify(() -> {

          assertThat(error).isInstanceOf(ValidationErrorException.class);
          final var code = ((ValidationErrorException) error).getCode();
          assertThat(code).isEqualTo("codePrefix.fieldName");
          testContext.completeNow();

        })));

  }

  /**
   * Check that a field can not be a white value.
   *
   * @param testContext test context to use.
   *
   * @see Validations#validateStringField(String, String, String, String...)
   */
  @Test
  public void shouldStringFieldNotBeValidBecauseValueNotAllowed(final VertxTestContext testContext) {

    Validations.validateStringField("codePrefix", "fieldName", "   value    ", "1", "2", "3")
        .onComplete(testContext.failing(error -> testContext.verify(() -> {

          assertThat(error).isInstanceOf(ValidationErrorException.class);
          final var code = ((ValidationErrorException) error).getCode();
          assertThat(code).isEqualTo("codePrefix.fieldName");
          testContext.completeNow();

        })));

  }

  /**
   * Check that a non empty string is valid.
   *
   * @param testContext test context to use.
   *
   * @see Validations#validateStringField(String, String, String, String...)
   */
  @Test
  public void shouldStringBeValid(final VertxTestContext testContext) {

    Validations.validateStringField("codePrefix", "fieldName", "   value    ")
        .onComplete(testContext.succeeding(result -> testContext.verify(() -> {

          assertThat(result).isEqualTo("value");
          testContext.completeNow();
        })));

  }

  /**
   * Check that a non empty string is valid because is defined in the possible
   * values.
   *
   * @param testContext test context to use.
   *
   * @see Validations#validateStringField(String, String, String, String...)
   */
  @Test
  public void shouldStringBeValidBecauseIsAPosisbleValue(final VertxTestContext testContext) {

    Validations.validateStringField("codePrefix", "fieldName", "   value    ", "val", "lue", "value")
        .onComplete(testContext.succeeding(result -> testContext.verify(() -> {

          assertThat(result).isEqualTo("value");
          testContext.completeNow();
        })));

  }

  /**
   * Check that a non empty string is valid.
   *
   * @param testContext test context to use.
   *
   * @see Validations#validateStringField(String, String, String, String...)
   */
  @Test
  public void shouldStringBeValidBecauseIsNotEmpty(final VertxTestContext testContext) {

    Validations.validateStringField("codePrefix", "fieldName", "   value    ", (String[]) null)
        .onComplete(testContext.succeeding(result -> testContext.verify(() -> {

          assertThat(result).isEqualTo("value");
          testContext.completeNow();
        })));

  }

  /**
   * Check that a {@code null} time stamp is valid.
   *
   * @param testContext test context to use.
   *
   * @see Validations#validateTimeStamp(String, String, Long, boolean)
   */
  @Test
  public void shouldNullTimeStampBeValid(final VertxTestContext testContext) {

    Validations.validateTimeStamp("codePrefix", "fieldName", null, true)
        .onComplete(testContext.succeeding(empty -> testContext.completeNow()));

  }

  /**
   * Check that a time stamp is valid.
   *
   * @param value       that has to be valid.
   * @param testContext test context to use.
   *
   * @see Validations#validateTimeStamp(String, String, Long, boolean)
   */
  @ParameterizedTest(name = "The timestamp '{0}' has to be valid.")
  @ValueSource(longs = { 0l, 1l, 200l, 43l })
  public void shouldTimeStampBeValid(final Long value, final VertxTestContext testContext) {

    Validations.validateTimeStamp("codePrefix", "fieldName", value, true)
        .onComplete(testContext.succeeding(empty -> testContext.completeNow()));

  }

  /**
   * Check that a time stamp is mot valid.
   *
   * @param value       that has to be valid.
   * @param testContext test context to use.
   *
   * @see Validations#validateTimeStamp(String, String, Long, boolean)
   */
  @ParameterizedTest(name = "The timestamp '{0}' has not to be valid.")
  @ValueSource(longs = { -1l, -200l, -43l })
  public void shouldTimeStampBeNotValid(final Long value, final VertxTestContext testContext) {

    Validations.validateTimeStamp("codePrefix", "fieldName", value, true)
        .onComplete(testContext.failing(error -> testContext.verify(() -> {

          assertThat(error).isInstanceOf(ValidationErrorException.class);
          final var code = ((ValidationErrorException) error).getCode();
          assertThat(code).isEqualTo("codePrefix.fieldName");
          testContext.completeNow();

        })));

  }

  /**
   * Check that a {@code null} time stamp is not valid.
   *
   * @param value       that is not valid.
   * @param testContext test context to use.
   *
   * @see Validations#validateTimeStamp(String, String, Long, boolean)
   */
  @ParameterizedTest(name = "The value '{0}' is not valid for as timestamp")
  @NullSource
  @ValueSource(longs = { -1l })
  public void shouldNullTimeStampNotBeValid(final Long value, final VertxTestContext testContext) {

    Validations.validateTimeStamp("codePrefix", "fieldName", value, false)
        .onComplete(testContext.failing(error -> testContext.verify(() -> {

          assertThat(error).isInstanceOf(ValidationErrorException.class);
          final var code = ((ValidationErrorException) error).getCode();
          assertThat(code).isEqualTo("codePrefix.fieldName");
          testContext.completeNow();

        })));

  }

  /**
   * Check that is valid a {@code null} as a nullable string list.
   *
   * @param testContext test context to use.
   *
   * @see Validations#validateNullableListStringField(String, String,
   *      java.util.List)
   */
  @Test
  public void shouldSuccessValidateNullableListStringFieldWithNull(final VertxTestContext testContext) {

    Validations.validateNullableListStringField("codePrefix", "fieldName", null)
        .onComplete(testContext.succeeding(result -> testContext.verify(() -> {

          assertThat(result).isNull();
          testContext.completeNow();
        })));

  }

  /**
   * Check that is valid a empty list as a nullable string list.
   *
   * @param testContext test context to use.
   *
   * @see Validations#validateNullableListStringField(String, String,
   *      java.util.List)
   */
  @Test
  public void shouldSuccessValidateNullableListStringFieldWithEmptyList(final VertxTestContext testContext) {

    Validations.validateNullableListStringField("codePrefix", "fieldName", new ArrayList<>())
        .onComplete(testContext.succeeding(result -> testContext.verify(() -> {

          assertThat(result).isNull();
          testContext.completeNow();
        })));
  }

  /**
   * Check that is valid a list with some strings.
   *
   * @param testContext test context to use.
   *
   * @see Validations#validateNullableListStringField(String, String,
   *      java.util.List)
   */
  @Test
  public void shouldSuccessValidateNullableListStringFieldWithSomeValues(final VertxTestContext testContext) {

    final List<String> values = new ArrayList<>();
    values.add(null);
    values.add("                 ");
    values.add("  123         ");
    values.add("  12345         ");
    values.add(null);
    values.add("  1 3         ");
    values.add("                 ");

    Validations.validateNullableListStringField("codePrefix", "fieldName", values)
        .onComplete(testContext.succeeding(result -> testContext.verify(() -> {

          assertThat(result).hasSize(3).contains("123", atIndex(0)).contains("12345", atIndex(1)).contains("1 3",
              atIndex(2));
          testContext.completeNow();
        })));
  }

  /**
   * Check that is valid a list with some strings.
   *
   * @param testContext test context to use.
   *
   * @see Validations#validateNullableListStringField(String, String,
   *      java.util.List)
   */
  @Test
  public void shouldFailValidateNullableListStringFieldWithDuplicatedValues(final VertxTestContext testContext) {

    final List<String> values = new ArrayList<>();
    values.add(null);
    values.add("                 ");
    values.add("  123         ");
    values.add("  12345         ");
    values.add(null);
    values.add("  1 3         ");
    values.add("                 ");
    values.add("123");

    Validations.validateNullableListStringField("codePrefix", "fieldName", values)
        .onComplete(testContext.failing(error -> testContext.verify(() -> {

          assertThat(error).isInstanceOf(ValidationErrorException.class);
          final var code = ((ValidationErrorException) error).getCode();
          assertThat(code).isEqualTo("codePrefix.fieldName[7]");
          testContext.completeNow();

        })));
  }

  /**
   * A null list of models has to be valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Validations#validate(List, java.util.function.BiPredicate, String,
   *      Vertx)
   */
  @Test
  public void shouldNullListOfModelsBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final Promise<Void> promise = Promise.promise();
    final var future = promise.future();
    future.compose(Validations.validate(null, (a, b) -> a.equals(b), "codePrefix", vertx))
        .onComplete(testContext.succeeding(empty -> testContext.completeNow()));
    promise.complete();
  }

  /**
   * An empty list of models has to be valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Validations#validate(List, java.util.function.BiPredicate, String,
   *      Vertx)
   */
  @Test
  public void shouldEmptyListOfModelsBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final Promise<Void> promise = Promise.promise();
    final var future = promise.future();
    future.compose(Validations.validate(new ArrayList<>(), (a, b) -> a.equals(b), "codePrefix", vertx))
        .onComplete(testContext.succeeding(empty -> testContext.completeNow()));
    promise.complete();
  }

  /**
   * An list of {@code null} models has to be valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Validations#validate(List, java.util.function.BiPredicate, String,
   *      Vertx)
   */
  @Test
  public void shouldListWithNullModelsBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final List<? extends Validable> models = new ArrayList<>();
    models.add(null);
    models.add(null);
    models.add(null);
    models.add(null);
    models.add(null);
    final Promise<Void> promise = Promise.promise();
    final var future = promise.future();
    future.compose(Validations.validate(models, (a, b) -> a.equals(b), "codePrefix", vertx))
        .onComplete(testContext.succeeding(empty -> testContext.verify(() -> {
          assertThat(models).isEmpty();
          testContext.completeNow();
        })));
    promise.complete();
  }

  /**
   * An list of valid models has to be valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Validations#validate(List, java.util.function.BiPredicate, String,
   *      Vertx)
   */
  @Test
  public void shouldListWithValidModelsBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final Validable model1 = new Validable() {

      @Override
      public Future<Void> validate(final String codePrefix, final Vertx vertx) {

        return Future.succeededFuture();
      }
    };
    final Validable model4 = new Validable() {

      @Override
      public Future<Void> validate(final String codePrefix, final Vertx vertx) {

        return Future.succeededFuture();
      }
    };
    final List<Validable> models = new ArrayList<>();
    models.add(null);
    models.add(model1);
    models.add(null);
    models.add(null);
    models.add(model4);
    final Promise<Void> promise = Promise.promise();
    final var future = promise.future();
    future.compose(Validations.validate(models, (a, b) -> a.equals(b), "codePrefix", vertx))
        .onComplete(testContext.succeeding(empty -> testContext.verify(() -> {
          assertThat(models).hasSize(2).contains(model1, atIndex(0)).contains(model4, atIndex(1));
          testContext.completeNow();
        })));
    promise.complete();
  }

  /**
   * An list of valid and invalid models has not to be valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Validations#validate(List, java.util.function.BiPredicate, String,
   *      Vertx)
   */
  @Test
  public void shouldListWithValidAndInvalidModelsBeNotValid(final Vertx vertx, final VertxTestContext testContext) {

    final var validationError = new ValidationErrorException("code_of_error", "model not valid");
    final Validable model1 = new Validable() {

      @Override
      public Future<Void> validate(final String codePrefix, final Vertx vertx) {

        return Future.failedFuture(validationError);

      }
    };
    final Validable model4 = new Validable() {

      @Override
      public Future<Void> validate(final String codePrefix, final Vertx vertx) {

        return Future.succeededFuture();
      }
    };
    final List<Validable> models = new ArrayList<>();
    models.add(null);
    models.add(model1);
    models.add(null);
    models.add(null);
    models.add(model4);
    final Promise<Void> promise = Promise.promise();
    final var future = promise.future();
    future.compose(Validations.validate(models, (a, b) -> a.equals(b), "codePrefix", vertx))
        .onComplete(testContext.failing(error -> testContext.verify(() -> {
          assertThat(error).isEqualTo(validationError);
          assertThat(models).hasSize(2).contains(model1, atIndex(0)).contains(model4, atIndex(1));
          testContext.completeNow();
        })));
    promise.complete();
  }

  /**
   * An list of valid models has not to be valid because one is duplicated.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Validations#validate(List, java.util.function.BiPredicate, String,
   *      Vertx)
   */
  @Test
  public void shouldListWithValidModelsBeNotValidBecauseExistoneDuplicatedOne(final Vertx vertx,
      final VertxTestContext testContext) {

    final Validable model1 = new Validable() {

      @Override
      public Future<Void> validate(final String codePrefix, final Vertx vertx) {

        return Future.succeededFuture();

      }
    };
    final Validable model3 = new Validable() {

      @Override
      public Future<Void> validate(final String codePrefix, final Vertx vertx) {

        return Future.succeededFuture();

      }

      /**
       * {@inheritDoc}
       */
      @Override
      public boolean equals(final Object obj) {

        return true;
      }
    };
    final Validable model4 = new Validable() {

      @Override
      public Future<Void> validate(final String codePrefix, final Vertx vertx) {

        return Future.succeededFuture();
      }
    };
    final List<Validable> models = new ArrayList<>();
    models.add(null);
    models.add(model1);
    models.add(null);
    models.add(model3);
    models.add(null);
    models.add(model4);
    final Promise<Void> promise = Promise.promise();
    final var future = promise.future();
    future.compose(Validations.validate(models, (a, b) -> a.equals(b), "codePrefix", vertx))
        .onComplete(testContext.failing(error -> testContext.verify(() -> {
          assertThat(error).isInstanceOf(ValidationErrorException.class);
          assertThat(((ValidationErrorException) error).getCode()).isEqualTo("codePrefix[2]");
          assertThat(models).hasSize(3).contains(model1, atIndex(0)).contains(model3, atIndex(1)).contains(model4,
              atIndex(2));
          testContext.completeNow();
        })));
    promise.complete();
  }

  /**
   * Check the a double value is not valid.
   *
   * @param val         value that is not valid.
   * @param min         minimum range value.
   * @param max         maximum range value.
   * @param testContext test context to use.
   *
   * @see Validations#validateNumberOnRange(String, String, Number, boolean,
   *      Number, Number)
   */
  @ParameterizedTest(name = "The value {0} should not be valid")
  @CsvSource({ "1,0,0.1", "1,2,3", "-1,0,", "10,,1" })
  public void shouldNumberNotBeValid(final String val, final String min, final String max,
      final VertxTestContext testContext) {

    final Double value = Double.parseDouble(val);
    final var nullable = false;
    final var minValue = this.extractDouble(min);
    final var maxValue = this.extractDouble(max);

    Validations.validateNumberOnRange("codePrefix", "fieldName", value, nullable, minValue, maxValue)
        .onComplete(testContext.failing(error -> testContext.verify(() -> {

          assertThat(error).isInstanceOf(ValidationErrorException.class);
          final var code = ((ValidationErrorException) error).getCode();
          assertThat(code).isEqualTo("codePrefix.fieldName");
          testContext.completeNow();

        })));

  }

  /**
   * Return the double value defined on a string.
   *
   * @param value to extract the double.
   *
   * @return the double value or {@code null} if not double value defined.
   */
  private Double extractDouble(final String value) {

    try {

      return Double.parseDouble(value);

    } catch (final Throwable t) {

      return null;
    }

  }

  /**
   * Check the a {@code null} value is not valid when the element is not nullable.
   *
   * @param testContext test context to use.
   *
   * @see Validations#validateNumberOnRange(String, String, Number, boolean,
   *      Number, Number)
   */
  @Test
  public void shouldNullNumberNotBeValidWhenIsNotNullable(final VertxTestContext testContext) {

    Validations.validateNumberOnRange("codePrefix", "fieldName", null, false, 0d, 1d)
        .onComplete(testContext.failing(error -> testContext.verify(() -> {

          assertThat(error).isInstanceOf(ValidationErrorException.class);
          final var code = ((ValidationErrorException) error).getCode();
          assertThat(code).isEqualTo("codePrefix.fieldName");
          testContext.completeNow();

        })));

  }

  /**
   * Check the a double value is valid.
   *
   * @param val         value that is not valid.
   * @param min         minimum range value.
   * @param max         maximum range value.
   * @param testContext test context to use.
   *
   * @see Validations#validateNumberOnRange(String, String, Number, boolean,
   *      Number, Number)
   */
  @ParameterizedTest(name = "The value {0} should not be valid")
  @CsvSource({ "1,0,1", "1,1,3", "0.5,0,1", "-1,,1", "1,0," })
  public void shouldNumberBeValid(final String val, final String min, final String max,
      final VertxTestContext testContext) {

    final Double value = Double.parseDouble(val);
    final var nullable = false;
    final var minValue = this.extractDouble(min);
    final var maxValue = this.extractDouble(max);

    Validations.validateNumberOnRange("codePrefix", "fieldName", value, nullable, minValue, maxValue)
        .onComplete(testContext.succeeding(empty -> testContext.completeNow()));

  }

  /**
   * Check that a {@code null} URL is valid.
   *
   * @param testContext test context to use.
   *
   * @see Validations#validateNullableURLField(String, String, String)
   */
  @Test
  public void shouldNullURLBeValid(final VertxTestContext testContext) {

    Validations.validateNullableURLField("codePrefix", "fieldName", null)
        .onComplete(testContext.succeeding(result -> testContext.verify(() -> {

          assertThat(result).isNull();
          testContext.completeNow();
        })));
  }

  /**
   * Check that an URL is valid.
   *
   * @param testContext test context to use.
   *
   * @see Validations#validateNullableURLField(String, String, String)
   */
  @Test
  public void shouldURLBeValid(final VertxTestContext testContext) {

    Validations.validateNullableURLField("codePrefix", "fieldName", "HTTP://localhost:9090/profiles")
        .onComplete(testContext.succeeding(result -> testContext.verify(() -> {

          assertThat(result).isEqualTo("http://localhost:9090/profiles");
          testContext.completeNow();
        })));
  }

  /**
   * Check that a bad URL is not valid.
   *
   * @param testContext test context to use.
   *
   * @see Validations#validateNullableURLField(String, String, String)
   */
  @Test
  public void shouldNotURLBeValid(final VertxTestContext testContext) {

    Validations.validateNullableURLField("codePrefix", "fieldName", "a://HTTP://localhost:9090/profiles")
        .onComplete(testContext.failing(error -> testContext.verify(() -> {

          assertThat(error).isInstanceOf(ValidationErrorException.class);
          final var code = ((ValidationErrorException) error).getCode();
          assertThat(code).isEqualTo("codePrefix.fieldName");
          testContext.completeNow();

        })));
  }

  /**
   * Check that validate id fails because it is {@code null}.
   *
   * @param testContext test context to use.
   *
   * @see Validations#composeValidateId(Future, String, String, String, boolean,
   *      java.util.function.Function)
   */
  @Test
  public void shouldComposeValidateIdFailesBecuaseIdIsNull(final VertxTestContext testContext) {

    Validations.composeValidateId(Future.succeededFuture(), "codePrefix", "fieldName", null, false, null)
        .onComplete(testContext.failing(error -> testContext.verify(() -> {

          assertThat(error).isInstanceOf(ValidationErrorException.class);
          final var code = ((ValidationErrorException) error).getCode();
          assertThat(code).isEqualTo("codePrefix.fieldName");
          testContext.completeNow();

        })));
  }

  /**
   * Check that validate id fails because it has to exist but cannot found.
   *
   * @param testContext test context to use.
   *
   * @see Validations#composeValidateId(Future, String, String, String, boolean,
   *      java.util.function.Function)
   */
  @Test
  public void shouldComposeValidateIdFailesBecuaseIdDoesNotExist(final VertxTestContext testContext) {

    Validations.composeValidateId(Future.succeededFuture(), "codePrefix", "fieldName", "1", true,
        id -> Future.failedFuture("not found")).onComplete(testContext.failing(error -> testContext.verify(() -> {

          assertThat(error).isInstanceOf(ValidationErrorException.class);
          final var code = ((ValidationErrorException) error).getCode();
          assertThat(code).isEqualTo("codePrefix.fieldName");
          testContext.completeNow();

        })));
  }

  /**
   * Check that validate id success because it exists.
   *
   * @param testContext test context to use.
   *
   * @see Validations#composeValidateId(Future, String, String, String, boolean,
   *      java.util.function.Function)
   */
  @Test
  public void shouldComposeValidateIdBecuaseIdExist(final VertxTestContext testContext) {

    Validations.composeValidateId(Future.succeededFuture(), "codePrefix", "fieldName", "1", true,
        id -> Future.succeededFuture()).onComplete(testContext.succeeding(empty -> testContext.completeNow()));

  }

  /**
   * Check that validate id fails because it has to exist but cannot found.
   *
   * @param testContext test context to use.
   *
   * @see Validations#composeValidateId(Future, String, String, String, boolean,
   *      java.util.function.Function)
   */
  @Test
  public void shouldComposeValidateIdFailesBecuaseIdExist(final VertxTestContext testContext) {

    Validations.composeValidateId(Future.succeededFuture(), "codePrefix", "fieldName", "1", false,
        id -> Future.succeededFuture()).onComplete(testContext.failing(error -> testContext.verify(() -> {

          assertThat(error).isInstanceOf(ValidationErrorException.class);
          final var code = ((ValidationErrorException) error).getCode();
          assertThat(code).isEqualTo("codePrefix.fieldName");
          testContext.completeNow();

        })));
  }

  /**
   * Check that validate id because it does not exist.
   *
   * @param testContext test context to use.
   *
   * @see Validations#composeValidateId(Future, String, String, String, boolean,
   *      java.util.function.Function)
   */
  @Test
  public void shouldComposeValidateIdBecuaseNotExist(final VertxTestContext testContext) {

    Validations
        .composeValidateId(Future.succeededFuture(), "codePrefix", "fieldName", "1", false,
            id -> Future.failedFuture("Not found"))
        .onComplete(testContext.succeeding(empty -> testContext.completeNow()));
  }

  /**
   * Check that a {@code null} list of ids is valid.
   *
   * @param testContext test context to use.
   *
   * @see Validations#composeValidateIds(Future, String, String, List, boolean,
   *      java.util.function.Function)
   */
  @Test
  public void shouldComposeValidateIdsForNullBeValid(final VertxTestContext testContext) {

    Validations.composeValidateIds(Future.succeededFuture(), "codePrefix", "fieldName", null, false, null)
        .onComplete(testContext.succeeding(empty -> testContext.completeNow()));
  }

  /**
   * Check that an empty list of ids is valid.
   *
   * @param testContext test context to use.
   *
   * @see Validations#composeValidateIds(Future, String, String, List, boolean,
   *      java.util.function.Function)
   */
  @Test
  public void shouldComposeValidateIdsForEmptyBeValid(final VertxTestContext testContext) {

    Validations.composeValidateIds(Future.succeededFuture(), "codePrefix", "fieldName", new ArrayList<>(), false, null)
        .onComplete(testContext.succeeding(empty -> testContext.completeNow()));
  }

  /**
   * Check that some ids are valid.
   *
   * @param testContext test context to use.
   *
   * @see Validations#composeValidateIds(Future, String, String, List, boolean,
   *      java.util.function.Function)
   */
  @Test
  public void shouldComposeValidateIdsBeValid(final VertxTestContext testContext) {

    final var ids = new ArrayList<String>();
    ids.add(null);
    ids.add("1");
    ids.add("2");
    Validations.composeValidateIds(Future.succeededFuture(), "codePrefix", "fieldName", ids, true,
        id -> Future.succeededFuture()).onComplete(testContext.succeeding(empty -> testContext.verify(() -> {

          assertThat(ids).hasSize(2).containsExactly("1", "2");
          testContext.completeNow();

        })));

  }

  /**
   * Check that some ids are not valid.
   *
   * @param testContext test context to use.
   *
   * @see Validations#composeValidateIds(Future, String, String, List, boolean,
   *      java.util.function.Function)
   */
  @Test
  public void shouldComposeValidateIdsNotValidBecuaseIdNotFound(final VertxTestContext testContext) {

    final var ids = new ArrayList<String>();
    ids.add("1");
    ids.add("2");
    Validations.composeValidateIds(Future.succeededFuture(), "codePrefix", "fieldName", ids, true,
        id -> Future.failedFuture("not found")).onComplete(testContext.failing(error -> testContext.verify(() -> {

          assertThat(error).isInstanceOf(ValidationErrorException.class);
          final var code = ((ValidationErrorException) error).getCode();
          assertThat(code).isEqualTo("codePrefix.fieldName[0]");
          testContext.completeNow();

        })));

  }

  /**
   * Check that some ids are not valid because one is duplicated.
   *
   * @param testContext test context to use.
   *
   * @see Validations#composeValidateIds(Future, String, String, List, boolean,
   *      java.util.function.Function)
   */
  @Test
  public void shouldComposeValidateIdsNotValidBecuaseDuplicatedId(final VertxTestContext testContext) {

    final var ids = new ArrayList<String>();
    ids.add("0");
    ids.add("1");
    ids.add("1");
    Validations.composeValidateIds(Future.succeededFuture(), "codePrefix", "fieldName", ids, false,
        id -> Future.succeededFuture()).onComplete(testContext.failing(error -> testContext.verify(() -> {

          assertThat(error).isInstanceOf(ValidationErrorException.class);
          final var code = ((ValidationErrorException) error).getCode();
          assertThat(code).isEqualTo("codePrefix.fieldName[2]");
          testContext.completeNow();

        })));

  }

  /**
   * Decode the Json defined on the specified value.
   *
   * @param data to decode.
   * @param type of the value to decode.
   *
   * @return the decoded json value.
   */
  @SuppressWarnings("unchecked")
  protected <T> T decodeJson(final String data, final Class<T> type) {

    T value = null;
    if (data != null) {

      final var normalizedData = data.trim().replaceAll("'", "\"");
      if (!normalizedData.isEmpty() && !"null".equals(normalizedData)) {

        try {

          value = (T) Json.decodeValue(normalizedData);

        } catch (final Throwable cause) {

          fail("Cannot obtain the JSON, because " + cause);
        }

      }
    }

    return value;

  }

  /**
   * Decode the Json object defined on the specified value.
   *
   * @param data to obtain the object.
   *
   * @return the json object defined on the data.
   */
  protected JsonObject decodeJsonObject(final String data) {

    return this.decodeJson(data, JsonObject.class);

  }

  /**
   * Decode the Json array defined on the specified value.
   *
   * @param data to obtain the array.
   *
   * @return the json array defined on the data.
   */
  protected JsonArray decodeJsonArray(final String data) {

    return this.decodeJson(data, JsonArray.class);

  }

  /**
   * Check that a specification is not valid.
   *
   * @param data        to use on the test.
   * @param testContext test context to use.
   *
   * @see Validations#validateOpenAPISpecification(String,io.vertx.core.json.JsonObject)
   */
  @ParameterizedTest(name = "The specification {0} has to not be valid")
  @ValueSource(strings = { "codePrefix#null", "codePrefix#{'undefined':'something'}",
      "codePrefix.undefined#{'type':'object','undefined':'something'}", "codePrefix#{'nullable':true}",
      "codePrefix.type#{'type':null}", "codePrefix.type#{'type':'undefined'}",
      "codePrefix.minimum#{'type':'integer','minimum':'1'}", "codePrefix.minimum#{'type':'string','minimum':1}",
      "codePrefix.maximum#{'type':'integer','maximum':'1'}", "codePrefix.maximum#{'type':'string','maximum':1}",
      "codePrefix.maximum#{'type':'integer','minimum':10,'maximum':1}",
      "codePrefix.multipleOf#{'type':'integer','multipleOf':'1'}",
      "codePrefix.multipleOf#{'type':'string','multipleOf':1}",
      "codePrefix.exclusiveMinimum#{'type':'integer','exclusiveMinimum':'1'}",
      "codePrefix.exclusiveMinimum#{'type':'string','exclusiveMinimum':1}",
      "codePrefix.exclusiveMaximum#{'type':'integer','exclusiveMaximum':'1'}",
      "codePrefix.exclusiveMaximum#{'type':'string','exclusiveMaximum':1}",
      "codePrefix.minimum#{'type':'number','minimum':'1'}", "codePrefix.minimum#{'type':'string','minimum':1}",
      "codePrefix.maximum#{'type':'number','minimum':10,'maximum':1}",
      "codePrefix.maximum#{'type':'string','maximum':1}", "codePrefix.multipleOf#{'type':'number','multipleOf':'1'}",
      "codePrefix.multipleOf#{'type':'string','multipleOf':1}",
      "codePrefix.exclusiveMinimum#{'type':'number','exclusiveMinimum':'1'}",
      "codePrefix.exclusiveMinimum#{'type':'string','exclusiveMinimum':1}",
      "codePrefix.exclusiveMaximum#{'type':'number','exclusiveMaximum':'1'}",
      "codePrefix.exclusiveMaximum#{'type':'string','exclusiveMaximum':1}",
      "codePrefix.default#{'type':'string','default':true}", "codePrefix.default#{'type':'string','default':'[]'}",
      "codePrefix.type#{'type':'array'}", "codePrefix.default#{'type':'string','default':'{'}",
      "codePrefix.properties#{'type':'object','properties':[]}", "codePrefix.items#{'type':'array','items':[]}",
      "codePrefix.nullable#{'type':'string','nullable':[]}",
      "codePrefix.required#{'type':'object','properties':{'id':{'type':'string'}},'required':[]}",
      "codePrefix.required#{'type':'string','required':['id']}",
      "codePrefix.required#{'type':'string','required':{'id':true}}",
      "codePrefix.required#{'type':'object','properties':{'id':{'type':'string'}},'required':{'id':true}}",
      "codePrefix.required[1]#{'type':'object','properties':{'id':{'type':'string'}},'required':['id',1]}",
      "codePrefix.required[1]#{'type':'object','properties':{'id':{'type':'string'}},'required':['id','undefined']}",
      "codePrefix.uniqueItems#{'type':'array','items':{'type':'integer'},'uniqueItems':{},'default':'[1,2,3]'}",
      "codePrefix.default[1]#{'type':'array','items':{'type':'integer'},'uniqueItems':true,'default':'[1,1,1]'}",
      "codePrefix.uniqueItems#{'type':'string','uniqueItems':true}", "codePrefix.enum#{'type':'string','enum':true}",
      "codePrefix.enum#{'type':'string','enum':[]}", "codePrefix.enum[1]#{'type':'string','enum':['a','a']}",
      "codePrefix.enum[1]#{'type':'string','enum':['a',null]}", "codePrefix.enum[1]#{'type':'string','enum':['a',2]}",
      "codePrefix.pattern#{'type':'integer','pattern':'^\\\\d{3}-\\\\d{2}-\\\\d{4}$'}",
      "codePrefix.pattern#{'type':'string','pattern':'^\\\\d{$'}", "codePrefix.pattern#{'type':'string','pattern':[]}",
      "codePrefix.uniqueItems#{'type':'string','uniqueItems':false}",
      "codePrefix.uniqueItems#{'type':'array','items':{'type':'string'},'uniqueItems':[]}",
      "codePrefix.minItems#{'type':'string','minItems':2}",
      "codePrefix.minItems#{'type':'array','items':{'type':'string'},'minItems':'2'}",
      "codePrefix.maxItems#{'type':'string','maxItems':2}",
      "codePrefix.maxItems#{'type':'array','items':{'type':'string'},'maxItems':'2'}",
      "codePrefix.maxItems#{'type':'array','items':{'type':'string'},'minItems':10,'maxItems':2}",
      "codePrefix.additionalProperties#{'type':'object','additionalProperties':true,'properties':{}}",
      "codePrefix.additionalProperties.type#{'type':'object','additionalProperties':{'type':'undefined'}}",
      "codePrefix.additionalProperties#{'type':'object','additionalProperties':{'type':'string'},'properties':{}}",
      "codePrefix.title#{'type':'object','title':{'name':'a'}}",
      "codePrefix.description#{'type':'object','description':{'name':'a'}}",
      "codePrefix.maxLength#{'type':'array','items':{},'maxLength':10}",
      "codePrefix.minLength#{'type':'array','items':{},'minLength':1}",
      "codePrefix.maxLength#{'type':'string','maxLength':1.1}",
      "codePrefix.minLength#{'type':'string','minLength':0.1}",
      "codePrefix.maxLength#{'type':'string','minLength':10,'maxLength':1}",
      "codePrefix.minProperties#{'type':'object','minProperties':2.01}",
      "codePrefix.maxProperties#{'type':'object','maxProperties':20.1}",
      "codePrefix.minProperties#{'type':'string','minProperties':2}",
      "codePrefix.maxProperties#{'type':'string','maxProperties':20}",
      "codePrefix.maxProperties#{'type':'object','minProperties':2,'maxProperties':1}",
      "codePrefix.$ref#{'$ref':'components/schemas/Task'}", "codePrefix.oneOf#{'oneOf':{}}",
      "codePrefix.anyOf#{'anyOf':{}}", "codePrefix.allOf#{'allOf':{}}", "codePrefix.oneOf#{'oneOf':[]}",
      "codePrefix.anyOf#{'anyOf':[]}", "codePrefix.allOf#{'allOf':[]}",
      "codePrefix.oneOf#{'oneOf':[{}],'type':'object'}", "codePrefix.anyOf#{'anyOf':[{}],'type':'object'}",
      "codePrefix.allOf#{'allOf':[{}],'type':'object'}",
      "codePrefix.oneOf[1].type#{'oneOf':[{'type':'object'},{'type':'undefined'}]}",
      "codePrefix.anyOf[1].type#{'anyOf':[{'type':'object'},{'type':'undefined'}]}",
      "codePrefix.allOf[1].type#{'allOf':[{'type':'object'},{'type':'undefined'}]}", })
  public void shouldNotBeValidBadOpenAPISpecification(final String data, final VertxTestContext testContext) {

    final var split = data.split("#");
    final var specification = this.decodeJsonObject(split[1]);
    Validations.validateOpenAPISpecification("codePrefix", specification)
        .onComplete(testContext.failing(error -> testContext.verify(() -> {

          assertThat(error).isInstanceOf(ValidationErrorException.class);
          final var code = ((ValidationErrorException) error).getCode();
          assertThat(code).isEqualTo(split[0]);
          testContext.completeNow();

        })));

  }

  /**
   * Check that a specification is not valid.
   *
   * @param data        to use on the test.
   * @param testContext test context to use.
   *
   * @see Validations#validateOpenAPIProperties(String, JsonObject)
   */
  @ParameterizedTest(name = "The specification {0} has to not be valid")
  @ValueSource(strings = { "codePrefix#null", "codePrefix.field#{'field':null}", "codePrefix.key#{'key':true}",
      "codePrefix.field#{'field':{'nullable':true}}", "codePrefix.field.type#{'field':{'type':null}}",
      "codePrefix.field.type#{'field':{'type':'undefined'}}",
      "codePrefix.field.minimum#{'field':{'type':'integer','minimum':'1'}}",
      "codePrefix.field.minimum#{'field':{'type':'string','minimum':1}}",
      "codePrefix.field.maximum#{'field':{'type':'integer','maximum':'1'}}",
      "codePrefix.field.maximum#{'field':{'type':'string','maximum':1}}",
      "codePrefix.field.multipleOf#{'field':{'type':'integer','multipleOf':'1'}}",
      "codePrefix.field.multipleOf#{'field':{'type':'string','multipleOf':1}}",
      "codePrefix.field.exclusiveMinimum#{'field':{'type':'integer','exclusiveMinimum':'1'}}",
      "codePrefix.field.exclusiveMinimum#{'field':{'type':'string','exclusiveMinimum':1}}",
      "codePrefix.field.exclusiveMaximum#{'field':{'type':'integer','exclusiveMaximum':'1'}}",
      "codePrefix.field.exclusiveMaximum#{'field':{'type':'string','exclusiveMaximum':1}}",
      "codePrefix.field.minimum#{'field':{'type':'number','minimum':'1'}}",
      "codePrefix.field.minimum#{'field':{'type':'string','minimum':1}}",
      "codePrefix.field.maximum#{'field':{'type':'number','maximum':'1'}}",
      "codePrefix.field.maximum#{'field':{'type':'string','maximum':1}}",
      "codePrefix.field.multipleOf#{'field':{'type':'number','multipleOf':'1'}}",
      "codePrefix.field.multipleOf#{'field':{'type':'string','multipleOf':1}}",
      "codePrefix.field.exclusiveMinimum#{'field':{'type':'number','exclusiveMinimum':'1'}}",
      "codePrefix.field.exclusiveMinimum#{'field':{'type':'string','exclusiveMinimum':1}}",
      "codePrefix.field.exclusiveMaximum#{'field':{'type':'number','exclusiveMaximum':'1'}}",
      "codePrefix.field.exclusiveMaximum#{'field':{'type':'string','exclusiveMaximum':1}}", })
  public void shouldNotBeValidBadOpenAPIProperties(final String data, final VertxTestContext testContext) {

    final var split = data.split("#");
    final var specification = this.decodeJsonObject(split[1]);
    Validations.validateOpenAPIProperties("codePrefix", specification)
        .onComplete(testContext.failing(error -> testContext.verify(() -> {

          assertThat(error).isInstanceOf(ValidationErrorException.class);
          final var code = ((ValidationErrorException) error).getCode();
          assertThat(code).isEqualTo(split[0]);
          testContext.completeNow();

        })));

  }

  /**
   * Check that a specification is valid.
   *
   * @param data        to use on the test.
   * @param testContext test context to use.
   *
   * @see Validations#validateOpenAPISpecification(String,io.vertx.core.json.JsonObject)
   */
  @ParameterizedTest(name = "The specification {0} has to be valid")
  @ValueSource(strings = { "{}", "{'type':'boolean'}", "{'type':'integer'}", "{'type':'integer','minimum':0}",
      "{'type':'integer','maximum':100}", "{'type':'integer','minimum':0,'exclusiveMinimum':true}",
      "{'type':'integer','maximum':100,'exclusiveMaximum':true}", "{'type':'integer','multipleOf':10}",
      "{'type':'integer','minimum':0,'maximum':100,'multipleOf':10,'exclusiveMinimum':true,'exclusiveMaximum':true}",
      "{'type':'number'}", "{'type':'number','minimum':0}", "{'type':'number','maximum':100}",
      "{'type':'number','minimum':0,'exclusiveMinimum':true}",
      "{'type':'number','maximum':100,'exclusiveMaximum':true}", "{'type':'number','multipleOf':10}",
      "{'type':'number','minimum':0,'maximum':100,'multipleOf':10,'exclusiveMinimum':true,'exclusiveMaximum':true}",
      "{'type':'object','properties':{}}", "{'type':'array','items':{}}", "{'type':'boolean','default':'true'}",
      "{'type':'integer','default':'100'}", "{'type':'number','default':'10.0'}",
      "{'type':'object','properties':{'id':{'type':'integer'}},'default':'{\\\"id\\\":0}'}",
      "{'type':'array','items':{'type':'integer'},'default':'[1,2,3]'}",
      "{'type':'array','items':{'type':'integer'},'default':'[1,2,3]','uniqueItems':true}",
      "{'type':'object','properties':{'id':{'type':'string'},'label':{'type':'string'}},'required':['id','label']}",
      "{'type':'string','enum':['a','b','c']}", "{'type':'integer','enum':[1,2,3,4]}",
      "{'type':'string','nullable':true,'enum':['a','b','c',null]}",
      "{'type':'string','pattern':'^\\\\d{3}-\\\\d{2}-\\\\d{4}$'}",
      "{'type':'array','items':{'type':'string'},'uniqueItems':false}",
      "{'type':'array','items':{'type':'string'},'uniqueItems':true}",
      "{'type':'array','items':{'type':'string'},'minItems':2}",
      "{'type':'array','items':{'type':'string'},'maxItems':2}",
      "{'type':'array','items':{'type':'string'},'minItems':1,'maxItems':2}",
      "{'type':'array','items':{'type':'string'},'minItems':2,'maxItems':2}",
      "{'type':'array','items':{'type':'string'},'minItems':1,'maxItems':2,'uniqueItems':true}", "{'type':'object'}",
      "{'type':'object','required':['id']}", "{'type':'object','additionalProperties':{'type':'boolean'}}",
      "{'type':'object','additionalProperties':{'type':'object','properties':{'id':{'type':'string'}}}}",
      "{'type':'string','example':'Hello world!'}", "{'type':'string','examples':{'en':'Hello!','hw':'Aloha'}}",
      "{'type':'string','maxLength':10}", "{'type':'string','minLength':1}",
      "{'type':'string','minLength':1,'maxLength':10}", "{'type':'string','format':'email'}",
      "{'type':'object','minProperties':2}", "{'type':'object','maxProperties':20}",
      "{'type':'object','minProperties':2,'maxProperties':20}", "{'oneOf':[{'type':'string'},{'type':'integer'}]}",
      "{'anyOf':[{'type':'string'},{'type':'integer'}]}",
      "{'allOf':[{'type':'object','properties':{'id':{'type':'string'}}},{'type':'object','properties':{'name':{'type':'integer'}}}]}", })
  public void shouldIsValidOpenAPISpecification(final String data, final VertxTestContext testContext) {

    final var specification = this.decodeJsonObject(data);
    Validations.validateOpenAPISpecification("codePrefix", specification)
        .onComplete(testContext.succeeding(empty -> testContext.completeNow()));

  }

  /**
   * Check that a specification is valid.
   *
   * @param data        to use on the test.
   * @param testContext test context to use.
   *
   * @see Validations#validateOpenAPIProperties(String,io.vertx.core.json.JsonObject)
   */
  @ParameterizedTest(name = "The specification {0} has to be valid")
  @ValueSource(strings = { "{}", "{'field':{}}", "{'field':{'type':'boolean'}}", "{'field':{'type':'integer'}}",
      "{'field':{'type':'integer','minimum':0}}", "{'field':{'type':'integer','maximum':100}}",
      "{'field':{'type':'integer','minimum':0,'exclusiveMinimum':true}}",
      "{'field':{'type':'integer','maximum':100,'exclusiveMaximum':true}}",
      "{'field':{'type':'integer','multipleOf':10}}",
      "{'field':{'type':'integer','minimum':0,'maximum':100,'multipleOf':10,'exclusiveMinimum':true,'exclusiveMaximum':true}}",
      "{'field':{'type':'number'}}", "{'field':{'type':'number','minimum':0}}",
      "{'field':{'type':'number','maximum':100}}", "{'field':{'type':'number','minimum':0,'exclusiveMinimum':true}}",
      "{'field':{'type':'number','maximum':100,'exclusiveMaximum':true}}",
      "{'field':{'type':'number','multipleOf':10}}",
      "{'field':{'type':'number','minimum':0,'maximum':100,'multipleOf':10,'exclusiveMinimum':true,'exclusiveMaximum':true}}",
      "{'field':{'type':'object','properties':{}}}", "{'field':{'type':'array','items':{}}}",
      "{'field':{'type':'string','nullable':true}}", "{'field':{'type':'string','nullable':false}}",
      "{'field1':{},'field2':{'type':'boolean'},'field3':{'type':'integer'},'field4':{'type':'number','maximum':100,'exclusiveMaximum':true}}",
      "{'field1':{'type':'object','properties':{'a':{'type':'string'},'b':{'type':'object','properties':{'d':{'type':'string'}}}}},'field2':{'type':'array','items':{'type':'integer'}},'field4':{'type':'number','maximum':100,'exclusiveMaximum':true}}", })
  public void shouldIsValidOpenAPIProperties(final String data, final VertxTestContext testContext) {

    final var specification = this.decodeJsonObject(data);
    Validations.validateOpenAPIProperties("codePrefix", specification)
        .onComplete(testContext.succeeding(empty -> testContext.completeNow()));

  }

  /**
   * Decode a value.
   *
   * @param data to obtain the value.
   *
   * @return the value defined on the data.
   */
  private Object decodeValue(final String data) {

    Object object = null;
    if (data != null) {

      final var normalizedData = data.trim().replaceAll("'", "\"");
      if (!normalizedData.isEmpty() && !"null".equals(normalizedData)) {

        if (normalizedData.charAt(0) == '"') {

          return normalizedData.substring(1, normalizedData.length() - 1);

        } else if (normalizedData.equalsIgnoreCase("true")) {

          return Boolean.TRUE;

        } else if (normalizedData.equalsIgnoreCase("false")) {

          return Boolean.FALSE;

        } else if (normalizedData.charAt(0) == '{' || normalizedData.charAt(0) == '[') {

          try {

            object = Json.decodeValue(normalizedData);

          } catch (final Throwable cause) {

            fail("Cannot obtain the JSON value, because " + cause);
          }

        } else {

          try {

            try {

              object = Long.valueOf(normalizedData);

            } catch (final Throwable cause) {

              object = Double.valueOf(normalizedData);
            }

          } catch (final Throwable cause) {

            fail("Cannot obtain the Number value, because " + cause);
          }
        }

      }
    }

    return object;

  }

  /**
   * Check that a value is not valid, because it not follow the OpenAPI
   * specification.
   *
   * @param data        to use on the test.
   * @param testContext test context to use.
   *
   * @see Validations#validateOpenAPIValue(String,io.vertx.core.json.JsonObject,Object)
   */
  @ParameterizedTest(name = "The value {0} has to not be valid")
  @ValueSource(strings = { "codePrefix#null#null", "codePrefix#null#{}", "codePrefix#{'type':'string'}#null",
      "codePrefix#{'nullable':false}#null", "codePrefix#{'type':'string'}#1",
      "codePrefix#{'type':'integer'}#{'field':1.0}", "codePrefix#{'type':'number'}#{'field':'1.0'}",
      "codePrefix#{'type':'boolean'}#{'field':'true'}", "codePrefix#{'type':'array','items':{'type':'string'}}#true",
      "codePrefix[0]#{'type':'array','items':{'type':'string'}}#[true]",
      "codePrefix#{'type':'object','properties':{}}#[]", "codePrefix#{'type':'object','properties':{}}#1",
      "codePrefix.field#{'type':'object','properties':{'field':{'nullable':false}}}#{'field':null}",
      "codePrefix.field#{'type':'object','properties':{'field':{'type':'string'}}}#{'field':1}",
      "codePrefix.field#{'type':'object','properties':{'field':{'type':'integer'}}}#{'field':1.0}",
      "codePrefix.field#{'type':'object','properties':{'field':{'type':'number'}}}#{'field':'1.0'}",
      "codePrefix.field#{'type':'object','properties':{'field':{'type':'boolean'}}}#{'field':'true'}",
      "codePrefix.field#{'type':'object','properties':{'field':{'type':'array','items':{'type':'string'}}}}#{'field':'true'}",
      "codePrefix.field[0]#{'type':'object','properties':{'field':{'type':'array','items':{'type':'string'}}}}#{'field':[true]}",
      "codePrefix.field#{'type':'object','properties':{'field':{'type':'object','properties':{}}}}#{'field':'true'}",
      "codePrefix.field#{'type':'object','properties':{'field':{'type':'object','properties':{}}}}#{'field':[]}",
      "codePrefix.parent.field#{'type':'object','properties':{'parent':{'type':'object','properties':{'field':{'nullable':false}}}}}#{'parent':{'field':null}}",
      "codePrefix.parent.field#{'type':'object','properties':{'parent':{'type':'object','properties':{'field':{'type':'string'}}}}}#{'parent':{'field':1}}",
      "codePrefix.parent.field#{'type':'object','properties':{'parent':{'type':'object','properties':{'field':{'type':'integer'}}}}}#{'parent':{'field':1.0}}",
      "codePrefix.parent.field#{'type':'object','properties':{'parent':{'type':'object','properties':{'field':{'type':'number'}}}}}#{'parent':{'field':'1.0'}}",
      "codePrefix.parent.field#{'type':'object','properties':{'parent':{'type':'object','properties':{'field':{'type':'boolean'}}}}}#{'parent':{'field':'true'}}",
      "codePrefix.parent.field#{'type':'object','properties':{'parent':{'type':'object','properties':{'field':{'type':'array','items':{'type':'string'}}}}}}#{'parent':{'field':'true'}}",
      "codePrefix.parent.field[0]#{'type':'object','properties':{'parent':{'type':'object','properties':{'field':{'type':'array','items':{'type':'string'}}}}}}#{'parent':{'field':[true]}}",
      "codePrefix.parent.field#{'type':'object','properties':{'parent':{'type':'object','properties':{'field':{'type':'object','properties':{}}}}}}#{'parent':{'field':'true'}}",
      "codePrefix.parent.field#{'type':'object','properties':{'parent':{'type':'object','properties':{'field':{'type':'object','properties':{}}}}}}#{'parent':{'field':[]}}",
      "codePrefix#{'type':'undefined'}#{}",
      "codePrefix.id#{'type':'object','properties':{'id':{'type':'string'}}}#{'id':[]}",
      "codePrefix#{'type':'string','enum':['a','b','c']}#null", "codePrefix#{'type':'string','enum':['a','b','c']}#'d'",
      "codePrefix#{'type':'number','maximum':1,'minimum':0}#-0.1",
      "codePrefix#{'type':'number','maximum':1,'minimum':0}#1.1",
      "codePrefix#{'type':'number','minimum':0,'exclusiveMinimum':true,'maximum':1}#0",
      "codePrefix#{'type':'number','minimum':0,'exclusiveMinimum':true,'maximum':1}#-0.1",
      "codePrefix#{'type':'number','minimum':0,'maximum':1,'exclusiveMaximum':true}#1",
      "codePrefix#{'type':'number','minimum':0,'maximum':1,'exclusiveMaximum':true}#1.1",
      "codePrefix#{'type':'number','minimum':0,'exclusiveMinimum':true,'maximum':1,'exclusiveMaximum':true}#0",
      "codePrefix#{'type':'number','minimum':0,'exclusiveMinimum':true,'maximum':1,'exclusiveMaximum':true}#1",
      "codePrefix#{'type':'integer','maximum':10,'minimum':0}#-1",
      "codePrefix#{'type':'integer','maximum':10,'minimum':0}#11",
      "codePrefix#{'type':'integer','minimum':0,'exclusiveMinimum':true,'maximum':10}#0",
      "codePrefix#{'type':'integer','minimum':0,'exclusiveMinimum':true,'maximum':10}#11",
      "codePrefix#{'type':'integer','minimum':0,'maximum':10,'exclusiveMaximum':true}#10",
      "codePrefix#{'type':'integer','minimum':0,'maximum':10,'exclusiveMaximum':true}#-1",
      "codePrefix#{'type':'integer','minimum':0,'exclusiveMinimum':true,'maximum':10,'exclusiveMaximum':true}#0",
      "codePrefix#{'type':'integer','minimum':0,'exclusiveMinimum':true,'maximum':10,'exclusiveMaximum':true}#10",
      "codePrefix#{'type':'string','pattern':'^\\\\d{3}-\\\\d{2}-\\\\d{4}$'}#''",
      "codePrefix[1]#{'type':'array','items':{'type':'string'},'uniqueItems':true}#['1','1','1']",
      "codePrefix#{'type':'array','items':{'type':'string'},'minItems':2}#['1']",
      "codePrefix#{'type':'array','items':{'type':'string'},'minItems':2}#[]",
      "codePrefix#{'type':'array','items':{'type':'string'},'maxItems':2}#['1','1','1']",
      "codePrefix#{'type':'array','items':{'type':'string'},'minItems':1,'maxItems':2}#['1','1','1']",
      "codePrefix#{'type':'array','items':{'type':'string'},'minItems':1,'maxItems':2}#[]",
      "codePrefix[1]#{'type':'array','items':{'type':'string'},'minItems':1,'maxItems':2,'uniqueItems':true}#['1','1']",
      "codePrefix#{'type':'array','items':{'type':'string'},'minItems':1,'maxItems':2,'uniqueItems':true}#[]",
      "codePrefix#{'type':'array','items':{'type':'string'},'minItems':1,'maxItems':2,'uniqueItems':true}#['1','2','1']",
      "codePrefix#{'type':'array','items':{'type':'string'},'minItems':1,'maxItems':2,'uniqueItems':true}#['1','2','3']",
      "codePrefix#{'type':'array','items':[],'minItems':1,'maxItems':2,'uniqueItems':true}#['1','2']",
      "codePrefix#{'type':'object','properties':[]}#{}",
      "codePrefix.field#{'type':'object','properties':{'field':{'nullable':true}},'required':['field']}#{}",
      "codePrefix.undefined#{'type':'object','properties':{'field':{'nullable':false}}}#{'undefined':true}",
      "codePrefix.id#{'type':'object','required':['id']}#{'a':true,'b':{'c':1}}",
      "codePrefix.b#{'type':'object','additionalProperties':{'type':'boolean'}}#{'a':true,'b':{'c':1}}",
      "codePrefix#{'type':'string','maxLength':1}#'123'", "codePrefix#{'type':'string','minLength':1}#''",
      "codePrefix#{'type':'string','minLength':1,'maxLength':10}#''",
      "codePrefix#{'type':'string','minLength':1,'maxLength':10}#'12345678901'",
      "codePrefix#{'oneOf':[{'type':'string'},{'type':'integer'}]}#true",
      "codePrefix#{'oneOf':[{'type':'number'},{'type':'integer'}]}#1",
      "codePrefix#{'anyOf':[{'type':'string'},{'type':'integer'}]}#true",
      "codePrefix#{'oneOf':[{'type':'object','properties':{'name':{'type':'string'}}},{'type':'object','properties':{'id':{'type':'integer'}}}]}#{'id':1,'name':'Jane Doe'}",
      "codePrefix#{'allOf':[{'type':'object','properties':{'id':{'type':'string'}}},{'type':'object','properties':{'name':{'type':'integer'}}}]}#true", })
  public void shouldNotBeValidBadOpenAPIValue(final String data, final VertxTestContext testContext) {

    final var split = data.split("#");
    final var specification = this.decodeJsonObject(split[1]);
    final var value = this.decodeValue(split[2]);

    Validations.validateOpenAPIValue("codePrefix", specification, value)
        .onComplete(testContext.failing(error -> testContext.verify(() -> {

          assertThat(error).isInstanceOf(ValidationErrorException.class);
          final var code = ((ValidationErrorException) error).getCode();
          assertThat(code).isEqualTo(split[0]);
          testContext.completeNow();

        })));

  }

  /**
   * Check that a value follows an OpenAPI specification.
   *
   * @param data        to use on the test.
   * @param testContext test context to use.
   *
   * @see Validations#validateOpenAPIValue(String,io.vertx.core.json.JsonObject,Object)
   */
  @ParameterizedTest(name = "The value {0} has to be valid")
  @ValueSource(strings = { "{}#null#null", "{}#{}#{}", "{'nullable':true}#null#null",
      "{'nullable':true,'default':'\\'something\\''}#null#'something'", "{'type':'string'}#'value'#'value'",
      "{'type':'string','default':'\\'value\\''}#null#'value'", "{'type':'integer'}#1#1",
      "{'type':'integer','default':'1'}#null#1", "{'type':'number'}#1.0#1.0",
      "{'type':'number','default':'1.0'}#null#1.0", "{'type':'boolean'}#true#true", "{'type':'boolean'}#false#false",
      "{'type':'boolean','default':'false'}#null#false",
      "{'type':'object','properties':{'field':{'nullable':true}}}#{'field':null}#{'field':null}#{'field':null}",
      "{'type':'object','properties':{'field':{'type':'string'}}}#{'field':'value'}#{'field':'value'}",
      "{'type':'object','properties':{'field':{'type':'integer'}}}#{'field':1}#{'field':1}",
      "{'type':'object','properties':{'field':{'type':'number'}}}#{'field':1.0}#{'field':1.0}",
      "{'type':'object','properties':{'field':{'type':'boolean'}}}#{'field':true}#{'field':true}",
      "{'type':'object','properties':{'field':{'type':'integer','default':'1'}}}#{}#{'field':1}",
      "{'type':'object','properties':{'id':{'type':'integer'}},'default':'{\\\"id\\\":0}'}#null#{'id':0}",
      "{'type':'object','properties':{'id':{'type':'integer'}},'default':'{\\\"id\\\":0}'}#{}#{}",
      "{'type':'string','enum':['a','b','c']}#'a'#'a'",
      "{'type':'string','nullable':true,'enum':['a','b','c',null]}#null#null",
      "{'type':'number','maximum':1,'minimum':0}#0#0", "{'type':'number','maximum':1,'minimum':0}#1#1",
      "{'type':'number','maximum':1,'minimum':0}#0.5#0.5",
      "{'type':'number','minimum':0,'exclusiveMinimum':true,'maximum':1}#1#1",
      "{'type':'number','minimum':0,'exclusiveMinimum':true,'maximum':1}#0.5#0.5",
      "{'type':'number','minimum':0,'maximum':1,'exclusiveMaximum':true}#0#0",
      "{'type':'number','minimum':0,'maximum':1,'exclusiveMaximum':true}#0.5#0.5",
      "{'type':'number','minimum':0,'exclusiveMinimum':true,'maximum':1,'exclusiveMaximum':true}#0.5#0.5",
      "{'type':'integer','maximum':10,'minimum':0}#0#0", "{'type':'integer','maximum':10,'minimum':0}#10#10",
      "{'type':'integer','maximum':10,'minimum':0}#5#5",
      "{'type':'integer','minimum':0,'exclusiveMinimum':true,'maximum':10}#10#10",
      "{'type':'integer','minimum':0,'exclusiveMinimum':true,'maximum':10}#5#5",
      "{'type':'integer','minimum':0,'maximum':10,'exclusiveMaximum':true}#0#0",
      "{'type':'integer','minimum':0,'maximum':10,'exclusiveMaximum':true}#5#5",
      "{'type':'integer','minimum':0,'exclusiveMinimum':true,'maximum':10,'exclusiveMaximum':true}#5#5",
      "{'type':'string','pattern':'^\\\\d{3}-\\\\d{2}-\\\\d{4}$'}#'123-12-1234'#'123-12-1234'",
      "{'type':'object','properties':{'id':{'type':'integer','default':'0'}},'required':['id']}#{}#{'id':0}",
      "{'type':'array','items':{'type':'string'},'uniqueItems':false}#['1','1','1']#['1','1','1']",
      "{'type':'array','items':{'type':'string'},'uniqueItems':true}#['1','2','3']#['1','2','3']",
      "{'type':'array','items':{'type':'string'},'minItems':2}#['1','1']#['1','1']",
      "{'type':'array','items':{'type':'string'},'minItems':2}#['1','2','3']#['1','2','3']",
      "{'type':'array','items':{'type':'string'},'maxItems':2}#['1','1']#['1','1']",
      "{'type':'array','items':{'type':'string'},'maxItems':2}#['1']#['1']",
      "{'type':'array','items':{'type':'string'},'maxItems':2}#[]#[]",
      "{'type':'array','items':{'type':'string'},'minItems':1,'maxItems':2}#['1','1']#['1','1']",
      "{'type':'array','items':{'type':'string'},'minItems':2,'maxItems':2}#['1','1']#['1','1']",
      "{'type':'array','items':{'type':'string'},'minItems':1,'maxItems':2}#['1']#['1']",
      "{'type':'array','items':{'type':'string'},'minItems':1,'maxItems':2,'uniqueItems':true}#['1','2']#['1','2']",
      "{'type':'object'}#{'a':true,'b':{'c':1}}#{'a':true,'b':{'c':1}}",
      "{'type':'object','required':['id']}#{'a':true,'b':{'c':1},'id':{'r':0}}#{'a':true,'b':{'c':1},'id':{'r':0}}",
      "{'type':'object','additionalProperties':{'type':'integer'}}#{'a':1,'b':2,'c':3}#{'a':1,'b':2,'c':3}",
      "{'type':'string','maxLength':1}#'1'#'1'", "{'type':'string','minLength':1}#'1'#'1'",
      "{'type':'string','minLength':1,'maxLength':10}#'1'#'1'",
      "{'type':'string','minLength':1,'maxLength':10}#'1234567890'#'1234567890'",
      "{'type':'string','minLength':1,'maxLength':10}#'12345'#'12345'",
      "{'type':'string','minLength':1,'maxLength':10,'pattern':'\\\\d*'}#'12345'#'12345'",
      "{'oneOf':[{'type':'string'},{'type':'integer'}]}#1#1",
      "{'oneOf':[{'type':'string'},{'type':'integer'}]}#'1'#'1'",
      "{'anyOf':[{'type':'string'},{'type':'integer'}]}#2#2",
      "{'anyOf':[{'type':'string'},{'type':'integer'}]}#'2'#'2'",
      "{'anyOf':[{'type':'number'},{'type':'integer'}]}#2#2",
      "{'oneOf':[{'type':'object','properties':{'name':{'type':'string'}}},{'type':'object','properties':{'id':{'type':'integer'}}}]}#{'name':'Jane Doe'}#{'name':'Jane Doe'}",
      "{'oneOf':[{'type':'object','properties':{'name':{'type':'string'}}},{'type':'object','properties':{'id':{'type':'integer'}}}]}#{'id':1}#{'id':1}",
      "{'anyOf':[{'type':'object','properties':{'name':{'type':'string'}}},{'type':'object','properties':{'id':{'type':'integer'}}}]}#{'name':'Jane Doe'}#{'name':'Jane Doe'}",
      "{'anyOf':[{'type':'object','properties':{'name':{'type':'string'}}},{'type':'object','properties':{'id':{'type':'integer'}}}]}#{'id':1}#{'id':1}",
      "{'anyOf':[{'type':'object','properties':{'name':{'type':'string'}}},{'type':'object','properties':{'id':{'type':'integer'}}}]}#{'id':1,'name':'Jane Doe'}#{'id':1,'name':'Jane Doe'}",
      "{'allOf':[{'type':'object','properties':{'name':{'type':'string'}}},{'type':'object','properties':{'id':{'type':'integer'}}}]}#{'id':1,'name':'Jane Doe'}#{'id':1,'name':'Jane Doe'}",
      "{'anyOf':[{'type':'object','properties':{'name':{'type':'string','default':'\\'Jane Doe\\''}}},{'type':'object','properties':{'id':{'type':'integer','default':'1'}}}]}#{}#{'id':1,'name':'Jane Doe'}",
      "{'allOf':[{'type':'object','properties':{'name':{'type':'string','default':'\\'Jane Doe\\''}}},{'type':'object','properties':{'id':{'type':'integer','default':'1'}}}]}#{}#{'id':1,'name':'Jane Doe'}" })
  public void shouldIsValidOpenAPIValue(final String data, final VertxTestContext testContext) {

    final var split = data.replaceAll("'", "\"").split("#");
    final var specification = this.decodeJsonObject(split[0]);
    final var value = this.decodeValue(split[1]);
    final var expectedValue = this.decodeValue(split[2]);

    Validations.validateOpenAPIValue("codePrefix", specification, value)
        .onComplete(testContext.succeeding(validValue -> testContext.verify(() -> {

          if (expectedValue instanceof Number && validValue instanceof Number) {

            assertThat(((Number) validValue).doubleValue()).isCloseTo(((Number) expectedValue).doubleValue(),
                Offset.offset(0.00001));

          } else {

            assertThat(validValue).isEqualTo(expectedValue);
          }
          testContext.completeNow();

        })));

  }

}
