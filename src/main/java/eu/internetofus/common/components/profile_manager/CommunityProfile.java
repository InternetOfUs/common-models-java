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

package eu.internetofus.common.components.profile_manager;

import java.util.List;

import eu.internetofus.common.components.CreateUpdateTsDetails;
import eu.internetofus.common.components.Mergeable;
import eu.internetofus.common.components.Merges;
import eu.internetofus.common.components.Updateable;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.ValidationErrorException;
import eu.internetofus.common.components.Validations;
import eu.internetofus.common.components.service.WeNetService;
import eu.internetofus.common.components.task_manager.ProtocolNorm;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * A community of WeNet users.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "CommunityProfile", description = "A community of WeNet users.")
public class CommunityProfile extends CreateUpdateTsDetails implements Validable, Mergeable<CommunityProfile>, Updateable<CommunityProfile> {

  /**
   * The identifier of the community.
   */
  @Schema(description = "The identifier of the community.", example = "15837028-645a-4a55-9aaf-ceb846439eba")
  public String id;

  /**
   * The identifier of the application where the community is defined.
   */
  @Schema(description = "The identifier of the application where the community is defined.", example = "15837028-645a-4a55-9aaf-ceb846439eba")
  public String appId;

  /**
   * The name of the community.
   */
  @Schema(description = "The name of the community.", example = "")
  public String name;

  /**
   * The description of the community.
   */
  @Schema(description = "The description of the community.", example = "")
  public String description;

  /**
   * The keywords that describe the community.
   */
  @ArraySchema(schema = @Schema(implementation = String.class), arraySchema = @Schema(description = "The keywords that describe the community."))
  public List<String> keywords;

  /**
   * The members of the community
   */
  @ArraySchema(schema = @Schema(implementation = CommunityMember.class), arraySchema = @Schema(description = "The members of the community."))
  public List<CommunityMember> members;

  /**
   * The social practices on the community.
   */
  @ArraySchema(schema = @Schema(implementation = SocialPractice.class), arraySchema = @Schema(description = "The social practice that are done into the community"))
  public List<SocialPractice> socialPractices;

  /**
   * The community norms.
   */
  @ArraySchema(schema = @Schema(implementation = Norm.class), arraySchema = @Schema(description = "The norms that regulates the community."))
  public List<ProtocolNorm> norms;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();
    try {

      this.id = Validations.validateNullableStringField(codePrefix, "id", 255, this.id);
      if (this.id != null) {

        future = Validations.composeValidateId(future, codePrefix, "id", this.id, false, WeNetProfileManager.createProxy(vertx)::retrieveCommunity);
      }
      this.appId = Validations.validateStringField(codePrefix, "appId", 255, this.appId);
      future = Validations.composeValidateId(future, codePrefix, "appId", this.appId, true, WeNetService.createProxy(vertx)::retrieveApp);

      this.name = Validations.validateStringField(codePrefix, "name", 255, this.name);
      this.description = Validations.validateNullableStringField(codePrefix, "description", 1023, this.description);
      this.keywords = Validations.validateNullableListStringField(codePrefix, "keywords", 255, this.keywords);
      future = future.compose(Validations.validate(this.members, (a, b) -> a.userId.equals(b.userId), codePrefix + ".members", vertx));
      future = future.compose(Validations.validate(this.socialPractices, (a, b) -> a.equals(b), codePrefix + ".socialPractices", vertx));
      future = future.compose(Validations.validate(this.norms, (a, b) -> a.equals(b), codePrefix + ".norms", vertx));

      promise.complete();

    } catch (final ValidationErrorException validationError) {

      promise.fail(validationError);
    }

    return future;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<CommunityProfile> merge(final CommunityProfile source, final String codePrefix, final Vertx vertx) {

    final Promise<CommunityProfile> promise = Promise.promise();
    var future = promise.future();

    if (source != null) {

      final var merged = new CommunityProfile();
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

      merged.norms = source.norms;
      if (merged.norms == null) {

        merged.norms = this.norms;
      }

      future = future.compose(Merges.mergeMembers(this.members, source.members, codePrefix + ".members", vertx, (model, mergedMembers) -> {
        model.members = mergedMembers;
      }));

      future = future.compose(Merges.mergeSocialPractices(this.socialPractices, source.socialPractices, codePrefix + ".socialPractices", vertx, (model, mergedSocialPractices) -> {
        model.socialPractices = mergedSocialPractices;
      }));

      future = future.compose(Validations.validateChain(codePrefix, vertx));

      // When merged set the fixed field values
      future = future.map(mergedValidatedModel -> {

        mergedValidatedModel.id = this.id;
        mergedValidatedModel._creationTs = this._creationTs;
        mergedValidatedModel._lastUpdateTs = this._lastUpdateTs;
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
  public Future<CommunityProfile> update(final CommunityProfile source, final String codePrefix, final Vertx vertx) {

    final Promise<CommunityProfile> promise = Promise.promise();
    var future = promise.future();

    if (source != null) {

      final var updated = new CommunityProfile();
      updated.appId = source.appId;
      updated.name = source.name;
      updated.description = source.description;
      updated.keywords = source.keywords;
      updated.members = source.members;
      updated.socialPractices = source.socialPractices;
      updated.norms = source.norms;

      future = future.compose(Validations.validateChain(codePrefix, vertx));

      // When merged set the fixed field values
      future = future.map(updatedValidatedModel -> {

        updatedValidatedModel.id = this.id;
        updatedValidatedModel._creationTs = this._creationTs;
        updatedValidatedModel._lastUpdateTs = this._lastUpdateTs;
        return updatedValidatedModel;
      });

      promise.complete(updated);

    } else {

      promise.complete(this);
    }
    return future;
  }

}
