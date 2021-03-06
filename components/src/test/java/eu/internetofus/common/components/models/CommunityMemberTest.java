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
import static eu.internetofus.common.model.UpdateAsserts.assertCanUpdate;
import static eu.internetofus.common.model.ValidableAsserts.assertIsNotValid;
import static eu.internetofus.common.model.ValidableAsserts.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.WeNetIntegrationExtension;
import eu.internetofus.common.components.WeNetValidateContext;
import eu.internetofus.common.model.ModelTestCase;
import eu.internetofus.common.model.TimeManager;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test the {@link CommunityMember}.
 *
 * @see CommunityMember
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetIntegrationExtension.class)
public class CommunityMemberTest extends ModelTestCase<CommunityMember> {

  /**
   * {@inheritDoc}
   */
  @Override
  public CommunityMember createModelExample(final int index) {

    final var model = new CommunityMember();
    model.userId = "User_" + index;
    model.privileges = new ArrayList<>();
    model.privileges.add("privilege_" + index);
    return model;
  }

  /**
   * Create an example model that has the specified index.
   *
   * @param index       to use in the example.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @return the created community member.
   */
  public Future<CommunityMember> createModelExample(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    return testContext
        .assertComplete(StoreServices.storeProfile(new WeNetUserProfile(), vertx, testContext).compose(stored -> {

          final var model = this.createModelExample(index);
          model.userId = stored.id;
          return Future.succeededFuture(model);

        }));

  }

  /**
   * Check that an empty model is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityMember#validate(WeNetValidateContext)
   */
  @Test
  public void shouldEmptyModelNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new CommunityMember();
    assertIsNotValid(model, "userId", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that the {@link #createModelExample(int)} is not valid.
   *
   * @param index       to verify
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityMember#validate(WeNetValidateContext)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldBasicExampleNotBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(index);
    assertIsNotValid(model, "userId", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that the {@link #createModelExample(int, Vertx, VertxTestContext)} is
   * valid.
   *
   * @param index       to verify
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityMember#validate(WeNetValidateContext)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(index, vertx, testContext).onSuccess(model -> {

      assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that a {@code null} user identifier is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityMember#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithANullUserId(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.userId = null;
    assertIsNotValid(model, "userId", new WeNetValidateContext("codePrefix", vertx), testContext);
  }

  /**
   * Check that an undefined user identifier is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityMember#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithAnUdefinedUserId(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.userId = UUID.randomUUID().toString();
    assertIsNotValid(model, "userId", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Should merge with {@code null}
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#merge(CommunityProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeWithNull(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> assertCanMerge(target, null,
        new WeNetValidateContext("codePrefix", vertx), testContext, merged -> assertThat(merged).isSameAs(target)));

  }

  /**
   * Should merge two examples.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityMember#merge(CommunityMember, WeNetValidateContext)
   */
  @Test
  public void shouldMergeExamples(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      target._creationTs = 10000;
      target._lastUpdateTs = TimeManager.now();
      this.createModelExample(2, vertx, testContext).onSuccess(source -> {

        assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {
          assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
          source.userId = target.userId;
          source._creationTs = target._creationTs;
          source._lastUpdateTs = target._lastUpdateTs;
          assertThat(merged).isEqualTo(source);
        });
      });
    });

  }

  /**
   * Should not merge empty model.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityMember#merge(CommunityMember, WeNetValidateContext)
   */
  @Test
  public void shouldMergeEmptyModel(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = new CommunityMember();
      assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {
        assertThat(merged).isEqualTo(target).isNotEqualTo(source);
      });

    });

  }

  /**
   * Should merge only privileges.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityMember#merge(CommunityMember, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergePrivileges(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      final var source = new CommunityMember();
      source.privileges = new ArrayList<>(target.privileges);
      source.privileges.add("New privilege");
      assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {
        assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
        source.userId = target.userId;
        source._creationTs = target._creationTs;
        source._lastUpdateTs = target._lastUpdateTs;
        assertThat(merged).isEqualTo(source);
      });
    });

  }

  /**
   * Should update with {@code null}
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#update(CommunityProfile, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateWithNull(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      assertCanUpdate(target, null, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {
        assertThat(updated).isSameAs(target);
      });
    });

  }

  /**
   * Should update two examples.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityMember#update(CommunityMember, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateExamples(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      target._creationTs = 10000;
      target._lastUpdateTs = TimeManager.now();
      this.createModelExample(2, vertx, testContext).onSuccess(source -> {

        assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {
          assertThat(updated).isNotEqualTo(target).isNotEqualTo(source);
          source.userId = target.userId;
          source._creationTs = target._creationTs;
          source._lastUpdateTs = target._lastUpdateTs;
          assertThat(updated).isEqualTo(source);
        });
      });
    });

  }

  /**
   * Should iterate over identifiers.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityMember#update(CommunityMember, WeNetValidateContext)
   */
  @Test
  public void shouldIterateByIds(final Vertx vertx, final VertxTestContext testContext) {

    var future = Future.succeededFuture(Collections.synchronizedList(new ArrayList<CommunityMember>()));
    for (var i = 0; i < 10; i++) {

      final var index = i;
      future = future.compose(models -> this.createModelExample(index, vertx, testContext).map(model -> {

        models.add(model);
        return models;
      }));

    }

    testContext.assertComplete(future).onSuccess(models -> {

      final var first = models.get(0);
      final var second = models.get(1);
      testContext.verify(() -> {
        final var iterable = CommunityMember.idsIterable(models);
        var iterator = iterable.iterator();
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).isSameAs(first.userId);
        iterator.remove();
        iterator = iterable.iterator();
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).isSameAs(second.userId);
      });

      testContext.completeNow();

    });

  }

}
