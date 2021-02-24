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

import eu.internetofus.common.components.task_manager.Task;
import eu.internetofus.common.components.task_manager.TaskTransaction;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Check the eat together protocol. ATTENTION: This test is sequential and
 * maintains the state between methods. In other words, you must to run the
 * entire test methods on the specified order to work.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractEatTogetherProtocolITC extends AbstractProtocolITC {

  /**
   * {@inheritDoc}
   */
  @Override
  protected Future<Task> waitUntilTaskCreated(final Vertx vertx, final VertxTestContext testContext) {

    final var mesageBuilder = new MessagePredicateBuilder();
    for (final var user : this.users) {

      if (!user.id.equals(this.task.requesterId)) {

        mesageBuilder.with(msg -> {

          return user.id.equals(msg.receiverId) && "TaskProposalNotification".equals(msg.label)
              && msg.attributes != null && this.task.id.equals(msg.attributes.getString("taskId"));
        });
      }

    }
    return this.waitUntilCallbacks(vertx, testContext, mesageBuilder.build())
        .compose(messages -> this.waitUntilTask(vertx, testContext, () -> {

          final var attributes = this.task.attributes;
          if (attributes == null || this.task.transactions == null || this.task.transactions.size() != 1) {

            return false;

          }

          final var unanswered = attributes.getJsonArray("unanswered");
          if (unanswered == null || unanswered.size() != this.users.size() - 1) {

            return false;

          }
          final var transaction = this.task.transactions.get(0);
          if (transaction.messages == null || transaction.messages.size() != this.users.size() - 1) {

            return false;

          }
          for (final var user : this.users) {

            if (!user.id.equals(this.task.requesterId)) {

              if (!unanswered.contains(user.id)) {

                return false;
              }
              var found = false;
              for (final var message : transaction.messages) {

                if (user.id.equals(message.receiverId) && "TaskProposalNotification".equals(message.label)
                    && message.attributes != null && this.task.id.equals(message.attributes.getString("taskId"))) {
                  found = true;
                  break;

                }

              }
              if (!found) {

                return false;
              }

            }
          }

          return true;

        }));

  }

  /**
   * Check that an user decline to do a task.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(6)
  public void shouldDeclineTask(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(5, testContext);

    final var transaction = new TaskTransaction();
    final var volunteerId = this.users.get(1).id;
    transaction.actioneerId = volunteerId;
    transaction.taskId = this.task.id;
    transaction.label = "refuseTask";
    transaction.attributes = new JsonObject().put("volunteerId", volunteerId);
    final var future = WeNetTaskManager.createProxy(vertx).doTaskTransaction(transaction)
        .compose(done -> this.waitUntilTask(vertx, testContext, () -> {

          final var attributes = this.task.attributes;
          if (attributes == null || this.task.transactions == null || this.task.transactions.size() != 2) {

            return false;

          }
          final var lastTransaction = this.task.transactions.get(1);
          if (!lastTransaction.label.equals(transaction.label)
              || !lastTransaction.actioneerId.equals(transaction.actioneerId)
              || !lastTransaction.attributes.equals(transaction.attributes)) {

            return false;
          }
          final var unanswered = attributes.getJsonArray("unanswered");
          final var declined = attributes.getJsonArray("declined");
          if (unanswered == null || unanswered.contains(volunteerId) || declined == null || declined.size() != 1
              || !declined.contains(volunteerId)) {

            return false;
          }
          return true;

        }));

    testContext.assertComplete(future).onComplete(removed -> this.assertSuccessfulCompleted(testContext));

  }
}
