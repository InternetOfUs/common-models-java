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

package eu.internetofus.common.components.models;

import eu.internetofus.common.components.WeNetValidateContext;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import eu.internetofus.common.model.Validable;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 * Represents an incentive for an user.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "Incentive", description = "An user incentive.")
public class Incentive extends ReflectionModel implements Model, Validable<WeNetValidateContext> {

  /**
   * Identifier of the application.
   */
  @Schema(example = "1")
  public String AppID;

  /**
   * Identifier of the user.
   */
  @Schema(example = "6")
  public String UserId;

  /**
   * type of incentive.
   */
  @Schema(example = "Message/Badge (only one)")
  public String IncentiveType;

  /**
   * Identifier of the issuer.
   */
  @Schema(example = "WeNet issuer")
  public String Issuer;

  /**
   * Message of the incentive.
   */
  @Schema(description = "Message of the icentive", nullable = true)
  public IncentiveMessage Message;

  /**
   * Badge of the incentive.
   */
  @Schema(description = "Badge of the icentive", nullable = true)
  public IncentiveBadge Badge;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final WeNetValidateContext context) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();

    if (this.AppID != null) {

      future = context.validateDefinedAppIdField("AppID", this.AppID, future);

    }
    if (this.UserId != null) {

      future = context.validateDefinedProfileIdField("UserId", this.UserId, future);

    }

    this.IncentiveType = context.validateStringField("IncentiveType", this.IncentiveType, promise);
    this.Issuer = context.validateStringField("Issuer", this.Issuer, promise);

    if (this.Message == null && this.Badge == null) {

      return context.failField("Message", "You must specify a message or a badge");
    }

    if (this.Message != null) {

      future = future.compose(context.validateField("Message", this.Message));
    }

    if (this.Badge != null) {

      future = future.compose(context.validateField("Badge", this.Badge));
    }

    promise.tryComplete();

    return future;

  }

}
