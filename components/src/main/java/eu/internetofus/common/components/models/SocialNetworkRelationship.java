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

import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.components.service.WeNetService;
import eu.internetofus.common.model.Mergeable;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import eu.internetofus.common.model.Updateable;
import eu.internetofus.common.model.Validable;
import eu.internetofus.common.model.ValidationErrorException;
import eu.internetofus.common.model.Validations;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import java.util.Objects;

/**
 * A relationship with another user.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, description = "A social relationship with another WeNet user.")
public class SocialNetworkRelationship extends ReflectionModel
    implements Model, Validable, Mergeable<SocialNetworkRelationship>, Updateable<SocialNetworkRelationship> {

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
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();

    if (this.type == null) {

      promise.fail(new ValidationErrorException(codePrefix + ".type",
          "It is not allowed a social relationship without a type'."));

    } else {

      future = Validations.composeValidateId(future, codePrefix, "appId", this.appId, true,
          WeNetService.createProxy(vertx)::retrieveApp);
      future = Validations.composeValidateId(future, codePrefix, "userId", this.userId, true,
          WeNetProfileManager.createProxy(vertx)::retrieveProfile);
      future = future
          .compose(empty -> Validations.validateNumberOnRange(codePrefix, "weight", this.weight, true, 0d, 1d));
      promise.complete();
    }

    return future;

  }

  /**
   * Check equals by user and type.
   *
   * @param relationship to compare.
   *
   * @return {@code true} if the relations has the same user and type.
   */
  public boolean equalsByAppUserAndType(final SocialNetworkRelationship relationship) {

    return relationship != null && Objects.equals(this.type, relationship.type)
        && Objects.equals(this.appId, relationship.appId) && Objects.equals(this.userId, relationship.userId);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<SocialNetworkRelationship> merge(final SocialNetworkRelationship source, final String codePrefix,
      final Vertx vertx) {

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
      future = future.compose(Validations.validateChain(codePrefix, vertx));

    } else {

      promise.complete(this);

    }
    return future;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<SocialNetworkRelationship> update(final SocialNetworkRelationship source, final String codePrefix,
      final Vertx vertx) {
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
      future = future.compose(Validations.validateChain(codePrefix, vertx));

    } else {

      promise.complete(this);

    }
    return future;
  }
}
