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

package eu.internetofus.common.components.profile_manager;

import eu.internetofus.common.components.AbstractComponentMocker;
import eu.internetofus.common.components.CRUDContext;
import eu.internetofus.common.components.models.CommunityProfile;
import eu.internetofus.common.components.models.WeNetUserProfile;
import io.vertx.ext.web.Router;

/**
 * The mocked server for the {@link WeNetProfileManager}.
 *
 * @see WeNetProfileManager
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetProfileManagerMocker extends AbstractComponentMocker {

  /**
   * The context to do CRUD operations over the profiles.
   */
  protected CRUDContext profilesContext = new CRUDContext("id", "profiles", WeNetUserProfile.class);

  /**
   * The context to do CRUD operations over the communities.
   */
  protected CRUDContext communitiesContext = new CRUDContext("id", "communities", CommunityProfile.class);

  /**
   * Start a mocker builder into a random port.
   *
   * @return the started mocker.
   */
  public static WeNetProfileManagerMocker start() {

    return start(0);

  }

  /**
   * Start a mocker builder into a port.
   *
   * @param port to bind the server.
   *
   * @return the started mocker.
   */
  public static WeNetProfileManagerMocker start(final int port) {

    final var mocker = new WeNetProfileManagerMocker();
    mocker.startServerAndWait(port);
    return mocker;
  }

  /**
   * {@inheritDoc}
   *
   * @see WeNetProfileManagerClient#PROFILE_MANAGER_CONF_KEY
   */
  @Override
  protected String getComponentConfigurationName() {

    return WeNetProfileManagerClient.PROFILE_MANAGER_CONF_KEY;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void fillInRouterHandler(final Router router) {

    router.post("/profiles").handler(this.profilesContext.postHandler());
    router.get("/profiles/:id").handler(this.profilesContext.getHandler());
    router.put("/profiles/:id").handler(this.profilesContext.putHandler());
    router.patch("/profiles/:id").handler(this.profilesContext.patchHandler());
    router.delete("/profiles/:id").handler(this.profilesContext.deleteHandler());

    router.post("/communities").handler(this.communitiesContext.postHandler());
    router.get("/communities/:id").handler(this.communitiesContext.getHandler());
    router.get("/communities").handler(this.communitiesContext.getPageHandler("appId", "name", "description"));
    router.put("/communities/:id").handler(this.communitiesContext.putHandler());
    router.patch("/communities/:id").handler(this.communitiesContext.patchHandler());
    router.delete("/communities/:id").handler(this.communitiesContext.deleteHandler());

  }

}
