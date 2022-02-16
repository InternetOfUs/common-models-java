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
import eu.internetofus.common.model.Merges;
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
@Schema(hidden = true, description = "A social relationship between two WeNet user.")
public class SocialNetworkRelationship extends ReflectionModel
    implements Model, Validable<WeNetValidateContext>, Mergeable<SocialNetworkRelationship, WeNetValidateContext>,
    Updateable<SocialNetworkRelationship, WeNetValidateContext> {

  /**
   * The identifier of the application where the relation happens.
   */
  @Schema(description = "The identifier of the application where the relation happens", example = "4c51ee0b", nullable = true)
  public String appId;

  /**
   * The identifier of the WeNet user that is the source of the relationship.
   */
  @Schema(description = "The identifier of the WeNet user that is the source of the relationship", example = "1", nullable = true)
  public String sourceId;

  /**
   * The identifier of the WeNet user the relationship is related to the source.
   */
  @Schema(description = "The identifier of the WeNet user the relationship is related to the source", example = "2", nullable = true)
  public String targetId;

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
      future = context.validateDefinedProfileIdField("sourceId", this.sourceId, future);
      future = context.validateDefinedProfileIdField("targetId", this.targetId, future);
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
      merged.appId = Merges.mergeValues(this.appId, source.appId);
      merged.sourceId = Merges.mergeValues(this.sourceId, source.sourceId);
      merged.targetId = Merges.mergeValues(this.targetId, source.targetId);
      merged.type = Merges.mergeValues(this.type, source.type);
      merged.weight = Merges.mergeValues(this.weight, source.weight);

      // validate the merged value
      future = future.compose(context.chain());

      promise.complete(merged);

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
      updated.sourceId = source.sourceId;
      updated.targetId = source.targetId;
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
  public static boolean compareIds(final SocialNetworkRelationship a, final SocialNetworkRelationship b) {

    return a != null && b != null && a.type != null && a.type.equals(b.type) && a.appId != null
        && a.appId.equals(b.appId) && a.sourceId != null && a.sourceId.equals(b.sourceId) && a.targetId != null
        && a.targetId.equals(b.targetId);

  }

}
