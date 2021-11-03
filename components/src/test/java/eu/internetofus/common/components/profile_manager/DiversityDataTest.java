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
import java.util.HashSet;

/**
 * Test the {@link DiversityData}.
 *
 * @see DiversityData
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class DiversityDataTest extends ModelTestCase<DiversityData> {

  /**
   * {@inheritDoc}
   */
  @Override
  public DiversityData createModelExample(final int index) {

    final var model = new DiversityData();
    model.attributes = new HashSet<>();
    model.attributes.add("gender");
    model.attributes.add("occupation");
    model.attributes.add("index of " + index);
    model.userIds = new HashSet<>();
    model.userIds.add("User of " + index);
    model.userIds.add("User of " + index + 1);
    model.userIds.add("User of " + index + 2);
    return model;

  }

  /**
   * Create a valid diversity data.
   *
   * @param index       of the example to create.
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @return the valid model.
   */
  public Future<DiversityData> createModelExample(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    final var model = new DiversityData();
    model.attributes = new HashSet<>();
    model.attributes.add("dateOfBirth.day");
    model.attributes.add("dateOfBirth.month");
    model.attributes.add("dateOfBirth.year");
    model.attributes.add("gender");
    model.attributes.add("locale");
    model.attributes.add("nationality");
    model.attributes.add("occupation");
    model.attributes.add("materials.department");
    model.attributes.add("materials.degree_programme");
    model.attributes.add("materials.accommodation");
    model.attributes.add("competences.c_food");
    model.attributes.add("competences.c_eating");
    model.attributes.add("competences.u_active");
    model.attributes.add("competences.u_essay");
    model.attributes.add("meanings.excitement");
    model.attributes.add("meanings.promotion");
    model.attributes.add("meanings.extraversion");
    model.attributes.add("meanings.agreeableness");
    model.userIds = new HashSet<>();
    var future = Future.succeededFuture(model);
    for (var i = 0; i < 3; i++) {

      final var profileIndex = index + i;
      future = future
          .compose(chainModel -> StoreServices.storeProfileExample(profileIndex, vertx, testContext).map(profile -> {

            chainModel.userIds.add(profile.id);
            return chainModel;

          }));
    }
    return future;
  }

}
