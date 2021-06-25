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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link AbstractMainVerticle}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith({ VertxExtension.class, MockitoExtension.class })
public class AbstractMainVerticleTest {

  /**
   * The verticle to test.
   */
  @Spy
  public AbstractMainVerticle verticle;

  /**
   * Check that start and stop verticle.
   *
   * @param vertx       event bus to use.
   * @param testContext context of the test.
   *
   * @throws Exception If the verticle cannot be started/stopped.
   */
  @Test
  public void shouldStartAndStop(final Vertx vertx, final VertxTestContext testContext) throws Exception {

    doReturn(new Class[0]).when(this.verticle).getVerticleClassesToDeploy();
    final Promise<Void> startPromise = Promise.promise();
    this.verticle.start(startPromise);
    startPromise.future().onComplete(testContext.succeeding(empty -> testContext.verify(() -> {

      this.verticle.stop();
      testContext.completeNow();
    })));

  }

  /**
   * Check that not start with bad verticle.
   *
   * @param vertx       event bus to use.
   * @param testContext context of the test.
   *
   * @throws Exception If the verticle cannot be started/stopped.
   */
  @Test
  public void shouldNotStartWithBadVerticle(final Vertx vertx, final VertxTestContext testContext) throws Exception {

    doReturn(new Class[] { DummyWorkerVerticle.class, NoStartVerticle.class }).when(this.verticle).getVerticleClassesToDeploy();
    doReturn(new JsonObject()).when(this.verticle).config();
    doReturn(vertx).when(this.verticle).getVertx();
    final Promise<Void> startPromise = Promise.promise();
    this.verticle.start(startPromise);
    startPromise.future().onComplete(testContext.failing(error -> testContext.completeNow()));

  }

  /**
   * Check that capture exception when start verticle.
   *
   * @param vertx       event bus to use.
   * @param testContext context of the test.
   *
   * @throws Exception If the verticle cannot be started/stopped.
   */
  @Test
  public void shouldCaptureExceptionWhenStartVerticle(final Vertx vertx, final VertxTestContext testContext) throws Exception {

    doReturn(new Class[] { null }).when(this.verticle).getVerticleClassesToDeploy();
    final var cause = new RuntimeException("No configuration defined");
    doThrow(cause).when(this.verticle).config();
    final Promise<Void> startPromise = Promise.promise();
    this.verticle.start(startPromise);
    startPromise.future().onComplete(testContext.failing(error -> testContext.verify(() -> {

      assertThat(error).isSameAs(cause);
      testContext.completeNow();

    })));

  }

}
