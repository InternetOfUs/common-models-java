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
import static org.mockito.Mockito.doReturn;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test the {@link ValidateContext}.
 *
 * @see ValidateContext
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
@ExtendWith(MockitoExtension.class)
public class ValidateContextTest {

  /**
   * The mocked context to test.
   */
  @Spy
  protected ValidateContext<?> context;

  /**
   * Should create a fail future with a message.
   *
   * @param testContext test context to use.
   *
   * @see ValidateContext#fail(String)
   */
  @Test
  public void shouldFailWithMessage(final VertxTestContext testContext) {

    doReturn("codePrefix").when(this.context).errorCode();
    final var future = this.context.fail("message");
    testContext.assertFailure(future).onFailure(error -> {

      testContext.verify(() -> {

        assertThat(error).isInstanceOf(ValidationErrorException.class);
        final var cause = (ValidationErrorException) error;
        assertThat(cause.getCode()).isEqualTo("codePrefix");
        assertThat(cause.getMessage()).isEqualTo("message");

      });
      testContext.completeNow();
    });

  }

  /**
   * Should create a fail future with a message and a code.
   *
   * @param testContext test context to use.
   *
   * @see ValidateContext#fail(String,String)
   */
  @Test
  public void shouldFailWithCodeAndMessage(final VertxTestContext testContext) {

    final var future = this.context.fail("codePrefix2", "message2");
    testContext.assertFailure(future).onFailure(error -> {

      testContext.verify(() -> {

        assertThat(error).isInstanceOf(ValidationErrorException.class);
        final var cause = (ValidationErrorException) error;
        assertThat(cause.getCode()).isEqualTo("codePrefix2");
        assertThat(cause.getMessage()).isEqualTo("message2");

      });
      testContext.completeNow();
    });

  }

  /**
   * Should create a fail future with a cause.
   *
   * @param testContext test context to use.
   *
   * @see ValidateContext#fail(Throwable)
   */
  @Test
  public void shouldFailWithCause(final VertxTestContext testContext) {

    doReturn("codeCause").when(this.context).errorCode();
    final var cause = new Throwable("cause");
    final var future = this.context.fail(cause);
    testContext.assertFailure(future).onFailure(error -> {

      testContext.verify(() -> {

        assertThat(error).isInstanceOf(ValidationErrorException.class);
        final var failedCause = (ValidationErrorException) error;
        assertThat(failedCause.getCode()).isEqualTo("codeCause");
        assertThat(failedCause.getMessage()).contains(cause.getMessage());
        assertThat(failedCause.getCause()).isEqualTo(cause);

      });
      testContext.completeNow();
    });

  }

  /**
   * Should create a fail field future with a cause.
   *
   * @param testContext test context to use.
   *
   * @see ValidateContext#failField(String,Throwable)
   */
  @Test
  public void shouldFailFieldWithCause(final VertxTestContext testContext) {

    doReturn("codeCause").when(this.context).errorCode();
    final var cause = new Throwable("cause");
    final var future = this.context.failField("field", cause);
    testContext.assertFailure(future).onFailure(error -> {

      testContext.verify(() -> {

        assertThat(error).isInstanceOf(ValidationErrorException.class);
        final var failedCause = (ValidationErrorException) error;
        assertThat(failedCause.getCode()).isEqualTo("codeCause.field");
        assertThat(failedCause.getMessage()).contains(cause.getMessage());
        assertThat(failedCause.getCause()).isEqualTo(cause);

      });
      testContext.completeNow();
    });

  }

  /**
   * Should create a fail field future with a cause.
   *
   * @param testContext test context to use.
   *
   * @see ValidateContext#failFieldElement(String, int, Throwable)
   */
  @Test
  public void shouldFailFieldElementWithCause(final VertxTestContext testContext) {

    doReturn("codeCause").when(this.context).errorCode();
    final var cause = new Throwable("cause");
    final var future = this.context.failFieldElement("field", 3, cause);
    testContext.assertFailure(future).onFailure(error -> {

      testContext.verify(() -> {

        assertThat(error).isInstanceOf(ValidationErrorException.class);
        final var failedCause = (ValidationErrorException) error;
        assertThat(failedCause.getCode()).isEqualTo("codeCause.field[3]");
        assertThat(failedCause.getMessage()).contains(cause.getMessage());
        assertThat(failedCause.getCause()).isEqualTo(cause);

      });
      testContext.completeNow();
    });

  }

  /**
   * Should create a fail future for a field.
   *
   * @param testContext test context to use.
   *
   * @see ValidateContext#failField(String, String)
   */
  @Test
  public void shouldFailFieldWithMessage(final VertxTestContext testContext) {

    doReturn("codePrefix3").when(this.context).errorCode();
    final var future = this.context.failField("field", "message3");
    testContext.assertFailure(future).onFailure(error -> {

      testContext.verify(() -> {

        assertThat(error).isInstanceOf(ValidationErrorException.class);
        final var cause = (ValidationErrorException) error;
        assertThat(cause.getCode()).isEqualTo("codePrefix3.field");
        assertThat(cause.getMessage()).isEqualTo("message3");

      });
      testContext.completeNow();
    });

  }

