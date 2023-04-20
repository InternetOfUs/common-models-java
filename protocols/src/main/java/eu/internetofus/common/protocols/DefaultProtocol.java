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
package eu.internetofus.common.protocols;

import eu.internetofus.common.components.models.TaskType;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ValidationErrorException;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * The methods implemented by any default protocol.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface DefaultProtocol {

  /**
   * Return the default task type identifier associated to the protocol.
   *
   * @return the default task type identifier for the protocol.
   */
  String taskTypeId();

  /**
   * Load the default protocol definition.
   *
   * @param vertx the event bus to use.
   *
   * @return the future with the task type associated to the protocol.
   */
  default Future<TaskType> load(final Vertx vertx) {

    return vertx.executeBlocking(promise -> {

      try {

        final var resource = "eu/internetofus/common/protocols/DefaultProtocol_"
            + this.taskTypeId().replaceAll("\\W", "_") + ".json";
        final var input = this.getClass().getClassLoader().getResourceAsStream(resource);
        final var content = new BufferedReader(new InputStreamReader(input)).lines().collect(Collectors.joining("\n"));
        final var taskType = Model.fromString(content, TaskType.class);
        promise.complete(taskType);

      } catch (final Throwable cause) {

        promise.fail(new ValidationErrorException(this.taskTypeId(), "Cannot load the protocol.", cause));
      }

    });
  }

}
