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
package eu.internetofus.common.vertx.basic;

import eu.internetofus.common.vertx.ComponentClient;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

/**
 * The client to interact with the {@link Users}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class BasicComponentClient extends ComponentClient implements BasicComponent {

  /**
   * The default URL to bind the client.
   */
  public static final String DEFAULT_BASIC_API_URL = "http://localhost:8080";

  /**
   * The name of the configuration property that contains the URL to the task
   * manager API.
   */
  public static final String BASIC_CONF_KEY = "basic";

  /**
   * Create the client.
   *
   * @param client to HTTP request.
   * @param conf   configuration.
   */
  public BasicComponentClient(final WebClient client, final JsonObject conf) {

    super(client, conf.getString(BASIC_CONF_KEY, DEFAULT_BASIC_API_URL));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createUser(final JsonObject user, final Handler<AsyncResult<JsonObject>> handler) {

    this.post(user, "/users").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveUser(final String id, final Handler<AsyncResult<JsonObject>> handler) {

    this.getJsonObject("/users", id).onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteUser(final String id, final Handler<AsyncResult<Void>> handler) {

    this.delete("/users", id).onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateUser(final String id, final JsonObject user, final Handler<AsyncResult<JsonObject>> handler) {

    this.put(user, "/users", id).onComplete(handler);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeUser(final String id, final JsonObject user, final Handler<AsyncResult<JsonObject>> handler) {

    this.patch(user, "/users", id).onComplete(handler);

  }

}
