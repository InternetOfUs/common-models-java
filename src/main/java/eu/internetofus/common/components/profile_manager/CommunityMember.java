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
import eu.internetofus.common.components.Updateable;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.ValidationErrorException;
import eu.internetofus.common.components.Validations;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * A member of a community.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "CommunityMember", description = "A member of a community.")
public class CommunityMember extends CreateUpdateTsDetails implements Validable, Mergeable<CommunityMember>, Updateable<CommunityMember> {

  /**
   * Identifier of the user that is member of the community.
   */
  @Schema(description = "The identifier of the user that is on the community.", example = "15837028-645a-4a55-9aaf-ceb846439eba")
  public String userId;

  /**
   * The privileges of the user on the community.
   */
  @ArraySchema(schema = @Schema(implementation = String.class), arraySchema = @Schema(description = "The keywords that describe the community."))
  public List<String> privileges;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();
    try {

      this.userId = Validations.validateStringField(codePrefix, "userId", 255, this.userId);
      future = Validations.composeValidateId(future, codePrefix, "userId", this.userId, true, WeNetProfileManager.createProxy(vertx)::retrieveProfile);
      this.privileges = Validations.validateNullableListStringField(codePrefix, "privileges", 255, this.privileges);
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
  public Future<CommunityMember> merge(final CommunityMember source, final String codePrefix, final Vertx vertx) {

    final Promise<CommunityMember> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var merged = new CommunityMember();
      merged.userId = this.userId;
      merged.privileges = source.privileges;
      if (merged.privileges == null) {

        merged.privileges = this.privileges;
      }

      future = future.compose(Validations.validateChain(codePrefix, vertx));
      future = future.map(mergedValidatedModel -> {

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
  public Future<CommunityMember> update(final CommunityMember source, final String codePrefix, final Vertx vertx) {

    final Promise<CommunityMember> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var updated = new CommunityMember();
      updated.userId = this.userId;
      updated.privileges = source.privileges;

      future = future.compose(Validations.validateChain(codePrefix, vertx));
      future = future.map(updateddValidatedModel -> {

        updateddValidatedModel._creationTs = this._creationTs;
        updateddValidatedModel._lastUpdateTs = this._lastUpdateTs;
        return updateddValidatedModel;
      });

      promise.complete(updated);

    } else {

      promise.complete(this);
    }

    return future;
  }

}
