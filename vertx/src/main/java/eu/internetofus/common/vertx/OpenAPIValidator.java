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

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import eu.internetofus.common.model.ValidationErrorException;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import javax.validation.constraints.NotNull;
import org.apache.commons.io.IOUtils;

/**
 * The class used to validate an OpenAPI specification or that a value follow an
 * OpenAPI specification.
 *
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class OpenAPIValidator {

  /**
   * Check that an specification is right.
   *
   * @param codePrefix    the prefix of the code to use for the error message.
   * @param vertx         the event bus infrastructure to use.
   * @param specification to validate.
   *
   * @return the future that inform if the specification is right or not.
   */
  public static final Future<Void> validateSpecification(final String codePrefix, final Vertx vertx,
      final JsonObject specification) {

    final var env = new SpecificationEnvironment(codePrefix, vertx, specification);
    return composeValidation(Future.succeededFuture(env), OpenAPIValidator::validateSpecification).map(chain -> null);

  }

  /**
   * Check that a composed specification is right. A composed specification is an
   * object where each field is a specification.
   *
   * @param codePrefix    the prefix of the code to use for the error message.
   * @param vertx         the event bus infrastructure to use.
   * @param specification to validate
   *
   * @return the future that inform if the specification is right or not.
   *
   * @see #validateSpecification(String, Vertx, JsonObject)
   */
  public static final Future<Void> validateComposedSpecification(final String codePrefix, final Vertx vertx,
      final JsonObject specification) {

    return composeValidation(Future.succeededFuture(new SpecificationEnvironment(codePrefix, vertx, specification)),
        OpenAPIValidator::validateComposedSpecification).map(env -> null);

  }

  /**
   * Check that a value follows an specification.
   *
   * @param codePrefix    the prefix of the code to use for the error message.
   * @param vertx         the event bus infrastructure to use.
   * @param specification that the value has to follow.
   * @param value         to validate. This value can be modified on the
   *                      validation process, for this reason the future return a
   *                      new value.
   *
   * @return the future with the validated value or an error if it is not valid.
   */
  @SuppressWarnings("unchecked")
  public static final <T> Future<T> validateValue(final String codePrefix, final Vertx vertx,
      final JsonObject specification, final T value) {

    return composeValidation(Future.succeededFuture(new ValueEnvironment(codePrefix, vertx, specification, value)),
        OpenAPIValidator::validateValue).map(env -> (T) env.value);

  }

  /**
   * The cache of reference specifications.
   */
  private static final WeakHashMap<String, JsonObject> references = new WeakHashMap<>();

  /**
   * The prefix before the reference name.
   */
  private static final String SCHEMAS_PREFIX = "#/components/schemas/";

  /**
   * The name of the resource with the common models.
   */
  private static final String COMMON_MODELS_RESOURCE_NAME = "wenet-models-openapi.yaml";

  /**
   * The environment used to do the validation.
   */
  private static abstract class Environment {

    /**
     * The event bus that is using.
     */
    public Vertx vertx;

    /**
     * The prefix of the code to use for the error message.
     */
    public String codePrefix;

    /**
     * The specification to use in the validation.
     */
    public JsonObject specification;

    /**
     * Create a new environment.
     *
     * @param codePrefix    the prefix of the code to use for the error message.
     * @param vertx         the event bus infrastructure to use.
     * @param specification to use in the validation process.
     */
    public Environment(final String codePrefix, final Vertx vertx, final JsonObject specification) {

      this.codePrefix = codePrefix;
      this.vertx = vertx;
      this.specification = specification;

    }

    /**
     * Create an exception that explains why the environment is not valid.
     *
     * @param message a brief description of the error to be read by a human.
     *
     * @return the exception with the message.
     */
    public ValidationErrorException notValid(final String message) {

      return this.notValid("", message, null);
    }

    /**
     * Create an exception that explains why the environment is not valid.
     *
     * @param message a brief description of the error to be read by a human.
     * @param cause   because the model is not right.
     *
     * @return the exception with the message and the cause.
     */
    public ValidationErrorException notValid(final String message, final Throwable cause) {

      return new ValidationErrorException(this.codePrefix, message, cause);
    }

    /**
     * Create an exception that explains why the environment is not valid.
     *
     * @param field   prefix to append to the code prefix.
     * @param message a brief description of the error to be read by a human.
     *
     * @return the exception with the message and the cause.
     */
    public ValidationErrorException notValid(final String field, final String message) {

      return this.notValid(field, message, null);
    }

    /**
     * Create an exception that explains why the environment is not valid.
     *
     * @param field   prefix to append to the code prefix.
     * @param message a brief description of the error to be read by a human.
     * @param cause   because the model is not right.
     *
     * @return the exception with the message and the cause.
     */
    public ValidationErrorException notValid(final String field, final String message, final Throwable cause) {

      return new ValidationErrorException(this.prefixFor(field), message, cause);

    }

    /**
     * Create an exception that explains why the environment is not valid.
     *
     * @param index   of the prefix value that is not valid.
     * @param message a brief description of the error to be read by a human.
     *
     * @return the exception with the message and the cause.
     */
    public ValidationErrorException notValid(final int index, final String message) {

      return this.notValid(index, message, null);

    }

    /**
     * Create an exception that explains why the environment is not valid.
     *
     * @param index   of the prefix value that is not valid.
     * @param message a brief description of the error to be read by a human.
     * @param cause   because the model is not right.
     *
     * @return the exception with the message and the cause.
     */
    public ValidationErrorException notValid(final int index, final String message, final Throwable cause) {

      return new ValidationErrorException(this.prefixFor(index), message, cause);

    }

    /**
     * Create the prefix for a field.
     *
     * @param field prefix to append to the code prefix.
     *
     * @return the code prefix to use for the field.
     */
    public String prefixFor(final String field) {

      if (field == null || field.isEmpty()) {

        return this.codePrefix;

      } else if (this.codePrefix != null) {

        return this.codePrefix + "." + field;

      } else {

        return field;
      }

    }

    /**
     * Create the prefix for an index of the current prefix.
     *
     * @param index of the element.
     *
     * @return the code prefix to use for the field.
     */
    public String prefixFor(final int index) {

      final var suffix = "[" + index + "]";
      if (this.codePrefix != null) {

        return this.codePrefix + suffix;

      } else {

        return suffix;
      }

    }

    /**
     * Obtain the schema defined by a reference.
     *
     * @param ref reference of the schema to obtain.
     *
     * @return the future with the schema.
     */
    public Future<JsonObject> obtainSchemaFor(@NotNull final String ref) {

      final var schema = references.get(ref);
      if (schema instanceof JsonObject) {

        return Future.succeededFuture(schema);

      } else {

        final var index = ref.lastIndexOf(SCHEMAS_PREFIX);
        if (index < 0) {

          return Future.failedFuture(this.notValid("$ref", "Not specified the schema path."));

        } else {

          final var name = ref.substring(index + SCHEMAS_PREFIX.length());
          final var path = ref.substring(0, index);
          String url = null;
          if (index == 0) {

            url = OpenAPIValidator.class.getClassLoader().getResource(COMMON_MODELS_RESOURCE_NAME).toString();

          } else {

            url = ref.substring(0, index);
          }
          return this.load(url).compose(resource -> {

            final Promise<JsonObject> promise = Promise.promise();
            final var schemas = resource.getJsonObject("components", new JsonObject()).getJsonObject("schemas",
                new JsonObject());
            for (final var field : schemas.fieldNames()) {

              final var value = schemas.getValue(field);
              if (value instanceof JsonObject) {

                final var newSchema = (JsonObject) value;
                references.put(path + SCHEMAS_PREFIX + field, newSchema);
                if (field.equals(name)) {

                  promise.complete(newSchema);
                }
              }

            }

            promise.tryFail(this.notValid("$ref", "Not found schema '" + name + "'."));

            return promise.future();

          });

        }
      }
    }

    /**
     * Load the json define on the specified path.
     *
     * @param remote path/URL to the remote resource.
     *
     * @return the future with the remote resource.
     */
    private Future<JsonObject> load(final String remote) {

      return this.vertx.executeBlocking(promise -> {
        try {

          final var url = new URL(remote);
          var content = IOUtils.toString(url.openStream(), Charset.defaultCharset());
          if (!url.getPath().endsWith(".json")) {

            final var mapper = new YAMLMapper();
            final var root = mapper.readTree(url.openStream());
            content = root.toString();
          }
          final var json = new JsonObject(content);
          promise.complete(json);

        } catch (final Throwable cause) {

          promise.fail(this.notValid("$ref", "Cannot obtain the reference.", cause));
        }
      });

    }

  }

  /**
   * Create the environment to validate the value.
   */
  public static class SpecificationEnvironment extends Environment {

    /**
     * The types that has been found.
     */
    public Set<String> foundTypes;

    /**
     * This is true when is a subtype.
     */
    public boolean isSubtype = false;

    /**
     * Create a new environment.
     *
     * @param codePrefix    the prefix of the code to use for the error message.
     * @param vertx         the event bus infrastructure to use.
     * @param specification to use in the validation process.
     */
    public SpecificationEnvironment(final String codePrefix, final Vertx vertx, final JsonObject specification) {

      super(codePrefix, vertx, specification);

    }

    /**
     * Add a type to the founded types.
     *
     * @param typeName name of the founded type.
     */
    public void addFoundType(final String typeName) {

      if (this.isSubtype) {
        if (this.foundTypes == null) {

          this.foundTypes = new HashSet<>();
        }

        this.foundTypes.add(typeName);
      }
    }

    /**
     * Add the found types of a chain.
     *
     * @param chain to get the found types.
     *
     * @return the environment where has added the founded types.
     */
    public SpecificationEnvironment addFoundTypesOf(final SpecificationEnvironment chain) {

      if (chain.foundTypes != null) {

        if (this.foundTypes == null) {

          this.foundTypes = new HashSet<>();
        }

        this.foundTypes.addAll(chain.foundTypes);
      }

      return this;

    }

    /**
     * Create a future with the environment of a sub type.
     *
     * @param field that this is the subtype.
     * @param type  specification for the subtype.
     *
     * @return a future with the child.
     */
    public Future<SpecificationEnvironment> futureChildFor(final String field, final JsonObject type) {

      final var child = new SpecificationEnvironment(this.prefixFor(field), this.vertx, type);
      child.isSubtype = true;
      return Future.succeededFuture(child);

    }

  }

  /**
   * Create the environment to validate the value.
   */
  public static class ValueEnvironment extends Environment {

    /**
     * The value to validate
     */
    public Object value;

    /**
     * The names of the fields that has been defined.
     */
    public Set<String> fieldNames;

    /**
     * This is {@code true} when is checking a compose element.
     */
    public boolean checkingCompose;

    /**
     * This number of succeed compose test.
     */
    public int succedCompose;

    /**
     * This is {@code true} when at least the value has done a match.
     */
    public boolean match;

    /**
     * Create a new environment.
     *
     * @param codePrefix    the prefix of the code to use for the error message.
     * @param vertx         the event bus infrastructure to use.
     * @param specification to use in the validation process.
     * @param value         to validate.
     */
    public ValueEnvironment(final String codePrefix, final Vertx vertx, final JsonObject specification,
        final Object value) {

      super(codePrefix, vertx, specification);

      this.value = value;
      this.fieldNames = null;
      this.checkingCompose = false;
      this.succedCompose = 0;

    }

    /**
     * Add the names of the properties fields.
     *
     * @param properties to get the field names.
     */
    public void addFieldNames(final JsonObject properties) {

      if (this.fieldNames == null) {

        this.fieldNames = new HashSet<>();
      }
      this.fieldNames.addAll(properties.fieldNames());
    }

    /**
     * Add the field names of another environment.
     *
     * @param environment to get the field names.
     */
    public void addFieldNames(final ValueEnvironment environment) {

      if (environment.fieldNames != null) {

        if (this.fieldNames == null) {

          this.fieldNames = new HashSet<>();
        }
        this.fieldNames.addAll(environment.fieldNames);
      }
    }

  }

  /**
   * Compose a value validation.
   *
   * @param future    to compose.
   * @param validator to execute.
   *
   * @return the future with the valid environment.
   */
  private static <T extends Environment> Future<T> composeValidation(final Future<T> future,
      final BiFunction<Promise<T>, T, Future<T>> validator) {

    return future.compose(environment -> {

      final Promise<T> promise = Promise.promise();
      var chain = promise.future();
      try {

        chain = validator.apply(promise, environment);

      } catch (final Throwable cause) {

        promise.fail(environment.notValid("The OpenAPI specification is not valid.", cause));
      }

      promise.tryComplete(environment);
      return chain;

    });

  }

  /**
   * Check that an specification is right.
   *
   * @param promise with the future to use.
   * @param env     environment with the data to validate.
   *
   * @return the future that inform if the specification is right or not.
   */
  static private Future<SpecificationEnvironment> validateSpecification(
      @NotNull final Promise<SpecificationEnvironment> promise, @NotNull final SpecificationEnvironment env) {

    var future = promise.future();
    if (env.specification == null) {

      promise.fail(env.notValid("The OpenAPI specification can not be null."));

    } else if (!env.specification.isEmpty()) {

      if (!env.specification.containsKey("type") && !env.specification.containsKey("oneOf")
          && !env.specification.containsKey("anyOf") && !env.specification.containsKey("allOf")
          && !env.specification.containsKey("$ref")) {

        promise.fail(env.notValid("You must define the 'type'."));

      } else {

        try {

          for (final var fieldName : env.specification.fieldNames()) {

            switch (fieldName) {
            case "type":
              checkType(env);
              break;
            case "multipleOf":
              checkMultipleOf(env);
              break;
            case "minimum":
              checkMinimum(env);
              break;
            case "maximum":
              checkMaximum(env);
              break;
            case "exclusiveMinimum":
            case "exclusiveMaximum":
              checkInstanceOfFor(fieldName, Boolean.class, env);
              checkIfTypeIsOneOf(fieldName, env, "integer", "number");
              break;
            case "nullable":
              checkInstanceOfFor(fieldName, Boolean.class, env);
              break;
            case "uniqueItems":
              checkInstanceOfFor(fieldName, Boolean.class, env);
              checkIfTypeIs(fieldName, env, "array");
              break;
            case "default":
              future = checkDefault(future, env);
              break;
            case "properties":
              future = checkProperties(future, env);
              break;
            case "items":
              future = checkItems(future, env);
              break;
            case "required":
              checkRequired(env);
              break;
            case "minItems":
              checkMinOf(fieldName, "array", env);
              break;
            case "maxItems":
              checkMaxOf(fieldName, "minItems", "array", env);
              break;
            case "minProperties":
              checkMinOf(fieldName, "object", env);
              break;
            case "maxProperties":
              checkMaxOf(fieldName, "minProperties", "object", env);
              break;
            case "minLength":
              checkMinOf(fieldName, "string", env);
              break;
            case "maxLength":
              checkMaxOf(fieldName, "minLength", "string", env);
              break;
            case "enum":
              future = checkEnum(future, env);
              break;
            case "pattern":
              checkPattern(env);
              break;
            case "additionalProperties":
              future = checkAdditionalProperties(future, env);
              break;
            case "oneOf":
            case "anyOf":
            case "allOf":
              future = checkComposedType(fieldName, future, env);
              break;
            case "not":
              future = checkNot(future, env);
              break;
            case "$ref":
              future = checkRef(future, env);
              break;
            case "description":
            case "title":
              checkInstanceOfFor(fieldName, String.class, env);
              break;
            case "example":
            case "examples":
              // Allow free examples because are not used for anything
            case "format":
              // No format is validated so it is unused
              break;
            default:
              throw env.notValid(fieldName, "Unexpected OpenAPI field.");
            }

          }

        } catch (final ValidationErrorException error) {

          promise.fail(error);
        }
      }

    } // else accept any value

    return future;

  }

  /**
   * Check that if the type exist is one of the expected ones.
   *
   * @param field         that require a type.
   * @param env           validation environment to use.
   * @param expectedTypes the expected types for the type if it is defined.
   *
   * @throws ValidationErrorException if it is not an instance of.
   */
  private static void checkIfTypeIsOneOf(@NotNull final String field, @NotNull final SpecificationEnvironment env,
      final String... expectedTypes) throws ValidationErrorException {

    final var type = env.specification.getString("type", null);
    if (type != null) {

      for (final var expectedType : expectedTypes) {

        if (type.equals(expectedType)) {

          return;
        }
      }

      throw env.notValid(field,
          "You only can use this feature if 'type' is one of '" + Arrays.toString(expectedTypes) + "'.\"");
    }

  }

  /**
   * Check that if the type exist is one of the expected ones.
   *
   * @param field        that require a type.
   * @param env          validation environment to use.
   * @param expectedType the expected types for the type if it is defined.
   *
   * @throws ValidationErrorException if it is not an instance of.
   */
  private static void checkIfTypeIs(@NotNull final String field, @NotNull final SpecificationEnvironment env,
      final String expectedType) throws ValidationErrorException {

    final var type = env.specification.getString("type", null);
    if (type != null && !type.equals(expectedType)) {

      throw env.notValid(field, "You only can use this feature if 'type'='" + type + "'.\"");
    }

  }

  /**
   * Check the specification field that indicates that a number is multiple of
   * another.
   *
   * @param env validation environment to use.
   *
   * @throws ValidationErrorException if it is not an instance of.
   */
  private static void checkMultipleOf(@NotNull final SpecificationEnvironment env) throws ValidationErrorException {

    final var value = env.specification.getValue("multipleOf");
    if (value instanceof Number) {

      final var multipleOf = (Number) value;
      if (Double.compare(multipleOf.doubleValue(), 0.0) <= 0) {

        throw env.notValid("multipleOf", "The value must be greater than 0.");

      } else {

        checkIfTypeIsOneOf("multipleOf", env, "integer", "number");
      }

    } else {

      throw env.notValid("multipleOf", "Expecting a 'number' value.");
    }

  }

  /**
   * Check the specification field that a value of the specified clazz.
   *
   * @param field name of the field to compare
   * @param clazz expected value for the value.
   * @param env   validation environment to use.
   *
   * @throws ValidationErrorException if it is not an instance of.
   */
  private static void checkInstanceOfFor(final String field, final Class<?> clazz,
      @NotNull final SpecificationEnvironment env) throws ValidationErrorException {

    final var value = env.specification.getValue(field);
    if (!clazz.isInstance(value)) {

      throw env.notValid(field, "Expecting a '" + clazz.getSimpleName() + "' value.");
    }

  }

  /**
   * Check the specification field that indicates that a number has to be as
   * minimum of another value.
   *
   * @param env validation environment to use.
   *
   * @throws ValidationErrorException if it is not an instance of.
   */
  private static void checkMinimum(@NotNull final SpecificationEnvironment env) throws ValidationErrorException {

    final var value = env.specification.getValue("minimum");
    if (value instanceof Number) {

      checkIfTypeIsOneOf("minimum", env, "integer", "number");

    } else {

      throw env.notValid("minimum", "Expecting a 'number' value.");
    }

  }

  /**
   * Check the specification field that indicates that a number has to be as
   * maximum of another value.
   *
   * @param env validation environment to use.
   *
   * @throws ValidationErrorException if it is not an instance of.
   */
  private static void checkMaximum(@NotNull final SpecificationEnvironment env) throws ValidationErrorException {

    final var value = env.specification.getValue("maximum");
    if (value instanceof Number) {

      final var maximum = (Number) value;
      final var minimum = env.specification.getNumber("minimum", 0.0d);
      final var exclusiveMinimum = env.specification.getBoolean("exclusiveMinimum", false);
      final var compare = Double.compare(minimum.doubleValue(), maximum.doubleValue());
      if (exclusiveMinimum) {

        if (compare >= 0) {

          throw env.notValid("maximum", "The value must be greater than '" + minimum + "'.");
        }

      } else if (compare > 0) {

        throw env.notValid("maximum", "The value must be equal to or greater than '" + minimum + "'.");

      } else {

        checkIfTypeIsOneOf("maximum", env, "integer", "number");

      }

    } else {

      throw env.notValid("maximum", "Expecting a 'number' value.");
    }

  }

  /**
   * Check that a composed specification is right. A composed specification is an
   * object where each field is a specification.
   *
   * @param promise that manage the future.
   * @param env     environment with the data to validate.
   *
   * @return the future that inform if the specification is right or not.
   */
  static private Future<SpecificationEnvironment> validateComposedSpecification(
      @NotNull final Promise<SpecificationEnvironment> promise, @NotNull final SpecificationEnvironment env) {

    var future = promise.future();
    if (env.specification == null) {

      promise.fail(env.notValid("The OpenAPI specification can not be null."));

    } else {

      for (final var propertyName : env.specification.fieldNames()) {

        final var value = env.specification.getValue(propertyName);
        if (value instanceof JsonObject) {

          final var propertySpecification = (JsonObject) value;
          future = future
              .compose(chain -> validateSpecification(env.prefixFor(propertyName), env.vertx, propertySpecification)
                  .map(empty -> chain));

        } else {

          promise.fail(env.notValid(propertyName, "Expecting a JSON object."));
          break;

        }
      }

    }
    return future;

  }

  /**
   * Check the specification field that indicates the required fields is valid.
   *
   * @param env validation environment to use.
   *
   * @throws ValidationErrorException if it is not an instance of.
   */
  private static void checkRequired(@NotNull final SpecificationEnvironment env) throws ValidationErrorException {

    final var value = env.specification.getValue("required");
    if (value instanceof JsonArray) {

      checkIfTypeIs("required", env, "object");

      final var array = (JsonArray) value;
      final var max = array.size();
      if (max == 0) {

        throw env.notValid("required", "You must define at least one required field.");

      } else {

        for (var i = 0; i < max; i++) {

          final var element = array.getValue(i);
          if (!(element instanceof String)) {

            throw env.notValid("required[" + i + "]", "Expecting a 'string' value.");
          }

        }

      }

    } else {

      throw env.notValid("required", "Expecting a 'JsonArray' value.");
    }

  }

  /**
   * Check the specification field that indicates that a minimum field of a type
   * is valid.
   *
   * @param minName      name of the minimum property to check.
   * @param expectedType for the minimum field.
   * @param env          validation environment to use.
   *
   * @throws ValidationErrorException if it is not an instance of.
   */
  private static void checkMinOf(@NotNull final String minName, @NotNull final String expectedType,
      @NotNull final SpecificationEnvironment env) throws ValidationErrorException {

    final var value = env.specification.getValue(minName);
    if (value instanceof Integer || value instanceof Long) {

      if (((Number) value).longValue() < 0) {

        throw env.notValid(minName, "The value must be equal to or greater than '0'.");

      } else {

        checkIfTypeIs(minName, env, expectedType);
      }

    } else {

      throw env.notValid(minName, "Expecting an 'integer' value.");
    }

  }

  /**
   * Check the specification field that indicates that a maximum field of a type
   * is valid.
   *
   * @param maxName      name of the maximum property to check.
   * @param minName      name of the minimum property to check.
   * @param expectedType for the minimum field.
   * @param env          validation environment to use.
   *
   * @throws ValidationErrorException if it is not an instance of.
   */
  private static void checkMaxOf(@NotNull final String maxName, @NotNull final String minName,
      @NotNull final String expectedType, @NotNull final SpecificationEnvironment env) throws ValidationErrorException {

    final var value = env.specification.getValue(maxName);
    if (value instanceof Integer || value instanceof Long) {

      final var max = ((Number) value).longValue();
      final var min = env.specification.getLong(minName, 0l);
      if (max < min) {

        throw env.notValid(maxName, "The value must be equal to or greater than '" + min + "'.");

      } else {

        checkIfTypeIs(maxName, env, expectedType);

      }

    } else {

      throw env.notValid(maxName, "Expecting an 'integer' value.");
    }

  }

  /**
   * Check the specification field that indicates the pattern for a string value.
   *
   * @param env validation environment to use.
   *
   * @throws ValidationErrorException if it is not an instance of.
   */
  private static void checkPattern(@NotNull final SpecificationEnvironment env) throws ValidationErrorException {

    final var value = env.specification.getValue("pattern");
    if (value instanceof String) {

      try {

        Pattern.compile((String) value);
        checkIfTypeIs("pattern", env, "string");

      } catch (final Throwable cause) {

        throw env.notValid("pattern", "You pattern is not right formatted.", cause);

      }

    } else {

      throw env.notValid("pattern", "Expecting a 'string' value.");
    }

  }

  /**
   * Check a composed type.
   *
   * @param field  name of the composed type.
   * @param future to compose.
   * @param env    validation environment to use.
   *
   * @throws ValidationErrorException if it is not an instance of.
   *
   * @return the future to check the composed type.
   */
  static Future<SpecificationEnvironment> checkComposedType(@NotNull final String field,
      @NotNull Future<SpecificationEnvironment> future, @NotNull final SpecificationEnvironment env)
      throws ValidationErrorException {

    final var value = env.specification.getValue(field);
    if (value instanceof JsonArray) {

      final var array = (JsonArray) value;
      final var max = array.size();
      if (max == 0) {

        throw env.notValid(field, "Expecting at least one type definition.");

      } else {

        for (var i = 0; i < max; i++) {

          final var elementPrefix = field + "[" + i + "]";
          final var elementValue = array.getValue(i);
          if (elementValue instanceof JsonObject) {

            future = composeValidation(env.futureChildFor(elementPrefix, (JsonObject) elementValue),
                OpenAPIValidator::validateSpecification).map(chain -> env.addFoundTypesOf(chain));

          } else {

            throw env.notValid(elementPrefix, "Expecting a 'JsonObject' value.");
          }
        }

        if ("allOf".equals(field)) {

          future = future.compose(chain -> {

            final Promise<SpecificationEnvironment> promise = Promise.promise();
            if (chain.foundTypes != null && chain.foundTypes.size() > 1) {

              promise.fail(
                  chain.notValid(field, "This specification does not allow any value, because you have mixed types."));

            } else {

              promise.complete(chain);
            }

            return promise.future();

          });

        }
      }

    } else {

      throw env.notValid(field, "Expecting a 'JsonArray' value.");
    }

    return future;

  }

  /**
   * Check that the enum filed is right.
   *
   * @param future to compose.
   * @param env    validation environment to use.
   *
   * @throws ValidationErrorException if it is not an instance of.
   *
   * @return the future to check the enum values.
   */
  private static Future<SpecificationEnvironment> checkEnum(@NotNull Future<SpecificationEnvironment> future,
      @NotNull final SpecificationEnvironment env) throws ValidationErrorException {

    final var value = env.specification.getValue("enum");
    if (value instanceof JsonArray) {

      final var array = (JsonArray) value;
      final var max = array.size();
      if (max == 0) {

        throw env.notValid("enum", "Expecting at least one value in the array.");

      } else {

        for (var i = 0; i < max; i++) {

          final var element = array.getValue(i);
          for (var j = i + 1; j < max; j++) {

            final var other = array.getValue(j);
            if (element == other || element != null && element.equals(other)) {

              throw env.notValid("enum[" + j + "]", "Duplicated enum value.");
            }

          }

          final var elementPrefix = "enum[" + i + "]";
          future = future
              .compose(chain -> validateValue(chain.prefixFor(elementPrefix), chain.vertx, chain.specification, element)
                  .map(any -> chain));
        }

      }

    } else {

      throw env.notValid("enum", "Expecting a 'JsonArray' value.");
    }

    return future;
  }

  /**
   * Check that the addition properties field is right.
   *
   * @param future to compose.
   * @param env    validation environment to use.
   *
   * @throws ValidationErrorException if it is not an instance of.
   *
   * @return the future to check the additional properties.
   */
  private static Future<SpecificationEnvironment> checkAdditionalProperties(
      @NotNull Future<SpecificationEnvironment> future, @NotNull final SpecificationEnvironment env)
      throws ValidationErrorException {

    if (env.specification.containsKey("properties")) {

      throw env.notValid("additionalProperties", "You cannot mix 'additionalProperties' with some  'properties'.");
    }

    final var value = env.specification.getValue("additionalProperties");
    if (value instanceof Boolean) {

      if (((Boolean) value).booleanValue()) {

        checkIfTypeIs("additionalProperties", env, "object");
      }

    } else if (value instanceof JsonObject) {

      final var additionalProperties = (JsonObject) value;
      future = future.compose(
          chain -> validateSpecification(chain.prefixFor("additionalProperties"), chain.vertx, additionalProperties)
              .map(any -> chain));

    } else {

      throw env.notValid("additionalProperties", "Expecting a 'JsonObject' or 'boolean' value.");
    }

    return future;
  }

  /**
   * Check that the 'not' field is right.
   *
   * @param future to compose.
   * @param env    validation environment to use.
   *
   * @throws ValidationErrorException if it is not an instance of.
   *
   * @return the future to check the 'not'.
   */
  private static Future<SpecificationEnvironment> checkNot(@NotNull Future<SpecificationEnvironment> future,
      @NotNull final SpecificationEnvironment env) throws ValidationErrorException {

    final var value = env.specification.getValue("not");
    if (value instanceof JsonObject) {

      final var not = (JsonObject) value;
      future = future
          .compose(chain -> validateSpecification(chain.prefixFor("not"), chain.vertx, not).map(any -> chain));

    } else {

      throw env.notValid("not", "Expecting a 'JsonObject' value.");
    }

    return future;
  }

  /**
   * Check that the reference to another type is right.
   *
   * @param future to compose.
   * @param env    validation environment to use.
   *
   * @throws ValidationErrorException if it is not an instance of.
   *
   * @return the future to check the 'not'.
   */
  private static Future<SpecificationEnvironment> checkRef(@NotNull Future<SpecificationEnvironment> future,
      @NotNull final SpecificationEnvironment env) throws ValidationErrorException {

    final var value = env.specification.getValue("$ref");
    if (value instanceof String) {

      final var ref = (String) value;
      future = future.compose(chain -> env.obtainSchemaFor(ref).map(any -> chain));

    } else {

      throw env.notValid("$ref", "Expecting a 'string' value.");
    }

    return future;
  }

  /**
   * Check that the default value is right.
   *
   * @param future to concatenate.
   * @param env    validation environment to use.
   *
   * @throws ValidationErrorException if it is not an instance of.
   *
   * @return the future that say if the default value is valid or not.
   */
  private static Future<SpecificationEnvironment> checkDefault(@NotNull Future<SpecificationEnvironment> future,
      @NotNull final SpecificationEnvironment env) throws ValidationErrorException {

    final var value = env.specification.getValue("default");
    if (value instanceof String) {

      try {

        final var decoded = Json.decodeValue((String) value);
        future = future
            .compose(chain -> validateValue(null, chain.vertx, chain.specification, decoded).transform(validation -> {

              final Promise<SpecificationEnvironment> promise = Promise.promise();
              if (validation.failed()) {

                final var cause = (ValidationErrorException) validation.cause();
                var prefix = "default";
                final var code = cause.getCode();
                if (code != null) {

                  prefix += code;
                }
                promise.fail(chain.notValid(prefix, cause.getMessage()));

              } else {

                promise.complete(chain);
              }

              return promise.future();
            }));

      } catch (final Throwable cause) {

        throw env.notValid("default", "Bad encoded JSON value.", cause);
      }

    } else {

      throw env.notValid("default", "Expecting a 'string' value.");

    }

    return future;

  }

  /**
   * Check that the properties value is right.
   *
   * @param future to compose.
   * @param env    validation environment to use.
   *
   * @throws ValidationErrorException if it is not an instance of.
   *
   * @return the future that say if the properties value is valid or not.
   */
  private static Future<SpecificationEnvironment> checkProperties(@NotNull Future<SpecificationEnvironment> future,
      @NotNull final SpecificationEnvironment env) throws ValidationErrorException {

    final var value = env.specification.getValue("properties");
    if (value instanceof JsonObject) {

      checkIfTypeIs("properties", env, "object");

      final var properties = (JsonObject) value;
      for (final var propertyName : properties.fieldNames()) {

        final var propertyValue = properties.getValue(propertyName);
        final var propertyPrefix = "properties." + propertyName;
        if (propertyValue instanceof JsonObject) {

          final var propertySpecification = (JsonObject) propertyValue;
          future = future.compose(
              chain -> validateSpecification(chain.prefixFor(propertyPrefix), chain.vertx, propertySpecification)
                  .map(empty -> chain));

        } else {

          throw env.notValid(propertyPrefix, "Expecting a 'JsonObject' value.");
        }
      }

    } else {

      throw env.notValid("properties", "Expecting a 'JsonObject' value.");

    }

    return future;
  }

  /**
   * Check that the items value is right.
   *
   * @param future to compose.
   * @param env    validation environment to use.
   *
   * @throws ValidationErrorException if it is not an instance of.
   *
   * @return the future that say if the items value is valid or not.
   */
  private static Future<SpecificationEnvironment> checkItems(@NotNull Future<SpecificationEnvironment> future,
      @NotNull final SpecificationEnvironment env) throws ValidationErrorException {

    final var value = env.specification.getValue("items");
    if (value instanceof JsonObject) {

      checkIfTypeIs("items", env, "array");

      future = future.compose(chain -> validateSpecification(chain.prefixFor("items"), chain.vertx, (JsonObject) value)
          .map(empty -> chain));

    } else {

      throw env.notValid("items", "Expecting a 'JsonObject' value.");

    }

    return future;
  }

  /**
   * Check the type of an specification.
   *
   * @param env validation environment to use.
   *
   * @throws ValidationErrorException if the specification is not valid.
   */
  private static void checkType(@NotNull final SpecificationEnvironment env) throws ValidationErrorException {

    final var type = env.specification.getValue("type");
    if (type instanceof String) {

      final var typeName = (String) type;
      switch (typeName) {
      case "boolean":
      case "number":
      case "integer":
      case "string":
      case "object":
        // nothing more to check
        break;
      case "array":
        if (!env.specification.containsKey("items")) {

          throw env.notValid("type", "You must define the field 'items' when the 'type' = 'array'.");
        }
        break;
      default:
        throw env.notValid("type", "Undefined 'type' value.");
      }

      env.addFoundType(typeName);

    } else {

      throw env.notValid("type", "Expecting a 'string' value.");
    }

  }

  /**
   * Validate that the value has the required properties.
   *
   * @param promise     to inform of the validation.
   * @param environment to use for the validation.
   *
   * @return the future that says if the value is valid or not.
   */
  static Future<ValueEnvironment> validateValue(@NotNull final Promise<ValueEnvironment> promise,
      @NotNull final ValueEnvironment environment) {

    var future = promise.future();
    if (!environment.specification.isEmpty()) {

      if (environment.value == null) {

        if (environment.specification.containsKey("default")) {

          final var defaultValue = environment.specification.getString("default");
          environment.value = Json.decodeValue(defaultValue);
          if (environment.value != null) {

            return validateValue(promise, environment);
          }
        }

        if (!environment.specification.getBoolean("nullable", false)) {

          promise.fail(environment.notValid("Not allowed a 'null' value."));

        } else {

          environment.match = true;
        }

      } else {

        if (environment.specification.containsKey("oneOf")) {

          future = composeValidation(future, OpenAPIValidator::validateOneOfValue);

        }

        if (environment.specification.containsKey("anyOf")) {

          future = composeValidation(future, OpenAPIValidator::validateAnyOfValue);

        }

        if (environment.specification.containsKey("allOf")) {

          future = composeValidation(future, OpenAPIValidator::validateAllOfValue);

        }

        if (environment.specification.containsKey("type")) {

          future = composeValidation(future, OpenAPIValidator::validateTypeValue);

        }

        if (environment.specification.containsKey("enum")) {

          future = composeValidation(future, OpenAPIValidator::validateEnumValue);

        }
        if (environment.specification.containsKey("required")) {

          future = composeValidation(future, OpenAPIValidator::validateRequiredValue);

        }

        if (!environment.checkingCompose) {

          future = composeValidation(future, OpenAPIValidator::validateUndefinedFieldsInValue);

        }

      }

    } // else accept any value

    return future;

  }

  /**
   * Validate that the value has the required properties.
   *
   * @param promise     to inform of the validation.
   * @param environment to use for the validation.
   *
   * @return the future that says if the value is valid or not.
   */
  static Future<ValueEnvironment> validateRequiredValue(@NotNull final Promise<ValueEnvironment> promise,
      @NotNull final ValueEnvironment environment) {

    final var object = (JsonObject) environment.value;
    final var required = environment.specification.getJsonArray("required");
    final var max = required.size();
    for (var i = 0; i < max; i++) {

      final var key = required.getString(i);
      if (!object.containsKey(key)) {

        promise.fail(environment.notValid(key, "The field '" + key + "' is required."));
        break;
      }

    }

    return promise.future();

  }

  /**
   * Validate that the value is one of the enumerated ones.
   *
   * @param promise to inform of the validation.
   * @param env     environment to use for the validation.
   *
   * @return the future that says if the value is valid or not.
   */
  static Future<ValueEnvironment> validateEnumValue(@NotNull final Promise<ValueEnvironment> promise,
      @NotNull final ValueEnvironment env) {

    final var enumValues = env.specification.getJsonArray("enum");
    if (!enumValues.contains(env.value)) {

      promise.fail(env.notValid("Expecting a value in " + enumValues + "."));

    }

    return promise.future();

  }

  /**
   * Validate that the value is one of the specified values.
   *
   * @param promise     to inform of the validation.
   * @param environment to use for the validation.
   *
   * @return the future that says if the value is valid or not.
   */
  static private Future<ValueEnvironment> validateOneOfValue(@NotNull final Promise<ValueEnvironment> promise,
      @NotNull final ValueEnvironment environment) {

    var future = promise.future();
    final var oneOf = environment.specification.getJsonArray("oneOf");
    final var max = oneOf.size();
    for (var i = 0; i < max; i++) {

      final var type = oneOf.getJsonObject(i);
      final var subEnvironment = new ValueEnvironment(environment.codePrefix, environment.vertx, type,
          environment.value);
      // this not a checking compose because only one type has to be valid
      subEnvironment.checkingCompose = true;
      future = future
          .compose(chain -> composeValidation(Future.succeededFuture(subEnvironment), OpenAPIValidator::validateValue)
              .transform(subResult -> {

                if (subResult.succeeded()) {

                  final var result = subResult.result();
                  if (result.match) {

                    chain.succedCompose++;
                    if (chain.succedCompose > 1) {

                      return Future
                          .failedFuture(chain.notValid("The value match more that one of the possible values"));

                    } else {

                      chain.value = result.value;
                      chain.addFieldNames(result);

                    }
                  }
                }

                return Future.succeededFuture(chain);
              }));
    }

    future = future.compose(chain -> {

      if (chain.succedCompose == 1) {

        chain.succedCompose = 0;
        return Future.succeededFuture(chain);

      } else {

        return Future.failedFuture(chain.notValid("The value does not match any of the possible values"));
      }

    });

    return future;

  }

  /**
   * Validate that the value is any of the specified values.
   *
   * @param promise     to inform of the validation.
   * @param environment to use for the validation.
   *
   * @return the future that says if the value is valid or not.
   */
  static private Future<ValueEnvironment> validateAnyOfValue(@NotNull final Promise<ValueEnvironment> promise,
      @NotNull final ValueEnvironment environment) {

    var future = promise.future();
    final var anyOf = environment.specification.getJsonArray("anyOf");
    final var max = anyOf.size();
    for (var i = 0; i < max; i++) {

      final var type = anyOf.getJsonObject(i);
      final var subEnvironment = new ValueEnvironment(environment.codePrefix, environment.vertx, type,
          environment.value);
      subEnvironment.checkingCompose = true;
      future = future
          .compose(chain -> composeValidation(Future.succeededFuture(subEnvironment), OpenAPIValidator::validateValue)
              .transform(subResult -> {

                if (subResult.succeeded()) {

                  final var result = subResult.result();
                  chain.value = result.value;
                  chain.succedCompose++;
                  chain.addFieldNames(result);
                }

                return Future.succeededFuture(chain);
              }));
    }

    future = future.compose(chain -> {

      if (chain.succedCompose > 0) {

        chain.succedCompose = 0;
        return Future.succeededFuture(chain);

      } else {

        return Future.failedFuture(chain.notValid("The value does not match any of the possible values"));
      }

    });

    return future;

  }

  /**
   * Validate that the value is all of the specified values.
   *
   * @param promise     to inform of the validation.
   * @param environment to use for the validation.
   *
   * @return the future that says if the value is valid or not.
   */
  static private Future<ValueEnvironment> validateAllOfValue(@NotNull final Promise<ValueEnvironment> promise,
      @NotNull final ValueEnvironment environment) {

    var future = promise.future();
    final var allOf = environment.specification.getJsonArray("allOf");
    final var max = allOf.size();
    for (var i = 0; i < max; i++) {

      final var type = allOf.getJsonObject(i);
      final var subEnvironment = new ValueEnvironment(environment.codePrefix, environment.vertx, type,
          environment.value);
      subEnvironment.checkingCompose = true;
      future = future
          .compose(chain -> composeValidation(Future.succeededFuture(subEnvironment), OpenAPIValidator::validateValue)
              .map(validSubEnvironment -> {
                chain.value = validSubEnvironment.value;
                chain.addFieldNames(validSubEnvironment);
                return chain;
              }));
    }

    return future;

  }

  /**
   * Validate that the value is type of the specified values.
   *
   * @param promise     to inform of the validation.
   * @param environment to use for the validation.
   *
   * @return the future that says if the value is valid or not.
   */
  static private Future<ValueEnvironment> validateTypeValue(@NotNull final Promise<ValueEnvironment> promise,
      @NotNull final ValueEnvironment environment) {

    var future = promise.future();
    final var type = environment.specification.getString("type");
    switch (type) {
    case "boolean":
      future = composeValidation(future, OpenAPIValidator::validateBooleanValue);
      break;
    case "string":
      future = composeValidation(future, OpenAPIValidator::validateStringValue);
      break;
    case "integer":
      future = composeValidation(future, OpenAPIValidator::validateIntegerValue);
      break;
    case "number":
      future = composeValidation(future, OpenAPIValidator::validateNumberValue);
      break;
    case "object":
      future = composeValidation(future, OpenAPIValidator::validateObjectValue);
      break;
    case "array":
      future = composeValidation(future, OpenAPIValidator::validateArrayValue);
      break;
    default:
      promise.fail(environment.notValid("Bad OpenAPI definition with an unexpected 'type' for the value."));

    }

    return future;
  }

  /**
   * Validate that the value is a valid string.
   *
   * @param promise     to inform of the validation.
   * @param environment to use for the validation.
   *
   * @return the future that says if the string is valid or not.
   */
  static Future<ValueEnvironment> validateStringValue(@NotNull final Promise<ValueEnvironment> promise,
      @NotNull final ValueEnvironment environment) {

    if (environment.value instanceof String) {

      final var value = (String) environment.value;
      final var pattern = environment.specification.getString("pattern", null);
      if (pattern != null && !value.matches(pattern)) {

        promise.fail(environment.notValid("The value does not match the pattern."));

      } else {

        final var min = environment.specification.getInteger("minLength", null);
        if (min != null && value.length() < min) {

          promise.fail(environment.notValid("The value has to have a length equal or greater than " + min + "."));

        } else {

          final var max = environment.specification.getInteger("maxLength", null);
          if (max != null && value.length() > max) {

            promise.fail(environment.notValid("The value has to have a length equal or less than " + max + "."));

          } else {

            environment.match = true;
          }
        }
      }

    } else {

      promise.fail(environment.notValid("Expecting a 'string' value."));
    }

    return promise.future();
  }

  /**
   * Validate that the value is a valid boolean.
   *
   * @param promise     to inform of the validation.
   * @param environment to use for the validation.
   *
   * @return the future that says if the boolean is valid or not.
   */
  static Future<ValueEnvironment> validateBooleanValue(@NotNull final Promise<ValueEnvironment> promise,
      @NotNull final ValueEnvironment environment) {

    if (environment.value instanceof Boolean) {

      environment.match = true;

    } else {

      promise.fail(environment.notValid("Expecting a 'boolean' value."));

    }

    return promise.future();
  }

  /**
   * Validate that the value is a valid integer.
   *
   * @param promise     to inform of the validation.
   * @param environment to use for the validation.
   *
   * @return the future that says if the integer is valid or not.
   */
  static Future<ValueEnvironment> validateIntegerValue(@NotNull final Promise<ValueEnvironment> promise,
      @NotNull final ValueEnvironment environment) {

    if (environment.value instanceof Integer || environment.value instanceof Long) {

      return validateNumberValue(promise, environment);

    } else {

      promise.fail(environment.notValid("Expecting an 'integer' value."));
      return promise.future();
    }

  }

  /**
   * Validate that the value is a valid number.
   *
   * @param promise     to inform of the validation.
   * @param environment to use for the validation.
   *
   * @return the future that says if the number is valid or not.
   */
  static Future<ValueEnvironment> validateNumberValue(@NotNull final Promise<ValueEnvironment> promise,
      @NotNull final ValueEnvironment environment) {

    if (environment.value instanceof Number) {

      final var value = (Number) environment.value;
      var error = false;
      final var bigValue = new BigDecimal(value.doubleValue());
      final var min = environment.specification.getNumber("minimum", null);
      if (min != null) {

        final var bigMin = new BigDecimal(min.doubleValue());
        final var compare = bigValue.compareTo(bigMin);
        final var excludeMin = environment.specification.getBoolean("exclusiveMinimum", false);
        if (excludeMin && compare < 1) {

          promise.fail(environment.notValid("The value must be greater than the minimum."));
          error = true;

        } else if (!excludeMin && compare < 0) {

          promise.fail(environment.notValid("The value must be equals or greater than the minimum."));
          error = true;
        }
      }

      final var max = environment.specification.getNumber("maximum", null);
      if (max != null) {

        final var bigMax = new BigDecimal(max.doubleValue());
        final var compare = bigValue.compareTo(bigMax);
        final var excludeMax = environment.specification.getBoolean("exclusiveMaximum", false);
        if (excludeMax && compare > -1) {

          promise.tryFail(environment.notValid("The value must be less than the maximum."));
          error = true;

        } else if (!excludeMax && compare > 0) {

          promise.tryFail(environment.notValid("The value must be equals or less than the maximum."));
          error = true;
        }

      }

      final var multipleOf = environment.specification.getNumber("multipleOf", null);
      if (multipleOf != null) {

        final var bigMultipleOf = new BigDecimal(multipleOf.doubleValue());
        final var remainder = bigValue.remainder(bigMultipleOf);
        final var precision = new BigDecimal(0.000000001);
        if (remainder.abs().max(precision).compareTo(precision) > 0) {
          // it is not a multiple of
          promise.tryFail(environment.notValid("The value must be a multiple of '" + multipleOf + "'."));
          error = true;
        }

      }

      if (!error) {

        environment.match = true;
      }

    } else {

      promise.fail(environment.notValid("Expecting a 'number' value."));

    }

    return promise.future();
  }

  /**
   * Validate that the value is a valid array.
   *
   * @param promise     to inform of the validation.
   * @param environment to use for the validation.
   *
   * @return the future that says if the array is valid or not.
   */
  static Future<ValueEnvironment> validateArrayValue(@NotNull final Promise<ValueEnvironment> promise,
      @NotNull final ValueEnvironment environment) {

    var future = promise.future();
    if (environment.value instanceof JsonArray) {

      final var value = (JsonArray) environment.value;
      final var max = value.size();
      final var minItems = environment.specification.getNumber("minItems", null);
      if (minItems != null && minItems.intValue() > max) {

        promise.fail(environment.notValid("The value require at least " + minItems + " items."));

      } else {

        final var maxItems = environment.specification.getNumber("maxItems", null);
        if (maxItems != null && maxItems.intValue() < max) {

          promise.fail(environment.notValid("The value require at maximum " + maxItems + " items."));

        } else {

          final var items = environment.specification.getJsonObject("items");
          for (var i = 0; i < max; i++) {

            final var pos = i;
            final var element = value.getValue(pos);
            future = future
                .compose(chain -> validateValue(chain.prefixFor(pos), chain.vertx, items, element).map(validElement -> {

                  final var chainArray = (JsonArray) chain.value;
                  if (chainArray.getValue(pos) != validElement) {

                    chainArray.set(pos, validElement);
                  }
                  chain.match = true;
                  return chain;
                }));

          }

          if (environment.specification.getBoolean("uniqueItems", false)) {

            future = composeValidation(future, OpenAPIValidator::validateArrayUniqueItemsValue);
          }

        }
      }

    } else {

      promise.fail(environment.notValid("Expecting a 'JsonArray' value."));

    }
    return future;
  }

  /**
   * Validate that the values or an array are unique.
   *
   * @param promise     to inform of the validation.
   * @param environment to use for the validation.
   *
   * @return the future that says if the array values are uniques or not.
   */
  static Future<ValueEnvironment> validateArrayUniqueItemsValue(@NotNull final Promise<ValueEnvironment> promise,
      @NotNull final ValueEnvironment environment) {

    final var value = (JsonArray) environment.value;
    final var max = value.size();

    I: for (var i = 0; i < max; i++) {

      for (var j = i + 1; j < max; j++) {

        final var a = value.getValue(i);
        final var b = value.getValue(j);
        if (a == null && b == null || a != null && a.equals(b)) {

          promise.fail(environment.notValid(j, "Duplicated value"));
          break I;
        }
      }

    }

    return promise.future();
  }

  /**
   * Validate that the value is a valid object.
   *
   * @param promise     to inform of the validation.
   * @param environment to use for the validation.
   *
   * @return the future that says if the value is valid object or not.
   */
  static Future<ValueEnvironment> validateObjectValue(@NotNull final Promise<ValueEnvironment> promise,
      @NotNull final ValueEnvironment environment) {

    var future = promise.future();
    if (environment.value instanceof JsonObject) {

      final var value = (JsonObject) environment.value;
      final var properties = environment.specification.getJsonObject("properties");
      if (properties != null) {

        for (final var field : properties.fieldNames()) {

          final var fieldType = properties.getJsonObject(field);
          if (value.containsKey(field)) {

            environment.match = true;
            final var fieldValue = value.getValue(field);
            future = future.compose(
                chain -> validateValue(chain.prefixFor(field), chain.vertx, fieldType, fieldValue).map(validValue -> {

                  if (((JsonObject) chain.value).getValue(field) != validValue) {

                    ((JsonObject) chain.value).put(field, validValue);
                  }

                  return chain;
                }));

          } else {
            // the fields are optional by default, but if they are not defined and has a
            // default value it has to be set
            future = future.compose(
                chain -> validateValue(chain.prefixFor(field), chain.vertx, fieldType, null).transform(validated -> {

                  if (validated.succeeded()) {

                    final var result = validated.result();
                    if (result != null) {

                      ((JsonObject) chain.value).put(field, result);
                    }
                    chain.match = true;
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

        final var additionalProperties = environment.specification.getValue("additionalProperties");
        if (additionalProperties instanceof JsonObject) {

          for (final var field : value.fieldNames()) {

            final var fieldValue = value.getValue(field);
            future = future.compose(chain -> validateValue(chain.prefixFor(field), chain.vertx,
                (JsonObject) additionalProperties, fieldValue).map(validValue -> {

                  if (fieldValue != validValue) {

                    ((JsonObject) chain.value).put(field, validValue);
                  }
                  chain.match = true;

                  return chain;
                }));

          }

        }
      }

      final var min = environment.specification.getNumber("minProperties", 0).intValue();
      if (value.size() < min) {

        promise.fail(environment.notValid("Expecting at least '" + min + "' properties."));
        return future;
      }

      if (environment.specification.containsKey("maxProperties")) {

        final var max = environment.specification.getNumber("maxProperties", 0).intValue();
        if (value.size() > max) {

          promise.fail(environment.notValid("Expecting at most '" + max + "' properties."));
          return future;
        }
      }

      final var required = environment.specification.getJsonArray("required", new JsonArray());
      final var max = required.size();
      for (var i = 0; i < max; i++) {

        final var key = required.getString(i);
        if (!value.containsKey(key)) {

          promise.fail(environment.notValid(key, "Required property not defined."));
          return future;
        }
      }

    } else {

      promise.fail(environment.notValid("Expecting a 'JsonObject' value."));

    }
    return future;
  }

  /**
   * Validate that the value does not contains any unexpected field.
   *
   * @param promise     to inform of the validation.
   * @param environment to use for the validation.
   *
   * @return the future that says if the value has all the expected fields or not.
   */
  static Future<ValueEnvironment> validateUndefinedFieldsInValue(@NotNull final Promise<ValueEnvironment> promise,
      @NotNull final ValueEnvironment environment) {

    if (environment.value instanceof JsonObject && environment.fieldNames != null) {

      final var value = (JsonObject) environment.value;
      for (final var field : value.fieldNames()) {

        if (!environment.fieldNames.contains(field)) {

          promise.fail(environment.notValid(field, "The field is not defined on the specification."));
          break;

        }
      }

    }

    return promise.future();

  }

}
