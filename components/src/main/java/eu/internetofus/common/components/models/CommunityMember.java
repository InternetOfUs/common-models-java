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
import eu.internetofus.common.model.CreateUpdateTsDetails;
import eu.internetofus.common.model.Mergeable;
import eu.internetofus.common.model.Merges;
import eu.internetofus.common.model.Updateable;
import eu.internetofus.common.model.Validable;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import java.util.Iterator;
import java.util.List;

/**
 * A member of a community.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "CommunityMember", description = "A member of a community.")
public class CommunityMember extends CreateUpdateTsDetails implements Validable<WeNetValidateContext>,
    Mergeable<CommunityMember, WeNetValidateContext>, Updateable<CommunityMember, WeNetValidateContext> {

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
  public Future<Void> validate(final WeNetValidateContext context) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();
    this.userId = context.normalizeString(this.userId);
    future = future.compose(context.validateField(this.userId, null));
    future = context.validateDefinedProfileIdField("userId", this.userId, future);
    this.privileges = context.validateNullableStringListField("privileges", this.privileges, promise);
    promise.tryComplete();
    return future;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<CommunityMember> merge(final CommunityMember source, final WeNetValidateContext context) {

    final Promise<CommunityMember> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var merged = new CommunityMember();
      merged._creationTs = this._creationTs;
      merged._lastUpdateTs = this._lastUpdateTs;
      merged.userId = this.userId;
      merged.privileges = Merges.mergeValues(this.privileges, source.privileges);
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
  public Future<CommunityMember> update(final CommunityMember source, final WeNetValidateContext context) {

    final Promise<CommunityMember> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var updated = new CommunityMember();
      updated._creationTs = this._creationTs;
      updated._lastUpdateTs = this._lastUpdateTs;
      updated.userId = this.userId;
      updated.privileges = source.privileges;
      future = future.compose(context.chain());

      promise.complete(updated);

    } else {

      promise.complete(this);
    }

    return future;
  }

  /**
   * Check if two community member practices are equivalent by its identifier
   * fields.
   *
   * @param a first model to compare.
   * @param b second model to compare.
   *
   * @return {@code true} if the community members can be considered equals by its
   *         identifier.
   */
  static boolean compareIds(final CommunityMember a, final CommunityMember b) {

    return a != null && a.userId != null && a.userId.equals(b.userId);

  }

  /**
   * Return an iterable object over the identifiers of some members.
   *
   * @param members to iterate its identifiers.
   *
   * @return the iterable component over the members.
   */
  public static Iterable<String> idsIterable(final Iterable<CommunityMember> members) {

    return new Iterable<String>() {

      /**
       * {@inheritDoc}
       */
      @Override
      public Iterator<String> iterator() {

        final var iter = members.iterator();
        return new Iterator<String>() {

          /**
           * {@inheritDoc}
           */
          @Override
          public boolean hasNext() {

            return iter.hasNext();
          }

          /**
           * {@inheritDoc}
           */
          @Override
          public String next() {

            return iter.next().userId;

          }

          /**
           * {@inheritDoc}
           */
          @Override
          public void remove() {

            iter.remove();
          }
        };
      }
    };

  }

}
