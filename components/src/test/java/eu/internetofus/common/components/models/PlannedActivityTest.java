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

package eu.internetofus.common.components.models;

import static eu.internetofus.common.model.MergeAsserts.assertCanMerge;
import static eu.internetofus.common.model.MergeAsserts.assertCannotMerge;
import static eu.internetofus.common.model.UpdateAsserts.assertCanUpdate;
import static eu.internetofus.common.model.UpdateAsserts.assertCannotUpdate;
import static eu.internetofus.common.model.ValidableAsserts.assertIsNotValid;
import static eu.internetofus.common.model.ValidableAsserts.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.WeNetIntegrationExtension;
import eu.internetofus.common.components.WeNetValidateContext;
import eu.internetofus.common.model.ModelTestCase;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test the {@link PlannedActivity}.
 *
 * @see PlannedActivity
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetIntegrationExtension.class)
public class PlannedActivityTest extends ModelTestCase<PlannedActivity> {

  /**
   * {@inheritDoc}
   */
  @Override
  public PlannedActivity createModelExample(final int index) {

    final var activity = new PlannedActivity();
    activity.id = null;
    activity.startTime = "2017-07-21T17:32:0" + index % 10 + "Z";
    activity.endTime = "2019-07-21T17:32:2" + index % 10 + "Z";
    activity.description = "description_" + index;
    activity.attendees = null;
    activity.status = PlannedActivityStatus.cancelled;
    return activity;
  }

