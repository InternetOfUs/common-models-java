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

package eu.internetofus.common.components.incentive_server;

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
  @Schema(description = "Message of the icentive")
  public Message Message;

  /**
   * Badge of the incentive.
   */
  @Schema(description = "Badge of the icentive")
  public Badge Badge;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();
    try {

      this.AppID = Validations.validateNullableStringField(codePrefix, "AppID", 255, this.AppID);
      if (this.AppID != null) {

        future = Validations.composeValidateId(future, codePrefix, "AppID", this.AppID, true, WeNetService.createProxy(vertx)::retrieveApp);

      }
      this.UserId = Validations.validateNullableStringField(codePrefix, "UserId", 255, this.UserId);
      if (this.UserId != null) {

        future = Validations.composeValidateId(future, codePrefix, "UserId", this.UserId, true, WeNetProfileManager.createProxy(vertx)::retrieveProfile);

      }
      this.IncentiveType = Validations.validateNullableStringField(codePrefix, "IncentiveType", 255, this.IncentiveType);
      this.Issuer = Validations.validateNullableStringField(codePrefix, "Issuer", 255, this.Issuer);
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
