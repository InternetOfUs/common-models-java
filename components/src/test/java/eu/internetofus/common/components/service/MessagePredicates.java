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
import org.tinylog.Logger;

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

      final var result = msg.label.equals(label);
      if (!result) {

        Logger.trace("Message label is not equals: {} != {}", label, msg.label);
      }
      return result;

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

      final var result = msg.receiverId.equals(receiver);
      if (!result) {

        Logger.trace("Message receiver is not equals: {} != {}", receiver, msg.receiverId);
      }
      return result;

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

      final var result = msg.appId.equals(app);
      if (!result) {

        Logger.trace("Message app is not equals: {} != {}", app, msg.appId);
      }
      return result;

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

      final var result = msg.attributes.equals(attributes);
      if (!result) {

        Logger.trace("Message attribute are NOT equal: {} != {}", attributes, msg.attributes);
      }
      return result;

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

      final var result = target.attributes != null && checkAttributes.test(target.attributes);
      if (!result) {

        Logger.trace("Unexpected message attribute: {}", target.attributes);
      }
      return result;

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

        Logger.trace("Message attributes are null, so can not check if are similar to {}", source);
        return false;
      }
      for (final var key : source.fieldNames()) {

        final var value = source.getValue(key);
        final var targetValue = target.attributes.getValue(key);
        if (value != targetValue && (value == null || !value.equals(targetValue))) {

          Logger.trace("Message attributes are not similar: {} != {}", value, targetValue);
          return false;
        }
      }

      return true;

    };

  }

}