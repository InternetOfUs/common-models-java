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

package eu.internetofus.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

/**
 * A model that has information when it is created and updated.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, description = "Generic model for the models that can be created and updated.")
public class CreateUpdateTsDetails extends ReflectionModel implements Model {

  /**
   * The instant of the creation.
   */
  @Schema(description = "The time stamp representing the account creation instant.", example = "1563871899", accessMode = AccessMode.READ_ONLY)
  public long _creationTs;

  /**
   * The instant of the last update.
   */
  @Schema(description = "The time stamp representing the last update instant.", example = "1563898764", accessMode = AccessMode.READ_ONLY)
  public long _lastUpdateTs;

  /**
   * Create a new model.
   */
  public CreateUpdateTsDetails() {

    this._creationTs = this._lastUpdateTs = TimeManager.now();

  }
}
