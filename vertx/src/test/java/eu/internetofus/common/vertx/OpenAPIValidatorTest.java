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
import static org.junit.Assert.fail;

import eu.internetofus.common.model.ValidationErrorException;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test the {@link OpenAPIValidator}.
 *
 * @see OpenAPIValidator
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class OpenAPIValidatorTest {

  /**
   * The code prefix to use as base.
   */
  public static final String ROOT = "root";

  /**
   * Check that a future fails.
   *
   * @param future      that has to fail.
   * @param testContext test cycle controller.
   */
  public static final <T> void assertFail(final Future<T> future, final VertxTestContext testContext) {

    assertFail(future, null, testContext);
  }

  /**
   * Check that a future fails.
   *
   * @param future      that has to fail.
   * @param codePrefix  expected error prefix.
   * @param testContext test cycle controller.
   */
  public static final <T> void assertFail(final Future<T> future, final String codePrefix,
      final VertxTestContext testContext) {

    future.onComplete(testContext.failing(error -> testContext.verify(() -> {

      assertThat(error).isInstanceOf(ValidationErrorException.class);
      final var code = ((ValidationErrorException) error).getCode();
      var expectedErrorCode = ROOT;
      if (codePrefix != null) {
        expectedErrorCode += "." + codePrefix;
      }
      assertThat(code).isEqualTo(expectedErrorCode);
      testContext.completeNow();

    })));

  }

  /**
   * Check that a future success.
   *
   * @param future      that has to success.
   * @param testContext test cycle controller.
   */
  public static final void assertSuccess(final Future<?> future, final VertxTestContext testContext) {

    assertSuccess(future, null, testContext);

  }

  /**
   * Check that an specification is valid.
   *
   * @param future      that has to success.
   * @param value       that has to be the future result.
   * @param testContext test cycle controller.
   */
  public static final <T> void assertSuccess(final Future<T> future, final T value,
      final VertxTestContext testContext) {

    future.onComplete(testContext.succeeding(successValue -> testContext.verify(() -> {

      assertThat(successValue).isEqualTo(value);
      testContext.completeNow();

    })));

  }

  /**
   * Load the arguments from some resource files.
   *
   * @param namePrefix       prefix for the files that contains the arguments.
   * @param argumentsBuilder the component that convert the loaded data to the
   *                         necessary arguments. The first argument is the test
   *                         identifier.
   *
   * @return the arguments to use.
   */
  private static Stream<Arguments> loadArguments(final String namePrefix,
      final BiFunction<String, JsonObject, Arguments> argumentsBuilder) {

    final List<Arguments> arguments = new ArrayList<>();
    try {

      final var loader = OpenAPIValidatorTest.class.getClassLoader();
      final var name = "eu/internetofus/common/vertx/" + namePrefix + ".json";
      final var input = loader.getResourceAsStream(name);
      final var code = IOUtils.toString(input, Charset.defaultCharset());
      final var content = (JsonObject) Json.decodeValue(code);
      for (final var id : content.fieldNames()) {

        final var data = content.getJsonObject(id);
        arguments.add(argumentsBuilder.apply(id, data));

      }

    } catch (final Throwable cause) {
      // can not load a file
      fail("Cannot load file data, because " + cause);
    }

    return arguments.stream();
  }

  /**
   * Provide some valid specifications.
   *
   * @return the valid specifications.
   */
  private static Stream<Arguments> validSpecifications() {

    return loadArguments("specification_valid", (id, specification) -> Arguments.of(id, specification));

  }

  /**
   * Check that an specification is valid.
   *
   * @param testId        identifier of the test.
   * @param specification that is valid.
   * @param vertx         event bus to use.
   * @param testContext   test cycle controller.
   *
   * @see OpenAPIValidator#validateSpecification(String, Vertx, JsonObject)
   */
  @ParameterizedTest(name = "{0}:Should {1} be valid")
  @MethodSource("validSpecifications")
  public void shouldBeValidSpecification(final String testId, final JsonObject specification, final Vertx vertx,
      final VertxTestContext testContext) {

    assertSuccess(OpenAPIValidator.validateSpecification(ROOT, vertx, specification), testContext);

  }

  /**
   * Provide the invalid specifications.
   *
   * @return the invalid specifications.
   */
  private static Stream<Arguments> invalidSpecifications() {

    return loadArguments("specification_invalid",
        (id, data) -> Arguments.of(id, data.getJsonObject("specification"), data.getString("codePrefix")));

  }

  /**
   * Check that an specification is not valid.
   *
   * @param testId        identifier of the test.
   * @param specification that is not valid.
   * @param codePrefix    expected error code.
   * @param vertx         event bus to use.
   * @param testContext   test cycle controller.
   *
   * @see OpenAPIValidator#validateSpecification(String, Vertx, JsonObject)
   */
  @ParameterizedTest(name = "{0}: Should {1} not be valid")
  @MethodSource("invalidSpecifications")
  public void shouldNotBeValidSpecification(final String testId, final JsonObject specification,
      final String codePrefix, final Vertx vertx, final VertxTestContext testContext) {

    assertFail(OpenAPIValidator.validateSpecification(ROOT, vertx, specification), codePrefix, testContext);

  }

  /**
   * Provide some valid composed specifications.
   *
   * @return the valid composed specifications.
   */
  private static Stream<Arguments> validComposedSpecifications() {

    return loadArguments("composed_specification_valid", (id, data) -> Arguments.of(id, data));

  }

  /**
   * Check that an composed specification is valid.
   *
   * @param testId                identifier of the test.
   * @param composedSpecification that is valid.
   * @param vertx                 event bus to use.
   * @param testContext           test cycle controller.
   *
   * @see OpenAPIValidator#validateComposedSpecification(String, Vertx,
   *      JsonObject)
   */
  @ParameterizedTest(name = "{0}: Should {1} be valid")
  @MethodSource("validComposedSpecifications")
  public void shouldBeValidComposedSpecification(final String testId, final JsonObject composedSpecification,
      final Vertx vertx, final VertxTestContext testContext) {

    assertSuccess(OpenAPIValidator.validateComposedSpecification(ROOT, vertx, composedSpecification), testContext);

  }

  /**
   * Provide the invalid composed specifications.
   *
   * @return the invalid composed specifications.
   */
  private static Stream<Arguments> invalidComposedSpecifications() {

    return loadArguments("composed_specification_invalid",
        (id, data) -> Arguments.of(id, data.getJsonObject("specification"), data.getString("codePrefix")));

  }

  /**
   * Check that an composed specification is not valid.
   *
   * @param testId                identifier of the test.
   * @param composedSpecification that is not valid.
   * @param codePrefix            expected error code.
   * @param vertx                 event bus to use.
   * @param testContext           test cycle controller.
   *
   * @see OpenAPIValidator#validateComposedSpecification(String, Vertx,
   *      JsonObject)
   */
  @ParameterizedTest(name = "{0}: Should {1} not be valid")
  @MethodSource("invalidComposedSpecifications")
  public void shouldNotBeValidComposedSpecification(final String testId, final JsonObject composedSpecification,
      final String codePrefix, final Vertx vertx, final VertxTestContext testContext) {

    assertFail(OpenAPIValidator.validateComposedSpecification(ROOT, vertx, composedSpecification), codePrefix,
        testContext);

  }

  /**
   * Provide some valid values.
   *
   * @return the valid values.
   */
  private static Stream<Arguments> validValues() {

    return loadArguments("value_valid", (id, data) -> Arguments.of(id, data.getJsonObject("specification"),
        data.getValue("value"), data.getValue("expectedValue")));

  }

  /**
   * Check that an value is valid.
   *
   * @param testId        identifier of the test.
   * @param specification that the value has to satisfy.
   * @param value         that is valid.
   * @param expectedValue the value that has to be valid.
   * @param vertx         event bus to use.
   * @param testContext   test cycle controller.
   *
   * @see OpenAPIValidator#validateValue(String, Vertx, JsonObject, Object)
   */
  @ParameterizedTest(name = "{0}: Should {2} be valid")
  @MethodSource("validValues")
  public void shouldBeValidValue(final String testId, final JsonObject specification, final Object value,
      final Object expectedValue, final Vertx vertx, final VertxTestContext testContext) {

    assertSuccess(OpenAPIValidator.validateValue(ROOT, vertx, specification, value), expectedValue, testContext);

  }

  /**
   * Provide the invalid values.
   *
   * @return the invalid values.
   */
  private static Stream<Arguments> invalidValues() {

    return loadArguments("value_invalid", (id, data) -> Arguments.of(id, data.getJsonObject("specification"),
        data.getValue("value"), data.getString("codePrefix")));

  }

  /**
   * Check that an value is not valid.
   *
   * @param testId        identifier of the test.
   * @param specification that the value has to satisfy.
   * @param value         that is not valid.
   * @param codePrefix    expected error code.
   * @param vertx         event bus to use.
   * @param testContext   test cycle controller.
   *
   * @see OpenAPIValidator#validateValue(String, Vertx, JsonObject, Object)
   */
  @ParameterizedTest(name = "{0}: Should {2} not be valid")
  @MethodSource("invalidValues")
  public void shouldNotBeValidValue(final String testId, final JsonObject specification, final Object value,
      final String codePrefix, final Vertx vertx, final VertxTestContext testContext) {

    assertFail(OpenAPIValidator.validateValue(ROOT, vertx, specification, value), codePrefix, testContext);

  }

}
