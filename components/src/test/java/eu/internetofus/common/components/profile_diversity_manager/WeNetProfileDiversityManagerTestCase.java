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

package eu.internetofus.common.components.profile_diversity_manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

import eu.internetofus.common.components.WeNetComponentTestCase;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;

/**
 * Generic test over the {@link WeNetProfileDiversityManager}.
 *
 * @see WeNetProfileDiversityManager
 *
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class WeNetProfileDiversityManagerTestCase
    extends WeNetComponentTestCase<WeNetProfileDiversityManager> {

  /**
   * {@inheritDoc}
   *
   * @see WeNetProfileDiversityManager#createProxy(Vertx)
   */
  @Override
  protected WeNetProfileDiversityManager createComponentProxy(final Vertx vertx) {

    return WeNetProfileDiversityManager.createProxy(vertx);
  }

  /**
   * Should not calculate the diversity with a bad agents data.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotCalculateDiversityWithBadAgentsData(final Vertx vertx, final VertxTestContext testContext) {

    this.createComponentProxy(vertx).calculateDiversityOf(new JsonObject().put("undefinedField", "value"),
        testContext.succeeding(diversity -> testContext.verify(() -> {

          assertThat(diversity.getDouble("value", -1d)).isEqualTo(0.0d);
          testContext.completeNow();
        })));

  }

  /**
   * An empty data will be a {@code 0} diversity.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldDiversityBeZeroIfEmptyData(final Vertx vertx, final VertxTestContext testContext) {

    this.createComponentProxy(vertx).calculateDiversityOf(new AgentsData())
        .onComplete(testContext.succeeding(diversity -> testContext.verify(() -> {

          assertThat(diversity.value).isEqualTo(0.0d);
          testContext.completeNow();
        })));

  }

  /**
   * Should calculate the diversity of some agents.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldCalculateDiversity(final Vertx vertx, final VertxTestContext testContext) {

    final var data = new AgentsData();
    data.agents = new ArrayList<>();
    final var agent1 = new AgentData();
    agent1.id = "1";
    agent1.quantitativeAttributes = new HashMap<>();
    agent1.quantitativeAttributes.put("introvert", 1.0);
    agent1.quantitativeAttributes.put("extrovert", 1.0);
    agent1.quantitativeAttributes.put("naturalist", 1.0);
    agent1.qualitativeAttributes = new HashMap<>();
    agent1.qualitativeAttributes.put("gender", "M");
    agent1.qualitativeAttributes.put("civilStatus", "married");
    data.agents.add(agent1);
    final var agent2 = new AgentData();
    agent2.id = "2";
    agent2.quantitativeAttributes = new HashMap<>();
    agent2.quantitativeAttributes.put("introvert", 0.0);
    agent2.quantitativeAttributes.put("extrovert", 0.0);
    agent2.quantitativeAttributes.put("naturalist", 0.0);
    agent2.qualitativeAttributes = new HashMap<>();
    agent2.qualitativeAttributes.put("gender", "F");
    agent2.qualitativeAttributes.put("civilStatus", "single");
    data.agents.add(agent2);

    data.qualitativeAttributes = new HashMap<>();
    data.qualitativeAttributes.put("gender", new HashSet<>(Arrays.asList("M", "F", "O")));
    data.qualitativeAttributes.put("civilStatus",
        new HashSet<>(Arrays.asList("single", "married", "divorced", "widow")));
    data.quantitativeAttributes = new HashSet<>(Arrays.asList("introvert", "extrovert", "naturalist"));
    this.createComponentProxy(vertx).calculateDiversityOf(data)
        .onComplete(testContext.succeeding(diversity -> testContext.verify(() -> {

          assertThat(diversity.value).isCloseTo(0.5261859507142914d, offset(0.000001));
          testContext.completeNow();
        })));

  }

  /**
   * Should not calculate the similarity with a bad attributes data.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotCalculateSimilarityWithBadAttributesData(final Vertx vertx, final VertxTestContext testContext) {

    this.createComponentProxy(vertx).calculateSimilarityOf(new JsonObject().put("undefinedField", "value"),
        testContext.failing(error -> testContext.completeNow()));

  }

  /**
   * An empty data will be an empty similarity.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldSimilarityFailWithEmptyData(final Vertx vertx, final VertxTestContext testContext) {

    this.createComponentProxy(vertx).calculateSimilarityOf(new AttributesData())
        .onComplete(testContext.failing(error -> testContext.completeNow()));

  }

  /**
   * Should calculate the similarity of some attributes.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldCalculateSimilarity(final Vertx vertx, final VertxTestContext testContext) {

    final var data = new AttributesData();
    data.source = "Do you have a bike?";
    data.attributes = new TreeSet<>();
    data.attributes.add("car");
    data.attributes.add("plane");
    data.attributes.add("vehicle");
    this.createComponentProxy(vertx).calculateSimilarityOf(data)
        .onComplete(testContext.succeeding(similarity -> testContext.verify(() -> {

          final var carSim = new AttributeSimilarity();
          carSim.attribute = "car";
          carSim.similarity = 0.6239031848677311d;
          final var planeSim = new AttributeSimilarity();
          planeSim.attribute = "plane";
          planeSim.similarity = 0.0d;
          final var vehicleSim = new AttributeSimilarity();
          vehicleSim.attribute = "vehicle";
          vehicleSim.similarity = 0.6239031848677311d;
          assertThat(similarity.similarities).hasSize(3).contains(carSim, planeSim, vehicleSim);
          testContext.completeNow();
        })));

  }

}
