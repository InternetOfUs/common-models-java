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

import static eu.internetofus.common.model.ValidableAsserts.assertIsNotValid;
import static eu.internetofus.common.model.ValidableAsserts.assertIsValid;

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.WeNetIntegrationExtension;
import eu.internetofus.common.components.service.App;
import eu.internetofus.common.model.ModelTestCase;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test the {@link Incentive}.
 *
 * @see Incentive
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetIntegrationExtension.class)
public class IncentiveTest extends ModelTestCase<Incentive> {

  /**
   * {@inheritDoc}
   */
  @Override
  public Incentive createModelExample(final int index) {

    final var model = new Incentive();
    model.AppID = "AppID" + index;
    model.UserId = "UserId" + index;
    model.IncentiveType = "incentive type" + index;
    model.Issuer = "Issuer" + index;
    model.Message = new IncentiveMessageTest().createModelExample(index);
    model.Badge = new IncentiveBadgeTest().createModelExample(index);
    return model;
  }

  /**
   * Create an example model that has the specified index.
   *
   * @param index       to use in the example.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @return the created incentive.
   */
  public Future<Incentive> createModelExample(final int index, final Vertx vertx, final VertxTestContext testContext) {

    return testContext
        .assertComplete(StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).compose(profile -> {

          return StoreServices.storeApp(new App(), vertx, testContext).compose(app -> {

            final var model = this.createModelExample(index);
            model.UserId = profile.id;
            model.AppID = app.appId;
            return Future.succeededFuture(model);

          });

        }));

  }

  /**
   * Check that the {@link #createModelExample(int)} is not valid.
   *
   * @param index       to verify
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Incentive#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleNoBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(index);
    assertIsNotValid(model, vertx, testContext);

  }

  /**
   * Check that the {@link #createModelExample(int, Vertx, VertxTestContext)} is
   * valid.
   *
   * @param index       to verify
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Incentive#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(index, vertx, testContext).onSuccess(model -> assertIsValid(model, vertx, testContext));

  }

  /**
   * Check that not accept an undefined appId.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Incentive#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithAnUndefinedAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.AppID = "Undefined";
      assertIsNotValid(model, "AppID", vertx, testContext);
    });

  }

  /**
   * Check that not accept an undefined userId.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Incentive#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithAnUndefinedUserId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.UserId = "Undefined";
      assertIsNotValid(model, "UserId", vertx, testContext);
    });

  }

  /**
   * Check that not accept without message and badge.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see Incentive#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithoutMesageAndBadge(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.Message = null;
      model.Badge = null;
      assertIsNotValid(model, "Message", vertx, testContext);
    });

  }

}
