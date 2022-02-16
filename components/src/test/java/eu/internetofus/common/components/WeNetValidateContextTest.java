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

package eu.internetofus.common.components;

import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.models.SocialNetworkRelationshipType;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import java.util.Arrays;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test the {@link WeNetValidateContext}.
 *
 * @see WeNetValidateContext
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetIntegrationExtension.class)
public class WeNetValidateContextTest {

  /**
   * Check that can not obtain model with undefined or {@code null} arguments.
   */
  @Test
  public void shouldNoGetUndefinedModel() {

    final var cache = new WeNetValidateContext("codePrefix", null);
    assertThat(cache.getModel(null, (Class<?>) null)).isNull();
    assertThat(cache.getModel("undefined", (Class<?>) null)).isNull();
    assertThat(cache.getModel(null, String.class)).isNull();
    assertThat(cache.getModel("undefined", String.class)).isNull();
  }

  /**
   * Check that can not obtain model with the same key if the type is different.
   */
  @Test
  public void shouldNoGetModelWithSameKeyDiferentClass() {

    final var cache = new WeNetValidateContext("codePrefix", null);
    cache.setModel("id", 3);
    assertThat(cache.getModel("id", Number.class)).isNull();

  }

  /**
   * Check that set/get model.
   */
  @Test
  public void shouldSetGetModel() {

    final var cache = new WeNetValidateContext("codePrefix", null);
    cache.setModel("id", "value");
    assertThat(cache.getModel("id", String.class)).isEqualTo("value");
    assertThat(cache.existModel("id", String.class)).isTrue();

  }

  /**
   * Check that set {@code null} model.
   */
  @Test
  public void shouldNotSetNullModel() {

    final var cache = new WeNetValidateContext("codePrefix", null);
    cache.setModel("id", null);
    assertThat(cache.existModel("id", String.class)).isFalse();

  }

  /**
   * Should not exist model with {@code null} id.
   */
  @Test
  public void shouldNotExistWithNullId() {

    final var cache = new WeNetValidateContext("codePrefix", null);
    assertThat(cache.existModel(null, String.class)).isFalse();

  }

  /**
   * Should not exist model with {@code null} type.
   */
  @Test
  public void shouldNotExistWithNullType() {

    final var cache = new WeNetValidateContext("codePrefix", null);
    assertThat(cache.existModel("id", null)).isFalse();

  }

  /**
   * Check that validate defined by id field.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetValidateContext#validateDefinedIdField
   */
  @Test
  public void shouldValidateDefinedIdField(final Vertx vertx, final VertxTestContext testContext) {

    final var id = UUID.randomUUID().toString();
    final var cache = new WeNetValidateContext("codePrefix", vertx);
    testContext
        .assertComplete(cache.validateDefinedIdField("name", id, String.class, any -> Future.succeededFuture(true),
            Future.succeededFuture()))
        .onSuccess(any -> testContext
            .assertComplete(cache.validateDefinedIdField("name", id, String.class, null, Future.succeededFuture()))
            .onSuccess(any2 -> testContext.completeNow()));

  }

  /**
   * Check that fail validate defined by id field.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetValidateContext#validateDefinedIdField
   */
  @Test
  public void shouldFailValidateDefinedIdField(final Vertx vertx, final VertxTestContext testContext) {

    final var id = UUID.randomUUID().toString();
    final var cache = new WeNetValidateContext("codePrefix", vertx);
    testContext.assertFailure(cache.validateDefinedIdField("name", id, String.class,
        any -> Future.failedFuture("Error not found"), Future.succeededFuture())).onFailure(
            any -> testContext
                .assertFailure(cache.validateDefinedIdField("name", id, String.class,
                    any2 -> Future.succeededFuture(false), Future.succeededFuture()))
                .onFailure(any2 -> testContext.completeNow()));

  }

