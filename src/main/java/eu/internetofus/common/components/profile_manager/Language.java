/*
 * -----------------------------------------------------------------------------
 *
 * Copyright (c) 2019 - 2022 UDT-IA, IIIA-CSIC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * -----------------------------------------------------------------------------
 */

package eu.internetofus.common.components.profile_manager;

import eu.internetofus.common.components.Mergeable;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ReflectionModel;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.ValidationErrorException;
import eu.internetofus.common.components.Validations;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * Define the language that an user can understand.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(name = "language", description = "The language that an user can understand.")
public class Language extends ReflectionModel implements Model, Validable, Mergeable<Language> {

  /**
   * The name of the language.
   */
  @Schema(description = "The name of the language", example = "English")
  public String name;

  /**
   * The ISO 639-1 code that identify the language.
   */
  @Schema(description = "The ISO 639-1 code that identify the language", example = "en")
  public String code;

  /**
   * The linguistic ability level of the person.
   */
  @Schema(description = "The linguistic ability level of the person.", example = "B2")
  public LanguageLevel level;

  /**
   * Create an empty language.
   */
  public Language() {

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    try {

      this.name = Validations.validateNullableStringField(codePrefix, "name", 255, this.name);
      this.code = Validations.validateNullableStringField(codePrefix, "code", 2, this.code);
      promise.complete();

    } catch (final ValidationErrorException validationError) {

      promise.fail(validationError);
    }

    return promise.future();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Language> merge(final Language source, final String codePrefix, final Vertx vertx) {

    final Promise<Language> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var merged = new Language();
      merged.name = source.name;
      if (merged.name == null) {

        merged.name = this.name;
      }

      merged.code = source.code;
      if (merged.code == null) {

        merged.code = this.code;
      }

      merged.level = source.level;
      if (merged.level == null) {

        merged.level = this.level;
      }
      promise.complete(merged);
      future = future.compose(Validations.validateChain(codePrefix, vertx));

    } else {

      promise.complete(this);
    }
    return future;

  }

}
