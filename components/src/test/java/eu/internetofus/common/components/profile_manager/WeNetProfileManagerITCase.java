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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import eu.internetofus.common.components.StoreServices;
import eu.internetofus.common.model.Models.CommunityProfile;
import eu.internetofus.common.model.Model;
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

    StoreServices.storeCommunityExample(1, vertx, testContext).onSuccess(community -> {
      StoreServices.storeCommunityExample(2, vertx, testContext).onSuccess(community2 -> {
        StoreServices.storeCommunityExample(3, vertx, testContext).onSuccess(community3 -> {
          StoreServices.storeCommunityExample(4, vertx, testContext).onSuccess(community4 -> {

            testContext.assertComplete(WeNetProfileManager.createProxy(vertx).retrieveCommunityProfilesPage(null, null, null, null, null, null, 1, 3).onSuccess(page -> testContext.verify(() -> {

              assertThat(page).isNotNull();
              assertThat(page.offset).isEqualTo(1);
              assertThat(page.total).isGreaterThan(3);
              assertThat(page.communities).isNotEmpty().hasSize(3);
              testContext.completeNow();

            })));
          });
        });
      });
    });

  }

  /**
   * Should retrieve communities that has specific appId.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldRetrieveCommunityProfilesPageByApp(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeCommunityExample(1, vertx, testContext).onSuccess(community -> {

      final var newCommunity = Model.fromJsonObject(community.toJsonObject(), CommunityProfile.class);
      newCommunity.id = null;
      StoreServices.storeCommunity(newCommunity, vertx, testContext).onSuccess(community2 -> {

        StoreServices.storeCommunity(newCommunity, vertx, testContext).onSuccess(community3 -> {

          StoreServices.storeCommunity(newCommunity, vertx, testContext).onSuccess(community4 -> {

            testContext.assertComplete(WeNetProfileManager.createProxy(vertx).retrieveCommunityProfilesPage(community.appId, null, null, null, null, null, 0, 2).onSuccess(page -> testContext.verify(() -> {

              assertThat(page).isNotNull();
              assertThat(page.offset).isEqualTo(0);
              assertThat(page.total).isEqualTo(4);
              assertThat(page.communities).isNotEmpty().hasSize(2).contains(community, community2);
              testContext.completeNow();

            })));
          });
        });
      });
    });

  }

  /**
   * Should retrieve communities that has specific name.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldRetrieveCommunityProfilesPageByName(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeCommunityExample(1, vertx, testContext).onSuccess(community -> {

      final var newCommunity = Model.fromJsonObject(community.toJsonObject(), CommunityProfile.class);
      final var name = UUID.randomUUID().toString();
      newCommunity.name = name;
      newCommunity.id = null;
      StoreServices.storeCommunity(newCommunity, vertx, testContext).onSuccess(community2 -> {

        StoreServices.storeCommunity(newCommunity, vertx, testContext).onSuccess(community3 -> {

          StoreServices.storeCommunity(newCommunity, vertx, testContext).onSuccess(community4 -> {

            testContext.assertComplete(WeNetProfileManager.createProxy(vertx).retrieveCommunityProfilesPage(null, name, null, null, null, null, 2, 3).onSuccess(page -> testContext.verify(() -> {

              assertThat(page).isNotNull();
              assertThat(page.offset).isEqualTo(2);
              assertThat(page.total).isEqualTo(3);
              assertThat(page.communities).isNotEmpty().hasSize(1).contains(community4);
              testContext.completeNow();

            })));
          });
        });
      });
    });

  }

  /**
   * Should retrieve communities that has specific description.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldRetrieveCommunityProfilesPageByDescription(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeCommunityExample(1, vertx, testContext).onSuccess(community -> {

      final var newCommunity = Model.fromJsonObject(community.toJsonObject(), CommunityProfile.class);
      final var description = UUID.randomUUID().toString();
      newCommunity.description = description;
      newCommunity.id = null;
      StoreServices.storeCommunity(newCommunity, vertx, testContext).onSuccess(community2 -> {

        StoreServices.storeCommunity(newCommunity, vertx, testContext).onSuccess(community3 -> {

          StoreServices.storeCommunity(newCommunity, vertx, testContext).onSuccess(community4 -> {

            testContext.assertComplete(WeNetProfileManager.createProxy(vertx).retrieveCommunityProfilesPage(null, null, description, null, null, null, 0, 3).onSuccess(page -> testContext.verify(() -> {

              assertThat(page).isNotNull();
              assertThat(page.offset).isEqualTo(0);
              assertThat(page.total).isEqualTo(3);
              assertThat(page.communities).isNotEmpty().hasSize(3).contains(community2, community3, community4);
              testContext.completeNow();

            })));
          });
        });
      });
    });

  }

  /**
   * Should retrieve communities that has specific keywords.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldRetrieveCommunityProfilesPageByKeywords(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeCommunityExample(1, vertx, testContext).onSuccess(community -> {

      final var newCommunity = Model.fromJsonObject(community.toJsonObject(), CommunityProfile.class);
      newCommunity.id = null;
      final var keyword = UUID.randomUUID().toString();
      newCommunity.keywords.add(keyword);
      StoreServices.storeCommunity(newCommunity, vertx, testContext).onSuccess(community2 -> {

        StoreServices.storeCommunity(newCommunity, vertx, testContext).onSuccess(community3 -> {

          StoreServices.storeCommunity(newCommunity, vertx, testContext).onSuccess(community4 -> {

            testContext.assertComplete(WeNetProfileManager.createProxy(vertx).retrieveCommunityProfilesPage(null, null, null, keyword, null, null, 1, 2).onSuccess(page -> testContext.verify(() -> {

              assertThat(page).isNotNull();
              assertThat(page.offset).isEqualTo(1);
              assertThat(page.total).isEqualTo(3);
              assertThat(page.communities).isNotEmpty().hasSize(2).contains(community3, community4);
              testContext.completeNow();

            })));
          });
        });
      });
    });

  }

  /**
   * Should retrieve communities that has specific members.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldRetrieveCommunityProfilesPageByMembers(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeCommunityExample(1, vertx, testContext).onSuccess(community -> {

      final var newCommunity = Model.fromJsonObject(community.toJsonObject(), CommunityProfile.class);
      newCommunity.id = null;
      StoreServices.storeCommunity(newCommunity, vertx, testContext).onSuccess(community2 -> {

        StoreServices.storeCommunity(newCommunity, vertx, testContext).onSuccess(community3 -> {

          StoreServices.storeCommunity(newCommunity, vertx, testContext).onSuccess(community4 -> {

            testContext.assertComplete(WeNetProfileManager.createProxy(vertx).retrieveCommunityProfilesPage(null, null, null, null, community.members.get(0).userId, null, 0, 100).onSuccess(page -> testContext.verify(() -> {

              assertThat(page).isNotNull();
              assertThat(page.offset).isEqualTo(0);
              assertThat(page.total).isEqualTo(4);
              assertThat(page.communities).isNotEmpty().hasSize(4).contains(community, community2, community3, community4);
              testContext.completeNow();

            })));
          });
        });
      });
    });

  }

  /**
   * Should retrieve communities that has specific order.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldRetrieveCommunityProfilesPageByOrder(final Vertx vertx, final VertxTestContext testContext) {

    StoreServices.storeCommunityExample(1, vertx, testContext).onSuccess(community -> {

      final var newCommunity = Model.fromJsonObject(community.toJsonObject(), CommunityProfile.class);
      newCommunity.id = null;
      newCommunity.name = "000" + newCommunity.name;
      StoreServices.storeCommunity(newCommunity, vertx, testContext).onSuccess(community2 -> {

        newCommunity.name = "111" + newCommunity.name;
        StoreServices.storeCommunity(newCommunity, vertx, testContext).onSuccess(community3 -> {

          newCommunity.name = "222" + newCommunity.name;
          StoreServices.storeCommunity(newCommunity, vertx, testContext).onSuccess(community4 -> {

            testContext.assertComplete(WeNetProfileManager.createProxy(vertx).retrieveCommunityProfilesPage(newCommunity.appId, null, null, null, null, "-name", 1, 2).onSuccess(page -> testContext.verify(() -> {

              assertThat(page).isNotNull();
              assertThat(page.offset).isEqualTo(1);
              assertThat(page.total).isEqualTo(4);
              assertThat(page.communities).isNotEmpty().hasSize(2).containsExactly(community4, community3);
              testContext.completeNow();

            })));
          });
        });
      });
    });
  }

}
