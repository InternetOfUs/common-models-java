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
