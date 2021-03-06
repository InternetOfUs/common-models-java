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

import io.vertx.junit5.VertxTestContext;

/**
 * Asserts to do over the {@link Validable} components.
 *
 * @see Validable
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ValidableAsserts {

  /**
   * Assert that a model is not valid.
   *
   * @param model       to validate.
   * @param context     with the loaded components.
   * @param testContext test context to use.
   * @param <C>         type of context.
   */
  public static <C extends ValidateContext<C>> void assertIsNotValid(final Validable<C> model, final C context,
      final VertxTestContext testContext) {

    assertIsNotValid(model, null, context, testContext);

  }

  /**
   * Assert that a model is not valid because a field is wrong.
   *
   * @param model       to validate.
   * @param fieldName   name of the field that is not valid.
   * @param context     with the loaded components.
   * @param testContext test context to use.
   * @param <C>         type of context.
   */
  public static <C extends ValidateContext<C>> void assertIsNotValid(final Validable<C> model, final String fieldName,
      final C context, final VertxTestContext testContext) {

    model.validate(context).onComplete(testContext.failing(error -> testContext.verify(() -> {

      assertThat(error).isInstanceOf(ValidationErrorException.class);
      final var code = ((ValidationErrorException) error).getCode();
      if (fieldName != null) {

        assertThat(code).isEqualTo(context.fieldErrorCode(fieldName));

      } else {

        assertThat(code).startsWith(context.errorCode());
      }

      testContext.completeNow();

    })));
  }

  /**
   * Assert that a model is valid.
   *
   * @param model       to validate.
   * @param context     with the loaded components.
   * @param testContext test context to use.
   * @param <T>         model to test.
   * @param <C>         type of context.
   */
  public static <C extends ValidateContext<C>, T extends Validable<C>> void assertIsValid(final T model,
      final C context, final VertxTestContext testContext) {

    assertIsValid(model, context, testContext, null);

  }

  /**
   * Assert that a model is valid.
   *
   * @param model       to validate.
   * @param context     with the loaded components.
   * @param testContext test context to use.
   * @param expected    function to check the validation result.
   * @param <T>         model to test.
   * @param <C>         type of context.
   */
  public static <C extends ValidateContext<C>, T extends Validable<C>> void assertIsValid(final T model,
      final C context, final VertxTestContext testContext, final Runnable expected) {

    model.validate(context).onComplete(validation -> testContext.verify(() -> {

      if (validation.failed()) {

        final var cause = validation.cause();
        testContext.failNow(cause);

      } else {

        if (expected != null) {

          expected.run();
        }

        testContext.completeNow();
      }

    }));

  }

}
