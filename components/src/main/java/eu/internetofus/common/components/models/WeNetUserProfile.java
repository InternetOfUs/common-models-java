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

import eu.internetofus.common.components.WeNetValidateContext;
import eu.internetofus.common.model.CreateUpdateTsDetails;
import eu.internetofus.common.model.Mergeable;
import eu.internetofus.common.model.Merges;
import eu.internetofus.common.model.Updateable;
import eu.internetofus.common.model.Validable;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import java.util.List;

/**
 * The profile of a WeNet user.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "WeNetUserProfile", description = "The profile of a WeNet user.")
public class WeNetUserProfile extends CreateUpdateTsDetails implements Validable<WeNetValidateContext>,
    Mergeable<WeNetUserProfile, WeNetValidateContext>, Updateable<WeNetUserProfile, WeNetValidateContext> {

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
  public Future<Void> validate(final WeNetValidateContext context) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();

    if (this.id != null) {

      future = context.validateNotDefinedProfileIdField("id", this.id, future);

    }

    if (this.name != null) {

      future = future.compose(context.validateField("name", this.name));
    }
    if (this.dateOfBirth != null) {

      future = future.compose(context.validateField("dateOfBirth", this.dateOfBirth));
    }

    this.gender = context.normalizeString(this.gender);
    if (this.gender != null) {

      context.validateEnumField("gender", this.gender, promise, GENDERS);
    }

    this.email = context.validateNullableEmailField("email", this.email, promise);
    this.locale = context.validateNullableLocaleField("locale", this.locale, promise);
    this.phoneNumber = context.validateNullableTelephoneField("phoneNumber", this.phoneNumber, this.locale, promise);
    this.avatar = context.validateNullableUrlField("avatar", this.avatar, promise);
    this.nationality = context.normalizeString(this.nationality);
    this.occupation = context.normalizeString(this.occupation);

    if (this.norms != null) {

      future = future.compose(context.validateListField("norms", this.norms, ProtocolNorm::compareIds));

    }
    if (this.plannedActivities != null) {

      future = future
          .compose(context.validateListField("plannedActivities", this.plannedActivities, PlannedActivity::compareIds));
    }
    if (this.relevantLocations != null) {

      future = future.compose(
          context.validateListField("relevantLocations", this.relevantLocations, RelevantLocation::compareIds));

    }
    if (this.personalBehaviors != null) {

      future = future
          .compose(context.validateListField("personalBehaviors", this.personalBehaviors, Routine::compareIds));
    }
    if (this.materials != null) {

      future = future.compose(context.validateListField("materials", this.materials, Material::compareIds));
    }
    if (this.competences != null) {

      future = future.compose(context.validateListField("competences", this.competences, Competence::compareIds));
    }
    if (this.meanings != null) {

      future = future.compose(context.validateListField("meanings", this.meanings, Meaning::compareIds));
    }

    promise.tryComplete();

    return future;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<WeNetUserProfile> merge(final WeNetUserProfile source, final WeNetValidateContext context) {

    final Promise<WeNetUserProfile> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var merged = new WeNetUserProfile();
      merged._creationTs = this._creationTs;
      merged._lastUpdateTs = this._lastUpdateTs;

      merged.gender = Merges.mergeValues(this.gender, source.gender);
      merged.email = Merges.mergeValues(this.email, source.email);
      merged.locale = Merges.mergeValues(this.locale, source.locale);
      merged.phoneNumber = Merges.mergeValues(this.phoneNumber, source.phoneNumber);
      merged.avatar = Merges.mergeValues(this.avatar, source.avatar);
      merged.nationality = Merges.mergeValues(this.nationality, source.nationality);
      merged.occupation = Merges.mergeValues(this.occupation, source.occupation);
      merged.hasLocations = Merges.mergeValues(this.hasLocations, source.hasLocations);

      future = future.compose(
          Merges.mergeField(context, "name", this.name, source.name, (model, mergedValue) -> model.name = mergedValue));

      future = future.compose(Merges.mergeField(context, "dateOfBirth", this.dateOfBirth, source.dateOfBirth,
          (model, mergedValue) -> model.dateOfBirth = (AliveBirthDate) mergedValue));

      future = future.compose(Merges.mergeListField(context, "norms", this.norms, source.norms,
          ProtocolNorm::compareIds, (model, norms) -> model.norms = norms));

      future = future
          .compose(Merges.mergeListField(context, "plannedActivities", this.plannedActivities, source.plannedActivities,
              PlannedActivity::compareIds, (model, mergedValue) -> model.plannedActivities = mergedValue));

      future = future
          .compose(Merges.mergeListField(context, "relevantLocations", this.relevantLocations, source.relevantLocations,
              RelevantLocation::compareIds, (model, mergedValue) -> model.relevantLocations = mergedValue));

      future = future
          .compose(Merges.mergeListField(context, "personalBehaviors", this.personalBehaviors, source.personalBehaviors,
              Routine::compareIds, (model, mergedValue) -> model.personalBehaviors = mergedValue));

      future = future.compose(Merges.mergeListField(context, "materials", this.materials, source.materials,
          Material::compareIds, (model, mergedValue) -> model.materials = mergedValue));

      future = future.compose(Merges.mergeListField(context, "competences", this.competences, source.competences,
          Competence::compareIds, (model, mergedValue) -> model.competences = mergedValue));

      future = future.compose(Merges.mergeListField(context, "meanings", this.meanings, source.meanings,
          Meaning::compareIds, (model, mergedValue) -> model.meanings = mergedValue));

      future = future.compose(context.chain());
      // When merged set the fixed field values
      future = future.map(mergedValidatedModel -> {

        mergedValidatedModel.id = this.id;
        return mergedValidatedModel;
      });

      promise.complete(merged);

    } else {

      promise.complete(this);
    }

    return future;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<WeNetUserProfile> update(final WeNetUserProfile source, final WeNetValidateContext context) {

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
      updated.name = source.name;
      updated.dateOfBirth = source.dateOfBirth;
      updated.norms = source.norms;
      updated.plannedActivities = source.plannedActivities;
      updated.relevantLocations = source.relevantLocations;
      updated.personalBehaviors = source.personalBehaviors;
      updated.materials = source.materials;
      updated.competences = source.competences;
      updated.meanings = source.meanings;

      future = future.compose(context.chain());
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