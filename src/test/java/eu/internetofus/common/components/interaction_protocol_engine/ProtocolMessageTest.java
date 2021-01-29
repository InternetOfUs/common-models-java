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

package eu.internetofus.common.components.interaction_protocol_engine;

import static eu.internetofus.common.components.AbstractComponentMocker.createClientWithDefaultSession;
import static eu.internetofus.common.components.ValidationsTest.assertIsNotValid;
import static eu.internetofus.common.components.ValidationsTest.assertIsValid;

import eu.internetofus.common.components.ModelTestCase;
import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.ValidationsTest;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.components.profile_manager.WeNetProfileManagerMocker;
import eu.internetofus.common.components.service.WeNetService;
import eu.internetofus.common.components.service.WeNetServiceSimulator;
import eu.internetofus.common.components.service.WeNetServiceSimulatorMocker;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import eu.internetofus.common.components.task_manager.WeNetTaskManagerMocker;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test the {@link ProtocolMessage}
 *
 * @see ProtocolMessage
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class ProtocolMessageTest extends ModelTestCase<ProtocolMessage> {

  /**
   * The profile manager mocked server.
   */
  protected static WeNetProfileManagerMocker profileManagerMocker;

  /**
   * The task manager mocked server.
   */
  protected static WeNetTaskManagerMocker taskManagerMocker;

  /**
   * The service mocked server.
   */
  protected static WeNetServiceSimulatorMocker serviceMocker;

  /**
   * Start the mocker server.
   */
  @BeforeAll
  public static void startMockers() {

    profileManagerMocker = WeNetProfileManagerMocker.start();
    taskManagerMocker = WeNetTaskManagerMocker.start();
    serviceMocker = WeNetServiceSimulatorMocker.start();
  }

  /**
   * Stop the mocker server.
   */
  @AfterAll
  public static void stopMockers() {

    profileManagerMocker.stopServer();
    taskManagerMocker.stopServer();
    serviceMocker.stopServer();
  }

  /**
   * Register the necessary services before to test.
   *
   * @param vertx event bus to register the necessary services.
   */
  @BeforeEach
  public void registerServices(final Vertx vertx) {

    final var client = createClientWithDefaultSession(vertx);
    final var profileConf = profileManagerMocker.getComponentConfiguration();
    WeNetProfileManager.register(vertx, client, profileConf);

    final var taskConf = taskManagerMocker.getComponentConfiguration();
    WeNetTaskManager.register(vertx, client, taskConf);

    final var conf = serviceMocker.getComponentConfiguration();
    WeNetService.register(vertx, client, conf);
    WeNetServiceSimulator.register(vertx, client, conf);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ProtocolMessage createModelExample(final int index) {

    final var model = new ProtocolMessage();
    model.appId = "appId_" + index;
    model.communityId = "communityId_" + index;
    model.sender = new ProtocolAddressTest().createModelExample(index);
    model.receiver = new ProtocolAddressTest().createModelExample(index);
    model.particle = "particle_" + index;
    model.taskId = "taskId_" + index;
    model.transactionId = "transactionId_" + index;
    model.content = new JsonObject().put("index", index);

    return model;

  }

  /**
   * Create an example model that has the specified index that create any required
   * component.
   *
   * @param index       to use in the example.
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @return the created protocol message.
   */
  public Future<ProtocolMessage> createModelExample(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    return testContext.assertComplete(StoreServices.storeTaskExample(index, vertx, testContext).compose(task -> {

      return StoreServices.storeCommunityExample(index, vertx, testContext).compose(community -> {

        return new ProtocolAddressTest().createModelExample(index, vertx, testContext).compose(sender -> {

          return new ProtocolAddressTest().createModelExample(index + 1, vertx, testContext).compose(receiver -> {

            final var model = this.createModelExample(index);
            model.appId = task.appId;
            model.taskId = task.id;
            model.communityId = community.id;
            model.sender = sender;
            model.receiver = receiver;
            return Future.succeededFuture(model);

          });
        });
      });
    }));

  }

  /**
   * Check that the {@link #createModelExample(int)} is NOT valid.
   *
   * @param index       to verify
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolMessage#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5, 6 })
  public void shouldExampleNotBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(index);
    assertIsNotValid(model, vertx, testContext);

  }

  /**
   * Check that the {@link #createModelExample(int, Vertx, VertxTestContext)} is
   * valid.
   *
   * @param index       to verify
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolMessage#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5, 6 })
  public void shouldExampleBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(index, vertx, testContext).onSuccess(model -> assertIsValid(model, vertx, testContext));

  }

  /**
   * Check that is valid without appId.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolMessage#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithoutAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.appId = null;
      assertIsValid(model, vertx, testContext);
    });
  }

  /**
   * Check that not accept a large appId.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolMessage#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithALargeAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.appId = ValidationsTest.STRING_256;
      assertIsNotValid(model, "appId", vertx, testContext);
    });
  }

  /**
   * Check that not accept an undefined appId.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolMessage#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithUndefinedAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.appId = "undefined";
      assertIsNotValid(model, "appId", vertx, testContext);
    });
  }

  /**
   * Check that is valid without communityId.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolMessage#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithoutCommunityId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.communityId = null;
      assertIsValid(model, vertx, testContext);
    });
  }

  /**
   * Check that not accept a large communityId.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolMessage#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithALargeCommunityId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.communityId = ValidationsTest.STRING_256;
      assertIsNotValid(model, "communityId", vertx, testContext);
    });
  }

  /**
   * Check that is valid without sender.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolMessage#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithoutSender(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.sender = null;
      assertIsNotValid(model, "sender", vertx, testContext);
    });
  }

  /**
   * Check that is valid with bad sender.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolMessage#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithBadSender(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.sender = new ProtocolAddressTest().createModelExample(1);
      assertIsNotValid(model, "sender.userId", vertx, testContext);
    });
  }

  /**
   * Check that is valid without receiver.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolMessage#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithoutReceiver(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.receiver = null;
      assertIsNotValid(model, "receiver", vertx, testContext);
    });
  }

  /**
   * Check that is valid with bad receiver.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolMessage#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithBadReceiver(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.receiver = new ProtocolAddressTest().createModelExample(2);
      assertIsNotValid(model, "receiver.userId", vertx, testContext);
    });
  }

  /**
   * Check that is valid without particle.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolMessage#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithoutParticle(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.particle = null;
      assertIsNotValid(model, "particle", vertx, testContext);
    });
  }

  /**
   * Check that is valid with large particle.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolMessage#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithLatgeParticle(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.particle = ValidationsTest.STRING_256;
      assertIsNotValid(model, "particle", vertx, testContext);
    });
  }

  /**
   * Check that is valid without taskId.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolMessage#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithoutTaskId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.taskId = null;
      assertIsValid(model, vertx, testContext);
    });
  }

  /**
   * Check that not accept a large taskId.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolMessage#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithALargeTaskId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.taskId = ValidationsTest.STRING_256;
      assertIsNotValid(model, "taskId", vertx, testContext);
    });
  }

  /**
   * Check that not accept a large transactionId.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolMessage#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithALargeTransactionId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.transactionId = ValidationsTest.STRING_256;
      assertIsNotValid(model, "transactionId", vertx, testContext);
    });
  }

  /**
   * Check that not accept an undefined taskId.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolMessage#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithAnUndefinedTaskId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(2, vertx, testContext).onSuccess(model -> {
      model.taskId = "undefined";
      assertIsNotValid(model, "taskId", vertx, testContext);
    });
  }

  /**
   * Check that not accept without content.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see ProtocolMessage#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithoutContent(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.content = null;
      assertIsNotValid(model, "content", vertx, testContext);
    });
  }

}
