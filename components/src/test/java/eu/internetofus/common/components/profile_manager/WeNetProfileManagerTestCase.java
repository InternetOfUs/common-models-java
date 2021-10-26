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

package eu.internetofus.common.components.profile_manager;

import static eu.internetofus.common.vertx.ComponentClientAsserts.assertStatusError;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.WeNetComponentTestCase;
import eu.internetofus.common.components.models.CommunityProfileTest;
import eu.internetofus.common.components.models.WeNetUserProfileTest;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import io.vertx.serviceproxy.ServiceException;
import java.util.UUID;
import javax.ws.rs.core.Response.Status;
import org.junit.jupiter.api.Test;

/**
 * Test the {@link WeNetProfileManager}.
 *
 * @see WeNetProfileManager
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class WeNetProfileManagerTestCase extends WeNetComponentTestCase<WeNetProfileManager> {

  /**
   * {@inheritDoc}
   *
   * @see WeNetProfileManager#createProxy(Vertx)
   */
  @Override
  protected WeNetProfileManager createComponentProxy(final Vertx vertx) {

    return WeNetProfileManager.createProxy(vertx);
  }

  /**
   * Should not create a bad profile.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotCreateBadProfile(final Vertx vertx, final VertxTestContext testContext) {

    this.createComponentProxy(vertx).createProfile(new JsonObject().put("undefinedField", "value"),
        testContext.failing(error -> testContext.completeNow()));

  }

  /**
   * Should not retrieve undefined profile.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotRetrieveUndefinedProfile(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertFailure(this.createComponentProxy(vertx).retrieveProfile("undefined-profile-identifier"))
        .onFailure(error -> testContext.completeNow());
  }

  /**
   * Should not delete undefined profile.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotDeleteUndefinedProfile(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertFailure(this.createComponentProxy(vertx).deleteProfile("undefined-profile-identifier"))
        .onFailure(error -> testContext.completeNow());

  }

  /**
   * Should create, retrieve and delete a profile.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldCreateRetrieveUpdateAndDeleteProfile(final Vertx vertx, final VertxTestContext testContext) {

    new WeNetUserProfileTest().createModelExample(1, vertx, testContext).onSuccess(profile1 -> {

      new WeNetUserProfileTest().createModelExample(2, vertx, testContext).onSuccess(profile2 -> {

        profile2.norms = null;
        profile2.plannedActivities = null;
        profile2.relevantLocations = null;
        final var service = this.createComponentProxy(vertx);
        testContext.assertComplete(service.createProfile(profile1)).onSuccess(created -> {

          final var id = created.id;
          testContext.assertComplete(service.retrieveProfile(id)).onSuccess(retrieved -> testContext.verify(() -> {

            assertThat(retrieved).isEqualTo(created);

            profile2.id = created.id;
            testContext.assertComplete(service.updateProfile(profile2)).onSuccess(updated -> testContext.verify(() -> {

              assertThat(updated._lastUpdateTs).isGreaterThanOrEqualTo(created._lastUpdateTs);
              profile2._creationTs = updated._creationTs;
              profile2._lastUpdateTs = updated._lastUpdateTs;
              created._lastUpdateTs = updated._lastUpdateTs;
              assertThat(updated).isEqualTo(profile2).isNotEqualTo(created);
              testContext.assertComplete(service.retrieveProfile(id)).onSuccess(retrieved2 -> testContext.verify(() -> {

                assertThat(retrieved2).isNotEqualTo(created).isEqualTo(updated);
                testContext.assertComplete(service.deleteProfile(id)).onSuccess(empty -> {

                  testContext.assertFailure(service.retrieveProfile(id)).onFailure(error -> testContext.verify(() -> {

                    assertThat(error).isExactlyInstanceOf(ServiceException.class);
                    assertThat(((ServiceException) error).failureCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
                    testContext.completeNow();

                  }));
                });
              }));
            }));
          }));
        });
      });
    });

  }

  /**
   * Should not create a bad community.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotCreateBadCommunity(final Vertx vertx, final VertxTestContext testContext) {

    this.createComponentProxy(vertx).createCommunity(new JsonObject().put("undefinedField", "value"),
        testContext.failing(error -> testContext.completeNow()));

  }

  /**
   * Should not retrieve undefined community.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotRetrieveUndefinedCommunity(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertFailure(this.createComponentProxy(vertx).retrieveCommunity("undefined-community-identifier"))
        .onFailure(error -> testContext.completeNow());
  }

  /**
   * Should not delete undefined community.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldNotDeleteUndefinedCommunity(final Vertx vertx, final VertxTestContext testContext) {

    assertStatusError(this.createComponentProxy(vertx).deleteCommunity("undefined-community-identifier"), testContext,
        Status.NOT_FOUND).onComplete(error -> testContext.completeNow());

  }

  /**
   * Should create, retrieve and delete a community.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldCreateRetrieveUpdateAndDeleteCommunity(final Vertx vertx, final VertxTestContext testContext) {

    new CommunityProfileTest().createModelExample(1, vertx, testContext).onSuccess(community1 -> {

      new CommunityProfileTest().createModelExample(2, vertx, testContext).onSuccess(community2 -> {

        final var service = this.createComponentProxy(vertx);
        testContext.assertComplete(service.createCommunity(community1)).onSuccess(created -> {

          final var id = created.id;
          testContext.assertComplete(service.retrieveCommunity(id)).onSuccess(retrieved -> testContext.verify(() -> {

            assertThat(retrieved).isEqualTo(created);

            community2.id = created.id;
            testContext.assertComplete(service.updateCommunity(community2))
                .onSuccess(updated -> testContext.verify(() -> {

                  assertThat(updated._lastUpdateTs).isGreaterThanOrEqualTo(created._lastUpdateTs);
                  community2._creationTs = updated._creationTs;
                  community2._lastUpdateTs = updated._lastUpdateTs;
                  created._lastUpdateTs = updated._lastUpdateTs;
                  assertThat(updated).isEqualTo(community2).isNotEqualTo(created);
                  testContext.assertComplete(service.retrieveCommunity(id))
                      .onSuccess(retrieved2 -> testContext.verify(() -> {

                        assertThat(retrieved2).isNotEqualTo(created).isEqualTo(updated);
                        testContext.assertComplete(service.deleteCommunity(id)).onSuccess(empty -> {

                          testContext.assertFailure(service.retrieveCommunity(id))
                              .onFailure(error -> testContext.verify(() -> {

                                assertThat(error).isExactlyInstanceOf(ServiceException.class);
                                assertThat(((ServiceException) error).failureCode())
                                    .isEqualTo(Status.NOT_FOUND.getStatusCode());
                                testContext.completeNow();

                              }));
                        });
                      }));
                }));
          }));
        });
      });
    });

  }

  /**
   * Should return an empty profiles page.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldReturnEmptyCommunityProfilesPage(final Vertx vertx, final VertxTestContext testContext) {

    final var appId = UUID.randomUUID().toString();
    testContext.assertComplete(WeNetProfileManager.createProxy(vertx).retrieveCommunityProfilesPage(appId, null, null,
        null, null, null, 0, 100)).onSuccess(page -> testContext.verify(() -> {

          assertThat(page).isNotNull();
          assertThat(page.offset).isEqualTo(0);
          assertThat(page.total).isEqualTo(0);
          assertThat(page.communities).isNull();

          testContext.assertComplete(WeNetProfileManager.createProxy(vertx).retrieveCommunityProfilesPage(appId, "name",
              "description", "keywords", "members", "name", 0, 100)).onSuccess(page2 -> testContext.verify(() -> {

                assertThat(page).isEqualTo(page2);
                testContext.completeNow();

              }));
        }));

  }

  /**
   * Should get user identifiers.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldGetUserIdentifiers(final Vertx vertx, final VertxTestContext testContext) {

    testContext.assertComplete(WeNetProfileManager.createProxy(vertx).getUserIdentifiersPage(0, 10))
        .onSuccess(page -> testContext.verify(() -> {

          assertThat(page).isNotNull();
          assertThat(page.offset).isEqualTo(0);
          if (page.total == 0) {

            assertThat(page.userIds).isNull();

          } else {

            assertThat(page.userIds).isNotNull().isNotEmpty().hasSizeGreaterThanOrEqualTo(1)
                .hasSizeLessThanOrEqualTo(10);
          }

          testContext.completeNow();
        }));

  }

}
