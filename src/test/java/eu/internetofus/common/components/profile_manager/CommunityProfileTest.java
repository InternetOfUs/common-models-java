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

import static eu.internetofus.common.components.AbstractComponentMocker.createClientWithDefaultSession;
import static eu.internetofus.common.components.MergesTest.assertCanMerge;
import static eu.internetofus.common.components.MergesTest.assertCannotMerge;
import static eu.internetofus.common.components.UpdatesTest.assertCanUpdate;
import static eu.internetofus.common.components.UpdatesTest.assertCannotUpdate;
import static eu.internetofus.common.components.ValidationsTest.assertIsNotValid;
import static eu.internetofus.common.components.ValidationsTest.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.TimeManager;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ModelTestCase;
import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.components.ValidationsTest;
import eu.internetofus.common.components.service.App;
import eu.internetofus.common.components.service.WeNetService;
import eu.internetofus.common.components.service.WeNetServiceSimulator;
import eu.internetofus.common.components.service.WeNetServiceSimulatorMocker;
import eu.internetofus.common.components.task_manager.ProtocolNormTest;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
@ExtendWith(VertxExtension.class)
public class CommunityProfileTest extends ModelTestCase<CommunityProfile> {

  /**
   * The profile manager mocked server.
   */
  protected static WeNetProfileManagerMocker profileManagerMocker;

  /**
   * The profile manager mocked server.
   */
  protected static WeNetServiceSimulatorMocker serviceMocker;

  /**
   * Start the mocker server.
   */
  @BeforeAll
  public static void startMocker() {

    profileManagerMocker = WeNetProfileManagerMocker.start();
    serviceMocker = WeNetServiceSimulatorMocker.start();
  }

  /**
   * Stop the mocker server.
   */
  @AfterAll
  public static void stopMockers() {

    profileManagerMocker.stopServer();
    serviceMocker.stopServer();
  }

  /**
   * Register the necessary services before to test.
   *
   * @param vertx event bus to register the necessary services.
   */
  @BeforeEach
  public void registerServices(final Vertx vertx) {

    final var client = createClientWithDefaultSession(vertx);
    final var profileManagerConf = profileManagerMocker.getComponentConfiguration();
    WeNetProfileManager.register(vertx, client, profileManagerConf);
    final var serviceConf = serviceMocker.getComponentConfiguration();
    WeNetService.register(vertx, client, serviceConf);
    WeNetServiceSimulator.register(vertx, client, serviceConf);

  }

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
   * @see CommunityProfile#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldBasicExampleNotBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(index);
    assertIsNotValid(model, "appId", vertx, testContext);

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

    return testContext.assertComplete(StoreServices.storeApp(new App(), vertx, testContext).compose(storedApp -> {

      return new CommunityMemberTest().createModelExample(index, vertx, testContext).compose(member -> {

        final var model = this.createModelExample(index);
        model.appId = storedApp.appId;
        model.members.clear();
        model.members.add(member);
        model.norms = null;
        model.socialPractices = null;
        return Future.succeededFuture(model);

      });
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
   * @see CommunityProfile#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(index, vertx, testContext).onSuccess(model -> assertIsValid(model, vertx, testContext));

  }

  /**
   * Check that a {@link #createModelExample(int, Vertx, VertxTestContext)} with
   * multiple members is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#validate(String, Vertx)
   */
  @Test
  public void shouldExampleWithMultipleMembersBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      new CommunityMemberTest().createModelExample(2, vertx, testContext).onSuccess(member2 -> {
        new CommunityMemberTest().createModelExample(3, vertx, testContext).onSuccess(member3 -> {

          model.members.add(member2);
          model.members.add(member3);
          assertIsValid(model, vertx, testContext);

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
   * @see CommunityProfile#validate(String, Vertx)
   */
  @Test
  public void shouldExampleWithMultipleNormsBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.norms.add(new ProtocolNormTest().createModelExample(2));
      model.norms.add(new ProtocolNormTest().createModelExample(3));
      assertIsValid(model, vertx, testContext);

    });

  }

  /**
   * Check that a {@link #createModelExample(int, Vertx, VertxTestContext)} with
   * multiple social practices is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#validate(String, Vertx)
   */
  @Test
  public void shouldExampleWithMultipleSocialPracticesBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.socialPractices.add(new SocialPracticeTest().createModelExample(2));
      model.socialPractices.add(new SocialPracticeTest().createModelExample(3));
      for (var i = 0; i < model.socialPractices.size(); i++) {

        model.socialPractices.get(i).norms.get(0).id = String.valueOf(i);
      }

      assertIsValid(model, vertx, testContext);

    });

  }

