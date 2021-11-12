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

import java.util.HashMap;
import java.util.Map;

/**
 * The cache of the values that has been used on the validation.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ValidationCache {

  /**
   * The cache with the values.
   */
  protected Map<String, Object> cache = new HashMap<>();

  /**
   * GEnerate.
   *
   * @param id   identifier of the model.
   * @param type of the model.
   *
   * @return the
   */
  protected String generateKey(final String id, final Class<?> type) {

    final var builder = new StringBuilder();
    builder.append(type.getName());
    builder.append("#");
    builder.append(id);
    return builder.toString();
  }

  /**
   * Get a cached model.
   *
   * @param id   of the model.
   * @param type of the model.
   * @param <T>  type of the model.
   *
   * @return the cached model or {@code null} if it is not defined.
   */
  @SuppressWarnings("unchecked")
  public <T> T getModel(final String id, final Class<T> type) {

    if (id == null || type == null) {

      return null;

    } else {

      final var key = this.generateKey(id, type);
      return (T) this.cache.get(key);

    }

  }

  /**
   * Add a model into the cache.
   *
   * @param id    of the model.
   * @param model to store.
   */
  public void setModel(final String id, final Object model) {

    if (id != null && model != null) {

      final var key = this.generateKey(id, model.getClass());
      this.cache.put(key, model);

    }

  }

  /**
   * Check if a model exist.
   *
   * @param id   of the model.
   * @param type of the model.
   *
   * @return {@code true} if the model is cached.
   */
  public boolean existModel(final String id, final Class<?> type) {

    if (id == null || type == null) {

      return false;

    } else {

      final var key = this.generateKey(id, type);
      return this.cache.containsKey(key);
    }
  }

}
