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

import io.vertx.core.Vertx;
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
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   * @param <T>         type of model to update.
   */
  public static <T> void assertCannotUpdate(final Updateable<T> target, final T source, final Vertx vertx,
      final VertxTestContext testContext) {

    assertCannotUpdate(target, source, null, vertx, testContext);

  }

  /**
   * Assert that a model is not valid because a field is wrong.
   *
   * @param target      model to update.
   * @param source      model to update.
   * @param fieldName   name of the field that is not valid.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   * @param <T>         type of model to update.
   */
  public static <T> void assertCannotUpdate(final Updateable<T> target, final T source, final String fieldName,
      final Vertx vertx, final VertxTestContext testContext) {

    target.update(source, "codePrefix", vertx).onComplete(testContext.failing(error -> testContext.verify(() -> {

      assertThat(error).isInstanceOf(ValidationErrorException.class);
      var expectedCode = "codePrefix";
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
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   * @param <T>         model to test.
   */
  public static <T extends Validable> void assertCanUpdate(final Updateable<T> target, final T source,
      final Vertx vertx, final VertxTestContext testContext) {

    assertCanUpdate(target, source, vertx, testContext, null);

  }

  /**
   * Assert that a model has been updated.
   *
   * @param target      model to update.
   * @param source      model to update.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   * @param expected    function to validate the updated value.
   * @param <T>         model to test.
   */
  public static <T extends Validable> void assertCanUpdate(final Updateable<T> target, final T source,
      final Vertx vertx, final VertxTestContext testContext, final Consumer<T> expected) {

    target.update(source, "codePrefix", vertx).onComplete(testContext.succeeding(updated -> testContext.verify(() -> {

      if (expected != null) {

        expected.accept(updated);
      }

      testContext.completeNow();

    })));

  }

}