  /**
   * Check that it is not valid if the identifier exist.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithADefinedId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      StoreServices.storeCommunityExample(200, vertx, testContext).onSuccess(storedModel -> {

        model.id = storedModel.id;
        assertIsNotValid(model, "id", vertx, testContext);

      });
    });
  }

  /**
   * Check that a {@code null} app identifier is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithANullAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.appId = null;
      assertIsNotValid(model, "appId", vertx, testContext);

    });
  }

  /**
   * Check that an undefined app identifier is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithUndefinedAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.appId = "Undefined identifier";
      assertIsNotValid(model, "appId", vertx, testContext);

    });
  }

  /**
   * Check that an invalid name is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithInvalidName(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.name = ValidationsTest.STRING_256;
      assertIsNotValid(model, "name", vertx, testContext);

    });
  }

  /**
   * Check that an invalid description is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithInvalidDescription(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.description = ValidationsTest.STRING_1024;
      assertIsNotValid(model, "description", vertx, testContext);

    });
  }

  /**
   * Check that an invalid keyword is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithInvalidKeyword(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.keywords.add(ValidationsTest.STRING_256);
      assertIsNotValid(model, "keywords[1]", vertx, testContext);

    });
  }

  /**
   * Check that an invalid member is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithInvalidMember(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.members.add(new CommunityMemberTest().createModelExample(2));
      assertIsNotValid(model, "members[1].userId", vertx, testContext);

    });
  }

  /**
   * Check that an invalid social practice is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithInvalidSocialPractice(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.socialPractices.add(new SocialPracticeTest().createModelExample(2));
      model.socialPractices.get(1).label = ValidationsTest.STRING_256;
      assertIsNotValid(model, "socialPractices[1].label", vertx, testContext);

    });
  }

  /**
   * Check that an invalid norm is not valid.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithInvalidNorm(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(model -> {

      model.norms.add(new ProtocolNormTest().createModelExample(2));
      model.norms.get(1).thenceforth = null;
      assertIsNotValid(model, "norms[1].thenceforth", vertx, testContext);

    });
  }

  /**
   * Should merge with {@code null}
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#merge(CommunityProfile, String, Vertx)
   */
  @Test
  public void shouldMergeWithNull(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      assertCanMerge(target, null, vertx, testContext, merged -> {
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
   * @see CommunityProfile#merge(CommunityProfile, String, Vertx)
   */
  @Test
  public void shouldMergeExamples(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      target._creationTs = 10000;
      target._lastUpdateTs = TimeManager.now();
      this.createModelExample(2, vertx, testContext).onSuccess(source -> {

        assertCanMerge(target, source, vertx, testContext, merged -> {
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
   * @see CommunityProfile#merge(CommunityProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithBadAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      final var source = new CommunityProfile();
      source.appId = "Undefined application id";
      assertCannotMerge(target, source, "appId", vertx, testContext);
    });

  }

  /**
   * Should not merge with a bad name.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#merge(CommunityProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithBadName(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      final var source = new CommunityProfile();
      source.name = ValidationsTest.STRING_256;
      assertCannotMerge(target, source, "name", vertx, testContext);
    });

  }

  /**
   * Should not merge with a bad description.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#merge(CommunityProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithBadDescription(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      final var source = new CommunityProfile();
      source.description = ValidationsTest.STRING_1024;
      assertCannotMerge(target, source, "description", vertx, testContext);
    });

  }

  /**
   * Should not merge with a bad keyword.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#merge(CommunityProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithBadKeywords(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      final var source = new CommunityProfile();
      source.keywords = new ArrayList<>(target.keywords);
      source.keywords.add(ValidationsTest.STRING_256);
      assertCannotMerge(target, source, "keywords[1]", vertx, testContext);
    });

  }

  /**
   * Should not merge with a bad social practice.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#merge(CommunityProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithBadSocialPractices(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      final var source = new CommunityProfile();
      source.socialPractices = new ArrayList<>(target.socialPractices);
      source.socialPractices.add(new SocialPracticeTest().createModelExample(2));
      source.socialPractices.get(1).label = ValidationsTest.STRING_256;
      assertCannotMerge(target, source, "socialPractices[1].label", vertx, testContext);
    });

  }

  /**
   * Should not merge with a bad norm.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#merge(CommunityProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithBadNorms(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      final var source = new CommunityProfile();
      source.norms = new ArrayList<>(target.norms);
      source.norms.add(new ProtocolNormTest().createModelExample(2));
      source.norms.get(1).whenever = null;
      assertCannotMerge(target, source, "norms[1].whenever", vertx, testContext);
    });

  }

  /**
   * Should not merge with a bad member.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#merge(CommunityProfile, String, Vertx)
   */
  @Test
  public void shouldNotMergeWithBadMembers(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      final var source = new CommunityProfile();
      source.members = new ArrayList<>(target.members);
      source.members.add(new CommunityMemberTest().createModelExample(2));
      assertCannotMerge(target, source, "members[1].userId", vertx, testContext);
    });

  }

  /**
   * Check merge social practices profiles.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#merge(WeNetUserProfile, String, Vertx)
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
      assertCanMerge(target, source, vertx, testContext, merged -> {

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
   * @see CommunityProfile#update(CommunityProfile, String, Vertx)
   */
  @Test
  public void shouldUpdateWithNull(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {

      assertCanUpdate(target, null, vertx, testContext, updated -> {
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
   * @see CommunityProfile#update(CommunityProfile, String, Vertx)
   */
  @Test
  public void shouldUpdateExamples(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      target._creationTs = 10000;
      target._lastUpdateTs = TimeManager.now();
      this.createModelExample(2, vertx, testContext).onSuccess(source -> {

        assertCanUpdate(target, source, vertx, testContext, updated -> {
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
   * @see CommunityProfile#update(CommunityProfile, String, Vertx)
   */
  @Test
  public void shouldNotUpdateWithBadAppId(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      final var source = Model.fromJsonObject(target.toJsonObject(), CommunityProfile.class);
      source.appId = "Undefined application id";
      assertCannotUpdate(target, source, "appId", vertx, testContext);
    });

  }

  /**
   * Should not update with a bad name.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#update(CommunityProfile, String, Vertx)
   */
  @Test
  public void shouldNotUpdateWithBadName(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      final var source = Model.fromJsonObject(target.toJsonObject(), CommunityProfile.class);
      source.name = ValidationsTest.STRING_256;
      assertCannotUpdate(target, source, "name", vertx, testContext);
    });

  }

  /**
   * Should not update with a bad description.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#update(CommunityProfile, String, Vertx)
   */
  @Test
  public void shouldNotUpdateWithBadDescription(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      final var source = Model.fromJsonObject(target.toJsonObject(), CommunityProfile.class);
      source.description = ValidationsTest.STRING_1024;
      assertCannotUpdate(target, source, "description", vertx, testContext);
    });

  }

  /**
   * Should not update with a bad keyword.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#update(CommunityProfile, String, Vertx)
   */
  @Test
  public void shouldNotUpdateWithBadKeywords(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      final var source = Model.fromJsonObject(target.toJsonObject(), CommunityProfile.class);
      source.keywords = new ArrayList<>(target.keywords);
      source.keywords.add(ValidationsTest.STRING_256);
      assertCannotUpdate(target, source, "keywords[1]", vertx, testContext);
    });

  }

  /**
   * Should not update with a bad social practice.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#update(CommunityProfile, String, Vertx)
   */
  @Test
  public void shouldNotUpdateWithBadSocialPractices(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      final var source = Model.fromJsonObject(target.toJsonObject(), CommunityProfile.class);
      source.socialPractices = new ArrayList<>(target.socialPractices);
      source.socialPractices.add(new SocialPracticeTest().createModelExample(2));
      source.socialPractices.get(1).label = ValidationsTest.STRING_256;
      assertCannotUpdate(target, source, "socialPractices[1].label", vertx, testContext);
    });

  }

  /**
   * Should not update with a bad norm.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#update(CommunityProfile, String, Vertx)
   */
  @Test
  public void shouldNotUpdateWithBadNorms(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      final var source = Model.fromJsonObject(target.toJsonObject(), CommunityProfile.class);
      source.norms = new ArrayList<>(target.norms);
      source.norms.add(new ProtocolNormTest().createModelExample(2));
      source.norms.get(1).thenceforth = null;
      assertCannotUpdate(target, source, "norms[1].thenceforth", vertx, testContext);
    });

  }

  /**
   * Should not update with a bad member.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see CommunityProfile#update(CommunityProfile, String, Vertx)
   */
  @Test
  public void shouldNotUpdateWithBadMembers(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext).onSuccess(target -> {
      final var source = Model.fromJsonObject(target.toJsonObject(), CommunityProfile.class);
      source.members = new ArrayList<>(target.members);
      source.members.add(new CommunityMemberTest().createModelExample(2));
      assertCannotUpdate(target, source, "members[1].userId", vertx, testContext);
    });

  }

  /**
   * Check update social practices profiles.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#update(WeNetUserProfile, String, Vertx)
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
      assertCanUpdate(target, source, vertx, testContext, updated -> {

        assertThat(updated.socialPractices).isNotEqualTo(target.socialPractices).isEqualTo(source.socialPractices);
        assertThat(updated.socialPractices.get(0).id).isNotEmpty();
        assertThat(updated.socialPractices.get(1).id).isEqualTo("1");
        assertThat(updated.socialPractices.get(2).id).isNotEmpty();

      });
    });
  }
}
