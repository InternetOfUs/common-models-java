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

import io.vertx.core.Future;
import java.util.function.Function;

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

    return Future.failedFuture(new ValidationErrorException(this.errorCode(), message));

  }

  /**
   * Create a context for an element.
   *
   * @param index of the element to create a new context.
   *
   * @return the context for the specified element in the current validating
   *         model.
   */
  default SELF createElementContext(final int index) {

    return this.createContextWithErrorCode(this.errorCode() + "[" + index + "]");
  }

  /**
   * Create a context for the field of a model.
   *
   * @param name of the field to create the context.
   *
   * @return the context for the specified field of the model.
   */
  default SELF createFieldContext(final String name) {

    return this.createContextWithErrorCode(this.errorCode() + "." + name);
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
}
