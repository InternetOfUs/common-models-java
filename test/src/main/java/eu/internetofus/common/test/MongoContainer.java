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

package eu.internetofus.common.test;

import io.vertx.core.json.JsonObject;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;
import org.tinylog.Logger;

/**
 * Class to manage a MongoDB container.
 *
 * @param <SELF> type of container.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class MongoContainer<SELF extends MongoContainer<SELF>> {

  /**
   * The name of the mongo docker container to use.
   */
  public static final String MONGO_DOCKER_NAME = "mongo:5.0.6";

  /**
   * The port for the MongoDB that has to be exported.
   */
  public static final int EXPORT_MONGODB_PORT = 27017;

  /**
   * The name of the database started on the container.
   */
  public static final String MONGODB_NAME = "wenetDB";

  /**
   * The name of the user to access the MongoDB on the container.
   */
  public static final String MONGODB_USER = "wenet";

  /**
   * The password of the user to access the MongoDB on the container.
   */
  public static final String MONGODB_PASSWORD = "password";

  /**
   * The network in witch all the containers are linked.
   */
  public Network network;

  /**
   * The container with a mongoDB.
   */
  public GenericContainer<?> mongoContainer;

  /**
   * Create the container.
   */
  public MongoContainer() {

    this.network = Network.newNetwork();

  }

  /**
   * Return the host where the MongoDB is listening.
   *
   * @return the host where is the MongoDB container.
   */
  public String getMongoDBHost() {

    return this.mongoContainer.getHost();
  }

  /**
   * Return the port where the MongoDB is listening.
   *
   * @return the port where is the MongoDB container.
   */
  public int getMongoDBPort() {

    return this.mongoContainer.getMappedPort(MongoContainer.EXPORT_MONGODB_PORT);

  }

  /**
   * Return the configuration to interact with the database.
   *
   * @return the configuration to interact with the MongoDB.
   */
  public JsonObject getMongoDBConfig() {

    return new JsonObject().put("db_name", MongoContainer.MONGODB_NAME).put("host", this.getMongoDBHost())
        .put("port", this.getMongoDBPort()).put("username", MongoContainer.MONGODB_USER)
        .put("password", MongoContainer.MONGODB_PASSWORD);
  }

  /**
   * Start the MongoDB container if it is not started yet.
   *
   * @return this containers instance.
   */
  @SuppressWarnings({ "resource", "unchecked" })
  public SELF startMongoContainer() {

    if (this.mongoContainer == null) {

      Logger.trace("Starting MongoDB container");
      this.mongoContainer = new GenericContainer<>(DockerImageName.parse(MONGO_DOCKER_NAME)).withStartupAttempts(1)
          .withEnv("MONGO_INITDB_ROOT_USERNAME", "root").withEnv("MONGO_INITDB_ROOT_PASSWORD", "password")
          .withEnv("MONGO_INITDB_DATABASE", MONGODB_NAME)
          .withCopyFileToContainer(
              MountableFile.forClasspathResource(
                  MongoContainer.class.getPackageName().replaceAll("\\.", "/") + "/initialize-wenetDB.js"),
              "/docker-entrypoint-initdb.d/init-mongo.js")
          .withExposedPorts(EXPORT_MONGODB_PORT).withNetwork(this.network).withNetworkAliases(MONGODB_NAME)
          .waitingFor(Wait.forListeningPort());
      this.mongoContainer.start();
      Logger.trace("Started MongoDB container");
    }

    return (SELF) this;
  }

}
