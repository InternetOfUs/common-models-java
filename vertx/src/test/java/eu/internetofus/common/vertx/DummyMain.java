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

package eu.internetofus.common.vertx;

import io.vertx.core.Verticle;

/**
 * Dummy Main class for test.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class DummyMain extends AbstractMain {

  /**
   * The verticle of the main.
   */
  public Verticle verticle;

  /**
   * Create a new main.
   *
   * @param verticle to use.
   */
  public DummyMain(final Verticle verticle) {

    this.verticle = verticle;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Verticle createMainVerticle() {

    return this.verticle;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getModuleName() {

    return "dummy";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getDefaultModuleConfigurationResurcePath() {

    return "eu/internetofus/common/vertx/"+super.getDefaultModuleConfigurationResurcePath();
  }
}
