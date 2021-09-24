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
 * Component to create predicates to check an interaction.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface InteractionPredicates {

  /**
   * Check that the interaction has the specified transaction label.
   *
   * @param label to match for the transaction in the interaction.
   *
   * @return the predicate to do this match.
   */
  static Predicate<Interaction> transactionLabelIs(final String label) {

    return interaction -> {

      return interaction.transactionLabel.equals(label);

    };
  }

  /**
   * Check that the interaction has the specified message label.
   *
   * @param label to match for the message in the interaction.
   *
   * @return the predicate to do this match.
   */
  static Predicate<Interaction> messageLabelIs(final String label) {

    return interaction -> {

      return interaction.messageLabel.equals(label);

    };
  }

  /**
   * Check that the interaction has the specified receiver.
   *
   * @param receiver identifier to match for the interaction.
   *
   * @return the predicate to do this match.
   */
  static Predicate<Interaction> receiverIs(final String receiver) {

    return interaction -> {

      return interaction.receiverId.equals(receiver);

    };
  }

  /**
   * Check that the interaction has the specified sender.
   *
   * @param sender identifier to match for the interaction.
   *
   * @return the predicate to do this match.
   */
  static Predicate<Interaction> senderIs(final String sender) {

    return interaction -> {

      return interaction.senderId.equals(sender);

    };
  }

  /**
   * Check that the interaction has the specified app.
   *
   * @param app identifier to match for the interaction.
   *
   * @return the predicate to do this match.
   */
  static Predicate<Interaction> appIs(final String app) {

    return interaction -> {

      return interaction.appId.equals(app);

    };
  }

  /**
   * Check that the interaction has the specified transaction transaction
   * attributes.
   *
   * @param attributes to match for the transaction interaction.
   *
   * @return the predicate to do this match.
   */
  static Predicate<Interaction> transactionAttributesAre(final JsonObject attributes) {

    return interaction -> {

      return interaction.transactionAttributes != null && interaction.transactionAttributes.equals(attributes);

    };
  }

  /**
   * Return a predicate the check that the transaction interaction attributes
   * satisfy the predicate.
   *
   * @param checkAttributes predicate to check the attributes.
   *
   * @return the predicate to check the transaction interaction attributes.
   */
  static Predicate<Interaction> transactionAttributesAre(final Predicate<JsonObject> checkAttributes) {

    return interaction -> {

      return interaction.transactionAttributes != null && checkAttributes.test(interaction.transactionAttributes);

    };

  }

  /**
   * Return a predicate the check that the transaction interaction attributes are
   * similar to a source.
   *
   * @param source attributes to be similar.
   *
   * @return the predicate to check the transaction interaction attributes.
   */
  static Predicate<Interaction> transactionAttributesSimilarTo(final JsonObject source) {

    return interaction -> {

      if (interaction.transactionAttributes == null) {

        return false;
      }
      for (final var key : source.fieldNames()) {

        final var value = source.getValue(key);
        final var interactionValue = interaction.transactionAttributes.getValue(key);
        if (value != interactionValue && (value == null || !value.equals(interactionValue))) {

          return false;
        }
      }

      return true;

    };

  }

  /**
   * Check that the interaction has the specified message message attributes.
   *
   * @param attributes to match for the message interaction.
   *
   * @return the predicate to do this match.
   */
  static Predicate<Interaction> messageAttributesAre(final JsonObject attributes) {

    return interaction -> {

      return interaction.messageAttributes != null && interaction.messageAttributes.equals(attributes);

    };
  }

  /**
   * Return a predicate the check that the message interaction attributes satisfy
   * the predicate.
   *
   * @param checkAttributes predicate to check the attributes.
   *
   * @return the predicate to check the message interaction attributes.
   */
  static Predicate<Interaction> messageAttributesAre(final Predicate<JsonObject> checkAttributes) {

    return interaction -> {

      return interaction.messageAttributes != null && checkAttributes.test(interaction.messageAttributes);

    };

  }

  /**
   * Return a predicate the check that the message interaction attributes are
   * similar to a source.
   *
   * @param source attributes to be similar.
   *
   * @return the predicate to check the message interaction attributes.
   */
  static Predicate<Interaction> messageAttributesSimilarTo(final JsonObject source) {

    return interaction -> {

      if (interaction.messageAttributes == null) {

        return false;
      }
      for (final var key : source.fieldNames()) {

        final var value = source.getValue(key);
        final var interactionValue = interaction.messageAttributes.getValue(key);
        if (value != interactionValue && (value == null || !value.equals(interactionValue))) {

          return false;
        }
      }

      return true;

    };

  }

}