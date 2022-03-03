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

package eu.internetofus.common.components.models;

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.WeNetIntegrationExtension;
import eu.internetofus.common.components.service.App;
import eu.internetofus.common.model.ModelTestCase;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test the {@link DeprecatedSocialNetworkRelationship}.
 *
 * @see DeprecatedSocialNetworkRelationship
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetIntegrationExtension.class)
public class DeprecatedSocialNetworkRelationshipTest extends ModelTestCase<DeprecatedSocialNetworkRelationship> {

  /**
   * {@inheritDoc}
   */
  @Override
  public DeprecatedSocialNetworkRelationship createModelExample(final int index) {

    final var model = new DeprecatedSocialNetworkRelationship();
    model.appId = "app_of_" + index;
    model.userId = "user_of_" + index;
    model.type = SocialNetworkRelationshipType.values()[index % SocialNetworkRelationshipType.values().length];
    model.weight = index % 1000 / 1000.0;
    return model;

  }

  /**
   * Create an example model that has the specified index.
   *
   * @param index       to use in the example.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @return the created social network relationship.
   */
  public Future<DeprecatedSocialNetworkRelationship> createModelExample(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    return StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).compose(user -> {

      final var relation = this.createModelExample(index);
      relation.userId = user.id;
      return StoreServices.storeApp(new App(), vertx, testContext).map(app -> {
        relation.appId = app.appId;
        return relation;
      });
    });

  }

}
