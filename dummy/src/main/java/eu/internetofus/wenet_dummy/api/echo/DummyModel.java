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
package eu.internetofus.wenet_dummy.api.echo;

import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * A model to check the echo service.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(name = "Dummy", description = "A dummy model to check the echo.")
public class DummyModel extends ReflectionModel implements Model {

  /**
   * The name of the dummy.
   */
  @Schema(description = "The name of the dummy.", example = "Mc Fly", nullable = true)
  public String name;

  /**
   * The parent of the dummy.
   */
  @Schema(description = "The parent of the dummy.", example = "{\"name\":\"Parent\"}", nullable = true)
  public DummyParentModel parent;

  /**
   * The list of historical transactions that has been done in this task.
   */
  @ArraySchema(schema = @Schema(implementation = DummyModel.class), arraySchema = @Schema(description = "Children of the dummy.", nullable = true))
  public List<DummyModel> children;

}
