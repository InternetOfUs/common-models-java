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

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.BiPredicate;
import java.util.function.Function;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.validator.routines.EmailValidator;

/**
 * Represents a context to use in an operation.
 *
 * @param <SELF> type of validation context.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface ValidateContext<SELF extends ValidateContext<SELF>> {

  /**
   * Return the error code associated to this context.
   *
   * @return the code that start any failure on the context.
   */
  String errorCode();

  /**
   * Crate a failed future with the specified error message.
   *
   * @param message of the error.
   * @param <T>     type of expecting future.
   *
   * @return The failed future with the {@link ValidationErrorException} with the
   *         error code and message.
   *
   * @see #errorCode()
   */
  default <T> Future<T> fail(final String message) {

    return this.fail(this.errorCode(), message);

  }

  /**
   * Crate a failed future with the specified error message.
   *
   * @param code    of the error.
   * @param message of the error.
   * @param <T>     type of expecting future.
   *
   * @return The failed future with the {@link ValidationErrorException} with the
   *         error code and message.
   *
   * @see #errorCode()
   */
  default <T> Future<T> fail(final String code, final String message) {

    return Future.failedFuture(new ValidationErrorException(code, message));

  }

  /**
   * Crate a failed future with the specified error message.
   *
   * @param field   name of the failed field.
   * @param message of the error.
   * @param <T>     type of expecting future.
   *
   * @return The failed future with the {@link ValidationErrorException} with the
   *         error code and message.
   *
   * @see #errorCode()
   */
  default <T> Future<T> failField(final String field, final String message) {

    final var code = this.fieldErrorCode(field);
    return this.fail(code, message);

  }

  /**
   * Create a context for a field element.
   *
   * @param name  of the field to create the context.
   * @param index of the element to create a new context.
   *
   * @return the context for the specified element in the current validating
   *         model.
   */
  default SELF createFieldElementContext(final String name, final int index) {

    var code = this.fieldErrorCode(name);
    code += "[" + index + "]";
    return this.createContextWithErrorCode(code);
  }

  /**
   * Create a context for the field of a model.
   *
   * @param name of the field to create the context.
   *
   * @return the context for the specified field of the model.
   */
  default SELF createFieldContext(final String name) {

    final var code = this.fieldErrorCode(name);
    return this.createContextWithErrorCode(code);
  }

  /**
   * Return the error code for a name.
   *
   * @param name of the field to create the context.
   *
   * @return the error code for a field.
   */
  private String fieldErrorCode(final String name) {

    return this.errorCode() + "." + name;
  }

  /**
   * Create a context with the error code.
   *
   * @param errorCode for the next context.
   *
   * @return the context with the specified error code.
   */
  SELF createContextWithErrorCode(String errorCode);

  /**
   * Return a instance to this context.
   *
   * @return the instance to this context.
   */
  @SuppressWarnings("unchecked")
  default SELF self() {

    return (SELF) this;
  }

  /**
   * Validate the model received from the chain of events.
   *
   * @param <T> type of chain model.
   *
   * @return the future to compose to validate the model from the chain.
   */
  default <T extends Validable<SELF>> Function<T, Future<T>> chain() {

    return model -> model.validate(this.self()).map(empty -> model);
  }

  /**
   * Verify a field value is a non {@code null} or an empty string.
   *
   * @param fieldName name of the checking field.
   * @param value     to verify.
   * @param promise   to notify of any error.
   *
   * @return the normalized string value.
   */
  default String validateStringField(final String fieldName, final String value, final Promise<Void> promise) {

    if (value == null) {

      promise.tryFail(
          new ValidationErrorException(this.fieldErrorCode(fieldName), "The '" + fieldName + "' can not be 'null'."));
      return value;

    } else {

      final var trimmedValue = value.trim();
      if (trimmedValue.length() == 0) {

        promise.tryFail(new ValidationErrorException(this.fieldErrorCode(fieldName),
            "The '" + fieldName + "' can not be an empty value."));
        return value;

      } else {

        return trimmedValue;

      }
    }

  }

  /**
   * Normalize a string value.
   *
   * @param value to normalize.
   *
   * @return the normalized string value, thus trim it and if it is an empty
   *         string return {@code null}.
   */
  default String normalizeString(final String value) {

    if (value != null) {

      final var trimmedValue = value.trim();
      if (trimmedValue.length() > 0) {

        return trimmedValue;
      }
    }

    return null;

  }

  /**
   * Verify an enumeration field.
   *
   * @param fieldName      name of the checking field.
   * @param value          to verify.
   * @param promise        to notify of any error.
   * @param possibleValues values that can have the value.
   */
  default <T> void validateEnumField(final String fieldName, final T value, final Promise<Void> promise,
      @SuppressWarnings("unchecked") final T... possibleValues) {

    if (!Arrays.stream(possibleValues).anyMatch(element -> value.equals(element))) {

      promise.tryFail(new ValidationErrorException(this.fieldErrorCode(fieldName),
          "'" + value + "' is not a valid value, because it is not in '" + Arrays.toString(possibleValues) + "'."));

    }

  }

  /**
   * Verify a field value is {@code null} or a valid email address.
   *
   * @param fieldName name of the checking field.
   * @param value     to verify.
   * @param promise   to notify of any error.
   *
   * @return the normalized email value.
   */
  default String validateNullableEmailField(final String fieldName, final String value, final Promise<Void> promise) {

    if (value != null) {

      final var trimmedValue = value.trim();
      if (trimmedValue.length() > 0) {

        if (!EmailValidator.getInstance().isValid(trimmedValue)) {

          promise.tryFail(new ValidationErrorException(this.fieldErrorCode(fieldName),
              "The '" + trimmedValue + "' is not a valid e-mail address."));
          return value;

        } else {

          return trimmedValue;

        }

      }
    }

    return null;

  }

  /**
   * Verify a field value is {@code null} or a valid locale.
   *
   * @param fieldName name of the checking field.
   * @param value     to verify.
   * @param promise   to notify of any error.
   *
   * @return the normalized locale value.
   */
  default String validateNullableLocaleField(final String fieldName, final String value, final Promise<Void> promise) {

    if (value != null) {

      final var trimmedValue = value.trim();
      if (trimmedValue.length() > 0) {

        try {

          LocaleUtils.toLocale(trimmedValue);
          return trimmedValue;

        } catch (final IllegalArgumentException badLocale) {

          promise.tryFail(new ValidationErrorException(this.fieldErrorCode(fieldName), badLocale));
          return value;
        }

      }

    }

    return null;

  }

  /**
   * Verify a field value is {@code null} or a valid telephone.
   *
   * @param fieldName name of the checking field.
   * @param value     to verify.
   * @param locale    to use.
   * @param promise   to notify of any error.
   *
   * @return the normalized telephone value.
   */
  default String validateNullableTelephoneField(final String fieldName, final String value, final String locale,
      final Promise<Void> promise) {

    if (value != null) {

      final var trimmedValue = value.trim();
      if (trimmedValue.length() > 0) {

        try {

          final var phoneUtil = PhoneNumberUtil.getInstance();
          var defaultRegion = Locale.getDefault().getCountry();
          if (locale != null) {

            defaultRegion = new Locale(locale).getCountry();

          }
          final var number = phoneUtil.parse(trimmedValue, defaultRegion);
          if (!phoneUtil.isValidNumber(number)) {

            promise.tryFail(new ValidationErrorException(this.fieldErrorCode(fieldName),
                "The '" + trimmedValue + "' is not a valid telephone number"));
            return value;

          } else {

            return phoneUtil.format(number, PhoneNumberFormat.INTERNATIONAL);

          }

        } catch (final Throwable badTelephone) {

          promise.tryFail(new ValidationErrorException(this.fieldErrorCode(fieldName), badTelephone));
          return value;
        }
      }
    }

    return null;

  }

  /**
   * Verify a field value is {@code null} or a valid url.
   *
   * @param fieldName name of the checking field.
   * @param value     to verify.
   * @param promise   to notify of any error.
   *
   * @return the normalized url value.
   */
  default String validateNullableUrlField(final String fieldName, final String value, final Promise<Void> promise) {

    if (value != null) {

      final var trimmedValue = value.trim();
      if (trimmedValue.length() > 0) {

        try {

          final var url = new URL(trimmedValue);
          return url.toString();

        } catch (final Throwable badUrl) {

          promise.tryFail(new ValidationErrorException(this.fieldErrorCode(fieldName), badUrl));
          return value;
        }
      }
    }

    return null;

  }

  /**
   * Verify a field value is {@code null} or a valid date.
   *
   * @param fieldName name of the checking field.
   * @param value     to verify.
   * @param format    that has to have the date.
   * @param promise   to notify of any error.
   *
   * @return the normalized date value.
   */
  default String validateNullableDateField(final String fieldName, final String value, final DateTimeFormatter format,
      final Promise<Void> promise) {

    if (value != null) {

      final var trimmedValue = value.trim();
      if (trimmedValue.length() > 0) {

        try {

          final var date = format.parse(trimmedValue);
          return format.format(date);

        } catch (final Throwable badDate) {

          promise.tryFail(new ValidationErrorException(this.fieldErrorCode(fieldName), badDate));
          return value;
        }
      }
    }

    return null;

  }

  /**
   * Validate that a number value is on the specified range.
   *
   * @param fieldName name of the checking field.
   * @param value     to verify.
   * @param minValue  minimum value, inclusive, of the range the value can be
   *                  defined, or {@code null} if not have minimum.
   * @param maxValue  maximum value, inclusive, of the range the value can be
   *                  defined, or {@code null} if not have maximum.
   * @param promise   to notify of any error.
   *
   * @param <T>       type of number to validate.
   */
  default <T extends Number> void validateNumberOnRangeField(final String fieldName, final T value, final T minValue,
      final T maxValue, final Promise<Void> promise) {

    if (value == null) {

      promise.tryFail(
          new ValidationErrorException(this.fieldErrorCode(fieldName), "The '" + value + "' has to be defined."));

    } else if (minValue != null && value.doubleValue() < minValue.doubleValue()) {

      promise.tryFail(new ValidationErrorException(this.fieldErrorCode(fieldName),
          "The '" + value + "' is not valid because it is less than '" + minValue + "'."));

    } else if (maxValue != null && value.doubleValue() > maxValue.doubleValue()) {

      promise.tryFail(new ValidationErrorException(this.fieldErrorCode(fieldName),
          "The '" + value + "' is not valid because it is greater than '" + maxValue + "'."));

    }
  }

  /**
   * Validate that a number value is on the specified range.
   *
   * @param fieldName name of the checking field.
   * @param value     to verify.
   * @param promise   to notify of any error.
   */
  default void validateTimeStampField(final String fieldName, final Long value, final Promise<Void> promise) {

    if (value == null) {

      promise.tryFail(
          new ValidationErrorException(this.fieldErrorCode(fieldName), "The '" + value + "' has to be defined."));

    } else if (value < 0) {

      promise.tryFail(new ValidationErrorException(this.fieldErrorCode(fieldName),
          "The '" + value + "' is not valid time stamp because is less than '0'."));

    }

  }

  /**
   * Verify a field value is {@code null} or a list of strings.
   *
   * @param fieldName name of the checking field.
   * @param value     to verify.
   * @param promise   to notify of any error.
   *
   * @return the normalized list value.
   */
  default List<String> validateNullableStringListField(final String fieldName, final List<String> value,
      final Promise<Void> promise) {

    if (value != null) {

      final var validValues = new ArrayList<String>();
      final var max = value.size();
      for (var index = 0; index < max; index++) {

        final var element = this.normalizeString(value.get(index));
        if (element != null) {

          final var found = validValues.indexOf(element);
          if (found > -1) {

            promise.tryFail(new ValidationErrorException(this.fieldErrorCode(fieldName) + "[" + index + "]",
                "'" + element + "' is duplicated at '" + found + "'."));
            return value;

          }
          validValues.add(element);
        }
      }

      return validValues;
    }

    return null;

  }

  /**
   * Verify that a field model is valid.
   *
   * @param fieldName name of the field to validate.
   * @param value     to verify.
   *
   * @param <T>       type of value to verify.
   *
   * @return the validation result of the field.
   */
  default <T extends Validable<SELF>> Function<Void, Future<Void>> validateField(final String fieldName,
      final T value) {

    return empty -> value.validate(this.createFieldContext(fieldName));

  }

  /**
   * Verify that a field with a list of models is valid.
   *
   * @param fieldName name of the field to validate.
   * @param value     to verify.
   * @param predicate used to compare if two models are equals, to check if they
   *                  are duplicated.
   *
   * @param <T>       type of models to verify.
   *
   * @return the validation result of the field.
   */
  default <T extends Validable<SELF>> Function<Void, Future<Void>> validateListField(final String fieldName,
      final List<T> value, final BiPredicate<T, T> predicate) {

    return empty -> {

      if (value == null) {

        return this.failField(fieldName, "The '" + fieldName + "' can not be 'null'.");

      } else {

        Future<Void> future = Future.succeededFuture();
        final var iterator = value.listIterator();
        while (iterator.hasNext()) {

          final var model = iterator.next();
          if (model == null) {

            iterator.remove();

          } else {

            final var index = iterator.previousIndex();
            final var elementContext = this.createFieldElementContext(fieldName, index);
            future = future.compose(empty2 -> model.validate(elementContext));
            future = future.compose(empty2 -> {

              for (var firstIndex = 0; firstIndex < index; firstIndex++) {

                final var element = value.get(firstIndex);
                if (predicate.test(model, element)) {

                  return elementContext.fail("This model is already defined at '" + firstIndex + "'.");

                }
              }

              return Future.succeededFuture();

            });
          }

        }
        return future;

      }
    };

  }

}
