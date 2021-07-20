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

package eu.internetofus.common.components.incentive_server;

import eu.internetofus.common.model.ModelTestCase;

/**
 * Test the {@link TaskTypeStatusBody}
 *
 * @see TaskTypeStatusBody
 *
 * @author UDT-IA, IIIA-CSIC
 */

public class TaskTypeStatusBodyTest extends ModelTestCase<TaskTypeStatusBody> {

  /**
   * {@inheritDoc}
   */
  @Override
  public TaskTypeStatusBody createModelExample(final int index) {

    final var model = new TaskTypeStatusBody();
    model.app_id = "WeNet_app" + index;
    model.user_id = "WeNet_user" + index;
    model.community_id = "WeNet_community_" + index;
    model.taskTypeId = "WeNet_task_type_" + index;
    model.count = index;
    return model;

  }

}