  /**
   * Should create a fail future for a field.
   *
   * @param testContext test context to use.
   *
   * @see ValidateContext#failFieldElement(String,int, String)
   */
  @Test
  public void shouldFailFieldElementWithMessage(final VertxTestContext testContext) {

    doReturn("codePrefix3").when(this.context).errorCode();
    final var future = this.context.failFieldElement("field", 4, "message3");
    testContext.assertFailure(future).onFailure(error -> {

      testContext.verify(() -> {

        assertThat(error).isInstanceOf(ValidationErrorException.class);
        final var cause = (ValidationErrorException) error;
        assertThat(cause.getCode()).isEqualTo("codePrefix3.field[4]");
        assertThat(cause.getMessage()).isEqualTo("message3");

      });
      testContext.completeNow();
    });

  }

  /**
   * Should create a context for a field element.
   *
   * @see ValidateContext#createFieldElementContext(String, int)
   */
  @Test
  public void shouldCreateFieldElementContext() {

    doReturn("codePrefix4").when(this.context).errorCode();
    doReturn(this.context).when(this.context).createContextWithErrorCode("codePrefix4.field2[6]");
    assertThat(this.context.createFieldElementContext("field2", 6)).isSameAs(this.context);

  }

  /**
   * Should create a context for a field .
   *
   * @see ValidateContext#createFieldContext(String)
   */
  @Test
  public void shouldCreateFieldContext() {

    doReturn("codePrefix5").when(this.context).errorCode();
    doReturn(this.context).when(this.context).createContextWithErrorCode("codePrefix5.field3");
    assertThat(this.context.createFieldContext("field3")).isSameAs(this.context);

  }

  /**
   * Should self be the same as this.
   *
   * @see ValidateContext#createFieldContext(String)
   */
  @Test
  public void shouldSelf() {

    assertThat(this.context.self()).isSameAs(this.context);

  }

  /**
   * Should chain.
   *
   * @param testContext test context to use.
   * @param model       to chain.
   *
   * @see ValidateContext#chain()
   */
  @SuppressWarnings("unchecked")
  @Test
  public void shouldChain(final VertxTestContext testContext,
      @SuppressWarnings("rawtypes") @Mock final Validable model) {

    doReturn(Future.succeededFuture()).when(model).validate(this.context);
    testContext.assertComplete(this.context.chain().apply(model)).onSuccess(result -> {

      testContext.verify(() -> {

        assertThat(result).isSameAs(model);

      });
      testContext.completeNow();

    });

  }

  /**
   * Should fail chain.
   *
   * @param testContext test context to use.
   * @param model       to chain.
   *
   * @see ValidateContext#chain()
   */
  @SuppressWarnings("unchecked")
  @Test
  public void shouldFailChain(final VertxTestContext testContext,
      @SuppressWarnings("rawtypes") @Mock final Validable model) {

    final var expectedError = new Exception("Unexpected exception");
    doReturn(Future.failedFuture(expectedError)).when(model).validate(this.context);
    final Future<?> future = Future.succeededFuture(model).compose(this.context.chain());
    testContext.assertFailure(future).onFailure(error -> {

      testContext.verify(() -> {

        assertThat(error).isSameAs(expectedError);

      });
      testContext.completeNow();
    });

  }

  /**
   * Should be valid some string field.
   *
   * @param value to validate.
   *
   * @see ValidateContext#validateStringField
   */
  @ParameterizedTest(name = "Should {0} be valid")
  @ValueSource(strings = { " valid ", "valid", "  va  lid  ", "  valid", "valid  " })
  public void shouldValidateStringField(final String value) {

    assertThat(this.context.validateStringField("field", value, null)).isEqualTo(value.trim());

  }

  /**
   * Should fail validate string field.
   *
   * @param value       to validate.
   * @param testContext test context to use.
   *
   * @see ValidateContext#validateStringField
   */
  @ParameterizedTest(name = "Should {0} be not valid")
  @NullSource
  @ValueSource(strings = { "  ", "", "  \n\t  " })
  public void shouldFailValidateStringField(final String value, final VertxTestContext testContext) {

    doReturn("codePrefix6").when(this.context).errorCode();
    final Promise<Void> promise = Promise.promise();
    assertThat(this.context.validateStringField("strField", value, promise)).isSameAs(value);
    testContext.assertFailure(promise.future()).onFailure(error -> {

      testContext.verify(() -> {

        assertThat(error).isInstanceOf(ValidationErrorException.class);
        final var cause = (ValidationErrorException) error;
        assertThat(cause.getCode()).isEqualTo("codePrefix6.strField");
        if (value == null) {

          assertThat(cause.getMessage()).contains("'null'");

        } else {

          assertThat(cause.getMessage()).contains("empty");
        }

      });
      testContext.completeNow();
    });

  }

