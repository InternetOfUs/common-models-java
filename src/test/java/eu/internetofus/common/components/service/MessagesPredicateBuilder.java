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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Create a list of predicated to match some messages.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class MessagesPredicateBuilder {

  /**
   * The builder predicates.
   */
  protected List<Predicate<Message>> predicates = new ArrayList<>();

  /**
   * The predicated to use.
   *
   * @return the build predicates.
   */
  public List<Predicate<Message>> build() {

    return this.predicates;
  }

  /**
   * Add a predicate to use.
   *
   * @param predicate to use.
   *
   * @return this builder.
   */
  public MessagesPredicateBuilder with(final Predicate<Message> predicate) {

    this.predicates.add(predicate);
    return this;

  }

  /**
   * Add a predicate to match the specified message.
   *
   * @param msg to match.
   *
   * @return this builder.
   */
  public MessagesPredicateBuilder with(final Message msg) {

    return this.with(target -> target.equals(msg));

  }

  /**
   * The predicate to match only the label and the receiver.
   *
   * @param label      to match for the message.
   * @param receiverId to match for the message.
   *
   * @return the predicate to do this match.
   */
  public static Predicate<Message> createPredicateForLabelAndReceiver(final String label, final String receiverId) {

    return msg -> {

      return msg.label.equals(label) && msg.receiverId.equals(receiverId);

    };
  }

  /**
   * Add a predicate to match the messages with the specified label and receiver.
   *
   * @param label      to match for the message.
   * @param receiverId to match for the message.
   *
   * @return this builder.
   */
  public MessagesPredicateBuilder withLabelAndReceiverId(final String label, final String receiverId) {

    return this.with(createPredicateForLabelAndReceiver(label, receiverId));

  }

  /**
   * The predicate to match only the label, receiver and attributes.
   *
   * @param label      to match for the message.
   * @param receiverId to match for the message.
   * @param attributes to match for the message.
   *
   * @return the predicate to do this match.
   */
  public static Predicate<Message> createPredicateForLabelReceiverAndAttributes(final String label,
      final String receiverId, final JsonObject attributes) {

    return msg -> {

      return msg.label.equals(label) && msg.receiverId.equals(receiverId) && msg.attributes != null
          && msg.attributes.equals(attributes);

    };
  }

  /**
   * Add a predicate to match the messages with the specified label, receiver and
   * attributes.
   *
   * @param label      to match for the message.
   * @param receiverId to match for the message.
   * @param attributes to match for the message.
   *
   * @return this builder.
   */
  public MessagesPredicateBuilder withLabelReceiverIdAndAttributes(final String label, final String receiverId,
      final JsonObject attributes) {

    return this.with(createPredicateForLabelReceiverAndAttributes(label, receiverId, attributes));

  }

}