  /**
   * Check that validate not defined by id field.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetValidateContext#validateNotDefinedIdField
   */
  @Test
  public void shouldValidateNotDefinedIdField(final Vertx vertx, final VertxTestContext testContext) {

    final var id = UUID.randomUUID().toString();
    final var cache = new WeNetValidateContext("codePrefix", vertx);
    testContext
        .assertComplete(cache.validateNotDefinedIdField("name", id, String.class, any -> Future.succeededFuture(false),
            Future.succeededFuture()))
        .onSuccess(any -> testContext
            .assertComplete(cache.validateNotDefinedIdField("name", id, String.class,
                any2 -> Future.succeededFuture(false), Future.succeededFuture()))
            .onSuccess(any2 -> testContext.completeNow()));

  }

  /**
   * Check that fail validate not defined by id field.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetValidateContext#validateNotDefinedIdField
   */
  @Test
  public void shouldFailValidateNotDefinedIdField(final Vertx vertx, final VertxTestContext testContext) {

    final var id = UUID.randomUUID().toString();
    final var cache = new WeNetValidateContext("codePrefix", vertx);
    testContext
        .assertFailure(cache.validateNotDefinedIdField("name", id, String.class,
            any -> Future.failedFuture("Error not found"), Future.succeededFuture()))
        .onFailure(any -> testContext
            .assertFailure(cache.validateNotDefinedIdField("name", id, String.class,
                any2 -> Future.succeededFuture(true), Future.succeededFuture()))
            .onFailure(any2 -> testContext
                .assertFailure(
                    cache.validateNotDefinedIdField("name", id, String.class, null, Future.succeededFuture()))
                .onFailure(any3 -> testContext.completeNow())));

  }

  /**
   * Check that validate defined model by id field.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetValidateContext#validateDefinedModelByIdField
   */
  @Test
  public void shouldValidateDefinedModelByIdField(final Vertx vertx, final VertxTestContext testContext) {

    final var id = UUID.randomUUID().toString();
    final var cache = new WeNetValidateContext("codePrefix", vertx);
    testContext.assertComplete(
        cache.validateDefinedModelByIdField("name", id, String.class, any -> Future.succeededFuture(id))).onSuccess(
            any -> testContext.assertComplete(cache.validateDefinedModelByIdField("name", id, String.class, null))
                .onSuccess(any2 -> testContext.completeNow()));

  }

  /**
   * Check that fail validate model defined by id field.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetValidateContext#validateDefinedIdField
   */
  @Test
  public void validateDefinedModelByIdField(final Vertx vertx, final VertxTestContext testContext) {

    final var id = UUID.randomUUID().toString();
    final var cache = new WeNetValidateContext("codePrefix", vertx);
    testContext.assertFailure(
        cache.validateDefinedModelByIdField("name", id, String.class, any -> Future.failedFuture("Error not found")))
        .onFailure(any -> testContext.completeNow());

  }

  /**
   * Check that fail validate defined App by id field.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetValidateContext#validateDefinedAppIdField
   */
  @Test
  public void shouldFailValidateDefinedAppIdField(final Vertx vertx, final VertxTestContext testContext) {

    final var id = UUID.randomUUID().toString();
    final var cache = new WeNetValidateContext("codePrefix", vertx);
    testContext.assertFailure(cache.validateDefinedAppIdField("name", id, Future.succeededFuture()))
        .onFailure(any -> testContext.completeNow());

  }

  /**
   * Check that fail validate defined Profile by id field.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetValidateContext#validateDefinedProfileIdField
   */
  @Test
  public void shouldFailValidateDefinedProfileIdField(final Vertx vertx, final VertxTestContext testContext) {

    final var id = UUID.randomUUID().toString();
    final var cache = new WeNetValidateContext("codePrefix", vertx);
    testContext.assertFailure(cache.validateDefinedProfileIdField("name", id, Future.succeededFuture()))
        .onFailure(any -> testContext.completeNow());

  }

  /**
   * Check that fail validate defined Community by id field.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetValidateContext#validateDefinedCommunityIdField
   */
  @Test
  public void shouldFailValidateDefinedCommunityIdField(final Vertx vertx, final VertxTestContext testContext) {

    final var id = UUID.randomUUID().toString();
    final var cache = new WeNetValidateContext("codePrefix", vertx);
    testContext.assertFailure(cache.validateDefinedCommunityIdField("name", id, Future.succeededFuture()))
        .onFailure(any -> testContext.completeNow());

  }

