/*
 * -----------------------------------------------------------------------------
 *
 *   Copyright 2019 - 2022 UDT-IA, IIIA-CSIC
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
   * Verify that can store a {@code null} dummy.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see DummiesRepository#storeDummy(Dummy)
   */
  @Test
  public void shouldStoreNullDummy(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertFailure(DummiesRepository.createProxy(vertx).storeDummy(null))
        .onFailure(error -> testContext.completeNow());

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

          assertThat(storedDummy).isNotNull();
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
    testContext.assertComplete(repository.storeDummy(dummy)).onSuccess(stored -> testContext.verify(() -> {

      assertThat(stored).isNotNull();
      testContext.assertFailure(repository.storeDummy(stored)).onFailure(error -> testContext.completeNow());

    }));
  }

}
