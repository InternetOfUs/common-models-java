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
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The utility components to merge values.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface Merges {

  /**
   * Merge a field lists.
   *
   * @param target           list to merge.
   * @param source           list to merge.
   * @param codePrefix       prefix for the error code.
   * @param vertx            the event bus infrastructure to use.
   * @param hasIdentifier    function to check if a model has identifier.
   * @param equalsIdentifier function to check if two models have the same
   *                         identifier.
   * @param setter           function to set the merged field list into the merged
   *                         model.
   *
   * @param <M>              type of merging model.
   * @param <T>              type of the field.
   *
   * @return the future that will provide the merged lists.
   */
  static <M, T extends Mergeable<T> & Validable> Function<M, Future<M>> mergeFieldList(final List<T> target,
      final List<T> source, final String codePrefix, final Vertx vertx, final Predicate<T> hasIdentifier,
      final BiPredicate<T, T> equalsIdentifier, final BiConsumer<M, List<T>> setter) {

    return model -> {
      final Promise<List<T>> promise = Promise.promise();
      var future = promise.future();
      if (source != null) {

        final List<T> targetWithIds = new ArrayList<>();
        if (target != null) {

          for (final T element : target) {

            if (hasIdentifier.test(element)) {

              targetWithIds.add(element);
            }
          }

        }
        INDEX: for (var index = 0; index < source.size(); index++) {

          final var codeElement = codePrefix + "[" + index + "]";
          final var sourceElement = source.get(index);
          // Search if it modify any original model
          if (hasIdentifier.test(sourceElement)) {

            for (var j = 0; j < index; j++) {

              final var element = source.get(j);
              if (hasIdentifier.test(element) && equalsIdentifier.test(element, sourceElement)) {

                return Future.failedFuture(
                    new ValidationErrorException(codeElement, "The identifier is already defined at " + j));
              }
            }
            for (var j = 0; j < targetWithIds.size(); j++) {

              final var targetElement = targetWithIds.get(j);
              if (equalsIdentifier.test(targetElement, sourceElement)) {

                targetWithIds.remove(j);
                future = future
                    .compose(merged -> targetElement.merge(sourceElement, codeElement, vertx).map(mergedElement -> {
                      merged.add(mergedElement);
                      return merged;
                    }));
                continue INDEX;
              }

            }
          }

          // Not found original model with the same id => check it as new
          future = future.compose(merged -> sourceElement.validate(codeElement, vertx).map(empty -> {
            merged.add(sourceElement);
            return merged;
          }));

        }

        promise.complete(new ArrayList<>());

      } else {

        promise.complete(target);
      }

      return future.map(mergedList -> {
        setter.accept(model, mergedList);
        return model;
      });
    };

  }

  /**
   * Merge a field.
   *
   * @param target     field value to merge.
   * @param source     field value to merge.
   * @param codePrefix prefix for the error code.
   * @param vertx      the event bus infrastructure to use.
   * @param setter     function to set the merged field list into the merged
   *                   model.
   *
   * @param <M>        type of merging model.
   * @param <T>        type of the field.
   *
   * @return the future that will provide the merged lists.
   */
  static <M, T extends Mergeable<T> & Validable> Function<M, Future<M>> mergeField(final T target, final T source,
      final String codePrefix, final Vertx vertx, final BiConsumer<M, T> setter) {

    return merged -> {

      if (target != null) {

        return target.merge(source, codePrefix, vertx).map(mergedField -> {

          setter.accept(merged, mergedField);
          return merged;
        });

      } else if (source != null) {

        return source.validate(codePrefix, vertx).map(empty -> {

          setter.accept(merged, source);
          return merged;
        });

      } else {

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
   * @return the merged value.
   */
  static Object mergeValues(final Object target, final Object source) {

    if (source == null) {

      return target;

    } else if (target == null || target.getClass() != source.getClass()) {

      return source;

    } else if (source instanceof JsonObject) {

      return mergeJsonObjects((JsonObject) target, (JsonObject) source);

    } else if (source instanceof JsonArray) {

      return mergeJsonArrays((JsonArray) target, (JsonArray) source);

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