  /**
   * Create an example model that has the specified index.
   *
   * @param index       to use in the example.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @return the created planned activity.
   */
  public Future<PlannedActivity> createModelExample(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    return testContext
        .assertComplete(StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).compose(profile -> {

          final var activity = this.createModelExample(index);
          activity.attendees = new ArrayList<>();
          activity.attendees.add(profile.id);
          return Future.succeededFuture(activity);

        }));

  }

  /**
   * Check that an empty model is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#validate(WeNetValidateContext)
   */
  @Test
  public void shouldEmptyModelBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new PlannedActivity();
    assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that the {@link #createModelExample(int)} is valid.
   *
   * @param index       to verify
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#validate(WeNetValidateContext)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(index);
    assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that the {@link #createModelExample(int, Vertx, VertxTestContext)} is
   * valid.
   *
   * @param index       to verify
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#validate(WeNetValidateContext)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleFromRepositoryBeValid(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    this.createModelExample(index, vertx, testContext).onSuccess(model -> {

      model.id = null;
      final var originalStartTime = model.startTime;
      model.startTime = " " + originalStartTime + " ";
      final var originalEndTime = model.endTime;
      model.endTime = " " + originalEndTime + " ";
      final var originalDescription = model.description;
      model.description = " " + originalDescription + " ";
      final List<String> originalAttendees = new ArrayList<>(model.attendees);
      model.attendees.add(0, null);
      model.attendees.add(null);

      assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

        assertThat(model.id).isNotNull();
        assertThat(model.startTime).isEqualTo(originalStartTime);
        assertThat(model.endTime).isEqualTo(originalEndTime);
        assertThat(model.description).isEqualTo(originalDescription);
        assertThat(model.attendees).isEqualTo(originalAttendees);
      });
    });

  }

  /**
   * Check that the model with id is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#validate(WeNetValidateContext)
   */
  @Test
  public void shouldBeValidWithAnId(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new PlannedActivity();
    model.id = "has_id";
    assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that not accept planned activity with bad start time.
   *
   * @param badTime     a bad time value.
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#validate(WeNetValidateContext)
   */
  @ParameterizedTest(name = "Should not be valid with startTime = {0}")
  @ValueSource(strings = { "0", "tomorrow", "2019-23-10", "10:00", "2019-02-30T00:00:00Z" })
  public void shouldNotBeValidWithABadStartTime(final String badTime, final Vertx vertx,
      final VertxTestContext testContext) {

    final var model = new PlannedActivity();
    model.startTime = badTime;
    assertIsNotValid(model, "startTime", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that not accept planned activity with bad end time.
   *
   * @param badTime     a bad time value.
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#validate(WeNetValidateContext)
   */
  @ParameterizedTest(name = "Should not be valid with endTime = {0}")
  @ValueSource(strings = { "0", "tomorrow", "2019-23-10", "10:00", "2019-02-30T00:00:00Z" })
  public void shouldNotBeValidWithABadEndTime(final String badTime, final Vertx vertx,
      final VertxTestContext testContext) {

    final var model = new PlannedActivity();
    model.endTime = badTime;
    assertIsNotValid(model, "endTime", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that not accept planned activity with bad attender.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithABadAttender(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new PlannedActivity();
    model.attendees = new ArrayList<>();
    model.attendees.add("undefined attendee identifier");
    assertIsNotValid(model, "attendees[0]", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that is valid without attenders.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#validate(WeNetValidateContext)
   */
  @Test
  public void shouldBeValidEmptyAttender(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new PlannedActivity();
    model.attendees = new ArrayList<>();
    assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check is valid with some attenders.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#validate(WeNetValidateContext)
   */
  @Test
  public void shouldBeValidWithSomeAttenders(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(stored -> {

      StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(stored2 -> {

        final var model = new PlannedActivity();
        model.attendees = new ArrayList<>();
        model.attendees.add(stored.id);
        model.attendees.add(stored2.id);
        assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext);

      });

    });

  }

  /**
   * Check is not valid is one attender is duplicated.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithDuplicatedAttenders(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(stored -> {

      StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(stored2 -> {

        final var model = new PlannedActivity();
        model.attendees = new ArrayList<>();
        model.attendees.add(stored.id);
        model.attendees.add(stored2.id);
        model.attendees.add(stored.id);
        assertIsNotValid(model, "attendees[2]", new WeNetValidateContext("codePrefix", vertx), testContext);

      });

    });

  }

  /**
   * Check that not accept planned activity with bad attender.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#validate(WeNetValidateContext)
   */
  @Test
  public void shouldEmptyAttenderWillRemoved(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new PlannedActivity();
    model.attendees = new ArrayList<>();
    model.attendees.add(null);
    model.attendees.add(null);
    model.attendees.add(null);

    assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext, () -> {

      final var expected = new PlannedActivity();
      expected.id = model.id;
      expected.attendees = new ArrayList<>();
      assertThat(model).isEqualTo(expected);
    });

  }

  /**
   * Check that not merge planned activity with bad start time.
   *
   * @param badTime     a bad time value.
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#merge(PlannedActivity, WeNetValidateContext)
   */
  @ParameterizedTest(name = "Should not be valid with startTime = {0}")
  @ValueSource(strings = { "0", "tomorrow", "2019-23-10", "10:00", "2019-02-30T00:00:00Z" })
  public void shouldNotMergeWithABadStartTime(final String badTime, final Vertx vertx,
      final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new PlannedActivity();
    source.startTime = badTime;
    assertCannotMerge(target, source, "startTime", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that not merge planned activity with bad end time.
   *
   * @param badTime     a bad time value.
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#merge(PlannedActivity, WeNetValidateContext)
   */
  @ParameterizedTest(name = "Should not be valid with endTime = {0}")
  @ValueSource(strings = { "0", "tomorrow", "2019-23-10", "10:00", "2019-02-30T00:00:00Z" })
  public void shouldNotMergeWithABadEndTime(final String badTime, final Vertx vertx,
      final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new PlannedActivity();
    source.endTime = badTime;
    assertCannotMerge(target, source, "endTime", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that not merge planned activity with bad attender.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#merge(PlannedActivity, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithABadAttender(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new PlannedActivity();
    source.attendees = new ArrayList<>();
    source.attendees.add("undefined attendee identifier");
    assertCannotMerge(target, source, "attendees[0]", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that merge without attenders.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#merge(PlannedActivity, WeNetValidateContext)
   */
  @Test
  public void shouldMergeEmptyAttender(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new PlannedActivity();
    source.attendees = new ArrayList<>();
    assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext,
        merged -> assertThat(merged.attendees).isEmpty());

  }

  /**
   * Check merge with some attenders.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#merge(PlannedActivity, WeNetValidateContext)
   */
  @Test
  public void shouldMergeWithSomeAttenders(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(stored -> {

      StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(stored2 -> {

        final var target = this.createModelExample(1);
        final var source = new PlannedActivity();
        source.attendees = new ArrayList<>();
        source.attendees.add(stored.id);
        source.attendees.add(stored2.id);
        assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext);

      });

    });

  }

  /**
   * Check is not valid is one attender is duplicated.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#merge(PlannedActivity, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithDuplicatedAttenders(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(stored -> {

      StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(stored2 -> {

        final var target = this.createModelExample(1);
        final var source = new PlannedActivity();
        source.attendees = new ArrayList<>();
        source.attendees.add(stored.id);
        source.attendees.add(stored2.id);
        source.attendees.add(stored.id);
        assertCannotMerge(target, source, "attendees[2]", new WeNetValidateContext("codePrefix", vertx), testContext);

      });

    });

  }

  /**
   * Check that not accept planned activity with bad attender.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#merge(PlannedActivity, WeNetValidateContext)
   */
  @Test
  public void shouldMergeEmptyAttenderWillRemoved(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new PlannedActivity();
    source.attendees = new ArrayList<>();
    source.attendees.add(null);
    source.attendees.add(null);
    source.attendees.add(null);
    assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext,
        merged -> assertThat(merged.attendees).isNotNull().isEmpty());

  }

  /**
   * Check that merge two models.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#merge(PlannedActivity, WeNetValidateContext)
   */
  @Test
  public void shouldMerge(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      this.createModelExample(1, vertx, testContext).onSuccess(source -> {

        target.id = "1";
        assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

          assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
          source.id = "1";
          assertThat(merged).isEqualTo(source);

        });
      });
    });

  }

  /**
   * Check that merge with {@code null} source.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#merge(PlannedActivity, WeNetValidateContext)
   */
  @Test
  public void shouldMergeWithNull(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      assertCanMerge(target, null, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

        assertThat(merged).isSameAs(target);
        testContext.completeNow();

      });

    });

  }

  /**
   * Check that merge only start time.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#merge(PlannedActivity, WeNetValidateContext)
   */
  @Test
  public void shouldMergeOnlyStartTime(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      target.id = "1";
      final var source = new PlannedActivity();
      source.startTime = "2000-02-19T16:18:00Z";
      assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {
        assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
        target.startTime = "2000-02-19T16:18:00Z";
        assertThat(merged).isEqualTo(target);
        testContext.completeNow();
      });
    });

  }

  /**
   * Check that merge only end time.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#merge(PlannedActivity, WeNetValidateContext)
   */
  @Test
  public void shouldMergeOnlyEndTime(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      target.id = "1";
      final var source = new PlannedActivity();
      source.endTime = "2020-02-19T16:18:00Z";
      assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {
        assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
        target.endTime = "2020-02-19T16:18:00Z";
        assertThat(merged).isEqualTo(target);
        testContext.completeNow();
      });
    });

  }

  /**
   * Check that merge only start time.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#merge(PlannedActivity, WeNetValidateContext)
   */
  @Test
  public void shouldMergeOnlyDescription(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      target.id = "1";
      final var source = new PlannedActivity();
      source.description = "New description";
      assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {
        assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
        target.description = "New description";
        assertThat(merged).isEqualTo(target);
        testContext.completeNow();
      });
    });

  }

  /**
   * Check that merge only start time.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#merge(PlannedActivity, WeNetValidateContext)
   */
  @Test
  public void shouldMergeOnlyStatus(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      target.id = "1";
      final var source = new PlannedActivity();
      source.status = PlannedActivityStatus.tentative;
      assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {
        assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
        target.status = PlannedActivityStatus.tentative;
        assertThat(merged).isEqualTo(target);
        testContext.completeNow();
      });
    });

  }

  /**
   * Check that merge only start time.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#merge(PlannedActivity, WeNetValidateContext)
   */
  @Test
  public void shouldMergeRemoveAttenders(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      target.id = "1";
      final var source = new PlannedActivity();
      source.attendees = new ArrayList<>();
      assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {
        assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
        target.attendees.clear();
        assertThat(merged).isEqualTo(target);
        testContext.completeNow();
      });
    });

  }

  /**
   * Check that merge only start time.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#merge(PlannedActivity, WeNetValidateContext)
   */
  @Test
  public void shouldMergeAddNewAttenders(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      target.id = "1";
      StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(stored -> {

        final var source = new PlannedActivity();
        source.attendees = new ArrayList<>();
        source.attendees.add(stored.id);
        source.attendees.addAll(target.attendees);
        assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

          assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
          target.attendees.add(0, stored.id);
          assertThat(merged).isEqualTo(target);
          testContext.completeNow();
        });
      });
    });
  }

  /**
   * Check that not update planned activity with bad start time.
   *
   * @param badTime     a bad time value.
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#update(PlannedActivity, WeNetValidateContext)
   */
  @ParameterizedTest(name = "Should not be valid with startTime = {0}")
  @ValueSource(strings = { "0", "tomorrow", "2019-23-10", "10:00", "2019-02-30T00:00:00Z" })
  public void shouldNotUpdateWithABadStartTime(final String badTime, final Vertx vertx,
      final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new PlannedActivity();
    source.startTime = badTime;
    assertCannotUpdate(target, source, "startTime", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that not update planned activity with bad end time.
   *
   * @param badTime     a bad time value.
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#update(PlannedActivity, WeNetValidateContext)
   */
  @ParameterizedTest(name = "Should not be valid with endTime = {0}")
  @ValueSource(strings = { "0", "tomorrow", "2019-23-10", "10:00", "2019-02-30T00:00:00Z" })
  public void shouldNotUpdateWithABadEndTime(final String badTime, final Vertx vertx,
      final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new PlannedActivity();
    source.endTime = badTime;
    assertCannotUpdate(target, source, "endTime", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that not update planned activity with bad attender.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#update(PlannedActivity, WeNetValidateContext)
   */
  @Test
  public void shouldNotUpdateWithABadAttender(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new PlannedActivity();
    source.attendees = new ArrayList<>();
    source.attendees.add("undefined attendee identifier");
    assertCannotUpdate(target, source, "attendees[0]", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that update without attenders.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#update(PlannedActivity, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateEmptyAttender(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new PlannedActivity();
    source.attendees = new ArrayList<>();
    assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext,
        updated -> assertThat(updated.attendees).isEmpty());

  }

  /**
   * Check update with some attenders.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#update(PlannedActivity, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateWithSomeAttenders(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(stored -> {

      StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(stored2 -> {

        final var target = this.createModelExample(1);
        final var source = new PlannedActivity();
        source.attendees = new ArrayList<>();
        source.attendees.add(stored.id);
        source.attendees.add(stored2.id);
        assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext);

      });

    });

  }

  /**
   * Check is not valid is one attender is duplicated.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#update(PlannedActivity, WeNetValidateContext)
   */
  @Test
  public void shouldNotUpdateWithDuplicatedAttenders(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(stored -> {

      StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(stored2 -> {

        final var target = this.createModelExample(1);
        final var source = new PlannedActivity();
        source.attendees = new ArrayList<>();
        source.attendees.add(stored.id);
        source.attendees.add(stored2.id);
        source.attendees.add(stored.id);
        assertCannotUpdate(target, source, "attendees[2]", new WeNetValidateContext("codePrefix", vertx), testContext);

      });

    });

  }

  /**
   * Check that not accept planned activity with bad attender.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#update(PlannedActivity, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateEmptyAttenderWillRemoved(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    final var source = new PlannedActivity();
    source.attendees = new ArrayList<>();
    source.attendees.add(null);
    source.attendees.add(null);
    source.attendees.add(null);
    assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext,
        updated -> assertThat(updated.attendees).isNotNull().isEmpty());

  }

  /**
   * Check that update two models.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#update(PlannedActivity, WeNetValidateContext)
   */
  @Test
  public void shouldUpdate(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      this.createModelExample(1, vertx, testContext).onSuccess(source -> {

        target.id = "1";
        assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {

          assertThat(updated).isNotEqualTo(target).isNotEqualTo(source);
          source.id = "1";
          assertThat(updated).isEqualTo(source);

        });
      });
    });

  }

  /**
   * Check that update with {@code null} source.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#update(PlannedActivity, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateWithNull(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      assertCanUpdate(target, null, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {

        assertThat(updated).isSameAs(target);
        testContext.completeNow();

      });

    });

  }

  /**
   * Check that update only start time.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#update(PlannedActivity, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateOnlyStartTime(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      target.id = "1";
      final var source = new PlannedActivity();
      source.startTime = "2000-02-19T16:18:00Z";
      assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {
        assertThat(updated).isNotEqualTo(target).isNotEqualTo(source);
        source.id = target.id;
        assertThat(updated).isEqualTo(source);
        testContext.completeNow();
      });
    });

  }

  /**
   * Check that update only end time.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#update(PlannedActivity, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateOnlyEndTime(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      target.id = "1";
      final var source = new PlannedActivity();
      source.endTime = "2020-02-19T16:18:00Z";
      assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {
        assertThat(updated).isNotEqualTo(target).isNotEqualTo(source);
        source.id = target.id;
        assertThat(updated).isEqualTo(source);
        testContext.completeNow();
      });
    });

  }

  /**
   * Check that update only start time.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#update(PlannedActivity, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateOnlyDescription(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      target.id = "1";
      final var source = new PlannedActivity();
      source.description = "New description";
      assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {
        assertThat(updated).isNotEqualTo(target).isNotEqualTo(source);
        source.id = target.id;
        assertThat(updated).isEqualTo(source);
        testContext.completeNow();
      });
    });

  }

  /**
   * Check that update only start time.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#update(PlannedActivity, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateOnlyStatus(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      target.id = "1";
      final var source = new PlannedActivity();
      source.status = PlannedActivityStatus.tentative;
      assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {
        assertThat(updated).isNotEqualTo(target).isNotEqualTo(source);
        source.id = target.id;
        assertThat(updated).isEqualTo(source);
        testContext.completeNow();
      });
    });

  }

  /**
   * Check that update only start time.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#update(PlannedActivity, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateRemoveAttenders(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      target.id = "1";
      final var source = new PlannedActivity();
      source.attendees = new ArrayList<>();
      assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {
        assertThat(updated).isNotEqualTo(target).isNotEqualTo(source);
        source.id = target.id;
        assertThat(updated).isEqualTo(source);
        testContext.completeNow();
      });
    });

  }

  /**
   * Check that update only start time.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#update(PlannedActivity, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateAddNewAttenders(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      target.id = "1";
      StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(stored -> {

        final var source = new PlannedActivity();
        source.attendees = new ArrayList<>();
        source.attendees.add(stored.id);
        source.attendees.addAll(target.attendees);
        assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {

          assertThat(updated).isNotEqualTo(target).isNotEqualTo(source);
          source.id = target.id;
          assertThat(updated).isEqualTo(source);
          testContext.completeNow();

        });
      });
    });
  }

}
