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
import eu.internetofus.common.components.service.App;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ModelTestCase;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test the {@link SocialNetworkRelationship}.
 *
 * @see SocialNetworkRelationship
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetIntegrationExtension.class)
public class SocialNetworkRelationshipTest extends ModelTestCase<SocialNetworkRelationship> {

  /**
   * {@inheritDoc}
   */
  @Override
  public SocialNetworkRelationship createModelExample(final int index) {

    final var model = new SocialNetworkRelationship();
    model.appId = "app_of_" + index;
    model.sourceId = "source_of_" + index;
    model.targetId = "target_of_" + index;
    model.type = SocialNetworkRelationshipType.values()[index % SocialNetworkRelationshipType.values().length];
    model.weight = index % 1000 / 1000.0;
    return model;

  }

  /**
   * Create an example model that has the specified index.
   *
   * @param index       to use in the example.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @return the created social network relationship.
   */
  public Future<SocialNetworkRelationship> createModelExample(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    return StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).compose(source -> {

      return StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).compose(target -> {

        final var relation = this.createModelExample(index);
        relation.sourceId = source.id;
        relation.targetId = target.id;
        return StoreServices.storeApp(new App(), vertx, testContext).map(app -> {
          relation.appId = app.appId;
          return relation;
        });
      });
    });

  }

  /**
   * Check that the {@link #createModelExample(int, Vertx, VertxTestContext)} is
   * valid.
   *
   * @param index       to verify
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialNetworkRelationship#validate(WeNetValidateContext)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleFromRepositoryBeValid(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    this.createModelExample(index, vertx, testContext).onSuccess(model -> {

      assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that a model is not valid with an undefined app id.
   *
   * @param appId       that is not valid.
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see SocialNetworkRelationship#validate(WeNetValidateContext)
   */
  @ParameterizedTest(name = "Should not be valid  a SocialNetworkRelationship with an appId = {0}")
  @NullAndEmptySource
  @ValueSource(strings = { "undefined value ", "9bec40b8-8209-4e28-b64b-1de52595ca6d" })
  public void shouldNotBeValidWithBadAppIdentifier(final String appId, final Vertx vertx,
      final VertxTestContext testContext) {

    this.createModelExample(0, vertx, testContext).onSuccess(model -> {

      model.appId = appId;
      model.type = SocialNetworkRelationshipType.colleague;
      assertIsNotValid(model, "appId", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that a model is not valid with an undefined source id.
   *
   * @param userId      that is not valid.
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see SocialNetworkRelationship#validate(WeNetValidateContext)
   */
  @ParameterizedTest(name = "Should not be valid  a SocialNetworkRelationship with an sourceId = {0}")
  @NullAndEmptySource
  @ValueSource(strings = { "undefined value ", "9bec40b8-8209-4e28-b64b-1de52595ca6d" })
  public void shouldNotBeValidWithBadSourceIdentifier(final String userId, final Vertx vertx,
      final VertxTestContext testContext) {

    this.createModelExample(0, vertx, testContext).onSuccess(model -> {

      model.sourceId = userId;
      model.type = SocialNetworkRelationshipType.colleague;
      assertIsNotValid(model, "sourceId", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that a model is not valid with an undefined target id.
   *
   * @param userId      that is not valid.
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see SocialNetworkRelationship#validate(WeNetValidateContext)
   */
  @ParameterizedTest(name = "Should not be valid  a SocialNetworkRelationship with an targetId = {0}")
  @NullAndEmptySource
  @ValueSource(strings = { "undefined value ", "9bec40b8-8209-4e28-b64b-1de52595ca6d" })
  public void shouldNotBeValidWithBadTargetIdentifier(final String userId, final Vertx vertx,
      final VertxTestContext testContext) {

    this.createModelExample(0, vertx, testContext).onSuccess(model -> {

      model.targetId = userId;
      model.type = SocialNetworkRelationshipType.colleague;
      assertIsNotValid(model, "targetId", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that a model is not valid without a type.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see SocialNetworkRelationship#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidModelWithoutType(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(0, vertx, testContext).onSuccess(model -> {
      model.type = null;
      assertIsNotValid(model, "type", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that a model is not valid without an app id.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see SocialNetworkRelationship#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidModelWithoutAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(0, vertx, testContext).onSuccess(model -> {

      model.type = SocialNetworkRelationshipType.colleague;
      model.appId = null;
      assertIsNotValid(model, "appId", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that a model is not valid without a source id.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see SocialNetworkRelationship#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidModelWithoutSourceId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(0, vertx, testContext).onSuccess(model -> {

      model.type = SocialNetworkRelationshipType.colleague;
      model.sourceId = null;
      assertIsNotValid(model, "sourceId", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that a model is not valid without a target id.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see SocialNetworkRelationship#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidModelWithoutTargetId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(0, vertx, testContext).onSuccess(model -> {

      model.type = SocialNetworkRelationshipType.colleague;
      model.targetId = null;
      assertIsNotValid(model, "targetId", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that a model is not valid without a bad weight.
   *
   * @param weight      that is not valid.
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see SocialNetworkRelationship#validate(WeNetValidateContext)
   */
  @ParameterizedTest(name = "Should a social network relationship with a weight {0} not be valid")
  @ValueSource(doubles = { -0.00001d, 1.000001d, -23d, +23d })
  public void shouldNotBeValidModelWithBadWeight(final double weight, final Vertx vertx,
      final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.weight = weight;
      assertIsNotValid(model, "weight", new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that merge only the app identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldMergeOnlyAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      StoreServices.storeApp(new App(), vertx, testContext).onSuccess(app -> {
        final var source = new SocialNetworkRelationship();
        source.appId = app.appId;
        assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

          assertThat(merged).isNotEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);
          target.appId = app.appId;
          assertThat(merged).isEqualTo(target);

        });
      });
    });
  }

  /**
   * Check that merge only the source identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldMergeOnlySourceId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(profile -> {
        final var newModel = new SocialNetworkRelationship();
        newModel.sourceId = profile.id;
        assertCanMerge(target, newModel, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

          assertThat(merged).isNotEqualTo(newModel).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(newModel);
          target.sourceId = profile.id;
          assertThat(merged).isEqualTo(target);

        });
      });
    });
  }

  /**
   * Check that merge only the target identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldMergeOnlyTargetId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(profile -> {
        final var newModel = new SocialNetworkRelationship();
        newModel.targetId = profile.id;
        assertCanMerge(target, newModel, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

          assertThat(merged).isNotEqualTo(newModel).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(newModel);
          target.targetId = profile.id;
          assertThat(merged).isEqualTo(target);

        });
      });
    });
  }

  /**
   * Check that not merge undefined app identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldFailMergeUndefinedAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = new SocialNetworkRelationship();
      source.appId = "undefinedAppId";
      assertCannotMerge(target, source, "appId", new WeNetValidateContext("codePrefix", vertx), testContext);
    });
  }

  /**
   * Check that not merge undefined source identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldFailMergeUndefinedSourceId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var newModel = new SocialNetworkRelationship();
      newModel.sourceId = "undefinedUserId";
      assertCannotMerge(target, newModel, "sourceId", new WeNetValidateContext("codePrefix", vertx), testContext);
    });
  }

  /**
   * Check that not merge undefined target identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldFailMergeUndefinedTargetId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var newModel = new SocialNetworkRelationship();
      newModel.targetId = "undefinedUserId";
      assertCannotMerge(target, newModel, "targetId", new WeNetValidateContext("codePrefix", vertx), testContext);
    });
  }

  /**
   * Check that merge only the type.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldMergeOnlyType(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = new SocialNetworkRelationship();
      source.type = SocialNetworkRelationshipType.follower;
      assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

        assertThat(merged).isNotEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);
        target.type = SocialNetworkRelationshipType.follower;
        assertThat(merged).isEqualTo(target);

      });
    });
  }

  /**
   * Check that merge only the weight.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldMergeOnlyWeight(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = new SocialNetworkRelationship();
      var weight = 0D;
      do {

        weight = Math.random();

      } while (weight == target.weight);
      source.weight = weight;
      assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

        assertThat(merged).isNotEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);
        target.weight = source.weight;
        assertThat(merged).isEqualTo(target);

      });
    });
  }

  /**
   * Check can not merge without a bad weight.
   *
   * @param weight      that is not valid.
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see SocialNetworkRelationship#validate(WeNetValidateContext)
   */
  @ParameterizedTest(name = "Should not merge a social network relationship with a weight {0} ")
  @ValueSource(doubles = { -0.00001d, 1.000001d, -23d, +23d })
  public void shouldNotMergeWithBadWeight(final double weight, final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = new SocialNetworkRelationship();
      source.weight = weight;
      assertCannotMerge(target, source, "weight", new WeNetValidateContext("codePrefix", vertx), testContext);

    });
  }

  /**
   * Check that merge two examples.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldMergeTwoExamples(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      this.createModelExample(2, vertx, testContext).onSuccess(source -> {
        assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {
          assertThat(merged).isEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);
        });
      });
    });

  }

  /**
   * Check that merge with {@code null}.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialNetworkRelationship#merge(SocialNetworkRelationship,
   *      WeNetValidateContext)
   */
  @Test
  public void shouldMergeWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanMerge(target, null, new WeNetValidateContext("codePrefix", vertx), testContext,
        merged -> assertThat(merged).isSameAs(target));
  }

  /**
   * Check that update only the app identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateOnlyAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      StoreServices.storeApp(new App(), vertx, testContext).onSuccess(app -> {
        final var source = Model.fromBuffer(target.toBuffer(), SocialNetworkRelationship.class);
        source.appId = app.appId;
        assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {

          assertThat(updated).isEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);

        });
      });
    });
  }

  /**
   * Check that update only the source identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateOnlySourceId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(profile -> {
        final var newModel = Model.fromBuffer(target.toBuffer(), SocialNetworkRelationship.class);
        newModel.sourceId = profile.id;
        assertCanUpdate(target, newModel, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {

          assertThat(updated).isEqualTo(newModel).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(newModel);

        });
      });
    });
  }

  /**
   * Check that update only the target identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateOnlyTargetId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(profile -> {
        final var newModel = Model.fromBuffer(target.toBuffer(), SocialNetworkRelationship.class);
        newModel.targetId = profile.id;
        assertCanUpdate(target, newModel, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {

          assertThat(updated).isEqualTo(newModel).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(newModel);

        });
      });
    });
  }

  /**
   * Check that not update undefined app identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldFailUpdateUndefinedAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = Model.fromBuffer(target.toBuffer(), SocialNetworkRelationship.class);
      source.appId = "undefinedAppId";
      assertCannotUpdate(target, source, "appId", new WeNetValidateContext("codePrefix", vertx), testContext);
    });
  }

  /**
   * Check that not update undefined source identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldFailUpdateUndefinedSourceId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var newModel = Model.fromBuffer(target.toBuffer(), SocialNetworkRelationship.class);
      newModel.sourceId = "undefinedUserId";
      assertCannotUpdate(target, newModel, "sourceId", new WeNetValidateContext("codePrefix", vertx), testContext);
    });
  }

  /**
   * Check that not update undefined target identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldFailUpdateUndefinedTargetId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var newModel = Model.fromBuffer(target.toBuffer(), SocialNetworkRelationship.class);
      newModel.targetId = "undefinedUserId";
      assertCannotUpdate(target, newModel, "targetId", new WeNetValidateContext("codePrefix", vertx), testContext);
    });
  }

  /**
   * Check that update only the type.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateOnlyType(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = Model.fromBuffer(target.toBuffer(), SocialNetworkRelationship.class);
      source.type = SocialNetworkRelationshipType.follower;
      assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {

        assertThat(updated).isEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);

      });
    });
  }

  /**
   * Check that update only the weight.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateOnlyWeight(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = Model.fromBuffer(target.toBuffer(), SocialNetworkRelationship.class);
      var weight = 0D;
      do {

        weight = Math.random();

      } while (weight == target.weight);
      source.weight = weight;
      assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {

        assertThat(updated).isEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);

      });
    });
  }

  /**
   * Check can not update without a bad weight.
   *
   * @param weight      that is not valid.
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see SocialNetworkRelationship#validate(WeNetValidateContext)
   */
  @ParameterizedTest(name = "Should not update a social network relationship with a weight {0} ")
  @ValueSource(doubles = { -0.00001d, 1.000001d, -23d, +23d })
  public void shouldNotUpdateWithBadWeight(final double weight, final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = Model.fromBuffer(target.toBuffer(), SocialNetworkRelationship.class);
      source.weight = weight;
      assertCannotUpdate(target, source, "weight", new WeNetValidateContext("codePrefix", vertx), testContext);

    });
  }

  /**
   * Check that update two examples.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateTwoExamples(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      this.createModelExample(2, vertx, testContext).onSuccess(source -> {
        assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {
          assertThat(updated).isEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);
        });
      });
    });
  }

  /**
   * Check that update with {@code null}.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialNetworkRelationship#update(SocialNetworkRelationship,
   *      WeNetValidateContext)
   */
  @Test
  public void shouldUpdateWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanUpdate(target, null, new WeNetValidateContext("codePrefix", vertx), testContext,
        updated -> assertThat(updated).isSameAs(target));
  }

  /**
   * Check that compare some relations by their identifiers.
   *
   * @see SocialNetworkRelationship#compareIds(SocialNetworkRelationship,
   *      SocialNetworkRelationship)
   */
  @Test
  public void shouldCompareIds() {

    assertThat(SocialNetworkRelationship.compareIds(null, null)).isFalse();
    final var source = new SocialNetworkRelationship();
    assertThat(SocialNetworkRelationship.compareIds(source, null)).isFalse();
    final var target = new SocialNetworkRelationship();
    assertThat(SocialNetworkRelationship.compareIds(null, target)).isFalse();
    assertThat(SocialNetworkRelationship.compareIds(source, target)).isFalse();
    source.type = SocialNetworkRelationshipType.acquaintance;
    assertThat(SocialNetworkRelationship.compareIds(source, target)).isFalse();
    target.type = SocialNetworkRelationshipType.colleague;
    assertThat(SocialNetworkRelationship.compareIds(source, target)).isFalse();
    target.type = SocialNetworkRelationshipType.acquaintance;
    assertThat(SocialNetworkRelationship.compareIds(source, target)).isFalse();
    source.appId = "appId";
    assertThat(SocialNetworkRelationship.compareIds(source, target)).isFalse();
    target.appId = "appId2";
    assertThat(SocialNetworkRelationship.compareIds(source, target)).isFalse();
    target.appId = "appId";
    assertThat(SocialNetworkRelationship.compareIds(source, target)).isFalse();
    source.sourceId = "sourceId";
    assertThat(SocialNetworkRelationship.compareIds(source, target)).isFalse();
    target.sourceId = "sourceId2";
    assertThat(SocialNetworkRelationship.compareIds(source, target)).isFalse();
    target.sourceId = "sourceId";
    assertThat(SocialNetworkRelationship.compareIds(source, target)).isFalse();
    source.targetId = "targetId";
    assertThat(SocialNetworkRelationship.compareIds(source, target)).isFalse();
    target.targetId = "targetId2";
    assertThat(SocialNetworkRelationship.compareIds(source, target)).isFalse();
    target.targetId = "targetId";
    assertThat(SocialNetworkRelationship.compareIds(source, target)).isTrue();
  }

}
