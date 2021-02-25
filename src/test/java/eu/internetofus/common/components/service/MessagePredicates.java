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

package eu.internetofus.common.components.service;

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