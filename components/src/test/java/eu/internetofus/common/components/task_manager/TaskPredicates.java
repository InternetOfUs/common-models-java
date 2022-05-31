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

import eu.internetofus.common.components.models.Task;
import eu.internetofus.common.components.models.TaskTransaction;
import io.vertx.core.json.JsonObject;
import java.util.function.Predicate;

/**
 * Generic predicates that can be done over a task.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface TaskPredicates {

  /**
   * Check if the passed task is similar to the source task.
   *
   * @param source task to be similar.
   *
   * @return the predicate to check if a task is similar to another one.
   */
  static Predicate<Task> similarTo(final Task source) {

    return target -> {

      if (source.taskTypeId != null && !source.taskTypeId.equals(target.taskTypeId)) {

        return false;
      }

      if (source.appId != null && !source.appId.equals(target.appId)) {

        return false;
      }

      if (source.communityId != null && !source.communityId.equals(target.communityId)) {

        return false;
      }

      if (source.requesterId != null && !source.requesterId.equals(target.requesterId)) {

        return false;
      }

      return true;

    };

  }

  /**
   * Return a predicate the check that the transaction of the specified position
   * satisfy the predicate.
   *
   * @param index            of the transaction.
   * @param checkTransaction predicate to satisfy by the transaction at the
   *                         specified position.
   *
   * @return the predicate to check the transaction at the specified position.
   */
  static Predicate<Task> transactionAt(final int index, final Predicate<TaskTransaction> checkTransaction) {

    return target -> {

      return target.transactions != null && target.transactions.size() > index
          && checkTransaction.test(target.transactions.get(index));

    };
  }

  /**
   * Return a predicate the check that the last transaction satisfy the predicate.
   *
   * @param checkTransaction predicate to satisfy by the last transaction.
   *
   * @return the predicate to check the last transaction.
   */
  static Predicate<Task> lastTransactionIs(final Predicate<TaskTransaction> checkTransaction) {

    return target -> {

      return target.transactions != null && target.transactions.size() > 0
          && checkTransaction.test(target.transactions.get(target.transactions.size() - 1));

    };
  }

  /**
   * Return a predicate the check that the task has the specified number of
   * transactions.
   *
   * @param size number of transactions.
   *
   * @return the predicate to check the transaction size.
   */
  static Predicate<Task> transactionSizeIs(final int size) {

    return target -> {

      return target.transactions != null && target.transactions.size() == size;

    };

  }

  /**
   * Return a predicate the check that the task contains the specified
   * transaction.
   *
   * @param checkTransaction predicate to satisfy by at least one transaction.
   *
   * @return the predicate to check exist at least one transaction satisfy the
   *         predicate.
   */
  static Predicate<Task> containsTransaction(final Predicate<TaskTransaction> checkTransaction) {

    return target -> {

      if (target.transactions != null && !target.transactions.isEmpty()) {

        for (final var transaction : target.transactions) {

          if (checkTransaction.test(transaction)) {

            return true;
          }

        }

      }

      return false;

    };
  }

  /**
   * Return a predicate the check that the task attributes are equals to a source.
   *
   * @param source expected attributes.
   *
   * @return the predicate to check the task attributes.
   */
  static Predicate<Task> attributesAre(final JsonObject source) {

    return attributesAre(target -> target.equals(source));

  }

  /**
   * Return a predicate the check that the task attributes satisfy the predicate.
   *
   * @param checkAttributes predicate to check the attributes.
   *
   * @return the predicate to check the task attributes.
   */
  static Predicate<Task> attributesAre(final Predicate<JsonObject> checkAttributes) {

    return target -> {

      return target.attributes != null && checkAttributes.test(target.attributes);

    };

  }

  /**
   * Return a predicate the check that the task attributes are similar to a
   * source.
   *
   * @param source attributes to be similar.
   *
   * @return the predicate to check the task attributes.
   */
  static Predicate<Task> attributesSimilarTo(final JsonObject source) {

    return target -> {

      if (target.attributes == null) {

        return false;
      }
      for (final var key : source.fieldNames()) {

        final var value = source.getValue(key);
        final var targetValue = target.attributes.getValue(key);
        if (value != targetValue && (value == null || !value.equals(targetValue))) {

          return false;
        }
      }

      return true;

    };

  }

  /**
   * Return a predicate the check that the task is closed.
   *
   * @return the predicate to check the task is closed.
   */
  static Predicate<Task> isClosed() {

    return target -> {

      return target.closeTs != null;

    };

  }

}
