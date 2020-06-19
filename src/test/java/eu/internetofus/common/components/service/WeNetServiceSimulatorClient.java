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

package eu.internetofus.common.components.service;

import javax.validation.constraints.NotNull;

import eu.internetofus.common.vertx.ComponentClient;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

/**
 * Service used to interact with the {@link WeNetServiceSimulator}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetServiceSimulatorClient extends WeNetServiceClient implements WeNetServiceSimulator, WeNetService {

  /**
   * Create a new service to interact with the WeNet interaction protocol engine.
   *
   * @param client to interact with the other modules.
   * @param conf   configuration.
   */
  public WeNetServiceSimulatorClient(final WebClient client, final JsonObject conf) {

    super(client, conf);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveApp(@NotNull final String id, @NotNull final Handler<AsyncResult<App>> retrieveHandler) {

    this.retrieveJsonApp(id, ComponentClient.handlerForModel(App.class, retrieveHandler));

  }

  /**
   * Call the {@link WeNetServiceSimulator} to create an application.
   *
   * {@inheritDoc}
   */
  @Override
  public void createApp(final JsonObject app, final Handler<AsyncResult<JsonObject>> createHandler) {

    this.post(app, createHandler, "/app");

  }

  /**
   * Call the {@link WeNetServiceSimulator} to delete an application.
   *
   * {@inheritDoc}
   */
  @Override
  public void deleteApp(final String id, final Handler<AsyncResult<Void>> deleteHandler) {

    this.delete(deleteHandler, "/app", id);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveJsonCallbacks(final String id, final Handler<AsyncResult<JsonArray>> retrieveHandler) {

    this.getJsonArray(retrieveHandler, "/callback", id);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addJsonCallBack(final String appId, final JsonObject message, final Handler<AsyncResult<JsonObject>> handler) {

    this.post(message, handler, "/callback", appId);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteCallbacks(final String appId, final Handler<AsyncResult<Void>> handler) {

    this.delete(handler, "/callback", appId);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveJsonArrayAppUserIds(final String id, final Handler<AsyncResult<JsonArray>> retrieveHandler) {

    this.getJsonArray(retrieveHandler, "/app/" + id + "/users");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addUsers(final String appId, final JsonArray users, final Handler<AsyncResult<JsonArray>> handler) {

    this.post(users, handler, "/app/" + appId + "/users");

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteUsers(final String appId, final Handler<AsyncResult<Void>> handler) {

    this.delete(handler, "/app/" + appId + "/users");

  }

}
