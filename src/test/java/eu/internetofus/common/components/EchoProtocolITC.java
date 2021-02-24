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

package eu.internetofus.common.components;

import eu.internetofus.common.components.task_manager.TaskTransaction;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import java.util.UUID;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Check the Echo protocol.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class EchoProtocolITC extends AbstractProtocolITC {

  /**
   * {@inheritDoc}
   *
   * @return {@link WeNetTaskManager#ECHO_V1_TASK_TYPE_ID}
   */
  @Override
  protected String getDefaultTaskTypeIdToUse() {

    return WeNetTaskManager.ECHO_V1_TASK_TYPE_ID;
  }

  /**
   * Should send transaction echo.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(6)
  public void shouldDoTransactionEcho(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(5, testContext);

    final var transaction = new TaskTransaction();
    transaction.actioneerId = this.task.requesterId;
    transaction.taskId = this.task.id;
    transaction.label = "echo";
    final var message = UUID.randomUUID().toString();
    transaction.attributes = new JsonObject().put("message", message);
    final var future = WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction)
        .compose(done -> this.waitUntilTask(vertx, testContext, () -> {

          if (this.task.transactions != null && !this.task.transactions.isEmpty()) {

            final var lastTransaction = this.task.transactions.get(this.task.transactions.size() - 1);
            if (lastTransaction.label.equals(transaction.label)
                && lastTransaction.actioneerId.equals(transaction.actioneerId)
                && lastTransaction.attributes.equals(transaction.attributes)) {

              return true;

            }
          }

          return false;

        }).compose(task -> this.waitUntilCallbacks(vertx, testContext,
            new MessagePredicateBuilder().withLabelAndReceiverId("echo", transaction.actioneerId).build())));

    testContext.assertComplete(future).onComplete(removed -> this.assertSuccessfulCompleted(testContext));

  }
}
