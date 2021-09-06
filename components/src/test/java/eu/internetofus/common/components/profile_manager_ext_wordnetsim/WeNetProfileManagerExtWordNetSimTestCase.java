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

package eu.internetofus.common.components.profile_manager_ext_wordnetsim;

import eu.internetofus.common.components.WeNetComponentTestCase;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;

/**
 * Generic test over the {@link WeNetProfileManagerExtWordNetSim}.
 *
 * @see WeNetProfileManagerExtWordNetSim
 *
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class WeNetProfileManagerExtWordNetSimTestCase
    extends WeNetComponentTestCase<WeNetProfileManagerExtWordNetSim> {

  /**
   * {@inheritDoc}
   *
   * @see WeNetProfileManagerExtWordNetSim#createProxy(Vertx)
   */
  @Override
  protected WeNetProfileManagerExtWordNetSim createComponentProxy(final Vertx vertx) {

    return WeNetProfileManagerExtWordNetSim.createProxy(vertx);
  }

  /**
   * Should not calculate similarity without source.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotCalculateSimilarityWithoutSource(final Vertx vertx, final VertxTestContext testContext) {

    final var data = new StringsSimilarityDataTest().createModelExample(1);
    data.source = null;
    this.createComponentProxy(vertx).similarityBetweenStrings(data)
        .onComplete(testContext.failing(any -> testContext.completeNow()));

  }

  /**
   * Should not calculate similarity without target.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotCalculateSimilarityWithoutTarget(final Vertx vertx, final VertxTestContext testContext) {

    final var data = new StringsSimilarityDataTest().createModelExample(1);
    data.target = null;
    this.createComponentProxy(vertx).similarityBetweenStrings(data)
        .onComplete(testContext.failing(any -> testContext.completeNow()));

  }

  /**
   * Should not calculate similarity without aggregation.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotCalculateSimilarityWithoutAggregation(final Vertx vertx, final VertxTestContext testContext) {

    final var data = new StringsSimilarityDataTest().createModelExample(1);
    data.aggregation = null;
    this.createComponentProxy(vertx).similarityBetweenStrings(data)
        .onComplete(testContext.failing(any -> testContext.completeNow()));

  }

}
