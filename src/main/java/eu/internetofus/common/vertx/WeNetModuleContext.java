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

package eu.internetofus.common.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Contains the information of a WeNet module that has been started.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetModuleContext {

  /**
   * The component that manage the started vertices.
   */
  public Vertx vertx;

  /**
   * The configuration that has been used to start the WeNet module.
   */
  public JsonObject configuration;

  /**
   * Create a new context.
   *
   * @param vertx         that manage the vertices.
   * @param configuration the effective configuration used to start the WeNet module.
   */
  public WeNetModuleContext(final Vertx vertx, final JsonObject configuration) {

    this.vertx = vertx;
    this.configuration = configuration;

  }

}
