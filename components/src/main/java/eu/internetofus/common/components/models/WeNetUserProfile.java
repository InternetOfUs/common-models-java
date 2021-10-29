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

package eu.internetofus.common.components.models;

import eu.internetofus.common.components.MergeFieldLists;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.model.CreateUpdateTsDetails;
import eu.internetofus.common.model.Mergeable;
import eu.internetofus.common.model.Merges;
import eu.internetofus.common.model.Updateable;
import eu.internetofus.common.model.Validable;
import eu.internetofus.common.model.Validations;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import java.util.List;

/**
 * The profile of a WeNet user.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "WeNetUserProfile", description = "The profile of a WeNet user.")
public class WeNetUserProfile extends CreateUpdateTsDetails
    implements Validable, Mergeable<WeNetUserProfile>, Updateable<WeNetUserProfile> {

  /**
   * A person whose gender identity matches to female.
   */
  public static final String FEMALE = "F";

  /**
   * A person whose gender identity matches to male.
   */
  public static final String MALE = "M";

  /**
   * A person whose gender is other.
   */
  public static final String OTHER = "O";

  /**
   * A person whose gender is not binary.
   */
  public static final String NON_BINARY = "non-binary";

  /**
   * A person whose not say its gender.
   */
  public static final String NOT_SAY = "not-say";

  /**
   * The possible genders of the user.
   */
  public static final String[] GENDERS = { FEMALE, MALE, OTHER, NON_BINARY, NOT_SAY };

  /**
   * The identifier of the profile.
   */
  @Schema(description = "The identifier of the profile.", example = "15837028-645a-4a55-9aaf-ceb846439eba", nullable = true)
  public String id;

  /**
   * The name of the user.
   */
  @Schema(description = "The name of the user.", nullable = true)
  public UserName name;

  /**
   * The date of birth of the user.
   */
  @Schema(description = "The date of birth of the user.", nullable = true)
  public AliveBirthDate dateOfBirth;

  /**
   * The gender of the user.
   */
  @Schema(description = "The gender of the user", example = "F", nullable = true)
  public String gender;

  /**
   * The email of the user.
   */
  @Schema(description = "The email of the user", example = "jonnyd@internetofus.eu", nullable = true)
  public String email;

  /**
   * The phone number of the user, on the E.164 format (^\+?[1-9]\d{1,14}$).
   */
  @Schema(description = "The phone number of the user, on the E.164 format(^\\+?[1-9]\\d{1,14}$)", example = "+34987654321", nullable = true)
  public String phoneNumber;

  /**
   * The email of the user.
   */
  @Schema(description = "The locale of the user", example = "es_ES", nullable = true)
  public String locale;

  /**
   * The avatar of the user.
   */
  @Schema(description = "The URL to an image that represents the avatar of the user.", example = "https://internetofus.eu/wp-content/uploads/sites/38/2019/02/WeNet_logo.png", nullable = true)
  public String avatar;

  /**
   * The email of the user.
   */
  @Schema(description = "The nationality of the user", example = "Spanish", nullable = true)
  public String nationality;

  /**
   * The email of the user.
   */
  @Schema(description = "The occupation of the user", example = "nurse", nullable = true)
  public String occupation;

  /**
   * The email of the user.
   */
  @Schema(description = "This is true if the platform has location information of the user", example = "true", nullable = true)
  public Boolean hasLocations;

  /**
   * The individual norms of the user
   */
  @ArraySchema(schema = @Schema(implementation = ProtocolNorm.class), arraySchema = @Schema(description = "The individual norms of the user", nullable = true))
  public List<ProtocolNorm> norms;

  /**
   * The planned activities of the user.
   */
  @ArraySchema(schema = @Schema(implementation = PlannedActivity.class), arraySchema = @Schema(description = "The planned activities of the user", nullable = true))
  public List<PlannedActivity> plannedActivities;

  /**
   * The locations of interest for the user.
   */
  @ArraySchema(schema = @Schema(implementation = RelevantLocation.class), arraySchema = @Schema(description = "The locations of interest for the user", nullable = true))
  public List<RelevantLocation> relevantLocations;

  /**
   * The user relationships.
   */
  @ArraySchema(schema = @Schema(implementation = SocialNetworkRelationship.class), arraySchema = @Schema(description = "The user relationships with other WeNet users.", nullable = true))
  public List<SocialNetworkRelationship> relationships;

  /**
   * The user routines.
   */
  @ArraySchema(schema = @Schema(implementation = Routine.class), arraySchema = @Schema(description = "The user routines", nullable = true))
  public List<Routine> personalBehaviors;

  /**
   * The materials of the user.
   */
  @ArraySchema(schema = @Schema(implementation = Material.class), arraySchema = @Schema(description = "The materials that has the user", nullable = true))
  public List<Material> materials;

  /**
   * The competences of the user.
   */
  @ArraySchema(schema = @Schema(implementation = Competence.class), arraySchema = @Schema(description = "The competences of the user", nullable = true))
  public List<Competence> competences;

  /**
   * The meanings of the user.
   */
  @ArraySchema(schema = @Schema(implementation = Meaning.class), arraySchema = @Schema(description = "The meanings of the user", nullable = true))
  public List<Meaning> meanings;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();

    if (this.id != null) {

      future = Validations.composeValidateId(future, codePrefix, "id", this.id, false,
          WeNetProfileManager.createProxy(vertx)::retrieveProfile);

    }

    if (this.name != null) {

      future = future.compose(mapper -> this.name.validate(codePrefix + ".name", vertx));
    }
    if (this.dateOfBirth != null) {

      future = future.compose(mapper -> this.dateOfBirth.validate(codePrefix + ".dateOfBirth", vertx));

    }

    future = future.compose(
        empty -> Validations.validateNullableStringField(codePrefix, "gender", this.gender, GENDERS).map(gender -> {
          this.gender = gender;
          return null;
        }));
    future = future
        .compose(empty -> Validations.validateNullableEmailField(codePrefix, "email", this.email).map(email -> {
          this.email = email;
          return null;
        }));
    future = future
        .compose(empty -> Validations.validateNullableLocaleField(codePrefix, "locale", this.locale).map(locale -> {
          this.locale = locale;
          return null;
        }));
    future = future.compose(empty -> Validations
        .validateNullableTelephoneField(codePrefix, "phoneNumber", this.locale, this.phoneNumber).map(phoneNumber -> {
          this.phoneNumber = phoneNumber;
          return null;
        }));
    future = future
        .compose(empty -> Validations.validateNullableURLField(codePrefix, "avatar", this.avatar).map(avatar -> {
          this.avatar = avatar;
          return null;
        }));
    future = future.compose(empty -> Validations
        .validateNullableStringField(codePrefix, "nationality", this.nationality).map(nationality -> {
          this.nationality = nationality;
          return null;
        }));
    future = future.compose(
        empty -> Validations.validateNullableStringField(codePrefix, "occupation", this.occupation).map(occupation -> {
          this.occupation = occupation;
          return null;
        }));
    future = future.compose(Validations.validate(this.norms, (a, b) -> a.equals(b), codePrefix + ".norms", vertx));
    future = future.compose(Validations.validate(this.plannedActivities, (a, b) -> a.id.equals(b.id),
        codePrefix + ".plannedActivities", vertx));
    future = future.compose(Validations.validate(this.relevantLocations, (a, b) -> a.id.equals(b.id),
        codePrefix + ".relevantLocations", vertx));
    future = future.compose(Validations.validate(this.relationships, (a, b) -> a.equalsByUserAndType(b),
        codePrefix + ".relationships", vertx));
    future = future.compose(
        Validations.validate(this.personalBehaviors, (a, b) -> a.equals(b), codePrefix + ".personalBehaviors", vertx));
    future = future
        .compose(Validations.validate(this.materials, (a, b) -> a.equals(b), codePrefix + ".materials", vertx));
    future = future
        .compose(Validations.validate(this.competences, (a, b) -> a.equals(b), codePrefix + ".competences", vertx));
    future = future
        .compose(Validations.validate(this.meanings, (a, b) -> a.equals(b), codePrefix + ".meanings", vertx));

    promise.complete();

    return future;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<WeNetUserProfile> merge(final WeNetUserProfile source, final String codePrefix, final Vertx vertx) {

    final Promise<WeNetUserProfile> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var merged = new WeNetUserProfile();
      merged._creationTs = this._creationTs;
      merged._lastUpdateTs = this._lastUpdateTs;

      merged.gender = source.gender;
      if (merged.gender == null) {

        merged.gender = this.gender;
      }

      merged.email = source.email;
      if (merged.email == null) {

        merged.email = this.email;
      }
      merged.locale = source.locale;
      if (merged.locale == null) {

        merged.locale = this.locale;
      }
      merged.phoneNumber = source.phoneNumber;
      if (merged.phoneNumber == null) {

        merged.phoneNumber = this.phoneNumber;
      }
      merged.avatar = source.avatar;
      if (merged.avatar == null) {

        merged.avatar = this.avatar;
      }

      merged.nationality = source.nationality;
      if (merged.nationality == null) {

        merged.nationality = this.nationality;
      }

      merged.occupation = source.occupation;
      if (merged.occupation == null) {

        merged.occupation = this.occupation;
      }

      merged.hasLocations = source.hasLocations;
      if (merged.hasLocations == null) {

        merged.hasLocations = this.hasLocations;
      }

      merged.relationships = source.relationships;
      if (merged.relationships == null) {

        merged.relationships = this.relationships;
      }

      future = future.compose(Validations.validateChain(codePrefix, vertx));

      future = future.compose(Merges.mergeField(this.name, source.name, codePrefix + ".name", vertx,
          (model, mergedName) -> model.name = mergedName));

      future = future.compose(Merges.mergeField(this.dateOfBirth, source.dateOfBirth, codePrefix + ".dateOfBirth",
          vertx, (model, mergedDateOfBirth) -> model.dateOfBirth = (AliveBirthDate) mergedDateOfBirth));

      merged.norms = source.norms;
      if (merged.norms == null) {

        merged.norms = this.norms;
      }

      future = future.compose(MergeFieldLists.mergePlannedActivities(this.plannedActivities, source.plannedActivities,
          codePrefix + ".plannedActivities", vertx, (model, mergedPlannedActivities) -> {
            merged.plannedActivities = mergedPlannedActivities;
          }));

      future = future.compose(MergeFieldLists.mergeRelevantLocations(this.relevantLocations, source.relevantLocations,
          codePrefix + ".relevantLocations", vertx, (model, mergedRelevantLocations) -> {
            model.relevantLocations = mergedRelevantLocations;
          }));

      future = future.compose(MergeFieldLists.mergeRoutines(this.personalBehaviors, source.personalBehaviors,
          codePrefix + ".personalBehaviors", vertx, (model, mergedPersonalBehaviors) -> {
            model.personalBehaviors = mergedPersonalBehaviors;
          }));

      future = future.compose(MergeFieldLists.mergeMaterials(this.materials, source.materials,
          codePrefix + ".materials", vertx, (model, mergedMaterials) -> {
            model.materials = mergedMaterials;
          }));

      future = future.compose(MergeFieldLists.mergeCompetences(this.competences, source.competences,
          codePrefix + ".competences", vertx, (model, mergedCompetences) -> {
            model.competences = mergedCompetences;
          }));

      future = future.compose(MergeFieldLists.mergeMeanings(this.meanings, source.meanings, codePrefix + ".meanings",
          vertx, (model, mergedMeanings) -> {
            model.meanings = mergedMeanings;
          }));

      promise.complete(merged);

      // When merged set the fixed field values
      future = future.map(mergedValidatedModel -> {

        mergedValidatedModel.id = this.id;
        return mergedValidatedModel;
      });

    } else {

      promise.complete(this);
    }

    return future;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<WeNetUserProfile> update(final WeNetUserProfile source, final String codePrefix, final Vertx vertx) {

    final Promise<WeNetUserProfile> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var updated = new WeNetUserProfile();
      updated._creationTs = this._creationTs;
      updated._lastUpdateTs = this._lastUpdateTs;

      updated.gender = source.gender;
      updated.email = source.email;
      updated.locale = source.locale;
      updated.phoneNumber = source.phoneNumber;
      updated.avatar = source.avatar;
      updated.nationality = source.nationality;
      updated.occupation = source.occupation;
      updated.hasLocations = source.hasLocations;
      updated.relationships = source.relationships;
      updated.name = source.name;
      updated.dateOfBirth = source.dateOfBirth;
      updated.norms = source.norms;
      updated.plannedActivities = source.plannedActivities;
      updated.relevantLocations = source.relevantLocations;
      updated.personalBehaviors = source.personalBehaviors;
      updated.materials = source.materials;
      updated.competences = source.competences;
      updated.meanings = source.meanings;

      future = future.compose(Validations.validateChain(codePrefix, vertx));
      future = future.map(updatedValidatedModel -> {

        updatedValidatedModel.id = this.id;
        return updatedValidatedModel;
      });

      promise.complete(updated);

    } else {

      promise.complete(this);
    }

    return future;
  }

}