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

package eu.internetofus.common.components;

import eu.internetofus.common.components.profile_manager.CommunityMember;
import eu.internetofus.common.components.profile_manager.Competence;
import eu.internetofus.common.components.profile_manager.Material;
import eu.internetofus.common.components.profile_manager.Meaning;
import eu.internetofus.common.components.profile_manager.Norm;
import eu.internetofus.common.components.profile_manager.PlannedActivity;
import eu.internetofus.common.components.profile_manager.RelevantLocation;
import eu.internetofus.common.components.profile_manager.Routine;
import eu.internetofus.common.components.profile_manager.SocialPractice;
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
   * Merge two list of norms.
   *
   * @param targetNorms target norms to merge.
   * @param sourceNorms source norms to merge.
   * @param codePrefix  prefix for the error code.
   * @param vertx       the event bus infrastructure to use.
   * @param setter      function to set the merged field list into the merged
   *                    model.
   *
   * @param <M>         type of merging model.
   *
   * @return the future that will provide the merged list of norms.
   */
  static <M> Function<M, Future<M>> mergeNorms(final List<Norm> targetNorms, final List<Norm> sourceNorms,
      final String codePrefix, final Vertx vertx, final BiConsumer<M, List<Norm>> setter) {

    return Merges.mergeFieldList(targetNorms, sourceNorms, codePrefix, vertx, norm -> norm.id != null,
        (targetNorm, sourceNorm) -> targetNorm.id.equals(sourceNorm.id), setter);

  }

  /**
   * Merge two list of planned activities.
   *
   * @param targetPlannedActivitys target planned activities to merge.
   * @param sourcePlannedActivitys source planned activities to merge.
   * @param codePrefix             prefix for the error code.
   * @param vertx                  the event bus infrastructure to use.
   * @param setter                 function to set the merged field list into the
   *                               merged model.
   *
   * @param <M>                    type of merging model.
   *
   * @return the future that will provide the merged list of planned activities.
   */
  static <M> Function<M, Future<M>> mergePlannedActivities(final List<PlannedActivity> targetPlannedActivitys,
      final List<PlannedActivity> sourcePlannedActivitys, final String codePrefix, final Vertx vertx,
      final BiConsumer<M, List<PlannedActivity>> setter) {

    return Merges.mergeFieldList(targetPlannedActivitys, sourcePlannedActivitys, codePrefix, vertx,
        plannedactivity -> plannedactivity.id != null,
        (targetPlannedActivity, sourcePlannedActivity) -> targetPlannedActivity.id.equals(sourcePlannedActivity.id),
        setter);

  }

  /**
   * Merge two list of relevant locations.
   *
   * @param targetRelevantLocations target relevant locations to merge.
   * @param sourceRelevantLocations source relevant locations to merge.
   * @param codePrefix              prefix for the error code.
   * @param vertx                   the event bus infrastructure to use.
   * @param setter                  function to set the merged field list into the
   *                                merged model.
   *
   * @param <M>                     type of merging model.
   *
   * @return the future that will provide the merged list of relevant locations.
   */
  static <M> Function<M, Future<M>> mergeRelevantLocations(final List<RelevantLocation> targetRelevantLocations,
      final List<RelevantLocation> sourceRelevantLocations, final String codePrefix, final Vertx vertx,
      final BiConsumer<M, List<RelevantLocation>> setter) {

    return Merges.mergeFieldList(targetRelevantLocations, sourceRelevantLocations, codePrefix, vertx,
        relevantlocation -> relevantlocation.id != null,
        (targetRelevantLocation, sourceRelevantLocation) -> targetRelevantLocation.id.equals(sourceRelevantLocation.id),
        setter);

  }

  /**
   * Merge two list of social practices.
   *
   * @param targetSocialPractices target social practices to merge.
   * @param sourceSocialPractices source social practices to merge.
   * @param codePrefix            prefix for the error code.
   * @param vertx                 the event bus infrastructure to use.
   * @param setter                function to set the merged field list into the
   *                              merged model.
   *
   * @param <M>                   type of merging model.
   *
   * @return the future that will provide the merged list of social practices.
   */
  static <M> Function<M, Future<M>> mergeSocialPractices(final List<SocialPractice> targetSocialPractices,
      final List<SocialPractice> sourceSocialPractices, final String codePrefix, final Vertx vertx,
      final BiConsumer<M, List<SocialPractice>> setter) {

    return Merges.mergeFieldList(targetSocialPractices, sourceSocialPractices, codePrefix, vertx,
        socialpractice -> socialpractice.id != null,
        (targetSocialPractice, sourceSocialPractice) -> targetSocialPractice.id.equals(sourceSocialPractice.id),
        setter);

  }

  /**
   * Merge two list of materials.
   *
   * @param targetMaterials target materials to merge.
   * @param sourceMaterials source materials to merge.
   * @param codePrefix      prefix for the error code.
   * @param vertx           the event bus infrastructure to use.
   * @param setter          function to set the merged field list into the merged
   *                        model.
   *
   * @param <M>             type of merging model.
   *
   * @return the future that will provide the merged list of materials.
   */
  static <M> Function<M, Future<M>> mergeMaterials(final List<Material> targetMaterials,
      final List<Material> sourceMaterials, final String codePrefix, final Vertx vertx,
      final BiConsumer<M, List<Material>> setter) {

    return Merges.mergeFieldList(targetMaterials, sourceMaterials, codePrefix, vertx,
        material -> material.name != null && material.classification != null,
        (targetMaterial, sourceMaterial) -> targetMaterial.name.equals(sourceMaterial.name)
            && targetMaterial.classification.equals(sourceMaterial.classification),
        setter);

  }

  /**
   * Merge two list of competences.
   *
   * @param targetCompetences target competences to merge.
   * @param sourceCompetences source competences to merge.
   * @param codePrefix        prefix for the error code.
   * @param vertx             the event bus infrastructure to use.
   * @param setter            function to set the merged field list into the
   *                          merged model.
   *
   * @param <M>               type of merging model.
   *
   * @return the future that will provide the merged list of competences.
   */
  static <M> Function<M, Future<M>> mergeCompetences(final List<Competence> targetCompetences,
      final List<Competence> sourceCompetences, final String codePrefix, final Vertx vertx,
      final BiConsumer<M, List<Competence>> setter) {

    return Merges.mergeFieldList(targetCompetences, sourceCompetences, codePrefix, vertx,
        competence -> competence.name != null && competence.ontology != null,
        (targetCompetence, sourceCompetence) -> targetCompetence.name.equals(sourceCompetence.name)
            && targetCompetence.ontology.equals(sourceCompetence.ontology),
        setter);

  }

  /**
   * Merge two list of meanings.
   *
   * @param targetMeanings target meanings to merge.
   * @param sourceMeanings source meanings to merge.
   * @param codePrefix     prefix for the error code.
   * @param vertx          the event bus infrastructure to use.
   * @param setter         function to set the merged field list into the merged
   *                       model.
   *
   * @param <M>            type of merging model.
   *
   * @return the future that will provide the merged list of meanings.
   */
  static <M> Function<M, Future<M>> mergeMeanings(final List<Meaning> targetMeanings,
      final List<Meaning> sourceMeanings, final String codePrefix, final Vertx vertx,
      final BiConsumer<M, List<Meaning>> setter) {

    return Merges.mergeFieldList(targetMeanings, sourceMeanings, codePrefix, vertx,
        meaning -> meaning.name != null && meaning.category != null,
        (targetMeaning, sourceMeaning) -> targetMeaning.name.equals(sourceMeaning.name)
            && targetMeaning.category.equals(sourceMeaning.category),
        setter);

  }

  /**
   * Merge two list of routines.
   *
   * @param targetRoutines target routines to merge.
   * @param sourceRoutines source routines to merge.
   * @param codePrefix     prefix for the error code.
   * @param vertx          the event bus infrastructure to use.
   * @param setter         function to set the merged field list into the merged
   *                       model.
   *
   * @param <M>            type of merging model.
   *
   * @return the future that will provide the merged list of routines.
   */
  static <M> Function<M, Future<M>> mergeRoutines(final List<Routine> targetRoutines,
      final List<Routine> sourceRoutines, final String codePrefix, final Vertx vertx,
      final BiConsumer<M, List<Routine>> setter) {

    return Merges.mergeFieldList(targetRoutines, sourceRoutines, codePrefix, vertx, routine -> false,
        (targetRoutine, sourceRoutine) -> false, setter);

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
   * Merge two list of community members.
   *
   * @param targetMembers target community members to merge.
   * @param sourceMembers source community members to merge.
   * @param codePrefix    prefix for the error code.
   * @param vertx         the event bus infrastructure to use.
   * @param setter        function to set the merged field list into the merged
   *                      model.
   *
   * @param <M>           type of merging model.
   *
   * @return the future that will provide the merged list of community members.
   */
  static <M> Function<M, Future<M>> mergeMembers(final List<CommunityMember> targetMembers,
      final List<CommunityMember> sourceMembers, final String codePrefix, final Vertx vertx,
      final BiConsumer<M, List<CommunityMember>> setter) {

    return Merges.mergeFieldList(targetMembers, sourceMembers, codePrefix, vertx, member -> member.userId != null,
        (targetMember, sourceMember) -> targetMember.userId.equals(sourceMember.userId), setter);

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
