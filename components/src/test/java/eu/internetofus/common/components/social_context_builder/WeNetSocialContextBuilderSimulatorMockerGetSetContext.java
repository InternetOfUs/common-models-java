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
package eu.internetofus.common.components.social_context_builder;

import eu.internetofus.common.components.GetSetContext;
import eu.internetofus.common.model.ErrorMessage;
import eu.internetofus.common.model.Model;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import javax.ws.rs.core.Response.Status;

/**
 * The context to store the values for the
 * {@link WeNetSocialContextBuilderSimulatorMocker}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetSocialContextBuilderSimulatorMockerGetSetContext extends GetSetContext {

  /**
   * Create the handler to manage the post of the user answers.
   *
   * @return the context to manage the user answers.
   */
  public Handler<RoutingContext> createSocialPreferencesAnswersPostHandler() {

    return ctx -> {

      final var id = this.getIdFrom(ctx, "SOCIAL_PREFERENCES_ANSWERS", "userId", "taskId");
      final var response = ctx.response();
      final var preferences = Model.fromBuffer(ctx.getBody(), AnswersData.class);
      if (preferences != null && preferences.data != null) {

        final var answers = Model.toJsonArray(preferences.data);
        this.values.put(id, answers);
        response.setStatusCode(Status.OK.getStatusCode());
        response.end(answers.toBuffer());

      } else {

        response.setStatusCode(Status.BAD_REQUEST.getStatusCode());
        response.end(new ErrorMessage("bad_value", "Does not exist a valid value on the body.").toBuffer());
      }

    };
  }

}
