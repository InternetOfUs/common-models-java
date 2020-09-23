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

import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
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

    profileManagerMocker.stop();
  }

  /**
   * Register the necessary services before to test.
   *
   * @param vertx event bus to register the necessary services.
   */
  @BeforeEach
  public void registerServices(final Vertx vertx) {

    final var client = WebClient.create(vertx);
    final var conf = profileManagerMocker.getComponentConfiguration();
    WeNetProfileManager.register(vertx, client, conf);

  }

  /**
   * Test constructor.
   *
   * @see SocialNetworkRelationship#SocialNetworkRelationship(SocialNetworkRelationshipType, String)
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

        final var profile = Model.fromJsonObject(creationResult.result(), WeNetUserProfile.class);
        if (profile == null) {

          creation.handle(Future.failedFuture("Can not obtain a profile form the JSON result"));

        } else {

          creation.handle(Future.succeededFuture(profile));
        }

      }
    });
  }

  /**
   * Create an example model that has the specified index.
   *
   * @param index       to use in the example.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   * @param creation    handler to manage the created social network relationship.
   */
  public void createModelExample(final int index, final Vertx vertx, final VertxTestContext testContext, final Handler<AsyncResult<SocialNetworkRelationship>> creation) {

    this.createNewEmptyProfile(vertx, testContext.succeeding(profile -> {

      final var relation = this.createModelExample(index);
      relation.userId = profile.id;
      creation.handle(Future.succeededFuture(relation));

    }));

  }

  /**
   * Check that the {@link #createModelExample(int, Vertx, VertxTestContext, Handler)} is valid.
   *
   * @param index       to verify
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialNetworkRelationship#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleFromRepositoryBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(index, vertx, testContext, testContext.succeeding(model -> {

      final var originalUserId = model.userId;
      model.userId = "   " + originalUserId + "   ";
      assertIsValid(model, vertx, testContext, () -> {

        assertThat(model.userId).isEqualTo(originalUserId);
      });
    }));

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
  @ValueSource(strings = { "undefined value ", "9bec40b8-8209-4e28-b64b-1de52595ca6d", ValidationsTest.STRING_256 })
  public void shouldNotBeValidWithBadUserIdentifier(final String userId, final Vertx vertx, final VertxTestContext testContext) {

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

    this.createNewEmptyProfile(vertx, testContext.succeeding(profile -> {
      final var model = new SocialNetworkRelationship();
      model.type = null;
      model.userId = profile.id;
      assertIsNotValid(model, "type", vertx, testContext);

    }));

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
  public void shouldNotBeValidModelWithBadWeight(final double weight, final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(model -> {

      model.weight = weight;
      assertIsNotValid(model, "weight", vertx, testContext);

    }));

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

      this.createNewEmptyProfile(vertx, testContext.succeeding(profile -> {
        final var source = new SocialNetworkRelationship();
        source.userId = profile.id;
        assertCanMerge(target, source, vertx, testContext, merged -> {

          assertThat(merged).isNotEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);
          target.userId = profile.id;
          assertThat(merged).isEqualTo(target);

        });
      }));
    }));
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

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      final var source = new SocialNetworkRelationship();
      source.userId = "undefinedUserId";
      assertCannotMerge(target, source, "userId", vertx, testContext);
    }));
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

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      final var source = new SocialNetworkRelationship();
      source.type = SocialNetworkRelationshipType.follower;
      assertCanMerge(target, source, vertx, testContext, merged -> {

        assertThat(merged).isNotEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);
        target.type = SocialNetworkRelationshipType.follower;
        assertThat(merged).isEqualTo(target);

      });
    }));
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

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

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
    }));
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

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      final var source = new SocialNetworkRelationship();
      source.weight = weight;
      assertCannotMerge(target, source, "weight", vertx, testContext);

    }));
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
   * Check that merge with {@code null}.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialNetworkRelationship#merge(SocialNetworkRelationship, String, Vertx)
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

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      this.createNewEmptyProfile(vertx, testContext.succeeding(profile -> {
        final var source = Model.fromBuffer(target.toBuffer(), SocialNetworkRelationship.class);
        source.userId = profile.id;
        assertCanUpdate(target, source, vertx, testContext, updated -> {

          assertThat(updated).isEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);

        });
      }));
    }));
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

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      final var source = Model.fromBuffer(target.toBuffer(), SocialNetworkRelationship.class);
      source.userId = "undefinedUserId";
      assertCannotUpdate(target, source, "userId", vertx, testContext);
    }));
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

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      final var source = Model.fromBuffer(target.toBuffer(), SocialNetworkRelationship.class);
      source.type = SocialNetworkRelationshipType.follower;
      assertCanUpdate(target, source, vertx, testContext, updated -> {

        assertThat(updated).isEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);

      });
    }));
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

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      final var source = Model.fromBuffer(target.toBuffer(), SocialNetworkRelationship.class);
      var weight = 0D;
      do {

        weight = Math.random();

      } while (weight == target.weight);
      source.weight = weight;
      assertCanUpdate(target, source, vertx, testContext, updated -> {

        assertThat(updated).isEqualTo(source).isNotEqualTo(target).isNotSameAs(target).isNotSameAs(source);

      });
    }));
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

    this.createModelExample(1, vertx, testContext, testContext.succeeding(target -> {

      final var source = Model.fromBuffer(target.toBuffer(), SocialNetworkRelationship.class);
      source.weight = weight;
      assertCannotUpdate(target, source, "weight", vertx, testContext);

    }));
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
   * Check that update with {@code null}.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see SocialNetworkRelationship#update(SocialNetworkRelationship, String, Vertx)
   */
  @Test
  public void shouldUpdateWithNull(final Vertx vertx, final VertxTestContext testContext) {

    final var target = this.createModelExample(1);
    assertCanUpdate(target, null, vertx, testContext, updated -> assertThat(updated).isSameAs(target));
  }
}
