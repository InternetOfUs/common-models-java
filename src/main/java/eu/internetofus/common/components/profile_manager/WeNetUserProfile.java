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

package eu.internetofus.common.components.profile_manager;

import java.util.List;

import eu.internetofus.common.components.Mergeable;
import eu.internetofus.common.components.Merges;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.ValidationErrorException;
import eu.internetofus.common.components.Validations;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * The profile of a WeNet user.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "WeNetUserProfile", description = "The profile of a WeNet user.")
public class WeNetUserProfile extends CreateUpdateTsDetails implements Validable, Mergeable<WeNetUserProfile> {

  /**
   * The identifier of the profile.
   */
  @Schema(description = "The identifier of the profile.", example = "15837028-645a-4a55-9aaf-ceb846439eba")
  public String id;

  /**
   * The name of the user.
   */
  @Schema(description = "The name of the user.")
  public UserName name;

  /**
   * The date of birth of the user.
   */
  @Schema(description = "The date of birth of the user.")
  public AliveBirthDate dateOfBirth;

  /**
   * The gender of the user.
   */
  @Schema(description = "The gender of the user", example = "F")
  public Gender gender;

  /**
   * The email of the user.
   */
  @Schema(description = "The email of the user", example = "jonnyd@internetofus.eu")
  public String email;

  /**
   * The phone number of the user, on the E.164 format (^\+?[1-9]\d{1,14}$).
   */
  @Schema(description = "The phone number of the user, on the E.164 format(^\\+?[1-9]\\d{1,14}$)", example = "+34987654321")
  public String phoneNumber;

  /**
   * The email of the user.
   */
  @Schema(description = "The locale of the user", example = "es_ES")
  public String locale;

  /**
   * The avatar of the user.
   */
  @Schema(description = "The URL to an image that represents the avatar of the user.", example = "https://internetofus.eu/wp-content/uploads/sites/38/2019/02/WeNet_logo.png")
  public String avatar;

  /**
   * The email of the user.
   */
  @Schema(description = "The nationality of the user", example = "Spanish")
  public String nationality;

  /**
   * The email of the user.
   */
  @Schema(description = "The occupation of the user", example = "nurse")
  public String occupation;

  /**
   * The individual norms of the user
   */
  @ArraySchema(schema = @Schema(implementation = Norm.class), arraySchema = @Schema(description = "The individual norms of the user"))
  public List<Norm> norms;

  /**
   * The planned activities of the user.
   */
  @ArraySchema(schema = @Schema(implementation = PlannedActivity.class), arraySchema = @Schema(description = "The planned activities of the user"))
  public List<PlannedActivity> plannedActivities;

  /**
   * The locations of interest for the user.
   */
  @ArraySchema(schema = @Schema(implementation = RelevantLocation.class), arraySchema = @Schema(description = "The locations of interest for the user"))
  public List<RelevantLocation> relevantLocations;

  /**
   * The user relationships.
   */
  @ArraySchema(schema = @Schema(implementation = SocialNetworkRelationship.class), arraySchema = @Schema(description = "The user relationships with other WeNet users."))
  public List<SocialNetworkRelationship> relationships;

  /**
   * The user social practices.
   */
  @ArraySchema(schema = @Schema(implementation = SocialPractice.class), arraySchema = @Schema(description = "The user social practices"))
  public List<SocialPractice> socialPractices;

  /**
   * The user routines.
   */
  @ArraySchema(schema = @Schema(implementation = Routine.class), arraySchema = @Schema(description = "The user routines"))
  public List<Routine> personalBehaviors;

  /**
   * The materials of the user.
   */
  @ArraySchema(schema = @Schema(implementation = Material.class), arraySchema = @Schema(description = "The materials that has the user"))
  public List<Material> materials;

  /**
   * The competences of the user.
   */
  @ArraySchema(schema = @Schema(implementation = Competence.class), arraySchema = @Schema(description = "The competences of the user"))
  public List<Competence> competences;

