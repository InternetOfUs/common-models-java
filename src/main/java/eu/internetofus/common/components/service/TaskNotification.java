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

package eu.internetofus.common.components.service;

import eu.internetofus.common.components.task_manager.Task;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A notification about a {@link Task} to send to an {@link App}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "taskNotification", description = "A notification to inform about the task status.")
public class TaskNotification extends Message {

  /**
   * The identifier of the task notification.
   */
  @Schema(description = "The identifier of the target task.", example = "28961582-84d2-41d1-b555-c09dce046831")
  public String taskId;

  /**
   * The possible notification types.
   */
  public enum NotificationType {
    /**
     * The notification that exist a new task that you can help to do.
     */
    taskProposal,
    /**
     * The notification to inform of a volunteer.
     */
    taskVolunteer,
    /**
     * The notification to inform that exist a message form an user.
     */
    messageFromUser,
    /**
     * The notification to inform that the task has concluded.
     */
    taskConcluded,
    /**
     * This notification is used in order to notify the user who volunteered about the decision of the task creator.
     */
    selectionVolunteer;

  }

  /**
   * The identifier of the task notification.
   */
  @Schema(description = "The type of the notification.", example = "taskProposal")
  public NotificationType notificationType;

  /**
   * Create a new textual message.
   */
  protected TaskNotification() {

    this.type = Type.taskNotification;
  }

}