  /**
   * Should normalize a string.
   *
   * @param value to normalize.
   *
   * @see ValidateContext#normalizeString(String)
   */
  @ParameterizedTest(name = "Should {0} be normalized")
  @ValueSource(strings = { " valid ", "valid", "  va  lid  ", "  valid", "valid  " })
  public void shouldNormalizeString(final String value) {

    assertThat(this.context.normalizeString(value)).isEqualTo(value.trim());

  }

  /**
   * Should normalize a {@code null} string.
   *
   * @see ValidateContext#normalizeString(String)
   */
  @Test
  public void shouldNormalizeNullString() {

    assertThat(this.context.normalizeString(null)).isNull();

  }

  /**
   * Should validate enum field.
   *
   * @see ValidateContext#validateEnumField(String, Object, Promise, Object...)
   */
  @Test
  public void shouldFailValidateEnumField() {

    final Promise<Void> promise = Promise.promise();
    this.context.validateEnumField("enumField", "F", promise, "M", "NPI", "F", "NB");
    assertThat(promise.tryComplete()).isTrue();

  }

  /**
   * Should fail validate enum field.
   *
   * @param testContext test context to use.
   *
   * @see ValidateContext#validateEnumField(String, Object, Promise, Object...)
   */
  @Test
  public void shouldFailValidateEnumField(final VertxTestContext testContext) {

    doReturn("codePrefixEnum").when(this.context).errorCode();
    final Promise<Void> promise = Promise.promise();
    this.context.validateEnumField("enumField", 3, promise, 0, 1, 2);
    testContext.assertFailure(promise.future()).onFailure(error -> {

      testContext.verify(() -> {

        assertThat(error).isInstanceOf(ValidationErrorException.class);
        final var cause = (ValidationErrorException) error;
        assertThat(cause.getCode()).isEqualTo("codePrefixEnum.enumField");
        assertThat(cause.getMessage()).contains("[0, 1, 2]");

      });
      testContext.completeNow();
    });

  }

  /**
   * Check that an email value is valid but it is {@code null}.
   *
   * @param value to be valid.
   *
   * @see ValidateContext#validateNullableEmailField(String, String, Promise)
   */
  @ParameterizedTest(name = "Should {0} be valid and return null")
  @NullSource
  @ValueSource(strings = { "", "        " })
  public void shouldValidateNullableEmailFieldReturnNull(final String value) {

    final Promise<Void> promise = Promise.promise();
    assertThat(this.context.validateNullableEmailField("emailField", value, promise)).isNull();
    assertThat(promise.tryComplete()).isTrue();

  }

  /**
   * Check that an email value is valid.
   *
   * @param value to be valid.
   *
   * @see ValidateContext#validateNullableEmailField(String, String, Promise)
   */
  @ParameterizedTest(name = "Should {0} be valid")
  @ValueSource(strings = { "a@b.com", "  a@b.com     " })
  public void shouldValidateNullableEmail(final String value) {

    final Promise<Void> promise = Promise.promise();
    assertThat(this.context.validateNullableEmailField("emailField", value, promise)).isEqualTo(value.trim());
    assertThat(promise.tryComplete()).isTrue();

  }

  /**
   * Check that an email value is not valid.
   *
   * @param value       to be valid.
   * @param testContext test context to use.
   *
   * @see ValidateContext#validateNullableEmailField(String, String, Promise)
   */
  @ParameterizedTest(name = "Should {0} not be valid")
  @ValueSource(strings = { "a", "a.@b", "a@b.", "     a     " })
  public void shouldFailValidateNullableEmail(final String value, final VertxTestContext testContext) {

    final Promise<Void> promise = Promise.promise();
    doReturn("codePrefixEmail").when(this.context).errorCode();
    assertThat(this.context.validateNullableEmailField("email", value, promise)).isEqualTo(value);
    testContext.assertFailure(promise.future()).onFailure(error -> {

      testContext.verify(() -> {

        assertThat(error).isInstanceOf(ValidationErrorException.class);
        final var cause = (ValidationErrorException) error;
        assertThat(cause.getCode()).isEqualTo("codePrefixEmail.email");
        assertThat(cause.getMessage()).contains(value.trim());

      });
      testContext.completeNow();
    });

  }

  /**
   * Check that a locale value is valid but it is {@code null}.
   *
   * @param value to be valid.
   *
   * @see ValidateContext#validateNullableLocaleField(String, String, Promise)
   */
  @ParameterizedTest(name = "Should {0} be valid and return null")
  @NullSource
  @ValueSource(strings = { "", "        " })
  public void shouldValidateNullableLocaleFieldReturnNull(final String value) {

    final Promise<Void> promise = Promise.promise();
    assertThat(this.context.validateNullableLocaleField("localeField", value, promise)).isNull();
    assertThat(promise.tryComplete()).isTrue();

  }

