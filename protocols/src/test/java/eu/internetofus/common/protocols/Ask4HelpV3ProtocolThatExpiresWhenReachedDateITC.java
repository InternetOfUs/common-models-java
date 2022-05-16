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
package eu.internetofus.common.protocols;

import eu.internetofus.common.components.models.Message;
import eu.internetofus.common.components.service.MessagePredicates;
import eu.internetofus.common.model.TimeManager;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import java.time.Duration;
import java.util.ArrayList;
import java.util.function.Predicate;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Check the {@link DefaultProtocols#ASK_4_HELP_V3} protocol. ATTENTION: This
 * test is sequential and maintains the state between methods. In other words,
 * you must to run the entire test methods on the specified order to work.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class Ask4HelpV3ProtocolThatExpiresWhenReachedDateITC extends AbstractAsk4HelpV3ProtocolITC {

  /**
   * {@inheritDoc}
   *
   * @return {@code 5} in any case.
   */
  @Override
  protected int numberOfUsersToCreate() {

    return 5;
  }

  /**
   * {@inheritDoc}
   *
   * @return {@link #numberOfUsersToCreate()}.
   */
  @Override
  public int maxUsers() {

    return this.numberOfUsersToCreate();
  }

  /**
   * {@inheritDoc}
   *
   * @return {@code 5} in any case.
   */
  @Override
  public int maxAnswers() {

    return 5;
  }

  /**
   * {@inheritDoc}
   *
   * @return ten seconds from now.
   */
  @Override
  public long expirationDate() {

    return TimeManager.now() + Duration.ofSeconds(10).toSeconds();

  }

  /**
   * Check that the task was expired.
   *
   * @param vertx       event bus to use.
   * @param testContext context to do the test.
   */
  @Test
  @Order(6)
  public void shouldTaskExpireByDate(final Vertx vertx, final VertxTestContext testContext) {

    this.assertLastSuccessfulTestWas(5, testContext);

    final var checkMessages = new ArrayList<Predicate<Message>>();
    final var checkMessage = this.createMessagePredicate().and(MessagePredicates.labelIs("QuestionExpirationMessage"))
        .and(MessagePredicates.receiverIs(this.task.requesterId))
        .and(MessagePredicates
            .attributesSimilarTo(new JsonObject().put("taskId", this.task.id).put("question", this.task.goal.name)
                .put("userId", this.task.requesterId).put("listOfTransactionIds", new JsonArray())));
    checkMessages.add(checkMessage);

    final var future = this.waitUntilCallbacks(vertx, testContext, checkMessages);
    future.onComplete(testContext.succeeding(ignored -> this.assertSuccessfulCompleted(testContext)));

  }
}
