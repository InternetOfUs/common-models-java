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

package eu.internetofus.common.components.incentive_server;

import java.util.function.Predicate;

/**
 * Component to create predicates to check a task type status.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface TaskTypeStatusBodyPredicates {

  /**
   * Check that the task status has the specified count.
   *
   * @param count to match for the task status.
   *
   * @return the predicate to do this match.
   */
  static Predicate<TaskTypeStatusBody> countIs(final int count) {

    return msg -> {

      return msg.count == count;

    };
  }

  /**
   * Check that the task status has the specified user.
   *
   * @param user identifier to match for the task status.
   *
   * @return the predicate to do this match.
   */
  static Predicate<TaskTypeStatusBody> userIs(final String user) {

    return msg -> {

      return msg.user_id.equals(user);

    };
  }

}