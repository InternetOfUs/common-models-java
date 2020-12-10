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

package eu.internetofus.common;

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

    super(name);
    this.addFixedExposedPort(port, Containers.EXPORT_API_PORT, InternetProtocol.TCP);
  }

  /**
   * Configure the container of the component.
   *
   * @param containers where the container will be used.
   *
   * @return the instance of this container.
   */
  public SELF with(final Containers containers) {

    return this.withStartupAttempts(1).withEnv("DB_HOST", containers.getMongoDBHost()).withEnv("DB_PORT", String.valueOf(containers.getMongoDBPort())).withEnv("DB_NAME", Containers.MONGODB_NAME)
        .withEnv("DB_USER_NAME", Containers.MONGODB_USER).withEnv("DB_USER_PASSWORD", Containers.MONGODB_PASSWORD).withNetwork(containers.network);

  }

}
