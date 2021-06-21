/*
 * -----------------------------------------------------------------------------
 *
 *   Copyright 2019 - 2022 UDT-IA, IIIA-CSIC
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
