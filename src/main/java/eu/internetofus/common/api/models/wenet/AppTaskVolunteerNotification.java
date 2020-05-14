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
 * A notification to inform about a task volunteer.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(
		hidden = true,
		name = "TaskVolunteerNotification",
		description = "This notification is used in order to notify the task creator that a new volunteer is proposing to participate to the task.")
public class AppTaskVolunteerNotification extends AppTaskNotification {

	/**
	 * The identifier of the volunteer.
	 */
	@Schema(description = "The wenet id of the user who is volunteering for the task.", example = "289615821")
	public String volunteerId;

	/**
	 * Create a new task volunteer notification.
	 */
	public AppTaskVolunteerNotification() {

		this.notificationType = NotificationType.taskVolunteer;
	}
}
