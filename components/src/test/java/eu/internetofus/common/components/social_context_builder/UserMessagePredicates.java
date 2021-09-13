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

package eu.internetofus.common.components.social_context_builder;

import eu.internetofus.common.components.models.Message;
import java.util.function.Predicate;

/**
 * Component to create predicates to check a user message.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface UserMessagePredicates {

  /**
   * Check that the identifier of the sender.
   *
   * @param senderId the identifier of the sender.
   *
   * @return the predicate to do this match.
   */
  static Predicate<UserMessage> senderId(final String senderId) {

    return msg -> {

      return msg.senderId == senderId || msg.senderId != null && msg.senderId.equals(senderId);

    };
  }

  /**
   * Check that the identifier of the transaction.
   *
   * @param transactionId the identifier of the transaction.
   *
   * @return the predicate to do this match.
   */
  static Predicate<UserMessage> transactionId(final String transactionId) {

    return msg -> {

      return msg.transactionId == transactionId || msg.transactionId != null && msg.transactionId.equals(transactionId);

    };
  }

  /**
   * Check that the message.
   *
   * @param checkMessage predicate to check the user message.
   *
   * @return the predicate to do this match.
   */
  static Predicate<UserMessage> message(final Predicate<Message> checkMessage) {

    return msg -> {

      return checkMessage.test(msg.message);

    };
  }

}