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

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import java.util.WeakHashMap;

/**
 * The class used to validate an OpenAPI specification or that a value follow an
 * OpenAPI specification.
 *
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class OpenAPIValidator {

  /**
   * The cache of reference specifications.
   */
  private static final WeakHashMap<String, JsonObject> references = new WeakHashMap<>();

  /**
   * The environment that is used to do the validation.
   */
  private class ValidationEnvironemnt {

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
    public ValidationEnvironemnt(final String codePrefix, final Vertx vertx, final JsonObject specification) {

      this.codePrefix = codePrefix;
      this.vertx = vertx;
      this.specification = specification;

    }

  }

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

    return Future.succeededFuture();
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

    if (specification == null) {

      return Future.failedFuture("");
    }

    return Future.succeededFuture();
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
  public static final <T> Future<T> validateValue(final String codePrefix, final Vertx vertx,
      final JsonObject specification, final T value) {

    return Future.succeededFuture();
  }

}
