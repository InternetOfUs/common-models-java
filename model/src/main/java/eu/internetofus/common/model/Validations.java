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
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.math.BigDecimal;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import javax.validation.constraints.NotNull;
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
   * @return the mapper function that can validate the model received from the
   *         future chain.
   *
   * @see Future#compose(Function)
   */
  static <T extends Validable> Function<T, Future<T>> validateChain(final String codePrefix, final Vertx vertx) {

    return model -> model.validate(codePrefix, vertx).map(validation -> model);

  }

  /**
   * Check if the OpenAPI specification is right.
   *
   * @param codePrefix    the prefix of the code to use for the error message.
   * @param specification to validate.
   *
   * @return the future that says if the OpenAPI specification is valid or not.
   */
  static Future<Void> validateOpenAPISpecification(final String codePrefix, final JsonObject specification) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();
    if (specification == null) {

      promise.fail(new ValidationErrorException(codePrefix, "The OpenAPI specification can not be null."));

    } else if (!specification.isEmpty()) {

      if (!specification.containsKey("type") && !specification.containsKey("oneOf")
          && !specification.containsKey("anyOf") && !specification.containsKey("allOf")
          && !specification.containsKey("$ref")) {

        promise.fail(new ValidationErrorException(codePrefix, "You must define the 'type'."));

      } else {

        FIELDS: for (final var fieldName : specification.fieldNames()) {

          final var value = specification.getValue(fieldName);
          final var fieldPrefix = codePrefix + "." + fieldName;
          switch (fieldName) {
          case "type":
            future = future.compose(empty -> checkType(fieldPrefix, value, specification));
            break;
          case "maximum":
            future = future.compose(empty -> isNotLessThan("minimum", value, specification, fieldPrefix));
          case "minimum":
          case "multipleOf":
            future = future.compose(empty -> ifTypeIs("integer", specification,
                () -> isInstanceOf(Number.class, value, fieldPrefix), () -> typeIs("number", specification, fieldPrefix)
                    .compose(empty2 -> isInstanceOf(Number.class, value, fieldPrefix))));
            break;
          case "exclusiveMinimum":
          case "exclusiveMaximum":
            future = future.compose(empty -> isInstanceOf(Boolean.class, value, fieldPrefix))
                .compose(empty -> ifTypeIs("integer", specification, () -> Future.succeededFuture(),
                    () -> typeIs("number", specification, fieldPrefix)));
            break;
          case "default":
            future = future.compose(empty -> checkDefault(fieldPrefix, value, specification));
            break;
          case "properties":
            future = future.compose(empty -> isInstanceOf(JsonObject.class, value, fieldPrefix))
                .compose(empty -> validateOpenAPIProperties(fieldPrefix, (JsonObject) value));
            break;
          case "items":
            future = future.compose(empty -> isInstanceOf(JsonObject.class, value, fieldPrefix))
                .compose(empty -> validateOpenAPISpecification(fieldPrefix, (JsonObject) value));
            break;
          case "nullable":
            future = future.compose(empty -> isInstanceOf(Boolean.class, value, fieldPrefix));
            break;
          case "required":
            future = future.compose(empty -> typeIs("object", specification, fieldPrefix))
                .compose(empty -> isInstanceOf(JsonArray.class, value, fieldPrefix))
                .compose(empty -> checkRequired(fieldPrefix, (JsonArray) value, specification));
            break;
          case "uniqueItems":
            future = future.compose(empty -> isInstanceOf(Boolean.class, value, fieldPrefix))
                .compose(empty -> typeIs("array", specification, fieldPrefix));
            break;
          case "maxItems":
            future = future.compose(empty -> isNotLessThan("minItems", value, specification, fieldPrefix));
          case "minItems":
            future = future.compose(empty -> isInstanceOfInteger(value, fieldPrefix))
                .compose(empty -> typeIs("array", specification, fieldPrefix));
            break;
          case "enum":
            future = future.compose(empty -> checkEnum(fieldPrefix, value, specification));
            break;
          case "maxLength":
            future = future.compose(empty -> isNotLessThan("minLength", value, specification, fieldPrefix));
          case "minLength":
            future = future.compose(empty -> isInstanceOfInteger(value, fieldPrefix))
                .compose(empty -> typeIs("string", specification, fieldPrefix));
            break;
          case "pattern":
            future = future.compose(empty -> isInstanceOf(String.class, value, fieldPrefix))
                .compose(empty -> typeIs("string", specification, fieldPrefix)).compose(empty -> {
                  try {

                    Pattern.compile((String) value);
                    return Future.succeededFuture();

                  } catch (final Throwable cause) {

                    return Future.failedFuture(
                        new ValidationErrorException(fieldPrefix, "You pattern is not right formatted.", cause));

                  }

                });
            break;
          case "additionalProperties":
            if (value instanceof Boolean && ((Boolean) value).booleanValue()) {

              future = future.compose(empty -> notContains("properties", specification, fieldPrefix));

            } else {

              future = future.compose(empty -> isInstanceOf(JsonObject.class, value, fieldPrefix))
                  .compose(empty -> validateOpenAPISpecification(fieldPrefix, (JsonObject) value))
                  .compose(empty -> typeIs("object", specification, fieldPrefix))
                  .compose(empty -> notContains("properties", specification, fieldPrefix));
            }
            break;
          case "maxProperties":
            future = future.compose(empty -> isNotLessThan("minProperties", value, specification, fieldPrefix));
          case "minProperties":
            future = future.compose(empty -> isInstanceOfInteger(value, fieldPrefix))
                .compose(empty -> typeIs("object", specification, fieldPrefix))
                .compose(empty -> notContains("properties", specification, fieldPrefix));
            break;
          case "oneOf":
          case "anyOf":
          case "allOf":
            future = future.compose(empty -> checkComposedType(value, specification, fieldPrefix))
                .compose(empty -> notContains("type", specification, fieldPrefix));
            break;
          case "not":
          case "$ref":
            promise.fail(new ValidationErrorException(fieldPrefix, "This feature is not supported yet."));
            break FIELDS;
          case "description":
          case "title":
            future = future.compose(empty -> typeIs("string", specification, fieldPrefix));
            break;
          case "example":
          case "examples":
            // Allow free examples because are not used for anything
          case "format":
            // No format is validated so it is unused
            break;
          default:
            promise.fail(new ValidationErrorException(fieldPrefix, "Unexpected OpenAPI field."));
            break FIELDS;
          }

        }
      }

    } // else accept any value

    promise.tryComplete();
    return future;
  }

  /**
   * Check the values that form a composed type.
   *
   * @param value         of the composed type.
   * @param specification where the composed type is defined.
   * @param codePrefix    the prefix of the code to use for the error message.
   *
   * @return The future that check if the value is of the specified type.
   */
  static Future<Void> checkComposedType(final Object value, final JsonObject specification, final String codePrefix) {

    return isInstanceOf(JsonArray.class, value, codePrefix).compose(empty -> {

      final Promise<Void> promise = Promise.promise();
      var future = promise.future();
      final var array = (JsonArray) value;
      final var max = array.size();
      if (max == 0) {

        promise.fail(new ValidationErrorException(codePrefix, "Expecting at least one type definition."));

      } else {

        for (var i = 0; i < max; i++) {

          final var pos = i;
          final var element = array.getValue(pos);
          final var elementPrefix = codePrefix + "[" + pos + "]";
          future = future.compose(empty2 -> isInstanceOf(JsonObject.class, element, elementPrefix))
              .compose(empty2 -> validateOpenAPISpecification(elementPrefix, (JsonObject) element));
        }

        promise.complete();
      }
      return future;
    });

  }

  /**
   * Check that a value is of the specified class type.
   *
   * @param clazz      that the value has to be instance of.
   * @param value      to validate.
   * @param codePrefix the prefix of the code to use for the error message.
   *
   * @return The future that check if the value is of the specified type.
   */
  private static Future<Void> isInstanceOf(@NotNull final Class<?> clazz, final Object value,
      @NotNull final String codePrefix) {

    if (clazz.isInstance(value)) {

      return Future.succeededFuture();

    } else {

      return Future
          .failedFuture(new ValidationErrorException(codePrefix, "Expecting a '" + clazz.getSimpleName() + "'."));
    }

  }

  /**
   * Check that a value is of the specified is an integer value.
   *
   * @param value      to validate.
   * @param codePrefix the prefix of the code to use for the error message.
   *
   * @return The future that check if the value is an integer.
   */
  private static Future<Void> isInstanceOfInteger(@NotNull final Object value, @NotNull final String codePrefix) {

    if (value instanceof Long || value instanceof Integer) {

      return Future.succeededFuture();

    } else {

      return Future.failedFuture(new ValidationErrorException(codePrefix, "Expecting an 'integer'."));
    }

  }

  /**
   * Check that the type of the specification is the expected one.
   *
   * @param type          that is expected.
   * @param specification where the type has to be defined.
   * @param codePrefix    the prefix of the code to use for the error message.
   *
   * @return The future that check if the value is an integer.
   */
  private static Future<Void> typeIs(@NotNull final String type, @NotNull final JsonObject specification,
      @NotNull final String codePrefix) {

    if (type.equals(specification.getString("type", null))) {

      return Future.succeededFuture();

    } else {

      return Future.failedFuture(
          new ValidationErrorException(codePrefix, "You only can use this feature if 'type'='" + type + "'."));
    }

  }

  /**
   * Do an action if the type is the specified.
   *
   * @param type          that is expected.
   * @param specification where the type has to be defined.
   * @param success       to use if the type is the expected one.
   * @param failure       to use if the type is not the expected one.
   *
   * @return The success future if is the specified type or the failure future
   *         otherwise.
   */
  private static Future<Void> ifTypeIs(@NotNull final String type, @NotNull final JsonObject specification,
      @NotNull final Supplier<Future<Void>> success, @NotNull final Supplier<Future<Void>> failure) {

    if (type.equals(specification.getString("type", null))) {

      return success.get();

    } else {

      return failure.get();
    }

  }

  /**
   * Check that a value is not less taht the value defined in another element.
   *
   * @param min           name of the field that the value can not be less than.
   * @param max           value to compare.
   * @param specification where the value is defined.
   * @param codePrefix    the prefix of the code to use for the error message.
   *
   * @return The future that check if the value is an integer.
   */
  private static Future<Void> isNotLessThan(@NotNull final String min, final Object max,
      @NotNull final JsonObject specification, @NotNull final String codePrefix) {

    if (max instanceof Number && ((Number) max).longValue() >= specification.getNumber(min, 0).longValue()) {

      return Future.succeededFuture();

    } else {

      return Future.failedFuture(new ValidationErrorException(codePrefix, "It can not be less than '" + min + "'."));
    }

  }

  /**
   * Check that the specification not contains the specified key.
   *
   * @param key           that has not be defined on the specification.
   * @param specification where the value is defined.
   * @param codePrefix    the prefix of the code to use for the error message.
   *
   * @return The future that check if the specification contains the key.
   */
  private static Future<Void> notContains(@NotNull final String key, @NotNull final JsonObject specification,
      @NotNull final String codePrefix) {

    if (specification.containsKey(key)) {

      return Future
          .failedFuture(new ValidationErrorException(codePrefix, "You can not use this feature with '" + key + "'."));

    } else {

      return Future.succeededFuture();

    }

  }

  /**
   * Check that the enum filed is right.
   *
   * @param codePrefix    the prefix of the code to use for the error message.
   * @param value         of the enum field.
   * @param specification that is checking.
   *
   * @return the future that say if the enum is valid or not.
   */
  private static Future<Void> checkEnum(final String codePrefix, final Object value, final JsonObject specification) {

    return isInstanceOf(JsonArray.class, value, codePrefix).compose(empty -> {

      final Promise<Void> promise = Promise.promise();
      var future = promise.future();
      final var array = (JsonArray) value;
      final var max = array.size();
      if (max == 0) {

        promise.fail(new ValidationErrorException(codePrefix, "Expecting at least one value for the enum."));

      } else {

        I: for (var i = 0; i < max; i++) {

          final var pos = i;
          final var enumValue = array.getValue(pos);
          for (var j = i + 1; j < max; j++) {

            final var otherValue = array.getValue(j);
            if (enumValue == otherValue || otherValue != null && otherValue.equals(enumValue)) {

              promise.fail(new ValidationErrorException(codePrefix + "[" + j + "]", "Duplicated enum value."));
              break I;
            }

          }
          if (enumValue == null) {

            if (!specification.getBoolean("nullable", false)) {

              promise
                  .fail(new ValidationErrorException(codePrefix + "[" + pos + "]", "The type not allow null values."));
              break;
            }

          } else {

            future = future
                .compose(empty2 -> validateOpenAPIValue(codePrefix + "[" + pos + "]", specification, enumValue)
                    .map(any -> null));
          }

        }

        promise.tryComplete();

      }

      return future;
    });
  }

  /**
   * Check that the default value is right.
   *
   * @param codePrefix    the prefix of the code to use for the error message.
   * @param value         of the default field.
   * @param specification that is checking.
   *
   * @return the future that say if the default value is valid or not.
   */
  private static Future<Void> checkDefault(final String codePrefix, final Object value,
      final JsonObject specification) {

    return isInstanceOf(String.class, value, codePrefix).compose(empty -> {

      try {

        final var decoded = Json.decodeValue((String) value);
        return validateOpenAPIValue(codePrefix, specification, decoded).map(any -> null);

      } catch (final Throwable cause) {

        return Future.failedFuture(new ValidationErrorException(codePrefix, "Bad encoded JSON value.", cause));
      }
    });

  }

  /**
   * Check the required field.
   *
   * @param codePrefix    the prefix of the code to use for the error message.
   * @param value         of the required field.
   * @param specification that is checking.
   *
   * @return The future that says if the required value is valid or not.
   */
  private static Future<Void> checkRequired(final String codePrefix, final JsonArray value,
      final JsonObject specification) {

    final Promise<Void> promise = Promise.promise();
    final var array = value;
    final var max = array.size();
    if (max == 0) {

      promise.fail(new ValidationErrorException(codePrefix, "The required field needs at least one field name."));

    } else {

      final var properties = specification.getJsonObject("properties", null);
      for (var i = 0; i < max; i++) {

        final var property = array.getValue(i);
        if (!(property instanceof String)) {

          promise.fail(new ValidationErrorException(codePrefix + "[" + i + "]", "Expecting a string value."));
          break;

        } else if (properties != null && !properties.containsKey((String) property)) {

          promise.fail(new ValidationErrorException(codePrefix + "[" + i + "]",
              "The required field is not defined in the properties."));
          break;
        }

      }
      promise.tryComplete();
    }

    return promise.future();

  }

  /**
   * Check if the OpenAPI properties are right.
   *
   * @param codePrefix the prefix of the code to use for the error message.
   * @param properties to validate.
   *
   * @return the future that says if the OpenAPI specification is valid or not.
   */
  static Future<Void> validateOpenAPIProperties(final String codePrefix, final JsonObject properties) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();
    if (properties == null) {

      promise.fail(new ValidationErrorException(codePrefix, "The OpenAPI specification can not be null."));

    } else {

      for (final var propertyName : properties.fieldNames()) {

        final var value = properties.getValue(propertyName);
        if (value instanceof JsonObject) {

          final var propertySpecification = (JsonObject) value;
          future = future
              .compose(empty -> validateOpenAPISpecification(codePrefix + "." + propertyName, propertySpecification));

        } else {

          promise.fail(new ValidationErrorException(codePrefix + "." + propertyName, "Expecting a JSON object."));
          break;

        }
      }

      promise.tryComplete();

    }
    return future;
  }

  /**
   * Check the type specification.
   *
   * @param codePrefix    the prefix for the error codes.
   * @param value         of the type.
   * @param specification where the type is defined.
   *
   * @return The future that say if the type is valid or not.
   */
  private static Future<Void> checkType(final String codePrefix, final Object value, final JsonObject specification) {

    return isInstanceOf(String.class, value, codePrefix).compose(empty -> {

      final Promise<Void> promise = Promise.promise();
      final var typeName = (String) value;
      switch (typeName) {
      case "boolean":
      case "number":
      case "integer":
      case "string":
      case "object":
        // nothing more to check
        break;
      case "array":
        if (!specification.containsKey("items")) {

          promise.fail(
              new ValidationErrorException(codePrefix, "You must define the field 'items' when the 'type' = 'array'."));
        }
        break;
      default:
        promise.fail(new ValidationErrorException(codePrefix, "Undefined 'type' value."));
      }

      promise.tryComplete();
      return promise.future();

    });
  }

  /**
   * Create the context to validate the value.
   */
  public class ValueValidationContext {

    /**
     * The prefix of the code to use for the error message.
     */
    public String codePrefix;

    /**
     * The specification that has to satisfy the value
     */
    public JsonObject specification;

    /**
     * The value to validate
     */
    public Object value;

    /**
     * The names of the fields that has been defined.
     */
    public Set<String> fieldNames;

    /**
     * Create a new context.
     *
     * @param codePrefix    the prefix of the code to use for the error message.
     * @param specification that has to satisfy the value.
     * @param value         to validate.
     */
    public ValueValidationContext(@NotNull final String codePrefix, @NotNull final JsonObject specification,
        final Object value) {

      this.codePrefix = codePrefix;
      this.specification = specification;
      this.value = value;
      this.fieldNames = null;

    }

    /**
     * Create an exception that explains why the context is not valid.
     *
     * @param message a brief description of the error to be read by a human.
     *
     * @return the exception with the message.
     */
    public ValidationErrorException notValid(final String message) {

      return this.notValid("", message, null);
    }

    /**
     * Create an exception that explains why the context is not valid.
     *
     * @param message a brief description of the error to be read by a human.
     * @param cause   because the model is not right.
     *
     * @return the exception with the message and the cause.
     */
    public ValidationErrorException notValid(final String message, final Throwable cause) {

      return this.notValid("", message, cause);
    }

    /**
     * Create an exception that explains why the context is not valid.
     *
     * @param partialPrefix prefix to append to the code prefix.
     * @param message       a brief description of the error to be read by a human.
     *
     * @return the exception with the message and the cause.
     */
    public ValidationErrorException notValid(final String partialPrefix, final String message) {

      return this.notValid(partialPrefix, message, null);
    }

    /**
     * Create an exception that explains why the context is not valid.
     *
     * @param partialPrefix prefix to append to the code prefix.
     * @param message       a brief description of the error to be read by a human.
     * @param cause         because the model is not right.
     *
     * @return the exception with the message and the cause.
     */
    public ValidationErrorException notValid(final String partialPrefix, final String message, final Throwable cause) {

      return new ValidationErrorException(this.codePrefix + partialPrefix, message, cause);
    }

    /**
     * Add the filed names of the specified component.
     *
     * @param properties to get the field names.
     */
    public void addFieldNames(final JsonObject properties) {

      if (this.fieldNames == null) {

        this.fieldNames = new HashSet<>();
      }
      this.fieldNames.addAll(properties.fieldNames());
    }

  }

  /**
   * Compose a value validation.
   *
   * @param future    to compose.
   * @param validator to execute.
   *
   * @return the future with the valid context.
   */
  private static Future<ValueValidationContext> composeValidation(final Future<ValueValidationContext> future,
      final BiFunction<Promise<ValueValidationContext>, ValueValidationContext, Future<ValueValidationContext>> validator) {

    return future.compose(context -> {

      final Promise<ValueValidationContext> promise = Promise.promise();
      var chain = promise.future();
      try {

        chain = validator.apply(promise, context);

      } catch (final Throwable cause) {

        promise.fail(context.notValid("The OpenAPI specification is not valid.", cause));
      }

      promise.tryComplete(context);
      return chain;

    });

  }

  /**
   * Check that a value match an OpenAPI specification.
   *
   * @param codePrefix    the prefix of the code to use for the error message.
   * @param specification that has to satisfy the value.
   * @param value         to validate.
   *
   * @return the future that says if the value follows or not the OpenAPI
   *         specification.
   */
  static Future<Object> validateOpenAPIValue(@NotNull final String codePrefix, @NotNull final JsonObject specification,
      final Object value) {

    return composeValidation(Future.succeededFuture(new ValueValidationContext(codePrefix, specification, value)),
        (promise, context) -> {

          var future = promise.future();
          if (!specification.isEmpty()) {

            if (value == null) {

              if (context.specification.containsKey("default")) {

                final var defaultValue = specification.getString("default");
                context.value = Json.decodeValue(defaultValue);

              } else if (!context.specification.getBoolean("nullable", false)) {

                promise.fail(context.notValid("Not allowed a 'null' value."));
              }

            } else {

              if (specification.containsKey("oneOf")) {

                future = composeValidation(future, Validations::validateOneOfValue);

              }

              if (specification.containsKey("anyOf")) {

                future = composeValidation(future, Validations::validateAnyOfValue);

              }

              if (specification.containsKey("allOf")) {

                future = composeValidation(future, Validations::validateAllOfValue);

              }

              if (specification.containsKey("type")) {

                future = composeValidation(future, Validations::validateTypeValue);

              }

              if (specification.containsKey("enum")) {

                future = composeValidation(future, Validations::validateEnumValue);

              }
              if (specification.containsKey("required")) {

                future = composeValidation(future, Validations::validateRequiredValue);

              }

              future = composeValidation(future, Validations::validateUndefinedFieldsInValue);

            }

          } // else accept any value

          return future;

        }).map(context -> context.value);

  }

  /**
   * Validate that the value has the required properties.
   *
   * @param promise to inform of the validation.
   * @param context to use for the validation.
   *
   * @return the future that says if the value is valid or not.
   */
  static Future<ValueValidationContext> validateRequiredValue(@NotNull final Promise<ValueValidationContext> promise,
      @NotNull final ValueValidationContext context) {

    final var object = (JsonObject) context.value;
    final var required = context.specification.getJsonArray("required");
    final var max = required.size();
    for (var i = 0; i < max; i++) {

      final var key = required.getString(i);
      if (!object.containsKey(key)) {

        promise.fail(context.notValid("." + key, "The field '" + key + "' is required."));
        break;
      }

    }

    return promise.future();

  }

  /**
   * Validate that the value is one of the enumerated ones.
   *
   * @param promise to inform of the validation.
   * @param context to use for the validation.
   *
   * @return the future that says if the value is valid or not.
   */
  static Future<ValueValidationContext> validateEnumValue(@NotNull final Promise<ValueValidationContext> promise,
      @NotNull final ValueValidationContext context) {

    final var enumValues = context.specification.getJsonArray("enum");
    if (!enumValues.contains(context.value)) {

      promise.fail(new ValidationErrorException(context.codePrefix, "Expecting a value in " + enumValues + "."));

    }

    return promise.future();

  }

  /**
   * Validate that the value is one of the specified values.
   *
   * @param promise to inform of the validation.
   * @param context to use for the validation.
   *
   * @return the future that says if the value is valid or not.
   */
  static private Future<ValueValidationContext> validateOneOfValue(
      @NotNull final Promise<ValueValidationContext> promise, @NotNull final ValueValidationContext context) {

    return promise.future();

  }

  /**
   * Validate that the value is any of the specified values.
   *
   * @param promise to inform of the validation.
   * @param context to use for the validation.
   *
   * @return the future that says if the value is valid or not.
   */
  static private Future<ValueValidationContext> validateAnyOfValue(
      @NotNull final Promise<ValueValidationContext> promise, @NotNull final ValueValidationContext context) {

    return promise.future();

  }

  /**
   * Validate that the value is all of the specified values.
   *
   * @param promise to inform of the validation.
   * @param context to use for the validation.
   *
   * @return the future that says if the value is valid or not.
   */
  static private Future<ValueValidationContext> validateAllOfValue(
      @NotNull final Promise<ValueValidationContext> promise, @NotNull final ValueValidationContext context) {

    return promise.future();

  }

  /**
   * Validate that the value is type of the specified values.
   *
   * @param promise to inform of the validation.
   * @param context to use for the validation.
   *
   * @return the future that says if the value is valid or not.
   */
  static private Future<ValueValidationContext> validateTypeValue(
      @NotNull final Promise<ValueValidationContext> promise, @NotNull final ValueValidationContext context) {

    var future = promise.future();
    final var type = context.specification.getString("type");
    switch (type) {
    case "boolean":
      future = future.compose(chain -> isInstanceOf(Boolean.class, chain.value, chain.codePrefix).map(empty -> chain));
      break;
    case "string":
      future = composeValidation(
          future.compose(chain -> isInstanceOf(String.class, chain.value, chain.codePrefix).map(empty -> chain)),
          Validations::validateStringValue);
      break;
    case "integer":
      future = future.compose(chain -> isInstanceOfInteger(chain.value, chain.codePrefix).map(empty -> chain));
    case "number":
      future = composeValidation(
          future.compose(chain -> isInstanceOf(Number.class, chain.value, chain.codePrefix).map(empty -> chain)),
          Validations::validateNumberValue);
      break;
    case "object":
      future = composeValidation(
          future.compose(chain -> isInstanceOf(JsonObject.class, chain.value, chain.codePrefix).map(empty -> chain)),
          Validations::validateObjectValue);
      break;
    case "array":
      future = composeValidation(
          future.compose(chain -> isInstanceOf(JsonArray.class, chain.value, chain.codePrefix).map(empty -> chain)),
          Validations::validateArrayValue);
      break;
    default:
      promise.fail(context.notValid("Bad OpenAPI definition with an unexpected 'type' for the value."));

    }

    return future;
  }

  /**
   * Validate that the value is a valid string.
   *
   * @param promise to inform of the validation.
   * @param context to use for the validation.
   *
   * @return the future that says if the string is valid or not.
   */
  static Future<ValueValidationContext> validateStringValue(@NotNull final Promise<ValueValidationContext> promise,
      @NotNull final ValueValidationContext context) {

    final var value = (String) context.value;
    final var pattern = context.specification.getString("pattern", null);
    if (pattern != null && !value.matches(pattern)) {

      promise.fail(context.notValid("The value does not match the pattern."));

    } else {

      final var min = context.specification.getInteger("minLength", null);
      if (min != null && value.length() < min) {

        promise.fail(context.notValid("The value has to have a length equal or greater than " + min + "."));

      } else {

        final var max = context.specification.getInteger("maxLength", null);
        if (max != null && value.length() > max) {

          promise.fail(context.notValid("The value has to have a length equal or less than " + max + "."));
        }
      }
    }

    return promise.future();
  }

  /**
   * Validate that the value is a valid number.
   *
   * @param promise to inform of the validation.
   * @param context to use for the validation.
   *
   * @return the future that says if the number is valid or not.
   */
  static Future<ValueValidationContext> validateNumberValue(@NotNull final Promise<ValueValidationContext> promise,
      @NotNull final ValueValidationContext context) {

    final var value = (Number) context.value;
    final var bigValue = new BigDecimal(value.doubleValue());
    final var min = context.specification.getNumber("minimum", null);
    if (min != null) {

      final var bigMin = new BigDecimal(min.doubleValue());
      final var compare = bigValue.compareTo(bigMin);
      final var excludeMin = context.specification.getBoolean("exclusiveMinimum", false);
      if (excludeMin && compare < 1) {

        promise.fail(context.notValid("The value must be greater than the minimum."));

      } else if (!excludeMin && compare < 0) {

        promise.fail(context.notValid("The value must be equals or greater than the minimum."));
      }
    }

    final var max = context.specification.getNumber("maximum", null);
    if (max != null) {

      final var bigMax = new BigDecimal(max.doubleValue());
      final var compare = bigValue.compareTo(bigMax);
      final var excludeMax = context.specification.getBoolean("exclusiveMaximum", false);
      if (excludeMax && compare > -1) {

        promise.tryFail(context.notValid("The value must be less than the maximum."));

      } else if (!excludeMax && compare > 0) {

        promise.tryFail(context.notValid("The value must be equals or less than the maximum."));
      }
    }

    return promise.future();
  }

  /**
   * Validate that the value is a valid array.
   *
   * @param promise to inform of the validation.
   * @param context to use for the validation.
   *
   * @return the future that says if the array is valid or not.
   */
  static Future<ValueValidationContext> validateArrayValue(@NotNull final Promise<ValueValidationContext> promise,
      @NotNull final ValueValidationContext context) {

    var future = promise.future();
    final var value = (JsonArray) context.value;
    final var max = value.size();
    final var minItems = context.specification.getNumber("minItems", null);
    if (minItems != null && minItems.intValue() > max) {

      promise.fail(context.notValid("The value require at least " + minItems + " items."));

    } else {

      final var maxItems = context.specification.getNumber("maxItems", null);
      if (maxItems != null && maxItems.intValue() < max) {

        promise.fail(context.notValid("The value require at maximum " + maxItems + " items."));

      } else {

        final var items = context.specification.getJsonObject("items");
        for (var i = 0; i < max; i++) {

          final var pos = i;
          final var element = value.getValue(pos);
          future = future.compose(
              chain -> validateOpenAPIValue(context.codePrefix + "[" + pos + "]", items, element).map(validElement -> {

                final var chainArray = (JsonArray) chain.value;
                if (chainArray.getValue(pos) != validElement) {

                  chainArray.set(pos, validElement);
                }
                return chain;
              }));

        }

        if (context.specification.getBoolean("uniqueItems", false)) {

          future = composeValidation(future, Validations::validateArrayUniqueItemsValue);
        }

      }
    }

    return future;
  }

  /**
   * Validate that the values or an array are unique.
   *
   * @param promise to inform of the validation.
   * @param context to use for the validation.
   *
   * @return the future that says if the array values are uniques or not.
   */
  static Future<ValueValidationContext> validateArrayUniqueItemsValue(
      @NotNull final Promise<ValueValidationContext> promise, @NotNull final ValueValidationContext context) {

    final var value = (JsonArray) context.value;
    final var max = value.size();

    I: for (var i = 0; i < max; i++) {

      for (var j = i + 1; j < max; j++) {

        final var a = value.getValue(i);
        final var b = value.getValue(j);
        if (a == null && b == null || a != null && a.equals(b)) {

          promise.fail(context.notValid("[" + j + "]", "Duplicated value"));
          break I;
        }
      }

    }

    return promise.future();
  }

  /**
   * Validate that the value is a valid object.
   *
   * @param promise to inform of the validation.
   * @param context to use for the validation.
   *
   * @return the future that says if the value is valid object or not.
   */
  static Future<ValueValidationContext> validateObjectValue(@NotNull final Promise<ValueValidationContext> promise,
      @NotNull final ValueValidationContext context) {

    var future = promise.future();
    final var value = (JsonObject) context.value;
    final var properties = context.specification.getJsonObject("properties");
    if (properties != null) {

      for (final var field : properties.fieldNames()) {

        final var fieldType = properties.getJsonObject(field);
        if (value.containsKey(field)) {

          final var fieldValue = value.getValue(field);
          future = future.compose(
              chain -> validateOpenAPIValue(context.codePrefix + "." + field, fieldType, fieldValue).map(validValue -> {

                if (((JsonObject) chain.value).getValue(field) != validValue) {

                  ((JsonObject) chain.value).put(field, validValue);
                }

                return chain;
              }));

        } else {
          // the fields are optional by default, but if they are not defined and has a
          // default value it has to be set
          future = future.compose(
              chain -> validateOpenAPIValue(context.codePrefix + "." + field, fieldType, null).transform(validated -> {

                if (validated.succeeded()) {

                  final var result = validated.result();
                  if (result != null) {

                    ((JsonObject) chain.value).put(field, result);
                  }
                }

                return Future.succeededFuture(chain);

              }));

        }
      }

      future = future.compose(chain -> {
        chain.addFieldNames(properties);
        return Future.succeededFuture(chain);
      });

    } else {

      final var additionalProperties = context.specification.getJsonObject("additionalProperties");
      if (additionalProperties != null) {

        for (final var field : value.fieldNames()) {

          final var fieldValue = value.getValue(field);
          future = future
              .compose(chain -> validateOpenAPIValue(context.codePrefix + "." + field, additionalProperties, fieldValue)
                  .map(validValue -> {

                    if (fieldValue != validValue) {

                      ((JsonObject) chain.value).put(field, validValue);
                    }

                    return chain;
                  }));

        }

      }
    }
    return future;
  }

  /**
   * Validate that the value does not contains any unexpected field.
   *
   * @param promise to inform of the validation.
   * @param context to use for the validation.
   *
   * @return the future that says if the value has all the expected fields or not.
   */
  static Future<ValueValidationContext> validateUndefinedFieldsInValue(
      @NotNull final Promise<ValueValidationContext> promise, @NotNull final ValueValidationContext context) {

    if (context.value instanceof JsonObject && context.fieldNames != null) {

      final var value = (JsonObject) context.value;
      for (final var field : value.fieldNames()) {

        if (!context.fieldNames.contains(field)) {

          promise.fail(context.notValid("." + field, "The field is not defined on the specification."));
          break;

        }
      }

    }

    return promise.future();

  }

}
