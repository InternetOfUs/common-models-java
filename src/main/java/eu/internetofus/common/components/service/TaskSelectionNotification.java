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

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A notification is used in order to notify the user who volunteered about the decision of the task creator
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "TaskSelectionNotification", description = "This notification is used in order to notify the user who volunteered about the decision of the task creator.")
public class TaskSelectionNotification extends TaskNotification {

  /**
   * Explains how the selection.
   */
  public enum Outcome {
    /**
     * This happens when the task is cancelled by its creator.
     */
    accepted,
    /**
     * This happens when the task completes correctly.
     */
    refused;
  }

  /**
   * The identifier of the selection.
   */
  @Schema(description = "The outcome of the selection.", example = "accepted")
  public Outcome outcome;

  /**
   * Create a new task volunteer notification.
   */
  public TaskSelectionNotification() {

    this.notificationType = NotificationType.selectionVolunteer;
  }
}
