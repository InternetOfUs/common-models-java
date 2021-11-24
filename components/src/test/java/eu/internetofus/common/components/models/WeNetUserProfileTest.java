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

import static eu.internetofus.common.model.MergeAsserts.assertCanMerge;
import static eu.internetofus.common.model.MergeAsserts.assertCannotMerge;
import static eu.internetofus.common.model.UpdateAsserts.assertCanUpdate;
import static eu.internetofus.common.model.UpdateAsserts.assertCannotUpdate;
import static eu.internetofus.common.model.ValidableAsserts.assertIsNotValid;
import static eu.internetofus.common.model.ValidableAsserts.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.WeNetIntegrationExtension;
import eu.internetofus.common.components.WeNetValidateContext;
import eu.internetofus.common.components.service.App;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ModelTestCase;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test the {@link WeNetUserProfile}.
 *
 * @see WeNetUserProfile
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetIntegrationExtension.class)
public class WeNetUserProfileTest extends ModelTestCase<WeNetUserProfile> {

  /**
   * The locales that can be used to fill in a profile.
   */
  public static final String[] LOCALES = { "ca", "es", "en", "it", "zh", "mn" };

  /**
   * The nationalities that can be used to fill in a profile.
   */
  public static final String[] NATIONALITIES = { "Italian", "Mexican", "Denmark", "Mongol", "Chinese", "Chilen" };

  /**
   * The occupations that can be used to fill in a profile.
   */
  public static final String[] OCCUPATIONS = { "Scientific", "Student", "Waiter", "Cooker", "Teacher", "Unemployed" };

  /**
   * The departments that can be used to fill in a profile.
   */
  public static final String[] DEPARTMENTS = { "Department of Accounting", "Department of Anthropology",
      "Department of Economics", "Department of Economic History", "European Institute", "Department of Finance",
      "Department of Gender Studies", "Department of Geography and Environment", "Institute of Global Affairs (IGA)",
      "Department of Government", "Department of Health Policy", "Department of International Development",
      "Department of International History", "International Inequalities Institute",
      "Department of International Relations", "Language Centre", "Department of Law", "Department of Management",
      "Marshall Institute", "Department of Mathematics", "Department of Media and Communications",
      "Department of Methodology", "Department of Philosophy, Logic and Scientific Method",
      "Department of Psychological and Behavioural Science",
      "School of Public Policy (formerly Institute of Public Affairs)", "Department of Social Policy",
      "Department of Sociology", "Department of Statistics" };

  /**
   * The degrees that can be used to fill in a profile.
   */
  public static final String[] DEGREES = { "01 Undergraduate year 1", "02 Undergraduate year 2",
      "03 Undergraduate year 3", "04 Undergraduate year 4", "05 MSc/MA", "06 MPhil/MRes/PhD" };

  /**
   * The live places that can be used to fill in a profile.
   */
  public static final String[] LIVE_PLACES = { "01: Hall of residence", "02: Private shared accommodation",
      "03: With family and/or relatives", "04: Other" };

