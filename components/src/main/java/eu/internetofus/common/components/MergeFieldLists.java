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

import eu.internetofus.common.components.models.CommunityMember;
import eu.internetofus.common.components.models.Competence;
import eu.internetofus.common.components.models.Material;
import eu.internetofus.common.components.models.Meaning;
import eu.internetofus.common.components.models.PlannedActivity;
import eu.internetofus.common.components.models.RelevantLocation;
import eu.internetofus.common.components.models.Routine;
import eu.internetofus.common.components.models.SocialPractice;
import eu.internetofus.common.model.Merges;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * The utility components to merge values.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface MergeFieldLists {

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

}
