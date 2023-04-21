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

package eu.internetofus.common.components.profile_manager;

import static eu.internetofus.common.model.ValidableAsserts.assertIsNotValid;
import static eu.internetofus.common.model.ValidableAsserts.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.WeNetIntegrationExtension;
import eu.internetofus.common.components.WeNetValidateContext;
import eu.internetofus.common.components.models.SocialNetworkRelationshipType;
import eu.internetofus.common.components.models.WeNetUserProfile;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import eu.internetofus.common.model.ModelTestCase;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test the {@link UserPerformanceRatingEvent}
 *
 * @see UserPerformanceRatingEvent
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetIntegrationExtension.class)
public class UserPerformanceRatingEventTest extends ModelTestCase<UserPerformanceRatingEvent> {

  /**
   * {@inheritDoc}
   */
  @Override
  public UserPerformanceRatingEvent createModelExample(final int index) {

    final var model = new UserPerformanceRatingEvent();
    model.sourceId = "SourceId_" + index;
    model.targetId = "TargetId_" + index;
    model.communityId = "CommunityId_" + index;
    model.taskTypeId = "TaskTypeId_" + index;
    model.taskId = "TaskId_" + index;
    model.reportTime = index;
    model.rating = 1.0 / Math.max(1, index + 2);
    return model;
  }

  /**
   * Create an example model that has the specified index.
   *
   * @param index       to use in the example.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @return the future created model.
   */
  public Future<UserPerformanceRatingEvent> createModelExample(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    return testContext.assertComplete(

        StoreServices.storeTaskExample(index, vertx, testContext).compose(task ->

        StoreServices.storeSocialNetworkRelationshipExample(index, vertx, testContext).compose(relationship -> {

          task.appId = relationship.appId;
          return testContext.assertComplete(WeNetTaskManager.createProxy(vertx).updateTask(task.id, task))
              .transform(any -> {

                final var model = new UserPerformanceRatingEvent();
                model.sourceId = relationship.sourceId;
                model.targetId = relationship.targetId;
                model.relationship = relationship.type;
                model.appId = relationship.appId;
                model.communityId = task.communityId;
                model.taskTypeId = task.taskTypeId;
                model.taskId = task.id;
                model.rating = 1.0 / Math.max(1, index + 2);
                return Future.succeededFuture(model);

              });

        })));

  }

