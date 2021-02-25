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

import eu.internetofus.common.components.service.Message;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.validation.constraints.NotNull;

/**
 * Create the predicate to check if the task is in the specified state.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class TaskPredicateBuilder {

  /**
   * The builder predicates.
   */
  protected List<Predicate<Task>> predicates = new ArrayList<>();

  /**
   * The predicated to use.
   *
   * @return the build predicates.
   */
  public Predicate<Task> build() {

    return task -> {

      for (final var predicate : this.predicates) {

        if (!predicate.test(task)) {

          return false;

        }
      }

      return true;

    };

  }

  /**
   * Add a predicate for the task.
   *
   * @param predicate to match the task.
   *
   * @return this builder.
   */
  public TaskPredicateBuilder with(final Predicate<Task> predicate) {

    this.predicates.add(predicate);
    return this;
  }

  /**
   * Add a predicate that the task has transactions.
   *
   * @return this builder.
   */
  public TaskPredicateBuilder withTransactions() {

    return this.with(task -> task.transactions != null && !task.transactions.isEmpty());
  }

  /**
   * Add a predicate that the task has the specified number of transactions.
   *
   * @param size number of transactions.
   *
   * @return this builder.
   */
  public TaskPredicateBuilder withTransactions(final int size) {

    return this.with(task -> task.transactions != null && task.transactions.size() == size);
  }

  /**
   * Add a predicate that check that on the task exist a similar transaction.
   *
   * @param transaction to be similar on the transaction of the task
   *
   * @return this builder.
   */
  public TaskPredicateBuilder withSimilarTransaction(@NotNull final TaskTransaction transaction) {

    return this.with(task -> {

      if (task.transactions != null) {

        for (final var target : task.transactions) {

          if ((transaction.taskId == null || transaction.taskId.equals(target.taskId))
              && (transaction.label == null || transaction.label.equals(target.label))
              && (transaction.actioneerId == null || transaction.actioneerId.equals(target.actioneerId))
              && (transaction.attributes == null || transaction.attributes.equals(target.attributes))
              && (transaction.messages == null || transaction.messages.equals(target.messages))) {
            return true;
          }

        }

      }

      return false;

    });
  }

  /**
   * Add a predicate that check that on the task exist a similar transaction with
   * the specified messages.
   *
   * @param transaction   to be similar on the transaction of the task
   * @param checkMessages the predicate to check the messages of the transaction.
   *
   * @return this builder.
   */
  public TaskPredicateBuilder withSimilarTransactionWithMessages(@NotNull final TaskTransaction transaction,
      @NotNull final List<Predicate<Message>> checkMessages) {

    return this.with(task -> {

      if (task.transactions != null) {

        for (final var target : task.transactions) {

          if ((transaction.taskId == null || transaction.taskId.equals(target.taskId))
              && (transaction.label == null || transaction.label.equals(target.label))
              && (transaction.actioneerId == null || transaction.actioneerId.equals(target.actioneerId))
              && (transaction.attributes == null || transaction.attributes.equals(target.attributes))
              && target.messages != null) {

            final var copy = new ArrayList<>(checkMessages);
            for (final var msg : target.messages) {

              final var iter = copy.iterator();
              while (iter.hasNext()) {

                final var checkMessage = iter.next();
                if (checkMessage.test(msg)) {

                  iter.remove();
                  break;
                }
              }
            }

            if (copy.isEmpty()) {

              return true;
            }

          }

        }

      }

      return false;

    });
  }

}
