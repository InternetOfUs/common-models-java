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

import eu.internetofus.common.vertx.ComponentClient;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

/**
 * The implementation of the {@link WeNetService}.
 *
 * @see WeNetService
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetServiceClient extends ComponentClient implements WeNetService {

  /**
   * The default URL to bind the client.
   */
  public static final String DEFAULT_SERVICE_API_URL = "https://wenet.u-hopper.com/prod/service";

  /**
   * The name of the configuration property that contains the URL to the service API.
   */
  public static final String SERVICE_CONF_KEY = "service";

  /**
   * Create a new service to interact with the WeNet service.
   *
   * @param client to interact with the other modules.
   * @param conf   configuration.
   */
  public WeNetServiceClient(final WebClient client, final JsonObject conf) {

    super(client, conf.getString(SERVICE_CONF_KEY, DEFAULT_SERVICE_API_URL));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveJsonApp(final String id, final Handler<AsyncResult<JsonObject>> retrieveHandler) {

    this.getJsonObject(retrieveHandler, "/app", id);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveJsonArrayAppUserIds(final String id, final Handler<AsyncResult<JsonArray>> retrieveHandler) {

    this.getJsonArray(retrieveHandler, "/app", id, "/users");

  }

}