  /**
   * The meanings of the user.
   */
  @ArraySchema(schema = @Schema(implementation = Meaning.class), arraySchema = @Schema(description = "The meanings of the user"))
  public List<Meaning> meanings;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    Future<Void> future = promise.future();
    try {

      this.id = Validations.validateNullableStringField(codePrefix, "id", 255, this.id);
      if (this.id != null) {

        future = future.compose(mapper -> {

          final Promise<Void> verifyNotRepeatedIdPromise = Promise.promise();
          WeNetProfileManager.createProxy(vertx).retrieveProfile(this.id, profile -> {

            if (profile.failed()) {

              verifyNotRepeatedIdPromise.complete();

            } else {

              verifyNotRepeatedIdPromise.fail(new ValidationErrorException(codePrefix + ".id", "The user '" + this.id + "' has already a profile."));
            }
          });
          return verifyNotRepeatedIdPromise.future();
        });
      }

      if (this.name != null) {

        future = future.compose(mapper -> this.name.validate(codePrefix + ".name", vertx));
      }
      if (this.dateOfBirth != null) {

        future = future.compose(mapper -> this.dateOfBirth.validate(codePrefix + ".dateOfBirth", vertx));

      }
      // Gender not verified because is a enumeration and this fix the possible values
      this.email = Validations.validateNullableEmailField(codePrefix, "email", this.email);
      this.locale = Validations.validateNullableLocaleField(codePrefix, "locale", this.locale);
      this.phoneNumber = Validations.validateNullableTelephoneField(codePrefix, "phoneNumber", this.locale, this.phoneNumber);
      this.avatar = Validations.validateNullableURLField(codePrefix, "avatar", this.avatar);
      this.nationality = Validations.validateNullableStringField(codePrefix, "nationality", 255, this.nationality);
      this.occupation = Validations.validateNullableStringField(codePrefix, "occupation", 255, this.occupation);
      future = future.compose(Validations.validate(this.norms, (a, b) -> a.equals(b), codePrefix + ".norms", vertx));
      future = future.compose(Validations.validate(this.plannedActivities, (a, b) -> a.equals(b), codePrefix + ".plannedActivities", vertx));
      future = future.compose(Validations.validate(this.relevantLocations, (a, b) -> a.equals(b), codePrefix + ".relevantLocations", vertx));
      future = future.compose(Validations.validate(this.relationships, (a, b) -> a.equals(b), codePrefix + ".relationships", vertx));
      future = future.compose(Validations.validate(this.socialPractices, (a, b) -> a.equals(b), codePrefix + ".socialPractices", vertx));
      future = future.compose(Validations.validate(this.personalBehaviors, (a, b) -> a.equals(b), codePrefix + ".personalBehaviors", vertx));
      future = future.compose(Validations.validate(this.materials, (a, b) -> a.equals(b), codePrefix + ".materials", vertx));
      future = future.compose(Validations.validate(this.competences, (a, b) -> a.equals(b), codePrefix + ".competences", vertx));
      future = future.compose(Validations.validate(this.meanings, (a, b) -> a.equals(b), codePrefix + ".meanings", vertx));

      promise.complete();

    } catch (final ValidationErrorException validationError) {

      promise.fail(validationError);
    }

    return future;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<WeNetUserProfile> merge(final WeNetUserProfile source, final String codePrefix, final Vertx vertx) {

    final Promise<WeNetUserProfile> promise = Promise.promise();
    Future<WeNetUserProfile> future = promise.future();

    try {

      final WeNetUserProfile merged = new WeNetUserProfile();
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

      merged.occupation = Validations.validateNullableStringField(codePrefix, "occupation", 255, source.occupation);
      if (merged.occupation == null) {

        merged.occupation = this.occupation;
      }

      merged.relationships = source.relationships;
      if (merged.relationships == null) {

        merged.relationships = this.relationships;
      }

      future = future.compose(Merges.validateMerged(codePrefix, vertx));

      future = future.compose(Merges.mergeField(this.name, source.name, codePrefix + ".name", vertx, (model, mergedName) -> model.name = mergedName));

      future = future.compose(Merges.mergeField(this.dateOfBirth, source.dateOfBirth, codePrefix + ".dateOfBirth", vertx, (model, mergedDateOfBirth) -> model.dateOfBirth = (AliveBirthDate) mergedDateOfBirth));

      future = future.compose(Merges.mergeNorms(this.norms, source.norms, codePrefix + ".norms", vertx, (model, mergedNorms) -> {
        model.norms = mergedNorms;
      }));

      future = future.compose(Merges.mergePlannedActivities(this.plannedActivities, source.plannedActivities, codePrefix + ".plannedActivities", vertx, (model, mergedPlannedActivities) -> {
        merged.plannedActivities = mergedPlannedActivities;
      }));

      future = future.compose(Merges.mergeRelevantLocations(this.relevantLocations, source.relevantLocations, codePrefix + ".relevantLocations", vertx, (model, mergedRelevantLocations) -> {
        model.relevantLocations = mergedRelevantLocations;
      }));

      future = future.compose(Merges.mergeSocialPractices(this.socialPractices, source.socialPractices, codePrefix + ".socialPractices", vertx, (model, mergedSocialPractices) -> {
        model.socialPractices = mergedSocialPractices;
      }));

      future = future.compose(Merges.mergeRoutines(this.personalBehaviors, source.personalBehaviors, codePrefix + ".personalBehaviors", vertx, (model, mergedPersonalBehaviors) -> {
        model.personalBehaviors = mergedPersonalBehaviors;
      }));

      future = future.compose(Merges.mergeMaterials(this.materials, source.materials, codePrefix + ".materials", vertx, (model, mergedMaterials) -> {
        model.materials = mergedMaterials;
      }));

      future = future.compose(Merges.mergeCompetences(this.competences, source.competences, codePrefix + ".competences", vertx, (model, mergedCompetences) -> {
        model.competences = mergedCompetences;
      }));

      future = future.compose(Merges.mergeMeanings(this.meanings, source.meanings, codePrefix + ".meanings", vertx, (model, mergedMeanings) -> {
        model.meanings = mergedMeanings;
      }));

      promise.complete(merged);

      // When merged set the fixed field values
      future = future.map(mergedValidatedModel -> {

        mergedValidatedModel.id = this.id;
        mergedValidatedModel._creationTs = this._creationTs;
        mergedValidatedModel._lastUpdateTs = this._lastUpdateTs;
        return mergedValidatedModel;
      });

      return future;

    } catch (final ValidationErrorException validationError) {

      promise.fail(validationError);
    }

    return future;
  }

}