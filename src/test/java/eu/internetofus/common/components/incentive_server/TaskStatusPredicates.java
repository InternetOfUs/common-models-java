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

package eu.internetofus.common.components.incentive_server;

import java.util.function.Predicate;

/**
 * Component to create predicates to check a task status.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface TaskStatusPredicates {

  /**
   * Check that the task status has the specified action.
   *
   * @param action to match for the task status.
   *
   * @return the predicate to do this match.
   */
  static Predicate<TaskStatus> actionIs(final String action) {

    return msg -> {

      return msg.Action.equals(action);

    };
  }

  /**
   * Check that the task status has the specified message.
   *
   * @param message to match for the task status.
   *
   * @return the predicate to do this match.
   */
  static Predicate<TaskStatus> messageIs(final String message) {

    return msg -> {

      return msg.Message.equals(message);

    };
  }

  /**
   * Check that the task status has the specified user.
   *
   * @param user identifier to match for the task status.
   *
   * @return the predicate to do this match.
   */
  static Predicate<TaskStatus> userIs(final String user) {

    return msg -> {

      return msg.user_id.equals(user);

    };
  }

}