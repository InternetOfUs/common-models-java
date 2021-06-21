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
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * The badge on an {@link Incentive}
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "Badge", description = "The badge on an incentive.")
public class IncentiveBadge extends ReflectionModel implements Model, Validable {

  /**
   * The badge class.
   */
  @Schema(example = "hpqAdI7hQf2maQ13AW1jXA")
  public String BadgeClass;

  /**
   * The badge image.
   */
  @Schema(example = "http://3.126.161.118:8000/media/uploads/badges/assertion-OYmfmtDFSIKG-qeZfXz4QQ.png")
  public String ImgUrl;

  /**
   * The badge criteria.
   */
  @Schema(example = "The user will get this badge for 50 relations in tweeter.")
  public String Criteria;

  /**
   * The badge message.
   */
  @Schema(example = "congratulations! you just earned a new badge for your relations on tweeter.")
  public String Message;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();

    try {

      this.BadgeClass = Validations.validateStringField(codePrefix, "BadgeClass", this.BadgeClass);
      this.ImgUrl = Validations.validateNullableURLField(codePrefix, "ImgUrl", this.ImgUrl);
      this.Criteria = Validations.validateStringField(codePrefix, "Criteria", this.Criteria);
      this.Message = Validations.validateStringField(codePrefix, "Message", this.Message);
      promise.complete();

    } catch (final ValidationErrorException validationError) {

      promise.fail(validationError);
    }

    return promise.future();
  }

}
