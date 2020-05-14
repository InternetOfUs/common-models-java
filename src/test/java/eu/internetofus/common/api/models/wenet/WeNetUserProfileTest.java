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

package eu.internetofus.common.api.models.wenet;

import static eu.internetofus.common.api.models.MergesTest.assertCanMerge;
import static eu.internetofus.common.api.models.MergesTest.assertCannotMerge;
import static eu.internetofus.common.api.models.ValidationsTest.assertIsNotValid;
import static eu.internetofus.common.api.models.ValidationsTest.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import eu.internetofus.common.api.models.ModelTestCase;
import eu.internetofus.common.api.models.ValidationsTest;
import eu.internetofus.common.services.WeNetProfileManagerService;
import eu.internetofus.common.services.WeNetProfileManagerServiceOnMemory;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Timeout;
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
	 * Register the necessary services before to test.
	 *
	 * @param vertx event bus to register the necessary services.
	 */
	@BeforeEach
	public void registerServices(Vertx vertx) {

		WeNetProfileManagerServiceOnMemory.register(vertx);

	}

	/**
	 * Create an basic model that has the specified index.
	 *
	 * @param index to use in the example.
	 *
	 * @return the basic example.
	 */
	public WeNetUserProfile createBasicExample(int index) {

		final WeNetUserProfile model = new WeNetUserProfile();
		model.id = null;
		model.name = new UserNameTest().createModelExample(index);
		model.dateOfBirth = new AliveBirthDateTest().createModelExample(index);
		model.gender = Gender.F;
		model.email = "user" + index + "@internetofus.eu";
		model.phoneNumber = "+34 987 65 43 " + (10 + index % 90);
		model.locale = "ca_AD";
		model.avatar = "https://internetofus.eu/wp-content/uploads/sites/38/2019/" + index + "/WeNet_logo.png";
		model.nationality = "nationality_" + index;
		model.languages = new ArrayList<>();
		model.languages.add(new LanguageTest().createModelExample(index));
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
	public WeNetUserProfile createModelExample(int index) {

		final WeNetUserProfile model = this.createBasicExample(index);
		model.norms = new ArrayList<>();
		model.norms.add(new NormTest().createModelExample(index));
		model.plannedActivities = new ArrayList<>();
		model.plannedActivities.add(new PlannedActivityTest().createModelExample(index));
		model.relevantLocations = new ArrayList<>();
		model.relevantLocations.add(new RelevantLocationTest().createModelExample(index));
		model.relationships = null;
		model.socialPractices = new ArrayList<>();
		model.socialPractices.add(new SocialPracticeTest().createModelExample(index));
		model.personalBehaviors = new ArrayList<>();
		model.personalBehaviors.add(new RoutineTest().createModelExample(index));
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
	public void createModelExample(int index, Vertx vertx, VertxTestContext testContext,
			Handler<AsyncResult<WeNetUserProfile>> createHandler) {

		StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext, testContext.succeeding(stored1 -> {

			StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext, testContext.succeeding(stored2 -> {

				final WeNetUserProfile profile = this.createModelExample(index);

				final PlannedActivity activity = new PlannedActivityTest().createModelExample(3);
				activity.attendees = new ArrayList<>();
				activity.attendees.add(stored1.id);
				activity.attendees.add(stored2.id);
				profile.plannedActivities.add(activity);
				profile.relationships = new ArrayList<>();
				profile.relationships.add(new SocialNetworkRelantionshipTest().createModelExample(5));
				profile.relationships.get(0).userId = stored1.id;
				profile.relationships.add(new SocialNetworkRelantionshipTest().createModelExample(6));
				profile.relationships.get(1).userId = stored2.id;
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
	public void shouldEmptyModelBeValid(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
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
	public void shouldExampleBeValid(int index, Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile model = this.createModelExample(index);
		assertIsValid(model, vertx, testContext);

	}

	/**
	 * Check that the
	 * {@link #createModelExample(int, Vertx, VertxTestContext, Handler)} is valid.
	 *
	 * @param index       to verify
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String, Vertx)
	 */
	@ParameterizedTest(name = "The model example {0} has to be valid")
	@ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
	public void shouldExampleFromRepositoryBeValid(int index, Vertx vertx, VertxTestContext testContext) {

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
	public void shouldNotBeValidWithAnExistingId(Vertx vertx, VertxTestContext testContext) {

		WeNetProfileManagerService.createProxy(vertx).createProfile(new JsonObject(), testContext.succeeding(created -> {

			final WeNetUserProfile model = new WeNetUserProfile();
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
	public void shouldBeValidWithAnNewId(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
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
	public void shouldNotBeValidWithABadName(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
		model.name = new UserNameTest().createModelExample(1);
		model.name.first = ValidationsTest.STRING_256;
		assertIsNotValid(model, "name.first", vertx, testContext);
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
	public void shouldNotBeValidWithABadBirthDate(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
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
	public void shouldNotBeValidWithABirthDateOnTheFuture(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
		model.dateOfBirth = new AliveBirthDate();
		final LocalDate tomorrow = LocalDate.now().plusDays(1);
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
	public void shouldNotBeValidWithABirthDateBeforeTheBirthDateOldestPersonOnWorld(Vertx vertx,
			VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
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
	public void shouldNotBeValidWithABadEmail(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
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
	public void shouldNotBeValidWithABadLocale(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
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
	public void shouldNotBeValidWithABadPhoneNumber(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
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
	public void shouldNotBeValidWithABadAvatar(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
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
	public void shouldNotBeValidWithABadNationality(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
		model.nationality = ValidationsTest.STRING_256;
		assertIsNotValid(model, "nationality", vertx, testContext);

	}

	/**
	 * Check that not accept profiles with bad languages.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithABadLanguages(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
		model.languages = new ArrayList<>();
		model.languages.add(new Language());
		model.languages.add(new Language());
		model.languages.add(new Language());
		model.languages.get(1).code = "bad code";
		assertIsNotValid(model, "languages[1].code", vertx, testContext);

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
	public void shouldNotBeValidWithABadOccupation(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
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
	public void shouldNotBeValidWithABadNorms(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
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
	public void shouldNotBeValidWithABadPlannedActivities(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
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
	public void shouldNotBeValidWithABadRelevantLocations(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
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
	public void shouldNotBeValidWithABadRelationships(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
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
	public void shouldNotBeValidWithADuplicatedRelationships(Vertx vertx, VertxTestContext testContext) {

		StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext, testContext.succeeding(stored -> {

			final WeNetUserProfile model = new WeNetUserProfile();
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
	public void shouldBeValidWithSomeRelationships(Vertx vertx, VertxTestContext testContext) {

		StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext, testContext.succeeding(stored -> {

			final WeNetUserProfile model = new WeNetUserProfile();
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
	 * Check that not accept profiles with bad social practices.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithABadSocialPractices(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
		model.socialPractices = new ArrayList<>();
		model.socialPractices.add(new SocialPractice());
		model.socialPractices.add(new SocialPractice());
		model.socialPractices.add(new SocialPractice());
		model.socialPractices.get(1).label = ValidationsTest.STRING_256;
		assertIsNotValid(model, "socialPractices[1].label", vertx, testContext);

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
	public void shouldNotBeValidWithABadPersonalBehaviors(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile model = new WeNetUserProfile();
		model.personalBehaviors = new ArrayList<>();
		model.personalBehaviors.add(new Routine());
		model.personalBehaviors.add(new Routine());
		model.personalBehaviors.add(new Routine());
		model.personalBehaviors.get(1).label = ValidationsTest.STRING_256;
		assertIsNotValid(model, "personalBehaviors[1].label", vertx, testContext);

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
	public void shouldNotMergeWithABadName(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldNotMergeWithABadBirthDate(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldNotMergeWithABirthDateOnTheFuture(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.dateOfBirth = new AliveBirthDate();
		final LocalDate tomorrow = LocalDate.now().plusDays(1);
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
	public void shouldNotMergeWithABirthDateBeforeTheBirthDateOldestPersonOnWorld(Vertx vertx,
			VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldNotMergeWithABadEmail(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldNotMergeWithABadLocale(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldNotMergeWithABadPhoneNumber(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldNotMergeWithABadAvatar(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldNotMergeWithABadNationality(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.nationality = ValidationsTest.STRING_256;
		assertCannotMerge(new WeNetUserProfile(), source, "nationality", vertx, testContext);

	}

	/**
	 * Check that not accept profiles with bad languages.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithABadLanguages(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.languages = new ArrayList<>();
		source.languages.add(new Language());
		source.languages.add(new Language());
		source.languages.add(new Language());
		source.languages.get(1).code = "bad code";
		assertCannotMerge(new WeNetUserProfile(), source, "languages[1].code", vertx, testContext);

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
	public void shouldNotMergeWithABadOccupation(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldNotMergeWithABadNorms(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldNotMergeWithADuplicatedNormIds(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.norms = new ArrayList<>();
		source.norms.add(new Norm());
		source.norms.add(new Norm());
		source.norms.add(new Norm());
		source.norms.get(1).id = "1";
		source.norms.get(2).id = "1";
		final WeNetUserProfile target = new WeNetUserProfile();
		target.norms = new ArrayList<>();
		target.norms.add(new Norm());
		target.norms.get(0).id = "1";
		assertCannotMerge(target, source, "norms[2]", vertx, testContext);

	}

	/**
	 * Check that not merge profiles with not defined social practice id.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithNotDefinecNormId(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.norms = new ArrayList<>();
		source.norms.add(new Norm());
		source.norms.add(new Norm());
		source.norms.add(new Norm());
		source.norms.get(1).id = "1";
		assertCannotMerge(new WeNetUserProfile(), source, "norms[1].id", vertx, testContext);

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
	public void shouldMergeWithNorms(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile target = new WeNetUserProfile();
		target.norms = new ArrayList<>();
		target.norms.add(new Norm());
		target.norms.get(0).id = "1";
		final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldNotMergeWithABadPlannedActivities(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.plannedActivities = new ArrayList<>();
		source.plannedActivities.add(new PlannedActivity());
		source.plannedActivities.add(new PlannedActivity());
		source.plannedActivities.add(new PlannedActivity());
		source.plannedActivities.get(1).description = ValidationsTest.STRING_256;
		assertCannotMerge(new WeNetUserProfile(), source, "plannedActivities[1].description", vertx, testContext);

	}

	/**
	 * Check that not merge profiles with duplicated planned activity identifiers.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithADuplicatedPlannedActivityIds(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.plannedActivities = new ArrayList<>();
		source.plannedActivities.add(new PlannedActivity());
		source.plannedActivities.add(new PlannedActivity());
		source.plannedActivities.add(new PlannedActivity());
		source.plannedActivities.get(1).id = "1";
		source.plannedActivities.get(2).id = "1";
		final WeNetUserProfile target = new WeNetUserProfile();
		target.plannedActivities = new ArrayList<>();
		target.plannedActivities.add(new PlannedActivity());
		target.plannedActivities.get(0).id = "1";
		assertCannotMerge(target, source, "plannedActivities[2]", vertx, testContext);

	}

	/**
	 * Check that not merge profiles with not defined planned activity id.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithNotDefinecPlannedActivityId(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.plannedActivities = new ArrayList<>();
		source.plannedActivities.add(new PlannedActivity());
		source.plannedActivities.add(new PlannedActivity());
		source.plannedActivities.add(new PlannedActivity());
		source.plannedActivities.get(1).id = "1";
		assertCannotMerge(new WeNetUserProfile(), source, "plannedActivities[1].id", vertx, testContext);

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
	public void shouldMergeWithPlannedActivities(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile target = new WeNetUserProfile();
		target.plannedActivities = new ArrayList<>();
		target.plannedActivities.add(new PlannedActivity());
		target.plannedActivities.get(0).id = "1";
		final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldNotMergeWithABadRelevantLocations(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldNotMergeWithADuplicatedRelevantLocationIds(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.relevantLocations = new ArrayList<>();
		source.relevantLocations.add(new RelevantLocation());
		source.relevantLocations.add(new RelevantLocation());
		source.relevantLocations.add(new RelevantLocation());
		source.relevantLocations.get(1).id = "1";
		source.relevantLocations.get(2).id = "1";
		final WeNetUserProfile target = new WeNetUserProfile();
		target.relevantLocations = new ArrayList<>();
		target.relevantLocations.add(new RelevantLocation());
		target.relevantLocations.get(0).id = "1";
		assertCannotMerge(target, source, "relevantLocations[2]", vertx, testContext);

	}

	/**
	 * Check that not merge profiles with not defined relevant location id.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithNotDefinecRelevantLocationId(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.relevantLocations = new ArrayList<>();
		source.relevantLocations.add(new RelevantLocation());
		source.relevantLocations.add(new RelevantLocation());
		source.relevantLocations.add(new RelevantLocation());
		source.relevantLocations.get(1).id = "1";
		assertCannotMerge(new WeNetUserProfile(), source, "relevantLocations[1].id", vertx, testContext);

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
	public void shouldMergeWithRelevantLocations(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile target = new WeNetUserProfile();
		target.relevantLocations = new ArrayList<>();
		target.relevantLocations.add(new RelevantLocation());
		target.relevantLocations.get(0).id = "1";
		final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldNotMergeWithABadRelationships(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldNotMergeWithDuplicatedRelationships(Vertx vertx, VertxTestContext testContext) {

		StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext, testContext.succeeding(stored -> {

			final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldMergeRelationships(Vertx vertx, VertxTestContext testContext) {

		StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext, testContext.succeeding(stored -> {

			final WeNetUserProfile target = new WeNetUserProfile();
			target.relationships = new ArrayList<>();
			target.relationships.add(new SocialNetworkRelationship());
			target.relationships.get(0).userId = stored.id;
			target.relationships.get(0).type = SocialNetworkRelationshipType.friend;

			final WeNetUserProfile source = new WeNetUserProfile();
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
	 * Check that not accept profiles with bad social practices.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithABadSocialPractices(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.socialPractices = new ArrayList<>();
		source.socialPractices.add(new SocialPractice());
		source.socialPractices.add(new SocialPractice());
		source.socialPractices.add(new SocialPractice());
		source.socialPractices.get(1).label = ValidationsTest.STRING_256;
		assertCannotMerge(new WeNetUserProfile(), source, "socialPractices[1].label", vertx, testContext);

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
	public void shouldNotMergeWithADuplicatedSocialPracticeIds(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.socialPractices = new ArrayList<>();
		source.socialPractices.add(new SocialPractice());
		source.socialPractices.add(new SocialPractice());
		source.socialPractices.add(new SocialPractice());
		source.socialPractices.get(1).id = "1";
		source.socialPractices.get(2).id = "1";
		final WeNetUserProfile target = new WeNetUserProfile();
		target.socialPractices = new ArrayList<>();
		target.socialPractices.add(new SocialPractice());
		target.socialPractices.get(0).id = "1";
		assertCannotMerge(target, source, "socialPractices[2]", vertx, testContext);

	}

	/**
	 * Check that not merge profiles with not defined social practice id.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithNotDefinecSocialPracticeId(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.socialPractices = new ArrayList<>();
		source.socialPractices.add(new SocialPractice());
		source.socialPractices.add(new SocialPractice());
		source.socialPractices.add(new SocialPractice());
		source.socialPractices.get(1).id = "1";
		assertCannotMerge(new WeNetUserProfile(), source, "socialPractices[1].id", vertx, testContext);

	}

	/**
	 * Check merge social practices profiles.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
	 */
	@Test
	public void shouldMergeWithSocialPractices(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile target = new WeNetUserProfile();
		target.socialPractices = new ArrayList<>();
		target.socialPractices.add(new SocialPractice());
		target.socialPractices.get(0).id = "1";
		final WeNetUserProfile source = new WeNetUserProfile();
		source.socialPractices = new ArrayList<>();
		source.socialPractices.add(new SocialPractice());
		source.socialPractices.add(new SocialPractice());
		source.socialPractices.add(new SocialPractice());
		source.socialPractices.get(1).id = "1";
		assertCanMerge(target, source, vertx, testContext, merged -> {

			assertThat(merged.socialPractices).isNotEqualTo(target.socialPractices).isEqualTo(source.socialPractices);
			assertThat(merged.socialPractices.get(0).id).isNotEmpty();
			assertThat(merged.socialPractices.get(1).id).isEqualTo("1");
			assertThat(merged.socialPractices.get(2).id).isNotEmpty();

		});

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
	public void shouldNotMergeWithABadPersonalBehaviors(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.personalBehaviors = new ArrayList<>();
		source.personalBehaviors.add(new Routine());
		source.personalBehaviors.add(new Routine());
		source.personalBehaviors.add(new Routine());
		source.personalBehaviors.get(1).label = ValidationsTest.STRING_256;
		assertCannotMerge(new WeNetUserProfile(), source, "personalBehaviors[1].label", vertx, testContext);

	}

	/**
	 * Check that not merge profiles with duplicated personal behavior identifiers.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithADuplicatedPersonalBehaviorIds(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.personalBehaviors = new ArrayList<>();
		source.personalBehaviors.add(new Routine());
		source.personalBehaviors.add(new Routine());
		source.personalBehaviors.add(new Routine());
		source.personalBehaviors.get(1).id = "1";
		source.personalBehaviors.get(2).id = "1";
		final WeNetUserProfile target = new WeNetUserProfile();
		target.personalBehaviors = new ArrayList<>();
		target.personalBehaviors.add(new Routine());
		target.personalBehaviors.get(0).id = "1";
		assertCannotMerge(target, source, "personalBehaviors[2]", vertx, testContext);

	}

	/**
	 * Check that not merge profiles with not defined personal behavior id.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithNotDefinecPersonalBehaviorId(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile source = new WeNetUserProfile();
		source.personalBehaviors = new ArrayList<>();
		source.personalBehaviors.add(new Routine());
		source.personalBehaviors.add(new Routine());
		source.personalBehaviors.add(new Routine());
		source.personalBehaviors.get(1).id = "1";
		assertCannotMerge(new WeNetUserProfile(), source, "personalBehaviors[1].id", vertx, testContext);

	}

	/**
	 * Check merge personal behaviors profiles.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
	 */
	@Test
	public void shouldMergeWithPersonalBehaviors(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile target = new WeNetUserProfile();
		target.personalBehaviors = new ArrayList<>();
		target.personalBehaviors.add(new Routine());
		target.personalBehaviors.get(0).id = "1";
		final WeNetUserProfile source = new WeNetUserProfile();
		source.personalBehaviors = new ArrayList<>();
		source.personalBehaviors.add(new Routine());
		source.personalBehaviors.add(new Routine());
		source.personalBehaviors.add(new Routine());
		source.personalBehaviors.get(1).id = "1";
		assertCanMerge(target, source, vertx, testContext, merged -> {

			assertThat(merged.personalBehaviors).isNotEqualTo(target.personalBehaviors).isEqualTo(source.personalBehaviors);
			assertThat(merged.personalBehaviors.get(0).id).isNotEmpty();
			assertThat(merged.personalBehaviors.get(1).id).isEqualTo("1");
			assertThat(merged.personalBehaviors.get(2).id).isNotEmpty();

		});

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
	public void shouldMergeEmptyModels(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile target = new WeNetUserProfile();
		final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldMergeBasicModels(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile target = new WeNetUserProfile();
		target.id = "1";
		target._creationTs = 2;
		target._lastUpdateTs = 3;
		final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldMergeExampleModels(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile target = this.createModelExample(1);
		target.id = "1";
		final WeNetUserProfile source = this.createModelExample(2);
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
	public void shouldMergeStoredModels(Vertx vertx, VertxTestContext testContext) {

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
	public void shouldMergeOnlyUserName(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile target = this.createBasicExample(1);
		assertIsValid(target, vertx, testContext, () -> {

			final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldMergeAddUserName(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile target = new WeNetUserProfile();
		target.id = "1";
		final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldMergeOnlyBirthDate(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile target = this.createBasicExample(1);
		assertIsValid(target, vertx, testContext, () -> {

			final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldMergeAddBirthDate(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile target = new WeNetUserProfile();
		target.id = "1";
		final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldMergeOnlyGender(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile target = this.createBasicExample(1);
		assertIsValid(target, vertx, testContext, () -> {

			final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldMergeOnlyEmail(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile target = this.createBasicExample(1);
		assertIsValid(target, vertx, testContext, () -> {

			final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldMergeOnlyLocale(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile target = this.createBasicExample(1);
		assertIsValid(target, vertx, testContext, () -> {

			final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldMergeOnlyPhoneNumber(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile target = this.createBasicExample(1);
		assertIsValid(target, vertx, testContext, () -> {

			final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldMergeOnlyAvatar(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile target = this.createBasicExample(1);
		assertIsValid(target, vertx, testContext, () -> {

			final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldMergeOnlyNationality(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile target = this.createBasicExample(1);
		assertIsValid(target, vertx, testContext, () -> {

			final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldMergeOnlyOccupation(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile target = this.createBasicExample(1);
		assertIsValid(target, vertx, testContext, () -> {

			final WeNetUserProfile source = new WeNetUserProfile();
			source.occupation = "Bus driver";
			assertCanMerge(target, source, vertx, testContext, merged -> {

				assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
				target.occupation = "Bus driver";
				assertThat(merged).isEqualTo(target);

			});

		});

	}

	/**
	 * Check merge remove languages.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
	 */
	@Test
	public void shouldMergeRemoveLanguages(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile target = this.createBasicExample(1);
		assertIsValid(target, vertx, testContext, () -> {

			final WeNetUserProfile source = new WeNetUserProfile();
			source.languages = new ArrayList<>();
			assertCanMerge(target, source, vertx, testContext, merged -> {

				assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
				target.languages.clear();
				assertThat(merged).isEqualTo(target);

			});

		});

	}

	/**
	 * Check merge add language and modify another.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
	 */
	@Test
	public void shouldMergeAddAndModifyLanguages(Vertx vertx, VertxTestContext testContext) {

		final WeNetUserProfile target = this.createBasicExample(1);
		target.languages.add(new LanguageTest().createModelExample(2));
		target.languages.get(1).code = "en";
		target.languages.add(new LanguageTest().createModelExample(3));
		target.languages.get(2).code = "fr";
		assertIsValid(target, vertx, testContext, () -> {

			final WeNetUserProfile source = new WeNetUserProfile();
			source.languages = new ArrayList<>();
			source.languages.add(new LanguageTest().createModelExample(2));
			source.languages.add(new LanguageTest().createModelExample(4));
			source.languages.add(new LanguageTest().createModelExample(3));
			source.languages.add(new LanguageTest().createModelExample(1));
			source.languages.get(0).code = "it";
			source.languages.get(1).code = "es";
			source.languages.get(2).code = "fr";
			source.languages.get(2).level = LanguageLevel.B1;
			source.languages.get(3).name = "Catalan";
			assertCanMerge(target, source, vertx, testContext, merged -> {

				assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
				target.languages.add(target.languages.remove(0));
				target.languages.get(0).code = "it";
				target.languages.add(1, new LanguageTest().createModelExample(4));
				target.languages.get(1).code = "es";
				target.languages.get(2).level = LanguageLevel.B1;
				target.languages.get(3).name = "Catalan";
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
	public void shouldMergeRemovePlannedActivities(Vertx vertx, VertxTestContext testContext) {

		this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

			assertIsValid(created, vertx, testContext, () -> {

				StoreServices.storeProfile(created, vertx, testContext, testContext.succeeding(target -> {

					final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldFailMergeBadPlannedActivities(Vertx vertx, VertxTestContext testContext) {

		this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

			assertIsValid(created, vertx, testContext, () -> {

				StoreServices.storeProfile(created, vertx, testContext, testContext.succeeding(target -> {

					final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldFailMergeBadNewPlannedActivities(Vertx vertx, VertxTestContext testContext) {

		this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

			assertIsValid(created, vertx, testContext, () -> {

				StoreServices.storeProfile(created, vertx, testContext, testContext.succeeding(target -> {

					final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldMergeAddmodifyPlannedActivities(Vertx vertx, VertxTestContext testContext) {

		this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

			assertIsValid(created, vertx, testContext, () -> {

				StoreServices.storeProfile(created, vertx, testContext, testContext.succeeding(target -> {

					final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldMergeRemoveRelevantLocations(Vertx vertx, VertxTestContext testContext) {

		this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

			assertIsValid(created, vertx, testContext, () -> {

				StoreServices.storeProfile(created, vertx, testContext, testContext.succeeding(target -> {

					final WeNetUserProfile source = new WeNetUserProfile();
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
	@Test
	@Timeout(value = 1, timeUnit = TimeUnit.DAYS)
	public void shouldFailMergeBadRelevantLocations(Vertx vertx, VertxTestContext testContext) {

		this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

			assertIsValid(created, vertx, testContext, () -> {

				StoreServices.storeProfile(created, vertx, testContext, testContext.succeeding(target -> {

					final WeNetUserProfile source = new WeNetUserProfile();
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
	@Test
	@Timeout(value = 1, timeUnit = TimeUnit.DAYS)
	public void shouldFailMergeBadNewRelevantLocations(Vertx vertx, VertxTestContext testContext) {

		this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

			assertIsValid(created, vertx, testContext, () -> {

				StoreServices.storeProfile(created, vertx, testContext, testContext.succeeding(target -> {

					final WeNetUserProfile source = new WeNetUserProfile();
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
	public void shouldMergeAddModifyRelevantLocations(Vertx vertx, VertxTestContext testContext) {

		this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

			assertIsValid(created, vertx, testContext, () -> {

				StoreServices.storeProfile(created, vertx, testContext, testContext.succeeding(target -> {

					final WeNetUserProfile source = new WeNetUserProfile();
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
	 * Check merge remove social practices.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
	 */
	@Test
	public void shouldMergeRemoveSocialPractices(Vertx vertx, VertxTestContext testContext) {

		this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

			assertIsValid(created, vertx, testContext, () -> {

				StoreServices.storeProfile(created, vertx, testContext, testContext.succeeding(target -> {

					final WeNetUserProfile source = new WeNetUserProfile();
					source.socialPractices = new ArrayList<>();
					assertCanMerge(target, source, vertx, testContext, merged -> {

						assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
						target.socialPractices.clear();
						assertThat(merged).isEqualTo(target);

					});
				}));
			});
		}));
	}

	/**
	 * Check fail merge with a bad defined social practice.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
	 */
	@Test
	@Timeout(value = 1, timeUnit = TimeUnit.DAYS)
	public void shouldFailMergeBadSocialPractices(Vertx vertx, VertxTestContext testContext) {

		this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

			assertIsValid(created, vertx, testContext, () -> {

				StoreServices.storeProfile(created, vertx, testContext, testContext.succeeding(target -> {

					final WeNetUserProfile source = new WeNetUserProfile();
					source.socialPractices = new ArrayList<>();
					source.socialPractices.add(new SocialPractice());
					source.socialPractices.get(0).id = target.socialPractices.get(0).id;
					source.socialPractices.get(0).label = ValidationsTest.STRING_256;
					assertCannotMerge(target, source, "socialPractices[0].label", vertx, testContext);
				}));
			});
		}));

	}

	/**
	 * Check fail merge with a bad new social practice.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
	 */
	@Test
	public void shouldFailMergeBadNewSocialPractices(Vertx vertx, VertxTestContext testContext) {

		this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

			assertIsValid(created, vertx, testContext, () -> {

				StoreServices.storeProfile(created, vertx, testContext, testContext.succeeding(target -> {

					final WeNetUserProfile source = new WeNetUserProfile();
					source.socialPractices = new ArrayList<>();
					source.socialPractices.add(new SocialPractice());
					source.socialPractices.get(0).label = ValidationsTest.STRING_256;
					assertCannotMerge(target, source, "socialPractices[0].label", vertx, testContext);
				}));
			});
		}));

	}

	/**
	 * Check merge add modify social practices.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
	 */
	@Test
	public void shouldMergeAddModifySocialPractices(Vertx vertx, VertxTestContext testContext) {

		this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

			assertIsValid(created, vertx, testContext, () -> {

				StoreServices.storeProfile(created, vertx, testContext, testContext.succeeding(target -> {

					final WeNetUserProfile source = new WeNetUserProfile();
					source.socialPractices = new ArrayList<>();
					source.socialPractices.add(new SocialPractice());
					source.socialPractices.add(new SocialPractice());
					source.socialPractices.get(1).id = target.socialPractices.get(0).id;
					source.socialPractices.get(1).label = "NEW label";
					assertCanMerge(target, source, vertx, testContext, merged -> {

						assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
						target.socialPractices.add(0, new SocialPractice());
						target.socialPractices.get(0).id = merged.socialPractices.get(0).id;
						target.socialPractices.get(1).label = "NEW label";
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
	public void shouldMergeRemovePersonalBehaviors(Vertx vertx, VertxTestContext testContext) {

		this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

			assertIsValid(created, vertx, testContext, () -> {

				StoreServices.storeProfile(created, vertx, testContext, testContext.succeeding(target -> {

					final WeNetUserProfile source = new WeNetUserProfile();
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
	 * Check fail merge with a bad defined personal behavior.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
	 */
	@Test
	@Timeout(value = 1, timeUnit = TimeUnit.DAYS)
	public void shouldFailMergeBadPersonalBehaviors(Vertx vertx, VertxTestContext testContext) {

		this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

			assertIsValid(created, vertx, testContext, () -> {

				StoreServices.storeProfile(created, vertx, testContext, testContext.succeeding(target -> {

					final WeNetUserProfile source = new WeNetUserProfile();
					source.personalBehaviors = new ArrayList<>();
					source.personalBehaviors.add(new Routine());
					source.personalBehaviors.get(0).id = target.personalBehaviors.get(0).id;
					source.personalBehaviors.get(0).label = ValidationsTest.STRING_256;
					assertCannotMerge(target, source, "personalBehaviors[0].label", vertx, testContext);
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
	@Timeout(value = 1, timeUnit = TimeUnit.DAYS)
	public void shouldFailMergeBadNewPersonalBehaviors(Vertx vertx, VertxTestContext testContext) {

		this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

			assertIsValid(created, vertx, testContext, () -> {

				StoreServices.storeProfile(created, vertx, testContext, testContext.succeeding(target -> {

					final WeNetUserProfile source = new WeNetUserProfile();
					source.personalBehaviors = new ArrayList<>();
					source.personalBehaviors.add(new Routine());
					source.personalBehaviors.get(0).label = ValidationsTest.STRING_256;
					assertCannotMerge(target, source, "personalBehaviors[0].label", vertx, testContext);
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
	public void shouldMergeAddModifyPersonalBehaviors(Vertx vertx, VertxTestContext testContext) {

		this.createModelExample(1, vertx, testContext, testContext.succeeding(created -> {

			assertIsValid(created, vertx, testContext, () -> {

				StoreServices.storeProfile(created, vertx, testContext, testContext.succeeding(target -> {

					final WeNetUserProfile source = new WeNetUserProfile();
					source.personalBehaviors = new ArrayList<>();
					source.personalBehaviors.add(new Routine());
					source.personalBehaviors.add(new Routine());
					source.personalBehaviors.get(1).id = target.personalBehaviors.get(0).id;
					source.personalBehaviors.get(1).label = "NEW label";
					assertCanMerge(target, source, vertx, testContext, merged -> {

						assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
						target.personalBehaviors.add(0, new Routine());
						target.personalBehaviors.get(0).id = merged.personalBehaviors.get(0).id;
						target.personalBehaviors.get(1).label = "NEW label";
						assertThat(merged).isEqualTo(target);

					});
				}));
			});
		}));
	}

}
