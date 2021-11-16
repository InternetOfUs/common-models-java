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

package eu.internetofus.common.components.interaction_protocol_engine;

import static eu.internetofus.common.model.ValidableAsserts.assertIsNotValid;
import static eu.internetofus.common.model.ValidableAsserts.assertIsValid;

import eu.internetofus.common.components.WeNetIntegrationExtension;
import eu.internetofus.common.components.WeNetValidateContext;
import eu.internetofus.common.components.models.TaskTransactionTest;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import eu.internetofus.common.model.ModelTestCase;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test the {@link AbstractProtocolAction}.
 *
 * @param <T> type of action to test.
 *
 * @see AbstractProtocolAction
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetIntegrationExtension.class)
public abstract class AbstractProtocolActionTestCase<T extends AbstractProtocolAction> extends ModelTestCase<T> {

  /**
   * Check that the {@link #createModelExample(int)} is NOT valid.
   *
   * @param index       to verify
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolEvent#validate(eu.internetofus.common.components.WeNetValidateContext)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5, 6 })
  public void shouldExampleNotBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(index);
    assertIsNotValid(model, new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Create an example model that has the specified index that create any required
   * component.
   *
   * @param index       to use in the example.
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @return the created protocol event.
   */
  public Future<T> createModelExample(final int index, final Vertx vertx, final VertxTestContext testContext) {

    return testContext
        .assertComplete(new TaskTransactionTest().createModelExample(index, vertx, testContext).compose(transaction -> {

          return WeNetTaskManager.createProxy(vertx).addTransactionIntoTask(transaction.taskId, transaction)
              .compose(addedTransaction -> {

                return WeNetTaskManager.createProxy(vertx).retrieveTask(transaction.taskId).compose(task -> {

                  final var model = this.createModelExample(index);
                  model.appId = task.appId;
                  model.communityId = task.communityId;
                  model.taskId = task.id;
                  model.transactionId = addedTransaction.id;
                  return Future.succeededFuture(model);

                });
              });

        }));
  }

  /**
   * Check that the {@link #createModelExample(int, Vertx, VertxTestContext)} is
   * valid.
   *
   * @param index       to verify
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolEvent#validate(WeNetValidateContext)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5, 6 })
  public void shouldExampleBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(index, vertx, testContext)
        .onSuccess(model -> assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext));

  }

  /**
   * Check that is valid without appId.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolEvent#validate(WeNetValidateContext)
   */
  @Test
  public void shouldBeValidWithoutAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.appId = null;
      assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext);
    });
  }

  /**
   * Check that not accept an undefined appId.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolEvent#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithUndefinedAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.appId = "undefined";
      assertIsNotValid(model, "appId", new WeNetValidateContext("codePrefix", vertx), testContext);
    });
  }

  /**
   * Check that is valid without communityId.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolEvent#validate(WeNetValidateContext)
   */
  @Test
  public void shouldBeValidWithoutCommunityId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.communityId = null;
      assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext);
    });
  }

  /**
   * Check that not accept an undefined communityId.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolEvent#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithUndefinedCommunityId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.communityId = "undefined";
      assertIsNotValid(model, "communityId", new WeNetValidateContext("codePrefix", vertx), testContext);
    });
  }

  /**
   * Check that is valid without taskId.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolEvent#validate(WeNetValidateContext)
   */
  @Test
  public void shouldBeValidWithoutTaskId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.taskId = null;
      model.transactionId = null;
      assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext);
    });
  }

  /**
   * Check that not accept an undefined taskId.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolEvent#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithUndefinedTaskId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.taskId = "undefined";
      assertIsNotValid(model, "taskId", new WeNetValidateContext("codePrefix", vertx), testContext);
    });
  }

  /**
   * Check that is valid without transactionId.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolEvent#validate(WeNetValidateContext)
   */
  @Test
  public void shouldBeValidWithoutTransactionId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.transactionId = null;
      assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext);
    });
  }

  /**
   * Check that is valid without transactionId.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolEvent#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithTransactionIdAndWithoutTask(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.taskId = null;
      assertIsNotValid(model, "transactionId", new WeNetValidateContext("codePrefix", vertx), testContext);
    });
  }

  /**
   * Check that not accept an undefined transactionId.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolEvent#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithUndefinedTransactionId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.transactionId = "undefined";
      assertIsNotValid(model, "transactionId", new WeNetValidateContext("codePrefix", vertx), testContext);
    });
  }

  /**
   * Check that is valid without particle.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolEvent#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithoutParticle(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.particle = null;
      assertIsNotValid(model, "particle", new WeNetValidateContext("codePrefix", vertx), testContext);
    });
  }

  /**
   * Check that not accept without content.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolEvent#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithoutContent(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.content = null;
      assertIsNotValid(model, "content", new WeNetValidateContext("codePrefix", vertx), testContext);
    });
  }

}
