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
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * The utility components to merge values.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface Merges {

  /**
   * Merge a field that is mergeable too.
   *
   * @param context   to use.
   * @param fieldName name of the field to merge.
   * @param target    field value to merge.
   * @param source    field value to merge.
   * @param setter    function to set the merged field into the merged model.
   *
   * @param <M>       type of merging model.
   * @param <T>       type of the field.
   * @param <C>       type of validation context to use.
   *
   * @return the future that will provide the merged lists.
   */
  static <C extends ValidateContext<C>, M, T extends Mergeable<T, C>> Function<M, Future<M>> mergeField(final C context,
      final String fieldName, final T target, final T source, final BiConsumer<M, T> setter) {

    return merged -> {

      if (target != null) {

        final var fieldContext = context.createFieldContext(fieldName);
        return target.merge(source, fieldContext).map(mergedField -> {

          setter.accept(merged, mergedField);
          return merged;

        });

      } else {

        if (source != null) {

          setter.accept(merged, source);
        }
        return Future.succeededFuture(merged);
      }

    };

  }

  /**
   * Merge a list field that is mergeable too.
   *
   * @param context          to use.
   * @param fieldName        name of the field to merge.
   * @param target           field value to merge.
   * @param source           field value to merge.
   * @param equalsIdentifier function to check if two models have the same
   *                         identifier.
   * @param setter           function to set the merged field list into the merged
   *                         model.
   *
   * @param <M>              type of merging model.
   * @param <T>              type of the field.
   * @param <C>              type of validation context to use.
   *
   * @return the future that will provide the merged lists.
   */
  static <C extends ValidateContext<C>, M, T extends Mergeable<T, C> & Validable<C>> Function<M, Future<M>> mergeListField(
      final C context, final String fieldName, final List<T> target, final List<T> source,
      final BiPredicate<T, T> equalsIdentifier, final BiConsumer<M, List<T>> setter) {

    return merged -> {

      if (target != null && source != null) {

        final Promise<List<T>> promise = Promise.promise();
        promise.complete(new ArrayList<>());
        var future = promise.future();
        final var copy = new ArrayList<>(target);
        for (final var sourceElement : source) {

          var found = false;
          final var max = copy.size();
          for (var i = 0; i < max; i++) {

            final var targetElement = copy.get(i);
            if (equalsIdentifier.test(targetElement, sourceElement)) {

              copy.remove(i);
              final var elementContext = context.createFieldElementContext(fieldName, i);
              future = future
                  .compose(mergedElements -> targetElement.merge(sourceElement, elementContext).map(mergedElement -> {

                    mergedElements.add(mergedElement);
                    return mergedElements;

                  }));
              found = true;
              break;
            }

          }
          if (!found) {

            future = future.compose(mergedElements -> {

              mergedElements.add(sourceElement);
              return Future.succeededFuture(mergedElements);

            });
          }
        }

        return future.map(mergedElements -> {

          setter.accept(merged, mergedElements);
          return merged;

        });

      } else {

        if (source != null) {

          setter.accept(merged, source);
        }
        return Future.succeededFuture(merged);

      }

    };

  }

  /**
   * Merge to values.
   *
   * @param target value to merge.
   * @param source value to merge.
   *
   * @param <T>    type of model to merge.
   *
   * @return the merged value.
   */
  @SuppressWarnings("unchecked")
  static <T> T mergeValues(final T target, final T source) {

    if (source == null) {

      return target;

    } else if (target == null || target.getClass() != source.getClass()) {

      return source;

    } else if (source instanceof JsonObject) {

      return (T) mergeJsonObjects((JsonObject) target, (JsonObject) source);

    } else if (source instanceof JsonArray) {

      return (T) mergeJsonArrays((JsonArray) target, (JsonArray) source);

    } else {

      return source;
    }

  }

  /**
   * Merge to JSON objects.
   *
   * @param target value to merge.
   * @param source value to merge.
   *
   * @return the merged JSON objects.
   */
  static JsonObject mergeJsonObjects(final JsonObject target, final JsonObject source) {

    if (source == null) {

      return target;

    } else if (target == null) {

      return source;

    } else {

      final var merged = target.copy();
      for (final var key : source.fieldNames()) {

        final var sourceValue = source.getValue(key);
        final var targetValue = target.getValue(key);
        final var mergedValue = mergeValues(targetValue, sourceValue);
        merged.put(key, mergedValue);

      }

      return merged;
    }

  }

  /**
   * Merge to JSON arrays.
   *
   * @param target value to merge.
   * @param source value to merge.
   *
   * @return the merged JSON arrays.
   */
  static JsonArray mergeJsonArrays(final JsonArray target, final JsonArray source) {

    if (source == null) {

      return target;

    } else if (target == null) {

      return source;

    } else {

      final var max = target.size();
      if (source.size() != max) {

        return source;

      } else {

        final var merged = new JsonArray();
        for (var i = 0; i < max; i++) {

          final var sourceValue = source.getValue(i);
          final var targetValue = target.getValue(i);
          final var mergedValue = mergeValues(targetValue, sourceValue);
          merged.add(mergedValue);

        }

        return merged;
      }
    }

  }

}
