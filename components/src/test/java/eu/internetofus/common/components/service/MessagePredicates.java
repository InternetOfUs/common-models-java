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

package eu.internetofus.common.components.service;

import eu.internetofus.common.components.models.Message;
import io.vertx.core.json.JsonObject;
import java.util.function.Predicate;

/**
 * Component to create predicates to check a message.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface MessagePredicates {

  /**
   * Check that the message has the specified label.
   *
   * @param label to match for the message.
   *
   * @return the predicate to do this match.
   */
  static Predicate<Message> labelIs(final String label) {

    return msg -> {

      return msg.label.equals(label);

    };
  }

  /**
   * Check that the message has the specified receiver.
   *
   * @param receiver identifier to match for the message.
   *
   * @return the predicate to do this match.
   */
  static Predicate<Message> receiverIs(final String receiver) {

    return msg -> {

      return msg.receiverId.equals(receiver);

    };
  }

  /**
   * Check that the message has the specified app.
   *
   * @param app identifier to match for the message.
   *
   * @return the predicate to do this match.
   */
  static Predicate<Message> appIs(final String app) {

    return msg -> {

      return msg.appId.equals(app);

    };
  }

  /**
   * Check that the message has the specified attributes.
   *
   * @param attributes to match for the message.
   *
   * @return the predicate to do this match.
   */
  static Predicate<Message> attributesAre(final JsonObject attributes) {

    return msg -> {

      return msg.attributes.equals(attributes);

    };
  }

  /**
   * Return a predicate the check that the message attributes satisfy the
   * predicate.
   *
   * @param checkAttributes predicate to check the attributes.
   *
   * @return the predicate to check the message attributes.
   */
  static Predicate<Message> attributesAre(final Predicate<JsonObject> checkAttributes) {

    return target -> {

      return target.attributes != null && checkAttributes.test(target.attributes);

    };

  }

  /**
   * Return a predicate the check that the message attributes are similar to a
   * source.
   *
   * @param source attributes to be similar.
   *
   * @return the predicate to check the message attributes.
   */
  static Predicate<Message> attributesSimilarTo(final JsonObject source) {

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