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
import static org.mockito.Mockito.doThrow;

import eu.internetofus.common.Containers;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.net.ServerSocket;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test the {@link AbstractAPIVerticle}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith({ VertxExtension.class, MockitoExtension.class })
public class AbstractAPIVerticleTest {

  /**
   * The verticle to test.
   */
  @Spy
  public AbstractAPIVerticle verticle;

  /**
   * Check that not start because can not register repositories.
   *
   * @param vertx       event bus to use.
   * @param testContext context of the test.
   *
   * @throws Exception If the verticle cannot be started/stopped.
   */
  @Test
  public void shouldNotStartWithBadOpenApiSpecification(final Vertx vertx, final VertxTestContext testContext)
      throws Exception {

    doReturn(vertx).when(this.verticle).getVertx();
    doReturn("eu/internetofus/common/vertx/badOpenApi.yml").when(this.verticle).getOpenAPIResourcePath();
    final Promise<Void> startPromise = Promise.promise();
    this.verticle.start(startPromise);
    startPromise.future().onComplete(testContext.failing(error -> testContext.completeNow()));

  }

  /**
   * Check that not start becuse can not mount service interfaces.
   *
   * @param vertx       event bus to use.
   * @param testContext context of the test.
   *
   * @throws Exception If the verticle cannot be started/stopped.
   */
  @Test
  public void shouldNotStartBacauseCannotMount(final Vertx vertx, final VertxTestContext testContext) throws Exception {

    doReturn(vertx).when(this.verticle).getVertx();
    doReturn("eu/internetofus/common/vertx/basicOpenApi.yaml").when(this.verticle).getOpenAPIResourcePath();
    doThrow(new RuntimeException("Unexpected error")).when(this.verticle).mountServiceInterfaces(any());
    final Promise<Void> startPromise = Promise.promise();
    this.verticle.start(startPromise);
    startPromise.future().onComplete(testContext.failing(error -> testContext.completeNow()));

  }

  /**
   * Check that not start becuse can not register repositories.
   *
   * @param vertx       event bus to use.
   * @param testContext context of the test.
   *
   * @throws Exception If the verticle cannot be started/stopped.
   */
  @Test
  public void shouldNotStartWithBindedPort(final Vertx vertx, final VertxTestContext testContext) throws Exception {

    final var port = Containers.nextFreePort();
    new Thread(() -> {

      try {

        final var serverSocket = new ServerSocket(port);
        serverSocket.accept();
        serverSocket.close();

      } catch (final Throwable ignored) {
      }

    }).start();
    doReturn(vertx).when(this.verticle).getVertx();
    doReturn("eu/internetofus/common/vertx/basicOpenApi.yaml").when(this.verticle).getOpenAPIResourcePath();
    doReturn(new JsonObject().put("api", new JsonObject().put("port", port))).when(this.verticle).config();
    final Promise<Void> startPromise = Promise.promise();
    this.verticle.start(startPromise);
    startPromise.future().onComplete(testContext.failing(error -> testContext.completeNow()));

  }

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

    doReturn(vertx).when(this.verticle).getVertx();
    doReturn("eu/internetofus/common/vertx/basicOpenApi.yaml").when(this.verticle).getOpenAPIResourcePath();
    doReturn(new JsonObject().put("api", new JsonObject().put("port", 0))).when(this.verticle).config();
    final Promise<Void> startPromise = Promise.promise();
    this.verticle.start(startPromise);
    startPromise.future().onComplete(testContext.succeeding(empty -> testContext.verify(() -> {

      assertThat(this.verticle.server).isNotNull();
      this.verticle.stop();
      assertThat(this.verticle.server).isNull();
      this.verticle.stop();
      testContext.completeNow();
    })));

  }

}
