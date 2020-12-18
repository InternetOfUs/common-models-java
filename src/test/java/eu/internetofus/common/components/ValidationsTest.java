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

package eu.internetofus.common.components;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.atIndex;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

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
   * A string with 256 characters.
   */
  public static final String STRING_256 = "0Ncu2eQI7boSct2Ga6VHViEPJn0HqffPajWKyL3TmgUyLG4ZjVLbaZSx7DZXuY0EAWGqWnWOB35Uql92cV2zTbBrSi4gVR0y9jJ3a5zsHnnXNFucmHRyplXw0v98l7BD4d8jvKro7QBIuZM4A4fARUol9gSrRAIoZ7PpUxtbNfteFkVhxfRUhGAkHKfRUsMulmgui5bRQaCM8ivevTJm8N4jXXUlgfkPepeMsQeQPzktJRnZDR3PxDrKLtKjoE24";

  /**
   * A string with 256 characters.
   */
  public static final String STRING_1024 = "qsXSHCN3y8WNG9fmnNfOaEo5JvvM609Kjzm6vZDU9KHK9hl0lYRgwS58qGd9nZz8RAVlpVqVVQZBjXOzkTgI9rGYNuAipZGFELNg9XevKjl2b1bD9Q1TJSFJJr35lWlGiaAUg6xfUVxbo1YgqKXCBdHiifmYWrrd7NmhnlPfPeRL3xJGDZGVYoT2J8kpZmzXoU9aw2ga3m4NucuZqcuaoqFSPGWNlFgdnjhHV7RryFxSwRRhfUMPj5xLyyhXDQA44mmLPGVmvtM4a32oQ6ZmANNLXECQKk12FqNKrXtk1GrWi6yFLT3zonuftSSb1K8dxhgxLIBuHdnNSJ2Sh6co6fp9QAqhZbVIBfVhT373HlHKZ0qrkEFmsgw2TgjY5OUX4ZzckFqbaTjipJpI5dxUUrEOQSKX0rmXQ9v2o6dfbYAXK7MuhLTBdqL0H20wFJcofb9XQOihMaTKIMsfRsNeiIK4ZGWxSni9SN6Y3z243zf2Cmp3gwDEYe8mH4VwVIDVxmrNZfDirLOYxHKMKHzZBcQ20ZBCODr6Wx2n7eMxhFkZa416aOssc0NRKseEnNE1SKBzjLY2GMJTnTNwRY0qQwrZJ2RenBVsQkZZoBH42qO3jafaDd0m0jhGOXfcJfkk690iTWy2AKRGEELeZiIe5fy629zFV9UGoPQ6PK7LClQX37KOxeENPxKcvlRabK49jcvV6bgjWxeYh5viFmakPbM3vddpr753FGjedyRCEszGkTLw90uKoz7yyZekEwbUV7pQcOju7nDLf8wa47BAP809YqRaEL8eFFGFpl9x72rsppZlok0Z2aSLfXyoTpYEqvsbZXvD4WjrMMEMkGySZOL7Xuwxuy1J4cEQQ5u6eAXlbr2qSZAlLXs8j5AAJJdz0Lh3r2NpLpSBHkEy35AYgJTkD9CP4YaNH93hqttJ9ajShPYYVTaObyYwWKRbLYpXCDicEmGr9Yk5HT60cU6vdAtWg38xnmFF8ltejRvq81BYyweRt8XvWwO3ZSoWqbbV";

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
  public static void assertIsNotValid(final Validable model, final String fieldName, final Vertx vertx, final VertxTestContext testContext) {

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
  public static <T extends Validable> void assertIsValid(final T model, final Vertx vertx, final VertxTestContext testContext) {

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
  public static <T extends Validable> void assertIsValid(final T model, final Vertx vertx, final VertxTestContext testContext, final Runnable expected) {

    model.validate("codePrefix", vertx).onComplete(testContext.succeeding(empty -> testContext.verify(() -> {

      if (expected != null) {

        expected.run();
      }

      testContext.completeNow();

    })));

  }

  /**
   * Check that a field can be null.
   *
   * @see Validations#validateNullableStringField(String, String, int,String)
   */
  @Test
  public void shouldNullStringFieldBeValid() {

    assertThatCode(() -> assertThat(Validations.validateNullableStringField("codePrefix", "fieldName", 255, null)).isEqualTo(null)).doesNotThrowAnyException();
  }

  /**
   * Check that an empty is right but is changed to null.
   *
   * @see Validations#validateNullableStringField(String, String, int,String)
   */
  @Test
  public void shouldEmptyStringFieldBeValid() {

    assertThatCode(() -> assertThat(Validations.validateNullableStringField("codePrefix", "fieldName", 255, "")).isEqualTo(null)).doesNotThrowAnyException();
  }

  /**
   * Check that an white value is right but is changed to null.
   *
   * @see Validations#validateNullableStringField(String, String, int,String)
   */
  @Test
  public void shouldWhiteStringFieldBeValid() {

    assertThatCode(() -> assertThat(Validations.validateNullableStringField("codePrefix", "fieldName", 255, "       ")).isEqualTo(null)).doesNotThrowAnyException();
  }

  /**
   * Check that the value is trimmed to be valid.
   *
   * @see Validations#validateNullableStringField(String, String, int,String)
   */
  @Test
  public void shouldStringWithWhiteFieldBeValid() {

    assertThatCode(() -> assertThat(Validations.validateNullableStringField("codePrefix", "fieldName", 255, "   a b c    ")).isEqualTo("a b c")).doesNotThrowAnyException();
  }

  /**
   * Check that the value of the field is not valid if it is too large.
   *
   * @see Validations#validateNullableStringField(String, String, int,String)
   */
  @Test
  public void shouldNotBeValidIfStringIsTooLarge() {

    assertThat(assertThrows(ValidationErrorException.class, () -> Validations.validateNullableStringField("codePrefix", "fieldName", 255, ValidationsTest.STRING_256)).getCode()).isEqualTo("codePrefix.fieldName");
  }

  /**
   * Check that a field can be null.
   *
   * @see Validations#validateNullableEmailField(String, String, String)
   */
  @Test
  public void shouldNullEmailFieldBeValid() {

    assertThatCode(() -> assertThat(Validations.validateNullableEmailField("codePrefix", "fieldName", null)).isEqualTo(null)).doesNotThrowAnyException();
  }

  /**
   * Check that an empty is right but is changed to null.
   *
   * @see Validations#validateNullableEmailField(String, String, String)
   */
  @Test
  public void shouldEmptyEmailFieldBeValid() {

    assertThatCode(() -> assertThat(Validations.validateNullableEmailField("codePrefix", "fieldName", "")).isEqualTo(null)).doesNotThrowAnyException();
  }

  /**
   * Check that an white value is right but is changed to null.
   *
   * @see Validations#validateNullableEmailField(String, String, String)
   */
  @Test
  public void shouldWhiteEmailFieldBeValid() {

    assertThatCode(() -> assertThat(Validations.validateNullableEmailField("codePrefix", "fieldName", "       ")).isEqualTo(null)).doesNotThrowAnyException();
  }

  /**
   * Check that the email value is trimmed to be valid.
   *
   * @see Validations#validateNullableEmailField(String, String, String)
   */
  @Test
  public void shouldEmailWithWhiteFieldBeValid() {

    assertThatCode(() -> assertThat(Validations.validateNullableEmailField("codePrefix", "fieldName", "   a@b.com    ")).isEqualTo("a@b.com")).doesNotThrowAnyException();
  }

  /**
   * Check that the email value of the field is not valid if it is too large.
   *
   * @see Validations#validateNullableEmailField(String, String, String)
   */
  @Test
  public void shouldNotBeValidIfEmailIsTooLarge() {

    assertThat(assertThrows(ValidationErrorException.class, () -> Validations.validateNullableEmailField("codePrefix", "fieldName", ValidationsTest.STRING_256.substring(0, 250) + "@b.com")).getCode()).isEqualTo("codePrefix.fieldName");
  }

  /**
   * Check that the email value of the field is not valid if it is too large.
   *
   * @see Validations#validateNullableEmailField(String, String, String)
   */
  @Test
  public void shouldNotBeValidABadEmailValue() {

    assertThat(assertThrows(ValidationErrorException.class, () -> Validations.validateNullableEmailField("codePrefix", "fieldName", "bad email(at)host.com")).getCode()).isEqualTo("codePrefix.fieldName");
  }

  /**
   * Check that a field can be null.
   *
   * @see Validations#validateNullableLocaleField(String, String, String)
   */
  @Test
  public void shouldNullLocaleFieldBeValid() {

    assertThatCode(() -> assertThat(Validations.validateNullableLocaleField("codePrefix", "fieldName", null)).isEqualTo(null)).doesNotThrowAnyException();
  }

  /**
   * Check that an empty is right but is changed to null.
   *
   * @see Validations#validateNullableLocaleField(String, String, String)
   */
  @Test
  public void shouldEmptyLocaleFieldBeValid() {

    assertThatCode(() -> assertThat(Validations.validateNullableLocaleField("codePrefix", "fieldName", "")).isEqualTo(null)).doesNotThrowAnyException();
  }

  /**
   * Check that an white value is right but is changed to null.
   *
   * @see Validations#validateNullableLocaleField(String, String, String)
   */
  @Test
  public void shouldWhiteLocaleFieldBeValid() {

    assertThatCode(() -> assertThat(Validations.validateNullableLocaleField("codePrefix", "fieldName", "       ")).isEqualTo(null)).doesNotThrowAnyException();
  }

  /**
   * Check that the locale value is trimmed to be valid.
   *
   * @see Validations#validateNullableLocaleField(String, String, String)
   */
  @Test
  public void shouldLocaleWithWhiteFieldBeValid() {

    assertThatCode(() -> assertThat(Validations.validateNullableLocaleField("codePrefix", "fieldName", "   en_US    ")).isEqualTo("en_US")).doesNotThrowAnyException();
  }

  /**
   * Check that the locale value of the field is not valid if it is too large.
   *
   * @see Validations#validateNullableLocaleField(String, String, String)
   */
  @Test
  public void shouldNotBeValidIfLocaleIsTooLarge() {

    assertThat(assertThrows(ValidationErrorException.class, () -> Validations.validateNullableLocaleField("codePrefix", "fieldName", "to_la_ge_")).getCode()).isEqualTo("codePrefix.fieldName");
  }

  /**
   * Check that the locale value of the field is not valid if it is too large.
   *
   * @see Validations#validateNullableLocaleField(String, String, String)
   */
  @Test
  public void shouldNotBeValidABadLocaleValue() {

    assertThat(assertThrows(ValidationErrorException.class, () -> Validations.validateNullableLocaleField("codePrefix", "fieldName", "de-Gr")).getCode()).isEqualTo("codePrefix.fieldName");
  }

  /**
   * Check that a field can be null.
   *
   * @see Validations#validateNullableTelephoneField(String, String, String,String)
   */
  @Test
  public void shouldNullTelephoneFieldBeValid() {

    assertThatCode(() -> assertThat(Validations.validateNullableTelephoneField("codePrefix", "fieldName", null, null)).isEqualTo(null)).doesNotThrowAnyException();
  }

  /**
   * Check that an empty is right but is changed to null.
   *
   * @see Validations#validateNullableTelephoneField(String, String, String,String)
   */
  @Test
  public void shouldEmptyTelephoneFieldBeValid() {

    assertThatCode(() -> assertThat(Validations.validateNullableTelephoneField("codePrefix", "fieldName", null, "")).isEqualTo(null)).doesNotThrowAnyException();
  }

  /**
   * Check that an white value is right but is changed to null.
   *
   * @see Validations#validateNullableTelephoneField(String, String, String,String)
   */
  @Test
  public void shouldWhiteTelephoneFieldBeValid() {

    assertThatCode(() -> assertThat(Validations.validateNullableTelephoneField("codePrefix", "fieldName", null, "       ")).isEqualTo(null)).doesNotThrowAnyException();
  }

  /**
   * Check that the telephone value is trimmed to be valid.
   *
   * @see Validations#validateNullableTelephoneField(String, String, String,String)
   */
  @Test
  public void shouldTelephoneWithWhiteFieldBeValid() {

    assertThatCode(() -> assertThat(Validations.validateNullableTelephoneField("codePrefix", "fieldName", null, "   +34987654321    ")).isEqualTo("+34 987 65 43 21")).doesNotThrowAnyException();
  }

  /**
   * Check that the telephone value of the field is not valid if it is too large.
   *
   * @see Validations#validateNullableTelephoneField(String, String, String,String)
   */
  @Test
  public void shouldNotBeValidIfTelephoneIsTooLarge() {

    assertThat(assertThrows(ValidationErrorException.class, () -> Validations.validateNullableTelephoneField("codePrefix", "fieldName", null, "+349876543211")).getCode()).isEqualTo("codePrefix.fieldName");
  }

  /**
   * Check that the telephone value of the field is not valid if it is too large.
   *
   * @see Validations#validateNullableTelephoneField(String, String, String,String)
   */
  @Test
  public void shouldNotBeValidABadTelephoneValue() {

    assertThat(assertThrows(ValidationErrorException.class, () -> Validations.validateNullableTelephoneField("codePrefix", "fieldName", null, "bad telephone number")).getCode()).isEqualTo("codePrefix.fieldName");
  }

  /**
   * Check that an empty is right but is changed to null.
   *
   * @see Validations#validateNullableStringDateField(String, String, DateTimeFormatter,String)
   */
  @Test
  public void shouldEmptyDateFieldBeValid() {

    assertThatCode(() -> assertThat(Validations.validateNullableStringDateField("codePrefix", "fieldName", DateTimeFormatter.ISO_INSTANT, "")).isEqualTo(null)).doesNotThrowAnyException();
  }

  /**
   * Check that an white value is right but is changed to null.
   *
   * @see Validations#validateNullableStringDateField(String, String, DateTimeFormatter,String)
   */
  @Test
  public void shouldWhiteDateFieldBeValid() {

    assertThatCode(() -> assertThat(Validations.validateNullableStringDateField("codePrefix", "fieldName", DateTimeFormatter.ISO_INSTANT, "       ")).isEqualTo(null)).doesNotThrowAnyException();
  }

  /**
   * Check that the date value is trimmed to be valid.
   *
   * @see Validations#validateNullableStringDateField(String, String, DateTimeFormatter,String)
   */
  @Test
  public void shouldDateWithWhiteFieldBeValid() {

    assertThatCode(() -> assertThat(Validations.validateNullableStringDateField("codePrefix", "fieldName", DateTimeFormatter.ISO_INSTANT, "   2011-12-03t10:15:30z    ")).isEqualTo("2011-12-03T10:15:30Z")).doesNotThrowAnyException();
  }

  /**
   * Check that the date value of the field is not valid if it is too large.
   *
   * @see Validations#validateNullableStringDateField(String, String, DateTimeFormatter,String)
   */
  @Test
  public void shouldNotBeValidABadDateValue() {

    assertThat(assertThrows(ValidationErrorException.class, () -> Validations.validateNullableStringDateField("codePrefix", "fieldName", null, "bad date")).getCode()).isEqualTo("codePrefix.fieldName");
  }

  /**
   * Check that the date value of the field is not valid if it is too large.
   *
   * @see Validations#validateNullableStringDateField(String, String, DateTimeFormatter,String)
   */
  @Test
  public void shouldNotBeValidABadIsoInstanceValue() {

    assertThat(assertThrows(ValidationErrorException.class, () -> Validations.validateNullableStringDateField("codePrefix", "fieldName", DateTimeFormatter.ISO_INSTANT, "bad date")).getCode()).isEqualTo("codePrefix.fieldName");
  }

  /**
   * Check that a field can not be a null value.
   *
   * @see Validations#validateStringField(String, String, int, String, String...)
   */
  @Test
  public void shouldNullStringFieldNotBeValid() {

    assertThat(assertThrows(ValidationErrorException.class, () -> Validations.validateStringField("codePrefix", "fieldName", 255, null)).getCode()).isEqualTo("codePrefix.fieldName");

  }

  /**
   * Check that a field can not be an empty value.
   *
   * @see Validations#validateStringField(String, String, int, String, String...)
   */
  @Test
  public void shouldEmptyStringFieldNotBeValid() {

    assertThat(assertThrows(ValidationErrorException.class, () -> Validations.validateStringField("codePrefix", "fieldName", 255, "")).getCode()).isEqualTo("codePrefix.fieldName");

  }

  /**
   * Check that a field can not be a white value.
   *
   * @see Validations#validateStringField(String, String, int, String, String...)
   */
  @Test
  public void shouldWhiteStringFieldNotBeValid() {

    assertThat(assertThrows(ValidationErrorException.class, () -> Validations.validateStringField("codePrefix", "fieldName", 255, "       ")).getCode()).isEqualTo("codePrefix.fieldName");

  }

  /**
   * Check that a field can not be a white value.
   *
   * @see Validations#validateStringField(String, String, int, String, String...)
   */
  @Test
  public void shouldStringFieldNotBeValidBecauseIsTooLong() {

    assertThat(assertThrows(ValidationErrorException.class, () -> Validations.validateStringField("codePrefix", "fieldName", 5, "123456")).getCode()).isEqualTo("codePrefix.fieldName");

  }

  /**
   * Check that a field can not be a white value.
   *
   * @see Validations#validateStringField(String, String, int, String, String...)
   */
  @Test
  public void shouldStringFieldNotBeValidBecauseValueNotAllowed() {

    assertThat(assertThrows(ValidationErrorException.class, () -> Validations.validateStringField("codePrefix", "fieldName", 255, "   value    ", "1", "2", "3")).getCode()).isEqualTo("codePrefix.fieldName");

  }

  /**
   * Check that a non empty string is valid.
   *
   * @see Validations#validateStringField(String, String, int, String, String...)
   */
  @Test
  public void shouldStringBeValid() {

    assertThatCode(() -> assertThat(Validations.validateStringField("codePrefix", "fieldName", 255, "   value    ")).isEqualTo("value")).doesNotThrowAnyException();
  }

  /**
   * Check that a non empty string is valid because is defined in the possible values.
   *
   * @see Validations#validateStringField(String, String, int, String, String...)
   */
  @Test
  public void shouldStringBeValidBecauseIsAPosisbleValue() {

    assertThatCode(() -> assertThat(Validations.validateStringField("codePrefix", "fieldName", 255, "   value    ", "val", "lue", "value")).isEqualTo("value")).doesNotThrowAnyException();
  }

  /**
   * Check that a non empty string is valid.
   *
   * @see Validations#validateStringField(String, String, int, String, String...)
   */
  @Test
  public void shouldStringBeValidBecauseIsNotEmpty() {

    assertThatCode(() -> assertThat(Validations.validateStringField("codePrefix", "fieldName", 255, "   value   ", (String[]) null)).isEqualTo("value")).doesNotThrowAnyException();
  }

  /**
   * Check that a {@code null} time stamp is valid.
   *
   * @see Validations#validateTimeStamp(String, String, Long, boolean)
   */
  @Test
  public void shouldNullTimeStampBeValid() {

    assertThatCode(() -> assertThat(Validations.validateTimeStamp("codePrefix", "fieldName", null, true)).isNull()).doesNotThrowAnyException();
  }

  /**
   * Check that a {@code null} time stamp is not valid.
   *
   * @see Validations#validateTimeStamp(String, String, Long, boolean)
   */
  @Test
  public void shouldNullTimeStampNotBeValid() {

    assertThat(assertThrows(ValidationErrorException.class, () -> Validations.validateTimeStamp("codePrefix", "fieldName", null, false)).getCode()).isEqualTo("codePrefix.fieldName");

  }

  /**
   * Check that a negative time stamp is not valid.
   *
   * @see Validations#validateTimeStamp(String, String, Long, boolean)
   */
  @Test
  public void shouldNegativeTimeStampNotBeValid() {

    assertThat(assertThrows(ValidationErrorException.class, () -> Validations.validateTimeStamp("codePrefix", "fieldName", -1l, false)).getCode()).isEqualTo("codePrefix.fieldName");

  }

  /**
   * Check that is valid a {@code null} as a nullable string list.
   *
   * @see Validations#validateNullableListStringField(String, String, int, java.util.List)
   */
  @Test
  public void shouldSuccessValidateNullableListStringFieldWithNull() {

    assertThatCode(() -> assertThat(Validations.validateNullableListStringField("codePrefix", "fieldName", 255, null)).isNull()).doesNotThrowAnyException();
  }

  /**
   * Check that is valid a empty list as a nullable string list.
   *
   * @see Validations#validateNullableListStringField(String, String, int, java.util.List)
   */
  @Test
  public void shouldSuccessValidateNullableListStringFieldWithEmptyList() {

    assertThatCode(() -> assertThat(Validations.validateNullableListStringField("codePrefix", "fieldName", 255, new ArrayList<>())).isEmpty()).doesNotThrowAnyException();
  }

  /**
   * Check that is valid a list with some strings.
   *
   * @see Validations#validateNullableListStringField(String, String, int, java.util.List)
   */
  @Test
  public void shouldSuccessValidateNullableListStringFieldWithSomeValues() {

    final List<String> values = new ArrayList<>();
    values.add(null);
    values.add("                 ");
    values.add("  123         ");
    values.add("  12345         ");
    values.add(null);
    values.add("  1 3         ");
    values.add("                 ");

    assertThatCode(() -> assertThat(Validations.validateNullableListStringField("codePrefix", "fieldName", 5, values)).hasSize(3).contains("123", atIndex(0)).contains("12345", atIndex(1)).contains("1 3", atIndex(2)))
    .doesNotThrowAnyException();
  }

  /**
   * Check that is not valid a list with large strings.
   *
   * @see Validations#validateNullableListStringField(String, String, int, java.util.List)
   */
  @Test
  public void shouldFailValidateNullableListStringFieldWithLargeStrings() {

    final List<String> values = new ArrayList<>();
    values.add(null);
    values.add("                 ");
    values.add("  123         ");
    values.add("  1234567         ");
    values.add(null);
    values.add("                 ");
    assertThat(assertThrows(ValidationErrorException.class, () -> Validations.validateNullableListStringField("codePrefix", "fieldName", 5, values)).getCode()).isEqualTo("codePrefix.fieldName[3]");
  }

  /**
   * A null list of models has to be valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Validations#validate(List, java.util.function.BiPredicate, String, Vertx)
   */
  @Test
  public void shouldNullListOfModelsBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final Promise<Void> promise = Promise.promise();
    final var future = promise.future();
    future.compose(Validations.validate(null, (a, b) -> a.equals(b), "codePrefix", vertx)).onComplete(testContext.succeeding(empty -> testContext.completeNow()));
    promise.complete();
  }

  /**
   * An empty list of models has to be valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Validations#validate(List, java.util.function.BiPredicate, String, Vertx)
   */
  @Test
  public void shouldEmptyListOfModelsBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final Promise<Void> promise = Promise.promise();
    final var future = promise.future();
    future.compose(Validations.validate(new ArrayList<>(), (a, b) -> a.equals(b), "codePrefix", vertx)).onComplete(testContext.succeeding(empty -> testContext.completeNow()));
    promise.complete();
  }

  /**
   * An list of {@code null} models has to be valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Validations#validate(List, java.util.function.BiPredicate, String, Vertx)
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
    future.compose(Validations.validate(models, (a, b) -> a.equals(b), "codePrefix", vertx)).onComplete(testContext.succeeding(empty -> testContext.verify(() -> {
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
   * @see Validations#validate(List, java.util.function.BiPredicate, String, Vertx)
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
    future.compose(Validations.validate(models, (a, b) -> a.equals(b), "codePrefix", vertx)).onComplete(testContext.succeeding(empty -> testContext.verify(() -> {
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
   * @see Validations#validate(List, java.util.function.BiPredicate, String, Vertx)
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
    future.compose(Validations.validate(models, (a, b) -> a.equals(b), "codePrefix", vertx)).onComplete(testContext.failing(error -> testContext.verify(() -> {
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
   * @see Validations#validate(List, java.util.function.BiPredicate, String, Vertx)
   */
  @Test
  public void shouldListWithValidModelsBeNotValidBecauseExistoneDuplicatedOne(final Vertx vertx, final VertxTestContext testContext) {

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
    future.compose(Validations.validate(models, (a, b) -> a.equals(b), "codePrefix", vertx)).onComplete(testContext.failing(error -> testContext.verify(() -> {
      assertThat(error).isInstanceOf(ValidationErrorException.class);
      assertThat(((ValidationErrorException) error).getCode()).isEqualTo("codePrefix[2]");
      assertThat(models).hasSize(3).contains(model1, atIndex(0)).contains(model3, atIndex(1)).contains(model4, atIndex(2));
      testContext.completeNow();
    })));
    promise.complete();
  }

  /**
   * Check the a double value is not valid.
   *
   * @param val value that is not valid.
   * @param min minimum range value.
   * @param max maximum range value.
   *
   * @see Validations#validateNumberOnRange(String, String, Number, boolean, Number, Number)
   */
  @ParameterizedTest(name = "The value {0} should not be valid")
  @CsvSource({ "1,0,0.1", "1,2,3", "-1,0,", "10,,1" })
  public void shouldNumberNotBeValid(final String val, final String min, final String max) {

    final Double value = Double.parseDouble(val);
    final var nullable = false;
    final var minValue = this.extractDouble(min);
    final var maxValue = this.extractDouble(max);
    assertThat(assertThrows(ValidationErrorException.class, () -> Validations.validateNumberOnRange("codePrefix", "fieldName", value, nullable, minValue, maxValue)).getCode()).isEqualTo("codePrefix.fieldName");

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
   * @see Validations#validateNumberOnRange(String, String, Number, boolean, Number, Number)
   */
  @Test
  public void shouldNullNumberNotBeValidWhenIsNotNullable() {

    assertThat(assertThrows(ValidationErrorException.class, () -> Validations.validateNumberOnRange("codePrefix", "fieldName", null, false, 0d, 1d)).getCode()).isEqualTo("codePrefix.fieldName");

  }

  /**
   * Check the a double value is valid.
   *
   * @param val value that is not valid.
   * @param min minimum range value.
   * @param max maximum range value.
   *
   * @see Validations#validateNumberOnRange(String, String, Number, boolean, Number, Number)
   */
  @ParameterizedTest(name = "The value {0} should not be valid")
  @CsvSource({ "1,0,1", "1,1,3", "0.5,0,1", "-1,,1", "1,0," })
  public void shouldNumberBeValid(final String val, final String min, final String max) {

    final Double value = Double.parseDouble(val);
    final var nullable = false;
    final var minValue = this.extractDouble(min);
    final var maxValue = this.extractDouble(max);
    assertThatCode(() -> assertThat(Validations.validateNumberOnRange("codePrefix", "fieldName", value, nullable, minValue, maxValue)).isEqualTo(value)).doesNotThrowAnyException();

  }

}
