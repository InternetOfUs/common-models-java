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
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
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

package eu.internetofus.common.components.profile_manager;

import static eu.internetofus.common.components.MergesTest.assertCanMerge;
import static eu.internetofus.common.components.MergesTest.assertCannotMerge;
import static eu.internetofus.common.components.ValidationsTest.assertIsNotValid;
import static eu.internetofus.common.components.ValidationsTest.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ModelTestCase;
import eu.internetofus.common.components.ValidationsTest;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link PlannedActivity}.
 *
 * @see PlannedActivity
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class PlannedActivityTest extends ModelTestCase<PlannedActivity> {

  /**
   * The profile manager mocked server.
   */
  protected static WeNetProfileManagerMocker mocker;

  /**
   * Start the mocker server.
   */
  @BeforeAll
  public static void startMocker() {

    mocker = WeNetProfileManagerMocker.start();
  }

  /**
   * Register the necessary services before to test.
   *
   * @param vertx event bus to register the necessary services.
   */
  @BeforeEach
  public void registerServices(final Vertx vertx) {

    final WebClient client = WebClient.create(vertx);
    final JsonObject conf = mocker.getComponentConfiguration();
    WeNetProfileManager.register(vertx, client, conf);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PlannedActivity createModelExample(final int index) {

    final PlannedActivity activity = new PlannedActivity();
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
   * @param creation    handler to manage the created planned activity.
   */
  public void createModelExample(final int index, final Vertx vertx, final VertxTestContext testContext, final Handler<AsyncResult<PlannedActivity>> creation) {

    this.createNewEmptyProfile(vertx, testContext.succeeding(profile -> {

      final PlannedActivity activity = this.createModelExample(index);
      activity.attendees = new ArrayList<>();
      activity.attendees.add(profile.id);
      creation.handle(Future.succeededFuture(activity));

    }));

  }

  /**
   * Create a new empty user profile. It has to be stored into the repository.
   *
   * @param vertx    event bus to use.
   * @param creation handler to manage the created user profile.
   */
  protected void createNewEmptyProfile(final Vertx vertx, final Handler<AsyncResult<WeNetUserProfile>> creation) {

    WeNetProfileManager.createProxy(vertx).createProfile(new JsonObject(), (creationResult) -> {

      if (creationResult.failed()) {

        creation.handle(Future.failedFuture(creationResult.cause()));

      } else {

        final WeNetUserProfile profile = Model.fromJsonObject(creationResult.result(), WeNetUserProfile.class);
        if (profile == null) {

          creation.handle(Future.failedFuture("Can not obtain a profile form the JSON result"));

        } else {

          creation.handle(Future.succeededFuture(profile));
        }

      }
    });
  }

  /**
   * Check that an empty model is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#validate(String, Vertx)
   */
  @Test
  public void shouldEmptyModelBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final PlannedActivity model = new PlannedActivity();
    assertIsValid(model, vertx, testContext);

  }

  /**
   * Check that the {@link #createModelExample(int)} is valid.
   *
   * @param index       to verify
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final PlannedActivity model = this.createModelExample(index);
    assertIsValid(model, vertx, testContext);

  }

  /**
   * Check that the {@link #createModelExample(int, Vertx, VertxTestContext, Handler)} is valid.
   *
   * @param index       to verify
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleFromRepositoryBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(index, vertx, testContext, testContext.succeeding(model -> {

      model.id = " ";
      final String originalStartTime = model.startTime;
      model.startTime = " " + originalStartTime + " ";
      final String originalEndTime = model.endTime;
      model.endTime = " " + originalEndTime + " ";
      final String originalDescription = model.description;
      model.description = " " + originalDescription + " ";
      final List<String> originalAttendees = new ArrayList<>(model.attendees);
      model.attendees.add(0, "");
      model.attendees.add(null);
      model.attendees.add(" ");

      assertIsValid(model, vertx, testContext, () -> {

        assertThat(model.id).isNotNull().isNotEqualTo(" ");
        assertThat(model.startTime).isEqualTo(originalStartTime);
        assertThat(model.endTime).isEqualTo(originalEndTime);
        assertThat(model.description).isEqualTo(originalDescription);
        assertThat(model.attendees).isEqualTo(originalAttendees);
      });
    }));

  }

  /**
   * Check that the model with id is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#validate(String,Vertx)
   */
  @Test
  public void shouldNotBeValidWithAnId(final Vertx vertx, final VertxTestContext testContext) {

    final PlannedActivity model = new PlannedActivity();
    model.id = "has_id";
    assertIsNotValid(model, "id", vertx, testContext);

  }

  /**
   * Check that not accept planned activity with bad start time.
   *
   * @param badTime     a bad time value.
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#validate(String,Vertx)
   */
  @ParameterizedTest(name = "Should not be valid with startTime = {0}")
  @ValueSource(strings = { "0", "tomorrow", "2019-23-10", "10:00", "2019-02-30T00:00:00Z" })
  public void shouldNotBeValidWithABadStartTime(final String badTime, final Vertx vertx, final VertxTestContext testContext) {

    final PlannedActivity model = new PlannedActivity();
    model.startTime = badTime;
    assertIsNotValid(model, "startTime", vertx, testContext);

  }

  /**
   * Check that not accept planned activity with bad end time.
   *
   * @param badTime     a bad time value.
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#validate(String,Vertx)
   */
  @ParameterizedTest(name = "Should not be valid with endTime = {0}")
  @ValueSource(strings = { "0", "tomorrow", "2019-23-10", "10:00", "2019-02-30T00:00:00Z" })
  public void shouldNotBeValidWithABadEndTime(final String badTime, final Vertx vertx, final VertxTestContext testContext) {

    final PlannedActivity model = new PlannedActivity();
    model.endTime = badTime;
    assertIsNotValid(model, "endTime", vertx, testContext);

  }

  /**
   * Check that not accept planned activity with bad description.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#validate(String,Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadDescription(final Vertx vertx, final VertxTestContext testContext) {

    final PlannedActivity model = new PlannedActivity();
    model.description = ValidationsTest.STRING_256;
    assertIsNotValid(model, "description", vertx, testContext);

  }

  /**
   * Check that not accept planned activity with bad attender.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#validate(String,Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadAttender(final Vertx vertx, final VertxTestContext testContext) {

    final PlannedActivity model = new PlannedActivity();
    model.attendees = new ArrayList<>();
    model.attendees.add("undefined attendee identifier");
    assertIsNotValid(model, "attendees[0]", vertx, testContext);

  }

  /**
   * Check that is valid without attenders.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#validate(String,Vertx)
   */
  @Test
  public void shouldBeValidEmptyAttender(final Vertx vertx, final VertxTestContext testContext) {

    final PlannedActivity model = new PlannedActivity();
    model.attendees = new ArrayList<>();
    assertIsValid(model, vertx, testContext);

  }

  /**
   * Check is valid with some attenders.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#validate(String,Vertx)
   */
  @Test
  public void shouldBeValidWithSomeAttenders(final Vertx vertx, final VertxTestContext testContext) {

    this.createNewEmptyProfile(vertx, testContext.succeeding(stored -> {

      this.createNewEmptyProfile(vertx, testContext.succeeding(stored2 -> {

        final PlannedActivity model = new PlannedActivity();
        model.attendees = new ArrayList<>();
        model.attendees.add(stored.id);
        model.attendees.add(stored2.id);
        assertIsValid(model, vertx, testContext);

      }));

    }));

  }

  /**
   * Check is not valid is one attender is duplicated.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#validate(String,Vertx)
   */
  @Test
  public void shouldNotBeValidWithDuplicatedAttenders(final Vertx vertx, final VertxTestContext testContext) {

    this.createNewEmptyProfile(vertx, testContext.succeeding(stored -> {

      this.createNewEmptyProfile(vertx, testContext.succeeding(stored2 -> {

        final PlannedActivity model = new PlannedActivity();
        model.attendees = new ArrayList<>();
        model.attendees.add(stored.id);
        model.attendees.add(stored2.id);
        model.attendees.add(stored.id);
        assertIsNotValid(model, "attendees[2]", vertx, testContext);

      }));

    }));

  }

  /**
   * Check that not accept planned activity with bad attender.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#validate(String,Vertx)
   */
  @Test
  public void shouldEmptyAttenderWillRemoved(final Vertx vertx, final VertxTestContext testContext) {

    final PlannedActivity model = new PlannedActivity();
    model.attendees = new ArrayList<>();
    model.attendees.add(null);
    model.attendees.add("");
    model.attendees.add("      ");

    assertIsValid(model, vertx, testContext, () -> {

      final PlannedActivity expected = new PlannedActivity();
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
   * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
   */
  @ParameterizedTest(name = "Should not be valid with startTime = {0}")
  @ValueSource(strings = { "0", "tomorrow", "2019-23-10", "10:00", "2019-02-30T00:00:00Z" })
  public void shouldNotMergeWithABadStartTime(final String badTime, final Vertx vertx, final VertxTestContext testContext) {

    final PlannedActivity target = this.createModelExample(1);
    final PlannedActivity source = new PlannedActivity();
    source.startTime = badTime;
    assertCannotMerge(target, source, "startTime", vertx, testContext);

  }

  /**
   * Check that not merge planned activity with bad end time.
   *
   * @param badTime     a bad time value.
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
   */
  @ParameterizedTest(name = "Should not be valid with endTime = {0}")
  @ValueSource(strings = { "0", "tomorrow", "2019-23-10", "10:00", "2019-02-30T00:00:00Z" })
  public void shouldNotMergeWithABadEndTime(final String badTime, final Vertx vertx, final VertxTestContext testContext) {

    final PlannedActivity target = this.createModelExample(1);
    final PlannedActivity source = new PlannedActivity();
    source.endTime = badTime;
    assertCannotMerge(target, source, "endTime", vertx, testContext);

  }

  /**
   * Check that not merge planned activity with bad description.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithABadDescription(final Vertx vertx, final VertxTestContext testContext) {

    final PlannedActivity target = this.createModelExample(1);
    final PlannedActivity source = new PlannedActivity();
    source.description = ValidationsTest.STRING_256;
    assertCannotMerge(target, source, "description", vertx, testContext);

  }

  /**
   * Check that not merge planned activity with bad attender.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithABadAttender(final Vertx vertx, final VertxTestContext testContext) {

    final PlannedActivity target = this.createModelExample(1);
    final PlannedActivity source = new PlannedActivity();
    source.attendees = new ArrayList<>();
    source.attendees.add("undefined attendee identifier");
    assertCannotMerge(target, source, "attendees[0]", vertx, testContext);

  }

  /**
   * Check that merge without attenders.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
   */
  @Test
  public void shouldMergeEmptyAttender(final Vertx vertx, final VertxTestContext testContext) {

    final PlannedActivity target = this.createModelExample(1);
    final PlannedActivity source = new PlannedActivity();
    source.attendees = new ArrayList<>();
    assertCanMerge(target, source, vertx, testContext, merged -> assertThat(merged.attendees).isEmpty());

  }

  /**
   * Check merge with some attenders.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
   */
  @Test
  public void shouldMergeWithSomeAttenders(final Vertx vertx, final VertxTestContext testContext) {

    this.createNewEmptyProfile(vertx, testContext.succeeding(stored -> {

      this.createNewEmptyProfile(vertx, testContext.succeeding(stored2 -> {

        final PlannedActivity target = this.createModelExample(1);
        final PlannedActivity source = new PlannedActivity();
        source.attendees = new ArrayList<>();
        source.attendees.add(stored.id);
        source.attendees.add(stored2.id);
        assertCanMerge(target, source, vertx, testContext);

      }));

    }));

  }

  /**
   * Check is not valid is one attender is duplicated.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithDuplicatedAttenders(final Vertx vertx, final VertxTestContext testContext) {

    this.createNewEmptyProfile(vertx, testContext.succeeding(stored -> {

      this.createNewEmptyProfile(vertx, testContext.succeeding(stored2 -> {

        final PlannedActivity target = this.createModelExample(1);
        final PlannedActivity source = new PlannedActivity();
        source.attendees = new ArrayList<>();
        source.attendees.add(stored.id);
        source.attendees.add(stored2.id);
        source.attendees.add(stored.id);
        assertCannotMerge(target, source, "attendees[2]", vertx, testContext);

      }));

    }));

  }

  /**
   * Check that not accept planned activity with bad attender.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
   */
  @Test
  public void shouldMergeEmptyAttenderWillRemoved(final Vertx vertx, final VertxTestContext testContext) {

    final PlannedActivity target = this.createModelExample(1);
    final PlannedActivity source = new PlannedActivity();
    source.attendees = new ArrayList<>();
    source.attendees.add(null);
    source.attendees.add("");
    source.attendees.add("      ");
    assertCanMerge(target, source, vertx, testContext, merged -> assertThat(merged.attendees).isNotNull().isEmpty());

  }

  /**
   * Check that merge two models.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
   */
  @Test
  public void shouldMerge(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      this.createModelExample(1, vertx, testContext, testContext.succeeding(source -> {

        target.id = "1";
        assertCanMerge(target, source, vertx, testContext, merged -> {

          assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
          source.id = "1";
          assertThat(merged).isEqualTo(source);

        });
      }));

    }));

  }

  /**
   * Check that merge with {@code null} source.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
   */
  @Test
  public void shouldMergeWithNull(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      assertCanMerge(target, null, vertx, testContext, merged -> {

        assertThat(merged).isSameAs(target);
        testContext.completeNow();

      });

    }));

  }

  /**
   * Check that merge only start time.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyStartTime(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {
      target.id = "1";
      final PlannedActivity source = new PlannedActivity();
      source.startTime = "2000-02-19T16:18:00Z";
      assertCanMerge(target, source, vertx, testContext, merged -> {
        assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
        target.startTime = "2000-02-19T16:18:00Z";
        assertThat(merged).isEqualTo(target);
        testContext.completeNow();
      });
    }));

  }

  /**
   * Check that merge only end time.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyEndTime(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      target.id = "1";
      final PlannedActivity source = new PlannedActivity();
      source.endTime = "2020-02-19T16:18:00Z";
      assertCanMerge(target, source, vertx, testContext, merged -> {
        assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
        target.endTime = "2020-02-19T16:18:00Z";
        assertThat(merged).isEqualTo(target);
        testContext.completeNow();
      });
    }));

  }

  /**
   * Check that merge only start time.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyDescription(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {
      target.id = "1";
      final PlannedActivity source = new PlannedActivity();
      source.description = "New description";
      assertCanMerge(target, source, vertx, testContext, merged -> {
        assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
        target.description = "New description";
        assertThat(merged).isEqualTo(target);
        testContext.completeNow();
      });
    }));

  }

  /**
   * Check that merge only start time.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyStatus(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {
      target.id = "1";
      final PlannedActivity source = new PlannedActivity();
      source.status = PlannedActivityStatus.tentative;
      assertCanMerge(target, source, vertx, testContext, merged -> {
        assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
        target.status = PlannedActivityStatus.tentative;
        assertThat(merged).isEqualTo(target);
        testContext.completeNow();
      });
    }));

  }

  /**
   * Check that merge only start time.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
   */
  @Test
  public void shouldMergeRemoveAttenders(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {
      target.id = "1";
      final PlannedActivity source = new PlannedActivity();
      source.attendees = new ArrayList<>();
      assertCanMerge(target, source, vertx, testContext, merged -> {
        assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
        target.attendees.clear();
        assertThat(merged).isEqualTo(target);
        testContext.completeNow();
      });
    }));

  }

  /**
   * Check that merge only start time.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see PlannedActivity#merge(PlannedActivity, String, Vertx)
   */
  @Test
  public void shouldMergeAddNewAttenders(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {
      target.id = "1";
      this.createNewEmptyProfile(vertx, testContext.succeeding(stored -> {

        final PlannedActivity source = new PlannedActivity();
        source.attendees = new ArrayList<>();
        source.attendees.add(stored.id);
        source.attendees.addAll(target.attendees);
        assertCanMerge(target, source, vertx, testContext, merged -> {

          assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
          target.attendees.add(0, stored.id);
          assertThat(merged).isEqualTo(target);
          testContext.completeNow();
        });
      }));
    }));
  }

}
