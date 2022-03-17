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

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.models.Task;
import eu.internetofus.common.components.models.WeNetUserProfile;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;

/**
 * Check the {@link DefaultProtocols#ASK_4_HELP_V2} protocol when the requester
 * does not have the required attributes to calculate diversity. ATTENTION: This
 * test is sequential and maintains the state between methods. In other words,
 * you must to run the entire test methods on the specified order to work.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class Ask4HelpV2_2ProtocolWithAllFiltersButWithEmptyProfileForRequesterITC
    extends AbstractAsk4HelpV2_2ProtocolRandomUsersITC {

  /**
   * {@inheritDoc}
   */
  @Override
  protected Task createTaskForProtocol() {

    final var task = super.createTaskForProtocol();
    task.attributes.put("domain", "studying_career").put("domainInterest", "different").put("beliefsAndValues",
        "different");

    return task;
  }

  /**
   * Create an empty profile for requester.
   *
   * {@inheritDoc}
   */
  @Override
  protected Future<WeNetUserProfile> createProfileFor(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    if (index == 0) {

      return StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext);

    } else {

      return super.createProfileFor(index, vertx, testContext);
    }
  }

}