  /**
   * Create an basic model that has the specified index.
   *
   * @param index to use in the example.
   *
   * @return the basic example.
   */
  public WeNetUserProfile createBasicExample(final int index) {

    final var model = new WeNetUserProfile();
    model.id = null;
    model.name = new UserNameTest().createModelExample(index);
    model.dateOfBirth = new AliveBirthDateTest().createModelExample(index);
    model.gender = WeNetUserProfile.GENDERS[index % WeNetUserProfile.GENDERS.length];
    model.email = "user" + index + "@internetofus.eu";
    model.phoneNumber = "+34 987 65 43 " + (10 + index % 90);
    model.locale = LOCALES[index % LOCALES.length];
    model.avatar = "https://internetofus.eu/wp-content/uploads/sites/38/2019/" + index + "/WeNet_logo.png";
    model.nationality = NATIONALITIES[index % NATIONALITIES.length];
    model.norms = new ArrayList<>();
    model.norms.add(new ProtocolNormTest().createModelExample(index));
    model.occupation = OCCUPATIONS[index % OCCUPATIONS.length];
    model.hasLocations = index % 2 == 0;
    model._creationTs = 1234567891 + index;
    model._lastUpdateTs = 1234567991 + index * 2;
    return model;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public WeNetUserProfile createModelExample(final int index) {

    final var model = this.createBasicExample(index);
    model.norms = new ArrayList<>();
    model.norms.add(new ProtocolNormTest().createModelExample(index));
    model.plannedActivities = new ArrayList<>();
    model.plannedActivities.add(new PlannedActivityTest().createModelExample(index));
    model.relevantLocations = new ArrayList<>();
    model.relevantLocations.add(new RelevantLocationTest().createModelExample(index));
    model.relationships = null;
    model.personalBehaviors = null;
    model.materials = this.createAnswerMaterials(index);
    model.competences = this.createAnswerCompetences(index);
    model.meanings = this.createAnswerMeanings(index);
    model._creationTs = 1234567891 + index;
    model._lastUpdateTs = 1234567991 + index * 2;
    return model;

  }

  /**
   * Create the materials obtained by the survey.
   *
   * @param index of the answers to create.
   *
   * @return the answers of the materials.
   */
  public List<Material> createAnswerMaterials(final int index) {

    final List<Material> materials = new ArrayList<>();
    // Q03
    var material = new Material();
    material.classification = "university_status";
    material.name = "department";
    material.description = DEPARTMENTS[index % DEPARTMENTS.length];
    material.quantity = 1;
    materials.add(material);

    // Q04
    material = new Material();
    material.classification = "university_status";
    material.name = "degree_programme";
    material.description = DEGREES[index % DEGREES.length];
    material.quantity = 1;
    materials.add(material);

    // Q05
    material = new Material();
    material.classification = "university_status";
    material.name = "accommodation";
    material.description = LIVE_PLACES[index % LIVE_PLACES.length];
    material.quantity = 1;
    materials.add(material);

    return materials;
  }

  /**
   * Create the competences obtained by the survey.
   *
   * @param index of the answers to create.
   *
   * @return the answers of the competences.
   */
  public List<Competence> createAnswerCompetences(final int index) {

    final List<Competence> competences = new ArrayList<>();
    // Q06a - Q06o
    for (final var name : new String[] { "c_food", "c_eating", "c_lit", "c_creatlit", "c_app_mus", "c_perf_mus",
        "c_plays", "c_perf_plays", "c_musgall", "c_perf_art", "c_watch_sp", "c_ind_sp", "c_team_sp", "c_accom",
        "c_locfac" }) {

      final var competence = new Competence();
      competence.ontology = "interest";
      competence.name = name;
      competence.level = 0.25 * (index % 5);
      competences.add(competence);

    }

    // Q07a-Q07h
    for (final var name : new String[] { "u_active", "u_read", "u_essay", "u_org", "u_balance", "u_assess", "u_theory",
        "u_pract" }) {

      final var competence = new Competence();
      competence.ontology = "university_activity";
      competence.name = name;
      competence.level = 0.25 * (index % 5);
      competences.add(competence);

    }

    return competences;
  }

  /**
   * Create the meanings obtained by the survey.
   *
   * @param index of the answers to create.
   *
   * @return the answers of the meanings.
   */
  public List<Meaning> createAnswerMeanings(final int index) {

    final List<Meaning> meanings = new ArrayList<>();

    // Q08a - Q08r
    for (final var name : new String[] { "excitement", "promotion", "existence", "suprapersonal", "interactive",
        "normative" }) {

      final var meaning = new Meaning();
      meaning.category = "guiding_principles";
      meaning.name = name;
      meaning.level = 0.25 * (index % 5);
      meanings.add(meaning);

    }

    // Q09a - Q09t
    for (final var name : new String[] { "extraversion", "agreeableness", "consientiousness", "neuroticism",
        "openness" }) {

      final var meaning = new Meaning();
      meaning.category = "big_five";
      meaning.name = name;
      meaning.level = 0.25 * (index % 5);
      meanings.add(meaning);

    }

    return meanings;
  }

  /**
   * Create an example model that has the specified index.
   *
   * @param index       to use in the example.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @return the created profile.
   */
  public Future<WeNetUserProfile> createModelExample(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    return testContext
        .assertComplete(StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).compose(stored1 ->

        StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).compose(stored2 -> {

          final var profile = this.createModelExample(index);

          final var activity = new PlannedActivityTest().createModelExample(index);
          activity.attendees = new ArrayList<>();
          activity.attendees.add(stored1.id);
          activity.attendees.add(stored2.id);
          profile.plannedActivities.add(activity);
          profile.relationships = new ArrayList<>();
          profile.relationships.add(new SocialNetworkRelationshipTest().createModelExample(index));
          profile.relationships.get(0).userId = stored1.id;
          profile.relationships.add(new SocialNetworkRelationshipTest().createModelExample(index + 1));
          profile.relationships.get(1).userId = stored2.id;
          profile.personalBehaviors = new ArrayList<>();
          profile.personalBehaviors.add(new RoutineTest().createModelExample(index));
          profile.personalBehaviors.get(0).user_id = stored2.id;
          return StoreServices.storeApp(new App(), vertx, testContext).map(app -> {

            profile.relationships.get(0).appId = app.appId;
            profile.relationships.get(1).appId = app.appId;
            return profile;
          });

        })));

  }

  /**
   * Check that an empty model is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldEmptyModelBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    testContext.assertComplete(model.validate(new WeNetValidateContext("codePrefix", vertx)))
        .onComplete(result -> testContext.completeNow());

  }

  /**
   * Check that the {@link #createModelExample(int)} is valid.
   *
   * @param index       to verify
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(WeNetValidateContext)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(index);
    assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that the {@link #createModelExample(int, Vertx, VertxTestContext)} is
   * valid.
   *
   * @param index       to verify
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(WeNetValidateContext)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleFromRepositoryBeValid(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.createModelExample(index, vertx, testContext))
        .onSuccess(model -> assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext));

  }

  /**
   * Check that the model with id is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithAnExistingId(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(created -> {

      final var model = new WeNetUserProfile();
      model.id = created.id;
      assertIsNotValid(model, "id", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that the model with an unique id.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldBeValidWithAnNewId(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.id = UUID.randomUUID().toString();
    assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that the birth date is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithABadBirthDate(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.dateOfBirth = new AliveBirthDateTest().createModelExample(1);
    model.dateOfBirth.month = 0;
    assertIsNotValid(model, "dateOfBirth.month", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that the birth date is not on the future.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithABirthDateOnTheFuture(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.dateOfBirth = new AliveBirthDate();
    final var tomorrow = LocalDate.now().plusDays(1);
    model.dateOfBirth.year = tomorrow.getYear();
    model.dateOfBirth.month = (byte) tomorrow.getMonthValue();
    model.dateOfBirth.day = (byte) tomorrow.getDayOfMonth();
    assertIsNotValid(model, "dateOfBirth", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that the birth date is not before the oldest people on world.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithABirthDateBeforeTheBirthDateOldestPersonOnWorld(final Vertx vertx,
      final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.dateOfBirth = new AliveBirthDate();
    model.dateOfBirth.year = 1903;
    model.dateOfBirth.month = 1;
    model.dateOfBirth.day = 1;
    assertIsNotValid(model, "dateOfBirth", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that not accept profiles with bad email address.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithABadEmail(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.email = " bad email @ adrress ";
    assertIsNotValid(model, "email", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that not accept profiles with bad locale.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithABadLocale(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.locale = " bad locale";
    assertIsNotValid(model, "locale", new WeNetValidateContext("codePrefix", vertx), testContext);
  }

  /**
   * Check that not accept profiles with bad phone number.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithABadPhoneNumber(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.phoneNumber = " bad phone number";
    assertIsNotValid(model, "phoneNumber", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that not accept profiles with bad avatar address.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithABadAvatar(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.avatar = " bad avatar";
    assertIsNotValid(model, "avatar", new WeNetValidateContext("codePrefix", vertx), testContext);
  }

  /**
   * Check that not accept profiles with bad norms.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithABadNorms(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.norms = new ArrayList<>();
    model.norms.add(new ProtocolNormTest().createModelExample(0));
    model.norms.add(new ProtocolNorm());
    model.norms.add(new ProtocolNormTest().createModelExample(2));
    assertIsNotValid(model, "norms[1].whenever", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that not accept profiles with bad planned activities.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithABadPlannedActivities(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.plannedActivities = new ArrayList<>();
    model.plannedActivities.add(new PlannedActivity());
    model.plannedActivities.add(new PlannedActivity());
    model.plannedActivities.add(new PlannedActivity());
    model.plannedActivities.get(1).attendees = new ArrayList<>();
    model.plannedActivities.get(1).attendees.add("undefined");
    assertIsNotValid(model, "plannedActivities[1].attendees[0]", new WeNetValidateContext("codePrefix", vertx),
        testContext);

  }

  /**
   * Check that not accept profiles with bad relevant locations.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithABadRelevantLocations(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.relevantLocations = new ArrayList<>();
    model.relevantLocations.add(new RelevantLocationTest().createModelExample(1));
    model.relevantLocations.add(new RelevantLocationTest().createModelExample(2));
    model.relevantLocations.add(new RelevantLocationTest().createModelExample(3));
    model.relevantLocations.get(1).latitude = 1988d;
    assertIsNotValid(model, "relevantLocations[1].latitude", new WeNetValidateContext("codePrefix", vertx),
        testContext);

  }

  /**
   * Check that not accept profiles with bad relationships.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithABadRelationships(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.relationships = new ArrayList<>();
    model.relationships.add(new SocialNetworkRelationship());
    assertIsNotValid(model, "relationships[0].type", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that not accept profiles with duplicated relationships.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithADuplicatedRelationships(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(stored -> {

      new SocialNetworkRelationshipTest().createModelExample(0, vertx, testContext)
          .onComplete(testContext.succeeding(relation -> {

            final var model = new WeNetUserProfile();
            model.relationships = new ArrayList<>();
            model.relationships.add(relation);
            model.relationships.add(Model.fromJsonObject(relation.toJsonObject(), SocialNetworkRelationship.class));
            assertIsNotValid(model, "relationships[1]", new WeNetValidateContext("codePrefix", vertx), testContext);

          }));

    });

  }

  /**
   * Check that is valid with some relationships.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldBeValidWithSomeRelationships(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(stored -> {

      new SocialNetworkRelationshipTest().createModelExample(0, vertx, testContext)
          .onComplete(testContext.succeeding(relation -> {

            final var model = new WeNetUserProfile();
            model.relationships = new ArrayList<>();
            model.relationships.add(relation);
            model.relationships.add(
                Model.fromJsonObject(relation.toJsonObject().put("type", "family"), SocialNetworkRelationship.class));
            assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext);

          }));

    });

  }

  /**
   * Check that not accept profiles with bad personal behaviors.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithABadPersonalBehaviors(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.personalBehaviors = new ArrayList<>();
    model.personalBehaviors.add(new Routine());
    model.personalBehaviors.add(new Routine());
    model.personalBehaviors.add(new Routine());
    assertIsNotValid(model, "personalBehaviors[1]", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that not accept profiles with bad materials.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithABadMaterials(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.materials = new ArrayList<>();
    model.materials.add(new MaterialTest().createModelExample(1));
    model.materials.add(new Material());
    assertIsNotValid(model, "materials[1].name", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that not accept profiles with bad competences.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithABadCompetences(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.competences = new ArrayList<>();
    model.competences.add(new CompetenceTest().createModelExample(1));
    model.competences.add(new Competence());
    assertIsNotValid(model, "competences[1].name", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that not accept profiles with bad meanings.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithABadMeanings(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.meanings = new ArrayList<>();
    model.meanings.add(new MeaningTest().createModelExample(1));
    model.meanings.add(new Meaning());
    assertIsNotValid(model, "meanings[1].level", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that the birth date is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithABadBirthDate(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.dateOfBirth = new AliveBirthDateTest().createModelExample(1);
    source.dateOfBirth.month = 13;
    assertCannotMerge(new WeNetUserProfile(), source, "dateOfBirth.month",
        new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that the birth date is not on the future.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithABirthDateOnTheFuture(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.dateOfBirth = new AliveBirthDate();
    final var tomorrow = LocalDate.now().plusDays(1);
    source.dateOfBirth.year = tomorrow.getYear();
    source.dateOfBirth.month = (byte) tomorrow.getMonthValue();
    source.dateOfBirth.day = (byte) tomorrow.getDayOfMonth();
    assertCannotMerge(new WeNetUserProfile(), source, "dateOfBirth", new WeNetValidateContext("codePrefix", vertx),
        testContext);

  }

  /**
   * Check that the birth date is not before the oldest people on world.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithABirthDateBeforeTheBirthDateOldestPersonOnWorld(final Vertx vertx,
      final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.dateOfBirth = new AliveBirthDate();
    source.dateOfBirth.year = 1903;
    source.dateOfBirth.month = 1;
    source.dateOfBirth.day = 1;
    assertCannotMerge(new WeNetUserProfile(), source, "dateOfBirth", new WeNetValidateContext("codePrefix", vertx),
        testContext);

  }

  /**
   * Check that not accept profiles with bad email address.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithABadEmail(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.email = " bad email @ adrress ";
    assertCannotMerge(new WeNetUserProfile(), source, "email", new WeNetValidateContext("codePrefix", vertx),
        testContext);

  }

  /**
   * Check that not accept profiles with bad locale.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithABadLocale(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.locale = " bad locale";
    assertCannotMerge(new WeNetUserProfile(), source, "locale", new WeNetValidateContext("codePrefix", vertx),
        testContext);
  }

  /**
   * Check that not accept profiles with bad phone number.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithABadPhoneNumber(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.phoneNumber = " bad phone number";
    assertCannotMerge(new WeNetUserProfile(), source, "phoneNumber", new WeNetValidateContext("codePrefix", vertx),
        testContext);

  }

  /**
   * Check that not accept profiles with bad avatar address.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithABadAvatar(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.avatar = " bad avatar";
    assertCannotMerge(new WeNetUserProfile(), source, "avatar", new WeNetValidateContext("codePrefix", vertx),
        testContext);
  }

  /**
   * Check that not accept profiles with bad norms.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithABadNorms(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.norms = new ArrayList<>();
    source.norms.add(new ProtocolNormTest().createModelExample(0));
    source.norms.add(new ProtocolNorm());
    source.norms.add(new ProtocolNormTest().createModelExample(1));
    assertCannotMerge(new WeNetUserProfile(), source, "norms[1].whenever",
        new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that not merge profiles with duplicated norms.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithADuplicatedNorms(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.norms = new ArrayList<>();
    source.norms.add(new ProtocolNormTest().createModelExample(0));
    source.norms.add(new ProtocolNormTest().createModelExample(1));
    source.norms.add(new ProtocolNormTest().createModelExample(1));
    final var target = new WeNetUserProfile();
    target.norms = new ArrayList<>();
    target.norms.add(new ProtocolNormTest().createModelExample(3));
    assertCannotMerge(target, source, "norms[2]", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check merge model norms.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeWithNorms(final Vertx vertx, final VertxTestContext testContext) {

    final var target = new WeNetUserProfile();
    target.norms = new ArrayList<>();
    target.norms.add(new ProtocolNormTest().createModelExample(1));
    final var source = new WeNetUserProfile();
    source.norms = new ArrayList<>();
    source.norms.add(new ProtocolNormTest().createModelExample(0));
    source.norms.add(new ProtocolNormTest().createModelExample(1));
    source.norms.add(new ProtocolNormTest().createModelExample(2));
    assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

      assertThat(merged.norms).isNotEqualTo(target.norms).isEqualTo(source.norms);

    });

  }

  /**
   * Check that not accept profiles with bad planned activities.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithABadPlannedActivities(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.plannedActivities = new ArrayList<>();
    source.plannedActivities.add(new PlannedActivity());
    source.plannedActivities.add(new PlannedActivity());
    source.plannedActivities.add(new PlannedActivity());
    source.plannedActivities.get(1).attendees = new ArrayList<>();
    source.plannedActivities.get(1).attendees.add("undefined");
    assertCannotMerge(new WeNetUserProfile(), source, "plannedActivities[1].attendees[0]",
        new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that not merge profiles with a duplicated planned activity id.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithDuplicatedPlannedActivityId(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.plannedActivities = new ArrayList<>();
    source.plannedActivities.add(new PlannedActivityTest().createModelExample(1));
    source.plannedActivities.add(new PlannedActivityTest().createModelExample(2));
    source.plannedActivities.add(new PlannedActivityTest().createModelExample(3));
    source.plannedActivities.get(0).id = "1";
    source.plannedActivities.get(1).id = "1";
    assertCannotMerge(new WeNetUserProfile(), source, "plannedActivities[1]",
        new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check merge planned activities profiles.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeWithPlannedActivities(final Vertx vertx, final VertxTestContext testContext) {

    final var target = new WeNetUserProfile();
    target.plannedActivities = new ArrayList<>();
    target.plannedActivities.add(new PlannedActivity());
    target.plannedActivities.get(0).id = "1";
    final var source = new WeNetUserProfile();
    source.plannedActivities = new ArrayList<>();
    source.plannedActivities.add(new PlannedActivity());
    source.plannedActivities.add(new PlannedActivity());
    source.plannedActivities.add(new PlannedActivity());
    source.plannedActivities.get(1).id = "1";
    assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

      assertThat(merged.plannedActivities).isNotEqualTo(target.plannedActivities).isEqualTo(source.plannedActivities);
      assertThat(merged.plannedActivities.get(0).id).isNotEmpty();
      assertThat(merged.plannedActivities.get(1).id).isEqualTo("1");
      assertThat(merged.plannedActivities.get(2).id).isNotEmpty();

    });

  }

  /**
   * Check that not accept profiles with bad relevant locations.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithABadRelevantLocations(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.relevantLocations = new ArrayList<>();
    source.relevantLocations.add(new RelevantLocationTest().createModelExample(0));
    source.relevantLocations.add(new RelevantLocationTest().createModelExample(1));
    source.relevantLocations.add(new RelevantLocationTest().createModelExample(2));
    source.relevantLocations.get(1).latitude = 1988D;
    assertCannotMerge(new WeNetUserProfile(), source, "relevantLocations[1].latitude",
        new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that not merge profiles with duplicated relevant location identifiers.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithADuplicatedRelevantLocationIds(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.relevantLocations = new ArrayList<>();
    source.relevantLocations.add(new RelevantLocationTest().createModelExample(0));
    source.relevantLocations.add(new RelevantLocationTest().createModelExample(1));
    source.relevantLocations.add(new RelevantLocationTest().createModelExample(2));
    source.relevantLocations.get(1).id = "1";
    source.relevantLocations.get(2).id = "1";
    final var target = new WeNetUserProfile();
    target.relevantLocations = new ArrayList<>();
    target.relevantLocations.add(new RelevantLocation());
    target.relevantLocations.get(0).id = "1";
    assertCannotMerge(target, source, "relevantLocations[2]", new WeNetValidateContext("codePrefix", vertx),
        testContext);

  }

  /**
   * Check that not merge profiles with a duplicated relevant location id.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithDuplicatedRelevantLocationId(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.relevantLocations = new ArrayList<>();
    source.relevantLocations.add(new RelevantLocationTest().createModelExample(0));
    source.relevantLocations.add(new RelevantLocationTest().createModelExample(1));
    source.relevantLocations.add(new RelevantLocationTest().createModelExample(2));
    source.relevantLocations.get(0).id = "1";
    source.relevantLocations.get(1).id = "1";
    assertCannotMerge(new WeNetUserProfile(), source, "relevantLocations[1]",
        new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check merge relevant locations profiles.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeWithRelevantLocations(final Vertx vertx, final VertxTestContext testContext) {

    final var target = new WeNetUserProfile();
    target.relevantLocations = new ArrayList<>();
    target.relevantLocations.add(new RelevantLocationTest().createModelExample(1));
    target.relevantLocations.get(0).id = "1";
    final var source = new WeNetUserProfile();
    source.relevantLocations = new ArrayList<>();
    source.relevantLocations.add(new RelevantLocationTest().createModelExample(1));
    source.relevantLocations.add(new RelevantLocationTest().createModelExample(2));
    source.relevantLocations.add(new RelevantLocationTest().createModelExample(3));
    source.relevantLocations.get(1).id = "1";
    assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

      assertThat(merged.relevantLocations).isNotEqualTo(target.relevantLocations).isEqualTo(source.relevantLocations);
      assertThat(merged.relevantLocations.get(0).id).isNotEmpty();
      assertThat(merged.relevantLocations.get(1).id).isEqualTo("1");
      assertThat(merged.relevantLocations.get(2).id).isNotEmpty();

    });

  }

  /**
   * Check that not accept profiles with bad planned activities.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithABadRelationships(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.relationships = new ArrayList<>();
    source.relationships.add(new SocialNetworkRelationship());
    assertCannotMerge(new WeNetUserProfile(), source, "relationships[0].type",
        new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that not merge with duplicated relationships.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithDuplicatedRelationships(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(stored -> {

      new SocialNetworkRelationshipTest().createModelExample(0, vertx, testContext)
          .onComplete(testContext.succeeding(relation -> {
            final var source = new WeNetUserProfile();

            source.relationships = new ArrayList<>();
            source.relationships.add(relation);
            source.relationships.add(Model.fromJsonObject(relation.toJsonObject(), SocialNetworkRelationship.class));
            assertCannotMerge(new WeNetUserProfile(), source, "relationships[1]",
                new WeNetValidateContext("codePrefix", vertx), testContext);

          }));

    });

  }

  /**
   * Check that merge some relationships.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeRelationships(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(stored -> {

      new SocialNetworkRelationshipTest().createModelExample(0, vertx, testContext)
          .onComplete(testContext.succeeding(relation -> {

            final var target = new WeNetUserProfile();
            target.relationships = new ArrayList<>();
            target.relationships.add(new SocialNetworkRelationship());
            target.relationships.get(0).appId = relation.appId;
            target.relationships.get(0).userId = stored.id;
            target.relationships.get(0).type = SocialNetworkRelationshipType.friend;
            target.relationships.get(0).weight = 0.5;

            final var source = new WeNetUserProfile();
            source.relationships = new ArrayList<>();
            source.relationships.add(new SocialNetworkRelationship());
            source.relationships.add(relation);
            source.relationships.get(0).appId = relation.appId;
            source.relationships.get(0).userId = stored.id;
            source.relationships.get(0).type = SocialNetworkRelationshipType.family;
            source.relationships.get(0).weight = 0d;
            assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

              assertThat(merged.relationships).isNotEqualTo(target.relationships).isEqualTo(source.relationships);

            });

          }));
    });

  }

  /**
   * Check that not merge profiles with bad personal behaviors.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithABadPersonalBehaviors(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.personalBehaviors = new ArrayList<>();
    source.personalBehaviors.add(new Routine());
    assertCannotMerge(new WeNetUserProfile(), source, "personalBehaviors[0].label_distribution",
        new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check merge empty profiles.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeEmptyModels(final Vertx vertx, final VertxTestContext testContext) {

    final var target = new WeNetUserProfile();
    final var source = new WeNetUserProfile();
    assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

      assertThat(merged).isEqualTo(target);

    });

  }

  /**
   * Check merge basic profiles.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeBasicModels(final Vertx vertx, final VertxTestContext testContext) {

    final var target = new WeNetUserProfile();
    target.id = "1";
    target._creationTs = 2;
    target._lastUpdateTs = 3;
    final var source = new WeNetUserProfile();
    source.id = "4";
    source._creationTs = 5;
    source._lastUpdateTs = 6;
    assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

      assertThat(merged).isEqualTo(target).isNotEqualTo(source);

    });

  }

  /**
   * Check merge example profiles.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeExampleModels(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "1";
    final var source = this.createModelExample(2);
    source.id = "2";
    assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

      source.id = target.id;
      source._creationTs = target._creationTs;
      source._lastUpdateTs = target._lastUpdateTs;
      assertThat(merged).isNotEqualTo(target).isEqualTo(source);

    });

  }

  /**
   * Check merge stored profiles.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeStoredModels(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext)
        .compose(targetToStore -> StoreServices.storeProfile(targetToStore, vertx, testContext)
            .compose(target -> this.createModelExample(2, vertx, testContext).compose(
                sourceToStore -> StoreServices.storeProfile(sourceToStore, vertx, testContext).onSuccess(source ->

                assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

                  source.id = target.id;
                  source._creationTs = target._creationTs;
                  source._lastUpdateTs = target._lastUpdateTs;
                  assertThat(merged).isNotEqualTo(target).isEqualTo(source);

                })))));

  }

  /**
   * Check merge only the user name.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeOnlyUserName(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createBasicExample(1);
    assertIsValid(target, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

      final var source = new WeNetUserProfile();
      source.name = new UserName();
      source.name.middle = "NEW MIDDLE NAME";
      assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

        assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
        target.name.middle = "NEW MIDDLE NAME";
        assertThat(merged).isEqualTo(target);

      });

    });

  }

  /**
   * Check merge add user name.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeAddUserName(final Vertx vertx, final VertxTestContext testContext) {

    final var target = new WeNetUserProfile();
    target.id = "1";
    final var source = new WeNetUserProfile();
    source.name = new UserName();
    source.name.middle = "NEW MIDDLE NAME";
    assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.name = new UserName();
      target.name.middle = "NEW MIDDLE NAME";
      assertThat(merged).isEqualTo(target);

    });

  }

  /**
   * Check merge only the birth date.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeOnlyBirthDate(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createBasicExample(1);
    assertIsValid(target, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

      final var source = new WeNetUserProfile();
      source.dateOfBirth = new AliveBirthDate();
      source.dateOfBirth.year = 1923;
      assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

        assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
        target.dateOfBirth.year = 1923;
        assertThat(merged).isEqualTo(target);

      });

    });

  }

  /**
   * Check merge add birth date.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeAddBirthDate(final Vertx vertx, final VertxTestContext testContext) {

    final var target = new WeNetUserProfile();
    target.id = "1";
    final var source = new WeNetUserProfile();
    source.dateOfBirth = new AliveBirthDateTest().createModelExample(1);
    assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

      assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
      target.dateOfBirth = new AliveBirthDateTest().createModelExample(1);
      assertThat(merged).isEqualTo(target);

    });

  }

  /**
   * Check merge only the gender.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeOnlyGender(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createBasicExample(1);
    assertIsValid(target, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

      final var source = new WeNetUserProfile();
      source.gender = WeNetUserProfile.GENDERS[0];
      assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

        assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
        target.gender = WeNetUserProfile.GENDERS[0];
        assertThat(merged).isEqualTo(target);

      });

    });

  }

  /**
   * Check merge only the email.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeOnlyEmail(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createBasicExample(1);
    assertIsValid(target, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

      final var source = new WeNetUserProfile();
      source.email = "new@email.com";
      assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

        assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
        target.email = "new@email.com";
        assertThat(merged).isEqualTo(target);

      });

    });

  }

  /**
   * Check merge only the locale.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeOnlyLocale(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createBasicExample(1);
    assertIsValid(target, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

      final var source = new WeNetUserProfile();
      source.locale = "en_NZ";
      assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

        assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
        target.locale = "en_NZ";
        assertThat(merged).isEqualTo(target);

      });

    });

  }

  /**
   * Check merge only the phone number.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeOnlyPhoneNumber(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createBasicExample(1);
    assertIsValid(target, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

      final var source = new WeNetUserProfile();
      source.phoneNumber = "+1 412 535 2223";
      assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

        assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
        target.phoneNumber = "+1 412-535-2223";
        assertThat(merged).isEqualTo(target);

      });

    });

  }

  /**
   * Check merge only the avatar.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeOnlyAvatar(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createBasicExample(1);
    assertIsValid(target, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

      final var source = new WeNetUserProfile();
      source.avatar = "http://new-avatar.com";
      assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

        assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
        target.avatar = "http://new-avatar.com";
        assertThat(merged).isEqualTo(target);

      });

    });

  }

  /**
   * Check merge only the nationality.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeOnlyNationality(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createBasicExample(1);
    assertIsValid(target, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

      final var source = new WeNetUserProfile();
      source.nationality = "Canadian";
      assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

        assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
        target.nationality = "Canadian";
        assertThat(merged).isEqualTo(target);

      });

    });

  }

  /**
   * Check merge only the occupation.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeOnlyOccupation(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createBasicExample(1);
    assertIsValid(target, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

      final var source = new WeNetUserProfile();
      source.occupation = "Bus driver";
      assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

        assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
        target.occupation = "Bus driver";
        assertThat(merged).isEqualTo(target);

      });

    });

  }

  /**
   * Check merge remove planned activities.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeRemovePlannedActivities(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext)
        .onSuccess(created -> assertIsValid(created, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

          StoreServices.storeProfile(created, vertx, testContext).onSuccess(target -> {

            final var source = new WeNetUserProfile();
            source.plannedActivities = new ArrayList<>();
            assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

              assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
              target.plannedActivities.clear();
              assertThat(merged).isEqualTo(target);

            });

          });

        }));

  }

  /**
   * Check fail merge with a bad defined planned activity.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldFailMergeBadPlannedActivities(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext)
        .onSuccess(created -> assertIsValid(created, new WeNetValidateContext("codePrefix", vertx), testContext,
            () -> StoreServices.storeProfile(created, vertx, testContext).onSuccess(target -> {

              final var source = new WeNetUserProfile();
              source.plannedActivities = new ArrayList<>();
              source.plannedActivities.add(new PlannedActivity());
              source.plannedActivities.get(0).id = target.plannedActivities.get(0).id;
              source.plannedActivities.get(0).attendees = new ArrayList<>();
              source.plannedActivities.get(0).attendees.add("undefined");
              assertCannotMerge(target, source, "plannedActivities[0].attendees[0]",
                  new WeNetValidateContext("codePrefix", vertx), testContext);

            })));

  }

  /**
   * Check fail merge with a bad new planned activity.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldFailMergeBadNewPlannedActivities(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(created -> {

      assertIsValid(created, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

        StoreServices.storeProfile(created, vertx, testContext).onSuccess(target -> {

          final var source = new WeNetUserProfile();
          source.plannedActivities = new ArrayList<>();
          source.plannedActivities.add(new PlannedActivity());
          source.plannedActivities.get(0).attendees = new ArrayList<>();
          source.plannedActivities.get(0).attendees.add("undefined");
          assertCannotMerge(target, source, "plannedActivities[0].attendees[0]",
              new WeNetValidateContext("codePrefix", vertx), testContext);

        });

      });

    });
  }

  /**
   * Check merge add modify planned activities.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeAddmodifyPlannedActivities(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(created -> {

      assertIsValid(created, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

        StoreServices.storeProfile(created, vertx, testContext).onSuccess(target -> {

          final var source = new WeNetUserProfile();
          source.plannedActivities = new ArrayList<>();
          source.plannedActivities.add(new PlannedActivity());
          source.plannedActivities.add(new PlannedActivity());
          source.plannedActivities.get(0).id = target.plannedActivities.get(1).id;
          source.plannedActivities.get(0).description = "NEW description";
          assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

            assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
            target.plannedActivities.remove(0);
            target.plannedActivities.get(0).description = "NEW description";
            target.plannedActivities.add(new PlannedActivity());
            target.plannedActivities.get(1).id = merged.plannedActivities.get(1).id;
            assertThat(merged).isEqualTo(target);

          });

        });
      });
    });

  }

  /**
   * Check merge remove relevant locations.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeRemoveRelevantLocations(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(created -> {

      assertIsValid(created, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

        StoreServices.storeProfile(created, vertx, testContext).onSuccess(target -> {

          final var source = new WeNetUserProfile();
          source.relevantLocations = new ArrayList<>();
          assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {
            assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
            target.relevantLocations.clear();
            assertThat(merged).isEqualTo(target);
          });

        });
      });
    });
  }

  /**
   * Check fail merge with a bad defined relevant location.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  public void shouldFailMergeBadRelevantLocations(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(created -> {

      assertIsValid(created, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

        StoreServices.storeProfile(created, vertx, testContext).onSuccess(target -> {

          final var source = new WeNetUserProfile();
          source.relevantLocations = new ArrayList<>();
          source.relevantLocations.add(new RelevantLocation());
          source.relevantLocations.get(0).id = target.relevantLocations.get(0).id;
          source.relevantLocations.get(0).longitude = 1988D;
          assertCannotMerge(target, source, "relevantLocations[0].longitude",
              new WeNetValidateContext("codePrefix", vertx), testContext);
        });
      });
    });
  }

  /**
   * Check fail merge with a bad new relevant location.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  public void shouldFailMergeBadNewRelevantLocations(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(created -> {

      assertIsValid(created, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

        StoreServices.storeProfile(created, vertx, testContext).onSuccess(target -> {

          final var source = new WeNetUserProfile();
          source.relevantLocations = new ArrayList<>();
          source.relevantLocations.add(new RelevantLocation());
          source.relevantLocations.get(0).longitude = 1088D;
          assertCannotMerge(target, source, "relevantLocations[0].longitude",
              new WeNetValidateContext("codePrefix", vertx), testContext);

        });
      });
    });
  }

  /**
   * Check merge add modify relevant locations.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeAddModifyRelevantLocations(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(created -> {

      assertIsValid(created, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

        StoreServices.storeProfile(created, vertx, testContext).onSuccess(target -> {

          final var source = new WeNetUserProfile();
          source.relevantLocations = new ArrayList<>();
          source.relevantLocations.add(new RelevantLocation());
          source.relevantLocations.add(new RelevantLocationTest().createModelExample(1));
          source.relevantLocations.get(1).id = target.relevantLocations.get(0).id;
          source.relevantLocations.get(1).label = "NEW label";
          assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

            assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
            target.relevantLocations.add(0, new RelevantLocation());
            target.relevantLocations.get(0).id = merged.relevantLocations.get(0).id;
            target.relevantLocations.get(1).label = "NEW label";
            assertThat(merged).isEqualTo(target);

          });
        });
      });
    });
  }

  /**
   * Check merge remove personal behaviors.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeRemovePersonalBehaviors(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(created -> {

      assertIsValid(created, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

        StoreServices.storeProfile(created, vertx, testContext).onSuccess(target -> {

          final var source = new WeNetUserProfile();
          source.personalBehaviors = new ArrayList<>();
          assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

            assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
            target.personalBehaviors.clear();
            assertThat(merged).isEqualTo(target);
            testContext.completeNow();
          });
        });
      });
    });
  }

  /**
   * Check fail merge with a bad new personal behavior.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldFailMergeBadNewPersonalBehaviors(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(created -> {

      assertIsValid(created, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

        StoreServices.storeProfile(created, vertx, testContext).onSuccess(target -> {

          final var source = new WeNetUserProfile();
          source.personalBehaviors = new ArrayList<>();
          source.personalBehaviors.add(new Routine());
          assertCannotMerge(target, source, "personalBehaviors[0].label_distribution",
              new WeNetValidateContext("codePrefix", vertx), testContext);
        });
      });
    });
  }

  /**
   * Check merge add modify personal behaviors.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeAddModifyPersonalBehaviors(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      new RoutineTest().createModelExample(1, vertx, testContext).onSuccess(createdRoutine -> {

        final var source = new WeNetUserProfile();
        source.personalBehaviors = new ArrayList<>();
        source.personalBehaviors.add(createdRoutine);
        source.personalBehaviors.addAll(target.personalBehaviors);
        source.personalBehaviors.get(1).confidence = 0.0;
        assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

          assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
          target.personalBehaviors.add(0, createdRoutine);
          target.personalBehaviors.get(1).confidence = 0.0;
          assertThat(merged).isEqualTo(target);
        });

      });

    });
  }

  /**
   * Check that not accept profiles with bad materials.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithABadMaterials(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.materials = new ArrayList<>();
    source.materials.add(new MaterialTest().createModelExample(1));
    source.materials.add(new MaterialTest().createModelExample(2));
    source.materials.add(new MaterialTest().createModelExample(3));
    source.materials.get(1).name = null;
    assertCannotMerge(new WeNetUserProfile(), source, "materials[1].name",
        new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check merge remove materials.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeRemoveMaterials(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertIsValid(target, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

      final var source = new WeNetUserProfile();
      source.materials = new ArrayList<>();
      assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

        assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
        target.materials.clear();
        assertThat(merged).isEqualTo(target);

      });

    });

  }

  /**
   * Check merge add material and modify another.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeAddAndModifyMaterials(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.materials.clear();
    target.materials.add(new MaterialTest().createModelExample(1));
    target.materials.add(new MaterialTest().createModelExample(2));
    target.materials.add(new MaterialTest().createModelExample(3));
    assertIsValid(target, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

      final var source = new WeNetUserProfile();
      source.materials = new ArrayList<>();
      source.materials.add(new MaterialTest().createModelExample(2));
      source.materials.add(new MaterialTest().createModelExample(4));
      source.materials.add(new MaterialTest().createModelExample(3));
      source.materials.add(new MaterialTest().createModelExample(1));
      source.materials.get(0).quantity = 143;
      source.materials.get(1).quantity = 144;
      source.materials.get(2).quantity = 145;
      source.materials.get(3).quantity = 146;
      assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

        assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
        target.materials.add(target.materials.remove(0));
        target.materials.get(0).quantity = 143;
        target.materials.add(1, new MaterialTest().createModelExample(4));
        target.materials.get(1).quantity = 144;
        target.materials.get(2).quantity = 145;
        target.materials.get(3).quantity = 146;
        assertThat(merged).isEqualTo(target);

      });

    });

  }

  /**
   * Check that not accept profiles with bad competences.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithABadCompetences(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.competences = new ArrayList<>();
    source.competences.add(new CompetenceTest().createModelExample(1));
    source.competences.add(new CompetenceTest().createModelExample(2));
    source.competences.add(new CompetenceTest().createModelExample(3));
    source.competences.get(1).name = null;
    assertCannotMerge(new WeNetUserProfile(), source, "competences[1].name",
        new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check merge remove competences.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeRemoveCompetences(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertIsValid(target, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

      final var source = new WeNetUserProfile();
      source.competences = new ArrayList<>();
      assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

        assertThat(merged).isNotEqualTo(target);
        assertThat(merged).isNotEqualTo(source);
        target.competences.clear();
        assertThat(merged).isEqualTo(target);

      });

    });

  }

  /**
   * Check merge add competence and modify another.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeAddAndModifyCompetences(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.competences.clear();
    target.competences.add(0, new CompetenceTest().createModelExample(1));
    target.competences.add(1, new CompetenceTest().createModelExample(2));
    target.competences.add(2, new CompetenceTest().createModelExample(3));
    assertIsValid(target, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

      final var source = new WeNetUserProfile();
      source.competences = new ArrayList<>();
      source.competences.add(new CompetenceTest().createModelExample(2));
      source.competences.add(new CompetenceTest().createModelExample(4));
      source.competences.add(new CompetenceTest().createModelExample(3));
      source.competences.add(new CompetenceTest().createModelExample(1));
      source.competences.get(0).level = 0.143;
      source.competences.get(1).level = 0.144;
      source.competences.get(2).level = 0.145;
      source.competences.get(3).level = 0.146;
      assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

        assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
        target.competences.add(target.competences.remove(0));
        target.competences.get(0).level = 0.143;
        target.competences.add(1, new CompetenceTest().createModelExample(4));
        target.competences.get(1).level = 0.144;
        target.competences.get(2).level = 0.145;
        target.competences.get(3).level = 0.146;
        assertThat(merged).isEqualTo(target);

      });

    });

  }

  /**
   * Check that not accept profiles with bad meanings.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithABadMeanings(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.meanings = new ArrayList<>();
    source.meanings.add(new MeaningTest().createModelExample(1));
    source.meanings.add(new MeaningTest().createModelExample(2));
    source.meanings.add(new MeaningTest().createModelExample(3));
    source.meanings.get(1).name = null;
    assertCannotMerge(new WeNetUserProfile(), source, "meanings[1].name", new WeNetValidateContext("codePrefix", vertx),
        testContext);

  }

  /**
   * Check merge remove meanings.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeRemoveMeanings(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertIsValid(target, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

      final var source = new WeNetUserProfile();
      source.meanings = new ArrayList<>();
      assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

        assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
        target.meanings.clear();
        assertThat(merged).isEqualTo(target);

      });

    });

  }

  /**
   * Check merge add meaning and modify another.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeAddAndModifyMeanings(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.meanings.clear();
    target.meanings.add(new MeaningTest().createModelExample(1));
    target.meanings.add(new MeaningTest().createModelExample(2));
    target.meanings.add(new MeaningTest().createModelExample(3));
    assertIsValid(target, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

      final var source = new WeNetUserProfile();
      source.meanings = new ArrayList<>();
      source.meanings.add(new MeaningTest().createModelExample(2));
      source.meanings.add(new MeaningTest().createModelExample(4));
      source.meanings.add(new MeaningTest().createModelExample(3));
      source.meanings.add(new MeaningTest().createModelExample(1));
      source.meanings.get(0).level = 143d;
      source.meanings.get(1).level = 144d;
      source.meanings.get(2).level = 145d;
      source.meanings.get(3).level = 146d;
      assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

        assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
        target.meanings.add(target.meanings.remove(0));
        target.meanings.get(0).level = 143d;
        target.meanings.add(1, new MeaningTest().createModelExample(4));
        target.meanings.get(1).level = 144d;
        target.meanings.get(2).level = 145d;
        target.meanings.get(3).level = 146d;
        assertThat(merged).isEqualTo(target);

      });

    });

  }

  /**
   * Should merge with {@code null}
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#merge(CommunityProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeWithNull(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> assertCanMerge(target, null,
        new WeNetValidateContext("codePrefix", vertx), testContext, merged -> assertThat(merged).isSameAs(target)));

  }

  /**
   * Check that the model is not valid it has two norms with the same identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotValidWithDuplicatedNorms(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.norms = new ArrayList<>();
    model.norms.add(new ProtocolNormTest().createModelExample(1));
    model.norms.add(new ProtocolNormTest().createModelExample(1));
    assertIsNotValid(model, "norms[1]", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that the model is not valid it has two relevant locations with the same
   * identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotValidWithDuplicatedRelevantLocations(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.relevantLocations = new ArrayList<>();
    for (var i = 0; i < 2; i++) {

      model.relevantLocations.add(new RelevantLocationTest().createModelExample(i));
      model.relevantLocations.get(i).id = "Duplicated Identifier";
    }
    assertIsNotValid(model, "relevantLocations[1]", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that the model is not valid it has two relevant locations with the same
   * identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotValidWithDuplicatedPersonalBehaviors(final Vertx vertx, final VertxTestContext testContext) {

    new RoutineTest().createModelExample(1, vertx, testContext).onSuccess(routine -> {

      final var model = new WeNetUserProfile();
      model.personalBehaviors = new ArrayList<>();
      model.personalBehaviors.add(routine);
      model.personalBehaviors.add(Model.fromJsonObject(routine.toJsonObject(), Routine.class));
      assertIsNotValid(model, "personalBehaviors[1]", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Should update with {@code null}
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#update(CommunityProfile, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateWithNull(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> assertCanUpdate(target, null,
        new WeNetValidateContext("codePrefix", vertx), testContext, updated -> assertThat(updated).isSameAs(target)));

  }

  /**
   * Check that not update profiles with bad personal behaviors.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#update(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldNotUpdateWithABadPersonalBehaviors(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.personalBehaviors = new ArrayList<>();
    source.personalBehaviors.add(new Routine());
    assertCannotUpdate(new WeNetUserProfile(), source, "personalBehaviors[0].label_distribution",
        new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check update empty profiles.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#update(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateEmptyModels(final Vertx vertx, final VertxTestContext testContext) {

    final var target = new WeNetUserProfile();
    final var source = new WeNetUserProfile();
    assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {

      assertThat(updated).isEqualTo(target);

    });

  }

  /**
   * Check update basic profiles.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#update(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateBasicModels(final Vertx vertx, final VertxTestContext testContext) {

    final var target = new WeNetUserProfile();
    target.id = "1";
    target._creationTs = 2;
    target._lastUpdateTs = 3;
    final var source = new WeNetUserProfile();
    source.id = "4";
    source._creationTs = 5;
    source._lastUpdateTs = 6;
    assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {

      assertThat(updated).isEqualTo(target).isNotEqualTo(source);

    });

  }

  /**
   * Check update example profiles.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#update(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateExampleModels(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "1";
    final var source = this.createModelExample(2);
    source.id = "2";
    assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {

      source.id = target.id;
      source._creationTs = target._creationTs;
      source._lastUpdateTs = target._lastUpdateTs;
      assertThat(updated).isNotEqualTo(target).isEqualTo(source);

    });

  }

  /**
   * Check update stored profiles.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#update(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateStoredModels(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext)
        .compose(targetToStore -> StoreServices.storeProfile(targetToStore, vertx, testContext)
            .compose(target -> this.createModelExample(2, vertx, testContext)
                .compose(sourceToStore -> StoreServices.storeProfile(sourceToStore, vertx, testContext)
                    .onSuccess(source -> assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx),
                        testContext, updated -> {

                          source.id = target.id;
                          source._creationTs = target._creationTs;
                          source._lastUpdateTs = target._lastUpdateTs;
                          assertThat(updated).isNotEqualTo(target).isEqualTo(source);

                        })))));

  }

  /**
   * Check that not allow two social relationships with the same used and type.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithRelationWithTheSameUserAndType(final Vertx vertx,
      final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onComplete(testContext.succeeding(model -> {

      final var copy = Model.fromJsonObject(model.relationships.get(0).toJsonObject(), SocialNetworkRelationship.class);
      copy.weight += 0.1d;
      model.relationships.add(copy);
      assertIsNotValid(model, "relationships[2]", new WeNetValidateContext("codePrefix", vertx), testContext);

    }));

  }

}
