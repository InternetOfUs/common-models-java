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
import io.vertx.core.Vertx;
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
 * Generic methods to validate the common fields of the models.
 *
 * @see ValidationErrorException
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface Validations {

  /**
   * Verify a string value that can be null.
   *
   * @param codePrefix     the prefix of the code to use for the error message.
   * @param fieldName      name of the checking field.
   * @param value          to verify.
   * @param possibleValues values that can have the value.
   *
   * @return the future with the validated value.
   */
  static Future<String> validateNullableStringField(final String codePrefix, final String fieldName, final String value,
      final String... possibleValues) {

    final Promise<String> promise = Promise.promise();
    if (value != null) {

      final var trimedValue = value.trim();
      if (trimedValue.length() == 0) {

        promise.complete();

      } else if (possibleValues != null && possibleValues.length > 0
          && !Arrays.stream(possibleValues).anyMatch(element -> trimedValue.equals(element))) {

        promise.fail(new ValidationErrorException(codePrefix + "." + fieldName,
            "'" + trimedValue + "' is not a valid value for the field '" + fieldName + "', because it expects any of '"
                + Arrays.toString(possibleValues) + "'."));

      } else {

        promise.complete(trimedValue);

      }

    } else {

      promise.complete();
    }

    return promise.future();
  }

  /**
   * Verify a list of string values.
   *
   * @param codePrefix the prefix of the code to use for the error message.
   * @param fieldName  name of the checking field.
   * @param values     to verify.
   *
   * @return the future verified value.
   */
  static Future<List<String>> validateNullableListStringField(final String codePrefix, final String fieldName,
      final List<String> values) {

    final Promise<List<String>> promise = Promise.promise();
    final var future = promise.future();
    if (values == null || values.isEmpty()) {

      promise.complete();

    } else {

      final var validValues = new ArrayList<String>();
      final var max = values.size();
      for (var index = 0; index < max; index++) {

        var value = values.get(index);
        if (value != null) {

          value = value.trim();
          if (value.length() > 0) {

            final var found = validValues.indexOf(value);
            if (found > -1) {

              promise.fail(new ValidationErrorException(codePrefix + "." + fieldName + "[" + index + "]",
                  "'" + value + "' is duplicated at '" + found + "'."));
              return future;

            }
            validValues.add(value);
          }
        }
      }

      promise.complete(validValues);
    }
    return future;

  }

  /**
   * Verify a string value.
   *
   * @param codePrefix     the prefix of the code to use for the error message.
   * @param fieldName      name of the checking field.
   * @param value          to verify.
   * @param possibleValues values that can have the value.
   *
   * @return the future verified value.
   *
   * @throws ValidationErrorException If the value is not a valid string.
   */
  static Future<String> validateStringField(final String codePrefix, final String fieldName, final String value,
      final String... possibleValues) throws ValidationErrorException {

    return validateNullableStringField(codePrefix, fieldName, value, possibleValues).compose(trimedValue -> {

      if (trimedValue == null) {

        return Future.failedFuture(new ValidationErrorException(codePrefix + "." + fieldName,
            "The '" + fieldName + "' can not be 'null' or contains an empty value."));

      } else {

        return Future.succeededFuture(trimedValue);
      }

    });
  }

  /**
   * Verify an email value.
   *
   * @param codePrefix the prefix of the code to use for the error message.
   * @param fieldName  name of the checking field.
   * @param value      to verify.
   *
   * @return the future verified email value.
   */
  static Future<String> validateNullableEmailField(final String codePrefix, final String fieldName,
      final String value) {

    return validateNullableStringField(codePrefix, fieldName, value).compose(validStringValue -> {
      if (validStringValue != null && !EmailValidator.getInstance().isValid(validStringValue)) {

        return Future.failedFuture(new ValidationErrorException(codePrefix + "." + fieldName,
            "The '" + validStringValue + "' is not a valid email address."));

      } else {

        return Future.succeededFuture(validStringValue);
      }

    });

  }

  /**
   * Verify a locale value.
   *
   * @param codePrefix the prefix of the code to use for the error message.
   * @param fieldName  name of the checking field.
   * @param value      to verify.
   *
   * @return the future verified locale value.
   */
  static Future<String> validateNullableLocaleField(final String codePrefix, final String fieldName,
      final String value) {

    return validateNullableStringField(codePrefix, fieldName, value).compose(validStringValue -> {

      try {

        if (validStringValue != null) {

          LocaleUtils.toLocale(validStringValue);
        }
        return Future.succeededFuture(validStringValue);

      } catch (final IllegalArgumentException badLocale) {

        return Future.failedFuture(new ValidationErrorException(codePrefix + "." + fieldName, badLocale));

      }

    });
  }

  /**
   * Verify a telephone value.
   *
   * @param codePrefix the prefix of the code to use for the error message.
   * @param fieldName  name of the checking field.
   * @param locale     to use.
   * @param value      to verify.
   *
   * @return the future verified telephone value.
   */
  static Future<String> validateNullableTelephoneField(final String codePrefix, final String fieldName,
      final String locale, final String value) {

    return validateNullableStringField(codePrefix, fieldName, value).compose(validStringValue -> {

      if (validStringValue != null) {

        try {

          final var phoneUtil = PhoneNumberUtil.getInstance();
          var defaultRegion = Locale.getDefault().getCountry();
          if (locale != null) {

            defaultRegion = new Locale(locale).getCountry();

          }
          final var number = phoneUtil.parse(validStringValue, defaultRegion);
          if (!phoneUtil.isValidNumber(number)) {

            return Future.failedFuture(new ValidationErrorException(codePrefix + "." + fieldName,
                "The '" + validStringValue + "' is not a valid telephone number"));

          } else {

            return Future.succeededFuture(phoneUtil.format(number, PhoneNumberFormat.INTERNATIONAL));

          }

        } catch (final Throwable badTelephone) {

          return Future.failedFuture(new ValidationErrorException(codePrefix + "." + fieldName, badTelephone));

        }

      } else {

        return Future.succeededFuture(validStringValue);
      }

    });

  }

  /**
   * Verify an URL value.
   *
   * @param codePrefix the prefix of the code to use for the error message.
   * @param fieldName  name of the checking field.
   * @param value      to verify.
   *
   * @return the future verified URL value.
   */
  static Future<String> validateNullableURLField(final String codePrefix, final String fieldName, final String value) {

    return validateNullableStringField(codePrefix, fieldName, value).compose(validStringValue -> {
      if (validStringValue != null) {

        try {

          final var url = new URL(value);
          return Future.succeededFuture(url.toString());

        } catch (final Throwable badURL) {

          return Future.failedFuture(new ValidationErrorException(codePrefix + "." + fieldName, badURL));

        }

      } else {

        return Future.succeededFuture();
      }

    });
  }

  /**
   * Verify a date value.
   *
   * @param codePrefix the prefix of the code to use for the error message.
   * @param fieldName  name of the checking field.
   * @param format     that has to have the date.
   * @param value      to verify.
   *
   * @return the future verified date.
   */
  static Future<String> validateNullableStringDateField(final String codePrefix, final String fieldName,
      final DateTimeFormatter format, final String value) {

    return validateNullableStringField(codePrefix, fieldName, value).compose(validStringValue -> {
      if (validStringValue != null) {

        try {

          final var date = format.parse(validStringValue);
          return Future.succeededFuture(format.format(date));

        } catch (final Throwable badDate) {

          return Future.failedFuture(new ValidationErrorException(codePrefix + "." + fieldName, badDate));

        }

      } else {

        return Future.succeededFuture();
      }
    });
  }

  /**
   * Validate a fields that contains a list of models.
   *
   * @param models     to validate.
   * @param predicate  used to compare if two models are equals, to check if they
   *                   are duplicated.
   * @param codePrefix the prefix of the code to use for the error message.
   * @param vertx      the event bus infrastructure to use.
   *
   * @param <T>        type of model to validate.
   *
   *
   * @return the function that can be composed with the future that is validating
   *         the model of the filed.
   *
   * @see ValidationErrorException
   */
  static <T extends Validable> Function<Void, Future<Void>> validate(final List<T> models,
      final BiPredicate<T, T> predicate, final String codePrefix, final Vertx vertx) {

    return mapper -> {
      final Promise<Void> promise = Promise.promise();
      var future = promise.future();
      if (models != null) {

        final var iterator = models.listIterator();
        while (iterator.hasNext()) {

          final var model = iterator.next();
          if (model == null) {

            iterator.remove();

          } else {

            final var index = iterator.previousIndex();
            final var modelPrefix = codePrefix + "[" + index + "]";
            future = future.compose(elementMapper -> model.validate(modelPrefix, vertx));
            future = future.compose(elementMapper -> {

              for (var firstIndex = 0; firstIndex < index; firstIndex++) {

                final var element = models.get(firstIndex);
                if (predicate.test(element, model)) {

                  return Future.failedFuture(new ValidationErrorException(modelPrefix,
                      "This model is already defined at '" + firstIndex + "'."));

                }
              }

              return Future.succeededFuture();

            });
          }

        }

      }

      promise.complete();

      return future;
    };
  }

  /**
   * Validate that the value is a valid time stamp.
   *
   * @param codePrefix the prefix of the code to use for the error message.
   * @param fieldName  name of the checking field.
   * @param value      to verify.
   * @param nullable   is{@code true} if the value can be {@code null}.
   *
   * @return the valid time stamp value.
   */
  static Future<Void> validateTimeStamp(final String codePrefix, final String fieldName, final Long value,
      final boolean nullable) {

    final Promise<Void> promise = Promise.promise();
    if (value != null && value < 0) {

      promise.fail(new ValidationErrorException(codePrefix + "." + fieldName,
          "The '" + value + "' is not valid time stamp because is less than '0'."));

    } else if (!nullable && value == null) {

      promise
          .fail(new ValidationErrorException(codePrefix + "." + fieldName, "The '" + value + "' has to be defined."));

    } else {

      promise.complete();
    }
    return promise.future();
  }

  /**
   * Validate that a number value is on the specified range.
   *
   * @param codePrefix the prefix of the code to use for the error message.
   * @param fieldName  name of the checking field.
   * @param value      to verify.
   * @param nullable   is {@code true} if the value can be {@code null}.
   * @param minValue   minimum value, inclusive, of the range the value can be
   *                   defined, or {@code null} if not have minimum.
   * @param maxValue   maximum value, inclusive, of the range the value can be
   *                   defined, or {@code null} if not have maximum.
   *
   * @return the valid time stamp value.
   *
   * @param <T> type of number to validate.
   */
  static <T extends Number> Future<Void> validateNumberOnRange(final String codePrefix, final String fieldName,
      final T value, final boolean nullable, final T minValue, final T maxValue) {

    final Promise<Void> promise = Promise.promise();
    if (value != null && minValue != null && value.doubleValue() < minValue.doubleValue()) {

      promise.fail(new ValidationErrorException(codePrefix + "." + fieldName,
          "The '" + value + "' is not valid because it is less than '" + minValue + "'."));

    } else if (value != null && maxValue != null && value.doubleValue() > maxValue.doubleValue()) {

      promise.fail(new ValidationErrorException(codePrefix + "." + fieldName,
          "The '" + value + "' is not valid because it is greather than '" + maxValue + "'."));

    } else if (!nullable && value == null) {

      promise
          .fail(new ValidationErrorException(codePrefix + "." + fieldName, "The '" + value + "' has to be defined."));

    } else {

      promise.complete();
    }
    return promise.future();
  }

  /**
   * Validate that an identifier is valid.
   *
   * @param future     to compose.
   * @param codePrefix the prefix of the code to use for the error message.
   * @param fieldName  name of the checking field.
   * @param id         value to verify.
   * @param exist      is {@code true} if the identifier has to exist.
   * @param searcher   component to search for the model.
   *
   * @param <T>        type of model associated to the id.
   *
   * @return the mapper that will check the identifier.
   */
  static <T> Future<Void> composeValidateId(final Future<Void> future, final String codePrefix, final String fieldName,
      final String id, final boolean exist, final Function<String, Future<T>> searcher) {

    return future.compose(map -> {

      final Promise<Void> promise = Promise.promise();

      if (id == null) {

        promise.fail(
            new ValidationErrorException(codePrefix + "." + fieldName, "The '" + fieldName + "' cannot be 'null'."));

      } else {

        searcher.apply(id).onComplete(search -> {

          if (search.failed()) {

            if (exist) {

              promise.fail(new ValidationErrorException(codePrefix + "." + fieldName,
                  "The '" + fieldName + "' '" + id + "' is not defined.", search.cause()));

            } else {

              promise.complete();
            }

          } else if (exist) {

            promise.complete();

          } else {

            promise.fail(new ValidationErrorException(codePrefix + "." + fieldName,
                "The '" + fieldName + "' '" + id + "' has already defined."));
          }

        });

      }

      return promise.future();
    });

  }

  /**
   * Validate that a list of identifiers are valid.
   *
   * @param future     to compose.
   * @param codePrefix the prefix of the code to use for the error message.
   * @param fieldName  name of the checking field.
   * @param ids        value to verify.
   * @param exist      is {@code true} if the identifier has to exist.
   * @param searcher   component to search for the model.
   *
   * @param <T>        type of model associated to the id.
   *
   * @return the mapper that will check the identifier.
   */
  static <T> Future<Void> composeValidateIds(final Future<Void> future, final String codePrefix, final String fieldName,
      final List<String> ids, final boolean exist, final Function<String, Future<T>> searcher) {

    return future.compose(map -> {

      if (ids == null || ids.isEmpty()) {

        return Future.succeededFuture();

      } else {

        final Promise<Void> promise = Promise.promise();
        var idsFuture = promise.future();
        for (final var i = ids.listIterator(); i.hasNext();) {

          final var id = i.next();
          if (id != null) {

            final var index = i.previousIndex();
            final var elementName = fieldName + "[" + index + "]";
            final var found = ids.indexOf(id);
            if (found != index) {

              return Future.failedFuture(new ValidationErrorException(codePrefix + "." + elementName,
                  "Duplicated identifier at '" + found + "'."));

            }
            idsFuture = Validations.composeValidateId(idsFuture, codePrefix, elementName, id, exist, searcher);

          } else {

            i.remove();
          }

        }
        promise.complete();

        return idsFuture;
      }
    });

  }

  /**
   * Validate the model received from the chain of events.
   *
   * @param codePrefix the prefix of the code to use for the error message.
   * @param vertx      the event bus infrastructure to use.
   *
   * @param <T>        type of {@link Validable} model to validate.
   *
   * @return the mapper function that can validate teh model received from the
   *         future chain.
   *
   * @see Future#compose(Function)
   */
  static <T extends Validable> Function<T, Future<T>> validateChain(final String codePrefix, final Vertx vertx) {

    return model -> model.validate(codePrefix, vertx).map(validation -> model);

  }

}
