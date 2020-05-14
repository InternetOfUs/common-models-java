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
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
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

package eu.internetofus.common.api.models.wenet;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A notification to exchange messages from users on the same task.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(
		hidden = true,
		name = "MessageFromUserNotification",
		description = "This notification is used in order to send a message exchanged between two participants to the same task.")
public class AppMessageFromUserNotification extends AppTaskNotification {

	/**
	 * The identifier of the volunteer.
	 */
	@Schema(
			description = "The wenet id of the user who wrote the message for the recipient user.",
			example = "ef3990b4-d75e-4339-b78a-125c23ab4614")
	public String senderId;

	/**
	 * Create a new task volunteer notification.
	 */
	public AppMessageFromUserNotification() {

		this.notificationType = NotificationType.messageFromUser;
	}
}
