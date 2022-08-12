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

import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.WeNetComponentTestCase;
import eu.internetofus.common.components.models.WeNetUserProfileTest;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * Test the {@link WeNetSocialContextBuilder}.
 *
 * @see WeNetSocialContextBuilder
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetSocialContextBuilderTestCase extends WeNetComponentTestCase<WeNetSocialContextBuilder> {

  /**
   * {@inheritDoc}
   *
   * @see WeNetSocialContextBuilder#createProxy(Vertx)
   */
  @Override
  protected WeNetSocialContextBuilder createComponentProxy(final Vertx vertx) {

    return WeNetSocialContextBuilder.createProxy(vertx);
  }

  /**
   * Should update preferences for user on task.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldUpdatePreferencesForUserOnTask(final Vertx vertx, final VertxTestContext testContext) {

    final var userId = UUID.randomUUID().toString();
    final var taskId = UUID.randomUUID().toString();
    final var volunteers = new ArrayList<String>();
    testContext
        .assertComplete(this.createComponentProxy(vertx).postSocialPreferencesForUserOnTask(userId, taskId, volunteers))
        .onSuccess(updated -> testContext.verify(() -> {

          assertThat(updated).isNotNull();
          testContext.completeNow();

        }));

  }

  /**
   * Should retrieve social relations.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldRetrieveSocialExplanation(final Vertx vertx, final VertxTestContext testContext) {

    final var userId = UUID.randomUUID().toString();
    final var taskId = UUID.randomUUID().toString();
    testContext.assertComplete(this.createComponentProxy(vertx).retrieveSocialExplanation(userId, taskId))
        .onSuccess(relations -> testContext.verify(() -> {

          assertThat(relations).isNotNull();
          testContext.completeNow();

        }));

  }

  /**
   * Should update preferences answers for user on task.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldUpdatePreferencesAnswersForUserOnTask(final Vertx vertx, final VertxTestContext testContext) {

    final var userId = UUID.randomUUID().toString();
    final var taskId = UUID.randomUUID().toString();
    final var userAnswers = new UserAnswersTest().createModelExample(0);
    testContext
        .assertComplete(
            this.createComponentProxy(vertx).postSocialPreferencesAnswersForUserOnTask(userId, taskId, userAnswers))
        .onSuccess(updated -> testContext.verify(() -> {

          assertThat(updated).isNotNull();
          testContext.completeNow();

        }));

  }

  /**
   * Should initialize social relations.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldInitializeSocialRelations(final Vertx vertx, final VertxTestContext testContext) {

    final var profile = new WeNetUserProfileTest().createBasicExample(1);
    profile.id = "Id of 1";
    this.createComponentProxy(vertx).initializeSocialRelations(profile)
        .onComplete(testContext.succeeding(any -> testContext.completeNow()));

  }

  /**
   * Should social notification interaction.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldSocialNotificationInteraction(final Vertx vertx, final VertxTestContext testContext) {

    final var message = new UserMessageTest().createModelExample(1);
    this.createComponentProxy(vertx).socialNotificationInteraction(message)
        .onComplete(testContext.succeeding(any -> testContext.completeNow()));

  }

  /**
   * Should social notification.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldPutSocialPreferencesSelectedAnswerForUserOnTask(final Vertx vertx,
      final VertxTestContext testContext) {

    final var userId = UUID.randomUUID().toString();
    final var taskId = UUID.randomUUID().toString();
    final var userAnswers = new UserAnswersTest().createModelExample(0);
    this.createComponentProxy(vertx).putSocialPreferencesSelectedAnswerForUserOnTask(userId, taskId, 0, userAnswers)
        .onComplete(testContext.succeeding(any -> testContext.completeNow()));

  }

  /**
   * Should social notification profile update.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldSocialNotificationProfileUpdate(final Vertx vertx, final VertxTestContext testContext) {

    final var userId = UUID.randomUUID().toString();
    final var notification = new ProfileUpdateNotification();
    notification.updatedFieldNames = new HashSet<String>();
    notification.updatedFieldNames.add("name");
    notification.updatedFieldNames.add("occupation");
    notification.updatedFieldNames.add("nationality");
    this.createComponentProxy(vertx).socialNotificationProfileUpdate(userId, notification)
        .onComplete(testContext.succeeding(any -> testContext.completeNow()));

  }

  /**
   * Should so a social shuffle.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldSocialShuffle(final Vertx vertx, final VertxTestContext testContext) {

    final var userIds = new UserIdsTest().createModelExample(0);

    testContext.assertComplete(this.createComponentProxy(vertx).socialShuffle(userIds))
        .onSuccess(updated -> testContext.verify(() -> {

          assertThat(updated).isNotNull().isEqualTo(userIds);
          testContext.completeNow();

        }));

  }

}
