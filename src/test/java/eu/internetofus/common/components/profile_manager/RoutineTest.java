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

package eu.internetofus.common.components.profile_manager;

import static eu.internetofus.common.components.MergesTest.assertCanMerge;
import static eu.internetofus.common.components.MergesTest.assertCannotMerge;
import static eu.internetofus.common.components.UpdatesTest.assertCanUpdate;
import static eu.internetofus.common.components.UpdatesTest.assertCannotUpdate;
import static eu.internetofus.common.components.ValidationsTest.assertIsNotValid;
import static eu.internetofus.common.components.ValidationsTest.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ModelTestCase;
import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.ValidationsTest;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link Routine}
 *
 * @see Routine
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class RoutineTest extends ModelTestCase<Routine> {

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

    final var client = WebClient.create(vertx);
    final var conf = mocker.getComponentConfiguration();
    WeNetProfileManager.register(vertx, client, conf);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Routine createModelExample(final int index) {

    final var model = new Routine();
    model.user_id = "user_id_" + index;
    model.weekday = "weekday_" + index;
    model.label_distribution = new JsonObject();
    model.label_distribution.put("additional_" + index, new JsonArray().add(new ScoredLabelTest().createModelExample(index).toJsonObject()).add(new ScoredLabelTest().createModelExample(index + 1).toJsonObject()));
    model.label_distribution.put("additional_" + (index + 1), new JsonArray());
    model.label_distribution.put("additional_" + (index + 2),
        new JsonArray().add(new ScoredLabelTest().createModelExample(index - 1).toJsonObject()).add(new ScoredLabelTest().createModelExample(index).toJsonObject()).add(new ScoredLabelTest().createModelExample(index + 1).toJsonObject()));
    model.confidence = 1.0 / Math.max(1.0, index + 1);
    return model;
  }

  /**
   * Check that the {@link #createModelExample(int)} is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#validate(String, io.vertx.core.Vertx)
   */
  @Test
  public void shouldExampleNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    assertIsNotValid(model, "user_id", vertx, testContext);
  }

  /**
   * Create a valid model example.
   *
   * @param index       of the example to create.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   * @param handler     to inform about the created routine.
   */
  public void createModelExample(final int index, final Vertx vertx, final VertxTestContext testContext, final Handler<AsyncResult<Routine>> handler) {

    StoreServices.storeProfileExample(index, vertx, testContext, testContext.succeeding(profile -> {

      final var model = this.createModelExample(index);
      model.user_id = profile.id;
      handler.handle(Future.succeededFuture(model));
    }));

  }

  /**
   * Check that the {@link #createModelExample(int, Vertx, VertxTestContext, Handler)} is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#validate(String, io.vertx.core.Vertx)
   */
  @Test
  public void shouldComplexExampleBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(routine -> {

      assertIsValid(routine, vertx, testContext);
    }));

  }

  /**
   * Check that a {@link Routine} without a user id is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(String, Vertx)
   */
  @Test
  public void shouldRoutineWithoutUserIdNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(model -> testContext.verify(() -> {

      model.user_id = null;
      assertIsNotValid(model, "user_id", vertx, testContext);

    })));

  }

  /**
   * Check that a {@link Routine} without an undefined user id is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(String, Vertx)
   */
  @Test
  public void shouldRoutineWithUndefinedUserIdNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(model -> testContext.verify(() -> {

      model.user_id = "Undefined user id";
      assertIsNotValid(model, "user_id", vertx, testContext);

    })));

  }

  /**
   * Check that a {@link Routine} without a weekday is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(String, Vertx)
   */
  @Test
  public void shouldRoutineWithoutWeekdaydNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(model -> testContext.verify(() -> {

      model.weekday = null;
      assertIsNotValid(model, "weekday", vertx, testContext);

    })));

  }

  /**
   * Check that a {@link Routine} with a large weekday is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(String, Vertx)
   */
  @Test
  public void shouldRoutineWithTooLargeWeekdaydNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(model -> testContext.verify(() -> {

      model.weekday = ValidationsTest.STRING_256;
      assertIsNotValid(model, "weekday", vertx, testContext);

    })));

  }

  /**
   * Check that a {@link Routine} without a confidence is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(String, Vertx)
   */
  @Test
  public void shouldRoutineWithoutConfidencedNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(model -> testContext.verify(() -> {

      model.confidence = null;
      assertIsNotValid(model, "confidence", vertx, testContext);

    })));

  }

  /**
   * Check that a {@link Routine} without a label_distribution is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(String, Vertx)
   */
  @Test
  public void shouldRoutineWithoutLabel_distributiondNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(model -> testContext.verify(() -> {

      model.label_distribution = null;
      assertIsNotValid(model, "label_distribution", vertx, testContext);

    })));

  }

  /**
   * Check that a {@link Routine} with a non array in the label_distribution is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(String, Vertx)
   */
  @Test
  public void shouldRoutineWithANotArrayInLabel_distributiondNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(model -> testContext.verify(() -> {

      model.label_distribution.put("bad_distribution", new JsonObject());
      assertIsNotValid(model, "label_distribution.bad_distribution", vertx, testContext);

    })));

  }

  /**
   * Check that a {@link Routine} with a non scored label in the label_distribution is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(String, Vertx)
   */
  @Test
  public void shouldRoutineWithANotScoredLabelArrayInLabel_distributiondNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(model -> testContext.verify(() -> {

      model.label_distribution.put("bad_value", new JsonArray().add(new JsonArray()));
      assertIsNotValid(model, "label_distribution.bad_value", vertx, testContext);

    })));

  }

  /**
   * Check that a {@link Routine} with a bad scored label in the label_distribution is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#validate(String, Vertx)
   */
  @Test
  public void shouldRoutineWithABadScoredLabelArrayInLabel_distributiondNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(model -> testContext.verify(() -> {

      model.label_distribution.put("bad_score_label", new JsonArray().add(new ScoredLabelTest().createModelExample(1).toJsonObject()).add(new JsonObject()));
      assertIsNotValid(model, "label_distribution.bad_score_label[1].label", vertx, testContext);

    })));

  }

  /**
   * Check that merge with {@code null}.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldMergeWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanMerge(target, null, vertx, testContext, merged -> assertThat(merged).isSameAs(target));
  }

  /**
   * Check that merge two examples.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldMergeTwoExamples(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> this.createModelExample(2, vertx, testContext,
        testContext.succeeding(source -> assertCanMerge(target, source, vertx, testContext, merged -> assertThat(merged).isEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source))))));
  }

  /**
   * Check that merge only the user identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyUserId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      StoreServices.storeProfileExample(43, vertx, testContext, testContext.succeeding(profile -> {

        final var source = new Routine();
        source.user_id = profile.id;
        assertCanMerge(target, source, vertx, testContext, merged -> {

          assertThat(merged).isNotEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);
          target.user_id = source.user_id;
          assertThat(merged).isEqualTo(target);

        });

      }));

    }));
  }

  /**
   * Check that can not merge with a bad user identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithBadUserId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      final var source = new Routine();
      source.user_id = "undefined user indentifier";
      assertCannotMerge(target, source, "user_id", vertx, testContext);

    }));
  }

  /**
   * Check that merge only the weekday.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyWeekday(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      final var source = new Routine();
      source.weekday = "DV";
      assertCanMerge(target, source, vertx, testContext, merged -> {

        assertThat(merged).isNotEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);
        target.weekday = source.weekday;
        assertThat(merged).isEqualTo(target);

      });

    }));
  }

  /**
   * Check that can not merge with a bad weekday.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithBadWeekday(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      final var source = new Routine();
      source.weekday = ValidationsTest.STRING_256;
      assertCannotMerge(target, source, "weekday", vertx, testContext);

    }));
  }

  /**
   * Check that merge only the confidence.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyConfidence(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      final var source = new Routine();
      source.confidence = 43d;
      assertCanMerge(target, source, vertx, testContext, merged -> {

        assertThat(merged).isNotEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);
        target.confidence = source.confidence;
        assertThat(merged).isEqualTo(target);

      });

    }));
  }

  /**
   * Check that merge only the label distribution.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyLabelDistribution(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      final var source = new Routine();
      source.label_distribution = new JsonObject();
      assertCanMerge(target, source, vertx, testContext, merged -> {

        assertThat(merged).isNotEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);
        target.label_distribution = source.label_distribution;
        assertThat(merged).isEqualTo(target);

      });

    }));
  }

  /**
   * Check that can not merge with a bad label distribution field.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithBadLabelDistibutionField(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      final var source = new Routine();
      source.label_distribution = new JsonObject().put("bad_field", new JsonObject());
      assertCannotMerge(target, source, "label_distribution.bad_field", vertx, testContext);

    }));
  }

  /**
   * Check that can not merge with a bad label distribution array.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithBadLabelDistibutionArray(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      final var source = new Routine();
      source.label_distribution = new JsonObject().put("bad_array", new JsonArray().add(new JsonArray()));
      assertCannotMerge(target, source, "label_distribution.bad_array", vertx, testContext);

    }));
  }

  /**
   * Check that can not merge with a bad label distribution scored label.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithBadLabelDistibutionScoredLabel(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      final var source = new Routine();
      source.label_distribution = new JsonObject().put("bad_scored_label", new JsonArray().add(new JsonObject()));
      assertCannotMerge(target, source, "label_distribution.bad_scored_label[0].label", vertx, testContext);

    }));
  }

  /**
   * Check that merge only a scored label in the label distribution.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyScoredLAbelInLabelDistribution(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      final var source = new Routine();
      source.label_distribution = new JsonObject(target.label_distribution.toBuffer());
      source.label_distribution.getJsonArray("additional_1").getJsonObject(0).clear().put("label", new JsonObject().put("name", "name_1").put("latitude", -43d)).put("score", 43d);
      assertCanMerge(target, source, vertx, testContext, merged -> {

        assertThat(merged).isNotEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);
        target.label_distribution.getJsonArray("additional_1").getJsonObject(0).getJsonObject("label").put("latitude", -43d);
        target.label_distribution.getJsonArray("additional_1").getJsonObject(0).put("score", 43d);
        assertThat(merged).isEqualTo(target);

      });

    }));
  }

  /**
   * Check that update with {@code null}.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldUpdateWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanUpdate(target, null, vertx, testContext, updated -> assertThat(updated).isSameAs(target));
  }

  /**
   * Check that update two examples.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldUpdateTwoExamples(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> this.createModelExample(2, vertx, testContext,
        testContext.succeeding(source -> assertCanUpdate(target, source, vertx, testContext, updated -> assertThat(updated).isEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source))))));
  }

  /**
   * Check that update only the user identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldUpdateOnlyUserId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      StoreServices.storeProfileExample(43, vertx, testContext, testContext.succeeding(profile -> {

        final var source = Model.fromBuffer(target.toBuffer(), Routine.class);
        source.user_id = profile.id;
        assertCanUpdate(target, source, vertx, testContext, updated -> {

          assertThat(updated).isEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);

        });

      }));

    }));
  }

  /**
   * Check that can not update with a bad user identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldNotUpdateWithBadUserId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      final var source = Model.fromBuffer(target.toBuffer(), Routine.class);
      source.user_id = "undefined user indentifier";
      assertCannotUpdate(target, source, "user_id", vertx, testContext);

    }));
  }

  /**
   * Check that update only the weekday.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldUpdateOnlyWeekday(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      final var source = Model.fromBuffer(target.toBuffer(), Routine.class);
      source.weekday = "DV";
      assertCanUpdate(target, source, vertx, testContext, updated -> {

        assertThat(updated).isEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);

      });

    }));
  }

  /**
   * Check that can not update with a bad weekday.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldNotUpdateWithBadWeekday(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      final var source = Model.fromBuffer(target.toBuffer(), Routine.class);
      source.weekday = ValidationsTest.STRING_256;
      assertCannotUpdate(target, source, "weekday", vertx, testContext);

    }));
  }

  /**
   * Check that update only the confidence.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldUpdateOnlyConfidence(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      final var source = Model.fromBuffer(target.toBuffer(), Routine.class);
      source.confidence = 43d;
      assertCanUpdate(target, source, vertx, testContext, updated -> {

        assertThat(updated).isEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);

      });

    }));
  }

  /**
   * Check that update only the label distribution.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldUpdateOnlyLabelDistribution(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      final var source = Model.fromBuffer(target.toBuffer(), Routine.class);
      source.label_distribution = new JsonObject();
      assertCanUpdate(target, source, vertx, testContext, updated -> {

        assertThat(updated).isEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);

      });

    }));
  }

  /**
   * Check that can not update with a bad label distribution field.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldNotUpdateWithBadLabelDistibutionField(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      final var source = Model.fromBuffer(target.toBuffer(), Routine.class);
      source.label_distribution = new JsonObject().put("bad_field", new JsonObject());
      assertCannotUpdate(target, source, "label_distribution.bad_field", vertx, testContext);

    }));
  }

  /**
   * Check that can not update with a bad label distribution array.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldNotUpdateWithBadLabelDistibutionArray(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      final var source = Model.fromBuffer(target.toBuffer(), Routine.class);
      source.label_distribution = new JsonObject().put("bad_array", new JsonArray().add(new JsonArray()));
      assertCannotUpdate(target, source, "label_distribution.bad_array", vertx, testContext);

    }));
  }

  /**
   * Check that can not update with a bad label distribution scored label.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldNotUpdateWithBadLabelDistibutionScoredLabel(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      final var source = Model.fromBuffer(target.toBuffer(), Routine.class);
      source.label_distribution = new JsonObject().put("bad_scored_label", new JsonArray().add(new JsonObject()));
      assertCannotUpdate(target, source, "label_distribution.bad_scored_label[0].label", vertx, testContext);

    }));
  }

}
