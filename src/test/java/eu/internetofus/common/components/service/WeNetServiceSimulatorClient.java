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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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

import eu.internetofus.common.components.Model;
import io.vertx.core.Future;
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
   * Call the {@link WeNetServiceSimulator} to create an application.
   *
   * {@inheritDoc}
   */
  @Override
  public Future<JsonObject> createApp(final JsonObject app) {

    return this.post(app, "/app");

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<App> retrieveApp(@NotNull final String id) {

    return Model.fromFutureJsonObject(this.retrieveJsonApp(id), App.class);

  }

  /**
   * Call the {@link WeNetServiceSimulator} to delete an application.
   *
   * {@inheritDoc}
   */
  @Override
  public Future<Void> deleteApp(final String id) {

    return this.delete("/app", id);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<JsonArray> retrieveJsonCallbacks(final String id) {

    return this.getJsonArray("/callback", id);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<JsonObject> addJsonCallBack(final String appId, final JsonObject message) {

    return this.post(message, "/callback", appId);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> deleteCallbacks(final String appId) {

    return this.delete("/callback", appId);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<JsonArray> retrieveAppUserIds(final String id) {

    return this.getJsonArray("/app/" + id + "/users");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<JsonArray> addUsers(final String appId, final JsonArray users) {

    return this.post(users, "/app/" + appId + "/users");

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> deleteUsers(final String appId) {

    return this.delete("/app/" + appId + "/users");

  }

}
