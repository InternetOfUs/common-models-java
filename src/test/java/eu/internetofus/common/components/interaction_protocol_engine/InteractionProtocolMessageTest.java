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

package eu.internetofus.common.components.interaction_protocol_engine;

import static eu.internetofus.common.components.ValidationsTest.assertIsNotValid;
import static eu.internetofus.common.components.ValidationsTest.assertIsValid;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import eu.internetofus.common.components.ModelTestCase;
import eu.internetofus.common.components.ValidationsTest;
import eu.internetofus.common.components.interaction_protocol_engine.InteractionProtocolMessage;
import eu.internetofus.common.components.profile_manager.Norm;
import eu.internetofus.common.components.profile_manager.NormTest;
import eu.internetofus.common.components.profile_manager.WeNetUserProfile;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link InteractionProtocolMessage}
 *
 * @see InteractionProtocolMessage
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class InteractionProtocolMessageTest extends ModelTestCase<InteractionProtocolMessage> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InteractionProtocolMessage createModelExample(int index) {

		final InteractionProtocolMessage model = new InteractionProtocolMessage();
		model.appId = "appId_" + index;
		model.communityId = "communityId_" + index;
		model.senderId = "senderId_" + index;
		model.taskId = "taskId_" + index;
		model.content = new JsonObject().put("Content", index);
		model.norms = new ArrayList<>();
		model.norms.add(new NormTest().createModelExample(index));
		return model;

	}

	/**
	 * Check that not accept a large appId.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithALargeAppId(Vertx vertx, VertxTestContext testContext) {

		final InteractionProtocolMessage model = this.createModelExample(1);
		model.appId = ValidationsTest.STRING_256;
		assertIsNotValid(model, "appId", vertx, testContext);
	}

	/**
	 * Check that not accept a large communityId.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithALargeCommunityId(Vertx vertx, VertxTestContext testContext) {

		final InteractionProtocolMessage model = this.createModelExample(1);
		model.communityId = ValidationsTest.STRING_256;
		assertIsNotValid(model, "communityId", vertx, testContext);
	}

	/**
	 * Check that not accept a large senderId.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithALargeSenderId(Vertx vertx, VertxTestContext testContext) {

		final InteractionProtocolMessage model = this.createModelExample(1);
		model.senderId = ValidationsTest.STRING_256;
		assertIsNotValid(model, "senderId", vertx, testContext);
	}

	/**
	 * Check that not accept a large taskId.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithALargeTaskId(Vertx vertx, VertxTestContext testContext) {

		final InteractionProtocolMessage model = this.createModelExample(1);
		model.taskId = ValidationsTest.STRING_256;
		assertIsNotValid(model, "taskId", vertx, testContext);
	}

	/**
	 * Check that not accept without content.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithoutContent(Vertx vertx, VertxTestContext testContext) {

		final InteractionProtocolMessage model = this.createModelExample(1);
		model.content = null;
		assertIsNotValid(model, "content", vertx, testContext);
	}

	/**
	 * Check that not accept with a bad norm.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see WeNetUserProfile#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithABadNorm(Vertx vertx, VertxTestContext testContext) {

		final InteractionProtocolMessage model = this.createModelExample(1);
		final Norm badNorm = new Norm();
		badNorm.attribute = ValidationsTest.STRING_256;
		model.norms.add(badNorm);
		assertIsNotValid(model, "norms[1].attribute", vertx, testContext);
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

		final InteractionProtocolMessage model = this.createModelExample(index);
		assertIsValid(model, vertx, testContext);

	}
}
