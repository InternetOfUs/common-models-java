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

package eu.internetofus.common.components;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
   * Assert that a model is not valid.
   *
   * @param model       to validate.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  public static void assertIsNotValid(final Validable model, final Vertx vertx, final VertxTestContext testContext) {

    assertIsNotValid(model, null, vertx, testContext);

  }

  /**
   * Assert that a model is not valid because a field is wrong.
   *
   * @param model       to validate.
   * @param fieldName   name of the field that is not valid.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   */
  public static void assertIsNotValid(final Validable model, final String fieldName, final Vertx vertx,
      final VertxTestContext testContext) {

    model.validate("codePrefix", vertx).onComplete(testContext.failing(error -> testContext.verify(() -> {

      assertThat(error).isInstanceOf(ValidationErrorException.class);
      final var code = ((ValidationErrorException) error).getCode();
      if (fieldName != null) {

        assertThat(code).isEqualTo("codePrefix." + fieldName);

      } else {

        assertThat(code).startsWith("codePrefix");
      }

      testContext.completeNow();

    })));
  }

  /**
   * Assert that a model is valid.
   *
   * @param model       to validate.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   * @param <T>         model to test.
   */
  public static <T extends Validable> void assertIsValid(final T model, final Vertx vertx,
      final VertxTestContext testContext) {

    assertIsValid(model, vertx, testContext, null);

  }

  /**
   * Assert that a model is valid.
   *
   * @param model       to validate.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   * @param expected    function to check the validation result.
   * @param <T>         model to test.
   */
  public static <T extends Validable> void assertIsValid(final T model, final Vertx vertx,
      final VertxTestContext testContext, final Runnable expected) {

    model.validate("codePrefix", vertx).onComplete(testContext.succeeding(empty -> testContext.verify(() -> {

      if (expected != null) {

        expected.run();
      }

      testContext.completeNow();

    })));

  }

  /**
   * Check that a field value is valid and converted tyo null.
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
  public void shouldNotBeValidLocale(final VertxTestContext testContext) {

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
        .onComplete(testContext.succeeding(result -> testContext.verify(() -> {

          assertThat(result).isNull();
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

}
