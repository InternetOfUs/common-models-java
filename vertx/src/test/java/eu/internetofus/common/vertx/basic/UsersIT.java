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
package eu.internetofus.common.vertx.basic;

import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.vertx.AbstractModelResourcesIT;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test the users integration
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(BasicIntegrationExtension.class)
public class UsersIT extends AbstractModelResourcesIT<User, String> {

  /**
   * {@inheritDoc}
   */
  @Override
  protected String modelPath() {

    return Users.PATH;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected User createInvalidModel() {

    final var model = new User();
    model.name = "";
    return model;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Future<User> createValidModelExample(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    final var model = new User();
    model.name = "Name of " + index;
    return Future.succeededFuture(model);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Future<User> storeModel(final User source, final Vertx vertx, final VertxTestContext testContext) {

    return testContext.assertComplete(UsersRepository.createProxy(vertx).storeUser(source));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void assertThatCreatedEquals(final User source, final User target) {

    source._id = target._id;
    assertThat(source).isEqualTo(target);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String idOf(final User model) {

    return model._id;

  }

}
