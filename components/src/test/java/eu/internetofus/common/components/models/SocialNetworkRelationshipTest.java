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

import static eu.internetofus.common.components.AbstractComponentMocker.createClientWithDefaultSession;
import static eu.internetofus.common.model.MergeAsserts.assertCanMerge;
import static eu.internetofus.common.model.MergeAsserts.assertCannotMerge;
import static eu.internetofus.common.model.UpdateAsserts.assertCanUpdate;
import static eu.internetofus.common.model.UpdateAsserts.assertCannotUpdate;
import static eu.internetofus.common.model.ValidableAsserts.assertIsNotValid;
import static eu.internetofus.common.model.ValidableAsserts.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.components.profile_manager.WeNetProfileManagerMocker;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ModelTestCase;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
@ExtendWith(VertxExtension.class)
public class SocialNetworkRelationshipTest extends ModelTestCase<SocialNetworkRelationship> {

  /**
   * The profile manager mocked server.
   */
  protected static WeNetProfileManagerMocker profileManagerMocker;

  /**
   * Start the mocker server.
   */
  @BeforeAll
  public static void startMocker() {

    profileManagerMocker = WeNetProfileManagerMocker.start();
  }

  /**
   * Stop the mocker server.
   */
  @AfterAll
  public static void stopMockers() {

    profileManagerMocker.stopServer();
  }

  /**
   * Register the necessary services before to test.
   *
   * @param vertx event bus to register the necessary services.
   */
  @BeforeEach
  public void registerServices(final Vertx vertx) {

    final var client = createClientWithDefaultSession(vertx);
    final var conf = profileManagerMocker.getComponentConfiguration();
    WeNetProfileManager.register(vertx, client, conf);

  }

  /**
   * Test constructor.
   *
   * @see SocialNetworkRelationship#SocialNetworkRelationship(SocialNetworkRelationshipType,
   *      String)
   */
  @Test
  public void shouldCreateARelationship() {

    final var type = SocialNetworkRelationshipType.colleague;
    final var userId = UUID.randomUUID().toString();
    final var model = new SocialNetworkRelationship(type, userId);
    assertThat(model.type).isEqualTo(type);
    assertThat(model.userId).isEqualTo(userId);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SocialNetworkRelationship createModelExample(final int index) {

    final var model = new SocialNetworkRelationship();
    model.userId = String.valueOf(index);
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

    return StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).compose(profile -> {

      final var relation = this.createModelExample(index);
      relation.userId = profile.id;
      return Future.succeededFuture(relation);

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
   * @see SocialNetworkRelationship#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleFromRepositoryBeValid(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    this.createModelExample(index, vertx, testContext).onSuccess(model -> {

      model.userId = model.userId;
      assertIsValid(model, vertx, testContext);

    });

  }

  /**
   * Check that a model is not valid with an undefined user id.
   *
   * @param userId      that is not valid.
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see SocialNetworkRelationship#validate(String, Vertx)
   */
  @ParameterizedTest(name = "Should not be valid  a SocialNetworkRelationship with an userId = {0}")
  @NullAndEmptySource
  @ValueSource(strings = { "undefined value ", "9bec40b8-8209-4e28-b64b-1de52595ca6d" })
  public void shouldNotBeValidWithBadUserIdentifier(final String userId, final Vertx vertx,
      final VertxTestContext testContext) {

    final var model = new SocialNetworkRelationship();
    model.userId = userId;
    model.type = SocialNetworkRelationshipType.colleague;
    assertIsNotValid(model, "userId", vertx, testContext);

  }

