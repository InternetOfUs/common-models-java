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

package eu.internetofus.common.components.social_context_builder;

import static eu.internetofus.common.components.AbstractComponentMocker.createClientWithDefaultSession;
import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test the {@link WeNetSocialContextBuilderSimulator}.
 *
 * @see WeNetSocialContextBuilderSimulator
 * @see WeNetSocialContextBuilderSimulatorClient
 * @see WeNetSocialContextBuilderSimulatorMocker
 *
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class WeNetSocialContextBuilderSimulatorTest extends WeNetSocialContextBuilderTestCase {

  /**
   * The social context builder mocked server.
   */
  protected static WeNetSocialContextBuilderSimulatorMocker socialContextBuilderMocker;

  /**
   * Start the mocker servers.
   */
  @BeforeAll
  public static void startMockers() {

    socialContextBuilderMocker = WeNetSocialContextBuilderSimulatorMocker.start();
  }

  /**
   * Stop the mocker server.
   */
  @AfterAll
  public static void stopMockers() {

    socialContextBuilderMocker.stopServer();
  }

  /**
   * Register the client.
   *
   * @param vertx event bus to use.
   */
  @BeforeEach
  public void registerClient(final Vertx vertx) {

    final var client = createClientWithDefaultSession(vertx);
    final var socialContextBuilderConf = socialContextBuilderMocker.getComponentConfiguration();
    WeNetSocialContextBuilderSimulator.register(vertx, client, socialContextBuilderConf);
    WeNetSocialContextBuilder.register(vertx, client, socialContextBuilderConf);

  }

  /**
   * Should set and get the social relations.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldSetGetSocialRelations(final Vertx vertx, final VertxTestContext testContext) {

    final var userId = UUID.randomUUID().toString();
    final var relations = new ArrayList<UserRelation>();
    testContext.assertComplete(WeNetSocialContextBuilderSimulator.createProxy(vertx).retrieveSocialRelations(userId))
        .onSuccess(retrieveRelations -> testContext.verify(() -> {

          assertThat(retrieveRelations).isEqualTo(relations);
          relations.add(new UserRelationTest().createModelExample(1));
          relations.add(new UserRelationTest().createModelExample(2));
          relations.add(new UserRelationTest().createModelExample(3));

          testContext
              .assertComplete(
                  WeNetSocialContextBuilderSimulator.createProxy(vertx).setSocialRelations(userId, relations))
              .onSuccess(storedRelations -> testContext.verify(() -> {

                assertThat(storedRelations).isEqualTo(relations);
                testContext
                    .assertComplete(
                        WeNetSocialContextBuilderSimulator.createProxy(vertx).retrieveSocialRelations(userId))
                    .onSuccess(retrieveRelations2 -> testContext.verify(() -> {

                      assertThat(retrieveRelations2).isEqualTo(relations);
                      testContext.completeNow();
                    }));
              }));
        }));
  }

  /**
   * Should set and get the social preferences.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldSetGetSocialPreferences(final Vertx vertx, final VertxTestContext testContext) {

    final var userId = UUID.randomUUID().toString();
    final var taskId = UUID.randomUUID().toString();
    final var preferences = new ArrayList<String>();
    testContext
        .assertComplete(
            WeNetSocialContextBuilderSimulator.createProxy(vertx).getPreferencesForUserOnTask(userId, taskId))
        .onSuccess(retrievePreferences -> testContext.verify(() -> {

          assertThat(retrievePreferences).isEqualTo(preferences);
          preferences.add("1");
          preferences.add("2");
          preferences.add("3");

          testContext
              .assertComplete(WeNetSocialContextBuilderSimulator.createProxy(vertx)
                  .postSocialPreferencesForUserOnTask(userId, taskId, preferences))
              .onSuccess(storedPreferences -> testContext.verify(() -> {

                assertThat(storedPreferences).isEqualTo(preferences);
                testContext
                    .assertComplete(WeNetSocialContextBuilderSimulator.createProxy(vertx)
                        .getPreferencesForUserOnTask(userId, taskId))
                    .onSuccess(retrievePreferences2 -> testContext.verify(() -> {

                      assertThat(retrievePreferences2).isEqualTo(preferences);
                      testContext.completeNow();
                    }));
              }));
        }));
  }

  /**
   * Should set and get the social explanation.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldSetGetSocialExplanation(final Vertx vertx, final VertxTestContext testContext) {

    final var userId = UUID.randomUUID().toString();
    final var taskId = UUID.randomUUID().toString();
    testContext
        .assertComplete(WeNetSocialContextBuilderSimulator.createProxy(vertx).retrieveSocialExplanation(userId, taskId))
        .onSuccess(retrieveExplanation -> testContext.verify(() -> {

          assertThat(retrieveExplanation).isEqualTo(new SocialExplanation());
          final var explanation = new SocialExplanationTest().createModelExample(1);

          testContext.assertComplete(
              WeNetSocialContextBuilderSimulator.createProxy(vertx).setSocialExplanation(userId, taskId, explanation))
              .onSuccess(storedExplanation -> testContext.verify(() -> {

                assertThat(storedExplanation).isEqualTo(explanation);
                testContext
                    .assertComplete(
                        WeNetSocialContextBuilderSimulator.createProxy(vertx).retrieveSocialExplanation(userId, taskId))
                    .onSuccess(retrieveExplanation2 -> testContext.verify(() -> {

                      assertThat(retrieveExplanation2).isEqualTo(explanation);
                      testContext.completeNow();
                    }));
              }));
        }));
  }

  /**
   * Should set and get the social preferences answers.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldSetGetSocialPreferencesAnswers(final Vertx vertx, final VertxTestContext testContext) {

    final var userId = UUID.randomUUID().toString();
    final var taskId = UUID.randomUUID().toString();
    final var preferences = new AnswersData();
    preferences.data = new ArrayList<UserAnswer>();
    testContext
        .assertComplete(
            WeNetSocialContextBuilderSimulator.createProxy(vertx).getPreferencesAnswersForUserOnTask(userId, taskId))
        .onSuccess(ranking -> testContext.verify(() -> {

          assertThat(ranking).isEqualTo(preferences.data);

          for (var i = 0; i < 5; i++) {

            final var userAnswer = new UserAnswer();
            userAnswer.userId = "UserId_" + i;
            userAnswer.answer = "Answer_" + i;
            preferences.data.add(userAnswer);

          }

          testContext
              .assertComplete(WeNetSocialContextBuilderSimulator.createProxy(vertx)
                  .postSocialPreferencesAnswersForUserOnTask(userId, taskId, preferences))
              .onSuccess(storedPreferences -> testContext.verify(() -> {

                assertThat(storedPreferences).isEqualTo(preferences.data);
                testContext
                    .assertComplete(WeNetSocialContextBuilderSimulator.createProxy(vertx)
                        .getPreferencesAnswersForUserOnTask(userId, taskId))
                    .onSuccess(retrievePreferences2 -> testContext.verify(() -> {

                      assertThat(retrievePreferences2).isEqualTo(preferences.data);
                      testContext.completeNow();
                    }));
              }));
        }));
  }

}
