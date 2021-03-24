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

package eu.internetofus.common.components.personal_context_builder;

import eu.internetofus.common.vertx.ComponentClient;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import java.util.HashMap;
import javax.validation.constraints.NotNull;

/**
 * The implementation of the {@link WeNetPersonalContextBuilder}.
 *
 * @see WeNetPersonalContextBuilder
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetPersonalContextBuilderClient extends ComponentClient implements WeNetPersonalContextBuilder {

  /**
   * The default URL to bind the client.
   */
  public static final String DEFAULT_PERSONAL_CONTEXT_BUILDER_API_URL = "https://wenet.u-hopper.com/prod/personal_context_builder";

  /**
   * The name of the configuration property that contains the URL to the personal
   * context builder API.
   */
  public static final String PERSONAL_CONTEXT_BUILDER_CONF_KEY = "personalContextBuilder";

  /**
   * Create a new service to interact with the WeNet service.
   *
   * @param client to interact with the other modules.
   * @param conf   configuration.
   */
  public WeNetPersonalContextBuilderClient(final WebClient client, final JsonObject conf) {

    super(client, conf.getString(PERSONAL_CONTEXT_BUILDER_CONF_KEY, DEFAULT_PERSONAL_CONTEXT_BUILDER_API_URL));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void obtainUserlocations(final JsonObject users, final Handler<AsyncResult<JsonObject>> handler) {

    this.post(users, "/locations/").onComplete(handler);

  }

  @Override
  public void obtainClosestUsersTo(final double latitude, final double longitude, final int numUsers,
      @NotNull final Handler<AsyncResult<JsonArray>> handler) {

    final var queryParams = new HashMap<String, String>();
    queryParams.put("latitude", String.valueOf(latitude));
    queryParams.put("longitude", String.valueOf(longitude));
    queryParams.put("nb_user_max", String.valueOf(numUsers));
    this.getJsonArray(queryParams, "/closest/").onComplete(handler);

  }

}
