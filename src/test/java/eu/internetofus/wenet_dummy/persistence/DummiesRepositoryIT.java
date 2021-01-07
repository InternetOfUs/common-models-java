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
package eu.internetofus.wenet_dummy.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.wenet_dummy.WeNetDummyIntegrationExtension;
import eu.internetofus.wenet_dummy.service.Dummy;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Integration test over the {@link DummiesRepository}.
 *
 * @see DummiesRepository
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetDummyIntegrationExtension.class)
public class DummiesRepositoryIT {

  /**
   * Verify that can store a dummy.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see DummiesRepository#storeDummy(Dummy)
   */
  @Test
  public void shouldStoreDummy(final Vertx vertx, final VertxTestContext testContext) {

    final var dummy = new Dummy();
    testContext.assertComplete(DummiesRepository.createProxy(vertx).storeDummy(dummy))
        .onSuccess(storedDummy -> testContext.verify(() -> {

          assertThat(storedDummy).isNotNull();
          assertThat(storedDummy.id).isNotEmpty();
          testContext.completeNow();
        }));

  }

  /**
   * Verify that can store a dummy.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see DummiesRepository#storeDummy(Dummy )
   */
  @Test
  public void shouldStoreDummyWithAnId(final Vertx vertx, final VertxTestContext testContext) {

    final var id = UUID.randomUUID().toString();
    final var dummy = new Dummy();
    dummy.id = id;
    testContext.assertComplete(DummiesRepository.createProxy(vertx).storeDummy(dummy))
        .onSuccess(storedDummy -> testContext.verify(() -> {

          assertThat(storedDummy.id).isEqualTo(id);
          testContext.completeNow();
        }));

  }

  /**
   * Verify that can store a dummy with an id of an stored dummy.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see DummiesRepository#storeDummy(Dummy)
   */
  @Test
  public void shouldNotStoreTwoDummyWithTheSameId(final Vertx vertx, final VertxTestContext testContext) {

    final var id = UUID.randomUUID().toString();
    final var dummy = new Dummy();
    dummy.id = id;
    final var repository = DummiesRepository.createProxy(vertx);
    testContext.assertFailure(repository.storeDummy(dummy).compose(stored -> repository.storeDummy(stored)))
        .onFailure(error -> testContext.completeNow());

  }

}
