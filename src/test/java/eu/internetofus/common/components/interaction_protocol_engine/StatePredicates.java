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

import io.vertx.core.json.JsonObject;
import java.util.function.Predicate;

/**
 * Component to create predicates to check a state.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface StatePredicates {

  /**
   * Check that the state has the specified community.
   *
   * @param community identifier to match for the state.
   *
   * @return the predicate to do this match.
   */
  static Predicate<State> communityIs(final String community) {

    return state -> {

      return state.communityId.equals(community);

    };
  }

  /**
   * Check that the state has the specified task.
   *
   * @param task identifier to match for the state.
   *
   * @return the predicate to do this match.
   */
  static Predicate<State> taskIs(final String task) {

    return state -> {

      return state.taskId.equals(task);

    };
  }

  /**
   * Check that the state has the specified user.
   *
   * @param user identifier to match for the state.
   *
   * @return the predicate to do this match.
   */
  static Predicate<State> userIs(final String user) {

    return state -> {

      return state.userId.equals(user);

    };
  }

  /**
   * Check that the state has the specified attributes.
   *
   * @param attributes to match for the state.
   *
   * @return the predicate to do this match.
   */
  static Predicate<State> attributesAre(final JsonObject attributes) {

    return state -> {

      return state.attributes.equals(attributes);

    };
  }

  /**
   * Return a predicate the check that the state attributes satisfy the predicate.
   *
   * @param checkAttributes predicate to check the attributes.
   *
   * @return the predicate to check the state attributes.
   */
  static Predicate<State> attributesAre(final Predicate<JsonObject> checkAttributes) {

    return target -> {

      return target.attributes != null && checkAttributes.test(target.attributes);

    };

  }

  /**
   * Return a predicate the check that the state attributes are similar to a
   * source.
   *
   * @param source attributes to be similar.
   *
   * @return the predicate to check the state attributes.
   */
  static Predicate<State> attributesSimilarTo(final JsonObject source) {

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

}