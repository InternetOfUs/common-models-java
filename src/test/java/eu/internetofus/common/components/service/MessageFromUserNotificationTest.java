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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Test the {@link MessageFromUserNotification}
 *
 * @see MessageFromUserNotification
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class MessageFromUserNotificationTest extends TaskNotificationTestCase<MessageFromUserNotification> {

  /**
   * {@inheritDoc}
   *
   * @see MessageFromUserNotification#MessageFromUserNotification()
   */
  @Override
  public MessageFromUserNotification createEmptyMessage() {

    return new MessageFromUserNotification();
  }

  /**
   * Verify that the notification type is a message from user.
   */
  @Test
  public void shouldNotificationTypeByMessageFromUser() {

    final var model = this.createEmptyMessage();
    assertThat(model.notificationType).isEqualTo(TaskNotification.NotificationType.messageFromUser);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public MessageFromUserNotification createModelExample(final int index) {

    final var model = super.createModelExample(index);
    model.senderId = "SenderId_" + index;
    return model;
  }

}