  /**
   * Check that a locale value is valid.
   *
   * @param value to be valid.
   *
   * @see ValidateContext#validateNullableLocaleField(String, String, Promise)
   */
  @ParameterizedTest(name = "Should {0} be valid")
  @ValueSource(strings = { "en_US", "  en_US    ", "en", "   en  " })
  public void shouldValidateNullableLocale(final String value) {

    final Promise<Void> promise = Promise.promise();
    assertThat(this.context.validateNullableLocaleField("localeField", value, promise)).isEqualTo(value.trim());
    assertThat(promise.tryComplete()).isTrue();

  }

  /**
   * Check that a locale value is not valid.
   *
   * @param value       to be valid.
   * @param testContext test context to use.
   *
   * @see ValidateContext#validateNullableLocaleField(String, String, Promise)
   */
  @ParameterizedTest(name = "Should {0} not be valid")
  @ValueSource(strings = { "de-Gr", "a@b", "a", "     de-Gr     " })
  public void shouldFailValidateNullableLocale(final String value, final VertxTestContext testContext) {

    final Promise<Void> promise = Promise.promise();
    doReturn("codePrefixLocale").when(this.context).errorCode();
    assertThat(this.context.validateNullableLocaleField("locale", value, promise)).isEqualTo(value);
    testContext.assertFailure(promise.future()).onFailure(error -> {

      testContext.verify(() -> {

        assertThat(error).isInstanceOf(ValidationErrorException.class);
        final var cause = (ValidationErrorException) error;
        assertThat(cause.getCode()).isEqualTo("codePrefixLocale.locale");
        assertThat(cause.getMessage()).contains(value.trim());

      });
      testContext.completeNow();
    });

  }

  /**
   * Check that a telephone value is valid but it is {@code null}.
   *
   * @param value to be valid.
   *
   * @see ValidateContext#validateNullableTelephoneField(String, String,String,
   *      Promise)
   */
  @ParameterizedTest(name = "Should {0} be valid and return null")
  @NullSource
  @ValueSource(strings = { "", "        " })
  public void shouldValidateNullableTelephoneFieldReturnNull(final String value) {

    final Promise<Void> promise = Promise.promise();
    assertThat(this.context.validateNullableTelephoneField("telephoneField", value, null, promise)).isNull();
    assertThat(promise.tryComplete()).isTrue();

  }

  /**
   * Check that a telephone value is valid.
   *
   * @param value to be valid.
   *
   * @see ValidateContext#validateNullableTelephoneField(String, String,String,
   *      Promise)
   */
  @ParameterizedTest(name = "Should {0} be valid")
  @ValueSource(strings = { "+34 987 65 43 21", "   +34 876 50 33 33  " })
  public void shouldValidateNullableTelephone(final String value) {

    final Promise<Void> promise = Promise.promise();
    assertThat(this.context.validateNullableTelephoneField("telephoneField", value.replaceAll("(\\d)\\s+(\\d)", "$1$2"),
        "es_ES", promise)).isEqualTo(value.trim());
    assertThat(promise.tryComplete()).isTrue();

  }

  /**
   * Check that a telephone value is not valid.
   *
   * @param value       to be valid.
   * @param testContext test context to use.
   *
   * @see ValidateContext#validateNullableTelephoneField(String, String, String,
   *      Promise)
   */
  @ParameterizedTest(name = "Should {0} not be valid")
  @ValueSource(strings = { "+349876543211", "bad telephone number", "1234567890123456789012345678" })
  public void shouldFailValidateNullableTelephone(final String value, final VertxTestContext testContext) {

    final Promise<Void> promise = Promise.promise();
    doReturn("codePrefixTelephone").when(this.context).errorCode();
    assertThat(this.context.validateNullableTelephoneField("telephone", value, null, promise)).isEqualTo(value);
    testContext.assertFailure(promise.future()).onFailure(error -> {

      testContext.verify(() -> {

        assertThat(error).isInstanceOf(ValidationErrorException.class);
        final var cause = (ValidationErrorException) error;
        assertThat(cause.getCode()).isEqualTo("codePrefixTelephone.telephone");
        assertThat(cause.getMessage()).isNotEmpty();

      });
      testContext.completeNow();
    });

  }

  /**
   * Check that a url value is valid but it is {@code null}.
   *
   * @param value to be valid.
   *
   * @see ValidateContext#validateNullableUrlField(String, String, Promise)
   */
  @ParameterizedTest(name = "Should {0} be valid and return null")
  @NullSource
  @ValueSource(strings = { "", "        " })
  public void shouldValidateNullableUrlFieldReturnNull(final String value) {

    final Promise<Void> promise = Promise.promise();
    assertThat(this.context.validateNullableUrlField("urlField", value, promise)).isNull();
    assertThat(promise.tryComplete()).isTrue();

  }

  /**
   * Check that a url value is valid.
   *
   * @param value to be valid.
   *
   * @see ValidateContext#validateNullableUrlField(String, String, Promise)
   */
  @ParameterizedTest(name = "Should {0} be valid")
  @ValueSource(strings = { "http://www.example.com/index.html ", "  http://www.example.com/index.html",
      "   http://www.example.com/index.html   " })
  public void shouldValidateNullableUrl(final String value) {

    final Promise<Void> promise = Promise.promise();
    assertThat(this.context.validateNullableUrlField("urlField", value, promise)).isEqualTo(value.trim());
    assertThat(promise.tryComplete()).isTrue();

  }

