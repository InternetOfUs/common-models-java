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

    if (specification == null) {

      return Future
          .failedFuture(new ValidationErrorException(codePrefix, "The OpenAPI specification can not be null."));

    } else {

      final Promise<Void> promise = Promise.promise();
      final var future = promise.future();
      var foundType = false;
      for (final var fieldName : specification.fieldNames()) {

        final var value = specification.getValue(fieldName);
        ValidationErrorException error = null;
        switch (fieldName) {
        case "type":
          error = checkType(foundType, codePrefix, value, specification);
          foundType = true;
          break;
        case "minimum":
        case "maximum":
        case "multipleOf":
        case "exclusiveMinimum":
        case "exclusiveMaximum":
          error = checkNumberFields(codePrefix, fieldName, value, specification);
          break;
        }
        if (error != null) {

          promise.fail(error);

        }

      }

      if (!foundType) {

        promise.fail(new ValidationErrorException(codePrefix, "You must define the 'type' of the field."));

      }

      promise.tryComplete();
      return future;
    }
  }

  /**
   * Check if the OpenAPI specification is right.
   *
   * @param codePrefix    the prefix of the code to use for the error message.
   * @param specification to validate.
   *
   * @return the future that says if the OpenAPI specification is valid or not.
   */
  static Future<Void> validateOpenAPIObjectSpecification(final String codePrefix, final JsonObject specification) {

    if (specification == null) {

      return Future
          .failedFuture(new ValidationErrorException(codePrefix, "The OpenAPI specification can not be null."));

    } else {

      final Promise<Void> promise = Promise.promise();
      var future = promise.future();
      for (final var propertyName : specification.fieldNames()) {

        final var propertySpecification = specification.getJsonObject(propertyName);
        future = future
            .compose(empty -> validateOpenAPISpecification(codePrefix + "." + propertyName, propertySpecification));
      }

      promise.tryComplete();
      return future;
    }
  }

  /**
   * Check the type specification field is valid for a number type.
   *
   * @param codePrefix    the prefix for the error codes.
   * @param fieldName     the name of the checking specification field.
   * @param value         of the specification field name.
   * @param specification where the field is defined.
   *
   * @return {@code null} if the type is valid or the exception that explains why
   *         the type is not valid.
   */
  static ValidationErrorException checkNumberFields(final String codePrefix, final String fieldName, final Object value,
      final JsonObject specification) {

    final var type = specification.getString("type");
    if (type != null && ("integer".equals(type) || "number".equals(type))) {

      if (fieldName.startsWith("exclusive")) {

        if (!(value instanceof Boolean)) {

          return new ValidationErrorException(codePrefix + "." + fieldName, "Requires a boolean value.");
        }

      } else if (!(value instanceof Number)) {

        return new ValidationErrorException(codePrefix + "." + fieldName, "Requires a numeric value.");

      }

    } else {

      return new ValidationErrorException(codePrefix + "." + fieldName,
          "This field only can be used when the type is 'integer' or 'number'.");
    }

    return null;
  }

  /**
   * Check the type specification.
   *
   * @param foundType     is {@code true} if already has a type.
   * @param codePrefix    the prefix for the error codes.
   * @param value         of the type.
   * @param specification where the type is defined.
   *
   * @return {@code null} if the type is valid or the exception that explains why
   *         the type is not valid.
   */
  static private ValidationErrorException checkType(final boolean foundType, final String codePrefix,
      final Object value, final JsonObject specification) {

    if (foundType) {

      return new ValidationErrorException(codePrefix + ".type",
          "You already has defined the type with oneOf, anyOf or allOf.");

    } else if (value instanceof String) {

      final var typeName = (String) value;
      switch (typeName) {
      case "boolean":
      case "number":
      case "integer":
      case "string":
        // nothing more to check
        break;
      case "object":
        final var properties = specification.getJsonObject("properties");
        if (properties == null) {

          return new ValidationErrorException(codePrefix + ".properties",
              "You must define the field 'properties' when the 'type' = 'object'.");
        }
        break;
      case "array":
        final var items = specification.getJsonObject("items");
        if (items == null) {

          return new ValidationErrorException(codePrefix + ".items",
              "You must define the field 'items' when the 'type' = 'array'.");
        }
        break;
      default:
        return new ValidationErrorException(codePrefix + ".type", "Unexpected 'type' value.");
      }
      return null;

    } else {

      return new ValidationErrorException(codePrefix + ".type", "Unexpected 'type' value.");

    }
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
  static Future<Void> validateOpenAPIValue(final String codePrefix, final JsonObject specification,
      final Object value) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();
    ValidationErrorException error = null;
    try {

      if (!specification.isEmpty()) {

        if (value == null) {
          // check if specification is nullable
          if (!specification.getBoolean("nullable", false)) {

            error = new ValidationErrorException(codePrefix, "The value cannot be null.");
          }

        } else {

          final var type = specification.getString("type");
          if (type != null) {

            switch (type) {
            case "boolean":
              if (!(value instanceof Boolean)) {

                error = new ValidationErrorException(codePrefix, "The value must be a boolean.");
              }
              break;
            case "number":
            case "integer":
              if (!(value instanceof Number)) {

                error = new ValidationErrorException(codePrefix, "The value must be a number.");
              }
              break;
            case "string":
              if (!(value instanceof Number)) {

                error = new ValidationErrorException(codePrefix, "The value must be a string.");
              }
              break;
            case "object":
              if (!(value instanceof JsonObject)) {

                error = new ValidationErrorException(codePrefix, "The value must be a JSON object.");

              } else {

                final var properties = specification.getJsonObject("properties");
                future = future
                    .compose(empty -> validateOpenAPIJsonObjectValue(codePrefix, properties, (JsonObject) value));
              }
              break;
            case "array":
              if (!(value instanceof JsonArray)) {

                error = new ValidationErrorException(codePrefix, "The value must be a JSON array.");

              } else {

                final var array = (JsonArray) value;
                final var max = array.size();
                final var items = specification.getJsonObject("items");
                for (var i = 0; i < max; i++) {

                  final var pos = i;
                  final var element = array.getValue(pos);
                  future = future.compose(empty -> validateOpenAPIValue(codePrefix + "[" + pos + "]", items, element));

                }
              }
              break;
            default:
              error = new ValidationErrorException(codePrefix, "Unexpected 'type' specificationvalue.");
            }
          }
        }

      } // else accept any value

    } catch (final Throwable cause) {

      error = new ValidationErrorException(codePrefix, "The OpenAPI specification is not valid.", cause);
    }

    if (error != null) {

      promise.fail(error);

    } else {

      promise.complete();
    }
    return future;

  }

  /**
   * Check that a {@link JsonObject} value match an OpenAPI specification.
   *
   * @param codePrefix    the prefix of the code to use for the error message.
   * @param specification that has to satisfy the value.
   * @param value         to validate.
   *
   * @return the future that says if the value follows or not the OpenAPI
   *         specification.
   */
  static Future<Void> validateOpenAPIJsonObjectValue(final String codePrefix, final JsonObject specification,
      final JsonObject value) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();
    ValidationErrorException error = null;
    try {

      for (final var field : value.fieldNames()) {

        final var type = specification.getJsonObject(field);
        if (type == null) {

          error = new ValidationErrorException(codePrefix + "." + field,
              "The field is not defined on the specification.");
          break;

        } else {

          final var fieldValue = value.getValue(field);
          future = future.compose(empty -> validateOpenAPIValue(codePrefix + "." + field, type, fieldValue));
        }

      }

      final var required = specification.getJsonArray("required");
      if (required != null) {

        final var max = required.size();
        for (var i = 0; i < max; i++) {

          final var property = required.getString(i);
          final var type = specification.getJsonObject(property);
          final var fieldValue = value.getValue(property);
          if (fieldValue == null) {

            try {

              final var defaultValue = type.getString("default");
              value.put(property, Json.decodeValue(defaultValue));

            } catch (final Throwable t) {

              error = new ValidationErrorException(codePrefix + "." + property, "The field is required.");
              break;

            }
          }

        }
      }

    } catch (final Throwable cause) {

      error = new ValidationErrorException(codePrefix, "The OpenAPI specification is not valid.", cause);
    }

    if (error != null) {

      promise.fail(error);

    } else {

      promise.complete();
    }
    return future;
  }
}