  /**
   * Check that validate not defined Community by id field.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetValidateContext#validateNotDefinedCommunityIdField
   */
  @Test
  public void shouldValidateNotDefinedCommunityIdField(final Vertx vertx, final VertxTestContext testContext) {

    final var id = UUID.randomUUID().toString();
    final var cache = new WeNetValidateContext("codePrefix", vertx);
    testContext.assertComplete(cache.validateNotDefinedCommunityIdField("name", id, Future.succeededFuture()))
        .onSuccess(any -> testContext.completeNow());

  }

  /**
   * Check that fail validate defined some Profile by ids field.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetValidateContext#validateDefinedProfileIdsField
   */
  @Test
  public void shouldFailValidateDefinedProfileIdsField(final Vertx vertx, final VertxTestContext testContext) {

    final var cache = new WeNetValidateContext("codePrefix", vertx);
    testContext.assertFailure(cache.validateDefinedProfileIdsField("name",
        Arrays.asList(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString()),
        Future.succeededFuture())).onFailure(any -> testContext.completeNow());

  }

  /**
   * Check that fail validate defined some TaskType by ids field.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetValidateContext#validateDefinedTaskTypeIdsField
   */
  @Test
  public void shouldFailValidateDefinedTaskTypeIdsField(final Vertx vertx, final VertxTestContext testContext) {

    final var cache = new WeNetValidateContext("codePrefix", vertx);
    testContext.assertFailure(cache.validateDefinedTaskTypeIdsField("name",
        Arrays.asList(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString()),
        Future.succeededFuture())).onFailure(any -> testContext.completeNow());

  }

  /**
   * Check that fail validate defined ids field.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetValidateContext#validateDefinedIdsField
   */
  @Test
  public void shouldFailValidateDefinedIdsField(final Vertx vertx, final VertxTestContext testContext) {

    final var id = UUID.randomUUID().toString();
    final var ids = Arrays.asList(id, UUID.randomUUID().toString(), UUID.randomUUID().toString(), id);
    final var cache = new WeNetValidateContext("codePrefix", vertx);
    testContext
        .assertFailure(cache.validateDefinedIdsField("name", ids, String.class,
            any -> Future.failedFuture("Error not found"), Future.succeededFuture()))
        .onFailure(any -> testContext
            .assertFailure(cache.validateDefinedIdsField("name", ids, String.class,
                any2 -> Future.succeededFuture(false), Future.succeededFuture()))
            .onFailure(any2 -> testContext
                .assertFailure(cache.validateDefinedIdsField("name", ids, String.class,
                    any3 -> Future.succeededFuture(true), Future.succeededFuture()))
                .onFailure(any3 -> testContext.completeNow())));

  }

  /**
   * Check that validate defined ids field.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetValidateContext#validateDefinedIdsField
   */
  @Test
  public void shouldValidateDefinedIdsField(final Vertx vertx, final VertxTestContext testContext) {

    final var ids = Arrays.asList(UUID.randomUUID().toString(), UUID.randomUUID().toString(),
        UUID.randomUUID().toString());
    final var cache = new WeNetValidateContext("codePrefix", vertx);
    testContext
        .assertComplete(cache.validateDefinedIdsField("name", ids, String.class, any -> Future.succeededFuture(true),
            Future.succeededFuture()))
        .onSuccess(any -> testContext
            .assertComplete(cache.validateDefinedIdsField("name", ids, String.class, null, Future.succeededFuture()))
            .onSuccess(any2 -> testContext.completeNow()));

  }

  /**
   * Check that validate not defined Profile by id field.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetValidateContext#validateNotDefinedProfileIdField
   */
  @Test
  public void shouldValidateNotDefinedProfileIdField(final Vertx vertx, final VertxTestContext testContext) {

    final var id = UUID.randomUUID().toString();
    final var cache = new WeNetValidateContext("codePrefix", vertx);
    testContext.assertComplete(cache.validateNotDefinedProfileIdField("name", id, Future.succeededFuture()))
        .onSuccess(any -> testContext.completeNow());

  }