  /**
   * Check that a url value is not valid.
   *
   * @param value       to be valid.
   * @param testContext test context to use.
   *
   * @see ValidateContext#validateNullableUrlField(String, String, Promise)
   */
  @ParameterizedTest(name = "Should {0} not be valid")
  @ValueSource(strings = { "a://HTTP://localhost:9090/profiles", "a:", "example.com/file[/].html",
      "     a://HTTP://localhost:9090/profiles     " })
  public void shouldFailValidateNullableUrl(final String value, final VertxTestContext testContext) {

    final Promise<Void> promise = Promise.promise();
    doReturn("codePrefixUrl").when(this.context).errorCode();
    assertThat(this.context.validateNullableUrlField("url", value, promise)).isEqualTo(value);
    testContext.assertFailure(promise.future()).onFailure(error -> {

      testContext.verify(() -> {

        assertThat(error).isInstanceOf(ValidationErrorException.class);
        final var cause = (ValidationErrorException) error;
        assertThat(cause.getCode()).isEqualTo("codePrefixUrl.url");
        assertThat(cause.getMessage()).isNotEmpty();

      });
      testContext.completeNow();
    });

  }

  /**
   * Check that a date value is valid but it is {@code null}.
   *
   * @param value to be valid.
   *
   * @see ValidateContext#validateNullableDateField(String,
   *      String,java.time.format.DateTimeFormatter, Promise)
   */
  @ParameterizedTest(name = "Should {0} be valid and return null")
  @NullSource
  @ValueSource(strings = { "", "        " })
  public void shouldValidateNullableDateFieldReturnNull(final String value) {

    final Promise<Void> promise = Promise.promise();
    assertThat(this.context.validateNullableDateField("dateField", value, null, promise)).isNull();
    assertThat(promise.tryComplete()).isTrue();

  }

  /**
   * Check that a date value is valid.
   *
   * @param value to be valid.
   *
   * @see ValidateContext#validateNullableDateField(String,
   *      String,java.time.format.DateTimeFormatter, Promise)
   */
  @ParameterizedTest(name = "Should {0} be valid")
  @ValueSource(strings = { "2011-12-03t10:15:30z", "  2011-12-03t10:15:30z    ", "2011-12-03T10:15:30Z",
      "  2011-12-03t10:15:30Z    " })
  public void shouldValidateNullableDate(final String value) {

    final Promise<Void> promise = Promise.promise();
    assertThat(this.context.validateNullableDateField("dateField", value, DateTimeFormatter.ISO_INSTANT, promise))
        .isEqualTo(value.trim().toUpperCase());
    assertThat(promise.tryComplete()).isTrue();

  }

  /**
   * Check that a date value is not valid.
   *
   * @param value       to be valid.
   * @param testContext test context to use.
   *
   * @see ValidateContext#validateNullableDateField(String, String,
   *      java.time.format.DateTimeFormatter, Promise)
   */
  @ParameterizedTest(name = "Should {0} not be valid")
  @ValueSource(strings = { "+349876543211", "bad date number", "2011era" })
  public void shouldFailValidateNullableDate(final String value, final VertxTestContext testContext) {

    final Promise<Void> promise = Promise.promise();
    doReturn("codePrefixDate").when(this.context).errorCode();
    assertThat(this.context.validateNullableDateField("date", value, DateTimeFormatter.ISO_INSTANT, promise))
        .isEqualTo(value);
    testContext.assertFailure(promise.future()).onFailure(error -> {

      testContext.verify(() -> {

        assertThat(error).isInstanceOf(ValidationErrorException.class);
        final var cause = (ValidationErrorException) error;
        assertThat(cause.getCode()).isEqualTo("codePrefixDate.date");
        assertThat(cause.getMessage()).isNotEmpty();

      });
      testContext.completeNow();
    });

  }

  /**
   * Check that a range value is not valid because it is {@code null}.
   *
   * @param testContext test context to use.
   *
   * @see ValidateContext#validateNumberOnRangeField(String, Number, Number,
   *      Number, Promise)
   */
  @Test
  public void shouldFailValidateNumberOnRangeFieldNullValue(final VertxTestContext testContext) {

    final Promise<Void> promise = Promise.promise();
    doReturn("codePrefixRange").when(this.context).errorCode();
    this.context.validateNumberOnRangeField("range", null, null, null, promise);
    testContext.assertFailure(promise.future()).onFailure(error -> {

      testContext.verify(() -> {

        assertThat(error).isInstanceOf(ValidationErrorException.class);
        final var cause = (ValidationErrorException) error;
        assertThat(cause.getCode()).isEqualTo("codePrefixRange.range");
        assertThat(cause.getMessage()).contains("'range'");

      });
      testContext.completeNow();
    });

  }

