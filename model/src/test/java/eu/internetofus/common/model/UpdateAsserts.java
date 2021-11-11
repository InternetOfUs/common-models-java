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
import java.util.function.Consumer;

/**
 * Asserts to do over the {@link Updateable} components.
 *
 * @see Updateable
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class UpdateAsserts {

  /**
   * Assert that a model can not be updated
   *
   * @param target      model to update.
   * @param source      model to update.
   * @param context     of the validated models.
   * @param testContext test context to use.
   * @param <T>         type of model to update.
   * @param <C>         type of context.
   */
  public static <T, C extends ValidateContext<C>> void assertCannotUpdate(final Updateable<T, C> target, final T source,
      final C context, final VertxTestContext testContext) {

    assertCannotUpdate(target, source, null, context, testContext);

  }

  /**
   * Assert that a model is not valid because a field is wrong.
   *
   * @param target      model to update.
   * @param source      model to update.
   * @param fieldName   name of the field that is not valid.
   * @param context     of the validated models.
   * @param testContext test context to use.
   * @param <T>         type of model to update.
   * @param <C>         type of context.
   */
  public static <T, C extends ValidateContext<C>> void assertCannotUpdate(final Updateable<T, C> target, final T source,
      final String fieldName, final C context, final VertxTestContext testContext) {

    target.update(source, context).onComplete(testContext.failing(error -> testContext.verify(() -> {

      assertThat(error).isInstanceOf(ValidationErrorException.class);
      var expectedCode = context.errorCode();
      if (fieldName != null) {

        expectedCode += "." + fieldName;

      }
      assertThat(((ValidationErrorException) error).getCode()).isEqualTo(expectedCode);

      testContext.completeNow();

    })));

  }

  /**
   * Assert that a model has been updated.
   *
   * @param target      model to update.
   * @param source      model to update.
   * @param context     of the validated models.
   * @param testContext test context to use.
   * @param <T>         model to test.
   * @param <C>         type of context.
   */
  public static <C extends ValidateContext<C>, T extends Validable<C>> void assertCanUpdate(
      final Updateable<T, C> target, final T source, final C context, final VertxTestContext testContext) {

    assertCanUpdate(target, source, context, testContext);

  }

  /**
   * Assert that a model has been updated.
   *
   * @param target      model to update.
   * @param source      model to update.
   * @param context     of the validated models.
   * @param testContext test context to use.
   * @param expected    function to validate the updated value.
   * @param <T>         model to test.
   * @param <C>         type of context.
   */
  public static <C extends ValidateContext<C>, T extends Validable<C>> void assertCanUpdate(
      final Updateable<T, C> target, final T source, final C context, final VertxTestContext testContext,
      final Consumer<T> expected) {

    target.update(source, context).onComplete(testContext.succeeding(updated -> testContext.verify(() -> {

      if (expected != null) {

        expected.accept(updated);
      }

      testContext.completeNow();

    })));

  }

}
