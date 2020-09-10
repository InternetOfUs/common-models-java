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

package eu.internetofus.common.components.profile_manager;

import static eu.internetofus.common.components.MergesTest.assertCanMerge;
import static eu.internetofus.common.components.MergesTest.assertCannotMerge;
import static eu.internetofus.common.components.UpdatesTest.assertCanUpdate;
import static eu.internetofus.common.components.UpdatesTest.assertCannotUpdate;
import static eu.internetofus.common.components.ValidationsTest.assertIsNotValid;
import static eu.internetofus.common.components.ValidationsTest.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ModelTestCase;
import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.ValidationsTest;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link WeNetUserProfile}.
 *
 * @see WeNetUserProfile
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class WeNetUserProfileTest extends ModelTestCase<WeNetUserProfile> {

  /**
   * The profile manager mocked server.
   */
  protected static WeNetProfileManagerMocker mocker;

  /**
   * Start the mocker server.
   */
  @BeforeAll
  public static void startMocker() {

    mocker = WeNetProfileManagerMocker.start();
  }

  /**
   * Register the necessary services before to test.
   *
   * @param vertx event bus to register the necessary services.
   */
  @BeforeEach
  public void registerServices(final Vertx vertx) {

    final var client = WebClient.create(vertx);
    final var conf = mocker.getComponentConfiguration();
    WeNetProfileManager.register(vertx, client, conf);

  }

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
    model.gender = Gender.F;
    model.email = "user" + index + "@internetofus.eu";
    model.phoneNumber = "+34 987 65 43 " + (10 + index % 90);
    model.locale = "ca_AD";
    model.avatar = "https://internetofus.eu/wp-content/uploads/sites/38/2019/" + index + "/WeNet_logo.png";
    model.nationality = "nationality_" + index;
    model.norms = new ArrayList<>();
    model.norms.add(new NormTest().createModelExample(index));
    model.occupation = "occupation " + index;
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
    model.norms.add(new NormTest().createModelExample(index));
    model.plannedActivities = new ArrayList<>();
    model.plannedActivities.add(new PlannedActivityTest().createModelExample(index));
    model.relevantLocations = new ArrayList<>();
    model.relevantLocations.add(new RelevantLocationTest().createModelExample(index));
    model.relationships = null;
    model.personalBehaviors = null;
    model.materials = new ArrayList<>();
    model.materials.add(new MaterialTest().createModelExample(index));
    model.competences = new ArrayList<>();
    model.competences.add(new CompetenceTest().createModelExample(index));
    model.meanings = new ArrayList<>();
    model.meanings.add(new MeaningTest().createModelExample(index));
    model._creationTs = 1234567891 + index;
    model._lastUpdateTs = 1234567991 + index * 2;
    return model;

  }

  /**
   * Create an example model that has the specified index.
   *
   * @param index         to use in the example.
   * @param vertx         event bus to use.
   * @param testContext   test context to use.
   * @param createHandler the component that will manage the created model.
   */
  public void createModelExample(final int index, final Vertx vertx, final VertxTestContext testContext, final Handler<AsyncResult<WeNetUserProfile>> createHandler) {

    StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext, testContext.succeeding(stored1 -> {

      StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext, testContext.succeeding(stored2 -> {

        final var profile = this.createModelExample(index);

        final var activity = new PlannedActivityTest().createModelExample(3);
        activity.attendees = new ArrayList<>();
        activity.attendees.add(stored1.id);
        activity.attendees.add(stored2.id);
        profile.plannedActivities.add(activity);
        profile.relationships = new ArrayList<>();
        profile.relationships.add(new SocialNetworkRelationshipTest().createModelExample(5));
        profile.relationships.get(0).userId = stored1.id;
        profile.relationships.add(new SocialNetworkRelationshipTest().createModelExample(6));
        profile.relationships.get(1).userId = stored2.id;
        profile.personalBehaviors = new ArrayList<>();
        profile.personalBehaviors.add(new RoutineTest().createModelExample(index));
        profile.personalBehaviors.get(0).user_id = stored2.id;
        createHandler.handle(Future.succeededFuture(profile));

      }));
    }));

  }

  /**
   * Check that an empty model is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldEmptyModelBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    testContext.assertComplete(model.validate("codePrefix", vertx)).onComplete(result -> testContext.completeNow());

  }

  /**
   * Check that the {@link #createModelExample(int)} is valid.
   *
   * @param index       to verify
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(index);
    assertIsValid(model, vertx, testContext);

  }

  /**
   * Check that the {@link #createModelExample(int, Vertx, VertxTestContext, Handler)} is valid.
   *
   * @param index       to verify
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleFromRepositoryBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(index, vertx, testContext, testContext.succeeding(model -> {

      assertIsValid(model, vertx, testContext);

    }));

  }

  /**
   * Check that the model with id is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithAnExistingId(final Vertx vertx, final VertxTestContext testContext) {

    WeNetProfileManager.createProxy(vertx).createProfile(new JsonObject(), testContext.succeeding(created -> {

      final var model = new WeNetUserProfile();
      model.id = created.getString("id");
      assertIsNotValid(model, "id", vertx, testContext);

    }));

  }

  /**
   * Check that the model with an unique id.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldBeValidWithAnNewId(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.id = UUID.randomUUID().toString();
    assertIsValid(model, vertx, testContext);

  }

  /**
   * Check that the name is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadName(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.name = new UserNameTest().createModelExample(1);
    model.name.first = ValidationsTest.STRING_256;
    assertIsNotValid(model, "name.first", vertx, testContext);

  }

  /**
   * Check that the birth date is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadBirthDate(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.dateOfBirth = new AliveBirthDateTest().createModelExample(1);
    model.dateOfBirth.month = 0;
    assertIsNotValid(model, "dateOfBirth.month", vertx, testContext);

  }

  /**
   * Check that the birth date is not on the future.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABirthDateOnTheFuture(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.dateOfBirth = new AliveBirthDate();
    final var tomorrow = LocalDate.now().plusDays(1);
    model.dateOfBirth.year = tomorrow.getYear();
    model.dateOfBirth.month = (byte) tomorrow.getMonthValue();
    model.dateOfBirth.day = (byte) tomorrow.getDayOfMonth();
    assertIsNotValid(model, "dateOfBirth", vertx, testContext);

  }

  /**
   * Check that the birth date is not before the oldest people on world.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABirthDateBeforeTheBirthDateOldestPersonOnWorld(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.dateOfBirth = new AliveBirthDate();
    model.dateOfBirth.year = 1903;
    model.dateOfBirth.month = 1;
    model.dateOfBirth.day = 1;
    assertIsNotValid(model, "dateOfBirth", vertx, testContext);

  }

  /**
   * Check that not accept profiles with bad email address.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadEmail(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.email = " bad email @ adrress ";
    assertIsNotValid(model, "email", vertx, testContext);

  }

  /**
   * Check that not accept profiles with bad locale.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadLocale(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.locale = " bad locale";
    assertIsNotValid(model, "locale", vertx, testContext);
  }

  /**
   * Check that not accept profiles with bad phone number.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadPhoneNumber(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.phoneNumber = " bad phone number";
    assertIsNotValid(model, "phoneNumber", vertx, testContext);

  }

  /**
   * Check that not accept profiles with bad avatar address.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadAvatar(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.avatar = " bad avatar";
    assertIsNotValid(model, "avatar", vertx, testContext);
  }

  /**
   * Check that not accept profiles with bad nationality.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadNationality(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.nationality = ValidationsTest.STRING_256;
    assertIsNotValid(model, "nationality", vertx, testContext);

  }

  /**
   * Check that not accept profiles with bad occupation.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadOccupation(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.occupation = ValidationsTest.STRING_256;
    assertIsNotValid(model, "occupation", vertx, testContext);

  }

  /**
   * Check that not accept profiles with bad norms.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadNorms(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.norms = new ArrayList<>();
    model.norms.add(new Norm());
    model.norms.add(new Norm());
    model.norms.add(new Norm());
    model.norms.get(1).attribute = ValidationsTest.STRING_256;
    assertIsNotValid(model, "norms[1].attribute", vertx, testContext);

  }

  /**
   * Check that not accept profiles with bad planned activities.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadPlannedActivities(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.plannedActivities = new ArrayList<>();
    model.plannedActivities.add(new PlannedActivity());
    model.plannedActivities.add(new PlannedActivity());
    model.plannedActivities.add(new PlannedActivity());
    model.plannedActivities.get(1).description = ValidationsTest.STRING_256;
    assertIsNotValid(model, "plannedActivities[1].description", vertx, testContext);

  }

  /**
   * Check that not accept profiles with bad relevant locations.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadRelevantLocations(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.relevantLocations = new ArrayList<>();
    model.relevantLocations.add(new RelevantLocation());
    model.relevantLocations.add(new RelevantLocation());
    model.relevantLocations.add(new RelevantLocation());
    model.relevantLocations.get(1).label = ValidationsTest.STRING_256;
    assertIsNotValid(model, "relevantLocations[1].label", vertx, testContext);

  }

  /**
   * Check that not accept profiles with bad relationships.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadRelationships(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.relationships = new ArrayList<>();
    model.relationships.add(new SocialNetworkRelationship());
    assertIsNotValid(model, "relationships[0].type", vertx, testContext);

  }

  /**
   * Check that not accept profiles with duplicated relationships.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithADuplicatedRelationships(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext, testContext.succeeding(stored -> {

      final var model = new WeNetUserProfile();
      model.relationships = new ArrayList<>();
      model.relationships.add(new SocialNetworkRelationship());
      model.relationships.add(new SocialNetworkRelationship());
      model.relationships.get(0).userId = stored.id;
      model.relationships.get(0).type = SocialNetworkRelationshipType.friend;
      model.relationships.get(1).userId = stored.id;
      model.relationships.get(1).type = SocialNetworkRelationshipType.friend;
      assertIsNotValid(model, "relationships[1]", vertx, testContext);

    }));

  }

  /**
   * Check that is valid with some relationships.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldBeValidWithSomeRelationships(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext, testContext.succeeding(stored -> {

      final var model = new WeNetUserProfile();
      model.relationships = new ArrayList<>();
      model.relationships.add(new SocialNetworkRelationship());
      model.relationships.add(new SocialNetworkRelationship());
      model.relationships.get(0).userId = stored.id;
      model.relationships.get(0).type = SocialNetworkRelationshipType.family;
      model.relationships.get(1).userId = stored.id;
      model.relationships.get(1).type = SocialNetworkRelationshipType.friend;
      assertIsValid(model, vertx, testContext);

    }));

  }

  /**
   * Check that not accept profiles with bad personal behaviors.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadPersonalBehaviors(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.personalBehaviors = new ArrayList<>();
    model.personalBehaviors.add(new Routine());
    model.personalBehaviors.add(new Routine());
    model.personalBehaviors.add(new Routine());
    assertIsNotValid(model, "personalBehaviors[0].user_id", vertx, testContext);

  }

  /**
   * Check that not accept profiles with bad materials.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadMaterials(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.materials = new ArrayList<>();
    model.materials.add(new MaterialTest().createModelExample(1));
    model.materials.add(new Material());
    assertIsNotValid(model, "materials[1].name", vertx, testContext);

  }

  /**
   * Check that not accept profiles with bad competences.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadCompetences(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.competences = new ArrayList<>();
    model.competences.add(new CompetenceTest().createModelExample(1));
    model.competences.add(new Competence());
    assertIsNotValid(model, "competences[1].name", vertx, testContext);

  }

  /**
   * Check that not accept profiles with bad meanings.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadMeanings(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.meanings = new ArrayList<>();
    model.meanings.add(new MeaningTest().createModelExample(1));
    model.meanings.add(new Meaning());
    assertIsNotValid(model, "meanings[1].name", vertx, testContext);

  }

  /**
   * Check that the name is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithABadName(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.name = new UserNameTest().createModelExample(1);
    source.name.first = ValidationsTest.STRING_256;
    assertCannotMerge(new WeNetUserProfile(), source, "name.first", vertx, testContext);

  }

  /**
   * Check that the birth date is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithABadBirthDate(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.dateOfBirth = new AliveBirthDateTest().createModelExample(1);
    source.dateOfBirth.month = 13;
    assertCannotMerge(new WeNetUserProfile(), source, "dateOfBirth.month", vertx, testContext);

  }

  /**
   * Check that the birth date is not on the future.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithABirthDateOnTheFuture(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.dateOfBirth = new AliveBirthDate();
    final var tomorrow = LocalDate.now().plusDays(1);
    source.dateOfBirth.year = tomorrow.getYear();
    source.dateOfBirth.month = (byte) tomorrow.getMonthValue();
    source.dateOfBirth.day = (byte) tomorrow.getDayOfMonth();
    assertCannotMerge(new WeNetUserProfile(), source, "dateOfBirth", vertx, testContext);

  }

  /**
   * Check that the birth date is not before the oldest people on world.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithABirthDateBeforeTheBirthDateOldestPersonOnWorld(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.dateOfBirth = new AliveBirthDate();
    source.dateOfBirth.year = 1903;
    source.dateOfBirth.month = 1;
    source.dateOfBirth.day = 1;
    assertCannotMerge(new WeNetUserProfile(), source, "dateOfBirth", vertx, testContext);

  }

  /**
   * Check that not accept profiles with bad email address.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithABadEmail(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.email = " bad email @ adrress ";
    assertCannotMerge(new WeNetUserProfile(), source, "email", vertx, testContext);

  }

  /**
   * Check that not accept profiles with bad locale.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithABadLocale(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.locale = " bad locale";
    assertCannotMerge(new WeNetUserProfile(), source, "locale", vertx, testContext);
  }

  /**
   * Check that not accept profiles with bad phone number.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithABadPhoneNumber(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.phoneNumber = " bad phone number";
    assertCannotMerge(new WeNetUserProfile(), source, "phoneNumber", vertx, testContext);

  }

  /**
   * Check that not accept profiles with bad avatar address.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithABadAvatar(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.avatar = " bad avatar";
    assertCannotMerge(new WeNetUserProfile(), source, "avatar", vertx, testContext);
  }

  /**
   * Check that not accept profiles with bad nationality.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithABadNationality(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.nationality = ValidationsTest.STRING_256;
    assertCannotMerge(new WeNetUserProfile(), source, "nationality", vertx, testContext);

  }

  /**
   * Check that not accept profiles with bad occupation.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithABadOccupation(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.occupation = ValidationsTest.STRING_256;
    assertCannotMerge(new WeNetUserProfile(), source, "occupation", vertx, testContext);

  }

  /**
   * Check that not accept profiles with bad norms.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithABadNorms(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.norms = new ArrayList<>();
    source.norms.add(new Norm());
    source.norms.add(new Norm());
    source.norms.add(new Norm());
    source.norms.get(1).attribute = ValidationsTest.STRING_256;
    assertCannotMerge(new WeNetUserProfile(), source, "norms[1].attribute", vertx, testContext);

  }

  /**
   * Check that not merge profiles with duplicated social practice identifiers.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithADuplicatedNormIds(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.norms = new ArrayList<>();
    source.norms.add(new Norm());
    source.norms.add(new Norm());
    source.norms.add(new Norm());
    source.norms.get(1).id = "1";
    source.norms.get(2).id = "1";
    final var target = new WeNetUserProfile();
    target.norms = new ArrayList<>();
    target.norms.add(new Norm());
    target.norms.get(0).id = "1";
    assertCannotMerge(target, source, "norms[2]", vertx, testContext);

  }

  /**
   * Check merge model norms.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldMergeWithNorms(final Vertx vertx, final VertxTestContext testContext) {

    final var target = new WeNetUserProfile();
    target.norms = new ArrayList<>();
    target.norms.add(new Norm());
    target.norms.get(0).id = "1";
    final var source = new WeNetUserProfile();
    source.norms = new ArrayList<>();
    source.norms.add(new Norm());
    source.norms.add(new Norm());
    source.norms.add(new Norm());
    source.norms.get(1).id = "1";
    assertCanMerge(target, source, vertx, testContext, merged -> {

      assertThat(merged.norms).isNotEqualTo(target.norms).isEqualTo(source.norms);
      assertThat(merged.norms.get(0).id).isNotEmpty();
      assertThat(merged.norms.get(1).id).isEqualTo("1");
      assertThat(merged.norms.get(2).id).isNotEmpty();

    });

  }

  /**
   * Check that not accept profiles with bad planned activities.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithABadPlannedActivities(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.plannedActivities = new ArrayList<>();
    source.plannedActivities.add(new PlannedActivity());
    source.plannedActivities.add(new PlannedActivity());
    source.plannedActivities.add(new PlannedActivity());
    source.plannedActivities.get(1).description = ValidationsTest.STRING_256;
    assertCannotMerge(new WeNetUserProfile(), source, "plannedActivities[1].description", vertx, testContext);

  }

  /**
   * Check that not merge profiles with a duplicated planned activity id.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
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
    assertCannotMerge(new WeNetUserProfile(), source, "plannedActivities[1]", vertx, testContext);

  }

  /**
   * Check merge planned activities profiles.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
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
    assertCanMerge(target, source, vertx, testContext, merged -> {

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
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithABadRelevantLocations(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.relevantLocations = new ArrayList<>();
    source.relevantLocations.add(new RelevantLocation());
    source.relevantLocations.add(new RelevantLocation());
    source.relevantLocations.add(new RelevantLocation());
    source.relevantLocations.get(1).label = ValidationsTest.STRING_256;
    assertCannotMerge(new WeNetUserProfile(), source, "relevantLocations[1].label", vertx, testContext);

  }

  /**
   * Check that not merge profiles with duplicated relevant location identifiers.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithADuplicatedRelevantLocationIds(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.relevantLocations = new ArrayList<>();
    source.relevantLocations.add(new RelevantLocation());
    source.relevantLocations.add(new RelevantLocation());
    source.relevantLocations.add(new RelevantLocation());
    source.relevantLocations.get(1).id = "1";
    source.relevantLocations.get(2).id = "1";
    final var target = new WeNetUserProfile();
    target.relevantLocations = new ArrayList<>();
    target.relevantLocations.add(new RelevantLocation());
    target.relevantLocations.get(0).id = "1";
    assertCannotMerge(target, source, "relevantLocations[2]", vertx, testContext);

  }

  /**
   * Check that not merge profiles with a duplicated relevant location id.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithDuplicatedRelevantLocationId(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.relevantLocations = new ArrayList<>();
    source.relevantLocations.add(new RelevantLocation());
    source.relevantLocations.add(new RelevantLocation());
    source.relevantLocations.add(new RelevantLocation());
    source.relevantLocations.get(0).id = "1";
    source.relevantLocations.get(1).id = "1";
    assertCannotMerge(new WeNetUserProfile(), source, "relevantLocations[1]", vertx, testContext);

  }

  /**
   * Check merge relevant locations profiles.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldMergeWithRelevantLocations(final Vertx vertx, final VertxTestContext testContext) {

    final var target = new WeNetUserProfile();
    target.relevantLocations = new ArrayList<>();
    target.relevantLocations.add(new RelevantLocation());
    target.relevantLocations.get(0).id = "1";
    final var source = new WeNetUserProfile();
    source.relevantLocations = new ArrayList<>();
    source.relevantLocations.add(new RelevantLocation());
    source.relevantLocations.add(new RelevantLocation());
    source.relevantLocations.add(new RelevantLocation());
    source.relevantLocations.get(1).id = "1";
    assertCanMerge(target, source, vertx, testContext, merged -> {

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
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithABadRelationships(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.relationships = new ArrayList<>();
    source.relationships.add(new SocialNetworkRelationship());
    assertCannotMerge(new WeNetUserProfile(), source, "relationships[0].type", vertx, testContext);

  }

  /**
   * Check that not merge with duplicated relationships.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithDuplicatedRelationships(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext, testContext.succeeding(stored -> {

      final var source = new WeNetUserProfile();
      source.relationships = new ArrayList<>();
      source.relationships.add(new SocialNetworkRelationship());
      source.relationships.add(new SocialNetworkRelationship());
      source.relationships.get(0).userId = stored.id;
      source.relationships.get(0).type = SocialNetworkRelationshipType.friend;
      source.relationships.get(1).userId = stored.id;
      source.relationships.get(1).type = SocialNetworkRelationshipType.friend;
      assertCannotMerge(new WeNetUserProfile(), source, "relationships[1]", vertx, testContext);

    }));

  }

  /**
   * Check that merge some relationships.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldMergeRelationships(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext, testContext.succeeding(stored -> {

      final var target = new WeNetUserProfile();
      target.relationships = new ArrayList<>();
      target.relationships.add(new SocialNetworkRelationship());
      target.relationships.get(0).userId = stored.id;
      target.relationships.get(0).type = SocialNetworkRelationshipType.friend;

      final var source = new WeNetUserProfile();
      source.relationships = new ArrayList<>();
      source.relationships.add(new SocialNetworkRelationship());
      source.relationships.add(new SocialNetworkRelationship());
      source.relationships.get(0).userId = stored.id;
      source.relationships.get(0).type = SocialNetworkRelationshipType.family;
      source.relationships.get(1).userId = stored.id;
      source.relationships.get(1).type = SocialNetworkRelationshipType.friend;
      assertCanMerge(target, source, vertx, testContext, merged -> {

        assertThat(merged.relationships).isNotEqualTo(target.relationships).isEqualTo(source.relationships);

      });

    }));

  }

  /**
   * Check that not merge profiles with bad personal behaviors.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithABadPersonalBehaviors(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.personalBehaviors = new ArrayList<>();
    source.personalBehaviors.add(new Routine());
    assertCannotMerge(new WeNetUserProfile(), source, "personalBehaviors[0].user_id", vertx, testContext);

  }

  /**
   * Check merge empty profiles.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldMergeEmptyModels(final Vertx vertx, final VertxTestContext testContext) {

    final var target = new WeNetUserProfile();
    final var source = new WeNetUserProfile();
    assertCanMerge(target, source, vertx, testContext, merged -> {

      assertThat(merged).isEqualTo(target);

    });

  }

  /**
   * Check merge basic profiles.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
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
    assertCanMerge(target, source, vertx, testContext, merged -> {

      assertThat(merged).isEqualTo(target).isNotEqualTo(source);

    });

  }

  /**
   * Check merge example profiles.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldMergeExampleModels(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "1";
    final var source = this.createModelExample(2);
    source.id = "2";
    assertCanMerge(target, source, vertx, testContext, merged -> {

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
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldMergeStoredModels(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(targetToStore -> {

      StoreServices.storeProfile(targetToStore, vertx, testContext, testContext.succeeding(target -> {

        this.createModelExample(2, vertx, testContext, testContext.succeeding(sourceToStore -> {

          StoreServices.storeProfile(sourceToStore, vertx, testContext, testContext.succeeding(source -> {

            assertCanMerge(target, source, vertx, testContext, merged -> {

              source.id = target.id;
              source._creationTs = target._creationTs;
              source._lastUpdateTs = target._lastUpdateTs;
              assertThat(merged).isNotEqualTo(target).isEqualTo(source);

            });
          }));
        }));

      }));
    }));

  }

  /**
   * Check merge only the user name.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyUserName(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createBasicExample(1);
    assertIsValid(target, vertx, testContext, () -> {

      final var source = new WeNetUserProfile();
      source.name = new UserName();
      source.name.middle = "NEW MIDDLE NAME";
      assertCanMerge(target, source, vertx, testContext, merged -> {

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
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldMergeAddUserName(final Vertx vertx, final VertxTestContext testContext) {

    final var target = new WeNetUserProfile();
    target.id = "1";
    final var source = new WeNetUserProfile();
    source.name = new UserName();
    source.name.middle = "NEW MIDDLE NAME";
    assertCanMerge(target, source, vertx, testContext, merged -> {

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
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyBirthDate(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createBasicExample(1);
    assertIsValid(target, vertx, testContext, () -> {

      final var source = new WeNetUserProfile();
      source.dateOfBirth = new AliveBirthDate();
      source.dateOfBirth.year = 1923;
      assertCanMerge(target, source, vertx, testContext, merged -> {

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
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldMergeAddBirthDate(final Vertx vertx, final VertxTestContext testContext) {

    final var target = new WeNetUserProfile();
    target.id = "1";
    final var source = new WeNetUserProfile();
    source.dateOfBirth = new AliveBirthDateTest().createModelExample(1);
    assertCanMerge(target, source, vertx, testContext, merged -> {

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
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyGender(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createBasicExample(1);
    assertIsValid(target, vertx, testContext, () -> {

      final var source = new WeNetUserProfile();
      source.gender = Gender.M;
      assertCanMerge(target, source, vertx, testContext, merged -> {

        assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
        target.gender = Gender.M;
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
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyEmail(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createBasicExample(1);
    assertIsValid(target, vertx, testContext, () -> {

      final var source = new WeNetUserProfile();
      source.email = "new@email.com";
      assertCanMerge(target, source, vertx, testContext, merged -> {

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
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyLocale(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createBasicExample(1);
    assertIsValid(target, vertx, testContext, () -> {

      final var source = new WeNetUserProfile();
      source.locale = "en_NZ";
      assertCanMerge(target, source, vertx, testContext, merged -> {

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
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyPhoneNumber(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createBasicExample(1);
    assertIsValid(target, vertx, testContext, () -> {

      final var source = new WeNetUserProfile();
      source.phoneNumber = "+1 412 535 2223";
      assertCanMerge(target, source, vertx, testContext, merged -> {

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
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyAvatar(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createBasicExample(1);
    assertIsValid(target, vertx, testContext, () -> {

      final var source = new WeNetUserProfile();
      source.avatar = "http://new-avatar.com";
      assertCanMerge(target, source, vertx, testContext, merged -> {

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
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyNationality(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createBasicExample(1);
    assertIsValid(target, vertx, testContext, () -> {

      final var source = new WeNetUserProfile();
      source.nationality = "Canadian";
      assertCanMerge(target, source, vertx, testContext, merged -> {

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
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyOccupation(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createBasicExample(1);
    assertIsValid(target, vertx, testContext, () -> {

      final var source = new WeNetUserProfile();
      source.occupation = "Bus driver";
      assertCanMerge(target, source, vertx, testContext, merged -> {

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
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldMergeRemovePlannedActivities(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

      assertIsValid(created, vertx, testContext, () -> {

        StoreServices.storeProfile(created, vertx, testContext, testContext.succeeding(target -> {

          final var source = new WeNetUserProfile();
          source.plannedActivities = new ArrayList<>();
          assertCanMerge(target, source, vertx, testContext, merged -> {

            assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
            target.plannedActivities.clear();
            assertThat(merged).isEqualTo(target);

          });

        }));

      });

    }));

  }

  /**
   * Check fail merge with a bad defined planned activity.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldFailMergeBadPlannedActivities(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

      assertIsValid(created, vertx, testContext, () -> {

        StoreServices.storeProfile(created, vertx, testContext, testContext.succeeding(target -> {

          final var source = new WeNetUserProfile();
          source.plannedActivities = new ArrayList<>();
          source.plannedActivities.add(new PlannedActivity());
          source.plannedActivities.get(0).id = target.plannedActivities.get(0).id;
          source.plannedActivities.get(0).description = ValidationsTest.STRING_256;
          assertCannotMerge(target, source, "plannedActivities[0].description", vertx, testContext);

        }));

      });

    }));

  }

  /**
   * Check fail merge with a bad new planned activity.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldFailMergeBadNewPlannedActivities(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

      assertIsValid(created, vertx, testContext, () -> {

        StoreServices.storeProfile(created, vertx, testContext, testContext.succeeding(target -> {

          final var source = new WeNetUserProfile();
          source.plannedActivities = new ArrayList<>();
          source.plannedActivities.add(new PlannedActivity());
          source.plannedActivities.get(0).description = ValidationsTest.STRING_256;
          assertCannotMerge(target, source, "plannedActivities[0].description", vertx, testContext);

        }));

      });

    }));
  }

  /**
   * Check merge add modify planned activities.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldMergeAddmodifyPlannedActivities(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

      assertIsValid(created, vertx, testContext, () -> {

        StoreServices.storeProfile(created, vertx, testContext, testContext.succeeding(target -> {

          final var source = new WeNetUserProfile();
          source.plannedActivities = new ArrayList<>();
          source.plannedActivities.add(new PlannedActivity());
          source.plannedActivities.add(new PlannedActivity());
          source.plannedActivities.get(0).id = target.plannedActivities.get(1).id;
          source.plannedActivities.get(0).description = "NEW description";
          assertCanMerge(target, source, vertx, testContext, merged -> {

            assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
            target.plannedActivities.remove(0);
            target.plannedActivities.get(0).description = "NEW description";
            target.plannedActivities.add(new PlannedActivity());
            target.plannedActivities.get(1).id = merged.plannedActivities.get(1).id;
            assertThat(merged).isEqualTo(target);

          });

        }));
      });
    }));

  }

  /**
   * Check merge remove relevant locations.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldMergeRemoveRelevantLocations(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

      assertIsValid(created, vertx, testContext, () -> {

        StoreServices.storeProfile(created, vertx, testContext, testContext.succeeding(target -> {

          final var source = new WeNetUserProfile();
          source.relevantLocations = new ArrayList<>();
          assertCanMerge(target, source, vertx, testContext, merged -> {
            assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
            target.relevantLocations.clear();
            assertThat(merged).isEqualTo(target);
          });

        }));
      });
    }));
  }

  /**
   * Check fail merge with a bad defined relevant location.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  public void shouldFailMergeBadRelevantLocations(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

      assertIsValid(created, vertx, testContext, () -> {

        StoreServices.storeProfile(created, vertx, testContext, testContext.succeeding(target -> {

          final var source = new WeNetUserProfile();
          source.relevantLocations = new ArrayList<>();
          source.relevantLocations.add(new RelevantLocation());
          source.relevantLocations.get(0).id = target.relevantLocations.get(0).id;
          source.relevantLocations.get(0).label = ValidationsTest.STRING_256;
          assertCannotMerge(target, source, "relevantLocations[0].label", vertx, testContext);
        }));
      });
    }));
  }

  /**
   * Check fail merge with a bad new relevant location.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  public void shouldFailMergeBadNewRelevantLocations(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

      assertIsValid(created, vertx, testContext, () -> {

        StoreServices.storeProfile(created, vertx, testContext, testContext.succeeding(target -> {

          final var source = new WeNetUserProfile();
          source.relevantLocations = new ArrayList<>();
          source.relevantLocations.add(new RelevantLocation());
          source.relevantLocations.get(0).label = ValidationsTest.STRING_256;
          assertCannotMerge(target, source, "relevantLocations[0].label", vertx, testContext);

        }));
      });
    }));
  }

  /**
   * Check merge add modify relevant locations.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldMergeAddModifyRelevantLocations(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

      assertIsValid(created, vertx, testContext, () -> {

        StoreServices.storeProfile(created, vertx, testContext, testContext.succeeding(target -> {

          final var source = new WeNetUserProfile();
          source.relevantLocations = new ArrayList<>();
          source.relevantLocations.add(new RelevantLocation());
          source.relevantLocations.add(new RelevantLocationTest().createModelExample(1));
          source.relevantLocations.get(1).id = target.relevantLocations.get(0).id;
          source.relevantLocations.get(1).label = "NEW label";
          assertCanMerge(target, source, vertx, testContext, merged -> {

            assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
            target.relevantLocations.add(0, new RelevantLocation());
            target.relevantLocations.get(0).id = merged.relevantLocations.get(0).id;
            target.relevantLocations.get(1).label = "NEW label";
            assertThat(merged).isEqualTo(target);

          });
        }));
      });
    }));
  }

  /**
   * Check merge remove personal behaviors.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldMergeRemovePersonalBehaviors(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

      assertIsValid(created, vertx, testContext, () -> {

        StoreServices.storeProfile(created, vertx, testContext, testContext.succeeding(target -> {

          final var source = new WeNetUserProfile();
          source.personalBehaviors = new ArrayList<>();
          assertCanMerge(target, source, vertx, testContext, merged -> {

            assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
            target.personalBehaviors.clear();
            assertThat(merged).isEqualTo(target);
            testContext.completeNow();
          });
        }));
      });
    }));
  }

  /**
   * Check fail merge with a bad new personal behavior.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldFailMergeBadNewPersonalBehaviors(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

      assertIsValid(created, vertx, testContext, () -> {

        StoreServices.storeProfile(created, vertx, testContext, testContext.succeeding(target -> {

          final var source = new WeNetUserProfile();
          source.personalBehaviors = new ArrayList<>();
          source.personalBehaviors.add(new Routine());
          assertCannotMerge(target, source, "personalBehaviors[0].user_id", vertx, testContext);
        }));
      });
    }));
  }

  /**
   * Check merge add modify personal behaviors.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldMergeAddModifyPersonalBehaviors(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

      assertIsValid(created, vertx, testContext, () -> {

        StoreServices.storeProfile(created, vertx, testContext, testContext.succeeding(target -> {

          new RoutineTest().createModelExample(1, vertx, testContext, testContext.succeeding(createdRoutine -> {

            final var source = new WeNetUserProfile();
            source.personalBehaviors = new ArrayList<>();
            source.personalBehaviors.add(createdRoutine);
            source.personalBehaviors.addAll(created.personalBehaviors);
            source.personalBehaviors.get(1).confidence = 0.0;
            assertCanMerge(target, source, vertx, testContext, merged -> {

              assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
              target.personalBehaviors.add(0, createdRoutine);
              target.personalBehaviors.get(1).confidence = 0.0;
              assertThat(merged).isEqualTo(target);
            });
          }));
        }));
      });
    }));
  }

  /**
   * Check that not accept profiles with bad materials.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithABadMaterials(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.materials = new ArrayList<>();
    source.materials.add(new MaterialTest().createModelExample(1));
    source.materials.add(new MaterialTest().createModelExample(2));
    source.materials.add(new MaterialTest().createModelExample(3));
    source.materials.get(1).name = null;
    assertCannotMerge(new WeNetUserProfile(), source, "materials[1].name", vertx, testContext);

  }

  /**
   * Check merge remove materials.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldMergeRemoveMaterials(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertIsValid(target, vertx, testContext, () -> {

      final var source = new WeNetUserProfile();
      source.materials = new ArrayList<>();
      assertCanMerge(target, source, vertx, testContext, merged -> {

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
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldMergeAddAndModifyMaterials(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.materials.add(new MaterialTest().createModelExample(2));
    target.materials.add(new MaterialTest().createModelExample(3));
    assertIsValid(target, vertx, testContext, () -> {

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
      assertCanMerge(target, source, vertx, testContext, merged -> {

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
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithABadCompetences(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.competences = new ArrayList<>();
    source.competences.add(new CompetenceTest().createModelExample(1));
    source.competences.add(new CompetenceTest().createModelExample(2));
    source.competences.add(new CompetenceTest().createModelExample(3));
    source.competences.get(1).name = null;
    assertCannotMerge(new WeNetUserProfile(), source, "competences[1].name", vertx, testContext);

  }

  /**
   * Check merge remove competences.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldMergeRemoveCompetences(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertIsValid(target, vertx, testContext, () -> {

      final var source = new WeNetUserProfile();
      source.competences = new ArrayList<>();
      assertCanMerge(target, source, vertx, testContext, merged -> {

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
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldMergeAddAndModifyCompetences(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.competences.add(new CompetenceTest().createModelExample(2));
    target.competences.add(new CompetenceTest().createModelExample(3));
    assertIsValid(target, vertx, testContext, () -> {

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
      assertCanMerge(target, source, vertx, testContext, merged -> {

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
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithABadMeanings(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.meanings = new ArrayList<>();
    source.meanings.add(new MeaningTest().createModelExample(1));
    source.meanings.add(new MeaningTest().createModelExample(2));
    source.meanings.add(new MeaningTest().createModelExample(3));
    source.meanings.get(1).name = null;
    assertCannotMerge(new WeNetUserProfile(), source, "meanings[1].name", vertx, testContext);

  }

  /**
   * Check merge remove meanings.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldMergeRemoveMeanings(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertIsValid(target, vertx, testContext, () -> {

      final var source = new WeNetUserProfile();
      source.meanings = new ArrayList<>();
      assertCanMerge(target, source, vertx, testContext, merged -> {

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
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldMergeAddAndModifyMeanings(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.meanings.add(new MeaningTest().createModelExample(2));
    target.meanings.add(new MeaningTest().createModelExample(3));
    assertIsValid(target, vertx, testContext, () -> {

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
      assertCanMerge(target, source, vertx, testContext, merged -> {

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
   * @see CommunityProfile#merge(CommunityProfile, String, Vertx)
   */
  @Test
  public void shoudMergeWithNull(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      assertCanMerge(target, null, vertx, testContext, merged -> {
        assertThat(merged).isSameAs(target);
      });
    }));

  }

  /**
   * Check that the model is not valid it has two norms with the same identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotValidWithDuplicatedNorms(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.norms = new ArrayList<>();
    for (var i = 0; i < 2; i++) {

      model.norms.add(new NormTest().createModelExample(i));
      model.norms.get(i).id = "Duplicated Identifier";
    }
    assertIsNotValid(model, "norms[1]", vertx, testContext);

  }

  /**
   * Check that the model is not valid it has two relevant locations with the same identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotValidWithDuplicatedRelevantLocations(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new WeNetUserProfile();
    model.relevantLocations = new ArrayList<>();
    for (var i = 0; i < 2; i++) {

      model.relevantLocations.add(new RelevantLocationTest().createModelExample(i));
      model.relevantLocations.get(i).id = "Duplicated Identifier";
    }
    assertIsNotValid(model, "relevantLocations[1]", vertx, testContext);

  }

  /**
   * Check that the model is not valid it has two relevant locations with the same identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotValidWithDuplicatedPersonalBehaviors(final Vertx vertx, final VertxTestContext testContext) {

    new RoutineTest().createModelExample(1, vertx, testContext, testContext.succeeding(routine -> {

      final var model = new WeNetUserProfile();
      model.personalBehaviors = new ArrayList<>();
      model.personalBehaviors.add(routine);
      model.personalBehaviors.add(Model.fromJsonObject(routine.toJsonObject(), Routine.class));
      assertIsNotValid(model, "personalBehaviors[1]", vertx, testContext);

    }));

  }

  /**
   * Should update with {@code null}
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#update(CommunityProfile, String, Vertx)
   */
  @Test
  public void shoudUpdateWithNull(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      assertCanUpdate(target, null, vertx, testContext, updated -> {
        assertThat(updated).isSameAs(target);
      });
    }));

  }

  /**
   * Check that not update profiles with bad personal behaviors.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#update(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldNotUpdateWithABadPersonalBehaviors(final Vertx vertx, final VertxTestContext testContext) {

    final var source = new WeNetUserProfile();
    source.personalBehaviors = new ArrayList<>();
    source.personalBehaviors.add(new Routine());
    assertCannotUpdate(new WeNetUserProfile(), source, "personalBehaviors[0].user_id", vertx, testContext);

  }

  /**
   * Check update empty profiles.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#update(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldUpdateEmptyModels(final Vertx vertx, final VertxTestContext testContext) {

    final var target = new WeNetUserProfile();
    final var source = new WeNetUserProfile();
    assertCanUpdate(target, source, vertx, testContext, updated -> {

      assertThat(updated).isEqualTo(target);

    });

  }

  /**
   * Check update basic profiles.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#update(WeNetUserProfile, String, Vertx)
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
    assertCanUpdate(target, source, vertx, testContext, updated -> {

      assertThat(updated).isEqualTo(target).isNotEqualTo(source);

    });

  }

  /**
   * Check update example profiles.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#update(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldUpdateExampleModels(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    target.id = "1";
    final var source = this.createModelExample(2);
    source.id = "2";
    assertCanUpdate(target, source, vertx, testContext, updated -> {

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
   * @see WeNetUserProfile#update(WeNetUserProfile, String, Vertx)
   */
  @Test
  public void shouldUpdateStoredModels(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(targetToStore -> {

      StoreServices.storeProfile(targetToStore, vertx, testContext, testContext.succeeding(target -> {

        this.createModelExample(2, vertx, testContext, testContext.succeeding(sourceToStore -> {

          StoreServices.storeProfile(sourceToStore, vertx, testContext, testContext.succeeding(source -> {

            assertCanUpdate(target, source, vertx, testContext, updated -> {

              source.id = target.id;
              source._creationTs = target._creationTs;
              source._lastUpdateTs = target._lastUpdateTs;
              assertThat(updated).isNotEqualTo(target).isEqualTo(source);

            });
          }));
        }));

      }));
    }));

  }
}