  /**
   * Check that a range value is not valid because it is less than the minimum.
   *
   * @param testContext test context to use.
   *
   * @see ValidateContext#validateNumberOnRangeField(String, Number, Number,
   *      Number, Promise)
   */
  @Test
  public void shouldFailValidateNumberOnRangeFieldLessThan(final VertxTestContext testContext) {

    final Promise<Void> promise = Promise.promise();
    doReturn("codePrefixRange").when(this.context).errorCode();
    this.context.validateNumberOnRangeField("range", 0, 1, null, promise);
    testContext.assertFailure(promise.future()).onFailure(error -> {

      testContext.verify(() -> {

        assertThat(error).isInstanceOf(ValidationErrorException.class);
        final var cause = (ValidationErrorException) error;
        assertThat(cause.getCode()).isEqualTo("codePrefixRange.range");
        assertThat(cause.getMessage()).contains("'0'", "'1'");

      });
      testContext.completeNow();
    });

  }

  /**
   * Check that a range value is not valid because it is greater than the maximum.
   *
   * @param testContext test context to use.
   *
   * @see ValidateContext#validateNumberOnRangeField(String, Number, Number,
   *      Number, Promise)
   */
  @Test
  public void shouldFailValidateNumberOnRangeFieldGreaterThan(final VertxTestContext testContext) {

    final Promise<Void> promise = Promise.promise();
    doReturn("codePrefixRange").when(this.context).errorCode();
    this.context.validateNumberOnRangeField("range", 1, null, 0, promise);
    testContext.assertFailure(promise.future()).onFailure(error -> {

      testContext.verify(() -> {

        assertThat(error).isInstanceOf(ValidationErrorException.class);
        final var cause = (ValidationErrorException) error;
        assertThat(cause.getCode()).isEqualTo("codePrefixRange.range");
        assertThat(cause.getMessage()).contains("'0'", "'1'");

      });
      testContext.completeNow();
    });

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
   * Check that a range value is valid.
   *
   * @param val value that is not valid.
   * @param min minimum range value.
   * @param max maximum range value.
   *
   * @see ValidateContext#validateNumberOnRangeField(String, Number, Number,
   *      Number, Promise)
   */
  @ParameterizedTest(name = "Should {0} be on the range [{1},{2}]")
  @CsvSource({ "0.1,0.0,1.0", "0,0,1", "1,0,1", "1,0,", "0,,1", "123,," })
  public void shouldValidateNumberOnRangeField(final String val, final String min, final String max) {

    final Promise<Void> promise = Promise.promise();
    final Double value = Double.parseDouble(val);
    final var minValue = this.extractDouble(min);
    final var maxValue = this.extractDouble(max);

    this.context.validateNumberOnRangeField("range", value, minValue, maxValue, promise);
    assertThat(promise.tryComplete()).isTrue();

  }

  /**
   * Check that a range value is not valid.
   *
   * @param val         value that is not valid.
   * @param min         minimum range value.
   * @param max         maximum range value.
   * @param testContext test context to use.
   *
   * @see ValidateContext#validateNumberOnRangeField(String, Number, Number,
   *      Number, Promise)
   */
  @ParameterizedTest(name = "Should {0} not be on the range [{1},{2}]")
  @CsvSource({ "10,,1", "1,2,", "0.9999,1,3", "0,1,1", "-1,0,1", "2,0,1", "0,1,1.0000001" })
  public void shouldFailValidateNumberOnRangeField(final String val, final String min, final String max,
      final VertxTestContext testContext) {

    doReturn("codePrefixRange").when(this.context).errorCode();
    final Promise<Void> promise = Promise.promise();
    final Double value = Double.parseDouble(val);
    final var minValue = this.extractDouble(min);
    final var maxValue = this.extractDouble(max);
    this.context.validateNumberOnRangeField("range", value, minValue, maxValue, promise);
    testContext.assertFailure(promise.future()).onFailure(error -> {

      testContext.verify(() -> {

        assertThat(error).isInstanceOf(ValidationErrorException.class);
        final var cause = (ValidationErrorException) error;
        assertThat(cause.getCode()).isEqualTo("codePrefixRange.range");
        assertThat(cause.getMessage()).contains(String.valueOf(value));

      });
      testContext.completeNow();
    });

  }

  /**
   * Check that a timestamp value is valid.
   *
   * @param value to be valid.
   *
   * @see ValidateContext#validateTimeStampField(String, Long, Promise)
   */
  @ParameterizedTest(name = "Should {0} be valid")
  @ValueSource(longs = { 0, 1, 1724376512, 0l, 1l, 200l, 43l })
  public void shouldValidateTimeStampField(final Long value) {

    final Promise<Void> promise = Promise.promise();
    this.context.validateTimeStampField("timestampField", value, promise);
    assertThat(promise.tryComplete()).isTrue();

  }

