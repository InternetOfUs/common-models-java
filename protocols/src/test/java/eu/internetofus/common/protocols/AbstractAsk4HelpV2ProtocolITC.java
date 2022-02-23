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
import eu.internetofus.common.components.models.Task;
import eu.internetofus.common.components.service.MessagePredicates;
import io.vertx.core.json.JsonObject;
import java.util.function.Predicate;

/**
 * Check the {@link DefaultProtocols#ASK_4_HELP_V2} protocol. ATTENTION: This
 * test is sequential and maintains the state between methods. In other words,
 * you must to run the entire test methods on the specified order to work.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractAsk4HelpV2ProtocolITC extends AbstractDefaultProtocolITC {

  /**
   * {@inheritDoc}
   *
   * @return {@link DefaultProtocols#ASK_4_HELP_V2}
   */
  @Override
  protected DefaultProtocols getDefaultProtocolsToUse() {

    return DefaultProtocols.ASK_4_HELP_V2;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Task createTaskForProtocol() {

    final var taskToCreate = super.createTaskForProtocol();
    taskToCreate.goal.name = "Any of you do you known what mean \u0631 \u06AF\u0631\u0648\u0647 \u0627\u0632 \u062F\u06CC\u06AF\u0631\u0627\u0646 \u0628\u067E\u0631\u0633\u06CC\u061F ?";
    taskToCreate.attributes = new JsonObject().put("domain", "varia_misc").put("domainInterest", "indifferent")
        .put("beliefsAndValues", "indifferent").put("sensitive", false).put("anonymous", false)
        .put("socialCloseness", "indifferent").put("positionOfAnswerer", "anywhere").put("maxUsers", 5);
    return taskToCreate;

  }

  /**
   * Create a predicate that check that the specified user receive the question to
   * answer.
   *
   * @param userId identifier of the user to receive the question to answer.
   *
   * @return predicate to check that the user has received the task question.
   */
  protected Predicate<Message> createMessagePredicateForQuestionToAnswerMessageBy(final String userId) {

    return this.createMessagePredicate().and(MessagePredicates.labelIs("QuestionToAnswerMessage"))
        .and(MessagePredicates.receiverIs(userId)).and(target -> {

          if (this.task == null) {

            return false;
          }
          final var attributes = new JsonObject().put("question", this.task.goal.name).put("taskId", this.task.id)
              .put("userId", this.task.requesterId).put("sensitive", this.task.attributes.getBoolean("sensitive"))
              .put("anonymous", this.task.attributes.getBoolean("anonymous"))
              .put("positionOfAnswerer", this.task.attributes.getString("positionOfAnswerer"));
          return MessagePredicates.attributesSimilarTo(attributes).test(target);

        });

  }

}
