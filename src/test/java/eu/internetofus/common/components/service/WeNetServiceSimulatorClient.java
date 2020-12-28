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
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
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
   * Create a new service to interact with the WeNet service simulator.
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
  public Future<App> retrieveApp(@NotNull final String id) {

    final Promise<JsonObject> promise = Promise.promise();
    this.retrieveApp(id, promise);
    return Model.fromFutureJsonObject(promise.future(), App.class);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<JsonArray> retrieveAppUserIds(@NotNull final String appId) {

    final Promise<JsonArray> promise = Promise.promise();
    this.retrieveAppUserIds(appId, promise);
    return promise.future();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createApp(@NotNull final JsonObject app, @NotNull final Handler<AsyncResult<JsonObject>> handler) {

    this.post(app, "/app").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteApp(@NotNull final String id, @NotNull final Handler<AsyncResult<Void>> handler) {

    this.delete("/app", id).onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveCallbacks(@NotNull final String appId, @NotNull final Handler<AsyncResult<JsonArray>> handler) {

    this.getJsonArray("/app", appId, "/callbacks").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addCallBack(final String appId, final JsonObject message, @NotNull final Handler<AsyncResult<JsonObject>> handler) {

    this.post(message, "/app", appId, "/callbacks").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteCallbacks(final String appId, @NotNull final Handler<AsyncResult<Void>> handler) {

    this.delete("/app", appId, "/callbacks").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addUsers(final String appId, final JsonArray users, @NotNull final Handler<AsyncResult<JsonArray>> handler) {

    this.post(users, "/app", appId, "/users").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteUsers(final String appId, @NotNull final Handler<AsyncResult<Void>> handler) {

    this.delete("/app/", appId, "/users").onComplete(handler);

  }

}
