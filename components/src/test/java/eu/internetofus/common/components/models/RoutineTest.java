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
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ModelTestCase;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test the {@link Routine}
 *
 * @see Routine
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetIntegrationExtension.class)
public class RoutineTest extends ModelTestCase<Routine> {

  /**
   * {@inheritDoc}
   */
  @Override
  public Routine createModelExample(final int index) {

    final var model = new Routine();
    model.user_id = "user_id_" + index;
    model.weekday = "weekday_" + index;
    model.label_distribution = new JsonObject();
    model.label_distribution.put("additional_" + index,
        new JsonArray().add(new ScoredLabelTest().createModelExample(index).toJsonObject())
            .add(new ScoredLabelTest().createModelExample(index + 1).toJsonObject()));
    model.label_distribution.put("additional_" + (index + 1), new JsonArray());
    model.label_distribution.put("additional_" + (index + 2),
        new JsonArray().add(new ScoredLabelTest().createModelExample(index - 1).toJsonObject())
            .add(new ScoredLabelTest().createModelExample(index).toJsonObject())
            .add(new ScoredLabelTest().createModelExample(index + 1).toJsonObject()));
    model.confidence = 1.0 / Math.max(1.0, index + 1);
    return model;
  }

  /**
   * Check that the {@link #createModelExample(int)} is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#validate(WeNetValidateContext)
   */
  @Test
  public void shouldExampleNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    assertIsNotValid(model, "user_id", new WeNetValidateContext("codePrefix", vertx), testContext);
  }

  /**
   * Create a valid model example.
   *
   * @param index       of the example to create.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @return the created routine.
   */
  public Future<Routine> createModelExample(final int index, final Vertx vertx, final VertxTestContext testContext) {

    return StoreServices.storeProfileExample(index, vertx, testContext).compose(profile -> {

      final var model = this.createModelExample(index);
      model.user_id = profile.id;
      return Future.succeededFuture(model);
    });

  }

  /**
   * Check that the {@link #createModelExample(int, Vertx, VertxTestContext)} is
   * valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#validate(WeNetValidateContext)
   */
  @Test
  public void shouldComplexExampleBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext)
        .onSuccess(routine -> assertIsValid(routine, new WeNetValidateContext("codePrefix", vertx), testContext));

  }

  /**
   * Check that a {@link Routine} without a user id is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#validate(WeNetValidateContext)
   */
  @Test
  public void shouldRoutineWithoutUserIdNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.user_id = null;
      assertIsNotValid(model, "user_id", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that a {@link Routine} without an undefined user id is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#validate(WeNetValidateContext)
   */
  @Test
  public void shouldRoutineWithUndefinedUserIdNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.user_id = "Undefined user id";
      assertIsNotValid(model, "user_id", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that a {@link Routine} without a weekday is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#validate(WeNetValidateContext)
   */
  @Test
  public void shouldRoutineWithoutWeekdaydNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.weekday = null;
      assertIsNotValid(model, "weekday", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that a {@link Routine} without a confidence is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#validate(WeNetValidateContext)
   */
  @Test
  public void shouldRoutineWithoutConfidencedNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.confidence = null;
      assertIsNotValid(model, "confidence", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that a {@link Routine} without a label_distribution is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#validate(WeNetValidateContext)
   */
  @Test
  public void shouldRoutineWithoutLabel_distributiondNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.label_distribution = null;
      assertIsNotValid(model, "label_distribution", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that a {@link Routine} with a non array in the label_distribution is
   * not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#validate(WeNetValidateContext)
   */
  @Test
  public void shouldRoutineWithANotArrayInLabel_distributiondNotBeValid(final Vertx vertx,
      final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.label_distribution.put("bad_distribution", new JsonObject());
      assertIsNotValid(model, "label_distribution.bad_distribution", new WeNetValidateContext("codePrefix", vertx),
          testContext);

    });

  }

  /**
   * Check that a {@link Routine} with a non scored label in the
   * label_distribution is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#validate(WeNetValidateContext)
   */
  @Test
  public void shouldRoutineWithANotScoredLabelArrayInLabel_distributiondNotBeValid(final Vertx vertx,
      final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.label_distribution.put("bad_value", new JsonArray().add(new JsonArray()));
      assertIsNotValid(model, "label_distribution.bad_value", new WeNetValidateContext("codePrefix", vertx),
          testContext);

    });

  }

  /**
   * Check that a {@link Routine} with a bad scored label in the
   * label_distribution is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#validate(WeNetValidateContext)
   */
  @Test
  public void shouldRoutineWithABadScoredLabelArrayInLabel_distributiondNotBeValid(final Vertx vertx,
      final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.label_distribution.put("bad_score_label",
          new JsonArray().add(new ScoredLabelTest().createModelExample(1).toJsonObject())
              .add(new ScoredLabelTest().createModelExample(2).toJsonObject().putNull("label")));
      assertIsNotValid(model, "label_distribution.bad_score_label[1].label",
          new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that merge with {@code null}.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#merge(Routine, WeNetValidateContext)
   */
  @Test
  public void shouldMergeWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanMerge(target, null, new WeNetValidateContext("codePrefix", vertx), testContext,
        merged -> assertThat(merged).isSameAs(target));
  }

  /**
   * Check that merge two examples.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#merge(Routine, WeNetValidateContext)
   */
  @Test
  public void shouldMergeTwoExamples(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext)
        .onSuccess(target -> this.createModelExample(2, vertx, testContext)
            .onSuccess(source -> assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx),
                testContext, merged -> assertThat(merged).isEqualTo(source).isNotEqualTo(target).isNotSameAs(target)
                    .isNotSameAs(source))));
  }

  /**
   * Check that merge only the user identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#merge(Routine, WeNetValidateContext)
   */
  @Test
  public void shouldMergeOnlyUserId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      StoreServices.storeProfileExample(43, vertx, testContext).onSuccess(profile -> {

        final var source = new Routine();
        source.user_id = profile.id;
        assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

          assertThat(merged).isNotEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);
          target.user_id = source.user_id;
          assertThat(merged).isEqualTo(target);

        });

      });

    });
  }

  /**
   * Check that can not merge with a bad user identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#merge(Routine, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithBadUserId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = new Routine();
      source.user_id = "undefined user indentifier";
      assertCannotMerge(target, source, "user_id", new WeNetValidateContext("codePrefix", vertx), testContext);

    });
  }

  /**
   * Check that merge only the weekday.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#merge(Routine, WeNetValidateContext)
   */
  @Test
  public void shouldMergeOnlyWeekday(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = new Routine();
      source.weekday = "DV";
      assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

        assertThat(merged).isNotEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);
        target.weekday = source.weekday;
        assertThat(merged).isEqualTo(target);

      });

    });
  }

  /**
   * Check that merge only the confidence.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#merge(Routine, WeNetValidateContext)
   */
  @Test
  public void shouldMergeOnlyConfidence(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = new Routine();
      source.confidence = 43d;
      assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

        assertThat(merged).isNotEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);
        target.confidence = source.confidence;
        assertThat(merged).isEqualTo(target);

      });

    });
  }

  /**
   * Check that merge only the label distribution.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#merge(Routine, WeNetValidateContext)
   */
  @Test
  public void shouldMergeOnlyLabelDistribution(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = new Routine();
      source.label_distribution = new JsonObject();
      assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

        assertThat(merged).isNotEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);
        target.label_distribution = source.label_distribution;
        assertThat(merged).isEqualTo(target);

      });

    });
  }

  /**
   * Check that can not merge with a bad label distribution field.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#merge(Routine, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithBadLabelDistibutionField(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = new Routine();
      source.label_distribution = new JsonObject().put("bad_field", new JsonObject());
      assertCannotMerge(target, source, "label_distribution.bad_field", new WeNetValidateContext("codePrefix", vertx),
          testContext);

    });
  }

  /**
   * Check that can not merge with a bad label distribution array.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#merge(Routine, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithBadLabelDistibutionArray(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = new Routine();
      source.label_distribution = new JsonObject().put("bad_array", new JsonArray().add(new JsonArray()));
      assertCannotMerge(target, source, "label_distribution.bad_array", new WeNetValidateContext("codePrefix", vertx),
          testContext);

    });
  }

  /**
   * Check that can not merge with a bad label distribution scored label.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#merge(Routine, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithBadLabelDistibutionScoredLabel(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = new Routine();
      source.label_distribution = new JsonObject().put("bad_scored_label",
          new JsonArray().add(new ScoredLabelTest().createModelExample(1).toJsonObject().putNull("label")));
      assertCannotMerge(target, source, "label_distribution.bad_scored_label[0].label",
          new WeNetValidateContext("codePrefix", vertx), testContext);

    });
  }

  /**
   * Check that merge only a scored label in the label distribution.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#merge(Routine, WeNetValidateContext)
   */
  @Test
  public void shouldMergeOnlyScoredLAbelInLabelDistribution(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = new Routine();
      source.label_distribution = new JsonObject(target.label_distribution.toBuffer());
      source.label_distribution.getJsonArray("additional_1").getJsonObject(0).clear()
          .put("label", new JsonObject().put("name", "name_1").put("latitude", -43d)).put("score", 43d);
      assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

        assertThat(merged).isNotEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);
        target.label_distribution.getJsonArray("additional_1").getJsonObject(0).getJsonObject("label").put("latitude",
            -43d);
        target.label_distribution.getJsonArray("additional_1").getJsonObject(0).put("score", 43d);
        assertThat(merged).isEqualTo(target);

      });

    });
  }

  /**
   * Check that update with {@code null}.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#update(Routine, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanUpdate(target, null, new WeNetValidateContext("codePrefix", vertx), testContext,
        updated -> assertThat(updated).isSameAs(target));
  }

  /**
   * Check that update two examples.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#update(Routine, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateTwoExamples(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext)
        .onSuccess(target -> this.createModelExample(2, vertx, testContext)
            .onSuccess(source -> assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx),
                testContext, updated -> assertThat(updated).isEqualTo(source).isNotEqualTo(target).isNotSameAs(target)
                    .isNotSameAs(source))));

  }

  /**
   * Check that update only the user identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#update(Routine, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateOnlyUserId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      StoreServices.storeProfileExample(43, vertx, testContext).onSuccess(profile -> {

        final var source = Model.fromBuffer(target.toBuffer(), Routine.class);
        source.user_id = profile.id;
        assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {

          assertThat(updated).isEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);

        });

      });

    });
  }

  /**
   * Check that can not update with a bad user identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#update(Routine, WeNetValidateContext)
   */
  @Test
  public void shouldNotUpdateWithBadUserId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = Model.fromBuffer(target.toBuffer(), Routine.class);
      source.user_id = "undefined user indentifier";
      assertCannotUpdate(target, source, "user_id", new WeNetValidateContext("codePrefix", vertx), testContext);

    });
  }

  /**
   * Check that update only the weekday.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#update(Routine, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateOnlyWeekday(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = Model.fromBuffer(target.toBuffer(), Routine.class);
      source.weekday = "DV";
      assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {

        assertThat(updated).isEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);

      });

    });
  }

  /**
   * Check that update only the confidence.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#update(Routine, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateOnlyConfidence(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = Model.fromBuffer(target.toBuffer(), Routine.class);
      source.confidence = 43d;
      assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {

        assertThat(updated).isEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);

      });

    });
  }

  /**
   * Check that update only the label distribution.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#update(Routine, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateOnlyLabelDistribution(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = Model.fromBuffer(target.toBuffer(), Routine.class);
      source.label_distribution = new JsonObject();
      assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {

        assertThat(updated).isEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);

      });

    });
  }

  /**
   * Check that can not update with a bad label distribution field.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#update(Routine, WeNetValidateContext)
   */
  @Test
  public void shouldNotUpdateWithBadLabelDistibutionField(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = Model.fromBuffer(target.toBuffer(), Routine.class);
      source.label_distribution = new JsonObject().put("bad_field", new JsonObject());
      assertCannotUpdate(target, source, "label_distribution.bad_field", new WeNetValidateContext("codePrefix", vertx),
          testContext);

    });
  }

  /**
   * Check that can not update with a bad label distribution array.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#update(Routine, WeNetValidateContext)
   */
  @Test
  public void shouldNotUpdateWithBadLabelDistibutionArray(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = Model.fromBuffer(target.toBuffer(), Routine.class);
      source.label_distribution = new JsonObject().put("bad_array", new JsonArray().add(new JsonArray()));
      assertCannotUpdate(target, source, "label_distribution.bad_array", new WeNetValidateContext("codePrefix", vertx),
          testContext);

    });
  }

  /**
   * Check that can not update with a bad label distribution scored label.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#update(Routine, WeNetValidateContext)
   */
  @Test
  public void shouldNotUpdateWithBadLabelDistibutionScoredLabel(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = Model.fromBuffer(target.toBuffer(), Routine.class);
      source.label_distribution = new JsonObject().put("bad_scored_label",
          new JsonArray().add(new ScoredLabelTest().createModelExample(1).toJsonObject().putNull("label")));
      assertCannotUpdate(target, source, "label_distribution.bad_scored_label[0].label",
          new WeNetValidateContext("codePrefix", vertx), testContext);

    });
  }

}
