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
import eu.internetofus.common.model.TimeManager;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test the {@link CommunityProfile}.
 *
 * @see CommunityProfile
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(WeNetIntegrationExtension.class)
public class CommunityProfileTest extends ModelTestCase<CommunityProfile> {

  /**
   * {@inheritDoc}
   */
  @Override
  public CommunityProfile createModelExample(final int index) {

    final var model = new CommunityProfile();
    model.appId = "AppId_" + index;
    model.id = "Id_" + index;
    model.keywords = new ArrayList<>();
    model.keywords.add("keyword" + index);
    model.members = new ArrayList<>();
    model.members.add(new CommunityMemberTest().createModelExample(index));
    model.name = "Name_" + index;
    model.description = "Description_" + index;
    model.norms = new ArrayList<>();
    model.norms.add(new ProtocolNormTest().createModelExample(index - 1));
    model.norms.add(new ProtocolNormTest().createModelExample(index));
    model.norms.add(new ProtocolNormTest().createModelExample(index + 1));
    model.socialPractices = new ArrayList<>();
    model.socialPractices.add(new SocialPracticeTest().createModelExample(index - 1));
    model.socialPractices.add(new SocialPracticeTest().createModelExample(index));
    model.socialPractices.add(new SocialPracticeTest().createModelExample(index + 1));
    return model;
  }

  /**
   * Check that the {@link #createModelExample(int)} is not valid.
   *
   * @param index       to verify
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#validate(WeNetValidateContext)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldBasicExampleNotBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(index);
    assertIsNotValid(model, "members[0].userId", new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Create an example model that has the specified index.
   *
   * @param index       to use in the example.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @return the community profile.
   */
  public Future<CommunityProfile> createModelExample(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    final Promise<CommunityProfile> promise = Promise.promise();
    StoreServices.storeApp(new App(), vertx, testContext).onSuccess(storedApp -> {
      testContext.assertComplete(new CommunityMemberTest().createModelExample(index, vertx, testContext))
          .onSuccess(member -> {

            final var model = this.createModelExample(index);
            model.appId = storedApp.appId;
            model.members.clear();
            model.members.add(member);
            model.norms = null;
            model.socialPractices = null;
            promise.complete(model);

          });

    });

    return promise.future();

  }

  /**
   * Create an example model that has the specified index.
   *
   * @param index       to use in the example.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @return the community profile.
   */
  public Future<CommunityProfile> createModelExampleWithTaskTypes(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    return this.createModelExample(index, vertx, testContext)
        .compose(model -> StoreServices.storeTaskType(new TaskType(), vertx, testContext).map(storedTaskType -> {

          model.taskTypeIds = new ArrayList<>();
          model.taskTypeIds.add(storedTaskType.id);
          return model;

        })).compose(model -> StoreServices.storeTaskType(new TaskType(), vertx, testContext).map(storedTaskType -> {

          model.taskTypeIds.add(storedTaskType.id);
          return model;

        })).compose(model -> StoreServices.storeTaskType(new TaskType(), vertx, testContext).map(storedTaskType -> {

          model.taskTypeIds.add(storedTaskType.id);
          return model;

        }));

  }