  /**
   * Check that a timestamp value is not valid.
   *
   * @param value       to be valid.
   * @param testContext test context to use.
   *
   * @see ValidateContext#validateTimeStampField(String, Long, Promise)
   */
  @ParameterizedTest(name = "Should {0} not be valid")
  @NullSource
  @ValueSource(longs = { -1, -2, -1l, -200l, -43l })
  public void shouldFailValidateTimeStampField(final Long value, final VertxTestContext testContext) {

    final Promise<Void> promise = Promise.promise();
    doReturn("codePrefixTimestamp").when(this.context).errorCode();
    this.context.validateTimeStampField("timestamp", value, promise);
    testContext.assertFailure(promise.future()).onFailure(error -> {

      testContext.verify(() -> {

        assertThat(error).isInstanceOf(ValidationErrorException.class);
        final var cause = (ValidationErrorException) error;
        assertThat(cause.getCode()).isEqualTo("codePrefixTimestamp.timestamp");
        assertThat(cause.getMessage()).contains(String.valueOf(value));

      });
      testContext.completeNow();
    });

  }

  /**
   * Check that a string list value is valid but it is {@code null}.
   *
   * @see ValidateContext#validateNullableStringListField(String, java.util.List,
   *      Promise)
   */
  @Test
  public void shouldValidateNullableStringListFieldWithNull() {

    final Promise<Void> promise = Promise.promise();
    assertThat(this.context.validateNullableStringListField("list", null, promise)).isNull();
    assertThat(promise.tryComplete()).isTrue();

  }

  /**
   * Check that a string list value is valid but it return empty list.
   *
   * @see ValidateContext#validateNullableStringListField(String, java.util.List,
   *      Promise)
   */
  @Test
  public void shouldValidateNullableStringListFieldReturnEmptyList() {

    final Promise<Void> promise = Promise.promise();
    final var value = Arrays.asList(null, "", "        ");
    assertThat(this.context.validateNullableStringListField("list", value, promise)).isEmpty();
    assertThat(promise.tryComplete()).isTrue();

  }

  /**
   * Check that a string list value is valid.
   *
   * @see ValidateContext#validateNullableStringListField(String, java.util.List,
   *      Promise)
   */
  @Test
  public void shouldValidateNullableStringListField() {

    final Promise<Void> promise = Promise.promise();
    final var value = Arrays.asList("key1   ", null, "   key2", "", "key3", "        ");
    final var expected = Arrays.asList("key1", "key2", "key3");
    assertThat(this.context.validateNullableStringListField("list", value, promise)).isEqualTo(expected);
    assertThat(promise.tryComplete()).isTrue();

  }

  /**
   * Check that a string list value is not valid.
   *
   * @param testContext test context to use.
   *
   * @see ValidateContext#validateNullableStringListField(String, java.util.List,
   *      Promise)
   */
  @Test
  public void shouldFailValidateNullableStringListField(final VertxTestContext testContext) {

    final Promise<Void> promise = Promise.promise();
    doReturn("codePrefixList").when(this.context).errorCode();
    final var value = Arrays.asList("key1   ", null, "   key2", "", "key2", "        ");
    assertThat(this.context.validateNullableStringListField("list", value, promise)).isSameAs(value);
    testContext.assertFailure(promise.future()).onFailure(error -> {

      testContext.verify(() -> {

        assertThat(error).isInstanceOf(ValidationErrorException.class);
        final var cause = (ValidationErrorException) error;
        assertThat(cause.getCode()).isEqualTo("codePrefixList.list[4]");
        assertThat(cause.getMessage()).contains("key2", "1");

      });
      testContext.completeNow();
    });

  }

  /**
   * Should validate field.
   *
   * @param testContext test context to use.
   * @param field       to validate.
   *
   * @see ValidateContext#validateField(String, Validable)
   */
  @SuppressWarnings("unchecked")
  @Test
  public void shouldValidateField(final VertxTestContext testContext,
      @SuppressWarnings("rawtypes") @Mock final Validable field) {

    doReturn("model").when(this.context).errorCode();
    doReturn(this.context).when(this.context).createContextWithErrorCode("model.field");
    doReturn(Future.succeededFuture()).when(field).validate(this.context);
    final Future<Void> future = Future.succeededFuture().compose(this.context.validateField("field", field));
    testContext.assertComplete(future).onSuccess(result -> testContext.completeNow());

  }

  /**
   * Should fail validate field.
   *
   * @param testContext test context to use.
   * @param field       to validate.
   *
   * @see ValidateContext#validateField(String, Validable)
   */
  @SuppressWarnings("unchecked")
  @Test
  public void shouldFailValidateField(final VertxTestContext testContext,
      @SuppressWarnings("rawtypes") @Mock final Validable field) {

    doReturn("model").when(this.context).errorCode();
    doReturn(this.context).when(this.context).createContextWithErrorCode("model.field");
    final var expectedError = new Exception("Unexpected exception");
    doReturn(Future.failedFuture(expectedError)).when(field).validate(this.context);
    final Future<Void> future = Future.succeededFuture().compose(this.context.validateField("field", field));
    testContext.assertFailure(future).onFailure(error -> {

      testContext.verify(() -> {

        assertThat(error).isSameAs(expectedError);

      });
      testContext.completeNow();
    });

  }

