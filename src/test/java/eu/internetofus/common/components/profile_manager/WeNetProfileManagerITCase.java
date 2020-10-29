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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.StoreServices;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link WeNetProfileManager}.
 *
 * @see WeNetProfileManager
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class WeNetProfileManagerITCase extends WeNetProfileManagerTestCase {

  /**
   * Should retrieve communities page.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldRetrieveCommunityProfilesPage(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeCommunityExample(1, vertx, testContext, testContext.succeeding(community -> {
      StoreServices.storeCommunityExample(2, vertx, testContext, testContext.succeeding(community2 -> {
        StoreServices.storeCommunityExample(3, vertx, testContext, testContext.succeeding(community3 -> {
          StoreServices.storeCommunityExample(4, vertx, testContext, testContext.succeeding(community4 -> {

            WeNetProfileManager.createProxy(vertx).retrieveCommunityProfilesPage(null, null, null, null, null, null, 1, 3, testContext.succeeding(page -> testContext.verify(() -> {

              assertThat(page).isNotNull();
              assertThat(page.offset).isEqualTo(1);
              assertThat(page.total).isGreaterThan(3);
              assertThat(page.communities).isNotEmpty().hasSize(3);
              testContext.completeNow();

            })));
          }));
        }));
      }));
    }));

  }

  /**
   * Should retrieve communities that has specific appId.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldRetrieveCommunityProfilesPageByApp(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeCommunityExample(1, vertx, testContext, testContext.succeeding(community -> {

      final var newCommunity = Model.fromJsonObject(community.toJsonObject(), CommunityProfile.class);
      newCommunity.id = null;
      StoreServices.storeCommunity(newCommunity, vertx, testContext, testContext.succeeding(community2 -> {

        StoreServices.storeCommunity(newCommunity, vertx, testContext, testContext.succeeding(community3 -> {

          StoreServices.storeCommunity(newCommunity, vertx, testContext, testContext.succeeding(community4 -> {

            WeNetProfileManager.createProxy(vertx).retrieveCommunityProfilesPage(community.appId, null, null, null, null, null, 0, 2, testContext.succeeding(page -> testContext.verify(() -> {

              assertThat(page).isNotNull();
              assertThat(page.offset).isEqualTo(0);
              assertThat(page.total).isGreaterThan(4);
              assertThat(page.communities).isNotEmpty().hasSize(2).contains(community, community2);
              testContext.completeNow();

            })));
          }));
        }));
      }));
    }));

  }

  /**
   * Should retrieve communities that has specific name.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldRetrieveCommunityProfilesPageByName(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeCommunityExample(1, vertx, testContext, testContext.succeeding(community -> {

      final var newCommunity = Model.fromJsonObject(community.toJsonObject(), CommunityProfile.class);
      newCommunity.id = null;
      StoreServices.storeCommunity(newCommunity, vertx, testContext, testContext.succeeding(community2 -> {

        StoreServices.storeCommunity(newCommunity, vertx, testContext, testContext.succeeding(community3 -> {

          StoreServices.storeCommunity(newCommunity, vertx, testContext, testContext.succeeding(community4 -> {

            WeNetProfileManager.createProxy(vertx).retrieveCommunityProfilesPage(null, community.name, null, null, null, null, 2, 3, testContext.succeeding(page -> testContext.verify(() -> {

              assertThat(page).isNotNull();
              assertThat(page.offset).isEqualTo(2);
              assertThat(page.total).isGreaterThan(4);
              assertThat(page.communities).isNotEmpty().hasSize(2).contains(community3, community4);
              testContext.completeNow();

            })));
          }));
        }));
      }));
    }));

  }

  /**
   * Should retrieve communities that has specific description.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldRetrieveCommunityProfilesPageByDescription(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeCommunityExample(1, vertx, testContext, testContext.succeeding(community -> {

      final var newCommunity = Model.fromJsonObject(community.toJsonObject(), CommunityProfile.class);
      newCommunity.id = null;
      StoreServices.storeCommunity(newCommunity, vertx, testContext, testContext.succeeding(community2 -> {

        StoreServices.storeCommunity(newCommunity, vertx, testContext, testContext.succeeding(community3 -> {

          StoreServices.storeCommunity(newCommunity, vertx, testContext, testContext.succeeding(community4 -> {

            WeNetProfileManager.createProxy(vertx).retrieveCommunityProfilesPage(null, null, community.description, null, null, null, 2, 3, testContext.succeeding(page -> testContext.verify(() -> {

              assertThat(page).isNotNull();
              assertThat(page.offset).isEqualTo(2);
              assertThat(page.total).isGreaterThan(4);
              assertThat(page.communities).isNotEmpty().hasSize(2).contains(community3, community4);
              testContext.completeNow();

            })));
          }));
        }));
      }));
    }));

  }

  /**
   * Should retrieve communities that has specific keywords.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldRetrieveCommunityProfilesPageByKeywords(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeCommunityExample(1, vertx, testContext, testContext.succeeding(community -> {

      final var newCommunity = Model.fromJsonObject(community.toJsonObject(), CommunityProfile.class);
      newCommunity.id = null;
      StoreServices.storeCommunity(newCommunity, vertx, testContext, testContext.succeeding(community2 -> {

        StoreServices.storeCommunity(newCommunity, vertx, testContext, testContext.succeeding(community3 -> {

          StoreServices.storeCommunity(newCommunity, vertx, testContext, testContext.succeeding(community4 -> {

            WeNetProfileManager.createProxy(vertx).retrieveCommunityProfilesPage(null, null, null, community.keywords.get(0), null, null, 1, 2, testContext.succeeding(page -> testContext.verify(() -> {

              assertThat(page).isNotNull();
              assertThat(page.offset).isEqualTo(1);
              assertThat(page.total).isGreaterThan(4);
              assertThat(page.communities).isNotEmpty().hasSize(2).contains(community2, community3);
              testContext.completeNow();

            })));
          }));
        }));
      }));
    }));

  }

  /**
   * Should retrieve communities that has specific members.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldRetrieveCommunityProfilesPageByMembers(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeCommunityExample(1, vertx, testContext, testContext.succeeding(community -> {

      final var newCommunity = Model.fromJsonObject(community.toJsonObject(), CommunityProfile.class);
      newCommunity.id = null;
      StoreServices.storeCommunity(newCommunity, vertx, testContext, testContext.succeeding(community2 -> {

        StoreServices.storeCommunity(newCommunity, vertx, testContext, testContext.succeeding(community3 -> {

          StoreServices.storeCommunity(newCommunity, vertx, testContext, testContext.succeeding(community4 -> {

            WeNetProfileManager.createProxy(vertx).retrieveCommunityProfilesPage(null, null, null, null, community.members.get(0).userId, null, 0, 100, testContext.succeeding(page -> testContext.verify(() -> {

              assertThat(page).isNotNull();
              assertThat(page.offset).isEqualTo(0);
              assertThat(page.total).isGreaterThan(4);
              assertThat(page.communities).isNotEmpty().hasSize(4).contains(community, community2, community3, community4);
              testContext.completeNow();

            })));
          }));
        }));
      }));
    }));

  }

  /**
   * Should retrieve communities that has specific order.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldRetrieveCommunityProfilesPageByOrder(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeCommunityExample(1, vertx, testContext, testContext.succeeding(community -> {

      final var newCommunity = Model.fromJsonObject(community.toJsonObject(), CommunityProfile.class);
      newCommunity.id = null;
      newCommunity.name = "000" + newCommunity.name;
      StoreServices.storeCommunity(newCommunity, vertx, testContext, testContext.succeeding(community2 -> {

        newCommunity.name = "111" + newCommunity.name;
        StoreServices.storeCommunity(newCommunity, vertx, testContext, testContext.succeeding(community3 -> {

          newCommunity.name = "222" + newCommunity.name;
          StoreServices.storeCommunity(newCommunity, vertx, testContext, testContext.succeeding(community4 -> {

            WeNetProfileManager.createProxy(vertx).retrieveCommunityProfilesPage(null, null, null, null, null, "-name", 1, 2, testContext.succeeding(page -> testContext.verify(() -> {

              assertThat(page).isNotNull();
              assertThat(page.offset).isEqualTo(0);
              assertThat(page.total).isGreaterThan(4);
              assertThat(page.communities).isNotEmpty().hasSize(4).contains(community4, community3);
              testContext.completeNow();

            })));
          }));
        }));
      }));
    }));

  }
}