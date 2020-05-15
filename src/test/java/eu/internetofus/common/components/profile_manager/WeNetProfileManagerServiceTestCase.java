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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link WeNetProfileManagerService}.
 *
 * @see WeNetProfileManagerService
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class WeNetProfileManagerServiceTestCase {

	/**
	 * Should not create a bad profile.
	 *
	 * @param vertx       that contains the event bus to use.
	 * @param testContext context over the tests.
	 */
	@Test
	public void shouldNotCreateBadProfile(Vertx vertx, VertxTestContext testContext) {

		WeNetProfileManagerService.createProxy(vertx).createProfile(new JsonObject().put("undefinedField", "value"),
				testContext.failing(handler -> {
					testContext.completeNow();

				}));

	}

	/**
	 * Should not retrieve undefined profile.
	 *
	 * @param vertx       that contains the event bus to use.
	 * @param testContext context over the tests.
	 */
	@Test
	public void shouldNotRretrieveUndefinedProfile(Vertx vertx, VertxTestContext testContext) {

		WeNetProfileManagerService.createProxy(vertx).retrieveProfile("undefined-profile-identifier",
				testContext.failing(handler -> {
					testContext.completeNow();

				}));

	}

	/**
	 * Should not delete undefined profile.
	 *
	 * @param vertx       that contains the event bus to use.
	 * @param testContext context over the tests.
	 */
	@Test
	public void shouldNotDeleteUndefinedProfile(Vertx vertx, VertxTestContext testContext) {

		WeNetProfileManagerService.createProxy(vertx).deleteProfile("undefined-profile-identifier",
				testContext.failing(handler -> {
					testContext.completeNow();

				}));

	}

	/**
	 * Should create, retrieve and delete a profile.
	 *
	 * @param vertx       that contains the event bus to use.
	 * @param testContext context over the tests.
	 */
	@Test
	public void shouldCreateRetrieveAndDeleteProfile(Vertx vertx, VertxTestContext testContext) {

		final WeNetProfileManagerService service = WeNetProfileManagerService.createProxy(vertx);
		service.createProfile(new WeNetUserProfile(), testContext.succeeding(create -> {

			final String id = create.id;
			service.retrieveProfile(id, testContext.succeeding(retrieve -> testContext.verify(() -> {

				assertThat(create).isEqualTo(retrieve);
				service.deleteProfile(id, testContext.succeeding(empty -> {

					service.retrieveProfile(id, testContext.failing(handler -> {
						testContext.completeNow();

					}));

				}));

			})));

		}));

	}

}
