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

package eu.internetofus.common.components;

import eu.internetofus.common.model.Model;
import eu.internetofus.common.vertx.ModelContext;
import io.vertx.core.Vertx;

/**
 * The context for a WeNet model.
 *
 * @param <T> type of model.
 * @param <I> type for the model identifier.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetModelContext<T extends Model, I> extends ModelContext<T, I, WeNetValidateContext> {

  /**
   * Create a new context.
   *
   * @param name  of the model.
   * @param type  of the model.
   * @param vertx event bus used.
   * @param <T>   type of model.
   * @param <I>   type for the model identifier.
   *
   * @return the created model.
   */
  public static <T extends Model, I> WeNetModelContext<T, I> creteWeNetContext(final String name, final Class<T> type,
      final Vertx vertx) {

    final var context = new WeNetModelContext<T, I>();
    context.name = name;
    context.type = type;
    context.validateContext = new WeNetValidateContext("bad_" + name, vertx);
    return context;
  }

}
