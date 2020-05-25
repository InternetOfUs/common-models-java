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

package eu.internetofus.common.components.social_context_builder;

import eu.internetofus.common.vertx.ComponentClient;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.serviceproxy.ServiceBinder;

/**
 * Implementation of the {@link WeNetSocialContextBuilder}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetSocialContextBuilderImpl extends ComponentClient implements WeNetSocialContextBuilder {

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_common.service.SocialContextBuilder";

  /**
   * Create a proxy of the {@link WeNetSocialContextBuilder}.
   *
   * @param vertx where the service has to be used.
   *
   * @return the task.
   */
  static WeNetSocialContextBuilder createProxy(final Vertx vertx) {

    return new WeNetSocialContextBuilderVertxEBProxy(vertx, WeNetSocialContextBuilder.ADDRESS);
  }

  /**
   * Register this service.
   *
   * @param vertx  that contains the event bus to use.
   * @param client to do HTTP requests to other services.
   * @param conf   configuration to use.
   */
  static void register(final Vertx vertx, final WebClient client, final JsonObject conf) {

    new ServiceBinder(vertx).setAddress(WeNetSocialContextBuilder.ADDRESS).register(WeNetSocialContextBuilder.class, new WeNetSocialContextBuilderImpl(client, conf));

  }


  /**
   * Create a new service to interact with the WeNet interaction protocol engine.
   *
   * @param client to interact with the other modules.
   * @param conf   configuration of the component.
   */
  public WeNetSocialContextBuilderImpl(final WebClient client, final JsonObject conf) {

    super(client, conf.getString("socialContextBuilder", "https://wenet.u-hopper.com/social_context_builder"));

  }

  /**
   * {@inheridDoc}
   */
  @Override
  public void retrieveJsonArraySocialRelations(final String userId, final Handler<AsyncResult<JsonArray>> retrieveHandler) {

    this.getJsonArray(retrieveHandler, "/social/relations", userId);

  }

}