  /**
   * Should validate list field.
   *
   * @param testContext test context to use.
   * @param element1    to validate.
   * @param element2    to validate.
   * @param element3    to validate.
   *
   * @see ValidateContext#validateListField(String, java.util.List,
   *      java.util.function.BiPredicate)
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Test
  public void shouldValidateListField(final VertxTestContext testContext, @Mock final Validable element1,
      @Mock final Validable element2, @Mock final Validable element3) {

    doReturn("model").when(this.context).errorCode();
    doReturn(this.context).when(this.context).createContextWithErrorCode("model.list[0]");
    doReturn(this.context).when(this.context).createContextWithErrorCode("model.list[1]");
    doReturn(this.context).when(this.context).createContextWithErrorCode("model.list[2]");
    final List<Validable> model = new ArrayList<>();
    model.add(null);
    model.add(element1);
    model.add(element2);
    model.add(null);
    model.add(element3);
    doReturn(Future.succeededFuture()).when(element1).validate(this.context);
    doReturn(Future.succeededFuture()).when(element2).validate(this.context);
    doReturn(Future.succeededFuture()).when(element3).validate(this.context);
    final Future<Void> future = Future.succeededFuture()
        .compose(this.context.validateListField("list", model, (a, b) -> false));
    testContext.assertComplete(future).onSuccess(empty -> {

      testContext.verify(() -> {

        assertThat(model).hasSize(3);
        assertThat(model.get(0)).isSameAs(element1);
        assertThat(model.get(1)).isSameAs(element2);
        assertThat(model.get(2)).isSameAs(element3);

      });
      testContext.completeNow();
    });

  }

  /**
   * Should fail validate list field.
   *
   * @param testContext test context to use.
   * @param element     to validate.
   *
   * @see ValidateContext#validateListField(String, java.util.List,
   *      java.util.function.BiPredicate)
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Test
  public void shouldFailValidateListField(final VertxTestContext testContext, @Mock final Validable element) {

    doReturn("model").when(this.context).errorCode();
    doReturn(this.context).when(this.context).createContextWithErrorCode("model.list[0]");
    final List<Validable> model = new ArrayList<>();
    model.add(null);
    model.add(element);
    model.add(null);
    final var expectedError = new Exception("Unexpected exception");
    doReturn(Future.failedFuture(expectedError)).when(element).validate(this.context);
    final Future<Void> future = Future.succeededFuture()
        .compose(this.context.validateListField("list", model, (a, b) -> false));

    testContext.assertFailure(future).onFailure(error -> {

      testContext.verify(() -> {

        assertThat(model).hasSize(1);
        assertThat(model.get(0)).isSameAs(element);
        assertThat(error).isSameAs(expectedError);

      });
      testContext.completeNow();
    });

  }

  /**
   * Should fail validate list field because duplicated field.
   *
   * @param testContext test context to use.
   * @param element1    to validate.
   * @param element2    to validate.
   * @param element3    to validate.
   * @param context1    to validate.
   * @param context2    to validate.
   * @param context3    to validate.
   *
   * @see ValidateContext#validateListField(String, java.util.List,
   *      java.util.function.BiPredicate)
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Test
  public void shouldFailValidateListFieldBecauseDuplicated(final VertxTestContext testContext,
      @Mock final Validable element1, @Mock final Validable element2, @Mock final Validable element3,
      @Mock(answer = Answers.CALLS_REAL_METHODS) final ValidateContext<?> context1,
      @Mock(answer = Answers.CALLS_REAL_METHODS) final ValidateContext<?> context2,
      @Mock(answer = Answers.CALLS_REAL_METHODS) final ValidateContext<?> context3) {

    doReturn("model").when(this.context).errorCode();
    doReturn(context1).when(this.context).createContextWithErrorCode("model.list[0]");
    doReturn(context2).when(this.context).createContextWithErrorCode("model.list[1]");
    doReturn(context3).when(this.context).createContextWithErrorCode("model.list[2]");
    doReturn("model.list[2]").when(context3).errorCode();
    final List<Validable> model = new ArrayList<>();
    model.add(element1);
    model.add(element2);
    model.add(element3);
    doReturn(Future.succeededFuture()).when(element1).validate(context1);
    doReturn(Future.succeededFuture()).when(element2).validate(context2);
    final Future<Void> future = Future.succeededFuture()
        .compose(this.context.validateListField("list", model, (a, b) -> a == element3));
    testContext.assertFailure(future).onFailure(error -> {

      testContext.verify(() -> {

        assertThat(error).isInstanceOf(ValidationErrorException.class);
        final var cause = (ValidationErrorException) error;
        assertThat(cause.getCode()).isEqualTo("model.list[2]");
        assertThat(cause.getMessage()).contains("0");

      });
      testContext.completeNow();
    });

  }

}
