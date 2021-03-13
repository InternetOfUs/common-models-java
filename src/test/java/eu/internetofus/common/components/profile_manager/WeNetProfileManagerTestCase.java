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

import static eu.internetofus.common.vertx.ComponentClientAsserts.assertStatusError;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.WeNetComponentTestCase;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import io.vertx.serviceproxy.ServiceException;
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

}
