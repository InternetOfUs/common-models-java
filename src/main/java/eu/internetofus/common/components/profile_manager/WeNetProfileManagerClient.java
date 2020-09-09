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

package eu.internetofus.common.components.profile_manager;

import eu.internetofus.common.vertx.ComponentClient;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

/**
 * The client to interact with the {@link WeNetProfileManager}.
 *
 * @see WeNetProfileManager
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetProfileManagerClient extends ComponentClient implements WeNetProfileManager {

  /**
   * The default URL to bind the client.
   */
  public static final String DEFAULT_PROFILE_MANAGER_API_URL = "https://wenet.u-hopper.com/prod/profile_manager";

  /**
   * The name of the configuration property that contains the URL to the profile manager API.
   */
  public static final String PROFILE_MANAGER_CONF_KEY = "profileManager";

  /**
   * Create a new service to interact with the WeNet profile manager.
   *
   * @param client to interact with the other modules.
   * @param conf   configuration.
   */
  public WeNetProfileManagerClient(final WebClient client, final JsonObject conf) {

    super(client, conf.getString(PROFILE_MANAGER_CONF_KEY, DEFAULT_PROFILE_MANAGER_API_URL));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createProfile(final JsonObject profile, final Handler<AsyncResult<JsonObject>> createHandler) {

    this.post(profile, createHandler, "/profiles");

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveJsonProfile(final String id, final Handler<AsyncResult<JsonObject>> retrieveHandler) {

    this.getJsonObject(retrieveHandler, "/profiles/", id);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteProfile(final String id, final Handler<AsyncResult<Void>> deleteHandler) {

    this.delete(deleteHandler, "/profiles/", id);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createCommunity(final JsonObject community, final Handler<AsyncResult<JsonObject>> createHandler) {

    this.post(community, createHandler, "/communities");

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveJsonCommunity(final String id, final Handler<AsyncResult<JsonObject>> retrieveHandler) {

    this.getJsonObject(retrieveHandler, "/communities/", id);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteCommunity(final String id, final Handler<AsyncResult<Void>> deleteHandler) {

    this.delete(deleteHandler, "/communities/", id);

  }

}