  /**
   * Check that a model is not valid without a type.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see SocialNetworkRelationship#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidModelWithoutType(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(profile -> {
      final var model = new SocialNetworkRelationship();
      model.type = null;
      model.userId = profile.id;
      assertIsNotValid(model, "type", vertx, testContext);

    });

  }

  /**
   * Check that a model is not valid without an user id.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see SocialNetworkRelationship#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidModelWithoutUserId(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new SocialNetworkRelationship();
    model.type = SocialNetworkRelationshipType.colleague;
    model.userId = null;
    assertIsNotValid(model, "userId", vertx, testContext);

  }

  /**
   * Check that a model is not valid without a bad weight.
   *
   * @param weight      that is not valid.
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see SocialNetworkRelationship#validate(String, Vertx)
   */
  @ParameterizedTest(name = "Should a social network relationship with a weight {0} not be valid")
  @ValueSource(doubles = { -0.00001d, 1.000001d, -23d, +23d })
  public void shouldNotBeValidModelWithBadWeight(final double weight, final Vertx vertx,
      final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.weight = weight;
      assertIsNotValid(model, "weight", vertx, testContext);

    });

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

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(profile -> {
        final var source = new SocialNetworkRelationship();
        source.userId = profile.id;
        assertCanMerge(target, source, vertx, testContext, merged -> {

          assertThat(merged).isNotEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);
          target.userId = profile.id;
          assertThat(merged).isEqualTo(target);

        });
      });
    });
  }

  /**
   * Check that not merge undefined user identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldMergeUndefinedUserId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = new SocialNetworkRelationship();
      source.userId = "undefinedUserId";
      assertCannotMerge(target, source, "userId", vertx, testContext);
    });
  }

  /**
   * Check that merge only the type.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldMergeOnlyType(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = new SocialNetworkRelationship();
      source.type = SocialNetworkRelationshipType.follower;
      assertCanMerge(target, source, vertx, testContext, merged -> {

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
   * @see RelevantLocation#merge(RelevantLocation, String, Vertx)
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
      assertCanMerge(target, source, vertx, testContext, merged -> {

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
   * @see SocialNetworkRelationship#validate(String, Vertx)
   */
  @ParameterizedTest(name = "Should not merge a social network relationship with a weight {0} ")
  @ValueSource(doubles = { -0.00001d, 1.000001d, -23d, +23d })
  public void shouldNotMergeWithBadWeight(final double weight, final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = new SocialNetworkRelationship();
      source.weight = weight;
      assertCannotMerge(target, source, "weight", vertx, testContext);

    });
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

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      this.createModelExample(2, vertx, testContext).onSuccess(source -> {
        assertCanMerge(target, source, vertx, testContext, merged -> {
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
   * @see SocialNetworkRelationship#merge(SocialNetworkRelationship, String,
   *      Vertx)
   */
  @Test
  public void shouldMergeWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanMerge(target, null, vertx, testContext, merged -> assertThat(merged).isSameAs(target));
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

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).onSuccess(profile -> {
        final var source = Model.fromBuffer(target.toBuffer(), SocialNetworkRelationship.class);
        source.userId = profile.id;
        assertCanUpdate(target, source, vertx, testContext, updated -> {

          assertThat(updated).isEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);

        });
      });
    });
  }

  /**
   * Check that not update undefined user identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldUpdateUndefinedUserId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = Model.fromBuffer(target.toBuffer(), SocialNetworkRelationship.class);
      source.userId = "undefinedUserId";
      assertCannotUpdate(target, source, "userId", vertx, testContext);
    });
  }

  /**
   * Check that update only the type.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see RelevantLocation#update(RelevantLocation, String, Vertx)
   */
  @Test
  public void shouldUpdateOnlyType(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = Model.fromBuffer(target.toBuffer(), SocialNetworkRelationship.class);
      source.type = SocialNetworkRelationshipType.follower;
      assertCanUpdate(target, source, vertx, testContext, updated -> {

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
   * @see RelevantLocation#update(RelevantLocation, String, Vertx)
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
      assertCanUpdate(target, source, vertx, testContext, updated -> {

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
   * @see SocialNetworkRelationship#validate(String, Vertx)
   */
  @ParameterizedTest(name = "Should not update a social network relationship with a weight {0} ")
  @ValueSource(doubles = { -0.00001d, 1.000001d, -23d, +23d })
  public void shouldNotUpdateWithBadWeight(final double weight, final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = Model.fromBuffer(target.toBuffer(), SocialNetworkRelationship.class);
      source.weight = weight;
      assertCannotUpdate(target, source, "weight", vertx, testContext);

    });
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

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      this.createModelExample(2, vertx, testContext).onSuccess(source -> {
        assertCanUpdate(target, source, vertx, testContext, updated -> {
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
   * @see SocialNetworkRelationship#update(SocialNetworkRelationship, String,
   *      Vertx)
   */
  @Test
  public void shouldUpdateWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanUpdate(target, null, vertx, testContext, updated -> assertThat(updated).isSameAs(target));
  }
}