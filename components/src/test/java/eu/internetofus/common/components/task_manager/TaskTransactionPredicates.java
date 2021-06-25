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

package eu.internetofus.common.components.task_manager;

import eu.internetofus.common.components.models.Message;
import eu.internetofus.common.components.models.TaskTransaction;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Generic predicates that can be done over a task transaction.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface TaskTransactionPredicates {

  /**
   * Check if the passed task transaction is similar to the source one.
   *
   * @param source task transaction to be similar.
   *
   * @return the predicate to check if a task transaction is similar to another
   *         one.
   */
  static Predicate<TaskTransaction> similarTo(final TaskTransaction source) {

    return target -> {

      if (source.taskId != null && !source.taskId.equals(target.taskId)) {

        return false;
      }

      if (source.label != null && !source.label.equals(target.label)) {

        return false;
      }

      if (source.actioneerId != null && !source.actioneerId.equals(target.actioneerId)) {

        return false;
      }

      if (source.attributes != null && !source.attributes.equals(target.attributes)) {

        return false;
      }

      return true;

    };

  }

  /**
   * Return a predicate the check that the transaction does not have messages.
   *
   * @return the predicate to check the transaction does not have messages.
   */
  static Predicate<TaskTransaction> withoutMessages() {

    return target -> {

      return target.messages == null || target.messages.isEmpty();

    };
  }

  /**
   * Return a predicate the check that the task transaction has the specified
   * number of messages.
   *
   * @param size number of messages.
   *
   * @return the predicate to check the messages size.
   */
  static Predicate<TaskTransaction> messagesSizeIs(final int size) {

    return target -> {

      return target.messages != null && target.messages.size() == size;

    };

  }

  /**
   * Return a predicate the check that the message of the specified position
   * satisfy the predicate.
   *
   * @param index        of the message.
   * @param checkMessage predicate to satisfy by the message at the specified
   *                     position.
   *
   * @return the predicate to check the message at the specified position.
   */
  static Predicate<TaskTransaction> messageAt(final int index, final Predicate<Message> checkMessage) {

    return target -> {

      return target.messages != null && target.messages.size() >= index
          && checkMessage.test(target.messages.get(index));

    };
  }

  /**
   * Return a predicate the check that the transaction contains the specified
   * message.
   *
   * @param checkMessage predicate to satisfy by at least one message.
   *
   * @return the predicate to check exist at least one message that satisfy the
   *         predicate.
   */
  static Predicate<TaskTransaction> containsMessage(final Predicate<Message> checkMessage) {

    return target -> {

      if (target.messages != null && !target.messages.isEmpty()) {

        for (final var msg : target.messages) {

          if (checkMessage.test(msg)) {

            return true;
          }

        }

      }

      return false;

    };
  }

  /**
   * Return a predicate the check that the transaction contains the specified
   * messages.
   *
   * @param checkMessages predicated to match at least one message.
   *
   * @return the predicate to check exist at least one message that satisfy the
   *         predicate.
   */
  static Predicate<TaskTransaction> containsMessages(final List<Predicate<Message>> checkMessages) {

    return target -> {

      if (target.messages != null && target.messages.size() >= checkMessages.size()) {

        final var copy = new ArrayList<>(checkMessages);
        for (final var msg : target.messages) {

          final var iter = copy.iterator();
          while (iter.hasNext()) {

            final var predicate = iter.next();
            if (predicate.test(msg)) {

              iter.remove();
              break;

            }
          }

        }

        return copy.isEmpty();

      } else {

        return false;

      }

    };
  }

}
