/*
 * -----------------------------------------------------------------------------
 *
 *   Copyright 2019 - 2022 UDT-IA, IIIA-CSIC
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

import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ReflectionModel;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.ValidationErrorException;
import eu.internetofus.common.components.Validations;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.components.service.WeNetService;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * Represents an incentive for an user.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "Incentive", description = "An user incentive.")
public class Incentive extends ReflectionModel implements Model, Validable {

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
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();
    try {

      this.AppID = Validations.validateNullableStringField(codePrefix, "AppID", this.AppID);
      if (this.AppID != null) {

        future = Validations.composeValidateId(future, codePrefix, "AppID", this.AppID, true,
            WeNetService.createProxy(vertx)::retrieveApp);

      }
      this.UserId = Validations.validateNullableStringField(codePrefix, "UserId", this.UserId);
      if (this.UserId != null) {

        future = Validations.composeValidateId(future, codePrefix, "UserId", this.UserId, true,
            WeNetProfileManager.createProxy(vertx)::retrieveProfile);

      }
      this.IncentiveType = Validations.validateNullableStringField(codePrefix, "IncentiveType", this.IncentiveType);
      this.Issuer = Validations.validateNullableStringField(codePrefix, "Issuer", this.Issuer);
      if (this.Message == null && this.Badge == null) {

        promise.fail(new ValidationErrorException(codePrefix + ".Message", "You must specify a message or a badge"));
      }

      if (this.Message != null) {

        future = future.compose(val -> this.Message.validate(codePrefix + ".Message", vertx));
      }

      if (this.Badge != null) {

        future = future.compose(val -> this.Badge.validate(codePrefix + ".Badge", vertx));

      }

      promise.tryComplete();

    } catch (final ValidationErrorException validationError) {

      promise.fail(validationError);
    }

    return future;

  }

}
