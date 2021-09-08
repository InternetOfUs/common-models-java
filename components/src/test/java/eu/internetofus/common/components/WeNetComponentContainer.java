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

package eu.internetofus.common.components;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.InternetProtocol;

/**
 * A container for a WeNet component.
 *
 * @param <SELF> type of container.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetComponentContainer<SELF extends WeNetComponentContainer<SELF>> extends GenericContainer<SELF> {

  /**
   * Create a new container.
   *
   * @param name of the container image.
   * @param port to export the API.
   */
  public WeNetComponentContainer(final String name, final int port) {

    this(name, port, Containers.EXPORT_API_PORT);

  }

  /**
   * Create a new container.
   *
   * @param name          of the container image.
   * @param port          to export the API.
   * @param containerPort port of the container to export.
   */
  public WeNetComponentContainer(final String name, final int port, final int containerPort) {

    super(name);
    this.addFixedExposedPort(port, containerPort, InternetProtocol.TCP);
    this.withStartupAttempts(1);
  }

  /**
   * Configure the container of the component.
   *
   * @param containers where the container will be used.
   *
   * @return the instance of this container.
   */
  public SELF with(final Containers containers) {

    return this.withEnv("DB_HOST", containers.getMongoDBHost())
        .withEnv("DB_PORT", String.valueOf(containers.getMongoDBPort())).withEnv("DB_NAME", Containers.MONGODB_NAME)
        .withEnv("DB_USER_NAME", Containers.MONGODB_USER).withEnv("DB_USER_PASSWORD", Containers.MONGODB_PASSWORD)
        .withNetwork(containers);

  }

  /**
   * Configure the container of the component.
   *
   * @param containers where the container will be used.
   *
   * @return the instance of this container.
   */
  public SELF withNetwork(final Containers containers) {

    return this.withNetwork(containers.network);

  }

}
