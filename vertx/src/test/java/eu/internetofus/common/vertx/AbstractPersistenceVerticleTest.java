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

package eu.internetofus.common.vertx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import eu.internetofus.common.Containers;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link AbstractPersistenceVerticle}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith({ VertxExtension.class, MockitoExtension.class })
public class AbstractPersistenceVerticleTest {

  /**
   * The verticle to test.
   */
  @Spy
  public AbstractPersistenceVerticle verticle;

  /**
   * Set the default mocking methods.
   *
   * @param vertx event bus to use.
   */
  @BeforeEach
  public void setDefaultMocks(final Vertx vertx) {

    doReturn(new JsonObject().put("persistence", Containers.status().startMongoContainer().getMongoDBConfig())).when(this.verticle).config();
    doReturn(vertx).when(this.verticle).getVertx();

  }

  /**
   * Check that not start because can not register repositories.
   *
   * @param testContext context of the test.
   *
   * @throws Exception If the verticle cannot be started.
   */
  @Test
  public void shouldNotStartBecauseFailToRegisterRepositoriesFor(final VertxTestContext testContext) throws Exception {

    doReturn(Future.failedFuture("Cannot register")).when(this.verticle).registerRepositoriesFor(any());
    final Promise<Void> startPromise = Promise.promise();
    this.verticle.start(startPromise);
    startPromise.future().onComplete(testContext.failing(error -> testContext.completeNow()));

  }

  /**
   * Check that start and stop the verticle
   *
   * @param testContext context of the test.
   *
   * @throws Exception If the verticle cannot be started/stopped.
   */
  @Test
  public void shouldStartAndStop(final VertxTestContext testContext) throws Exception {

    doReturn(Future.succeededFuture()).when(this.verticle).registerRepositoriesFor(any());
    final Promise<Void> startPromise = Promise.promise();
    this.verticle.start(startPromise);
    startPromise.future().onComplete(testContext.succeeding(empty -> testContext.verify(() -> {

      assertThat(this.verticle.pool).isNotNull();
      this.verticle.stop();
      assertThat(this.verticle.pool).isNull();
      this.verticle.stop();
      testContext.completeNow();
    })));

  }

}