  /**
   * Check that validate not defined Task by id field.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetValidateContext#validateNotDefinedTaskIdField
   */
  @Test
  public void shouldValidateNotDefinedTaskIdField(final Vertx vertx, final VertxTestContext testContext) {

    final var id = UUID.randomUUID().toString();
    final var cache = new WeNetValidateContext("codePrefix", vertx);
    testContext.assertComplete(cache.validateNotDefinedTaskIdField("name", id, Future.succeededFuture()))
        .onSuccess(any -> testContext.completeNow());

  }

  /**
   * Check that validate not defined TaskType by id field.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetValidateContext#validateNotDefinedTaskTypeIdField
   */
  @Test
  public void shouldValidateNotDefinedTaskTypeIdField(final Vertx vertx, final VertxTestContext testContext) {

    final var id = UUID.randomUUID().toString();
    final var cache = new WeNetValidateContext("codePrefix", vertx);
    testContext.assertComplete(cache.validateNotDefinedTaskTypeIdField("name", id, Future.succeededFuture()))
        .onSuccess(any -> testContext.completeNow());

  }

  /**
   * Check that fail validate defined TaskType by id field.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetValidateContext#validateDefinedTaskTypeByIdField
   */
  @Test
  public void shouldFailValidateDefinedTaskTypeByIdField(final Vertx vertx, final VertxTestContext testContext) {

    final var cache = new WeNetValidateContext("codePrefix", vertx);
    testContext.assertFailure(cache.validateDefinedTaskTypeByIdField("name", UUID.randomUUID().toString()))
        .onFailure(any -> testContext.completeNow());

  }

  /**
   * Check that fail validate defined Task by id field.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetValidateContext#validateDefinedTaskByIdField
   */
  @Test
  public void shouldFailValidateDefinedTaskByIdField(final Vertx vertx, final VertxTestContext testContext) {

    final var cache = new WeNetValidateContext("codePrefix", vertx);
    testContext.assertFailure(cache.validateDefinedTaskByIdField("name", UUID.randomUUID().toString()))
        .onFailure(any -> testContext.completeNow());

  }

  /**
   * Check that fail validate defined Profile by id field.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetValidateContext#validateDefinedProfileByIdField
   */
  @Test
  public void shouldFailValidateDefinedProfileByIdField(final Vertx vertx, final VertxTestContext testContext) {

    final var cache = new WeNetValidateContext("codePrefix", vertx);
    testContext.assertFailure(cache.validateDefinedProfileByIdField("name", UUID.randomUUID().toString()))
        .onFailure(any -> testContext.completeNow());

  }

  /**
   * Check that fail validate defined TaskType by id field.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetValidateContext#validateDefinedTaskTypeIdField
   */
  @Test
  public void shouldFailValidateDefinedTaskTypeIdField(final Vertx vertx, final VertxTestContext testContext) {

    final var id = UUID.randomUUID().toString();
    final var cache = new WeNetValidateContext("codePrefix", vertx);
    testContext.assertFailure(cache.validateDefinedTaskTypeIdField("name", id, Future.succeededFuture()))
        .onFailure(any -> testContext.completeNow());

  }

  /**
   * Check that fail validate exists social network relationship field.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetValidateContext#validateExistSocialNetworkRelationshipField
   */
  @Test
  public void shouldFailValidateExistSocialNetworkRelationshipField(final Vertx vertx,
      final VertxTestContext testContext) {

    final var cache = new WeNetValidateContext("codePrefix", vertx);
    testContext.assertFailure(cache.validateExistSocialNetworkRelationshipField("name", UUID.randomUUID().toString(),
        UUID.randomUUID().toString(), UUID.randomUUID().toString(), null, Future.succeededFuture())).onFailure(
            any -> testContext
                .assertFailure(cache.validateExistSocialNetworkRelationshipField("name", UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                    SocialNetworkRelationshipType.acquaintance, Future.succeededFuture()))
                .onFailure(any2 -> testContext.completeNow()));

  }

}
