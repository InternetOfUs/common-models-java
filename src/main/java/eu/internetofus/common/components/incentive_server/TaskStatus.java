/*
 * -----------------------------------------------------------------------------
 *
 *   Copyright 2019 - 2022 UDT-IA, IIIA-CSIC
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

package eu.internetofus.common.components.incentive_server;

import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ReflectionModel;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents the status of a task.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "TaskStatus", description = "The status of a task.")
public class TaskStatus extends ReflectionModel implements Model {

  /**
   * Identifier of the app.
   */
  @Schema(example = "WeNet_user5")
  public String app_id = "-1";

  /**
   * Identifier of the user.
   */
  @Schema(example = "WeNet_user5")
  public String user_id = "-1";

  /**
   * Identifier of the community.
   */
  @Schema(example = "WeNet_community_5")
  public String community_id;

  /**
   * Identifier of the task.
   */
  @Schema(example = "WeNet_task")
  public String task_id;

  /**
   * The action that update the task.
   */
  @Schema(example = "Starts")
  public String Action;

  /**
   * The message of the status.
   */
  @Schema(example = "some message")
  public String Message;

}
