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

package eu.internetofus.common.components.social_context_builder;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link WeNetSocialContextBuilderSimulator}.
 *
 * @see WeNetSocialContextBuilderSimulator
 * @see WeNetSocialContextBuilderSimulatorClient
 * @see WeNetSocialContextBuilderSimulatorMocker
 *
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class WeNetSocialContextBuilderSimulatorTest extends WeNetSocialContextBuilderTestCase {

  /**
   * The social context builder mocked server.
   */
  protected static WeNetSocialContextBuilderSimulatorMocker socialContextBuilderMocker;

  /**
   * Start the mocker servers.
   */
  @BeforeAll
  public static void startMockers() {

    socialContextBuilderMocker = WeNetSocialContextBuilderSimulatorMocker.start();
  }

  /**
   * Stop the mocker server.
   */
  @AfterAll
  public static void stopMockers() {

    socialContextBuilderMocker.stopServer();
  }

  /**
   * Register the client.
   *
   * @param vertx event bus to use.
   */
  @BeforeEach
  public void registerClient(final Vertx vertx) {

    final var client = WebClient.create(vertx);
    final var socialContextBuilderConf = socialContextBuilderMocker.getComponentConfiguration();
    WeNetSocialContextBuilderSimulator.register(vertx, client, socialContextBuilderConf);
    WeNetSocialContextBuilder.register(vertx, client, socialContextBuilderConf);

  }

  /**
   * Should set and get the social relations.
   *
   * @param vertx       that contains the event bus to use.
   * @param testContext context over the tests.
   */
  @Test
  public void shouldSetGetSocialRelations(final Vertx vertx, final VertxTestContext testContext) {

    var userId = UUID.randomUUID().toString();
    var relations = new ArrayList<UserRelation>();
    testContext.assertComplete(WeNetSocialContextBuilderSimulator.createProxy(vertx).retrieveSocialRelations(userId))
        .onSuccess(retrieveRelations -> testContext.verify(() -> {

          assertThat(retrieveRelations).isEqualTo(relations);
          relations.add(new UserRelationTest().createModelExample(1));
          relations.add(new UserRelationTest().createModelExample(2));
          relations.add(new UserRelationTest().createModelExample(3));

          testContext
              .assertComplete(
                  WeNetSocialContextBuilderSimulator.createProxy(vertx).setSocialRelations(userId, relations))
              .onSuccess(storedRelations -> testContext.verify(() -> {

                assertThat(storedRelations).isEqualTo(relations);
                testContext
                    .assertComplete(
                        WeNetSocialContextBuilderSimulator.createProxy(vertx).retrieveSocialRelations(userId))
                    .onSuccess(retrieveRelations2 -> testContext.verify(() -> {

                      assertThat(retrieveRelations2).isEqualTo(relations);
                      testContext.completeNow();
                    }));
              }));
        }));
  }

}
