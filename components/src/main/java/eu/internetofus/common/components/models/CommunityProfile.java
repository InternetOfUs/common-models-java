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

import eu.internetofus.common.components.HumanDescriptionWithCreateUpdateTsDetails;
import eu.internetofus.common.components.WeNetValidateContext;
import eu.internetofus.common.model.Mergeable;
import eu.internetofus.common.model.Merges;
import eu.internetofus.common.model.Updateable;
import eu.internetofus.common.model.Validable;
import eu.internetofus.common.model.ValidationErrorException;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import java.util.List;

/**
 * A community of WeNet users.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "CommunityProfile", description = "A community of WeNet users.")
public class CommunityProfile extends HumanDescriptionWithCreateUpdateTsDetails
    implements Validable<WeNetValidateContext>, Mergeable<CommunityProfile, WeNetValidateContext>,
    Updateable<CommunityProfile, WeNetValidateContext> {

  /**
   * The identifier of the community.
   */
  @Schema(description = "The identifier of the community.", example = "15837028-645a-4a55-9aaf-ceb846439eba", nullable = true)
  public String id;

  /**
   * The identifier of the application where the community is defined.
   */
  @Schema(description = "The identifier of the application where the community is defined.", example = "15837028-645a-4a55-9aaf-ceb846439eba", nullable = true)
  public String appId;

  /**
   * The members of the community
   */
  @ArraySchema(schema = @Schema(implementation = CommunityMember.class), arraySchema = @Schema(description = "The members of the community.", nullable = true))
  public List<CommunityMember> members;

  /**
   * The social practices on the community.
   */
  @ArraySchema(schema = @Schema(implementation = SocialPractice.class), arraySchema = @Schema(description = "The social practice that are done into the community", nullable = true))
  public List<SocialPractice> socialPractices;

  /**
   * The community norms.
   */
  @ArraySchema(schema = @Schema(implementation = ProtocolNorm.class), arraySchema = @Schema(description = "The norms that regulates the community.", nullable = true))
  public List<ProtocolNorm> norms;

  /**
   * The allowed task types to do.
   */
  @ArraySchema(schema = @Schema(implementation = String.class), arraySchema = @Schema(description = "The identifiers of the allowed task types to do on the community.", nullable = true))
  public List<String> taskTypeIds;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final WeNetValidateContext context) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();

    if (this.id != null) {

      future = context.validateNotDefinedCommunityIdField("id", this.id, future);
    }
    if (this.appId != null) {

      future = context.validateDefinedAppIdField("appId", this.appId, future);
    }

    this.name = context.normalizeString(this.name);
    this.description = context.normalizeString(this.description);
    this.keywords = context.validateNullableStringListField("keywords", this.keywords, promise);

    if (this.members != null) {

      future = context.validateDefinedProfileIdsField("members", CommunityMember.idsIterable(this.members), future)
          .transform(check -> {

            if (check.failed()) {

              final var cause = (ValidationErrorException) check.cause();
              final var code = cause.getCode();
              if (code.contains("members[")) {

                return Future
                    .failedFuture(new ValidationErrorException(code + ".userId", cause.getMessage(), cause.getCause()));
              }
            }

            return (Future<Void>) check;

          });
    }

    if (this.socialPractices != null) {

      future = future
          .compose(context.validateListField("socialPractices", this.socialPractices, SocialPractice::compareIds));
    }
    if (this.norms != null) {

      future = future.compose(context.validateListField("norms", this.norms, ProtocolNorm::compareIds));
    }
    if (this.taskTypeIds != null) {

      future = context.validateDefinedTaskTypeIdsField("taskTypeIds", this.taskTypeIds, future);
    }

    promise.tryComplete();

    return future;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<CommunityProfile> merge(final CommunityProfile source, final WeNetValidateContext context) {

    final Promise<CommunityProfile> promise = Promise.promise();
    var future = promise.future();

    if (source != null) {

      final var merged = new CommunityProfile();
      merged._creationTs = this._creationTs;
      merged._lastUpdateTs = this._lastUpdateTs;

      merged.appId = source.appId;
      if (merged.appId == null) {

        merged.appId = this.appId;
      }

      merged.name = source.name;
      if (merged.name == null) {

        merged.name = this.name;
      }

      merged.description = source.description;
      if (merged.description == null) {

        merged.description = this.description;
      }

      merged.keywords = source.keywords;
      if (merged.keywords == null) {

        merged.keywords = this.keywords;
      }

      future = future.compose(Merges.mergeListField(context, "members", this.members, source.members,
          CommunityMember::compareIds, (model, members) -> model.members = members));
      future = future.compose(Merges.mergeListField(context, "socialPractices", this.socialPractices,
          source.socialPractices, SocialPractice::compareIds, (model, practices) -> model.socialPractices = practices));
      future = future.compose(Merges.mergeListField(context, "norms", this.norms, source.norms,
          ProtocolNorm::compareIds, (model, norms) -> model.norms = norms));

      merged.taskTypeIds = source.taskTypeIds;
      if (merged.taskTypeIds == null) {

        merged.taskTypeIds = this.taskTypeIds;
      }

      future = future.compose(context.chain());

      // When merged set the fixed field values
      future = future.map(mergedValidatedModel -> {

        mergedValidatedModel.id = this.id;
        return mergedValidatedModel;
      });

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
  public Future<CommunityProfile> update(final CommunityProfile source, final WeNetValidateContext context) {

    final Promise<CommunityProfile> promise = Promise.promise();
    var future = promise.future();

    if (source != null) {

      final var updated = new CommunityProfile();
      updated._creationTs = this._creationTs;
      updated._lastUpdateTs = this._lastUpdateTs;
      updated.appId = source.appId;
      updated.name = source.name;
      updated.description = source.description;
      updated.keywords = source.keywords;
      updated.members = source.members;
      updated.socialPractices = source.socialPractices;
      updated.norms = source.norms;
      future = future.compose(context.chain());

      // When merged set the fixed field values
      future = future.map(updatedValidatedModel -> {

        updatedValidatedModel.id = this.id;
        return updatedValidatedModel;
      });

      promise.complete(updated);

    } else {

      promise.complete(this);
    }
    return future;
  }

}
