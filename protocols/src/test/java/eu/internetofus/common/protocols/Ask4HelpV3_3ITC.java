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
package eu.internetofus.common.protocols;

import eu.internetofus.common.components.models.SocialNetworkRelationship;
import eu.internetofus.common.components.models.SocialNetworkRelationshipType;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Test the Ask4Help version 3.3.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class Ask4HelpV3_3ITC extends PilotM46NUMProtocolWithAllIndifferentDimensionsITC {

  /**
   * {@inheritDoc}
   *
   * @see DefaultProtocols#ASK_4_HELP_V3_3
   */
  @Override
  protected DefaultProtocols getDefaultProtocolsToUse() {

    return DefaultProtocols.ASK_4_HELP_V3_3;
  }

  /**
   * Check that a task is created.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Override
  @Test
  @Order(9)
  public void shouldFillSocialCloseness(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(8, testContext);

    @SuppressWarnings("rawtypes")
    final List<Future> added = new ArrayList<>();
    for (var i = 1; i < this.users.size() - 1; i++) {

      final var weight = this.socialClosenessTo(i);
      if (weight != null) {

        final var relationship = new SocialNetworkRelationship();
        relationship.appId = this.app.appId;
        relationship.sourceId = this.users.get(0).id;
        relationship.targetId = this.users.get(i).id;
        relationship.weight = weight;
        relationship.type = SocialNetworkRelationshipType.values()[i
            % (SocialNetworkRelationshipType.values().length - 1)];
        added.add(WeNetProfileManager.createProxy(vertx).addOrUpdateSocialNetworkRelationship(relationship));
      }
    }

    CompositeFuture.all(added)
        .onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));
  }
}
