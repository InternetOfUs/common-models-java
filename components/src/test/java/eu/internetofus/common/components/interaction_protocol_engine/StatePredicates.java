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