  /**
   * Check that the {@link #createModelExample(int, Vertx, VertxTestContext)} is
   * valid.
   *
   * @param index       to verify
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#validate(WeNetValidateContext)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(index, vertx, testContext)
        .onSuccess(model -> assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext));

  }

  /**
   * Check that a {@link #createModelExample(int, Vertx, VertxTestContext)} with
   * multiple members is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldExampleWithMultipleMembersBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      new CommunityMemberTest().createModelExample(2, vertx, testContext).onSuccess(member2 -> {
        new CommunityMemberTest().createModelExample(3, vertx, testContext).onSuccess(member3 -> {

          model.members.add(member2);
          model.members.add(member3);
          assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext);

        });
      });
    });

  }

  /**
   * Check that a {@link #createModelExample(int, Vertx, VertxTestContext)} with
   * multiple norms is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldExampleWithMultipleNormsBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {
      model.norms = new ArrayList<>();
      model.norms.add(new ProtocolNormTest().createModelExample(1));
      model.norms.add(new ProtocolNormTest().createModelExample(2));
      model.norms.add(new ProtocolNormTest().createModelExample(3));
      assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that a {@link #createModelExample(int, Vertx, VertxTestContext)} with
   * multiple social practices is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldExampleWithMultipleSocialPracticesBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.socialPractices = new ArrayList<>();
      model.socialPractices.add(new SocialPracticeTest().createModelExample(1));
      model.socialPractices.add(new SocialPracticeTest().createModelExample(2));
      model.socialPractices.add(new SocialPracticeTest().createModelExample(3));

      assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext);

    });

  }

  /**
   * Check that it is not valid if the identifier exist.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithADefinedId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      StoreServices.storeCommunityExample(200, vertx, testContext).onSuccess(storedModel -> {

        model.id = storedModel.id;
        assertIsNotValid(model, "id", new WeNetValidateContext("codePrefix", vertx), testContext);

      });
    });
  }

  /**
   * Check that an empty community is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldEmptyCommunityHasToBeValid(final Vertx vertx, final VertxTestContext testContext) {

    assertIsValid(new CommunityProfile(), new WeNetValidateContext("codePrefix", vertx), testContext);

  }

  /**
   * Check that an undefined app identifier is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithUndefinedAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.appId = "Undefined identifier";
      assertIsNotValid(model, "appId", new WeNetValidateContext("codePrefix", vertx), testContext);

    });
  }

  /**
   * Check that an invalid member is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithInvalidMember(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.members.add(new CommunityMemberTest().createModelExample(2));
      assertIsNotValid(model, "members[1].userId", new WeNetValidateContext("codePrefix", vertx), testContext);

    });
  }

  /**
   * Check that an invalid social practice is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithInvalidSocialPractice(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.socialPractices = new ArrayList<>();
      model.socialPractices.add(new SocialPracticeTest().createModelExample(1));
      model.socialPractices.add(new SocialPracticeTest().createModelExample(2));
      model.socialPractices.get(1).materials.get(0).name = null;
      assertIsNotValid(model, "socialPractices[1].materials[0].name", new WeNetValidateContext("codePrefix", vertx),
          testContext);

    });
  }

  /**
   * Check that an invalid norm is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#validate(WeNetValidateContext)
   */
  @Test
  public void shouldNotBeValidWithInvalidNorm(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.norms = new ArrayList<>();
      model.norms.add(new ProtocolNormTest().createModelExample(1));
      model.norms.add(new ProtocolNormTest().createModelExample(2));
      model.norms.get(1).thenceforth = null;
      assertIsNotValid(model, "norms[1].thenceforth", new WeNetValidateContext("codePrefix", vertx), testContext);

    });
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

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      assertCanMerge(target, null, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {
        assertThat(merged).isSameAs(target);
      });
    });

  }

  /**
   * Should merge two examples.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#merge(CommunityProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeExamples(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      target._creationTs = 10000;
      target._lastUpdateTs = TimeManager.now();
      this.createModelExample(2, vertx, testContext).onSuccess(source -> {

        assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {
          assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
          source.id = target.id;
          source._creationTs = target._creationTs;
          source._lastUpdateTs = target._lastUpdateTs;
          assertThat(merged).isEqualTo(source);
        });
      });
    });

  }

  /**
   * Should not merge with a bad application identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#merge(CommunityProfile, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithBadAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      final var source = new CommunityProfile();
      source.appId = "Undefined application id";
      assertCannotMerge(target, source, "appId", new WeNetValidateContext("codePrefix", vertx), testContext);
    });

  }

  /**
   * Should not merge with a bad social practice.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#merge(CommunityProfile, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithBadSocialPractices(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      final var source = new CommunityProfile();
      source.socialPractices = new ArrayList<>();
      source.socialPractices.add(new SocialPracticeTest().createModelExample(1));
      source.socialPractices.add(new SocialPracticeTest().createModelExample(2));
      source.socialPractices.get(1).materials.get(0).name = null;
      assertCannotMerge(target, source, "socialPractices[1].materials[0].name",
          new WeNetValidateContext("codePrefix", vertx), testContext);
    });

  }

  /**
   * Should not merge with a bad norm.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#merge(CommunityProfile, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithBadNorms(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      final var source = new CommunityProfile();
      source.norms = new ArrayList<>();
      source.norms.add(new ProtocolNormTest().createModelExample(0));
      source.norms.add(new ProtocolNormTest().createModelExample(1));
      source.norms.add(new ProtocolNormTest().createModelExample(2));
      source.norms.get(1).whenever = null;
      assertCannotMerge(target, source, "norms[1].whenever", new WeNetValidateContext("codePrefix", vertx),
          testContext);
    });

  }

  /**
   * Should not merge with a bad member.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#merge(CommunityProfile, WeNetValidateContext)
   */
  @Test
  public void shouldNotMergeWithBadMembers(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      final var source = new CommunityProfile();
      source.members = new ArrayList<>(target.members);
      source.members.add(new CommunityMemberTest().createModelExample(2));
      assertCannotMerge(target, source, "members[1].userId", new WeNetValidateContext("codePrefix", vertx),
          testContext);
    });

  }

  /**
   * Check merge social practices profiles.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldMergeWithSocialPractices(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      target.socialPractices = new ArrayList<>();
      target.socialPractices.add(new SocialPractice());
      target.socialPractices.get(0).id = "1";
      final var source = new CommunityProfile();
      source.socialPractices = new ArrayList<>();
      source.socialPractices.add(new SocialPractice());
      source.socialPractices.add(new SocialPractice());
      source.socialPractices.add(new SocialPractice());
      source.socialPractices.get(1).id = "1";
      assertCanMerge(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, merged -> {

        assertThat(merged.socialPractices).isNotEqualTo(target.socialPractices).isEqualTo(source.socialPractices);
        assertThat(merged.socialPractices.get(0).id).isNotEmpty();
        assertThat(merged.socialPractices.get(1).id).isEqualTo("1");
        assertThat(merged.socialPractices.get(2).id).isNotEmpty();

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
   * @see CommunityProfile#update(CommunityProfile, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateExamples(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      target._creationTs = 10000;
      target._lastUpdateTs = TimeManager.now();
      this.createModelExample(2, vertx, testContext).onSuccess(source -> {

        assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {
          assertThat(updated).isNotEqualTo(target).isNotEqualTo(source);
          source.id = target.id;
          source._creationTs = target._creationTs;
          source._lastUpdateTs = target._lastUpdateTs;
          assertThat(updated).isEqualTo(source);
        });
      });
    });

  }

  /**
   * Should not update with a bad application identifier.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#update(CommunityProfile, WeNetValidateContext)
   */
  @Test
  public void shouldNotUpdateWithBadAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      final var source = Model.fromJsonObject(target.toJsonObject(), CommunityProfile.class);
      source.appId = "Undefined application id";
      assertCannotUpdate(target, source, "appId", new WeNetValidateContext("codePrefix", vertx), testContext);
    });

  }

  /**
   * Should not update with a bad social practice.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#update(CommunityProfile, WeNetValidateContext)
   */
  @Test
  public void shouldNotUpdateWithBadSocialPractices(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      final var source = Model.fromJsonObject(target.toJsonObject(), CommunityProfile.class);
      source.socialPractices = new ArrayList<>();
      source.socialPractices.add(new SocialPracticeTest().createModelExample(1));
      source.socialPractices.add(new SocialPracticeTest().createModelExample(2));
      source.socialPractices.get(1).materials.get(0).name = null;
      assertCannotUpdate(target, source, "socialPractices[1].materials[0].name",
          new WeNetValidateContext("codePrefix", vertx), testContext);
    });

  }

  /**
   * Should not update with a bad norm.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#update(CommunityProfile, WeNetValidateContext)
   */
  @Test
  public void shouldNotUpdateWithBadNorms(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      final var source = Model.fromJsonObject(target.toJsonObject(), CommunityProfile.class);
      source.norms = new ArrayList<>();
      source.norms.add(new ProtocolNormTest().createModelExample(0));
      source.norms.add(new ProtocolNormTest().createModelExample(1));
      source.norms.add(new ProtocolNormTest().createModelExample(2));
      source.norms.get(1).thenceforth = null;
      assertCannotUpdate(target, source, "norms[1].thenceforth", new WeNetValidateContext("codePrefix", vertx),
          testContext);
    });

  }

  /**
   * Should not update with a bad member.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#update(CommunityProfile, WeNetValidateContext)
   */
  @Test
  public void shouldNotUpdateWithBadMembers(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      final var source = Model.fromJsonObject(target.toJsonObject(), CommunityProfile.class);
      source.members = new ArrayList<>(target.members);
      source.members.add(new CommunityMemberTest().createModelExample(2));
      assertCannotUpdate(target, source, "members[1].userId", new WeNetValidateContext("codePrefix", vertx),
          testContext);
    });

  }

  /**
   * Check update social practices profiles.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#update(WeNetUserProfile, WeNetValidateContext)
   */
  @Test
  public void shouldUpdateWithSocialPractices(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      target.socialPractices = new ArrayList<>();
      target.socialPractices.add(new SocialPractice());
      target.socialPractices.get(0).id = "1";
      final var source = Model.fromJsonObject(target.toJsonObject(), CommunityProfile.class);
      source.socialPractices = new ArrayList<>();
      source.socialPractices.add(new SocialPractice());
      source.socialPractices.add(new SocialPractice());
      source.socialPractices.add(new SocialPractice());
      source.socialPractices.get(1).id = "1";
      assertCanUpdate(target, source, new WeNetValidateContext("codePrefix", vertx), testContext, updated -> {

        assertThat(updated.socialPractices).isNotEqualTo(target.socialPractices).isEqualTo(source.socialPractices);
        assertThat(updated.socialPractices.get(0).id).isNotEmpty();
        assertThat(updated.socialPractices.get(1).id).isEqualTo("1");
        assertThat(updated.socialPractices.get(2).id).isNotEmpty();

      });
    });
  }

  /**
   * Check that the
   * {@link #createModelExampleWithTaskTypes(int, Vertx, VertxTestContext)} is
   * valid.
   *
   * @param index       to verify
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#validate(WeNetValidateContext)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleBeValidWithTaskTypes(final int index, final Vertx vertx,
      final VertxTestContext testContext) {

    this.createModelExampleWithTaskTypes(index, vertx, testContext)
        .onSuccess(model -> assertIsValid(model, new WeNetValidateContext("codePrefix", vertx), testContext));

  }

}
