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

import eu.internetofus.common.components.AbstractComponentMocker;
import eu.internetofus.common.components.CRUDContext;
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
