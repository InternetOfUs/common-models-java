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
import eu.internetofus.common.model.Mergeable;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import eu.internetofus.common.model.Updateable;
import eu.internetofus.common.model.Validable;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 * A relationship with another user.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, description = "A social relationship with another WeNet user.")
public class SocialNetworkRelationship extends ReflectionModel
    implements Model, Validable<WeNetValidateContext>, Mergeable<SocialNetworkRelationship, WeNetValidateContext>,
    Updateable<SocialNetworkRelationship, WeNetValidateContext> {

  /**
   * The identifier of the application where the relation happens.
   */
  @Schema(description = "The identifier of the application where the relation happens", example = "4c51ee0b-b7ec-4577-9b21-ae6832656e33", nullable = true)
  public String appId;

  /**
   * The identifier of the WeNet user the relationship is related to.
   */
  @Schema(description = "The identifier of the WeNet user the relationship is related to", example = "4c51ee0b-b7ec-4577-9b21-ae6832656e33", nullable = true)
  public String userId;

  /**
   * The relationship type.
   */
  @Schema(description = "The relationship type", example = "friend", nullable = true)
  public SocialNetworkRelationshipType type;

  /**
   * The weight of the relation.
   */
  @Schema(description = "A number from 0 to 1 that indicates the strength of the relation. 0 indicates a deleted/non-exisiting relation.", example = "0.2", nullable = true)
  public Double weight;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final WeNetValidateContext context) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();

    if (this.type == null) {

      return context.failField("type", "It is not allowed a social relationship without a type'.");

    } else {

      future = context.validateDefinedAppIdField("appId", this.appId, future);
      future = context.validateDefinedProfileIdField("userId", this.userId, future);
      context.validateNumberOnRangeField("weight", this.weight, 0.0d, 1.0d, promise);
      promise.tryComplete();
    }

    return future;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<SocialNetworkRelationship> merge(final SocialNetworkRelationship source,
      final WeNetValidateContext context) {

    final Promise<SocialNetworkRelationship> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var merged = new SocialNetworkRelationship();
      merged.appId = source.appId;
      if (merged.appId == null) {

        merged.appId = this.appId;
      }

      merged.userId = source.userId;
      if (merged.userId == null) {

        merged.userId = this.userId;
      }

      merged.type = source.type;
      if (merged.type == null) {

        merged.type = this.type;
      }

      merged.weight = source.weight;
      if (merged.weight == null) {

        merged.weight = this.weight;
      }

      promise.complete(merged);

      // validate the merged value
      future = future.compose(context.chain());

    } else {

      promise.complete(this);

    }
    return future;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<SocialNetworkRelationship> update(final SocialNetworkRelationship source,
      final WeNetValidateContext context) {
    final Promise<SocialNetworkRelationship> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var updated = new SocialNetworkRelationship();
      updated.appId = source.appId;
      updated.userId = source.userId;
      updated.type = source.type;
      updated.weight = source.weight;

      promise.complete(updated);

      // validate the updated value
      future = future.compose(context.chain());

    } else {

      promise.complete(this);

    }
    return future;
  }

  /**
   * Check if two social network relationships are equivalent by its identifier
   * fields.
   *
   * @param a first model to compare.
   * @param b second model to compare.
   *
   * @return {@code true} if the social network relationships can be considered
   *         equals by its identifier.
   */
  static boolean compareIds(final SocialNetworkRelationship a, final SocialNetworkRelationship b) {

    return a != null && a.type != null && a.type.equals(b.type) && a.appId != null && a.appId.equals(b.appId)
        && a.userId != null && a.userId.equals(b.userId);

  }

}
