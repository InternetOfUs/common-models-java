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

import static eu.internetofus.common.api.models.ValidationsTest.assertIsNotValid;
import static eu.internetofus.common.api.models.ValidationsTest.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import eu.internetofus.common.api.models.Model;
import eu.internetofus.common.api.models.ModelTestCase;
import eu.internetofus.common.api.models.ValidationsTest;
import eu.internetofus.common.services.WeNetProfileManagerService;
import eu.internetofus.common.services.WeNetProfileManagerServiceOnMemory;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link SocialNetworkRelationship}.
 *
 * @see SocialNetworkRelationship
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class SocialNetworkRelantionshipTest extends ModelTestCase<SocialNetworkRelationship> {

	/**
	 * Test constructor.
	 *
	 * @see SocialNetworkRelationship#SocialNetworkRelationship(SocialNetworkRelationshipType,
	 *      String)
	 */
	@Test
	public void shouldCreateARelationship() {

		final SocialNetworkRelationshipType type = SocialNetworkRelationshipType.colleague;
		final String userId = UUID.randomUUID().toString();
		final SocialNetworkRelationship model = new SocialNetworkRelationship(type, userId);
		assertThat(model.type).isEqualTo(type);
		assertThat(model.userId).isEqualTo(userId);

	}

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
	 * {@inheritDoc}
	 */
	@Override
	public SocialNetworkRelationship createModelExample(int index) {

		final SocialNetworkRelationship model = new SocialNetworkRelationship();
		model.userId = String.valueOf(index);
		model.type = SocialNetworkRelationshipType.values()[index % (SocialNetworkRelationshipType.values().length - 1)];
		return model;

	}

	/**
	 * Create a new empty user profile. It has to be stored into the repository.
	 *
	 * @param vertx    event bus to use.
	 * @param creation handler to manage the created user profile.
	 */
	protected void createNewEmptyProfile(Vertx vertx, Handler<AsyncResult<WeNetUserProfile>> creation) {

		WeNetProfileManagerService.createProxy(vertx).createProfile(new JsonObject(), (creationResult) -> {

			if (creationResult.failed()) {

				creation.handle(Future.failedFuture(creationResult.cause()));

			} else {

				final WeNetUserProfile profile = Model.fromJsonObject(creationResult.result(), WeNetUserProfile.class);
				if (profile == null) {

					creation.handle(Future.failedFuture("Can not obtain a profile form the JSON result"));

				} else {

					creation.handle(Future.succeededFuture(profile));
				}

			}
		});
	}

	/**
	 * Create an example model that has the specified index.
	 *
	 * @param index       to use in the example.
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 * @param creation    handler to manage the created social network relationship.
	 */
	public void createModelExample(int index, Vertx vertx, VertxTestContext testContext,
			Handler<AsyncResult<SocialNetworkRelationship>> creation) {

		this.createNewEmptyProfile(vertx, testContext.succeeding(profile -> {

			final SocialNetworkRelationship relation = this.createModelExample(index);
			relation.userId = profile.id;
			creation.handle(Future.succeededFuture(relation));

		}));

	}

	/**
	 * Check that the
	 * {@link #createModelExample(int, Vertx, VertxTestContext, Handler)} is valid.
	 *
	 * @param index       to verify
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see SocialNetworkRelationship#validate(String, Vertx)
	 */
	@ParameterizedTest(name = "The model example {0} has to be valid")
	@ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
	public void shouldExampleFromRepositoryBeValid(int index, Vertx vertx, VertxTestContext testContext) {

		this.createModelExample(index, vertx, testContext, testContext.succeeding(model -> {

			final String originalUserId = model.userId;
			model.userId = "   " + originalUserId + "   ";
			assertIsValid(model, vertx, testContext, () -> {

				assertThat(model.userId).isEqualTo(originalUserId);
			});
		}));

	}

	/**
	 * Check that a model is not valid with an undefined user id.
	 *
	 * @param userId      that is not valid.
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see SocialNetworkRelationship#validate(String, Vertx)
	 */
	@ParameterizedTest(name = "Should not be valid  a SocialNetworkRelationship with an userId = {0}")
	@NullAndEmptySource
	@ValueSource(strings = { "undefined value ", "9bec40b8-8209-4e28-b64b-1de52595ca6d", ValidationsTest.STRING_256 })
	public void shouldNotBeValidWithBadUserIdentifier(String userId, Vertx vertx, VertxTestContext testContext) {

		final SocialNetworkRelationship model = new SocialNetworkRelationship();
		model.userId = userId;
		model.type = SocialNetworkRelationshipType.colleague;
		assertIsNotValid(model, "userId", vertx, testContext);

	}

	/**
	 * Check that a model is not valid without a type.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see SocialNetworkRelationship#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidAmodelWithoutAType(Vertx vertx, VertxTestContext testContext) {

		this.createNewEmptyProfile(vertx, testContext.succeeding(profile -> {
			final SocialNetworkRelationship model = new SocialNetworkRelationship();
			model.type = null;
			model.userId = profile.id;
			assertIsNotValid(model, "type", vertx, testContext);

		}));

	}

	/**
	 * Check that a model is not valid without an user id.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see SocialNetworkRelationship#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidAmodelWithoutAUserId(Vertx vertx, VertxTestContext testContext) {

		final SocialNetworkRelationship model = new SocialNetworkRelationship();
		model.type = SocialNetworkRelationshipType.colleague;
		model.userId = null;
		assertIsNotValid(model, "userId", vertx, testContext);

	}

}
