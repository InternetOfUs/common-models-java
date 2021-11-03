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

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.model.ModelTestCase;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link SimilarityData}.
 *
 * @see SimilarityData
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class SimilarityDataTest extends ModelTestCase<SimilarityData> {

  /**
   * {@inheritDoc}
   */
  @Override
  public SimilarityData createModelExample(final int index) {

    final var model = new SimilarityData();
    model.source = "Source of " + index;
    model.userId = "User of " + index;
    return model;

  }

  /**
   * Create a valid similarity data.
   *
   * @param index       of the example to create.
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @return the valid model.
   */
  public Future<SimilarityData> createModelExample(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    return StoreServices.storeProfileExample(index, vertx, testContext).map(profile -> {

      final var model = new SimilarityData();
      model.userId = profile.id;
      model.source = "Witch is the best nationality flag?";
      return model;

    });
  }

}
