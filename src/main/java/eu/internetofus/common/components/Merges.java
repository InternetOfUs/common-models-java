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
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
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

package eu.internetofus.common.components;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import eu.internetofus.common.components.profile_manager.Language;
import eu.internetofus.common.components.profile_manager.Norm;
import eu.internetofus.common.components.profile_manager.PlannedActivity;
import eu.internetofus.common.components.profile_manager.RelevantLocation;
import eu.internetofus.common.components.profile_manager.Routine;
import eu.internetofus.common.components.profile_manager.SocialPractice;
import eu.internetofus.common.components.task_manager.TaskAttribute;
import eu.internetofus.common.components.task_manager.TaskAttributeType;
import eu.internetofus.common.components.task_manager.TaskTransactionType;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

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
   * @param equalsIdentifier function to check if two models have the same identifier.
   * @param setter           function to set the merged field list into the merged model.
   *
   * @param <M>              type of merging model.
   * @param <T>              type of the field.
   *
   * @return the future that will provide the merged lists.
   */
  static <M, T extends Mergeable<T> & Validable> Function<M, Future<M>> mergeFieldList(final List<T> target, final List<T> source, final String codePrefix, final Vertx vertx, final Predicate<T> hasIdentifier, final BiPredicate<T, T> equalsIdentifier,
      final BiConsumer<M, List<T>> setter) {

    return model -> {
      final Promise<List<T>> promise = Promise.promise();
      Future<List<T>> future = promise.future();
      if (source != null) {

        final List<T> original = new ArrayList<>();
        final List<T> originalMerged = new ArrayList<>();
        if (target != null) {

          for (final T element : target) {

            if (hasIdentifier.test(element)) {

              original.add(element);
            }
          }

        }
        INDEX: for (int index = 0; index < source.size(); index++) {

          final String codeElement = codePrefix + "[" + index + "]";
          final T sourceElement = source.get(index);
          // search if it modify any original model
          if (hasIdentifier.test(sourceElement)) {

            for (int j = 0; j < original.size(); j++) {

              final T targetElement = original.get(j);
              if (equalsIdentifier.test(targetElement, sourceElement)) {

                originalMerged.add(original.remove(j));
                future = future.compose(merged -> targetElement.merge(sourceElement, codeElement, vertx).map(mergedElement -> {
                  merged.add(mergedElement);
                  return merged;
                }));
                continue INDEX;
              }

            }
            for (final T element : originalMerged) {

              if (equalsIdentifier.test(element, sourceElement)) {

                future = Future.failedFuture(new ValidationErrorException(codeElement, "This model is already merged."));
                break INDEX;
              }

            }
          }

          // not found original model with the same id => check it as new
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
   * Merge two list of languages.
   *
   * @param targetLanguages target languages to merge.
   * @param sourceLanguages source languages to merge.
   * @param codePrefix      prefix for the error code.
   * @param vertx           the event bus infrastructure to use.
   * @param setter          function to set the merged field list into the merged model.
   *
   * @param <M>             type of merging model.
   *
   * @return the future that will provide the merged list of languages.
   */
  static <M> Function<M, Future<M>> mergeLanguages(final List<Language> targetLanguages, final List<Language> sourceLanguages, final String codePrefix, final Vertx vertx, final BiConsumer<M, List<Language>> setter) {

    return Merges.mergeFieldList(targetLanguages, sourceLanguages, codePrefix, vertx, language -> language.code != null || language.name != null,
        (targetLang, sourceLang) -> (targetLang.code != null && targetLang.code.equals(sourceLang.code) || targetLang.name != null && targetLang.name.equals(sourceLang.name)), setter);

  }

  /**
   * Merge two list of norms.
   *
   * @param targetNorms target norms to merge.
   * @param sourceNorms source norms to merge.
   * @param codePrefix  prefix for the error code.
   * @param vertx       the event bus infrastructure to use.
   * @param setter      function to set the merged field list into the merged model.
   *
   * @param <M>         type of merging model.
   *
   * @return the future that will provide the merged list of norms.
   */
  static <M> Function<M, Future<M>> mergeNorms(final List<Norm> targetNorms, final List<Norm> sourceNorms, final String codePrefix, final Vertx vertx, final BiConsumer<M, List<Norm>> setter) {

    return Merges.mergeFieldList(targetNorms, sourceNorms, codePrefix, vertx, norm -> norm.id != null, (targetNorm, sourceNorm) -> targetNorm.id.equals(sourceNorm.id), setter);

  }

  /**
   * Merge two list of planned activities.
   *
   * @param targetPlannedActivitys target planned activities to merge.
   * @param sourcePlannedActivitys source planned activities to merge.
   * @param codePrefix             prefix for the error code.
   * @param vertx                  the event bus infrastructure to use.
   * @param setter                 function to set the merged field list into the merged model.
   *
   * @param <M>                    type of merging model.
   *
   * @return the future that will provide the merged list of planned activities.
   */
  static <M> Function<M, Future<M>> mergePlannedActivities(final List<PlannedActivity> targetPlannedActivitys, final List<PlannedActivity> sourcePlannedActivitys, final String codePrefix, final Vertx vertx, final BiConsumer<M, List<PlannedActivity>> setter) {

    return Merges.mergeFieldList(targetPlannedActivitys, sourcePlannedActivitys, codePrefix, vertx, plannedactivity -> plannedactivity.id != null,
        (targetPlannedActivity, sourcePlannedActivity) -> targetPlannedActivity.id.equals(sourcePlannedActivity.id), setter);

  }

  /**
   * Merge two list of relevant locations.
   *
   * @param targetRelevantLocations target relevant locations to merge.
   * @param sourceRelevantLocations source relevant locations to merge.
   * @param codePrefix              prefix for the error code.
   * @param vertx                   the event bus infrastructure to use.
   * @param setter                  function to set the merged field list into the merged model.
   *
   * @param <M>                     type of merging model.
   *
   * @return the future that will provide the merged list of relevant locations.
   */
  static <M> Function<M, Future<M>> mergeRelevantLocations(final List<RelevantLocation> targetRelevantLocations, final List<RelevantLocation> sourceRelevantLocations, final String codePrefix, final Vertx vertx, final BiConsumer<M, List<RelevantLocation>> setter) {

    return Merges.mergeFieldList(targetRelevantLocations, sourceRelevantLocations, codePrefix, vertx, relevantlocation -> relevantlocation.id != null,
        (targetRelevantLocation, sourceRelevantLocation) -> targetRelevantLocation.id.equals(sourceRelevantLocation.id), setter);

  }

  /**
   * Merge two list of social practices.
   *
   * @param targetSocialPractices target social practices to merge.
   * @param sourceSocialPractices source social practices to merge.
   * @param codePrefix            prefix for the error code.
   * @param vertx                 the event bus infrastructure to use.
   * @param setter                function to set the merged field list into the merged model.
   *
   * @param <M>                   type of merging model.
   *
   * @return the future that will provide the merged list of social practices.
   */
  static <M> Function<M, Future<M>> mergeSocialPractices(final List<SocialPractice> targetSocialPractices, final List<SocialPractice> sourceSocialPractices, final String codePrefix, final Vertx vertx, final BiConsumer<M, List<SocialPractice>> setter) {

    return Merges.mergeFieldList(targetSocialPractices, sourceSocialPractices, codePrefix, vertx, socialpractice -> socialpractice.id != null,
        (targetSocialPractice, sourceSocialPractice) -> targetSocialPractice.id.equals(sourceSocialPractice.id), setter);

  }

  /**
   * Merge two list of routines.
   *
   * @param targetRoutines target routines to merge.
   * @param sourceRoutines source routines to merge.
   * @param codePrefix     prefix for the error code.
   * @param vertx          the event bus infrastructure to use.
   * @param setter         function to set the merged field list into the merged model.
   *
   * @param <M>            type of merging model.
   *
   * @return the future that will provide the merged list of routines.
   */
  static <M> Function<M, Future<M>> mergeRoutines(final List<Routine> targetRoutines, final List<Routine> sourceRoutines, final String codePrefix, final Vertx vertx, final BiConsumer<M, List<Routine>> setter) {

    return Merges.mergeFieldList(targetRoutines, sourceRoutines, codePrefix, vertx, routine -> false, (targetRoutine, sourceRoutine) -> false, setter);

  }

  /**
   * Merge two list of task attributes.
   *
   * @param targetTaskAttributes target task attributes to merge.
   * @param sourceTaskAttributes source task attributes to merge.
   * @param codePrefix           prefix for the error code.
   * @param vertx                the event bus infrastructure to use.
   * @param setter               function to set the merged field list into the merged model.
   *
   * @param <M>                  type of merging model.
   *
   * @return the future that will provide the merged list of task attributes.
   */
  static <M> Function<M, Future<M>> mergeTaskAttributes(final List<TaskAttribute> targetTaskAttributes, final List<TaskAttribute> sourceTaskAttributes, final String codePrefix, final Vertx vertx, final BiConsumer<M, List<TaskAttribute>> setter) {

    return Merges.mergeFieldList(targetTaskAttributes, sourceTaskAttributes, codePrefix, vertx, taskAttribute -> taskAttribute.name != null,
        (targetTaskAttribute, sourceTaskAttribute) -> targetTaskAttribute.name.equals(sourceTaskAttribute.name), setter);

  }

  /**
   * Convert a validation action to a merge action.
   *
   * @param codePrefix the prefix of the code to use for the error message.
   * @param vertx      the event bus infrastructure to use.
   *
   * @param <T>        type of {@link Validable} to convert to {@link Mergeable}.
   *
   * @return the mapper function that can validate a merged value.
   *
   * @see Future#compose(Function)
   */
  static <T extends Validable> Function<T, Future<T>> validateMerged(final String codePrefix, final Vertx vertx) {

    return merged -> merged.validate(codePrefix, vertx).map(validation -> merged);

  }

  /**
   * Merge a field.
   *
   * @param target     field value to merge.
   * @param source     field value to merge.
   * @param codePrefix prefix for the error code.
   * @param vertx      the event bus infrastructure to use.
   * @param setter     function to set the merged field list into the merged model.
   *
   * @param <M>        type of merging model.
   * @param <T>        type of the field.
   *
   * @return the future that will provide the merged lists.
   */
  static <M, T extends Mergeable<T> & Validable> Function<M, Future<M>> mergeField(final T target, final T source, final String codePrefix, final Vertx vertx, final BiConsumer<M, T> setter) {

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
   * Merge two list of task attribute types.
   *
   * @param targetTaskAttributeTypes target task attribute types to merge.
   * @param sourceTaskAttributeTypes source task attribute types to merge.
   * @param codePrefix               prefix for the error code.
   * @param vertx                    the event bus infrastructure to use.
   * @param setter                   function to set the merged field list into the merged model.
   *
   * @param <M>                      type of merging model.
   *
   * @return the future that will provide the merged list of task attribute types.
   */
  static <M> Function<M, Future<M>> mergeTaskAttributeTypes(final List<TaskAttributeType> targetTaskAttributeTypes, final List<TaskAttributeType> sourceTaskAttributeTypes, final String codePrefix, final Vertx vertx, final BiConsumer<M, List<TaskAttributeType>> setter) {

    return Merges.mergeFieldList(targetTaskAttributeTypes, sourceTaskAttributeTypes, codePrefix, vertx, taskattributetype -> taskattributetype.name != null,
        (targetTaskAttributeType, sourceTaskAttributeType) -> targetTaskAttributeType.name.equals(sourceTaskAttributeType.name), setter);

  }

  /**
   * Merge two list of task transaction types.
   *
   * @param targetTaskTransactionTypes target task transaction types to merge.
   * @param sourceTaskTransactionTypes source task transaction types to merge.
   * @param codePrefix                 prefix for the error code.
   * @param vertx                      the event bus infrastructure to use.
   * @param setter                     function to set the merged field list into the merged model.
   *
   * @param <M>                        type of merging model.
   *
   * @return the future that will provide the merged list of task transaction types.
   */
  static <M> Function<M, Future<M>> mergeTaskTransactionTypes(final List<TaskTransactionType> targetTaskTransactionTypes, final List<TaskTransactionType> sourceTaskTransactionTypes, final String codePrefix, final Vertx vertx,
      final BiConsumer<M, List<TaskTransactionType>> setter) {

    return Merges.mergeFieldList(targetTaskTransactionTypes, sourceTaskTransactionTypes, codePrefix, vertx, taskTransactionType -> taskTransactionType.label != null,
        (targetTaskTransactionType, sourceTaskTransactionType) -> targetTaskTransactionType.label.equals(sourceTaskTransactionType.label), setter);

  }

}
