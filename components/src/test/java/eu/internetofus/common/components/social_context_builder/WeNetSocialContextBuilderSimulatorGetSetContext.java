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
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import javax.ws.rs.core.Response.Status;

/**
 * The context user by the simulator.
 *
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetSocialContextBuilderSimulatorGetSetContext extends GetSetContext {

  /**
   * Create a handler to store the social preferences answers.
   *
   * @return the handler to set a value.
   */
  public Handler<RoutingContext> createSetSocialPreferencesAnswersHandler() {

    return ctx -> {

      final var response = ctx.response();
      try {

        final var id = this.getIdFrom(ctx, "SOCIAL_PREFERENCES_ANSWERS", "userId", "taskId");
        final var value = ctx.body().asJsonObject();
        this.values.put(id, value);
        response.setStatusCode(Status.CREATED.getStatusCode());
        response.end(value.getJsonArray("data").toBuffer());

      } catch (final Throwable t) {

        response.setStatusCode(Status.BAD_REQUEST.getStatusCode());
        response.end(new ErrorMessage("bad_value", "Does not exist a valid value on the body.").toBuffer());
      }

    };

  }

  /**
   * Return the defined social notifications.
   *
   * @return the social notifications.
   */
  protected JsonArray getSocialNotificationInteractions() {

    var notifications = (JsonArray) this.values.get("SOCIAL_NOTIFICATION_INTERACTION");
    if (notifications == null) {

      notifications = new JsonArray();
      this.values.put("SOCIAL_NOTIFICATION_INTERACTION", notifications);
    }
    return notifications;

  }

  /**
   * Create a handler to store the social preferences answers.
   *
   * @return the handler to set a value.
   */
  public Handler<RoutingContext> createAddSocialNotificationInteractionHandler() {

    return ctx -> {

      final var response = ctx.response();
      try {

        final var value = ctx.body().asJsonObject();
        final var state = Model.fromJsonObject(value, UserMessage.class);
        if (state == null) {

          response.setStatusCode(Status.BAD_REQUEST.getStatusCode());
          response.end(new ErrorMessage("bad_status", "The body is not a valid user message").toBuffer());

        } else {

          final var notifications = this.getSocialNotificationInteractions();
          notifications.add(value);
          response.setStatusCode(Status.CREATED.getStatusCode());
          response.end(value.toBuffer());
        }

      } catch (final Throwable t) {

        response.setStatusCode(Status.BAD_REQUEST.getStatusCode());
        response.end(new ErrorMessage("bad_value", "Does not exist a valid value on the body.").toBuffer());
      }

    };

  }

  /**
   * Create a handler to delete the social preferences answers.
   *
   * @return the handler to set a value.
   */
  public Handler<RoutingContext> createDeleteSocialNotificationInteractionHandler() {

    return ctx -> {

      final var notifications = this.getSocialNotificationInteractions();
      notifications.clear();
      final var response = ctx.response();
      response.setStatusCode(Status.NO_CONTENT.getStatusCode());
      response.end();

    };

  }

  /**
   * Return the defined social notifications.
   *
   * @return the social notifications.
   */
  protected JsonArray getSocialNotificationProfileUpdates() {

    var notifications = (JsonArray) this.values.get("SOCIAL_NOTIFICATION_PROFILE_UPDATE");
    if (notifications == null) {

      notifications = new JsonArray();
      this.values.put("SOCIAL_NOTIFICATION_PROFILE_UPDATE", notifications);
    }
    return notifications;

  }

}