  /**
   * Check that an empty event is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserPerformanceRatingEvent#validate(WeNetValidateContext)
   */
  @Test
  public void shouldEmptyEventNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    assertIsNotValid(new UserPerformanceRatingEvent(), "sourceId", new WeNetValidateContext("codePrefix", vertx),
        testContext);

  }

  /**
   * Check that a {@link #createModelExample(int)} is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserPerformanceRatingEvent#validate(WeNetValidateContext)
   */
  @Test
  public void shouldBasicExampleNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final var event = this.createModelExample(1);
    assertIsNotValid(event, "sourceId", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that an event with source, target and rating be valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserPerformanceRatingEvent#validate(WeNetValidateContext)
   */
  @Test
  public void shouldEventWithSourceTargetAndRatingBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(created -> {

      final var model = new UserPerformanceRatingEvent();
      model.sourceId = created.sourceId;
      model.targetId = created.targetId;
      model.rating = created.rating;
      assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

        assertThat(model.sourceId).isEqualTo(created.sourceId);
        assertThat(model.targetId).isEqualTo(created.targetId);

      });

    });

  }

  /**
   * Check that an event with source equals to target is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserPerformanceRatingEvent#validate(WeNetValidateContext)
   */
  @Test
  public void shouldEventWithSourceEqualsToTargetNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.targetId = model.sourceId;
      assertIsNotValid(model, "targetId", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that an event with source, target, rating and application identifier be
   * valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserPerformanceRatingEvent#validate(WeNetValidateContext)
   */
  @Test
  public void shouldEventWithSourceTargetRatingAndAppBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(created -> {

      final var model = new UserPerformanceRatingEvent();
      model.sourceId = created.sourceId;
      model.targetId = created.targetId;
      model.rating = created.rating;
      model.appId = created.appId;
      assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

        assertThat(model.appId).isEqualTo(created.appId);

      });

    });

  }

  /**
   * Check that an event with source, target, rating and community identifier be
   * valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserPerformanceRatingEvent#validate(WeNetValidateContext)
   */
  @Test
  public void shouldEventWithSourceTargetRatingAndCommunityBeValid(final Vertx vertx,
      final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(created -> {

      final var model = new UserPerformanceRatingEvent();
      model.sourceId = created.sourceId;
      model.targetId = created.targetId;
      model.rating = created.rating;
      model.communityId = created.communityId;
      assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

        assertThat(model.communityId).isEqualTo(created.communityId);

      });

    });

  }

  /**
   * Check that an event with source, target, rating and task type identifier be
   * valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserPerformanceRatingEvent#validate(WeNetValidateContext)
   */
  @Test
  public void shouldEventWithSourceTargetRatingAndTaskTypeBeValid(final Vertx vertx,
      final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(created -> {

      final var model = new UserPerformanceRatingEvent();
      model.sourceId = created.sourceId;
      model.targetId = created.targetId;
      model.rating = created.rating;
      model.taskTypeId = created.taskTypeId;
      assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

        assertThat(model.taskTypeId).isEqualTo(created.taskTypeId);

      });

    });

  }

  /**
   * Check that an event with source, target, rating and task identifier be valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserPerformanceRatingEvent#validate(WeNetValidateContext)
   */
  @Test
  public void shouldEventWithSourceTargetRatingAndTaskBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(created -> {

      final var model = new UserPerformanceRatingEvent();
      model.sourceId = created.sourceId;
      model.targetId = created.targetId;
      model.rating = created.rating;
      model.taskId = created.taskId;
      assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

        assertThat(model.taskId).isEqualTo(created.taskId);

      });

    });

  }

  /**
   * Check that an event with source, target, rating and relationship be valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserPerformanceRatingEvent#validate(WeNetValidateContext)
   */
  @Test
  public void shouldEventWithSourceTargetRatingAndRelationshipBeValid(final Vertx vertx,
      final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(created -> {

      final var model = new UserPerformanceRatingEvent();
      model.sourceId = created.sourceId;
      model.targetId = created.targetId;
      model.rating = created.rating;
      model.relationship = created.relationship;
      model.appId = created.appId;
      assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that the {@link #createModelExample(int, Vertx, VertxTestContext)} is
   * valid.
   *
   * @param index       to verify
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserPerformanceRatingEvent#validate(WeNetValidateContext)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(index, vertx, testContext)
        .onSuccess(model -> assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext));

  }

  /**
   * Check that a model with a bad rating is not valid.
   *
   * @param rating      that is wrong.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserPerformanceRatingEvent#validate(WeNetValidateContext)
   */
  @ParameterizedTest(name = "The event with a rating {0} has not to be valid")
  @ValueSource(doubles = { -0.0001, -0.1, 1.1, 1.000001 })
  public void shouldEventWithBadRatingNotBeValid(final double rating, final Vertx vertx,
      final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.rating = rating;
      assertIsNotValid(model, "rating", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that an event with a bad sourceId is not valid.
   *
   * @param sourceId    invalid source identifier.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserPerformanceRatingEvent#validate(WeNetValidateContext)
   */
  @ParameterizedTest(name = "The event with the sourceId {0} has not to be valid")
  @ValueSource(strings = { "a", "jbdfy17yt879o", "550e8400-e29b-41d4-a716-446655440000" })
  public void shouldEventWithBadSourceIdNotBeValid(final String sourceId, final Vertx vertx,
      final VertxTestContext testContext) {

    testContext.assertComplete(StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext))
        .onSuccess(stored -> {

          final var model = new UserPerformanceRatingEvent();
          model.sourceId = sourceId;
          model.targetId = stored.id;
          model.rating = Math.random();
          assertIsNotValid(model, "sourceId", new WeNetValidateContext("codePrefix", vertx), testContext);

        });

  }

  /**
   * Check that an event with a bad targetId is not valid.
   *
   * @param targetId    invalid target identifier.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserPerformanceRatingEvent#validate(WeNetValidateContext)
   */
  @ParameterizedTest(name = "The event with the targetId {0} has not to be valid")
  @ValueSource(strings = { "a", "jbdfy17yt879o", "550e8400-e29b-41d4-a716-446655440000" })
  public void shouldEventWithBadTargetIdNotBeValid(final String targetId, final Vertx vertx,
      final VertxTestContext testContext) {

    testContext.assertComplete(StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext))
        .onSuccess(stored -> {

          final var model = new UserPerformanceRatingEvent();
          model.sourceId = stored.id;
          model.targetId = targetId;
          model.rating = Math.random();
          assertIsNotValid(model, "targetId", new WeNetValidateContext("codePrefix", vertx), testContext);

        });

  }

  /**
   * Check that an event with a bad appId is not valid.
   *
   * @param appId       invalid application identifier.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserPerformanceRatingEvent#validate(WeNetValidateContext)
   */
  @ParameterizedTest(name = "The event with the appId {0} has not to be valid")
  @ValueSource(strings = { "a", "jbdfy17yt879o", "550e8400-e29b-41d4-a716-446655440000" })
  public void shouldEventWithBadAppIdNotBeValid(final String appId, final Vertx vertx,
      final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.appId = appId;
      assertIsNotValid(model, "appId", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that an event with a appId that is not equals to the appId of the task.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserPerformanceRatingEvent#validate(WeNetValidateContext)
   */
  @Test
  public void shouldEventWithAppIdDiferentTotehAppIdOfTheTaskNotBeValid(final Vertx vertx,
      final VertxTestContext testContext) {

    testContext.assertComplete(StoreServices.storeAppExample(2, vertx, testContext)).onSuccess(stored -> {

      testContext.assertComplete(this.createModelExample(1, vertx, testContext)).onSuccess(model -> {

        model.appId = stored.appId;
        assertIsNotValid(model, "appId", new WeNetValidateContext("codePrefix", vertx), testContext);

      });
    });

  }

  /**
   * Check that an event with a bad communityId is not valid.
   *
   * @param communityId invalid community identifier.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserPerformanceRatingEvent#validate(WeNetValidateContext)
   */
  @ParameterizedTest(name = "The event with the communityId {0} has not to be valid")
  @ValueSource(strings = { "a", "jbdfy17yt879o", "550e8400-e29b-41d4-a716-446655440000" })
  public void shouldEventWithBadCommunityIdNotBeValid(final String communityId, final Vertx vertx,
      final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.communityId = communityId;
      assertIsNotValid(model, "communityId", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that an event with a bad taskTypeId is not valid.
   *
   * @param taskTypeId  invalid task type identifier.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserPerformanceRatingEvent#validate(WeNetValidateContext)
   */
  @ParameterizedTest(name = "The event with the taskTypeId {0} has not to be valid")
  @ValueSource(strings = { "a", "jbdfy17yt879o", "550e8400-e29b-41d4-a716-446655440000" })
  public void shouldEventWithBadTaskTypeIdNotBeValid(final String taskTypeId, final Vertx vertx,
      final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.taskTypeId = taskTypeId;
      assertIsNotValid(model, "taskTypeId", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that an event with a taskTypeId that is not equals to the taskTypeId of
   * the task.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserPerformanceRatingEvent#validate(WeNetValidateContext)
   */
  @Test
  public void shouldEventWithTaskTypeIdDiferentTotehTaskTypeIdOfTheTaskNotBeValid(final Vertx vertx,
      final VertxTestContext testContext) {

    testContext.assertComplete(StoreServices.storeTaskTypeExample(2, vertx, testContext)).onSuccess(stored -> {
      this.createModelExample(1, vertx, testContext).onSuccess(model -> {

        model.taskTypeId = stored.id;
        assertIsNotValid(model, "taskTypeId", new WeNetValidateContext("codePrefix", vertx), testContext);

      });
    });

  }

  /**
   * Check that an event with a bad taskId is not valid.
   *
   * @param taskId      invalid task identifier.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserPerformanceRatingEvent#validate(WeNetValidateContext)
   */
  @ParameterizedTest(name = "The event with the taskId {0} has not to be valid")
  @ValueSource(strings = { "a", "jbdfy17yt879o", "550e8400-e29b-41d4-a716-446655440000" })
  public void shouldEventWithBadTaskIdNotBeValid(final String taskId, final Vertx vertx,
      final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.taskId = taskId;
      assertIsNotValid(model, "taskId", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that an event with a bad relationship is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see UserPerformanceRatingEvent#validate(WeNetValidateContext)
   */
  @Test
  public void shouldEventWithBadRelationshipNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.relationship = SocialNetworkRelationshipType.acquaintance;
      assertIsNotValid(model, "relationship", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

}
