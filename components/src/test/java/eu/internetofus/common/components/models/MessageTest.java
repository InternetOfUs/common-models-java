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
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test the {@link Message}
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetIntegrationExtension.class)
public class MessageTest extends ModelTestCase<Message> {

  /**
   * {@inheritDoc}
   */
  @Override
  public Message createModelExample(final int index) {

    final var model = new Message();
    model.appId = "appId_" + index;
    model.attributes = new JsonObject().put("key", "value").put("index", index)
        .put("communityId", "communityId_" + index).put("taskId", "taskId" + index);
    model.label = "label_" + index;
    model.receiverId = "receiverId_" + index;
    return model;

  }

  /**
   * Create an example model that has the specified index.
   *
   * @param index       to use in the example.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @return the created message.
   */
  public Future<Message> createModelExample(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(index);
    return StoreServices.storeApp(new App(), vertx, testContext).compose(app -> {

      model.appId = app.appId;
      return StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext);

    }).compose(profile -> {

      model.receiverId = profile.id;
      return Future.succeededFuture(model);

    });

  }

  /**
   * Check that the {@link #createModelExample(int)} is not valid.
   *
   * @param index       to verify
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Message#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The model example {0} has to be not valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldSimpleExampleBeInValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

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
   * @see Message#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(index, vertx, testContext).onSuccess(model -> assertIsValid(model, vertx, testContext));

  }

  /**
   * Check that an empty message is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Message#validate(String, Vertx)
   */
  @Test
  public void shouldEmptyMessageNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    assertIsNotValid(new Message(), vertx, testContext);

  }

  /**
   * Check that can not be valid with a bad app identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.appId = "undefined";
      assertIsNotValid(model, "appId", vertx, testContext);

    });

  }

  /**
   * Check that can not be valid with a bad receiver identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadReceiverId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.receiverId = "undefined";
      assertIsNotValid(model, "receiverId", vertx, testContext);

    });

  }

}
