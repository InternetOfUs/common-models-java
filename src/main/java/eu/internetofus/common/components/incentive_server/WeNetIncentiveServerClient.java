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

package eu.internetofus.common.components.incentive_server;

import eu.internetofus.common.vertx.ComponentClient;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

/**
 * The client to interact with the {@link WeNetIncentiveServer}.
 *
 * @see WeNetIncentiveServer#register(io.vertx.core.Vertx, WebClient, io.vertx.core.json.JsonObject)
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetIncentiveServerClient extends ComponentClient implements WeNetIncentiveServer {

  /**
   * The default URL to bind the client.
   */
  public static final String DEFAULT_INCENTIVE_SERVER_API_URL = "https://wenet.u-hopper.com/prod/incentive_server";

  /**
   * The name of the configuration property that contains the URL to the incentive server API.
   */
  public static final String INCENTIVE_SERVER_CONF_KEY = "incentiveServer";

  /**
   * Create a new incentive_server.
   *
   * @param client to interact with the other modules.
   * @param conf   configuration of the component.
   */
  public WeNetIncentiveServerClient(final WebClient client, final JsonObject conf) {

    super(client, conf.getString(INCENTIVE_SERVER_CONF_KEY, DEFAULT_INCENTIVE_SERVER_API_URL));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateJsonTaskStatus(final JsonObject status, final Handler<AsyncResult<JsonObject>> updateHandler) {

    this.post(status, updateHandler, "/Tasks/TaskStatus/");

  }

}
