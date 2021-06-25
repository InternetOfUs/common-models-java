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
import eu.internetofus.common.model.CreateUpdateTsDetails;
import eu.internetofus.common.model.Mergeable;
import eu.internetofus.common.model.Updateable;
import eu.internetofus.common.model.Validable;
import eu.internetofus.common.model.Validations;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import java.util.List;

/**
 * A member of a community.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "CommunityMember", description = "A member of a community.")
public class CommunityMember extends CreateUpdateTsDetails
    implements Validable, Mergeable<CommunityMember>, Updateable<CommunityMember> {

  /**
   * Identifier of the user that is member of the community.
   */
  @Schema(description = "The identifier of the user that is on the community.", example = "15837028-645a-4a55-9aaf-ceb846439eba", nullable = true)
  public String userId;

  /**
   * The privileges of the user on the community.
   */
  @ArraySchema(schema = @Schema(implementation = String.class), arraySchema = @Schema(description = "The keywords that describe the community.", nullable = true))
  public List<String> privileges;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();
    future = Validations.composeValidateId(future, codePrefix, "userId", this.userId, true,
        WeNetProfileManager.createProxy(vertx)::retrieveProfile);
    future = future.compose(empty -> Validations
        .validateNullableListStringField(codePrefix, "privileges", this.privileges).map(privileges -> {
          this.privileges = privileges;
          return null;
        }));
    promise.complete();
    return future;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<CommunityMember> merge(final CommunityMember source, final String codePrefix, final Vertx vertx) {

    final Promise<CommunityMember> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var merged = new CommunityMember();
      merged._creationTs = this._creationTs;
      merged._lastUpdateTs = this._lastUpdateTs;
      merged.userId = this.userId;
      merged.privileges = source.privileges;
      if (merged.privileges == null) {

        merged.privileges = this.privileges;
      }

      future = future.compose(Validations.validateChain(codePrefix, vertx));

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
  public Future<CommunityMember> update(final CommunityMember source, final String codePrefix, final Vertx vertx) {

    final Promise<CommunityMember> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var updated = new CommunityMember();
      updated._creationTs = this._creationTs;
      updated._lastUpdateTs = this._lastUpdateTs;
      updated.userId = this.userId;
      updated.privileges = source.privileges;

      future = future.compose(Validations.validateChain(codePrefix, vertx));

      promise.complete(updated);

    } else {

      promise.complete(this);
    }

    return future;
  }

}
