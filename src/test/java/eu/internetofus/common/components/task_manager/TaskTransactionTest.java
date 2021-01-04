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

package eu.internetofus.common.components.task_manager;

import static eu.internetofus.common.components.ValidationsTest.assertIsNotValid;
import static eu.internetofus.common.components.ValidationsTest.assertIsValid;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import eu.internetofus.common.TimeManager;
import eu.internetofus.common.components.ModelTestCase;
import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.components.profile_manager.WeNetProfileManagerMocker;
import eu.internetofus.common.components.service.MessageTest;
import eu.internetofus.common.components.service.WeNetService;
import eu.internetofus.common.components.service.WeNetServiceSimulator;
import eu.internetofus.common.components.service.WeNetServiceSimulatorMocker;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link TaskTransaction}.
 *
 * @see TaskTransaction
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class TaskTransactionTest extends ModelTestCase<TaskTransaction> {

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

    final var client = WebClient.create(vertx);
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
  public TaskTransaction createModelExample(final int index) {

    assert index >= 0;
    final var model = new TaskTransaction();
    model._creationTs = index;
    model._lastUpdateTs = TimeManager.now() - index;
    model.actioneerId = "actioneerId_" + index;
    model.attributes = new JsonObject().put("index", index);
    model.id = "id_" + index;
    model.label = "label_" + index;
    model.messages = new ArrayList<>();
    model.messages.add(new MessageTest().createModelExample(index));
    model.taskId = "taskId_" + index;
    return model;

  }

  /**
   * Create a valid task transaction example.
   *
   * @param index       of the example to create.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @return a future with the created task transaction.
   *
   */
  public Future<TaskTransaction> createModelExample(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    return StoreServices.storeTaskExample(index, vertx, testContext).compose(task -> {

      final var model = this.createModelExample(index);
      model.taskId = task.id;
      model.actioneerId = task.requesterId;
      return Future.succeededFuture(model);

    });
  }

  /**
   * Check that the {@link #createModelExample(int)} is not valid.
   *
   * @param index       to verify.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleNotBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(index);
    assertIsNotValid(model, "taskId", vertx, testContext);

  }

  /**
   * Check that the {@link #createModelExample(int,Vertx,VertxTestContext)} is
   * valid.
   *
   * @param index       to verify.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(index, vertx, testContext)
        .onComplete(testContext.succeeding(model -> assertIsValid(model, vertx, testContext)));

  }

  /**
   * An empty transaction not be valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldEmptyTransactionBeNotValid(final Vertx vertx, final VertxTestContext testContext) {

    assertIsNotValid(new TaskTransaction(), "taskId", vertx, testContext);
  }

  /**
   * A task transaction without task identifier cannot be valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldTaskTransactionBeNotValidWithoutTaskId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onComplete(testContext.succeeding(model -> {
      model.taskId = null;
      assertIsNotValid(model, "taskId", vertx, testContext);
    }));

  }

  /**
   * A task transaction without task type identifier cannot be valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Task#validate(String, Vertx)
   */
  @Test
  public void shouldTaskTransactionBeNotValidWithoutTaskLabel(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onComplete(testContext.succeeding(model -> {
      model.label = null;
      assertIsNotValid(model, "label", vertx, testContext);

    }));
  }

}
